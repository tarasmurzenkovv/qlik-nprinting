package com.nprinting.model.nprinting.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Data<T> {
    private List<T> items;
    private Integer totalItems;
    private Integer offset;
    private Integer limit;
}
