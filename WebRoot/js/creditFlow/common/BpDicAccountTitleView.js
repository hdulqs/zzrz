/**
 * @author
 * @class BpDicAccountTitleView
 * @extends Ext.Panel
 * @description [BpDicAccountTitleView]管理
 * @company 互融软件
 * @createtime:
 */
BpDicAccountTitleView = Ext.extend(Ext.Panel, {
			// 构造函数
			constructor : function(_cfg) {
				Ext.applyIf(this, _cfg);
				// 初始化组件
				this.initUIComponents();
				// 调用父类构造
				BpDicAccountTitleView.superclass.constructor.call(this, {
							id : 'BpDicAccountTitleView',
							title : '会计准则科目',
							region : 'center',
							iconCls :'menu-flowManager',
							layout : 'border',
							items : [this.searchPanel, this.gridPanel]
						});
			},// end of constructor
			// 初始化组件
			initUIComponents : function() {
				// 初始化搜索条件Panel
				this.searchPanel=new HT.SearchPanel({
							layout : 'column',
							region : 'north',
							items : [{
								columnWidth : '.25',
								layout : 'form',
								border : false,
								labelAlign : 'right',
								labelWidth : 80,
								items:[{
									xtype : "combo",
									anchor : "100%",
									hiddenName : "Q_titleClass_S_LK",
									displayField : 'itemName',
									valueField : 'itemId',
									triggerAction : 'all',
									editable:false,
									readOnly : this.readOnly,
									disable : true,
									store : new Ext.data.SimpleStore({
											autoLoad : true,
											url :  __ctxPath+ '/creditFlow/customer/enterprise/getClassNameJson1BpDicAccountTitle.do',
												  fields : ['itemId', 'itemName']
											}),
											fieldLabel : "一级类别",
											blankText : "一级不能为空，请正确填写!",
											listeners : {
													afterrender : function(combox) {
														var st = combox.getStore();
														st.on("load", function() {
														    combox.setValue(combox
																	.getValue());
															combox.clearInvalid();
														})
		                                            }
												}
										}]
							},{
								columnWidth : '.25',
								layout : 'form',
								border : false,
								labelAlign : 'right',
								labelWidth : 80,
								items:[{
									fieldLabel:'一级名称',
									name : 'Q_title_S_LK',
									anchor : '98%',
									xtype : 'textfield'
								}]
							},{
								columnWidth : '.07',
								layout : 'form',
								border : false,
								items : [{
										xtype : 'button',
										text:'查询',
										scope:this,
										iconCls:'btn-search',
										handler:this.search
									}]
							},{
								columnWidth : '.07',
								layout : 'form',
								border : false,
								items : [{
									xtype : 'button',
									text:'重置',
									scope:this,
									iconCls:'btn-reset',
									handler:this.reset
								}]
							}]
				});// end of searchPanel

				this.topbar = new Ext.Toolbar({
						items : [{
									iconCls : 'btn-add',
									text : '添加',
									xtype : 'button',
									scope : this,
									handler : this.createRs
								},'-',{
									iconCls : 'btn-detail',
									text : '查看',
									xtype : 'button',
									scope : this,
									handler : this.seeRs
								},'-',{
									iconCls : 'btn-edit',
									text : '编辑',
									xtype : 'button',
									scope : this,
									handler : this.editRs
								},'-', {
									iconCls : 'btn-del',
									text : '删除',
									xtype : 'button',
									scope:this,
									handler : this.removeSelRs
								}]
				});
	
				this.gridPanel=new HT.GridPanel({
					region:'center',
				//	tbar:this.topbar,
					id:'BpDicAccountTitleGrid',
					url : __ctxPath + "/creditFlow/customer/enterprise/listBpDicAccountTitle.do",
					fields : [{
									name : 'id',
									type : 'int'
								}
								,'keyName','titleClass','title'
								,'remark'																																			],
					columns:[
								{
									header : 'id',
									dataIndex : 'id',
									hidden : true
								},{
									header : '一级科目编号',	
									dataIndex : 'keyName'
								},{
									header : '一级科目分类',	
									dataIndex : 'titleClass'
								},{
									header : '一级科目名称',	
									dataIndex : 'title'
								},{
									header : '备注',	
									dataIndex : 'remark'
								}
					]//end of columns
				});
				
				this.gridPanel.addListener('rowdblclick',this.rowClick);
					
			},// end of the initComponents()
			//重置查询表单
			reset : function(){
				this.searchPanel.getForm().reset();
			},
			//按条件搜索
			search : function() {
				$search({
					searchPanel:this.searchPanel,
					gridPanel:this.gridPanel
				});
			},
			//GridPanel行点击处理事件
			rowClick:function(grid,rowindex, e) {
				grid.getSelectionModel().each(function(rec) {
					new CsBankForm({bankid:rec.data.bankid,isAllReadOnly:false}).show();
				});
			},
			//创建记录
			createRs : function() {
				new CsBankForm().show();
			},
			//按ID删除记录
			removeRs : function(id) {
				$postDel({
					url:__ctxPath+ '/creditFlow/common/multiDelCsBank.do',
					ids:id,
					grid:this.gridPanel
				});
			},
			//把选中ID删除
			removeSelRs : function() {
				$delGridRs({
					url:__ctxPath + '/creditFlow/common/multiDelCsBank.do',
					grid:this.gridPanel,
					idName:'bankid'
				});
			},
			//查看Rs
			seeRs : function(record) {
				var s = this.gridPanel.getSelectionModel().getSelections();
				if (s <= 0) {
					Ext.ux.Toast.msg('操作信息','请选中任何一条记录');
					return false;
				}else if(s.length>1){
					Ext.ux.Toast.msg('操作信息','只能选中一条记录');
					return false;
				}else{	
					record=s[0]
					new CsBankForm({
						bankid : record.data.bankid,
						isAllReadOnly:true
					}).show();
				}
			},
			//编辑Rs
			editRs : function(record) {
				var s = this.gridPanel.getSelectionModel().getSelections();
				if (s <= 0) {
					Ext.ux.Toast.msg('操作信息','请选中任何一条记录');
					return false;
				}else if(s.length>1){
					Ext.ux.Toast.msg('操作信息','只能选中一条记录');
					return false;
				}else{	
					record=s[0]
					new CsBankForm({
						bankid : record.data.bankid,
						isAllReadOnly:false
					}).show();
				}
			},
			//行的Action
			onRowAction : function(grid, record, action, row, col) {
				switch (action) {
					case 'btn-del' :
						this.removeRs.call(this,record.data.bankid);
						break;
					case 'btn-edit' :
						this.editRs.call(this,record);
						break;
					default :
						break;
				}
			}
});
