package com.xiyuan.templateString.template;

import com.xiyuan.templateString.TemplateString;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xiyuan_fengyu on 2019/4/11 13:47.
 */
public class FreemarkerEngine extends TemplateEngine {

    private final Configuration configuration;

    public FreemarkerEngine(Properties properties) {
        super(properties);

        if (System.getProperty("org.freemarker.loggerLibrary") == null) {
            // 屏蔽freemarker的异常日志，异常日志由 TemplateString 打印
            System.setProperty("org.freemarker.loggerLibrary", "none");
        }

        if (properties == null) {
            properties = tryLoadProperties("freemarker");
        }

        configuration = new Configuration(Configuration.VERSION_2_3_28);
        try {
            configuration.setSettings(properties);
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        // 模板都是以UTF-8保存的
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassLoaderForTemplateLoading(FreemarkerEngine.class.getClassLoader(), TemplateString.resourcePath);
    }

    @Override
    public String parse(String templateName, Map<String, Object> context) throws Exception {
        Template template = configuration.getTemplate(templateName);
        StringWriter out = new StringWriter();
        template.process(context, out);
        return out.toString();
    }

}
