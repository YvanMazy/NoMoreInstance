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

import java.util.concurrent.atomic.AtomicLong;

import static be.yvanmazy.nomoreinstance.util.BitUtil.getFlag;
import static be.yvanmazy.nomoreinstance.util.BitUtil.setFlag;

sealed class LongCleanablePoolImpl<T> extends CleanablePoolImpl<T> permits LongCleanablePoolImpl.Synchronized {

    private long dirty;

    LongCleanablePoolImpl(final @NotNull T[] pool) {
        super(pool);
        if (pool.length > 64) {
            throw new IllegalArgumentException("Pool is too big for this implementation!");
        }
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
        this.dirty = 0L;
    }

    private boolean isDirty(final int index) {
        return getFlag(this.dirty, index);
    }

    private void setDirty(final int index, final boolean state) {
        this.dirty = setFlag(this.dirty, index, state);
    }

    @Override
    public @NotNull PoolConcurrency poolConcurrency() {
        return PoolConcurrency.NOT_CONCURRENT;
    }

    static final class Synchronized<T> extends LongCleanablePoolImpl<T> {

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

        private final AtomicLong dirty = new AtomicLong();

        LockFree(final @NotNull T[] pool) {
            super(pool);
            if (pool.length > 64) {
                throw new IllegalArgumentException("Pool is too big for this implementation!");
            }
        }

        @Override
        public @NotNull Cleanable<T> get() {
            for (int i = 1; i <= this.pool.length; i++) {
                final long current = this.dirty.get();
                if (!getFlag(current, i) && this.dirty.compareAndSet(current, setFlag(current, i, true))) {
                    return this.wrap(i, this.getAt(i - 1));
                }
            }
            return this.wrapIgnore(this.getFromSupplier());
        }

        @Override
        public void clean(final int index) {
            long current, next;
            do {
                current = this.dirty.get();
                if (!getFlag(current, index)) {
                    return;
                }
                next = setFlag(current, index, false);
            } while (!this.dirty.compareAndSet(current, next));
        }

        @Override
        public void cleanAll() {
            this.dirty.set(0L);
        }

        @Override
        public @NotNull PoolConcurrency poolConcurrency() {
            return PoolConcurrency.LOCK_FREE;
        }

    }

}