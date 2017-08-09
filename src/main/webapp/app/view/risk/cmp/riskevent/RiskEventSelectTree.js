Ext.define('FHD.view.risk.cmp.riskevent.RiskEventSelectTree', {
	extend : 'Ext.container.Container',
	alias : 'widget.riskeventselecttree',
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
	
	/**
	 * 我的文件夹树是否显示
	 */
	riskmyfoldertreevisable:false,
	riskmyfolderUrl:'/cmp/risk/getMyfolderRiskTreeRecord',
	riskmyfolderListUrl:'/cmp/risk/findmyfolderRiskEventByRiskId.f',
	
	/**
	 * 风险分类树是否显示
	 */
	riskcatalogtreevisable:false,
	riskcatalogListUrl:'/cmp/risk/findPotentialRiskByRiskId.f',
	
	/**
	 * 风险树是否显示
	 */
	risktreevisable:true,
	risktreeUrl:'/cmp/risk/getRiskTreeRecord',
	risktreeEventUrl:'/cmp/risk/findPotentialRiskEventByRiskId',
	
	/**
	 * 组织树是否显示
	 */
	orgtreevisable:true,
	orgtreeEventUrl:'/cmp/risk/findPotentialRiskEventByOrgId',
	
	/**
	 * 指标树是否显示
	 */
	kpitreevisable:true,
	kpitreeEventUrl:'/cmp/risk/findPotentialRiskEventByKpiId',
	
	/**
	 * 流程树是否显示
	 */
	processtreevisable:true,
	processtreeEventUrl:'/cmp/risk/findPotentialRiskEventByProcessId',
	
	reloadStore:function(url,extraParams){
		var me = this;
		//alert(extraParams.id);
		me.findParentBy(function(container, component){
			//获得指标列表的grid
			var candidategrid = container.candidategrid;
			//参数赋值
			candidategrid.store.proxy.url = url;
			candidategrid.store.proxy.extraParams = extraParams; 
			//重新加载grid中的数据
			candidategrid.store.load({
				callback: function(records, operation, success) {
					me.face.candidatelabel.setText('(' + records.length + ')');
				}
			});
			
		});
		
	},
	init:function(){
		var me = this;
		
		if(me.riskmyfoldertreevisable){
			
			//初始化参数
	    	var extraParams = {};
	    	extraParams.rbs = true;
	    	extraParams.canChecked = false;
	    	me.riskmyfoldertree = Ext.create('FHD.ux.TreePanel', {
	    		root:{
	    	        "id": "root",
	    	        "text": "我的文件夹",
	    	        "dbid": "myfolder",
	    	        "leaf": false,
	    	        "code": "myfolder",
	    	        "type": "myfolder",
	    	        "expanded": true,
	    	        'iconCls':'icon-ibm-new-group-view'	//样式
	    	    },
	    		rootVisible: true,
	    		url:__ctxPath + me.riskmyfolderUrl,
	    		title:'我的文件夹',
				iconCls: 'icon-table-key',
	           	extraParams:extraParams,
	   			listeners: {
	   				itemclick: function(t,r,i,o){
	   					var id = r.data.id;
	                	var url = __ctxPath + me.riskmyfolderListUrl;
	        			var extraParams = {id:id};
	                	me.reloadStore(url,extraParams);
	                }
	            }
	        });
			me.add(me.riskmyfoldertree);
		}

		/**
		 * 加载风险分类树
		 */
		if(me.riskcatalogtreevisable){
			
			//初始化参数
	    	var extraParams = {};
	    	extraParams.rbs = true;
	    	extraParams.canChecked = false;
	    	me.riskcatalogtree = Ext.create('FHD.ux.TreePanel', {
	    		root:{
	    	        "id": "root",
	    	        "text": "风险分类",
	    	        "dbid": "sm_root",
	    	        "leaf": false,
	    	        "code": "sm",
	    	        "type": "orgRisk",
	    	        "expanded": true,
	    	        'iconCls':'icon-ibm-icon-scorecards'	//样式
	    	    },
	    		rootVisible: true,
	    		url:__ctxPath + me.risktreeUrl,
	    		title:'风险分类',
				iconCls: 'icon-table-key',
	           	extraParams:extraParams,
	   			listeners: {
	   				itemclick: function(t,r,i,o){
	   					var id = r.data.id;
	                	var url = __ctxPath + me.riskcatalogListUrl;
	        			var extraParams = {id:id};
	                	me.reloadStore(url,extraParams);
	                }
	            }
	        });
			me.add(me.riskcatalogtree);
		}
		
		/**
		 * 加载风险树
		 */
		if(me.risktreevisable){
			//alert("schm:="+me.schm)
			//初始化参数
	    	var extraParams = {};
	    	extraParams.rbs = true;
	    	extraParams.canChecked = false;
	    	me.risktree = Ext.create('FHD.ux.TreePanel', {
	    		root:{
	    	        "id": "root",
	    	        "text": "风险",
	    	        "dbid": "sm_root",
	    	        "leaf": false,
	    	        "code": "sm",
	    	        "type": "orgRisk",
	    	        "expanded": true,
	    	        'iconCls':'icon-ibm-icon-scorecards'	//样式
	    	    },
	    		rootVisible: true,
	    		//吉志强添加分库标识2017年3月30日17:03:30
	    		url:__ctxPath + me.risktreeUrl+"?schm="+me.schm,
	    		title:'风险',
				iconCls: 'icon-ibm-icon-scorecards',
	           	extraParams:extraParams,
	   			listeners: {
	   				itemclick: function(t,r,i,o){
	   					var id = r.data.id;
	                	var url = __ctxPath + me.risktreeEventUrl;
	        			var extraParams = {id:id,schm:me.typeId};
	                	me.reloadStore(url,extraParams);
	                }
	            }
	        });
			me.add(me.risktree);
		}
		
		/**
		 * 加载组织树
		 */
		if(me.orgtreevisable){
			
			me.orgtree = Ext.create('FHD.ux.org.DeptTree',{
	        	title:'组织',
	        	iconCls : 'icon-ibm-new-group-view',
	        	subCompany: false,
	        	companyOnly: false,
	        	checkable:false,
	        	border:false,
	        	schm:me.typeId,
	        	rootVisible: true,
	        	myexpand:true,	//默认不展开树，多个签展开出错
	        	listeners: {
	   				itemclick: function(t,r,i,o){
	                	var id = r.data.id;
	                	var url = __ctxPath + me.orgtreeEventUrl;
	        			var extraParams = {id:id,schm:me.typeId};
	                	me.reloadStore(url,extraParams);
	                }
	            }
		    });
			me.add(me.orgtree);
		}
		
		
		/**
		 * 加载指标树
		 */
		if(me.kpitreevisable){
			me.kpitree = Ext.create('FHD.view.kpi.cmp.StrategyMapTree', {
				iconCls : 'icon-strategy',//icon-flag-red
				title : '目标',
				listeners: {
	   				itemclick: function(t,r,i,o){
	                	var id = r.data.id;
	                	var url = __ctxPath + me.kpitreeEventUrl;
	        			var extraParams = {id:id};
	                	me.reloadStore(url,extraParams);
	                }
	            }
			});
			me.add(me.kpitree);
		}
		
		/**
		 * 加载流程树 
		 */
		if(me.processtreevisable){
			me.processtree = Ext.create('FHD.ux.process.ProcessTree', {
	        	border:false,
	        	title:'流程',
	        	iconCls : 'icon-ibm-icon-metrictypes',
	        	processTreeIcon:'',
	        	processTitle:'',
	        	myexpand : true,
				extraParams : {canChecked : false}
			});
			me.processtree.processTree.on('itemclick',function(t,r,i,o){
            	var id = r.data.id;
            	var url = __ctxPath + me.processtreeEventUrl;
    			var extraParams = {id:id};
            	me.reloadStore(url,extraParams);
	        });
			me.add(me.processtree);
		}

	},
	
	initComponent : function() {
		
		var me = this;
		Ext.applyIf(me, {
			
		});
		
		me.callParent(arguments);
		me.init();
	}
});