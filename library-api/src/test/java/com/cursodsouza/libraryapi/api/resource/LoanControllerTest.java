package com.cursodsouza.libraryapi.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cursodsouza.libraryapi.api.dto.LoanDTO;
import com.cursodsouza.libraryapi.api.dto.LoanFilterDTO;
import com.cursodsouza.libraryapi.api.dto.ReturnedLoadDTO;
import com.cursodsouza.libraryapi.exception.BussinessException;
import com.cursodsouza.libraryapi.model.entity.Book;
import com.cursodsouza.libraryapi.model.entity.Loan;
import com.cursodsouza.libraryapi.service.BookService;
import com.cursodsouza.libraryapi.service.LoanService;
import com.cursodsouza.libraryapi.service.LoanServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class) 
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc 
public class LoanControllerTest {
	
	static String LOAN_API = "/api/loans";
	
	@Autowired
	MockMvc mvc; // simula requisicoes para api
	
	@MockBean
	BookService bookService;
	
	@MockBean
	LoanService loanService;
	
	@Test
	@DisplayName("Deve criar um empréstimo.")
	public void createLoanTest() throws Exception {
		LoanDTO dto = LoanDTO.builder().isbn("123").email("customer@email.com").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1l).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123"))
			.willReturn(Optional.of(book));
		
		Loan loan = Loan.builder().id(1l).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(LOAN_API)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(content().string("1"));
	}

	@Test
	@DisplayName("Deve retornar um erroao tentar fazer empréstimo de um livro inexistente.")
	public void invalidIsbnCreateLoanTest() throws Exception {
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(LOAN_API)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
	}

	@Test
	@DisplayName("Deve retornar um erro ao tentar fazer empréstimo de um livro emprestado.")
	public void loanedBookErrorOnCreateLoanTest() throws Exception {
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1l).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
		
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BussinessException("Book already loaned"));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(LOAN_API)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value("Book already loaned"));
	}
	
	@Test
	@DisplayName("Deve retornar um livro emprestado.")
	public void returnBookTest() throws Exception {
		// cenario  {returned : true}
		ReturnedLoadDTO dto = ReturnedLoadDTO.builder().returned(true).build();
		String json = new ObjectMapper().writeValueAsString(dto);

		Loan loan = Loan.builder().id(1l).build();
		BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.patch(LOAN_API.concat("/1"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
			
			mvc.perform(request)
				.andExpect(status().isOk());

		Mockito.verify(loanService, Mockito.times(1)).update(loan);
	}
	

	@Test
	@DisplayName("Deve retornar 404 quanto tentar devolver um livro inexistente.")
	public void returnInexistentBookTest() throws Exception {
		// cenario  {returned : true}
		ReturnedLoadDTO dto = ReturnedLoadDTO.builder().returned(true).build();
		String json = new ObjectMapper().writeValueAsString(dto);

		BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.patch(LOAN_API.concat("/1"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
			
			mvc.perform(request)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve filtrar empréstimos.")
	public void findLoansTest() throws Exception {
		Long id = 1L;
		Loan loan = LoanServiceTest.createLoan();
		loan.setId(id);
		Book book = Book.builder().id(1l).isbn("321").build();
		loan.setBook(book);
		
		BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
			.willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));
		
		String queryString = String.format("?isbn=%s&costumer=%s&page=0&size=10", 
				book.getIsbn(), loan.getCustomer());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(LOAN_API.concat(queryString))
				.accept(MediaType.APPLICATION_JSON);
			
			mvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(jsonPath("content", Matchers.hasSize(1)))
				.andExpect(jsonPath("totalElements").value(1))
				.andExpect(jsonPath("pageable.pageSize").value(10))
				.andExpect(jsonPath("pageable.pageNumber").value(0));
		
	}
}
