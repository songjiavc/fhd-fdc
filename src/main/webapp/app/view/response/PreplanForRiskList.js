/*
 * 针对风险的预案列表页面 
 * */
Ext.define('FHD.view.response.PreplanForRiskList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.preplanforrisklist',
    requires: [
    	'FHD.view.response.PreplanViewForm'
    ],
    pagable:true,
    layout: 'fit',
    flex : 11,
    border:false,
	//可编辑列表为只读属性
	readOnly : false,
	url : __ctxPath + '/app/view/response/PreplanList.json',
    initComponent: function(){
    	var me = this;
		//评价计划列表
        me.on('selectionchange',me.setstatus);//选择记录发生改变时改变按钮可用状态
        me.cols = [
        	{header : '风险名称',dataIndex : 'riskName',sortable : false, flex : 2}, 
    		{header : '方案编号',dataIndex : 'code',sortable : false, flex : 1}, 
 			{header : '方案名称',dataIndex : 'name',sortable : false, flex : 3,
 				renderer : function(value,metaData,record,colIndex,store,view){ 
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showMoreInfo('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
					}
 			},
 			{header : '负责人',dataIndex : 'personInCharge',sortable : false, flex : 1},
 			{header : '责任部门',dataIndex : 'manageOrg',sortable : false, flex:1}, 
 			{header : '预计日期',dataIndex : 'completeDate',sortable : false, flex : 1},
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
			{header : '创建时间',dataIndex :'createTime',sortable : false, flex : 1}
		];
        me.tbarItems = [
        	{iconCls : 'icon-add',text: '添加',tooltip: '添加风险预案',handler :me.addPlan,scope : this},
			'-', 
			{iconCls : 'icon-edit',text: '修改',tooltip: '修改风险预案',handler :me.editPlan,disabled: true,scope : this},
			'-', 
			{iconCls : 'icon-del',text: '删除',tooltip: '删除风险预案',handler :me.delPlan,disabled: true,scope : this}
			
        ];
        Ext.apply(me,{
        	border:me.border
        });
        me.callParent(arguments);
    },
    //新增方案
    addPlan:function(){
    	var me=this;
//    	me.removeAll(true);
//    	var preplanform = Ext.create('FHD.view.response.PreplanForm',{});
//		me.add(preplanform);
    	var preplanlistpanel = me.up('preplanlistpanel');
    	preplanlistpanel.setActiveItem(preplanlistpanel.preplanforriskform);
    },
    //编辑方案
    editPlan: function(button){
    },
    //删除方案
    delPlan: function(){
    },
    //新增预案
    addPreplan: function(){
    	
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
		var preplanviewform = Ext.widget('preplanviewform',{type : 'risk'});
		var win = Ext.create('FHD.ux.Window',{
			title:'应对方案详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(preplanviewform);
	}
});