package com.xiyuan.rawString.template;

import java.util.Map;

/**
 * Created by xiyuan_fengyu on 2019/4/11 13:39.
 */
public abstract class TemplateEngine<T> {

    public TemplateEngine(T config) {
    }

    public abstract String parse(String templateName, Map<String, Object> context) throws Exception;

}
