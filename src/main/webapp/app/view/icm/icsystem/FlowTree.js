/**
 * 流程树
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.icm.icsystem.FlowTree',{
	extend: 'FHD.ux.TreePanel',
    alias: 'widget.flowtree',
	url : __ctxPath + '/process/processTree/findrootProcessTreeLoader.f',
	root: {
        text: '流程库',
        dbid: 'process_root',
        leaf: false,
        code: 'category',
        type: '',
        expanded: true
    },
	autoScroll:true,
    animate: false,
    rootVisible: true,
    collapsible: true,
    border: false,
    singleExpand: false,
    checked: false,
    selectId : '',
	onTreepanelItemClick: function(store, records){
		var me = this;

		me.selectId = records.data.id;
		me.up('flowmainmanage').flowtabmainpanel.reLoadNav(me.selectId);
		
		 //流程维护form
        if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_BASIC')){
        	var parentNode = records.parentNode;
			var floweditpanel = me.up('flowmainmanage').flowtabmainpanel.flowtabpanel.floweditpanel;
			floweditpanel.clearFormData();
			floweditpanel.reLoadData(store, records, parentNode);
		
		}
        //流程节点信息
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_POINT')){
			var flownotemainpanel = me.up('flowmainmanage').flowtabmainpanel.flowtabpanel.flownotemainpanel;
			if(flownotemainpanel){
				var flownotelist = flownotemainpanel.flownotelist;
				flownotelist.paramObj.processId = me.selectId;
				flownotelist.reloadData();
			}
		}
		//风险控制维护
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_RISK')){
			var flownotemainpanel = me.up('flowmainmanage').flowtabmainpanel.flowtabpanel.riskmeasuremainpanel;
			if(flownotemainpanel){
				var flowrisklist = flownotemainpanel.flowrisklist;
				flowrisklist.paramObj.processId = me.selectId;
				flowrisklist.reloadData();
			}
		}
		//流程图
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_FLOWCHART')){
			var grapheditor = me.up('flowmainmanage').flowtabmainpanel.flowtabpanel.grapheditor;
			if(grapheditor){
				var html = "<iframe src='"+__ctxPath+"/graph/findprocessgraph.f?viewType=grapheditor&processId="+me.selectId+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
				grapheditor.update(html);
			}
		}
		//缺陷信息
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_DEFECT')){
			var icmmydefectinfo = me.up('flowmainmanage').flowtabmainpanel.flowtabpanel.icmmydefectinfo;
			if(icmmydefectinfo){
				icmmydefectinfo.initParam({processId : me.selectId});
				icmmydefectinfo.reloadData();
			}

		}
		//控制矩阵
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_MATRIX')){
			var riskcontrolmatrix = me.up('flowmainmanage').flowtabmainpanel.flowtabpanel.riskcontrolmatrix;
			if(riskcontrolmatrix){
				riskcontrolmatrix.initParam({processId : me.selectId});
				riskcontrolmatrix.reloadData();
			}

		}
		//图形分析
		var graphrelaprocesspanel = me.up('flowmainmanage').flowtabmainpanel.flowtabpanel.graphrelaprocesspanel;
		if(graphrelaprocesspanel){
			graphrelaprocesspanel.initParam({processId : me.selectId});
			graphrelaprocesspanel.reloadData();
		}
		
	},
	refreshTree: function(){
		var me = this;
        me.getStore().load();
	},
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            listeners: {
                itemclick: me.onTreepanelItemClick/*,
                load: function (store, records) { //默认选择首节点
                    var rootNode = me.getRootNode();
                    if (rootNode.childNodes.length > 0) {
                        me.getSelectionModel().select(rootNode.firstChild);//默认选择首节点
                        me.up('flowmainmanage').flowtabmainpanel.reLoadNav(rootNode.firstChild);//加载首节点数据
                    }
                }*/
            },
            viewConfig: {
                listeners: {
                    itemcontextmenu: function (view, rec, node, index, e) {
                        e.stopEvent();
                        return false;
                    }
                }
            }

        });

        me.callParent(arguments);
    },
    //导航条点击导航调用的固定函数
    reloadNavigator : function(id){
    	var me = this;
		if(id.indexOf('my') != -1){
			var idstr = id.split('++');
			id = idstr[0];
		}if(id.indexOf('_') != -1){
			var idstr = id.split('_');
			id = idstr[0];
		}
		var flowtree = me;
		var rootNode = flowtree.getRootNode();
		var selectNode = flowtree.findNode(rootNode,id);
		flowtree.selectedNodeClick(selectNode);
	},
	selectedNodeClick:function(selectedNode){
    	var me = this;
    	me.getSelectionModel().select(selectedNode);
    	me.onTreepanelItemClick(me.getStore(),
    		{
    			parentNode : selectedNode.parentNode,
    			data : {
    					id : selectedNode.data.id
    					}
    		});
    },
    findNode:function(root,nodeid){
    	var me = this;
    	var childnodes = root.childNodes;//获取根节点的子节点
        for(var i=0; i < childnodes.length; i++){
           var node = childnodes[i];
           if(node.data.id == nodeid)
           {
             me.navNode = node;
           }
           if(node.hasChildNodes()){
        	 me.findNode(node,nodeid);//递归调用
           }
        }
        return me.navNode;
    }
    
});