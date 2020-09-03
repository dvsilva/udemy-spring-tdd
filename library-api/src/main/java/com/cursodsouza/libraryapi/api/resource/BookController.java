package com.cursodsouza.libraryapi.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cursodsouza.libraryapi.api.dto.BookDTO;
import com.cursodsouza.libraryapi.api.exception.ApiErrors;
import com.cursodsouza.libraryapi.exception.BussinessException;
import com.cursodsouza.libraryapi.model.entity.Book;
import com.cursodsouza.libraryapi.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private BookService service;
	private ModelMapper modelMapper;
	
	public BookController(BookService service, ModelMapper modelMapper) {
		this.service = service;
		this.modelMapper = modelMapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@Valid @RequestBody BookDTO dto) {
		Book entity = modelMapper.map(dto, Book.class);
		entity = service.save(entity);
		return modelMapper.map(entity, BookDTO.class);
	}
	
	@GetMapping("{id}")
	public BookDTO get(@PathVariable Long id) {
		return service.getById(id)
				.map(book -> modelMapper.map(book, BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.delete(book);
	}

	@PutMapping("{id}")
	public BookDTO update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
		return service.getById(id)
			.map(book -> {
				book.setAuthor(dto.getAuthor());
				book.setTitle(dto.getTitle());
				book.setIsbn(dto.getIsbn());
				book = service.update(book);
				return modelMapper.map(book, BookDTO.class);
			}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping
	public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
		Book filter = modelMapper.map(dto, Book.class);
		Page<Book> result = service.find(filter, pageRequest);
		
		List<BookDTO> list = result.getContent().stream()
			.map(entity -> modelMapper.map(entity, BookDTO.class))
			.collect(Collectors.toList());
		
		return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		return new ApiErrors(bindingResult);
	}

	@ExceptionHandler(BussinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationException(BussinessException ex) {
		return new ApiErrors(ex);
	}
	
	/**
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@RequestBody BookDTO dto) {
		// BookDTO dto = BookDTO.builder().author("Autor").title("Meu livro").isbn("1213212").id(1l).build();
		Book entity = Book.builder()
				.author(dto.getAuthor())
				.title(dto.getTitle())
				.isbn(dto.getIsbn())
				.build();
		
		entity = service.save(entity);
		
		return BookDTO.builder()
				.id(entity.getId())
				.author(entity.getAuthor())
				.title(entity.getTitle())
				.isbn(entity.getIsbn())
				.build();
	}
	*/

}
