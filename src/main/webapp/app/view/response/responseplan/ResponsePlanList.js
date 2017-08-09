/*
 * 内控评价列表页面 
 * */
Ext.define('FHD.view.response.responseplan.ResponsePlanList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.responseplanlist',
    requires: [
    	'FHD.view.response.responseplan.form.ResponsePlanRangeFormForView',
    	'FHD.view.response.responseplan.bpm.ResponsePlanApproveForBpm'
    ],
    pagable:true,
    layout: 'fit',
    flex : 12,
    border:false,
	//可编辑列表为只读属性
	readOnly : false,
    initComponent: function(){
    	var me = this;
		//评价计划列表
        me.on('selectionchange',me.setstatus);//选择记录发生改变时改变按钮可用状态
        me.cols = [
    		{header : '计划编号',dataIndex : 'code',sortable : false, flex : 1}, 
 			{header : '计划名称',dataIndex : 'name',sortable : false, flex : 3,
 				renderer : function(value,metaData,record,colIndex,store,view){ 
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showMoreInfo('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
					}
 			},
 			{header : '工作内容',dataIndex : 'type',sortable : false, flex:1}, 
 			{header : '计划开始日期',dataIndex : 'targetStartDate',sortable : false, flex : 1},
 			{header : '计划完成日期',dataIndex : 'targetEndDate',sortable : false, flex : 1},
 			{header : '状态',dataIndex :'status',sortable : false,flex:1,
				renderer:function(value){
					if(value=='S'){
						return '已保存';
					}else if(value=='P'){
						return '已提交';
					}else{
						return '已处理';
					}
				}
 			},
 			{header : '执行状态',dataIndex :'dealStatus',sortable : false,flex:1,
				renderer:function(value){
					if(value=='N'){
						return '未开始';
					}else if(value=='H'){
						return '处理中';
					}else if(value=='F'){
						return '已完成';
					}
				}
 			},
			{header : '创建时间',dataIndex :'createTime',sortable : false, flex : 2}
		];
		if(me.readOnly){
			me.searchable = false;
		}else{
	        me.tbarItems = [
	        	{iconCls : 'icon-add',text: '添加',tooltip: '应对计划',handler :me.addPlan,scope : this},
				'-', 
				{iconCls : 'icon-edit',text: '修改',tooltip: '应对计划',handler :me.editPlan,disabled: true,scope : this},
				'-', 
				{iconCls : 'icon-del',text: '删除',tooltip: '应对计划',handler :me.delPlan,disabled: true,scope : this},
				'-', 
				{text: '计划审批',tooltip: '计划审批',handler :me.responsePlanApply,scope : this},
				'-', 
				{text: '方案审批',tooltip: '方案审批',handler :me.solutionPlanApply,scope : this}
	        ];
		}
        //评价计划列表
        Ext.apply(me,{
        	border:me.border
        });
        me.callParent(arguments);
    },
    //新增计划
    addPlan:function(){
    	var me=this;
    	var responseplaneditpanel = me.up('responseplaneditpanel');
    	responseplaneditpanel.setActiveItem(responseplaneditpanel.responseplanpanel);
    },
    //编辑计划
    editPlan: function(){
    	var me=this;
    	var responseplaneditpanel = me.up('responseplaneditpanel');
    	responseplaneditpanel.setActiveItem(responseplaneditpanel.responseplanpanel);
    },
    //编辑计划
    responsePlanApply: function(){
    	var responseplanapproveforbpm = Ext.create('FHD.view.response.responseplan.bpm.ResponsePlanApproveForBpm');
		var win = Ext.create('FHD.ux.Window',{
			title:'计划审批',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(responseplanapproveforbpm);
    },
    //编辑计划
    solutionPlanApply: function(){
    	var solutionapproveformforbpm = Ext.create('FHD.view.response.responseplan.bpm.SolutionApproveFormForBpm');
		var win = Ext.create('FHD.ux.Window',{
			title:'方案审批',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(solutionapproveformforbpm);
    },
    //删除计划
    delPlan: function(){
    },
    setstatus: function(){
    	var me = this;
        me.down('[iconCls=icon-edit]').setDisabled(me.getSelectionModel().getSelection().length === 0);
		me.down('[iconCls=icon-del]').setDisabled(me.getSelectionModel().getSelection().length === 0);
    },
    setCenterContainer:function(compent){
    	var me=this;
    	
    	me.removeAll(true);
    	me.add(compent);
    },
	reloadData:function(){
		var me=this;
		me.store.proxy.url = __ctxPath + '/response/business/web/findresponselistbypage.f';
        me.store.proxy.extraParams = {
        	type : 'process',
        	
        };
		me.store.load();
	},
	showMoreInfo : function(){ 
		var responseplanrangeformforview = Ext.widget('responseplanrangeformforview');
		var win = Ext.create('FHD.ux.Window',{
			title:'建设计划详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(responseplanrangeformforview);
	}
});