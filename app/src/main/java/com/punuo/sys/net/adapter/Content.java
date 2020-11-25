package com.punuo.sys.net.adapter;

public class Content {
    private String title;
    private String content;

    public Content(String title,String content){
        this.title = title;
        this.content = content;
    }
    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
}
