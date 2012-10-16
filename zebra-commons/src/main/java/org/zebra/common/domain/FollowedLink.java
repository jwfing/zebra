package org.zebra.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = false)
public class FollowedLink {
    @Transient
    private static final long serialVersionUID = 3294254512031773014L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false, length = 64, updatable = false, unique = true)
    private String urlMd5;
    @Column(nullable = false, length = 1024, updatable = false)
    private String url;
    @Column(nullable = false, length = 1024, updatable = false)
    private String seedUrl;
    @Column
    private String tags;
    @Column
    private String comments;
    @Column
    private Long timeCreated;

    public String getUrlMd5() {
        return urlMd5;
    }
    public void setUrlMd5(String urlMd5) {
        this.urlMd5 = urlMd5;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getSeedUrl() {
        return seedUrl;
    }
    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public Long getTimeCreated() {
        return timeCreated;
    }
    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
