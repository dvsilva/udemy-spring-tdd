package com.cursodsouza.libraryapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {
// configuracao fazer o build para rodar em um tomcat externo
// public class LibraryApiApplication extends SpringBootServletInitializer
	
	// @Autowired
	// private EmailService emailService;
	
	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	/**
	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<String> emails = Arrays.asList("library-api-954d64@inbox.mailtrap.io");
			emailService.sendMails("Testando serviço de emails", emails);
			System.out.println("EMAILS ENVIADOS");
		};
	}
	
	@Scheduled(cron ="0 5 8 1/1 * ?")
	public void testeAgendamentoTarefas() {
		System.out.println("AGENDAMENTO DE TAREFAS FUNCIONANDO COM SUCESSO");
	}
	*/
}
