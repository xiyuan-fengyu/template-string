package com.xiyuan.rawString;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.parser.JavacParser;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by xiyuan_fengyu on 2019/4/9 16:23.
 */
@SupportedAnnotationTypes("com.xiyuan.rawString.EnableRawString")
public final class EnableRawStringProcessor extends AbstractProcessor {

    private Messager messager;

    private Filer mFiler;

    @Override
    public void init(final ProcessingEnvironment procEnv) {
        super.init(procEnv);
        messager = procEnv.getMessager();
        mFiler = procEnv.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (annotations.size() > 0) {
            for (Element ele : roundEnv.getElementsAnnotatedWith(EnableRawString.class)) {
                if (ele instanceof Symbol.ClassSymbol) {
                    Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) ele;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(classSymbol.sourcefile.openInputStream(), StandardCharsets.UTF_8))) {
                        int lineI = 1;
                        CharSequence className = classSymbol.flatname;

                        String line;
                        String rawLinesKey = null;
                        ArrayList<String> rawLines = new ArrayList<>();
                        while ((line = reader.readLine()) != null) {
                            String trimLine = line.trim();
                            boolean isStartEndLine = false;

                            if (trimLine.startsWith("*/)")) {
                                // 多行raw string结束标记
                                isStartEndLine = true;

                                String prefix = blankPrefix(line);
                                boolean samePrefix = true;
                                for (String rawLine : rawLines) {
                                    if (!rawLine.trim().isEmpty() && !prefixEquals(rawLine, prefix)) {
                                        samePrefix = false;
                                        break;
                                    }
                                }

                                if (samePrefix) {
                                    List<String> newRawLines = rawLines.stream()
                                            .map(item -> item.isEmpty() ? item : item.substring(Math.min(prefix.length(), item.length())))
                                            .collect(Collectors.toList());
                                    generateRawLinesResource(rawLinesKey, newRawLines);
                                }
                                else {
                                    generateRawLinesResource(rawLinesKey, rawLines);
                                }

                                rawLines.clear();
                                rawLinesKey = null;
                            }

                            if (trimLine.endsWith("$(/*")) {
                                // 多行raw string开始标记
                                isStartEndLine = true;

                                rawLines.clear();
                                rawLinesKey = className + "_" + lineI;
                            }

                            if (!isStartEndLine) {
                                if (rawLinesKey != null) {
                                    if (line.contains("*\\/")) {
                                        line = line.replaceAll("\\*\\\\/", "*/");
                                    }
                                    rawLines.add(line);
                                }
                            }

                            lineI++;
                        }
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return true;
    }

    private static String blankPrefix(String str) {
        for (int i = 0, len = str.length(); i < len; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return str.substring(0, i);
            }
        }
        return "";
    }

    private static boolean prefixEquals(String str, String prefix) {
        int strLen = str.length();
        int prefixLen = prefix.length();
        if (strLen < prefixLen) {
            return false;
        }

        for (int i = 0; i < prefixLen; i++) {
            if (str.charAt(i) != prefix.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private String parseLine(String line) {
        return null;
    }

    private void generateRawLinesResource(String key, List<String> rawLines) {
        try {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                    "", "raw-string/"  + key);
            try (Writer writer = new OutputStreamWriter(fileObject.openOutputStream(), StandardCharsets.UTF_8)) {
                String rawLinesStr = String.join("\n", rawLines);
                writer.append(String.valueOf(rawLinesStr.length())).append("\n")
                        .append(rawLinesStr);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
