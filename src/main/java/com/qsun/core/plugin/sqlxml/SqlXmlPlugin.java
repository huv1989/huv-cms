package com.qsun.core.plugin.sqlxml;

import com.jfinal.plugin.IPlugin;
import com.qsun.core.kit.SqlXmlKit;

public class SqlXmlPlugin implements IPlugin
{

	public SqlXmlPlugin()
	{
	}

	@Override
	public boolean start()
	{
		SqlXmlKit.init();
		return true;
	}

	@Override
	public boolean stop()
	{
		SqlXmlKit.destory();
		return true;
	}

}
