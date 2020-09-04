package com.cursodsouza.libraryapi.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cursodsouza.libraryapi.api.dto.LoanFilterDTO;
import com.cursodsouza.libraryapi.exception.BussinessException;
import com.cursodsouza.libraryapi.model.entity.Loan;
import com.cursodsouza.libraryapi.model.repository.LoanRepository;
import com.cursodsouza.libraryapi.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	private LoanRepository repository;

	public LoanServiceImpl(LoanRepository repository) {
		this.repository = repository;
	}

	@Override
	public Loan save(Loan loan) {
		if(repository.existsByBookAndNotReturned(loan.getBook()))
			throw new BussinessException("Book already loaned");
		
		return repository.save(loan);
	}

	@Override
	public Optional<Loan> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public Loan update(Loan loan) {
		return repository.save(loan);
	}

	@Override
	public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
		return repository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
	}

}
