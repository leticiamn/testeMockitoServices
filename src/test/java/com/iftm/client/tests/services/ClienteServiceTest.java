package com.iftm.client.tests.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
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


@ExtendWith(SpringExtension.class)
public class ClienteServiceTest {

		@InjectMocks
		private ClientService service;
		@Mock
		private ClientRepository repository;

		@Test
		public void testDeleteReturnsEmptyIdExists() {
			Long id = 1l;

			Mockito.doNothing().when(repository).deleteById(id);
			Assertions.assertDoesNotThrow(() -> service.delete(id));
			Mockito.verify(repository, Mockito.times(1)).deleteById(id);
		}

		public void testDeleteReturnsExceptionIdNotExists() {
			Long idNaoExistente = 1000l;
			Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(idNaoExistente);
			Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idNaoExistente));
			Mockito.verify(repository, Mockito.times(1)).deleteById(idNaoExistente);
		}

		@Test
		public void testFindAll() {
			List<Client> lista = new ArrayList<Client>();
			PageRequest pageRequest = PageRequest.of(11, 1);
			lista.add(new Client(12L, "'Jorge Amado", "10204374161", 2500.0, Instant.parse("1975-11-10T07:00:00Z"), 0));

			Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
			Mockito.when(repository.findAll(pageRequest)).thenReturn(pag);
			Page<ClientDTO> result = service.findAllPaged(pageRequest);

			Assertions.assertFalse(result.isEmpty());
			Assertions.assertEquals(lista.size(), result.getNumberOfElements());
			for (int i = 0; i < lista.size(); i++) {
				Assertions.assertEquals(lista.get(i), result.toList().get(i).toEntity());
			}
			Mockito.verify(repository, Mockito.times(1)).findAll(pageRequest);
		}

		@Test
		public void testFindByIncome() {
			PageRequest pageRequest = PageRequest.of(0, 1, Direction.valueOf("ASC"), "name");
			Double income = 10000.0;
			List<Client> lista = new ArrayList<Client>();
			lista.add(new Client(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0));
			Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
			Mockito.when(repository.findByIncome(income, pageRequest)).thenReturn(pag);
			Page<ClientDTO> result = service.findByIncome(pageRequest, income);
			Assertions.assertFalse(result.isEmpty());
			Assertions.assertEquals(lista.size(), result.getNumberOfElements());
			for (int i = 0; i < lista.size(); i++) {
				Assertions.assertEquals(lista.get(i), result.toList().get(i).toEntity());
			}
			Mockito.verify(repository, Mockito.times(1)).findByIncome(income, pageRequest);
		}

		@Test
		public void testFindByIdExistingId() {
			PageRequest pageRequest = PageRequest.of(0, 1);
			Long id = 10l;
			Optional<Client> client = Optional.of(new Client(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0));
			Mockito.when(repository.findById(id)).thenReturn(client);
			ClientDTO result = service.findById(id);
			Assertions.assertNotNull(result);
			Assertions.assertEquals(client.get(), result.toEntity());
			Mockito.verify(repository, Mockito.times(1)).findById(id);
		}

		@Test
		public void testFindByIdReturnsExceptionNotExistingId() {
			Long id = 1000l;
			Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(id);
			Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
			Mockito.verify(repository, Mockito.times(1)).findById(id);
		}

		@Test
		public void testUpdateReturnsExistingId() {
			Long id = 10l;
			Optional<Client> client = Optional.of(new Client());
			Client inicialClient = new Client(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0);
			Mockito.when(repository.getOne(id)).thenReturn(inicialClient);

			Client finalClient = new Client(10L, "Toni Morrison", "10219344681", 15000.0,  Instant.parse("1940-02-23T07:00:00Z"), 1);
			Mockito.when(repository.save(finalClient)).thenReturn(finalClient);

			ClientDTO dto = service.update(10L, new ClientDTO(finalClient));

			Assertions.assertEquals(finalClient, dto.toEntity());
			Mockito.verify(repository, Mockito.times(1)).getOne(id);
			Mockito.verify(repository, Mockito.times(1)).save(finalClient);
		}

		@Test
		public void testUpdateReturnsExceptionNotExistingId() {
			Long id = 1000l;
			Optional<Client> client = Optional.of(new Client());

			Client inicialClient = new Client(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0);
			Mockito.doThrow(ResourceNotFoundException.class).when(repository).getOne(id);

			Client finalClient = new Client(10L, "Toni Morrison", "10219344681", 15000.0,  Instant.parse("1940-02-23T07:00:00Z"), 1);
			Mockito.when(repository.save(finalClient)).thenReturn(finalClient);

			Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(id, new ClientDTO(finalClient)));

			Mockito.verify(repository, Mockito.times(1)).getOne(id);
			Mockito.verify(repository, Mockito.times(0)).save(finalClient);
		}

		@Test
		public void testInsertNewClient() {
			Long id = 10l;
			Optional<Client> client = Optional.of(new Client());

			Client inicialClient = new Client(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0);

			ClientDTO finalClient = new ClientDTO(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0);
			Mockito.when(repository.save(finalClient.toEntity())).thenReturn(finalClient.toEntity());
			ClientDTO dto = service.insert(finalClient);

			Assertions.assertEquals(dto.toEntity(), inicialClient);
			Mockito.verify(repository, Mockito.times(1)).save(dto.toEntity());
		}
	}