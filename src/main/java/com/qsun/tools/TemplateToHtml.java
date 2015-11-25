package com.qsun.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.ext.tag.TrimHtml;

import com.jfinal.core.JFinal;
import com.qsun.base.model.HireModel;
import com.qsun.base.model.NewsModel;



public class TemplateToHtml {

	public static void createHTML(ServletContext context,
			Map<String, Object> data, String templateFolder,
			String templateFile, String targetHtmlPath) {
		FileResourceLoader resourceLoader = new FileResourceLoader(
				context.getRealPath("/") + templateFolder, "utf-8");
		Configuration cfg = null;
		try {
			cfg = Configuration.defaultConfiguration();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		gt.registerTag("compress", TrimHtml.class);
		Template t = gt.getTemplate("/" + templateFile);
		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(context.getRealPath("/")
							+ targetHtmlPath), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 处理模版并开始输出静态页面
		t.binding(data);
		t.renderTo(out);
		t.render();
	}
	/**
	 * 新闻静态化
	 * 
	 *@author huwei
	 * 2015年11月25日
	 *
	 *@param request
	 */
	public static void dealNewsPageHtml(HttpServletRequest request) {
		// 新闻
		List<NewsModel> newsList = NewsModel.dao
				.find("select  id, title,CASE WHEN CHAR_LENGTH(summary)>300 THEN CONCAT(SUBSTRING(summary,1,300),'...') ELSE summary END AS summary,editor,DATE_FORMAT(create_time,\"%Y-%m-%d\") as createTime,month,day  from news where status=1 order by create_time desc");
		if (newsList == null || newsList.size() <= 0) {
			return;
		}
			Map<String, Object> data = new HashMap<String, Object>();
			String base = request.getContextPath();
			data.put("newsList", newsList);
			data.put("base", base);
			String templateFile = "news.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_TEMP_FOLDER+ File.separator +"news" + File.separator + "index.html";
			createHTML(request.getServletContext(), data, templateFolder,templateFile, targetHtmlPath);
		}
	/**
	 * 招聘静态化
	 * 
	 *@author huwei
	 * 2015年11月25日
	 *
	 *@param request
	 */
	public static void dealHirePageHtml(HttpServletRequest request) {
		// 招聘
		List<HireModel> hireList = HireModel.dao
				.find("select  id, position,location,pay,experience,edu,attract,req,duty,DATE_FORMAT(create_time,\"%Y-%m-%d\") as createTime from hire where status=1 order by create_time desc");
		if (hireList == null || hireList.size() <= 0) {
			return;
		}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("hireList", hireList);
			String templateFile = "jobs.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_TEMP_FOLDER + File.separator + "jobs.html";
			createHTML(request.getServletContext(), data, templateFolder,templateFile, targetHtmlPath);
		}
	/**
	 * 临时覆盖正式
	 */
	public static void cover(String tempHtml,String staticHtml){
		try {
			int byteread = 0;
			FileInputStream in = new FileInputStream(new File(JFinal.me().getServletContext().getRealPath("/") + tempHtml));
			FileOutputStream out = new FileOutputStream(new File(JFinal.me().getServletContext().getRealPath("/") + staticHtml));
			byte[] buffer = new byte[1444];
			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
/*
 * public static void dealNewsPageHtml(HttpServletRequest request) {
		// 新闻
		List<NewsModel> newsList = NewsModel.dao
				.find("select  id, title, image_name, image_path,CASE WHEN CHAR_LENGTH(summary)>102 THEN CONCAT(SUBSTRING(summary,1,102),'...') ELSE summary END AS summary,content,category,source,enable,sort,create_time,DATE_FORMAT(update_time,\"%Y-%m-%d\") as update_time,update_time up_time from news where enable=1 order by up_time desc");
		if (newsList == null || newsList.size() <= 0) {
			return;
		}

		Integer pageSize = 5;

		Integer pageCount = newsList.size() % 5 == 0 ? newsList.size() / 5
				: newsList.size() / 5 + 1;

		Integer total = newsList.size();

		for (int i = 0; i <= pageCount - 1; i++) {
			List<NewsModel> newsPageList = new ArrayList<NewsModel>();
			int temp = i * pageSize;
			for (int j = temp; j < temp + 5; j++) {
				if (j >= newsList.size()) {
					break;
				}
				newsPageList.add(newsList.get(j));
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("page", i + 1);
			data.put("total", total);
			data.put("pageCount", pageCount);
			data.put("pageSize", pageSize);
			data.put("newsList", newsPageList);
			data.put("pre", "news_" + i + ".html");
			data.put("next", "news_" + (i + 2) + ".html");
			String targetFile = "news_" + (i + 1) + ".html";
			String templateFile = "news.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			createHTML(request.getServletContext(), data, templateFolder,
					templateFile, targetHtmlPath);
		}
	}
	// 首页静态化处理
	public static void dealHomePageHtml(HttpServletRequest request) {
		// 轮播
		List<HomePageModel> carousels = HomePageModel.dao
				.find("select * from home_page where module=1 and enable=1 order by sort limit 4");
		// 平台
		List<HomePageModel> platforms = HomePageModel.dao
				.find("select * from home_page where module=4 and enable=1 order by sort limit 3");
		// 合作伙伴
		List<HomePageModel> partners = HomePageModel.dao
				.find("select * from home_page where module=3 and enable=1 order by sort limit 9");
		// 简介
		List<HomePageModel> introduces = HomePageModel.dao
				.find("select * from home_page where module=2 and enable=1 order by sort limit 3");
		// 新闻
		List<NewsModel> newsList = NewsModel.dao
				.find("select id, title, image_name, image_path, CASE WHEN CHAR_LENGTH(summary)>102 THEN CONCAT(SUBSTRING(summary,1,102),'...') ELSE summary END AS summary,content,category,source,enable,sort,create_time,DATE_FORMAT(update_time,\"%Y-%m-%d\") as update_time,update_time up_time from news where enable=1 order by up_time desc limit 4");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("carousels", carousels);
		data.put("platforms", platforms);
		data.put("partners", partners);
		data.put("introduces", introduces);
		data.put("newsList", newsList);
		String templateFile = "index.html";
		String templateFolder = SysConstants.TEMPLATE_FOLDER;
		String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
				+ File.separator + "index.html";
		createHTML(request.getServletContext(), data, templateFolder,
				templateFile, targetHtmlPath);
	}


	// 新闻详情
	public static void dealNewsDetailPageHtml(HttpServletRequest request,
			int id, int type) {
		if (type == 0) { // 删除
			String targetFile = "news" + File.separator + "newsDetail_" + id
					+ ".html";
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			File file = new File(request.getServletContext().getRealPath("/")
					+ targetHtmlPath);
			if (file.exists()) {
				file.delete();
			}
		}

		List<NewsModel> newsList = NewsModel.dao
				.find("select  id, title, image_name, image_path,summary,content,category,source,enable,sort,create_time,DATE_FORMAT(update_time,\"%Y-%m-%d\") as update_time,update_time up_time from news where enable=1 order by up_time desc");
		if (newsList == null || newsList.size() <= 0) {
			return;
		}
		for (int i = 0; i < newsList.size(); i++) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("news", newsList.get(i));
			if (i == 0) {
				data.put("hasPre", false);
			} else {
				data.put("hasPre", true);
				data.put("pre", newsList.get(i - 1));
			}

			if (i == newsList.size() - 1) {
				data.put("hasNext", false);
			} else {
				data.put("hasNext", true);
				data.put("next", newsList.get(i + 1));
			}

			String targetFile = "news" + File.separator + "newsDetail_"
					+ newsList.get(i).get("id") + ".html";
			String templateFile = "newsDetail.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			createHTML(request.getServletContext(), data, templateFolder,
					templateFile, targetHtmlPath);
		}

	}

	// 联系页
	public static void dealContactPageHtml(HttpServletRequest request) {
		List<ContactModel> contacts = ContactModel.dao
				.find("SELECT * from contact where enable = 1 order by sort asc");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("contacts", contacts);
		String templateFile = "contactUs.html";
		String templateFolder = SysConstants.TEMPLATE_FOLDER;
		String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
				+ File.separator + "contactUs.html";
		TemplateToHtml.createHTML(request.getServletContext(), data,
				templateFolder, templateFile, targetHtmlPath);
	}

	// 产品页
	public static void dealProductPageHtml(HttpServletRequest request) {
		List<ProductModel> products = ProductModel.dao
				.find("SELECT * from product where enable = 1 order by sort asc");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("products", products);
		String templateFile = "proInfo.html";
		String templateFolder = SysConstants.TEMPLATE_FOLDER;
		String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
				+ File.separator + "proInfo.html";
		TemplateToHtml.createHTML(request.getServletContext(), data,
				templateFolder, templateFile, targetHtmlPath);
	}

	// 公司文化、活动页
	public static void dealCulturePageHtml(HttpServletRequest request) {
		List<CultureModel> cultureEnters = CultureModel.dao
				.find("select id,title,image_path,sort from culture where module='1' and enable='1' order by sort asc limit 3");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("cultureEnters", cultureEnters);

		List<CultureModel> cultureModels = CultureModel.dao
				.find("select id,title,image_path,introduction,sort from culture where module='2' and enable='1' order by sort asc limit 3");
		data.put("cultureModels", cultureModels);
		String templateFile = "culture.html";
		String templateFolder = SysConstants.TEMPLATE_FOLDER;
		String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
				+ File.separator + "culture.html";
		TemplateToHtml.createHTML(request.getServletContext(), data,
				templateFolder, templateFile, targetHtmlPath);
	}

	// 公司简介
	public static void dealProfileHtml(HttpServletRequest request) {
		List<ProfileModel> companyProfile = ProfileModel.dao
				.find("select introduction from about_us where update_time=(select max(update_time) from about_us)");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("companyProfile", companyProfile);
		String templateFile = "aboutUs.html";
		String templateFolder = SysConstants.TEMPLATE_FOLDER;
		String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
				+ File.separator + "aboutUs.html";
		TemplateToHtml.createHTML(request.getServletContext(), data,
				templateFolder, templateFile, targetHtmlPath);
	}

	// 公司活动详情页
	public static void dealCultureDetailsHtml(HttpServletRequest request,
			CultureModel cm, int type) {
		if (type == 0) {// 删除
			String targetFile = "news" + File.separator + "introduce_"
					+ cm.getInt("id") + ".html";
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			File file = new File(request.getServletContext().getRealPath("/")
					+ targetHtmlPath);
			if (file.exists()) {
				file.delete();
			}
		} else {
			Map<String, Object> data = new HashMap<String, Object>();
			cm.put("content", cm.get("introduction"));
			cm.put("title", cm.get("title"));
			cm.put("source", "公司活动");
			Date update = cm.getDate("update_time");
			cm.set("update_time", DateTools.formatDate(update));
			data.put("news", cm);// //////////////
			data.put("hasPre", false);
			data.put("hasNext", false);

			String targetFile = "news" + File.separator + "culture_"
					+ cm.get("id") + ".html";
			String templateFile = "newsDetail.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			createHTML(request.getServletContext(), data, templateFolder,
					templateFile, targetHtmlPath);
		}
	}

	// 首页简介区详情页
	public static void dealComIntrHtml(HttpServletRequest request,
			HomePageModel vo, int type) {
		if (type == 0) { // 删除
			String targetFile = "news" + File.separator + "introduce_"
					+ vo.getInt("id") + ".html";
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			File file = new File(request.getServletContext().getRealPath("/")
					+ targetHtmlPath);
			if (file.exists()) {
				file.delete();
			}
		} else {
			Map<String, Object> data = new HashMap<String, Object>();
			vo.put("content", vo.get("introduction"));
			vo.put("title", vo.get("name"));
			vo.put("source", "公司简介");
			Date update = vo.getDate("update_time");
			vo.set("update_time", DateTools.formatDate(update));
			data.put("news", vo);
			data.put("hasPre", false);
			data.put("hasNext", false);

			String targetFile = "news" + File.separator + "introduce_"
					+ vo.get("id") + ".html";
			String templateFile = "newsDetail.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			createHTML(request.getServletContext(), data, templateFolder,
					templateFile, targetHtmlPath);
		}
	}

	// 发展历程
	public static void dealHistoryHtml(HttpServletRequest request) {
		List<HistoryModel> Historys = HistoryModel.dao
				.find("select time,introduction,image_note,image_path from dev_history where enable='1' order by time desc");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("Historys", Historys);
		String templateFile = "timeInfo.html";
		String templateFolder = SysConstants.TEMPLATE_FOLDER;
		String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
				+ File.separator + "timeInfo.html";
		TemplateToHtml.createHTML(request.getServletContext(), data,
				templateFolder, templateFile, targetHtmlPath);
	}

	// 公司活动详情页 批量生成
	public static void dealBatchCultureDetailsHtml(HttpServletRequest request) {
		// 公司活动
		List<CultureModel> cultureModels = CultureModel.dao
				.find("select * from culture where module=2 and enable=1 order by sort asc limit 3");
		for (CultureModel cm : cultureModels) {
			Map<String, Object> data = new HashMap<String, Object>();
			cm.put("content", cm.get("introduction"));
			cm.put("title", cm.get("title"));
			cm.put("source", "公司活动");
			Date update = cm.getDate("update_time");
			cm.set("update_time", DateTools.formatDate(update));
			data.put("news", cm);
			data.put("hasPre", false);
			data.put("hasNext", false);

			String targetFile = "news" + File.separator + "culture_"
					+ cm.get("id") + ".html";
			String templateFile = "newsDetail.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			createHTML(request.getServletContext(), data, templateFolder,
					templateFile, targetHtmlPath);
		}
	}

	// 首页简介区详情页 批量生成
	public static void dealBatchComIntrHtml(HttpServletRequest request) {
		// 简介
		List<HomePageModel> introduces = HomePageModel.dao
				.find("select * from home_page where module=2 and enable=1 order by sort limit 3");
		for (HomePageModel vo : introduces) {
			Map<String, Object> data = new HashMap<String, Object>();
			vo.put("content", vo.get("introduction"));
			vo.put("title", vo.get("name"));
			vo.put("source", "公司简介");
			Date update = vo.getDate("update_time");
			vo.set("update_time", DateTools.formatDate(update));
			data.put("news", vo);
			data.put("hasPre", false);
			data.put("hasNext", false);

			String targetFile = "news" + File.separator + "introduce_"
					+ vo.get("id") + ".html";
			String templateFile = "newsDetail.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			createHTML(request.getServletContext(), data, templateFolder,
					templateFile, targetHtmlPath);
		}
	}

	// 新闻详情 批量生成
	public static void dealBatchNewsDetailPageHtml(HttpServletRequest request) {
		List<NewsModel> newsList = NewsModel.dao
				.find("select  id, title, image_name, image_path,summary,content,category,source,enable,sort,create_time,DATE_FORMAT(update_time,\"%Y-%m-%d\") as update_time,update_time up_time from news where enable=1 order by up_time desc");
		if (newsList == null || newsList.size() <= 0) {
			return;
		}
		for (int i = 0; i < newsList.size(); i++) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("news", newsList.get(i));
			if (i == 0) {
				data.put("hasPre", false);
			} else {
				data.put("hasPre", true);
				data.put("pre", newsList.get(i - 1));
			}

			if (i == newsList.size() - 1) {
				data.put("hasNext", false);
			} else {
				data.put("hasNext", true);
				data.put("next", newsList.get(i + 1));
			}

			String targetFile = "news" + File.separator + "newsDetail_"
					+ newsList.get(i).get("id") + ".html";
			String templateFile = "newsDetail.html";
			String templateFolder = SysConstants.TEMPLATE_FOLDER;
			String targetHtmlPath = SysConstants.HTML_STATIC_FOLDER
					+ File.separator + targetFile;
			createHTML(request.getServletContext(), data, templateFolder,
					templateFile, targetHtmlPath);
		}

	}
	
	public static void dealAllPage(HttpServletRequest request){
		// 首页简介区
		TemplateToHtml.dealBatchComIntrHtml(request);
		// 新闻详情
		TemplateToHtml.dealBatchNewsDetailPageHtml(request);
		// 联系我们
		TemplateToHtml.dealContactPageHtml(request);
		// 企业文化
		TemplateToHtml.dealCulturePageHtml(request);
		// 首页
		TemplateToHtml.dealHomePageHtml(request);
		// 新闻分页
		TemplateToHtml.dealNewsPageHtml(request);
		// 产品服务
		TemplateToHtml.dealProductPageHtml(request);
		// 公司简介
		TemplateToHtml.dealProfileHtml(request);
		//发展历程
		TemplateToHtml.dealHistoryHtml(request);
		//公司活动详情页
		TemplateToHtml.dealBatchCultureDetailsHtml(request);
	}*/
}
