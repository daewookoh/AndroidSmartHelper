package com.bestmafen.easeblelib.util;

import java.util.Arrays;
import java.util.Locale;

/**
 * This class is used to be a helper.
 */
public class EaseUtils {
    /**
     * Convert a integer to a byte array.
     *
     * @param n an integer to be converted to a byte array
     * @return a byte array representation of the argument.
     */
    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];

        for (int i = 0; i < 4; i++) {
            b[i] = (byte) ((n >> (24 - i * 8)) & 0xff);
        }
        return b;
    }

    public static int bytesToInt(byte[] bytes) {
        int goal = 0;
        for (int i = 0; i < 4; i++) {
            goal += (bytes[i] & 0xff) << ((3 - i) * 8);
        }
        return goal;
    }

    /**
     * Concatenate two byte arrays.
     *
     * @param first  a byte array
     * @param second another byte array
     * @return a new byte array contains whole elements in two arguments.
     */
    public static byte[] concat(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Convert a byte array to a String array based on hex format,for example:[32,32] -> ["0x20","0x20"].
     *
     * @param data a byte array to convert to a String array
     * @return a String array representation of the argument.
     */
    private static String[] byteArray2HexStringArray(byte[] data) {
        if (data == null || data.length == 0) {
            return new String[0];
        }
        String[] arr = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            int j = data[i] & 0xff;
            String s1 = Integer.toHexString(j).toUpperCase();
            if (s1.length() == 1) {
                s1 = "0x0" + s1;
            } else {
                s1 = "0x" + s1;
            }
            arr[i] = s1;
        }
        return arr;
    }

    public static String byteArray2HexString(byte[] data) {
        return Arrays.toString(byteArray2HexStringArray(data));
    }

    /**
     * Convert a byte array to a String based on hex format without 0x,for example:[32,32] -> "2020".
     *
     * @param data a byte array to convert to a String
     * @return a hex String representation without 0x of the argument.
     */
    public static String bytesArray2HexStringWithout0x(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (data == null || data.length <= 0) {
            return null;
        }
        for (byte aSrc : data) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase(Locale.getDefault());
    }

    public static void writeFloat(byte[] bytes, int offset, float value, boolean bigEndian) {
        if (bytes == null) return;

        if (offset + 4 > bytes.length) return;

        int intBits = Float.floatToIntBits(value);
        for (int i = 0; i < 4; i++) {
            if (bigEndian) {
                bytes[i + offset] = (byte) ((intBits >> (24 - i * 8)) & 0xff);
            } else {
                bytes[offset + 3 - i] = (byte) ((intBits >> (24 - i * 8)) & 0xff);
            }
        }
    }

    public static void reverseArray(byte[] t) {
        if (t == null || t.length < 1) return;

        int index;
        for (int i = 0, l = t.length / 2; i < l; i++) {
            index = t.length - 1 - i;
            byte tem = t[i];
            t[i] = t[index];
            t[index] = tem;
        }
    }
}
