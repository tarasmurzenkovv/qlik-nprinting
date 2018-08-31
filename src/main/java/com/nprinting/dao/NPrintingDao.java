package com.nprinting.dao;

import static com.nprinting.model.constants.UriConstants.CONNECTIONS;
import static com.nprinting.model.constants.UriConstants.ONDEMAND;
import static com.nprinting.model.constants.UriConstants.RENDERED_DOCUMENT;
import static com.nprinting.model.constants.UriConstants.REPORTS;
import static com.nprinting.model.constants.UriConstants.REQUEST;
import static com.nprinting.utils.JsonUtils.deserializeConnection;
import static com.nprinting.utils.JsonUtils.deserializeReport;
import static com.nprinting.utils.JsonUtils.readValue;
import static com.nprinting.utils.JsonUtils.toJson;
import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nprinting.configuration.client.ApplicationHttpClient;
import com.nprinting.model.nprinting.request.RequestBody;
import com.nprinting.model.nprinting.response.Connection;
import com.nprinting.model.nprinting.response.Report;
import com.nprinting.model.nprinting.response.Response;
import com.nprinting.model.nprinting.response.ResponseForQueuedFile;
import com.nprinting.service.SecurityServiceProvider;
import com.nprinting.model.constants.HttpStatusCode;

public class NPrintingDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(NPrintingDao.class);
    private final SecurityServiceProvider securityServiceProvider;
    private final HttpHost httpHost;
    private final HttpClient client;
    private final HttpClientContext context;
    private final ApplicationHttpClient applicationClient;

    public NPrintingDao(final SecurityServiceProvider securityServiceProvider,
        final ApplicationHttpClient applicationClient) {
        this.securityServiceProvider = securityServiceProvider;
        LOGGER.info("Started making authentication");
        final Header[] authenticate = this.securityServiceProvider.authenticate(applicationClient);
        LOGGER.info("Done with authentication. Headers size is {}", authenticate.length);
        this.httpHost = applicationClient.getTargetHost();
        this.client = applicationClient.getHttpClient();
        this.applicationClient = applicationClient;
        this.context = this.securityServiceProvider.getContext();
        LOGGER.info("Got the following X-RSF-TOKEN value: {}", this.securityServiceProvider.getTokenValue());
    }

    public Response<Connection> getAllConnections() throws IOException {
        final String json = issueGetRequest(CONNECTIONS);
        return deserializeConnection(json);
    }

    public Response<Report> getAllReports() throws IOException {
        final String json = issueGetRequest(REPORTS);
        return deserializeReport(json);
    }

    public ResponseForQueuedFile queueReportForRendering(final RequestBody requestBodyForFile)
        throws IOException {
        final HttpPost httpPost = buildHttpPostRequest(requestBodyForFile);
        final HttpClientContext context = this.securityServiceProvider.addTokenToRequest(httpPost);
        final HttpResponse response = this.client.execute(httpHost, httpPost, context);
        if (response.getStatusLine().getStatusCode() == HttpStatusCode.INTERNAL_SERVER_ERROR) {
            throw new RuntimeException("Cannot place report in queue, the internal server error occurred.");
        }
        final String jsonResponse = EntityUtils.toString(response.getEntity());
        return readValue(jsonResponse, ResponseForQueuedFile.class);
    }

    public InputStream downloadReport(final UUID reportIdToRender) throws IOException {
        LOGGER.info("Going to download the following report id from the queue '{}'", reportIdToRender);
        final HttpGet httpGetForRenderedFile = new HttpGet(format(RENDERED_DOCUMENT, reportIdToRender));
        HttpResponse response = client.execute(httpHost, httpGetForRenderedFile, context);
        StatusLine statusLine = response.getStatusLine();
        AtomicInteger numberOfTries = new AtomicInteger();
        int statusCode = statusLine.getStatusCode();
        LOGGER.info("Status code while downloading '{}' for report id '{}'", statusCode, reportIdToRender);
        while (statusCode != HttpStatusCode.OK) {
            response = executeWithTimeOut(httpGetForRenderedFile, numberOfTries);
            statusCode = response.getStatusLine().getStatusCode();
        }
        LOGGER.info("Status code while downloading '{}' for report id '{}'", statusCode, reportIdToRender);
        final HttpEntity entity = response.getEntity();
        return entity.getContent();
    }

    public void deleteRenderedReport(final UUID reportIdToRender) throws IOException {
        LOGGER.info("Going to delete the following report id from the queue {}", reportIdToRender);
        HttpDelete httpDelete = new HttpDelete(format(REQUEST, reportIdToRender));
        this.securityServiceProvider.removeContentHeader();
        httpDelete = this.securityServiceProvider.updateWithToken(httpDelete);
        HttpResponse deleteResponse = null;
        AtomicInteger numberOfTries = new AtomicInteger();
        try {
            deleteResponse = client.execute(httpHost, httpDelete, context);
            LOGGER.info("Current number of tries: '{}'", numberOfTries.incrementAndGet());
        } catch (ConnectionPoolTimeoutException e) {
            deleteResponse = startDataPolling(httpDelete, deleteResponse, numberOfTries);
        }
        LOGGER.info("Https status of the response for the the delete is '{}' for the report id '{}'",
            deleteResponse.getStatusLine().getStatusCode(), reportIdToRender);
    }

    private HttpResponse startDataPolling(final HttpDelete httpDelete, HttpResponse deleteResponse,
        final AtomicInteger numberOfTries) throws IOException {
        int statusCode = HttpStatusCode.FAKE_STATUS;
        while (statusCode != HttpStatusCode.OK) {
            deleteResponse = executeWithTimeOut(httpDelete, numberOfTries);
            statusCode = deleteResponse.getStatusLine().getStatusCode();
        }
        return deleteResponse;
    }

    private <T extends HttpRequestBase> HttpResponse executeWithTimeOut(final T httpRequest,
        final AtomicInteger numberOfTries)
        throws IOException {
        HttpResponse response;
        try {
            response = client.execute(httpHost, httpRequest, context);
            LOGGER.info("Current number of tries: '{}'", numberOfTries.incrementAndGet());
        } catch (ConnectionPoolTimeoutException exception) {
            response = applicationClient.getHttpClient().execute(httpHost, httpRequest, context);
            LOGGER.info("Current number of tries for time out exception: '{}'", numberOfTries.incrementAndGet());
        }
        return response;
    }


    private HttpPost buildHttpPostRequest(final RequestBody requestBodyForFile) throws UnsupportedEncodingException {
        final HttpPost httpPost = new HttpPost(ONDEMAND);
        final String requestForFile = toJson(requestBodyForFile);
        httpPost.setEntity(new StringEntity(requestForFile));
        return httpPost;
    }

    private String issueGetRequest(final String uri) throws IOException {
        final HttpRequest httpRequest = new HttpGet(uri);
        final HttpResponse response = client.execute(httpHost, httpRequest, context);
        return EntityUtils.toString(response.getEntity());
    }
}
