package com.viseo.apph.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "setting")
public class Setting extends BaseEntity {
    long uploadSize;
    long downloadSize;

    public Setting() {
        super();
    }

    public long getUploadSize() {
        return uploadSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public Setting setUploadSize(long uploadSize) {
        this.uploadSize = uploadSize;
        return this;
    }

    public Setting setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
        return this;
    }
}
