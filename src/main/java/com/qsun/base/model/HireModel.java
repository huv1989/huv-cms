package com.qsun.base.model;

import java.util.LinkedList;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.qsun.base.common.Pager;
import com.qsun.core.kit.SqlXmlKit;
import com.qsun.core.plugin.annotation.Table;

/**
 * @author huwei
 * @date 2015年11月25日
 *
 */
@SuppressWarnings("serial")
@Table(tableName = "hire")
public class HireModel extends Model<HireModel>{
public static final HireModel dao = new HireModel();
	

	public Page<HireModel> page(Pager pager)
	{
		LinkedList<Object> param = new LinkedList<Object>();
		Page<HireModel> page = dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select  *  ",SqlXmlKit.getSql("Hire.pager", pager.getParamsMap(), param), param.toArray());
		return page;
	}
}
