package com.xiyuan.rawString;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.file.BaseFileObject;
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
import java.util.Random;
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
                    BaseFileObject sourcefile = (BaseFileObject) classSymbol.sourcefile;
                    String classOwnerName = classSymbol.flatname.toString();
                    int lastDotI = classOwnerName.lastIndexOf('.');
                    if (lastDotI > -1) {
                        classOwnerName = classOwnerName.substring(0, lastDotI + 1) + sourcefile.getShortName();
                    }
                    else {
                        classOwnerName = sourcefile.getShortName();
                    }

                    long javaExpMagic = (System.currentTimeMillis() << 22) + new Random().nextInt(1 << 22);

                    String filePath  = sourcefile.getName().replaceAll("\\\\", "/");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(classSymbol.sourcefile.openInputStream(), StandardCharsets.UTF_8))) {
                        int lineI = 1;

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
                                rawLinesKey = classOwnerName + "_" + lineI;
                            }

                            if (!isStartEndLine) {
                                if (rawLinesKey != null) {
                                    LineParseRes lineParseRes = parseLine(line, filePath, lineI, javaExpMagic);
                                    // todo
                                    rawLines.add(lineParseRes.parsedLine);
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

    private LineParseRes parseLine(String line, String filePath, int lineNum, long javaExpMagic) {
        LineParseRes lineParseRes = new LineParseRes(line, filePath, lineNum);

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
                    throw new RuntimeException("bar raw line(" + lineParseRes.pos + "): " + lineParseRes.orignalLine);
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

        // 开始解析 ${javaExp}
        // $的位置
        int $I = -1;
        /*
        0:默认状态
        3:读取到$
        4:$后紧跟着{
         */
        status = 0;
        cursor = 0;
        String badJavaExp = null;
        while (cursor < line.length()) {
            char c = line.charAt(cursor);
            if (c == '}') {
                if (status == 4) {
                    if ($I + 2 == cursor) {
                        // 当做空字符串处理
                        line = line.substring(0, $I) + line.substring($I + 3);
                    }
                    else {
                        // 尝试将${}之间的字符串当做java exp解析
                        String exp = line.substring($I + 2, cursor);
                        if (JavaExpChecker.isValid(exp)) {
                            badJavaExp = null;
                            //  解析到 正确的java exp
                            status = 0;

                            // 替换字符串为 $javaExpMagic_lineNum_$I$
                            String javaExpMethod = "$" + javaExpMagic + "_" + lineNum + "_" + $I + "$";
                            lineParseRes.javaExps.add(new String[]{
                                    javaExpMethod,
                                    exp
                            });
                            line = line.substring(0, $I) + javaExpMethod + line.substring(cursor + 1);
                            cursor = javaExpMethod.length() - exp.length() - 2;
                        }
                        else {
                            badJavaExp = exp;
                        }
                    }
                    cursor++;
                }
                else {
                    cursor++;
                }
            }
            else if (c == '$')  {
                if (status != 4) {
                    status = 3;
                    $I = cursor;
                }
                cursor++;
            }
            else if (c == '{') {
                if (status == 3) {
                    status = 4;
                }
                cursor++;
            }
            else {
                cursor++;
            }
        }

        if (badJavaExp != null) {
            throw new RuntimeException("bad java exp(" + lineParseRes.pos + "): " + badJavaExp);
        }

        return lineParseRes;
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

    private static final class LineParseRes  {

        public String orignalLine;

        public String parsedLine;

        public String pos;

        public List<String[]> javaExps = new ArrayList<>();

        public LineParseRes(String orignalLine, String filePath, int lineNum) {
            this.orignalLine = orignalLine;
            this.pos = filePath + ":" + lineNum;
        }

    }

}
