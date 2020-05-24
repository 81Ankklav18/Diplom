package com.anklav.diplom.mapper;

import com.anklav.diplom.dto.TableViewDTO;
import com.anklav.diplom.entity.Mail;

public class TableViewMapper {
    public static TableViewDTO MailToTable(Mail mail) {
        return new TableViewDTO(
                mail.getMessageId(),
                mail.getLabel(),
                mail.getSubject(),
                mail.getSnippet(),
                mail.getDate());
    }
}
