package com.qsun.tools;

/**
 * @author huwei
 * @date 2015年11月24日
 *
 */
public class NumToChineseTool {

	public static String getChinese(String str){
			String sb ="";
	        switch(str){
	            case "00":sb = "零";break;
	            case "01":sb = "一";break;
	            case "02":sb = "二";break;
	            case "03":sb = "三";break;
	            case "04":sb = "四";break;
	            case "05":sb = "五";break;
	            case "06":sb = "六";break;
	            case "07":sb = "七";break;
	            case "08":sb = "八";break;
	            case "09":sb = "九";break;
	            case "10":sb = "十";break;
	            case "11":sb = "十一";break;
	            case "12":sb = "十二";break;
	            default :sb = str;
	        }
	    return sb;
	}
}
