package com.huntkey.rx.config.server.model;

/**
 * Created by zhaomj on 2017/4/12.
 */
public class GitAuthor {
    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", email:'" + email + '\'' +
                '}';
    }
}
