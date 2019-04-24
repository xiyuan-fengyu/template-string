package com.xiyuan.templateString.template;

import com.xiyuan.templateString.TemplateString;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xiyuan_fengyu on 2019/4/12 0:10.
 */
public class ThymeleafEngine extends TemplateEngine {

    private final org.thymeleaf.TemplateEngine templateEngine;

    public ThymeleafEngine(Properties properties) {
        super(properties);

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setCacheable(true);
        templateResolver.setPrefix(TemplateString.resourcePath + "/");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new org.thymeleaf.TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public String name() {
        return "thymeleaf";
    }

    @Override
    public String parse(String templateName, Map<String, Object> context) throws Exception {
        Context tempContext = new Context();
        tempContext.setVariables(context);
        StringWriter writer = new StringWriter();
        templateEngine.process(templateName, tempContext, writer);
        return writer.toString();
    }

}
