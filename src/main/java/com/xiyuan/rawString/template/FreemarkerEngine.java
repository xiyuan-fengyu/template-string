package com.xiyuan.rawString.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by xiyuan_fengyu on 2019/4/11 13:47.
 */
public class FreemarkerEngine extends TemplateEngine<Configuration> {

    private final Configuration configuration;

    public FreemarkerEngine(Configuration config) {
        super(config);

        if (config == null) {
            configuration = new Configuration(Configuration.VERSION_2_3_28);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setLogTemplateExceptions(false);
            configuration.setWrapUncheckedExceptions(true);
            try (InputStream in = FreemarkerEngine.class.getClassLoader().getResourceAsStream("raw-string/freemarker.properties")) {
                if (in != null) {
                    configuration.setSettings(in);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            configuration = config;
        }
        // 模板都是以UTF-8保存的
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassLoaderForTemplateLoading(FreemarkerEngine.class.getClassLoader(), "raw-string");
    }

    @Override
    public String parse(String templateName, Map<String, Object> context) throws Exception {
        Template template = configuration.getTemplate(templateName);
        StringWriter out = new StringWriter();
        template.process(context, out);
        return out.toString();
    }

}
