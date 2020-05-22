package com.anklav.diplom.mapper;

import com.anklav.diplom.dto.MailDTO;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.apache.http.client.utils.DateUtils;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MailMapper {
    public static List<MailDTO> getViewDTO(List<Message> messageList) {
        List<MailDTO> mailDTOList = new ArrayList<>();
        messageList
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
                    mailDTOList.add(mailDTO);
                    return mailDTOList;
                })
        .collect(Collectors.toList());

        return mailDTOList;
    }
}
