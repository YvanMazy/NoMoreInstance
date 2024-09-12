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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BitUtil {

    private BitUtil() throws IllegalAccessException {
        throw new IllegalAccessException("You cannot instantiate a utility class");
    }

    @Contract(pure = true)
    public static boolean getFlag(final byte value, final int index) {
        return ((value >> (index - 1)) & 1) == 1;
    }

    @Contract(pure = true)
    public static byte setFlag(final byte value, final int index, final boolean state) {
        if (state) {
            return (byte) (value | (byte) (1 << (index - 1)));
        } else {
            return (byte) (value & (byte) ~(1 << (index - 1)));
        }
    }

    @Contract(pure = true)
    public static boolean getFlag(final short value, final int index) {
        return ((value >> (index - 1)) & 1) == 1;
    }

    @Contract(pure = true)
    public static short setFlag(final short value, final int index, final boolean state) {
        if (state) {
            return (short) (value | (1 << (index - 1)));
        } else {
            return (short) (value & ~(1 << (index - 1)));
        }
    }

    @Contract(pure = true)
    public static boolean getFlag(final int value, final int index) {
        return ((value >> (index - 1)) & 1) == 1;
    }

    @Contract(pure = true)
    public static int setFlag(final int value, final int index, final boolean state) {
        if (state) {
            return value | (1 << (index - 1));
        } else {
            return value & ~(1 << (index - 1));
        }
    }

    @Contract(pure = true)
    public static boolean getFlag(final long value, final int index) {
        return ((value >> (index - 1)) & 1) == 1;
    }

    @Contract(pure = true)
    public static long setFlag(final long value, final int index, final boolean state) {
        if (state) {
            return value | (1L << (index - 1));
        } else {
            return value & ~(1L << (index - 1));
        }
    }

    @Contract(pure = true)
    public static @NotNull String getBinaryString(final byte number) {
        return getBinaryString(Integer.toBinaryString(number), Byte.SIZE);
    }

    @Contract(pure = true)
    public static @NotNull String getBinaryString(final short number) {
        return getBinaryString(Integer.toBinaryString(number), Short.SIZE);
    }

    @Contract(pure = true)
    public static @NotNull String getBinaryString(final int number) {
        return getBinaryString(Integer.toBinaryString(number), Integer.SIZE);
    }

    @Contract(pure = true)
    public static @NotNull String getBinaryString(final long number) {
        return getBinaryString(Long.toBinaryString(number), Long.SIZE);
    }

    @Contract(pure = true)
    public static @NotNull String getBinaryString(final double number) {
        return getBinaryString(Long.toBinaryString(Double.doubleToLongBits(number)), Long.SIZE);
    }

    @Contract(pure = true)
    public static @NotNull String getBinaryString(final float number) {
        return getBinaryString(Integer.toBinaryString(Float.floatToIntBits(number)), Integer.SIZE);
    }

    private static String getBinaryString(final String string, final int size) {
        return "0".repeat(size - string.length()) + string;
    }

}