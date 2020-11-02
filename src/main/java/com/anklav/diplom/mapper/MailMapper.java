package com.anklav.diplom.mapper;

import com.anklav.diplom.dto.MailDTO;
import com.anklav.diplom.entity.Mail;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.apache.http.client.utils.DateUtils;
import org.jsoup.Jsoup;

import java.util.List;
import java.util.stream.Collectors;

public class MailMapper {
    public static List<MailDTO> getViewDTO(List<Message> messageList) {
        return messageList
                .stream()
                .map(x -> {
                    MailDTO mailDTO = new MailDTO();
                    mailDTO.setBody(Jsoup.parseBodyFragment(
                            StringUtils.newStringUtf8(Base64.decodeBase64(x.getPayload().getBody().getData()))).text());
                    mailDTO.setDeliveredTo(
                            x.getPayload().getHeaders()
                                    .stream()
                                    .filter(y -> y.getName().equals("Delivered-To"))
                                    .map(MessagePartHeader::getValue)
                                    .findFirst()
                                    .get());
                    mailDTO.setEnvelopeFrom(
                            x.getPayload().getHeaders()
                                    .stream()
                                    .filter(y -> y.getName().equals("Envelope-From"))
                                    .map(MessagePartHeader::getValue)
                                    .findFirst()
                                    .get());
                    mailDTO.setDate(DateUtils.parseDate(
                            x.getPayload().getHeaders()
                                    .stream()
                                    .filter(y -> y.getName().equals("Date"))
                                    .map(MessagePartHeader::getValue)
                                    .findFirst()
                                    .get()));
                    mailDTO.setId(x.getId());
                    mailDTO.setLabel((x.getLabelIds().contains("Label_2665037630844836558")) ? "COVID" : "FABLES");
                    mailDTO.setSnippet(x.getSnippet());
                    mailDTO.setSubject(
                            x.getPayload().getHeaders()
                            .stream()
                                    .filter(y -> y.getName().equals("Subject"))
                            .map(MessagePartHeader::getValue)
                            .findFirst()
                            .get());
                    return mailDTO;
                })
        .collect(Collectors.toList());
    }

    public static List<Mail> getMailsEntity(List<MailDTO> mailDTOS) {
        return mailDTOS.stream()
                .map(x -> {
                    Mail mail = new Mail();
                    mail.setMessageId(x.getId());
                    mail.setBody(x.getBody());
                    mail.setDate(x.getDate());
                    mail.setDeliveredTo(x.getDeliveredTo());
                    mail.setEnvelopeFrom(x.getEnvelopeFrom());
                    mail.setLabel(x.getLabel());
                    mail.setSnippet(x.getSnippet());
                    mail.setSubject(x.getSubject());
                    return mail;
                })
                .collect(Collectors.toList());
    }
}
