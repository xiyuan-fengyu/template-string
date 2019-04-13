package com.xiyuan.templateString;

import com.xiyuan.templateString.template.TemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xiyuan_fengyu on 2019/4/9 16:02.
 */
@SuppressWarnings("WeakerAccess")
public class TemplateString {

    public static final String resourcePath = "template-string";

    private final Map<String, Object> context = new ConcurrentHashMap<>();

    private TemplateEngine templateEngine;

    public TemplateString(Object ...args) {
        if (args != null) {
            for (int i = 0, len = args.length; i < len; i++) {
                context.put("_" + i, args[i]);
            }
        }
    }

    public TemplateString put(String key, Object value) {
        context.put(key, value);
        return this;
    }

    public TemplateString putAll(Map<String, Object> params) {
        context.putAll(params);
        return this;
    }

    public TemplateString setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }

    public String $() {
        return $(templateEngine, context, new Throwable().getStackTrace()[1]);
    }

    public static final class S {

        private S() {}

        public static String $() {
            return TemplateString.$(null, null, new Throwable().getStackTrace()[1]);
        }

    }

    private static String $(TemplateEngine templateEngine, Map<String, Object> context, StackTraceElement traceElement) {
        String key = traceElement.getClassName();
        int lastDotI = key.lastIndexOf('.');
        if (lastDotI > -1)  {
            key = key.substring(0, lastDotI + 1) + traceElement.getFileName();
        }
        else {
            key = traceElement.getFileName();
        }
        key +=  "_" + traceElement.getLineNumber();

        if (templateEngine == null) {
            templateEngine = TemplateEngine.get();
        }
        if (context == null) {
            context = new HashMap<>();
        }
        try {
            return templateEngine.parse(key, context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
