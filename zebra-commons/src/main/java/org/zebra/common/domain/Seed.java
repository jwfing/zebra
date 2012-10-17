package org.zebra.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.*;

@Entity
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Table(name="zb_seed")
public class Seed {
    @Transient
    private static final long serialVersionUID = 3294254521331773014L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 64, updatable = false, unique = true)
    private String urlMd5;

    @Column(nullable = false, length = 1024, updatable = false)
    private String url;

    @Column(nullable = true)
    private String tags;

    @Column(nullable = true, length = 1024)
    private String strict;

    @Column
    private Long time_created;

    @Column(updatable = true)
    private Long update_period;

    @Column(updatable = true)
    private Long next_fetch;

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getStrict() {
        return strict;
    }

    public void setStrict(String follow_strict) {
        this.strict = follow_strict;
    }

    public Long getTimeCreated() {
        return time_created;
    }

    public void setTimeCreated(Long time_created) {
        this.time_created = time_created;
    }

    public Long getUpdatePeriod() {
        return update_period;
    }

    public void setUpdatePeriod(Long update_period) {
        this.update_period = update_period;
    }

    public Long getNextFetch() {
        return next_fetch;
    }

    public void setNextFetch(Long next_fetch) {
        this.next_fetch = next_fetch;
    }
}
