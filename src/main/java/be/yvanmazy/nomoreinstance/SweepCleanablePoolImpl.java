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

import java.util.concurrent.atomic.AtomicInteger;

sealed class SweepCleanablePoolImpl<T> extends AbstractPool<T> implements SweepCleanablePool<T> permits SweepCleanablePoolImpl.Synchronized {

    private int index;

    SweepCleanablePoolImpl(final @NotNull T[] pool) {
        super(pool);
    }

    @Override
    public @NotNull T get() {
        if (this.index >= this.pool.length) {
            return this.getFromSupplier();
        }
        return this.getAt(this.index++);
    }

    @Override
    public void cleanAll() {
        this.index = 0;
    }

    @Override
    public @NotNull PoolConcurrency poolConcurrency() {
        return PoolConcurrency.NOT_CONCURRENT;
    }

    static final class Synchronized<T> extends SweepCleanablePoolImpl<T> {

        Synchronized(final @NotNull T[] pool) {
            super(pool);
        }

        @Override
        public synchronized @NotNull T get() {
            return super.get();
        }

        @Override
        public @NotNull PoolConcurrency poolConcurrency() {
            return PoolConcurrency.SYNCHRONIZED;
        }

    }

    static final class LockFree<T> extends AbstractPool<T> implements SweepCleanablePool<T> {

        private final AtomicInteger index = new AtomicInteger();

        LockFree(final @NotNull T[] pool) {
            super(pool);
        }

        @Override
        public @NotNull T get() {
            int index = this.index.get();
            if (index >= this.pool.length || (index = this.index.getAndIncrement()) >= this.pool.length) {
                return this.getFromSupplier();
            }
            return this.getAt(index);
        }

        @Override
        public void cleanAll() {
            this.index.set(0);
        }

        @Override
        public @NotNull PoolConcurrency poolConcurrency() {
            return PoolConcurrency.LOCK_FREE;
        }

    }

}