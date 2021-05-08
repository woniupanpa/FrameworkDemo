//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.admin.frameworkdemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class BytesUtil {
    public static final String CHARSET_ISO8859_1 = "ISO-8859-1";
    public static final String CHARSET_ISO8859_5 = "ISO-8859-5";
    public static final String CHARSET_GBK = "GBK";
    public static final String CHARSET_GB2312 = "GB2312";
    public static final String CHARSET_UTF8 = "UTF-8";

    public BytesUtil() {
    }

    public static String bytes2HexString(byte[] data) {
        StringBuilder buffer = new StringBuilder();

        for(int i = 0; i < data.length; ++i) {
            byte b = data[i];
            String hex = Integer.toHexString(b & 255);
            if (hex.length() == 1) {
                buffer.append('0');
            }

            buffer.append(hex);
        }

        return buffer.toString().toUpperCase();
    }

    public static byte[] hexString2Bytes(String data) {
        byte[] result = new byte[(data.length() + 1) / 2];
        if ((data.length() & 1) == 1) {
            data = data + "0";
        }

        for(int i = 0; i < result.length; ++i) {
            result[i] = (byte)(hex2byte(data.charAt(i * 2 + 1)) | hex2byte(data.charAt(i * 2)) << 4);
        }

        return result;
    }

    public static byte hex2byte(char hex) {
        if (hex <= 'f' && hex >= 'a') {
            return (byte)(hex - 97 + 10);
        } else if (hex <= 'F' && hex >= 'A') {
            return (byte)(hex - 65 + 10);
        } else {
            return hex <= '9' && hex >= '0' ? (byte)(hex - 48) : 0;
        }
    }

    public static byte[] subBytes(byte[] data, int offset, int len) {
        if (offset >= 0 && data.length > offset) {
            if (len < 0 || data.length < offset + len) {
                len = data.length - offset;
            }

            byte[] ret = new byte[len];
            System.arraycopy(data, offset, ret, 0, len);
            return ret;
        } else {
            return null;
        }
    }

    public static byte[] merage(byte[]... data) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            byte[][] arrayOfByte = data;
            int j = data.length;

            for(int i = 0; i < j; ++i) {
                byte[] d = arrayOfByte[i];
                if (d == null) {
                    throw new IllegalArgumentException("");
                }

                buffer.write(d);
            }

            byte[] var16 = buffer.toByteArray();
            return var16;
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        return null;
    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes.length > 4) {
            return -1;
        } else {
            int lastIndex = bytes.length - 1;
            int result = 0;

            for(int i = 0; i < bytes.length; ++i) {
                result |= (bytes[i] & 255) << (lastIndex - i << 3);
            }

            return result;
        }
    }

    public static int littleEndianBytesToInt(byte[] bytes) {
        if (bytes.length > 4) {
            return -1;
        } else {
            int result = 0;

            for(int i = 0; i < bytes.length; ++i) {
                result |= (bytes[i] & 255) << (i << 3);
            }

            return result;
        }
    }

    public static byte[] intToBytes(int intValue) {
        byte[] bytes = new byte[4];

        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)(intValue >> (3 - i << 3) & 255);
        }

        return bytes;
    }

    public static byte[] intToLittleEndianBytes(int intValue) {
        byte[] bytes = new byte[4];

        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)(intValue >> (i << 3) & 255);
        }

        return bytes;
    }

    public static String bcd2Ascii(byte[] bcd) {
        if (bcd == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(bcd.length << 1);
            byte[] arrayOfByte = bcd;
            int j = bcd.length;

            for(int i = 0; i < j; ++i) {
                byte ch = arrayOfByte[i];
                byte half = (byte)(ch >> 4);
                sb.append((char)(half + (half > 9 ? 55 : 48)));
                half = (byte)(ch & 15);
                sb.append((char)(half + (half > 9 ? 55 : 48)));
            }

            return sb.toString();
        }
    }

    public static byte[] ascii2Bcd(String ascii) {
        if (ascii == null) {
            return null;
        } else {
            if ((ascii.length() & 1) == 1) {
                ascii = "0" + ascii;
            }

            byte[] asc = ascii.getBytes();
            byte[] bcd = new byte[ascii.length() >> 1];

            for(int i = 0; i < bcd.length; ++i) {
                bcd[i] = (byte)(hex2byte((char)asc[2 * i]) << 4 | hex2byte((char)asc[2 * i + 1]));
            }

            return bcd;
        }
    }

    public static byte[] toBytes(String data, String charsetName) {
        try {
            return data.getBytes(charsetName);
        } catch (UnsupportedEncodingException var3) {
            return null;
        }
    }

    public static byte[] toBytes(String data) {
        return toBytes(data, "ISO-8859-1");
    }

    public static byte[] toGBK(String data) {
        return toBytes(data, "GBK");
    }

    public static byte[] toGB2312(String data) {
        return toBytes(data, "GB2312");
    }

    public static byte[] toUtf8(String data) {
        return toBytes(data, "UTF-8");
    }

    public static final byte[] toFourByteArray(int i) {
        byte[] array = new byte[]{(byte)(i >> 24 & 127), (byte)(i >> 16), (byte)(i >> 8), (byte)i};
        return array;
    }

    public static byte[] toBCDAmountBytes(long data) {
        byte[] bcd = new byte[]{0, 0, 0, 0, 0, 0};
        byte[] bcdDou = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        if (data <= 0L) {
            return bcd;
        } else {
            int i;
            for(i = bcdDou.length - 1; data != 0L; --i) {
                bcdDou[i] = (byte)((int)(data % 10L));
                data /= 10L;
            }

            for(i = bcd.length - 1; i >= 0; --i) {
                bcd[i] = (byte)(bcdDou[i * 2 + 1] & 15 | bcdDou[i * 2] << 4 & 240);
            }

            return bcd;
        }
    }

    public static String fromBytes(byte[] data, String charsetName) {
        try {
            return new String(data, charsetName);
        } catch (UnsupportedEncodingException var3) {
            return null;
        }
    }

    public static String fromBytes(byte[] data) {
        return fromBytes(data, "ISO-8859-1");
    }

    public static String fromGBK(byte[] data) {
        return fromBytes(data, "GBK");
    }

    public static String fromGB2312(byte[] data) {
        return fromBytes(data, "GB2312");
    }

    public static String fromUtf8(byte[] data) {
        return fromBytes(data, "UTF-8");
    }

    public static byte[] subByte(byte[] srcBytes, int offset, int len) {
        if (srcBytes == null) {
            return null;
        } else if (len <= srcBytes.length && offset + len <= srcBytes.length && offset < srcBytes.length) {
            byte[] bytes;
            if (len == -1) {
                bytes = new byte[srcBytes.length - offset];
                System.arraycopy(srcBytes, offset, bytes, 0, srcBytes.length - offset);
            } else {
                bytes = new byte[len];
                System.arraycopy(srcBytes, offset, bytes, 0, len);
            }

            return bytes;
        } else {
            return null;
        }
    }

    public static byte[] mergeBytes(byte[] bytesA, byte[] bytesB) {
        if (bytesA != null && bytesA.length != 0) {
            if (bytesB != null && bytesB.length != 0) {
                byte[] bytes = new byte[bytesA.length + bytesB.length];
                System.arraycopy(bytesA, 0, bytes, 0, bytesA.length);
                System.arraycopy(bytesB, 0, bytes, bytesA.length, bytesB.length);
                return bytes;
            } else {
                return bytesA;
            }
        } else {
            return bytesB;
        }
    }

    public static byte[] merge(byte[]... data) {
        if (data == null) {
            return null;
        } else {
            byte[] bytes = null;

            for(int i = 0; i < data.length; ++i) {
                bytes = mergeBytes(bytes, data[i]);
            }

            return bytes;
        }
    }

    public static int bytecmp(byte[] hex1, byte[] hex2, int len) {
        for(int i = 0; i < len; ++i) {
            if (hex1[i] != hex2[i]) {
                return 1;
            }
        }

        return 0;
    }

    public static byte[] hexString2ByteArray(String hexStr) {
        if (hexStr == null) {
            return null;
        } else if (hexStr.length() % 2 != 0) {
            return null;
        } else {
            byte[] data = new byte[hexStr.length() / 2];

            for(int i = 0; i < hexStr.length() / 2; ++i) {
                char hc = hexStr.charAt(2 * i);
                char lc = hexStr.charAt(2 * i + 1);
                byte hb = hexChar2Byte(hc);
                byte lb = hexChar2Byte(lc);
                if (hb < 0 || lb < 0) {
                    return null;
                }

                int n = hb << 4;
                data[i] = (byte)(n + lb);
            }

            return data;
        }
    }

    public static byte hexChar2Byte(char c) {
        if (c >= '0' && c <= '9') {
            return (byte)(c - 48);
        } else if (c >= 'a' && c <= 'f') {
            return (byte)(c - 97 + 10);
        } else {
            return c >= 'A' && c <= 'F' ? (byte)(c - 65 + 10) : -1;
        }
    }

    public static String byteArray2HexString(byte[] arr) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < arr.length; ++i) {
            sb.append(String.format("%02x", arr[i]).toUpperCase());
        }

        return sb.toString();
    }

    public static String byteArray2HexString(List<Byte> arrList) {
        byte[] arr = new byte[arrList.size()];

        for(int i = 0; i < arrList.size(); ++i) {
            arr[i] = (Byte)arrList.get(i);
        }

        return byteArray2HexString(arr);
    }

    public static String byteArray2HexStringWithSpace(byte[] arr) {
        StringBuilder sbd = new StringBuilder();
        byte[] arrayOfByte = arr;
        int j = arr.length;

        for(int i = 0; i < j; ++i) {
            byte b = arrayOfByte[i];
            String tmp = Integer.toHexString(255 & b);
            if (tmp.length() < 2) {
                tmp = "0" + tmp;
            }

            sbd.append(tmp);
            sbd.append(" ");
        }

        return sbd.toString();
    }

    public static String getBCDString(byte[] data, int start, int end) {
        byte[] t = new byte[end - start + 1];
        System.arraycopy(data, start, t, 0, t.length);
        return byteArray2HexString(t);
    }

    public static String getHexString(byte[] data, int start, int end) {
        byte[] t = new byte[end - start + 1];
        System.arraycopy(data, start, t, 0, t.length);
        return byteArray2HexStringWithSpace(t);
    }

    public static byte[] toByteArray(int source, int len) {
        byte[] bLocalArr = new byte[len];

        for(int i = 0; i < 4 && i < len; ++i) {
            bLocalArr[len - 1 - i] = (byte)(source >> 8 * i & 255);
        }

        return bLocalArr;
    }

    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;

        for(int i = 0; i < (asc_len + 1) / 2; ++i) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte)((j >= asc_len ? 0 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }

        return bcd;
    }

    private static byte asc_to_bcd(byte asc) {
        byte bcd;
        if (asc >= 48 && asc <= 57) {
            bcd = (byte)(asc - 48);
        } else if (asc >= 65 && asc <= 70) {
            bcd = (byte)(asc - 65 + 10);
        } else if (asc >= 97 && asc <= 102) {
            bcd = (byte)(asc - 97 + 10);
        } else {
            bcd = (byte)(asc - 48);
        }

        return bcd;
    }

    public static boolean[] getBooleanArray(byte b) {
        boolean[] array = new boolean[8];

        for(int i = 7; i >= 0; --i) {
            array[i] = (b & 1) == 1;
            b = (byte)(b >> 1);
        }

        return array;
    }

    public static int booleanArray2i(boolean[] b) {
        int res = 0;
        int len = b.length;

        for(int i = 0; i < len; ++i) {
            if (b[i]) {
                res += (int)Math.pow(2.0D, (double)(len - i - 1));
            }
        }

        return res;
    }

    public static int byte2Int(byte[] b) {
        int temp = 0;

        for(int i = 0; i < b.length; ++i) {
            temp += (b[i] & 255) << 8 * (b.length - i - 1);
        }

        return temp;
    }

    public static int byte2Int(byte[] b, int bytesNum) {
        int intValue = 0;

        for(int i = 0; i < b.length; ++i) {
            intValue += (b[i] & 255) << 8 * (bytesNum - 1 - i);
        }

        return intValue;
    }

    public static byte[] int2Bytes(int length, int bytesNum) {
        if (bytesNum > 4) {
            bytesNum = 4;
        } else if (bytesNum <= 0) {
            bytesNum = 1;
        }

        if (bytesNum == 4) {
            return new byte[]{(byte)(length >> 24 & 255), (byte)(length >> 16 & 255), (byte)(length >> 8 & 255), (byte)(length & 255)};
        } else if (bytesNum == 3) {
            return new byte[]{(byte)(length >> 16 & 255), (byte)(length >> 8 & 255), (byte)(length & 255)};
        } else {
            return bytesNum == 2 ? new byte[]{(byte)(length >> 8 & 255), (byte)(length & 255)} : new byte[]{(byte)(length & 255)};
        }
    }

    public static String asc2Bcd(String str) {
        byte[] bcd = ASCII_To_BCD(str.getBytes(), str.length());
        return bcd2Str(bcd);
    }

    private static String bcd2Str(byte[] bytes) {
        char[] temp = new char[bytes.length * 2];

        for(int i = 0; i < bytes.length; ++i) {
            char val = (char)((bytes[i] & 240) >> 4 & 15);
            temp[i * 2] = (char)(val > '\t' ? val + 65 - 10 : val + 48);
            val = (char)(bytes[i] & 15);
            temp[i * 2 + 1] = (char)(val > '\t' ? val + 65 - 10 : val + 48);
        }

        return new String(temp);
    }

    public static byte[] hex2byte(String str) {
        int len = str.length();
        String stmp = null;
        byte[] bt = new byte[len / 2];

        for(int n = 0; n < len / 2; ++n) {
            stmp = str.substring(n * 2, n * 2 + 2);
            bt[n] = (byte)Integer.parseInt(stmp, 16);
        }

        return bt;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for(int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }

        return hs.toUpperCase();
    }

    public static String byte2hex(byte b) {
        String hs = "";
        String stmp = "";
        stmp = Integer.toHexString(b & 255);
        if (stmp.length() == 1) {
            hs = hs + "0" + stmp;
        } else {
            hs = hs + stmp;
        }

        return hs.toUpperCase();
    }

    public static List<Byte> hexString2ByteList(String dataStr) {
        byte[] dataArray = hexString2ByteArray(dataStr);
        List<Byte> result = new ArrayList();

        for(int i = 0; i < dataArray.length; ++i) {
            result.add(dataArray[i]);
        }

        return result;
    }

    public static boolean isBitSet(byte val, int bitPos) {
        if (bitPos >= 1 && bitPos <= 8) {
            return (val >> 8 - bitPos & 1) == 1;
        } else {
            throw new IllegalArgumentException("parameter 'bitPos' must be between 1 and 8. bitPos=" + bitPos);
        }
    }

   /* public static byte[] makeDistanceBytes(int distance) {
        int fiveKm = false;
        int fourKm = false;
        int threeKm = false;
        int twoKm = false;
        int oneKm = false;
        int halfOfOneKm = 0;
        int fiveKm = distance / 5000;
        distance -= 5000 * fiveKm;
        int fourKm = distance / 4000;
        distance -= 4000 * fourKm;
        int threeKm = distance / 3000;
        distance -= 3000 * threeKm;
        int twoKm = distance / 2000;
        distance -= 2000 * twoKm;
        int oneKm = distance / 1000;
        distance -= 1000 * oneKm;
        if (distance != 0) {
            halfOfOneKm = 1;
        }

        byte[] distanceBytes = new byte[fiveKm + fourKm + threeKm + twoKm + oneKm + halfOfOneKm];
        int k = 0;
        int i;
        if (fiveKm > 0) {
            for(i = 0; i < fiveKm; ++i) {
                distanceBytes[k++] = 5;
            }
        }

        if (fourKm > 0) {
            for(i = 0; i < fourKm; ++i) {
                distanceBytes[k++] = 4;
            }
        }

        if (threeKm > 0) {
            for(i = 0; i < threeKm; ++i) {
                distanceBytes[k++] = 3;
            }
        }

        if (twoKm > 0) {
            for(i = 0; i < twoKm; ++i) {
                distanceBytes[k++] = 2;
            }
        }

        if (oneKm > 0) {
            for(i = 0; i < oneKm; ++i) {
                distanceBytes[k++] = 1;
            }
        }

        if (halfOfOneKm > 0) {
            for(i = 0; i < halfOfOneKm; ++i) {
                distanceBytes[k++] = 0;
            }
        }

        return distanceBytes;
    }*/

    public static int getDistanceFromBytes(byte[] addBytes) {
        if (addBytes != null && addBytes.length != 0) {
            int km = 0;

            for(int i = 0; i < addBytes.length; ++i) {
                if ((addBytes[i] & 255) == 0) {
                    km += 500;
                } else if ((addBytes[i] & 255) == 1) {
                    km += 1000;
                } else if ((addBytes[i] & 255) == 2) {
                    km += 2000;
                } else if ((addBytes[i] & 255) == 3) {
                    km += 3000;
                } else if ((addBytes[i] & 255) == 4) {
                    km += 4000;
                } else if ((addBytes[i] & 255) == 5) {
                    km += 5000;
                }
            }

            return km;
        } else {
            return 0;
        }
    }

    public static final int getUnsignedInt(byte b) {
        return b & 255;
    }

    public static String string2HexString(String dataStr) {
        return bytes2HexString(toBytes(dataStr));
    }
}
