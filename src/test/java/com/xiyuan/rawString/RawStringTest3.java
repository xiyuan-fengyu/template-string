package com.xiyuan.rawString;

/**
 * Created by xiyuan_fengyu on 2019/4/9 16:06.
 */
class RawString$Test3 {

    @EnableRawString
    private static class Inner {

        public static class Temp {

            public static void main(String[] args) {
                // _0 参数未提供，报错
                String rawStr = new RawString()
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