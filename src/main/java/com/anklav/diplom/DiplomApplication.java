package com.anklav.diplom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiplomApplication {
//TODO: не запускать процесс построения дерева, если в БД оно уже есть
//TODO: добавить таблицы для построения решетки. Если в БД нету такого ключа -- генерим дерево и добавляем в БД

	//TODO: классификация: на вход: {ids}, {nameOfAlgo}. На выход: pre, rec, f1
	//TODO: similarity: на вход: get{id}. На выход: top-N Edit'oв
	//TODO: Также в Edit'ы подавать дерево
	//TODO: разделить алгоритмы построения и классификации
	//TODO: возвращать Сет<Стринг>, Трии
	public static void main(String[] args) {
		SpringApplication.run(DiplomApplication.class, args);
	}
}
