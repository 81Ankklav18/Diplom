package com.anklav.diplom.dto;

import lombok.Data;

@Data
public class AnalisysDTO {
    double precision;
    double recall;
    double f1;

    public AnalisysDTO(double precision, double recall, double f1) {
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
    }
}
