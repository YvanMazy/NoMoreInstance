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

import be.yvanmazy.nomoreinstance.util.CleanablePoolWrapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static be.yvanmazy.nomoreinstance.util.ArrayTestUtil.filledVectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanablePoolTest extends AbstractPoolTest<CleanablePool<?>> {

    @ParameterizedTest
    @EnumSource(PoolConcurrency.class)
    void testCleanCorrectlyResetIndex(final PoolConcurrency concurrency) {
        final CleanablePool<Vector> pool =
                this.buildCleanable(Pool.<Vector>newBuilder().concurrency(concurrency).supplier(() -> new Vector(1, 1, 1)),
                        filledVectors(2));

        for (int i = 0; i < 10; i++) {
            try (final Cleanable<Vector> cleanable = pool.get()) {
                assertEquals(ZERO_VECTOR, cleanable.value());
            }
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(ZERO_VECTOR, pool.get().value());
            assertEquals(ZERO_VECTOR, pool.get().value());
            assertEquals(ONE_VECTOR, pool.get().value());
            assertEquals(ONE_VECTOR, pool.get().value());
            pool.cleanAll();
        }
    }

    @Override
    protected <T> Pool<T> build(final Pool.Builder<T> builder, final T[] poolArray) {
        return new CleanablePoolWrapper<>(this.buildCleanable(builder, poolArray));
    }

    protected <T> CleanablePool<T> buildCleanable(final Pool.Builder<T> builder, final T[] poolArray) {
        return builder.build(poolArray);
    }

    @Override
    protected CleanablePool<Object> buildConcurrencyPool(final Pool.Builder<Object> builder, final Object[] poolArray) {
        return builder.build(poolArray);
    }

    @Override
    protected void cleanAll(final CleanablePool<?> pool) {
        pool.cleanAll();
    }

}