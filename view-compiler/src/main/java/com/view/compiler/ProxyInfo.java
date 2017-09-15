package com.view.compiler;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by wll on 2017/9/7.
 */

public class ProxyInfo {
    /**
     * 包名
     */
    private String packageName;
    /**
     * 代理类的绝对路径名称(包路径 + 类名)
     */
    private String proxyClassName;
    /**
     * 元素类型
     */
    private TypeElement typeElement;

    //key为id，value为对应的成员变量
    public Map<Integer, VariableElement> finderElement = new HashMap<>();
    public Set<Integer> clickElement = new HashSet<>();

    private StringBuilder listenerBuilder = new StringBuilder();
    /**
     * 代理类类名后半部分
     */
    public static final String PROXY = "ViewInject";

    public ProxyInfo(Elements elementUtil, TypeElement typeElement) {
        this.typeElement = typeElement;
        PackageElement packageElement = elementUtil.getPackageOf(typeElement);
        //父元素的全限定名
        String packageName = packageElement.getQualifiedName().toString();
        this.packageName = packageName;
        this.proxyClassName = getClassName(typeElement, packageName) + "$" + PROXY;
    }

    String getClassName(TypeElement typeElement, String packageName) {
        int packageLen = packageName.length() + 1;
        return typeElement.getQualifiedName().toString().substring(packageLen).replace(".", "$");
    }

    TypeElement getTypeElement() {
        return typeElement;
    }

    String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    /**
     * 根据View的Id以及属性元素拼接findById代码
     *
     * @param viewId          控件的ID
     * @param variableElement 在类中定义的属性元素
     */
    void putFliedElem(Integer viewId, VariableElement variableElement) {
        finderElement.put(viewId, variableElement);
    }

    public void putMethodElem(int[] value, ExecutableElement element) {
        if (value != null && value.length != 0) {
            for (int id : value) {
                clickElement.add(id);
            }
            String simpleName = element.getSimpleName().toString();
            if (listenerBuilder.length() == 0) {
                listenerBuilder.append("   android.view.View.OnClickListener listener= new android.view.View.OnClickListener() {\n")
                        .append("       @Override\n")
                        .append("       public void onClick(android.view.View view) {\n")
                        .append("           host.").append(simpleName).append("(view);\n")
                        .append("       }\n")
                        .append("   };\n");
            }
        }
    }

    /**
     * 拼接Class类文件内容
     *
     * @return
     */
    String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n")
                // 拼接类所在的包名
                .append("package ").append(packageName).append(";\n\n")
                //导入所有包
                .append("import com.view.api.*;\n\n")

                .append("public class ").append(proxyClassName).append(" implements ").append(ProxyInfo.PROXY).append
                ("<").append(typeElement.getQualifiedName()).append(">")
                .append("{\n");
        generateMethods(builder);
        builder.append("\n");
        builder.append("}\n");

        return builder.toString();
    }

    /**
     * 拼接方法
     *
     * @param builder
     */
    private void generateMethods(StringBuilder builder) {
        builder.append("@Override\n");
        builder.append("public void inject(").append("final ").append(typeElement.getQualifiedName()).append(" host, Object source ) {" + " \n")
                .append(listenerBuilder);
        //拼接if
        builder.append("  if (source instanceof android.app.Activity){\n");
        //拼接方法中的内容
        getFinderStr(builder, "android.app.Activity");
        getMethodStr(builder, "android.app.Activity");
        //else
        builder.append("    } else {\n");
        //拼接方法中的内容
        getFinderStr(builder, "android.view.View");
        getMethodStr(builder, "android.view.View");
        //拼接方法结尾}
        builder.append("}\n");
        builder.append("    }\n");
    }

    private void getFinderStr(StringBuilder builder, String clazzType) {
        Set<Integer> keySet = finderElement.keySet();
        for (Integer viewId : keySet) {
            VariableElement variableElement = finderElement.get(viewId);
            // 获取属性名称
            String name = variableElement.getSimpleName().toString();
            // 获取属性的类型
            String type = variableElement.asType().toString();
            builder.append("        host.").append(name).append(" = ")
                    .append("(").append(type).append(")((").append(clazzType).append(")source).findViewById(").append(viewId)
                    .append(");\n");
        }

    }

    private void getMethodStr(StringBuilder builder, String clazzType) {
        for (Integer viewId : clickElement) {
            builder.append("       ((").append(clazzType).append(")source).findViewById(").append(viewId)
                    .append(").setOnClickListener(").append("listener").append(");\n");
        }
    }

}
