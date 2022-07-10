package com.iftm.client.tests.services;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class ClienteServiceTestIT {

	@Autowired
	private ClientService service;

	@Test
	public void testDeleteReturnsEmptyIdExists() {
		Long id = 1l;
		Assertions.assertDoesNotThrow(() -> service.delete(id));
	}

	@Test
	public void testDeleteReturnsExceptionIdNotExists() {
		Long idNaoExistente = 1000l;
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idNaoExistente));
	}

	@Test
	public void testFindAll() {
		List<Client> lista = new ArrayList<Client>();
		PageRequest pageRequest = PageRequest.of(11, 1);
		lista.add(new Client(12L, "'Jorge Amado", "10204374161", 2500.0, Instant.parse("1975-11-10T07:00:00Z"), 0));
		Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
		Page<ClientDTO> result = service.findAllPaged(pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(lista.size(), result.getNumberOfElements());
		for (int i = 0; i < lista.size(); i++) {
			Assertions.assertEquals(lista.get(i), result.toList().get(i).toEntity());
		}
	}

	@Test
	public void testFindByIncome() {
		PageRequest pageRequest = PageRequest.of(0, 1, Direction.valueOf("ASC"), "name");
		Double income = 10000.0;
		List<Client> lista = new ArrayList<Client>();
		lista.add(new Client(8L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0));
		Page<Client> pag = new PageImpl<>(lista, pageRequest, lista.size());
		Page<ClientDTO> result = service.findByIncome(pageRequest, income);
		Assertions.assertFalse(result.isEmpty());
		System.out.println("----------------------"+result.toList());
		Assertions.assertEquals(lista.size(), result.getNumberOfElements());
		for (int i = 0; i < lista.size(); i++) {
			Assertions.assertEquals(lista.get(i), result.toList().get(i).toEntity());
		}
	}

	@Test
	public void testFindByIdExistingId() {
		PageRequest pageRequest = PageRequest.of(0, 1);
		Long id = 10l;
		Optional<Client> client = Optional.of(new Client(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0));
		ClientDTO result = service.findById(id);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(client.get(), result.toEntity());
	}

	@Test
	public void testFindByIdReturnsExceptionNotExistingId() {
		Long id = 1000l;
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
	}

	@Test
	public void testUpdateReturnsExistingId() {
		Long id = 8l;
		Client finalClient = new Client(10L, "Toni Morrison", "10219344681", 15000.0,  Instant.parse("1940-02-23T07:00:00Z"), 1);
		ClientDTO dto = service.update(10L, new ClientDTO(finalClient));
		Assertions.assertEquals(finalClient, dto.toEntity());
	}

	@Test
	public void testUpdateReturnsExceptionNotExistingId() {
		Long id = 1000l;
		Client finalClient = new Client(10L, "Toni Morrison", "10219344681", 15000.0,  Instant.parse("1940-02-23T07:00:00Z"), 1);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(id, new ClientDTO(finalClient)));
	}

	@Test
	public void testInsertNewClient() {
		Long id = 10l;
		Optional<Client> client = Optional.of(new Client());
		Client inicialClient = new Client(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0);
		ClientDTO finalClient = new ClientDTO(10L, "Toni Morrison", "10219344681", 10000.0,  Instant.parse("1940-02-23T07:00:00Z"), 0);
		ClientDTO dto = service.insert(finalClient);
		Assertions.assertEquals(dto.toEntity(), inicialClient);
	}



}