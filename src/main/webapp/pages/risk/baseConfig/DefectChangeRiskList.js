/*
 * 内控评价列表页面 
 * */
Ext.define('pages.risk.baseConfig.DefectChangeRiskList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.defectchangerisklist',
    pagable:true,
    layout: 'fit',
    flex : 12,
    height : 510,
    border:false,
	//可编辑列表为只读属性
	readOnly : false,
	url : __ctxPath + '/pages/risk/baseConfig/DefectChangeRiskList.json',
    initComponent: function(){
    	var me = this;
		//评价计划列表
        me.on('selectionchange',me.setstatus);//选择记录发生改变时改变按钮可用状态
        me.cols = [
    		{header : '发生频率',dataIndex : 'code',sortable : false, flex : 1}, 
 			{header : '缺陷等级',dataIndex : 'name',sortable : false, flex : 3
 				
 			},
 			{header : '发生可能性',dataIndex : 'type',sortable : false, flex:1}, 
 			{header : '影响程度',dataIndex : 'targetStartDate',sortable : false, flex : 1}
 			
		];
		if(me.readOnly){
			me.searchable = false;
		}else{
	        me.tbarItems = [
	        	{iconCls : 'icon-add',text: '添加',tooltip: '应对计划',handler :me.addPlan,scope : this},
				'-', 
				{iconCls : 'icon-edit',text: '修改',tooltip: '应对计划',handler :me.editPlan,disabled: true,scope : this},
				'-', 
				{iconCls : 'icon-del',text: '删除',tooltip: '应对计划',handler :me.delPlan,disabled: true,scope : this}
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
//		me.store.proxy.url = __ctxPath + '/app/view/response/responseplan/ResponsePlanList.json',
//        me.store.proxy.extraParams = {
//        	status : 'S'
//        };
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