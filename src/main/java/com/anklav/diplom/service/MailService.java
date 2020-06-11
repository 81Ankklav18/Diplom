package com.anklav.diplom.service;

import com.anklav.diplom.algorithm.CloseByOne;
import com.anklav.diplom.algorithm.Niagara;
import com.anklav.diplom.algorithm.NiagaraObject;
import com.anklav.diplom.algorithm.Norris;
import com.anklav.diplom.dto.*;
import com.anklav.diplom.entity.Mail;
import com.anklav.diplom.enums.ChooseAlgorithm;
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
        fillTrees(Collections.singletonList(editViewDTO.getId()), Boolean.TRUE);

        return mailFromDb;
    }

    public AnalisysDTO classification(ClassificationDTO dto) {
        List<Mail> mailDTOList = getMailsByIds(dto.getId());
        Map<Set<String>, Tree> semiLatticePlus = null;
        Map<Set<String>, Tree> semiLatticeMinus = null;
        Map<Set<String>, Tree> semiLatticeWithAdd = new HashMap<>();

        List<Mail> class1 = new ArrayList<>();
        List<Mail> class2 = new ArrayList<>();
        int c1 = 0;
        int c2 = 0;
        int classSize = ((mailDTOList.size() * dto.getTrainPercent()) / 100) / 2;
//        if (dto.getMethod().equals(ChooseAlgorithm.NIAGARA.name())) {
//            classSize = 20;
//        } else {
//        classSize = 7;
//        }

        for (Mail value : mailDTOList) {
            if (value.getLabel().equals(FABLES)) {
                if (c1 < classSize) {
                    class1.add(value);
                    c1++;
                }
            } else {
                if (c2 < classSize) {
                    class2.add(value);
                    c2++;
                }
            }
        }

        boolean flag = Boolean.TRUE;

        mailDTOList.removeAll(class1);
        mailDTOList.removeAll(class2);

        fillTrees(dto.getId(), Boolean.FALSE);

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

        Map<Mail, String> classification = new HashMap<>();
        if (dto.getMethod().equals(ChooseAlgorithm.CB0.name())) {
            System.out.println("CbO START");
            long l = System.currentTimeMillis();
            semiLatticePlus = getCbOSemiLattice(class1, matrixOfClass1);
            semiLatticeMinus = getCbOSemiLattice(class2, matrixOfClass2);
            Map<Set<String>, Tree> finalSemiLatticeMinus = semiLatticeMinus;
            semiLatticePlus.forEach((k1, v1) -> {
                finalSemiLatticeMinus.forEach((k2, v2) -> {
                    if (!TreeUtils.equalsTrees(v1, v2)) {
                        semiLatticeWithAdd.putAll(Map.of(k1, v1));
                    } else {
                        System.out.println("Wrong: " + k1.toString());
                    }
                });
            });

            if (flag) {
                Map<Set<String>, Tree> temp = new HashMap<>();
                semiLatticePlus.forEach((k, v) -> {
                    if (k.size() == classSize) {
                        temp.put(k, v);
                    }
                });
                semiLatticePlus = temp;
            }
            System.out.println("CbO CbOEnd " + (double) (System.currentTimeMillis() - l));
            classification = CbOClassification(semiLatticeWithAdd, mailDTOList);

        } else if (dto.getMethod().equals(ChooseAlgorithm.NORRIS.name())) {
            System.out.println("Norris START");
            long l = System.currentTimeMillis();
            semiLatticePlus = getNorris(matrixOfClass1);
            //TODO: duplicate
            semiLatticeMinus = getNorris(matrixOfClass2);
            Map<Set<String>, Tree> finalSemiLatticeMinus = semiLatticeMinus;
            semiLatticePlus.forEach((k1, v1) -> {
                finalSemiLatticeMinus.forEach((k2, v2) -> {
                    if (!TreeUtils.equalsTrees(v1, v2)) {
                        semiLatticeWithAdd.putAll(Map.of(k1, v1));
                    } else {
                        System.out.println("Wrong: " + k1.toString());
                    }
                });
            });

            if (flag) {
                Map<Set<String>, Tree> temp = new HashMap<>();
                semiLatticePlus.forEach((k, v) -> {
                    if (k.size() == classSize) {
                        temp.put(k, v);
                    }
                });
                semiLatticePlus = temp;
            }
            System.out.println("Norris NorrisEnd " + (double) (System.currentTimeMillis() - l));
            classification = NorrisClassification(semiLatticeWithAdd, mailDTOList);

        } else if (dto.getMethod().equals(ChooseAlgorithm.NIAGARA.name())) {
            System.out.println("Niagara start");
            if (classSize == 3) {
                long l = System.currentTimeMillis();
                semiLatticePlus = getNiagara(class1, matrixOfClass1);
                System.out.println("Nia NiaEnd " + (double) (System.currentTimeMillis() - l));
            } else {
                Norris norris = new Norris();
                long l = System.currentTimeMillis();
                semiLatticePlus = norris.getNorris(matrixOfClass1);
                System.out.println("Nia NiaEnd " + (double) (System.currentTimeMillis() - l));
            }

            Map<Set<String>, Tree> temp = new HashMap<>();
            semiLatticePlus.forEach((k, v) -> {
                if (k.size() == classSize) {
                    temp.put(k, v);
                }
            });
            semiLatticePlus = temp;
            Map<Set<String>, Tree> finalSemiLatticeMinus = semiLatticeMinus;

            classification = NiagaraClassification(semiLatticePlus, mailDTOList);
        }

        assert semiLatticePlus != null;
        semiLatticePlus = semiLatticePlus
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().size()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

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

        System.out.println("===================================");

        List<SemiLatticeViewDTO> semiLatticeViewDTOS = new ArrayList<>();
        AtomicInteger cnt = new AtomicInteger();
        semiLatticePlus.forEach((k, v) -> {
            cnt.getAndIncrement();
            semiLatticeViewDTOS.add(new SemiLatticeViewDTO(k.toString(), k, v.pennString()));
        });

        return new AnalisysDTO(precision, recall, f1, semiLatticeViewDTOS);
    }

    private void fillTrees(List<String> ids, boolean update) {
        List<String> emptyTreeMails;
        List<Mail> mailDTOList;

        if (!update) {
            emptyTreeMails = ids.stream()
                    .filter(x -> getMailById(x).getTree() == null)
                    .collect(Collectors.toList());
            mailDTOList = getMailsByIds(emptyTreeMails);
        } else {
            mailDTOList = getMailsByIds(ids);
        }

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
                        mail.setTree(tree[0].pennString());
                        mailRepository.save(mail);
                    });

                    return tree[0];
                })
                .collect(Collectors.toList());
    }

    private Map<Set<String>, Tree> getCbOSemiLattice(
            List<Mail> class1, ConcurrentHashMap<Set<String>, Tree> matrixOfClass1) {
//        System.out.println("CbO START");
//        long l = System.currentTimeMillis();
        CloseByOne closeByOneC1 = new CloseByOne(matrixOfClass1, class1.size());
//        System.out.println("CbO CbOEnd " + (double) (System.currentTimeMillis() - l));

        return closeByOneC1.recursiveCbO(matrixOfClass1);
    }

    private Map<Set<String>, Tree> getNorris(ConcurrentHashMap<Set<String>, Tree> matrixOfClass1) {
        Norris norris = new Norris();

//        System.out.println("Norris START");
//        long l = System.currentTimeMillis();
        Map<Set<String>, Tree> treeMap = norris.getNorris(matrixOfClass1);
//        System.out.println("Norris NorrisEnd " + (double) (System.currentTimeMillis() - l));

        return treeMap;
    }

    private Map<Set<String>, Tree> getNiagara(List<Mail> class1, ConcurrentHashMap<Set<String>, Tree> matrixOfClass1) {
        System.out.println("Niagara start");
        TreeUtils treeUtils = new TreeUtils();
        List<NiagaraObject> niagaraObjectList1 = new ArrayList<>();
        List<NiagaraObject> niagaraObjectList2 = new ArrayList<>();

        for (Mail mail : class1) {
            if (Tree.valueOf(mail.getTree()) != null)
                matrixOfClass1.put(Set.of(mail.getId().toString()), Tree.valueOf(mail.getTree()));
        }

//        for (Mail mail : class2) {
//            if (Tree.valueOf(mail.getTree()) != null)
//                matrixOfClass2.put(Set.of(mail.getId().toString()), Tree.valueOf(mail.getTree()));
//        }

        for (Mail item : class1) {
            niagaraObjectList1.add(new NiagaraObject(Set.of(Long.toString(item.getId())),
                    Tree.valueOf(item.getTree())));
        }

//        for (Mail item : class2) {
//            niagaraObjectList2.add(new NiagaraObject(Set.of(Long.toString(item.getId())),
//                    Tree.valueOf(item.getTree())));
//        }

        Niagara niagara = new Niagara();
//        long l = System.currentTimeMillis();
        Map<Set<String>, Tree> niagaraList = niagara.startNiagara(niagaraObjectList1, niagaraObjectList2);
//        System.out.println("Nia NiaEnd " + (double) (System.currentTimeMillis() - l));

        return niagaraList;

    }

    private Map<Mail, String> CbOClassification(Map<Set<String>, Tree> resultCbO1,
                                                List<Mail> mailDTOList) {
        TreeUtils treeUtils = new TreeUtils();
        Map<Mail, String> classification = new ConcurrentHashMap<>();
        System.out.println("Classify START");
        long l = System.currentTimeMillis();
        for (Mail mail : mailDTOList) {
            resultCbO1.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    classification.put(mail, FABLES);
//                    System.out.println(mail.toString() + " " + FABLES);
                }
            });

            if (!classification.containsKey(mail)) {
                classification.put(mail, COVID);
//                System.out.println(mail.toString() + " " + COVID);
            }
        }
        System.out.println("CbO classEnd" + (double) (System.currentTimeMillis() - l));
        System.out.println("Classify END");

        return classification;
    }

    private Map<Mail, String> NorrisClassification(Map<Set<String>, Tree> resultNorris1,
                                                   List<Mail> mailDTOList) {
        TreeUtils treeUtils = new TreeUtils();
        Map<Mail, String> classification = new ConcurrentHashMap<>();

        long l = System.currentTimeMillis();
        for (Mail mail : mailDTOList) {
            resultNorris1.forEach((key, value) -> {
                if (TreeUtils.equalsTrees(value, treeUtils.treesIntersection(value, Tree.valueOf(mail.getTree())))) {
                    classification.put(mail, FABLES);
//                    System.out.println(mail.toString() + " " + FABLES);
                }
            });

            if (!classification.containsKey(mail)) {
                classification.put(mail, COVID);
//                System.out.println(mail.toString() + " " + COVID);
            }
        }
        System.out.println("Classify END");
        System.out.println("Norr classEnd" + (double) (System.currentTimeMillis() - l));

        return classification;
    }

    private Map<Mail, String> NiagaraClassification(Map<Set<String>, Tree> niagaraList, List<Mail> mailDTOList) {
        Map<Mail, String> classification = new ConcurrentHashMap<>();
        TreeUtils treeUtils = new TreeUtils();

        long l = System.currentTimeMillis();
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
        System.out.println("Nia classEnd" + (double) (System.currentTimeMillis() - l));
        System.out.println("Classify END");

        return classification;
    }
}
