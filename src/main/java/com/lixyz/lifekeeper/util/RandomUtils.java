package com.lixyz.lifekeeper.util;

import java.util.UUID;

public class RandomUtils {

    private static final String charlist = "0123456789";

    public static String createRandomString(int len) {
        String str = new String();
        for (int i = 0; i < len; i++) {
            str += charlist.charAt(getRandom(charlist.length()));
        }
        return str;
    }

    public static int getRandom(int mod) {
        if (mod < 1) {
            return 0;
        }
        int ret = getInt() % mod;
        return ret;
    }

    private static int getInt() {
        int ret = Math.abs(Long.valueOf(getRandomNumString()).intValue());
        return ret;
    }

    private static String getRandomNumString() {
        double d = Math.random();
        String dStr = String.valueOf(d).replaceAll("[^\\d]", "");
        if (dStr.length() > 1) {
            dStr = dStr.substring(0, dStr.length() - 1);
        }
        return dStr;
    }

    /**
     * 获取文件真实名称
     * 由于浏览器的不同获取的名称可能为:c:/upload/1.jpg或者1.jpg
     * 最终获取的为  1.jpg
     * @param name 上传上来的文件名称
     * @return 真实名称
     */
    public static String getRealName(String name) {
        //获取最后一个"/"
        int index = name.lastIndexOf("\\");
        return name.substring(index + 1);
    }


    /**
     * 获取随机名称
     *
     * @param realName 真实名称
     * @return uuid 随机名称
     */
    public static String getUUIDName(String realName) {
        //realname  可能是  1sfasdf.jpg   也可能是 1sfasdf 1
        //获取后缀名
        int index = realName.lastIndexOf(".");
        if (index == -1) {
            return UUID.randomUUID().toString().replace("-", "").toUpperCase();
        } else {
            return UUID.randomUUID().toString().replace("-", "").toUpperCase() + realName.substring(index);
        }
    }

}
