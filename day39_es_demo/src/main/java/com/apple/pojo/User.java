package com.apple.pojo;

/**
 * Package: com.apple.pojo
 * ClassName:User
 *
 * @author:吴沛恒
 * @version:1.0
 */
public class User {
    private int id;
    private String name;
    private boolean sex;
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void myName() {
        System.out.println("名字是:" + name);
    }
}
