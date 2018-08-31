package com.nprinting;

import static com.nprinting.utils.StringUtils.asString;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nprinting.configuration.ApplicationFactoryConfiguration;
import com.nprinting.model.nprinting.request.RequestBody;
import com.nprinting.model.nprinting.request.Selection;
import com.nprinting.service.FilterService;
import com.nprinting.service.NPrintingService;


public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        final ApplicationFactoryConfiguration factory = new ApplicationFactoryConfiguration(args);
        final NPrintingService nPrintingService = factory.getNprintingService();
        LOGGER.info("Started making the  on demand request....");
        final UUID reportId = nPrintingService.getReportId();
        final UUID connectionId = nPrintingService.getConnectionId();
        final RequestBody request = nPrintingService.makeRequest(reportId, connectionId);
        final FilterService filterService = factory.getFilterService();
        final List<Selection> selections = filterService.buildSelections();
        LOGGER.info("Prepared the following filter: '{}'", asString(selections));
        request.setSelections(selections);
        nPrintingService.downloadReport(request);
        LOGGER.info("Done with report retrieving");
    }
}
