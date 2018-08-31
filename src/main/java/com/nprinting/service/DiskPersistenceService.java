package com.nprinting.service;

import static com.nprinting.utils.DateUtils.getCurrentDateAsString;
import static java.io.File.separator;
import static java.lang.String.format;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nprinting.model.ProgramArguments;

public class DiskPersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskPersistenceService.class);
    private static final String FILE_SUFFIX = "generated_at_%s_%s.pdf";
    private final ProgramArguments programArguments;

    public DiskPersistenceService(final ProgramArguments programArguments) {
        this.programArguments = programArguments;
    }

    public void saveToFile(final String fileName, final InputStream is) {
        final String filePath = buildFullPathToFile(fileName);
        if (exists(get(filePath))) {
            doDelete(filePath);
        }
        doSaveToFile(is, filePath);
    }

    private void doDelete(final String filePath) {
        try {
            delete(get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doSaveToFile(final InputStream is, final String filePath) {
        try (final FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            LOGGER.info("Started saving content to the following file '{}'", filePath);
            doCopyInputToOutput(is, fos);
            LOGGER.info("Done with saving content to the following file '{}'", filePath);
        } catch (IOException e) {
            LOGGER.error("Error occurred with saving content to the following file '{}'", filePath);
            closeQuietly(is);
            throw new RuntimeException(e);
        }
    }

    private String buildFullPathToFile(final String fileName) {
        final String pathToFolder = programArguments.getPathToFolder();
        final String currentDateAsString = getCurrentDateAsString();
        return format(pathToFolder + separator + FILE_SUFFIX, currentDateAsString, fileName);
    }

    private void doCopyInputToOutput(final InputStream is, final OutputStream fos) throws IOException {
        int inByte;
        while ((inByte = is.read()) != -1) {
            fos.write(inByte);
        }
    }
}
