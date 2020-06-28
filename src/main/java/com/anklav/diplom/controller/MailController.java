package com.anklav.diplom.controller;

import com.anklav.diplom.dto.*;
import com.anklav.diplom.entity.Mail;
import com.anklav.diplom.mapper.MailShortFormatMapper;
import com.anklav.diplom.mapper.TableViewMapper;
import com.anklav.diplom.repository.MailRepository;
import com.anklav.diplom.service.MailService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
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
    public ResponseEntity<String> importDataJson(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Выберите файл для импорта.");
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<Mail> mails = Arrays.asList(mapper.readValue(reader, Mail[].class));
            mails.forEach(mail -> mailService.createMessage(mailRepository.save(mail)));
            return ResponseEntity.status(HttpStatus.OK).body("Импорт завершен.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка обработки JSON файла.");
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
            mails.stream()
                    .map(MailShortFormatMapper::CsvToMail)
                    .forEach(mail -> mailService.createMessage(mailRepository.save(mail)));
            return ResponseEntity.ok("Импорт завершен.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка обработки CSV файла.");
        }
    }

    @GetMapping(value = "export/json", produces = "application/json")
    public ResponseEntity<StreamingResponseBody> exportDataJson(final HttpServletResponse response) {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment;filename=exportedData.json");
        List<Mail> mail = mailRepository.findAll();
        StreamingResponseBody stream = out -> {
            try (Writer writer = new OutputStreamWriter(response.getOutputStream())) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(writer, mail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        return ResponseEntity.ok(stream);
    }

    @GetMapping(value = "export/csv", produces = "text/csv")
    public ResponseEntity<StreamingResponseBody> exportDataCsv(final HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment;filename=exportedData.csv");
        List<MailCsvDTO> mail = mailRepository.findAll()
                .stream()
                .map(MailShortFormatMapper::MailToCsv)
                .collect(Collectors.toList());
        StreamingResponseBody stream = out -> {
            try (Writer writer = new OutputStreamWriter(response.getOutputStream())) {
                StatefulBeanToCsv<MailCsvDTO> csvWriter = new StatefulBeanToCsvBuilder<MailCsvDTO>(writer)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                        .withOrderedResults(false)
                        .build();
                csvWriter.write(mail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        return ResponseEntity.ok(stream);
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
