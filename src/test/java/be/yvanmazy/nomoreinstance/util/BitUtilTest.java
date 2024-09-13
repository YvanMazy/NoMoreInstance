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

import static org.junit.jupiter.api.Assertions.*;

class BitUtilTest {

    @Test
    void testGetBinaryStringWithManyValues() {
        assertEquals("00000000", BitUtil.getBinaryString((byte) 0));
        assertEquals("00000001", BitUtil.getBinaryString((byte) 1));
        assertEquals("0000000011111111", BitUtil.getBinaryString((short) 255));
        assertEquals("00000000000000000000000011111111", BitUtil.getBinaryString(255));
        assertEquals("01111111111111111111111111111111", BitUtil.getBinaryString(Integer.MAX_VALUE));
        assertEquals("0011111111010011001100110011001100110011001100110011001100110011", BitUtil.getBinaryString(0.3d));
        assertEquals("0100000000111001000000000000000000000000000000000000000000000000", BitUtil.getBinaryString(25d));
    }

    @Test
    void testGetAndSetFlags() {
        for (int i = 0; i < Integer.SIZE; i++) {
            assertFalse(BitUtil.getFlag(0, i));
            assertEquals(i == 2, BitUtil.getFlag(2, i));
        }
        final long value = 0L;
        final long edited = BitUtil.setFlag(value, 32, true);
        assertNotEquals(edited, value);
        assertEquals(BitUtil.setFlag(value, 32, false), value);
    }

}