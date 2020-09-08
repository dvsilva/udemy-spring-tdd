package com.cursodsouza.libraryapi.api.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cursodsouza.libraryapi.api.dto.BookDTO;
import com.cursodsouza.libraryapi.api.dto.LoanDTO;
import com.cursodsouza.libraryapi.api.dto.LoanFilterDTO;
import com.cursodsouza.libraryapi.api.dto.ReturnedLoadDTO;
import com.cursodsouza.libraryapi.model.entity.Book;
import com.cursodsouza.libraryapi.model.entity.Loan;
import com.cursodsouza.libraryapi.service.BookService;
import com.cursodsouza.libraryapi.service.LoanService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Api("Loans API")
public class LoanController {
	
	private final LoanService service;
	private final BookService bookService;
	private final ModelMapper modelMapper;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Create a loan")
	public Long create(@RequestBody LoanDTO dto) {
		Book book = bookService
				.getBookByIsbn(dto.getIsbn())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
		
		Loan entity = Loan.builder()
				.book(book).customer(dto.getCustomer()).loanDate(LocalDate.now()).build();
		
		entity = service.save(entity);
		return entity.getId();
	}
	
	@PatchMapping("{id}")
	@ApiOperation("Return a book")
	public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoadDTO dto) {
		Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		loan.setReturned(dto.getReturned());
		service.update(loan);
	}

	@GetMapping
	@ApiOperation("Find loans by params")
	public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
		Page<Loan> result = service.find(dto, pageRequest);
		
		List<LoanDTO> loans = result.getContent().stream()
			.map(entity -> {
					Book book = entity.getBook();
					BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
					LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
					loanDTO.setBook(bookDTO);
					return loanDTO;
				})
			.collect(Collectors.toList());
		
		return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
	}
}
