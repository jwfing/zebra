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
    private static int MAX_URL_LEN = 256;
    private static int MAX_TITLE_LEN = 256;
    private static int MAX_DESC_LEN = 1024;
    private static int MAX_ARTICLE_LEN = 2048;

    @Transient
    private static final long serialVersionUID = 3294254521331173014L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false, length = 64, updatable = false, unique = true)
    private String urlMd5;
    @Column(nullable = false, length = 256, updatable = false)
    private String sourceUrl;
    @Column(nullable = false, length = 256, updatable = false)
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
        if (null != this.title && this.title.length() > MAX_TITLE_LEN) {
            this.title = this.title.substring(0, MAX_TITLE_LEN - 1);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (null != this.description && this.description.length() > MAX_DESC_LEN) {
            this.description = this.description.substring(0, MAX_DESC_LEN - 1);
        }
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
        if (null != this.articleText && this.articleText.length() > MAX_ARTICLE_LEN) {
            this.articleText = this.articleText.substring(0, MAX_ARTICLE_LEN);
        }
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
    public String getUrlMd5() {
        return urlMd5;
    }
    public void setUrlMd5(String urlMd5) {
        this.urlMd5 = urlMd5;
    }
    public String getSourceUrl() {
        return sourceUrl;
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
