package com.xiyuan.templateString;


/**
 * Created by xiyuan_fengyu on 2019/4/9 16:06.
 */
@EnableTemplateString
public class TemplateStringTest4 {

    public static void main(String[] args) {
        String rawStr = new TemplateString("123", "Tom", 25).$(/*
        {
            "id": ${_0},
            "name": "${_1}",
            "age": ${_2}
        }
        */);
        System.out.println(rawStr);
    }

}