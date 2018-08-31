package com.nprinting.service;

import static com.nprinting.utils.PropertiesUtils.loadFromFile;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nprinting.model.ProgramArguments;
import com.nprinting.model.constants.CommandLineConstants;


public class ApplicationArgumentsParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationArgumentsParser.class);
    private final CommandLineParser parser = new GnuParser();
    private final Options options = this.buildOptions();
    private final CommandLine parsedCommandLine;

    public ApplicationArgumentsParser(final String[] args) {
        parsedCommandLine = this.parseCommandLineArguments(args);
    }

    public ProgramArguments getParsedArguments() {
        return (parsedCommandLine.hasOption(CommandLineConstants.FILE)) ? doMakeProgramArguments() : doHandleException();
    }

    public String getFilters() {
        return this.parsedCommandLine.getOptionValue(CommandLineConstants.FILTERS);
    }

    private ProgramArguments doHandleException() {
        this.printHelp();
        throw new RuntimeException("Provide the path to the application properties file. ");
    }

    private ProgramArguments doMakeProgramArguments() {
        final String pathToPropertiesFile = parsedCommandLine.getOptionValue(CommandLineConstants.FILE);
        final Properties properties = loadFromFile(pathToPropertiesFile);
        return new ProgramArguments.Builder().withProperties(properties).build();
    }

    private CommandLine parseCommandLineArguments(String[] commandLineArguments) {
        try {
            return parser.parse(options, commandLineArguments);
        } catch (ParseException exp) {
            LOGGER.error("Parsing failed.  Reason: {}", exp.getMessage(), exp);
            throw new RuntimeException(exp);
        }
    }

    private Options buildOptions() {
        return new Options()
            .addOption(this.pathToPropertiesOption())
            .addOption(this.filtersDescription());
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar nprinting_connector.jar", options);
    }

    private Option pathToPropertiesOption() {
        return new Option("f", CommandLineConstants.FILE, true, "Path to properties file that contains login, password and url");
    }

    private Option filtersDescription() {
        return new Option("s", CommandLineConstants.FILTERS, true, "String representation of the filters, might be null");
    }
}
