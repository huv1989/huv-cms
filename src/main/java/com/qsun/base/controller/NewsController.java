package com.qsun.base.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.JFinal;
import com.jfinal.kit.JsonKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.qsun.base.common.BaseController;
import com.qsun.base.common.DisplayTool;
import com.qsun.base.common.Pager;
import com.qsun.base.model.NewsModel;
import com.qsun.base.model.SysLoginLogModel;
import com.qsun.base.model.SysRolesModel;
import com.qsun.base.model.SysUserModel;
import com.qsun.core.plugin.annotation.Control;
import com.qsun.tools.AliyunOSSUtil;
import com.qsun.tools.DateTools;
import com.qsun.tools.NumToChineseTool;
import com.qsun.tools.SysConstants;
import com.qsun.tools.TemplateToHtml;

/**
 * @author huwei
 * @date 2015年11月23日
 *
 */
@Control(controllerKey = "/news")
public class NewsController  extends BaseController{
	
	@SuppressWarnings("unused")
	private static final Logger	LOG		= Logger.getLogger(NewsController.class);
	
	private static String	control	= "新闻管理模块";
	
	public void index()
	{
		setAttr("rolesJson", JsonKit.toJson(SysRolesModel.dao.allRoles()));
		render("base/news_mgr");
	}
	public void list()
	{
		SysUserModel user = getUser();
		Pager pager = createPager();

		pager.addParam("status", getPara("status"));
		pager.addParam("role", user.get("roleID"));

		Page<?> page = NewsModel.dao.page(pager);

		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}
	
	public void imgUpload() throws Exception {  
		   
	        // 获得response,request  
	        HttpServletResponse response = getResponse();  
	        response.setCharacterEncoding("utf-8");  
	        
	        String path = JFinal.me().getServletContext().getRealPath("/upload/news/");
	       
            UploadFile upload = getFile("upload", path, 600 * 1024);
            String uploadContentType = upload.getContentType();// 文件类型  
	       
	        response.setCharacterEncoding("utf-8");  
	        PrintWriter out = response.getWriter();  
	        String callback = getRequest().getParameter("CKEditorFuncNum");
	        String expandedName = ""; // 文件扩展名  
	        if (uploadContentType.equals("image/pjpeg")  
	                || uploadContentType.equals("image/jpeg")) {  
	            // IE6上传jpg图片的headimageContentType是image/pjpeg，而IE9以及火狐上传的jpg图片是image/jpeg  
	            expandedName = ".jpg";  
	        } else if (uploadContentType.equals("image/png")  
	                || uploadContentType.equals("image/x-png")) {  
	            // IE6上传的png图片的headimageContentType是"image/x-png"  
	            expandedName = ".png";  
	        } else if (uploadContentType.equals("image/gif")) {  
	            expandedName = ".gif";  
	        } else if (uploadContentType.equals("image/bmp")) {  
	            expandedName = ".bmp";  
	        } else {  
	            out.println("<script type=\"text/javascript\">");  
	            out.println("window.parent.CKEDITOR.tools.callFunction(" + callback  
	                    + ",''," + "'文件格式不正确（必须为.jpg/.gif/.bmp/.png文件）');");  
	            out.println("</script>");  
	        }  
	        if (upload.getFile().length() > 600 * 1024) {  
	            out.println("<script type=\"text/javascript\">");  
	            out.println("window.parent.CKEDITOR.tools.callFunction(" + callback  
	                    + ",''," + "'文件大小不得大于600k');");  
	            out.println("</script>");  
	        }  
	  
	        InputStream is = new FileInputStream(upload.getFile());  
	        //图片上传路径  
	        String fileName = java.util.UUID.randomUUID().toString(); // 采用时间+UUID的方式随即命名  
	        fileName += expandedName;  
	        File file = new File(path);  
	        if (!file.exists()) { // 如果路径不存在，创建  
	            file.mkdirs();  
	        }  
	        File toFile = new File(path, fileName);  
	        OutputStream os = new FileOutputStream(toFile);  
	        byte[] buffer = new byte[1024];  
	        int length = 0;  
	        while ((length = is.read(buffer)) > 0) {  
	            os.write(buffer, 0, length);  
	        }  
	        try {
				if(path != null){
					File file2 = new File(path+"\\"+fileName);
					InputStream input = new FileInputStream(file2);
					String key = "html/upload/"+fileName;
					AliyunOSSUtil.putImageObject(key, input , file2.length(), uploadContentType, null);
					file2.delete();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	        is.close();  
	        os.close();  
	  
	        // 返回"图像"选项卡并显示图片  request.getContextPath()为web项目名   
			renderHtml("<script>window.parent.CKEDITOR.tools.callFunction(" + callback   + ",'http://image.song-1.com/html/upload/" + fileName + "','')</script>");
	    }
	public void add(){
		NewsModel news = getModel(NewsModel.class, "news");
		Date date = new Date();
		news.set("create_time", date);
		String dateStr = DateTools.getDate(date);
		String[] dataStrs = dateStr.split("-");
		news.set("month",NumToChineseTool.getChinese(dataStrs[1]));
		news.set("day",dataStrs[2]);		
		if (news.save())
		{
			setAttr(RESULT, true);
			setAttr(MESSAGE, "新增成功！");
			setAttr("id", news.get("id"));
		}
		else
		{
			setAttr(RESULT, false);
			setAttr(MESSAGE, "新增失败！");
		}
		renderJson();
	}
	public void update(){
		NewsModel news = getModel(NewsModel.class, "news");
		if(news.get("id") == null){
			setAttr(RESULT, false);
			setAttr(MESSAGE, "更新失败！");
			renderJson();
			return;
		}
		news.set("update_time", new Date());
		news.update();
		setAttr(RESULT, true);
		setAttr(MESSAGE, "更新成功！");
		renderJson();
	}
	public void del()
	{
		Integer id = getParaToInt("id");
		if (id != null && NewsModel.dao.deleteById(id))
		{
			setAttr(RESULT, true);
			setAttr(MESSAGE, "删除成功！");
		}
		else
		{
			setAttr(RESULT, false);
			setAttr(MESSAGE, "删除失败！");
		}
		renderJson();
	}
	
	public void batchDel() {
		try {
			String ids = getPara("userIds");
			String[] idss = ids.split("\\|");
			for (String id : idss) {
				Integer idd = Integer.parseInt(id);
				NewsModel.dao.deleteById(idd);
			}
		} catch (Exception e) {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "删除失败！");
			renderJson();
		}
		setAttr(RESULT, true);
		setAttr(MESSAGE, "删除成功！");
		renderJson();
	}
   public void preview(){
	   try{
	   TemplateToHtml.dealNewsPageHtml(getRequest());
	   }catch(Exception e){
		   setAttr(RESULT, false);
		   setAttr(MESSAGE, "创建失败！");
		   renderJson();
		   return ;
	   }
	   setAttr(RESULT, true);
	   setAttr(MESSAGE, "创建成功！");
	   renderJson();
   }
   public void detail(){
	   Integer id = getParaToInt("id");
	   NewsModel news = NewsModel.dao.findById(id);
	   setAttr("detail", news.get("content"));
	   render("base/news_detail");
   }
   public void cover(){
		try {
			TemplateToHtml.dealNewsPageHtml(getRequest()); 
			String tempHtml = SysConstants.HTML_TEMP_FOLDER + File.separator+ "news" + File.separator + "index.html";
			String staticHtml = SysConstants.HTML_STATIC_FOLDER+ File.separator + "news" + File.separator + "index.html";
			TemplateToHtml.cover(tempHtml, staticHtml);
		} catch (Exception e) {
			setAttr(RESULT, false);
			setAttr(MESSAGE, "覆盖失败！");
			renderJson();
			return;
		}
		setAttr(RESULT, true);
		setAttr(MESSAGE, "覆盖成功！");
		renderJson();
	}
}
