package com.bestmafen.smablelib.util;

import android.text.TextUtils;

import com.bestmafen.easeblelib.util.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SmaBleUtils {
    public static TimeZone getDefaultTimeZone() {
        return TimeZone.getTimeZone("GMT+0");
    }

    public static byte getLanguageCode() {
        byte code = 0x01;
        String language = Locale.getDefault().getLanguage();
        if (language.equalsIgnoreCase("zh")) {
            //中文
            code = 0x00;
        } else if (language.equalsIgnoreCase("tr")) {
            //土耳其语
            code = 0x02;
        } else if (language.equalsIgnoreCase("ru")) {
            //俄罗斯语
            code = 0x04;
        } else if (language.equalsIgnoreCase("es")) {
            //西班牙语
            code = 0x05;
        } else if (language.equalsIgnoreCase("it")) {
            //意大利语
            code = 0x06;
        } else if (language.equalsIgnoreCase("ko")) {
            //韩语
            code = 0x07;
        } else if (language.equalsIgnoreCase("pt")) {
            //葡萄牙语
            code = 0x08;
        } else if (language.equalsIgnoreCase("de")) {
            //德语
            code = 0x09;
        } else if (language.equalsIgnoreCase("fr")) {
            //法语
            code = 0x0A;
        } else if (language.equalsIgnoreCase("nl")) {
            //荷兰语
            code = 0xB;
        } else if (language.equalsIgnoreCase("pl")) {
            //波兰语
            code = 0x0C;
        } else if (language.equalsIgnoreCase("cs")) {
            //捷克语
            code = 0x0D;
        } else if (language.equalsIgnoreCase("hu")) {
            //匈牙利语
            code = 0x0E;
        } else if (language.equalsIgnoreCase("sk")) {
            //斯洛伐克语
            code = 0x0F;
        } else if (language.equalsIgnoreCase("ja")) {
            //日语
            code = 0x10;
        }else if (language.equalsIgnoreCase("da")) {
            //丹麦
            code = 0x11;
        }else if (language.equalsIgnoreCase("fi")) {
            //芬兰
            code = 0x12;
        }else if (language.equalsIgnoreCase("no")) {
            //挪威
            code = 0x13;
        }else if (language.equalsIgnoreCase("sv")) {
            //瑞典
            code = 0x14;
        }
        L.d("language: " + language + "," + code);
        return code;
    }

    public static String addressPlus1(String address) {
        if (TextUtils.isEmpty(address)) {
            return "";
        }

        String s = address.replace(":", "");
        long value = Long.parseLong(s, 16) + 1;
        s = Long.toHexString(value);

        int l = s.length() / 2;
        StringBuilder sb = new StringBuilder(s);
        for (int i = 1; i < l; i++) {
            sb.insert(i * 2 + (i - 1), ":");
        }

        return sb.toString().toUpperCase();
    }

    /**
     * Get distance by height and steps
     *
     * @param height height(cm)
     * @param steps  steps
     * @return distance(km)
     */
    public static float getDistance(float height, int steps) {
        return height * steps * 45 / 10000 / 1000;
    }

    /**
     * Get calorie by weight ,steps and gender
     *
     * @param weight weight(kg)
     * @param steps  steps
     * @param gender 0 female;1 male
     * @return calorie(Kcal)
     */
    public static int getCalorie(float weight, int steps, int gender) {
        return gender == 0 ? (int) Math.floor(0.46f * weight * steps / 1000) : (int) Math.floor(0.55f * weight * steps /
                1000);
    }

    public static List<byte[]> getBuffer4XMode(InputStream inputStream) {
        try {
//            InputStream inputStream = mContext.getResources().openRawResource(R.raw.watch_000010);
            int total = inputStream.available();
            if (total < 1) return Collections.emptyList();

            byte[] data = new byte[total];
            if (inputStream.read(data) > 0) {
                inputStream.close();

                int len = total % 128 == 0 ? total / 128 : total / 128 + 1;
                List<byte[]> buffers = new ArrayList<>(len);
                byte[] buffer;
                for (int i = 0; i < len; i++) {
                    short[] temp_cmd = new short[128];
                    buffer = new byte[133];
                    buffer[0] = 0x01;
                    byte serialNum = (byte) (i + 1);
                    buffer[1] = serialNum;
                    buffer[2] = (byte) (0xff - serialNum);
                    for (int j = 0; j < 128; j++) {
                        if ((128 * i + j) < total) {
                            temp_cmd[j] = data[128 * i + j];
                            buffer[j + 3] = data[128 * i + j];
                        }
                    }
                    short crc_value = crc16_ccitt(temp_cmd, 128);
                    OtaHelper.putShort(buffer, crc_value, 131);
                    buffers.add(buffer);
                }
                return buffers;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    /**
     * get buffer for transferring by x-mode
     *
     * @param file the file to transfer
     * @return buffer
     */
    public static List<byte[]> getBuffer4XMode(File file) {
        try {
            return getBuffer4XMode(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private static short crc16_ccitt(short[] buf, int len) {
        short crc = 0;
        for (int counter = 0; counter < len; counter++) {
            crc = (short) ((crc << 8) ^ OtaHelper.crc16_table[(((crc >> 8) ^ buf[counter]) & 0x00FF)]);
        }
        return crc;
    }
}
