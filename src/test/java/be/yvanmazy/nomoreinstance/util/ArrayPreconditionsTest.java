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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArrayPreconditionsTest {

    private static final String MESSAGE = "message";

    @Test
    void testNonNullArray() {
        assertDoesNotThrow(() -> ArrayPreconditions.requireNonNull(new Object[] {"value"}, MESSAGE));
        assertDoesNotThrow(() -> ArrayPreconditions.requireNonNull(new Integer[] {0}, MESSAGE));
        assertDoesNotThrow(() -> ArrayPreconditions.requireNonNull(new String[0], MESSAGE));
        assertDoesNotThrow(() -> ArrayPreconditions.requireNonNull(new Object[] {"value", 0, 'c'}, MESSAGE));
    }

    @Test
    void testNullArray() {
        assertThrows(NullPointerException.class, () -> ArrayPreconditions.requireNonNull(null, MESSAGE));
        assertThrows(NullPointerException.class, () -> ArrayPreconditions.requireNonNull(new String[1], MESSAGE));
        assertThrows(NullPointerException.class, () -> ArrayPreconditions.requireNonNull(new String[12], MESSAGE));
        assertThrows(NullPointerException.class, () -> ArrayPreconditions.requireNonNull(new String[] {"value", null, "another"}, MESSAGE));
        assertThrows(NullPointerException.class, () -> ArrayPreconditions.requireNonNull(new String[] {null}, MESSAGE));
        assertThrows(NullPointerException.class, () -> ArrayPreconditions.requireNonNull(new String[] {null, "value"}, MESSAGE));
    }

}