package com.view.compiler;

import com.google.auto.service.AutoService;
import com.view.annotation.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by wll on 2017/9/7.
 */
@AutoService(Processor.class)
public class ViewFinderProcessor extends AbstractProcessor {
    private Elements mElementUtil;//元素相关的辅助类，获取元素相关的信息

    private Map<String, ProxyInfo> mPoxyMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.mElementUtil = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("============================================================================");
        mPoxyMap.clear();
        //获取所有被@BindView注解的元素
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        System.out.println("======Size====" + elements.size());
        // 收集信息
        for (Element element : elements) {
            // 检查element类型
            if (!checkAnnotationUseValid(element, BindView.class)) {
                return false;
            }
            if (!(element instanceof VariableElement)) {
                return false;
            }
            VariableElement variableElement = (VariableElement) element;
            // 获取当前元素所在的类元素的信息
            TypeElement classTypeElement = (TypeElement) variableElement.getEnclosingElement();

            System.out.println("-->>TypeElement--getSimpleName=" + classTypeElement.getSimpleName());
            System.out.println("-->>variableElement--getSimpleName=" + variableElement.getSimpleName());

            // 获取全类名（包路径 + 类名）
            String qualifiedName = classTypeElement.getQualifiedName().toString();

            // 根据全类名查找缓存中是否存在类元素的信息存储对象
            ProxyInfo proxyInfo = mPoxyMap.get(qualifiedName);
            if (proxyInfo == null) {
                //如果不存在当前类信息存储对象；则实例化一个信息存储对象，并将全类名作为key将信息存储对象作为value存入缓存中
                proxyInfo = new ProxyInfo(mElementUtil, classTypeElement);
                mPoxyMap.put(qualifiedName, proxyInfo);
            }

            // 获得元素上的BindView注解对象
            BindView annotation = variableElement.getAnnotation(BindView.class);
            int viewId = annotation.value();
            // 将被@BindView声明的VariableElement放入到ProxyInfo中的缓存中（个人理解：VariableElement应该就是被@BindView注解的属性Field）
            proxyInfo.put(viewId, variableElement);
        }

        //对收集的信息进行处理，也就是根据收集的信息生成代理类
        for (String key : mPoxyMap.keySet()) {
            ProxyInfo proxyInfo = mPoxyMap.get(key);
            try {
                //生成代理类文件
                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer = sourceFile.openWriter();
                //往代理类文件中写入代码
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean checkAnnotationUseValid(Element element, Class<?> clazz) {
        if (element.getKind() != ElementKind.FIELD) { // 判断当前元素是否是Field类型的
            error(element, "%s must be declared on field.", clazz.getSimpleName());
            return false;
        }
        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            error(element, "%s() must can not be private.", element.getSimpleName());
            return false;
        }
        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }

    /**
     * 返回支持的注解类型
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    /**
     * 返回支持的源码版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
