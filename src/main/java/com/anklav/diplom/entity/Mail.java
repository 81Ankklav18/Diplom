package com.anklav.diplom.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
public class Mail {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column(columnDefinition="VARCHAR(500)")
    private String messageId;
    @Column(columnDefinition="VARCHAR(500)")
    private String label;
    @Column(columnDefinition="VARCHAR(500)")
    private String subject;
    @Column(columnDefinition="VARCHAR(4000)")
    private String body;
    @Column(columnDefinition="VARCHAR(500)")
    private String deliveredTo;
    @Column(columnDefinition="VARCHAR(500)")
    private String envelopeFrom;
    @Column(columnDefinition="VARCHAR(500)")
    private String snippet;
    private Date date;
    @Column(columnDefinition="VARCHAR(4000)")
    private String tree;
}
