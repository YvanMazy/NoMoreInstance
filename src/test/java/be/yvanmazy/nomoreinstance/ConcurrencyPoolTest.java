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

import be.yvanmazy.concurrenttesting.ConcurrentTester;
import be.yvanmazy.nomoreinstance.object.Wrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class ConcurrencyPoolTest<P extends Pool<?>> {

    private static final int THREADS = 100;

    private Collection<Object> history = new ConcurrentLinkedQueue<>();

    @BeforeEach
    void setUp() {
        this.history.clear();
    }

    @ParameterizedTest
    @MethodSource("getConcurrencyArguments")
    void testHighConcurrency(final int poolSize, final PoolConcurrency concurrency) {
        final AtomicInteger counter = new AtomicInteger();
        final P pool = this.buildConcurrencyPool(Pool.newBuilder()
                .supplier(() -> new Wrapper<>(String.valueOf(counter.incrementAndGet())))
                .concurrency(concurrency), new Wrapper[poolSize]);

        ConcurrentTester.run(barrier -> {
            final String threadName = Thread.currentThread().getName();
            for (int j = 0; j < poolSize; j++) {
                final Object val = pool.get();
                assertTrue(this.history.add(val), "Duplicated value in " + threadName + " at " + j);
            }
            barrier.await();
            barrier.await();
            for (int j = 0; j < poolSize; j++) {
                final Object val = pool.get();
                assertTrue(this.history.add(val), "Duplicated value in " + threadName + " at " + j + " (second round)");
            }
            barrier.await();
        }, c -> c.afterStart(barrier -> {
            barrier.await();
            final int uniqueSize = new HashSet<>(this.history).size();
            final int totalSize = this.history.size();
            assertEquals(uniqueSize, totalSize, "Duplicated entries found");
            this.cleanAll(pool);
            this.history = new ConcurrentLinkedQueue<>();
            barrier.await();
            barrier.await();
            assertEquals(uniqueSize, totalSize, "Duplicated entries found (second round)");
        }).threads(THREADS));
    }

    protected abstract P buildConcurrencyPool(final Pool.Builder<Object> builder, final Object[] poolArray);

    protected abstract void cleanAll(final P pool);

    private static Stream<Arguments> getConcurrencyArguments() {
        return Arrays.stream(PoolConcurrency.values())
                .filter(PoolConcurrency::isThreadSafe)
                .flatMap(c -> Stream.of(Arguments.of(Long.SIZE, c), Arguments.of(200, c)));
    }

}