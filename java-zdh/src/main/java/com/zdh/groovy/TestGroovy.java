package com.zdh.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zdh
 * @date 2023/1/15 19:43
 * @Version 1.0
 */
public class TestGroovy {
    public  GroovyObject newClazz(String script) {
        GroovyClassLoader loader = new GroovyClassLoader(this.getClass().getClassLoader());
        Class clazz = loader.parseClass(script);
        try {
            return  (GroovyObject)clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("实例化groovy脚本失败");
        }
    }
    public static void main(String[] args) {

        TestGroovy testGroovy = new TestGroovy();
        String add = "import java.text.SimpleDateFormat\n" +
                "\n" +
                "class GrvyCalculator {\n" +
                "    //java语法\n" +
                "    String add(Map<Integer, String> map){\n" +
                "        SimpleDateFormat sdf = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n" +
                "        Date dateValue = sdf.parse(\"2017-12-08 08:00:00\");\n" +
                "\n" +
                "        String var1 = map.get(0);\n" +
                "        String var2 = map.get(1);\n" +
                "        String var3 = map.get(2);\n" +
                "        StringBuilder sb = new StringBuilder();\n" +
                "        return sb.append(var1).append(\"_\").append(var2).append(\"_\").append(var3).append(\"_\").append(dateValue.format(\"yyyy-MM-dd HH:mm:ss\"));\n" +
                "    }\n" +
                "\n" +
                "}";
        GroovyObject groovyObject = testGroovy.newClazz(add);
        //执行加法脚本
        Map<Integer, String> paramMap = new HashMap<>();
        paramMap.put(0, "语文");
        paramMap.put(1, "数学");
        paramMap.put(2, "英语");
        Object[] params2 = new Object[]{paramMap};
        String result = (String) groovyObject.invokeMethod("add", params2);
        System.out.println("mapToString:" + result);
    }
}
