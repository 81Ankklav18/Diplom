package com.anklav.diplom.service;

import com.anklav.diplom.dto.EditViewDTO;
import com.anklav.diplom.dto.MailDTO;
import com.anklav.diplom.entity.Mail;
import com.anklav.diplom.mapper.EditViewMapper;
import com.anklav.diplom.mapper.MailMapper;
import com.anklav.diplom.repository.MailRepository;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailService {
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static List<Message> resultListForDB = new ArrayList<>();

    private static JsonMapper jsonMapper = new JsonMapper();

    @Autowired
    MailRepository mailRepository;

    public void getEmails() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        try {
            ListMessagesResponse response = service.users().messages().list("me").execute();

            List<Message> messages = new ArrayList<Message>();
            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = service.users().messages().list("me").setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }

//            for (Message message : messages) {
////                System.out.println(message.getId());
//                Message test = service.users().messages().get("me", message.getId()).setFormat("full").execute();
////                System.out.println(StringUtils.newStringUtf8(Base64.decodeBase64(test.getPayload().getBody().getData())));
////                System.out.println(test.getSnippet());
//                resultListForDB.add(test);
//            }

            for (int i = 0; i < 300; i++) {
                Message test = service.users().messages().get("me", messages.get(i).getId()).setFormat("full").execute();
                resultListForDB.add(test);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<MailDTO> mailDTOList = MailMapper.getViewDTO(resultListForDB
                .stream()
                .filter(y -> y.getLabelIds().contains("Label_2665037630844836558")
                        || y.getLabelIds().contains("Label_3743344143877711668"))
                .collect(Collectors.toList()));

//        jsonMapper.writeValueAsString(mailDTOList);

        mailRepository.saveAll(MailMapper.getMailsEntity(mailDTOList));
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = MailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void runService() throws GeneralSecurityException, IOException {
        getEmails();
    }

    public Mail createMessage(Mail mail) {
        mail.setSnippet(mail.getBody().length() > 300 ? mail.getBody().substring(0, 300) : mail.getBody());
        mail.setMessageId(mail.getId().toString());

        mailRepository.save(mail);

        return mail;
    }

    public void deleteMessage(List<String> ids) {
        Long mailId = null;

        for (int i = 0; i < ids.size(); i++) {
            try {
                int finalI = i;
                mailId = mailRepository.findAll()
                        .stream()
                        .filter(x -> x.getMessageId().equals(ids.get(finalI)))
                        .map(Mail::getId)
                        .findFirst()
                        .get();
            } catch (Exception e) {

            }

            if (mailId == null) {
                mailId = mailRepository.getOne(Long.valueOf(ids.get(i))).getId();
            }

            mailRepository.deleteById(mailId);
        }
    }

    public EditViewDTO getById(String id) {
        Mail mail = getMailById(id);

        return EditViewMapper.MailToEdit(mail);
    }

    public Mail getMailById(String id) {
        Mail mail = null;
        try {
            mail = mailRepository.findAll()
                    .stream()
                    .filter(x -> x.getMessageId().equals(id))
                    .findFirst()
                    .orElseGet(null);
        } catch (Exception e) {

        }


        if (mail == null) {
            mail = mailRepository.getOne(Long.valueOf(id));
        }

        return mail;
    }

    public Mail updateMail(Mail mailFromDb, EditViewDTO editViewDTO) {
        mailFromDb.setBody(editViewDTO.getBody());
        mailFromDb.setDate(editViewDTO.getDate());
        mailFromDb.setDeliveredTo(editViewDTO.getDeliveredTo());
        mailFromDb.setEnvelopeFrom(editViewDTO.getEnvelopeFrom());
        mailFromDb.setLabel(editViewDTO.getLabel());
        mailFromDb.setSubject(editViewDTO.getSubject());
        mailFromDb.setSnippet(editViewDTO.getBody().length() > 300 ?
                editViewDTO.getBody().substring(0, 300) : editViewDTO.getBody());
        mailFromDb.setMessageId(editViewDTO.getId());

        return mailFromDb;
    }
}
