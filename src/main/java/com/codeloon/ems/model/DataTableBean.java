package com.codeloon.ems.model;

import lombok.Data;

import java.util.List;


@Data
public class DataTableBean {

    private long count;
    private long pagecount;
    private List<Object> list;
    private String msg;
    private String code;
}
