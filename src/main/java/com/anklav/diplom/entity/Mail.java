package com.anklav.diplom.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
public class Mail {
    @Id
    private String id;
    private String label;
    private String subject;
    private String body;
    private String deliveredTo;
    private String envelopeFrom;
    private String snippet;
    private Date date;
}
