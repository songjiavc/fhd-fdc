Ext.define('FHD.view.risk.riskversion.RiskDetailTreeGrid', {
	extend : 'FHD.ux.TreeGridPanel',
	alias : 'widget.RiskDetailTreeGrid',

	//查看风险明细
	showRisk:function(historyId){
    	var me = this;
    	me.up('riskversioncardmain').detailtForm.reloadData(historyId);
    	me.up('riskversioncardmain').showDetailtForm();
    },
    
    getColsShow: function(){
    	var me = this;
    	var cols = [
		    		{
		        		hidden:true,
		        		text: 'historyId',
		        	    dataIndex: 'historyId'
		        	},{
		        		hidden:true,
		        		text: 'riskId',
		        	    dataIndex: 'riskId'
		        	},{
		        		hidden:true,
		        	    dataIndex: 'linked'
		        	},
		        	{
		        	    xtype: 'treecolumn', 
		        	    text: "风险名称",
		        	    flex: 4,
		        	    dataIndex: 'riskName',
		        	    sortable: false,
		        	    renderer:function(value,metaData,record,colIndex,store,view) {
			            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
			            	if(record.get('riskId')){
			            		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
	     						+ "').showRisk('" + record.get('historyId') + "')\">"+value+"</a>";
			            	}else{
			            		return value;
			            	}
		     				
		     			}
		        	},
		        	{text: '责任部门/人',dataIndex: 'mainName',flex: 1,hideable:false,sortable: false},
		        	{text: '相关部门/人',dataIndex: 'relaName',flex: 1,hideable:false,sortable: false},
		        	{text: '风险水平',dataIndex: 'score',flex: 1,hideable:false,sortable: false}
		        ];
		return cols;
    },
    
    //加载数据
    reloadData: function(verId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/risk/riskhistory/findrisksdetailbyverid.f';
 		me.store.proxy.extraParams.verId = verId;//版本分库标识
 		me.store.load();
    },
    
    //返回到版本列表
    backToGrid: function(){
    	var me = this;
    	me.up('riskversioncardmain').showVersionGrid();
    },
	
	// 初始化方法
	initComponent : function() {
		var me = this;
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
	        clicksToEdit: me.clicksToEdit
	    });
		
		var cols = me.getColsShow();
		Ext.apply(me, {
			cols: cols,
			useArrows : true,
			rootVisible : false,
			multiSelect : true,
			border : false,
			rowLines : true,
			singleExpand : false,
			checked : false,
			autoScroll : true,
			plugins: [cellEditing],
			tbarItems:[
				{
	   				text: '全部展开',
	   				iconCls: 'icon-expand-all',
	   				handler:function(){
	   					me.expandAll();
	   				}
				},
				{
	   				text: '返回',
	   				iconCls: 'icon-control-repeat-blue',
	   				handler:function(){
	   					me.backToGrid();
	   				}
				}
			]
		});
	
		me.callParent(arguments);
	}
});