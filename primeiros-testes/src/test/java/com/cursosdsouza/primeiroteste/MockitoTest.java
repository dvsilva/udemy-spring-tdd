package com.cursosdsouza.primeiroteste;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

// @RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class MockitoTest {

	@Mock
	List<String> lista;

	@Test
	public void primeiroTesteMokito() {
		// List<String> lista = Mockito.mock(ArrayList.class);
		// mock vai sempre retornar o valor default
		// ao nao ser que defina qual vai ser o retorno
		// lista.add("teste");

		Mockito.when(lista.size()).thenReturn(20);

		int size = lista.size();
		lista.add("");

		// verificar execucao de metodos na ordem desejada
		InOrder inOrder = Mockito.inOrder(lista);
		inOrder.verify(lista).size();
		inOrder.verify(lista).add("");

		Assertions.assertThat(size).isEqualTo(20);

		// verificar chamadas de metodos
		Mockito.verify(lista, Mockito.times(1)).size();
		// Mockito.verify(lista, Mockito.never()).size();
	}

}
