package com.anklav.diplom.service;

import com.anklav.diplom.algorithm.CloseByOne;
import com.anklav.diplom.algorithm.Niagara;
import com.anklav.diplom.algorithm.NiagaraObject;
import com.anklav.diplom.dto.EditViewDTO;
import com.anklav.diplom.dto.MailDTO;
import com.anklav.diplom.entity.Mail;
import com.anklav.diplom.mapper.EditViewMapper;
import com.anklav.diplom.mapper.MailMapper;
import com.anklav.diplom.repository.MailRepository;
import com.anklav.diplom.utils.TreeUtils;
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
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.LabeledConstituent;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MailService {
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final String COVID = "COVID";
    private static final String FABLES = "FABLES";

    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final List<Message> resultListForDB = new ArrayList<>();

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

            for (int i = 0; i < 400; i++) {
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
            } catch (Exception ignored) {/*...*/}

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

    public List<Mail> getMailsByIds(List<String> ids) {
        return ids.stream()
                .map(this::getMailById)
                .collect(Collectors.toList());
    }

    public Mail getMailById(String id) {
        Mail mail = null;
        try {
            mail = mailRepository.findAll()
                    .stream()
                    .filter(x -> x.getMessageId().equals(id))
                    .findFirst()
                    .orElseGet(null);
        } catch (Exception ignored) {/*...*/}

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

    public void classification(List<String> ids) {
        Map<Mail, String> classification = new ConcurrentHashMap<>();
        TreeUtils treeUtils = new TreeUtils();

        List<Mail> mailDTOList = getMailsByIds(ids);

        List<Mail> class1 = new ArrayList<>();
        List<Mail> class2 = new ArrayList<>();
        int c1 = 0;
        int c2 = 0;
        int classSize = 15;

        for (int i = 0; i < mailDTOList.size(); i++) {
            if (mailDTOList.get(i).getLabel().equals(FABLES)) {
                if (c1 < classSize) {
                    class1.add(mailDTOList.get(i));
                    c1++;
                }
            } else {
                if (c2 < classSize) {
                    class2.add(mailDTOList.get(i));
                    c2++;
                }
            }
        }

        mailDTOList.removeAll(class1);
        mailDTOList.removeAll(class2);

//        List<Tree> treeDataSet;
//
//        treeDataSet = mailDTOList.stream()
//                .map(x -> {
//                    Annotation document =
//                            new Annotation(x.getBody());
//                    Properties props = new Properties();
//                    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
//                    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//                    pipeline.annotate(document);
//                    List<Tree> treeList = new ArrayList<>();
//                    for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//                        Tree constituencyParse = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
//                        constituencyParse.pennPrint();
//                        treeList.add(constituencyParse);
//                    }
//                    return Map.of(x.getId(), treeList);
//                })
//                .map(y -> {
//                    Label label = new LabeledConstituent();
//                    label.setFromString("ROOT");
//                    final Tree[] tree = new Tree[1];
//                    y.forEach((k, v) -> {
//                        tree[0] = v.get(0);
//                        for (int j = 1; j < v.size(); j++) {
//                            int n = v.get(j).numChildren();
//                            for (int l = 0; l < n; l++) {
//                                tree[0].addChild(v.get(j).getChild(l));
//                            }
//                        }
//                        Mail mail = getMailById(k.toString());
//                        mail.setTree(tree[0].toString());
//                        mailRepository.save(mail);
//                    });
//
//                    return tree[0];
//                })
//        .collect(Collectors.toList());

        fillTrees(ids);

        List<NiagaraObject> niagaraObjectList1 = new ArrayList<>();
        List<NiagaraObject> niagaraObjectList2 = new ArrayList<>();

        ConcurrentHashMap<Set<String>, Tree> matrixOfClass1 = new ConcurrentHashMap<>();
        ConcurrentHashMap<Set<String>, Tree> matrixOfClass2 = new ConcurrentHashMap<>();

        for (Mail mail : class1) {
            if (Tree.valueOf(mail.getTree()) != null)
                matrixOfClass1.put(Set.of(mail.getId().toString()), Tree.valueOf(mail.getTree()));
        }

        for (Mail mail : class2) {
            if (Tree.valueOf(mail.getTree()) != null)
                matrixOfClass2.put(Set.of(mail.getId().toString()), Tree.valueOf(mail.getTree()));
        }

        for (int i = 0; i < class1.size(); i++) {
            niagaraObjectList1.add(new NiagaraObject(Set.of(Long.toString(class1.get(i).getId())),
                    Tree.valueOf(class1.get(i).getTree())));
        }

        for (int i = 0; i < class2.size(); i++) {
            niagaraObjectList2.add(new NiagaraObject(Set.of(Long.toString(class2.get(i).getId())),
                    Tree.valueOf(class1.get(i).getTree())));
        }

        Niagara niagara = new Niagara();
        Map<Set<String>, Tree> niagaraList = niagara.debut(niagaraObjectList1, niagaraObjectList2);

        for (Mail mail : mailDTOList) {
            niagaraList.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    classification.put(mail, FABLES);
                }
            });

            if (!classification.containsKey(mail)) {
                classification.put(mail, COVID);
            }
        }
        System.out.println("Classify END");

        AtomicInteger tpN = new AtomicInteger();
        AtomicInteger tnN = new AtomicInteger();
        AtomicInteger fpN = new AtomicInteger();
        AtomicInteger fnN = new AtomicInteger();

        classification.forEach((k, v) -> {
            if (k.getLabel().equals(v) && v.equals(FABLES)) {
                tpN.getAndIncrement();
            } else {
                tnN.getAndIncrement();
            }

            if (k.getLabel().equals(v) && v.equals(COVID)) {
                fpN.getAndIncrement();
            } else {
                fnN.getAndIncrement();
            }
        });

        double precisionN = (double) tpN.get() / ((double) fpN.get() + (double) fpN.get());
        double recallN = (double) tpN.get() / ((double) fpN.get() + (double) fnN.get());

        double f1N = 2 * ((precisionN * recallN) / (precisionN + recallN));

        System.out.println(f1N);

        CloseByOne closeByOneC1 = new CloseByOne(matrixOfClass1, class1.size());
        CloseByOne closeByOneC2 = new CloseByOne(matrixOfClass2, class2.size());
        System.out.println("CbO1 START");
        Map<Set<String>, Tree> resultCbO1 = closeByOneC1.recursiveCbO(matrixOfClass1);
        System.out.println("CbO1 END");
        System.out.println("CbO2 START");
        Map<Set<String>, Tree> resultCbO2 = closeByOneC2.recursiveCbO(matrixOfClass2);
        System.out.println("CbO2 END");

        System.out.println("Classify START");
//        for (Mail mail : mailDTOList) {
//            resultCbO1.forEach((key, value) -> {
//                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
//                    classification.put(mail, FABLES);
//                }
//            });
//
//            if (!classification.containsKey(mail)) {
//                classification.put(mail, COVID);
//            }
//        }
//        System.out.println("Classify END");
//
//        AtomicInteger tp = new AtomicInteger();
//        AtomicInteger tn = new AtomicInteger();
//        AtomicInteger fp = new AtomicInteger();
//        AtomicInteger fn = new AtomicInteger();
//
//        classification.forEach((k, v) -> {
//            if (k.getLabel().equals(v) && v.equals(FABLES)) {
//                tp.getAndIncrement();
//            } else {
//                tn.getAndIncrement();
//            }
//
//            if (k.getLabel().equals(v) && v.equals(COVID)) {
//                fp.getAndIncrement();
//            } else {
//                fn.getAndIncrement();
//            }
//        });
//
//        double precision = (double)tp.value / ((double)fp.value+(double)fp.value);
//        double recall = (double)tp.value / ((double)fp.value+(double)fn.value);
//
//        double f1 = 2 * ((precision * recall) / (precision + recall));

        for (Mail mail : mailDTOList) {
            resultCbO1.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    classification.put(mail, FABLES);
                }
            });

            if (!classification.containsKey(mail)) {
                classification.put(mail, COVID);
            }
        }
        System.out.println("Classify END");

        AtomicInteger tp = new AtomicInteger();
        AtomicInteger tn = new AtomicInteger();
        AtomicInteger fp = new AtomicInteger();
        AtomicInteger fn = new AtomicInteger();

        classification.forEach((k, v) -> {
            if (k.getLabel().equals(v) && v.equals(FABLES)) {
                tp.getAndIncrement();
            } else {
                tn.getAndIncrement();
            }

            if (k.getLabel().equals(v) && v.equals(COVID)) {
                fp.getAndIncrement();
            } else {
                fn.getAndIncrement();
            }
        });

        double precision = (double) tp.get() / ((double) fp.get() + (double) fp.get());
        double recall = (double) tp.get() / ((double) fp.get() + (double) fn.get());

        double f1 = 2 * ((precision * recall) / (precision + recall));

        System.out.println(f1);

        System.exit(0);
        System.out.println("===================================");

        for (Mail mail : mailDTOList) {
            resultCbO2.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    classification.put(mail, COVID);
                }
            });

            if (!classification.containsKey(mail)) {
                classification.put(mail, FABLES);
            }
        }
        System.out.println("Classify END");

        AtomicInteger tp1 = new AtomicInteger();
        AtomicInteger tn1 = new AtomicInteger();
        AtomicInteger fp1 = new AtomicInteger();
        AtomicInteger fn1 = new AtomicInteger();

        classification.forEach((k, v) -> {
            if (k.getLabel().equals(v) && v.equals(FABLES)) {
                tp1.getAndIncrement();
            } else {
                tn1.getAndIncrement();
            }

            if (k.getLabel().equals(v) && v.equals(COVID)) {
                fp1.getAndIncrement();
            } else {
                fn1.getAndIncrement();
            }
        });

        precision = (double) tp1.get() / ((double) fp1.get() + (double) fp1.get());
        recall = (double) tp1.get() / ((double) fp1.get() + (double) fn1.get());

        f1 = 2 * ((precision * recall) / (precision + recall));

        System.out.println(f1);

        System.out.println("===================================");

        for (Mail mail : mailDTOList) {
            resultCbO1.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    classification.put(mail, FABLES);
                }
            });

            if (!classification.containsKey(mail)) {
                classification.put(mail, COVID);
            }
        }
        System.out.println("Classify END");

        AtomicInteger tp2 = new AtomicInteger();
        AtomicInteger tn2 = new AtomicInteger();
        AtomicInteger fp2 = new AtomicInteger();
        AtomicInteger fn2 = new AtomicInteger();

        classification.forEach((k, v) -> {
            if (k.getLabel().equals(v) && v.equals(FABLES)) {
                tp2.getAndIncrement();
            } else {
                tn2.getAndIncrement();
            }

            if (k.getLabel().equals(v) && v.equals(COVID)) {
                fp2.getAndIncrement();
            } else {
                fn2.getAndIncrement();
            }
        });

        precision = (double) tp2.get() / ((double) fp2.get() + (double) fp2.get());
        recall = (double) tp2.get() / ((double) fp2.get() + (double) fn2.get());

        f1 = 2 * ((precision * recall) / (precision + recall));

        System.out.println(f1);

        System.out.println("===================================");

        for (Mail mail : mailDTOList) {
            resultCbO1.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    classification.put(mail, COVID);
                }
            });

            if (!classification.containsKey(mail)) {
                classification.put(mail, FABLES);
            }
        }
        System.out.println("Classify END");

        AtomicInteger tp3 = new AtomicInteger();
        AtomicInteger tn3 = new AtomicInteger();
        AtomicInteger fp3 = new AtomicInteger();
        AtomicInteger fn3 = new AtomicInteger();

        classification.forEach((k, v) -> {
            if (k.getLabel().equals(v) && v.equals(FABLES)) {
                tp3.getAndIncrement();
            } else {
                tn3.getAndIncrement();
            }

            if (k.getLabel().equals(v) && v.equals(COVID)) {
                fp3.getAndIncrement();
            } else {
                fn3.getAndIncrement();
            }
        });

        precision = (double) tp3.get() / ((double) fp3.get() + (double) fp3.get());
        recall = (double) tp3.get() / ((double) fp3.get() + (double) fn3.get());

        f1 = 2 * ((precision * recall) / (precision + recall));

        System.out.println(f1);

        System.out.println("===================================");

        for (Mail mail : mailDTOList) {
            resultCbO1.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    resultCbO1.forEach((key1, value1) -> {
                        if (!TreeUtils.equalsTrees(value1, treeUtils.treesIntersection(value1, Tree.valueOf(mail.getTree())))) {
                            classification.put(mail, FABLES);
                        } else {
                            if (value.depth() > value1.depth()) {
                                classification.put(mail, FABLES);
                            } else {
                                classification.put(mail, COVID);
                            }
                        }
                    });
                }
            });
        }
        System.out.println("Classify END");

        AtomicInteger tp4 = new AtomicInteger();
        AtomicInteger tn4 = new AtomicInteger();
        AtomicInteger fp4 = new AtomicInteger();
        AtomicInteger fn4 = new AtomicInteger();

        classification.forEach((k, v) -> {
            if (k.getLabel().equals(v) && v.equals(FABLES)) {
                tp4.getAndIncrement();
            } else {
                tn4.getAndIncrement();
            }

            if (k.getLabel().equals(v) && v.equals(COVID)) {
                fp4.getAndIncrement();
            } else {
                fn4.getAndIncrement();
            }
        });

        precision = (double) tp4.get() / ((double) fp4.get() + (double) fp4.get());
        recall = (double) tp4.get() / ((double) fp4.get() + (double) fn4.get());

        f1 = 2 * ((precision * recall) / (precision + recall));

        System.out.println(f1);

        System.out.println("===================================");

        for (Mail mail : mailDTOList) {
            resultCbO1.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    resultCbO1.forEach((key1, value1) -> {
                        if (!TreeUtils.equalsTrees(value1, treeUtils.treesIntersection(value1, Tree.valueOf(mail.getTree())))) {
                            classification.put(mail, COVID);
                        } else {
                            if (value.depth() > value1.depth()) {
                                classification.put(mail, COVID);
                            } else {
                                classification.put(mail, FABLES);
                            }
                        }
                    });
                }
            });
        }
        System.out.println("Classify END");

        AtomicInteger tp5 = new AtomicInteger();
        AtomicInteger tn5 = new AtomicInteger();
        AtomicInteger fp5 = new AtomicInteger();
        AtomicInteger fn5 = new AtomicInteger();

        classification.forEach((k, v) -> {
            if (k.getLabel().equals(v) && v.equals(FABLES)) {
                tp5.getAndIncrement();
            } else {
                tn5.getAndIncrement();
            }

            if (k.getLabel().equals(v) && v.equals(COVID)) {
                fp5.getAndIncrement();
            } else {
                fn5.getAndIncrement();
            }
        });

        precision = (double) tp5.get() / ((double) fp5.get() + (double) fp5.get());
        recall = (double) tp5.get() / ((double) fp5.get() + (double) fn5.get());

        f1 = 2 * ((precision * recall) / (precision + recall));

        System.out.println(f1);
        System.out.println();
    }

    private void fillTrees(List<String> ids) {
        List<String> emptyTreeMails = ids.stream()
                .filter(x -> getMailById(x).getTree() == null)
                .collect(Collectors.toList());

        List<Mail> mailDTOList = getMailsByIds(emptyTreeMails);

        mailDTOList.stream()
                .map(x -> {
                    Annotation document =
                            new Annotation(x.getBody());
                    Properties props = new Properties();
                    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
                    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
                    pipeline.annotate(document);
                    List<Tree> treeList = new ArrayList<>();
                    for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
                        Tree constituencyParse = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
                        constituencyParse.pennPrint();
                        treeList.add(constituencyParse);
                    }
                    return Map.of(x.getId(), treeList);
                })
                .map(y -> {
                    Label label = new LabeledConstituent();
                    label.setFromString("ROOT");
                    final Tree[] tree = new Tree[1];
                    y.forEach((k, v) -> {
                        tree[0] = v.get(0);
                        for (int j = 1; j < v.size(); j++) {
                            int n = v.get(j).numChildren();
                            for (int l = 0; l < n; l++) {
                                tree[0].addChild(v.get(j).getChild(l));
                            }
                        }
                        Mail mail = getMailById(k.toString());
                        mail.setTree(tree[0].toString());
                        mailRepository.save(mail);
                    });

                    return tree[0];
                })
                .collect(Collectors.toList());
    }
}
