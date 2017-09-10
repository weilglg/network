package com.view.compiler;

import java.util.HashMap;
import java.util.Map;

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

    private StringBuilder findBuilder = new StringBuilder();

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
    void put(Integer viewId, VariableElement variableElement) {
        /*
          * TextView tv
          */
        // 获取属性名称
        String name = variableElement.getSimpleName().toString();
        // 获取属性的类型
        String type = variableElement.asType().toString();
        findBuilder.append("host.").append(name).append(" = ")
                .append("(").append(type).append(")(((android.app.Activity)source).findViewById(").append(viewId)
                .append("));\n");
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
        builder.append("public void inject(").append(typeElement.getQualifiedName()).append(" host, Object source ) {" +
                " \n");
        //拼接if
        builder.append("  if (source instanceof android.app.Activity){\n");
        //拼接方法中的内容
        builder.append(findBuilder);
        //else
        builder.append("} else {\n");
        //拼接方法中的内容
        builder.append(findBuilder);
        //拼接方法结尾}
        builder.append("}\n");
        builder.append("    }\n");
    }
}
