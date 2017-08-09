/**
 * 评估范围fieldSet，部门承办人列表
 */
Ext.define('FHD.view.check.yearcheck.plan.YearCheckPlanNextGrid',{
	extend:'FHD.ux.GridPanel',
	alias: 'widget.yearCheckPlanNextGrid',
	requires: [
    ],
 
    //传参方法
    submitPlanByParams: function(approverId,businessId){
    	var me = this;
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({
			url : __ctxPath + '/check/yearcheck/submitYearCheckPlan.f',
			params : {
			approverId:approverId,
			businessId:businessId,
			executionId:me.executionId
			},
			callback : function(data){
				me.body.unmask();
				if(me.executionId){
				Ext.getCmp(me.winId).close();
				}else{
				var prt = me.up('yearcheckcard');
				prt.yearCheckPlanGrid.store.load();
				//取消列表已选中的列，解决提交后未刷新的重复修改问题
				prt.yearCheckPlanGrid.getSelectionModel().deselectAll(true);
	    		prt.showPlanConformGrid();
				}
			}
		});
    },
 
    //加载列表数据
    loadData: function(planId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/check/yearcheck/findEmpOrgByPlanId.f';
 		me.store.proxy.extraParams.planId = planId;
 		me.store.load();
    },
	
	initComponent:function(){
		var me=this;
		me.cols=[
        	{header: '承办人', dataIndex: 'empName', sortable : true, flex: 5 },
        	{header: '考核部门', dataIndex: 'orgName', sortable : true, flex: 5 },
			{dataIndex : 'id', hidden : true}
		];
//		me.tbar = [
//        		   {text:'配置', iconCls: 'icon-add', handler:function(){
//        		   		me.risksSelect();
//        		   }},
//        		     {text:'删除', iconCls: 'icon-del', handler:function(){
//        		   		me.deleteOrmEmp();
//        		   }}
//                   ];
        
        Ext.apply(me, {
            cols:me.cols,
		    border: true,
		    columnLines: true,
		    checked: false,
		    pagable : false,
		    searchable : false,
		    autoScroll:true
        });
                   
		me.callParent(arguments);
	}
	
});