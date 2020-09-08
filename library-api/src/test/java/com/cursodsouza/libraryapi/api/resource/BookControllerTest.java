package com.cursodsouza.libraryapi.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.cursodsouza.libraryapi.api.dto.BookDTO;
import com.cursodsouza.libraryapi.exception.BussinessException;
import com.cursodsouza.libraryapi.model.entity.Book;
import com.cursodsouza.libraryapi.service.BookService;
import com.cursodsouza.libraryapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;


// @RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class) // mini contexto com classes que pedir para rodar os testes
@ActiveProfiles("test") // configurar ambiente de tesste
@WebMvcTest(controllers = BookController.class) // subir apenas um controller
@AutoConfigureMockMvc // configura objeto para realizar as requisicoes
public class BookControllerTest {

	// import estatico ctrl + shift + m
	
	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc; // simula requisicoes para api
	
	@MockBean
	BookService service;
	
	@MockBean
	LoanService loanService;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso.")
	public void createBookTest() throws Exception {
		BookDTO dto = createNewBook();
		Book savedBook = Book.builder().id(10l).author("Artur").title("As aventuras").isbn("001").build();
		
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(BOOK_API)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").isNotEmpty())
			.andExpect(jsonPath("id").value(10l))
			.andExpect(jsonPath("title").value(dto.getTitle()))
			.andExpect(jsonPath("author").value(dto.getAuthor()))
			.andExpect(jsonPath("isbn").value(dto.getIsbn()));
	}

	@Test
	@DisplayName("Deve lançar erro de validação quando não houver dados para a criação do livro.")
	public void createInvalidBookTest() throws Exception {
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
			
			mvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(3)));
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
	public void createBookWithDuplicatedIsbnTest() throws Exception {
		String mensagemErro = "Isbn já cadastrado";
		
		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(service.save(Mockito.any(Book.class)))
			.willThrow(new BussinessException(mensagemErro));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
			
			mvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value(mensagemErro));
	}
	

	@Test
	@DisplayName("Deve obter informações de um livro.")
	public void getBookDetailsTest() throws Exception {
		// cenario (given)
		Long id = 1l;
		
		BookDTO dto = createNewBook();
		Book book = Book.builder()
				.id(id).author(dto.getAuthor()).title(dto.getTitle()).isbn(dto.getIsbn()).build();
		
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
		
		// execucao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);
			
			mvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").isNotEmpty())
				.andExpect(jsonPath("id").value(id))
				.andExpect(jsonPath("title").value(dto.getTitle()))
				.andExpect(jsonPath("author").value(dto.getAuthor()))
				.andExpect(jsonPath("isbn").value(dto.getIsbn()));
	}
	
	@Test
	@DisplayName("Deve retornar resource not found quando o livro procurado não existir.")
	public void bookNotFoundTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);
			
			mvc.perform(request)
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Deve deletar um livro.")
	public void deleteBookTest() throws Exception {
		Book book = Book.builder().id(1l).build();
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(book));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/" + 1));
			
			mvc.perform(request)
				.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("Deve retornar resource not found quando não encontrar o livro para deletar.")
	public void deleteInexistentBookTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/" + 1));
			
			mvc.perform(request)
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Deve atualizar um livro.")
	public void updateBookTest() throws Exception {
		Long id = 1l;
		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book updatingBook = Book.builder().id(id).title("some title").author("some author").isbn("321").build();
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));

		Book updatedBook = Book.builder().id(id).author("Artur").title("As aventuras").isbn("001").build();
		BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.put(BOOK_API.concat("/" + 1))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").isNotEmpty())
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("title").value(dto.getTitle()))
			.andExpect(jsonPath("author").value(dto.getAuthor()))
			.andExpect(jsonPath("isbn").value(dto.getIsbn()));
	}

	@Test
	@DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente.")
	public void updateInexistentBookTest() throws Exception {
		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.put(BOOK_API.concat("/" + 1))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc.perform(request)
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve filtrar livros.")
	public void findBooksTest() throws Exception {
		Long id = 1L;
		
		BookDTO dto = createNewBook();
		Book book = Book.builder()
				.id(id).author(dto.getAuthor()).title(dto.getTitle()).isbn(dto.getIsbn()).build();
		
		BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
			.willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));
		
		String queryString = String.format("?title=%s&author=%s&page=0&size=100", 
				book.getTitle(), book.getAuthor());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat(queryString))
				.accept(MediaType.APPLICATION_JSON);
			
			mvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(jsonPath("content", Matchers.hasSize(1)))
				.andExpect(jsonPath("totalElements").value(1))
				.andExpect(jsonPath("pageable.pageSize").value(100))
				.andExpect(jsonPath("pageable.pageNumber").value(0));
	}
	
	private BookDTO createNewBook() {
		BookDTO dto = BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
		return dto;
	}
	

}
