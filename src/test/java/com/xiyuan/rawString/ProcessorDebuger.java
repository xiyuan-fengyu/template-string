package com.xiyuan.rawString;

/**
 * Created by xiyuan_fengyu on 2019/4/9 17:12.
 * processor debug 参考 https://stackoverflow.com/questions/8587096/how-do-you-debug-java-annotation-processors-using-intellij
 */
public class ProcessorDebuger {

    public static void main(String[] args) throws Exception {
        com.sun.tools.javac.Main.main(new String[]{
                "-proc:only",
                "-d",
                "target/test-classes",
                "-processor",
                "com.xiyuan.rawString.EnableRawStringProcessor",
//                "src/test/java/com/xiyuan/rawString/RawStringTest0.java",
//                "src/test/java/com/xiyuan/rawString/RawStringTest1.java",
//                "src/test/java/com/xiyuan/rawString/RawStringTest2.java",
                "src/test/java/com/xiyuan/rawString/RawStringTest3.java"
        });
    }

}
