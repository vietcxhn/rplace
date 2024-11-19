package com.projet.gestioncv.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.projet.gestioncv.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;

@DataJpaTest
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testCreateAndFindPerson() {
        Person person = new Person();
        person.setUsername("username1");
        person.setLastName("lastname1");
        person.setFirstName("firstname1");
        person.setEmail("person1@gmail.com");
        person.setPassword("password");
        personRepository.save(person);

        Optional<Person> found = personRepository.findById(person.getUsername());
        assertTrue(found.isPresent());
        assertEquals("lastname1", found.get().getLastName());
    }

    @Test
    public void testUpdatePerson() {
        Person person = new Person();
        person.setUsername("username2");
        person.setLastName("lastname2");
        person.setFirstName("firstname2");
        person.setEmail("person2@gmail.com");
        person.setPassword("password");
        personRepository.save(person);

        person.setLastName("lastname2 change");
        personRepository.save(person);

        Optional<Person> updatedPerson = personRepository.findById(person.getUsername());
        assertTrue(updatedPerson.isPresent());
        assertEquals("lastname2 change", updatedPerson.get().getLastName());
    }

    @Test
    public void testDeletePerson() {
        Person person = new Person();
        person.setUsername("username3");
        person.setLastName("lastname3");
        person.setFirstName("firstname3");
        person.setEmail("person3@gmail.com");
        person.setPassword("password");
        personRepository.save(person);

        personRepository.delete(person);
        Optional<Person> deletedPerson = personRepository.findById(person.getUsername());
        assertFalse(deletedPerson.isPresent());
    }
}
