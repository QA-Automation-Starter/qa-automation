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

package dev.aherscu.qa.tester.utils.supermachine.traverser;

import java.lang.reflect.*;
import java.util.*;

// NOTE: code from https://github.com/lbovet/super-machine/
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Traverser {
    private final Map<Object, Object>   _objVisited = new IdentityHashMap<>();
    private final Map<Class, ClassInfo> _classCache = new HashMap<>();

    /**
     * @param o
     *            Any Java Object
     * @param skip
     *            String[] of class names to not include in the tally
     * @param stopClass
     *            stopClass
     * @param visitor
     *            Visitor is called for every object encountered during the Java
     *            object graph traverser.
     */
    public static void traverse(Object o, Class[] skip, Class stopClass,
        Visitor visitor) {
        Traverser traverse = new Traverser();
        traverse.walk(o, skip, stopClass, visitor);
        traverse._objVisited.clear();
        traverse._classCache.clear();
    }

    private static void walkCollection(Deque stack, Collection col) {
        for (Object o : col) {
            if (o != null && !o.getClass().isPrimitive()) {
                stack.add(o);
            }
        }
    }

    // DELETEME -- unused, and marked as endless recursion by spotbugs
    // /**
    // * @param o
    // * Any Java Object
    // * @param visitor
    // * Visitor is called for every object encountered during the Java
    // * object graph traverser.
    // */
    // public static void traverse(Object o, Visitor visitor) {
    // traverse(o, visitor);
    // }

    private static void walkMap(Deque stack, Map map) {
        for (Map.Entry entry : (Iterable<Map.Entry>) map.entrySet()) {
            Object o = entry.getKey();

            if (o != null && !o.getClass().isPrimitive()) {
                stack.add(entry.getKey());
                stack.add(entry.getValue());
            }
        }
    }

    private ClassInfo getClassInfo(Class current, Class[] skip) {
        ClassInfo classCache = _classCache.get(current);
        if (classCache != null) {
            return classCache;
        }

        classCache = new ClassInfo(current, skip);
        _classCache.put(current, classCache);
        return classCache;
    }

    /**
     * Traverse the object graph referenced by the passed in root.
     *
     * @param root
     *            Any Java object.
     * @param skip
     *            Set of classes to skip (ignore). Allowed to be null.
     */
    private void walk(Object root, Class[] skip, Class stopClass,
        Visitor visitor) {
        Deque stack = new LinkedList();
        stack.add(root);

        while (!stack.isEmpty()) {
            Object current = stack.removeFirst();

            if (current == null || _objVisited.containsKey(current)) {
                continue;
            }

            final Class clazz = current.getClass();
            ClassInfo classInfo = getClassInfo(clazz, skip);
            if (classInfo._skip) { // Do not process any classes that are
                                   // assignableFrom the skip classes list.
                continue;
            }

            _objVisited.put(current, null);
            visitor.process(current);

            if (stopClass == null || clazz != stopClass) {
                if (clazz.isArray()) {
                    int len = Array.getLength(current);
                    Class compType = clazz.getComponentType();

                    if (!compType.isPrimitive()) { // Speed up: do not walk
                                                   // primitives
                        ClassInfo info = getClassInfo(compType, skip);
                        if (!info._skip) { // Do not walk array elements of a
                                           // class type that is to be skipped.
                            for (int i = 0; i < len; i++) {
                                Object element = Array.get(current, i);
                                if (element != null) { // Skip processing null
                                                       // array elements
                                    stack.add(Array.get(current, i));
                                }
                            }
                        }
                    }
                } else { // Process fields of an object instance
                    if (current instanceof Collection) {
                        walkCollection(stack, (Collection) current);
                    } else if (current instanceof Map) {
                        walkMap(stack, (Map) current);
                    } else {
                        walkFields(stack, current, skip);
                    }
                }
            }
        }
    }

    private void walkFields(Deque stack, Object current, Class[] skip) {
        ClassInfo classInfo = getClassInfo(current.getClass(), skip);

        for (Field field : classInfo._refFields) {
            try {
                Object value = field.get(current);
                if (value == null || value.getClass().isPrimitive()) {
                    continue;
                }
                stack.add(value);
            } catch (@SuppressWarnings("unused") IllegalAccessException ignored) {
            }
        }
    }

    public interface Visitor {
        void process(Object o);
    }

    /**
     * This class wraps a class in order to cache the fields so they are only
     * reflectively obtained once.
     */
    private static class ClassInfo {
        private final Collection<Field> _refFields = new ArrayList<>();
        private boolean                 _skip      = false;

        public ClassInfo(Class c, Class[] skip) {
            if (skip != null) {
                for (Class klass : skip) {
                    if (klass.isAssignableFrom(c)) {
                        _skip = true;
                        return;
                    }
                }
            }

            Collection<Field> fields = ReflectionUtils.getDeepDeclaredFields(c);
            for (Field field : fields) {
                Class fc = field.getType();

                if (!fc.isPrimitive()) {
                    _refFields.add(field);
                }
            }
        }
    }
}
