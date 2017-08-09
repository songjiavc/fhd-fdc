Ext.define('FHD.view.icm.standard.StandardPlanDashboard', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.standardplandashboard',
    requires : [
  		'FHD.view.icm.standard.form.StandardPreview'
    ],
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    border : false,
    
    initComponent: function() {
        var me = this;
        
        // 计划进度列表
        me.standardPlanGrid = Ext.widget('fhdgrid',{
        	url: __ctxPath + '/icm/standard/findStandardByPage.f',
        	extraParams:{
            	companyId:__user.companyId,
            	dealStatus:'H,F,A'
            },
        	checked:false,
			searchable:true,
			pagable : true,
			flex:7,
			height:300,
			border:true,
			cols : [
			    /*{dataIndex: 'id',hidden:true},
				{header:'计划编号', sortable: false,dataIndex: 'code',flex:1, hidden:true},*/
				{header:'计划名称', sortable: false,dataIndex: 'name',flex:3,
					renderer:function(value,metaData,record,colIndex,store,view) { 
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showPlanViewList('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
					}
				},
				/*
				{header:'计划进度', sortable: false,dataIndex: 'schedule',flex:1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(value){
							value = value +'%';
						}
						return value;
					}
				},
				*/
				{header:'实际进度', sortable: false,dataIndex: 'actualProgress',flex:1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(record.data.dealStatus!='N'){
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessViewList('" + record.data.id +"')\" >" + value +'%' + "</a>"; 
						}
						return value;
					}
				},
				{header:'执行状态', sortable: false,dataIndex: 'dealstatus',flex:1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						if(value == 'N'){
							return "未开始";
						}else if(value == 'H'){
							return "处理中";
						}else if(value == 'U'){
							return "待更新";
						}else if(value == 'O'){
							return "已纳入内控手册运转";
						}else if(value == 'F'){
							return "已完成";
						}
						return value;
					}
				},
				{header:'创建日期', sortable: false,dataIndex: 'createTime',flex:2}
			]
        });
        
        Ext.applyIf(me, {
        	layout: {
				type: 'hbox',
	        	align:'stretch'
	        },
        	items:[me.standardPlanGrid]
        });

        me.callParent(arguments);
    },
    showProcessViewList:function(id){
    	var me=this;
    	me.processInstanceView =Ext.create('FHD.view.bpm.processinstance.ProcessInstanceView',{
			businessId:id
		});
		me.processInstanceView.reloadData();
		var popWin = Ext.create('FHD.ux.Window',{
			title:'内控标准进度信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
		}).show();
		
		popWin.add(me.processInstanceView);
    },
    showPlanViewList:function(id,dealStatus){
    	var me=this;
    	
    	me.standardPlanPanel=Ext.widget('standardpreview',{
    		businessId:id,
			dealStatus:dealStatus
		});
		
		me.standardPlanPanel.reloadData();
		
		var win = Ext.create('FHD.ux.Window',{
			title:'内控标准详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true,//（是否增加最大化，默认没有）
			buttonAlign: 'center'
    	}).show();
    	
    	win.add(me.standardPlanPanel);
    },
    reloadData:function(){
    	var me=this;
    	
    }
});