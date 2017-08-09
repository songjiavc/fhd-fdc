/**
 * 
 * 风险整理树面板
 */

Ext.define('FHD.view.risk.assess.newAgainRiskTidy.NewAssessTree', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.newAssessTree',
   
    sets : function(typeId, type){
    	var me = this;
    	var types = '';
    	var extraParams = {
        		assessPlanId : me.riskTidyMan.businessId,
        		typeId : typeId,
        		type : type
        };

        //当点击树上节点时判断tab当前打开的页面刷新下面的grid
    	me.riskTidyMan.riskTidyTab.riskTidyGrid.store.proxy.extraParams = extraParams;
    	me.riskTidyMan.riskTidyTab.getActiveTab().store.load();

    	//alert(typeId + "---" + types);
    	//me.riskTidyMan.riskTidyTab.riskTidyCard.riskHeatMapPanel.initParams(types);
    	//me.riskTidyMan.riskTidyTab.riskTidyCard.riskHeatMapPanel.reloadData(typeId);
    	
    	//me.riskTidyMan.riskTidyTab.riskTidyCard.riskGroupCountPanel.initParams(types);
    	//me.riskTidyMan.riskTidyTab.riskTidyCard.riskGroupCountPanel.reloadData(typeId);
    },   
    reloadData:function(){
    	var me = this;
//    	me.orgTree.store.load();
//    	me.strategyTree.store.load();
//    	me.processTree.store.load();
    	me.riskTree.store.load();
    },         
                
    // 初始化方法
    initComponent: function() {
        var me = this; 
        var extraParams = me.extraParams;//{ids:'2,3'};
        var ids = extraParams.ids.split(',');
        var extraRiskParams = me.extraRiskParams;//{ids:'[{id:"abc",type:"re",icon:"abcicon"},{id:"efd",type:"rbs",icon:"edficon"}]'};
     	extraRiskParams.ids = ids;
        //  var orgTreeUrl = __ctxPath + '/potentialRiskEvent/getOrgTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
      	//  var strategyTreeUrl = __ctxPath + '/potentialRiskEvent/getStrategyMapTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
       	// var processTreeUrl = __ctxPath + '/potentialRiskEvent/getProcessTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
        var riskTreeUrl = __ctxPath + '/potentialRiskEvent/getRiskTreeRecordByEventIds?assessPlanId=' + me.assessPlanId;
       // var riskTreeUrl = __ctxPath + '/cmp/risk/getRiskTreeRecord?schm='+me.schm;
        
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
   			listeners: {
   				load : function(){
   					var tree = this;
   					//获取首个叶子节点
   					var lastNode = tree.getRootNode().firstChild;
   					if(lastNode != null){
   						tree.getSelectionModel().select(lastNode);//默认选择首节点
   						//模拟选中点击事件
   						me.sets(lastNode.data.id,'risk');
   					}
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
            width:220,
        	treeArr:[me.riskTree]
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
        	if(me.nav){
        		if(Ext.getCmp(me.riskTidyMan.winId)){
            		if(me.riskTidyMan.winId != me.riskTidyMan.id){
                		if(me.riskTidyMan.riskTidyTopPanel.collapsed == 'top'){
                    		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight() - 90 + 3);
                    	}else{
                    		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight() - 150 + 3);
                    	}
                	}else{
                		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight() - 32 + 3); //28
                	}
            	}else{
            		if(me.riskTidyMan.riskTidyTopPanel.collapsed == 'top'){
                		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.id).getHeight() - 90 + 30 + 3);
                	}else{
                		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.id).getHeight() - 150 + 30 + 3);
                	}
            	}
            	me.accordionTree.setWidth(me.getWidth());
        	}else{
        		if(Ext.getCmp(me.riskTidyMan.winId)){
            		if(me.riskTidyMan.winId != me.riskTidyMan.id){
                		if(me.riskTidyMan.riskTidyTopPanel.collapsed == 'top'){
                    		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight() - 90 + 3);
                    	}else{
                    		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight() - 150 + 3);
                    	}
                	}else{
                		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.winId).getHeight() - 32 + 3); //28
                	}
            	}else{
            		if(me.riskTidyMan.riskTidyTopPanel.collapsed == 'top'){
                		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.id).getHeight() - 90 + 30 + 3);
                	}else{
                		me.accordionTree.setHeight(Ext.getCmp(me.riskTidyMan.id).getHeight() - 150 + 30 + 88 + 3);
                	}
            	}
            	me.accordionTree.setWidth(me.getWidth());
        	}
    	});
    }

});