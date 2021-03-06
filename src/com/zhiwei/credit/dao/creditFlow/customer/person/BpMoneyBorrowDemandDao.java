package com.zhiwei.credit.dao.creditFlow.customer.person;
/*
 *  北京金智万维软件有限公司   -- http://www.credit-software.com
 *	Copyright @ 2004 - 2010 Yuseen.com all rights reserved.京ICP备 05007290 号
*/
import java.util.List;

import com.zhiwei.core.dao.BaseDao;
import com.zhiwei.credit.model.creditFlow.customer.person.BpMoneyBorrowDemand;

/**
 * 
 * @author 
 *
 */
public interface BpMoneyBorrowDemandDao extends BaseDao<BpMoneyBorrowDemand>{
	public List<BpMoneyBorrowDemand> getMessageByProjectID(Long projectID);
	public BpMoneyBorrowDemand load(int id);
	public BpMoneyBorrowDemand getByProjectId(Long projectId);
}