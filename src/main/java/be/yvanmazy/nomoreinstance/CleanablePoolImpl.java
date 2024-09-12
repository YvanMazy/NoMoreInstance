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

abstract sealed class CleanablePoolImpl<T> extends AbstractPool<T> implements CleanablePool<T> permits LongArrayCleanablePoolImpl, LongArrayCleanablePoolImpl.LockFree, LongCleanablePoolImpl, LongCleanablePoolImpl.LockFree {

    static <T> CleanablePoolImpl<T> build(final @NotNull T[] pool, final @NotNull PoolConcurrency concurrency) {
        final boolean small = pool.length <= 64;
        return switch (concurrency) {
            case NOT_CONCURRENT -> {
                if (small) {
                    yield new LongCleanablePoolImpl<>(pool);
                }
                yield new LongArrayCleanablePoolImpl<>(pool);
            }
            case SYNCHRONIZED -> {
                if (small) {
                    yield new LongCleanablePoolImpl.Synchronized<>(pool);
                }
                yield new LongArrayCleanablePoolImpl.Synchronized<>(pool);
            }
            case LOCK_FREE -> {
                if (small) {
                    yield new LongCleanablePoolImpl.LockFree<>(pool);
                }
                yield new LongArrayCleanablePoolImpl.LockFree<>(pool);
            }
        };
    }

    CleanablePoolImpl(final @NotNull T[] pool) {
        super(pool);
    }

    protected Cleanable<T> wrap(final int index, final T object) {
        return new CleanableImpl<>(this, index, object);
    }

    protected Cleanable<T> wrapIgnore(final T object) {
        return new IgnoredCleanable<>(object);
    }

}