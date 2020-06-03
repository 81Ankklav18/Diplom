package com.anklav.diplom.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @Column(columnDefinition="VARCHAR(10000)")
    private String body;
    @Column(columnDefinition="VARCHAR(500)")
    private String deliveredTo;
    @Column(columnDefinition="VARCHAR(500)")
    private String envelopeFrom;
    @Column(columnDefinition="VARCHAR(500)")
    private String snippet;
    @JsonDeserialize(using= CustomerDateAndTimeDeserialize.class)
    private Date date;
    @Column(columnDefinition="VARCHAR(10000)")
    private String tree;
}
