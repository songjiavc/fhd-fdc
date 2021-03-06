/*
 * 内控评价列表页面 
 * */
Ext.define('FHD.view.icm.icsystem.constructplan.ConstructPlanList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.constructplanlist',
    requires: [
    	'FHD.view.icm.icsystem.constructplan.form.ConstructPlanRangeFormForView'
    ],
    pagable:true,
    layout: 'fit',
    flex : 12,
    border:false,
	//可编辑列表为只读属性
	readOnly : false,
	url : '',//__ctxPath + '/icm/icsystem/constructplan/findconstructplansbypage.f',
    initComponent: function(){
    	var me = this;
		//评价计划列表
        me.on('selectionchange',me.setstatus);//选择记录发生改变时改变按钮可用状态
        me.cols = [
    		{header : '计划编号',dataIndex : 'code',sortable : false, flex : 1}, 
 			{header : '计划名称',dataIndex : 'name',sortable : false, flex : 3},
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
	        	{iconCls : 'icon-add',text: '添加',tooltip: '建设计划',handler :me.addPlan,scope : this},
				'-', 
				{iconCls : 'icon-edit',text: '修改',tooltip: '建设计划',handler :me.editPlan,disabled: true,scope : this},
				'-', 
				{iconCls : 'icon-del',text: '删除',tooltip: '建设计划',handler :me.delPlan,disabled: true,scope : this}
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
    	
    	var constructplaneditpanel = me.up('constructplaneditpanel');
    	constructplaneditpanel.initParam({
    		editFlag : false,
    		businessId : ''
    	});
    	constructplaneditpanel.reloadData();
    	constructplaneditpanel.navBtnHandler(1);
    },
    //编辑计划
    editPlan: function(button){
    	var me=this;
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	var isSubmit=selection[0].get('status');
    	if(isSubmit=='P' || isSubmit == 'D'){
    		FHD.notification('对不起,您不能修改已提交或者是已处理的数据!',FHD.locale.get('fhd.common.prompt'));
    		return false;
    	}
    	
    	var constructplaneditpanel = me.up('constructplaneditpanel');
    	if(constructplaneditpanel){
    		constructplaneditpanel.initParam({
    		editFlag : true,
    		businessId : selection[0].get('id')
    	});
    	constructplaneditpanel.reloadData();
		//激活编辑面板
		constructplaneditpanel.navBtnHandler(1);
    	}
    },
    //删除计划
    delPlan: function(){
		var me=this;
		
		var selection = me.getSelectionModel().getSelection();//得到选中的记录
		
		if(0 == selection.length){
    		FHD.notification(FHD.locale.get('fhd.common.msgDel'),FHD.locale.get('fhd.common.prompt'));
    	}else{
			Ext.MessageBox.show({
				title : FHD.locale.get('fhd.common.delete'),
				width : 260,
				msg : FHD.locale.get('fhd.common.makeSureDelete'),
				buttons : Ext.MessageBox.YESNO,
				icon : Ext.MessageBox.QUESTION,
				fn : function(btn) {
					if(btn == 'yes') {
	 					var ids = [];
						for(var i = 0; i < selection.length; i++) {
							var isSubmit=selection[i].get('status');
					    	if(isSubmit=='P'){
					    		FHD.notification('对不起,您不能删除已提交的数据!',FHD.locale.get('fhd.common.prompt'));
					    		return false;
					    	}else{
					    		ids.push(selection[i].get('id'));
					    	}
	 					}
	 					FHD.ajax({
	 						url : __ctxPath+ '/icm/icsystem/removeconstructplanbyids.f',
	 						params : {
	 							constructPlanIds : ids
	 						},
	 						
	 						callback : function(data) {
	 							if (data.success) { 
	                                FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
		 							me.store.load();
	                            } else {
	                                FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
	                            }
	   						}
						});
					}
				}
			});
    	}
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
		me.store.proxy.url = __ctxPath + '/icm/icsystem/constructplan/findconstructplansbypage.f',
        me.store.proxy.extraParams = {
        	status : 'S'
        };
		me.store.load();
	},
	showPlanViewList:function(id,dealStatus){
    	var me=this;
    	me.constructPlanRangeForm = Ext.widget('constructplanrangeformforview',{
        	businessId : id,
        	editflag: false
        });
        me.constructPlanRangeForm.initParam({
        	businessId : id
        });
        me.constructPlanRangeForm.reloadData();
		var win = Ext.create('FHD.ux.Window',{
			title:'建设计划详细信息',
			//modal:true,//是否模态窗口
			collapsible:true,
			maximizable:true,//（是否增加最大化，默认没有）
			autoScroll : true,
			items:[ me.constructPlanRangeForm ]
    	}).show();
    }
});