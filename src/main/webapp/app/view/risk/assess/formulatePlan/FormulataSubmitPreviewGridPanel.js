Ext.define('FHD.view.risk.assess.formulatePlan.FormulataSubmitPreviewGridPanel', {
    extend: 'FHD.ux.EditorGridPanel',
    alias: 'widget.formulatasubmitpreviewGridPanel',
    flex:1,
   
    // 初始化方法
    initComponent: function() {
        var me = this;
        //目标名称 目标责任人 衡量指标 权重  指标说明  指标责任人
        me.cols = [ 
         			{header : "部门名称", dataIndex : 'deptName', sortable : true, flex : 1},
                    {header : "承办人", dataIndex : 'empName', sortable : true, flex : 1},
                    {header : "审批人", dataIndex : 'approverName', sortable : true, flex : 1}
                  ];
                  
        Ext.apply(me, {
        	region:'center',
        	height: 200,
        	//isNotAutoload : true,
            cols:me.cols,
		    border: true,
		    pagable : false,
		    searchable : false,
		    checked : false
        });

        me.callParent(arguments);
        
    }

});