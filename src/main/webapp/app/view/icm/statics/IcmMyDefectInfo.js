/**
 * 我的数据
 * 我的缺陷
 * @author 吴德福
 */
Ext.define('FHD.view.icm.statics.IcmMyDefectInfo', {
	extend: 'Ext.container.Container',
 	alias: 'widget.icmmydefectinfo',
 	
 	requires: [
    	'FHD.ux.GridPanel',
    	'FHD.view.icm.statics.DefectCountChart',
    	'FHD.view.icm.defect.form.DefectFormForView',
    	'FHD.ux.Window'
    ],
 	      
 	overflowX: 'hidden',
	overflowY: 'auto',
	layout: {
        type: 'vbox',
        align: 'stretch'
    },
    
    displayChart:true,
    initParam:function(extraParams){
	   	var me = this;
	   	me.extraParams = extraParams;
    },
    // 初始化方法
    initComponent: function() {
    	 var me = this;
    	 me.callParent(arguments);
		 me.gird = Ext.widget('fhdgrid', {
			border:false,
		 	flex:1,
			cols: [
				{ dataIndex:'id',hidden:true},
				{ header: '缺陷描述',  dataIndex: 'defectDesc' ,flex: 3 ,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"';
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showDefectView('" + record.data.id + "','defect')\" >" + value + "</a>"; 
					}
				},
				{ header: '缺陷等级', dataIndex: 'defectLevel', flex: 1},
				{ header: '缺陷类型', dataIndex: 'defectType', flex: 1},
				{ header: '整改状态', dataIndex: 'dealStatus', flex: 1}, 
				{ header: '整改责任部门', dataIndex: 'orgName', flex: 1},
				{ header: '更新日期', dataIndex: 'updateDate', width:90}
			],
			url: __ctxPath+'/icm/statics/finddefectbysome.f',
			storeAutoLoad:false,
			//extraParams:me.extraParams,
			tbarItems: [
				{iconCls : 'icon-ibm-action-export-to-excel',text:'导出到Excel',tooltip: '把当前列表导出到Excel',handler :me.exportChart,scope : this}
			],
			checked:false,
			searchable:true,
			pagable : true
		});			
		if(me.displayChart){
			me.defectcountchart = Ext.widget('defectcountchart',{
				flex:1,
				extraParams:me.extraParams,
				toolRegion:'west'
			});
        	me.add(me.defectcountchart);
        }
        me.add(me.gird);
    },
    reloadData:function(){
    	var me=this;
    	me.gird.store.proxy.extraParams = me.extraParams;
    	me.gird.store.load();
    	if(me.displayChart){
	    	me.defectcountchart.extraParams = me.extraParams;
	    	me.defectcountchart.reloadData();
    	}
    },
    showDefectView:function(defectId){
    	var me = this;
    	
    	var grid = Ext.widget('defectformForview',{defectId:defectId,readOnly:true});;
    	grid.reloadData();
    	me.win=Ext.widget('fhdwindow',{
			title : '详细查看',
			flex:1,
			autoHeight:true,
			collapsible : true,
			modal : true,
			maximizable : true,
			listeners:{
				close : function(){
				}
			},
			items:[grid]
		}).show();
    },
    //导出grid列表
    exportChart:function(item, pressed){
    	var me=this;
    	if(me.gird.getStore().getCount()>0){
    		FHD.exportExcel(me.gird,'exportexcel','缺陷数据');
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'没有要导出的数据!');
    	}
    }
});