package com.anklav.diplom.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MailDTO {
    String id;
    String label;
    String subject;
    String body;
    String deliveredTo;
    String envelopeFrom;
    String snippet;
    Date date;
}
