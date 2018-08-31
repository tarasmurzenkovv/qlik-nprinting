package com.nprinting.model.constants;

public interface UriConstants {
    String NPRINTING_NTLM_URI = "/api/v1/login/ntlm";
    String CONNECTIONS = "/api/v1/connections";
    String REPORTS = "/api/v1/reports";
    String ONDEMAND = "/api/v1/ondemand/requests";
    String RENDERED_DOCUMENT = "/api/v1/ondemand/requests/%s/result";
    String REQUEST = "/api/v1/ondemand/requests/%s";
    String SCHEMA = "https";
}
