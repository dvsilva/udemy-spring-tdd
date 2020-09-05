package com.cursodsouza.libraryapi.model.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cursodsouza.libraryapi.model.entity.Book;
import com.cursodsouza.libraryapi.model.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

	// @Query(value = "", nativeQuery = true) // para realizar queries usando sql nativo
	@Query("select case when (count (l.id) > 0) then true else false end "
			+ "from Loan l where l.book = :book and (l.returned is null or l.returned is false)")
	boolean existsByBookAndNotReturned(@Param("book") Book book);

	@Query("select l from Loan as l join l.book as b where b.isbn =:isbn or l.customer =:customer")
	Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String isbn, @Param("customer") String customer, 
			Pageable pageRequest);

	Page<Loan> findByBook(Book book, Pageable pageable);

	@Query("select l from Loan l where l.loanDate <= :threeDaysAgo and (l.returned is null or l.returned is false)")
	List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);

}