

Ext.define('FHD.view.icm.icsystem.FlowTabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.flowtabpanel',
//    requires: [
//        'FHD.view.icm.icsystem.FlowNoteMainPanel',
//        'FHD.view.icm.icsystem.FlowEditPanel',
//        'FHD.view.icm.icsystem.RiskMeasureMainPanel',
//        'FHD.view.icm.statics.IcmMyDefectInfo',
//        'FHD.view.icm.statics.RiskControlMatrix',
//        'FHD.view.comm.graph.GraphRelaProcessPanel'
//    ],

    plain: true,
    
    //传递的参数对象
    paramObj:{},
    
    //添加监听事件
    listeners: {
    	beforetabchange : function(tabPanel, newCard, oldCard, eOpts){
    		//判断树中是否有选中的元素
    		var selectId = this.up('flowmainmanage').flowtree.selectId;   
    		if(selectId == ''){
    			Ext.Msg.alert("注意","请选择一个且唯一一个流程进行流程的节点和风险维护!");
    			return false;
    		}
    	},
    	tabchange: function (tabPanel, newCard, oldCard, eOpts) {
        	if(true){
                if (newCard.onClick) {
                    newCard.onClick();
                }
        	}
        }
    },
    /**
     * 设置激活的tab页签
     */
    setActiveItem:function(index){
    	me = this;
    	me.setActiveTab(index);
    },
    
    initParam:function(paramObj){
    	var me = this;
    	me.paramObj = paramObj;
    },
    
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	tabBar:{
        		style : 'border-right: 1px  #99bce8 solid;'
        	}
        });
        me.callParent(arguments);
        
        //流程维护form
        if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_BASIC')){
        	// 流程维护form
       		me.floweditpanel = Ext.create('FHD.view.icm.icsystem.FlowEditPanel',{title: '基本信息'});
			me.add(me.floweditpanel);
		}
        //流程节点信息
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_POINT')){
			var c1 = Ext.create("Ext.container.Container",{
				border:false,
        		title:'流程节点信息',
        		layout: 'fit',
            	onClick: function () {
            		if(!me.flownotemainpanel){
            			me.flownotemainpanel = Ext.create('FHD.view.icm.icsystem.FlowNoteMainPanel',{
            				border:false,
            				listeners: {
            					afterrender:function(){
            						//第一次实例化，刷新数据。保证只创建时刷新1次，并不是每次显示时都刷新
            						var selectId = me.up('flowtabmainpanel').up('flowmainmanage').flowtree.selectId;
            						me.flownotemainpanel.flownotelist.paramObj.processId = selectId;
									me.flownotemainpanel.flownotelist.reloadData();
            					}
            				}
            			});
            			this.add(me.flownotemainpanel);
            		}
            	}
        	});
			me.add(c1);
		}
		//风险控制维护
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_RISK')){
			var c2 = Ext.create("Ext.container.Container",{
				border:false,
        		title:'风险控制',
        		layout: 'fit',
            	onClick: function () {
            		if(!me.riskmeasuremainpanel){
            			me.riskmeasuremainpanel = Ext.create('FHD.view.icm.icsystem.RiskMeasureMainPanel',{
            				border:false,
            				listeners: {
            					afterrender:function(){
            						//第一次实例化，刷新数据。保证只创建时刷新1次，并不是每次显示时都刷新
            						var selectId = me.up('flowtabmainpanel').up('flowmainmanage').flowtree.selectId;
            						me.riskmeasuremainpanel.flowrisklist.paramObj.processId = selectId;
									me.riskmeasuremainpanel.flowrisklist.reloadData();
            					}
            				}
            			});
            			this.add(me.riskmeasuremainpanel);
            		}
            	}
        	});
			me.add(c2);
		}
		//流程图
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_FLOWCHART')){
			var c3 = Ext.create("Ext.container.Container",{
				border:false,
        		title:'流程图',
        		layout: 'fit',
            	onClick: function () {
            		if(!me.grapheditor){
            			me.grapheditor = Ext.create('Ext.panel.Panel',{
            				border:false,
            				html:"<iframe src='"+__ctxPath+"/graph/findprocessgraph.f?viewType=grapheditor' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>",
            				listeners: {
            					afterrender:function(){
            						//第一次实例化，刷新数据。保证只创建时刷新1次，并不是每次显示时都刷新
            						var selectId = me.up('flowtabmainpanel').up('flowmainmanage').flowtree.selectId;
									var html = "<iframe src='"+__ctxPath+"/graph/findprocessgraph.f?viewType=grapheditor&processId="+selectId+"' scrolling='no' frameBorder=0 height='100%' width='100%'></iframe>";
									me.grapheditor.update(html);
            					}
            				}
            			});
            			this.add(me.grapheditor);
            		}
            	}
        	});
			me.add(c3);
		}
		//缺陷信息
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_DEFECT')){
			var c4 = Ext.create("Ext.container.Container",{
				border:false,
        		title:'缺陷信息',
        		layout: 'fit',
            	onClick: function () {
            		if(!me.icmmydefectinfo){
            			me.icmmydefectinfo = Ext.create('FHD.view.icm.statics.IcmMyDefectInfo',{
            				border:false,
            				listeners: {
            					afterrender:function(){
            						//第一次实例化，刷新数据。保证只创建时刷新1次，并不是每次显示时都刷新
            						var selectId = me.up('flowtabmainpanel').up('flowmainmanage').flowtree.selectId;
									me.icmmydefectinfo.initParam({processId : selectId});
									me.icmmydefectinfo.reloadData();
            					}
            				}
            			});
            			this.add(me.icmmydefectinfo);
            		}
            	}
        	});
			me.add(c4);
		}
		//控制矩阵
		if($ifAllGranted('ROLE_ALL_ENV_ICDATAPROCESSBASE_MATRIX')){
			var c5 = Ext.create("Ext.container.Container",{
				border:false,
        		title:'控制矩阵',
        		layout: 'fit',
            	onClick: function () {
            		if(!me.riskcontrolmatrix){
            			me.riskcontrolmatrix = Ext.create('FHD.view.icm.statics.RiskControlMatrix',{
            				border:false,
            				listeners: {
            					afterrender:function(){
            						//第一次实例化，刷新数据。保证只创建时刷新1次，并不是每次显示时都刷新
            						var selectId = me.up('flowtabmainpanel').up('flowmainmanage').flowtree.selectId;
									me.riskcontrolmatrix.initParam({processId : selectId});
									me.riskcontrolmatrix.reloadData();
            					}
            				}
            			});
            			this.add(me.riskcontrolmatrix);
            		}
            	}
        	});
			me.add(c5);
		}
		//图形分析		
		var c6 = Ext.create("Ext.container.Container",{
			border:false,
    		title:'图形分析',
    		layout: 'fit',
        	onClick: function () {
        		if(!me.graphrelaprocesspanel){
        			me.graphrelaprocesspanel = Ext.create('FHD.view.comm.graph.GraphRelaProcessPanel',{
        				border:false,
        				listeners: {
        					afterrender:function(){
        						//第一次实例化，刷新数据。保证只创建时刷新1次，并不是每次显示时都刷新
        						var selectId = me.up('flowtabmainpanel').up('flowmainmanage').flowtree.selectId;
								me.graphrelaprocesspanel.initParam({processId : selectId});
								me.graphrelaprocesspanel.reloadData();
        					}
        				}
        			});
        			this.add(me.graphrelaprocesspanel);
        		}
        	}
    	});
		me.add(c6);
			
        me.getTabBar().insert(0,{xtype:'tbfill'});
    },
    reloadData : function() {
    	var me = this;
    }
});