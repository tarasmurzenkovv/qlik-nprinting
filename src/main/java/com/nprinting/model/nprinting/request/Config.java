package com.nprinting.model.nprinting.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Config {
    private UUID reportId;
    private String outputFormat = "PDF";
}
