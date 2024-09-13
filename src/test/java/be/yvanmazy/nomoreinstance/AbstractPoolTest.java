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

import be.yvanmazy.nomoreinstance.exception.NoMoreObjectException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static be.yvanmazy.nomoreinstance.util.ArrayTestUtil.filledVectors;
import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractPoolTest<P extends Pool<?>> extends ConcurrencyPoolTest<P> {

    protected static final Vector ZERO_VECTOR = new Vector(0, 0, 0);
    protected static final Vector ONE_VECTOR = new Vector(1, 1, 1);

    @ParameterizedTest
    @EnumSource(PoolConcurrency.class)
    void testGetThrowExceptionWithoutSupplier(final PoolConcurrency concurrency) {
        final Pool<Vector> pool = this.build(Pool.<Vector>newBuilder().concurrency(concurrency), filledVectors(2));

        assertDoesNotThrow(pool::get);
        assertDoesNotThrow(pool::get);
        assertThrowsExactly(NoMoreObjectException.class, pool::get);
    }

    @ParameterizedTest
    @EnumSource(PoolConcurrency.class)
    void testGetDoesNotThrowExceptionWithSupplier(final PoolConcurrency concurrency) {
        final Pool<Vector> pool = this.build(Pool.<Vector>newBuilder().concurrency(concurrency).supplier(Vector::new), filledVectors(2));

        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(pool::get);
        }
    }

    @ParameterizedTest
    @EnumSource(PoolConcurrency.class)
    void testGetGiveObjectInCorrectOrder(final PoolConcurrency concurrency) {
        final Pool<Vector> pool =
                this.build(Pool.<Vector>newBuilder().concurrency(concurrency).supplier(() -> new Vector(1, 1, 1)), filledVectors(2));

        assertEquals(ZERO_VECTOR, pool.get());
        assertEquals(ZERO_VECTOR, pool.get());
        assertEquals(ONE_VECTOR, pool.get());
        assertEquals(ONE_VECTOR, pool.get());
        assertEquals(ONE_VECTOR, pool.get());
    }

    protected abstract <T> Pool<T> build(final Pool.Builder<T> builder, final T[] poolArray);

}