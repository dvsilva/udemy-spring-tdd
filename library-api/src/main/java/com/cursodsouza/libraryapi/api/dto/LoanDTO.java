package com.cursodsouza.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

	private Long id;
	private String isbn;
	private String customer;
	private BookDTO book;

}
