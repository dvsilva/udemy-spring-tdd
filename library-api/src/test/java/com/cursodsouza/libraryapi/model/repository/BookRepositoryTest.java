package com.cursodsouza.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursodsouza.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
// indica que vamos fazer testes com JPA, cria instancia do bd em memoria para executar os teste e apaga quando terminar
@DataJpaTest 
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager; // criar cenario, simula o entityManager para testes
	
	@Autowired
	BookRepository repository;

	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado.")
	public void returnTrueWhenIsbnExistsTest() throws Exception {
		// cenario
		String isbn = "123";
		Book entity = createNewBook(isbn);
		entityManager.persist(entity);
		
		// execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		// verificacao
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado.")
	public void returnFalseWhenIsbnDoesntExistsTest() throws Exception {
		// cenario
		String isbn = "123";
		
		// execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		// verificacao
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("Deve obter um livro por Id.")
	public void findByIdTest() throws Exception {
		// cenario
		String isbn = "123";
		Book book = createNewBook(isbn);
		entityManager.persist(book);
		
		// execucao
		Optional<Book> foundBook = repository.findById(book.getId());
		
		// verificacao
		assertThat(foundBook.isPresent()).isTrue();
	}

	@Test
	@DisplayName("Deve salvar um livro.")
	public void saveBookTest() {
		Book book = createNewBook("123");
		Book savedBook = repository.save(book);
		assertThat(savedBook.getId()).isNotNull();
	}

	@Test
	@DisplayName("Deve deletar um livro.")
	public void deleteBookTest() {
		Book book = createNewBook("123");
		entityManager.persist(book);
		
		Book foundBook = entityManager.find(Book.class, book.getId());
		
		repository.delete(foundBook);

		Book deletedBook = entityManager.find(Book.class, book.getId());

		assertThat(deletedBook).isNull();
	}

	public static Book createNewBook(String isbn) {
		return Book.builder().title("As aventuras").author("Fulano").isbn(isbn).build();
	}
	
}
