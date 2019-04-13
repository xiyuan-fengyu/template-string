package com.xiyuan.templateString;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.Pair;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
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
@SupportedAnnotationTypes("com.xiyuan.templateString.EnableTemplateString")
public final class EnableTemplateStringProcessor extends AbstractProcessor {

    private Messager messager;

    private Filer filer;

    @Override
    public void init(final ProcessingEnvironment procEnv) {
        super.init(procEnv);
        messager = procEnv.getMessager();
        filer = procEnv.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (annotations.size() > 0) {
            for (Element ele : roundEnv.getElementsAnnotatedWith(EnableTemplateString.class)) {
                if (ele instanceof Symbol.ClassSymbol) {
                    Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) ele;

                    Pair<Symbol.MethodSymbol, Attribute> attributePair = classSymbol.getMetadata().getDeclarationAttributes().get(0).values.head;
                    String charset = attributePair == null ? "UTF-8" : (String) attributePair.snd.getValue();

                    JavaFileObject sourcefile = classSymbol.sourcefile;
                    String classOwnerName = classSymbol.flatname.toString();
                    int lastDotI = classOwnerName.lastIndexOf('.');
                    String sourceFileName = sourcefile.getName();
                    int lastPathDivider = sourceFileName.lastIndexOf('/');
                    if (lastPathDivider == -1) {
                        lastPathDivider = sourceFileName.lastIndexOf('\\');
                    }
                    if (lastPathDivider > -1) {
                        sourceFileName = sourceFileName.substring(lastPathDivider + 1);
                    }

                    if (lastDotI > -1) {
                        classOwnerName = classOwnerName.substring(0, lastDotI + 1) + sourceFileName;
                    }
                    else {
                        classOwnerName = sourceFileName;
                    }

                    String filePath  = sourcefile.getName().replaceAll("\\\\", "/");

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(classSymbol.sourcefile.openInputStream(), charset))) {
                        int lineI = 1;

                        String line;
                        String rawLinesKey = null;
                        ArrayList<String> rawLines = new ArrayList<>();
                        while ((line = reader.readLine()) != null) {
                            String trimLine = line.trim();
                            boolean isStartEndLine = false;

                            if (trimLine.startsWith("*/)")) {
                                // 多行template string结束标记
                                isStartEndLine = true;

                                String prefix = blankPrefix(line);
                                boolean samePrefix = true;
                                for (String rawLine : rawLines) {
                                    if (!rawLine.trim().isEmpty() && !prefixEquals(rawLine, prefix)) {
                                        samePrefix = false;
                                        messager.printMessage(Diagnostic.Kind.WARNING, "Multi lines don't share a same blank prefix with the end flag line, " +
                                                "so the prefix of each lines will keep as it is.");
                                        break;
                                    }
                                }

                                if (samePrefix) {
                                    List<String> newRawLines = rawLines.stream()
                                            .map(item -> item.length() < prefix.length() ? "" : item.substring(prefix.length()))
                                            .collect(Collectors.toList());
                                    generateRawLinesResource(rawLinesKey, newRawLines);
                                }
                                else {
                                    generateRawLinesResource(rawLinesKey, rawLines);
                                }

                                rawLines.clear();
                                rawLinesKey = null;
                            }


                            if (trimLine.endsWith("(/*")) {
                                int trimLineLen = trimLine.length();
                                if (trimLineLen > 3) {
                                    char c = trimLine.charAt(trimLineLen - 4);
                                    if (c == '$' || c == 'r') {
                                        // $()会使用模板语言解析；r()不会使用模板语言解析，直接返回原字符串
                                        // 多行template string开始标记
                                        isStartEndLine = true;

                                        rawLines.clear();
                                        rawLinesKey = classOwnerName + "_" + lineI;
                                    }
                                }
                            }

                            if (!isStartEndLine) {
                                if (rawLinesKey != null) {
                                    line = parseLine(line, filePath + ":" + lineI);
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

    private String parseLine(String line, String pos) {
        String orignalLine = line;

        // 将 *\/ 这种替换为 */
        // 将 *\\\\\/ 这种替换为 *\\\\/
        // *的位置
        int starI =-1;
        /*
        0:默认状态
        1:读取到*
        2:*后面紧跟着一连串\
         */
        int status = 0;
        int cursor = 0;
        while (cursor < line.length()) {
            char c = line.charAt(cursor);
            if (c == '*')  {
                status = 1;
                starI = cursor;
                cursor++;
            }
            else if (c == '\\') {
                if (status == 1 || status == 2) {
                    status = 2;
                }
                else {
                    status = 0;
                    starI = -1;
                }
                cursor++;
            }
            else if (c == '/') {
                if (status == 1) {
                    throw new RuntimeException("bad raw line(" + pos + "): " + orignalLine);
                }
                else if (status == 2) {
                    line = line.substring(0, starI + 1) + line.substring(starI + 2);
                }
                else {
                    cursor++;
                }
                status = 0;
                starI = -1;
            }
            else {
                starI = -1;
                status = 0;
                cursor++;
            }
        }

        return line;
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

    private void generateRawLinesResource(String key, List<String> rawLines) {
        try {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                    "", TemplateString.resourcePath + "/"  + key);
            try (Writer writer = new OutputStreamWriter(fileObject.openOutputStream(), StandardCharsets.UTF_8)) {
                String rawLinesStr = String.join("\n", rawLines);
                writer.append(rawLinesStr);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
