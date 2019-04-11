package com.xiyuan.rawString.template;

import java.lang.reflect.Constructor;

/**
 * Created by xiyuan_fengyu on 2019/4/11 13:56.
 */
public class TemplateEngineFactory {

    private static class Container {

        private static final TemplateEngine defaultEngine = createDefault();

    }

    private static TemplateEngine createDefault() {
        Class clazz = tryClassForName("freemarker.template.Template");
        if (clazz != null) {
            TemplateEngine temp = tryInit("com.xiyuan.rawString.template.FreemarkerEngine");
            if (temp != null) {
                return temp;
            }
        }

        throw new RuntimeException("template engine is missing, add a dependency to your pom.xml, supported template engine:\n" +
                "freemarker:" +
                "<dependency>\n" +
                "   <groupId>org.freemarker</groupId>\n" +
                "   <artifactId>freemarker</artifactId>\n" +
                "   <version>2.3.28</version>\n" +
                "</dependency>");
    }

    private static Class tryClassForName(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static TemplateEngine tryInit(String className) {
        Class<? extends TemplateEngine> clazz = tryClassForName(className);
        if (clazz != null) {
            try {
                Constructor[] constructors = clazz.getConstructors();
                for (Constructor constructor : constructors) {
                    try {
                        return (TemplateEngine) constructor.newInstance(new Object[]{null});
                    }
                    catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    private static TemplateEngine global;

    public static TemplateEngine get() {
        if (global == null) {
            return Container.defaultEngine;
        }
        return global;
    }

    public static TemplateEngine set(TemplateEngine newEngine) {
        TemplateEngine old = global;
        global = newEngine;
        return old;
    }

}
