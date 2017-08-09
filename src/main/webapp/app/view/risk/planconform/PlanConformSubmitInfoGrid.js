Ext.define('FHD.view.risk.planconform.PlanConformSubmitInfoGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.planConformSubmitInfoGrid',
    flex:1,
   
    reloadData: function(id){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/formulateplan/queryriskcircuseegrid.f';
		me.store.proxy.extraParams.planId = id;
		me.store.load();
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.cols = [ 
         			{header : "部门名称", dataIndex : 'deptName', sortable : true, flex : 1},
                    {header : "承办人", dataIndex : 'empName', sortable : true, flex : 1},
                    {header : "审批人", dataIndex : 'approverName', sortable : true, flex : 1}
                  ];
                  
        Ext.apply(me, {
        	region:'center',
        	height: 200,
            cols:me.cols,
		    border: true,
		    pagable : false,
		    searchable : false,
		    checked : false
        });

        me.callParent(arguments);
        
    }

});