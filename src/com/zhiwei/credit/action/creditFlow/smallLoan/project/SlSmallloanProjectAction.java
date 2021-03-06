package com.zhiwei.credit.action.creditFlow.smallLoan.project;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.jbpm.api.TaskService;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.task.TaskImpl;
import org.springframework.beans.BeanUtils;

import com.credit.proj.entity.VProcreditDictionary;
import com.credit.proj.mortgage.morservice.service.MortgageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hurong.core.util.UUIDGenerator;
import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.parser.JSONParser;
import com.webServices.custom.BaseCustomService;
import com.zhiwei.core.Constants;
import com.zhiwei.core.command.QueryFilter;
import com.zhiwei.core.log.LogResource;
import com.zhiwei.core.util.AppUtil;
import com.zhiwei.core.util.BeanUtil;
import com.zhiwei.core.util.ContextUtil;
import com.zhiwei.core.util.DateUtil;
import com.zhiwei.core.util.GroupUtil;
import com.zhiwei.core.util.JsonUtil;
import com.zhiwei.core.util.StringUtil;
import com.zhiwei.core.web.action.BaseAction;
import com.zhiwei.core.web.paging.PageBean;
import com.zhiwei.core.web.paging.PagingBean;
import com.zhiwei.credit.core.commons.CreditBaseDao;
import com.zhiwei.credit.core.project.StatsPro;
import com.zhiwei.credit.dao.system.CsHolidayDao;
import com.zhiwei.credit.model.agentAproval.BankCredit;
import com.zhiwei.credit.model.creditFlow.archives.PlArchivesMaterials;
import com.zhiwei.credit.model.creditFlow.archives.PlProjectArchives;
import com.zhiwei.credit.model.creditFlow.common.GlobalSupervisemanage;
import com.zhiwei.credit.model.creditFlow.creditAssignment.bank.ObAccountDealInfo;
import com.zhiwei.credit.model.creditFlow.creditAssignment.bank.ObSystemAccount;
import com.zhiwei.credit.model.creditFlow.customer.bankRelationPerson.CustomerBankRelationPerson;
import com.zhiwei.credit.model.creditFlow.customer.common.EnterpriseBank;
import com.zhiwei.credit.model.creditFlow.customer.enterprise.Enterprise;
import com.zhiwei.credit.model.creditFlow.customer.enterprise.EnterpriseShareequity;
import com.zhiwei.credit.model.creditFlow.customer.person.BpMoneyBorrowDemand;
import com.zhiwei.credit.model.creditFlow.customer.person.CsPersonCar;
import com.zhiwei.credit.model.creditFlow.customer.person.CsPersonHouse;
import com.zhiwei.credit.model.creditFlow.customer.person.Person;
import com.zhiwei.credit.model.creditFlow.customer.person.Spouse;
import com.zhiwei.credit.model.creditFlow.customer.person.workcompany.WorkCompany;
import com.zhiwei.credit.model.creditFlow.fileUploads.FileForm;
import com.zhiwei.credit.model.creditFlow.finance.SlActualToCharge;
import com.zhiwei.credit.model.creditFlow.finance.SlFundIntent;
import com.zhiwei.credit.model.creditFlow.finance.SlPlansToCharge;
import com.zhiwei.credit.model.creditFlow.finance.VFundDetail;
import com.zhiwei.credit.model.creditFlow.financeProject.FlFinancingProject;
import com.zhiwei.credit.model.creditFlow.financingAgency.PlBidInfo;
import com.zhiwei.credit.model.creditFlow.financingAgency.PlBidPlan;
import com.zhiwei.credit.model.creditFlow.financingAgency.persion.BpPersionDirPro;
import com.zhiwei.credit.model.creditFlow.fund.project.BpFundProject;
import com.zhiwei.credit.model.creditFlow.guarantee.project.GLGuaranteeloanProject;
import com.zhiwei.credit.model.creditFlow.leaseFinance.project.FlLeaseFinanceProject;
import com.zhiwei.credit.model.creditFlow.materials.SlProcreditMaterials;
import com.zhiwei.credit.model.creditFlow.multiLevelDic.AreaDic;
import com.zhiwei.credit.model.creditFlow.ourmain.SlCompanyMain;
import com.zhiwei.credit.model.creditFlow.ourmain.SlPersonMain;
import com.zhiwei.credit.model.creditFlow.pawn.project.PlPawnProject;
import com.zhiwei.credit.model.creditFlow.repaymentSource.SlRepaymentSource;
import com.zhiwei.credit.model.creditFlow.smallLoan.finance.BorrowerInfo;
import com.zhiwei.credit.model.creditFlow.smallLoan.finance.SlAlterAccrualRecord;
import com.zhiwei.credit.model.creditFlow.smallLoan.finance.SlEarlyRepaymentRecord;
import com.zhiwei.credit.model.creditFlow.smallLoan.meeting.SlConferenceRecord;
import com.zhiwei.credit.model.creditFlow.smallLoan.project.ProjectPropertyClassification;
import com.zhiwei.credit.model.creditFlow.smallLoan.project.SlSmallloanProject;
import com.zhiwei.credit.model.creditFlow.smallLoan.project.VSmallloanProject;
import com.zhiwei.credit.model.creditFlow.smallLoan.supervise.SlSuperviseRecord;
import com.zhiwei.credit.model.customer.BpCustRelation;
import com.zhiwei.credit.model.customer.InvestEnterprise;
import com.zhiwei.credit.model.customer.InvestPersonInfo;
import com.zhiwei.credit.model.entityhbm.CashDeposit;
import com.zhiwei.credit.model.flow.ProcessForm;
import com.zhiwei.credit.model.flow.ProcessRun;
import com.zhiwei.credit.model.flow.TaskSignData;
import com.zhiwei.credit.model.p2p.BpCustMember;
import com.zhiwei.credit.model.p2p.loan.P2pLoanProduct;
import com.zhiwei.credit.model.pay.BpBidLoan;
import com.zhiwei.credit.model.pay.BpMoneyManager;
import com.zhiwei.credit.model.pay.MadaiLoanInfoBean;
import com.zhiwei.credit.model.pay.MoneyMoreMore;
import com.zhiwei.credit.model.pay.ResultLoanBean;
import com.zhiwei.credit.model.system.AppUser;
import com.zhiwei.credit.model.system.CsHoliday;
import com.zhiwei.credit.model.system.Dictionary;
import com.zhiwei.credit.model.system.DictionaryIndependent;
import com.zhiwei.credit.model.system.GlobalType;
import com.zhiwei.credit.model.system.product.BpProductParameter;
import com.zhiwei.credit.model.thirdInterface.PlThirdInterfaceLog;
import com.zhiwei.credit.service.agentAproval.BankCreditService;
import com.zhiwei.credit.service.agentAproval.BankCreditUserLogService;
import com.zhiwei.credit.service.creditFlow.archives.PlArchivesMaterialsService;
import com.zhiwei.credit.service.creditFlow.archives.PlProjectArchivesService;
import com.zhiwei.credit.service.creditFlow.common.CreditProjectService;
import com.zhiwei.credit.service.creditFlow.common.GlobalSupervisemanageService;
import com.zhiwei.credit.service.creditFlow.creditAssignment.bank.ObAccountDealInfoService;
import com.zhiwei.credit.service.creditFlow.customer.common.EnterpriseBankService;
import com.zhiwei.credit.service.creditFlow.customer.enterprise.EnterpriseService;
import com.zhiwei.credit.service.creditFlow.customer.person.BpMoneyBorrowDemandService;
import com.zhiwei.credit.service.creditFlow.customer.person.CsPersonCarService;
import com.zhiwei.credit.service.creditFlow.customer.person.CsPersonHouseService;
import com.zhiwei.credit.service.creditFlow.customer.person.PersonService;
import com.zhiwei.credit.service.creditFlow.customer.person.SpouseService;
import com.zhiwei.credit.service.creditFlow.customer.person.workcompany.WorkCompanyService;
import com.zhiwei.credit.service.creditFlow.fileUploads.FileFormService;
import com.zhiwei.credit.service.creditFlow.finance.BpFundIntentService;
import com.zhiwei.credit.service.creditFlow.finance.FundIntentService;
import com.zhiwei.credit.service.creditFlow.finance.SlActualToChargeService;
import com.zhiwei.credit.service.creditFlow.finance.SlFundIntentService;
import com.zhiwei.credit.service.creditFlow.finance.SlPlansToChargeService;
import com.zhiwei.credit.service.creditFlow.finance.VFundDetailService;
import com.zhiwei.credit.service.creditFlow.financeProject.FlFinancingProjectService;
import com.zhiwei.credit.service.creditFlow.financingAgency.PlBidInfoService;
import com.zhiwei.credit.service.creditFlow.financingAgency.PlBidPlanService;
import com.zhiwei.credit.service.creditFlow.financingAgency.persion.BpPersionDirProService;
import com.zhiwei.credit.service.creditFlow.fund.project.BpFundProjectService;
import com.zhiwei.credit.service.creditFlow.guarantee.project.GLGuaranteeloanProjectService;
import com.zhiwei.credit.service.creditFlow.leaseFinance.project.FlLeaseFinanceProjectService;
import com.zhiwei.credit.service.creditFlow.materials.SlProcreditMaterialsService;
import com.zhiwei.credit.service.creditFlow.multiLevelDic.AreaDicService;
import com.zhiwei.credit.service.creditFlow.ourmain.SlCompanyMainService;
import com.zhiwei.credit.service.creditFlow.ourmain.SlPersonMainService;
import com.zhiwei.credit.service.creditFlow.pawn.project.PlPawnProjectService;
import com.zhiwei.credit.service.creditFlow.repaymentSource.SlRepaymentSourceService;
import com.zhiwei.credit.service.creditFlow.smallLoan.finance.BorrowerInfoService;
import com.zhiwei.credit.service.creditFlow.smallLoan.finance.SlAlterAccrualRecordService;
import com.zhiwei.credit.service.creditFlow.smallLoan.finance.SlEarlyRepaymentRecordService;
import com.zhiwei.credit.service.creditFlow.smallLoan.meeting.SlConferenceRecordService;
import com.zhiwei.credit.service.creditFlow.smallLoan.project.SlSmallloanProjectService;
import com.zhiwei.credit.service.creditFlow.smallLoan.project.VSmallloanProjectService;
import com.zhiwei.credit.service.creditFlow.smallLoan.supervise.SlSuperviseRecordService;
import com.zhiwei.credit.service.customer.BpCustRelationService;
import com.zhiwei.credit.service.customer.InvestEnterpriseService;
import com.zhiwei.credit.service.customer.InvestPersonInfoService;
import com.zhiwei.credit.service.entityhbm.CashDepositService;
import com.zhiwei.credit.service.flow.JbpmService;
import com.zhiwei.credit.service.flow.ProDefinitionService;
import com.zhiwei.credit.service.flow.ProcessFormService;
import com.zhiwei.credit.service.flow.ProcessRunService;
import com.zhiwei.credit.service.flow.TaskSignDataService;
import com.zhiwei.credit.service.p2p.BpCustMemberService;
import com.zhiwei.credit.service.p2p.loan.P2pLoanProductService;
import com.zhiwei.credit.service.pay.BpMoneyManagerService;
import com.zhiwei.credit.service.pay.IPayService;
import com.zhiwei.credit.service.system.AppUserService;
import com.zhiwei.credit.service.system.DictionaryIndependentService;
import com.zhiwei.credit.service.system.DictionaryService;
import com.zhiwei.credit.service.system.GlobalTypeService;
import com.zhiwei.credit.service.system.OrganizationService;
import com.zhiwei.credit.service.system.product.BpProductParameterService;
import com.zhiwei.credit.service.thirdInterface.PlThirdInterfaceLogService;
import com.zhiwei.credit.util.Common;
import com.zhiwei.credit.util.HibernateProxyTypeAdapter;
import com.zhiwei.credit.util.ProjectActionUtil;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;


@SuppressWarnings( { "static-access", "unchecked", "unused" })
public class SlSmallloanProjectAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private static final String String = null;
	@Resource
	private ObAccountDealInfoService obAccountDealInfoService;
	@Resource
    private BpCustRelationService bpCustRelationService;
	@Resource
	private FlFinancingProjectService flFinancingProjectService;
	@Resource
	private CreditProjectService creditProjectService;
	@Resource
	private AppUserService appUserService;
	@Resource
	private PersonService personService;
	@Resource
	private EnterpriseBankService enterpriseBankService;
	@Resource
	private SpouseService spouseService;
	@Resource
	private ProDefinitionService proDefinitionService;
	@Resource
	private DictionaryIndependentService dictionaryIndependentService;
	@Resource
	private AreaDicService areaDicService;
	@Resource
	private SlCompanyMainService slCompanyMainService;
	@Resource
	private BpMoneyManagerService bpMoneyManagerService;
	@Resource
	private SlPersonMainService slPersonMainService;
	@Resource
	private SlSmallloanProjectService slSmallloanProjectService; // 小额贷款
	@Resource
	private GLGuaranteeloanProjectService glGuaranteeloanProjectService; // 企业贷款
	@Resource
	private SlSuperviseRecordService superviseRecordService;
	@Resource
	private ProcessFormService processFormService;
	@Resource
	private SlActualToChargeService slActualToChargeService;
	@Resource
	private SlPlansToChargeService slPlansToChargeService;
	@Resource
	private SlProcreditMaterialsService slProcreditMaterialsService;
	@Resource
	private TaskService taskService;
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private TaskSignDataService taskSignDataService;
	@Resource
	private SlFundIntentService slFundIntentService;
	@Resource
	private JbpmService jbpmService;
	@Resource
	private IPayService iPayService;
	@Resource
	private InvestPersonInfoService investPersonInfoService;
	@Resource
	private BpCustMemberService bpCustMemberService;
	@Resource
	private VSmallloanProjectService vSmallloanProjectService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private EnterpriseService enterpriseService;
	@Resource
	private PersonService personServcie;
	@Resource
	private SlEarlyRepaymentRecordService slEarlyRepaymentRecordService;
	@Resource
	private SlAlterAccrualRecordService slAlterAccrualRecordService;
	@Resource
	private PlBidInfoService plBidInfoService;
	@Resource
	private CreditBaseDao creditBaseDao;
	@Resource
	private BaseCustomService baseCustomService;
	@Resource
	private PlArchivesMaterialsService plArchivesMaterialsService;
	@Resource
	private MortgageService mortgageService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private SlSuperviseRecordService slSuperviseRecordService;
	@Resource
	private VFundDetailService vFundDetailService;
	@Resource
	private PlPawnProjectService plPawnProjectService;
	@Resource
	private FlLeaseFinanceProjectService flLeaseFinanceProjectService;
	@Resource
	private GlobalSupervisemanageService globalSupervisemanageService;
	@Resource
	private WorkCompanyService workCompanyService;
	@Resource
	private BpMoneyBorrowDemandService bpMoneyBorrowDemandService;
	@Resource
	private BpProductParameterService bpProductParameterService;
	@Resource
	private BpPersionDirProService bpPersionDirProService;
	@Resource
	private CsPersonCarService csPersonCarService;
	@Resource
	private CsPersonHouseService csPersonHouseService;
	@Resource
	private InvestEnterpriseService investEnterpriseService;
	@Resource
	private FundIntentService fundIntentService;
	@Resource
	private PlThirdInterfaceLogService plThirdInterfaceLogService;
	@Resource
	private PlBidPlanService plBidPlanService;
	@Resource
	private BpFundIntentService bpFundIntentService;
	@Resource
	private BpMoneyBorrowDemandService bpMoneyBorrowDemandServie;
	@Resource
	private BpFundProjectService bpFundProjectService;
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private BorrowerInfoService borrowerInfoService;
	@Resource
	private SlRepaymentSourceService slRepaymentSourceService;
	@Resource
	private SlConferenceRecordService slConferenceRecordService;
	@Resource
	private P2pLoanProductService p2pLoanProductService;
	@Resource
	private CsHolidayDao csHolidayDao;
	@Resource
	private PlProjectArchivesService plProjectArchivesService;
	@Resource
	private FileFormService fileFormService;
	
	//得到整个系统全部的config.properties读取的所有资源
	private static Map configMap = AppUtil.getConfigMap();
	
	private Long runId;
	private boolean isShow = true;
	private String taskId;
	private String businessType;
	private SlSmallloanProject slSmallloanProject;
	private Person person;
	private Enterprise enterprise;
	private SlSuperviseRecord slSuperviseRecord;
	private SlEarlyRepaymentRecord slEarlyRepaymentRecord;
	private SlAlterAccrualRecord slAlterAccrualRecord;
	private EnterpriseShareequity enterpriseShareequity;
	private GLGuaranteeloanProject gLGuaranteeloanProject;
	private CustomerBankRelationPerson customerBankRelationPerson;
	private PlBidPlan plBidPlan;//招标计划
	private long operationType;
	private Date startTime;
	private Date endTime;
	private long departmentId;
	private short projectStatus;
	private String userName;
	private String state;
	private Boolean isGrantedShowAllProjects;// 是否授权查看所有项目信息
	private EnterpriseBank enterpriseBank;
	private Spouse spouse;
	private ProjectPropertyClassification projectPropertyClassification;
	private SlConferenceRecord slConferenceRecord;
	private Long superviseManageId;
	private GlobalSupervisemanage globalSupervisemanage;

	private BpMoneyBorrowDemand bpMoneyBorrowDemand;// 借款需求
	private String personHouseDate;// 房产信息
	private String personCarData;// 车产信息
	private String slActualData;// 手续费用信息
	private WorkCompany workCompany;// 公司信息

	private BpFundProject ownBpFundProject;// 自有资金
	private BpFundProject platFormBpFundProject;// 平台资金
	
	private PlProjectArchives plProjectArchives;
	
	
	public  String customerAna (){
		List<SlSmallloanProject>  sl=slSmallloanProjectService.customerStatistical();
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
		.append(sl.size()).append(",result:");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd")
		/* .excludeFieldsWithoutExposeAnnotation() */.create();
		buff.append(gson.toJson(sl));
		buff.append("}");
		jsonString = buff.toString();
	  return SUCCESS;
	}
	
	
	public String get() {
		SlSmallloanProject ssp = slSmallloanProjectService.get(projectId);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		// 将数据转成JSON格式
		/*StringBuffer sb = new StringBuffer("{success:true,data:");
		sb.append(gson.toJson(ssp));
		sb.append("}");
		setJsonString(sb.toString());*/
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("slSmallloanproject", ssp);
		StringBuffer buff = new StringBuffer("{success:true,data:");
		JSONSerializer json = JsonUtil.getJSONSerializerDateByFormate("yyyy-MM-dd");
		buff.append(json.serialize(map));
		buff.append("}");
		jsonString = buff.toString();
		return SUCCESS;
	}


	/**
	 * 启动结清流程
	 * 
	 * @return
	 */
	public String StartRbjrSettlementFlow() {
		String  fundId=getRequest().getParameter("fundId");
		if(fundId!=null&&!"".equals(fundId)&&fundId!="undefined"){
			BpFundProject  bp=bpFundProjectService.get(Long.valueOf(fundId));
			bp.setIsOtherFlow(Short.valueOf("4"));
			// 完成贷款的查询
			bp.setProjectStatus(Short.valueOf("2"));
			bpFundProjectService.merge(bp);
		}
		String  processType=getRequest().getParameter("processType");
		if(projectId!=null){
		String str=slSmallloanProjectService.stateRbjrSettlementFlow(projectId,processType);
		jsonString="{success:true,"+str+"}";
		}
		return SUCCESS;
	}
	
	/**
	 * 匹配成功后启动手续办理流程
	 * 
	 * @return
	 */
	public String startGoThroughFormalitiesFlow() {
		// creditProjectService.startGoThroughFormalitiesFlow(this.getRequest());//SmallLoanProject
		creditProjectService.startFormalitiesFlow(this.getRequest());
		return SUCCESS;
	}

	/***
	 * 转为匹配失败项目
	 * 
	 * @return
	 */
	public String tofail() {
		if (projectId != null) {
			SlSmallloanProject sl = slSmallloanProjectService.get(projectId);
			sl.setProjectStatus(Short.valueOf("3"));
			slSmallloanProjectService.save(sl);

		}
		return SUCCESS;
	}

	/***
	 * 查询匹配成功项目
	 * 
	 * @return
	 */
	public String findList() {
		QueryFilter filter = new QueryFilter(getRequest());
		// 获取查询面板的查询条件值
		int start = filter.getPagingBean().getStart();
		int limit = filter.getPagingBean().getPageSize();
		String projectName = getRequest().getParameter("projectName");
		String projectNumber = filter.getRequest().getParameter(
				"Q_projNum_S_LK");
		String minMoneyStr = filter.getRequest().getParameter(
				"Q_incomemoney_D_GE");
		String maxMoneyStr = filter.getRequest().getParameter(
				"Q_incomemoney_D_LE");
		String projectStatus = filter.getRequest()
				.getParameter("projectStatus");
		PagingBean pb = new PagingBean(start, limit);
		List<SlSmallloanProject> slSmallloanProjects = slSmallloanProjectService
				.findList(projectName, projectNumber, minMoneyStr, maxMoneyStr,
						projectStatus, pb);
		for (SlSmallloanProject s : slSmallloanProjects) {
			s.setSlFundIntents(null);
		}
		int counts = slSmallloanProjects.size();
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(counts).append(",result:");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd")
		/* .excludeFieldsWithoutExposeAnnotation() */.create();
		buff.append(gson.toJson(slSmallloanProjects));
		buff.append("}");
		jsonString = buff.toString();
		return SUCCESS;
	}

	/**
	 * 信贷流程-项目申请表保存方法
	 * 
	 * @return
	 */
	public String updateCreditFlowInfo() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			personHouseDate = this.getRequest().getParameter("personHouseData");
			personCarData = this.getRequest().getParameter("personCarData");
			slActualData = this.getRequest().getParameter(
					"slActualToChargeData");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");

			String projectId = this.getRequest().getParameter(
					"slSmallloanProject.projectId");
			SlSmallloanProject sl = slSmallloanProjectService.get(Long
					.valueOf(projectId));
			Short flag1 = 1;
			if(sl.getOppositeType().equals("company_customer"))flag1=0;
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(
							sl.getOppositeID().intValue(), flag1, Short
							.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(sl.getOppositeID()
							.intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(flag1);
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
					slSmallloanProject.setBankId(enterpriseBank.getId().longValue());
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					bank.setName(enterpriseBank.getName());
					bank.setOpenType(enterpriseBank.getOpenType());
					bank.setAccountnum(enterpriseBank.getAccountnum());
					bank.setAccountType(enterpriseBank.getAccountType());
					bank.setBankid(enterpriseBank.getBankid());
					bank.setBankname(enterpriseBank.getBankname());
					bank.setAreaId(enterpriseBank.getAreaId());
					bank.setAreaName(enterpriseBank.getAreaName());
					bank.setBankOutletsName(enterpriseBank.getBankOutletsName());
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			/*
			 * // 保存款项计划 slFundIntentService.savejson(fundIntentJsonData, new
			 * Long
			 * (projectId),"SmallLoan",Short.parseShort("1"),sl.getCompanyId());
			 */
			Integer flag = this.slSmallloanProjectService.updateCreditFlowInfo(
					slSmallloanProject, person, personHouseDate, personCarData,
					sb, bpMoneyBorrowDemand, workCompany, slActualData);
			if(spouse!=null&&spouse.getPersonId()!=null){
				spouseService.save(spouse);
			}else if(spouse!=null){
				spouse.setPersonId(person.getId());
				spouseService.save(spouse);
			}
			if (flag != 0) {
				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("填写项目申请表节点-保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}

	public String findMoney() {
		SlSmallloanProject slSmallloanProject = slSmallloanProjectService
				.get(projectId);
		Double money = slSmallloanProject.getProjectMoney().doubleValue();
		jsonString = money.toString();
		return SUCCESS;
	}

	/***
	 * 信贷流程-电审保存方法
	 */
	public String savePhoneCheckInfo() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String phoneCheckData1 = this.getRequest().getParameter(
					"phoneCheckData1");
			String phoneCheckData2 = this.getRequest().getParameter(
					"phoneCheckData2");
			String phoneCheckData3 = this.getRequest().getParameter(
					"phoneCheckData3");
			Map<String, String> map = new HashMap<String, String>();
			if (null != phoneCheckData1 && !"".equals(phoneCheckData1)) {
				map.put("phoneCheckData1", phoneCheckData1);
			}
			if (null != phoneCheckData2 && !"".equals(phoneCheckData2)) {
				map.put("phoneCheckData2", phoneCheckData2);
			}
			if (null != phoneCheckData3 && !"".equals(phoneCheckData3)) {
				map.put("phoneCheckData3", phoneCheckData3);
			}
			SlSmallloanProject project=slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			BeanUtil.copyNotNullProperties(project, slSmallloanProject);
			slSmallloanProjectService.merge(project);
			Integer flag = this.slSmallloanProjectService.updatePhoneCheckInfo(map);
			if (flag != 0) {
				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("信贷流程-电审保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}

	/***
	 * 信贷流程-网审保存方法
	 */
	public String saveNetCheckInfo() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String netCheckData = this.getRequest()
					.getParameter("netCheckData");
			Integer flag = this.slSmallloanProjectService
					.updateNetCheckInfo(netCheckData);
			if (flag != 0) {
				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("信贷流程-网审保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}

	/**
	 * 项目信息列表
	 * 
	 * @author lisl
	 */
	public String projectList() {
		String userIdsStr = "";
		PagingBean pb = new PagingBean(start, limit);
		String roleTypeStr = ContextUtil.getRoleTypeSession();
		if (!isGrantedShowAllProjects && !roleTypeStr.equals("control")) {// 如果用户不拥有查看所有项目信息的权限
			userIdsStr = appUserService.getUsersStr();// 当前登录用户以及其所有下属用户
		}
		Short[] projectStatuses = null;
		switch (projectStatus) {
		case 1:
			projectStatuses = new Short[] { Constants.PROJECT_STATUS_MIDDLE,
					Constants.PROJECT_POSTPONED_STATUS_ACT,
					Constants.PROJECT_POSTPONED_STATUS_REFUSE,
					Constants.PROJECT_POSTPONED_STATUS_PASS };
			break;
		case 0:
			projectStatuses = new Short[] { Constants.PROJECT_STATUS_ACTIVITY };
			break;
		case 2:
			projectStatuses = new Short[] { Constants.PROJECT_STATUS_COMPLETE };
			break;
		case 3:
			projectStatuses = new Short[] { Constants.PROJECT_STATUS_STOP };
			break;
		case 4:
			projectStatuses = new Short[] { Constants.PROJECT_POSTPONED_STATUS_ACT };
			break;
		case 5:
			projectStatuses = new Short[] { Constants.PROJECT_POSTPONED_STATUS_PASS };
			break;
		case 6:
			projectStatuses = new Short[] { Constants.PROJECT_POSTPONED_STATUS_REFUSE };
			break;
		case 7:
			projectStatuses = new Short[] {};
			break;
		case 8:
			projectStatuses = new Short[] { Constants.PROJECT_STATUS_BREAKACONTRACT };
			break;
		case 10:
			projectStatuses = new Short[] { Constants.PROJECT_STATUS_SUSPENDED };
			break;
		case 15:
			projectStatuses = new Short[] { Constants.PROJECT_STATUS_MIDDLE,
					Constants.PROJECT_POSTPONED_STATUS_PASS };
			break;
		}
		List<VSmallloanProject> list = vSmallloanProjectService.getProjectList(
				userIdsStr, projectStatuses, pb, getRequest());
		// int counts =
		// vSmallloanProjectService.getProjectList(userIdsStr,projectStatuses,
		// null, getRequest()).size();
		int ls = list.size();
		/*
		 * if (projectStatus == 1) { for (int i = 0; i < ls; i++) {
		 * SlSuperviseIn ssi = slSuperviseInService
		 * .getLatelySlSuperviseInByProjectId(list.get(i) .getProjectId()); if
		 * (ssi != null && ssi.getSuperviseOpinion() != null) {
		 * list.get(i).setSuperviseOpinionName( dictionaryService.get(
		 * Long.valueOf(ssi.getSuperviseOpinion())) .getItemValue());
		 * list.get(i).setSupervisionPersonName(
		 * ssi.getSupervisionPersonName()); list.get(i)
		 * .setSuperviseDateTime(ssi.getSuperviseDateTime()); } } }
		 */
		for (VSmallloanProject vp : list) {
			String appuers = "";
			if (null != vp.getBusinessManager()) {
				String[] appstr = vp.getBusinessManager().split(",");
				Set<AppUser> userSet = this.appUserService
						.getAppUserByStr(appstr);
				for (AppUser s : userSet) {
					appuers = appuers + s.getFamilyName() + ",";
				}
			}
			if (appuers.length() > 0) {
				appuers = appuers.substring(0, appuers.length() - 1);
			}
			vp.setBusinessManagerValue(appuers);

			// 获取当前所处任务的处理人
			String executor = "";// 任务执行人      
			if (null != vp.getActivityName()
					&& !vp.getActivityName().equals("null")) {
				String  taskId = vp.getTaskId();
				if(null!=taskId&&!"".equals(taskId)){
					//taskService.getSubTasks(arg0)
				}
				Set<AppUser> appExecutor = jbpmService.getNodeHandlerUsers(vp
						.getDefId(), vp.getActivityName());
				for (AppUser su : appExecutor) {
					if(su!=null){
						executor = executor + su.getFamilyName() + ",";
					}
				}
				if (executor.length() > 0) {
					executor = executor.substring(0, executor.length() - 1);
				}
				vp.setExecutor(executor);
				List<SlSuperviseRecord> slist = slSuperviseRecordService
						.getListByProjectId(vp.getProjectId(), vp
								.getBusinessType());// ◎
				if (null != slist && slist.size() > 0) {
					SlSuperviseRecord s = slist.get(0);
					if (null != s) {
						vp.setRepaymentDate(s.getEndDate());
					}
				} else {
					vp.setRepaymentDate(vp.getExpectedRepaymentDate());
				}
			}
		}

		Type type = new TypeToken<List<VSmallloanProject>>() {
		}.getType();
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(list == null ? 0 : pb.getTotalItems()).append(
						",result:");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd")
				.excludeFieldsWithoutExposeAnnotation().create();
		buff.append(gson.toJson(list, type));
		buff.append("}");
		jsonString = buff.toString();
		return SUCCESS;
	}

	public String getStatus() {

		SlSmallloanProject project = this.slSmallloanProjectService
				.get(projectId);
		StringBuffer sb = new StringBuffer("{success:true,data:");
		sb.append("[{\"status\":\"" + project.getProjectStatus() + "\"}]");
		sb.append("}");
		setJsonString(sb.toString());
		return SUCCESS;
	}

	public void startBreach() {

		String projectId = this.getRequest().getParameter("projectID_");
		String fundProjectId = this.getRequest().getParameter("fundProjectId");
		String listed = this.getRequest().getParameter("listed_");
		String comments = this.getRequest().getParameter("comments");// 违约说明
		/*SlSmallloanProject project = (SlSmallloanProject) this.slSmallloanProjectService
				.getProjectById(Long.valueOf(projectId)).get(0);*/
		BpFundProject project=bpFundProjectService.get(Long.valueOf(fundProjectId));
		boolean flag = this.slSmallloanProjectService.startBreach(project,
				listed, comments);
		StringBuffer buff = null;
		if (flag) {
			buff = new StringBuffer("{success:true,'totalCounts':");
		} else {

			buff = new StringBuffer("{success:false,'totalCounts':");
		}
		buff.append(0);
		buff.append("}");
		com.zhiwei.credit.core.creditUtils.JsonUtil.responseJsonString(buff
				.toString());
	}

	public String getOptions() {

		String projeId = this.getRequest().getParameter("projeId");
		String processName = this.getRequest().getParameter("processName");
		String businessType = this.getRequest().getParameter("businessType");
		String filterableNodeKeys = getRequest().getParameter(
				"filterableNodeKeys");
		projectId = Long.valueOf(projeId);
		ProcessRun pr = this.processRunService.getByProcessNameProjectId(
				projectId, businessType, processName).get(0);
		Long runIds = pr.getRunId();
		List<ProcessForm> pfs = processFormService.getCommentsByRunId(runIds,
				filterableNodeKeys);
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(pfs.size()).append(",result:");
		JSONSerializer json = JsonUtil.getJSONSerializer();
		buff.append(json.serialize(pfs));
		buff.append("}");
		jsonString = buff.toString();
		return SUCCESS;
	}

	public String getExecutiveOpinionInfo() {
		try {

			projectId = Long.valueOf(this.getRequest()
					.getParameter("projectId"));
			businessType = this.getRequest().getParameter("businessType");
			List<ProcessRun> runs = processRunService
					.getByProcessNameProjectId(projectId, businessType,
							"slDirectorOpinionFlow");
			Long runId = null;
			if (null != runs && runs.size() > 0) {
				runId = runs.get(0).getRunId();
			}
			ProcessForm processFormParent = this.processFormService
					.getByRunIdFormTskIdIsNull(runId); // 父taskId;
			String parentTaskId = "";
			if (null != processFormParent) {
				parentTaskId = processFormParent.getTaskId();
			}
			Task parentTask = jbpmService.getParentTask(parentTaskId);
			ProcessForm processForm = null;
			if (null != parentTask) {

				// 取得已经投票的会签情况
				List<TaskSignData> signDataList = taskSignDataService
						.getByTaskId(parentTask.getId());
				if (signDataList != null && signDataList.size() != 0) {
					for (int i = 0; i < signDataList.size(); i++) {
						TaskSignData taskSign = (TaskSignData) signDataList
								.get(i);
						processForm = this.processFormService
								.getProcessFormByRunIdAndActivityName(taskSign
										.getRunId(), taskSign.getVoteId());
						AppUser user = appUserService.get(taskSign.getVoteId());
						taskSign.setPosition(user.getPosNames());
						taskSign.setComments(processForm.getComments());
						taskSign.setCreateTime(processForm.getCreatetime());
						taskSign.setTaskLimitTime(processForm
								.getTaskLimitTime());
					}
				}
				List<String> unHandleUserList = jbpmService
						.getAssigneeByTaskId(parentTask.getId());
				for (String userId : unHandleUserList) {
					TaskSignData data = new TaskSignData();
					AppUser user = appUserService.get(new Long(userId));
					data.setPosition(user.getPosNames());
					data.setVoteName(user.getFullname());
					data.setCreateTime(processForm.getCreatetime());
					data.setTaskLimitTime(processForm.getTaskLimitTime());
					signDataList.add(data);
				}
				StringBuffer buff = new StringBuffer(
						"{success:true,'totalCounts':").append(
						signDataList.size()).append(",result:");
				Gson gson = new Gson();
				buff.append(gson.toJson(signDataList));
				buff.append("}");
				setJsonString(buff.toString());
			} else {
				List<TaskSignData> signDataList = taskSignDataService
						.getByRunId(runId);
				if (signDataList != null && signDataList.size() != 0) {
					for (int i = 0; i < signDataList.size(); i++) {
						TaskSignData taskSign = (TaskSignData) signDataList
								.get(i);
						processForm = this.processFormService
								.getProcessFormByRunIdAndActivityName(taskSign
										.getRunId(), taskSign.getVoteId());
						AppUser user = appUserService.get(taskSign.getVoteId());
						taskSign.setPosition(user.getPosNames());
						taskSign.setComments(processForm.getComments());
						taskSign.setCreateTime(processForm.getCreatetime());
						taskSign.setTaskLimitTime(processForm
								.getTaskLimitTime());
					}
				}
				StringBuffer buff = new StringBuffer(
						"{success:true,'totalCounts':").append(
						signDataList.size()).append(",result:");
				Gson gson = new Gson();
				buff.append(gson.toJson(signDataList));
				buff.append("}");
				setJsonString(buff.toString());

			}
			return SUCCESS;

		} catch (Exception e) {
			e.printStackTrace();
			return SUCCESS;
		}

	}

	public String askFor() { // 申请展期,启动流程

		try {
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"money_plan");
			Long proId = Long.valueOf(this.getRequest().getParameter(
					"projectId"));
			String fundIntentJsonData = this.getRequest().getParameter(
					"intent_plan");
			String fundIntentJsonDataSuperviseRecord = this.getRequest()
					.getParameter("intent_plan_SuperviseRecord");
			String SlActualToChargeJsonDataSuperviseRecord = this.getRequest()
					.getParameter("money_plan_SuperviseRecord");
			String isNoStart = this.getRequest().getParameter("isNoStart");
			boolean flag = false;
			if (null != isNoStart && isNoStart.equals("1")) {
				flag = true;
			}
			String categoryIds = this.getRequest().getParameter("categoryIds");
			slSmallloanProject = this.slSmallloanProjectService.get(proId);
			List<SlFundIntent> slFundIntentsSuperviseRecord = savejsonintent(
					fundIntentJsonDataSuperviseRecord, slSmallloanProject,
					Short.parseShort("1"));
			Short isvalid = 2;
			for (SlFundIntent s : slFundIntentsSuperviseRecord) {
				s.setIsValid(isvalid);
			}
			List<SlActualToCharge> slActualToChargesuperviseRecord = savejsoncharge(
					SlActualToChargeJsonDataSuperviseRecord,
					slSmallloanProject, Short.parseShort("2"));
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("0"));
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));
			String isPreposePayAccrual = this.getRequest().getParameter(
					"isPreposePayAccrualsupervise");

			if (isPreposePayAccrual != null) {

				slSuperviseRecord.setIsPreposePayAccrualsupervise(1);
			} else {
				slSuperviseRecord.setIsPreposePayAccrualsupervise(0);

			}
			this.slSmallloanProjectService.askForProject(proId,
					slActualToCharges, slActualToChargesuperviseRecord,
					slFundIntents, slFundIntentsSuperviseRecord,
					slSuperviseRecord, categoryIds, flag);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("小额贷款贷中监管-申请展期出错:" + e.getMessage());
			return "error";
		}
	}

	public String askForEarlyRepayment() { // 申请提前还款,启动流程

		try {
			String contractids = this.getRequest().getParameter("contractids");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"money_plan");
			Long proId = Long.valueOf(this.getRequest().getParameter(
					"projectId"));
			String fundIntentJsonData = this.getRequest().getParameter(
					"intent_plan");
			String fundIntentJsonDataSuperviseRecord = this.getRequest()
					.getParameter("intent_plan_earlyRepayment");
			String SlActualToChargeJsonDataSuperviseRecord = this.getRequest()
					.getParameter("money_plan_earlyRepayment");
			// Short flag =
			// Short.valueOf(this.getRequest().getParameter("flag"));
			slSmallloanProject = this.slSmallloanProjectService.get(proId);
			List<SlFundIntent> slFundIntentsSuperviseRecord = savejsonintent(
					fundIntentJsonDataSuperviseRecord, slSmallloanProject,
					Short.parseShort("0"));
			Short isvalid = 4;
			for (SlFundIntent s : slFundIntentsSuperviseRecord) {
				s.setIsValid(isvalid);
			}

			this.slSmallloanProjectService.askForEarlyRepaymentProject(proId,
					null, null, null, slFundIntentsSuperviseRecord,
					slEarlyRepaymentRecord, contractids);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("小额贷款贷中监管-申请提前还款出错:" + e.getMessage());
			return "error";
		}
	}

	public String askForAlterAccrual() { // 申请利率变更,启动流程

		try {
			// String contractids =
			// this.getRequest().getParameter("contractids");
			// String slActualToChargeJsonData = this.getRequest().getParameter(
			// "money_plan");
			Long proId = Long.valueOf(this.getRequest().getParameter(
					"projectId"));
			String fundIntentJsonData = this.getRequest().getParameter(
					"intent_plan");
			String fundIntentJsonDataSuperviseRecord = this.getRequest()
					.getParameter("intent_plan_earlyRepayment");
			// String SlActualToChargeJsonDataSuperviseRecord =
			// this.getRequest()
			// .getParameter("money_plan_earlyRepayment");
			// Short flag =
			// Short.valueOf(this.getRequest().getParameter("flag"));
			slSmallloanProject = this.slSmallloanProjectService.get(proId);
			List<SlFundIntent> slFundIntentsSuperviseRecord = savejsonintent(
					fundIntentJsonDataSuperviseRecord, slSmallloanProject,
					Short.parseShort("0"));
			Short isvalid = 3;
			for (SlFundIntent s : slFundIntentsSuperviseRecord) {
				s.setIsValid(isvalid);
			}
			// List<SlActualToCharge> slActualToChargesuperviseRecord =
			// savejsoncharge(
			// SlActualToChargeJsonDataSuperviseRecord,
			// slSmallloanProject, Short.parseShort("0"));

			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("0"));
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			// slActualToCharges = savejsoncharge(slActualToChargeJsonData,
			// slSmallloanProject, Short.parseShort("0"));

			this.slSmallloanProjectService.askForAlterAccrualProject(proId,
					slActualToCharges, null, slFundIntents,
					slFundIntentsSuperviseRecord, slAlterAccrualRecord, null);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("小额贷款贷中利率变更申请节点启动出错:" + e.getMessage());
			return "error";
		}
	}

	public String askForAlterAccrualFlow() { // 利率变更申请节点
		try {
			Long proId = Long.valueOf(this.getRequest().getParameter("projectId"));
			String fundIntentJsonDataSuperviseRecord = this.getRequest().getParameter("intent_plan_earlyRepayment");
			//		
			slSmallloanProject = this.slSmallloanProjectService.get(proId);
			List<SlFundIntent> slFundIntentsSuperviseRecord = savejsonintent(
					fundIntentJsonDataSuperviseRecord, slSmallloanProject,
					Short.parseShort("0"));
			Short isvalid = 3;
			for (SlFundIntent s : slFundIntentsSuperviseRecord) {
				s.setIsValid(isvalid);
			}

			this.slSmallloanProjectService.askForAlterAccrualProjectFlow(proId,
					null, null, null, slFundIntentsSuperviseRecord,
					slAlterAccrualRecord, null);

			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("小额贷款贷中利率变更申请节点出错:" + e.getMessage());
			return "error";
		}
	}

	public String askForEarlyRepaymentFlow() { // 提前还款申请节点
		try {
			Long proId = Long.valueOf(this.getRequest().getParameter(
					"projectId"));
			String fundIntentJsonDataSuperviseRecord = this.getRequest()
					.getParameter("intent_plan_earlyRepayment");
			slSmallloanProject = this.slSmallloanProjectService.get(proId);
			List<SlFundIntent> slFundIntentsSuperviseRecord = savejsonintent(
					fundIntentJsonDataSuperviseRecord, slSmallloanProject,
					Short.parseShort("0"));
			Short isvalid = 4;
			for (SlFundIntent s : slFundIntentsSuperviseRecord) {
				s.setIsValid(isvalid);
			}
			this.slSmallloanProjectService.askForEarlyRepaymentProjectFlow(
					proId, null, null, null, slFundIntentsSuperviseRecord,
					slEarlyRepaymentRecord, null);
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("小额贷款贷中利率变更申请节点保存信息出错:" + e.getMessage());
			return "error";
		}
	}

	public String estimateRate() {

		DecimalFormat df = new DecimalFormat(".##########");
		String calcuDayProfit = "";
		String calcuProfitMoney = "";
		StatsPro statsPro = new StatsPro();
		String startDate = DateUtil.dateToStr(
				slSmallloanProject.getStartDate(), "yyyy-MM-dd");
		String endDate = DateUtil.dateToStr(slSmallloanProject.getIntentDate(),
				"yyyy-MM-dd");
		BigDecimal calcuDayProfitDouble = statsPro.calcuDayProfit(
				slSmallloanProject.getOperationType(), slSmallloanProject
						.getAccrualtype(), slSmallloanProject.getAccrual()
						.doubleValue(), startDate, endDate);
		BigDecimal stem = calcuDayProfitDouble.multiply(
				slSmallloanProject.getProjectMoney()).divide(
				new BigDecimal(100));
		calcuDayProfit = df.format(calcuDayProfitDouble);
		calcuProfitMoney = df.format(stem);
		StringBuffer sb = new StringBuffer("{success:true,data:");
		sb.append("[{\"calcuDayProfit\":\"" + calcuDayProfit + "\",");
		sb.append("\"calcuProfitMoney\":\"" + calcuProfitMoney + "\"}]");
		sb.append("}");
		setJsonString(sb.toString());
		return SUCCESS;
	}

	public String isProjectFlag() {
		StringBuffer buff = new StringBuffer("{success:true,'flag':");
		Long id = Long.valueOf(this.getRequest().getParameter("proId"));
		SlSmallloanProject sl = this.slSmallloanProjectService.get(id);
		BigDecimal val = new BigDecimal(5000000);
		if (sl.getProjectMoney().compareTo(val) >= 0) {
			buff.append("true");
		} else {
			buff.append("false");
		}
		buff.append("}");
		jsonString = buff.toString();
		setJsonString(jsonString);
		return SUCCESS;

	}

	public String loadProjectName() throws NumberFormatException, Exception {
		String operationType = this.getRequest().getParameter("operationType");
		String mineType = this.getRequest().getParameter("mineType");
		String mineId = this.getRequest().getParameter("mineId");
		String cusName = this.getRequest().getParameter("cusName");// 客户名称
		String flowType = this.getRequest().getParameter("flowType");// 客户名称
		String mineNameShort = "";
		SimpleDateFormat formart = new SimpleDateFormat("yyyy", Locale.CHINA);
		SimpleDateFormat formart1 = new SimpleDateFormat("MM", Locale.CHINA);
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		String yearStr = formart.format(calendar.getTime());
		String monthStr = formart1.format(calendar.getTime());
		String operationTypeStr = areaDicService.getById(
				Integer.valueOf(operationType)).getText();
		String flowTypeStr = areaDicService.getById(Integer.valueOf(flowType))
				.getText();
		if (mineType.equals("304")) // 企业
		{
			mineNameShort = this.slCompanyMainService.get(Long.valueOf(mineId))
					.getSimpleName();
		} else if (mineType.equals("305")) // 个人
		{
			mineNameShort = this.slPersonMainService.get(Long.valueOf(mineId))
					.getName();
		}
		String projectName = cusName + yearStr + "年" + monthStr + "月"
				+ operationTypeStr + "项目(" + mineNameShort + ")(" + flowTypeStr
				+ ")";
		StringBuffer sb = new StringBuffer("{success:true,data:");
		sb.append("[{\"dd\":\"" + projectName + "\"}]");
		sb.append("}");
		setJsonString(sb.toString());
		return SUCCESS;
	}

	public String updateSupervise() {
		/*
		 * try { String slSuperviseDatas = this.getRequest().getParameter(
		 * "slSuperviseDatas"); Long proId =
		 * Long.valueOf(this.getRequest().getParameter( "projectId")); String
		 * fundIntentJsonData = this.getRequest().getParameter(
		 * "fundIntentJsonData"); String slActualToChargeJsonData =
		 * this.getRequest().getParameter( "slActualToChargeJsonData");
		 * List<SlSuperviseIn> lSuperviseIns = new ArrayList<SlSuperviseIn>();
		 * if (null != slSuperviseDatas && !"".equals(slSuperviseDatas)) {
		 * String[] shareequityArr = slSuperviseDatas.split("@"); for (int i =
		 * 0; i < shareequityArr.length; i++) { String str = shareequityArr[i];
		 * JSONParser parser = new JSONParser(new StringReader(str)); try {
		 * SlSuperviseIn enterpriseShareequity = (SlSuperviseIn) JSONMapper
		 * .toJava(parser.nextValue(), SlSuperviseIn.class);
		 * lSuperviseIns.add(enterpriseShareequity);
		 * 
		 * } catch (Exception e) { e.printStackTrace(); } } }
		 * 
		 * // 保存款项计划 List<SlFundIntent> slFundIntents = new
		 * ArrayList<SlFundIntent>(); slFundIntents =
		 * savejsonintent(fundIntentJsonData, slSmallloanProject,
		 * Short.parseShort("0"));
		 * 
		 * // 保存杂项费用 List<SlActualToCharge> slActualToCharges = new
		 * ArrayList<SlActualToCharge>(); slActualToCharges =
		 * savejsoncharge(slActualToChargeJsonData, slSmallloanProject,
		 * Short.parseShort("0"));
		 * this.slSmallloanProjectService.updateSuperviseProject( slFundIntents,
		 * lSuperviseIns, proId, slActualToCharges);
		 * 
		 * String taskId = this.getRequest().getParameter("task_id"); String
		 * comments = this.getRequest().getParameter("comments"); if (null !=
		 * taskId && !"".equals(taskId) && null != comments &&
		 * !"".equals(comments.trim())) {
		 * this.creditProjectService.saveComments(taskId, comments); } } catch
		 * (Exception e) { e.printStackTrace(); logger.error("贷中监管保存数据出错:" +
		 * e.getMessage()); }
		 */
		return SUCCESS;
	}

	/**
	 * 微贷修改贷款卡节点—更新 信息
	 */

	public String savepartcustomerInfo() {
		try {
			EnterpriseBank bank = (EnterpriseBank) this.creditBaseDao.getById(
					EnterpriseBank.class, enterpriseBank.getId());
			bank.setBankid(enterpriseBank.getBankid());
			bank.setOpenType(enterpriseBank.getOpenType());
			bank.setAccountnum(enterpriseBank.getAccountnum());
			bank.setAccountType(enterpriseBank.getAccountType());
			bank.setName(enterpriseBank.getName());
			bank.setBankname(enterpriseBank.getBankname());
			bank.setAreaId(enterpriseBank.getAreaId());
			bank.setAreaName(enterpriseBank.getAreaName());
			bank.setBankOutletsName(enterpriseBank.getBankOutletsName());

			this.creditBaseDao.updateDatas(bank);
			baseCustomService.getCustomToweb("1", bank.getEnterpriseid(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	/**
	 * 
	 * @小贷修改贷款账户信息
	 */
	public String saveSmallLoanPartCustomerInfo() {
		try {
			EnterpriseBank bank = (EnterpriseBank) this.creditBaseDao.getById(
					EnterpriseBank.class, enterpriseBank.getId());
			bank.setBankid(enterpriseBank.getBankid());
			bank.setOpenType(enterpriseBank.getOpenType());
			bank.setAccountnum(enterpriseBank.getAccountnum());
			bank.setAccountType(enterpriseBank.getAccountType());
			bank.setName(enterpriseBank.getName());
			bank.setBankname(enterpriseBank.getBankname());
			bank.setAreaId(enterpriseBank.getAreaId());
			bank.setAreaName(enterpriseBank.getAreaName());
			bank.setBankOutletsName(enterpriseBank.getBankOutletsName());
			this.creditBaseDao.updateDatas(bank);
			baseCustomService.getCustomToweb(bank.getIsEnterprise().toString(),
					bank.getEnterpriseid(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	/**
	 * 放款审批节点—更新 信息
	 */
	public String checkload() {
		try {
			SlSmallloanProject persistent = this.slSmallloanProjectService
					.get(slSmallloanProject.getProjectId());
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			slSmallloanProject.setCompanyId(persistent.getCompanyId());
			String degree = this.getRequest().getParameter("degree");
			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			String isAheadPay = this.getRequest().getParameter("isAheadPay");// 不知道前台怎么传到后台
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}

			// end
			BeanUtils.copyProperties(slSmallloanProject, persistent,
					new String[]

					{ "id", "operationType", "flowType", "mineType", "mineId",
							"oppositeType", "oppositeID", "projectName",
							"projectNumber", "oppositeType", "businessType",
							"createDate" });
			this.slSmallloanProjectService.save(persistent);
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}

	/*
	 * 微贷补录历史项目信息
	 */
	public String updateMcroHistoryRecords() {

		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String borrowerInfo = this.getRequest()
					.getParameter("borrowerInfo");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String degree = this.getRequest().getParameter("degree");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");
			String projectStatus = this.getRequest().getParameter(
					"projectStatus");
			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			slSmallloanProject.setProjectStatus(Short.valueOf(projectStatus));
			List<BorrowerInfo> listBO = new ArrayList<BorrowerInfo>(); // 共同借款人
			if (null != borrowerInfo && !"".equals(borrowerInfo)) {
				String[] borrowerInfoArr = borrowerInfo.split("@");
				for (int i = 0; i < borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					try {
						BorrowerInfo borrower = (BorrowerInfo) JSONMapper
								.toJava(parser.nextValue(), BorrowerInfo.class);
						listBO.add(borrower);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			String repaymentSource = this.getRequest().getParameter("repaymentSource");
			List<SlRepaymentSource> listRepaymentSources = new ArrayList<SlRepaymentSource>(); // 第一还款来源
			if (null != repaymentSource && !"".equals(repaymentSource)){
				String[] repaymentSourceArr = repaymentSource.split("@");
				for (int i = 0; i < repaymentSourceArr.length; i++) {

					try {
						String str = repaymentSourceArr[i];
						JSONParser parser = new JSONParser(
								new StringReader(str));
						SlRepaymentSource slRepaymentSource = (SlRepaymentSource) JSONMapper
								.toJava(parser.nextValue(),
										SlRepaymentSource.class);
						listRepaymentSources.add(slRepaymentSource);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,slSmallloanProject, Short.parseShort("1"));
			/*
			 * if
			 * (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
			 * int a = 0; for (SlFundIntent s : slFundIntents) { if
			 * (s.getFundType().equals("principalRepayment")) { a++; }
			 * slSmallloanProject.setPayintentPeriod(a); } }
			 */

			StatsPro statsPro = new StatsPro();
			statsPro.calcuProIntentDate(slSmallloanProject);
			// 保存杂项费用
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));

			String isAheadPay = this.getRequest().getParameter("isAheadPay");
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}

			/*
			 * String isPreposePayAccrual = this.getRequest().getParameter(
			 * "isPreposePayAccrualCheck"); if (isPreposePayAccrual != null) {
			 * 
			 * slSmallloanProject.setIsPreposePayAccrual(1); } else {
			 * slSmallloanProject.setIsPreposePayAccrual(0);
			 * 
			 * }
			 */
			/**
			 * 年化净利率
			 */
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			Integer flag = this.slSmallloanProjectService.updateMcroLoanInfo(
					slSmallloanProject, person, listBO, listRepaymentSources,
					slActualToCharges, isDeleteAllFundIntent, enterpriseBank,
					spouse, slFundIntents, sb);

			if (flag == 0) {
			} else {

				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			logger.error("微贷补录历史项目信息节点-保存（更新）信息出错:" + e.getMessage());
		}

		return SUCCESS;

	}

	/**
	 * 尽职调查节点-保存（更新）信息
	 * 
	 * @return
	 */
	public String updateMcroLoan() {

		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String borrowerInfo = this.getRequest()
					.getParameter("borrowerInfo");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String degree = this.getRequest().getParameter("degree");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");

			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			List<BorrowerInfo> listBO = new ArrayList<BorrowerInfo>(); // 共同借款人
			if (null != borrowerInfo && !"".equals(borrowerInfo)) {
				String[] borrowerInfoArr = borrowerInfo.split("@");
				for (int i = 0; i < borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					try {
						BorrowerInfo borrower = (BorrowerInfo) JSONMapper
								.toJava(parser.nextValue(), BorrowerInfo.class);
						listBO.add(borrower);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			String repaymentSource = this.getRequest().getParameter(
					"repaymentSource");
			List<SlRepaymentSource> listRepaymentSources = new ArrayList<SlRepaymentSource>(); // 第一还款来源
			if (null != repaymentSource && !"".equals(repaymentSource)) {
				String[] repaymentSourceArr = repaymentSource.split("@");
				for (int i = 0; i < repaymentSourceArr.length; i++) {

					try {
						String str = repaymentSourceArr[i];
						JSONParser parser = new JSONParser(
								new StringReader(str));
						SlRepaymentSource slRepaymentSource = (SlRepaymentSource) JSONMapper
								.toJava(parser.nextValue(),
										SlRepaymentSource.class);
						listRepaymentSources.add(slRepaymentSource);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("1"));
			// if
			// (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
			// int a = 0;
			// for (SlFundIntent s : slFundIntents) {
			// if (s.getFundType().equals("principalRepayment")) {
			// a++;
			// }
			// slSmallloanProject.setPayintentPeriod(a);
			// }
			// }
			StatsPro statsPro = new StatsPro();
			statsPro.calcuProIntentDate(slSmallloanProject);
			// 保存杂项费用
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));

			String isAheadPay = this.getRequest().getParameter("isAheadPay");
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}

			/*
			 * String isPreposePayAccrual = this.getRequest().getParameter(
			 * "isPreposePayAccrualCheck"); if (isPreposePayAccrual != null) {
			 * 
			 * slSmallloanProject.setIsPreposePayAccrual(1); } else {
			 * slSmallloanProject.setIsPreposePayAccrual(0);
			 * 
			 * }
			 */
			/**
			 * 年化净利率
			 */
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			Integer flag = this.slSmallloanProjectService.updateMcroLoanInfo(
					slSmallloanProject, person, listBO, listRepaymentSources,
					slActualToCharges, isDeleteAllFundIntent, enterpriseBank,
					spouse, slFundIntents, sb);

			if (flag == 0) {
			} else {

				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			logger.error("尽职调查节点-保存（更新）信息出错:" + e.getMessage());
		}

		return SUCCESS;

	}

	/*
	 * 项目编辑页面保存
	 */
	public String updateAll() {

		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String borrowerInfo = this.getRequest().getParameter("borrowerInfo");

			String fundIntentJsonData = this.getRequest().getParameter("fundIntentJsonData");
			String slActualToChargeJsonData = this.getRequest().getParameter("slActualToChargeJsonData");
			String repaymentSource=this.getRequest().getParameter("repaymentSource");
			String gudongInfo=this.getRequest().getParameter("gudongInfo");
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			if(persistent.getOppositeType().equals("person_customer")){
				//保存客户信息
				String str=personService.savePersonInfo(person, spouse, workCompany);
				sb.append(","+str);
			}else{	enterpriseService.saveSingleEnterprise(enterprise, person, gudongInfo);
			//保存客户银行信息
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(persistent.getOppositeID().intValue(), Short.valueOf("0"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(persistent.getOppositeID().intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("0"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			//保存第一还款来源
			if(null != repaymentSource && !"".equals(repaymentSource)) {

				String[] repaymentSourceArr = repaymentSource.split("@");
				
				for(int i=0; i<repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource temp = (SlRepaymentSource)JSONMapper.toJava(parser.nextValue(),SlRepaymentSource.class);
					boolean flag = StringUtil.isNumeric(temp.getTypeId());
					GlobalType globalType = globalTypeService.getByNodeKey("repaymentSource").get(0);
					if (flag == false) {
						Dictionary dic = new Dictionary();
						dic.setItemValue(temp.getTypeId());
						dic.setItemName(globalType.getTypeName());
						dic.setProTypeId(globalType.getProTypeId());
						dic.setDicKey(temp.getTypeId());
						dic.setStatus("0");
						dictionaryService.save(dic);
						temp.setTypeId(String.valueOf(dic.getDicId()));
					} else {
						Dictionary cd = dictionaryService.get(Long.valueOf(temp
								.getTypeId()));
						if (null == cd) {
							Dictionary dic = new Dictionary();
							dic.setItemValue(temp.getTypeId());
							dic.setItemName(globalType.getTypeName());
							dic.setProTypeId(globalType.getProTypeId());
							dic.setDicKey(temp.getTypeId());
							dic.setStatus("0");
							dictionaryService.save(dic);
							temp.setTypeId(String.valueOf(dic.getDicId()));
						}
					}
					temp.setProjId(slSmallloanProject.getProjectId());
					if (temp.getSourceId() == null) {
						this.slRepaymentSourceService.save(temp);
					} else {
						SlRepaymentSource rPersistent = this.slRepaymentSourceService.get(temp.getSourceId());
						BeanUtil.copyNotNullProperties(rPersistent, temp);
						this.slRepaymentSourceService.save(rPersistent);
					}
				
					
				}
			}
			//保存共同借款人
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {

				String[] borrowerInfoArr = borrowerInfo.split("@");
				
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}}
			
			
			
			// 更新借款需求
			if(null!=bpMoneyBorrowDemand){
				bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
				if (bpMoneyBorrowDemand.getBorrowid() == null) {
					this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
				} else {
					BpMoneyBorrowDemand bbd = bpMoneyBorrowDemandService.getByBorrowId(bpMoneyBorrowDemand.getBorrowid());
					BeanUtil.copyNotNullProperties(bbd, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(bbd);
				}
				sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
			}
			BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
			// 自有资金款项信息
			if(null!=ownBpFundProject){
				
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
					bpFundProjectService.merge(ownFund);
				}
			}
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), (ownFund.getProjectStatus()==0?Short.valueOf("1"):Short.valueOf("0")), persistent.getCompanyId());
			}
			//保存款项信息
			if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
				List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownBpFundProject.getId());
				for (SlFundIntent sfi : oldList) {
					if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
						slFundIntentService.remove(sfi);
					}
				}
				String[] shareequityArr = fundIntentJsonData.split("@");
				for (int i = 0; i < shareequityArr.length; i++) {
					String fundIntentstr = shareequityArr[i];
					JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
			
					SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
					SlFundIntent1.setProjectId(persistent.getProjectId());
					SlFundIntent1.setProjectName(persistent.getProjectName());
					SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

					if (null == SlFundIntent1.getFundIntentId()) {

						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
						}
						SlFundIntent1.setAfterMoney(new BigDecimal(0));
						SlFundIntent1.setAccrualMoney(new BigDecimal(0));
						SlFundIntent1.setFlatMoney(new BigDecimal(0));
						Short isvalid = 0;
						SlFundIntent1.setIsValid(isvalid);
						/*if (SlFundIntent1.getFundType().equals("principalLending")) {
							SlFundIntent1.setIsCheck(Short.valueOf("0"));
						} else {*/
						SlFundIntent1.setIsCheck(ownFund.getProjectStatus()==0?Short.valueOf("1"):Short.valueOf("0"));
						//}
						SlFundIntent1.setBusinessType(persistent.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
						slFundIntentService.save(SlFundIntent1);
					} else {
						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
						}
						SlFundIntent1.setBusinessType(persistent.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
					
						SlFundIntent1.setIsCheck(ownFund.getProjectStatus()==0?Short.valueOf("1"):Short.valueOf("0"));
						
						SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
						BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

						slFundIntentService.merge(slFundIntent2);

					}
					
				}
			}
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("尽职调查节点-保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}

	/**
	 * 更新编辑展期项目的信息
	 */
	public String updatePostponedInfo() {
		try{
			String slSuperviseRecordId = this.getRequest().getParameter(
					"slSuperviseRecordId");
	
			if(null!=slSuperviseRecordId && !"".equals(slSuperviseRecordId)){
				SlSuperviseRecord record=slSuperviseRecordService.get(Long.valueOf(slSuperviseRecordId));
				BeanUtil.copyNotNullProperties(record, slSuperviseRecord);
				slSuperviseRecordService.save(record);
				String slActualToChargeJsonData = this.getRequest().getParameter("slActualToChargeJson");
				BpFundProject fundProject =  bpFundProjectService.get(record.getProjectId());
				//保存费用信息
				if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
					slActualToChargeService.savejson(slActualToChargeJsonData, fundProject.getProjectId(), fundProject.getBusinessType(), (record.getCheckStatus()==1?Short.valueOf("0"):Short.valueOf("1")), fundProject.getCompanyId());
				}
			//保存款项信息
				String slFundIentJson=this.getRequest().getParameter("slFundIentJsonData");
				if(null != slFundIentJson && !"".equals(slFundIentJson)) {
					List<SlFundIntent> slFundIntentsAllsupervise = slFundIntentService.getlistbyslSuperviseRecordId(record.getId(), "SmallLoan",fundProject.getProjectId());
					for (SlFundIntent s : slFundIntentsAllsupervise) {
						slFundIntentService.remove(s);
					}
					String[] slFundIentJsonArr = slFundIentJson.split("@");
					
					for(int i=0; i<slFundIentJsonArr.length; i++) {
						String str = slFundIentJsonArr[i];
						JSONParser parser = new JSONParser(new StringReader(str));
						
						SlFundIntent slFundIntent = (SlFundIntent)JSONMapper.toJava(parser.nextValue(),SlFundIntent.class);
						slFundIntent.setSlSuperviseRecordId(Long.valueOf(slSuperviseRecordId));
						slFundIntent.setProjectId(fundProject.getProjectId());
						slFundIntent.setBusinessType(fundProject.getBusinessType());
						slFundIntent.setProjectName(fundProject.getProjectName());
						slFundIntent.setProjectNumber(fundProject.getProjectNumber());
						slFundIntent.setCompanyId(fundProject.getCompanyId());
						slFundIntent.setIsValid(Short.valueOf("0"));
						BigDecimal lin = new BigDecimal(0.00);
						if(slFundIntent.getIncomeMoney().compareTo(lin)==0){
				        	slFundIntent.setNotMoney(slFundIntent.getPayMoney());
				        }else{
				        	slFundIntent.setNotMoney(slFundIntent.getIncomeMoney());
				        	
				        }
						slFundIntent.setAfterMoney(new BigDecimal(0));
						slFundIntent.setAccrualMoney(new BigDecimal(0));
						slFundIntent.setFlatMoney(new BigDecimal(0));
						slFundIntent.setIsCheck((record.getCheckStatus()==1?Short.valueOf("0"):Short.valueOf("1")));
						if(null==slFundIntent.getFundIntentId()){
							slFundIntentService.save(slFundIntent);
						}else{
							SlFundIntent orgSlFundIntent=slFundIntentService.get(slFundIntent.getFundIntentId());
							BeanUtil.copyNotNullProperties(orgSlFundIntent, slFundIntent);
							slFundIntentService.save(orgSlFundIntent);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		/*if (projectId != null && !"".equals(projectId)
				&& slSuperviseRecordId != null
				&& !"".equals(slSuperviseRecordId)) {
			FlowRunInfo flowRunInfo = new FlowRunInfo(this.getRequest());
			slSmallloanProjectService.updatePostponedInfo(flowRunInfo, Long
					.valueOf(projectId), Long.valueOf(slSuperviseRecordId));
		}*/
		return SUCCESS;
	}

	/**
	 * 
	 * @小贷补录历史项目信息
	 */
	public String updateSmallHistoryRecords() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String shareequity = this.getRequest().getParameter("gudongInfo");
			String borrowerInfo = this.getRequest()
					.getParameter("borrowerInfo");
			String degree = this.getRequest().getParameter("degree");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");
			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			List<BorrowerInfo> listBO = new ArrayList<BorrowerInfo>(); // 共同借款人
			if (null != borrowerInfo && !"".equals(borrowerInfo)) {
				String[] borrowerInfoArr = borrowerInfo.split("@");
				for (int i = 0; i < borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					try {
						BorrowerInfo borrower = (BorrowerInfo) JSONMapper
								.toJava(parser.nextValue(), BorrowerInfo.class);
						listBO.add(borrower);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			List<EnterpriseShareequity> listES = new ArrayList<EnterpriseShareequity>(); // 股东信息
			if (null != shareequity && !"".equals(shareequity)) {
				String[] shareequityArr = shareequity.split("@");
				for (int i = 0; i < shareequityArr.length; i++) {
					String str = shareequityArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					try {
						EnterpriseShareequity enterpriseShareequity = (EnterpriseShareequity) JSONMapper
								.toJava(parser.nextValue(),
										EnterpriseShareequity.class);
						listES.add(enterpriseShareequity);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			String repaymentSource = this.getRequest().getParameter(
					"repaymentSource");
			List<SlRepaymentSource> listRepaymentSources = new ArrayList<SlRepaymentSource>(); // 第一还款来源
			if (null != repaymentSource && !"".equals(repaymentSource)) {
				String[] repaymentSourceArr = repaymentSource.split("@");
				for (int i = 0; i < repaymentSourceArr.length; i++) {

					try {
						String str = repaymentSourceArr[i];
						JSONParser parser = new JSONParser(
								new StringReader(str));
						SlRepaymentSource slRepaymentSource = (SlRepaymentSource) JSONMapper
								.toJava(parser.nextValue(),
										SlRepaymentSource.class);
						listRepaymentSources.add(slRepaymentSource);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("1"));
			/*
			 * if
			 * (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
			 * int a = 0; for (SlFundIntent s : slFundIntents) { if
			 * (s.getFundType().equals("principalRepayment")) { a++; }
			 * slSmallloanProject.setPayintentPeriod(a); } }
			 */
			/*
			 * StatsPro statsPro = new StatsPro();
			 * statsPro.calcuProIntentDate(slSmallloanProject);
			 */
			// 保存杂项费用
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));

			String isAheadPay = this.getRequest().getParameter("isAheadPay");
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}

			/*
			 * String isPreposePayAccrual = this.getRequest().getParameter(
			 * "isPreposePayAccrualCheck"); if (isPreposePayAccrual != null) {
			 * 
			 * slSmallloanProject.setIsPreposePayAccrual(1); } else {
			 * slSmallloanProject.setIsPreposePayAccrual(0);
			 * 
			 * }
			 */
			/**
			 * 年化净利率
			 */
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			Integer flag = this.slSmallloanProjectService.updateSmallLoadInfo(
					slSmallloanProject, person, enterprise, listES, listBO,
					listRepaymentSources, slFundIntents, slActualToCharges,
					enterpriseBank, spouse, isDeleteAllFundIntent, sb,
					projectPropertyClassification, slConferenceRecord);
			// Integer flag =
			// this.slSmallloanProjectService.updateInfo(slSmallloanProject,
			// person, enterprise, listES,listRepaymentSources, slFundIntents,
			// slActualToCharges,isDeleteAllFundIntent);
			if (flag == 0) {
			} else {

				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("小贷补录历史项目信息节点-保存（更新）信息出错:" + e.getMessage());
		}

		return SUCCESS;
	}

	public String updateSlFundIntent() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");
			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("1"));
			Integer flag = this.slSmallloanProjectService.updateSlFundIntent(
					slSmallloanProject, slFundIntents, isDeleteAllFundIntent,
					"1");
			if (flag != 0) {
				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("信贷流程-放款确认节点-保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}

	public String update() {
		try {
			String commentsxxxx = this.getRequest().getParameter("comments");

			StringBuffer sb = new StringBuffer("{success:true");
			String shareequity = this.getRequest().getParameter("gudongInfo");
			String borrowerInfo = this.getRequest()
					.getParameter("borrowerInfo");
			String degree = this.getRequest().getParameter("degree");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");

			List<BorrowerInfo> listBO = new ArrayList<BorrowerInfo>(); // 共同借款人
			if (null != borrowerInfo && !"".equals(borrowerInfo)) {
				String[] borrowerInfoArr = borrowerInfo.split("@");
				for (int i = 0; i < borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo borrower = (BorrowerInfo) JSONMapper.toJava(
							parser.nextValue(), BorrowerInfo.class);
					listBO.add(borrower);
				}
			}
			List<EnterpriseShareequity> listES = new ArrayList<EnterpriseShareequity>(); // 股东信息
			if (null != shareequity && !"".equals(shareequity)) {
				String[] shareequityArr = shareequity.split("@");
				for (int i = 0; i < shareequityArr.length; i++) {
					String str = shareequityArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					EnterpriseShareequity enterpriseShareequity = (EnterpriseShareequity) JSONMapper
							.toJava(parser.nextValue(),
									EnterpriseShareequity.class);
					listES.add(enterpriseShareequity);
				}
			}
			String repaymentSource = this.getRequest().getParameter(
					"repaymentSource");
			List<SlRepaymentSource> listRepaymentSources = new ArrayList<SlRepaymentSource>(); // 第一还款来源
			if (null != repaymentSource && !"".equals(repaymentSource)) {
				String[] repaymentSourceArr = repaymentSource.split("@");
				for (int i = 0; i < repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource slRepaymentSource = (SlRepaymentSource) JSONMapper
							.toJava(parser.nextValue(), SlRepaymentSource.class);
					listRepaymentSources.add(slRepaymentSource);
				}
			}
			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("1"));
			// if
			// (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
			// int a = 0;
			// for (SlFundIntent s : slFundIntents) {
			// if (s.getFundType().equals("principalRepayment")) {
			// a++;
			// }
			// slSmallloanProject.setPayintentPeriod(a);
			// }
			// }
			// StatsPro statsPro = new StatsPro();
			// statsPro.calcuProIntentDate(slSmallloanProject);
			// 保存杂项费用
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));

			String isAheadPay = this.getRequest().getParameter("isAheadPay");
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}
			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			String isPreposePayAccrual = this.getRequest().getParameter(
					"isPreposePayAccrualCheck");
			if (isPreposePayAccrual != null) {
				slSmallloanProject.setIsPreposePayAccrual(1);
			} else {
				slSmallloanProject.setIsPreposePayAccrual(0);
			}
			/**
			 * 年化净利率
			 */
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			Integer flag = this.slSmallloanProjectService.updateSmallLoadInfo(
					slSmallloanProject, person, enterprise, listES, listBO,
					listRepaymentSources, slFundIntents, slActualToCharges,
					enterpriseBank, spouse, isDeleteAllFundIntent, sb,
					projectPropertyClassification, slConferenceRecord);
			// Integer flag =
			// this.slSmallloanProjectService.updateInfo(slSmallloanProject,
			// person, enterprise, listES,listRepaymentSources, slFundIntents,
			// slActualToCharges,isDeleteAllFundIntent);
			if (flag == 0) {
			} else {
				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			logger.error("尽职调查节点-保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}

	/***
	 * 信贷流程-放款确认保存更新方法
	 * 
	 * @return
	 */
	public String updateLoan() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String projectId = this.getRequest().getParameter(
					"slSmallloanProject.projectId");

			// 平台资金款项信息
			BpFundProject platFormFund = bpFundProjectService.getByProjectId(
					Long.valueOf(projectId), Short.valueOf("1"));
			if (null != platFormFund) {
				// 保存款项计划
				slFundIntentService.savejson(fundIntentJsonData, new Long(
						projectId), "SmallLoan", Short.parseShort("1"),
						platFormFund.getCompanyId(), platFormFund.getId(),
						platFormFund.getFundResource());
			}

			// 意见说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("信贷流程-放款确认保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}

	public String newUpdate() {

		try {

			// 获取参数--〉封装
			String __projectInfo = this.getRequest()
					.getParameter("projectInfo");
			String _projectInfo = URLDecoder.decode(__projectInfo, "utf-8");// url解码

			// JSONParser _parser = new JSONParser(new
			// StringReader(_projectInfo));
			// com.test2.ProjectInfo projectInfo = (com.test2.ProjectInfo)
			// JSONMapper.toJava(
			// _parser.nextValue(), com.test2.ProjectInfo.class);

			String _enterpriseInfo = this.getRequest().getParameter(
					"enterpriseInfo");
			String enterpriseInfo = URLDecoder.decode(_enterpriseInfo, "utf-8");// url解码

			// String borrowerInfo = this.getRequest()
			// .getParameter("borrowerInfo");
			// String degree = this.getRequest().getParameter("degree");
			// String fundIntentJsonData = this.getRequest().getParameter(
			// "fundIntentJsonData");
			// String slActualToChargeJsonData = this.getRequest().getParameter(
			// "slActualToChargeJsonData");
			// String isDeleteAllFundIntent = this.getRequest().getParameter(
			// "isDeleteAllFundIntent");

			// 解析参数
			String borrowerInfo = "";
			String degree = "";
			String fundIntentJsonData = "";
			String slActualToChargeJsonData = "";
			String isDeleteAllFundIntent = "";

			//
			// slSmallloanProject, person, enterprise, listES, listBO,
			// listRepaymentSources, slFundIntents, slActualToCharges,
			// enterpriseBank, spouse, isDeleteAllFundIntent, sb
			// 股东信息,不变
			String shareequity = this.getRequest().getParameter("gudong");
			StringBuffer sb = new StringBuffer("{success:true");

			List<BorrowerInfo> listBO = new ArrayList<BorrowerInfo>(); // 共同借款人
			if (null != borrowerInfo && !"".equals(borrowerInfo)) {
				String[] borrowerInfoArr = borrowerInfo.split("@");
				for (int i = 0; i < borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo borrower = (BorrowerInfo) JSONMapper.toJava(
							parser.nextValue(), BorrowerInfo.class);
					listBO.add(borrower);
				}
			}
			List<EnterpriseShareequity> listES = new ArrayList<EnterpriseShareequity>(); // 股东信息
			if (null != shareequity && !"".equals(shareequity)) {
				String[] shareequityArr = shareequity.split("@");
				for (int i = 0; i < shareequityArr.length; i++) {
					String str = shareequityArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					EnterpriseShareequity enterpriseShareequity = (EnterpriseShareequity) JSONMapper
							.toJava(parser.nextValue(),
									EnterpriseShareequity.class);
					listES.add(enterpriseShareequity);
				}
			}
			String repaymentSource = this.getRequest().getParameter(
					"repaymentSource");
			List<SlRepaymentSource> listRepaymentSources = new ArrayList<SlRepaymentSource>(); // 第一还款来源
			if (null != repaymentSource && !"".equals(repaymentSource)) {
				String[] repaymentSourceArr = repaymentSource.split("@");
				for (int i = 0; i < repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource slRepaymentSource = (SlRepaymentSource) JSONMapper
							.toJava(parser.nextValue(), SlRepaymentSource.class);
					listRepaymentSources.add(slRepaymentSource);
				}
			}

			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("1"));
			// if
			// (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
			// int a = 0;
			// for (SlFundIntent s : slFundIntents) {
			// if (s.getFundType().equals("principalRepayment")) {
			// a++;
			// }
			// slSmallloanProject.setPayintentPeriod(a);
			// }
			// }
			// StatsPro statsPro = new StatsPro();
			// statsPro.calcuProIntentDate(slSmallloanProject);
			// 保存杂项费用
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));

			String isAheadPay = this.getRequest().getParameter("isAheadPay");
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}
			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			String isPreposePayAccrual = this.getRequest().getParameter(
					"isPreposePayAccrualCheck");
			if (isPreposePayAccrual != null) {
				slSmallloanProject.setIsPreposePayAccrual(1);
			} else {
				slSmallloanProject.setIsPreposePayAccrual(0);
			}
			/**
			 * 年化净利率
			 */
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			Integer flag = this.slSmallloanProjectService.updateSmallLoadInfo(
					slSmallloanProject, person, enterprise, listES, listBO,
					listRepaymentSources, slFundIntents, slActualToCharges,
					enterpriseBank, spouse, isDeleteAllFundIntent, sb,
					projectPropertyClassification, slConferenceRecord);
			// Integer flag =
			// this.slSmallloanProjectService.updateInfo(slSmallloanProject,
			// person, enterprise, listES,listRepaymentSources, slFundIntents,
			// slActualToCharges,isDeleteAllFundIntent);
			if (flag == 0) {
			} else {
				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			logger.error("尽职调查节点-保存（更新）信息出错:" + e.getMessage());
		}

		return SUCCESS;

	}

	public String updateCommon() {

		try {

			StringBuffer sb = new StringBuffer("{success:true,msg:");
			String ddd = "ddd";
			sb.append("'" + ddd + "'");
			sb.append("}");
			String shareequity = this.getRequest().getParameter("gudongInfo");

			String degree = this.getRequest().getParameter("degree");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");

			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			List<EnterpriseShareequity> listES = new ArrayList<EnterpriseShareequity>(); // 股东信息
			if (null != shareequity && !"".equals(shareequity)) {
				String[] shareequityArr = shareequity.split("@");
				for (int i = 0; i < shareequityArr.length; i++) {
					String str = shareequityArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					try {
						EnterpriseShareequity enterpriseShareequity = (EnterpriseShareequity) JSONMapper
								.toJava(parser.nextValue(),
										EnterpriseShareequity.class);
						listES.add(enterpriseShareequity);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			String repaymentSource = this.getRequest().getParameter(
					"repaymentSource");
			List<SlRepaymentSource> listRepaymentSources = new ArrayList<SlRepaymentSource>(); // 第一还款来源
			if (null != repaymentSource && !"".equals(repaymentSource)) {
				String[] repaymentSourceArr = repaymentSource.split("@");
				for (int i = 0; i < repaymentSourceArr.length; i++) {

					try {
						String str = repaymentSourceArr[i];
						JSONParser parser = new JSONParser(
								new StringReader(str));
						SlRepaymentSource slRepaymentSource = (SlRepaymentSource) JSONMapper
								.toJava(parser.nextValue(),
										SlRepaymentSource.class);
						listRepaymentSources.add(slRepaymentSource);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("0"));
			// if
			// (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
			// int a = 0;
			// for (SlFundIntent s : slFundIntents) {
			// if (s.getFundType().equals("principalRepayment")) {
			// a++;
			// }
			// slSmallloanProject.setPayintentPeriod(a);
			// }
			// }
			StatsPro statsPro = new StatsPro();
			statsPro.calcuProIntentDate(slSmallloanProject);
			// 保存杂项费用
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));

			String isAheadPay = this.getRequest().getParameter("isAheadPay");
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}

			/*
			 * String isPreposePayAccrual = this.getRequest().getParameter(
			 * "isPreposePayAccrualCheck"); if (isPreposePayAccrual != null) {
			 * 
			 * slSmallloanProject.setIsPreposePayAccrual(1); } else {
			 * slSmallloanProject.setIsPreposePayAccrual(0);
			 * 
			 * }
			 */
			/**
			 * 年化净利率
			 */
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			Integer flag = this.slSmallloanProjectService.updateInfo(
					slSmallloanProject, person, enterprise, listES,
					listRepaymentSources, slFundIntents, slActualToCharges,
					isDeleteAllFundIntent);

			if (flag == 0) {
			} else {

				String taskId = this.getRequest().getParameter("task_id");
				String comments = this.getRequest().getParameter("comments");
				if (null != taskId && !"".equals(taskId) && null != comments
						&& !"".equals(comments.trim())) {
					this.creditProjectService.saveComments(taskId, comments);
				}
			}

			setJsonString(sb.toString());

		} catch (Exception e) {
			logger.error("尽职调查节点-保存（更新）信息出错:" + e.getMessage());
		}

		return SUCCESS;

	}

	public String saveComments() {

		String taskId = this.getRequest().getParameter("task_id");
		String comments = this.getRequest().getParameter("comments");
		if (null != taskId && !"".equals(taskId) && null != comments
				&& !"".equals(comments.trim())) {
			this.creditProjectService.saveComments(taskId, comments);
		}
		return SUCCESS;
	}

	public String saveComapny() {
		String shareequity = this.getRequest().getParameter("gudongInfo");
		String projectId = this.getRequest().getParameter("projectId");
		int enterpriseId = 0;
		String oppositeType = "";
		if ("Guarantee".equals(businessType)) {
			GLGuaranteeloanProject project = this.glGuaranteeloanProjectService
					.get(Long.valueOf(projectId));
			enterpriseId = project.getOppositeID().intValue();
			oppositeType = project.getOppositeType();
		} else if ("SmallLoan".equals(businessType)) {
			SlSmallloanProject project = this.slSmallloanProjectService
					.get(Long.valueOf(projectId));
			enterpriseId = project.getOppositeID().intValue();
			oppositeType = project.getOppositeType();
		} else if ("Financing".equals(businessType)) {
			FlFinancingProject project = this.flFinancingProjectService
					.get(Long.valueOf(projectId));
			enterpriseId = project.getOppositeID().intValue();
			oppositeType = project.getOppositeType();
		}
		if ((null != enterprise.getId())
				&& (enterpriseId == enterprise.getId())
				&& (oppositeType.equals("company_customer"))) {
			setJsonString("{success:true,msg:null}");
		} else {
			List<EnterpriseShareequity> listES = new ArrayList<EnterpriseShareequity>();
			if (null != shareequity && !"".equals(shareequity)) {
				String[] shareequityArr = shareequity.split("@");
				for (int i = 0; i < shareequityArr.length; i++) {
					String str = shareequityArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					try {
						EnterpriseShareequity enterpriseShareequity = (EnterpriseShareequity) JSONMapper
								.toJava(parser.nextValue(),
										EnterpriseShareequity.class);
						listES.add(enterpriseShareequity);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return "success";
	}

	@LogResource(description = "编辑项目")
	public String updateintent() {// 项目编辑保存
		String fundIntentJsonData = this.getRequest().getParameter(
				"fundIntentJsonData");
		String slActualToChargeJsonData = this.getRequest().getParameter(
				"slActualToChargeJsonData");
		String activityName = this.getRequest().getParameter("activityName"); // 项目信息保存要先判断节点与签合同的先后
		String runId = this.getRequest().getParameter("runId");
		List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
		List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
		// if (null == activityName) { // 款项确认
		slFundIntents = savejsonintent(fundIntentJsonData, slSmallloanProject,
				Short.parseShort("0"));
		// }
		// if (activityName != null) {
		// boolean ischeck = creditProjectService.isBeforeTask(Long
		// .valueOf(runId), activityName);
		// if (ischeck == true) {
		// slFundIntents = savejsonintent(fundIntentJsonData,
		// slSmallloanProject, Short.parseShort("0"));
		// } else {
		// slFundIntents = savejsonintent(fundIntentJsonData,
		// slSmallloanProject, Short.parseShort("1"));
		// }
		// }
		StatsPro statsPro = new StatsPro();
		statsPro.calcuProIntentDate(slSmallloanProject);
		slActualToCharges = savejsoncharge(slActualToChargeJsonData,
				slSmallloanProject, Short.parseShort("0"));
		this.slSmallloanProjectService.updateintnetProjectDetail(
				slSmallloanProject, slFundIntents, slActualToCharges);
		String taskId = this.getRequest().getParameter("task_id");
		String comments = this.getRequest().getParameter("comments");
		if (null != taskId && !"".equals(taskId) && null != comments
				&& !"".equals(comments.trim())) {
			this.creditProjectService.saveComments(taskId, comments);
		}
		return "success";

	}

	/**
	 * 
	 * @小贷放款及款项计划确认节点
	 */
	public String updateSmallLoanIntentaffirm() {

		String fundIntentJsonData = this.getRequest().getParameter(
				"fundIntentJsonData");
		String slActualToChargeJsonData = this.getRequest().getParameter(
				"slActualToChargeJsonData");
		List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
		List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
		slFundIntents = savejsonintent(fundIntentJsonData, slSmallloanProject,
				Short.parseShort("1"));
		// if (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
		// int a = 0;
		// for (SlFundIntent s : slFundIntents) {
		// if (s.getFundType().equals("principalRepayment")) {
		// a++;
		// }
		// slSmallloanProject.setPayintentPeriod(a);
		// }
		// }
		StatsPro statsPro = new StatsPro();
		statsPro.calcuProIntentDate(slSmallloanProject);
		slActualToCharges = savejsoncharge(slActualToChargeJsonData,
				slSmallloanProject, Short.parseShort("0"));
		String degree = this.getRequest().getParameter("degree");

		if (null != degree && !"".equals(degree)) {
			slSmallloanProject.setAppUserId(degree);
		}
		String isAheadPay = this.getRequest().getParameter("isAheadPay");
		if (isAheadPay != null) {
			slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
		} else {
			slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
		}

		/*
		 * String isPreposePayAccrual = this.getRequest().getParameter(
		 * "isPreposePayAccrualCheck"); if (isPreposePayAccrual != null) {
		 * 
		 * slSmallloanProject.setIsPreposePayAccrual(1); } else {
		 * slSmallloanProject.setIsPreposePayAccrual(0);
		 * 
		 * }
		 */
		this.slSmallloanProjectService.updateintnet(slSmallloanProject,
				slFundIntents, slActualToCharges);
		String taskId = this.getRequest().getParameter("task_id");
		String comments = this.getRequest().getParameter("comments");
		if (null != taskId && !"".equals(taskId) && null != comments
				&& !"".equals(comments.trim())) {
			this.creditProjectService.saveComments(taskId, comments);
		}
		return "success";

	}

	/**
	 * 
	 * 微贷放款及款项计划确认节点
	 */
	public String updateintentaffirm() {

		String fundIntentJsonData = this.getRequest().getParameter(
				"fundIntentJsonData");
		String slActualToChargeJsonData = this.getRequest().getParameter(
				"slActualToChargeJsonData");
		List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
		List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
		slFundIntents = savejsonintent(fundIntentJsonData, slSmallloanProject,
				Short.parseShort("1"));
		// if (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
		// int a = 0;
		// for (SlFundIntent s : slFundIntents) {
		// if (s.getFundType().equals("principalRepayment")) {
		// a++;
		// }
		// slSmallloanProject.setPayintentPeriod(a);
		// }
		// }
		StatsPro statsPro = new StatsPro();
		statsPro.calcuProIntentDate(slSmallloanProject);
		slActualToCharges = savejsoncharge(slActualToChargeJsonData,
				slSmallloanProject, Short.parseShort("0"));
		String degree = this.getRequest().getParameter("degree");

		if (null != degree && !"".equals(degree)) {
			slSmallloanProject.setAppUserId(degree);
		}
		String isAheadPay = this.getRequest().getParameter("isAheadPay");
		if (isAheadPay != null) {
			slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
		} else {
			slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
		}

		/*
		 * String isPreposePayAccrual = this.getRequest().getParameter(
		 * "isPreposePayAccrualCheck"); if (isPreposePayAccrual != null) {
		 * 
		 * slSmallloanProject.setIsPreposePayAccrual(1); } else {
		 * slSmallloanProject.setIsPreposePayAccrual(0);
		 * 
		 * }
		 */
		this.slSmallloanProjectService.updateintnet(slSmallloanProject,
				slFundIntents, slActualToCharges);
		String taskId = this.getRequest().getParameter("task_id");
		String comments = this.getRequest().getParameter("comments");
		if (null != taskId && !"".equals(taskId) && null != comments
				&& !"".equals(comments.trim())) {
			this.creditProjectService.saveComments(taskId, comments);
		}
		return "success";

	}

	public List<SlFundIntent> savejsonintent(String fundIntentJsonData,
			SlSmallloanProject slSmallloanProject, Short flag) {
		List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
		if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {

			String[] shareequityArr = fundIntentJsonData.split("@");

			for (int i = 0; i < shareequityArr.length; i++) {
				String str = shareequityArr[i];
				JSONParser parser = new JSONParser(new StringReader(str));

				try {
					SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper
							.toJava(parser.nextValue(), SlFundIntent.class);
					SlFundIntent1.setProjectId(slSmallloanProject
							.getProjectId());
					SlFundIntent1.setProjectName(slSmallloanProject
							.getProjectName());
					SlFundIntent1.setProjectNumber(slSmallloanProject
							.getProjectNumber());

					if (null == SlFundIntent1.getFundIntentId()) {

						BigDecimal lin = new BigDecimal(0.00);

						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getIncomeMoney());

						}
						SlFundIntent1.setAfterMoney(new BigDecimal(0));
						SlFundIntent1.setAccrualMoney(new BigDecimal(0));
						SlFundIntent1.setFlatMoney(new BigDecimal(0));
						Short isvalid = 0;
						SlFundIntent1.setIsValid(isvalid);
						SlFundIntent1.setIsCheck(flag);
						SlFundIntent1.setBusinessType("SmallLoan");
						SlFundIntent1.setCompanyId(slSmallloanProject
								.getCompanyId());
						slFundIntents.add(SlFundIntent1);
					} else {
						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getIncomeMoney());

						}
						SlFundIntent slFundIntent2 = slFundIntentService
								.get(SlFundIntent1.getFundIntentId());
						if (slFundIntent2.getAfterMoney().compareTo(
								new BigDecimal(0)) == 0) {
							BeanUtil.copyNotNullProperties(slFundIntent2,
									SlFundIntent1);
							slFundIntent2.setBusinessType("SmallLoan");
							slFundIntent2.setCompanyId(slSmallloanProject
									.getCompanyId());
							slFundIntent2.setIsCheck(flag);
							slFundIntents.add(slFundIntent2);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return slFundIntents;
	}

	public List<SlActualToCharge> savejsoncharge(
			String slActualToChargeJsonData,
			SlSmallloanProject slSmallloanProject, Short flag) {
		List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
		if (null != slActualToChargeJsonData
				&& !"".equals(slActualToChargeJsonData)) {

			String[] shareequityArr = slActualToChargeJsonData.split("@");

			for (int i = 0; i < shareequityArr.length; i++) {
				String str = shareequityArr[i];
				String[] strArr = str.split(",");
				String typestr = "";
				if (strArr.length == 7) {
					typestr = strArr[1];
				} else {
					typestr = strArr[0];
				}
				String typeId = "";
				String typename = "";
				if (typestr.endsWith("\"") == true) {
					typename = typestr.substring(typestr.indexOf(":") + 2,
							typestr.length() - 1);
				} else {
					typeId = typestr.substring(typestr.indexOf(":") + 1,
							typestr.length());
				}
				SlPlansToCharge slPlansToCharge = new SlPlansToCharge();

				if (!typename.equals("")) {

					List<SlPlansToCharge> list = slPlansToChargeService
							.getAll();
					int k = 0;
					for (SlPlansToCharge p : list) {
						if (p.getName().equals(typename)
								&& p.getBusinessType().equals("SmallLoan")) {
							k++;
						}
					}

					if (k == 0) {
						slPlansToCharge.setName(typename);
						slPlansToCharge.setIsType(1);
						slPlansToCharge.setIsValid(0);
						slPlansToCharge.setBusinessType("SmallLoan");
						slPlansToChargeService.save(slPlansToCharge);
						if (strArr.length == 9) {
							str = "{";
							for (int j = 0; j <= strArr.length - 2; j++) {
								if (j != 1) {
									str = strArr[j] + ",";
								}
							}
							str = str + strArr[strArr.length - 1];

						} else {
							str = "{";
							for (int j = 1; j <= strArr.length - 2; j++) {
								str = str + strArr[j] + ",";
							}
							str = str + strArr[strArr.length - 1];
						}
					}
				} else {
					long typeid = Long.parseLong(typeId);
					slPlansToCharge = slPlansToChargeService.get(typeid);

				}

				JSONParser parser = new JSONParser(new StringReader(str));

				try {

					SlActualToCharge slActualToCharge = (SlActualToCharge) JSONMapper
							.toJava(parser.nextValue(), SlActualToCharge.class);

					slActualToCharge.setProjectId(slSmallloanProject
							.getProjectId());
					slActualToCharge.setProjectName(slSmallloanProject
							.getProjectName());
					slActualToCharge.setProjectNumber(slSmallloanProject
							.getProjectNumber());

					slActualToCharge.setPlanChargeId(slPlansToCharge
							.getPlansTochargeId());
					if (null == slActualToCharge.getActualChargeId()) {

						slActualToCharge.setAfterMoney(new BigDecimal(0));
						slActualToCharge.setFlatMoney(new BigDecimal(0));
						if (slActualToCharge.getIncomeMoney().equals(
								new BigDecimal(0.00))) {
							slActualToCharge.setNotMoney(slActualToCharge
									.getPayMoney());
						} else {
							slActualToCharge.setNotMoney(slActualToCharge
									.getIncomeMoney());
						}
						slActualToCharge.setBusinessType("SmallLoan");
						slActualToCharge.setCompanyId(slSmallloanProject
								.getCompanyId());
						slActualToCharge.setIsCheck(flag);
						slActualToCharges.add(slActualToCharge);
					} else {

						SlActualToCharge slActualToCharge1 = slActualToChargeService
								.get(slActualToCharge.getActualChargeId());
						if (slActualToCharge1.getAfterMoney().compareTo(
								new BigDecimal(0)) == 0) {
							BeanUtil.copyNotNullProperties(slActualToCharge1,
									slActualToCharge);
							if (slActualToCharge1.getIncomeMoney().equals(
									new BigDecimal(0.00))) {
								slActualToCharge1.setNotMoney(slActualToCharge
										.getPayMoney());
							} else {
								slActualToCharge1.setNotMoney(slActualToCharge
										.getIncomeMoney());
							}
							slActualToCharge1.setPlanChargeId(slPlansToCharge
									.getPlansTochargeId());
							slActualToCharge1.setBusinessType("SmallLoan");
							slActualToCharge1.setCompanyId(slSmallloanProject
									.getCompanyId());
							slActualToCharge1.setIsCheck(flag);
							slActualToCharges.add(slActualToCharge1);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}

		return slActualToCharges;
	}

	public String updateSuperviseRecord() {
		try {
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("1"));
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("1"));
			superviseRecordService.save(slSuperviseRecord);
		} catch (Exception e) {
			logger.error("小额贷款贷中监管-更新展期记录出错:" + e.getMessage());
		}
		return "success";
	}

	/**
	 * 添加第三方保证 添加个人信息
	 * 
	 * @return
	 */
	public String savePerson() {

		String projectId = this.getRequest().getParameter("projectId");
		try {
			int personId = 0;
			String oppositeType = "";
			if ("Guarantee".equals(businessType)) {
				GLGuaranteeloanProject project = this.glGuaranteeloanProjectService
						.get(Long.valueOf(projectId));
				personId = project.getOppositeID().intValue();
				oppositeType = project.getOppositeType();
			} else if ("SmallLoan".equals(businessType)) {
				SlSmallloanProject project = this.slSmallloanProjectService
						.get(Long.valueOf(projectId));
				personId = project.getOppositeID().intValue();
				oppositeType = project.getOppositeType();
			} else if ("Financing".equals(businessType)) {
				FlFinancingProject project = this.flFinancingProjectService
						.get(Long.valueOf(projectId));
				personId = project.getOppositeID().intValue();
				oppositeType = project.getOppositeType();
			}
			if ((null != person.getId()) && (personId == person.getId())
					&& (oppositeType.equals("person_customer"))) {
				setJsonString("{success:true,msg:null}");
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}

	// 获取归档记录列表add by lisl
	public String getFilingRecords() {
		List<SlProcreditMaterials> list = slProcreditMaterialsService
				.getByProjIdBusinessTypeKey(projectId.toString(), "SmallLoan");
		List<PlArchivesMaterials> plist = plArchivesMaterialsService
				.listbyProjectId(projectId, "SmallLoan");
		for (PlArchivesMaterials p : plist) {
			SlProcreditMaterials spm = new SlProcreditMaterials();
			spm.setMaterialsName(p.getMaterialsName());
			spm.setIsPigeonhole(p.getIsPigeonhole());
			spm.setIsReceive(p.getIsReceive());
			spm.setConfirmTime(p.getPigeonholeTime());
			spm.setRecieveTime(p.getRecieveTime());
			spm.setArchiveConfirmRemark(p.getArchiveConfirmRemark());
			spm.setRecieveRemarks(p.getRemark());
			list.add(spm);
		}
		List mlist = mortgageService.ajaxGetMortgageAllDataByProjectId(
				projectId, "SmallLoan", null);
		if (null != mlist) {
			for (int i = 0; i < mlist.size(); i++) {
				VProcreditDictionary mortgage = (VProcreditDictionary) mlist
						.get(i);
				SlProcreditMaterials spm = new SlProcreditMaterials();
				spm.setMaterialsName(mortgage.getMortgagepersontypeforvalue());
				spm.setIsPigeonhole(mortgage.getIsunchain());
				spm.setIsReceive(mortgage.getIsRecieve());
				spm.setConfirmTime(mortgage.getUnchaindate());
				spm.setRecieveTime(mortgage.getRecieveDate());
				spm.setArchiveConfirmRemark(mortgage.getRemarks());
				spm.setRecieveRemarks(mortgage.getRecieveRemarks());
				list.add(spm);
			}
		}
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(list.size()).append(",result:");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		buff.append(gson.toJson(list));
		buff.append("}");

		jsonString = buff.toString();
		return SUCCESS;
	}

	// 根据taskId获取项目名称-处理人名字-任务分配时间-任务完成时限 add by lu 2011.12.14
	public String getTaskInfo() {

		SlSmallloanProject sl = null;
		GLGuaranteeloanProject gl = null;
		String projectName = "";

		TaskImpl task = (TaskImpl) taskService.getTask(taskId);
		if (businessType.equals("SmallLoan")) {
			sl = slSmallloanProjectService.get(projectId);
			if (null != sl) {
				projectName = sl.getProjectName();
			}
		} else if (businessType.equals("Guarantee")) {
			gl = glGuaranteeloanProjectService.get(projectId);
			if (null != gl) {
				projectName = gl.getProjectName();
			}
		} else if ("Financing".equals(businessType)) {
			FlFinancingProject fp = flFinancingProjectService.get(projectId);
			if (fp != null) {
				projectName = fp.getProjectName();
			}
		}
		AppUser user = ContextUtil.getCurrentUser();
		jsonString = "{success:true,data:{projectName:'" + projectName
				+ "',createTime:'"
				+ task.getCreateTime().toString().substring(0, 19)
				+ "',dueTime:'" + task.getDuedate().toString().substring(0, 19)
				+ "',creator:'" + user.getFullname() + "'}}";
		return SUCCESS;
	}

	public String isListed() {
		String oppositeType = this.getRequest().getParameter("oppositeType");
		Integer oppositeID = Integer.valueOf(this.getRequest().getParameter(
				"oppositeID"));
		String projectId = this.getRequest().getParameter("projectId");
		SlSmallloanProject sp = slSmallloanProjectService.get(Long
				.valueOf(projectId));
		String isListed = "";
		if ("company_customer".equals(oppositeType)) {
			Enterprise e = enterpriseService.getById(oppositeID);
			if (null != e.getIsBlack() && e.getIsBlack()) {
				isListed = "true";
			} else {
				isListed = "false";
			}
		} else if ("person_customer".equals(oppositeType)) {
			Person p = personServcie.getById(oppositeID);
			if (null != p.getIsBlack() && p.getIsBlack()) {
				isListed = "true";
			} else {
				isListed = "false";
			}
		}
		jsonString = "{isListed:" + isListed + ",breachComment:'"
				+ sp.getBreachComment() + "'}";
		return SUCCESS;
	}

	public String earlyrepaymentRecords() {
		List<SlEarlyRepaymentRecord> list = slEarlyRepaymentRecordService
				.listByProIdAndType(projectId, businessType);
		BpFundProject sp = null;
		FlLeaseFinanceProject fl = null;
		BigDecimal money = null;
		if ("LeaseFinance".equals(businessType)) {
			fl = flLeaseFinanceProjectService.get(projectId);
		} else {
			sp = bpFundProjectService.get(projectId);
		}
		if ("LeaseFinance".equals(businessType)) {
			money = fl.getProjectMoney();
		} else {
			money = sp.getProjectMoney();
		}
		if (null != list) {
			for (SlEarlyRepaymentRecord srr : list) {
				/*if (null != srr.getEarlyProjectMoney()) {
					srr.setSurplusProjectMoney(money.subtract(srr
							.getEarlyProjectMoney()));
					money = money.subtract(srr.getEarlyProjectMoney());
				}
				List<SlFundIntent> slist = slFundIntentService
						.getlistbyslEarlyRepaymentId(srr
								.getSlEarlyRepaymentId(), businessType,
								projectId);
				Long slSuperviseRecordId = new Long(0);
				if (null != slist && slist.size() > 0) {
					SlFundIntent sf = slist.get(0);
					if (null != sf.getSlSuperviseRecordId()) {
						slSuperviseRecordId = sf.getSlSuperviseRecordId();
					}
				}
				srr.setPayintentPeriod(slSuperviseRecordId.intValue());*/
				if ("LeaseFinance".equals(businessType)) {
					srr.setDateMode(fl.getProjectName());
				} else {
					srr.setDateMode(sp.getProjectName());
				}
			}
		}
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(null == list ? 0 : list.size()).append(",result:");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		buff.append(gson.toJson(list));
		buff.append("}");
		jsonString = buff.toString();
		return SUCCESS;
	}

	public String alterAccrualRecords() {
		List<SlAlterAccrualRecord> list = slAlterAccrualRecordService
				.getByProjectId(projectId, businessType);
		BpFundProject slProject = null;
		FlLeaseFinanceProject flProject = null;
		for (SlAlterAccrualRecord slAlterAccrualRecord : list) {
			List<SlFundIntent> slist = slFundIntentService
					.getlistbyslslAlteraccrualRecordId(slAlterAccrualRecord
							.getSlAlteraccrualRecordId(), businessType,
							projectId);
			
			if ("LeaseFinance".equals(businessType)) {
				flProject = flLeaseFinanceProjectService
						.get(slAlterAccrualRecord.getProjectId());
			} else {
				slProject = bpFundProjectService.get(slAlterAccrualRecord.getProjectId());
			}
			if (null != flProject) {
				slAlterAccrualRecord.setDateMode(flProject.getProjectName());
			} else if (null != slProject) {
				slAlterAccrualRecord.setDateMode(slProject.getProjectName());
			}
		}
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
				.append(list.size()).append(",result:");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		buff.append(gson.toJson(list));
		buff.append("}");
		jsonString = buff.toString();
		return SUCCESS;
	}
	/**
	 * 信贷流程-制定资金方案保存方法
	 * 
	 * @return
	 */
	public String updateBpFundInfo() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String bidPlanId=this.getRequest().getParameter("bidPlanId");
			String pFundsDatas = this.getRequest().getParameter("pFundsDatas");
			String fundsJson=this.getRequest().getParameter("fundsJson");
			String chargeJson=this.getRequest().getParameter("chargeJson");
			String isCheck=this.getRequest().getParameter("isCheck");
			PlBidPlan plan=plBidPlanService.get(Long.valueOf(bidPlanId));
			BeanUtil.copyNotNullProperties(plan, plBidPlan);
			plBidPlanService.merge(plan);
			BpFundProject fundProject =null;
			if(plan.getProType().equals("P_Dir") || plan.getProType().equals("B_Dir")){
				// 平台资金款项信息
				fundProject= bpFundProjectService.get(platFormBpFundProject.getId());
			}
			if(plan.getProType().equals("P_Or") || plan.getProType().equals("B_Or")){
				// 自有资金款项信息
				fundProject = bpFundProjectService.get(ownBpFundProject.getId());
			}
			//投资人的放款收息表
			bpFundIntentService.saveFundIntent(pFundsDatas, plan, fundProject, Short.valueOf(isCheck));
			
			
			//保存费用信息
			if(null!=chargeJson && !chargeJson.equals("")){
				slActualToChargeService.savejson(chargeJson, fundProject.getProjectId(), fundProject.getBusinessType(), Short.valueOf(isCheck), fundProject.getCompanyId());
			}

			// 意见说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("信贷流程-制定资金方案保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}

	public String loanFinished() {

		if (businessType != null && !"".equals(businessType)
				&& projectId != null && !"".equals(projectId)) {
			String fundType = this.getRequest().getParameter("fundType");
			if (fundType != null && !"".equals(fundType)) {
				List<SlFundIntent> intentList = slFundIntentService
						.getByProjectId3(projectId, businessType, fundType);
				BigDecimal incomeMoney = new BigDecimal(0);
				BigDecimal afterMoney = new BigDecimal(0);
				for (SlFundIntent slFundIntent : intentList) {
					if (slFundIntent.getIncomeMoney() != null) {
						incomeMoney=incomeMoney.add(slFundIntent.getIncomeMoney());
					}
					if (slFundIntent.getAfterMoney() != null) {
						afterMoney=afterMoney.add(slFundIntent.getAfterMoney());
					}
				}
				if (incomeMoney.compareTo(afterMoney)!=0 ) {
					jsonString = "{success:false}";
				} else {
					String fundProjectId=this.getRequest().getParameter("fundProjectId");
					BpFundProject sp = bpFundProjectService.get(Long.valueOf(fundProjectId));
					sp.setProjectStatus(Constants.PROJECT_STATUS_COMPLETE);
					bpFundProjectService.merge(sp);
					jsonString = "{success:true}";
				}
			}
		 //贷款结清释放保证金及综合授信
			slFundIntentService.releaseCashBank(businessType, projectId);
		}
		return SUCCESS;
	}

	/**
	 * 
	 * 小贷确认终审通过意见
	 */
	public String updateSmallLoanConfirm() {
		try {
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("1"));
			// if
			// (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
			// int a = 0;
			// for (SlFundIntent s : slFundIntents) {
			// if (s.getFundType().equals("principalRepayment")) {
			// a++;
			// }
			// slSmallloanProject.setPayintentPeriod(a);
			//					
			// }
			// }
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");
			if (isDeleteAllFundIntent.equals("1")) {
				List<SlFundIntent> allintent = this.slFundIntentService
						.getByProjectId1(slSmallloanProject.getProjectId(),
								"SmallLoan");
				for (SlFundIntent s : allintent) {
					slFundIntentService.evict(s);
					if (s.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
						this.slFundIntentService.remove(s);
					}
				}

			}
			for (SlFundIntent a : slFundIntents) {
				slFundIntentService.evict(a);
				this.slFundIntentService.save(a);
			}
			// 保存杂项费用
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));
			List<SlActualToCharge> allactual = this.slActualToChargeService
					.listbyproject(slSmallloanProject.getProjectId(),
							"SmallLoan");
			for (SlActualToCharge a : slActualToCharges) {
				this.slActualToChargeService.save(a);

			}
			SlSmallloanProject persistent = this.slSmallloanProjectService
					.get(slSmallloanProject.getProjectId());
			String degree = this.getRequest().getParameter("degree");
			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			String isAheadPay = this.getRequest().getParameter("isAheadPay");
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			slSmallloanProject.setCompanyId(persistent.getCompanyId());
			// end
			BeanUtils.copyProperties(slSmallloanProject, persistent,
					new String[] { "id", "operationType", "flowType",
							"mineType", "mineId", "oppositeType", "oppositeID",
							"projectName", "projectNumber", "oppositeType",
							"businessType", "createDate" });
			Map<String, BigDecimal> map = slFundIntentService
					.saveProjectfiance(persistent.getProjectId(), "SmallLoan");
			persistent.setPaychargeMoney(map.get("paychargeMoney"));
			persistent.setIncomechargeMoney(map.get("incomechargeMoney"));
			persistent.setAccrualMoney(map.get("loanInterest"));
			persistent.setConsultationMoney(map.get("consultationMoney"));
			persistent.setServiceMoney(map.get("serviceMoney"));
			this.slSmallloanProjectService.save(persistent);
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	/**
	 * 
	 * 微贷确认终审通过意见
	 */
	public String updateConfirmComments() {
		try {
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			// 保存款项计划
			List<SlFundIntent> slFundIntents = new ArrayList<SlFundIntent>();
			slFundIntents = savejsonintent(fundIntentJsonData,
					slSmallloanProject, Short.parseShort("1"));
			// if
			// (!slSmallloanProject.getAccrualtype().equals("singleInterest")) {
			// int a = 0;
			// for (SlFundIntent s : slFundIntents) {
			// if (s.getFundType().equals("principalRepayment")) {
			// a++;
			// }
			// slSmallloanProject.setPayintentPeriod(a);
			//					
			// }
			// }
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");
			if (isDeleteAllFundIntent.equals("1")) {
				List<SlFundIntent> allintent = this.slFundIntentService
						.getByProjectId1(slSmallloanProject.getProjectId(),
								"SmallLoan");
				for (SlFundIntent s : allintent) {
					slFundIntentService.evict(s);
					if (s.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
						this.slFundIntentService.remove(s);
					}
				}
			}
			for (SlFundIntent a : slFundIntents) {
				slFundIntentService.evict(a);
				this.slFundIntentService.save(a);
			}
			// 保存杂项费用
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("0"));
			List<SlActualToCharge> allactual = this.slActualToChargeService
					.listbyproject(slSmallloanProject.getProjectId(),
							"SmallLoan");
			for (SlActualToCharge a : slActualToCharges) {
				this.slActualToChargeService.save(a);

			}
			SlSmallloanProject persistent = this.slSmallloanProjectService
					.get(slSmallloanProject.getProjectId());
			String degree = this.getRequest().getParameter("degree");
			if (null != degree && !"".equals(degree)) {
				slSmallloanProject.setAppUserId(degree);
			}
			String isAheadPay = this.getRequest().getParameter("isAheadPay");
			if (isAheadPay != null) {
				slSmallloanProject.setIsAheadPay(Short.valueOf("1"));
			} else {
				slSmallloanProject.setIsAheadPay(Short.valueOf("0"));
			}
			ProjectActionUtil pu = new ProjectActionUtil();
			pu.getSmallloanMode(slSmallloanProject);
			slSmallloanProject.setCompanyId(persistent.getCompanyId());
			// end
			BeanUtils.copyProperties(slSmallloanProject, persistent,
					new String[] { "id", "operationType", "flowType",
							"mineType", "mineId", "oppositeType", "oppositeID",
							"projectName", "projectNumber", "oppositeType",
							"businessType", "createDate" });
			Map<String, BigDecimal> map = slFundIntentService
					.saveProjectfiance(persistent.getProjectId(), "SmallLoan");
			persistent.setPaychargeMoney(map.get("paychargeMoney"));
			persistent.setIncomechargeMoney(map.get("incomechargeMoney"));
			persistent.setAccrualMoney(map.get("loanInterest"));
			persistent.setConsultationMoney(map.get("consultationMoney"));
			persistent.setServiceMoney(map.get("serviceMoney"));
			this.slSmallloanProjectService.save(persistent);
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public String getEarlyMoney() {
		List<VFundDetail> list = vFundDetailService.listByFundType(
				"principalRepayment", projectId, "SmallLoan");
		BigDecimal payMoney = new BigDecimal(0);
		for (VFundDetail VFundDetail : list) {
			payMoney = payMoney
					.add(new BigDecimal(VFundDetail.getAfterMoney()));
		}

		jsonString = "{success:true,payMoney:" + payMoney + "}";
		return SUCCESS;
	}

	public String getPayIntentPeriod() {
		String payintentPeriod = this.getRequest().getParameter(
				"payintentPeriod");
		int period = 0;
		if (null == payintentPeriod || payintentPeriod.equals("")) {
			if (null != businessType && businessType.equals("SmallLoan")) {
				SlSmallloanProject project = slSmallloanProjectService
						.get(projectId);
				if (null != project.getPayintentPeriod()) {
					period = project.getPayintentPeriod();
				}
			} else if (null != businessType && businessType.equals("Financing")) {
				FlFinancingProject project = flFinancingProjectService
						.get(projectId);
				if (null != project.getPayintentPeriod()) {
					period = project.getPayintentPeriod();
				}
			} else if (null != businessType && businessType.equals("Pawn")) {
				PlPawnProject project = plPawnProjectService.get(projectId);
				if (null != project.getPayintentPeriod()) {
					period = project.getPayintentPeriod();
				}
			} else if (null != businessType
					&& businessType.equals("LeaseFinance")) {
				FlLeaseFinanceProject project = flLeaseFinanceProjectService
						.get(projectId);
				if (null != project.getPayintentPeriod()) {
					period = project.getPayintentPeriod();
				}
			} else if (null != businessType
					&& businessType.equals("ExhibitionBusiness")) {
				SlSuperviseRecord reocrd = slSuperviseRecordService
						.get(projectId);
				if (null != reocrd.getPayintentPeriod()) {
					period = reocrd.getPayintentPeriod();
				}
			}
		}
		if (null != payintentPeriod && !"".equals(payintentPeriod)) {
			period = Integer.valueOf(payintentPeriod);
		}
		StringBuffer buff = new StringBuffer("[");
		// if (null != payintentPeriod && !"".equals(payintentPeriod)) {
		for (int i = 0; i < period + 1; i++) {
			buff.append("['").append((i)).append("','").append((i)).append(
					"'],");
		}
		if (period > 0) {
			buff.deleteCharAt(buff.length() - 1);
		}
		// }
		buff.append("]");
		setJsonString(buff.toString());
		return SUCCESS;
	}

	public void initPersionP2p(BpFundProject platFormFund,
			BpPersionDirPro bpPersionDirPro, Person person) {
		bpPersionDirPro.setProId(platFormFund.getId());// 项目Id
		bpPersionDirPro.setBusinessType(platFormFund.getOperationType());// 业务品种
		bpPersionDirPro.setProName(platFormFund.getProjectName() + "_P2P");// 项目名称
		bpPersionDirPro.setProNumber(platFormFund.getProjectNumber() + "_P2P");// 项目编号
		bpPersionDirPro.setYearInterestRate(platFormFund.getYearAccrualRate());// 年化利率
		bpPersionDirPro.setMonthInterestRate(platFormFund.getAccrual());// 月化利率
		bpPersionDirPro.setDayInterestRate(platFormFund.getDayAccrualRate());// 日化利率
		bpPersionDirPro.setTotalInterestRate(platFormFund.getSumAccrualRate());// 合计利率
		bpPersionDirPro.setInterestPeriod("1");// 计息周期
		if ("sameprincipalandInterest".equals(platFormFund.getAccrualtype())) {// 等额本息
			bpPersionDirPro.setPayIntersetWay("1");
		} else if ("sameprincipal".equals(platFormFund.getAccrualtype())) {// 等额本金
			bpPersionDirPro.setPayIntersetWay("2");
		} else if ("sameprincipalsameInterest".equals(platFormFund
				.getAccrualtype())) {// 等本等息
			bpPersionDirPro.setPayIntersetWay("3");
		} else if ("singleInterest".equals(platFormFund.getAccrualtype())) {// 按期收息,到期还本
			bpPersionDirPro.setPayIntersetWay("4");
		}
		if ("1".equals(platFormFund.getIsInterestByOneTime())) {// 一次性支付全部利息
			bpPersionDirPro.setPayIntersetWay("5");
		}
		bpPersionDirPro.setBidMoney(platFormFund.getPlatFormJointMoney());// 未招标金额
		bpPersionDirPro.setLoanLife(platFormFund.getPayintentPeriod());// 期望借款期限
		bpPersionDirPro.setBidTime(null);// 发标日期
		bpPersionDirPro.setCreateTime(new Date());// 创建日期
		bpPersionDirPro.setUpdateTime(new Date());// 修改日期
		bpPersionDirPro.setKeepStat(0);// 维护状态
		bpPersionDirPro.setSchemeStat(0);// 方案状态

		bpPersionDirPro.setPersionId(Long.valueOf(person.getId()));// 个人Id
		bpPersionDirPro.setPersionName(person.getName());// 个人名称
		bpPersionDirPro.setSex(dictionaryService.get(
				Long.valueOf(person.getSex())).getItemValue());// 性别
		Dictionary dictionary1 = dictionaryService.get(Long.valueOf(person
				.getDgree()));
		if (null != dictionary1) {
			bpPersionDirPro.setEducation(dictionary1.getItemValue());// 学历
		}
		Dictionary dictionary2 = dictionaryService.get(Long.valueOf(person
				.getMarry()));
		if (null != dictionary2) {
			bpPersionDirPro.setMarriage(dictionary2.getItemValue());// 婚姻
		}
		Dictionary dictionary3 = dictionaryService.get(Long.valueOf(person
				.getJob()));
		if (null != dictionary3) {
			bpPersionDirPro.setPosition(dictionary3.getItemValue());// 职务
		}
		bpPersionDirPro.setAge(person.getAge());// 年龄
		bpPersionDirPro.setAddress(person.getFamilyaddress());// 现居住地
		bpPersionDirPro.setWorkTime(String.valueOf(person.getJobstarttime()));// 工作时间
		bpPersionDirPro.setWorkCity(person.getUnitaddress());// 工作城市
		bpPersionDirPro.setUserName("");// 用户名

		List<CsPersonHouse> csPersonHouseList = csPersonHouseService
				.getByPersonId(person.getId().toString()); // 房产 房贷
		if (null != csPersonHouseList && csPersonHouseList.size() > 0) {
			bpPersionDirPro.setHouseProperty(0);
			boolean flag = false;
			for (int i = 0; i < csPersonHouseList.size(); i++) {
				if ("1".equals(csPersonHouseList.get(i).getIsMortgage())) {
					flag = true;
					break;
				} else {
					continue;
				}
			}
			if (flag) {
				bpPersionDirPro.setHouseLoan(0);
			} else {
				bpPersionDirPro.setHouseLoan(1);
			}
		} else {
			bpPersionDirPro.setHouseProperty(1);
		}
		List<CsPersonCar> csPersonCarList = csPersonCarService
				.getByPersonId(person.getId().toString()); // 车产 车贷
		if (null != csPersonCarList && csPersonCarList.size() > 0) {
			bpPersionDirPro.setVehicleProperty(0);
			boolean flag = false;
			for (int i = 0; i < csPersonCarList.size(); i++) {
				if ("1".equals(csPersonCarList.get(i).getIsMortgage())) {
					flag = true;
					break;
				} else {
					continue;
				}
			}
			if (flag) {
				bpPersionDirPro.setVehicleLoan(0);
			} else {
				bpPersionDirPro.setVehicleLoan(1);
			}
		} else {
			bpPersionDirPro.setVehicleProperty(1);
		}
	}

	/**
	 * 信贷流程-制定资金方案保存方法
	 * 
	 * @return
	 *//*
	public String updateBpFundInfo() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String fundIntentJsonData = this.getRequest().getParameter(
					"fundIntentJsonData");
			String chargeJson = this.getRequest().getParameter(
			"chargeJson");
			String projectId = this.getRequest().getParameter(
					"slSmallloanProject.projectId");
			String bidPlanId = this.getRequest().getParameter(
			"bidPlanId");
			if(chargeJson!=null&&!"".equals(chargeJson)&&!"undefined".equals(chargeJson)){
					slActualToChargeService.saveJson(chargeJson,Long.parseLong(projectId),"smallloan",(short)0,(new Long(0)),Long.parseLong(bidPlanId));
				}

			// 自有资金款项信息
			BpFundProject ownFund = bpFundProjectService.getByProjectId(Long
					.valueOf(projectId), Short.valueOf("0"));
			if (null != ownFund) {
			//	BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
				bpFundProjectService.merge(ownFund);
				// 保存款项计划
				slFundIntentService.savejson(fundIntentJsonData, new Long(
						projectId), "SmallLoan", Short.parseShort("1"), ownFund
						.getCompanyId(), ownFund.getId(), ownFund
						.getFundResource());
			}

			// 平台资金款项信息
			BpFundProject platFormFund = bpFundProjectService.getByProjectId(
					Long.valueOf(projectId), Short.valueOf("1"));
			if (null != platFormFund) {
				BeanUtil.copyNotNullProperties(platFormFund,
						platFormBpFundProject);
				bpFundProjectService.merge(platFormFund);
			}

			// 保存个人直投标项目缓信息
			BpPersionDirPro bpPersionDirPro = bpPersionDirProService
					.getByBpFundProjectId(platFormFund.getId());
			if (null == bpPersionDirPro) {
				bpPersionDirPro = new BpPersionDirPro();
				Person person = personService.getById(Integer
						.parseInt((platFormFund.getOppositeID().toString())));// 客户信息
				this.initPersionP2p(platFormFund, bpPersionDirPro, person);
				bpPersionDirProService.save(bpPersionDirPro);
				// bpPersionDirPro.setMonthIncome();
				// bpPersionDirPro.setCompanyIndustry(aValue)//公司行业 个人所在公司信息
				// bpPersionDirPro.setCompanyScale(aValue)//公司规模
			} else {
				Person person = personService.getById(Integer
						.parseInt((platFormFund.getOppositeID().toString())));// 客户信息
				this.initPersionP2p(platFormFund, bpPersionDirPro, person);
				bpPersionDirProService.merge(bpPersionDirPro);
			}

			// 意见说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("信贷流程-制定资金方案保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}*/

	/****
	 * 读取信贷流程项目相关信息
	 * 
	 * @author zcb
	 * @return
	 */
	public String getCreditLoanProjectInfo() {
		String projectId = this.getRequest().getParameter("slProjectId"); // 项目ID
		String taskId = this.getRequest().getParameter("slTaskId"); // 任务ID

		String bidPlanId = this.getRequest().getParameter("bidPlanId");
		//读取项目信息
		SlSmallloanProject project = this.slSmallloanProjectService.get(Long.valueOf(projectId));
		if(project.getPurposeType()!=null){
			Dictionary dictionary = dictionaryService.get(project.getPurposeType());
			if(dictionary!=null&&project.getPurposeTypeStr()==null){
				project.setPurposeTypeStr(dictionary.getItemValue());
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 读取个人客户信息
			Person p = new Person();
			Enterprise enterprise = new Enterprise();
			short sub = 0;
			if (project.getOppositeType().equals("company_customer")) {
				sub = 0;
				Enterprise enterprise1 = enterpriseService.getById(project
						.getOppositeID().intValue());
				if (enterprise1.getLegalpersonid() != null) {
					p = this.personService.getById(enterprise1
							.getLegalpersonid());
					map.put("person", p);
				}
				if (null != enterprise1.getHangyeType()) {
					if (null != areaDicService.getById(enterprise1
							.getHangyeType())) {
						enterprise1.setHangyeName(areaDicService.getById(
								enterprise1.getHangyeType()).getText());
					}
				}
				map.put("enterprise", enterprise1);
				List<EnterpriseBank> list = enterpriseBankService.getBankList(
						project.getOppositeID().intValue(), sub, Short
								.valueOf("0"), Short.valueOf("0"));
				if (null != list && list.size() > 0) {
					EnterpriseBank bank = list.get(0);
					map.put("enterpriseBank", bank);
				}

			} else if (project.getOppositeType().equals("person_customer")) {
				sub = 1;
				p = this.personService.getById(project.getOppositeID()
						.intValue());
				map.put("person", p);
				if (null != p) {
					if (null != p.getId()) {
//						if (null != p.getMarry() && p.getMarry() == 317) {
							Spouse spouse = spouseService.getByPersonId(p
									.getId());
							if(null!=spouse){
								map.put("spouse", spouse);
							}
//						}
					}
				}
			}
		
			String mineName = getMainName(project.getMineType(), project.getMineId());
			String managementConsultingMineName = getMainName(project.getManagementConsultingMineType(),project.getManagementConsultingMineId());
			String financeServiceMineName = getMainName(project.getFinanceServiceMineType(),project.getFinanceServiceMineId());
			//
			map.put("mineName", mineName);
			map.put("financeServiceMineName", financeServiceMineName);
			map.put("managementConsultingMineName", managementConsultingMineName);
			project.setMineName(mineName);
			project.setManagementConsultingMineName(managementConsultingMineName);
			project.setFinanceServiceMineName(financeServiceMineName);
			map.put("person", p);
			// 个人旗下公司
			WorkCompany workCompany = workCompanyService
					.getWorkCompanyByPersonId(p.getId());
			map.put("workCompany", workCompany);
			// 借款需求
			if (null != projectId && !"".equals(projectId)) {
				List<BpMoneyBorrowDemand> list = this.bpMoneyBorrowDemandService
						.getMessageByProjectID(Long.valueOf(projectId));
				if (list.size() > 0) {
					bpMoneyBorrowDemand = list.get(0);
					map.put("bpMoneyBorrowDemand", bpMoneyBorrowDemand);
				}
			}
			SlActualToCharge slActualToCharge=new SlActualToCharge();
			//手续清单
			if (null != projectId && !"".equals(projectId)) {
				List<SlActualToCharge>  list1 = this.slActualToChargeService.listbyproject(Long.valueOf(projectId));
				if(null!=list1&&list1.size()!=0){
					slActualToCharge=list1.get(0);
					map.put("slActualToCharge", slActualToCharge);
					}
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取信贷流程项目相关信息:" + e.getMessage());
		}

		List<ProcessRun> runList = processRunService.getByProcessNameProjectId(
				project.getProjectId(), project.getBusinessType(), project
						.getFlowType());
		if (runList != null && runList.size() != 0) {
			List<TaskSignData> tsdList = taskSignDataService.getByRunId(runList
					.get(0).getRunId());
			if (tsdList.size() > 0) {
				map.put("isOnline", true);
			} else {
				map.put("isOnline", false);
			}
		}
		// 自有资金
		BpFundProject ownFund = bpFundProjectService.getByProjectId(Long
				.valueOf(projectId), Short.valueOf("0"));
		if (null != ownFund) {
			map.put("ownBpFundProject", ownFund);
		}

		// 平台资金
		BpFundProject platFormFund = bpFundProjectService.getByProjectId(Long
				.valueOf(projectId), Short.valueOf("1"));
		if (null != platFormFund) {
			map.put("platFormBpFundProject", platFormFund);
		}
		if (project.getAvailableType() != null
				&& project.getAvailableType().equals("1")) {
			project.setAvailableType("小贷公司");
		} else if (project.getAvailableType() != null
				&& project.getAvailableType().equals("2")) {
			project.setAvailableType("担保公司");
		}
		if (project.getAvailableId() != null) {
			InvestEnterprise enter = investEnterpriseService.get(project
					.getAvailableId());
			if (enter != null) {
				project.setValilableName(enter.getEnterprisename());
				if (enter.getNowCreditLimit() != null)
					project.setLaveCredit(enter.getNowCreditLimit().toString());
			}
		}
		String businessTypeKey = creditProjectService.getGlobalTypeValue(
				"businessType", project.getBusinessType(), project
						.getProjectId());
		String operationTypeKey = creditProjectService.getGlobalTypeValue(
				"operationType", project.getBusinessType(), project
						.getProjectId());
		String definitionTypeKey = creditProjectService.getGlobalTypeValue(
				"definitionType", project.getBusinessType(), project
						.getProjectId());
		
		
		map.put("slSmallloanProject", project);
		
		map.put("businessTypeKey", businessTypeKey);
		map.put("operationTypeKey", operationTypeKey);
		map.put("definitionTypeKey", definitionTypeKey);
		if (null != project.getProductId() && !"".equals(project.getProductId())) {
			BpProductParameter bpProductParameter = bpProductParameterService.get(project.getProductId());
			if(null!=bpProductParameter){
				map.put("bpProductParameter", bpProductParameter);
			}
			if("信贷业务".equals(project.getFlowType())){
				P2pLoanProduct p2pProduct=p2pLoanProductService.get(project.getProductId());
				if(null!=p2pProduct){
					map.put("bpProductParameter", p2pProduct);
				}
			}
		}
		if (null != taskId && !"".equals(taskId)) {
			ProcessForm pform = processFormService.getByTaskId(taskId);
			if (pform == null) {
				pform = creditProjectService.getCommentsByTaskId(taskId);
			}
			if (pform != null && pform.getComments() != null
					&& !"".equals(pform.getComments())) {
				map.put("comments", pform.getComments());
			}
		}
		//plBidPlan
		if(bidPlanId!=null&&!"".equals(bidPlanId)&&!"undefined".equals(bidPlanId)){
			PlBidPlan plBidPlan = plBidPlanService.get(Long.parseLong(bidPlanId));
			if(plBidPlan!=null){
				map.put("plBidPlan", plBidPlan);
			}
		}
		if(project.getAppUserId()!=null&&!project.getAppUserId().contains(",")){
			AppUser au = appUserService.get(Long.valueOf(project.getAppUserId()));
			map.put("appUserNumber", au.getUserNumber());
		}
		doJson(map);	
		return SUCCESS;
	}
	private String getMainName(String type,Long id){
		if(type!=null&&id!=null)
			if(type.equals("company_ourmain")){
				if(id!=null){
					SlCompanyMain slCompanyMain = slCompanyMainService.get(id);
					if(slCompanyMain!=null)
					return slCompanyMain.getCorName();
				}
				
			}else{
				SlPersonMain slPersonMain = slPersonMainService.get(id);
				if(slPersonMain!=null)
					return slPersonMain.getName();
			}
		return null;
	}
	/**
	 * 读取小额贷款项目信息
	 */
	public String getInfo() {
		String projectId = this.getRequest().getParameter("slProjectId"); // 项目ID
		String productId = this.getRequest().getParameter("productId");
		String taskId = this.getRequest().getParameter("slTaskId"); // 任务ID
		SlSmallloanProject project = this.slSmallloanProjectService.get(Long
				.valueOf(projectId));
		Map<String, Object> map = new HashMap<String, Object>();
		String mineName = "";
		String financeServiceMineName = "";
		String managementConsultingMineName = "";
		if (null != project.getFinanceServiceMineType()
				&& !"".equals(project.getFinanceServiceMineType())) {
			if (null != project.getFinanceServiceMineId()
					&& !"".equals(project.getFinanceServiceMineId())) {
				if (project.getFinanceServiceMineType().equals(
						"company_ourmain")) {
					financeServiceMineName = this.slCompanyMainService.get(
							project.getFinanceServiceMineId()).getCorName();
				} else {
					financeServiceMineName = this.slPersonMainService.get(
							project.getFinanceServiceMineId()).getName();
				}
			}
		}

		if (null != project.getManagementConsultingMineType()
				&& !"".equals(project.getManagementConsultingMineType())) {
			if (null != project.getManagementConsultingMineId()
					&& !"".equals(project.getManagementConsultingMineId())) {
				if (project.getManagementConsultingMineType().equals(
						"company_ourmain")) {
					managementConsultingMineName = this.slCompanyMainService
							.get(project.getManagementConsultingMineId())
							.getCorName();
				} else {
					managementConsultingMineName = this.slPersonMainService
							.get(project.getManagementConsultingMineId())
							.getName();
				}
			}
		}
		// 集团办 主体单位名称 获取 add by gaoqingrui
		Boolean flag = Boolean.valueOf(AppUtil.getSystemIsGroupVersion());

		try {
			if("true".equals(AppUtil.getSystemIsGroupVersion())&&null==this.organizationService.get(project.getMineId())){
				project.setMineName("");
			}else if(!"true".equals(AppUtil.getSystemIsGroupVersion())&&null==this.slCompanyMainService.get(project.getMineId())){
				project.setMineName("");
			}else if(null==project.getMineId()||0==project.getMineId()){
				project.setMineName("");
			}else if (flag == false) {
				
				if (project.getMineType().equals("company_ourmain")) {
					
					if ("true".equals(AppUtil.getSystemIsGroupVersion())) {
						mineName = this.organizationService.get(
								project.getMineId()).getOrgName();
					} else {
						
						mineName = this.slCompanyMainService.get(
								project.getMineId()).getCorName();
					}
					// getMineId 为 organization 下的 id 原来是 企业主体下的id
					// mineName =
					// this.organizationService.get(project.getMineId()).getOrgName();
				} else if (project.getMineType().equals("person_ourmain")) {
					mineName = this.slPersonMainService
							.get(project.getMineId()).getName();
				}
			} else {
				// if(project.getMineType().equals("company_ourmain")){
				// mineName =
				// this.companyMainService.get(project.getMineId()).getCorName();
				// getMineId 为 organization 下的 id 原来是 企业主体下的id
				if (null != this.organizationService.get(project.getMineId())) {
					mineName = this.organizationService
							.get(project.getMineId()).getOrgName();
				}
				/*
				 * }else if (project.getMineType().equals("person_ourmain")){
				 * mineName =
				 * this.slPersonMainService.get(project.getMineId()).getName();
				 * }
				 */
			}

			Person p = new Person();
			// if判断是企业客户 elseif是个人客户
			Short sub = 0;
			if (project.getOppositeType().equals("company_customer")) {
				sub = 0;
				Enterprise enterprise1 = enterpriseService.getById(project
						.getOppositeID().intValue());
				if (enterprise1.getLegalpersonid() != null) {
					p = this.personService.getById(enterprise1
							.getLegalpersonid());
					map.put("person", p);
				}
				if (null != enterprise1.getHangyeType()) {
					if (null != areaDicService.getById(enterprise1
							.getHangyeType())) {
						enterprise1.setHangyeName(areaDicService.getById(
								enterprise1.getHangyeType()).getText());
					}
				}
				map.put("enterprise", enterprise1);

			} else if (project.getOppositeType().equals("person_customer")) {
				sub = 1;
				p = this.personService.getById(project.getOppositeID()
						.intValue());
				map.put("person", p);
				if (null != p) {
					if (null != p.getId()) {
						if (null != p.getMarry() && p.getMarry() == 317) {
							Spouse spouse = spouseService.getByPersonId(p
									.getId());
							map.put("spouse", spouse);
						}
					}
				}
			}
			if (null != project.getOppositeID()) {
				List<EnterpriseBank> list = enterpriseBankService.getBankList(
						project.getOppositeID().intValue(), sub, Short
								.valueOf("0"), Short.valueOf("0"));
				if (null != list && list.size() > 0) {
					EnterpriseBank bank = list.get(0);
					map.put("enterpriseBank", bank);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ProjectAction:" + e.getMessage());
		}

		List<ProcessRun> runList = processRunService.getByProcessNameProjectId(
				project.getProjectId(), project.getBusinessType(), project
						.getFlowType());
		if (runList != null && runList.size() != 0) {
			List<TaskSignData> tsdList = taskSignDataService.getByRunId(runList
					.get(0).getRunId());
			if (tsdList.size() > 0) {
				map.put("isOnline", true);
			} else {
				map.put("isOnline", false);
			}
		}

		String appuers = "";
		if (null != project.getAppUserId()) {
			String[] appstr = project.getAppUserId().split(",");
			Set<AppUser> userSet = this.appUserService.getAppUserByStr(appstr);
			for (AppUser s : userSet) {
				appuers += s.getFullname() + ",";
			}
		}
		if (appuers.length() > 0) {
			appuers = appuers.substring(0, appuers.length() - 1);
		}
		StringBuffer textBuffer = new StringBuffer(mineName);
		project.setMineName(textBuffer.toString());
		project.setAppUsers(appuers);
		project.setAppUserName(appuers);
		// 推介机构
		if (project.getAvailableType() != null
				&& project.getAvailableType().equals("1")) {
			project.setAvailableType("小贷公司");
		} else if (project.getAvailableType() != null
				&& project.getAvailableType().equals("2")) {
			project.setAvailableType("担保公司");
		}
		if (project.getAvailableId() != null) {
			// if(project.getCredit()==null||"".equals(project.getCredit())||"0".equals(project.getCredit())){
			InvestEnterprise enter = investEnterpriseService.get(project
					.getAvailableId());
			if (enter != null) {
				project.setValilableName(enter.getEnterprisename());
				if (enter.getNowCreditLimit() != null)
					project.setLaveCredit(enter.getNowCreditLimit().toString());
			}
			// }
		}
		if (null == project.getIntentDate()
				|| "".equals(project.getIntentDate())) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (project.getPayaccrualType() == null
					|| project.getPayaccrualType().equals("")) {
				project.setPayaccrualType("monthPay");
			}
			/*Date intentDate = StatsPro.getIntentDate(sdf.format(project
					.getPoupseDate()), project.getPayaccrualType(),// 还款周期
					project.getPoupsePeriod(), null);
			project.setIntentDate(intentDate);*/
		}
		String slSuperviseRecordId = this.getRequest().getParameter(
				"slSuperviseRecordId"); //	 
		if (null != slSuperviseRecordId && !"".equals(slSuperviseRecordId)) {
			SlSuperviseRecord slSuperviseRecord = this.slSuperviseRecordService
					.get(Long.valueOf(slSuperviseRecordId));
			map.put("slSuperviseRecord", slSuperviseRecord);
		}
		List<SlFundIntent> list = slFundIntentService.getByProjectId3(project
				.getProjectId(), project.getBusinessType(), "principalLending");
		if (null != list && list.size() > 0) {
			SlFundIntent f = list.get(0);
			map.put("startDate", f.getIntentDate());
		}
		// Long times = processFormService.getActvityExeTimes(runId,
		// "slnExaminationArrangement");//是否经过审贷会
		String businessTypeKey = creditProjectService.getGlobalTypeValue(
				"businessType", project.getBusinessType(), project
						.getProjectId());
		String operationTypeKey = creditProjectService.getGlobalTypeValue(
				"operationType", project.getBusinessType(), project
						.getProjectId());
		String definitionTypeKey = creditProjectService.getGlobalTypeValue(
				"definitionType", project.getBusinessType(), project
						.getProjectId());
		// map.put("times", times);
		// 设置还款日期
		map.put("slSmallloanProject", project);
		map.put("mineName", mineName);
		map.put("financeServiceMineName", financeServiceMineName);
		map.put("managementConsultingMineName", managementConsultingMineName);
		map.put("businessType", project.getBusinessType());
		map.put("isAheadPay", project.getIsAheadPay());
		map.put("businessTypeKey", businessTypeKey);
		map.put("operationTypeKey", operationTypeKey);
		map.put("definitionTypeKey", definitionTypeKey);
		map
				.put("flowTypeKey", this.proDefinitionService
						.getProdefinitionByProcessName(project.getFlowType())
						.getName());
		if(null!=project.getMineType()&&!"".equals(project.getMineType())){
			if(null!=this.dictionaryIndependentService.getByDicKey(project.getMineType())&&this.dictionaryIndependentService.getByDicKey(project.getMineType()).size()!=0)
				map.put("mineTypeKey", this.dictionaryIndependentService.getByDicKey(project.getMineType()).get(0).getItemValue());
		}
		if (null != project.getProductId()
				&& !"".equals(project.getProductId())) {
			BpProductParameter bpProductParameter = bpProductParameterService
					.get(project.getProductId());
			map.put("bpProductParameter", bpProductParameter);
		}
			
		try {
			if (null != project.getLoanLimit()
					&& !project.getLoanLimit().equals("")) {
				DictionaryIndependent dic = this.dictionaryIndependentService
						.getByDicKey(project.getLoanLimit()).get(0);
				if (null != dic) {
					map.put("smallLoanTypeKey", dic.getItemValue());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != taskId && !"".equals(taskId)) {
			ProcessForm pform = processFormService.getByTaskId(taskId);
			if (pform == null) {
				pform = creditProjectService.getCommentsByTaskId(taskId);
			}
			if (pform != null && pform.getComments() != null
					&& !"".equals(pform.getComments())) {
				map.put("comments", pform.getComments());
			}
		}
		// add by lisl 2012-10-10 监管记录信息
		/*
		 * if (this.getRequest().getParameter("superviseManageId") != null) {
		 * String smId = this.getRequest().getParameter("superviseManageId"); if
		 * (!"".equals(smId)) { Long id = Long.valueOf(smId); SlSupervisemanage
		 * ss = slSupervisemanageService.get(id); map.put("slSupervisemanage",
		 * ss); } }
		 */

		doJson(map);
		// 整合以前的数据
		// doJson(getNewMap(map));

		return SUCCESS;
	}

	public String saveProjectFundIntentInfo() {
		String tag = this.getRequest().getParameter("tag");
		String projectId = this.getRequest().getParameter("projectId");
		if (tag != null && tag.equals("1")) {
			SlSmallloanProject persistent = slSmallloanProjectService.get(Long
					.valueOf(projectId));
			BeanUtils.copyProperties(slSmallloanProject, persistent,
					new String[] { "id", "operationType", "flowType",
							"mineType", "mineId", "oppositeType", "oppositeID",
							"projectName", "projectNumber", "oppositeType",
							"businessType", "createDate", "appUserId",
							"mineId", "financeServiceMineId",
							"managementConsultingMineId" });
			slSmallloanProjectService.merge(persistent);
			String slActualToChargeJsonData = this.getRequest().getParameter(
					"slActualToChargeJsonData");
			String isDeleteAllFundIntent = this.getRequest().getParameter(
					"isDeleteAllFundIntent");
			List<SlActualToCharge> slActualToCharges = new ArrayList<SlActualToCharge>();
			slActualToCharges = savejsoncharge(slActualToChargeJsonData,
					slSmallloanProject, Short.parseShort("1"));
			if (isDeleteAllFundIntent.equals("1")) {
				for (SlActualToCharge a : slActualToCharges) {
					slActualToChargeService.save(a);
				}
			}
		}
		String taskId = this.getRequest().getParameter("task_id");
		String comments = this.getRequest().getParameter("comments");
		if (null != taskId && !"".equals(taskId) && null != comments
				&& !"".equals(comments.trim())) {
			this.creditProjectService.saveComments(taskId, comments);
		}
		return SUCCESS;
	}

	/**
	 * 小贷标准流程-款项计划确认保存
	 * 
	 * @return
	 */
	public String savePaymentPlanRecognition() {
		String fundIntentJsonData = this.getRequest().getParameter(
				"fundIntentJsonData");
		String slActualToChargeJsonData = this.getRequest().getParameter(
				"slActualToChargeJsonData");
		String projectId = this.getRequest().getParameter("slProjectId");
		SlSmallloanProject persistent = slSmallloanProjectService.get(Long
				.valueOf(projectId));
		slActualToChargeService.savejson(slActualToChargeJsonData, Long
				.valueOf(projectId), persistent.getBusinessType(), Short
				.parseShort("0"), ContextUtil.getLoginCompanyId());
		List<SlFundIntent> oldList = slFundIntentService.getByProjectId(Long
				.valueOf(projectId), persistent.getBusinessType());
		for (SlFundIntent sfi : oldList) {
			if (!sfi.getFundType().equals("principalLending")) {
				slFundIntentService.remove(sfi);
			}
		}

		if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
			String[] shareequityArr = fundIntentJsonData.split("@");
			for (int i = 0; i < shareequityArr.length; i++) {
				String str = shareequityArr[i];
				JSONParser parser = new JSONParser(new StringReader(str));
				try {
					SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper
							.toJava(parser.nextValue(), SlFundIntent.class);
					SlFundIntent1.setProjectId(persistent.getProjectId());
					SlFundIntent1.setProjectName(persistent.getProjectName());
					SlFundIntent1.setProjectNumber(persistent
							.getProjectNumber());

					if (null == SlFundIntent1.getFundIntentId()) {

						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getIncomeMoney());
						}
						SlFundIntent1.setAfterMoney(new BigDecimal(0));
						SlFundIntent1.setAccrualMoney(new BigDecimal(0));
						SlFundIntent1.setFlatMoney(new BigDecimal(0));
						Short isvalid = 0;
						SlFundIntent1.setIsValid(isvalid);
						SlFundIntent1.setIsCheck(Short.valueOf("0"));
						SlFundIntent1.setBusinessType(persistent
								.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
						slFundIntentService.save(SlFundIntent1);
					} else {
						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getIncomeMoney());
						}
						SlFundIntent slFundIntent2 = slFundIntentService
								.get(SlFundIntent1.getFundIntentId());
						if (slFundIntent2.getAfterMoney().compareTo(
								new BigDecimal(0)) == 0) {
							BeanUtil.copyNotNullProperties(slFundIntent2,
									SlFundIntent1);
							SlFundIntent1.setBusinessType(persistent
									.getBusinessType());
							SlFundIntent1.setCompanyId(persistent
									.getCompanyId());
							SlFundIntent1.setIsCheck(Short.valueOf("0"));
							slFundIntentService.merge(SlFundIntent1);
						}
					}
					String taskId = this.getRequest().getParameter("task_id");
					String comments = this.getRequest()
							.getParameter("comments");
					if (null != taskId && !"".equals(taskId)
							&& null != comments && !"".equals(comments.trim())) {
						this.creditProjectService
								.saveComments(taskId, comments);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("小贷标准流程-款项计划确认保存信息出错：" + e.getMessage());
				}
			}
		}
		return SUCCESS;
	}

	/**
	 * 小贷标准流程-贷款发放保存
	 * 
	 * @return
	 */
	public String saveProjectInfoDkff() {
		String fundIntentJsonData = this.getRequest().getParameter(
				"fundIntentJsonData");
		String projectId = this.getRequest().getParameter("slProjectId");
		SlSmallloanProject persistent = slSmallloanProjectService.get(Long
				.valueOf(projectId));
		persistent.setIntentDate(slSmallloanProject.getIntentDate());
		persistent.setStartDate(slSmallloanProject.getStartDate());
		slSmallloanProjectService.save(persistent);
		List<SlFundIntent> oldList = slFundIntentService.getByProjectId(Long
				.valueOf(projectId), persistent.getBusinessType());
		for (SlFundIntent sfi : oldList) {
			if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
				slFundIntentService.remove(sfi);
			}
		}

		if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
			String[] shareequityArr = fundIntentJsonData.split("@");
			for (int i = 0; i < shareequityArr.length; i++) {
				String str = shareequityArr[i];
				JSONParser parser = new JSONParser(new StringReader(str));
				try {
					SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper
							.toJava(parser.nextValue(), SlFundIntent.class);
					SlFundIntent1.setProjectId(persistent.getProjectId());
					SlFundIntent1.setProjectName(persistent.getProjectName());
					SlFundIntent1.setProjectNumber(persistent
							.getProjectNumber());

					if (null == SlFundIntent1.getFundIntentId()) {

						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getIncomeMoney());
						}
						SlFundIntent1.setAfterMoney(new BigDecimal(0));
						SlFundIntent1.setAccrualMoney(new BigDecimal(0));
						SlFundIntent1.setFlatMoney(new BigDecimal(0));
						Short isvalid = 0;
						SlFundIntent1.setIsValid(isvalid);
						if (SlFundIntent1.getFundType().equals(
								"principalLending")) {
							SlFundIntent1.setIsCheck(Short.valueOf("0"));
						} else {
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
						}
						SlFundIntent1.setBusinessType(persistent
								.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
						slFundIntentService.save(SlFundIntent1);
					} else {
						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1
									.getIncomeMoney());
						}
						SlFundIntent1.setBusinessType(persistent
								.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
						if (SlFundIntent1.getFundType().equals(
								"principalLending")) {
							SlFundIntent1.setIsCheck(Short.valueOf("0"));
						} else {
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
						}
						SlFundIntent slFundIntent2 = slFundIntentService
								.get(SlFundIntent1.getFundIntentId());
						BeanUtil.copyNotNullProperties(slFundIntent2,
								SlFundIntent1);

						slFundIntentService.merge(slFundIntent2);

					}
					String taskId = this.getRequest().getParameter("task_id");
					String comments = this.getRequest()
							.getParameter("comments");
					if (null != taskId && !"".equals(taskId)
							&& null != comments && !"".equals(comments.trim())) {
						this.creditProjectService
								.saveComments(taskId, comments);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("小贷标准流程-款项计划确认保存信息出错：" + e.getMessage());
				}
			}
		}
		return SUCCESS;
	}

	/**
	 * 用来获取当前系统放款支持的第三方支付类型
	 * @return
	 */
	public String getThirdPayConfig(){
		String thirdPayConfig=configMap.get("thirdPayConfig").toString();
		jsonString = "{success:true,thirdPayConfig:'"+thirdPayConfig+"'}";
		return SUCCESS;
	}
	// 放款确认
	public String confirmLoan() {
		return SUCCESS;
	}

	/*
	 * 修改款项状态
	 * 
	 * 点击放宽确认按钮，修改投资人放款状态-----本金发放
	 * **/
	private void updateFundState(List<InvestPersonInfo> invetPersonList){
		if(null==invetPersonList&&invetPersonList.size()==0){
			return ;
		}
		for(int i=0;i<invetPersonList.size();i++){
			InvestPersonInfo investPerson = invetPersonList.get(i);
			bpFundIntentService.updateState(investPerson.getOrderNo(), (short)0, "principalLending");
			plBidPlanService.loanMoney(investPerson.getBidPlanId());
		}
	}
	/**
	 * 更新投标 信息 第三方为 网关接口 调用
	 * @param investPersionList
	 */
	private String[] updateDeal(List<InvestPersonInfo> investPersionList,BpCustMember LoanMember) {
		String[] retArr = new String[2];
		String[] dealArr = new String[2];
		PlBidInfo bidInfo = new PlBidInfo();
		PlBidPlan plan = new PlBidPlan();
		Long planId = null;
		String	orderNum=ContextUtil.createRuestNumber();//第三方账号及系统账号的生成方法
		try {
			for (InvestPersonInfo investPersion : investPersionList) {
				if (planId == null) {
					planId = investPersion.getBidPlanId();
				}
				Map<String,Object> map=new HashMap<String,Object>();
		    	map.put("investPersonId",LoanMember.getId());//投资人Id（必填）
		    	map.put("investPersonType",ObSystemAccount.type0);//投资人类型：0线上投资人，1线下投资人(参见ObSystemAccount中的常量) （必填）					 
		    	map.put("transferType",ObAccountDealInfo.T_INMONEY);//交易类型:1表示充值，2表示取现,3收益，4投资，5还本(参见ObAccountDealInfo中的常量) （必填）
		    	map.put("money",investPersion.getInvestMoney());//交易金额	（必填）			 
		    	map.put("dealDirection",ObAccountDealInfo.DIRECTION_INCOME);//交易方向:1收入，2支出（参见ObAccountDealInfo中的常量）（必填）
		    	map.put("dealType",ObAccountDealInfo.THIRDPAYDEAL);//交易方式：1表示现金交易，2表示银行卡交易，3表示第三方支付交易（参见ObAccountDealInfo中的常量）（必填）
		    	map.put("recordNumber",orderNum+"-L-"+investPersion.getInvestId());//交易流水号	（必填）
		    	map.put("dealStatus",ObAccountDealInfo.DEAL_STATUS_2);//资金交易状态：1等待支付，2支付成功，3 支付失败。。。(参见ObAccountDealInfo中的常量)	（必填）
		    	String[] relt =obAccountDealInfoService.operateAcountInfo(map);
		    	
				Map<String,Object> mapUP=new HashMap<String,Object>();
				mapUP.put("investPersonId",investPersion.getInvestPersonId());//投资人Id
				mapUP.put("investPersonType",investPersion.getPersionType());//投资人类型：0线上投资人，1线下投资人(参见ObSystemAccount中的常量)
				mapUP.put("transferType",ObAccountDealInfo.T_INVEST);//交易类型:1表示充值，2表示取现,3收益，4投资，5还本(参见ObAccountDealInfo中的常量)
				mapUP.put("money",investPersion.getInvestMoney());//交易金额
				mapUP.put("dealDirection",ObAccountDealInfo.DIRECTION_PAY);//交易方向:1收入，2支出（参见ObAccountDealInfo中的常量）
				mapUP.put("DealInfoId",null);//交易记录id，没有默认为null
				mapUP.put("recordNumber",investPersion.getOrderNo());//交易流水号
				mapUP.put("dealStatus",ObAccountDealInfo.DEAL_STATUS_2);//资金交易状态：1等待支付，2支付成功，3 支付失败。。。(参见ObAccountDealInfo中的常量)
				dealArr =obAccountDealInfoService.updateAcountInfo(mapUP);
				if (dealArr != null&& dealArr[0].equals(com.zhiwei.core.Constants.CODE_SUCCESS)) {
					bidInfo = plBidInfoService.getByOrderNo(investPersion.getOrderNo());
					bidInfo.setState(Short.valueOf("2"));
					plBidInfoService.save(bidInfo);
				}
			}
			// 修改投标计划状态
			plan = plBidPlanService.get(planId);
			plan.setState(PlBidPlan.STATE7);
			plBidPlanService.save(plan);
			retArr[0] = com.zhiwei.core.Constants.CODE_SUCCESS;
			retArr[1] = "放款成功";
		} catch (Exception e) {
			retArr[0] = com.zhiwei.core.Constants.CODE_FAILED;
			retArr[1] = "放款失败";
		}
		return retArr;
	}
	
	/**
	 * 钱多多  放款
	 * @param bidType
	 * @param investPersionList
	 * @param bidId
	 * @param projectId
	 */
	public void moneyMoreMoreLoan(String bidType,List<InvestPersonInfo> investPersionList,String bidId , String projectId){
		// 转账列表 ==投标转账列表+服务费转账列表
		StringBuffer sb = new StringBuffer();
		// 服务费转账列表
		StringBuffer sb2 = new StringBuffer();
		PlThirdInterfaceLog plThirdInterfaceLog = null;// 投标转账信息
		try {
			if(bidType.equals("bid")){
				for (InvestPersonInfo investPersonInfo : investPersionList) {
					//取数据容易出现问题，
					plThirdInterfaceLog = plThirdInterfaceLogService.getByOrdId(investPersonInfo
							.getOrderNo());
					if(plThirdInterfaceLog!=null){
						sb.append(plThirdInterfaceLog.getRemark2());
						sb.append(",");
					}
					plThirdInterfaceLog = null;
				}
				sb = sb.deleteCharAt(sb.length() - 1);
				//投标放款
				transferaudit(sb.toString());
			}
			
			//服务费放款
			if(bidType.equals("service")){
				// 服务费转账列表 生成
				sb2 = serviceCharge(sb2, bidId);
				sb2 = sb2.deleteCharAt(sb2.length() - 1);
			transferaudit(sb2.toString());
			}
			// 修改项目状态---放款后贷款
			if (null != projectId && !"".equals(projectId)) {
				SlSmallloanProject project = slSmallloanProjectService.get(Long
						.parseLong(projectId));
				project.setProjectStatus((short) 1);
				slSmallloanProjectService.merge(project);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void transferaudit(String sb){
		MoneyMoreMore moneyMoreMore = new MoneyMoreMore();
		// 第三方标识
		moneyMoreMore.setLoanNoList(sb.toString());
		moneyMoreMore.setAuditType(MoneyMoreMore.TRANSFER1);
		moneyMoreMore.setRandomTimeStamp(Common.getRandomNum(2)
				+ DateUtil.dateToStr(new Date(), "yyyyMMddHHmmssSSS")); // 启用防抵赖
		iPayService.transferaudit(moneyMoreMore, this.getBasePath(), this
				.getResponse());
	}
     /**
      * 通过标ID获取要收费的项目
      * @param sb
      * @param bidPlanID
      * @return
      */
	public StringBuffer serviceCharge(StringBuffer sb, String bidPlanID) {
		MoneyMoreMore moneyMoreMore=createMoneyMoreMore(bidPlanID);
        String jsonList=iPayService.transfer(moneyMoreMore, this.getBasePath());
        getLoanList(bidPlanID,sb);
		return sb;
	}
	
	/**
	 * 获取返回的 转账列表
	 * 
	 * @param 同00+bid 获取转账列表
	 * @return
	 */
	private StringBuffer getLoanList(String bidPlanID,StringBuffer sb) {
		QueryFilter filter =new QueryFilter(this.getRequest());
		filter.addFilter("Q_BatchNo_S_EQ", "00"+bidPlanID);
		filter.addFilter("Q_type_S_EQ", BpMoneyManager.TYPE7);
		filter.addFilter("Q_status_S_EQ", "88");
				
		try {
			List<BpMoneyManager> list=bpMoneyManagerService.getAll(filter);
			for(BpMoneyManager monger:list){
				sb.append(monger.getLoanNo());
				sb.append(",");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb;
	}
	
	/**
	 * 获取订单列表
	 * 
	 * @param jsonStr
	 * @return
	 */
	private StringBuffer getLoanNoList(String jsonStr,StringBuffer sb) {
		try {
			jsonStr = URLDecoder.decode(jsonStr, "utf-8");
		
		ResultLoanBean result = null;
		Gson gson = new GsonBuilder().registerTypeAdapterFactory(
				HibernateProxyTypeAdapter.FACTORY).create();
		Type type = new TypeToken<List<com.zhiwei.credit.model.pay.ResultLoanBean>>() {
		}.getType();
		List<ResultLoanBean> retList = gson.fromJson(jsonStr, type);
		BpBidLoan bpBidLoad = null;
		for (ResultLoanBean ret : retList) {
			sb.append(ret.getLoanNo());
		}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb;
	}
	

	/**
	 * 生成 服务费转账列表
	 * 
	 * @param bidPlanID
	 *            标ID
	 * @return
	 */
	private MoneyMoreMore createMoneyMoreMore(String bidPlanID) {// 生成转账列表
		MoneyMoreMore  moneyMoreMore=new MoneyMoreMore();
		// 标的信息
		PlBidPlan plan = plBidPlanService.get(Long.valueOf(bidPlanID));

		List<MadaiLoanInfoBean> listmlib = new ArrayList<MadaiLoanInfoBean>();
		QueryFilter filter = new QueryFilter(this.getRequest());
		filter.addFilter("Q_bidPlanId_L_EQ", bidPlanID);
		List<SlActualToCharge> slActual = slActualToChargeService
				.getAll(filter);

		for (int i = 0; i < slActual.size(); i++) {
			MadaiLoanInfoBean mlib = new MadaiLoanInfoBean();
			mlib.setLoanOutMoneymoremore(getCustFlag(plan)); // 付款人账户
			mlib.setLoanInMoneymoremore(AppUtil.getSysConfig().get("MM_PlatformMoneymoremore").toString());// 收款人为
			mlib.setOrderNo(UUIDGenerator.getUUID());
			mlib.setBatchNo("00"+bidPlanID);// 标号 填 (00和标id 的组合)00+id 这里
			mlib.setAmount(slActual.get(i).getIncomeMoney().toString());// 转账金额
			mlib.setFullAmount("");
			mlib.setTransferName("");
			mlib.setRemark(slActual.get(i).getRemark()==null?"":slActual.get(i).getRemark());
			listmlib.add(mlib);
		}

		// 设置 转账列表
		moneyMoreMore.setLoanJsonList(Common.JSONEncode(listmlib));
		moneyMoreMore.setTransferAction(MoneyMoreMore.TACTION_2);
		moneyMoreMore.setAction(MoneyMoreMore.ACTION_2);
		moneyMoreMore.setTransferType(MoneyMoreMore.TTYPE_2);
		//moneyMoreMore.setNeedAudit("1"); //空.需要审核 1.自动通过
		return moneyMoreMore;

	}

	/**
	 * 获取第三方 标识
	 * 
	 * @param bidplan
	 * @return
	 */
	private String getCustFlag(PlBidPlan bidplan) {
		//借款人标识
		String custFlag=null;
		//借款人ID
		Long custID = null;
		//借款人前台注册ID
		Long custRegID=null;
		try{
		// 项目类型 企业直投 B_Dir 企业 债权 B_Or 个人直投 P_Dir 个人债权 P_Or * @return String
		if (bidplan.getProType() != null
				&& bidplan.getProType().equals("B_Dir")) {
			// 借款客户ID
			custID = bidplan.getBpBusinessDirPro().getBusinessId();
		} else if (bidplan.getProType() != null
				&& bidplan.getProType().equals("B_Or")) {
			custID = bidplan.getBpBusinessOrPro().getBusinessId();
		} else if (bidplan.getProType() != null
				&& bidplan.getProType().equals("P_Dir")) {
			custID = bidplan.getBpPersionDirPro().getPersionId();
		} else if (bidplan.getProType() != null
				&& bidplan.getProType().equals("P_Or")) {
			custID = bidplan.getBpPersionOrPro().getPersionId();
		}
		QueryFilter filter =new QueryFilter(this.getRequest());
		filter.addFilter("Q_offlineCusId_L_EQ", custID.toString());
		List<BpCustRelation> custRelation =bpCustRelationService.getAll(filter);
		custFlag=bpCustMemberService.get(custRelation.get(0).getP2pCustId()).getMoneymoremoreId();
		}catch(Exception e){
			e.printStackTrace();
		}
		return custFlag;
	}

	public String getIntentDate() {
		String startDate = this.getRequest().getParameter("startDate");
		String payAccrualType = this.getRequest()
				.getParameter("payAccrualType");
		String payintentPeriodStr = this.getRequest().getParameter(
				"payintentPeriod");
		String dayOfEveryPeriodStr = this.getRequest().getParameter(
				"dayOfEveryPeriod");
		Integer payintentPeriod = null;
		if (null != payintentPeriodStr && !"".equals(payintentPeriodStr)) {// 期数
			payintentPeriod = Integer.parseInt(payintentPeriodStr);
		}
		Integer dayOfEveryPeriod = null;
		if (null != dayOfEveryPeriodStr && !"".equals(dayOfEveryPeriodStr)) {
			dayOfEveryPeriod = Integer.parseInt(dayOfEveryPeriodStr);
		}
		StringBuffer buff = new StringBuffer("{success:true,intentDate:'");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		Date intentDate = StatsPro.getIntentDate(startDate, payAccrualType,// 还款周期
				payintentPeriod, dayOfEveryPeriod);
		if (null != intentDate) {
			buff.append(sd.format(intentDate));
		}
		buff.append("'}");
		jsonString = buff.toString();
		return SUCCESS;
	}

	// 对应查出来项目表用来发布产品
	public void LoanIngProjectList() {
		slSmallloanProjectService.LoanIngProjectList(start, limit);
	}

	public String saveSupervisonInfo() {
		try {
			GlobalSupervisemanage orgGlobalSupervisemanage = globalSupervisemanageService
					.get(superviseManageId);
			BeanUtil.copyNotNullProperties(orgGlobalSupervisemanage,
					globalSupervisemanage);
			orgGlobalSupervisemanage.setIsProduceTask(true);
			globalSupervisemanageService.merge(orgGlobalSupervisemanage);
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/***
	 * 快速保存方法
	 * ****/
	public String fastSave(){
		//项目信息必定已经存在
		slSmallloanProjectService.merge(slSmallloanProject);
		
		
		
		return SUCCESS;
	}
	//保存款项信息=======1、小贷借款人款项信息，2、直投标款项信息（投资人、借款人），3、债权标款项信息（投资人、借款人）
	public String saveFinance(){
		//小贷----------fundIntentJsonData
		
		//直投表--------platLoanFundJson,platInvestFundJson
		String platLoanFundJson = getRequest().getParameter("platLoanFundJson");
		String bidPlanId  = getRequest().getParameter("bidPlanId");
		String platId = getRequest().getParameter("platFormBpFundProject.id");
		String platInvestFundJson = getRequest().getParameter("platInvestFundJson");
		if(null != slSmallloanProject && !"".equals(platLoanFundJson) && null !=platLoanFundJson){
			if(null==bidPlanId||"".equals(bidPlanId)){
				slSmallloanProjectService.saveSlFundIntent(slSmallloanProject,platLoanFundJson,null,Long.parseLong(platId));
			}else{
				slSmallloanProjectService.saveSlFundIntent(slSmallloanProject,platLoanFundJson,Long.parseLong(bidPlanId),Long.parseLong(platId));
			}
		}
		if(null != slSmallloanProject && !"".equals(platInvestFundJson) && null !=platInvestFundJson){
			BpFundProject ownFund = bpFundProjectService.get(Long.parseLong(platId));
			fundIntentService.savejson(platInvestFundJson, Long.valueOf(projectId),"SmallLoan",Short.parseShort("0"),ownFund.getCompanyId(),ownFund.getId(),ownFund.getFundResource(),Long.parseLong(bidPlanId));
		}
		
		
		//债权标--------ownLoanFundJson,ownInvestFundJson
		//ownLoanFundJson -- 借款人款项信息
		String ownLoanFundJson = getRequest().getParameter("ownLoanFundJson");
		//String bidPlanId  = getRequest().getParameter("bidPlanId");
		String ownId = getRequest().getParameter("ownBpFundProject.id");
		String ownInvestFundJson = getRequest().getParameter("ownInvestFundJson");
		if(null != slSmallloanProject && !"".equals(ownLoanFundJson) && null !=ownLoanFundJson){
			if(null==bidPlanId||"".equals(bidPlanId)){
				slSmallloanProjectService.saveSlFundIntent(slSmallloanProject,ownLoanFundJson,null,Long.parseLong(ownId));
			}else{
				slSmallloanProjectService.saveSlFundIntent(slSmallloanProject,ownLoanFundJson,Long.parseLong(bidPlanId),Long.parseLong(ownId));
			}
		}
		if(null != slSmallloanProject && !"".equals(ownInvestFundJson) && null !=ownInvestFundJson){
			BpFundProject ownFund = bpFundProjectService.get(Long.parseLong(ownId));
			fundIntentService.savejson(ownInvestFundJson, Long.valueOf(projectId),"SmallLoan",Short.parseShort("0"),ownFund.getCompanyId(),ownFund.getId(),ownFund.getFundResource(),Long.parseLong(bidPlanId));
		}
		
		
		
		
		//fundIntentService.savejson(pFundsDatas, Long.valueOf(projectId),"SmallLoan",Short.parseShort("0"),oldBpFund2.getCompanyId(),oldBpFund2.getId(),oldBpFund2.getFundResource(),Long.parseLong(bidPlanId));
		return SUCCESS;
	}
	public String saveBorrowDemand(){
		BpMoneyBorrowDemand borrowDemand =new BpMoneyBorrowDemand();
		try {
			BeanUtil.populateEntity(getRequest(), borrowDemand,"bpMoneyBorrowDemand");
			if(null==borrowDemand.getBorrowid()){
				borrowDemand.setProjectID(slSmallloanProject.getProjectId());
				bpMoneyBorrowDemandService.save(borrowDemand);
			}else{
				BpMoneyBorrowDemand demand = bpMoneyBorrowDemandService.getByBorrowId(borrowDemand.getBorrowid());
				BeanUtil.copyNotNullProperties(demand, borrowDemand);
				bpMoneyBorrowDemandService.merge(demand);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
		}
		return null;
	}
	
	public String getEarlyProjectInfo() {
		String projectId = this.getRequest().getParameter("slProjectId"); // 项目ID
		String taskId = this.getRequest().getParameter("slTaskId"); // 任务ID
		String slEarlyRepaymentId = this.getRequest().getParameter("slEarlyRepaymentId"); // 任务ID
		String bidPlanId = this.getRequest().getParameter("bidPlanId");
		//读取项目信息
		SlSmallloanProject project = this.slSmallloanProjectService.get(Long.valueOf(projectId));
		if(project.getPurposeType()!=null){
			Dictionary dictionary = dictionaryService.get(project.getPurposeType());
			if(dictionary!=null&&project.getPurposeTypeStr()==null){
				project.setPurposeTypeStr(dictionary.getItemValue());
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 读取个人客户信息
			Person p = new Person();
			Enterprise enterprise = new Enterprise();
			short sub = 0;
			if (project.getOppositeType().equals("company_customer")) {
				sub = 0;
				Enterprise enterprise1 = enterpriseService.getById(project
						.getOppositeID().intValue());
				if (enterprise1.getLegalpersonid() != null) {
					p = this.personService.getById(enterprise1
							.getLegalpersonid());
					map.put("person", p);
				}
				if (null != enterprise1.getHangyeType()) {
					if (null != areaDicService.getById(enterprise1
							.getHangyeType())) {
						enterprise1.setHangyeName(areaDicService.getById(
								enterprise1.getHangyeType()).getText());
					}
				}
				map.put("enterprise", enterprise1);
				List<EnterpriseBank> list = enterpriseBankService.getBankList(project.getOppositeID().intValue(), sub, Short.valueOf("0"), Short.valueOf("0"));
				if (null != list && list.size() > 0) {
					EnterpriseBank bank = list.get(0);
					map.put("enterpriseBank", bank);
				}

			} else if (project.getOppositeType().equals("person_customer")) {
				sub = 1;
				p = this.personService.getById(project.getOppositeID()
						.intValue());
				map.put("person", p);
				if (null != p) {
					if (null != p.getId()) {
//						if (null != p.getMarry() && p.getMarry() == 317) {
							Spouse spouse = spouseService.getByPersonId(p
									.getId());
							if(null!=spouse){
								map.put("spouse", spouse);
							}
//						}
					}
				}
			}
			
			String mineName = getMainName(project.getMineType(), project.getMineId());
			String managementConsultingMineName = getMainName(project.getManagementConsultingMineType(),project.getManagementConsultingMineId());
			String financeServiceMineName = getMainName(project.getFinanceServiceMineType(),project.getFinanceServiceMineId());
			//
			map.put("mineName", mineName);
			map.put("financeServiceMineName", financeServiceMineName);
			map.put("managementConsultingMineName", managementConsultingMineName);
			project.setMineName(mineName);
			project.setManagementConsultingMineName(managementConsultingMineName);
			project.setFinanceServiceMineName(financeServiceMineName);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取信贷流程项目相关信息:" + e.getMessage());
		}

		List<ProcessRun> runList = processRunService.getByProcessNameProjectId(
				project.getProjectId(), project.getBusinessType(), project
						.getFlowType());
		if (runList != null && runList.size() != 0) {
			List<TaskSignData> tsdList = taskSignDataService.getByRunId(runList
					.get(0).getRunId());
			if (tsdList.size() > 0) {
				map.put("isOnline", true);
			} else {
				map.put("isOnline", false);
			}
		}
		// 自有资金
		BpFundProject ownFund = bpFundProjectService.getByProjectId(Long
				.valueOf(projectId), Short.valueOf("0"));
		if (null != ownFund) {
			map.put("ownBpFundProject", ownFund);
		}

		// 平台资金
		BpFundProject platFormFund = bpFundProjectService.getByProjectId(Long
				.valueOf(projectId), Short.valueOf("1"));
		if (null != platFormFund) {
			map.put("platFormBpFundProject", platFormFund);
		}
		if(null!=slEarlyRepaymentId && !slEarlyRepaymentId.equals("")){
			SlEarlyRepaymentRecord slEarlyRepaymentRecord=slEarlyRepaymentRecordService.get(Long.valueOf(slEarlyRepaymentId));
			map.put("slEarlyRepaymentRecord", slEarlyRepaymentRecord);
		}
		
		String businessTypeKey = creditProjectService.getGlobalTypeValue(
				"businessType", project.getBusinessType(), project
						.getProjectId());
		String operationTypeKey = creditProjectService.getGlobalTypeValue(
				"operationType", project.getBusinessType(), project
						.getProjectId());
		String definitionTypeKey = creditProjectService.getGlobalTypeValue(
				"definitionType", project.getBusinessType(), project
						.getProjectId());
		
		
		map.put("slSmallloanProject", project);
		
		map.put("businessTypeKey", businessTypeKey);
		map.put("operationTypeKey", operationTypeKey);
		map.put("definitionTypeKey", definitionTypeKey);
		if (null != project.getProductId()
				&& !"".equals(project.getProductId())) {
			BpProductParameter bpProductParameter = bpProductParameterService
					.get(project.getProductId());
			map.put("bpProductParameter", bpProductParameter);
		}
		if (null != taskId && !"".equals(taskId)) {
			ProcessForm pform = processFormService.getByTaskId(taskId);
			if (pform == null) {
				pform = creditProjectService.getCommentsByTaskId(taskId);
			}
			if (pform != null && pform.getComments() != null
					&& !"".equals(pform.getComments())) {
				map.put("comments", pform.getComments());
			}
		}
		//plBidPlan
		if(bidPlanId!=null&&!"".equals(bidPlanId)&&!"undefined".equals(bidPlanId)){
			PlBidPlan plBidPlan = plBidPlanService.get(Long.parseLong(bidPlanId));
			if(plBidPlan!=null){
				map.put("plBidPlan", plBidPlan);
			}
		}
		if(project.getAppUserId()!=null&&!project.getAppUserId().contains(",")){
			AppUser au = appUserService.get(Long.valueOf(project.getAppUserId()));
			map.put("appUserNumber", au.getUserNumber());
		}
		doJson(map);
		return SUCCESS;
	}
	
	@Resource
	private BankCreditService bankCreditService;
	
	@Resource
	private BankCreditUserLogService bankCreditUserLogService;
	@Resource
	private CashDepositService cashetService;
	
	
	public String getSmallLoanProjectInfo() {
		
		String projectId = this.getRequest().getParameter("slProjectId"); // 项目ID
		String taskId = this.getRequest().getParameter("slTaskId"); // 任务ID

		String bidPlanId = this.getRequest().getParameter("bidPlanId");
		//读取项目信息
		SlSmallloanProject project = this.slSmallloanProjectService.get(Long.valueOf(projectId));
		if(project.getPurposeType()!=null){
			Dictionary dictionary = dictionaryService.get(project.getPurposeType());
			if(dictionary!=null&&project.getPurposeTypeStr()==null){
				project.setPurposeTypeStr(dictionary.getItemValue());
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 读取个人客户信息
			Person p = new Person();
			Enterprise enterprise = new Enterprise();
			short sub = 0;
			
			//授信---使用---开始
			Long   bcgetOppositeID   = null;//客户类型主键
			String bcOppositeType ="";//客户类型
			//授信---使用--结束
			if (project.getOppositeType().equals("company_customer")) {
			
				sub = 0;
				Enterprise enterprise1 = enterpriseService.getById(project.getOppositeID().intValue());
			
				//授信---使用---开始
				bcOppositeType  = project.getOppositeType();
				bcgetOppositeID = enterprise1.getId().longValue();
				//授信---使用--结束
				if (enterprise1.getLegalpersonid() != null) {
					p = this.personService.getById(enterprise1.getLegalpersonid());
					map.put("person", p);
				}
				if (null != enterprise1.getHangyeType()) {
					if (null != areaDicService.getById(enterprise1.getHangyeType())) {
						enterprise1.setHangyeName(areaDicService.getById(enterprise1.getHangyeType()).getText());
					}
				}
				map.put("enterprise", enterprise1);
			} else if (project.getOppositeType().equals("person_customer")) {
				sub = 1;
				p = this.personService.getById(project.getOppositeID().intValue());
				if(p.getBelongedId()!= null && !"".equals(p.getBelongedId())){
					String belongedName = "";
					String []str = p.getBelongedId().split(",");
					Set<AppUser> userSet = appUserService.getAppUserByStr(str);
					int i = 0;
					for(AppUser s:userSet){
						belongedName += s.getFamilyName();
						i++;
						if(i != userSet.size()){
							belongedName= belongedName+",";
						}
					}
					p.setBelongedName(belongedName);
				}
				if(null!=p.getHomeland()){
					AreaDic areaDic=areaDicService.getById(p.getHomeland());
					p.setParentHomeland(areaDic.getParentId());
					map.put("homelandProvince", p.getParentHomeland());
				}
				if(null!=p.getLiveCity()){
					AreaDic areaDic=areaDicService.getById(p.getLiveCity());
					p.setParentLiveCity(areaDic.getParentId());
				}
				if(null!=p.getHireCity()){
					AreaDic areaDic=areaDicService.getById(p.getHireCity());
					p.setParentHireCity(areaDic.getParentId());
				}
				FileForm fileForm = fileFormService.getFileByMark("cs_person_tx."+p.getId());
				if(fileForm != null){
					p.setPersonPhotoId(fileForm.getFileid());
					p.setPersonPhotoUrl(fileForm.getWebPath());
					p.setPersonPhotoExtendName(fileForm.getExtendname());
				}
				FileForm fileForm2 = fileFormService.getFileByMark("cs_person_sfzz."+p.getId());
				if(fileForm2 != null){
					p.setPersonSFZZId(fileForm2.getFileid());
					p.setPersonSFZZUrl(fileForm2.getWebPath());
					p.setPersonSFZZExtendName(fileForm2.getExtendname());
				}
				FileForm fileForm3 = fileFormService.getFileByMark("cs_person_sfzf."+p.getId());
				if(fileForm3 != null){
					p.setPersonSFZFId(fileForm3.getFileid());
					p.setPersonSFZFUrl(fileForm3.getWebPath());
					p.setPersonSFZFExtendName(fileForm3.getExtendname());
				}
				//授信---使用---开始
				bcOppositeType  = project.getOppositeType();
				bcgetOppositeID = p.getId().longValue();
				//授信---使用--结束
				map.put("person", p);
				if (null != p) {
					if (null != p.getId()) {
						if (null != p.getMarry() && p.getMarry() == 317) {
							Spouse spouse = spouseService.getByPersonId(p.getId());
							if(null!=spouse){
								map.put("spouse", spouse);
							}
						}
					}
				}
			}
			if(null!=project.getBankId()){
				EnterpriseBank enterpriseBank=enterpriseBankService.getById(project.getBankId().intValue());
				map.put("enterpriseBank", enterpriseBank);
			}
			
			//授信---使用---开始
		    BankCredit bankCredit  = new BankCredit();
		    if(project.getBankCreditId()!=null && !"".equals(project.getBankCreditId())){
		    	bankCredit  =     bankCreditService.get(project.getBankCreditId());  
		    }
			map.put("bankCredit",bankCredit);
			
			CashDeposit  cashDeposit = new CashDeposit();
			if(project.getBaozhengjinId()!=null && !"".equals(project.getBaozhengjinId())){
				cashDeposit  =  cashetService.get(project.getBaozhengjinId());  
		    }
			map.put("cashDeposit",cashDeposit);
			
			//授信---使用--结束
			map.put("person", p);
			// 个人旗下公司
//			WorkCompany workCompany = workCompanyService.getWorkCompanyByPersonId(p.getId());
//			map.put("workCompany", workCompany);
			// 借款需求
			if (null != projectId && !"".equals(projectId)) {
				List<BpMoneyBorrowDemand> list = this.bpMoneyBorrowDemandService.getMessageByProjectID(Long.valueOf(projectId));
				if (list.size() > 0) {
					bpMoneyBorrowDemand = list.get(0);
					map.put("bpMoneyBorrowDemand", bpMoneyBorrowDemand);
				}
				String managementConsultingMineName = getMainName(project.getManagementConsultingMineType(),project.getManagementConsultingMineId());
				String financeServiceMineName = getMainName(project.getFinanceServiceMineType(),project.getFinanceServiceMineId());
				map.put("financeServiceMineName", financeServiceMineName);
				map.put("managementConsultingMineName", managementConsultingMineName);
			}
			
			//归档信息
			List<PlProjectArchives> archives=plProjectArchivesService.getbyproject(Long.valueOf(projectId),"SmallLoan");
			if(null!=archives && archives.size()>0){
				map.put("plProjectArchives",archives.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取信贷流程项目相关信息:" + e.getMessage());
		}

		List<ProcessRun> runList = processRunService.getByProcessNameProjectId(project.getProjectId(), project.getBusinessType(), project.getFlowType());
		if (runList != null && runList.size() != 0) {
			List<TaskSignData> tsdList = taskSignDataService.getByRunId(runList.get(0).getRunId());
			if (tsdList.size() > 0) {
				map.put("isOnline", true);
			} else {
				map.put("isOnline", false);
			}
		}
		// 自有资金
		BpFundProject ownFund = bpFundProjectService.getByProjectId(Long.valueOf(projectId), Short.valueOf("0"));
		if (null != ownFund) {
			map.put("ownBpFundProject", ownFund);
		}

		// 平台资金
		/*BpFundProject platFormFund = bpFundProjectService.getByProjectId(Long.valueOf(projectId), Short.valueOf("1"));
		if (null != platFormFund) {
			map.put("platFormBpFundProject", platFormFund);
		}*/
		if (project.getAvailableType() != null&& project.getAvailableType().equals("1")) {
			project.setAvailableType("小贷公司");
		} else if (project.getAvailableType() != null&& project.getAvailableType().equals("2")) {
			project.setAvailableType("担保公司");
		}
		if (project.getAvailableId() != null) {
			InvestEnterprise enter = investEnterpriseService.get(project.getAvailableId());
			if (enter != null) {
				project.setValilableName(enter.getEnterprisename());
				if (enter.getNowCreditLimit() != null){
					project.setLaveCredit(enter.getNowCreditLimit().toString());
				}
			}
		}
		String businessTypeKey = creditProjectService.getGlobalTypeValue("businessType", project.getBusinessType(), project.getProjectId());
		String operationTypeKey = creditProjectService.getGlobalTypeValue("operationType", project.getBusinessType(), project.getProjectId());
		String definitionTypeKey = creditProjectService.getGlobalTypeValue("definitionType", project.getBusinessType(), project.getProjectId());
		
		map.put("businessTypeKey", businessTypeKey);
		map.put("operationTypeKey", operationTypeKey);
		map.put("definitionTypeKey", definitionTypeKey);
		if (null != project.getProductId()&& !"".equals(project.getProductId())) {
			String isOnlineApply=this.getRequest().getParameter("isOnlineApply");//是否是线上申请
			if(null!=isOnlineApply && !"".equals(isOnlineApply) && "true".equals(isOnlineApply)){
				P2pLoanProduct product=p2pLoanProductService.get(project.getProductId());
				map.put("bpProductParameter",product);
			}else{
				BpProductParameter bpProductParameter = bpProductParameterService.get(project.getProductId());
				map.put("bpProductParameter", bpProductParameter);
			}
		}
		map.put("slSmallloanProject", project);
		if (null != taskId && !"".equals(taskId)) {
			ProcessForm pform = processFormService.getByTaskId(taskId);
			if (pform == null) {
				pform = creditProjectService.getCommentsByTaskId(taskId);
			}
			if (pform != null && pform.getComments() != null
					&& !"".equals(pform.getComments())) {
				map.put("comments", pform.getComments());
			}
		}
		//plBidPlan
		if(bidPlanId!=null&&!"".equals(bidPlanId)&&!"undefined".equals(bidPlanId)){
			PlBidPlan plBidPlan = plBidPlanService.get(Long.parseLong(bidPlanId));
			if(plBidPlan!=null){
				map.put("plBidPlan", plBidPlan);
			}
		}
		if(project.getAppUserId()!=null&&!project.getAppUserId().contains(",")){
			AppUser au = appUserService.get(Long.valueOf(project.getAppUserId()));
			map.put("appUserNumber", au.getUserNumber());
		}
		doJson(map);
		return SUCCESS;
	}
	/**
	 * 企贷标准流程赌球项目信息
	 */
	public String getEnterpriseProjectInfo() {
		String projectId = this.getRequest().getParameter("slProjectId"); // 项目ID
		String taskId = this.getRequest().getParameter("slTaskId"); // 任务ID
		SlSmallloanProject project = this.slSmallloanProjectService.get(Long.valueOf(projectId));
		Map<String, Object> map = new HashMap<String, Object>();
		
		String financeServiceMineName = "";
		String managementConsultingMineName = "";
		if (null != project.getFinanceServiceMineType() && !"".equals(project.getFinanceServiceMineType())) {
			if (null != project.getFinanceServiceMineId() && !"".equals(project.getFinanceServiceMineId())) {
				if (project.getFinanceServiceMineType().equals("company_ourmain")) {
					financeServiceMineName = this.slCompanyMainService.get(project.getFinanceServiceMineId()).getCorName();
				} else {
					financeServiceMineName = this.slPersonMainService.get(project.getFinanceServiceMineId()).getName();
				}
			}
		}

		if (null != project.getManagementConsultingMineType() && !"".equals(project.getManagementConsultingMineType())) {
			if (null != project.getManagementConsultingMineId() && !"".equals(project.getManagementConsultingMineId())) {
				if (project.getManagementConsultingMineType().equals("company_ourmain")) {
					managementConsultingMineName = this.slCompanyMainService.get(project.getManagementConsultingMineId()).getCorName();
				} else {
					managementConsultingMineName = this.slPersonMainService.get(project.getManagementConsultingMineId()).getName();
				}
			}
		}

		Person p = new Person();
		// if判断是企业客户 elseif是个人客户
		Short sub = 0;
		if (project.getOppositeType().equals("company_customer")) {
			sub = 0;
			Enterprise enterprise1 = enterpriseService.getById(project.getOppositeID().intValue());
			if (enterprise1.getLegalpersonid() != null) {
				p = this.personService.getById(enterprise1.getLegalpersonid());
				map.put("person", p);
			}
			if (null != enterprise1.getHangyeType()) {
				if (null != areaDicService.getById(enterprise1.getHangyeType())) {
					enterprise1.setHangyeName(areaDicService.getById(enterprise1.getHangyeType()).getText());
				}
			}
			map.put("enterprise", enterprise1);
		} 
		if (null != project.getOppositeID()) {
			List<EnterpriseBank> list = enterpriseBankService.getBankList(project.getOppositeID().intValue(), sub, Short.valueOf("0"), Short.valueOf("0"));
			if (null != list && list.size() > 0) {
				EnterpriseBank bank = list.get(0);
				map.put("enterpriseBank", bank);
			}
		}
		
		String slSuperviseRecordId = this.getRequest().getParameter("slSuperviseRecordId"); //	 
		if (null != slSuperviseRecordId && !"".equals(slSuperviseRecordId)) {
			SlSuperviseRecord slSuperviseRecord = this.slSuperviseRecordService.get(Long.valueOf(slSuperviseRecordId));
			map.put("slSuperviseRecord", slSuperviseRecord);
		}
		List<SlFundIntent> list = slFundIntentService.getByProjectId3(project.getProjectId(), project.getBusinessType(), "principalLending");
		if (null != list && list.size() > 0) {
			SlFundIntent f = list.get(0);
			map.put("startDate", f.getIntentDate());
		}
		// 自有资金
		BpFundProject ownFund = bpFundProjectService.getByProjectId(Long.valueOf(projectId), Short.valueOf("0"));
		if (null != ownFund) {
			map.put("ownBpFundProject", ownFund);
		}

		// 平台资金
		BpFundProject platFormFund = bpFundProjectService.getByProjectId(Long.valueOf(projectId), Short.valueOf("1"));
		if (null != platFormFund) {
			map.put("platFormBpFundProject", platFormFund);
		}
		List<ProcessRun> runList = processRunService.getByProcessNameProjectId(project.getProjectId(), project.getBusinessType(), project.getFlowType());
		if(runList!=null&&runList.size()!=0){
			List<TaskSignData> tsdList =  taskSignDataService.getByRunId(runList.get(0).getRunId());
			if(tsdList.size()>0) {
				map.put("isOnline", true);
			}else {
				map.put("isOnline", false);
			}
		}
		String businessTypeKey = creditProjectService.getGlobalTypeValue("businessType", project.getBusinessType(), project.getProjectId());
		String operationTypeKey = creditProjectService.getGlobalTypeValue("operationType", project.getBusinessType(), project.getProjectId());
		String definitionTypeKey = creditProjectService.getGlobalTypeValue("definitionType", project.getBusinessType(), project.getProjectId());
	
		map.put("slSmallloanProject", project);
		map.put("financeServiceMineName", financeServiceMineName);
		map.put("managementConsultingMineName", managementConsultingMineName);
		map.put("businessType", project.getBusinessType());
		map.put("isAheadPay", project.getIsAheadPay());
		map.put("businessTypeKey", businessTypeKey);
		map.put("operationTypeKey", operationTypeKey);
		map.put("definitionTypeKey", definitionTypeKey);
		map.put("flowTypeKey", this.proDefinitionService.getProdefinitionByProcessName(project.getFlowType()).getName());
	
		if (null != taskId && !"".equals(taskId)) {
			ProcessForm pform = processFormService.getByTaskId(taskId);
			if (pform == null) {
				pform = creditProjectService.getCommentsByTaskId(taskId);
			}
			if (pform != null && pform.getComments() != null && !"".equals(pform.getComments())) {
				map.put("comments", pform.getComments());
			}
		}
		
		if (null != project.getProductId()&& !"".equals(project.getProductId())) {
			String isOnlineApply=this.getRequest().getParameter("isOnlineApply");//是否是线上申请
			if(null!=isOnlineApply && !"".equals(isOnlineApply) && "true".equals(isOnlineApply)){
				P2pLoanProduct product=p2pLoanProductService.get(project.getProductId());
				map.put("bpProductParameter",product);
			}else{
				BpProductParameter bpProductParameter = bpProductParameterService.get(project.getProductId());
				map.put("bpProductParameter", bpProductParameter);
			}
		}
		
		doJson(map);
		
		return SUCCESS;
	}
	
	/*
	 * 企贷标准流程的保存方法
	 */
	public String saveEnterpriseProjectInfo(){
		try{
			StringBuffer sb = new StringBuffer("{success:true");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			
			//保存客户信息
			String gudongInfo=this.getRequest().getParameter("gudongInfo");
			enterpriseService.saveSingleEnterprise(enterprise, person, gudongInfo);
			//保存客户银行信息
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(persistent.getOppositeID().intValue(), Short.valueOf("0"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(persistent.getOppositeID().intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("0"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			//保存第一还款来源
			String repaymentSource=this.getRequest().getParameter("repaymentSource");
			if(null != repaymentSource && !"".equals(repaymentSource)) {

				String[] repaymentSourceArr = repaymentSource.split("@");
				
				for(int i=0; i<repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource temp = (SlRepaymentSource)JSONMapper.toJava(parser.nextValue(),SlRepaymentSource.class);
					boolean flag = StringUtil.isNumeric(temp.getTypeId());
					GlobalType globalType = globalTypeService.getByNodeKey("repaymentSource").get(0);
					if (flag == false) {
						Dictionary dic = new Dictionary();
						dic.setItemValue(temp.getTypeId());
						dic.setItemName(globalType.getTypeName());
						dic.setProTypeId(globalType.getProTypeId());
						dic.setDicKey(temp.getTypeId());
						dic.setStatus("0");
						dictionaryService.save(dic);
						temp.setTypeId(String.valueOf(dic.getDicId()));
					} else {
						Dictionary cd = dictionaryService.get(Long.valueOf(temp
								.getTypeId()));
						if (null == cd) {
							Dictionary dic = new Dictionary();
							dic.setItemValue(temp.getTypeId());
							dic.setItemName(globalType.getTypeName());
							dic.setProTypeId(globalType.getProTypeId());
							dic.setDicKey(temp.getTypeId());
							dic.setStatus("0");
							dictionaryService.save(dic);
							temp.setTypeId(String.valueOf(dic.getDicId()));
						}
					}
					temp.setProjId(slSmallloanProject.getProjectId());
					if (temp.getSourceId() == null) {
						this.slRepaymentSourceService.save(temp);
					} else {
						SlRepaymentSource rPersistent = this.slRepaymentSourceService.get(temp.getSourceId());
						BeanUtil.copyNotNullProperties(rPersistent, temp);
						this.slRepaymentSourceService.save(rPersistent);
					}
				
					
				}
			}
			//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {

				String[] borrowerInfoArr = borrowerInfo.split("@");
				
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}
			//保存会议纪要
			if(null!=slConferenceRecord){
				if(slConferenceRecord.getConforenceId()==null){
					slConferenceRecordService.save(slConferenceRecord);
					Long conforenceId=slConferenceRecord.getConforenceId();
					
				}else{
					SlConferenceRecord orgSlConferenceRecord=slConferenceRecordService.get(slConferenceRecord.getConforenceId());
					BeanUtil.copyNotNullProperties(orgSlConferenceRecord, slConferenceRecord);
					slConferenceRecordService.save(orgSlConferenceRecord);
				
				}
				sb.append(",conforenceId:"+slConferenceRecord.getConforenceId());
			}
			
			//保存费用信息
			String slActualToChargeJsonData=this.getRequest().getParameter("slActualToChargeJsonData");
			String isCheck=this.getRequest().getParameter("isCheck");
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}
			// 自有资金款项信息
			if(null!=ownBpFundProject){
				BpFundProject ownFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("0"));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
					bpFundProjectService.merge(ownFund);
				}
			}
			// 平台资金款项信息
			if(null!=platFormBpFundProject){
				BpFundProject platFormFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("1"));
				if (null != platFormFund) {
					BeanUtil.copyNotNullProperties(platFormFund,platFormBpFundProject);
					bpFundProjectService.merge(platFormFund);
				}
			}
			//保存款项信息
			String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
			if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
				List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownBpFundProject.getId());
				for (SlFundIntent sfi : oldList) {
					if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
						slFundIntentService.remove(sfi);
					}
				}
				String[] shareequityArr = fundIntentJsonData.split("@");
				for (int i = 0; i < shareequityArr.length; i++) {
					String fundIntentstr = shareequityArr[i];
					JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
			
					SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
					SlFundIntent1.setProjectId(persistent.getProjectId());
					SlFundIntent1.setProjectName(persistent.getProjectName());
					SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

					if (null == SlFundIntent1.getFundIntentId()) {

						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
						}
						SlFundIntent1.setAfterMoney(new BigDecimal(0));
						SlFundIntent1.setAccrualMoney(new BigDecimal(0));
						SlFundIntent1.setFlatMoney(new BigDecimal(0));
						Short isvalid = 0;
						SlFundIntent1.setIsValid(isvalid);
						/*if (SlFundIntent1.getFundType().equals("principalLending")) {
							SlFundIntent1.setIsCheck(Short.valueOf("0"));
						} else {*/
						SlFundIntent1.setIsCheck(Short.valueOf("1"));
						//}
						SlFundIntent1.setBusinessType(persistent.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
						slFundIntentService.save(SlFundIntent1);
					} else {
						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
						}
						SlFundIntent1.setBusinessType(persistent.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
						/*if (SlFundIntent1.getFundType().equals(
								"principalLending")) {
							SlFundIntent1.setIsCheck(Short.valueOf("0"));
						} else {*/
						SlFundIntent1.setIsCheck(Short.valueOf("1"));
						//}
						SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
						BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

						slFundIntentService.merge(slFundIntent2);

					}
					
				}
			}
			slSmallloanProjectService.merge(persistent);
			sb.append("}");
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 企贷直投标快速补录流程
	 * @return
	 */
	public String saveDirEnterpriseProjectInfo(){
		try{
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			StringBuffer sb = new StringBuffer("{success:true");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			
			//保存客户信息
			String gudongInfo=this.getRequest().getParameter("gudongInfo");
			enterpriseService.saveSingleEnterprise(enterprise, person, gudongInfo);
			//保存客户银行信息
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(persistent.getOppositeID().intValue(), Short.valueOf("0"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(persistent.getOppositeID().intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("0"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			//保存第一还款来源
			String repaymentSource=this.getRequest().getParameter("repaymentSource");
			if(null != repaymentSource && !"".equals(repaymentSource)) {

				String[] repaymentSourceArr = repaymentSource.split("@");
				
				for(int i=0; i<repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource temp = (SlRepaymentSource)JSONMapper.toJava(parser.nextValue(),SlRepaymentSource.class);
					boolean flag = StringUtil.isNumeric(temp.getTypeId());
					GlobalType globalType = globalTypeService.getByNodeKey("repaymentSource").get(0);
					if (flag == false) {
						Dictionary dic = new Dictionary();
						dic.setItemValue(temp.getTypeId());
						dic.setItemName(globalType.getTypeName());
						dic.setProTypeId(globalType.getProTypeId());
						dic.setDicKey(temp.getTypeId());
						dic.setStatus("0");
						dictionaryService.save(dic);
						temp.setTypeId(String.valueOf(dic.getDicId()));
					} else {
						Dictionary cd = dictionaryService.get(Long.valueOf(temp
								.getTypeId()));
						if (null == cd) {
							Dictionary dic = new Dictionary();
							dic.setItemValue(temp.getTypeId());
							dic.setItemName(globalType.getTypeName());
							dic.setProTypeId(globalType.getProTypeId());
							dic.setDicKey(temp.getTypeId());
							dic.setStatus("0");
							dictionaryService.save(dic);
							temp.setTypeId(String.valueOf(dic.getDicId()));
						}
					}
					temp.setProjId(slSmallloanProject.getProjectId());
					if (temp.getSourceId() == null) {
						this.slRepaymentSourceService.save(temp);
					} else {
						SlRepaymentSource rPersistent = this.slRepaymentSourceService.get(temp.getSourceId());
						BeanUtil.copyNotNullProperties(rPersistent, temp);
						this.slRepaymentSourceService.save(rPersistent);
					}
				
					
				}
			}
			//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {

				String[] borrowerInfoArr = borrowerInfo.split("@");
				
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}
			
			//保存费用信息
			String slActualToChargeJsonData=this.getRequest().getParameter("slActualToChargeJsonData");
			String isCheck=this.getRequest().getParameter("isCheck");
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}
		
			// 平台资金款项信息
			if(null!=fundProjectId && !fundProjectId.equals("")){
				BpFundProject platFormFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				if (null != platFormFund) {
					BeanUtil.copyNotNullProperties(platFormFund,slSmallloanProject);
					platFormFund.setPlatFormJointMoney(slSmallloanProject.getProjectMoney());
					bpFundProjectService.merge(platFormFund);
				}
			}
			slSmallloanProjectService.merge(persistent);
			sb.append("}");
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 企贷债权转让快速补录流程保存方法
	 * @return
	 */
	public String saveOrEnterpriseProjectInfo(){
		try{
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			StringBuffer sb = new StringBuffer("{success:true");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			
			//保存客户信息
			String gudongInfo=this.getRequest().getParameter("gudongInfo");
			enterpriseService.saveSingleEnterprise(enterprise, person, gudongInfo);
			//保存客户银行信息
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(persistent.getOppositeID().intValue(), Short.valueOf("0"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(persistent.getOppositeID().intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("0"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			//保存第一还款来源
			String repaymentSource=this.getRequest().getParameter("repaymentSource");
			if(null != repaymentSource && !"".equals(repaymentSource)) {

				String[] repaymentSourceArr = repaymentSource.split("@");
				
				for(int i=0; i<repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource temp = (SlRepaymentSource)JSONMapper.toJava(parser.nextValue(),SlRepaymentSource.class);
					boolean flag = StringUtil.isNumeric(temp.getTypeId());
					GlobalType globalType = globalTypeService.getByNodeKey("repaymentSource").get(0);
					if (flag == false) {
						Dictionary dic = new Dictionary();
						dic.setItemValue(temp.getTypeId());
						dic.setItemName(globalType.getTypeName());
						dic.setProTypeId(globalType.getProTypeId());
						dic.setDicKey(temp.getTypeId());
						dic.setStatus("0");
						dictionaryService.save(dic);
						temp.setTypeId(String.valueOf(dic.getDicId()));
					} else {
						Dictionary cd = dictionaryService.get(Long.valueOf(temp
								.getTypeId()));
						if (null == cd) {
							Dictionary dic = new Dictionary();
							dic.setItemValue(temp.getTypeId());
							dic.setItemName(globalType.getTypeName());
							dic.setProTypeId(globalType.getProTypeId());
							dic.setDicKey(temp.getTypeId());
							dic.setStatus("0");
							dictionaryService.save(dic);
							temp.setTypeId(String.valueOf(dic.getDicId()));
						}
					}
					temp.setProjId(slSmallloanProject.getProjectId());
					if (temp.getSourceId() == null) {
						this.slRepaymentSourceService.save(temp);
					} else {
						SlRepaymentSource rPersistent = this.slRepaymentSourceService.get(temp.getSourceId());
						BeanUtil.copyNotNullProperties(rPersistent, temp);
						this.slRepaymentSourceService.save(rPersistent);
					}
				
					
				}
			}
			//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {

				String[] borrowerInfoArr = borrowerInfo.split("@");
				
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}
			
			//保存费用信息
			String slActualToChargeJsonData=this.getRequest().getParameter("slActualToChargeJsonData");
			String isCheck=this.getRequest().getParameter("isCheck");
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}
		
			// 自有资金款项信息
			if(null!=fundProjectId && !fundProjectId.equals("")){
				String mineType=this.getRequest().getParameter("ownBpFundProject.mineType");
				String mineId=this.getRequest().getParameter("ownBpFundProject.mineId");
				String reciverType=this.getRequest().getParameter("ownBpFundProject.reciverType");
				String receiverName=this.getRequest().getParameter("ownBpFundProject.receiverName");
				String receiverId=this.getRequest().getParameter("ownBpFundProject.receiverId");
				String receiverP2PAccountNumber=this.getRequest().getParameter("ownBpFundProject.receiverP2PAccountNumber");
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
					ownFund.setPlatFormJointMoney(slSmallloanProject.getProjectMoney());
					ownFund.setMineId(ownFund.getMineId());
					ownFund.setMineType(ownFund.getMineType());
					ownFund.setReciverType(reciverType);
					ownFund.setReceiverName(receiverName);
					ownFund.setReceiverP2PAccountNumber(receiverP2PAccountNumber);
					if(receiverId!=null&&!"".equals(receiverId)){
						ownFund.setReciverId(Long.valueOf(receiverId));
					}
					if(null!=ownFund){
						ownFund.setMineId(ownFund.getMineId());
						ownFund.setMineType(ownFund.getMineType());
					}
					bpFundProjectService.merge(ownFund);
				}
				//保存款项信息
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							/*if (SlFundIntent1.getFundType().equals("principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							/*if (SlFundIntent1.getFundType().equals(
									"principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			slSmallloanProjectService.merge(persistent);
			sb.append("}");
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	
	/**
	 * 审批项目查询中的编辑页面保存方法
	 * @return
	 */
	public String saveApproveProjectInfo(){

		try{
			StringBuffer sb = new StringBuffer("{success:true");
			//保存个人客户信息
			String str=personService.savePersonInfo(person, spouse, workCompany);
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			//保存企业客户信息
			String gudongInfo=this.getRequest().getParameter("gudongInfo");
			enterpriseService.saveSingleEnterprise(enterprise, person, gudongInfo);
			//保存客户银行信息
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(persistent.getOppositeID().intValue(), Short.valueOf("0"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(persistent.getOppositeID().intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("0"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			// 更新借款需求
			if(null!=bpMoneyBorrowDemand){
				bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
				if (bpMoneyBorrowDemand.getBorrowid() == null) {
					this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
				} else {
					BpMoneyBorrowDemand bbd = bpMoneyBorrowDemandService.getByBorrowId(bpMoneyBorrowDemand.getBorrowid());
					BeanUtil.copyNotNullProperties(bbd, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(bbd);
				}
				sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
			}
			//保存费用信息
			/*String chargeJson=this.getRequest().getParameter("chargeJson");
			String isCheck=this.getRequest().getParameter("isCheck");
			if(null!=chargeJson && !chargeJson.equals("")){
				slActualToChargeService.savejson(chargeJson, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}*/
			//保存网审信息
			String netCheckData=this.getRequest().getParameter("netCheckData");
			slSmallloanProjectService.updateNetCheckInfo(netCheckData);
			
			// 自有资金款项信息
			if(null!=ownBpFundProject){
				BpFundProject ownFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("0"));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
					bpFundProjectService.merge(ownFund);
				}
			}

			// 平台资金款项信息
			if(null!=platFormBpFundProject){
				BpFundProject platFormFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("1"));
				if (null != platFormFund) {
					BeanUtil.copyNotNullProperties(platFormFund,platFormBpFundProject);
					bpFundProjectService.merge(platFormFund);
				}
			}
			//保存款项信息
			String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
			if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
				List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownBpFundProject.getId());
				for (SlFundIntent sfi : oldList) {
					if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
						slFundIntentService.remove(sfi);
					}
				}
				String[] shareequityArr = fundIntentJsonData.split("@");
				for (int i = 0; i < shareequityArr.length; i++) {
					String fundIntentstr = shareequityArr[i];
					JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
			
					SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
					SlFundIntent1.setProjectId(persistent.getProjectId());
					SlFundIntent1.setProjectName(persistent.getProjectName());
					SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

					if (null == SlFundIntent1.getFundIntentId()) {

						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
						}
						SlFundIntent1.setAfterMoney(new BigDecimal(0));
						SlFundIntent1.setAccrualMoney(new BigDecimal(0));
						SlFundIntent1.setFlatMoney(new BigDecimal(0));
						Short isvalid = 0;
						SlFundIntent1.setIsValid(isvalid);
						/*if (SlFundIntent1.getFundType().equals("principalLending")) {
							SlFundIntent1.setIsCheck(Short.valueOf("0"));
						} else {*/
						SlFundIntent1.setIsCheck(Short.valueOf("1"));
						//}
						SlFundIntent1.setBusinessType(persistent.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
						slFundIntentService.save(SlFundIntent1);
					} else {
						BigDecimal lin = new BigDecimal(0.00);
						if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
							SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
						} else {
							SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
						}
						SlFundIntent1.setBusinessType(persistent.getBusinessType());
						SlFundIntent1.setCompanyId(persistent.getCompanyId());
						/*if (SlFundIntent1.getFundType().equals(
								"principalLending")) {
							SlFundIntent1.setIsCheck(Short.valueOf("0"));
						} else {*/
						SlFundIntent1.setIsCheck(Short.valueOf("1"));
						//}
						SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
						BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

						slFundIntentService.merge(slFundIntent2);

					}
					
				}
			}
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append(","+str);
			sb.append("}");
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	
	}
	public String saveCust(){
		
		return null;
	}
	
	//个贷标准流程快速保存方法（睿本）
	public String saveSmallProjectInfo(){
		try{
			String thisTaskName=getRequest().getParameter("thisTaskName");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			StringBuffer sb = new StringBuffer("{success:true");
//			//保证金
//			CashDeposit  cd = new CashDeposit();
//			BeanUtil.populateEntity(this.getRequest(), cd, "cashDeposit");
			
//			//授信信息
//			BankCredit  bank = new BankCredit();
//			BeanUtil.populateEntity(this.getRequest(), bank, "bankCredit");
			
			//保存客户信息
			String str=personService.savePersonInfo(person, spouse, workCompany);
//			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
//			persistent.setBankCreditId(bank.getId());//保存授信表的主键
//			persistent.setBaozhengjinId(cd.getId());//保存保证金表的主键
			if(slSmallloanProject.getExamineProjectMoney()!=null&&!"".equals(slSmallloanProject.getExamineProjectMoney())){
				persistent.setProjectMoney(slSmallloanProject.getExamineProjectMoney());
			}
			if(enterpriseBank!=null){
				if(enterpriseBank.getId()!=null){
					EnterpriseBank e=enterpriseBankService.getById(enterpriseBank.getId());
					e.setAccountType(enterpriseBank.getAccountType());
					e.setName(enterpriseBank.getName());
					e.setAccountnum(enterpriseBank.getAccountnum());
					e.setBankid(enterpriseBank.getBankid());
					e.setBankOutletsName(enterpriseBank.getBankOutletsName());
					e.setAreaId(enterpriseBank.getAreaId());
					e.setIscredit(Short.valueOf("0"));
					e.setAreaName(enterpriseBank.getAreaName());
					enterpriseBankService.merge(e);
				}
				else{
					enterpriseBank.setIscredit(Short.valueOf("0"));
					if("person_customer".equals(persistent.getOppositeType())){
						enterpriseBank.setIsEnterprise(Short.valueOf("0"));
						enterpriseBank.setEnterpriseid(person.getId());
					}else{
						enterpriseBank.setIsEnterprise(Short.valueOf("1"));
						enterpriseBank.setEnterpriseid(enterprise.getId());
					}
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					if(enterpriseBank.getAccountnum()!=null){
						enterpriseBankService.save(enterpriseBank);
					}
				}
			}
		/*	//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {
				String[] borrowerInfoArr = borrowerInfo.split("@");
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str1 = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str1));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}*/
			//保存网审信息
			String netCheckData=this.getRequest().getParameter("netCheckData");
			slSmallloanProjectService.updateNetCheckInfo(netCheckData);
			// 更新借款需求
			BpMoneyBorrowDemand moneyBorrowDemand=bpMoneyBorrowDemandServie.getByProjectId(slSmallloanProject.getProjectId());
			if(null!=moneyBorrowDemand){
				if(bpMoneyBorrowDemand!=null){
					BeanUtil.copyNotNullProperties(moneyBorrowDemand, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(moneyBorrowDemand);
					sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
				}
			}else {
				if(bpMoneyBorrowDemand!=null){
					bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
					bpMoneyBorrowDemand=this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
					sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
				}
			}
			
			// 自有资金款项信息
			if(null!=ownBpFundProject){
				BpFundProject ownFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("0"));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
					bpFundProjectService.merge(ownFund);
				}
			}

			// 平台资金款项信息
			if(null!=platFormBpFundProject){
				BpFundProject platFormFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("1"));
				if (null != platFormFund) {
					BeanUtil.copyNotNullProperties(platFormFund,platFormBpFundProject);
					bpFundProjectService.merge(platFormFund);
				}
			}
			
			if(null!=thisTaskName && thisTaskName.equals("czzy")){
				//保存款项信息
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				
				String isValid = "0";
				String isCheck = "0";
				
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getByProjectId(slSmallloanProject.getProjectId(),"SmallLoan");
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());
						
						//配置顺延至和宽限至
						//获得顺延日
						CsHoliday ch=csHolidayDao.getMaxDeferDay(SlFundIntent1.getIntentDate());
						if(null!=ch){
							SlFundIntent1.setContinueDay(sdf.format(DateUtil.addDaysToDate(ch.getYearStr(),1)));
						}else{
							SlFundIntent1.setContinueDay(sdf.format(SlFundIntent1.getIntentDate()));
						}
						//获得宽限日
						Date newEnd=DateUtil.addDaysToDate(DateUtil.parseDate(SlFundIntent1.getContinueDay()),AppUtil.getGraceDayNum());
						SlFundIntent1.setGraceDay(sdf.format(newEnd));//宽限至
						
						if (null == SlFundIntent1.getFundIntentId()) {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setOverdureMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							SlFundIntent1.setIsValid(Short.valueOf(isValid));
							SlFundIntent1.setIsCheck(Short.valueOf(isCheck));
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							SlFundIntent1.setIsValid(Short.valueOf(isValid));
							SlFundIntent1.setIsCheck(Short.valueOf(isCheck));
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							if(slFundIntent2!=null&&!"".equals(slFundIntent2)){
								BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);
								slFundIntentService.merge(slFundIntent2);
							}
						}
					}
				}
			}
			//费用清单信息
			String chargeJson=this.getRequest().getParameter("chargeJson");
			if(null!=chargeJson && !chargeJson.equals("")){
				slActualToChargeService.savejson(chargeJson, persistent.getProjectId(),persistent.getBusinessType(), Short.valueOf("0"), persistent.getCompanyId());
			}
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			
			//归档信息
			if(null!=plProjectArchives){
				if(plProjectArchives.getProjtoarchiveId()==null){
					plProjectArchives.setBusinessType(persistent.getBusinessType());
					plProjectArchives.setProjectId(persistent.getProjectId());
					plProjectArchivesService.save(plProjectArchives);
				}else{
					PlProjectArchives orgPlProjectArchives=plProjectArchivesService.get(plProjectArchives.getProjtoarchiveId());
					try{
						BeanUtil.copyNotNullProperties(orgPlProjectArchives, plProjectArchives);
						plProjectArchivesService.merge(orgPlProjectArchives);
					}catch(Exception ex){
						logger.error(ex.getMessage());
					}
				}
			}
			
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			/*sb.append(","+str+",bankId:"+enterpriseBank.getId());
			sb.append("}");
			jsonString=sb.toString();*/
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	//个贷标准流程快速保存方法  （中卓运营）
	public String saveZZYYSmallProjectInfo(){
		try{
			String thisTaskName=getRequest().getParameter("thisTaskName");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			StringBuffer sb = new StringBuffer("{success:true");
//			//保证金
//			CashDeposit  cd = new CashDeposit();
//			BeanUtil.populateEntity(this.getRequest(), cd, "cashDeposit");
			
//			//授信信息
//			BankCredit  bank = new BankCredit();
//			BeanUtil.populateEntity(this.getRequest(), bank, "bankCredit");
			
			//保存客户信息
			String str=personService.savePersonInfo(person, spouse, workCompany);
//			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
//			persistent.setBankCreditId(bank.getId());//保存授信表的主键
//			persistent.setBaozhengjinId(cd.getId());//保存保证金表的主键
			if(slSmallloanProject.getExamineProjectMoney()!=null&&!"".equals(slSmallloanProject.getExamineProjectMoney())){
				persistent.setProjectMoney(slSmallloanProject.getExamineProjectMoney());
			}
			if(enterpriseBank!=null){
				if(enterpriseBank.getId()!=null){
					EnterpriseBank e=enterpriseBankService.getById(enterpriseBank.getId());
					e.setAccountType(enterpriseBank.getAccountType());
					e.setName(enterpriseBank.getName());
					e.setAccountnum(enterpriseBank.getAccountnum());
					e.setBankid(enterpriseBank.getBankid());
					e.setBankOutletsName(enterpriseBank.getBankOutletsName());
					e.setAreaId(enterpriseBank.getAreaId());
					e.setIscredit(Short.valueOf("0"));
					e.setAreaName(enterpriseBank.getAreaName());
					enterpriseBankService.merge(e);
				}
				else{
					enterpriseBank.setIscredit(Short.valueOf("0"));
					if("person_customer".equals(persistent.getOppositeType())){
						enterpriseBank.setIsEnterprise(Short.valueOf("0"));
						enterpriseBank.setEnterpriseid(person.getId());
					}else{
						enterpriseBank.setIsEnterprise(Short.valueOf("1"));
						enterpriseBank.setEnterpriseid(enterprise.getId());
					}
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					if(enterpriseBank.getAccountnum()!=null){
						enterpriseBankService.save(enterpriseBank);
					}
				}
			}
				//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {
				String[] borrowerInfoArr = borrowerInfo.split("@");
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str1 = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str1));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}
			//保存网审信息
			String netCheckData=this.getRequest().getParameter("netCheckData");
			slSmallloanProjectService.updateNetCheckInfo(netCheckData);
			// 更新借款需求
			BpMoneyBorrowDemand moneyBorrowDemand=bpMoneyBorrowDemandServie.getByProjectId(slSmallloanProject.getProjectId());
			if(null!=moneyBorrowDemand){
				if(bpMoneyBorrowDemand!=null){
					BeanUtil.copyNotNullProperties(moneyBorrowDemand, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(moneyBorrowDemand);
					sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
				}
			}else {
				if(bpMoneyBorrowDemand!=null){
					bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
					bpMoneyBorrowDemand=this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
					sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
				}
			}
			
			// 自有资金款项信息
			if(null!=ownBpFundProject){
				BpFundProject ownFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("0"));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
					bpFundProjectService.merge(ownFund);
				}
			}
			
			// 平台资金款项信息
			if(null!=platFormBpFundProject){
				BpFundProject platFormFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("1"));
				if (null != platFormFund) {
					BeanUtil.copyNotNullProperties(platFormFund,platFormBpFundProject);
					bpFundProjectService.merge(platFormFund);
				}
			}
			
			if(null!=thisTaskName && thisTaskName.equals("czzy")){
				//保存款项信息
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				
				String isValid = "0";
				String isCheck = "0";
				
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getByProjectId(slSmallloanProject.getProjectId(),"SmallLoan");
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());
						
						//配置顺延至和宽限至
						//获得顺延日
						CsHoliday ch=csHolidayDao.getMaxDeferDay(SlFundIntent1.getIntentDate());
						if(null!=ch){
							SlFundIntent1.setContinueDay(sdf.format(DateUtil.addDaysToDate(ch.getYearStr(),1)));
						}else{
							SlFundIntent1.setContinueDay(sdf.format(SlFundIntent1.getIntentDate()));
						}
						//获得宽限日
						Date newEnd=DateUtil.addDaysToDate(DateUtil.parseDate(SlFundIntent1.getContinueDay()),AppUtil.getGraceDayNum());
						SlFundIntent1.setGraceDay(sdf.format(newEnd));//宽限至
						
						if (null == SlFundIntent1.getFundIntentId()) {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setOverdureMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							SlFundIntent1.setIsValid(Short.valueOf(isValid));
							SlFundIntent1.setIsCheck(Short.valueOf(isCheck));
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							SlFundIntent1.setIsValid(Short.valueOf(isValid));
							SlFundIntent1.setIsCheck(Short.valueOf(isCheck));
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							if(slFundIntent2!=null&&!"".equals(slFundIntent2)){
								BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);
								slFundIntentService.merge(slFundIntent2);
							}
						}
					}
				}
			}
			//费用清单信息
			String chargeJson=this.getRequest().getParameter("chargeJson");
			if(null!=chargeJson && !chargeJson.equals("")){
				slActualToChargeService.savejson(chargeJson, persistent.getProjectId(),persistent.getBusinessType(), Short.valueOf("0"), persistent.getCompanyId());
			}
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			
			//归档信息
			if(null!=plProjectArchives){
				if(plProjectArchives.getProjtoarchiveId()==null){
					plProjectArchives.setBusinessType(persistent.getBusinessType());
					plProjectArchives.setProjectId(persistent.getProjectId());
					plProjectArchivesService.save(plProjectArchives);
				}else{
					PlProjectArchives orgPlProjectArchives=plProjectArchivesService.get(plProjectArchives.getProjtoarchiveId());
					try{
						BeanUtil.copyNotNullProperties(orgPlProjectArchives, plProjectArchives);
						plProjectArchivesService.merge(orgPlProjectArchives);
					}catch(Exception ex){
						logger.error(ex.getMessage());
					}
				}
			}
			
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			/*sb.append(","+str+",bankId:"+enterpriseBank.getId());
			sb.append("}");
			jsonString=sb.toString();*/
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/**
	 * 个贷债权转让快速补录流程
	 */
	public String saveOrFastSmallProjectInfo(){
		try{
			StringBuffer sb = new StringBuffer("{success:true");
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			//保存客户信息
			String str=personService.savePersonInfo(person, spouse, workCompany);
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			
			// 更新借款需求
			if(null!=bpMoneyBorrowDemand){
				bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
				if (bpMoneyBorrowDemand.getBorrowid() == null) {
					this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
				} else {
					BpMoneyBorrowDemand bbd = bpMoneyBorrowDemandService.getByBorrowId(bpMoneyBorrowDemand.getBorrowid());
					BeanUtil.copyNotNullProperties(bbd, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(bbd);
				}
				sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
			}


			// 自有资金款项信息
			if(null!=fundProjectId && !fundProjectId.equals("")){
				String mineType=this.getRequest().getParameter("ownBpFundProject.mineType");
				String mineId=this.getRequest().getParameter("ownBpFundProject.mineId");
				String reciverType=this.getRequest().getParameter("ownBpFundProject.reciverType");
				String receiverName=this.getRequest().getParameter("ownBpFundProject.receiverName");
				String receiverId=this.getRequest().getParameter("ownBpFundProject.receiverId");
				String receiverP2PAccountNumber=this.getRequest().getParameter("ownBpFundProject.receiverP2PAccountNumber");
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
				ownFund.setOwnJointMoney(slSmallloanProject.getProjectMoney());
				if(null!=ownBpFundProject){
					ownFund.setMineId(ownBpFundProject.getMineId());
					ownFund.setMineType(ownBpFundProject.getMineType());
					ownFund.setReciverType(reciverType);
					ownFund.setReceiverName(receiverName);
					ownFund.setReceiverP2PAccountNumber(receiverP2PAccountNumber);
					if(receiverId!=null&&!"".equals(receiverId)){
						ownFund.setReciverId(Long.valueOf(receiverId));
					}
				}
				bpFundProjectService.merge(ownFund);
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							/*if (SlFundIntent1.getFundType().equals("principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							/*if (SlFundIntent1.getFundType().equals(
									"principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append(","+str);
			sb.append("}");
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 直投标快速补录流程快速保存
	 * @return
	 */
	public String saveFastSmallProjectInfo(){
		try{
			StringBuffer sb = new StringBuffer("{success:true");
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			//保存客户信息
			String str=personService.savePersonInfo(person, spouse, workCompany);
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			
			// 更新借款需求
			if(null!=bpMoneyBorrowDemand){
				bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
				if (bpMoneyBorrowDemand.getBorrowid() == null) {
					this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
				} else {
					BpMoneyBorrowDemand bbd = bpMoneyBorrowDemandService.getByBorrowId(bpMoneyBorrowDemand.getBorrowid());
					BeanUtil.copyNotNullProperties(bbd, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(bbd);
				}
				sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
			}


			// 平台资金款项信息
			if(null!=fundProjectId && !fundProjectId.equals("")){
				BpFundProject platFormFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				BeanUtil.copyNotNullProperties(platFormFund,slSmallloanProject);
				platFormFund.setPlatFormJointMoney(slSmallloanProject.getProjectMoney());
				bpFundProjectService.merge(platFormFund);
				
			}
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append(","+str);
			sb.append("}");
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	public String getCreditProjectInfo() {
		String projectId = this.getRequest().getParameter("slProjectId"); // 项目ID
		String taskId = this.getRequest().getParameter("slTaskId"); // 任务ID

		String bidPlanId = this.getRequest().getParameter("bidPlanId");
		//读取项目信息
		SlSmallloanProject project = this.slSmallloanProjectService.get(Long.valueOf(projectId));
	
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 读取个人客户信息
			Person p = new Person();
			Enterprise enterprise = new Enterprise();
			short sub = 0;
			if (project.getOppositeType().equals("company_customer")) {
				sub = 0;
				Enterprise enterprise1 = enterpriseService.getById(project
						.getOppositeID().intValue());
				if (enterprise1.getLegalpersonid() != null) {
					p = this.personService.getById(enterprise1
							.getLegalpersonid());
					map.put("person", p);
				}
				if (null != enterprise1.getHangyeType()) {
					if (null != areaDicService.getById(enterprise1
							.getHangyeType())) {
						enterprise1.setHangyeName(areaDicService.getById(
								enterprise1.getHangyeType()).getText());
					}
				}
				map.put("enterprise", enterprise1);
				List<EnterpriseBank> list = enterpriseBankService.getBankList(project.getOppositeID().intValue(), sub, Short.valueOf("0"), Short.valueOf("0"));
				if (null != list && list.size() > 0) {
					EnterpriseBank bank = list.get(0);
					map.put("enterpriseBank", bank);
				}

			} else if (project.getOppositeType().equals("person_customer")) {
				sub = 1;
				p = this.personService.getById(project.getOppositeID()
						.intValue());
				map.put("person", p);
				if (null != p) {
					if (null != p.getId()) {
						if (null != p.getMarry() && p.getMarry() == 317) {
							Spouse spouse = spouseService.getByPersonId(p
									.getId());
							if(null!=spouse){
								map.put("spouse", spouse);
							}
						}
					}
				}
			}
			
			String mineName = getMainName(project.getMineType(), project.getMineId());
			//
			map.put("mineName", mineName);
			project.setMineName(mineName);
			map.put("person", p);
			// 个人旗下公司
			WorkCompany workCompany = workCompanyService
					.getWorkCompanyByPersonId(p.getId());
			map.put("workCompany", workCompany);
			// 借款需求
			if (null != projectId && !"".equals(projectId)) {
				List<BpMoneyBorrowDemand> list = this.bpMoneyBorrowDemandService
						.getMessageByProjectID(Long.valueOf(projectId));
				if (list.size() > 0) {
					bpMoneyBorrowDemand = list.get(0);
					map.put("bpMoneyBorrowDemand", bpMoneyBorrowDemand);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取信贷流程项目相关信息:" + e.getMessage());
		}

		List<ProcessRun> runList = processRunService.getByProcessNameProjectId(
				project.getProjectId(), project.getBusinessType(), project
						.getFlowType());
		if (runList != null && runList.size() != 0) {
			List<TaskSignData> tsdList = taskSignDataService.getByRunId(runList
					.get(0).getRunId());
			if (tsdList.size() > 0) {
				map.put("isOnline", true);
			} else {
				map.put("isOnline", false);
			}
		}
		// 自有资金
		BpFundProject ownFund = bpFundProjectService.getByProjectId(Long
				.valueOf(projectId), Short.valueOf("0"));
		if (null != ownFund) {
			map.put("ownBpFundProject", ownFund);
		}
		
		String businessTypeKey = creditProjectService.getGlobalTypeValue(
				"businessType", project.getBusinessType(), project
						.getProjectId());
		String operationTypeKey = creditProjectService.getGlobalTypeValue(
				"operationType", project.getBusinessType(), project
						.getProjectId());
		String definitionTypeKey = creditProjectService.getGlobalTypeValue(
				"definitionType", project.getBusinessType(), project
						.getProjectId());
		
		
		map.put("slSmallloanProject", project);
		
		map.put("businessTypeKey", businessTypeKey);
		map.put("operationTypeKey", operationTypeKey);
		map.put("definitionTypeKey", definitionTypeKey);
		if (null != project.getProductId()
				&& !"".equals(project.getProductId())) {
			BpProductParameter bpProductParameter = bpProductParameterService
					.get(project.getProductId());
			map.put("bpProductParameter", bpProductParameter);
		}
		if (null != taskId && !"".equals(taskId)) {
			ProcessForm pform = processFormService.getByTaskId(taskId);
			if (pform == null) {
				pform = creditProjectService.getCommentsByTaskId(taskId);
			}
			if (pform != null && pform.getComments() != null
					&& !"".equals(pform.getComments())) {
				map.put("comments", pform.getComments());
			}
		}
		//plBidPlan
		if(bidPlanId!=null&&!"".equals(bidPlanId)&&!"undefined".equals(bidPlanId)){
			PlBidPlan plBidPlan = plBidPlanService.get(Long.parseLong(bidPlanId));
			if(plBidPlan!=null){
				map.put("plBidPlan", plBidPlan);
			}
		}
		if(project.getAppUserId()!=null&&!project.getAppUserId().contains(",")){
			AppUser au = appUserService.get(Long.valueOf(project.getAppUserId()));
			map.put("appUserNumber", au.getUserNumber());
		}
		doJson(map);
		return SUCCESS;
	}
	/*
	 * 审批项目查询
	 */
	public String approveProjectList(){
		Object ids=this.getRequest().getSession().getAttribute("userIds");
		Map<String,String> map=GroupUtil.separateFactor(getRequest(),ids);
		String userIdsStr=map.get("userId");
		
		PageBean<SlSmallloanProject> pageBean = new PageBean<SlSmallloanProject>(start, limit,getRequest());
		slSmallloanProjectService.approveProjectList(pageBean,Short.valueOf(projectStatus),userIdsStr);
	
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
		.append(pageBean.getTotalCounts()).append(",result:" );
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		buff.append(gson.toJson(pageBean.getResult()));
		buff.append("}");
		jsonString = buff.toString();
		return SUCCESS;
	
	}
	
	/*
	 * 企业客户小贷的保存方法
	 */
	public String saveEnterpriseSmallProjectInfo(){
		try{
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			StringBuffer sb = new StringBuffer("{success:true");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			
			//保存客户信息
			String gudongInfo=this.getRequest().getParameter("gudongInfo");
			enterpriseService.saveSingleEnterprise(enterprise, person, gudongInfo);
			//保存客户银行信息
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(persistent.getOppositeID().intValue(), Short.valueOf("0"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(persistent.getOppositeID().intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("0"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			//保存第一还款来源
			String repaymentSource=this.getRequest().getParameter("repaymentSource");
			if(null != repaymentSource && !"".equals(repaymentSource)) {

				String[] repaymentSourceArr = repaymentSource.split("@");
				
				for(int i=0; i<repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource temp = (SlRepaymentSource)JSONMapper.toJava(parser.nextValue(),SlRepaymentSource.class);
					boolean flag = StringUtil.isNumeric(temp.getTypeId());
					GlobalType globalType = globalTypeService.getByNodeKey("repaymentSource").get(0);
					if (flag == false) {
						Dictionary dic = new Dictionary();
						dic.setItemValue(temp.getTypeId());
						dic.setItemName(globalType.getTypeName());
						dic.setProTypeId(globalType.getProTypeId());
						dic.setDicKey(temp.getTypeId());
						dic.setStatus("0");
						dictionaryService.save(dic);
						temp.setTypeId(String.valueOf(dic.getDicId()));
					} else {
						Dictionary cd = dictionaryService.get(Long.valueOf(temp
								.getTypeId()));
						if (null == cd) {
							Dictionary dic = new Dictionary();
							dic.setItemValue(temp.getTypeId());
							dic.setItemName(globalType.getTypeName());
							dic.setProTypeId(globalType.getProTypeId());
							dic.setDicKey(temp.getTypeId());
							dic.setStatus("0");
							dictionaryService.save(dic);
							temp.setTypeId(String.valueOf(dic.getDicId()));
						}
					}
					temp.setProjId(slSmallloanProject.getProjectId());
					if (temp.getSourceId() == null) {
						this.slRepaymentSourceService.save(temp);
					} else {
						SlRepaymentSource rPersistent = this.slRepaymentSourceService.get(temp.getSourceId());
						BeanUtil.copyNotNullProperties(rPersistent, temp);
						this.slRepaymentSourceService.save(rPersistent);
					}
				
					
				}
			}
			//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {

				String[] borrowerInfoArr = borrowerInfo.split("@");
				
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}
			
			//保存费用信息
			String slActualToChargeJsonData=this.getRequest().getParameter("slActualToChargeJsonData");
			String isCheck=this.getRequest().getParameter("isCheck");
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}
			//保存会议纪要
			if(null!=slConferenceRecord){
				if(slConferenceRecord.getConforenceId()==null){
					slConferenceRecordService.save(slConferenceRecord);
					Long conforenceId=slConferenceRecord.getConforenceId();
					
				}else{
					SlConferenceRecord orgSlConferenceRecord=slConferenceRecordService.get(slConferenceRecord.getConforenceId());
					BeanUtil.copyNotNullProperties(orgSlConferenceRecord, slConferenceRecord);
					slConferenceRecordService.save(orgSlConferenceRecord);
				
				}
				sb.append(",conforenceId:"+slConferenceRecord.getConforenceId());
			}
			// 自有资金款项信息
			if(null!=fundProjectId && !fundProjectId.equals("")){
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
					if(null!=slSmallloanProject.getProjectMoney()){
						ownFund.setPlatFormJointMoney(slSmallloanProject.getProjectMoney());
					}
					if(null!=ownBpFundProject){
						BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
					}
					bpFundProjectService.merge(ownFund);
				}
				//保存款项信息
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							/*if (SlFundIntent1.getFundType().equals("principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf(isCheck));
							//}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							/*if (SlFundIntent1.getFundType().equals(
									"principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf(isCheck));
							//}
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			slSmallloanProjectService.merge(persistent);
			sb.append("}");
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 企业客户小贷读取项目信息
	 */
	public String getEntSmallProjectInfo() {
		String projectId = this.getRequest().getParameter("slProjectId"); // 项目ID
		String taskId = this.getRequest().getParameter("slTaskId"); // 任务ID
		SlSmallloanProject project = this.slSmallloanProjectService.get(Long.valueOf(projectId));
		Map<String, Object> map = new HashMap<String, Object>();
		
		String financeServiceMineName = "";
		String managementConsultingMineName = "";
		if (null != project.getFinanceServiceMineType() && !"".equals(project.getFinanceServiceMineType())) {
			if (null != project.getFinanceServiceMineId() && !"".equals(project.getFinanceServiceMineId())) {
				if (project.getFinanceServiceMineType().equals("company_ourmain")) {
					financeServiceMineName = this.slCompanyMainService.get(project.getFinanceServiceMineId()).getCorName();
				} else {
					financeServiceMineName = this.slPersonMainService.get(project.getFinanceServiceMineId()).getName();
				}
			}
		}

		if (null != project.getManagementConsultingMineType() && !"".equals(project.getManagementConsultingMineType())) {
			if (null != project.getManagementConsultingMineId() && !"".equals(project.getManagementConsultingMineId())) {
				if (project.getManagementConsultingMineType().equals("company_ourmain")) {
					managementConsultingMineName = this.slCompanyMainService.get(project.getManagementConsultingMineId()).getCorName();
				} else {
					managementConsultingMineName = this.slPersonMainService.get(project.getManagementConsultingMineId()).getName();
				}
			}
		}
	

			Person p = new Person();
			// if判断是企业客户 elseif是个人客户
			Short sub = 0;
			if (project.getOppositeType().equals("company_customer")) {
				sub = 0;
				Enterprise enterprise1 = enterpriseService.getById(project
						.getOppositeID().intValue());
				if (enterprise1.getLegalpersonid() != null) {
					p = this.personService.getById(enterprise1.getLegalpersonid());
					map.put("person", p);
				}
				if (null != enterprise1.getHangyeType()) {
					if (null != areaDicService.getById(enterprise1.getHangyeType())) {
						enterprise1.setHangyeName(areaDicService.getById(enterprise1.getHangyeType()).getText());
					}
				}
				map.put("enterprise", enterprise1);

			} 
			if (null != project.getOppositeID()) {
				List<EnterpriseBank> list = enterpriseBankService.getBankList(project.getOppositeID().intValue(), sub, Short.valueOf("0"), Short.valueOf("0"));
				if (null != list && list.size() > 0) {
					EnterpriseBank bank = list.get(0);
					map.put("enterpriseBank", bank);
				}
			}
		
		
		
		
		// 自有资金
		BpFundProject ownFund = bpFundProjectService.getByProjectId(Long
				.valueOf(projectId), Short.valueOf("0"));
		if (null != ownFund) {
			map.put("ownBpFundProject", ownFund);
		}

		
		List<ProcessRun> runList = processRunService.getByProcessNameProjectId(project.getProjectId(), project.getBusinessType(), project.getFlowType());
		if(runList!=null&&runList.size()!=0){
			List<TaskSignData> tsdList =  taskSignDataService.getByRunId(runList.get(0).getRunId());
			if(tsdList.size()>0) {
				map.put("isOnline", true);
			}else {
				map.put("isOnline", false);
			}
		}
		String businessTypeKey = creditProjectService.getGlobalTypeValue("businessType", project.getBusinessType(), project.getProjectId());
		String operationTypeKey = creditProjectService.getGlobalTypeValue("operationType", project.getBusinessType(), project.getProjectId());
		String definitionTypeKey = creditProjectService.getGlobalTypeValue("definitionType", project.getBusinessType(), project.getProjectId());
	
		map.put("slSmallloanProject", project);
		map.put("financeServiceMineName", financeServiceMineName);
		map.put("managementConsultingMineName", managementConsultingMineName);
		map.put("businessType", project.getBusinessType());
		map.put("isAheadPay", project.getIsAheadPay());
		map.put("businessTypeKey", businessTypeKey);
		map.put("operationTypeKey", operationTypeKey);
		map.put("definitionTypeKey", definitionTypeKey);
		map.put("flowTypeKey", this.proDefinitionService.getProdefinitionByProcessName(project.getFlowType()).getName());
	
		if (null != taskId && !"".equals(taskId)) {
			ProcessForm pform = processFormService.getByTaskId(taskId);
			if (pform == null) {
				pform = creditProjectService.getCommentsByTaskId(taskId);
			}
			if (pform != null && pform.getComments() != null && !"".equals(pform.getComments())) {
				map.put("comments", pform.getComments());
			}
		}
		
		doJson(map);
		
		return SUCCESS;
	}
	/**
	 * 个贷债权转让快速补录流程
	 */
	public String savePersonSmallProjectInfo(){
		try{
			StringBuffer sb = new StringBuffer("{success:true");
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			String isCheck=this.getRequest().getParameter("isCheck");
			//保存客户信息
			String s=personService.savePersonInfo(person, spouse, workCompany);
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(person.getId(), Short.valueOf("1"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						enterpriseBankService.merge(e);
					}
					enterpriseBank.setEnterpriseid(person.getId());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("1"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					enterpriseBankService.save(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					enterpriseBankService.merge(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			
			// 更新借款需求
			if(null!=bpMoneyBorrowDemand){
				bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
				if (bpMoneyBorrowDemand.getBorrowid() == null) {
					this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
				} else {
					BpMoneyBorrowDemand bbd = bpMoneyBorrowDemandService.getByBorrowId(bpMoneyBorrowDemand.getBorrowid());
					BeanUtil.copyNotNullProperties(bbd, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(bbd);
				}
				sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
			}

			//保存第一还款来源
			String repaymentSource=this.getRequest().getParameter("repaymentSource");
			if(null != repaymentSource && !"".equals(repaymentSource)) {

				String[] repaymentSourceArr = repaymentSource.split("@");
				
				for(int i=0; i<repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource temp = (SlRepaymentSource)JSONMapper.toJava(parser.nextValue(),SlRepaymentSource.class);
					boolean flag = StringUtil.isNumeric(temp.getTypeId());
					GlobalType globalType = globalTypeService.getByNodeKey("repaymentSource").get(0);
					if (flag == false) {
						Dictionary dic = new Dictionary();
						dic.setItemValue(temp.getTypeId());
						dic.setItemName(globalType.getTypeName());
						dic.setProTypeId(globalType.getProTypeId());
						dic.setDicKey(temp.getTypeId());
						dic.setStatus("0");
						dictionaryService.save(dic);
						temp.setTypeId(String.valueOf(dic.getDicId()));
					} else {
						Dictionary cd = dictionaryService.get(Long.valueOf(temp
								.getTypeId()));
						if (null == cd) {
							Dictionary dic = new Dictionary();
							dic.setItemValue(temp.getTypeId());
							dic.setItemName(globalType.getTypeName());
							dic.setProTypeId(globalType.getProTypeId());
							dic.setDicKey(temp.getTypeId());
							dic.setStatus("0");
							dictionaryService.save(dic);
							temp.setTypeId(String.valueOf(dic.getDicId()));
						}
					}
					temp.setProjId(slSmallloanProject.getProjectId());
					if (temp.getSourceId() == null) {
						this.slRepaymentSourceService.save(temp);
					} else {
						SlRepaymentSource rPersistent = this.slRepaymentSourceService.get(temp.getSourceId());
						BeanUtil.copyNotNullProperties(rPersistent, temp);
						this.slRepaymentSourceService.save(rPersistent);
					}
				
					
				}
			}
			//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {

				String[] borrowerInfoArr = borrowerInfo.split("@");
				
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}
			
			//保存费用信息
			String slActualToChargeJsonData=this.getRequest().getParameter("slActualToChargeJsonData");
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}
			// 自有资金款项信息
			if(null!=fundProjectId && !fundProjectId.equals("")){
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
				if(null!=slSmallloanProject.getProjectMoney()){
					ownFund.setOwnJointMoney(slSmallloanProject.getProjectMoney());
				}
				
				if(null!=ownBpFundProject){
					BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
				}
				bpFundProjectService.merge(ownFund);
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							/*if (SlFundIntent1.getFundType().equals("principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf(isCheck));
							//}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							/*if (SlFundIntent1.getFundType().equals(
									"principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf(isCheck));
							//}
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append(","+s);
			sb.append("}");
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	public String getPersonSmallLoanProjectInfo() {
		String projectId = this.getRequest().getParameter("slProjectId"); // 项目ID
		String taskId = this.getRequest().getParameter("slTaskId"); // 任务ID

		//读取项目信息
		SlSmallloanProject project = this.slSmallloanProjectService.get(Long.valueOf(projectId));
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 读取个人客户信息
			Person p = new Person();
			Enterprise enterprise = new Enterprise();
			short sub = 0;
			if (project.getOppositeType().equals("company_customer")) {
				sub = 0;
				Enterprise enterprise1 = enterpriseService.getById(project
						.getOppositeID().intValue());
				if (enterprise1.getLegalpersonid() != null) {
					p = this.personService.getById(enterprise1
							.getLegalpersonid());
					map.put("person", p);
				}
				if (null != enterprise1.getHangyeType()) {
					if (null != areaDicService.getById(enterprise1
							.getHangyeType())) {
						enterprise1.setHangyeName(areaDicService.getById(
								enterprise1.getHangyeType()).getText());
					}
				}
				map.put("enterprise", enterprise1);

			} else if (project.getOppositeType().equals("person_customer")) {
				sub = 1;
				p = this.personService.getById(project.getOppositeID()
						.intValue());
				map.put("person", p);
				if (null != p) {
					if (null != p.getId()) {
						if (null != p.getMarry() && p.getMarry() == 317) {
							Spouse spouse = spouseService.getByPersonId(p
									.getId());
							if(null!=spouse){
								map.put("spouse", spouse);
							}
						}
					}
				}
			}
			if (null != project.getOppositeID()) {
				List<EnterpriseBank> list = enterpriseBankService.getBankList(project.getOppositeID().intValue(), sub, Short.valueOf("0"), Short.valueOf("0"));
				if (null != list && list.size() > 0) {
					EnterpriseBank bank = list.get(0);
					map.put("enterpriseBank", bank);
				}
			}
			
			
			map.put("person", p);
			// 个人旗下公司
			WorkCompany workCompany = workCompanyService
					.getWorkCompanyByPersonId(p.getId());
			map.put("workCompany", workCompany);
			// 借款需求
			if (null != projectId && !"".equals(projectId)) {
				List<BpMoneyBorrowDemand> list = this.bpMoneyBorrowDemandService
						.getMessageByProjectID(Long.valueOf(projectId));
				if (list.size() > 0) {
					bpMoneyBorrowDemand = list.get(0);
					map.put("bpMoneyBorrowDemand", bpMoneyBorrowDemand);
				}
				String managementConsultingMineName = getMainName(project.getManagementConsultingMineType(),project.getManagementConsultingMineId());
				String financeServiceMineName = getMainName(project.getFinanceServiceMineType(),project.getFinanceServiceMineId());
				//
				map.put("financeServiceMineName", financeServiceMineName);
				map.put("managementConsultingMineName", managementConsultingMineName);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("读取信贷流程项目相关信息:" + e.getMessage());
		}

		// 自有资金
		BpFundProject ownFund = bpFundProjectService.getByProjectId(Long
				.valueOf(projectId), Short.valueOf("0"));
		if (null != ownFund) {
			map.put("ownBpFundProject", ownFund);
		}

		String businessTypeKey = creditProjectService.getGlobalTypeValue(
				"businessType", project.getBusinessType(), project
						.getProjectId());
		String operationTypeKey = creditProjectService.getGlobalTypeValue(
				"operationType", project.getBusinessType(), project
						.getProjectId());
		String definitionTypeKey = creditProjectService.getGlobalTypeValue(
				"definitionType", project.getBusinessType(), project
						.getProjectId());
		
		
		map.put("slSmallloanProject", project);
		
		map.put("businessTypeKey", businessTypeKey);
		map.put("operationTypeKey", operationTypeKey);
		map.put("definitionTypeKey", definitionTypeKey);
		if (null != taskId && !"".equals(taskId)) {
			ProcessForm pform = processFormService.getByTaskId(taskId);
			if (pform == null) {
				pform = creditProjectService.getCommentsByTaskId(taskId);
			}
			if (pform != null && pform.getComments() != null
					&& !"".equals(pform.getComments())) {
				map.put("comments", pform.getComments());
			}
		}
		doJson(map);
		return SUCCESS;
	}
	
	//个贷历史补录流程
	public String savePerHistorySmallProjectInfo(){
		try{
			StringBuffer sb = new StringBuffer("{success:true");
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			//保存客户信息
			String s=personService.savePersonInfo(person, spouse, workCompany);
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(person.getId(), Short.valueOf("1"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						enterpriseBankService.merge(e);
					}
					enterpriseBank.setEnterpriseid(person.getId());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("1"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					enterpriseBankService.save(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					enterpriseBankService.merge(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			
			// 更新借款需求
			if(null!=bpMoneyBorrowDemand){
				bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
				if (bpMoneyBorrowDemand.getBorrowid() == null) {
					this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
				} else {
					BpMoneyBorrowDemand bbd = bpMoneyBorrowDemandService.getByBorrowId(bpMoneyBorrowDemand.getBorrowid());
					BeanUtil.copyNotNullProperties(bbd, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(bbd);
				}
				sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
			}

			//保存第一还款来源
			String repaymentSource=this.getRequest().getParameter("repaymentSource");
			if(null != repaymentSource && !"".equals(repaymentSource)) {

				String[] repaymentSourceArr = repaymentSource.split("@");
				
				for(int i=0; i<repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource temp = (SlRepaymentSource)JSONMapper.toJava(parser.nextValue(),SlRepaymentSource.class);
					boolean flag = StringUtil.isNumeric(temp.getTypeId());
					GlobalType globalType = globalTypeService.getByNodeKey("repaymentSource").get(0);
					if (flag == false) {
						Dictionary dic = new Dictionary();
						dic.setItemValue(temp.getTypeId());
						dic.setItemName(globalType.getTypeName());
						dic.setProTypeId(globalType.getProTypeId());
						dic.setDicKey(temp.getTypeId());
						dic.setStatus("0");
						dictionaryService.save(dic);
						temp.setTypeId(String.valueOf(dic.getDicId()));
					} else {
						Dictionary cd = dictionaryService.get(Long.valueOf(temp
								.getTypeId()));
						if (null == cd) {
							Dictionary dic = new Dictionary();
							dic.setItemValue(temp.getTypeId());
							dic.setItemName(globalType.getTypeName());
							dic.setProTypeId(globalType.getProTypeId());
							dic.setDicKey(temp.getTypeId());
							dic.setStatus("0");
							dictionaryService.save(dic);
							temp.setTypeId(String.valueOf(dic.getDicId()));
						}
					}
					temp.setProjId(slSmallloanProject.getProjectId());
					if (temp.getSourceId() == null) {
						this.slRepaymentSourceService.save(temp);
					} else {
						SlRepaymentSource rPersistent = this.slRepaymentSourceService.get(temp.getSourceId());
						BeanUtil.copyNotNullProperties(rPersistent, temp);
						this.slRepaymentSourceService.save(rPersistent);
					}
				
					
				}
			}
			//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {

				String[] borrowerInfoArr = borrowerInfo.split("@");
				
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}
			
			//保存费用信息
			String slActualToChargeJsonData=this.getRequest().getParameter("slActualToChargeJsonData");
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf("1"), persistent.getCompanyId());
			}
			// 自有资金款项信息
			if(null!=fundProjectId && !fundProjectId.equals("")){
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
				if(null!=slSmallloanProject.getProjectMoney()){
					ownFund.setOwnJointMoney(slSmallloanProject.getProjectMoney());
				}
				
				bpFundProjectService.merge(ownFund);
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							/*if (SlFundIntent1.getFundType().equals("principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							/*if (SlFundIntent1.getFundType().equals(
									"principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append(","+s);
			sb.append("}");
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	//企贷历史补录流程
	public String saveEntHistorySmallProjectInfo(){
		try{
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			StringBuffer sb = new StringBuffer("{success:true");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			
			//保存客户信息
			String gudongInfo=this.getRequest().getParameter("gudongInfo");
			enterpriseService.saveSingleEnterprise(enterprise, person, gudongInfo);
			//保存客户银行信息
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(persistent.getOppositeID().intValue(), Short.valueOf("0"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(persistent.getOppositeID().intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("0"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			//保存第一还款来源
			String repaymentSource=this.getRequest().getParameter("repaymentSource");
			if(null != repaymentSource && !"".equals(repaymentSource)) {

				String[] repaymentSourceArr = repaymentSource.split("@");
				
				for(int i=0; i<repaymentSourceArr.length; i++) {
					String str = repaymentSourceArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					SlRepaymentSource temp = (SlRepaymentSource)JSONMapper.toJava(parser.nextValue(),SlRepaymentSource.class);
					boolean flag = StringUtil.isNumeric(temp.getTypeId());
					GlobalType globalType = globalTypeService.getByNodeKey("repaymentSource").get(0);
					if (flag == false) {
						Dictionary dic = new Dictionary();
						dic.setItemValue(temp.getTypeId());
						dic.setItemName(globalType.getTypeName());
						dic.setProTypeId(globalType.getProTypeId());
						dic.setDicKey(temp.getTypeId());
						dic.setStatus("0");
						dictionaryService.save(dic);
						temp.setTypeId(String.valueOf(dic.getDicId()));
					} else {
						Dictionary cd = dictionaryService.get(Long.valueOf(temp
								.getTypeId()));
						if (null == cd) {
							Dictionary dic = new Dictionary();
							dic.setItemValue(temp.getTypeId());
							dic.setItemName(globalType.getTypeName());
							dic.setProTypeId(globalType.getProTypeId());
							dic.setDicKey(temp.getTypeId());
							dic.setStatus("0");
							dictionaryService.save(dic);
							temp.setTypeId(String.valueOf(dic.getDicId()));
						}
					}
					temp.setProjId(slSmallloanProject.getProjectId());
					if (temp.getSourceId() == null) {
						this.slRepaymentSourceService.save(temp);
					} else {
						SlRepaymentSource rPersistent = this.slRepaymentSourceService.get(temp.getSourceId());
						BeanUtil.copyNotNullProperties(rPersistent, temp);
						this.slRepaymentSourceService.save(rPersistent);
					}
				
					
				}
			}
			//保存共同借款人
			String borrowerInfo=this.getRequest().getParameter("borrowerInfo");
			if(null != borrowerInfo && !"".equals(borrowerInfo)) {

				String[] borrowerInfoArr = borrowerInfo.split("@");
				
				for(int i=0; i<borrowerInfoArr.length; i++) {
					String str = borrowerInfoArr[i];
					JSONParser parser = new JSONParser(new StringReader(str));
					BorrowerInfo bo = (BorrowerInfo)JSONMapper.toJava(parser.nextValue(),BorrowerInfo.class);
					if (bo.getBorrowerInfoId() == null) {
						bo.setProjectId(persistent.getProjectId());
						bo.setBusinessType(persistent.getBusinessType());
						bo.setOperationType(persistent.getOperationType());
						this.borrowerInfoService.save(bo);
					} else {
						BorrowerInfo boPersistent = this.borrowerInfoService.get(bo.getBorrowerInfoId());
						BeanUtils.copyProperties(boPersistent, bo);
						this.borrowerInfoService.merge(boPersistent);
					}
					if (null != bo.getType() && bo.getType() == 0) {
						if (null != bo.getCustomerId()) {
							Enterprise e = this.enterpriseService.getById(bo.getCustomerId());
							e.setArea(bo.getAddress());
							e.setCciaa(bo.getCardNum());
							e.setTelephone(bo.getTelPhone());
							this.enterpriseService.merge(e);
						}
					} else if (null != bo.getType() && bo.getType() == 1) {
						Person p = this.personService.getById(bo.getCustomerId());
						p.setPostaddress(bo.getAddress());
						p.setCardnumber(bo.getCardNum());
						p.setCellphone(bo.getTelPhone());
						this.personService.merge(p);
					}
				}
			}
			
			//保存费用信息
			String slActualToChargeJsonData=this.getRequest().getParameter("slActualToChargeJsonData");
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf("1"), persistent.getCompanyId());
			}
			//保存会议纪要
			if(null!=slConferenceRecord){
				if(slConferenceRecord.getConforenceId()==null){
					slConferenceRecordService.save(slConferenceRecord);
					Long conforenceId=slConferenceRecord.getConforenceId();
					
				}else{
					SlConferenceRecord orgSlConferenceRecord=slConferenceRecordService.get(slConferenceRecord.getConforenceId());
					BeanUtil.copyNotNullProperties(orgSlConferenceRecord, slConferenceRecord);
					slConferenceRecordService.save(orgSlConferenceRecord);
				
				}
				sb.append(",conforenceId:"+slConferenceRecord.getConforenceId());
			}
			// 自有资金款项信息
			if(null!=fundProjectId && !fundProjectId.equals("")){
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
					if(null!=slSmallloanProject.getProjectMoney()){
						ownFund.setPlatFormJointMoney(slSmallloanProject.getProjectMoney());
					}
					bpFundProjectService.merge(ownFund);
				}
				//保存款项信息
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							/*if (SlFundIntent1.getFundType().equals("principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							/*if (SlFundIntent1.getFundType().equals(
									"principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			slSmallloanProjectService.merge(persistent);
			sb.append("}");
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 转让标起息办理流程保存方法
	 */
	public String updateBpFundInfoOr() {
		try {
			StringBuffer sb = new StringBuffer("{success:true");
			String bidPlanId=this.getRequest().getParameter("bidPlanId");
			PlBidPlan plan=plBidPlanService.get(Long.valueOf(bidPlanId));
			BeanUtil.copyNotNullProperties(plan, plBidPlan);
			plBidPlanService.merge(plan);
			BpFundProject fundProject = bpFundProjectService.get(ownBpFundProject.getId());
			
			//投资人的放款收息表
			//bpFundIntentService.saveFundIntent(pFundsDatas, plan, fundProject, Short.valueOf(isCheck));
			
			
			//保存费用信息
			/*if(null!=chargeJson && !chargeJson.equals("")){
				slActualToChargeService.savejson(chargeJson, fundProject.getProjectId(), fundProject.getBusinessType(), Short.valueOf(isCheck), fundProject.getCompanyId());
			}*/

			// 意见说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append("}");
			setJsonString(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("信贷流程-制定资金方案保存（更新）信息出错:" + e.getMessage());
		}
		return SUCCESS;
	}
	
	
	//个人快速审批流程快速保存方法
	public String saveSmallFastProjectInfo(){
		try{
			StringBuffer sb = new StringBuffer("{success:true");
			//保存客户信息
			String str=personService.savePersonInfo(person, spouse, workCompany);
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			
			// 更新借款需求
			/*if(null!=bpMoneyBorrowDemand){
				bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
				if (bpMoneyBorrowDemand.getBorrowid() == null) {
					this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
				} else {
					BpMoneyBorrowDemand bbd = bpMoneyBorrowDemandService.getByBorrowId(bpMoneyBorrowDemand.getBorrowid());
					BeanUtil.copyNotNullProperties(bbd, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(bbd);
				}
				sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
			}*/
			//保存费用信息
			String chargeJson=this.getRequest().getParameter("chargeJson");
			String isCheck=this.getRequest().getParameter("isCheck");
			if(null!=chargeJson && !chargeJson.equals("")){
				slActualToChargeService.savejson(chargeJson, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}
			//保存网审信息
			/*String netCheckData=this.getRequest().getParameter("netCheckData");
			  slSmallloanProjectService.updateNetCheckInfo(netCheckData);
			*/
			// 自有资金款项信息
			/*if(null!=ownBpFundProject){
				BpFundProject ownFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("0"));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
					bpFundProjectService.merge(ownFund);
				}
			}*/
			// 平台资金款项信息
			/*if(null!=platFormBpFundProject){
				BpFundProject platFormFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("1"));
				if (null != platFormFund) {
					BeanUtil.copyNotNullProperties(platFormFund,platFormBpFundProject);
					bpFundProjectService.merge(platFormFund);
				}
			}*/
			//保存款项信息
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			if(null!=fundProjectId && !fundProjectId.equals("")){
				String mineType=this.getRequest().getParameter("ownBpFundProject.mineType");
				String mineId=this.getRequest().getParameter("ownBpFundProject.mineId");
				String reciverType=this.getRequest().getParameter("ownBpFundProject.reciverType");
				String receiverName=this.getRequest().getParameter("ownBpFundProject.receiverName");
				String receiverId=this.getRequest().getParameter("ownBpFundProject.receiverId");
				String receiverP2PAccountNumber=this.getRequest().getParameter("ownBpFundProject.receiverP2PAccountNumber");
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
				ownFund.setOwnJointMoney(slSmallloanProject.getProjectMoney());
				if(null!=ownBpFundProject){
					ownFund.setMineId(ownBpFundProject.getMineId());
					ownFund.setMineType(ownBpFundProject.getMineType());
					ownFund.setReciverType(reciverType);
					ownFund.setReceiverName(receiverName);
					ownFund.setReceiverP2PAccountNumber(receiverP2PAccountNumber);
					if(receiverId!=null&&!"".equals(receiverId)){
						ownFund.setReciverId(Long.valueOf(receiverId));
					}
				}
				bpFundProjectService.merge(ownFund);
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							/*if (SlFundIntent1.getFundType().equals("principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							/*if (SlFundIntent1.getFundType().equals(
									"principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append(","+str);
			sb.append("}");
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;

	}
	
	
	//个人贷款标准流程快速保存方法
	public String savePersonCreditSmallProjectInfo(){
		try{
			StringBuffer sb = new StringBuffer("{success:true");
			//保存客户信息
			String str=personService.savePersonInfo(person, spouse, workCompany);
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			
			// 更新借款需求
			if(null!=bpMoneyBorrowDemand){
				bpMoneyBorrowDemand.setProjectID(slSmallloanProject.getProjectId());
				if (bpMoneyBorrowDemand.getBorrowid() == null) {
					this.bpMoneyBorrowDemandService.save(bpMoneyBorrowDemand);
				} else {
					BpMoneyBorrowDemand bbd = bpMoneyBorrowDemandService.getByBorrowId(bpMoneyBorrowDemand.getBorrowid());
					BeanUtil.copyNotNullProperties(bbd, bpMoneyBorrowDemand);
					this.bpMoneyBorrowDemandService.merge(bbd);
				}
				sb.append(",borrowid:"+bpMoneyBorrowDemand.getBorrowid());
			}
			//保存费用信息
			String chargeJson=this.getRequest().getParameter("chargeJson");
			String isCheck=this.getRequest().getParameter("isCheck");
			if(null!=chargeJson && !chargeJson.equals("")){
				slActualToChargeService.savejson(chargeJson, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}
			//保存网审信息
			String netCheckData=this.getRequest().getParameter("netCheckData");
			slSmallloanProjectService.updateNetCheckInfo(netCheckData);
			
			// 自有资金款项信息
			if(null!=ownBpFundProject){
				BpFundProject ownFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("0"));
				if (null != ownFund) {
					BeanUtil.copyNotNullProperties(ownFund, ownBpFundProject);
					bpFundProjectService.merge(ownFund);
				}
			}
			// 平台资金款项信息
			if(null!=platFormBpFundProject){
				BpFundProject platFormFund = bpFundProjectService.getByProjectId(persistent.getProjectId(), Short.valueOf("1"));
				if (null != platFormFund) {
					BeanUtil.copyNotNullProperties(platFormFund,platFormBpFundProject);
					bpFundProjectService.merge(platFormFund);
				}
			}
			
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			if(null!=fundProjectId && !fundProjectId.equals("")){
				String mineType=this.getRequest().getParameter("ownBpFundProject.mineType");
				String mineId=this.getRequest().getParameter("ownBpFundProject.mineId");
				String reciverType=this.getRequest().getParameter("ownBpFundProject.reciverType");
				String receiverName=this.getRequest().getParameter("ownBpFundProject.receiverName");
				String receiverId=this.getRequest().getParameter("ownBpFundProject.receiverId");
				String receiverP2PAccountNumber=this.getRequest().getParameter("ownBpFundProject.receiverP2PAccountNumber");
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
				ownFund.setOwnJointMoney(slSmallloanProject.getProjectMoney());
				if(null!=ownBpFundProject){
					ownFund.setMineId(ownBpFundProject.getMineId());
					ownFund.setMineType(ownBpFundProject.getMineType());
					ownFund.setReciverType(reciverType);
					ownFund.setReceiverName(receiverName);
					ownFund.setReceiverP2PAccountNumber(receiverP2PAccountNumber);
					if(receiverId!=null&&!"".equals(receiverId)){
						ownFund.setReciverId(Long.valueOf(receiverId));
					}
				}
				bpFundProjectService.merge(ownFund);
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			slSmallloanProjectService.merge(persistent);
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			sb.append(","+str);
			sb.append("}");
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/*
	 * 企业快速审批流程的快速保存方法
	 */
	public String saveEnFastProjectInfo(){
		try{
			StringBuffer sb = new StringBuffer("{success:true");
			SlSmallloanProject persistent = this.slSmallloanProjectService.get(slSmallloanProject.getProjectId());
			BeanUtil.copyNotNullProperties(persistent, slSmallloanProject);
			
			//保存客户信息
			/*String gudongInfo=this.getRequest().getParameter("gudongInfo");
			enterpriseService.saveSingleEnterprise(enterprise, person, gudongInfo);*/
			//保存客户银行信息
			if (enterpriseBank != null) {
				if (enterpriseBank.getId() == null) {
					List<EnterpriseBank> list = enterpriseBankService.getBankList(persistent.getOppositeID().intValue(), Short.valueOf("0"), Short.valueOf("0"),Short.valueOf("0"));
					for (EnterpriseBank e : list) {
						e.setIscredit(Short.valueOf("1"));
						creditBaseDao.updateDatas(e);
					}
					enterpriseBank.setEnterpriseid(persistent.getOppositeID().intValue());
					enterpriseBank.setIscredit(Short.valueOf("0"));
					enterpriseBank.setIsEnterprise(Short.valueOf("0"));
					enterpriseBank.setCompanyId(ContextUtil.getLoginCompanyId());
					enterpriseBank.setIsInvest(Short.valueOf("0"));
					creditBaseDao.saveDatas(enterpriseBank);
				} else {
					EnterpriseBank bank = (EnterpriseBank) creditBaseDao.getById(EnterpriseBank.class, enterpriseBank.getId());
					BeanUtil.copyNotNullProperties(bank, enterpriseBank);
					creditBaseDao.updateDatas(bank);
				}
				sb.append(",enterpriseBankId:" + enterpriseBank.getId());
			}
			//保存费用信息
			String slActualToChargeJsonData=this.getRequest().getParameter("slActualToChargeJsonData");
			String isCheck=this.getRequest().getParameter("isCheck");
			if(null!=slActualToChargeJsonData && !slActualToChargeJsonData.equals("")){
				slActualToChargeService.savejson(slActualToChargeJsonData, persistent.getProjectId(), persistent.getBusinessType(), Short.valueOf(isCheck), persistent.getCompanyId());
			}
			
			//保存款项信息
			String fundProjectId=this.getRequest().getParameter("fundProjectId");
			if(null!=fundProjectId && !fundProjectId.equals("")){
				String mineType=this.getRequest().getParameter("ownBpFundProject.mineType");
				String mineId=this.getRequest().getParameter("ownBpFundProject.mineId");
				String reciverType=this.getRequest().getParameter("ownBpFundProject.reciverType");
				String receiverName=this.getRequest().getParameter("ownBpFundProject.receiverName");
				String receiverId=this.getRequest().getParameter("ownBpFundProject.receiverId");
				String receiverP2PAccountNumber=this.getRequest().getParameter("ownBpFundProject.receiverP2PAccountNumber");
				BpFundProject ownFund = bpFundProjectService.get(Long.valueOf(fundProjectId));
				BeanUtil.copyNotNullProperties(ownFund,slSmallloanProject);
				ownFund.setOwnJointMoney(slSmallloanProject.getProjectMoney());
				if(null!=ownBpFundProject){
					ownFund.setMineId(ownBpFundProject.getMineId());
					ownFund.setMineType(ownBpFundProject.getMineType());
					ownFund.setReciverType(reciverType);
					ownFund.setReceiverName(receiverName);
					ownFund.setReceiverP2PAccountNumber(receiverP2PAccountNumber);
					if(receiverId!=null&&!"".equals(receiverId)){
						ownFund.setReciverId(Long.valueOf(receiverId));
					}
				}
				bpFundProjectService.merge(ownFund);
				String fundIntentJsonData=this.getRequest().getParameter("fundIntentJsonData");
				if (null != fundIntentJsonData && !"".equals(fundIntentJsonData)) {
					List<SlFundIntent> oldList = slFundIntentService.getbyPreceptId(ownFund.getId());
					for (SlFundIntent sfi : oldList) {
						if (sfi.getAfterMoney().compareTo(new BigDecimal(0)) == 0) {
							slFundIntentService.remove(sfi);
						}
					}
					String[] shareequityArr = fundIntentJsonData.split("@");
					for (int i = 0; i < shareequityArr.length; i++) {
						String fundIntentstr = shareequityArr[i];
						JSONParser parser = new JSONParser(new StringReader(fundIntentstr));
				
						SlFundIntent SlFundIntent1 = (SlFundIntent) JSONMapper.toJava(parser.nextValue(), SlFundIntent.class);
						SlFundIntent1.setProjectId(persistent.getProjectId());
						SlFundIntent1.setProjectName(persistent.getProjectName());
						SlFundIntent1.setProjectNumber(persistent.getProjectNumber());

						if (null == SlFundIntent1.getFundIntentId()) {

							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setAfterMoney(new BigDecimal(0));
							SlFundIntent1.setAccrualMoney(new BigDecimal(0));
							SlFundIntent1.setFlatMoney(new BigDecimal(0));
							Short isvalid = 0;
							SlFundIntent1.setIsValid(isvalid);
							/*if (SlFundIntent1.getFundType().equals("principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							slFundIntentService.save(SlFundIntent1);
						} else {
							BigDecimal lin = new BigDecimal(0.00);
							if (SlFundIntent1.getIncomeMoney().compareTo(lin) == 0) {
								SlFundIntent1.setNotMoney(SlFundIntent1.getPayMoney());
							} else {
								SlFundIntent1.setNotMoney(SlFundIntent1.getIncomeMoney());
							}
							SlFundIntent1.setBusinessType(persistent.getBusinessType());
							SlFundIntent1.setCompanyId(persistent.getCompanyId());
							/*if (SlFundIntent1.getFundType().equals(
									"principalLending")) {
								SlFundIntent1.setIsCheck(Short.valueOf("0"));
							} else {*/
							SlFundIntent1.setIsCheck(Short.valueOf("1"));
							//}
							SlFundIntent slFundIntent2 = slFundIntentService.get(SlFundIntent1.getFundIntentId());
							BeanUtil.copyNotNullProperties(slFundIntent2,SlFundIntent1);

							slFundIntentService.merge(slFundIntent2);

						}
						
					}
				}
			}
			slSmallloanProjectService.merge(persistent);
			sb.append("}");
			//保存意见与说明
			String taskId = this.getRequest().getParameter("task_id");
			String comments = this.getRequest().getParameter("comments");
			if (null != taskId && !"".equals(taskId) && null != comments
					&& !"".equals(comments.trim())) {
				this.creditProjectService.saveComments(taskId, comments);
			}
			jsonString=sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public Spouse getSpouse() {
		return spouse;
	}

	public void setSpouse(Spouse spouse) {
		this.spouse = spouse;
	}

	// public FinanceInfo getFinanceInfo() {
	// return financeInfo;
	// }
	//
	// public void setFinanceInfo(FinanceInfo financeInfo) {
	// this.financeInfo = financeInfo;
	// }

	public PlBidPlan getPlBidPlan() {
		return plBidPlan;
	}

	public void setPlBidPlan(PlBidPlan plBidPlan) {
		this.plBidPlan = plBidPlan;
	}

	public EnterpriseBank getEnterpriseBank() {
		return enterpriseBank;
	}

	public void setEnterpriseBank(EnterpriseBank enterpriseBank) {
		this.enterpriseBank = enterpriseBank;
	}

	public SlSuperviseRecord getSlSuperviseRecord() {
		return slSuperviseRecord;
	}

	public void setSlSuperviseRecord(SlSuperviseRecord slSuperviseRecord) {
		this.slSuperviseRecord = slSuperviseRecord;
	}

	public SlEarlyRepaymentRecord getSlEarlyRepaymentRecord() {
		return slEarlyRepaymentRecord;
	}

	public SlAlterAccrualRecord getSlAlterAccrualRecord() {
		return slAlterAccrualRecord;
	}

	public void setSlAlterAccrualRecord(
			SlAlterAccrualRecord slAlterAccrualRecord) {
		this.slAlterAccrualRecord = slAlterAccrualRecord;
	}

	public void setSlEarlyRepaymentRecord(
			SlEarlyRepaymentRecord slEarlyRepaymentRecord) {
		this.slEarlyRepaymentRecord = slEarlyRepaymentRecord;
	}

	public long getOperationType() {
		return operationType;
	}

	public void setOperationType(long operationType) {
		this.operationType = operationType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(long departmentId) {
		this.departmentId = departmentId;
	}

	public short getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(short projectStatus) {
		this.projectStatus = projectStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public CustomerBankRelationPerson getCustomerBankRelationPerson() {
		return customerBankRelationPerson;
	}

	public void setCustomerBankRelationPerson(
			CustomerBankRelationPerson customerBankRelationPerson) {
		this.customerBankRelationPerson = customerBankRelationPerson;
	}

	public GLGuaranteeloanProject getgLGuaranteeloanProject() {
		return gLGuaranteeloanProject;
	}

	public void setgLGuaranteeloanProject(
			GLGuaranteeloanProject gLGuaranteeloanProject) {
		this.gLGuaranteeloanProject = gLGuaranteeloanProject;
	}

	public EnterpriseShareequity getEnterpriseShareequity() {
		return enterpriseShareequity;
	}

	public void setEnterpriseShareequity(
			EnterpriseShareequity enterpriseShareequity) {
		this.enterpriseShareequity = enterpriseShareequity;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	private Long projectId;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public SlSmallloanProject getSlSmallloanProject() {
		return slSmallloanProject;
	}

	public void setSlSmallloanProject(SlSmallloanProject slSmallloanProject) {
		this.slSmallloanProject = slSmallloanProject;
	}

	public Long getRunId() {
		return runId;
	}

	public void setRunId(Long runId) {
		this.runId = runId;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Boolean getIsGrantedShowAllProjects() {
		return isGrantedShowAllProjects;
	}

	public void setIsGrantedShowAllProjects(Boolean isGrantedShowAllProjects) {
		this.isGrantedShowAllProjects = isGrantedShowAllProjects;
	}

	public SlConferenceRecord getSlConferenceRecord() {
		return slConferenceRecord;
	}

	public void setSlConferenceRecord(SlConferenceRecord slConferenceRecord) {
		this.slConferenceRecord = slConferenceRecord;
	}

	public ProjectPropertyClassification getProjectPropertyClassification() {
		return projectPropertyClassification;
	}

	public void setProjectPropertyClassification(
			ProjectPropertyClassification projectPropertyClassification) {
		this.projectPropertyClassification = projectPropertyClassification;
	}

	public Long getSuperviseManageId() {
		return superviseManageId;
	}

	public void setSuperviseManageId(Long superviseManageId) {
		this.superviseManageId = superviseManageId;
	}

	public GlobalSupervisemanage getGlobalSupervisemanage() {
		return globalSupervisemanage;
	}

	public void setGlobalSupervisemanage(
			GlobalSupervisemanage globalSupervisemanage) {
		this.globalSupervisemanage = globalSupervisemanage;
	}

	public String getPersonHouseDate() {
		return personHouseDate;
	}

	public void setPersonHouseDate(String personHouseDate) {
		this.personHouseDate = personHouseDate;
	}

	public BpMoneyBorrowDemand getBpMoneyBorrowDemand() {
		return bpMoneyBorrowDemand;
	}

	public void setBpMoneyBorrowDemand(BpMoneyBorrowDemand bpMoneyBorrowDemand) {
		this.bpMoneyBorrowDemand = bpMoneyBorrowDemand;
	}

	public WorkCompany getWorkCompany() {
		return workCompany;
	}

	public void setWorkCompany(WorkCompany workCompany) {
		this.workCompany = workCompany;
	}

	public String getSlActualData() {
		return slActualData;
	}

	public void setSlActualData(String slActualData) {
		this.slActualData = slActualData;
	}

	public BpFundProject getOwnBpFundProject() {
		return ownBpFundProject;
	}

	public void setOwnBpFundProject(BpFundProject ownBpFundProject) {
		this.ownBpFundProject = ownBpFundProject;
	}

	public BpFundProject getPlatFormBpFundProject() {
		return platFormBpFundProject;
	}

	public void setPlatFormBpFundProject(BpFundProject platFormBpFundProject) {
		this.platFormBpFundProject = platFormBpFundProject;
	}

	public String getPersonCarData() {
		return personCarData;
	}

	public void setPersonCarData(String personCarData) {
		this.personCarData = personCarData;
	}

	public PlProjectArchives getPlProjectArchives() {
		return plProjectArchives;
	}

	public void setPlProjectArchives(PlProjectArchives plProjectArchives) {
		this.plProjectArchives = plProjectArchives;
	}

}
