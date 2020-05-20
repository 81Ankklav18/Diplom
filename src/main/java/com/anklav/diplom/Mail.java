package com.anklav.diplom;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Mail {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String text;
}
