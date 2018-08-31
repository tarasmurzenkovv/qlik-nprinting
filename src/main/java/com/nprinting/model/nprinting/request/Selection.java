package com.nprinting.model.nprinting.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Selection {
    private String fieldName;
    private String selectedCount;
    private List<String> selectedValues;
    private String isNumeric;
}
