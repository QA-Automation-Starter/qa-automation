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

import java.awt.*;
import java.awt.RenderingHints.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

import javax.imageio.*;

import com.google.common.collect.*;

import lombok.*;
import lombok.experimental.*;
import net.coobird.thumbnailator.*;
import org.apache.commons.lang3.tuple.*;

/**
 * Image manipulation utilities.
 *
 * @author Adrian Herscu
 */
@UtilityClass
public class ImageUtils {
    /**
     * Rendering hints for best quality with scaling.
     *
     * @see #scale(BufferedImage, float, float, Map)
     */
    public final static Map<Key, Object> DEFAULT_RENDERING_HINTS =
        ImmutableMap.<RenderingHints.Key, Object> builder()
            .put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
            .build();
    /**
     * Four-bit color model.
     */
    public final static IndexColorModel  FOUR_BIT_COLOR_MODEL;
    /**
     * Gray scale color model.
     */
    public final static IndexColorModel  GREY_SCALE_COLOR_MODEL;

    static {
        // noinspection MagicNumber
        val SIZE = 256;
        val r = new byte[SIZE];
        val g = new byte[SIZE];
        val b = new byte[SIZE];
        for (int i = 0; i < SIZE; i++) {
            r[i] = g[i] = b[i] = (byte) i;
        }
        GREY_SCALE_COLOR_MODEL = new IndexColorModel(8, SIZE, r, g, b);
    }

    static {
        val cmap = new int[] { 0x000000, 0x800000, 0x008000, 0x808000,
            0x000080, 0x800080, 0x008080, 0x808080, 0xC0C0C0, 0xFF0000,
            0x00FF00, 0xFFFF00, 0x0000FF, 0xFF00FF, 0x00FFFF, 0xFFFFFF };

        FOUR_BIT_COLOR_MODEL = new IndexColorModel(4, cmap.length, cmap, 0,
            false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
    }

    /**
     * Reduces the color palette of specified image.
     *
     * @param inputImage
     *            the image to reduce
     * @param indexColorModel
     *            the color model to apply
     * @return the reduced image
     */
    public static BufferedImage reduce(
        final BufferedImage inputImage,
        final IndexColorModel indexColorModel) {

        val outputReducedImage =
            new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),
                BufferedImage.TYPE_BYTE_INDEXED,
                indexColorModel);

        val g2 = outputReducedImage.createGraphics();
        g2.drawImage(inputImage, 0, 0, null);
        g2.dispose();

        return outputReducedImage;
    }

    /**
     * Scales a specified image using plain calls to {@link Graphics2D} and
     * {@link AffineTransform}.
     * <p>
     * Not best quality when scaling down.
     * </p>
     *
     * @param inputImage
     *            the image to scale
     * @param widthScale
     *            width scale factor
     * @param heightScale
     *            height scale factor
     * @param hints
     *            rendering hints
     * @return the scaled image
     * @see #DEFAULT_RENDERING_HINTS
     */
    public static BufferedImage scale(
        final BufferedImage inputImage,
        final float widthScale,
        final float heightScale,
        final Map<Key, Object> hints) {

        val outputScaledImage =
            new BufferedImage(
                Math.round(inputImage.getWidth() * widthScale),
                Math.round(inputImage.getHeight() * heightScale),
                BufferedImage.TYPE_BYTE_INDEXED);

        val g2 = outputScaledImage.createGraphics();

        g2.setRenderingHints(hints);

        g2.drawRenderedImage(inputImage,
            AffineTransform.getScaleInstance(widthScale, heightScale));

        g2.dispose();

        return outputScaledImage;
    }

    /**
     * Scales a specified image using dedicated library.
     * <p>
     * Good quality when scaling down.
     * </p>
     *
     * @param inputImage
     *            the image to scale
     * @param widthScale
     *            the width scale factor
     * @param heightScale
     *            the height scale factor
     * @return the scaled image
     */
    @SneakyThrows
    public static BufferedImage scale(
        final BufferedImage inputImage,
        final double widthScale,
        final double heightScale) {
        return Thumbnails.of(inputImage)
            .scale(widthScale, heightScale)
            .asBufferedImage();
    }

    /**
     * @param image
     *            the image
     * @return the area of image
     */
    public static double areaOf(final BufferedImage image) {
        return image.getWidth() * image.getHeight();
    }

    /**
     * Makes two images same size by scaling down the image with bigger area.
     * 
     * @param pair
     *            images to adapt
     * @return adapted images
     */
    public static Pair<BufferedImage, BufferedImage> adapt(
        final Pair<BufferedImage, BufferedImage> pair) {
        return areaOf(pair.getLeft()) < areaOf(pair.getRight())
            ? Pair.of(pair.getLeft(),
                Pipeline.from(pair.getRight()).scale(
                    1d * pair.getLeft().getWidth()
                        / pair.getRight().getWidth(),
                    1d * pair.getLeft().getHeight()
                        / pair.getRight().getHeight()).image)
            : Pair.of(Pipeline.from(pair.getLeft()).scale(
                1d * pair.getRight().getWidth()
                    / pair.getLeft().getWidth(),
                1d * pair.getRight().getHeight()
                    / pair.getLeft().getHeight()).image,
                pair.getRight());
    }

    /**
     * Image transformation pipeline. Call {@link #from(InputStream)} or
     * {@link #from(BufferedImage)} to start the pipeline. Call
     * {@link #into(Supplier, String)} to end the pipeline.
     */
    @RequiredArgsConstructor
    public static class Pipeline {
        /**
         * The encapsulated image in this pipeline.
         */
        public final BufferedImage image;

        /**
         * Starts an image processing pipeline.
         *
         * @param image
         *            the image to process
         * @return a processing pipeline
         */
        public static Pipeline from(final BufferedImage image) {
            return new Pipeline(image);
        }

        /**
         * Starts an image processing pipeline.
         *
         * @param input
         *            input stream providing the image to process
         * @return a processing pipeline
         */
        @SneakyThrows
        public static Pipeline from(final InputStream input) {
            return new Pipeline(ImageIO.read(input));
        }

        /**
         * Ends the image processing pipeline.
         *
         * @param output
         *            the output stream to serialize into
         * @param format
         *            informal image format label
         * @param <OUTPUT>
         *            type of output stream
         * @return the filled output stream; useful with in-memory streams (e.g.
         *         {@link ByteArrayOutputStream}
         */
        @SneakyThrows
        public <OUTPUT extends OutputStream> OUTPUT into(
            final OUTPUT output,
            final String format) {
            ImageIO.write(image, format, output);
            return output;
        }

        /**
         * Ends the image processing pipeline.
         *
         * @param outputSupplier
         *            the output stream to serialize into; will be closed
         *            automatically
         * @param format
         *            informal image format label
         * @param <OUTPUT>
         *            type of output stream
         * @return the filled output stream; useful with in-memory streams (e.g.
         *         {@link ByteArrayOutputStream}
         */
        @SneakyThrows
        public <OUTPUT extends OutputStream> OUTPUT into(
            final Supplier<OUTPUT> outputSupplier,
            final String format) {
            try (val output = outputSupplier.get()) {
                return into(output, format);
            }
        }

        /**
         * Applies a specified pipeline. Allows using predefined pipelines.
         *
         * @param pipeline
         *            a specified pipeline
         * @return this pipeline
         */
        public Pipeline map(final UnaryOperator<Pipeline> pipeline) {
            return pipeline.apply(this);
        }

        /**
         * Reduces the color palette per specified model.
         *
         * @param indexColorModel
         *            the color model to apply
         * @return this pipeline
         */
        public Pipeline reduce(final IndexColorModel indexColorModel) {
            return new Pipeline(
                ImageUtils.reduce(image, indexColorModel));
        }

        /**
         * Scales by specified factors.
         * <p>
         * Good quality when scaling down.
         * </p>
         *
         * @param widthScale
         *            the width scale factor
         * @param heightScale
         *            the height scale factor
         * @return this pipeline
         */
        public Pipeline scale(
            final double widthScale,
            final double heightScale) {
            return new Pipeline(ImageUtils
                .scale(image, widthScale, heightScale));
        }

        /**
         * Scales by specified factors and hints.
         * <p>
         * Good quality when scaling down.
         * </p>
         *
         * @param widthScale
         *            the width scale factor
         * @param heightScale
         *            the height scale factor
         * @param hints
         *            rendering hints
         * @return this pipeline
         *
         * @see #DEFAULT_RENDERING_HINTS
         */
        public Pipeline scale(
            final float widthScale,
            final float heightScale,
            final Map<Key, Object> hints) {
            return new Pipeline(ImageUtils
                .scale(image, widthScale, heightScale, hints));
        }

        /**
         * Difference between this image and other image.
         * 
         * @param other
         *            image to find difference from
         * @param differentiator
         *            a differentiating method
         * @param adapter
         *            an adaptation method
         * @param <D>
         *            type of difference
         * @return the difference
         */
        public <D> D diff(final BufferedImage other,
            final Function<Pair<BufferedImage, BufferedImage>, D> differentiator,
            final Function<Pair<BufferedImage, BufferedImage>, Pair<BufferedImage, BufferedImage>> adapter) {
            return differentiator.apply(adapter.apply(Pair.of(image, other)));
        }

        /**
         * Difference between this image and other image, using a
         * {@link ImageUtils#adapt(Pair)} as default adapter.
         *
         * @param other
         *            image to find difference from
         * @param differentiator
         *            a differentiating method
         * @param <D>
         *            type of difference
         * @return the difference
         */
        public <D> D diff(final BufferedImage other,
            final Function<Pair<BufferedImage, BufferedImage>, D> differentiator) {
            return diff(other, differentiator, ImageUtils::adapt);
        }
    }
}
