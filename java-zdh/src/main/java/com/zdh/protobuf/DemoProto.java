package com.zdh.protobuf;

/**
 * @author zdh
 * @date 2023/1/20 20:01
 * @Version 1.0
 */
public class DemoProto {

    public static void main(String[] args) {
        StudentProto.Student zdh = StudentProto.Student.newBuilder()
                .setAge(12)
                .setName("zdh")
                .build();
        System.out.println(zdh);
        byte[] bytes = zdh.toByteArray();


    }
}
