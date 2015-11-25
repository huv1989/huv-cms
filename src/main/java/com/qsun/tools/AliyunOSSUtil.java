package com.qsun.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.StringHolder;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

/**
 * @ClassName: AliyunOSSUtil
 * @Description: 阿里云开放存储服务OSS操作辅助类
 * @author Jelly.Liu
 * @date 2014年11月17日 下午1:49:14
 * 
 */
public class AliyunOSSUtil {
	private static OSSClient client = null;
	private static String ACCESS_ID = "ndm6c0zcwyz1x6n5hqe66rig";
	private static String ACCESS_KEY = "uDbjGwyWTrXwzGjDZUoMdRUq9cE=";
	private static String OSS_ENDPOINT =  "http://oss-cn-hangzhou.aliyuncs.com";
	private static String OSS_BUCKET = "cherrytime";
	private static String IMAGE_SERVER_HOST = "http://songimage.oss-cn-hangzhou.aliyuncs.com";
	public static String IMAGE_SERVER_BUCKET = "songimage";

	private static boolean isInitFlag = false;
	
	public static void initParams() {
		Properties p =  new Properties();
		try {
			p.load(AliyunOSSUtil.class.getResourceAsStream("/deployment.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		ACCESS_ID = p.getProperty("aliyun_accessKeyId");
		ACCESS_KEY = p.getProperty("aliyun_accessKeySecret");
		OSS_ENDPOINT = p.getProperty("aliyun_cherrytime_host");
		OSS_BUCKET = p.getProperty("aliyun_cherrytime_bucket");
		IMAGE_SERVER_HOST = p.getProperty("aliyun_img_server_host");
		IMAGE_SERVER_BUCKET = p.getProperty("aliyun_member_image_bucket");
	}
	
	/**
	 * 获取图片的地址
	 * @param key
	 * @return  String   
	 */
	public static String getImageURL(String key){
		if (key != null && key.startsWith("http://")) {
			return key;
		}
		if(!isInitFlag){
			init();
		}
		if(StringUtils.isBlank(key)){
			return key;
		}
		
		if(StringUtils.isBlank(IMAGE_SERVER_HOST)){
			IMAGE_SERVER_HOST = "http://songimage.oss-cn-hangzhou.aliyuncs.com";
		}
		String url = IMAGE_SERVER_HOST;
		if(key.startsWith(url)){
			return key;
		}
		if(!url.endsWith("/")){
			url += "/"; 
		}
		if(key.startsWith("/")){
			key = StringUtils.substring(key, 1, key.length());
		}
		key = StringUtil.encodeStr(key, null);
		key = StringUtils.replace(key, "%2F", "/");
		key = StringUtils.replace(key, "+", "%20");
		url += key;
		return url;
	}

	private static void init() {
		initParams();
		client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
		isInitFlag = true;
	}

	/**
	 * 上传文件到OSS
	 * 
	 * @param key
	 * @param file
	 * @param contentType
	 * @param md5Value
	 * @return PutObjectResult
	 * @throws OSSException
	 * @throws ClientException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * 
	 */
	public static PutObjectResult uploadFile(String key, File file, String contentType, String md5Value)
			throws OSSException, ClientException, FileNotFoundException, IOException {
		if (file == null) {
			return null;
		}
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			return putObject(key, input, file.length(), contentType, md5Value);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (OSSException e) {
			throw e;
		} catch (ClientException e) {
			throw e;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					throw e;
				} finally {
					input = null;
				}
			}
		}
	}

	/**
	 * 实现:上传文件到OSS
	 * 
	 * @param key
	 * @param input
	 * @param size
	 *            文件大小 (小于5G)
	 * @param contentType
	 * @param md5Value
	 * @return PutObjectResult
	 * @throws OSSException
	 * @throws ClientException
	 * 
	 */
	public static PutObjectResult putObject(String key, InputStream input, long size, String contentType,
			String md5Value) throws OSSException, ClientException {
		return putObject(null, key, input, size, contentType, md5Value, null);
	}
	
	/**
	 * 实现:上传文件到OSS
	 * 
	 * @param key
	 * @param input
	 * @param size
	 *            文件大小 (小于5G)
	 * @param contentType
	 * @param md5Value
	 * @return PutObjectResult
	 * @throws OSSException
	 * @throws ClientException
	 * 
	 */
	public static PutObjectResult putImageObject(String key, InputStream input, long size, String contentType,
			String md5Value) throws OSSException, ClientException {
		return putObject(IMAGE_SERVER_BUCKET, key, input, size, contentType, md5Value, null);
	}
	/**
	 * 实现:上传文件到OSS
	 * 
	 * @param key
	 * @param input
	 * @param size
	 *            文件大小 (小于5G)
	 * @param contentType
	 * @param md5Value
	 * @return PutObjectResult
	 * @throws OSSException
	 * @throws ClientException
	 * 
	 */
	public static PutObjectResult putObject(String bucket ,String key, InputStream input, long size, String contentType,
			String md5Value,StringHolder strh) throws OSSException, ClientException {
		if (!isInitFlag) {
			init();
		}
		ObjectMetadata objectMeta = new ObjectMetadata();
		objectMeta.setContentLength(size);
		objectMeta.setContentType(contentType);
		if(StringUtils.isNotBlank(md5Value)){
			objectMeta.addUserMetadata("MD5", md5Value);
		}
		if(StringUtils.isBlank(bucket)){
			bucket = OSS_BUCKET;
		}
		PutObjectResult result = client.putObject(bucket, key, input, objectMeta);
		if(strh != null){
			String url = "http://";
			String str = StringUtils.replace(OSS_ENDPOINT, url, "");
			url += bucket + ".";
			url += str;
			if(url.endsWith("/")){
				url += key;
			}else{
				url += "/" + key;
			}
			strh.value = url;
		}
		return result;
	}

	// // test main function
	public static void main(String[] args) {
		if (!isInitFlag) {
			init();
		}
		System.out.println(ACCESS_ID);
		System.out.println(ACCESS_KEY);
		System.out.println(OSS_ENDPOINT);
		System.out.println(OSS_BUCKET);
	}

}
