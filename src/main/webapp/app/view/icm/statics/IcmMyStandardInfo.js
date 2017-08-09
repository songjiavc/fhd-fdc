/**
 * 我的数据
 * 我的标准
 * @author 邓广义
 */
Ext.define('FHD.view.icm.statics.IcmMyStandardInfo', {
    alias: 'widget.icmmystandardinfo',
 	extend: 'Ext.container.Container',
 	overflowX: 'hidden',
	overflowY: 'auto',
	layout: {
        type: 'vbox',
        align: 'stretch'
    },
    requires: [
       'FHD.view.icm.standard.form.StandardControlPlanPreview'
    ],
    initParam:function(extraParams){
	   	var me = this;
	   	me.extraParams = extraParams;
    },
    // 初始化方法
    initComponent: function() {
    	 var me = this;
		 me.gird = Ext.create('FHD.ux.GridPanel', {
	 		flex:1,
			cols: [
				{dataIndex:'id',hidden: true},
				{dataIndex:'parentId',hidden: true},
				{ header: '要求编号',  dataIndex: 'standardCode' ,flex: 1 ,
					renderer:function(value,metaData,record,colIndex,store,view) {
							return value; 
					}
				},
				{ header: '要求内容', dataIndex: 'standardName', flex: 2 ,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'" '; 
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showStandardView('" + record.data.id + "')\" >" + value + "</a>"; 
					}
				},
				{header:'责任部门',dataIndex:'orgName',flex:1},
				{header:'内控标准',dataIndex:'parentName',flex:1},
				{header:'控制层级',dataIndex:'controlLevel',flex:1},
				{header:'内控要素',dataIndex:'controlPoint',flex:1},
				{header:'处理状态',dataIndex:'dealStatus',flex:1},
				{header:'更新日期',dataIndex:'updateDate',width:90}
			],
			url: __ctxPath+'/icm/statics/findstandardbysome.f',
			storeAutoLoad:false,
			extraParams:me.extraParams,
			tbarItems: [
				{iconCls : 'icon-ibm-action-export-to-excel',text:'导出到Excel',tooltip: '把当前列表导出到Excel',handler :me.exportChart,scope : this}
			],
			checked:false,
			searchable:true,
			pagable : true
		});			 
		
		me.standardcountchart = Ext.create('FHD.view.icm.statics.StandardCountChart',{
			flex:1,
			extraParams:me.extraParams,
			toolRegion:'west'
		});
        me.callParent(arguments);
        me.add(me.standardcountchart);
        me.add(me.gird);
    },
    reloadData:function(orgid){
    	var me=this;
		me.gird.store.proxy.extraParams = me.extraParams;
    	me.gird.store.load();
    	me.standardcountchart.extraParams = me.extraParams;
    	me.standardcountchart.reloadData();
    },
    showStandardView:function(standardId){
    	var me=this;
    	me.standardPlanPanel=Ext.widget('standardcontrolplanpreview',{});
		me.standardPlanPanel.initParam({
		 	standardControlId : standardId
		});
		me.standardPlanPanel.reloadData();
		
		var win = Ext.create('FHD.ux.Window',{
			title:'详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true,//（是否增加最大化，默认没有）
			buttonAlign: 'center'
    	}).show();
    	win.add(me.standardPlanPanel);
    },
    //导出grid列表
    exportChart:function(item, pressed){
    	var me=this;
    	if(me.gird.getStore().getCount()>0){
    		FHD.exportExcel(me.gird,'exportexcel','内控要求数据');
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'没有要导出的数据!');
    	}
    }
});