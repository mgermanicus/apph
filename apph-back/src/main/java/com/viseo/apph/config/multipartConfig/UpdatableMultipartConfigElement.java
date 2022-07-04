package com.viseo.apph.config.multipartConfig;

import javax.servlet.MultipartConfigElement;

public class UpdatableMultipartConfigElement extends MultipartConfigElement {
    // 10485760 = 10MB
    volatile long maxFileSize = 10485760;
    volatile long maxRequestSize = 10485760;

    public UpdatableMultipartConfigElement(String location, long maxFileSize, long maxRequestSize, int fileSizeThreshold) {
        super(location, maxFileSize, maxRequestSize, fileSizeThreshold);
    }

    @Override
    public long getMaxFileSize() {
        return maxFileSize;
    }

    public UpdatableMultipartConfigElement setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
        return this;
    }

    @Override
    public long getMaxRequestSize() {
        return maxRequestSize;
    }

    public UpdatableMultipartConfigElement setMaxRequestSize(long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
        return this;
    }
}
