/**
 * 我的数据
 * 我的控制措施
 * @author 邓广义
 */
Ext.define('FHD.view.icm.statics.IcmMyControlInfo', {
    alias: 'widget.icmmycontrolinfo',
 	extend: 'Ext.container.Container',
 	overflowX: 'hidden',
	overflowY: 'auto',
	layout: {
        type: 'vbox',
        align: 'stretch'
    },
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
				{dataIndex:'orgId',hidden: true},
				{ header: '控制编号',  dataIndex: 'measureCode' ,flex: 1 ,
					renderer:function(value,metaData,record,colIndex,store,view) {
							return value; 
					}
				},
				{ header: '控制名称', dataIndex: 'measureName', flex: 2 ,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'" '; 
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showControlMeasureView('" + record.data.id + "')\" >" + value + "</a>"; 
					}
				},
				{header:'责任部门',dataIndex:'orgName',flex:1},
				{header:'控制方式',dataIndex:'controlMeasureName',flex:1},
				{header:'实施证据',dataIndex:'implementProof',flex:1},
				{header:'是否关键控制点',dataIndex:'isKeyControl',flex:1},
				{header:'更新日期',dataIndex:'updateDate',width:90}
			],
			url: __ctxPath+'/icm/statics/findcontrolmeasurebysome.f',
			storeAutoLoad:false,
			extraParams:me.extraParams,
			tbarItems: [
				{iconCls : 'icon-ibm-action-export-to-excel',text:'导出到Excel',tooltip: '把当前列表导出到Excel',handler :me.exportChart,scope : this}
			],
			checked:false,
			searchable:true,
			pagable : true
		});			 
		me.controlmeasurecountchart = Ext.create('FHD.view.icm.statics.ControlMeasureCountChart',{
			flex:1,
			extraParams:me.extraParams,
			toolRegion:'west'
		});
		me.callParent(arguments);
		me.add(me.controlmeasurecountchart);
        me.add(me.gird);
    },
    showControlMeasureView:function(measureId){
    	var me = this;
    	var grid = Ext.create('FHD.view.icm.icsystem.form.MeaSureEditFormForView',{paramObj:{measureId:measureId},readOnly:true});;
    	grid.reloadData();
    	me.win=Ext.create('FHD.ux.Window',{
			title : '详细查看',
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
    reloadData:function(){
    	var me=this;
    	me.gird.store.proxy.extraParams = me.extraParams;
    	me.gird.store.load();
    	me.controlmeasurecountchart.extraParams = me.extraParams;
    	me.controlmeasurecountchart.reloadData();
    },
     //导出grid列表
    exportChart:function(item, pressed){
    	var me=this;
    	if(me.gird.getStore().getCount()>0){
    		FHD.exportExcel(me.gird,'exportexcel','控制措施数据');
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'没有要导出的数据!');
    	}
    }
});