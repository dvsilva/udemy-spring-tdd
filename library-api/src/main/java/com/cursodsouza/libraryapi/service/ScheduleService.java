package com.cursodsouza.libraryapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cursodsouza.libraryapi.model.entity.Loan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	// 00:00:00 / TODOS OS DIAS (1/1) / QUALQUER MES E ANO
	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

	private final LoanService loanService;
	private final EmailService emailService;

	@Value("${application.mails.lateLoans.message}")
	private String message;

	@Scheduled(cron = CRON_LATE_LOANS)
	public void sendMailToLateLoans() {
		List<Loan> allLateLoans = loanService.getAllLateLoans();

		List<String> mailsList = allLateLoans.stream().map(loan -> loan.getCustomerEmail())
				.collect(Collectors.toList());

		emailService.sendMails(message, mailsList);
	}

}
