package com.anklav.diplom.mapper;

import com.anklav.diplom.dto.MailCsvDTO;
import com.anklav.diplom.entity.Mail;

import java.util.Date;

public class MailShortFormatMapper {
    public static Mail CsvToMail(MailCsvDTO csv) {
        Mail mail = new Mail();
        mail.setBody(csv.getBody());
        mail.setLabel(csv.getLabel());
        mail.setDate(new Date());
        return mail;
    }
    public static MailCsvDTO MailToCsv(Mail mail) {
        MailCsvDTO dto = new MailCsvDTO();
        dto.setBody(mail.getBody());
        dto.setLabel(mail.getLabel());
        return dto;
    }
}
