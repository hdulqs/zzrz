// var formulatestore = new HT.JsonStore({
// url : __ctxPath + "/finance/listSlFundIntent.do"
// });

superviseSlFundIntentVM = Ext.extend(Ext.Panel, {
	slSuperviseRecordId : null,
	isUnLoadData : false,
	isThisSuperviseRecord : null,
	// 构造函数
	constructor : function(_cfg) {
		if (typeof(_cfg.projId) != "undefined") {
			this.projectId = _cfg.projId;
		}
		if (typeof(_cfg.businessType) != "undefined") {
			this.businessType = _cfg.businessType;
		}
		if (typeof(_cfg.slSuperviseRecordId) != "undefined") {
			this.slSuperviseRecordId = _cfg.slSuperviseRecordId;
		}
		if (typeof(_cfg.isUnLoadData) != "undefined") {
			this.isUnLoadData = _cfg.isUnLoadData;
		}
		if (typeof(_cfg.isThisSuperviseRecord) != "undefined") {
			this.isThisSuperviseRecord = _cfg.isThisSuperviseRecord;
		}
		if (typeof(_cfg.isHidddenautocreate) != "undefined") {
			this.isHidddenautocreate = _cfg.isHidddenautocreate;
		}
		if (typeof(_cfg.object) != "undefined") {
			this.object = _cfg.object;
		}
		// this.businessType="Financing";
		Ext.applyIf(this, _cfg);
		// 必须先初始化组件
		this.initUIComponents();
		superviseSlFundIntentVM.superclass.constructor.call(this, {
					layout : 'anchor',
					anchor : '100%',
					items : [{
						xtype : 'panel',
						border : false,
						bodyStyle : 'margin-bottom:5px',
						html : '<br/><B><font class="x-myZW-fieldset-title">【'
								+ this.titleText + '】:</font></B><font class="x-myZW-fieldset-title">（</font>颜色预警：<font color=red>逾期款项</font>&nbsp;&nbsp<font style="line-height:20px">未结清项</font>&nbsp;&nbsp<font color=gray>已结清项</font><font class="x-myZW-fieldset-title" >）：</font>'
					}, this.gridPanel]
				});

	},// end of the constructor
	// 初始化组件

	initUIComponents : function() {
		var summary = new Ext.ux.grid.GridSummary();
		function totalMoney(v, params, data) {
			return '总计(元)';
		}
		this.ttbar = new Ext.Toolbar({
					items : [{
								text : '增加',
								iconCls : 'btn-add',
								scope : this,
								hidden:this.isHiddenAdd,
								handler : this.createRs
							},  new Ext.Toolbar.Separator({
								hidden : this.isHiddenAdd
							}), {
								iconCls : 'btn-del',
								scope : this,
								text : '删除',
								hidden:this.isHiddenDel,
								handler : this.removeSelRs
							},  new Ext.Toolbar.Separator({
								hidden : this.isHiddenDel
							}), {
								iconCls : 'btn-info-add',
								scope : this,
								text : '生成',
								hidden:this.isHiddenAuto,
								handler : this.autocreate
							}, new Ext.Toolbar.Separator({
								//hidden : this.isHiddenDelBtn
							}), {
								iconCls : 'btn-detail',
								text : '查看单项流水记录',
								xtype : 'button',
								scope : this,
								hidden : this.isHiddenseeqlideBtn,
								handler : function() {
									var selRs = this.gridPanel.getSelectionModel()
											.getSelections();
									if (selRs <= 0) {
										Ext.ux.Toast.msg('操作信息', '请选中任何一条记录');
										return;
									} else if (selRs.length > 1) {
										Ext.ux.Toast.msg('操作信息', '只能选中一条记录');
										return;
									}
									this.fundIntentWaterReconciliationInfo.call(this, selRs[0],
											1);
								}
						}, new Ext.Toolbar.Separator({
								hidden : this.isHiddenseeqlideBtn
						}),{
							iconCls : 'btn-details',
							text : '项目总流水记录',
							xtype : 'button',
							hidden : this.isHiddenseesumqlideBtn,
							scope : this,
							handler : function() {
								this.projectWaterReconciliationInfo(2);
							}
						}, new Ext.Toolbar.Separator({
								hidden : this.isHiddenseesumqlideBtn
						}), {
							iconCls : 'btn-details',
							text : '导出Excel',
							xtype : 'button',
							scope : this,
							hidden : this.isHiddenExcelBtn==true?true:false,
							handler : function() {
								this.toExcel();
							}
						}, "->", {
							xtype : 'checkbox',
							boxLabel : '费用相关',
							inputValue : true,
							name : "charge",
							checked : true,
							scope : this,
							handler : function() {
								var charge = this.ttbar.getCmpByName("charge");
								var intent = this.ttbar.getCmpByName("intent");
								if (charge.getValue() == true && intent.getValue() == true) {
									this.related("all");
								}
								if (charge.getValue() == false && intent.getValue() == true) {
									this.related("intent");
								}
								if (charge.getValue() == true && intent.getValue() == false) {
									this.related("charge");
								}
								if (charge.getValue() == false
										&& intent.getValue() == false) {
									this.related("none");
								}
							}
						}, '-', {
							xtype : 'checkbox',
							name : "intent",
							boxLabel : '本金相关',
							inputValue : true,
							scope : this,
							checked : true,
							handler : function() {
								var charge = this.ttbar.getCmpByName("charge");
								var intent = this.ttbar.getCmpByName("intent");
								if (charge.getValue() == true && intent.getValue() == true) {
									this.related("all");
								}
								if (charge.getValue() == false && intent.getValue() == true) {
									this.related("intent");
								}
								if (charge.getValue() == true && intent.getValue() == false) {
									this.related("charge");
								}
								if (charge.getValue() == false
										&& intent.getValue() == false) {
									this.related("none");
								}
							}
						}, ' ', ' ', ' ', ' ']
				});
		this.datafield = new Ext.form.DateField({
					format : 'Y-m-d',
					allowBlank : false,
					readOnly : this.isHidden
				})
		var fundTypenodekey = "";
		if (this.businessType == "Financing") {
			fundTypenodekey = "finaning_fund";
		}
		if (this.businessType == "SmallLoan") {
			fundTypenodekey = "financeType";
		}
		this.comboType = new DicIndepCombo({
					editable : false,
					readOnly : this.isHidden,
					lazyInit : false,
					forceSelection : false,
					nodeKey : fundTypenodekey
				})
		this.comboType.store.reload();
		var params1 = {
			projectId : this.projectId,
			flag1 : 1,
			preceptId:this.preceptId,
			businessType : this.businessType
		};
		url = __ctxPath
				+ "/creditFlow/finance/listbySuperviseRecordSlFundIntent.do?slSuperviseRecordId="
				+ this.slSuperviseRecordId + "&isThisSuperviseRecord="
				+ this.isThisSuperviseRecord + "&isUnLoadData="
				+ this.isUnLoadData;
		this.gridPanel = new HT.EditorGridPanel({
			border : false,
			clicksToEdit : 1,
			isShowTbar : this.isHidden1 ? false : true,
			showPaging : false,
			autoHeight : true,
			hiddenCm : this.isHidden1,
			plugins : [summary],
			// 使用RowActions
			// rowActions : true,
			id : 'FundIntent_formulate_editGrid',
			url : url,
			baseParams : params1,
			fields : [{
						name : 'fundIntentId'
					}, {
						name : 'fundType'
					}, {
						name : 'fundTypeName'
					}, {
						name : 'intentDate'
					}, {
						name : 'payMoney'
					}, {
						name : 'incomeMoney'
					}, {
						name : 'remark'
					}, {
						name : 'dayOfEveryPeriod'
					},{
						name : 'factDate'
					},{
						name : 'afterMoney'
					},{
						name : 'notMoney'
					},{
					    name : 'flatMoney'
					},{
						name : 'overdueRate'
					},{
						name : 'accrualMoney'
					},{
						name : 'payintentPeriod'
					},{
						name : 'interestStarTime'
					},{
						name : 'interestEndTime'
					}],
			tbar : this.isHidden1 ? null : this.ttbar,
			columns : [{
				header : '期数',
				dataIndex:'payintentPeriod',
				editor : new Ext.form.ComboBox({
				    triggerAction: 'all',
				    readOnly : this.isHidden1,
				    store: new Ext.data.SimpleStore({
						autoLoad : true,
						url : __ctxPath+ '/project/getPayIntentPeriodSlSmallloanProject.do',
						fields : ['itemId', 'itemName'],
						baseParams : {payintentPeriod:typeof(this.object)!='undefined'?this.object.getCmpByName('slSuperviseRecord.payintentPeriod').getValue():0,projectId:this.projectId,businessType:this.businessType}
					}),
				    valueField: 'itemId',
				    displayField: 'itemName'
		
				}),
				
				renderer : function(value, metaData, record, rowIndex,colIndex, store){
					return "展期第"+value+"期"
					
				}
			},{
				header : '资金类型',
				dataIndex : 'fundType',
				width : 170,
				summaryType : 'count',
				summaryRenderer : totalMoney,
				editor : this.comboType,
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var objcom = this.editor;
					var objStore = objcom.getStore();

					var idx = objStore.find("dicKey", value);

					if (idx != "-1") {

						return objStore.getAt(idx).data.itemValue;
					} else {

						if (record.data.fundTypeName == "") {

							record.data.fundTypeName = value

						} else {
							var x = store.getModifiedRecords();
							if (x != null && x != "") {
								record.data.fundTypeName = value
							}
						}

						return record.get("fundTypeName")
					}

				}

			}, {
				header : '计划收入金额',
				dataIndex : 'incomeMoney',
				align : 'right',
				width : 127,
				summaryType : 'sum',
				editor : {
					xtype : 'numberfield',
					readOnly : this.isHidden,
					listeners : {
						blur : function(v) {
							if (v.getValue() == "") {
								v.setValue("0.00")
							}

						}

					}
				},
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					if (this.isThisSuperviseRecord != null) {
						if ((flag1 == 1)
								|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
						} else {
							if (record.data.isValid == 1) {
								return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元</font>"
							} 
//							else {
//								return '<font style="font-style:italic;text-decoration: line-through">'
//										+ Ext.util.Format.number(value,
//												',000,000,000.00') + "元</font>"
//							}
						}

					}

					if (record.data.isValid == 1) {
						return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
								+ Ext.util.Format.number(value,
										',000,000,000.00') + "元</font>"
					} else {
						if (record.data.notMoney == 0) {
							return '<font style="color:gray">'
									+ Ext.util.Format.number(value,
											',000,000,000.00') + "元"
									+ "</font>";
						}
						if (record.data.isOverdue == "是" && flag1 != 1) {

							return '<font style="color:red">'
									+ Ext.util.Format.number(value,
											',000,000,000.00') + "元"
									+ "</font>";
						}

						if (record.data.afterMoney == 0) {
							return Ext.util.Format.number(value,
									',000,000,000.00')
									+ "元"

						}

						return Ext.util.Format.number(value, ',000,000,000.00')
								+ "元";
					}

				}
			}, {
				header : '计划支出金额',
				dataIndex : 'payMoney',
				align : 'right',
				width : 127,
				summaryType : 'sum',
				editor : {
					xtype : 'numberfield',
					readOnly : this.isHidden,
					listeners : {
						blur : function(v) {
							if (v.getValue() == "") {
								v.setValue("0.00")
							}

						}

					}
				},
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					if (this.isThisSuperviseRecord != null) {
						if ((flag1 == 1)
								|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
						} else {
							if (record.data.isValid == 1) {
								return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元</font>"
							}
//							else {
//								return '<font style="font-style:italic;text-decoration: line-through">'
//										+ Ext.util.Format.number(value,
//												',000,000,000.00') + "元</font>"
//							}
						}

					}
					if (record.data.isValid == 1) {
						return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
								+ Ext.util.Format.number(value,
										',000,000,000.00') + "元</font>"
					} else {
						if (record.data.notMoney == 0) {
							return '<font style="color:gray">'
									+ Ext.util.Format.number(value,
											',000,000,000.00') + "元"
									+ "</font>";
						}
						if (record.data.isOverdue == "是" && flag1 != 1) {

							return '<font style="color:red">'
									+ Ext.util.Format.number(value,
											',000,000,000.00') + "元"
									+ "</font>";
						}

						if (record.data.afterMoney == 0) {
							return Ext.util.Format.number(value,
									',000,000,000.00')
									+ "元";

						}

						return Ext.util.Format.number(value, ',000,000,000.00')
								+ "元";
					}

				}

			}, {
				header : '计划到账日',
				dataIndex : 'intentDate',
				format : 'Y-m-d',
				editor : this.datafield,
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					var v;
					try {
						if (typeof(value) == "string") {
							v = value;
							//return v;
						}
						else{
						v= Ext.util.Format.date(value, 'Y-m-d');
						}
					} catch (err) {
						v = value;
						return v;
					}
					if (this.isThisSuperviseRecord != null) {
						if ((flag1 == 1)
								|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
						} else {
							if (record.data.isValid == 1) {
								return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
										+ v + "</font>";
							} 
//							else {
//								return '<font style="font-style:italic;text-decoration: line-through">'
//										+ v + "</font>";
//							}
						}

					}
					if (record.data.isValid == 1) {
						return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
								+ v + "</font>";
					} else {
						if (record.data.notMoney == 0) {
							return '<font style="color:gray">' + v + "</font>";
						}
						if (record.data.isOverdue == "是" && flag1 != 1) {

							return '<font style="color:red">' + v + "</font>";
						}

						if (record.data.afterMoney == 0) {
							return v;

						}
						return v;

					}

				}
			}, {
				header : '实际到账日',
				dataIndex : 'factDate',
				format : 'Y-m-d',
				// editor :this.datafield1,
				width : 80,
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					var v;
					try {
						if (typeof(value) == "string") {
							v = value;
					//		return v;
						}
						else{
						v= Ext.util.Format.date(value, 'Y-m-d');
						}
					} catch (err) {
						v = value;
						return v;
					}
					if (v != null) {
						if (this.isThisSuperviseRecord != null) {
							if ((flag1 == 1)
									|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
							} else {
								if (record.data.isValid == 1) {
									return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
											+ v + "</font>";
								} 
//								else {
//									return '<font style="font-style:italic;text-decoration: line-through">'
//											+ v + "</font>";
//								}
							}

						}
						if (record.data.isValid == 1) {

							return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
									+ v + "</font>";

						} else {
							if (record.data.notMoney == 0) {
								return '<font style="color:gray">' + v
										+ "</font>";
							}
							if (record.data.isOverdue == "是" && flag1 != 1) {

								return '<font style="color:red">' + v
										+ "</font>";
							}

							if (record.data.afterMoney == 0) {
								return v;

							}

							return v;
						}

					} else {
						return "";
					}

				}
			}, {
				
				header : '计息开始日期',
				dataIndex : 'interestStarTime',
				format : 'Y-m-d',
				 editor :this.datafield,
				width : 80,
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
							if(value!=null){
						v= Ext.util.Format.date(value, 'Y-m-d');
							}else{
								v="";
							}
						return v;
						}
			}, {
				header : '计息结束日期',
				dataIndex : 'interestEndTime',
				format : 'Y-m-d',
				 editor :this.datafield,
				width : 80,
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
							if(value!=null){
						v= Ext.util.Format.date(value, 'Y-m-d');
							}else{
								v="";
							}
						return v;
						}
			}, {
				header : '已对账金额',
				dataIndex : 'afterMoney',
				align : 'right',
				summaryType : 'sum',
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					if (value != null) {

						if (this.isThisSuperviseRecord != null) {
							if ((flag1 == 1)
									|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
							} else {
								if (record.data.isValid == 1) {
									return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
											+ Ext.util.Format.number(value,
													',000,000,000.00')
											+ "元</font>"
								} 
//								else {
//									return '<font style="font-style:italic;text-decoration: line-through">'
//											+ Ext.util.Format.number(value,
//													',000,000,000.00')
//											+ "元</font>"
//								}
							}

						}
						if (record.data.isValid == 1) {
							return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
									+ Ext.util.Format.number(value,
											',000,000,000.00') + "元</font>"
						} else {
							if (record.data.notMoney == 0) {
								return '<font style="color:gray">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元"
										+ "</font>";
							}
							if (record.data.isOverdue == "是" && flag1 != 1) {

								return '<font style="color:red">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元"
										+ "</font>";
							}

							if (record.data.afterMoney == 0) {
								return Ext.util.Format.number(value,
										',000,000,000.00')
										+ "元"

							}

							return Ext.util.Format.number(value,
									',000,000,000.00')
									+ "元"
						}
					} else
						return "";

				}
			}, {
				header : '未对账金额',
				dataIndex : 'notMoney',
				align : 'right',
				summaryType : 'sum',
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					if (value != null) {

						if (this.isThisSuperviseRecord != null) {
							if ((flag1 == 1)
									|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
							} else {
								if (record.data.isValid == 1) {
									return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
											+ Ext.util.Format.number(value,
													',000,000,000.00')
											+ "元</font>"
								}
//								else {
//									return '<font style="font-style:italic;text-decoration: line-through">'
//											+ Ext.util.Format.number(value,
//													',000,000,000.00')
//											+ "元</font>"
//								}
							}

						}
						if (record.data.isValid == 1) {
							return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
									+ Ext.util.Format.number(value,
											',000,000,000.00') + "元</font>"
						} else {
							if (record.data.notMoney == 0) {
								return '<font style="color:gray">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元"
										+ "</font>";
							}
							if (record.data.isOverdue == "是" && flag1 != 1) {

								return '<font style="color:red">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元"
										+ "</font>";
							}

							if (record.data.afterMoney == 0) {
								return Ext.util.Format.number(value,
										',000,000,000.00')
										+ "元"

							}

							return Ext.util.Format.number(value,
									',000,000,000.00')
									+ "元"
						}
					} else
						return "";
				}
			}, {
				header : '已平账金额',
				dataIndex : 'flatMoney',
				align : 'right',
				summaryType : 'sum',
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					if (value != null) {
						if (this.isThisSuperviseRecord != null) {
							if ((flag1 == 1)
									|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
							} else {
								if (record.data.isValid == 1) {
									return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
											+ Ext.util.Format.number(value,
													',000,000,000.00')
											+ "元</font>"
								} 
//								else {
//									return '<font style="font-style:italic;text-decoration: line-through">'
//											+ Ext.util.Format.number(value,
//													',000,000,000.00')
//											+ "元</font>"
//								}
							}

						}
						if (record.data.isValid == 1) {
							return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
									+ Ext.util.Format.number(value,
											',000,000,000.00') + "元</font>"
						} else {
							if (record.data.notMoney == 0) {
								return '<font style="color:gray">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元"
										+ "</font>";
							}
							if (record.data.isOverdue == "是" && flag1 != 1) {

								return '<font style="color:red">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元"
										+ "</font>";
							}

							if (record.data.afterMoney == 0) {
								return Ext.util.Format.number(value,
										',000,000,000.00')
										+ "元"

							}

							return Ext.util.Format.number(value,
									',000,000,000.00')
									+ "元"
						}
					} else
						return "";

				}

			}/*, {
				header : '逾期费率',
				dataIndex : 'overdueRate',
				align : 'center',
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					if (value != null && flag1 != 1) {
						if (this.isThisSuperviseRecord != null) {
							if ((flag1 == 1)
									|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
							} else {
								if (record.data.isValid == 1) {
									return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
											+ Ext.util.Format.number(value,
													',000,000,000.000')
											+ "元</font>"
								} 
							}

						}
						if (record.data.isValid == 1) {
							return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
									+ Ext.util.Format.number(value,
											',000,000,000.000') + "%/日</font>"
						} else {
							if (record.data.notMoney == 0) {
								return '<font style="color:gray">'
										+ Ext.util.Format.number(value,
												',000,000,000.000') + "%/日"
										+ "</font>";
							}
							if (record.data.isOverdue == "是") {

								return '<font style="color:red">'
										+ Ext.util.Format.number(value,
												',000,000,000.000') + "%/日"
										+ "</font>";
							}

							if (record.data.afterMoney == 0) {
								return Ext.util.Format.number(value,
										',000,000,000.000')
										+ "%/日"

							}

							return Ext.util.Format.number(value,
									',000,000,000.000')
									+ "%/日"
						}
					} else
						return "";

				}
			}, {
				header : '逾期违约金总额',
				dataIndex : 'accrualMoney',
				align : 'right',
				summaryType : 'sum',
				width : 100,
				renderer : function(value, metaData, record, rowIndex,
						colIndex, store) {
					var flag1 = 0; // incomeMoney
					if (record.data.payMoney != 0) { // payMoney
						flag1 = 1;
					}
					if (value != null && flag1 != 1) {
						if (this.isThisSuperviseRecord != null) {
							if ((flag1 == 1)
									|| (flag1 == 0 && record.data.incomeMoney == record.data.afterMoney)) {
							} else {
								if (record.data.isValid == 1) {
									return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
											+ Ext.util.Format.number(value,
													',000,000,000.00')
											+ "元</font>"
								} 
							}

						}
						if (record.data.isValid == 1) {
							return '<font style="font-style:italic;text-decoration: line-through;color:gray">'
									+ Ext.util.Format.number(value,
											',000,000,000.00') + "元</font>"
						} else {
							if (record.data.notMoney == 0) {
								return '<font style="color:gray">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元"
										+ "</font>";
							}
							if (record.data.isOverdue == "是") {

								return '<font style="color:red">'
										+ Ext.util.Format.number(value,
												',000,000,000.00') + "元"
										+ "</font>";
							}

							if (record.data.afterMoney == 0) {
								return Ext.util.Format.number(value,
										',000,000,000.00')
										+ "元"

							}

							return Ext.util.Format.number(value,
									',000,000,000.00')
									+ "元"
						}
					} else
						return "";

				}
			}*/, {
				header : '备注',
				dataIndex : 'remark',
				align : 'center',
				editor : new Ext.form.TextField({
							allowBlank : false,
							readOnly : this.isHidden
						})
			}/*
				 * , {
				 * 
				 * header :'', dataIndex : '', scope : this, editor : new
				 * Ext.form.TextField( { id : 'fundIntentJsonData2', name :
				 * 'fundIntentJsonData', allowBlank : true }) }
				 */],
			listeners : {
				afteredit : function(e) {
					if(e.record.data['fundType'] =='principalLending' || e.record.data['fundType'] =='backInterest' ){
						e.record.set('incomeMoney',0);
						e.record.commit();
						
					}else{
						e.record.set('payMoney',0);
						e.record.commit()
					}
				}
			}
				// end of columns
			});

	},
	autocreate : function() {
		var gridPanel1 = this.gridPanel
		var operationType = null;//
		var accrualtype = null;
		var payaccrualType = null;
		var continuationMoney = null;
		var isStartDatePay = null;
		var startDate = null;
		var payintentPerioDate = null;
		var continuationRate = null;
		var financeServiceOfRate = null;
		var managementConsultingOfRate = null;
		var payintentPeriod = null;
		var payintentPerioddays = null;//
		var endDate = null;
		var dayOfEveryPeriod = null;
		var isPreposePayConsultingCheck=null;
		var continuationRateNew=null;
		var isInterestByOneTime=null;
		var dayManagementConsultingOfRate=null;
		var dayFinanceServiceOfRate=null;
		var isPreposePayAccrualsupervise=null;
		var dateMode=null;
		var financeServiceOfRate=null;
		var isActualDay ='no',
		accrualtype = this.object.getCmpByName("slSuperviseRecord.accrualtype").getValue();
		payaccrualType = this.object.getCmpByName("slSuperviseRecord.payaccrualType").getValue();
		continuationMoney = this.object.getCmpByName("slSuperviseRecord.continuationMoney").getValue();
		isStartDatePay = this.object.getCmpByName("slSuperviseRecord.isStartDatePay").getValue();
		startDate = this.object.getCmpByName("slSuperviseRecord.startDate").getValue();
		payintentPerioDate = this.object.getCmpByName("slSuperviseRecord.payintentPerioDate").getValue();
		continuationRate = this.object.getCmpByName("slSuperviseRecord.continuationRate").getValue();
		continuationRateNew = this.object.getCmpByName("slSuperviseRecord.continuationRateNew").getValue();
		managementConsultingOfRate = 0;
		payintentPeriod = this.object.getCmpByName("slSuperviseRecord.payintentPeriod").getValue();
		endDate = this.object.getCmpByName("slSuperviseRecord.endDate").getValue();
		dayOfEveryPeriod = this.object.getCmpByName("slSuperviseRecord.dayOfEveryPeriod").getValue();
		isInterestByOneTime=this.object.getCmpByName("slSuperviseRecord.isInterestByOneTime").getValue();
		isActualDay = this.object.getCmpByName('slSuperviseRecord.isActualDay').getValue();
		dayManagementConsultingOfRate=0;
		dayFinanceServiceOfRate=0;
		financeServiceOfRate=0;
		isPreposePayAccrualsupervise = this.object.getCmpByName("slSuperviseRecord.isPreposePayAccrualsupervise").getValue();
		dateMode=this.object.getCmpByName("slSuperviseRecord.dateMode").getValue();
		var calcutype = this.businessType; // 业务品种
		var projId = this.projectId;
		 
		var message = "";
		if (accrualtype == "请选择" || accrualtype == null || accrualtype == "") {
			message = "还款方式";
		} else if (payaccrualType == "请选择" || payaccrualType == null) {
			message = "还款周期";
		} else if (managementConsultingOfRate === "" || managementConsultingOfRate == null) {
			message = "管理咨询费率";
		}
		if (calcutype == "SmallLoan") {

			if (continuationMoney == "" || continuationMoney == null) {
				message = "展期金额";
			}

			if (startDate == "" || startDate == null) {
				message = "展期开始日期";
			}
			if (payintentPeriod == "" || payintentPeriod == null) {
				message = "展期期限";
			}
			if (continuationRateNew == "" || continuationRateNew == null) {
				message = "展期利率";
			}
			if (accrualtype == "ontTimeAccrual") {

				if (endDate == "" || endDate == null) {
					message = "展期结束日期";
				}

			} else {
				if (isStartDatePay == "1") {

					if (payintentPerioDate == "" || payintentPerioDate == null) {
						message = "固定在几号还款";
					}
				}
				//endDate = startDate;
				if (payaccrualType == "owerPay") {
					if (dayOfEveryPeriod == "" || dayOfEveryPeriod == null) {
						message = "自定义周期";
					}
				}

			}

		}

		if (message != "") {
			Ext.MessageBox.show({
						title : '操作信息',
						msg : message + '不能为空',
						buttons : Ext.MessageBox.OK,
						icon : 'ext-mb-error'
					});
			return null;
		}

		if (calcutype == "SmallLoan" && payaccrualType == "ontTimeAccrual"
				&& endDate <= startDate) {

			message = "展期结束日期必须在展期开始日期之后";
			Ext.MessageBox.show({
						title : '操作信息',
						msg : message,
						buttons : Ext.MessageBox.OK,
						icon : 'ext-mb-error'
					});
			return null;

		}
		var combox=new Ext.form.ComboBox({
		    triggerAction: 'all',
		    store: new Ext.data.SimpleStore({
				autoLoad : true,
				url : __ctxPath+ '/project/getPayIntentPeriodSlSmallloanProject.do',
				fields : ['itemId', 'itemName'],
				baseParams : {payintentPeriod:payintentPeriod}
			}),
		    valueField: 'itemId',
		    displayField: 'itemName'

		})
		gridPanel1.getColumnModel().setEditor(2,combox);
		
		var params2={
			'projectId':projId,
			'slSuperviseRecord.accrualtype':accrualtype,
			'slSuperviseRecord.isPreposePayAccrualsupervise':isPreposePayAccrualsupervise,
			'slSuperviseRecord.payaccrualType':payaccrualType,
			'slSuperviseRecord.continuationMoney':continuationMoney,
			'slSuperviseRecord.startDate':startDate,
			'slSuperviseRecord.isStartDatePay':isStartDatePay,
			'slSuperviseRecord.endDate':endDate,
			'slSuperviseRecord.payintentPerioDate':payintentPerioDate,
			'slSuperviseRecord.continuationRate':continuationRate,
			'slSuperviseRecord.payintentPeriod':payintentPeriod,
			'slSuperviseRecord.isInterestByOneTime':isInterestByOneTime,
			'slSuperviseRecord.managementConsultingOfRate':managementConsultingOfRate,
			'slSuperviseRecord.financeServiceOfRate':financeServiceOfRate,
			'slSuperviseRecord.dateMode':dateMode,
			'calcutype':calcutype,
			'flag1':0,
			'slSuperviseRecord.isActualDay' : isActualDay
		}
		var gridstore = gridPanel1.getStore();
		gridstore.on('beforeload', function(gridstore, o) {
			
			Ext.apply(o.params, params2);
		});
		gridstore.reload()
	},

	createRs : function() {
		var payintentPeriod=this.object.getCmpByName("slSuperviseRecord.payintentPeriod").getValue()
		this.datafield.setValue('');
		var newRecord = this.gridPanel.getStore().recordType;
		var newData = new newRecord({
					fundIntentId : '',
					fundType : '',
					payMoney : 0,
					incomeMoney : 0,
					intentDate : new Date(),
					remark : '',
					payintentPeriod:payintentPeriod
				});
		var combox=new Ext.form.ComboBox({
		    triggerAction: 'all',
		    store: new Ext.data.SimpleStore({
				autoLoad : true,
				url : __ctxPath+ '/project/getPayIntentPeriodSlSmallloanProject.do',
				fields : ['itemId', 'itemName'],
				baseParams : {payintentPeriod:payintentPeriod}
			}),
		    valueField: 'itemId',
		    displayField: 'itemName'

		})
		this.gridPanel.getColumnModel().setEditor(2,combox);
		this.gridPanel.stopEditing();
		this.gridPanel.getStore().insert(this.gridPanel.getStore().getCount(),
				newData);
		this.gridPanel.getView().refresh();
		this.gridPanel.getSelectionModel().selectRow((this.gridPanel.getStore()
				.getCount() - 1));
		this.gridPanel.startEditing(0, 0);

	},

	getGridDate : function() {
		var vRecords = this.gridPanel.getStore().getRange(0,
				this.gridPanel.getStore().getCount()); // 得到修改的数据（记录对象）
		var vCount = vRecords.length; // 得到记录长度
		var vDatas = '';
		if (vCount > 0) {
			var st = "";
			for (var i = 0; i < vCount; i++) {
				if (vRecords[i].data.fundType != null
						&& vRecords[i].data.fundType != "") {
					
					if(null==vRecords[i].data.afterMoney || vRecords[i].data.afterMoney==0){
						st = {
							"fundType" : vRecords[i].data.fundType,
							"incomeMoney" : vRecords[i].data.incomeMoney,
							"payMoney" : vRecords[i].data.payMoney,
							"intentDate" : vRecords[i].data.intentDate,
							"remark" : vRecords[i].data.remark,
							"payintentPeriod":vRecords[i].data.payintentPeriod
							,"interestStarTime":vRecords[i].data.interestStarTime,
							"interestEndTime":vRecords[i].data.interestEndTime,
							"preceptId":this.preceptId,
							"slSuperviseRecordId":this.slSuperviseRecordId
						};
						vDatas += Ext.util.JSON.encode(st) + '@';
					}
					
				}
			}
			vDatas = vDatas.substr(0, vDatas.length - 1);
		}
	
		return vDatas;
	},
	saveRs : function() {
		var data = this.getGridDate();
		Ext.Ajax.request({
					url : __ctxPath
							+ '/creditFlow/finance/savejsonSlFundIntent.do',
					method : 'POST',
					scope : this,
					params : {
						slFundIentJson : data,
						projectId : this.projectId,
						businessType : this.businessType
					},
					success : function(response, request) {
						this.gridPanel.getStore().setBaseParam('flag1', 1);
						this.gridPanel.getStore().reload();

					}
				});

	},
	save : function() {
		this.gridPanel.getStore().setBaseParam('flag1', 1);
		this.gridPanel.getStore().reload();

	},
	removeSelRs : function() {
		var fundIntentGridPanel = this.gridPanel;
		var projId = this.projectId
		var deleteFun = function(url, prame, sucessFun, i, j) {
			Ext.Ajax.request({
				url : url,
				method : 'POST',
				success : function(response) {
					if (response.responseText.trim() == '{success:true}') {
						if (i == (j - 1)) {
							Ext.ux.Toast.msg('操作信息', '删除成功!');
						}
						sucessFun();
					} else if (response.responseText.trim() == '{success:false}') {
						Ext.ux.Toast.msg('操作信息', '删除失败!');
					}
				},
				params : prame
			});
		};
		var a = fundIntentGridPanel.getSelectionModel().getSelections();
		if (a <= 0) {
			Ext.ux.Toast.msg('操作信息', '请选择要删除的记录');
			return false;
		};
		Ext.Msg.confirm("提示!", '确定要删除吗？', function(btn) {

			if (btn == "yes") {
				// grid_sharteequity.stopEditing();
				var s = fundIntentGridPanel.getSelectionModel().getSelections();
				for (var i = 0; i < s.length; i++) {
					var row = s[i];
					if (row.data.fundIntentId == null
							|| row.data.fundIntentId == '') {
						fundIntentGridPanel.getStore().remove(row);
					} else {
						deleteFun(
								__ctxPath
										+ '/creditFlow/finance/deleteSlFundIntent.do',
								{
									fundIntentId : row.data.fundIntentId,
									projectId : projId,
									businessType : this.businessType
								}, function() {
									fundIntentGridPanel.getStore().remove(row);
									fundIntentGridPanel.getStore().reload();
								}, i, s.length);

					}

				}

			}

		});

	},
	projectWaterReconciliationInfo : function(flag) {
		new detailView({
			projectId : this.projectId,
			flag : flag,// 1.款项流水2.项目流水
			hidden1 : false,
			hidden2 : true,
			businessType : this.businessType
		}).show();
	},
	toExcel:function(){
		var projectId = this.projectId;
		var businessType=this.businessType;
		window.open( __ctxPath + "/creditFlow/finance/downloadSlFundIntent.do?projectId="+projectId+"&businessType="+businessType,'_blank');
		
	},
	related : function(flag) {
		var gridstore = this.gridPanel.getStore();
		var o = gridstore.params;
		var projectId = this.projectId;
		var businessType = this.businessType
		gridstore.on('beforeload', function(gridstore, o) {
			var new_params = {
				projectId : projectId,
				businessType : businessType,
				relateIntentOrCharge : flag
			};
			Ext.apply(o.params, new_params);
		});
		this.gridPanel.getStore().reload();
	},
	fundIntentWaterReconciliationInfo : function(record, flag) {
		var hidden = false;
		if (record.data.fundType == "principalLending") {
			hidden = true;
		}
		if(null==record.get("fundIntentId")){
			Ext.ux.Toast.msg('操作信息', '请先保存该笔款项，再查看该笔款项的流水记录!');
			return;
		}else{
			new detailView({
				fundIntentId : record.get("fundIntentId"),
				flag : flag,// 1.款项流水2.项目流水,
				fundType : record.data.fundType,
				hidden1 : hidden,
				hidden2 : true
			}).show();
		}
	}

});