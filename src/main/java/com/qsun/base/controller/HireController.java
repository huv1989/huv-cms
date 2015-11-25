package com.qsun.base.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import com.jfinal.kit.JsonKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.qsun.base.common.BaseController;
import com.qsun.base.common.Pager;
import com.qsun.base.model.HireModel;
import com.qsun.base.model.NewsModel;
import com.qsun.base.model.SysRolesModel;
import com.qsun.base.model.SysUserModel;
import com.qsun.core.plugin.annotation.Control;
import com.qsun.tools.SysConstants;
import com.qsun.tools.TemplateToHtml;

/**
 * @author huwei
 * @date 2015年11月25日
 *
 */
@Control(controllerKey = "/hire")
public class HireController extends BaseController{
	@SuppressWarnings("unused")
	private static final Logger	LOG		= Logger.getLogger(HireModel.class);
	public void index()
	{
		setAttr("rolesJson", JsonKit.toJson(SysRolesModel.dao.allRoles()));
		render("base/hire_mgr");
	}
	public void list()
	{
		SysUserModel user = getUser();
		Pager pager = createPager();

		pager.addParam("status", getPara("status"));
		pager.addParam("role", user.get("roleID"));

		Page<?> page = HireModel.dao.page(pager);

		setAttr("total", page.getTotalRow());
		setAttr("rows", page.getList());
		renderJson();
	}
	public void add(){
		HireModel hire = getModel(HireModel.class, "hire");
		Date date = new Date();
		hire.set("create_time", date);
		if (hire.save())
		{
			setAttr(RESULT, true);
			setAttr(MESSAGE, "新增成功！");
			setAttr("id", hire.get("id"));
		}
		else
		{
			setAttr(RESULT, false);
			setAttr(MESSAGE, "新增失败！");
		}
		renderJson();
	}
	public void update(){
		HireModel hire = getModel(HireModel.class, "hire");
		if(hire.get("id") == null){
			setAttr(RESULT, false);
			setAttr(MESSAGE, "更新失败！");
			renderJson();
			return;
		}
		hire.set("update_time", new Date());
		hire.update();
		setAttr(RESULT, true);
		setAttr(MESSAGE, "更新成功！");
		renderJson();
	}
	public void del()
	{
		Integer id = getParaToInt("id");
		if (id != null && HireModel.dao.deleteById(id))
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
				HireModel.dao.deleteById(idd);
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
		   TemplateToHtml.dealHirePageHtml(getRequest());
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
	 public void cover(){
			try {
				TemplateToHtml.dealHirePageHtml(getRequest()); 
				String tempHtml = SysConstants.HTML_TEMP_FOLDER  + File.separator + "jobs.html";
				String staticHtml = SysConstants.HTML_STATIC_FOLDER+ File.separator + "jobs.html";
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
