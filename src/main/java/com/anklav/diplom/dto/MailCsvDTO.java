package com.anklav.diplom.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class MailCsvDTO {
    @CsvBindByName
    private String label;
    @CsvBindByName
    private String body;
}
