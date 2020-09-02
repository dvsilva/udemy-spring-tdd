package com.cursodsouza.ppr;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class MeuResource {

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente salvar(@RequestBody Cliente cliente) {
		// service.save(cliente);
		return cliente; 
	}
	
	/**
	// @RequestMapping(value = "/api/clientes", method = RequestMethod.POST)
	@PostMapping("/api/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity salvar(@RequestBody Cliente cliente) {
		// service.save(cliente);
		return new ResponseEntity(cliente, HttpStatus.CREATED); 
	}
	*/
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		// service.buscarPorId(id);
		// service.delete(cliente);
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
		// service.buscarPorId(id);
		// service.update(cliente);
		return cliente; 
	}
	
	// @RequestMapping(value = "/api/clientes/{id}", method = RequestMethod.GET)
	@GetMapping("{id}")
	public Cliente obterDadosCliente(@PathVariable Long id) {
		System.out.println(String.format("Id recebido via url: %s", id));
		Cliente cliente = new Cliente("Fulano", "000.000.000-00");
		return cliente;
	}

}
