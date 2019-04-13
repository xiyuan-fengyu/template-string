package com.xiyuan.templateString.template;

import com.xiyuan.templateString.TemplateString;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * Created by xiyuan_fengyu on 2019/4/11 22:47.
 */
public class VelocityEngine extends TemplateEngine {

    private final org.apache.velocity.app.VelocityEngine velocityEngine;

    private final WeakHashMap<String, org.apache.velocity.Template> templateCache = new WeakHashMap<>();

    public VelocityEngine(Properties properties) {
        super(properties);

        if (properties == null) {
            properties = tryLoadProperties("velocity");
        }

        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        properties.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");

        velocityEngine = new org.apache.velocity.app.VelocityEngine();
        velocityEngine.init(properties);
    }

    @Override
    public String parse(String templateName, Map<String, Object> context) throws Exception {
        Template template = templateCache.computeIfAbsent(templateName,
                key -> velocityEngine.getTemplate(TemplateString.resourcePath + "/" + key));
        VelocityContext velocityContext = new VelocityContext(context);
        StringWriter writer = new StringWriter();
        template.merge(velocityContext, writer);
        return writer.toString();
    }

}