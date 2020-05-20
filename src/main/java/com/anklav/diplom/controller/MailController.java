package com.anklav.diplom.controller;

import com.anklav.diplom.Mail;
import com.anklav.diplom.repository.MailRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("mail")
@CrossOrigin("*")
public class MailController {
    @Autowired
    MailRepository mailRepository;

    @GetMapping
    public List<Mail> getAll() {
        return mailRepository.findAll();
    }

    @GetMapping("{id}")
    public Mail get(@PathVariable("id") Mail mail) {
        return mail;
    }

    @PostMapping
    public Mail create(@RequestBody Mail mail) {
        return mailRepository.save(mail);
    }

    @PutMapping
    public Mail update(@PathVariable("id") Mail mailFromDb, @RequestBody Mail mail) {
        BeanUtils.copyProperties(mail, mailFromDb, "id");
        return mailRepository.save(mail);
    }
}
