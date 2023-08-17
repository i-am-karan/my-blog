package com.blog.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "blogs")
@NamedQueries({
        @NamedQuery(
                name = "com.blog.core.BlogPost.findAll",
                query = "SELECT bp FROM BlogPost bp"
        )
})
public class BlogPost {
    @Id
    private String id;
    @JsonProperty("title")
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @JsonProperty("content")
    @NotBlank(message = " cannot be blank")
    private String content;
    @JsonProperty("username")
    @NotBlank(message = "cannot be blank")
    private String username;

    public BlogPost(){

    }
    public BlogPost(String id, String title, String content, String username){
        this.id=id;
        this.title=title;
        this.content=content;
        this.username=username;
    }
    public void setId(String id){
        this.id=id;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setContent(String Content){
        this.content=Content;
    }
    public void setUsername(String username){
        this.username=username;
    }

    public String getId() {
        return id;
    }
    public String getTitle(){
        return  title;
    }
    public String getContent(){
        return  content;
    }
    public String getUsername(){
        return  username;
    }
}
