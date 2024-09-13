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

package be.yvanmazy.nomoreinstance.util;

import be.yvanmazy.nomoreinstance.CleanablePool;
import be.yvanmazy.nomoreinstance.Pool;
import be.yvanmazy.nomoreinstance.PoolConcurrency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record CleanablePoolWrapper<T>(CleanablePool<T> delegate) implements Pool<T> {

    @Override
    public @NotNull T get() {
        return this.delegate.get().value();
    }

    @Override
    public @Range(from = 0L, to = Integer.MAX_VALUE) int size() {
        return this.delegate.size();
    }

    @Override
    public @NotNull PoolConcurrency poolConcurrency() {
        return this.delegate.poolConcurrency();
    }

}