/**
 * @author 
 * @createtime 
 * @class CsBankForm
 * @extends Ext.Window
 * @description CsBank表单
 * @company 智维软件
 */
CsBankForm = Ext.extend(Ext.Window, {
			//构造函数
			constructor : function(_cfg) {
				Ext.applyIf(this, _cfg);
				//必须先初始化组件
				this.initUIComponents();
				CsBankForm.superclass.constructor.call(this, {
							id : 'CsBankFormWin',
							layout : 'fit',
							items : this.formPanel,
							modal : true,
							height : 200,
							width : 500,
							maximizable : true,
							title : '银行详细信息',
							buttonAlign : 'center',
							buttons : [
										{
											text : '保存',
											iconCls : 'btn-save',
											scope : this,
											hidden:this.isAllReadOnly,
											disabled:this.isAllReadOnly,
											handler : this.save
										}, {
											text : '重置',
											iconCls : 'btn-reset',
											scope : this,
											hidden:this.isAllReadOnly,
											disabled:this.isAllReadOnly,
											handler : this.reset
										}, {
											text : '取消',
											iconCls : 'btn-cancel',
											scope : this,
											hidden:this.isAllReadOnly,
											disabled:this.isAllReadOnly,
											handler : this.cancel
										}
							         ]
				});
			},//end of the constructor
			//初始化组件
			initUIComponents : function() {
				this.formPanel = new Ext.FormPanel({
							layout : 'form',
							bodyStyle : 'padding:10px',
							border : false,
							autoScroll:true,
							//id : 'CsBankForm',
							defaults : {
								anchor : '96%,96%'
							},
							labelWidth : 80,
							labelAlign : 'right',
							items : [{
								name : 'csBank.bankid',
								xtype : 'hidden',
								value : this.bankid == null ? '' : this.bankid
							},{
								xtype : 'textfield',
								fieldLabel : '银行名称',	
								allowBlank : false,
								readonly:this.isAllReadOnly,
 								name : 'csBank.bankname',
 								maxLength: 255
 							},{
 								xtype : 'textarea',
								fieldLabel : '备注',	
								readOnly:this.isAllReadOnly,
 								name : 'csBank.remarks',
 								maxLength: 255
 							}]
						});
				//加载表单对应的数据	
				if (this.bankid != null && this.bankid != 'undefined') {
					this.formPanel.loadData({
								url : __ctxPath + '/creditFlow/common/getCsBank.do?bankid='+ this.bankid,
								root : 'data',
								preName : 'csBank'
							});
				}
				
			},//end of the initcomponents

			/**
			 * 重置
			 * @param {} formPanel
			 */
			reset : function() {
				this.formPanel.getForm().reset();
			},
			/**
			 * 取消
			 * @param {} window
			 */
			cancel : function() {
				this.close();
			},
			/**
			 * 保存记录
			 */
			save : function() {
				this.formPanel.getForm().submit({
					url:__ctxPath + '/creditFlow/common/saveCsBank.do',
					method : 'post',
					scope: this,
					waitMsg : '数据正在提交，请稍后...',
					success : function(fp, action) {
						var obj=Ext.util.JSON.decode(action.response.responseText)
						if(obj.msg==false){
							Ext.ux.Toast.msg('操作信息', '不能添加重复的银行名称!');
							return;
						}else{
							var gridPanel = Ext.getCmp('CsBankGrid');
							if (gridPanel != null) {
								gridPanel.getStore().reload();
							}
							this.close();
						}
					}
				})
			}//end of save

		});