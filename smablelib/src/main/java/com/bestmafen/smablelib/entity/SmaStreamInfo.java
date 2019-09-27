package com.bestmafen.smablelib.entity;

import com.bestmafen.easeblelib.util.EaseUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by xiaokai on 2018/7/24.
 */
public class SmaStreamInfo implements ISmaCmd {
    public int      size;
    public int      flag;
    public String[] extras;//因为传输的时候会将所有元素用"\t"拼接，所以元素本身中的"\t"在传输前会被删除

    @Override
    public byte[] toByteArray() {
        try {
            byte[] bytes = new byte[10];
            byte[] sizeBytes = EaseUtils.intToBytes(size);
            System.arraycopy(sizeBytes, 0, bytes, 2, 4);
            bytes[6] = (byte) ((flag >> 8) & 0xff);
            bytes[7] = (byte) (flag & 0xff);
            int packageCount = size % 2035 == 0 ? size / 2035 : size / 2035 + 1;
            bytes[8] = (byte) ((packageCount >> 8) & 0xff);
            bytes[9] = (byte) (packageCount & 0xff);

            if (extras != null) {
                for (String extra : extras) {
                    if (extra == null) {
                        extra = "";
                    } else {
                        extra = extra.replace("\t", "");
                    }
                    bytes = EaseUtils.concat(bytes, (extra + "\t").getBytes("ASCII"));
                }
            }
            return bytes;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "SmaStreamInfo{" +
                "size=" + size +
                ", flag=" + flag +
                ", extras=" + Arrays.toString(extras) +
                '}';
    }
}
