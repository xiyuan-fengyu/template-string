package com.xiyuan.rawString;

import static com.xiyuan.rawString.RawString.S.$;

/**
 * Created by xiyuan_fengyu on 2019/4/9 16:06.
 */
class RawString$Test3 {

    @EnableRawString
    private static class Inner {

        public static class Temp {

            public static void main(String[] args) {
                String rawStr = $(/*
                第1行
                第2行
                第3行*\/
                */);
                System.out.println(rawStr);
            }

            public void $123$() {

            }

        }

    }

}