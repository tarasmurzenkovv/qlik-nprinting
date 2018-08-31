package com.nprinting.model.nprinting.request;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestBody {
    private String type;
    private Config config;
    private Filters filters = new Filters();
    private List<Selection> selections;
    private UUID connectionId;
}
