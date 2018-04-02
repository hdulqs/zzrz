package com.zhiwei.credit.dao.creditFlow.finance;
/*
 *  北京互融时代软件有限公司   -- http://www.hurongtime.com
 *	Copyright @ 2004 - 2010 Yuseen.com all rights reserved.京ICP备 05007290 号
*/
import java.util.List;
import java.util.Map;

import com.zhiwei.core.dao.BaseDao;
import com.zhiwei.credit.model.creditFlow.finance.VPunishDetail;

/**
 * 
 * @author 
 *
 */
public interface VPunishDetailDao extends BaseDao<VPunishDetail>{
	public List<VPunishDetail> wslistbyPunish(String businessType,Long projectId,String factDate);

	public List<VPunishDetail> search(Map<String, String> map);

	public int searchsize(Map<String, String> map);
}