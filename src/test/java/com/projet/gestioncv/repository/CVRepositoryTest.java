package com.projet.gestioncv.repository;

import com.projet.gestioncv.model.CV;
import com.projet.gestioncv.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CVRepositoryTest {

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testCreateAndFindCV() {
        Person person = new Person();
        person.setUsername("username1");
        person.setLastName("lastname1");
        person.setFirstName("firstname1");
        person.setEmail("person1@gmail.com");
        person.setPassword("password");
        personRepository.save(person);

        CV cv = new CV();
        cv.setPerson(person);
        person.setCv(cv);
        cvRepository.save(cv);

        Optional<Person> oPerson = personRepository.findById(person.getUsername());
        assertTrue(oPerson.isPresent());
        assertNotNull(oPerson.get().getCv());
        assertEquals("lastname1", oPerson.get().getLastName());
    }

    @Test
    public void testUpdateCV() {
        Person person = new Person();
        person.setUsername("username2");
        person.setLastName("lastname2");
        person.setFirstName("firstname2");
        person.setEmail("person2@gmail.com");
        person.setPassword("password");
        personRepository.save(person);

        Person person2 = new Person();
        person2.setUsername("username3");
        person2.setLastName("lastname3");
        person2.setFirstName("firstname3");
        person2.setEmail("person3@gmail.com");
        person2.setPassword("password");
        personRepository.save(person2);

        CV cv = new CV();
        cv.setPerson(person);
        cvRepository.save(cv);

        cv.setPerson(person2);
        cvRepository.save(cv);

        Optional<CV> updatedCV = cvRepository.findById(cv.getId());
        assertTrue(updatedCV.isPresent());
        assertEquals(person2.getUsername(), updatedCV.get().getPerson().getUsername());
    }

    @Test
    public void testDeleteCV() {
        Person person = new Person();
        person.setUsername("username4");
        person.setLastName("lastname4");
        person.setFirstName("firstname4");
        person.setEmail("person4@gmail.com");
        person.setPassword("password");
        personRepository.save(person);

        CV cv = new CV();
        cv.setPerson(person);
        cvRepository.save(cv);

        cvRepository.delete(cv);
        Optional<CV> deletedCV = cvRepository.findById(cv.getId());
        assertFalse(deletedCV.isPresent());
    }
}
