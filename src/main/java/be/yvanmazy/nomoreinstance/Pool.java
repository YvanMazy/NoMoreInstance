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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface Pool<T> {

    @Contract(value = "-> new", pure = true)
    static <T> Builder<T> newBuilder() {
        return new PoolBuilder<>();
    }

    @NotNull T get();

    @Contract(pure = true)
    @Range(from = 0L, to = Integer.MAX_VALUE)
    int size();

    @Contract(pure = true)
    @NotNull PoolConcurrency poolConcurrency();

    interface Builder<T> {

        @Contract("_ -> this")
        @NotNull Builder<T> supplier(final @NotNull Supplier<T> supplier);

        @Contract("-> this")
        @NotNull Builder<T> withoutSupplier();

        @Contract("_ -> this")
        @NotNull Builder<T> concurrency(final @NotNull PoolConcurrency concurrency);

        @Contract("_ -> this")
        @NotNull Builder<T> cleaner(final @NotNull UnaryOperator<T> cleaner);

        @Contract("-> this")
        @NotNull Builder<T> withoutCleaner();

        @Contract("_, _ -> new")
        @NotNull CleanablePool<T> build(final @NotNull Class<T> objectClass, final int size);

        @Contract("_, _ -> new")
        @NotNull SweepCleanablePool<T> buildSweep(final @NotNull Class<T> objectClass, final int size);

        @Contract("_ -> new")
        @NotNull CleanablePool<T> build(final @NotNull T[] poolArray);

        @Contract("_ -> new")
        @NotNull SweepCleanablePool<T> buildSweep(final @NotNull T[] poolArray);

    }

}