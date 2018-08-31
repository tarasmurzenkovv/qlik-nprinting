package com.nprinting.service;

import static java.lang.String.format;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;

import com.nprinting.configuration.SecurityConfiguration;
import com.nprinting.configuration.client.ApplicationHttpClient;
import com.nprinting.model.constants.UriConstants;
import com.nprinting.utils.HeaderUtils;


public class SecurityServiceProvider {

    private static final String HEADER_WITH_TOKEN = "NPWEBCONSOLE_XSRF-TOKEN";
    private HttpClientContext context;
    private final SecurityConfiguration securityConfiguration;

    public SecurityServiceProvider(
        final SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
    }

    public Header[] authenticate(final ApplicationHttpClient httpClient) {
        final CredentialsProvider credsProvider = securityConfiguration.getCredentialProvider();
        context = makeContext(credsProvider);
        return makeAuthentication(httpClient);
    }

    public HttpClientContext getContext() {
        return this.context;
    }

    public <T extends HttpRequestBase> T updateWithToken(T httpRequest){
        final Header securityHeader = HeaderUtils.getSecurityHeader(this.getTokenValue());
        httpRequest.addHeader(securityHeader);
        return httpRequest;
    }

    public void removeContentHeader(){
        final Header contentType = HeaderUtils.getContentTypeHeader();
        context.getRequest().removeHeader(contentType);
    }

    public HttpClientContext addTokenToRequest(final HttpPost httpPost) {
        final Header tokenHeader = HeaderUtils.getSecurityHeader(this.getTokenValue());
        final Header contentType = HeaderUtils.getContentTypeHeader();
        httpPost.addHeader(contentType);
        httpPost.addHeader(tokenHeader);
        context.getRequest().addHeader(contentType);
        context.getRequest().addHeader(tokenHeader);
        return context;
    }

    private Header[] makeAuthentication(final ApplicationHttpClient httpClient) {
        try {
            final HttpGet getRequestForAuthentication = new HttpGet(UriConstants.NPRINTING_NTLM_URI);
            final HttpHost targetHost = httpClient.getTargetHost();
            final HttpResponse authenticationResponse = httpClient.getHttpClient().execute(targetHost,
                getRequestForAuthentication, context);
            return authenticationResponse.getAllHeaders();
        } catch (IOException e) {
            final String message = format("Cannot make the authentication. Reason: '%s'", e.getMessage());
            throw new RuntimeException(message, e);
        }
    }

    public String getTokenValue() {
        final List<Cookie> cookies = context.getCookieStore().getCookies();
        return cookies.stream().filter(cookie -> HEADER_WITH_TOKEN.equals(cookie.getName())).findAny().get().getValue();
    }

    private HttpClientContext makeContext(final CredentialsProvider credsProvider) {
        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        return context;
    }
}
