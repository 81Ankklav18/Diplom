package com.anklav.diplom.controller;

import com.anklav.diplom.dto.*;
import com.anklav.diplom.entity.Mail;
import com.anklav.diplom.mapper.MailImportMapper;
import com.anklav.diplom.mapper.TableViewMapper;
import com.anklav.diplom.repository.MailRepository;
import com.anklav.diplom.service.MailService;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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

    @PostMapping("import/json")
    public void importDataJson(@RequestBody List<Mail> mails) {
        for (Mail mail : mails) {
            mailService.createMessage(mailRepository.save(mail));
        }
    }

    @PostMapping("import/csv")
    public ResponseEntity<String> importDataCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Выберите файл для импорта.");
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<MailCsvDTO> csvToBean = new CsvToBeanBuilder<MailCsvDTO>(reader)
                    .withType(MailCsvDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<MailCsvDTO> mails = csvToBean.parse();

            for (MailCsvDTO mail : mails) {
                mailService.createMessage(mailRepository.save(MailImportMapper.CsvToMail(mail)));
            }
            return ResponseEntity.status(HttpStatus.OK).body("Импорт завершен.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка обработки CSV файла.");
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
    public AnalisysDTO classification(@RequestBody ClassificationDTO dto) throws Exception {
        return mailService.classification(dto);
    }

    @PostMapping("similarity")
    public void similarity(@RequestBody SimilarityDTO dto) throws Exception {
        //TODO: similarity
        System.out.println();
    }
}
