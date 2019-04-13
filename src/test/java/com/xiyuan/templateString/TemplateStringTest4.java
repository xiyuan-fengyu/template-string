package com.xiyuan.templateString;


import static com.xiyuan.templateString.TemplateString.S.r;

/**
 * Created by xiyuan_fengyu on 2019/4/9 16:06.
 */
@EnableTemplateString
public class TemplateStringTest4 {

    public static void main(String[] args) {
        String templateStr = new TemplateString("123", "Tom", 25).$(/*
        {
            "id": ${_0},
            "name": "${_1}",
            "age": ${_2}
        }
        */);
        System.out.println(templateStr);

        String rawStr = r(/*
        {
            "id": ${_0},
            "name": "${_1}",
            "age": ${_2}
        }
        */);
        System.out.println(rawStr);
    }

}