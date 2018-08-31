package com.nprinting.model;

import static com.nprinting.utils.StringUtils.isEmpty;

import java.util.Properties;

import com.nprinting.model.constants.CommandLineConstants;

public class ProgramArguments {
    private final String login;
    private final String password;
    private final String hostName;
    private final int port;
    private final String pathToFolder;

    public static class Builder {

        private Properties properties;

        public Builder withProperties(final Properties properties) {
            if (null == properties) {
                throw new RuntimeException("Cannot build the program arguments. Provide the properties source");
            }
            this.properties = properties;
            return this;
        }

        public ProgramArguments build() {
            return new ProgramArguments(properties);
        }
    }

    private ProgramArguments(final Properties properties) {
        this.login = getProperty(properties, CommandLineConstants.LOGIN);
        this.password = getProperty(properties, CommandLineConstants.PASSWORD);
        this.hostName = getProperty(properties, CommandLineConstants.HOST);
        this.pathToFolder = getProperty(properties, CommandLineConstants.PATH_TO_FOLDER);
        this.port = Integer.parseInt(getProperty(properties, CommandLineConstants.PORT));
    }

    private String getProperty(final Properties properties, final String propertyName) {
        if (isEmpty(properties.getProperty(propertyName))) {
            final String message = String.format("Cannot locate the provided property value '%s' in the file. ", propertyName);
            throw new RuntimeException(message);
        }
        return properties.getProperty(propertyName);
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getPathToFolder() {
        return pathToFolder;
    }

    @Override
    public String toString() {
        return "ProgramArguments{" +
            "login='" + login + '\'' +
            ", password='" + "***************" + '\'' +
            ", hostName='" + hostName + '\'' +
            ", port=" + port +
            ", pathToFolder='" + pathToFolder + '\'' +
            '}';
    }
}
