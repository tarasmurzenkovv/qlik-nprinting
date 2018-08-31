package com.nprinting.utils;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public final class HeaderUtils {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";

    public static Header getContentTypeHeader() {
        return new BasicHeader(CONTENT_TYPE, APPLICATION_JSON);
    }

    public static Header getSecurityHeader(final String token) {
        return new BasicHeader(X_XSRF_TOKEN, token);
    }
}
