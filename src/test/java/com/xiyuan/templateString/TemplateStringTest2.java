package com.xiyuan.templateString;

import static com.xiyuan.templateString.TemplateString.S.$;

/**
 * Created by xiyuan_fengyu on 2019/4/9 16:06.
 */
@EnableTemplateString
public class TemplateStringTest2 {

    public static void main(String[] args) {
        String rawStr = $(/*
        第1行
        第2行
        第3行*\/
        */);
        System.out.println(rawStr);
    }

}