Ext.define('FHD.view.response.major.scheme.MajorRiskItemsGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.majorriskitemsgrid',
 	requires: [
	],
	schemeObjectId:"",
	executionObjectId:"",
	loadData:function(schemeObjectId){
		var me = this;
		var param = {schemeObjectId:schemeObjectId,planId:me.businessId};
		me.store.proxy.url = __ctxPath+"/majorResponse/loadItemBySchemeId";
		me.store.sync = true;
		me.store.proxy.extraParams = param;
		me.store.load();
	},
	initData:function(){
		var me = this;
		var param = {planId:me.businessId,empType:me.empType,executionId:me.executionId};
		me.store.proxy.url = __ctxPath+"/majorResponse/InfoItemByPlanAndDeptInfo";
		me.store.sync = true;
		me.store.proxy.extraParams = param;
		me.store.load();
	},
	initParam:function(schemeObjectId,executionObjectId,businessId,schemeType,empType){
		var me = this;
		me.schemeObjectId = schemeObjectId;
		me.executionObjectId = executionObjectId;
		me.businessId = businessId;
		me.schemeType = schemeType;
		me.empType = empType;
	},
	addRiskItems :function(){
		var me = this;
		me.openItemAddWin(me.schemeObjectId,me.executionObjectId,me.businessId,me.schemeType,me.empType)
	},
	//打开添加风险事项面板
	openItemAddWin:function(schemeObjectId,executionObjectId,businessId,schemeType,empType){
		var me = this;
		if(schemeObjectId == null ||schemeObjectId ==""){
			FHD.notification('保存方案信息失败,请联系技术人员！',FHD.locale.get('fhd.common.prompt'));
			return false;
		}
		var schemeObjectId = schemeObjectId;
		var executionObjectId = executionObjectId;
		me.win = Ext.create('FHD.view.response.major.scheme.MajorRiskAddRiskItemsWindow',{//添加风险事项弹窗
    		modal: true,
    		businessId: businessId,
    		executionId:me.executionId,
    		schemeObjectId:schemeObjectId,
    		executionObjectId:executionObjectId,
    		schemeType:schemeType,
    		empType:empType,
		   	onSubmit:function(win){
		   		me.loadData(schemeObjectId);
    		}
		}).show();
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
	            header: "风险事项名称",
	            dataIndex: 'description',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "相关流程",
	            dataIndex: 'flow',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "产生动因",
	            dataIndex: 'reason',
	            sortable: true,
	            width:40,
	            flex:2
	        },
	        {
	            header: "负责人",
	            dataIndex: 'userName',
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
    			text:'添加风险事项', 
    			iconCls: 'icon-add', 
    			handler:function(){
    				me.addRiskItems();
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
        //me.initData();
    }

});