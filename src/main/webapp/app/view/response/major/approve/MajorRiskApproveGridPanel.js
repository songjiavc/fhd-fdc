Ext.define('FHD.view.response.major.approve.MajorRiskApproveGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.majorriskapprovegridpanel',
 	requires: [
	],
	loadData:function(planId){
		var me = this;
		var param = {planId:planId};
		me.store.proxy.url = __ctxPath+"/majorResponse/loadPlanApproveDataByPlanId";
		me.store.sync = true;
		me.store.proxy.extraParams = param;
		me.store.load();
		return me.store;
		
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
			{
				header: "planId",
				dataIndex:'planId',
				hidden:true
			},
			{
				header: "riskId",
				dataIndex:'riskId',
				hidden:true
			},
			{
				header: "deptId",
				dataIndex:'deptId',
				hidden:true
			},
	        {
	            header: "重大风险",
	            dataIndex: 'riskName',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "部门数量",
	            dataIndex: 'deptCount',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "主责部门",
	            dataIndex: 'majorDept',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "辅责部门",
	            dataIndex: 'relaDept',
	            sortable: true,
	            width:40,
	            flex:2
	        }
        ];
        
        Ext.apply(me,{
        	region:'center',
        	cols:cols,
        	tbarItems:['<b>重大风险列表</b>',
        		'-',
        		{
        			text:'查看全部', 
        			iconCls: 'icon-scorecards', 
        			handler:function(){

        			}
    			}],
		    border: true,
		    checked : false,
		    pagable : false
        });
        me.callParent(arguments);
        me.loadData(me.businessId);
    }

});