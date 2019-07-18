package com.apple.pojo;

/**
 * Package: com.apple.pojo
 * ClassName:User
 * date: 2019/7/18 22:21
 *
 * @author:吴沛恒
 * @version:1.0
 */
public class User {
    private int id;
    private String name;
    private boolean sex;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public void myName(){
        System.out.println("名字是:"+name);
    }
}
