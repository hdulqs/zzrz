package com.zhiwei.credit.service.creditFlow.leaseFinance.supplior;
/*
 *  北京互融时代软件有限公司   -- http://www.hurongtime.com
 *	Copyright @ 2004 - 2010 Yuseen.com all rights reserved.京ICP备 05007290 号
*/
import com.zhiwei.core.service.BaseService;
import com.zhiwei.credit.model.creditFlow.leaseFinance.supplior.FlObjectSupplior;

/**
 * 
 * @author 
 *
 */
public interface FlObjectSuppliorService extends BaseService<FlObjectSupplior>{
	public void updateSuppliorByObjectId(Long suppliorId,FlObjectSupplior flObjectSupplior);
}


