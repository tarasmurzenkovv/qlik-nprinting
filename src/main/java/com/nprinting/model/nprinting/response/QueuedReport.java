package com.nprinting.model.nprinting.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class QueuedReport {
    private UUID id;
    private String status;
    private LocalDateTime created;
    private String requestType;
    private String reportType;
    private String outputFormat;
    private String title;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(final String requestType) {
        this.requestType = requestType;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(final String reportType) {
        this.reportType = reportType;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
