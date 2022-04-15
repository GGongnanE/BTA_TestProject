package com.ggne.practice.week1;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeWork {


    /**
     * Q1.
     * ["Blenders", "Old", "Johnnie"] 와
     * "[Pride", "Monk", "Walker”] 를 순서대로 하나의 스트림으로
     * 처리되는 로직 검증
     */
    @Test
    public void case01() {
        Flux<String> first_grp = Flux.just("Blenders", "Old", "Johnnie")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> second_grp = Flux.just("Pride", "Monk", "Walker")
                .delayElements(Duration.ofSeconds(1));
        Flux<String> result = Flux.concat(first_grp, second_grp).log();

        // 검증(Verify)
        StepVerifier.create(result)
                .expectSubscription()
                .expectNext("Blenders", "Old", "Johnnie","Pride", "Monk", "Walker")
                .verifyComplete();
    }

    /**
     *  Q2.
     *  1~100 까지의 자연수 중 짝수만 출력하는 로직 검증
     */
    @Test
    public void case02() {

        Flux<Integer> number = Flux.range(1, 100)
                        .filter(num -> num % 2 == 0).log();

        StepVerifier.create(number)
                .expectSubscription()
                .thenConsumeWhile(i -> i % 2 == 0)
                .verifyComplete();
    }

    /**
     *  Q3.
     *  “hello”, “there” 를 순차적으로 publish하여
     *  순서대로 나오는지 검증
     */
    @Test
    public void case03() {
        // TODO : Mono로 구현해야할 것 같다.
        Flux<String> first_str = Flux.just("hello");
        Flux<String> second_str = Flux.just("there");
        Flux<String> result = Flux.concat(first_str, second_str).log();

        StepVerifier.create(result)
                .expectSubscription()
                .expectNext("hello", "there")
                .verifyComplete();
    }


    /**
     *  Q4.
     *  아래와 같은 객체가 전달될 때 “JOHN”, “JACK” 등
     *  이름이 대문자로 변환되어 출력되는 로직 검증
     */
    @Test
    public void case04() {

        // Test Object
        Person john = new Person("John", "[john@gmail.com](mailto:john@gmail.com)", "12345678");
        Person jack = new Person("Jack", "[jack@gmail.com](mailto:jack@gmail.com)", "12345678");

        Flux<Person> humanFlux = Flux.just(john, jack)
                .map(person -> {
                   person.setName(person.getName().toUpperCase());
                   return person;
                }).log();

        StepVerifier.create(humanFlux)
                .assertNext(person -> {assertThat(person.getName()).isEqualTo("JOHN");})
                .assertNext(person -> {assertThat(person.getName()).isEqualTo("JACK");})
                .verifyComplete();
    }

    /**
     *  Q5.
     *  ["Blenders", "Old", "Johnnie"] 와
     *  ["Pride", "Monk", "Walker”]를 압축하여
     *  스트림으로 처리 검증
     *
     *  예상되는 스트림 결과값
     *  ["Blenders Pride", "Old Monk", "Johnnie Walker”]
     */
    @Test
    public void case05() {
        Flux<String> firstStr = Flux.fromArray(new String[] {"Blenders", "Old", "Johnnie"});
        Flux<String> secondStr = Flux.just("Pride", "Monk", "Walker");
        Flux<String> result = Flux.zip(firstStr, secondStr, (str1, str2) -> str1 + " " + str2).log();

        StepVerifier.create(result)
                .expectNext("[\"Blenders Pride\", \"Old Monk\", \"Johnnie Walker”]");
    }

    /**
     *  Q6.
     *  ["google", "abc", "fb", "stackoverflow”] 의 문자열 중
     *  5자 이상 되는 문자열만 대문자로 비동기로 치환하여 1번 반복하는 스트림으로 처리하는 로직 검증
     */
    @Test
    public void case06() {
        Flux<String> strFlux = Flux.just("google", "abc", "fb", "stackoverflow")
                .filter(str -> str.length() >= 5)
                .flatMap(str -> Flux.just(str.toUpperCase()))
                .repeat(1)
                .log();

        StepVerifier.create(strFlux)
                .expectNext("GOOGLE", "STACKOVERFLOW", "GOOGLE", "STACKOVERFLOW")
                .verifyComplete();
    }
}