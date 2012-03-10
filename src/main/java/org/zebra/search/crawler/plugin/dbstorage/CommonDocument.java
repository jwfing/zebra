package org.zebra.search.crawler.plugin.dbstorage;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.*;

@Entity
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
public class CommonDocument {
    @Transient
    private static final long serialVersionUID = 3294254521331173014L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false, length = 256, updatable = false, unique = true)
    private String url;
    private long downloadTime = 0l;
    @Column(length = 256)
    private String title = "";
    @Column(length = 1024)
    private String description = "";
    @Column(length = 2048)
    private String articleText = "";

    public String toString() {
        return "url=" + this.url + ", title:" + this.title
                + ", description:" + this.description
                + ", articleText:" + this.articleText;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(long downloadTime) {
        this.downloadTime = downloadTime;
    }
}
