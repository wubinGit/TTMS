package com.ttms.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CnSwap {
    private String GetTime(){
        SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        return format;
    }
    /**
     * 功能描述: <br>
     * 〈〉
     * @Param: [len]
     * @Return: java.lang.String
     * @Author: 吴彬
     * @Date: 17:45 17:45
     */
    private  String generateCode(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0,len);
    }

    /**
     * 功能描述: <br>
     * 〈〉编号命名前缀
     * @Param: [chinese]
     * @Return: java.lang.String
     * @Author: 吴彬
     * @Date: 15:47 15:47
     */
    public  String getPrefix(String chinese) {
        if(cn2FirstSpell(chinese).equals("GNY")) {
            return "TPCN-CHN";
        }
        return "TLCN";
    }

    /**
     * 功能描述: <br>
     * 〈〉中文转换器
     * @Param: [chinese]
     * @Return: java.lang.String
     * @Author: 吴彬
     * @Date: 15:47 15:47
     */
    public  String cn2FirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (_t != null) {
                        pybf.append(_t[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim().toUpperCase();
    }
    /**
     * 功能描述: <br>
     * 〈〉生成产品编号
     * @Param: []
     * @Return: java.lang.String
     * @Author: 吴彬
     * @Date: 17:50 17:50
     */
    private  String productName(){
        StringBuilder sb=new StringBuilder();
        StringBuilder sB=new StringBuilder();
        sB.append(getPrefix("国内游")).append("-").append(cn2FirstSpell("云南")).append("-")
                .append( GetTime()).append("-").append(cn2FirstSpell("昆明")).append("-")
                .append(generateCode(3));
        return sB.toString();
    }

    public static void main(String[] args) {
        CnSwap sn=new CnSwap();
        String s = sn.productName();

        System.out.println(s);

    }
}
