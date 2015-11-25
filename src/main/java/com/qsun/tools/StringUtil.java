/**   
 * @Title: StringUtil.java 
 * @Package com.base.util 
 * @Description: TODO
 * @author Jeckey.Liu   
 * @date 2014年8月6日 下午1:52:20 
 * @version V1  
 */
package com.qsun.tools;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @ClassName: StringUtil
 * @Description: TODO
 * @author Jeckey.Liu
 * @date 2014年8月6日 下午1:52:20
 * 
 */
public class StringUtil{
	private static final Log logger = LogFactory.getLog(StringUtil.class);
	
	public static final String SPECIAL_CHARACTERS_COMMA = ",";
	
	public static final String[] LETTERLIST = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	  public static boolean isEmptyString(String str)
	  {
	    return (str == null) || (str.trim().equals(""));
	  }

	  public static String convertGb2312ToIso8859(String in)
	  {
	    String out = null;
	    try {
	      byte[] ins = in.getBytes("gb2312");
	      out = new String(ins, "iso-8859-1");
	    } catch (Exception localException) {
	    }
	    return out;
	  }

	  public static String md5EncodeString(String str, boolean islong)
	  {
	    MessageDigest md = null;
	    try {
	      md = MessageDigest.getInstance("MD5");
	      md.update(str.getBytes());
	    }
	    catch (Exception e) {
	      throw new IllegalArgumentException();
	    }

	    String res = byteToString(md.digest());
	    if (!islong) {
	      res = res.substring(8, 24);
	    }
	    return res;
	  }

	  private static String byteToString(byte[] b) {
	    StringBuffer hexString = new StringBuffer();

	    for (int i = 0; i < b.length; ++i) {
	      String plainText = Integer.toHexString(0xFF & b[i]);
	      if (plainText.length() < 2) {
	        plainText = "0" + plainText;
	      }
	      hexString.append(plainText);
	    }

	    return hexString.toString();
	  }

	  public static String encodeString(String str, String enc) throws Exception
	  {
	    if (str == null) {
	      return "";
	    }
	    String theDefenc = "UTF-8";
	    if (!isEmptyString(enc)) {
	      theDefenc = enc;
	    }
	    return URLEncoder.encode(str, theDefenc);
	  }

	  public static String decodeString(String str, String enc)
	    throws Exception
	  {
	    if (str == null) {
	      return "";
	    }
	    String theDefenc = "UTF-8";
	    if (!isEmptyString(enc)) {
	      theDefenc = enc;
	    }
	    return URLDecoder.decode(str, theDefenc);
	  }

	  public static String genRandomString(int size)
	  {
	    return RandomStringUtils.random(size, "0123456789abcedfghijklmnpqrstuvwxyz0123456789");
	  }
	  public static String genRandomString(int size, String textChars) {
	    return RandomStringUtils.random(size, textChars);
	  }

	  public static String replaceTextTplFlag(String text, Map<String, String> valueKeyMap) {
	    List<String> list = strRgxMatch(".*?\\$\\{(.*?)\\}.*?", text);
	    for (String key : list) {
	      if (valueKeyMap.get(key) != null) {
	        text = text.replaceAll("\\$\\{" + key + "\\}", (String)valueKeyMap.get(key));
	      }
	    }
	    return text;
	  }

	  public static List<String> strRgxMatch(String rgx, String sourceStr) {
	    List res = new ArrayList();
	    Pattern pat = Pattern.compile(rgx, 10);
	    Matcher mat = pat.matcher(sourceStr);
	    while (mat.find()) {
	      res.add(mat.group(1));
	    }
	    return res;
	  }

	  public static boolean isStrRgxMatch(String rgx, String sourceStr) {
	    Pattern pat = Pattern.compile(rgx, 10);
	    return pat.matcher(sourceStr).matches();
	  }


	  public static boolean getBooleanFromSringChar(String src, int charIndex)
	  {
	    if ((isEmptyString(src)) || (src.trim().length() < charIndex + 1)) {
	      return false;
	    }
	    char c = src.charAt(charIndex);

	    return c != '0';
	  }

	  public static String[] split(String str, String regex, int limit)
	  {
	    if (str != null) {
	      return str.split(regex, limit);
	    }
	    return null;
	  }
	  public static String[] split(String str, String regex) {
	    if (str != null) {
	      return str.split(regex);
	    }
	    return null;
	  }

	  public static void main(String[] args)
	    throws Exception
	  {
	  }
	public static String encodeStr(String str, String enc){
		if(StringUtils.isBlank(str)){
			return str;
		}
		try{
			return encodeString(str, enc);
		}catch(Exception e){
			return str;
		}
	}

	/**
	 * 获取当前时间的格式化字符串
	 * 
	 * @param parrten
	 *            格式字符串
	 * @return
	 */
	public static String getFormateDate(String parrten) {
		if (isEmptyString(parrten)) {
			return getFormateDate();
		}
		Date date = new Date();
		SimpleDateFormat formate = new SimpleDateFormat(parrten);
		String str = formate.format(date);
		return str;
	}

	public static String getFormateDate(Date date, String parrten) {
		if (date == null) {
			return "";
		}
		if (isEmptyString(parrten)) {
			return getFormateDate();
		}
		SimpleDateFormat formate = new SimpleDateFormat(parrten);
		String str = formate.format(date);
		return str;
	}

	/**
	 * 获取默认格式:[yyyy-MM-dd hh:mm:ss:SSS]格式化当前时间的字符串
	 * 
	 * @return
	 */
	public static String getFormateDate() {
		Date date = new Date();
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		String str = formate.format(date);
		return str;
	}
	
	/**
	 * 
	 * getFormateDates 
	 * 获取默认格式:[yyyy-MM-dd]格式化当前时间的字符串
	 * @param date
	 * @return    
	 * String
	 */
	public static String getFormateDates(Date date) {
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
		String str = formate.format(date);
		return str;
	}

	public static Date getFormateDate(String str, String parrten) {
		if (!isEmptyString(str)) {
			int index = str.toUpperCase().indexOf("T");
			String s = str;
			if (index > 0) {
				s = new String(str.substring(0, index));
			}
			SimpleDateFormat format = new SimpleDateFormat(parrten);
			try {
				Date date = format.parse(s);
				return date;
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				return null;
			}
		}
		return null;
	}

	public static String getStartDateStr(Date date) {
		if (date != null) {
			String str = getFormateDate(date, "yyyy-MM-dd");
			if (!isEmptyString(str)) {
				str += " 00:00:00";
				return str;
			}
		}
		return null;
	}
	public static Date getStartDate(Date date) {
		if (date != null) {
			String str = getFormateDate(date, "yyyy-MM-dd");
			if (!isEmptyString(str)) {
				str += " 00:00:00";
				return getFormateDate(str, "yyyy-MM-dd hh:mm:ss");
			}
		}
		return date;
	}

	public static String getEndDateStr(Date date) {
		if (date != null) {
			String str = getFormateDate(date, "yyyy-MM-dd");
			if (!isEmptyString(str)) {
				str += " 23:59:59";
				return str;
			}
		}
		return null;
	}
	public static Date getEndDate(Date date) {
		if (date != null) {
			String str = getFormateDate(date, "yyyy-MM-dd");
			if (!isEmptyString(str)) {
				str += " 23:59:59";
				return getFormateDate(str, "yyyy-MM-dd hh:mm:ss");
			}
		}
		return date;
	}

	/**
	 * 获取格式化的日期字符串,如果输入不满足,返回NULL
	 * 
	 * @param str
	 * @return String
	 */
	public static String formateDateStr(String str) {
		if (!isEmptyString(str)) {
			int index = str.toUpperCase().indexOf("T");
			if (index > 0) {
				String s = new String(str.substring(0, index));
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					format.parse(s);
					return s;
				} catch (ParseException e) {
					e.printStackTrace();
					logger.error(e.getMessage(), e);
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * 获取字符串里面的数字
	 * 
	 * @param str
	 * @return
	 */
	public static String getIntFormStr(String str) {
		if (isEmptyString(str)) {
			return "0";
		}
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	/**
	 * 获取文件的后缀
	 * @param str
	 * @return String    
	 * 
	 */
	public static String getSuffix(String str){
		if(isEmptyString(str)){
			return str;
		}
		String regEx = "\\w+.+([.]\\w+.)";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		if(m.find()){
			String suffix = m.group(1);
			return suffix;
		}
		return null;
	}

	/**
	 * 转义特殊的sql字符
	 * @param queryStr
	 * @return String 
	 */
	public static String convertSpecialSQLStr(String queryStr) {
		if(StringUtils.isBlank(queryStr)){
			return queryStr;
		}
		queryStr = queryStr.replace("\\", "\\\\");
//		System.out.println(queryStr);
		queryStr = queryStr.replace("'", "\\\'");
//		System.out.println(queryStr);
		queryStr = queryStr.replace("\"", "\\\"");
//		System.out.println(queryStr);
		//queryStr = queryStr.replace("_", "\\_");
//		System.out.println(queryStr);
		queryStr = queryStr.replace("%", "\\%");
//		System.out.println(queryStr);
		return queryStr;
	}
	/**
	 * 
	 * solrAssert 
	 * solr 查询是否为空，为空怎么返回false，不为空则返回true 
	 * @param data
	 * @return    
	 * boolean
	 */
	public static boolean solrAsserts(String data){
		if (StringUtils.isEmpty(data)) {
			return false;
		}else if (StringUtils.isBlank(data)) {
			return false;
		}else if(data.equals("[]")){
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * solrAssert 
	 * solr 查询是否为空，为空怎么返回false，不为空则返回true 
	 * 有可能solr查询出来在数据只有分页对象,而分页对象中没有数据返回false
	 * @param data
	 * @return    
	 * boolean
	 */
	
	/**
	 * 
	 * listToString 
	 * 集合转换成字符串,通过字符参数c来拼接
	 * @param list
	 * @param c
	 * @return    
	 * String
	 */
	public static String listToString(List<?> list, char c) {    
		StringBuilder sb = new StringBuilder();    
		for (int i = 0; i < list.size(); i++) {        
			sb.append(list.get(i)).append(c);    
		}   
		return sb.toString().substring(0,sb.toString().length()-1);
	}
}
