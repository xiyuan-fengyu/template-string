package com.xiyuan.templateString.template;

import com.xiyuan.templateString.TemplateString;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xiyuan_fengyu on 2019/4/11 13:39.
 */
@SuppressWarnings("unchecked")
public abstract class TemplateEngine {

    static {
        if (tryClassForName("org.slf4j.LoggerFactory") != null) {
            /*
            屏蔽以下警告
             */
            List<String> hideMsgs = Arrays.asList(
                    "SLF4J: Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".",
                    "SLF4J: Defaulting to no-operation (NOP) logger implementation",
                    "SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details."
            );
            PrintStream oldErr = System.err;
            PrintStream newErr = new PrintStream(oldErr) {
                @Override
                public void println(String msg) {
                    if (!hideMsgs.contains(msg)) {
                        oldErr.println(msg);
                    }
                }
            };
            System.setErr(newErr);
            org.slf4j.LoggerFactory.getLogger("root");
            System.setErr(oldErr);
        }
    }

    public TemplateEngine(Properties properties) {
    }

    public abstract String parse(String templateName, Map<String, Object> context) throws Exception;

    protected Properties tryLoadProperties(String type) {
        Properties properties = new Properties();
        try (InputStream in = FreemarkerEngine.class.getClassLoader().getResourceAsStream(TemplateString.resourcePath + "/" + type + ".properties")) {
            if (in != null) {
                properties.load(in);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static class Container {

        private static final TemplateEngine defaultEngine = createDefault();

    }

    private static final String[][] engines = {
            {
                    "freemarker.template.Template",
                    "com.xiyuan.templateString.template.FreemarkerEngine"
            },
            {
                    "org.apache.velocity.Template",
                    "com.xiyuan.templateString.template.VelocityEngine"
            },
            {
                    "org.thymeleaf.TemplateEngine",
                    "com.xiyuan.templateString.template.ThymeleafEngine"
            }
    };

    private static TemplateEngine createDefault() {
        for (String[] engine : engines) {
            Class clazz = tryClassForName(engine[0]);
            if (clazz != null) {
                TemplateEngine temp = tryInit(engine[1]);
                if (temp != null) {
                    return temp;
                }
            }
        }

        throw new RuntimeException("template engine is missing, add a dependency to your pom.xml, supported template engine:\n" +
                "\n" +
                "freemarker:\n" +
                "<dependency>\n" +
                "   <groupId>org.freemarker</groupId>\n" +
                "   <artifactId>freemarker</artifactId>\n" +
                "   <version>2.3.28</version>\n" +
                "</dependency>\n" +
                "\n" +
                "velocity:\n" +
                "<dependency>\n" +
                "    <groupId>org.apache.velocity</groupId>\n" +
                "    <artifactId>velocity-engine-core</artifactId>\n" +
                "    <version>2.1</version>\n" +
                "</dependency>\n" +
                "\n" +
                "thymeleaf:\n" +
                "<dependency>\n" +
                "    <groupId>org.thymeleaf</groupId>\n" +
                "    <artifactId>thymeleaf</artifactId>\n" +
                "    <version>3.0.11.RELEASE</version>\n" +
                "    <scope>provided</scope>\n" +
                "</dependency>\n");
    }

    private static Class tryClassForName(String className) {
        try {
            return Class.forName(className, false, TemplateEngine.class.getClassLoader());
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
