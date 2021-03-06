/**
 * 当前业务信息
 * @extends Ext.Panel
 */
CurrentBusiness = Ext.extend(Ext.Panel, {
		layout : 'anchor',
		anchor : '100%',
		constructor : function(_cfg) {
			Ext.applyIf(this, _cfg);
			this.initUIComponents();
			CurrentBusiness.superclass.constructor.call(this,{
						items : [{
							xtype:'panel',
							border:false,
							bodyStyle:'margin-bottom:5px'
						},
						this.gridPanel 
						]
					});
		},
		initUIComponents : function() {
			this.toolBar = new Ext.Toolbar({
				items : [
					{
						iconCls : 'btn-readdocument',
						text : '增加',
						xtype : 'button',			
						scope : this,
						buttonAlign:'right'
						//handler:this.
					}]
				});
	var url="";
	this.gridPanel = new HT.GridPanel(
			{
				border : false,
				region:'center',
				showPaging:false,
				//tbar : this.toolBar,
				autoHeight : true,
				clicksToEdit :1,
				stripeRows : true,
				viewConfig : {
					forceFit : true
				},
				store : new Ext.data.Store({
					proxy : new Ext.data.HttpProxy({
						url : url,
						method : "POST"
					}),
					reader : new Ext.data.JsonReader({
						fields : Ext.data.Record.create([
						]),
						root : 'result'
				})
			}),
				columns : [
					{
						header : '贷款产品',
						dataIndex : '',						
						sortable : true
					},
					{
						header : '合作机构',
						dataIndex : '',
						sortable : true
					},
					{
						header : '机构客户经理',
						dataIndex : '',
						sortable : true
					},{
						header : '借款金额',
						dataIndex : '',						
						sortable : true
					},{
						header : '办理状态',
						dataIndex : '',
						sortable : true
					},{
						header : '进件日期',
						dataIndex : '',
						sortable : true
					},{
						header : '审批日期',
						dataIndex : '',
						sortable : true
					},{
						header : '放款日期',
						dataIndex : '',
						sortable : true
					},{
						header : '放款金额',
						dataIndex : '',
						sortable : true
					},{
						header : '备注',
						dataIndex : '',
						sortable : true
					}]

              });
     }
});
