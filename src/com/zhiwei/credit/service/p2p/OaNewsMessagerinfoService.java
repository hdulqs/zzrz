package com.zhiwei.credit.service.p2p;
/*
 *  北京互融时代软件有限公司   -- http://www.zhiweitime.com
 *	Copyright @ 2004 - 2010 Yuseen.com all rights reserved.京ICP备 05007290 号
*/
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.zhiwei.core.service.BaseService;
import com.zhiwei.core.web.paging.PagingBean;
import com.zhiwei.credit.model.p2p.OaNewsMessagerinfo;

/**
 * 
 * @author 
 *
 */
public interface OaNewsMessagerinfoService extends BaseService<OaNewsMessagerinfo>{

	public List<OaNewsMessagerinfo> getAllInfo(PagingBean pb,
			HttpServletRequest request);
	
}


