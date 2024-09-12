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
import be.yvanmazy.nomoreinstance.exception.NullObjectProvidedException;
import be.yvanmazy.nomoreinstance.util.ArrayPreconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

abstract class AbstractPool<T> {

    protected final T[] pool;
    protected Supplier<T> supplier;
    protected UnaryOperator<T> cleaner;

    protected AbstractPool(final @NotNull T[] pool) {
        this.pool = ArrayPreconditions.requireNonNull(pool, "pool must not be null");
    }

    protected @NotNull T getAt(final int index) {
        T object = this.pool[index];
        final UnaryOperator<T> cleaner = this.cleaner;
        if (cleaner != null) {
            object = cleaner.apply(object);
            if (object == null) {
                throw new NullObjectProvidedException("Pool cleaner produce a null object!");
            }
            return this.pool[index] = object;
        }
        return object;
    }

    protected @NotNull T getFromSupplier() {
        final Supplier<T> supplier = this.supplier;
        if (supplier == null) {
            throw new NoMoreObjectException();
        }
        final T object = supplier.get();
        if (object == null) {
            throw new NullObjectProvidedException("Pool supplier produce a null object!");
        }
        return object;
    }

    public @Range(from = 0L, to = Integer.MAX_VALUE) int size() {
        return this.pool.length;
    }

    public void setSupplier(final @Nullable Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public void setCleaner(final @Nullable UnaryOperator<T> cleaner) {
        this.cleaner = cleaner;
    }

}