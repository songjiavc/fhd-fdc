Ext.define('FHD.view.response.major.scheme.approve.ApproveManagerSchemeListGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.approvemanagerschemelistgridpanel',
 	requires: [
	],
	loadData:function(planId){
		var me = this;
		//empType = 2是部门汇总人员标识，此处是部门风险管理员查看本部门员工所制定的方案列表
		var param = {planId:planId,empType:"2",executionId:me.executionId};
		me.store.proxy.url = __ctxPath+"/majorResponse/loadCollectSchemeOfDept";
		me.store.sync = true;
		me.store.proxy.extraParams = param;
		me.store.load();
		
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
        	{
				header: "taskExecutionId",
				dataIndex:'taskExecutionId',
				hidden:true
			},
			{
				header: "planId",
				dataIndex:'planId',
				hidden:true
			},
			{
				header: "schemeId",
				dataIndex:'schemeId',
				hidden:true
			},
			{
				header: "empId",
				dataIndex:'empId',
				hidden:true
			},
			{
				header: "riskId",
				dataIndex:'riskId',
				hidden:true
			},
			{
				header: "orgId",
				dataIndex:'orgId',
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
	            header: "方案描述",
	            dataIndex: 'description',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "方案制定人",
	            dataIndex: 'empName',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "操作",
	            dataIndex: 'operation',
	            sortable: true,
	            width:40,
	            flex:2,
	            xtype:'actioncolumn',
	            items: [
	            	{
		                icon:  __ctxPath+'/images/icons/trend.gif',
		                tooltip: '查看明细	',
		                handler: function(grid, rowIndex, colIndex) {
			                	grid.getSelectionModel().deselectAll();
		    					var rows=[grid.getStore().getAt(rowIndex)];
		    	    			var selectRowData = rows[0].data;
		                	}
		       			}
	            ]
	        }
        ];
        
        Ext.apply(me,{
        	region:'center',
        	cols:cols,
        	tbarItems:['<b>部门汇总员工->方案信息</b>',
        		'-',
        		],
		    border: true,
		    checked : false,
		    pagable : false
        });
        me.callParent(arguments);
        me.loadData(me.businessId);
    }

});