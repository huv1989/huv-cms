package com.qsun.base.model;

import java.util.LinkedList;
import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.qsun.base.common.Pager;
import com.qsun.core.kit.SqlXmlKit;
import com.qsun.core.plugin.annotation.Table;

/**
 * @author huwei
 * @date 2015年11月23日
 *
 *DROP TABLE IF EXISTS `news`;
  CREATE TABLE `news` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `summary` varchar(2000) DEFAULT NULL COMMENT '摘要',
  `content` text COMMENT '详细介绍',
  `editor` varchar(255) DEFAULT '未知' COMMENT '编辑人员',
  `category` int(11) DEFAULT '1' COMMENT '分类（1微赢新闻  2行业动态）',
  `source` varchar(255) DEFAULT NULL COMMENT '来源',
  `enable` int(11) DEFAULT NULL COMMENT '是否启用（1启用 2停用）',
  `sort` int(11) DEFAULT NULL COMMENT '编号、排序',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新闻';

 */
@SuppressWarnings("serial")
@Table(tableName = "news")
public class NewsModel extends Model<NewsModel> {
	
	public static final NewsModel dao = new NewsModel();
	
	public Page<NewsModel> page(Pager pager)
	{
		LinkedList<Object> param = new LinkedList<Object>();
		Page<NewsModel> page = dao.paginate(pager.getPageNo(), pager.getPageSize(),
				" select  *  ",SqlXmlKit.getSql("News.pager", pager.getParamsMap(), param), param.toArray());
		return page;
	}
}
