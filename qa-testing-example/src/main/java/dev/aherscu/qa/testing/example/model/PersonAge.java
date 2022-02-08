/*
 * Copyright 2022 Adrian Herscu
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
package dev.aherscu.qa.testing.example.model;

import java.time.*;
import java.util.*;

import dev.aherscu.qa.jgiven.commons.model.*;

/**
 * Represents person age.
 *
 * @author Adrian Herscu
 *
 */
public class PersonAge extends Numeric<Integer> {

    /**
     * @param value
     *            the value to be assigned to this identifier
     */
    public PersonAge(final Integer value) {
        super(value);
    }

    /**
     * Initializes with years since specified birth date.
     * 
     * @param birthDate
     *            the birth date
     */
    public PersonAge(final Date birthDate) {
        super(java.time.Period
            .between(birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(), LocalDate.now())
            .getYears());
    }

}
