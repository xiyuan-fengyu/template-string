package com.xiyuan.templateString;

/**
 * Created by xiyuan_fengyu on 2019/4/9 16:06.
 */
class TemplateString$Test3 {

    @EnableTemplateString
    private static class Inner {

        public static class Temp {

            public static void main(String[] args) {
                String rawStr = new TemplateString()
                        .put("id", 123).$(/*
                第1行
                第2行 ${id}
                第3行*\/
                */);
                System.out.println(rawStr);
            }

            public void $123$() {

            }

        }

    }

}