
package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    private String content;
    private boolean isPublic;

    public Long getId() { return id; }
    public String getContent() { return content; }
    public boolean isPublic() { return isPublic; }

    public void setContent(String content) { this.content = content; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
}
