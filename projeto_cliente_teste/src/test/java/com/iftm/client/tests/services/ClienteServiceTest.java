package com.iftm.client.tests.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ClienteServiceTest {

		@InjectMocks
		private ClientService servico;
		@Mock
		private ClientRepository repositorio;

		@Test
		public void testRetornaVazioQuandoIdExiste() {
			Long idExistente = 2l;
			Mockito.doNothing().when(repositorio).deleteById(idExistente);

			Assertions.assertDoesNotThrow(() -> servico.delete(idExistente));
			Mockito.verify(repositorio, Mockito.times(1)).deleteById(idExistente);
		}

		public void testRetornaExceptionQuandoIdNaoExiste() {
			Long idNaoExistente = 1000l;
			Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).deleteById(idNaoExistente);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.delete(idNaoExistente));
			Mockito.verify(repositorio, Mockito.times(1)).deleteById(idNaoExistente);
		}

		@Test
		public void testFindAllRetornaPaginaComTodosClientes() {
			PageRequest pageRequest = PageRequest.of(11, 1);
			List<Client> lista = new ArrayList<Client>();
			lista.add(new Client(8L, "'Jorge Amado", "10204374161", 2500.0, Instant.parse("1975-11-10T07:00:00Z"), 0));

			Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
			Mockito.when(repositorio.findAll(pageRequest)).thenReturn(pag);
			Page<ClientDTO> resultado = servico.findAllPaged(pageRequest);

			Assertions.assertFalse(resultado.isEmpty());
			Assertions.assertEquals(lista.size(), resultado.getNumberOfElements());
			for (int i = 0; i < lista.size(); i++) {
				Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
			}
			Mockito.verify(repositorio, Mockito.times(1)).findAll(pageRequest);
		}

		@Test
		public void testFindByIncome() {
			PageRequest pageRequest = PageRequest.of(0, 1, Direction.valueOf("ASC"), "name");
			Double entrada = 4500.00;
			List<Client> lista = new ArrayList<Client>();
			lista.add(new Client(8L, "Djamila Ribeiro", "10619244884", 4500.0, Instant.parse("1975-11-10T07:00:00Z"), 1));

			Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
			Mockito.when(repositorio.findByIncome(entrada, pageRequest)).thenReturn(pag);
			Page<ClientDTO> resultado = servico.findByIncome(pageRequest, entrada);
			Assertions.assertFalse(resultado.isEmpty());
			Assertions.assertEquals(lista.size(), resultado.getNumberOfElements());
			for (int i = 0; i < lista.size(); i++) {
				Assertions.assertEquals(lista.get(i), resultado.toList().get(i).toEntity());
			}
			Mockito.verify(repositorio, Mockito.times(1)).findByIncome(entrada, pageRequest);
		}

		@Test
		public void testFindByIdRetornaClientDtoQuandoIdExistir() {
			PageRequest pageRequest = PageRequest.of(0, 1);
			Long idExistente = 1l;
			Optional<Client> client = Optional.of(new Client());
			Mockito.when(repositorio.findById(idExistente)).thenReturn(client);
			ClientDTO resultado = servico.findById(idExistente);
			Assertions.assertNotNull(resultado);
			Assertions.assertEquals(client.get(), resultado.toEntity());
			Mockito.verify(repositorio, Mockito.times(1)).findById(idExistente);
		}

		@Test
		public void testFindByIdLancaExceptionQuandoIdNaoExistir() {
			Long id = 1000l;
			Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).findById(id);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.findById(id));
			Mockito.verify(repositorio, Mockito.times(1)).findById(id);
		}

		@Test
		public void testUpdateRetornaClientDtoQuandoExistirId() {
			PageRequest pageRequest = PageRequest.of(0, 1);
			Long id = 1l;
			Optional<Client> client = Optional.of(new Client());
			Mockito.when(repositorio.findById(id)).thenReturn(client);
			ClientDTO resultado = servico.findById(id);
			ClientDTO test = servico.update(id, resultado);
			Assertions.assertNotNull(test);
			Assertions.assertEquals(client.get(), resultado.toEntity());
			Mockito.verify(repositorio, Mockito.times(1)).findById(id);
		}

		@Test
		public void testUpdateRetornaExceptionNaoExistirId() {
			Long idNaoExistente = 1000l;
			Mockito.doThrow(ResourceNotFoundException.class).when(repositorio).findById(idNaoExistente);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.findById(idNaoExistente));
			Mockito.verify(repositorio, Mockito.times(1)).findById(idNaoExistente);
		}
	}