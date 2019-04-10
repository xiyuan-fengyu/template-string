参考  
https://stackoverflow.com/questions/878573/java-multiline-string/878958  
https://github.com/mageddo/mageddo-projects/tree/master/raw-string-literals  
https://stackoverflow.com/questions/8587096/how-do-you-debug-java-annotation-processors-using-intellij  

Processor Debug  
add JAVA_HOME/lib/tools.jar to Java SDK    
![tools_jar.png](https://i.loli.net/2019/04/09/5cac7479f1571.png)  
add breakpointers in src/main/java/com/xiyuan/rawString/EnableRawStringProcessor.java  
![breakpointers.png](https://i.loli.net/2019/04/09/5cac75022b7ae.png)  
run src/test/java/com/xiyuan/rawString/ProcessorDebuger.java in debug mode  
