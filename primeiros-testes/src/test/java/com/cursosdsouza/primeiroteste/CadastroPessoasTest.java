package com.cursosdsouza.primeiroteste;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CadastroPessoasTest {

	@Test
	@DisplayName("deve criar o cadastro de pessoas")
	public void deveCriarOCadastrDePessoas() {
		// cenario e execucao
		CadastroPessoas cadastro = new CadastroPessoas();

		// verificacao
		Assertions.assertThat(cadastro.getPessoas()).isEmpty();
	}

	@Test
	@DisplayName("deve adicionar uma pessoa")
	public void deveAdicionarUmaPessoa() {
		// cenario
		CadastroPessoas cadastro = new CadastroPessoas();
		Pessoa pessoa = new Pessoa();
		pessoa.setNome("Wilson");

		// execucao
		cadastro.adicionar(pessoa);

		// verificacao
		Assertions.assertThat(cadastro.getPessoas()).isNotEmpty().hasSize(1).contains(pessoa);
	}

	@Test // (expected = PessoaSemNomeException.class)
	@DisplayName("não deve adicionar uma pessoa com nome vazio")
	public void naoDeveAdicionarUmaPessoaComNomeVazio() {
		// cenario
		CadastroPessoas cadastro = new CadastroPessoas();
		Pessoa pessoa = new Pessoa();

		// execucao
		org.junit.jupiter.api.Assertions.assertThrows(PessoaSemNomeException.class, () -> cadastro.adicionar(pessoa));
	}

	@Test
	@DisplayName("deve remover uma pessoa")
	public void deveRemoverUmaPessoa() {
		// cenario
		CadastroPessoas cadastro = new CadastroPessoas();
		Pessoa pessoa = new Pessoa();
		pessoa.setNome("Wilson");
		cadastro.adicionar(pessoa);

		// execucao
		cadastro.remover(pessoa);

		// verificacao
		Assertions.assertThat(cadastro.getPessoas()).isEmpty();
	}

	@Test // (expected = CadastroVazioException.class)
	@DisplayName("deve lançar erro ao remover uma pessoa inexistente")
	public void deveLancarErroAoRemoverUmaPessoaInexistente() {
		// cenario
		CadastroPessoas cadastro = new CadastroPessoas();
		Pessoa pessoa = new Pessoa();

		// execucao
		org.junit.jupiter.api.Assertions.assertThrows(CadastroVazioException.class, () -> cadastro.remover(pessoa));
	}
}
