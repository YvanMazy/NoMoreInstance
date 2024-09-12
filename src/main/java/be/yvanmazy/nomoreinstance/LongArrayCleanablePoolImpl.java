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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongArray;

sealed class LongArrayCleanablePoolImpl<T> extends CleanablePoolImpl<T> permits LongArrayCleanablePoolImpl.Synchronized {

    private final long[] dirty;

    LongArrayCleanablePoolImpl(final @NotNull T[] pool) {
        super(pool);
        this.dirty = new long[(pool.length + 63) / 64];
    }

    @Override
    public @NotNull Cleanable<T> get() {
        for (int i = 1; i <= this.pool.length; i++) {
            if (!this.isDirty(i)) {
                this.setDirty(i, true);
                return this.wrap(i, this.getAt(i - 1));
            }
        }
        return this.wrapIgnore(this.getFromSupplier());
    }

    @Override
    public void clean(final int index) {
        this.setDirty(index, false);
    }

    @Override
    public void cleanAll() {
        Arrays.fill(this.dirty, 0L);
    }

    private boolean isDirty(final int index) {
        final int arrayIndex = (index - 1) / 64;
        final int bitIndex = (index - 1) % 64;
        return ((this.dirty[arrayIndex] >> bitIndex) & 1) == 1;
    }

    private void setDirty(final int index, final boolean state) {
        final int arrayIndex = (index - 1) / 64;
        final int bitIndex = (index - 1) % 64;
        if (state) {
            this.dirty[arrayIndex] |= (1L << bitIndex);
        } else {
            this.dirty[arrayIndex] &= ~(1L << bitIndex);
        }
    }

    @Override
    public @NotNull PoolConcurrency poolConcurrency() {
        return PoolConcurrency.NOT_CONCURRENT;
    }

    static final class Synchronized<T> extends LongArrayCleanablePoolImpl<T> {

        Synchronized(final @NotNull T[] pool) {
            super(pool);
        }

        @Override
        public synchronized @NotNull Cleanable<T> get() {
            return super.get();
        }

        @Override
        public synchronized void clean(final int index) {
            super.clean(index);
        }

        @Override
        public synchronized void cleanAll() {
            super.cleanAll();
        }

        @Override
        public @NotNull PoolConcurrency poolConcurrency() {
            return PoolConcurrency.SYNCHRONIZED;
        }

    }

    static final class LockFree<T> extends CleanablePoolImpl<T> {

        private final AtomicLongArray dirty;

        LockFree(final @NotNull T[] pool) {
            super(pool);
            this.dirty = new AtomicLongArray((pool.length + 63) / 64);
        }

        @Override
        public @NotNull Cleanable<T> get() {
            long prev, next;
            for (int i = 1; i <= this.pool.length; i++) {
                final int arrayIndex = (i - 1) / 64;
                final int bitIndex = (i - 1) % 64;
                prev = this.dirty.get(arrayIndex);
                if (((prev >> bitIndex) & 1) == 0) {
                    next = prev | (1L << bitIndex);
                    if (this.dirty.compareAndSet(arrayIndex, prev, next)) {
                        return this.wrap(i, this.getAt(i - 1));
                    }
                }
            }
            return this.wrapIgnore(this.getFromSupplier());
        }

        @Override
        public void clean(final int index) {
            final int arrayIndex = (index - 1) / 64;
            final int bitIndex = (index - 1) % 64;
            long current, next;
            do {
                current = this.dirty.get(arrayIndex);
                next = current & ~(1L << bitIndex);
            } while (!this.dirty.compareAndSet(arrayIndex, current, next));
        }

        @Override
        public void cleanAll() {
            final int length = this.dirty.length();
            for (int i = 0; i < length; i++) {
                this.dirty.set(i, 0L);
            }
        }

        @Override
        public @NotNull PoolConcurrency poolConcurrency() {
            return PoolConcurrency.LOCK_FREE;
        }

    }

}