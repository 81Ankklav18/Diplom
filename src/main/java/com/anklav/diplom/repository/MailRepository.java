package com.anklav.diplom.repository;

import com.anklav.diplom.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface MailRepository extends JpaRepository<Mail, Long> {
}
