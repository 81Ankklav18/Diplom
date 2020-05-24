package com.anklav.diplom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiplomApplication {
//TODO: удаление, выгрузка из БД неуникальна, обновление по gmail-UID, генерация снипета и айди если пустые
//TODO: analyze с массивом idшников -- пост запрос
//TODO: сервис для анализа -- придумать что можно отобразить
	public static void main(String[] args) {
		SpringApplication.run(DiplomApplication.class, args);
	}
}
