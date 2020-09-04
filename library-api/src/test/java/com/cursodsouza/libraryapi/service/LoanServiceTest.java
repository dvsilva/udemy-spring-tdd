package com.cursodsouza.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursodsouza.libraryapi.api.dto.LoanFilterDTO;
import com.cursodsouza.libraryapi.exception.BussinessException;
import com.cursodsouza.libraryapi.model.entity.Book;
import com.cursodsouza.libraryapi.model.entity.Loan;
import com.cursodsouza.libraryapi.model.repository.LoanRepository;
import com.cursodsouza.libraryapi.service.impl.LoanServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
	
	LoanService service;
	
	@MockBean
	LoanRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new LoanServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve salvar um empréstimo.")
	public void saveLoanTest() {
		Book book = Book.builder().id(1l).build();
		String customer = "Fulano";
		
		Loan savingLoan = Loan.builder()
				.book(book)
				.customer(customer)
				.loanDate(LocalDate.now())
				.build();
		
		Loan savedLoan = Loan.builder()
				.id(1l)
				.loanDate(LocalDate.now())
				.customer(customer)
				.book(book)
				.build();

		when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
		when(repository.save(savingLoan)).thenReturn(savedLoan);

		Loan loan = service.save(savingLoan);

		assertThat(loan.getId()).isEqualTo(savedLoan.getId());
		assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
		assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
		assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
	}
	
	@Test
	@DisplayName("Deve lançar erro de negócio ao salvar empréstimo com livro já emprestado.")
	public void updateInvalidBookTest() {
		Book book = Book.builder().id(1l).build();
		String customer = "Fulano";
		
		Loan savingLoan = Loan.builder()
				.book(book)
				.customer(customer)
				.loanDate(LocalDate.now())
				.build();
				
		when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
		
		Throwable exception = catchThrowable(() -> service.save(savingLoan));

		assertThat(exception).isInstanceOf(BussinessException.class)
			.hasMessage("Book already loaned");
		
		Mockito.verify(repository, Mockito.never()).save(savingLoan);
	}
	
	@Test
	@DisplayName("Deve obter as informações de um empréstimo pelo Id.")
	public void getLoanDetailsTest() {
		Long id = 1l;
		Loan loan = createLoan();
		loan.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));
		
		// execucao
		Optional<Loan> result = service.getById(id);
		
		// verificacao
		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getId()).isEqualTo(id);
		assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
		assertThat(result.get().getBook()).isEqualTo(loan.getBook());
		assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

		Mockito.verify(repository).findById(id);
	}

	@Test
	@DisplayName("Deve atualizar um empréstimo.")
	public void updateLoanTest() {
		Long id = 1l;
		Loan loan = createLoan();
		loan.setId(id);
		loan.setReturned(true);
		
		Mockito.when(repository.save(loan)).thenReturn(loan);
		
		// execucao
		Loan updatedLoan = service.update(loan);
		
		// verificacao
		assertThat(updatedLoan.getReturned()).isTrue();
		Mockito.verify(repository).save(loan);
	}
	
	@Test
	@DisplayName("Deve filtrar empréstimos pelas propriedades.")
	public void findLoanTest() {
		// cenario
		LoanFilterDTO loanFilter = LoanFilterDTO.builder().customer("Fulano").isbn("123").build();
		
		Long id = 1l;
		Loan loan = createLoan();
		loan.setId(id);
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Loan> lista = Arrays.asList(loan);
		Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());

		when(repository.findByBookIsbnOrCustomer(
				Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
			.thenReturn(page);
		
		// execucao
		Page<Loan> result = service.find(loanFilter, pageRequest);

		// verificacoes
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(lista);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
	}
	
	public static Loan createLoan() {
		Book book = Book.builder().id(1l).build();
		String customer = "Fulano";
		
		Loan savingLoan = Loan.builder()
				.book(book)
				.customer(customer)
				.loanDate(LocalDate.now())
				.build();
		
		return savingLoan;
	}
}
