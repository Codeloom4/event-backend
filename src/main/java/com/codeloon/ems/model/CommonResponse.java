package com.codeloon.ems.model;

import lombok.Data;

@Data
public class CommonResponse {
    private String responseMsg;
    private String responseCode;
    private Object content;
}
