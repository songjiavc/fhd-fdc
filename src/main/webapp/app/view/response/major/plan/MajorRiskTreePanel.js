Ext.define('FHD.view.response.major.plan.MajorRiskTreePanel', {
	extend : 'Ext.container.Container',
	alias : 'widget.majorrisktreepanel',
	border:true,
	split: true,
    collapsible: false,
    width: 265,
    maxWidth: 300,
	layout : {
		type : 'accordion',
		titleCollapse: true,
        animate: false,
        collapseFirst: true,
        activeOnTop: false			
	},
	planId:null,
	/**
	 * 风险分类树是否显示
	 */
	riskcatalogtreevisable:true,
	riskcatalogListUrl:'/cmp/risk/findPotentialRiskByRiskId.f',
	init:function(){
		var me = this;
		if(me.riskcatalogtreevisable){
			//初始化参数
	    	var extraParams = {};
	    	extraParams.rbs = true;
	    	extraParams.canChecked = true;
	    	extraParams.schm = me.schm;
	    	extraParams.planId = me.planId;
	    	me.riskcatalogtree = Ext.create('FHD.ux.TreePanel', {
	    		root:{
	    	        "id": "root",
	    	        "text": "重大风险树",
	    	        "dbid": "sm_root",
	    	        "leaf": false,
	    	        "code": "sm",
	    	        "type": "orgRisk",
	    	        "expanded": true,
	    	        'iconCls':'icon-ibm-icon-scorecards'	//样式
	    	    },
	    	    multiSelect:true,
	    	    checkModel:"multiple",
	    		rootVisible: false,
	    		url:__ctxPath + '/majorResponse/getTreeRecord',
	    		title:'重大风险树',
				iconCls: 'icon-table-key',
	           	extraParams:extraParams,
	           	check:function(tree,node,checked){
	           		var treeNode = node.data;
	           		var checked = treeNode.checked;//true or false
	           		//节点名称
	           		var nodeName = treeNode.text;
	           		//节点id
	           		var nodeId = treeNode.id;
	           		//计划id
	           		var planId = me.up("majorriskplanformtwo").planId.getValue();
	           		var selectedNodes = me.riskcatalogtree.getChecked();
	           		//已选的重大风险数组
	           		var arr = new Array();
	           		Ext.each(selectedNodes, function (node) {
	           			 var selectedNodeData = node.data;
	           			 var ret = {
	           					majorRiskId:selectedNodeData.id,
	           					majorRiskName:selectedNodeData.text,
	           					planId:planId
	           			 }
	           			 arr.push(ret);
				    });
	           		var majorRiskSelectedGrid = me.up("majorriskplanformtwo").riskSelecteGrid;
	           		//设置获取已选重大风险下所涉及的部门：主责和相关
	           		var majorRisk = {
	           				majorRiskId : nodeId,
	           				majorRiskName :nodeName,
	           				planId:planId
	           				
	           			};
	           		var store = majorRiskSelectedGrid.getStore();
	           		if(checked){//选中
	           			FHD.ajax({
		        			url:__ctxPath + "/majorResponse/getDeptForMajorRisk",
		                    params:{
		                    	majorRisk:JSON.stringify(majorRisk)
		                    },
		                    async: false,
		                    callback: function (result) {
	                    		Ext.each(result, function (data) {
		               				majorRiskSelectedGrid.getStore().insert(0,data);
		    	           		});
		               			
		                    }
		                });
	           		}else{//未选中
	           			var store = majorRiskSelectedGrid.getStore();
	           			var findResult = store.query("riskName",nodeName);
	           			Ext.each(findResult.items, function (data) {
	           				store.remove(data);
	           			});
	           		}
	           	}
	        });
			me.add(me.riskcatalogtree);
		}
	},
	//设置tree节点状态
	setCheck :function(gridData){
		var me = this;
		var checkedNodes = me.riskcatalogtree.getChecked();
		var flag = false;
		Ext.each(checkedNodes,function(node){
			 for(var i=0;i<gridData.length;i++){
				 //判断表格中是否含有tree中的节点数据
				 if(gridData[i].data.riskName == node.data.text){
					 flag = true;
					 break;
				 }
			 }
			 node.set("checked",flag);
		});
	},
	initComponent : function() {
		
		var me = this;
		Ext.applyIf(me, {
			
		});
		
		me.callParent(arguments);
		me.init();
		//异步加载设置tree勾选状态
		me.riskcatalogtree.addListener("afteritemexpand",function(){
			var majorRiskSelectedGrid = me.up("majorriskplanformtwo").riskSelecteGrid;
			var gridData = majorRiskSelectedGrid.getStore().data.items;
			me.setCheck(gridData)
		})
	}
});