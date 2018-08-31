package com.nprinting.service;


import static com.nprinting.utils.JsonUtils.toJson;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nprinting.dao.NPrintingDao;
import com.nprinting.model.nprinting.request.Config;
import com.nprinting.model.nprinting.request.RequestBody;
import com.nprinting.model.nprinting.request.Selection;
import com.nprinting.model.nprinting.response.Connection;
import com.nprinting.model.nprinting.response.Report;
import com.nprinting.model.nprinting.response.Response;
import com.nprinting.model.nprinting.response.ResponseForQueuedFile;
import com.nprinting.utils.StringUtils;

public class NPrintingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NPrintingService.class);
    private final DiskPersistenceService fileSaver;
    private final NPrintingDao nPrintingDao;

    public NPrintingService(final NPrintingDao nPrintingDao, final DiskPersistenceService fileSaver) {
        this.fileSaver = fileSaver;
        this.nPrintingDao = nPrintingDao;
    }

    public synchronized void downloadReport(final RequestBody request) {
        try {
            final ResponseForQueuedFile responseForQueuedFile = placeInQueue(request);
            final UUID reportIdToRender = responseForQueuedFile.getData().getId();
            final String pdfFileName = responseForQueuedFile.getData().getTitle();
            final InputStream inputStream = nPrintingDao.downloadReport(reportIdToRender);
            fileSaver.saveToFile(pdfFileName, inputStream);
            nPrintingDao.deleteRenderedReport(reportIdToRender);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized ResponseForQueuedFile placeInQueue(final RequestBody request) throws IOException {
        LOGGER.info("Started making request to queue report, request '{}'", toJson(request));
        final ResponseForQueuedFile responseForQueuedFile = nPrintingDao.queueReportForRendering(request);
        final UUID reportIdToRender = responseForQueuedFile.getData().getId();
        LOGGER.info("Placed in the queue the following report id '{}'", reportIdToRender);
        return responseForQueuedFile;
    }

    public synchronized UUID getConnectionId() throws IOException {
        LOGGER.info("Going to fetch all connection ids");
        final Response<Connection> allConnections = nPrintingDao.getAllConnections();
        final List<UUID> connectionIds = allConnections.getData().getItems().stream().map(Connection::getId).collect(
            toList());
        LOGGER.info("Fetched all connections ids: '{}'", StringUtils.asString(connectionIds));
        final UUID connectionId = getConnectionId(allConnections);
        LOGGER.info("Choosing the following connection id to work with: '{}'", connectionId);
        return connectionId;
    }

    public synchronized UUID getReportId() throws IOException {
        LOGGER.info("Going to fetch all report ids");
        final Response<Report> allReports = nPrintingDao.getAllReports();
        final List<UUID> ids = allReports.getData().getItems().stream().map(Report::getId).collect(toList());
        LOGGER.info("Fetched all report ids: '{}'", StringUtils.asString(ids));
        final UUID reportId = getReportId(allReports);
        LOGGER.info("Choosing the following report id to work with: '{}'", reportId);
        return reportId;
    }

    private synchronized UUID getConnectionId(final Response<Connection> allConnections) {
        return allConnections.getData().getItems().get(0).getId();
    }

    private synchronized UUID getReportId(final Response<Report> allReports) {
        return allReports.getData().getItems().get(0).getId();
    }

    public synchronized RequestBody makeRequest(final UUID reportId, final UUID connectionId) {

        final Config config = new Config();
        config.setReportId(reportId);
        config.setOutputFormat("pdf");

        final Selection selection = new Selection();
        selection.setFieldName("SSN");
        selection.setSelectedCount("1");
        selection.setSelectedValues(singletonList("722837899"));
        selection.setIsNumeric("true");


        final RequestBody requestBody = new RequestBody();
        requestBody.setType("report");
        requestBody.setConfig(config);
        requestBody.setSelections(singletonList(selection));
        requestBody.setConnectionId(connectionId);
        return requestBody;
    }
}
