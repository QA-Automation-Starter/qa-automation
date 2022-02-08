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
package dev.aherscu.qa.tester.utils;

import java.util.*;

import org.apache.commons.lang3.tuple.*;

import lombok.*;
import lombok.experimental.*;

/**
 * Map utilities.
 * 
 * @author aherscu
 *
 */
@UtilityClass
public final class MapUtils {
    /**
     * Creates a map from a list of pairs.
     * 
     * @param pairs
     *            one ore more pairs; if none, an empty map will be returned
     * 
     * @param <L>
     *            the type of left-side of the pair; this will become the key of
     *            the map
     * @param <R>
     *            the type of right-side of the pair; this will become the value
     *            of the map
     * 
     * @return the map of pairs
     * 
     * @see Pair
     */
    @SafeVarargs
    public static <L, R> Map<L, R> mapOf(final Pair<L, R>... pairs) {
        val pairsMap = new HashMap<L, R>();
        // TODO eliminate the for loop below by encapsulating the pairs array
        // into a map
        for (val pair : pairs) {
            pairsMap.put(pair.getKey(), pair.getValue());
        }
        return pairsMap;
    }
}
