package com.nprinting.configuration;


import static org.apache.http.auth.AuthScope.ANY;
import static org.apache.http.ssl.SSLContexts.custom;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;

import com.nprinting.model.ProgramArguments;
import com.nprinting.model.constants.ApplicationConstants;
import com.nprinting.model.constants.UriConstants;

public class SecurityConfiguration {
    private final ProgramArguments programArguments;

    public SecurityConfiguration(
        final ProgramArguments programArguments) {
        this.programArguments = programArguments;
    }

    public Registry<ConnectionSocketFactory> getRegistry() {
        try {
            SSLContext sslContext = buildSslSelfSignedContext();
            return noOpSslByPassConnection(sslContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CredentialsProvider getCredentialProvider() {
        final CredentialsProvider provider = new BasicCredentialsProvider();
        final NTCredentials ntcCredentials = getNtcCredentials(programArguments);
        provider.setCredentials(ANY, ntcCredentials);
        return provider;
    }

    private Registry<ConnectionSocketFactory> noOpSslByPassConnection(final SSLContext sslContext) {
        final SSLConnectionSocketFactory sslConnectionSelfSignedFactory = new SSLConnectionSocketFactory(sslContext,
            NoopHostnameVerifier.INSTANCE);
        return RegistryBuilder.<ConnectionSocketFactory>create().register(UriConstants.SCHEMA, sslConnectionSelfSignedFactory).build();
    }

    private SSLContext buildSslSelfSignedContext()
        throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        return custom().loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE).build();
    }

    private NTCredentials getNtcCredentials(final ProgramArguments programArguments) {
        return new NTCredentials(programArguments.getLogin(), programArguments.getPassword(),
            programArguments.getLogin(),
            ApplicationConstants.DOMAIN);
    }
}
