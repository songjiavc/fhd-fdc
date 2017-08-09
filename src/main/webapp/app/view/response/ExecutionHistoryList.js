/*
 * 内控评价列表页面 
 * */
Ext.define('FHD.view.response.ExecutionHistoryList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.executionhistorylist',
    requires: [
    ],
    layout: 'fit',
    flex : 9,
    checked :  false,
    searchable:false,
    pagable : false,
    autoHeight : true,
    border:false,
	//可编辑列表为只读属性
	url : '',
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    initComponent: function(){
    	var me = this;
        me.cols = [
 			{header : 'id',dataIndex : 'id',hidden : true},
 			{header : '实际开始时间',dataIndex : 'realStartTime',sortable : false, flex : 1},
 			{header : '实际结束时间',dataIndex : 'realFinishTime',sortable : false, flex : 1},
 			{header : '实际成本',dataIndex : 'realCost',sortable : false, flex : 1},
 			{header : '实际收入',dataIndex : 'realIncome',sortable : false, flex : 1},
 			{header : '进度',dataIndex : 'progress',sortable : false, flex : 1},
 			{header : '描述',dataIndex : 'desc',sortable : false, flex : 1}
		];
        Ext.apply(me,{
        	border:me.border
        });
        me.callParent(arguments);
    },
	reloadData:function(){
		var me=this;
		me.store.proxy.url = __ctxPath + '/response/execute/executionhistorylist.f',
        me.store.proxy.extraParams = {
        	solutionExecutionId : me.paramObj.solutionExecutionId
        };
		me.store.load();
	}
});