Ext.define('FHD.view.response.major.scheme.MajorRiskCounterGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.majorriskcountergrid',
 	requires: [
	],
	
	loadData:function(itemId){
		var me = this;
		var param = {itemId:itemId,schemeObjectId:me.schemeObjectId};
		me.store.proxy.url = __ctxPath+"/majorResponse/loadCounterByItemId";
		me.store.sync = true;
		me.store.proxy.extraParams = param;
		me.store.load();
	},
	
	addCounter :function(){
		var me = this;
		me.up("majorriskaddriskitemsformpanel").saveItem();
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "改进措施",
	            dataIndex: 'description',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "管理目标",
	            dataIndex: 'target',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "责任人",
	            dataIndex: 'userName',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "完成标志",
	            dataIndex: 'completeSign',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "操作",
	            dataIndex: 'operation',
	            sortable: true,
	            width:40,
	            flex:2,
	            xtype:'actioncolumn',
	            items: [
	            	{
		                icon: __ctxPath+'/images/icons/delete_icon.gif',  // Use a URL in the icon config
		                tooltip: FHD.locale.get('fhd.common.del'),
		                handler: function(grid, rowIndex, colIndex) {
		                	grid.getSelectionModel().deselectAll();
	    					var rows=[grid.getStore().getAt(rowIndex)];
	    	    			grid.getSelectionModel().select(rows,true);
	    	    			Ext.MessageBox.show({
	    	    	    		title : FHD.locale.get('fhd.common.delete'),
	    	    	    		width : 260,
	    	    	    		msg : FHD.locale.get('fhd.common.makeSureDelete'),
	    	    	    		buttons : Ext.MessageBox.YESNO,
	    	    	    		icon : Ext.MessageBox.QUESTION,
	    	    	    		fn : function(btn) {
	    	    	    			if (btn == 'yes') {//确认删除
	    	    	    				
	    	    	    				
	    	    	    			}
	    	    	    		}
	    	    	    	});
	    	    			
		                }
		            }
	            ]
	        }
        ];
        me.tbar = [
        	{
    			text:'添加措施', 
    			iconCls: 'icon-add', 
    			handler:function(){
    				me.addCounter();
    			}
			},
			/*{
				btype:'delete',
    			handler:function(){
    			}
			}*/];
        Ext.apply(me,{
        	region:'center',
        	cols:cols,
        	tbarItems:me.tbar,
		    border: true,
		    checked : true,
		    pagable : false
        });
        me.callParent(arguments);
        me.loadData("");
    }

});