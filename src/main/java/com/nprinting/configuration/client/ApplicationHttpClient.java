package com.nprinting.configuration.client;

import static org.apache.http.impl.client.HttpClients.custom;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.nprinting.configuration.SecurityConfiguration;
import com.nprinting.model.ProgramArguments;
import com.nprinting.model.constants.UriConstants;

public class ApplicationHttpClient {

    private final SecurityConfiguration securityConfiguration;
    private final ProgramArguments programArguments;

    public ApplicationHttpClient(final ProgramArguments programArguments,
        final SecurityConfiguration securityConfiguration) {
        this.programArguments = programArguments;
        this.securityConfiguration = securityConfiguration;
    }

    public HttpClient getHttpClient() {
        return buildCloseableHttpClient();
    }

    public HttpHost getTargetHost(){
        return new HttpHost(programArguments.getHostName(), programArguments.getPort(), UriConstants.SCHEMA);
    }

    private HttpClient buildCloseableHttpClient() {
        final Registry<ConnectionSocketFactory> registry = securityConfiguration.getRegistry();
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        int timeout = 5;
        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000).build();
        return custom()
            .setDefaultRequestConfig(config)
            .setConnectionManager(connectionManager)
            .build();
    }
}