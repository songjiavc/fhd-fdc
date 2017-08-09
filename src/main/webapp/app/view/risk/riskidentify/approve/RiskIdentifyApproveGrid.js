Ext.define('FHD.view.risk.riskidentify.approve.RiskIdentifyApproveGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskIdentifyApproveGrid',
    requires: [
	],
    flex:1,
    //加载数据
    loadData: function(id){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';//查询列表
	    me.store.proxy.extraParams.planId = id;
	    me.store.load();
    },
    //查看选中部门的风险明细
	seeRisksInfoByDeptId: function(seeAll){
    	var me = this;
		me.riskIdentifyPreviewRiskGrid = Ext.create('FHD.view.risk.riskidentify.approve.RiskIdentifyPreviewRiskGrid',{
			height:600,
			width:1100,
			businessId: me.businessId
		});
		
		if(!seeAll){//按部门查看
			var selection = me.getSelectionModel().getSelection();
			deptId = selection[0].get('id');
			me.riskIdentifyPreviewRiskGrid.loadData(me.businessId,deptId);
		}else{//查看全部
			me.riskIdentifyPreviewRiskGrid.loadData(me.businessId,'');
		}
		var win = Ext.create('FHD.ux.Window', {
    		autoScroll:false,
    		title:'风险事件详细信息',
    		maximizable: true,
    		width:900,
    		height:500,
        	items:[me.riskIdentifyPreviewRiskGrid]
		});
    	win.show();
	},
    // 初始化方法
    initComponent: function() {
        var me=this;
		me.cols=[
			{dataIndex : 'id', invisible : true}, 
        	{header: '部门名称', dataIndex: 'deptName', sortable : false, flex: 1 },
        	{header: '风险数量', dataIndex: 'riskCournts', sortable : false, flex: 1 },
			{header: '承办人', dataIndex: 'cbrName', sortable : false, flex: 1 },
			{
	            header: "操作",
	            dataIndex: '',
	            sortable: true,
	            width:40,
	            flex:1,
	            renderer:function(){
					return "<a href=\"javascript:void(0);\" >查看明细</a>&nbsp;&nbsp;&nbsp;"	//
				},
				listeners:{
	        		click:function(){
	        			me.seeRisksInfoByDeptId(false);
    				}
        		}
			}
		];
		
		me.tbar = [
        		   {text:'查看全部', iconCls: 'icon-scorecards', handler:function(){
        		   		me.seeRisksInfoByDeptId(true);
        		   }}
                   ];
        
        Ext.apply(me, {
            cols:me.cols,
            type:'editgrid',
            storeAutoLoad: false,
            tbarItems:me.tbar,
		    border: true,
		    //minHeight: 250,
		    columnLines: false,
		    checked: false,
		    pagable : false,
		    scroll : 'vertical',
		    searchable : true
        });
                   
		me.callParent(arguments);
    }

});