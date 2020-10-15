/*
 * Copyright (C) 2020 Muntashir Al-Islam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.muntashirakon.AppManager.utils;

import androidx.annotation.NonNull;

/**
 * Hexadecimal encoding where each byte is represented by two hexadecimal digits.
 */
// Source: libcore/luni/src/main/java/libcore/util/HexEncoding.java
public class HexEncoding {
    private static final char[] LOWER_CASE_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private static final char[] UPPER_CASE_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Hidden constructor to prevent instantiation.
     */
    private HexEncoding() {
    }

    /**
     * Encodes the provided byte as a two-digit hexadecimal String value.
     */
    @NonNull
    public static String encodeToString(byte b, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : LOWER_CASE_DIGITS;
        char[] buf = new char[2]; // We always want two digits.
        buf[0] = digits[(b >> 4) & 0xf];
        buf[1] = digits[b & 0xf];
        return new String(buf, 0, 2);
    }

    /**
     * Encodes the provided data as a sequence of hexadecimal characters.
     */
    @NonNull
    public static char[] encode(byte[] data) {
        return encode(data, 0, data.length, true /* upperCase */);
    }

    /**
     * Encodes the provided data as a sequence of hexadecimal characters.
     */
    @NonNull
    public static char[] encode(byte[] data, boolean upperCase) {
        return encode(data, 0, data.length, upperCase);
    }

    /**
     * Encodes the provided data as a sequence of hexadecimal characters.
     */
    @NonNull
    public static char[] encode(byte[] data, int offset, int len) {
        return encode(data, offset, len, true /* upperCase */);
    }

    /**
     * Encodes the provided data as a sequence of hexadecimal characters.
     */
    @NonNull
    private static char[] encode(byte[] data, int offset, int len, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : LOWER_CASE_DIGITS;
        char[] result = new char[len * 2];
        for (int i = 0; i < len; i++) {
            byte b = data[offset + i];
            int resultIndex = 2 * i;
            result[resultIndex] = (digits[(b >> 4) & 0x0f]);
            result[resultIndex + 1] = (digits[b & 0x0f]);
        }

        return result;
    }

    /**
     * Encodes the provided data as a sequence of hexadecimal characters.
     */
    @NonNull
    public static String encodeToString(byte[] data) {
        return encodeToString(data, true /* upperCase */);
    }

    /**
     * Encodes the provided data as a sequence of hexadecimal characters.
     */
    @NonNull
    public static String encodeToString(byte[] data, boolean upperCase) {
        return new String(encode(data, upperCase));
    }

    /**
     * Decodes the provided hexadecimal string into a byte array.  Odd-length inputs
     * are not allowed.
     * <p>
     * Throws an {@code IllegalArgumentException} if the input is malformed.
     */
    @NonNull
    public static byte[] decode(@NonNull String encoded) throws IllegalArgumentException {
        return decode(encoded.toCharArray());
    }

    /**
     * Decodes the provided hexadecimal string into a byte array. If {@code allowSingleChar}
     * is {@code true} odd-length inputs are allowed and the first character is interpreted
     * as the lower bits of the first result byte.
     * <p>
     * Throws an {@code IllegalArgumentException} if the input is malformed.
     */
    @NonNull
    public static byte[] decode(@NonNull String encoded, boolean allowSingleChar)
            throws IllegalArgumentException {
        return decode(encoded.toCharArray(), allowSingleChar);
    }

    /**
     * Decodes the provided hexadecimal string into a byte array.  Odd-length inputs
     * are not allowed.
     * <p>
     * Throws an {@code IllegalArgumentException} if the input is malformed.
     */
    @NonNull
    public static byte[] decode(char[] encoded) throws IllegalArgumentException {
        return decode(encoded, false);
    }

    /**
     * Decodes the provided hexadecimal string into a byte array. If {@code allowSingleChar}
     * is {@code true} odd-length inputs are allowed and the first character is interpreted
     * as the lower bits of the first result byte.
     * <p>
     * Throws an {@code IllegalArgumentException} if the input is malformed.
     */
    @NonNull
    public static byte[] decode(@NonNull char[] encoded, boolean allowSingleChar)
            throws IllegalArgumentException {
        int encodedLength = encoded.length;
        int resultLengthBytes = (encodedLength + 1) / 2;
        byte[] result = new byte[resultLengthBytes];

        int resultOffset = 0;
        int i = 0;
        if (allowSingleChar) {
            if ((encodedLength % 2) != 0) {
                // Odd number of digits -- the first digit is the lower 4 bits of the first result
                // byte.
                result[resultOffset++] = (byte) toDigit(encoded, i);
                i++;
            }
        } else {
            if ((encodedLength % 2) != 0) {
                throw new IllegalArgumentException("Invalid input length: " + encodedLength);
            }
        }

        for (; i < encodedLength; i += 2) {
            result[resultOffset++] = (byte) ((toDigit(encoded, i) << 4) | toDigit(encoded, i + 1));
        }

        return result;
    }

    private static int toDigit(@NonNull char[] str, int offset) throws IllegalArgumentException {
        // NOTE: that this isn't really a code point in the traditional sense, since we're
        // just rejecting surrogate pairs outright.
        int pseudoCodePoint = str[offset];

        if ('0' <= pseudoCodePoint && pseudoCodePoint <= '9') {
            return pseudoCodePoint - '0';
        } else if ('a' <= pseudoCodePoint && pseudoCodePoint <= 'f') {
            return 10 + (pseudoCodePoint - 'a');
        } else if ('A' <= pseudoCodePoint && pseudoCodePoint <= 'F') {
            return 10 + (pseudoCodePoint - 'A');
        }

        throw new IllegalArgumentException("Illegal char: " + str[offset] + " at offset " + offset);
    }
}