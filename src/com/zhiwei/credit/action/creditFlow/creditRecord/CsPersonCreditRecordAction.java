package com.zhiwei.credit.action.creditFlow.creditRecord;
/*
 *  北京互融时代软件有限公司   -- http://www.zhiweitime.com
 *	Copyright @ 2004 - 2010 Yuseen.com all rights reserved.京ICP备 05007290 号
*/
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;

import java.lang.reflect.Type;

import com.bfd.facade.MerchantServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thirdPayInterface.baiRongJinFu.BaiRongUtil;
import com.zhiwei.core.util.BeanUtil;

import com.zhiwei.core.command.QueryFilter;
import com.zhiwei.core.web.action.BaseAction;


import com.zhiwei.credit.model.creditFlow.creditRecord.CreditRecord;
import com.zhiwei.credit.model.creditFlow.creditRecord.CsPersonCreditRecord;
import com.zhiwei.credit.model.creditFlow.customer.person.Person;
import com.zhiwei.credit.service.creditFlow.creditRecord.CreditRecordService;
import com.zhiwei.credit.service.creditFlow.creditRecord.CsPersonCreditRecordService;
import com.zhiwei.credit.service.creditFlow.customer.person.PersonService;
/**
 * 
 * @author 
 *
 */
public class CsPersonCreditRecordAction extends BaseAction{
	@Resource
	private CsPersonCreditRecordService csPersonCreditRecordService;
	@Resource
	private PersonService personService;
	@Resource
	private CreditRecordService creditRecordService;
	private CsPersonCreditRecord csPersonCreditRecord;
	
	private Long id;
	private Long projectId;
	private String type;
	private Integer personId;
	
	private static final Object lock = new Object();
	private static String status;
	private static String tokenid;
	private static MerchantServer ms;
	static {
		ms = new MerchantServer();
	}
	
	
	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CsPersonCreditRecord getCsPersonCreditRecord() {
		return csPersonCreditRecord;
	}

	public void setCsPersonCreditRecord(CsPersonCreditRecord csPersonCreditRecord) {
		this.csPersonCreditRecord = csPersonCreditRecord;
	}

	/**
	 * 显示列表
	 */
	public String list(){
		
		QueryFilter filter=new QueryFilter(getRequest());
		List<CsPersonCreditRecord> list= csPersonCreditRecordService.getAll(filter);
		
		Type type=new TypeToken<List<CsPersonCreditRecord>>(){}.getType();
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
		.append(filter.getPagingBean().getTotalItems()).append(",result:");
		
		Gson gson=new Gson();
		buff.append(gson.toJson(list, type));
		buff.append("}");
		
		jsonString=buff.toString();
		
		return SUCCESS;
	}
	/**
	 * 显示百融金服第三方返回参数列表
	 * @throws JSONException 
	 */
	public String list1() throws JSONException{
		
		String id=this.getRequest().getParameter("id");
		String projectId=this.getRequest().getParameter("projectId");
		String type1=this.getRequest().getParameter("type");
		QueryFilter filter=new QueryFilter(getRequest());
		List<CreditRecord> list=null;
		if(getRequest().getParameter("Q_nameKey_S_EQ")!=null && !getRequest().getParameter("Q_nameKey_S_EQ").equals("")){
			list=creditRecordService.getAll(filter);
		}else{
			list=creditRecordService.getAll();
		}
		List<CreditRecord>  list1=new ArrayList<CreditRecord>();
		
		CsPersonCreditRecord csPersonCreditRecord=null;
		if(id!=null&& id.equals("") && !id.equals("undefined")){
			csPersonCreditRecord=csPersonCreditRecordService.get(Long.valueOf(id));
		}else{
			csPersonCreditRecord=csPersonCreditRecordService.getByProjectIdType(Long.valueOf(projectId), type1).get(0);
		}
		if(type.equals("ApplyLoanMon") || type.equals("KeyAttribution")){//多次申请审核验证     身份证号手机号归属地
			
			org.codehaus.jettison.json.JSONObject jsonObj = new org.codehaus.jettison.json.JSONObject(csPersonCreditRecord.getNameValue());
			Iterator<String> nameItr = jsonObj.keys();
			Map<String, String> outMap = new HashMap<String, String>();
			String name;
			while (nameItr.hasNext()) {
				name = nameItr.next();
				outMap.put(name, jsonObj.getString(name));
			}
			for (CreditRecord record : list) {
				for (String key : outMap.keySet()) {
					
					if(record.getNameKey().equals(key)){
						record.setNameValue(outMap.get(key));
						list1.add(record);
					}
				}
			}
		}else{//手机三要素验证    身份证二要素验证
			org.codehaus.jettison.json.JSONObject jsonObj = new org.codehaus.jettison.json.JSONObject(csPersonCreditRecord.getNameValue());
			Iterator<String> nameItr = jsonObj.keys();
			Map<String, String> outMap = new HashMap<String, String>();
			String name1;
			while (nameItr.hasNext()) {
				name1 = nameItr.next();
				if(name1.equals("flag") || name1.equals("product")){//单独调用：海纳接口   返回的json需要再次取key和value
					org.codehaus.jettison.json.JSONObject jsonObj1 = new org.codehaus.jettison.json.JSONObject(jsonObj.getString(name1));
					Iterator<String> itr = jsonObj1.keys();
					while(itr.hasNext()){
						String name2=itr.next();
						outMap.put(name2, jsonObj1.getString(name2));
					}
				}else{
					outMap.put(name1, jsonObj.getString(name1));
				}
			}
			for (CreditRecord record : list) {
				for (String key : outMap.keySet()) {
					
					if(record.getNameKey().equals(key)){
						record.setNameValue(outMap.get(key));
						list1.add(record);
					}
				}
			}
		}
		Type type=new TypeToken<List<CreditRecord>>(){}.getType();
		StringBuffer buff = new StringBuffer("{success:true,'totalCounts':")
		//.append(filter.getPagingBean().getTotalItems()).append(",result:");
		.append(list1.size()).append(",result:");
		
		Gson gson=new Gson();
		buff.append(gson.toJson(list1, type));
		//buff.append(gson.toJson(list, type));
		buff.append("}");
		
		jsonString=buff.toString();
		System.out.println("三方返回参数"+jsonString);
		return SUCCESS;
	}
	/**
	 * 批量删除
	 * @return
	 */
	public String multiDel(){
		
		String[]ids=getRequest().getParameterValues("ids");
		if(ids!=null){
			for(String id:ids){
				csPersonCreditRecordService.remove(new Long(id));
			}
		}
		
		jsonString="{success:true}";
		
		return SUCCESS;
	}
	
	/**
	 * 显示详细信息
	 * @return
	 */
	public String get(){
		CsPersonCreditRecord csPersonCreditRecord=csPersonCreditRecordService.get(id);
		
		Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		//将数据转成JSON格式
		StringBuffer sb = new StringBuffer("{success:true,data:");
		sb.append(gson.toJson(csPersonCreditRecord));
		sb.append("}");
		setJsonString(sb.toString());
		
		return SUCCESS;
	}
	/**
	 * 添加及保存操作
	 */
	public String save(){
		if(csPersonCreditRecord.getId()==null){
			csPersonCreditRecordService.save(csPersonCreditRecord);
		}else{
			CsPersonCreditRecord orgCsPersonCreditRecord=csPersonCreditRecordService.get(csPersonCreditRecord.getId());
			try{
				BeanUtil.copyNotNullProperties(orgCsPersonCreditRecord, csPersonCreditRecord);
				csPersonCreditRecordService.save(orgCsPersonCreditRecord);
			}catch(Exception ex){
				logger.error(ex.getMessage());
			}
		}
		setJsonString("{success:true}");
		return SUCCESS;
		
	}
	
	public String getMoreApplyReport(){
		List<CsPersonCreditRecord> recordList=csPersonCreditRecordService.getByProjectIdType(projectId, type);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		//SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(recordList!=null && recordList.size()>0){
			StringBuffer sb=new StringBuffer("{success:true,result:");
			//sb.append(recordList.get(0).getNameValue());
			sb.append(gson.toJson(recordList.get(0)));
			sb.append("}");
			setJsonString(sb.toString());
			System.out.println("查询json==="+sb.toString());
		}else{
			//百融征信报告查询
			Map tongDunProperty=BaiRongUtil.BaiRongProperty();
			String apiCode=(java.lang.String) tongDunProperty.get("apiCode");//接入方编码
			String userName=(java.lang.String) tongDunProperty.get("userName");//用户名
			String password=(java.lang.String) tongDunProperty.get("password");//密码
			String loginName=(java.lang.String) tongDunProperty.get("loginName");//
			String BankServer4ApiUrl=(java.lang.String) tongDunProperty.get("BankServer4ApiUrl");//打包调用类型的url
			String HainaApiUrl=(java.lang.String) tongDunProperty.get("HainaApiUrl");//单独调用类型的url
			Person person=personService.getById(personId);
			if(person!=null){
				//Map<String, String> map = getTokenid("zurzAPI","zurzAPI","LoginApi","3000814");
				Map<String, String> map = getTokenid(apiCode,userName,password,loginName);
				if(map.get("status").equals("1")){
					if(type.equals("ApplyLoanMon")){//多次申请审核验证
						JSONObject jso = new JSONObject();
						JSONObject reqData = new JSONObject();
						//jso.put("apiName", "BankServer4Api");//config配置文件对应的url地址。
						jso.put("apiName", BankServer4ApiUrl);//config配置文件对应的url地址。
						jso.put("tokenid", map.get("tokenid"));//
						
						reqData.put("name", person.getName());//姓名
						reqData.put("cell", person.getCellphone());//手机号
						reqData.put("id", person.getCardnumber());//身份证
						jso.put("reqData", reqData);
						try {
							String result = new CreditRecordAction().query(jso);
							if (result.contains("code")&& JSONObject.fromObject(result).getString("code").equals("100007")) {//100007代表tokenid过期
								result = new CreditRecordAction().query(jso);
							}
							if(JSONObject.fromObject(result).getString("code").equals("00")&&result.contains("swift_number")){
								CsPersonCreditRecord record=new CsPersonCreditRecord();
								record.setProjectId(projectId);
								record.setPersonId(personId);
								record.setType(type);
								record.setReqDate(new Date());
								record.setInterfaceSign("多次申请审核验证");
								record.setNameValue(result);
								csPersonCreditRecordService.merge(record);
								
								StringBuffer sb=new StringBuffer("{success:true,result:");
								//sb.append(result);
								sb.append(gson.toJson(record));
								sb.append("}");
								setJsonString(sb.toString());
							}
							System.out.println("result=="+result);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if(type.equals("TelCheck")){//手机三要素验证
						JSONObject jso2 = new JSONObject();
						JSONObject reqData2 = new JSONObject();
						//jso2.put("apiName", "HainaApi");//config配置文件对应的url地址。
						jso2.put("apiName", HainaApiUrl);//config配置文件对应的url地址。
						jso2.put("tokenid", map.get("tokenid"));//
						
						reqData2.put("meal", "TelCheck");
						reqData2.put("name", person.getName());//姓名
						reqData2.put("cell", person.getCellphone());//手机号
						reqData2.put("id", person.getCardnumber());//身份证
						jso2.put("reqData", reqData2);
						try {
							String result2 = new CreditRecordAction().query(jso2);
							if (result2.contains("code")&& JSONObject.fromObject(result2).getString("code").equals("100007")) {//100007代表tokenid过期
								result2 = new CreditRecordAction().query(jso2);
							}
							if(JSONObject.fromObject(result2).getString("code").equals("600000") && result2.contains("swift_number")){
								CsPersonCreditRecord record=new CsPersonCreditRecord();
								record.setProjectId(projectId);
								record.setPersonId(personId);
								record.setType(type);
								record.setReqDate(new Date());
								record.setNameValue(result2);
								record.setInterfaceSign("手机三要素验证");
								csPersonCreditRecordService.merge(record);
								
								StringBuffer sb=new StringBuffer("{success:true,result:");
								//sb.append(result2);
								sb.append(gson.toJson(record));
								sb.append("}");
								setJsonString(sb.toString());
								System.out.println("result2  json=="+sb.toString());
							}
							System.out.println("result2=="+result2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if(type.equals("IdTwo_z")){//身份证二要素验证
						JSONObject jso3 = new JSONObject();
						JSONObject reqData3 = new JSONObject();
						//jso3.put("apiName", "HainaApi");//config配置文件对应的url地址。
						jso3.put("apiName", HainaApiUrl);//config配置文件对应的url地址。
						jso3.put("tokenid", map.get("tokenid"));//
						
						reqData3.put("meal", "IdTwo_z");
						reqData3.put("name", person.getName());//姓名
						//reqData3.put("cell", person.getCellphone());//手机号
						reqData3.put("id", person.getCardnumber());//身份证
						jso3.put("reqData", reqData3);
						try {
							String result3 = new CreditRecordAction().query(jso3);
							if (result3.contains("code")&& JSONObject.fromObject(result3).getString("code").equals("100007")) {//100007代表tokenid过期
								result3 = new CreditRecordAction().query(jso3);
							}
							if(JSONObject.fromObject(result3).getString("code").equals("600000") && result3.contains("swift_number")){
								CsPersonCreditRecord record=new CsPersonCreditRecord();
								record.setProjectId(projectId);
								record.setPersonId(personId);
								record.setType(type);
								record.setReqDate(new Date());
								record.setInterfaceSign("身份证二要素验证");
								record.setNameValue(result3);
								csPersonCreditRecordService.merge(record);
								
								StringBuffer sb=new StringBuffer("{success:true,result:");
								//sb.append(result3);
								sb.append(gson.toJson(record));
								sb.append("}");
								setJsonString(sb.toString());
								System.out.println("result3== json"+sb.toString());
							}
							System.out.println("result3=="+result3);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if(type.equals("KeyAttribution")){//身份证号手机号归属地
						
						JSONObject jso4 = new JSONObject();
						JSONObject reqData4 = new JSONObject();
						//jso4.put("apiName", "BankServer4Api");//config配置文件对应的url地址。
						jso4.put("apiName", BankServer4ApiUrl);//config配置文件对应的url地址。
						jso4.put("tokenid", map.get("tokenid"));//
						
						reqData4.put("name", person.getName());//姓名
						reqData4.put("cell", person.getCellphone());//手机号
						reqData4.put("id", person.getCardnumber());//身份证
						jso4.put("reqData", reqData4);
						try {
							String result4 = new CreditRecordAction().query(jso4);
							if (result4.contains("code")&& JSONObject.fromObject(result4).getString("code").equals("100007")) {//100007代表tokenid过期
								result4 = new CreditRecordAction().query(jso4);
							}
							if(JSONObject.fromObject(result4).getString("code").equals("00") && result4.contains("swift_number")){
								CsPersonCreditRecord record=new CsPersonCreditRecord();
								record.setProjectId(projectId);
								record.setPersonId(personId);
								record.setType(type);
								record.setReqDate(new Date());
								record.setInterfaceSign("身份证号手机号归属地");
								record.setNameValue(result4);
								csPersonCreditRecordService.merge(record);
								
								StringBuffer sb=new StringBuffer("{success:true,result:");
								//sb.append(result4);
								sb.append(gson.toJson(record));
								sb.append("}");
								setJsonString(sb.toString());
								System.out.println("result4 json=="+sb.toString());
							}
							System.out.println("result4=="+result4);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}else{
					System.out.println("map===="+map.toString());
					setJsonString("{success:false,msg:'百融金服登录失败！'}");
				}
			}else{
				setJsonString("{success:false,msg:'查询客户不存在，请核对信息！'}");
			}
		}
		
		return SUCCESS;
	}
	public String query(JSONObject json) throws Exception {
		String result = "";
		result = ms.getApiData(json.toString(),"3000814");
		if (result.contains("code")&& JSONObject.fromObject(result).getString("code").equals("100007")) {//100007代表tokenid过期
			tokenid = null;
		}
		return result;
	}
	
	public static Map<String,String> getTokenid(String apiCode,String userName,String password,String loginName) {
		Map<String,String> map = new HashMap<String,String>();
		if (StringUtils.isBlank(tokenid)) {
			synchronized (lock) {
				if (StringUtils.isBlank(tokenid)) {
					login(apiCode,userName,password,loginName);
				}
			}
		}
		map.put("tokenid", tokenid);
		map.put("status", status);
		return map;
	}
	public static void login(String userName,String password,String loginName,String apiCode) {
		try {
			String loginResult = ms.login(userName, password,loginName,apiCode);
			if(StringUtils.isNotBlank(loginResult)){
				JSONObject json = JSONObject.fromObject(loginResult);
				if(json.containsKey("tokenid")){
					tokenid = json.getString("tokenid");
					status = "1";
				}else{
					tokenid = loginResult;
					status = "0";
				}
			}else{
				tokenid = loginResult;
				status = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
}