package com.cursodsouza.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursodsouza.libraryapi.model.entity.Book;
import com.cursodsouza.libraryapi.model.entity.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest 
public class LoanRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	LoanRepository repository;

	@Test
	@DisplayName("Deve verificar se existe empréstimo não devolvido para o livro.")
	public void existsByBookAndNotReturnedTest() throws Exception {
		// cenario
		Loan loan = createAndPersistLoan(LocalDate.now());
		Book book = loan.getBook();
		
		// execucao
		boolean exists = repository.existsByBookAndNotReturned(book);
		
		// verificacao
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer.")
	public void findByBookIsbnIrCustomerTest() throws Exception {
		Loan loan = createAndPersistLoan(LocalDate.now());

		Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent()).contains(loan);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("Deve obter empréstimos cuja data empéstimo for menor ou igual a tres dias atras e nao retornados.")
	public void findByLoanDateLessThanAndNotReturnedTest() throws Exception {
		Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));
		List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		assertThat(result).hasSize(1).contains(loan);
	}

	@Test
	@DisplayName("Deve retornar vazio quando não houver empréstimos atrasados")
	public void notfindByLoanDateLessThanAndNotReturnedTest() throws Exception {
		createAndPersistLoan(LocalDate.now());
		List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		assertThat(result).isEmpty();
	}
	
	private Loan createAndPersistLoan(LocalDate loanDate) {
		Book book = BookRepositoryTest.createNewBook("123");
		entityManager.persist(book);
		
		Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();
		entityManager.persist(loan);
		return loan;
	}
}
