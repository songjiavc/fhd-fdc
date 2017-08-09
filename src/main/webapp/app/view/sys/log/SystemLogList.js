Ext.define('FHD.view.sys.log.SystemLogList',{
	extend: 'Ext.container.Container',
    alias: 'widget.systemloglist',
    
    initComponent: function(){
    	var me = this;
    	
		//系统日志列表
		me.systemLogGrid = Ext.create('FHD.ux.GridPanel', {
	        border: false,
	        url: __ctxPath + '/sys/log/findSystemLogList.f',
	        cols: [
	    		{header : '记录时间',dataIndex : 'logDate',sortable : true, flex : 2}, 
	 			{header : '日志级别', sortable: false,dataIndex: 'logLevel',flex:1},
	 			{header : '位置',dataIndex : 'location',sortable : true, flex:3,
	 				renderer:function(value,metaData,record,colIndex,store,view) { 
			    		metaData.tdAttr = 'data-qtip="'+value+'"';
						return value; 
					}
	 			},
	 			{header : '描述',dataIndex : 'message',sortable : true, flex:4,
	 				renderer:function(value,metaData,record,colIndex,store,view) { 
			    		metaData.tdAttr = 'data-qtip="'+value+'"';
						return value; 
					}
	 			}
			]
		});
		Ext.applyIf(me,{
			layout: 'fit',
			items:[me.systemLogGrid]
		});
        me.callParent(arguments);
    },
    reloadData:function(){
		var me=this;
		
		me.systemLogGrid.store.load();
	}
});