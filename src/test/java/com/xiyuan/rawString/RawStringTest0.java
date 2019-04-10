package com.xiyuan.rawString;


/**
 * Created by xiyuan_fengyu on 2019/4/9 16:06.
 */
@EnableRawString
public class RawStringTest0 {

    public static void main(String[] args) {
        String rawStr = new RawString("just test", "中文测试", true).$(/*
        const str = 123;
        console.log("/*123中文*\/");
        console.log(${arg0});
        console.log(${arg1});
        console.log(${arg2 ? "ok", "false"});

        */);
        System.out.println(rawStr);
    }

}