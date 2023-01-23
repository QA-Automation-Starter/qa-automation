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
package dev.aherscu.qa.tester.utils.config;

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.math.*;
import java.time.*;
import java.util.*;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.combined.*;
import org.apache.commons.configuration2.builder.fluent.*;
import org.apache.commons.configuration2.ex.*;
import org.apache.commons.configuration2.interpol.*;
import org.apache.commons.configuration2.sync.*;
import org.apache.commons.io.*;
import org.apache.commons.text.*;

import lombok.*;

//import lombok.experimental.Delegate;

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
    implements Configuration {

    /**
     * The configuration sources.
     */
    public static final String CONFIGURATION_SOURCES =
        "configuration-sources.xml";                  //$NON-NLS-1$
    // @Delegate(types = Configuration.class)
    protected final T          wrappedConfiguration;

    /**
     * Builds a configuration.
     *
     * @param wrappedConfiguration
     *            the configuration to wrap
     */
    protected AbstractConfiguration(final T wrappedConfiguration) {
        this.wrappedConfiguration = wrappedConfiguration;
    }

    /**
     * @return configuration per {@link #CONFIGURATION_SOURCES}
     * @throws ConfigurationException
     *             if an error occurs
     */
    public static Configuration defaultConfiguration()
        throws ConfigurationException {
        // return new Configurations()
        // .combinedBuilder(AbstractConfiguration.class
        // .getClassLoader()
        // .getResource(CONFIGURATION_SOURCES))
        // .getConfiguration();
        return new CombinedConfigurationBuilder()
            .configure(new Parameters()
                .fileBased()
                .setFileName(CONFIGURATION_SOURCES)
                .setEncoding(UTF_8.toString()))
            .getConfiguration();
    }

    @Override
    public boolean containsKey(final String key) {
        return wrappedConfiguration.containsKey(key);
    }

    @Override
    public <T> T get(final Class<T> cls, final String key) {
        return wrappedConfiguration.get(cls, key);
    }

    @Override
    public <T> T get(final Class<T> cls, final String key,
        final T defaultValue) {
        return wrappedConfiguration.get(cls, key, defaultValue);
    }

    @Override
    public Object getArray(final Class<?> cls, final String key) {
        return wrappedConfiguration.getArray(cls, key);
    }

    @Override
    @Deprecated
    public Object getArray(final Class<?> cls, final String key,
        final Object defaultValue) {
        return wrappedConfiguration.getArray(cls, key, defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(final String key) {
        return wrappedConfiguration.getBigDecimal(key);
    }

    @Override
    public BigDecimal getBigDecimal(final String key,
        final BigDecimal defaultValue) {
        return wrappedConfiguration.getBigDecimal(key, defaultValue);
    }

    @Override
    public BigInteger getBigInteger(final String key) {
        return wrappedConfiguration.getBigInteger(key);
    }

    @Override
    public BigInteger getBigInteger(final String key,
        final BigInteger defaultValue) {
        return wrappedConfiguration.getBigInteger(key, defaultValue);
    }

    @Override
    public boolean getBoolean(final String key) {
        return wrappedConfiguration.getBoolean(key);
    }

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        return wrappedConfiguration.getBoolean(key, defaultValue);
    }

    @Override
    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        return wrappedConfiguration.getBoolean(key, defaultValue);
    }

    @Override
    public byte getByte(final String key) {
        return wrappedConfiguration.getByte(key);
    }

    @Override
    public byte getByte(final String key, final byte defaultValue) {
        return wrappedConfiguration.getByte(key, defaultValue);
    }

    @Override
    public Byte getByte(final String key, final Byte defaultValue) {
        return wrappedConfiguration.getByte(key, defaultValue);
    }

    @Override
    public <T> Collection<T> getCollection(final Class<T> cls, final String key,
        final Collection<T> target) {
        return wrappedConfiguration.getCollection(cls, key, target);
    }

    @Override
    public <T> Collection<T> getCollection(final Class<T> cls, final String key,
        final Collection<T> target, final Collection<T> defaultValue) {
        return wrappedConfiguration.getCollection(cls, key, target,
            defaultValue);
    }

    @Override
    public double getDouble(final String key) {
        return wrappedConfiguration.getDouble(key);
    }

    @Override
    public double getDouble(final String key, final double defaultValue) {
        return wrappedConfiguration.getDouble(key, defaultValue);
    }

    @Override
    public Double getDouble(final String key, final Double defaultValue) {
        return wrappedConfiguration.getDouble(key, defaultValue);
    }

    @Override
    public Duration getDuration(final String key) {
        return wrappedConfiguration.getDuration(key);
    }

    @Override
    public Duration getDuration(final String key, final Duration defaultValue) {
        return wrappedConfiguration.getDuration(key, defaultValue);
    }

    @Override
    public String getEncodedString(final String key) {
        return wrappedConfiguration.getEncodedString(key);
    }

    @Override
    public String getEncodedString(final String key,
        final ConfigurationDecoder decoder) {
        return wrappedConfiguration.getEncodedString(key, decoder);
    }

    @Override
    public <T extends Enum<T>> T getEnum(final String key,
        final Class<T> enumType) {
        return wrappedConfiguration.getEnum(key, enumType);
    }

    @Override
    public <T extends Enum<T>> T getEnum(final String key,
        final Class<T> enumType, final T defaultValue) {
        return wrappedConfiguration.getEnum(key, enumType, defaultValue);
    }

    @Override
    public float getFloat(final String key) {
        return wrappedConfiguration.getFloat(key);
    }

    @Override
    public float getFloat(final String key, final float defaultValue) {
        return wrappedConfiguration.getFloat(key, defaultValue);
    }

    @Override
    public Float getFloat(final String key, final Float defaultValue) {
        return wrappedConfiguration.getFloat(key, defaultValue);
    }

    @Override
    public int getInt(final String key) {
        return wrappedConfiguration.getInt(key);
    }

    @Override
    public int getInt(final String key, final int defaultValue) {
        return wrappedConfiguration.getInt(key, defaultValue);
    }

    @Override
    public Integer getInteger(final String key, final Integer defaultValue) {
        return wrappedConfiguration.getInteger(key, defaultValue);
    }

    @Override
    public Iterator<String> getKeys() {
        return wrappedConfiguration.getKeys();
    }

    @Override
    public Iterator<String> getKeys(final String prefix) {
        return wrappedConfiguration.getKeys(prefix);
    }

    @Override
    public <T> List<T> getList(final Class<T> cls, final String key) {
        return wrappedConfiguration.getList(cls, key);
    }

    @Override
    public <T> List<T> getList(final Class<T> cls, final String key,
        final List<T> defaultValue) {
        return wrappedConfiguration.getList(cls, key, defaultValue);
    }

    @Override
    public List<Object> getList(final String key) {
        return wrappedConfiguration.getList(key);
    }

    @Override
    public List<Object> getList(final String key, final List<?> defaultValue) {
        return wrappedConfiguration.getList(key, defaultValue);
    }

    @Override
    public long getLong(final String key) {
        return wrappedConfiguration.getLong(key);
    }

    @Override
    public long getLong(final String key, final long defaultValue) {
        return wrappedConfiguration.getLong(key, defaultValue);
    }

    @Override
    public Long getLong(final String key, final Long defaultValue) {
        return wrappedConfiguration.getLong(key, defaultValue);
    }

    @Override
    public Properties getProperties(final String key) {
        return wrappedConfiguration.getProperties(key);
    }

    @Override
    public Object getProperty(final String key) {
        return wrappedConfiguration.getProperty(key);
    }

    @Override
    public short getShort(final String key) {
        return wrappedConfiguration.getShort(key);
    }

    @Override
    public short getShort(final String key, final short defaultValue) {
        return wrappedConfiguration.getShort(key, defaultValue);
    }

    @Override
    public Short getShort(final String key, final Short defaultValue) {
        return wrappedConfiguration.getShort(key, defaultValue);
    }

    @Override
    public String getString(final String key) {
        return wrappedConfiguration.getString(key);
    }

    @Override
    public String getString(final String key, final String defaultValue) {
        return wrappedConfiguration.getString(key, defaultValue);
    }

    @Override
    public String[] getStringArray(final String key) {
        return wrappedConfiguration.getStringArray(key);
    }

    @Override
    public ImmutableConfiguration immutableSubset(final String prefix) {
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

    public Set<Map.Entry<Object, Object>> entrySet() {
        return ConfigurationConverter.getMap(wrappedConfiguration).entrySet();
    }

    @Override
    public Synchronizer getSynchronizer() {
        return wrappedConfiguration.getSynchronizer();
    }

    @Override
    public void setSynchronizer(final Synchronizer sync) {
        wrappedConfiguration.setSynchronizer(sync);
    }

    @Override
    public void lock(final LockMode mode) {
        wrappedConfiguration.lock(mode);
    }

    @Override
    public void unlock(final LockMode mode) {
        wrappedConfiguration.unlock(mode);
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
            ConfigurationConverter.getProperties(wrappedConfiguration));
    }

    @Override
    public Configuration subset(final String prefix) {
        return wrappedConfiguration.subset(prefix);
    }

    @Override
    public void addProperty(final String key, final Object value) {
        wrappedConfiguration.addProperty(key, value);
    }

    @Override
    public void setProperty(final String key, final Object value) {
        wrappedConfiguration.setProperty(key, value);
    }

    @Override
    public void clearProperty(final String key) {
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
    public void setInterpolator(final ConfigurationInterpolator ci) {
        wrappedConfiguration.setInterpolator(ci);
    }

    @Override
    public void installInterpolator(
        final Map<String, ? extends Lookup> prefixLookups,
        final Collection<? extends Lookup> defLookups) {
        wrappedConfiguration.installInterpolator(prefixLookups, defLookups);
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
}
