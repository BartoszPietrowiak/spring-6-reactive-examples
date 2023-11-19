package guru.springframework.spring6reactiveexamples.repository;

import guru.springframework.spring6reactiveexamples.domain.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonRepositoryImplTest {


    private final PersonRepository personRepository = new PersonRepositoryImpl();

    @Test
    void testMonoByIdBlock() {
        Mono<Person> personMono = personRepository.getById(2);

        Person person = personMono.block();

        System.out.println(person.toString());
    }

    @Test
    void testMonoByIdSubscriber() {
        Mono<Person> personMono = personRepository.getById(1);

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testMapOperation() {
        Mono<Person> personMono = personRepository.getById(6);

        personMono.map(Person::getFirstName)
                .subscribe(System.out::println);
    }

    @Test
    void testFluxBlock() {
        Flux<Person> personFlux = personRepository.findAll();

        Person person = personFlux.blockFirst();

        System.out.println(person.toString());
    }

    @Test
    void testFluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFluxMapOperation() {
        Flux<Person> personFlux = personRepository.findAll();
        personFlux.map(Person::getFirstName)
                .subscribe(System.out::println);
    }

    @Test
    void testFluxToList() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> listMono = personFlux.collectList();

        listMono.subscribe(list -> list.forEach(System.out::println));
    }

    @Test
    void testFilterOnName() {
        personRepository.findAll()
                .filter(person -> person.getFirstName().equals("Fiona"))
                .subscribe(System.out::println);
    }

    @Test
    void testFindPersonByIdNotFound() {
        Mono<Person> personMono = personRepository.findAll()
                .filter(person -> person.getId().equals(0))
                .single()
                .doOnError(throwable -> {
                    System.out.println("Error occured in flux");
                    System.out.println(throwable.toString());
                });

        personMono.subscribe(System.out::println);
    }

    @Test
    void getByIdFound() {
        Mono<Person> personMono = personRepository.getById(3);

        assertTrue(personMono.hasElement().block());
    }

    @Test
    void getByIdFoundStepVerifier() {
        Mono<Person> personMono = personRepository.getById(3);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        personMono.subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void getByIdNotFoundStepVerifier() {
        Mono<Person> personMono = personRepository.getById(8);

        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();

        personMono.subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void getByIdNotFound() {
        Mono<Person> personMono = personRepository.getById(8);


        assertFalse(personMono.hasElement().block());
    }

}
