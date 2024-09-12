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

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

final class PoolBuilder<T> implements Pool.Builder<T> {

    private Supplier<T> supplier;
    private PoolConcurrency concurrency = PoolConcurrency.NOT_CONCURRENT;
    private UnaryOperator<T> cleaner;

    @Override
    public Pool.@NotNull Builder<T> supplier(final @NotNull Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier must not be null. Use #withoutSupplier instead.");
        return this;
    }

    @Override
    public Pool.@NotNull Builder<T> withoutSupplier() {
        this.supplier = null;
        return this;
    }

    @Override
    public Pool.@NotNull Builder<T> concurrency(final @NotNull PoolConcurrency concurrency) {
        this.concurrency = Objects.requireNonNull(concurrency, "concurrency must not be null");
        return this;
    }

    @Override
    public Pool.@NotNull Builder<T> cleaner(final @NotNull UnaryOperator<T> cleaner) {
        this.cleaner = Objects.requireNonNull(cleaner, "cleaner must not be null");
        return this;
    }

    @Override
    public Pool.@NotNull Builder<T> withoutCleaner() {
        this.cleaner = null;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull CleanablePool<T> build(final @NotNull Class<T> objectClass, final int size) {
        return this.build((T[]) Array.newInstance(objectClass, size));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull SweepCleanablePool<T> buildSweep(final @NotNull Class<T> objectClass, final int size) {
        return this.buildSweep((T[]) Array.newInstance(objectClass, size));
    }

    @Override
    public @NotNull CleanablePool<T> build(final @NotNull T[] poolArray) {
        this.prepareArray(poolArray);
        final CleanablePoolImpl<T> pool = CleanablePoolImpl.build(poolArray, this.concurrency);
        pool.setSupplier(this.supplier);
        pool.setCleaner(this.cleaner);
        return pool;
    }

    @Override
    public @NotNull SweepCleanablePool<T> buildSweep(final @NotNull T[] poolArray) {
        this.prepareArray(poolArray);
        final var pool = switch (this.concurrency) {
            case NOT_CONCURRENT -> new SweepCleanablePoolImpl<>(poolArray);
            case SYNCHRONIZED -> new SweepCleanablePoolImpl.Synchronized<>(poolArray);
            case LOCK_FREE -> new SweepCleanablePoolImpl.LockFree<>(poolArray);
        };
        pool.setSupplier(this.supplier);
        pool.setCleaner(this.cleaner);
        return pool;
    }

    private void prepareArray(final T[] array) {
        final Supplier<T> supplier = this.supplier;
        if (supplier != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    array[i] = supplier.get();
                }
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                final T t = array[i];
                if (t == null) {
                    throw new NullPointerException(
                            "Pool array contain null value at index '" + i + "'. Provide a supplier or fill the array.");
                }
            }
        }
    }

}