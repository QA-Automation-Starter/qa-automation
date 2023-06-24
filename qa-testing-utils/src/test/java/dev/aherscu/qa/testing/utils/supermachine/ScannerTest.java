/*
 * Copyright 2023 Adrian Herscu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.aherscu.qa.testing.utils.supermachine;

import static dev.aherscu.qa.testing.utils.supermachine.BeanScanner.*;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;
import org.testng.annotations.*;

import lombok.*;

@Test
public class ScannerTest {
    public void addresses() {
        Company company =
            Company.builder().departments(new HashMap<>()).build();
        Address addr = Address.builder().build();
        City ny = City.builder()
            .name("New York")
            .mayor(Person.builder()
                .name("Rudi")
                .addresses(new Address[] { addr })
                .build())
            .build();
        addr.setCity(ny);
        addr.setStreet("House St.");
        company.getDepartments().put("software",
            Department.builder()
                .boss(Person.builder()
                    .name("Bill")
                    .build())
                .employees(Collections.singletonList(
                    Person.builder()
                        .name("John")
                        .addresses(new Address[] { Address.builder()
                            .street("Wall St.")
                            .city(ny)
                            .build() })
                        .build()))
                .employees(Collections.singletonList(
                    Person.builder()
                        .name("Mike")
                        .addresses(new Address[] { Address.builder()
                            .street("Long St.")
                            .city(ny)
                            .build() })
                        .build()))
                .build());
        company.getDepartments().put("hardware",
            Department.builder()
                .boss(Person.builder()
                    .name("Steve")
                    .build())
                .employees(Arrays.asList(
                    Person.builder()
                        .name("Kylie")
                        .addresses(new Address[] { Address.builder()
                            .street("5th Ave.")
                            .city(ny)
                            .build() })
                        .build(),
                    Person.builder()
                        .name("Paul")
                        .addresses(new Address[] { Address.builder()
                            .street("Downing St.")
                            .city(City.builder().name("London").build())
                            .build() })
                        .build()))
                .build());
        company.getDepartments().put("sales",
            Department.builder()
                .boss(Person.builder()
                    .name("Donald")
                    .build())
                .employees(Collections.singletonList(
                    Person.builder()
                        .name("Margareth")
                        .addresses(new Address[] { Address.builder()
                            .street("Hyde Park")
                            .city(City.builder().name("London").build())
                            .build(),
                            Address.builder()
                                .street("Short St.")
                                .city(ny)
                                .build() })
                        .build()))
                .build());

        String[] names =
            from(company)
                .find(Department.class)
                .filter(d -> !d.getBoss().getName().equals("Bill"))
                .find(Person.class)
                .filter(p -> from(p).find(City.class).extract(City::getMayor)
                    .stream().anyMatch(m -> m.getName().equals("Rudi")))
                .then(e -> e.extract(Person::getName),
                    e -> e.extract(p -> p.getAddresses()[0])
                        .extract(Address::getStreet))
                .stream()
                .toArray(String[]::new);

        Assertions.assertThat(names)
            .containsExactly(
                "Margareth", "Hyde Park", "Kylie", "5th Ave.");
    }

    public void binaryTree() {
        BinaryTree tree = new BinaryTree("root",
            new BinaryTree("left",
                new BinaryTree("left-left", null, null, null),
                new BinaryTree("left-right", null, null, null), null),
            new BinaryTree("right",
                new BinaryTree("right-left", null, null, null),
                new BinaryTree("right-right", null, null, null), null),
            null);
        String[] result =
            from(tree).find(String.class).stream().toArray(String[]::new);

        Assertions.assertThat(result)
            .containsExactly(
                "root", "left", "right",
                "left-left", "left-right",
                "right-left", "right-right");
    }

    public void streamScanner() {
        String[] result =
            StreamScanner
                .scan(Stream.concat(IntStream.range(0, 40).boxed(),
                    DoubleStream.of(24.0).boxed()))
                .find(Integer.class)
                .filter(i -> i % 4 == 0)
                .extract(Object::toString)
                .then((s -> s.filter(x -> x.startsWith("2"))),
                    (s -> s.filter(x -> x.endsWith("2"))))
                .stream()
                .toArray(String[]::new);

        Assertions.assertThat(result)
            .containsExactly("12", "20", "24", "28", "32");
    }

    @Data
    @Builder
    private static class Address {
        private String street;
        private City   city;
    }

    @Data
    @Builder
    private static class BinaryTree {
        private String     name;
        private BinaryTree left;
        private BinaryTree right;
        private Object     element;
    }

    @Data
    @Builder
    private static class City {
        String name;
        Person mayor;
    }

    @Data
    @Builder
    private static class Company {
        private Map<String, Department> departments;
    }

    @Data
    @Builder
    private static class Department {
        private Person       boss;
        private List<Person> employees;
    }

    @Data
    @Builder
    private static class Person {
        private String    name;
        private Address[] addresses;
    }
}
