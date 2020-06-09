package com.anklav.diplom.dto;

import lombok.Data;

import java.util.List;

//TODO: чекнуть Дата на конструктор
@Data
public class AnalisysDTO {
    double precision;
    double recall;
    double f1;
    List<SemiLatticeViewDTO> semiLatticeViewDTO;

    public AnalisysDTO(double precision, double recall, double f1, List<SemiLatticeViewDTO> semiLatticeViewDTO) {
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
        this.semiLatticeViewDTO = semiLatticeViewDTO;
    }
}
