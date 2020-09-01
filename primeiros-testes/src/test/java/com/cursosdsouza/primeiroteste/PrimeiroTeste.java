package com.cursosdsouza.primeiroteste;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrimeiroTeste {

	private Calculadora calculadora;
	int numero1 = 10, numero2 = 5;

	@BeforeEach
	public void setUp() {
		calculadora = new Calculadora();
	}

	@Test
	public void estruturaDeUmTeste() {
		// cenario
		// execucao
		// verificacoes
	}

	@Test
	public void deveSomar2Numeros() {
		// cenario

		// execucao
		int resultado = calculadora.somar(numero1, numero2);

		// verificacoes
		// Assert.assertEquals(15, resultado);
		// com a biblioteca assertions
		// Assertions.assertThat(resultado).isBetween(14, 16);
		Assertions.assertThat(resultado).isEqualTo(15);
	}

	@Test // (expected = RuntimeException.class)
	public void naoDeveSomarNumerosNegativos() {
		// cenario
		int numero1 = -10, numero2 = 5;

		// execucao
		
		org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> calculadora.somar(numero1, numero2));
	}

	@Test
	public void deveSubtrair2Numeros() {
		// cenario

		// execucao
		int resultado = calculadora.subtrair(numero1, numero2);

		// verificacoes
		Assertions.assertThat(resultado).isEqualTo(5);
	}

	@Test
	public void deveMultiplicar2Numeros() {
		// cenario

		// execucao
		int resultado = calculadora.multiplicar(numero1, numero2);

		// verificacoes
		Assertions.assertThat(resultado).isEqualTo(50);
	}

	@Test
	public void deveDividir2Numeros() {
		// cenario

		// execucao
		float resultado = calculadora.dividir(numero1, numero2);

		// verificacoes
		Assertions.assertThat(resultado).isEqualTo(2);
	}

	@Test // (expected = ArithmeticException.class)
	public void naoDeveDividirPorZero() {
		// cenario
		int numero1 = 10, numero2 = 0;

		// execucao
		org.junit.jupiter.api.Assertions.assertThrows(ArithmeticException.class, () -> calculadora.dividir(numero1, numero2));

	}
}

class Calculadora {
	int somar(int num1, int num2) {
		if (num1 < 0 || num2 < 0)
			throw new RuntimeException("Não é permitido somar números negativos.");

		return num1 + num2;
	}

	int subtrair(int num1, int num2) {
		return num1 - num2;
	}

	int multiplicar(int num1, int num2) {
		return num1 * num2;
	}

	float dividir(int num1, int num2) {
		return num1 / num2;
	}

}
