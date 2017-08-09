/**
 * 我的数据
 * 我的风险
 * @author 邓广义
 */
Ext.define('FHD.view.icm.statics.RiskControlMatrix', {
	extend: 'Ext.container.Container',
    alias: 'widget.riskcontrolmatrix',
    
    requires: [
        'FHD.view.icm.icsystem.form.RiskEditFormForView',
        'FHD.view.icm.icsystem.bpm.PlanProcessEditTabPanelForView',
        'FHD.ux.GridPanel',
        'FHD.ux.Window'
   	],
           
 	overflowX: 'hidden',
	overflowY: 'auto',
	
	layout: 'fit',
	initParam:function(extraParams){
	   	var me = this;
	   	me.extraParams = extraParams;
    },
    // 初始化方法
    initComponent: function() {
    	 var me = this;
		 me.gird = Ext.widget('fhdgrid', {
			border:false,
			cols: [
				{ dataIndex:'id',hidden: true},
				{ dataIndex:'riskId',hidden: true},
				{ dataIndex:'processId',hidden: true},
				{ header: '风险分类', dataIndex: 'parentName', flex: 2,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
							return value; 
					}
				},
				{ header: '风险名称', dataIndex: 'riskName', flex: 2,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showRiskEditView('" + record.data.riskId + "')\" >" + value + "</a>"; 
					}
				},
				{ header: '流程', dataIndex: 'processName', flex: 2,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.processId + "')\" >" + value + "</a>"; 
					}
				},
				{ header: '流程节点', dataIndex: 'processPointName', flex: 1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
							return value; 
					}
				},
				{ header: '控制措施', dataIndex: 'measureName', flex: 2,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
							return value; 
					}
				},
				{ header: '控制方式', dataIndex: 'controlMethod', flex: 1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
							return value; 
					}
				},
				{ header: '控制频率', dataIndex: 'controlFrequency', flex: 1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
							return value; 
					}
				},
				{ header: '责任部门', dataIndex: 'orgName', flex: 1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
							return value; 
					}
				},
				{ header: '责任人', dataIndex: 'empName', flex: 1,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
							return value; 
					}
				},
				{header:'更新日期',dataIndex:'updateDate',width:90}
				
			],
			url: __ctxPath+'/icm/statics/findriskcontrolmatrixbysome.f',
			storeAutoLoad:false,
			//extraParams:me.extraParams,
			tbarItems: [
				{iconCls : 'icon-ibm-action-export-to-excel',text:'导出到Excel',tooltip: '把当前列表导出到Excel',handler :me.exportChart,scope : this}
			],
			checked:false,
			searchable:true,
			pagable : true
		});			 
		
        me.callParent(arguments);
        me.add(me.gird);
    },
    showRiskEditView:function(id){
    	var me=this;
    	me.riskeditformforview=Ext.widget('riskeditformforview',{processRiskId:id});
		me.riskeditformforview.initParam({
			processRiskId : id
		});
		me.riskeditformforview.reloadData();
		me.riskeditformforview.getInitData();
		var win = Ext.create('FHD.ux.Window',{
			title:'风险详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(me.riskeditformforview);
    },
    showProcessView:function(processid){
    	var me = this;
    	var grid = Ext.widget('planprocessedittabpanelforview',{paramObj:{processId:processid},readOnly:true});;
    	grid.reloadData();
    	me.win=Ext.widget('fhdwindow',{
			title : '流程详细信息',
			flex:1,
			autoHeight:true,
			autoScroll:true,
			collapsible : true,
			modal : true,
			maximizable : true,
			listeners:{
				close : function(){
				}
			}
		}).show();
		me.win.add(grid);
    },
    reloadData:function(orgid){
    	var me=this;
		me.gird.store.proxy.extraParams = me.extraParams;
    	me.gird.store.load();
    },
    //导出grid列表
    exportChart:function(item, pressed){
    	var me=this;
    	if(me.gird.getStore().getCount()>0){
    		FHD.exportExcel(me.gird,'exportexcel','风险控制矩阵数据');
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'没有要导出的数据!');
    	}
    }
});