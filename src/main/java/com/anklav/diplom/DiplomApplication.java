package com.anklav.diplom;

import com.anklav.diplom.service.MailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
public class DiplomApplication {

	public static void main(String[] args) throws IOException, GeneralSecurityException {
//		MailService.runService();
		SpringApplication.run(DiplomApplication.class, args);
	}
}
