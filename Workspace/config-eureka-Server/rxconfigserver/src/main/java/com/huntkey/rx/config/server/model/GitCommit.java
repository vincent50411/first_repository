package com.huntkey.rx.config.server.model;

import java.util.Set;

/**
 * Created by zhaomj on 2017/4/12.
 */
public class GitCommit {
    private String id;
    private String message;
    private String timestamp;
    private String url;
    private GitAuthor author;
    private Set<String> added;
    private Set<String> modified;
    private Set<String> removed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GitAuthor getAuthor() {
        return author;
    }

    public void setAuthor(GitAuthor author) {
        this.author = author;
    }

    public Set<String> getAdded() {
        return added;
    }

    public void setAdded(Set<String> added) {
        this.added = added;
    }

    public Set<String> getModified() {
        return modified;
    }

    public void setModified(Set<String> modified) {
        this.modified = modified;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    public void setRemoved(Set<String> removed) {
        this.removed = removed;
    }

    @Override
    public String toString() {
        return "{" +
                "id:'" + id + '\'' +
                ", message:'" + message + '\'' +
                ", timestamp:'" + timestamp + '\'' +
                ", url:'" + url + '\'' +
                ", author:" + author.toString() +
                ", added:" + added +
                ", modified:" + modified +
                ", removed:" + removed +
                '}';
    }
}
