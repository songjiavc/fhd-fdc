Ext.define('FHD.view.response.major.plan.MajorRiskGridSelected', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.majorriskgridselected',
 	requires: [
	],
	loadData:function(planId){
		var me = this;
		var param = {planId:planId};
		me.store.proxy.url = __ctxPath+"/majorResponse/loadPlanSelectedDataByPlanId";
		me.store.sync = true;
		me.store.proxy.extraParams = param;
		me.store.load();
		return me.store;
		
	},
	//删除方法
    del : function(me){
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	Ext.MessageBox.show({
    		title : FHD.locale.get('fhd.common.delete'),
    		width : 260,
    		msg : FHD.locale.get('fhd.common.makeSureDelete'),
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
    				Ext.each(selection, function (data) {
           				me.store.remove(data);
           				var gridData = me.getStore().data.items;
	    				me.up("majorriskplanformtwo").treePanel.setCheck(gridData);
           			});
    				
    			}
    		}
    	});
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
				header: "planId",
				dataIndex:'planId',
				hidden:true
			},
			{
				header: "riskId",
				dataIndex:'riskId',
				hidden:true
			},
			{
				header: "deptId",
				dataIndex:'deptId',
				hidden:true
			},
	        {
	            header: "重大风险",
	            dataIndex: 'riskName',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "部门",
	            dataIndex: 'dept',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "责任类型",
	            dataIndex: 'deptType',
	            sortable: true,
	            width:40,
	            flex:2,
	            renderer: function(value){
	            	if(value =="A"){
	            		return "相关部门";
	            	}else if(value =="M"){
	            		return "主责部门";
	            	}
	            }
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
	    	    	    				grid.getStore().removeAt(rowIndex);
	    	    	    				var gridData = grid.getStore().data.items;
	    	    	    				me.up("majorriskplanformtwo").treePanel.setCheck(gridData);
	    	    	    			}
	    	    	    		}
	    	    	    	});
	    	    			
		                }
		            }
	            ]
	        }
        ];
        
        Ext.apply(me,{
        	region:'center',
        	cols:cols,
        	tbarItems:['<b>已选择的重大风险列表</b>','-',{
        			btype:'delete',
        			handler:function(){
        				me.del(me);
        			}
    			}],
		    border: false,
		    checked : true,
		    pagable : false
        });
        me.callParent(arguments);
//        me.store.on('load',function(){
//	    });
    }

});