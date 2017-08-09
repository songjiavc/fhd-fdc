Ext.define('FHD.view.risk.riskidentify.tidy.IdentifyTidyTree', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.identifyTidyTree',
    
    currentNodeClick: function (me) {
    	
        var selectedNode;
        var nodeItems = me.getSelectionModel().selected.items;
        if (nodeItems.length > 0) {
            selectedNode = nodeItems[0];
            me.currentNode = selectedNode;
        }
        if (selectedNode == null) {
	        var firstNode = me.getRootNode().firstChild;
	        if(null!=firstNode){
	        	//默认选中首节点
	            me.getSelectionModel().select(firstNode);
	            me.currentNode = firstNode;
	            this.sets('root', 'risk');
            }
        } else {
        	//当前选中节点
        	me.currentNode = selectedNode;
        }
    },
    
    sets : function(typeId, type){
    	var me = this;
    	var types = '';
    	
    	var extraParams = {
        		assessPlanId : me.riskTidyMan.businessId,
        		typeId : typeId,
        		type : type
        };
    	
    	me.riskTidyMan.riskTidyTab.store.proxy.extraParams = extraParams;
    	me.riskTidyMan.riskTidyTab.store.load();
    	
    	if(type == 'risk'){
    		types = 'risk';
    	}else if(type == 'dept'){
    		types = 'org';
    	}else if(type == 'strategyMap'){
    		types = 'sm';
    	}else if(type == 'processure'){
    		types = 'process';
    	}
    },   
    reloadData:function(){
    	var me = this;
    	me.orgTree.store.load();
    	me.riskTree.store.load();
    },         
                
    // 初始化方法
    initComponent: function() {
        var me = this; 
        var extraParams = me.extraParams;
        var ids = extraParams.ids.split(',');
        var extraRiskParams = me.extraRiskParams;
        var orgTreeUrl = __ctxPath + '/potentialRiskEvent/getOrgTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        var strategyTreeUrl = __ctxPath + '/potentialRiskEvent/getStrategyMapTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        var processTreeUrl = __ctxPath + '/potentialRiskEvent/getProcessTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        //var riskTreeUrl = __ctxPath + '/potentialRiskEvent/getRiskTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        var riskTreeUrl = __ctxPath + '/potentialRiskEvent/getRiskTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        me.orgTree = Ext.create("FHD.ux.TreePanel", {
        	treeTitle:'组织',
        	treeIconCls : 'icon-ibm-new-group-view',
    		rootVisible: true,
    		title : '',
    		border:false,
    		method : 'POST',
    		root: {
    	        "id": "root",
    	        "text": "组织",
    	        "expanded": true,
    	        'iconCls':'icon-ibm-new-group-view'	//样式
    	    },
           	extraParams:extraParams,
   			url: orgTreeUrl,
   			onClick:function(){
           		me.accordionTree.setTitle('');
           		me.accordionTree.setIconCls('');
			},
   			listeners: {
                itemclick: function(tablepanel, record, item, index, e, options){
                	me.sets(record.data.id, 'dept');
                }
            }
        });
        
        me.riskTree = Ext.create("FHD.ux.TreePanel", {
      		treeTitle:'风险',
        	treeIconCls : 'icon-ibm-icon-scorecards',
    		rootVisible: true,
    		border:false,
    		root: {
    	        "id": "root",
    	        "text": "风险",
    	        "dbid": "sm_root",
    	        "leaf": false,
    	        "code": "sm",
    	        "type": "orgRisk",
    	        "expanded": true,
    	        'iconCls':'icon-ibm-icon-scorecards'	//样式
    	    },
           	extraParams:extraRiskParams,
           	url:riskTreeUrl,
           	onClick:function(){
           		me.accordionTree.setTitle('');
           		me.accordionTree.setIconCls('');
			},
   			listeners: {
   				load : function(){
   					me.riskTidyMan.assessTree.currentNodeClick(this);
   				},
                itemclick: function(tablepanel, record, item, index, e, options){
                	me.sets(record.data.id, 'risk');
                }
            }
        });
        
      me.accordionTree = Ext.create("FHD.ux.layout.AccordionTree",{
        	border:false,
        	collapsible : false,
        	title: '',
        	hiddenTitleBar : true,
        	//treeArr:[me.riskTree,me.orgTree]
        	treeArr:[me.riskTree]
        });
        
        Ext.apply(me, {
    		split: true,
    		layout: 'fit',
    		collapsible : true,
    		collapsed : me.collapseds,
        	region: 'west',
			border:false,
        	items : [me.accordionTree]
        });

        me.callParent(arguments);
        
    }

});