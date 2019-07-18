package com.apple.pojo;

/**
 * Package: com.apple.pojo
 * ClassName:Content
 * date: 2019/7/18 9:00
 *
 * @author:吴沛恒
 * @version:1.0
 */
public class Content {
    private Long id;
    private String title;
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
