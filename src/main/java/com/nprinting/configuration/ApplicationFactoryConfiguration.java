package com.nprinting.configuration;

import com.nprinting.configuration.client.ApplicationHttpClient;
import com.nprinting.dao.NPrintingDao;
import com.nprinting.model.ProgramArguments;
import com.nprinting.service.ApplicationArgumentsParser;
import com.nprinting.service.DiskPersistenceService;
import com.nprinting.service.FilterService;
import com.nprinting.service.NPrintingService;
import com.nprinting.service.SecurityServiceProvider;

public class ApplicationFactoryConfiguration {

    private final ProgramArguments parsedArguments;
    private final ApplicationArgumentsParser applicationArgumentsParser;

    public ApplicationFactoryConfiguration(String[] args) {
        applicationArgumentsParser = new ApplicationArgumentsParser(args);
        parsedArguments = applicationArgumentsParser.getParsedArguments();
    }

    public NPrintingService getNprintingService() {
        final SecurityConfiguration securityConfiguration = new SecurityConfiguration(parsedArguments);
        final SecurityServiceProvider securityServiceProvider = new SecurityServiceProvider(securityConfiguration);
        final ApplicationHttpClient client = new ApplicationHttpClient(parsedArguments, securityConfiguration);
        final DiskPersistenceService diskPersistenceService = new DiskPersistenceService(parsedArguments);
        final NPrintingDao nPrintingDao = new NPrintingDao(securityServiceProvider, client);
        return new NPrintingService(nPrintingDao, diskPersistenceService);
    }

    public FilterService getFilterService() {
        final String filters = applicationArgumentsParser.getFilters();
        return new FilterService(filters);
    }
}
