Ext.define('FHD.view.sys.log.LogFileList',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.logfilelist',
    
    pagable: false,
    
    initComponent: function(){
    	var me = this;
    	
		Ext.apply(me,{
			url: __ctxPath + '/sys/log/findlogfilelist.f',
	        cols: [
	    		{header: '文件名称',dataIndex: 'fileName',sortable: true, flex: 1}, 
	 			{header: '最后更新日期', sortable: true,dataIndex: 'lastUpdateTime',flex:1},
	 			{header: '文件大小', sortable: true,dataIndex: 'size',flex:1},
	    		{header: '操作',dataIndex:'filePath',align: 'center',
	 				xtype:'actioncolumn',
	 				items: [{
		                iconCls: 'icon-download',  // Use a URL in the icon config
		                tooltip: '下载',
		                handler: function(grid, rowIndex, colIndex) {
		                    grid.getSelectionModel().deselectAll();
		                    var recode = grid.getStore().getAt(rowIndex);
	    					var rows=[recode];
	    	    			grid.getSelectionModel().select(rows,true);
	    	    			window.open(__ctxPath + "/tmp/import/download.jsp?fullpath=" + recode.get("filePath"));
		                }
	 				}]
	    		}]
		});
        me.callParent(arguments);
    },
    reloadData:function(){
		var me=this;
		
		me.store.load();
	}
});