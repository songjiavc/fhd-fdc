Ext.define('FHD.view.response.workplan.workplanapprove.WorkPlanApproverSubmitGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.workPlanApproverSubmitGridPanel',
    requires: [
	],
    flex:1,
    //加载数据
    reloadData: function(businessId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
	    me.store.proxy.extraParams.planId = businessId;
	    me.store.load();
    },
    //查看选中部门的风险明细
	seeRisksInfoByDeptId: function(seeAll){
    	var me = this;
		var workPlanPreviewGrid = Ext.create('FHD.view.response.responsplanemore.ResponsePlanApprovePreview',{
			height:600,
			width:1100,
			businessId: me.businessId
		});
		
		if(!seeAll){//是否查看全部
			var selection = me.getSelectionModel().getSelection();
			workPlanPreviewGrid.deptId = selection[0].get('id');//部门id
			workPlanPreviewGrid.reloadData(me.businessId,workPlanPreviewGrid.deptId);
		}else{
			workPlanPreviewGrid.reloadData(me.businessId,null);
		}
		
		var win = Ext.create('FHD.ux.Window', {
    		autoScroll:false,
    		title:'风险事件详细信息',
    		maximizable: true,
    		width:900,
    		height:500,
        	items:[workPlanPreviewGrid]
		});
    	win.show();
	},
    // 初始化方法
    initComponent: function() {
        var me=this;
		me.cols=[
        	{header: '部门名称', dataIndex: 'deptName', sortable : false, flex: 1 },
        	{header: '风险数量', dataIndex: 'riskCournts', sortable : false, flex: 1 },
			{dataIndex : 'id', hidden : true}, 
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