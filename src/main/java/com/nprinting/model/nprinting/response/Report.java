package com.nprinting.model.nprinting.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Report {
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime lastUpdate;
    private String title;
    private String description;
    private String type;
    private List<String> outputFormats;
    private UUID entityId;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(final LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public List<String> getOutputFormats() {
        return outputFormats;
    }

    public void setOutputFormats(final List<String> outputFormats) {
        this.outputFormats = outputFormats;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(final UUID entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return "Report{" +
            "id=" + id +
            ", created=" + created +
            ", lastUpdate=" + lastUpdate +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", type='" + type + '\'' +
            ", outputFormats=" + outputFormats +
            ", entityId=" + entityId +
            '}';
    }
}
