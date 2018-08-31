package com.nprinting.model.nprinting.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Connection {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime created;
    private LocalDateTime lastUpdate;
    private UUID appId;
    private String connectionString;
    private String cacheStatus;
    private String connectionStatus;
}
