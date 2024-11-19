package com.projet.gestioncv.service;


import com.projet.gestioncv.model.Person;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @Test
    @Order(1)
    public void testCreatePerson() {
        Person person = new Person();
        person.setUsername("username");
        person.setFirstName("firstName");
        person.setLastName("lastName");

        Person created = personService.createPerson(person);
        assertEquals("username", created.getUsername());
    }

    @Test
    @Order(2)
    public void testGetPerson() {
        Optional<Person> found = personService.getPerson("username");
        assertTrue(found.isPresent());
        assertEquals("username", found.get().getUsername());
    }

    @Test
    @Order(3)
    public void testUpdatePerson() {
        Person updatedPerson = new Person();
        updatedPerson.setLastName("new lastName");

        Person result = personService.updatePerson("username", updatedPerson);

        assertEquals("new lastName", result.getLastName());
    }

    @Test
    @Order(4)
    public void testDeletePerson() {
        personService.deletePerson("username");
        Optional<Person> found = personService.getPerson("username");
        assertTrue(found.isEmpty());
    }
}
