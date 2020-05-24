package com.anklav.diplom.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TableViewDTO {
    String id;
    String label;
    String subject;
    String snippet;
    Date date;

    public TableViewDTO(String id, String label, String subject, String snippet, Date date) {
        this.id = id;
        this.label = label;
        this.subject = subject;
        this.snippet = snippet;
        this.date = date;
    }
}
