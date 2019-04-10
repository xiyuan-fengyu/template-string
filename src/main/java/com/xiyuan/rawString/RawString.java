package com.xiyuan.rawString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by xiyuan_fengyu on 2019/4/9 16:02.
 */
public class RawString {

    private Object[] args;

    public RawString(Object ...args) {
        this.args = args;
    }

    public String $() {

        return null;
    }

    public static final class S {

        public static String $() {
            StackTraceElement element = new Throwable().getStackTrace()[1];
            String key = element.getClassName();
            int lastDotI = key.lastIndexOf('.');
            if (lastDotI > -1)  {
                key = key.substring(0, lastDotI + 1) + element.getFileName();
            }
            else {
                key = element.getFileName();
            }
            key +=  "_" + element.getLineNumber();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            RawString.class.getClassLoader().getResourceAsStream("raw-string/" + key), StandardCharsets.UTF_8))) {
                String lengthLine = reader.readLine();
                int length = Integer.parseInt(lengthLine);
                char[] chars = new char[length];
                reader.read(chars);
                return new String(chars);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}
