/**
 * 
 * 风险整理树面板
 */

Ext.define('FHD.view.risk.assess.utils.AssessTree', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.assessTree',
    
    sets : function(typeId, type){
    	var me = this;
    	var types = '';
    	var extraParams = {
        		assessPlanId : me.riskTidyMan.businessId,
        		typeId : typeId,
        		type : type
        	};
    	
    	me.riskTidyMan.riskTidyCard.riskTidyGrid.store.proxy.extraParams = extraParams;
    	me.riskTidyMan.riskTidyCard.riskTidyGrid.store.load();
    },   
    reloadData:function(){
    	var me = this;
    	me.orgTree.store.load();
    	me.strategyTree.store.load();
    	me.processTree.store.load();
    	me.riskTree.store.load();
    },         
                
    // 初始化方法
    initComponent: function() {
        var me = this; 
        var extraParams = me.extraParams;//{ids:'2,3'};
        var ids = extraParams.ids.split(',');
        var extraRiskParams = me.extraRiskParams;//{ids:'[{id:"abc",type:"re",icon:"abcicon"},{id:"efd",type:"rbs",icon:"edficon"}]'};
        var orgTreeUrl = __ctxPath + '/potentialRiskEvent/getOrgTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        var strategyTreeUrl = __ctxPath + '/potentialRiskEvent/getStrategyMapTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        var processTreeUrl = __ctxPath + '/potentialRiskEvent/getProcessTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        var riskTreeUrl = __ctxPath + '/potentialRiskEvent/getRiskTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        
        me.orgTree = Ext.create("FHD.ux.TreePanel", {
        	treeTitle:'组织',
        	treeIconCls : 'icon-ibm-new-group-view',
    		rootVisible: true,
    		title : '',
    		border:true,
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
        me.strategyTree = Ext.create("FHD.ux.TreePanel", {
        	treeTitle:'目标',
        	treeIconCls : 'icon-strategy',
    		rootVisible: true,
    		border:true,
    		root: {
    	        "id": "root",
    	        "text": "目标",
    	        "expanded": true,
    	        'iconCls':'icon-strategy'	//样式
    	    },
           	extraParams:extraParams,
   			url: strategyTreeUrl,
   			onClick:function(){
           		me.accordionTree.setTitle('');
           		me.accordionTree.setIconCls('');
			},
   			listeners: {
                itemclick: function(tablepanel, record, item, index, e, options){
                	me.sets(record.data.id, 'strategyMap');
                }
            }
        });
        
        me.processTree = Ext.create("FHD.ux.TreePanel", {
        	treeTitle:'流程',
        	treeIconCls : 'icon-ibm-icon-metrictypes',
    		rootVisible: true,
    		border:true,
    		root: {
    	        "id": "root",
    	        "text": "流程",
    	        "dbid": "p_root",
    	        "leaf": false,
    	        "code": "sm",
    	        "type": "orgRisk",
    	        "expanded": true,
    	        'iconCls':'icon-ibm-icon-metrictypes'	//样式
    	    },
           	extraParams:extraParams,
   			url: processTreeUrl,
   			onClick:function(){
           		me.accordionTree.setTitle('');
           		me.accordionTree.setIconCls('');
			},
   			listeners: {
                itemclick: function(tablepanel, record, item, index, e, options){
                	me.sets(record.data.id, 'processure');
                }
            }
        });
        
        me.riskTree = Ext.create("FHD.ux.TreePanel", {
      		treeTitle:'风险',
        	treeIconCls : 'icon-ibm-icon-scorecards',
    		rootVisible: true,
    		
    		border:true,
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
            width:220,
        	treeArr:[me.riskTree,me.orgTree,me.strategyTree,me.processTree]
        });
        
        Ext.apply(me, {
    		split: true,
    		collapsible : true,
    		collapsed : me.collapseds,
        	region: 'west',
			border:false,
        	items : [me.accordionTree]
        });

        me.callParent(arguments);
       
        me.on('resize',function(p){
        	if(me.riskTidyMan.winId != me.riskTidyMan.id){
        		if(me.riskTidyMan.riskTidyTopPanel.collapsed == 'top'){
            		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight() - 90);
            	}else{
            		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight() - 150);
            	}
        	}else{
        		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight()-28)
        	}
        	
        	me.accordionTree.setWidth(me.getWidth());
    	});
    }

});