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
package dev.aherscu.qa.tester.utils.config;

import static java.nio.charset.StandardCharsets.*;
import static java.util.Spliterator.*;
import static java.util.Spliterators.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.StreamSupport.*;

import java.io.*;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.fluent.*;
import org.apache.commons.configuration2.ex.*;
import org.apache.commons.configuration2.interpol.*;
import org.apache.commons.configuration2.sync.*;
import org.apache.commons.io.*;
import org.apache.commons.text.*;

import com.google.common.collect.*;

import lombok.*;

/**
 * Makes a {@link Configuration} look like a {@link Map}.
 *
 * @author aherscu
 *
 * @param <T>
 *            type of wrapped configuration
 */
@SuppressWarnings("ClassWithTooManyMethods")
public class AbstractConfiguration<T extends Configuration>
    implements Configuration, Map<Object, Object> {

    /**
     * The configuration sources.
     */
    public static final String CONFIGURATION_SOURCES =
        "configuration-sources.xml";                  //$NON-NLS-1$

    // static {
    // // IMPORTANT: otherwise it will try to parse strings containing commas
    // // into lists
    // org.apache.commons.configuration.AbstractConfiguration
    // .setDefaultListDelimiter((char) 0);
    // }

    protected final T          wrappedConfiguration;

    /**
     * Builds a configuration.
     *
     * @param wrappedConfiguration
     *            the configuration to wrap
     */
    protected AbstractConfiguration(final T wrappedConfiguration) {
        this.wrappedConfiguration = wrappedConfiguration;

        if (wrappedConfiguration instanceof org.apache.commons.configuration2.AbstractConfiguration) {
            ((org.apache.commons.configuration2.AbstractConfiguration) wrappedConfiguration)
                .setThrowExceptionOnMissing(true);
        }
    }

    /**
     * @return configuration per {@link #CONFIGURATION_SOURCES}
     * @throws ConfigurationException
     *             if an error occurs
     */
    public static Configuration defaultConfiguration()
        throws ConfigurationException {
        return new Configurations()
            .combinedBuilder(new File(CONFIGURATION_SOURCES))
            .getConfiguration();
    }

    /**
     * Resolves {@link AbstractConfiguration} references in a string.
     *
     * @param value
     *            the value to process after {@link Object#toString()} is called
     * @return the supplied value with all its <code>${...}</code> references
     *         resolved
     */
    public String resolve(final Object value) {
        return StringSubstitutor.replace(value,
            ConfigurationConverter.getProperties(this));
    }

    /**
     * Reads string contents of referenced resource.
     *
     * @param path
     *            the path from root of classes
     * @return the string contents
     * @throws NullPointerException
     *             if the resource could not be found or not enough privileges
     *             to access
     */
    @SneakyThrows(IOException.class)
    protected String stringResourceFrom(final String path) {
        return IOUtils
            .toString(
                Objects.requireNonNull(getClass()
                    .getClassLoader()
                    .getResource(path),
                    "cannot find or access resource"),
                UTF_8);
    }

    @Override
    public Configuration subset(String prefix) {
        return wrappedConfiguration.subset(prefix);
    }

    @Override
    public void addProperty(String key, Object value) {
        wrappedConfiguration.addProperty(key, value);
    }

    @Override
    public void setProperty(String key, Object value) {
        wrappedConfiguration.setProperty(key, value);
    }

    @Override
    public void clearProperty(String key) {
        wrappedConfiguration.clearProperty(key);
    }

    @Override
    public void clear() {
        wrappedConfiguration.clear();
    }

    @Override
    public ConfigurationInterpolator getInterpolator() {
        return wrappedConfiguration.getInterpolator();
    }

    @Override
    public void setInterpolator(ConfigurationInterpolator ci) {
        wrappedConfiguration.setInterpolator(ci);
    }

    @Override
    public void installInterpolator(Map<String, ? extends Lookup> prefixLookups,
        Collection<? extends Lookup> defLookups) {
        wrappedConfiguration.installInterpolator(prefixLookups, defLookups);
    }

    @Override
    public boolean containsKey(String key) {
        return wrappedConfiguration.containsKey(key);
    }

    @Override
    public <T> T get(Class<T> cls, String key) {
        return wrappedConfiguration.get(cls, key);
    }

    @Override
    public <T> T get(Class<T> cls, String key, T defaultValue) {
        return wrappedConfiguration.get(cls, key, defaultValue);
    }

    @Override
    public Object getArray(Class<?> cls, String key) {
        return wrappedConfiguration.getArray(cls, key);
    }

    @Override
    @Deprecated
    public Object getArray(Class<?> cls, String key, Object defaultValue) {
        return wrappedConfiguration.getArray(cls, key, defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return wrappedConfiguration.getBigDecimal(key);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return wrappedConfiguration.getBigDecimal(key, defaultValue);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return wrappedConfiguration.getBigInteger(key);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return wrappedConfiguration.getBigInteger(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key) {
        return wrappedConfiguration.getBoolean(key);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return wrappedConfiguration.getBoolean(key, defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return wrappedConfiguration.getBoolean(key, defaultValue);
    }

    @Override
    public byte getByte(String key) {
        return wrappedConfiguration.getByte(key);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return wrappedConfiguration.getByte(key, defaultValue);
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return wrappedConfiguration.getByte(key, defaultValue);
    }

    @Override
    public <T> Collection<T> getCollection(Class<T> cls, String key,
        Collection<T> target) {
        return wrappedConfiguration.getCollection(cls, key, target);
    }

    @Override
    public <T> Collection<T> getCollection(Class<T> cls, String key,
        Collection<T> target, Collection<T> defaultValue) {
        return wrappedConfiguration.getCollection(cls, key, target,
            defaultValue);
    }

    @Override
    public double getDouble(String key) {
        return wrappedConfiguration.getDouble(key);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return wrappedConfiguration.getDouble(key, defaultValue);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return wrappedConfiguration.getDouble(key, defaultValue);
    }

    @Override
    public Duration getDuration(String key) {
        return wrappedConfiguration.getDuration(key);
    }

    @Override
    public Duration getDuration(String key, Duration defaultValue) {
        return wrappedConfiguration.getDuration(key, defaultValue);
    }

    @Override
    public String getEncodedString(String key) {
        return wrappedConfiguration.getEncodedString(key);
    }

    @Override
    public String getEncodedString(String key, ConfigurationDecoder decoder) {
        return wrappedConfiguration.getEncodedString(key, decoder);
    }

    @Override
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumType) {
        return wrappedConfiguration.getEnum(key, enumType);
    }

    @Override
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumType,
        T defaultValue) {
        return wrappedConfiguration.getEnum(key, enumType, defaultValue);
    }

    @Override
    public float getFloat(String key) {
        return wrappedConfiguration.getFloat(key);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return wrappedConfiguration.getFloat(key, defaultValue);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return wrappedConfiguration.getFloat(key, defaultValue);
    }

    @Override
    public int getInt(String key) {
        return wrappedConfiguration.getInt(key);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return wrappedConfiguration.getInt(key, defaultValue);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return wrappedConfiguration.getInteger(key, defaultValue);
    }

    @Override
    public Iterator<String> getKeys() {
        return wrappedConfiguration.getKeys();
    }

    @Override
    public Iterator<String> getKeys(String prefix) {
        return wrappedConfiguration.getKeys(prefix);
    }

    @Override
    public <T> List<T> getList(Class<T> cls, String key) {
        return wrappedConfiguration.getList(cls, key);
    }

    @Override
    public <T> List<T> getList(Class<T> cls, String key, List<T> defaultValue) {
        return wrappedConfiguration.getList(cls, key, defaultValue);
    }

    @Override
    public List<Object> getList(String key) {
        return wrappedConfiguration.getList(key);
    }

    @Override
    public List<Object> getList(String key, List<?> defaultValue) {
        return wrappedConfiguration.getList(key, defaultValue);
    }

    @Override
    public long getLong(String key) {
        return wrappedConfiguration.getLong(key);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return wrappedConfiguration.getLong(key, defaultValue);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return wrappedConfiguration.getLong(key, defaultValue);
    }

    @Override
    public Properties getProperties(String key) {
        return wrappedConfiguration.getProperties(key);
    }

    @Override
    public Object getProperty(String key) {
        return wrappedConfiguration.getProperty(key);
    }

    @Override
    public short getShort(String key) {
        return wrappedConfiguration.getShort(key);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return wrappedConfiguration.getShort(key, defaultValue);
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return wrappedConfiguration.getShort(key, defaultValue);
    }

    @Override
    public String getString(String key) {
        return wrappedConfiguration.getString(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return wrappedConfiguration.getString(key, defaultValue);
    }

    @Override
    public String[] getStringArray(String key) {
        return wrappedConfiguration.getStringArray(key);
    }

    @Override
    public ImmutableConfiguration immutableSubset(String prefix) {
        return wrappedConfiguration.immutableSubset(prefix);
    }

    @Override
    public boolean isEmpty() {
        return wrappedConfiguration.isEmpty();
    }

    @Override
    public int size() {
        return wrappedConfiguration.size();
    }

    @Override
    public Synchronizer getSynchronizer() {
        return wrappedConfiguration.getSynchronizer();
    }

    @Override
    public void setSynchronizer(Synchronizer sync) {
        wrappedConfiguration.setSynchronizer(sync);
    }

    @Override
    public void lock(LockMode mode) {
        wrappedConfiguration.lock(mode);
    }

    @Override
    public void unlock(LockMode mode) {
        wrappedConfiguration.unlock(mode);
    }

    @Override
    public boolean containsKey(Object key) {
        return wrappedConfiguration.containsKey(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    public Object get(Object key) {
        return wrappedConfiguration.getString(key.toString());
    }

    @Override
    public Object put(Object key, Object value) {
        val v = wrappedConfiguration.getString(key.toString());
        wrappedConfiguration.setProperty(key.toString(), value);
        return v;
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<?, ?> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Object> keySet() {
        return streamKeys().collect(toSet());
    }

    @Override
    public Collection<Object> values() {
        return streamKeys()
            .map(wrappedConfiguration::getString)
            .collect(toSet());
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return streamKeys()
            .map(key -> Maps.<Object, Object> immutableEntry(
                key,
                wrappedConfiguration.getString(key)))
            .collect(toSet());
    }

    private Stream<String> streamKeys() {
        return stream(
            spliteratorUnknownSize(wrappedConfiguration.getKeys(), ORDERED),
            false);
    }
}
