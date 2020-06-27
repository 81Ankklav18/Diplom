package com.anklav.diplom.mapper;

import com.anklav.diplom.dto.MailCsvDTO;
import com.anklav.diplom.entity.Mail;

import java.util.Date;

public class MailImportMapper {
    public static Mail CsvToMail(MailCsvDTO csv) {
        Mail mail = new Mail();
        mail.setBody(csv.getBody());
        mail.setLabel(csv.getLabel());
        mail.setDate(new Date());
        return mail;
    }
}
