# template-string

## 快速开始
创建一个新项目，通过maven管理jar依赖。    
在pom.xml中添加依赖  
```
<dependencies>
    <dependency>
        <groupId>com.github.xiyuan-fengyu</groupId>
        <artifactId>template-string</artifactId>
        <version>1.0.0</version>
    </dependency>

    <dependency>
        <groupId>org.freemarker</groupId>
        <artifactId>freemarker</artifactId>
        <version>2.3.28</version>
    </dependency>
</dependencies>
```
编写一个用于测试的类  
```
import com.xiyuan.templateString.EnableTemplateString;
import com.xiyuan.templateString.TemplateString;

import static com.xiyuan.templateString.TemplateString.S.r;

@EnableTemplateString
public class TemplateStringTest {

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
```
输出结果如下  
```
{
    "id": 123,
    "name": "Tom",
    "age": 25
}
{
    "id": ${_0},
    "name": "${_1}",
    "age": ${_2}
}
```

## 实现说明
如果一个java文件中有一个类有 @EnableTemplateString 注解，则在编译期间，会扫描这个java文件，将其中满足如下格式的内容(...部分)提取为模板。  
typeA  
```
    String str = r(/*
    ...
    */);
```
typeB  
```
    String str = $(/*
    ...
    */);
```
typeC  
```
    String str = new TemplateString().$(/*
    ...
    */);
```

开始标记行必须以 r(/* 或 $(* 结尾，后面可以有空白字符  
结束标记行必须以 */) 开始，前面可以有空白字符  
其间的 ... 为模板内容  

在将...部分提取为模板的过程中，如果模板部分每一个非空(not blank)行和结束行"*/)"有相同的空白(blank)前缀，
，则会从模板内容中每行删除这个相同的空白前缀。  
例如：  
```
    String str = r(/*
    line 1
    line 2
    line 3
    */);
```
解析出来的模板类容为  
```
line 1
line 2
line 3
```
而
```
    String str = r(/*
    line 1
  line 2
    line 3
    */);
```
解析出来的模板类容为  
```
    line 1
  line 2
    line 3
```

```
另外，当模板内容中存在"*/"这种字符串时，需要替换为"*\/"，直接写作 "*/"，在编译过程中会提示错误     
当 * 和 / 之间有 n 个 \ 时，解析模板后，将变为 *(n-1)个\/  
```

其中 typeA 不经过模板引擎解析，直接返回原内容；typeB，typeC需要模板引擎解析后返回结果，typeC可以设置参数，实现字符串插值。  
typeB，typeC 两种方式需要引入额外的模板引擎依赖，目前内置支持 freemarker, velocity, thymeleaf，但在使用的时候需要
引入对应的包  
```
        <dependency>
            <groupId>org.freemarker</groupId>
            <artifactId>freemarker</artifactId>
            <version>2.3.28</version>
        </dependency>

        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity-engine-core</artifactId>
            <version>2.1</version>
        </dependency>

        <dependency>
            <groupId>org.thymeleaf</groupId>
            <artifactId>thymeleaf</artifactId>
            <version>3.0.11.RELEASE</version>
        </dependency>
```
当有两个及以上的模板引擎包存在时，TemplateString启用模板引擎的顺序优先级为：freemarker > velocity > thymeleaf  

通过 typeC 方式声明 TemplateString 时，支持以下方式设置参数，而且可以指定模板引擎  
```
    String str = new TemplateString(1, 2, 3)
            .put("someKey", "someValue")
            .putAll(new HashMap<>())
            .setTemplateEngine(new VelocityEngine(null))
            .$(/*
    {
        "id": ${_0},
        "name": "${_1}",
        "age": ${_2}
    }
    */);
    System.out.println(str);
```  
构造函数传递的参数将依次解析为 _0, _1, _2, ...  
通过 put, putAll 设置的参数以key作为参数名  

## 模板引擎语法文档参考  
freemarker  
https://freemarker.apache.org/docs/ref.html  

velocity  
http://velocity.apache.org/engine/devel/user-guide.html  

thymeleaf  
https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html  



## 如何开发调试这个项目  
将 JAVA_HOME/lib/tools.jar 添加到 Java SDK      
![tools_jar.png](https://i.loli.net/2019/04/09/5cac7479f1571.png)  
在 src/main/java/com/xiyuan/templateString/EnableTemplateStringProcessor.java 中添加断点    
![annotation-processor调试.png](https://i.loli.net/2019/04/13/5cb1acc676d02.png)  
以debug模式运行 src/test/java/com/xiyuan/templateString/ProcessorDebuger.java  



参考  
https://stackoverflow.com/questions/878573/java-multiline-string/878958  
https://github.com/mageddo/mageddo-projects/tree/master/raw-string-literals  
https://stackoverflow.com/questions/8587096/how-do-you-debug-java-annotation-processors-using-intellij  


