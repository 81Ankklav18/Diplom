package com.anklav.diplom.controller;

import com.anklav.diplom.dto.ClassificationDTO;
import com.anklav.diplom.dto.EditViewDTO;
import com.anklav.diplom.dto.SimilarityDTO;
import com.anklav.diplom.dto.TableViewDTO;
import com.anklav.diplom.entity.Mail;
import com.anklav.diplom.mapper.TableViewMapper;
import com.anklav.diplom.repository.MailRepository;
import com.anklav.diplom.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("mail")
@CrossOrigin("*")
public class MailController {
    @Autowired
    MailRepository mailRepository;
    @Autowired
    MailService mailService;

    @GetMapping("/db")
    public void getAllFromDB() throws GeneralSecurityException, IOException {
        mailService.runService();
    }

    @GetMapping
    public List<TableViewDTO> getAll() throws GeneralSecurityException, IOException {
        return mailRepository.findAll()
                .stream()
                .map(TableViewMapper::MailToTable)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public EditViewDTO get(@PathVariable("id") String id) {
        return mailService.getById(id);
    }

    @PostMapping
    public Mail create(@RequestBody Mail mail) {
        return mailService.createMessage(mailRepository.save(mail));
    }

    @PostMapping("import")
    public void importDataJson(@RequestBody List<Mail> mails) {
        for (Mail mail : mails) {
            mailService.createMessage(mailRepository.save(mail));
        }
    }

    @GetMapping("export")
    public List<Mail> exportDataJson() {
        return mailRepository.findAll();
    }

    @PutMapping("{id}")
    public Mail update(@PathVariable("id") String id, @RequestBody EditViewDTO editViewDTO) {
        Mail mailFromDb = mailService.getMailById(id);
        return mailRepository.save(mailService.updateMail(mailFromDb, editViewDTO));
    }

    @DeleteMapping
    public void delete(@RequestBody List<String> ids) {
        mailService.deleteMessage(ids);
    }

    @PostMapping("classification")
    public void classification(@RequestBody ClassificationDTO dto) throws Exception {
        mailService.classification(dto.getId());
        System.out.println();
    }

    @PostMapping("similarity")
    public void similarity(@RequestBody SimilarityDTO dto) throws Exception {
        //TODO: similarity
        System.out.println();
    }
}
