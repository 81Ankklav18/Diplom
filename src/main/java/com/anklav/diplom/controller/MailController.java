package com.anklav.diplom.controller;

import com.anklav.diplom.entity.Mail;
import com.anklav.diplom.repository.MailRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping("mail")
@CrossOrigin("*")
public class MailController {
    @Autowired
    MailRepository mailRepository;

    @GetMapping
    public List<Mail> getAll() throws GeneralSecurityException, IOException {
        return mailRepository.findAll();
    }

    @GetMapping("{id}")
    public Mail get(@PathVariable("id") Mail mail) {
        return mail;
    }

    //TODO: добавить CRUD-методы

    @PostMapping
    public Mail create(@RequestBody Mail mail) {
        return mailRepository.save(mail);
    }

    @PutMapping
    public Mail update(@PathVariable("id") Mail mailFromDb, @RequestBody Mail mail) {
        BeanUtils.copyProperties(mail, mailFromDb, "id");
        return mailRepository.save(mail);
    }

    //TODO: mail/analyze
}
