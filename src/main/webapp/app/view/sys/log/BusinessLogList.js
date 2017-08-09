Ext.define('FHD.view.sys.log.BusinessLogList',{
	extend: 'Ext.container.Container',
    alias: 'widget.businessloglist',
    
    initComponent: function(){
    	var me = this;
    	
		//业务日志列表
		me.businessLogGrid = Ext.create('FHD.ux.GridPanel', {
	        border: false,
	        url: __ctxPath + '/sys/log/findBusinessLogList.f',
	        cols: [
	            {header : 'id',dataIndex : 'id', hidden:true}, 
	    		{header : '用户名',dataIndex : 'username',sortable : true, flex : 2}, 
	 			{header : 'IP地址', sortable: false,dataIndex: 'ip',flex:2},
	 			{header : '操作时间',dataIndex : 'operateTime',sortable : true, flex:2,},
	 			{header : '操作类型',dataIndex : 'operateType',sortable : true, flex:1},
	 			{header : '操作模块',dataIndex : 'moduleName',sortable : true, flex:2},
	 			{header : '操作结果',dataIndex : 'isSuccess',sortable : true, flex:1},
	 			{header : '操作记录',dataIndex : 'operateRecord',sortable : true, flex:5,
	 				renderer:function(value,metaData,record,colIndex,store,view) { 
			    		metaData.tdAttr = 'data-qtip="'+value+'"';
						return value; 
					}
	 			},
	 			{header : '所属公司',dataIndex : 'orgname',sortable : true, flex:2,
	 				renderer:function(value,metaData,record,colIndex,store,view) { 
			    		metaData.tdAttr = 'data-qtip="'+value+'"';
						return value; 
					}
	 			}
			],
			tbarItems: [
				{iconCls: 'icon-export', text:'导出', tooltip: '导出当前页日志', handler: me.exportLog, scope : this},
			]
		});
		Ext.applyIf(me,{
			layout: 'fit',
			items:[me.businessLogGrid]
		});
        me.callParent(arguments);
    },
    //导出grid列表
    exportLog:function(item, pressed){
    	var me=this;
    	
    	if(me.businessLogGrid){
    		FHD.exportExcel(me.businessLogGrid,'exportBusinessLog','exportBusinessLog');
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'没有grid数据源!');
    	}
    },
    reloadData:function(){
		var me=this;
		
		me.businessLogGrid.store.load();
	}
});