package com.anklav.diplom.mapper;

import com.anklav.diplom.dto.EditViewDTO;
import com.anklav.diplom.entity.Mail;

public class EditViewMapper {
    public static EditViewDTO MailToEdit(Mail mail) {
        return new EditViewDTO(
                mail.getMessageId(),
                mail.getLabel(),
                mail.getSubject(),
                mail.getBody(),
                mail.getDeliveredTo(),
                mail.getEnvelopeFrom(),
                mail.getDate(),
                mail.getTree());
    }

    public static EditViewDTO MailToGetByID(Mail mail) {
        return new EditViewDTO(
                mail.getId().toString(),
                mail.getLabel(),
                mail.getSubject(),
                mail.getBody(),
                mail.getDeliveredTo(),
                mail.getEnvelopeFrom(),
                mail.getDate(),
                mail.getTree());
    }
}
