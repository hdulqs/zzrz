/**
 * @author
 * @class BpMortgageCarPlaqueView
 * @extends Ext.Panel
 * @description [BpMortgageCarPlaque]管理
 * @company 智维软件
 * @createtime:
 */
BpMortgageCarPlaqueView = Ext.extend(Ext.Panel, {
			// 构造函数
			constructor : function(_cfg) {
				Ext.applyIf(this, _cfg);
				// 初始化组件
				this.initUIComponents();
				// 调用父类构造
				BpMortgageCarPlaqueView.superclass.constructor.call(this, {
							id : 'BpMortgageCarPlaqueView',
							title : '[BpMortgageCarPlaque]管理',
							region : 'center',
							layout : 'border',
							items : [this.searchPanel, this.gridPanel]
						});
			},// end of constructor
			// 初始化组件
			initUIComponents : function() {
				// 初始化搜索条件Panel
				this.searchPanel=new HT.SearchPanel({
							layout : 'form',
							region : 'north',
							colNums:3,
							items:[
																					 																																					 								{
									fieldLabel:'steeringWheel',
									name : 'Q_steeringWheel_S_EQ',
									flex:1,
																		xtype : 'textfield'
																	}
																,
															 							 																																					 								{
									fieldLabel:'leftFrontDoorPlate',
									name : 'Q_leftFrontDoorPlate_S_EQ',
									flex:1,
																		xtype : 'textfield'
																	}
																,
															 							 																																					 								{
									fieldLabel:'driverSeat',
									name : 'Q_driverSeat_S_EQ',
									flex:1,
																		xtype : 'textfield'
																	}
																,
															 							 																																					 								{
									fieldLabel:'viceDriverSeat',
									name : 'Q_viceDriverSeat_S_EQ',
									flex:1,
																		xtype : 'textfield'
																	}
																,
															 							 																																					 								{
									fieldLabel:'rightFrontPlaque',
									name : 'Q_rightFrontPlaque_S_EQ',
									flex:1,
																		xtype : 'textfield'
																	}
																,
															 							 																																					 								{
									fieldLabel:'leftFrontPlaque',
									name : 'Q_leftFrontPlaque_S_EQ',
									flex:1,
																		xtype : 'textfield'
																	}
																,
															 							 																																					 								{
									fieldLabel:'afterChair',
									name : 'Q_afterChair_S_EQ',
									flex:1,
																		xtype : 'textfield'
																	}
																,
															 							 																																					 								{
									fieldLabel:'rightAfterPlaque',
									name : 'Q_rightAfterPlaque_S_EQ',
									flex:1,
																		xtype : 'textfield'
																	}
																,
															 							 																													 								 								{
									fieldLabel:'mortgageid',
									name : 'Q_mortgageid_N_EQ',
									flex:1,
																		xtype:'numberfield'
																	}
															 							 							 															],
								buttons:[
									{
										text:'查询',
										scope:this,
										iconCls:'btn-search',
										handler:this.search
									},{
										text:'重置',
										scope:this,
										iconCls:'btn-reset',
										handler:this.reset
									}							
								]	
				});// end of searchPanel

				this.topbar = new Ext.Toolbar({
						items : [{
									iconCls : 'btn-add',
									text : '添加[BpMortgageCarPlaque]',
									xtype : 'button',
									scope : this,
									handler : this.createRs
								}, {
									iconCls : 'btn-del',
									text : '删除[BpMortgageCarPlaque]',
									xtype : 'button',
									scope:this,
									handler : this.removeSelRs
								}]
				});
	
				this.gridPanel=new HT.GridPanel({
					region:'center',
					tbar:this.topbar,
					//使用RowActions
					rowActions:true,
					id:'BpMortgageCarPlaqueGrid',
					url : __ctxPath + "/creditFlow.mortgage.vehicle/listBpMortgageCarPlaque.do",
					fields : [{
									name : 'id',
									type : 'int'
								}
																																																	,'steeringWheel'
																																										,'leftFrontDoorPlate'
																																										,'driverSeat'
																																										,'viceDriverSeat'
																																										,'rightFrontPlaque'
																																										,'leftFrontPlaque'
																																										,'afterChair'
																																										,'rightAfterPlaque'
																																										,'mortgageid'
																																			],
					columns:[
								{
									header : 'id',
									dataIndex : 'id',
									hidden : true
								}
																																																								,{
																	header : 'steeringWheel',	
																	dataIndex : 'steeringWheel'
								}
																																																,{
																	header : 'leftFrontDoorPlate',	
																	dataIndex : 'leftFrontDoorPlate'
								}
																																																,{
																	header : 'driverSeat',	
																	dataIndex : 'driverSeat'
								}
																																																,{
																	header : 'viceDriverSeat',	
																	dataIndex : 'viceDriverSeat'
								}
																																																,{
																	header : 'rightFrontPlaque',	
																	dataIndex : 'rightFrontPlaque'
								}
																																																,{
																	header : 'leftFrontPlaque',	
																	dataIndex : 'leftFrontPlaque'
								}
																																																,{
																	header : 'afterChair',	
																	dataIndex : 'afterChair'
								}
																																																,{
																	header : 'rightAfterPlaque',	
																	dataIndex : 'rightAfterPlaque'
								}
																																																,{
																	header : 'mortgageid',	
																	dataIndex : 'mortgageid'
								}
																																								, new Ext.ux.grid.RowActions({
									header:'管理',
									width:100,
									actions:[{
											 iconCls:'btn-del',qtip:'删除',style:'margin:0 3px 0 3px'
										},{
											 iconCls:'btn-edit',qtip:'编辑',style:'margin:0 3px 0 3px'
										}
									],
									listeners:{
										scope:this,
										'action':this.onRowAction
									}
								})
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
					new BpMortgageCarPlaqueForm({id:rec.data.id}).show();
				});
			},
			//创建记录
			createRs : function() {
				new BpMortgageCarPlaqueForm().show();
			},
			//按ID删除记录
			removeRs : function(id) {
				$postDel({
					url:__ctxPath+ '/creditFlow.mortgage.vehicle/multiDelBpMortgageCarPlaque.do',
					ids:id,
					grid:this.gridPanel
				});
			},
			//把选中ID删除
			removeSelRs : function() {
				$delGridRs({
					url:__ctxPath + '/creditFlow.mortgage.vehicle/multiDelBpMortgageCarPlaque.do',
					grid:this.gridPanel,
					idName:'id'
				});
			},
			//编辑Rs
			editRs : function(record) {
				new BpMortgageCarPlaqueForm({
					id : record.data.id
				}).show();
			},
			//行的Action
			onRowAction : function(grid, record, action, row, col) {
				switch (action) {
					case 'btn-del' :
						this.removeRs.call(this,record.data.id);
						break;
					case 'btn-edit' :
						this.editRs.call(this,record);
						break;
					default :
						break;
				}
			}
});
