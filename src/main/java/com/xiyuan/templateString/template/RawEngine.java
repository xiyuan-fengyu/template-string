package com.xiyuan.templateString.template;

import com.xiyuan.templateString.TemplateString;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * Created by xiyuan_fengyu on 2019/4/11 22:47.
 */
public class RawEngine extends TemplateEngine {

    private static final class Container {

        private static final RawEngine instance = new RawEngine();

    }

    public static RawEngine getInstance() {
        return Container.instance;
    }

    private final WeakHashMap<String, String> templateCache = new WeakHashMap<>();

    private RawEngine() {
        super(null);
    }

    private RawEngine(Properties properties) {
        super(properties);
    }

    @Override
    public String parse(String templateName, Map<String, Object> context) throws Exception {
        return templateCache.computeIfAbsent(templateName, key -> {
            try (InputStream in = RawEngine.class.getClassLoader().getResourceAsStream(TemplateString.resourcePath + "/" + key)) {
                if (in != null) {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        byte[] bytes = new byte[1024];
                        int readLen;
                        while ((readLen = in.read(bytes)) > -0) {
                            out.write(bytes, 0, readLen);
                        }
                        return new String(out.toByteArray(), 0, out.size());
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}