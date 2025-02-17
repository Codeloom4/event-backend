package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestBean {
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String text;
    private String attachmentPath;
    private List<String> inlineImagesPath;
    private List<List<String>> tableData;
}
