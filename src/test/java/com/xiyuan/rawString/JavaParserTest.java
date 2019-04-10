package com.xiyuan.rawString;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;

/**
 * Created by xiyuan_fengyu on 2019/4/10 10:59.
 */
public class JavaParserTest {

    private static JavacFileManager jcFileManager = new JavacFileManager(new Context(), true, Charset.defaultCharset());

    private static JavacTool jcTool = new JavacTool();

    public static void main(String[] args) {
        String javaFilePath = "src/test/java/com/xiyuan/rawString/RawStringTest0.java";
        parseJavaSourceFile(javaFilePath);
    }

    public static void parseJavaSourceFile(String filePath)
    {
        Iterable<? extends JavaFileObject> javaFiles = jcFileManager.getJavaFileObjects(filePath);

        /* Get the java compiler task object. */
        JavaCompiler.CompilationTask cTask = jcTool.getTask(null, jcFileManager, null, null, null, javaFiles);
        JavacTask jcTask = (JavacTask) cTask;

        try
        {
            /* Iterate the java compiler parse out task. */
            Iterable<? extends CompilationUnitTree> codeResult = jcTask.parse();
            for (CompilationUnitTree codeTree : codeResult) {
                System.out.println(codeTree);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}
