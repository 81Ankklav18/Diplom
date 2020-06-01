package com.anklav.diplom.dto;

import lombok.Data;

import java.util.Date;

@Data
public class EditViewDTO {
    String id;
    String label;
    String subject;
    String body;
    String deliveredTo;
    String envelopeFrom;
    Date date;
    String tree;

    public EditViewDTO(String id, String label, String subject, String body, String deliveredTo,
                       String envelopeFrom, Date date, String tree) {
        this.id = id;
        this.label = label;
        this.subject = subject;
        this.body = body;
        this.deliveredTo = deliveredTo;
        this.envelopeFrom = envelopeFrom;
        this.date = date;
        this.tree = tree;
    }
}
