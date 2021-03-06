// 项目信息
extEnterpriseBusinessProjectInfoPanel = Ext.extend(Ext.Panel, {
	layout : "form",
	autoHeight : true,
	labelAlign : 'right',
	isAllReadOnly : false,
	isDiligenceReadOnly : false,
	isMineNameEditable :false,
	constructor : function(_cfg) {
		if (_cfg == null) {
			_cfg = {};
		}
		if (_cfg.isAllReadOnly) {
			this.isAllReadOnly = _cfg.isAllReadOnly;
			this.isDiligenceReadOnly = true;
		}
		if (_cfg.isDiligenceReadOnly) {
			this.isDiligenceReadOnly = _cfg.isDiligenceReadOnly;
		}
		if(_cfg.isMineNameEditable){
			this.isMineNameEditable = _cfg.isMineNameEditable
		}
		Ext.applyIf(this, _cfg);
		var leftlabel=85;
		var centerlabel=87;
		var rightlabel=91;
		extEnterpriseBusinessProjectInfoPanel.superclass.constructor.call(this, {
			items : [{
				layout : "column",
				border : false,
				scope : this,
				defaults : {
					anchor : '100%',
					columnWidth : 1,
					isFormField : true,
					labelWidth : leftlabel
				},
				items : [{
					xtype : 'hidden',
					name : 'gLGuaranteeloanProject.projectId'
				},
				{
					xtype : 'hidden',
					name : 'gLGuaranteeloanProject.mineId'
				},
				{
					xtype : 'hidden',
					name : 'businessType'
				}
				,{
					columnWidth : .595, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : leftlabel,
					items : [{
						fieldLabel : "项目名称",
						xtype : "textfield",
						readOnly : this.isAllReadOnly,
						readOnly : this.isDiligenceReadOnly,
						name : "gLGuaranteeloanProject.projectName",
						blankText : "项目名称不能为空，请正确填写!",
						allowBlank : false,
						anchor : "100%"// 控制文本框的长度

					}]
				}, {
					columnWidth : .405, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : leftlabel,
					items : [{
						fieldLabel : "项目编号",
						xtype : "textfield",
						name : "gLGuaranteeloanProject.projectNumber",
						allowBlank : false,
						readOnly : this.isAllReadOnly,
						readOnly : this.isDiligenceReadOnly,
						blankText : "项目编号不能为空，请正确填写!",
						anchor : "100%"

					}]
				}, {
					columnWidth : .295, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth :leftlabel,
					defaults : {
							anchor : '100%'
						},
					items : [{
						xtype : "textfield",
						anchor : "100%",
						readOnly : this.isAllReadOnly,
						readOnly : this.isDiligenceReadOnly,
						name : "businessTypeKey",
						fieldLabel : "业务类别"
					}]
				}, {
					columnWidth : .3, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : centerlabel,
					items : [{
						fieldLabel : "业务品种",
						xtype : "textfield",
						name:"operationTypeKey",
						readOnly:true,
						anchor : "100%"
					}]
				}, {
					columnWidth : .405, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : leftlabel,
					items : [{
						fieldLabel : "流程类别",
						xtype : "textfield",
						name:"flowTypeKey",
						anchor : '100%',
						readOnly : true
					}]
			},isGroup=="true"?(this.isMineNameEditable?{
					columnWidth : .595, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : leftlabel,
					items : [{
						xtype : "combo",
						anchor : "100%",
						name : "gLGuaranteeloanProject.mineName",
						fieldLabel : "主体单位名称",
						displayField : 'itemName',
						valueField　: 'itemValue',
						store : new Ext.data.SimpleStore({
										autoLoad : true,
										url : __ctxPath + '/system/getControlNameOrganization.do',
										baseParams : {isGroup:this.isGroup},
										fields : ['itemValue', 'itemName']
							}),
						emptyText : "请选择",
						allowBlank : false,
						hiddenName : 'mineId',
						typeAhead : true,
						mode : 'local',
						editable : false,
						selectOnFocus : true,
						triggerAction : 'all',
						blankText :"主体单位名称不能为空",
						listeners : {
							scope : this,
							afterrender : function(combox){
								combox.clearInvalid();
							},
							change : function(combox) {
					    		 var compID=combox.getValue();
					    		 this.getCmpByName("gLGuaranteeloanProject.mineId").setValue(compID);
							}
						}

					}]
			
			}:
			{
					columnWidth : .595, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : leftlabel,
					items : [{
						xtype : "textfield",
						name : "gLGuaranteeloanProject.mineName",
						fieldLabel : "主体单位名称",
						readOnly : this.isAllReadOnly,
						readOnly : this.isDiligenceReadOnly,
						anchor : "100%"
					}]
			
			}):({
					columnWidth : .595, // 该列在整行中所占的百分比
					layout : "column",
					items : [{
						columnWidth : .495, // 该列在整行中所占的百分比
						layout : "form", // 从上往下的布局
						labelWidth : leftlabel,
						items : [{
							fieldLabel : "我方主体类型",
							xtype : "textfield",
							name:"mineTypeKey",
							readOnly : this.isAllReadOnly,
							readOnly : this.isDiligenceReadOnly,
							anchor : "100%"
						}]
					},{
						columnWidth : .505, // 该列在整行中所占的百分比
						layout : "form", // 从上往下的布局
						labelWidth : centerlabel,
						items : [{
							xtype : "textfield",
							name : "gLGuaranteeloanProject.mineName",
							fieldLabel : "我方主体",
							readOnly : this.isAllReadOnly,
							readOnly : this.isDiligenceReadOnly,
							anchor : "100%"
						}]
					}]// 从上往下的布局
			}),
			{
					columnWidth : .405, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : leftlabel,
					items : [{xtype:"hidden",name:"gLGuaranteeloanProject.appUserIdOfA"},{
						fieldLabel : "项目经理",
						xtype : "combo",
						allowBlank : false,
						editable : false,
						triggerClass : 'x-form-search-trigger',
						itemVale : creditkindDicId, // xx代表分类名称
						hiddenName : "appUsersOfA",
						readOnly : this.isAllReadOnly,
					    anchor : "100%",
					    onTriggerClick : function(cc) {
						     var obj = this;
						     var appuerIdObj=obj.previousSibling();
							 new UserDialog({
							 	userIds:appuerIdObj.getValue(),
							 	userName:obj.getValue(),
								single : false,
								title:"项目经理",
								callback : function(uId, uname) {
									obj.setRawValue(uname);
									appuerIdObj.setValue(uId);
								}
							}).show();
						}
					}]
				},isGroup=="true"?({
					columnWidth : .295, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : leftlabel,
					items : [{
					    fieldLabel : "项目来源",
						xtype : "dickeycombo",
						allowBlank : false,
						nodeKey : "gLGuaranteeloan_projectSource",
						hiddenName:'gLGuaranteeloanProject.projectSource',
						readOnly : this.isAllReadOnly,
						emptyText : "请选择",
						value:null,
						anchor : "100%",
					    listeners : {
						   afterrender:function (combox){
						     	  var st=combox.getStore();
						     	  st.on("load",function()
						     	  {
						     	 	   combox.clearInvalid();
						     	 	   if(combox.getValue()>0)
						     	 	   {       
						     	 	          combox.setValue(combox.getValue());
						     	 	      
					                   }
						     	   })
						    }
						}
					}]
			},{
					columnWidth : .3, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : centerlabel,
					items : [{
						fieldLabel : "贷款用途",
						xtype : "dickeycombo",
						allowBlank : false,
						name:'gLGuaranteeloanProject.purposeType',
						nodeKey : 'gLGuaranteeloan_purposeType',
						hiddenName:'gLGuaranteeloanProject.purposeType',
						readOnly : this.isAllReadOnly,
						emptyText : "请选择",
						value:null,
						anchor : "100%",
					    listeners : {
						   afterrender:function (combox){
						     	  var st=combox.getStore();
						     	  st.on("load",function()
						     	  {
						     	 	   combox.clearInvalid();
						     	 	   if(combox.getValue()>0)
						     	 	   {
						     	 	          combox.setValue(combox.getValue());
						     	 	          
					                   }
						     	   })
						    }
						}
					}]
				}):({//ie 兼容性问题，布局要对应
					columnWidth : .595, // 该列在整行中所占的百分比
					layout : "column",
					items : [{
						columnWidth : .495, // 该列在整行中所占的百分比
						layout : "form", // 从上往下的布局
						labelWidth : leftlabel,
						items : [{
						    fieldLabel : "项目来源",
							xtype : "dickeycombo",
							allowBlank : false,
							nodeKey : "gLGuaranteeloan_projectSource",
							hiddenName:'gLGuaranteeloanProject.projectSource',
							readOnly : this.isAllReadOnly,
							emptyText : "请选择",
							value:null,
							anchor : "100%",
						    listeners : {
							   afterrender:function (combox){
							     	  var st=combox.getStore();
							     	  st.on("load",function()
							     	  {
							     	 	   combox.clearInvalid();
							     	 	   if(combox.getValue()>0)
							     	 	   {       
							     	 	          combox.setValue(combox.getValue());
							     	 	      
						                   }
							     	   })
							    }
							}
						}]
					},{
						columnWidth : .505, // 该列在整行中所占的百分比
						layout : "form", // 从上往下的布局
						labelWidth : centerlabel,
						items : [{
							fieldLabel : "贷款用途",
							xtype : "dickeycombo",
							allowBlank : false,
							name:'gLGuaranteeloanProject.purposeType',
							nodeKey : 'gLGuaranteeloan_purposeType',
							hiddenName:'gLGuaranteeloanProject.purposeType',
							readOnly : this.isAllReadOnly,
							emptyText : "请选择",
							value:null,
							anchor : "100%",
						    listeners : {
							   afterrender:function (combox){
							     	  var st=combox.getStore();
							     	  st.on("load",function()
							     	  {
							     	 	   combox.clearInvalid();
							     	 	   if(combox.getValue()>0)
							     	 	   {
							     	 	          combox.setValue(combox.getValue());
							     	 	          
						                   }
							     	   })
							    }
							}
						}]
					}]// 从上往下的布局
			}),{
					columnWidth : .405, // 该列在整行中所占的百分比
					layout : "form", // 从上往下的布局
					labelWidth : leftlabel,
					items : [{
						fieldLabel : "信贷种类",
						xtype : "dickeycombo",
						allowBlank : false,
						name:'gLGuaranteeloanProject.creditType',
						nodeKey : 'gLGuaranteeloan_creditType',
						hiddenName:'gLGuaranteeloanProject.creditType',
						readOnly : this.isAllReadOnly,
						value:null,
						emptyText : "请选择",
						anchor : "100%",
					    listeners : {
						   afterrender:function (combox){
						     	  var st=combox.getStore();
						     	  st.on("load",function()
						     	  {
						     	 	   combox.clearInvalid();
						     	 	   if(combox.getValue()>0)
						     	 	   {
						     	 	          combox.setValue(combox.getValue());
						     	 	          
					                   }
						     	   })
						    }
						}
					}]
				}]
			}]
		});
	}
});