
package com.eversec.database.sdb.util;

import java.util.List;

public class StringUtils {

    public static String processStringDefault(Object str) {
        if (str == null || str.equals("")) {
            return "";
        } else {
            return str.toString();
        }
    }

    public static String leftTrims(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            String regularExp = "^[　 ]+";
            return str.replaceAll(regularExp, "");
        }
    }

    public static String rightTrims(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            str = "" + str;
            String regularExp = "[　 ]+$";
            return str.replaceAll(regularExp, "");
        }
    }

    public static String trims(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            str = "" + str;
            String regularExp = "^[　 ]+|[　 ]+$";
            return str.replaceAll(regularExp, "");
        }
    }

    /**
     * 获取字段个数
     *
     * @param s
     * 原字符串
     * @param str
     * 分隔符
     * @param limit
     * 末尾是否以分隔符结尾
     * @return 字段个数
     */
    private static int count(String s, String str, int limit) {
        int k = 0;
        for (int index = 0;; index += str.length()) {
            index = s.indexOf(str, index);
            if (index == -1) {
                break;
            }
            k++;
        }
        return limit < 1 ? ++k : k;
    }

    /**
     * 按分隔符分割字符串，取代jdk的String.split方法
     *
     * @param str
     * 待分割字符串
     * @param split
     * 分隔符
     * @param length
     * 分隔符长度
     * @return 字符串数组
     */
    public static String[] split(String str, String split, int length) {
        if (!str.endsWith(split)) {
            str += split;
        }
        return split(str, split, length, 1);
    }

    /**
     * 按分隔符分割字符串，取代jdk的String.split方法
     *
     * @param str
     * 待分割字符串
     * @param split
     * 分隔符
     * 
     * @param length
     * 分隔符长度
     * 
     * @param limit
     * 是否以分隔符结尾 (1是,0否)
     * 
     * @return 字符串数组
     */
    public static String[] split(String str, String split, int length, int limit) {
        String[] array = new String[count(str, split, limit)];
        if (array.length == 0) {
            return null;
        }
        int index = 0;
        int offset = 0;
        int i = 0;
        while ((index = str.indexOf(split, offset)) != -1) {
            array[i] = str.substring(offset, index);
            offset = index + length;
            i++;
        }
        if (limit < 1) {
            array[array.length - 1] = str.substring(str.lastIndexOf(split) + length);
        }
        return array;
    }

    /**
     * 按分隔符分割字符串，取代jdk的String.split方法
     *
     * @param str
     * 待分割字符串
     * @param array
     * 存储分割后的字符串数组
     * @param split
     * 分隔符
     * @param length
     * 分隔符长度
     * @param limit
     * 是否以分隔符结尾 (1是,0否)
     * @return 字符串数组
     */
    public static void split(String str, List<String> list, String split, int length, int limit) {
        if (list == null) {
            throw new NullPointerException();
        }
        int index = 0;
        int offset = 0;
        while ((index = str.indexOf(split, offset)) != -1) {
            list.add(str.substring(offset, index));
            offset = index + length;
        }
        if (limit < 1) {
            list.add(str.substring(str.lastIndexOf(split) + length));
        }
    }

    /**
     * 按分隔符分割字符串，取代jdk的String.split方法
     *
     * @param str
     * 待分割字符串
     * @param array
     * 存储分割后的字符串数组
     * @param split
     * 分隔符
     * @param length
     * 分隔符长度
     * @return 字符串数组
     * @throws ArrayIndexOutOfBoundsException
     * 数组长度与字符串实际长度不匹配
     */
    public static void split(String str, List<String> list, String split, int length) {
        split(str, list, split, length, 1);
    }

    /**
     * 字符串转换为长整形 ，且当值为空时，赋值为0
     *
     * @param code
     * 待转换字段
     * @return 返回转换后的长整形值
     */
    public static long str2Long(String code) {
        if (code.trim().equals("") || code.trim().toLowerCase().equals("null")) {
            code = "0";
        } else if (code.indexOf(".") > 0) {
            code = code.substring(0, code.indexOf("."));
        }
        return Long.parseLong(code);
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * 需要判断的字段
     * @return 当为数字时返回true，否则为false
     */
    public static boolean isNum(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNull(String code) {
        if (code == null) {
            return true;
        }
        code = code.trim().toLowerCase();
        if (code.equals("") || code.equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    public static int[] strArrToIntArr(String[] strArr) {
        int arrLength = strArr.length;
        int[] reArr = new int[arrLength];
        for (int i = 0; i < arrLength; i++) {
            reArr[i] = Integer.parseInt(strArr[i].trim());
        }
        return reArr;
    }

    /**
     * 读取字符串第i次出现特定符号的位置
     *
     * @return
     */
    public static int getCharacterPosition(String string, int i, String character) {

        int fromIndex = 0;
        for (int j = 0; j < string.length(); j++) {
            fromIndex = string.indexOf(character, fromIndex + 1);
            if (fromIndex != -1) {
                if (j + 1 == i) {
                    break;
                }
            } else {
                return -1;
            }
        }
        return fromIndex;
    }

    /**
     */
    public static void main(String[] args) {
        String ipStart = "192.168.134.1";
        int posit2 = getCharacterPosition(ipStart, 2, ".");
        int posit3 = getCharacterPosition(ipStart, 3, ".");
        System.out.println(ipStart.substring(posit2 + 1, posit3));
        System.out.println(getCharacterPosition("192.168.0.1", 2, "."));
        System.out.println(getCharacterPosition("192.168.0.1", 3, "."));
        System.out.println(getCharacterPosition("192.168.0.1", 4, "."));
        System.out.println("=================");
        System.out.println("192.18.0.1".indexOf(".", 1));
        /*
         * String ods =
         * "120192168169214133721968800429428200196117764#|10.234.139.12#|10.235.148.9#|
         * 10.204.51.97#|13#|10.235.148.9#|1#|3009#|2012-05-17
         * 09:54:48.053159#|2012-05-17
         * 09:54:48.322079#|268920#|15323169769#|/orderDeal.jspx?dealType=2&
         * productId=135000000000000021401&idType=03^M<96>#|200#|1#|10.235.148.9#|
         * 460036230129497#|BREW-Applet/0x010DB517 (BREW/3.1.5.186; DeviceId: 40157;
         * Lang: zhcn)#|3#|14#|2#|application/xml#|#|WAP2脨颅脪茅CDR路脰脦枚_214#|20#
         * |20120517095000#|a" ; String str =
         * "A000002F4488F8|13313607167|460030913650583|黑龙江|哈尔滨|三星| SCH-B309|Jul 07
         * 2011|2012-08-13 06:55:13"; ods = str; long start =
         * System.currentTimeMillis(); int count = 10000000; for (int i = 0; i < count;
         * i++) { ods.split("#\\|"); } System.out.println("split耗时" +
         * (System.currentTimeMillis() - start)); start = System.currentTimeMillis();
         * for (int i = 0; i < count; i++) { split(ods, "#|", "#|".length(), 0); }
         * System.out.println("Array优化耗时" + (System.currentTimeMillis() - start)); start
         * = System.currentTimeMillis(); List<String> list = new ArrayList<String>();
         * for (int i = 0; i < count; i++) { split(ods, list, "#|", "#|".length(), 0);
         * list.clear(); } System.out.println("ArrayList优化耗时" +
         * (System.currentTimeMillis() - start));
         */
    }

}
