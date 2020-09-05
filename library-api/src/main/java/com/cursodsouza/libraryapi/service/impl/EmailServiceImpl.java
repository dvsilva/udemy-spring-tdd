package com.cursodsouza.libraryapi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.cursodsouza.libraryapi.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	@Value("${application.mails.default-remetent}")
	private String rementent;
	
	private final JavaMailSender javaMailSender;

	@Override
	public void sendMails(String message, List<String> mailsList) {
		String[] mails = mailsList.toArray(new String[mailsList.size()]);
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(rementent);
		mailMessage.setSubject("Livro com empréstimo atrasado");
		mailMessage.setText(message);
		mailMessage.setTo(mails);
		
		javaMailSender.send(mailMessage);
	}

}
