package com.xiyuan.rawString;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Context;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by xiyuan_fengyu on 2019/4/10 22:05.
 */
public class JavaExpChecker {

    private static final JavacFileManager jcFileManager = new JavacFileManager(new Context(), true, Charset.defaultCharset());

    private static final JavacTool jcTool = JavacTool.create();

    public static boolean isValid(String exp) {
        File javaExpFile = null;
        try {
            javaExpFile = File.createTempFile("javaExp", ".java");
            try (FileOutputStream out = new FileOutputStream(javaExpFile)) {
                String checkTempalte = "class ExpCheckTemplate {\n" +
                        "    Object exp() {\n" +
                        "        return " + exp + ";\n" +
                        "    }\n" +
                        "}";
                out.write(checkTempalte.getBytes(StandardCharsets.UTF_8));
            }

            Iterable<? extends JavaFileObject> javaFiles = jcFileManager.getJavaFileObjects(javaExpFile);
            JavacTask javacTask = jcTool.getTask(null, jcFileManager, null, null, null, javaFiles);
            CompilationUnitTree codeTree = javacTask.parse().iterator().next();
            boolean[] res = {true};
            for (Tree typeDecl : codeTree.getTypeDecls()) {
                if (typeDecl instanceof JCTree) {
                    new TreeScanner() {
                        @Override
                        public void visitErroneous(JCTree.JCErroneous jcErroneous) {
                            super.visitErroneous(jcErroneous);
                            res[0] = false;
                        }
                    }.scan((JCTree) typeDecl);
                    if (!res[0]) {
                        return false;
                    }
                }
            }
            return res[0];
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (javaExpFile != null) {
                javaExpFile.delete();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(isValid("test ? \"OK\" : \"asd\""));
    }

}
