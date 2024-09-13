/*
 * MIT License
 *
 * Copyright (c) 2024 Yvan Mazy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.yvanmazy.nomoreinstance;

import be.yvanmazy.nomoreinstance.object.Wrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Objects;

import static be.yvanmazy.nomoreinstance.util.ArrayTestUtil.filledVectors;
import static org.junit.jupiter.api.Assertions.*;

class PoolBuilderTest {

    @ParameterizedTest
    @EnumSource(PoolConcurrency.class)
    @NullSource
    void testBuildReturnCorrectConcurrencyImplementation(final PoolConcurrency concurrency) {
        final Vector[] testArray = filledVectors(1);

        final PoolBuilder<Vector> builder = new PoolBuilder<>();
        if (concurrency != null) {
            builder.concurrency(concurrency);
        }
        final PoolConcurrency expected = Objects.requireNonNullElse(concurrency, PoolConcurrency.DEFAULT);
        assertEquals(expected, builder.build(testArray).poolConcurrency());
        assertEquals(expected, builder.buildSweep(testArray).poolConcurrency());
    }

    @Test
    void testBuildThrowExceptionWhenArrayIsNull() {
        final Vector[] testArray = filledVectors(1);

        final PoolBuilder<Vector> builder = new PoolBuilder<>();
        assertDoesNotThrow(() -> builder.build(testArray));
        assertDoesNotThrow(() -> builder.buildSweep(testArray));

        final Wrapper<Vector[]> nullArray = new Wrapper<>(new Vector[] {null});
        final Wrapper<Vector[]> partiallyNullArray = new Wrapper<>(new Vector[] {new Vector(), null, new Vector()});
        assertThrows(RuntimeException.class, () -> builder.build(null));
        assertThrows(RuntimeException.class, () -> builder.buildSweep(null));
        assertThrows(RuntimeException.class, () -> builder.build(nullArray.getValue()));
        assertThrows(RuntimeException.class, () -> builder.build(partiallyNullArray.getValue()));
        assertThrows(RuntimeException.class, () -> builder.buildSweep(nullArray.getValue()));
        assertThrows(RuntimeException.class, () -> builder.buildSweep(partiallyNullArray.getValue()));

        builder.supplier(Vector::new);

        nullArray.setValue(new Vector[] {null});
        partiallyNullArray.setValue(new Vector[] {new Vector(), null, new Vector()});
        assertDoesNotThrow(() -> builder.build(nullArray.getValue()));
        assertDoesNotThrow(() -> builder.build(partiallyNullArray.getValue()));
        nullArray.setValue(new Vector[] {null});
        partiallyNullArray.setValue(new Vector[] {new Vector(), null, new Vector()});
        assertDoesNotThrow(() -> builder.buildSweep(nullArray.getValue()));
        assertDoesNotThrow(() -> builder.buildSweep(partiallyNullArray.getValue()));

        builder.withoutSupplier();

        nullArray.setValue(new Vector[] {null});
        assertThrows(RuntimeException.class, () -> builder.build(nullArray.getValue()));
    }

}