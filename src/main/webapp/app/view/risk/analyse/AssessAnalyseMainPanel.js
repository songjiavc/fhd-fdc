/**
 * 评估结果分析主面板
 * @author ZJ
 */
Ext.define('FHD.view.risk.analyse.AssessAnalyseMainPanel', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.assessanalysemainpanel',
    
    nodeId:null,		//左侧选择的树节点id
    nodeType:null,	//risk,org,strategy,process
    isload:true,	//是否点击页签时页签内部数据重新加载，默认是true,但是当添加风险分类是，不希望数据加载，此时使用
    /**
     * 按id在树上查找节点
     */
    findNode:function(root,nodeid){
    	var me = this;
    	var navNode = null;
    	var childnodes = root.childNodes;
        for(var i=0; i < childnodes.length; i++){
           var node = childnodes[i];
           if(node.data.id == nodeid)
           {
             navNode = node;
             break;
           }
           if(node.hasChildNodes()){
        	 navNode = me.findNode(node,nodeid);//递归调用
        	 if(navNode!=null){
        		 break;
        	 }
           }
        }
        return navNode;
    },
    /**
     * 选中左侧树节点，并设置导航
     * type:risk,org,strategy,process
     */
    changeNavigation:function(type){
    	var me = this;
 
    	var navType = type;	//用作导航的类型
    	var itree = me.riskTree;		//树实例
    	var icontainer = me.containers; //导航container实例
    	if(type=='org'){
    		itree = me.orgTree;
    		icontainer = me.orgContainer;
    	}else if(type=='strategy'){
    		itree = me.strategyTree;
    		icontainer = me.strategyContainer;
    		navType = 'sm';
    	}else if(type=='process'){
    		itree = me.processTree;
    		icontainer = me.processContainer;
    	}else{
    	
    	}
    	
    	//1.选择组织树默认节点
		var selectedNode;
        var nodeItems = itree.getSelectionModel().selected.items;
        if (nodeItems.length > 0) {
            selectedNode = nodeItems[0];
        }
        //没有选中节点，默认选中第一个节点
        if (selectedNode == null) {	
            var firstNode = itree.getRootNode().firstChild;
            if(null!=firstNode){
            	itree.getSelectionModel().select(firstNode);
            	selectedNode = firstNode;
            }
        }
		//2更改右侧导航
        var id = selectedNode.data.id;
		me.navigationBar.renderHtml(icontainer.id + 'DIV', id, '', navType, itree.id);
		
		//3.保存当前节点状态
		me.nodeId = id;
		me.nodeType = type;
    },
    /**
     * 创建风险事件添加容器,id没有值为添加，有值为修改
     */
    showRiskEventAddContainer:function(id,parentId,name){
    	var me = this;
    	
       	if(!me.riskEventAddContainer){    		
    		//风险事件基本信息
	        me.riskEventAddForm = Ext.create('FHD.view.risk.analyse.AssessRiskEventAddForm',{
				face:me,
	        	title:'基本信息',
				height:FHD.getCenterPanelHeight()-47
			});
    		me.riskEventTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
            	items:[me.riskEventAddForm]
            });
    		me.riskEventAddContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer',{
    			border:false,
             	tabpanel:me.riskEventTabPanel,
            	flex:1
    		});
    		me.cardpanel.add(me.riskEventAddContainer);
    	}

    	//切换标签
    	me.cardpanel.setActiveItem(me.riskEventAddContainer);
    	if(id){
    		me.riskEventAddForm.reloadData(id);
    	}else{
    		me.riskEventAddForm.resetData(me.nodeType,me.nodeId);
    	}
    	
    	//设置导航
    	var type =me.nodeType;
       	var icontainer = null;
       	var itree = null;
       	if(type=='risk'){
    		icontainer = me.containers;
    		itree = me.riskTree;
    	}else if(type=='org'){
    		icontainer = me.orgContainer;
    		itree = me.orgTree;
    	}else if(type=='strategy'){
    		icontainer = me.strategyContainer;
    		itree = me.strategyTree;
    		type = 'sm';
    	}else if(type=='process'){
    		icontainer = me.processContainer;
    		itree = me.processTree;
    	}else{
    		
    	}
    	if(id){
       		if(type=='risk'){	//风险事件重新按所属风险分类导航，其他直接在后面写名称
       			me.navigationBar.renderHtml(me.riskEventAddContainer.id + 'DIV', parentId, name, type, itree.id);
       		}else{
       	       	me.navigationBar.renderHtml(me.riskEventAddContainer.id + 'DIV', me.nodeId, name, type, itree.id);
       		}
       	}else{
        	me.navigationBar.renderHtml(me.riskEventAddContainer.id + 'DIV', me.nodeId, '添加风险事件', type, itree.id);
       	}
    	//设置返回的container
    	me.riskEventAddForm.riskEventGrid = icontainer;
    	me.riskEventAddForm.loadConainer = me.riskEventGrid;
    	me.riskEventAddForm.linkcmpurl = itree.id;
    },
    /**
     * 创建风险事件查看容器
     */
    showRiskEventDetailContainer:function(eventId,parentId,name){
    	var me = this;
    	
       	if(!me.riskEventDetailContainer){    		
    		//风险事件基本信息
	        me.riskEventDetailForm =  Ext.create('FHD.view.risk.analyse.AssessRiskEventDetail',{
	        	title:'基本信息',
	        	face:me,
	        	border:false,
	        	autoHeight : true
	        });
	        //图表分析
	        me.riskTrendLinePanel = Ext.create('FHD.view.risk.cmp.chart.RiskTrendLinePanel',{
	        	title : '图表分析',
	        	type : 'risk',
	        	border:false,
                listeners : {
                	afterlayout : function(){
                		me.riskTrendLinePanel.reloadData(eventId);
                	}
                }
	        });
	        //风险事件历史记录
			me.riskEventHistoryGrid =  Ext.create('FHD.view.risk.risk.RiskHistoryGrid',{
				title:'历史记录',
	        	type:'riskevent',
	        	border:false,
	        	height:FHD.getCenterPanelHeight()-47
	        });
	        
    		me.riskEventTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
            	items:[me.riskEventDetailForm,me.riskTrendLinePanel,me.riskEventHistoryGrid]
            });
    		me.riskEventDetailContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer',{
    			border:false,
             	tabpanel:me.riskEventTabPanel,
            	flex:1
    		});
    		me.cardpanel.add(me.riskEventDetailContainer);
    	}
       	
       	//切换标签
       	me.cardpanel.setActiveItem(me.riskEventDetailContainer);
        //设置导航
       	var type =me.nodeType;
       	var icontainer = null;
       	var itree = null;
       	if(type=='risk'){
    		icontainer = me.containers;
    		itree = me.riskTree;
    	}else if(type=='org'){
    		icontainer = me.orgContainer;
    		itree = me.orgTree;
    	}else if(type=='strategy'){
    		icontainer = me.strategyContainer;
    		itree = me.strategyTree;
    		type = 'sm';
    	}else if(type=='process'){
    		icontainer = me.processContainer;
    		itree = me.processTree;
    	}else{
    		
    	}
    	if(type=='risk'){	//风险事件重新按所属风险分类导航，其他直接在后面写名称
       		
       		//左侧树定位到当前节点,全部展开
       		me.riskTree.expandAll(function(){
       			me.navigationBar.renderHtml(me.riskEventDetailContainer.id + 'DIV', parentId, name, type, itree.id);
       		});
       	}else{
       		me.navigationBar.renderHtml(me.riskEventDetailContainer.id + 'DIV', me.nodeId, name, type, itree.id);
       	}
    	//设置返回的container
    	me.riskEventDetailForm.riskEventGrid = icontainer;
    	me.riskEventDetailForm.loadConainer = me.riskEventGrid;
    	me.riskEventDetailForm.linkcmpurl = itree.id;
       	//初始化数据
       	me.riskEventDetailForm.reloadData(eventId);
       	me.riskEventHistoryGrid.reloadData(eventId);
       	me.riskTrendLinePanel.reloadData(eventId);
    },
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        
        //风险树
        me.riskTreeContainer =  Ext.create('Ext.container.Container',{
        	treeTitle:'风险',
        	treeIconCls : 'icon-ibm-icon-scorecards',
        	layout:'fit',
        	onClick:function(){
        		//2.加载右侧tab
        		me.cardpanel.setActiveItem(me.containers);
        		//3.设置导航
        		me.changeNavigation('risk');
        	}
        });
        //初始化风险树
        me.riskTree = Ext.create('FHD.view.risk.cmp.RiskTreePanel',{
        	border:false,
        	face:me,
        	rbs:true,
        	showLight : true,
            reloadNavigator:function(id,name){
		    	var nid = id.split('_')[0];
		    	//将导航节点在节点上选中
		    	var rootNode = me.riskTree.getRootNode();//得到根节点
				var selectNode = me.findNode(rootNode,nid);
				me.riskTree.getSelectionModel().select(selectNode);
				//保存当前节点状态
    			me.nodeId = id;
    			me.nodeType = 'risk';
		    	me.navigationBar.renderHtml(me.containers.id + 'DIV', id, '', 'risk',me.riskTree.id);
				me.cardpanel.setActiveItem(me.containers);
		        var tab = me.tabpanel;
		        var activeTab = tab.getActiveTab();
		        if(activeTab.id == me.riskBasicFormViewContainer.id){
		        	//1 请求风险详细信息
		        	me.riskBasicFormView.reloadData(id);
		        }else if(activeTab.id == me.riskEventGridContainer.id){
		        	//2 请求风险事件列表
		        	me.riskEventGrid.reloadData('risk',id);
		        }else if(activeTab.id == me.riskHistoryGridContainer.id){
		        	//3 请求风险历史记录
		        	me.riskHistoryGrid.reloadData(id);
		        }else if(activeTab.id == me.assessAnalyseContainer.id){
		        	//4 请求风险编辑信息
		        	me.assessAnalyseCardPanel.reloadData(id);
		        }
		    },
        	listeners: {	
                load : function(){
        			//设置导航
        			me.changeNavigation('risk');
        			me.assessAnalyseCardPanel.reloadData(me.nodeId);
        		},
   				//树单击事件
                itemclick : function (tablepanel, record, item, index, e, options) {
                	var id = record.data.id;
                	 //根节点没有操作
			        me.cardpanel.setActiveItem(me.containers);
					//保存当前节点状态
	    			me.nodeId = id;
	    			me.nodeType = 'risk';
			    	me.navigationBar.renderHtml(me.containers.id + 'DIV', id, '', 'risk',me.riskTree.id);
			        var tab = me.tabpanel;
			        var activeTab = tab.getActiveTab();
			        if(activeTab.id == me.riskBasicFormViewContainer.id){
			        	//1 请求风险详细信息
			        	me.riskBasicFormView.reloadData(id);
			        }else if(activeTab.id == me.riskEventGridContainer.id){
			        	//2 请求风险事件列表
			        	me.riskEventGrid.reloadData('risk',id);
			        }else if(activeTab.id == me.riskHistoryGridContainer.id){
			        	//3 请求风险历史记录
			        	me.riskHistoryGrid.reloadData(id);
			        }else if(activeTab.id == me.assessAnalyseContainer.id){
			        	//4 请求风险编辑信息
			        	me.assessAnalyseCardPanel.reloadData(id);
			        }
                }
            }
        });
        me.riskTreeContainer.add(me.riskTree);
        me.riskTreeContainer.doLayout();
        
        //组织树
        var orgTreeContainer =  Ext.create('Ext.container.Container',{
        	treeTitle:'组织',
        	treeIconCls : 'icon-ibm-new-group-view',
        	layout:'fit',
        	onClick:function(){
    			//1.加载右侧card
        		if(!me.orgContainer){
                    //风险事件列表页
                    var orgRiskEventGridContainer =  Ext.create('Ext.container.Container',{
                    	layout:'fit',
                    	title:'风险列表',
                    	listeners:{
            	        	show:function(){
            	        		if(!me.orgRiskEventGrid){
            	        			me.orgRiskEventGrid =  Ext.create('FHD.view.risk.risk.RiskEventGrid',{
            	        				type:'org',
            	        				face:me,
            	                    	border:false,
            	                    	height:FHD.getCenterPanelHeight()-47
            	                    });
            	            		this.add(me.orgRiskEventGrid);
            	            		this.doLayout();
            	        		}
            	        		//根据左侧选中节点，初始化数据
            	        		if(me.nodeId != ''){
            	        			me.orgRiskEventGrid.reloadData('org',me.nodeId);
            	        		}
            	        	}
                    	}
                    });
            		
                    //历史记录页
                    var orgRiskHistoryGridContainer =  Ext.create('Ext.container.Container',{
                    	layout:'fit',
                    	title:'历史记录',
                    	listeners:{
            	        	show:function(){
            	        		if(!me.orgRiskHistoryGrid){
            	        			me.orgRiskHistoryGrid =  Ext.create('FHD.view.risk.risk.RiskHistoryGrid',{
            	        	        	face:me,
            	        	        	type:'org',
            	        	        	border:false,
            	        	        	callback : function(data){
            	        	        		me.orgTree.getStore().load();
            	        	        	}
            	        	        });
            	            		this.add(me.orgRiskHistoryGrid);
            	            		this.doLayout();
            	        		}
            	        		//根据左侧选中节点，初始化数据
            	        		if(me.nodeId != ''){
            	        			me.orgRiskHistoryGrid.reloadData(me.nodeId);
            	        		}
            	        	}
                    	}
                    });
            		//图表分析
				    me.orgassessAnalyseContainer =  Ext.create('Ext.container.Container',{
			        	title:'图表分析',
			        	layout : 'fit',
			        	listeners:{
				        	show:function(){
				        		if(!me.orgassessAnalyseCardPanel){
				        			me.orgassessAnalyseCardPanel =  Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel',{
				                    	face:me,
				                    	type : 'org',
				                    	border:false
				                    });
				            		this.add(me.orgassessAnalyseCardPanel);
				            		this.doLayout();
				        		}
				        		if(me.nodeId != ''){
				        			me.orgassessAnalyseCardPanel.reloadData(me.nodeId);
				        		}
				        	}
			        	}
			        });
			        me.orgassessAnalyseCardPanel =  Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel',{
                    	face:me,
                    	type : 'org',
                    	border:false
                    });
			        me.orgassessAnalyseContainer.add(me.orgassessAnalyseCardPanel);
            		me.orgassessAnalyseContainer.doLayout();
                    me.orgTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
                    	items:[me.orgassessAnalyseContainer,orgRiskEventGridContainer,orgRiskHistoryGridContainer]
                    });
            		me.orgContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer',{
            			border:false,
                     	tabpanel:me.orgTabPanel,
                    	flex:1
            		});
            		me.cardpanel.add(me.orgContainer);
        		}
        		me.cardpanel.setActiveItem(me.orgContainer);
        		
        		//2.加载左侧树
        		if(!me.orgTree){
                    me.orgTree = Ext.create('FHD.ux.org.DeptTree',{
                    	showLight:true,
                    	subCompany: false,
                    	companyOnly: false,
                    	checkable:false,
                    	border:false,
                    	face:me,
                    	rootVisible: true,
                		reloadNavigator:function(navid,name){
	            	    	var id = navid.split('_')[0];
	            	    	//将导航节点在节点上选中
	            	    	var rootNode = me.orgTree.getRootNode();//得到根节点
	            			var selectNode = me.findNode(rootNode,id);
	            			me.orgTree.getSelectionModel().select(selectNode);
	            			
	            			//设置导航
	            	        me.navigationBar.renderHtml(me.orgContainer.id + 'DIV', id, '', 'org',me.orgTree.id);
	            	        //保存当前节点状态
	            			me.nodeId = id;
	            			me.nodeType = 'org';
	            			me.cardpanel.setActiveItem(me.orgContainer);
	            	        //初始化右侧风险事件列表数据
	            	        var tab = me.orgTabPanel;
	            	        var activeTab = tab.getActiveTab();
	            	        if(activeTab.id == orgRiskEventGridContainer.id){
	            	        	//1 请求风险事件列表
	            	        	me.orgRiskEventGrid.reloadData('org',id);
	            	        }else if(activeTab.id == orgRiskHistoryGridContainer.id){
	              	        	//2请求历史记录
	            	        	me.orgRiskHistoryGrid.reloadData(id);
	            	        }else if(activeTab.id == me.orgassessAnalyseContainer.id){
	            	        	me.orgassessAnalyseCardPanel.reloadData(id);
	            	        }
	            		},
                    	listeners:{
                    		afteritemexpand:function(){
                    			//设置导航
                    			me.changeNavigation('org');
                    			me.orgassessAnalyseCardPanel.reloadData(me.nodeId);
                    		},
                    		itemclick: function(tablepanel, record, item, index, e, options){

                    	    	var id = record.data.id;
                    	    	
                    	    	/**
                    	         * 切换标签
                    	         */
                    	        me.cardpanel.setActiveItem(me.orgContainer);
                    	    	//设置导航
                    	        me.navigationBar.renderHtml(me.orgContainer.id + 'DIV', id, '', 'org',me.orgTree.id);
                    	        //保存当前节点状态
                    			me.nodeId = id;
                    			me.nodeType = 'org';
                    			
                    	        //初始化右侧风险事件列表数据
                    	        var tab = me.orgTabPanel;
                    	        var activeTab = tab.getActiveTab();
                    	        if(activeTab.id == orgRiskEventGridContainer.id){
                    	        	//1 请求风险事件列表
                    	        	me.orgRiskEventGrid.reloadData('org',id);
                    	        }else if(activeTab.id == orgRiskHistoryGridContainer.id){
                      	        	//2请求历史记录
                    	        	me.orgRiskHistoryGrid.reloadData(id);
                    	        }else if(activeTab.id == me.orgassessAnalyseContainer.id){
		            	        	me.orgassessAnalyseCardPanel.reloadData(id);
		            	        }
                    		}
                    	}
            	    });
               		this.add(me.orgTree);
            		this.doLayout();
        		}else{
            		//设置导航
            		me.changeNavigation('org');
        		}
        	}
        });

        //目标树
        var strategyTreeContainer =  Ext.create('Ext.container.Container',{
        	treeTitle:'目标',
        	treeIconCls : 'icon-strategy',
        	layout:'fit',
        	onClick:function(){
    			//1.加载右侧card
        		if(!me.strategyContainer){
                    //风险事件列表页
                    var strategyRiskEventGridContainer =  Ext.create('Ext.container.Container',{
                    	layout:'fit',
                    	title:'风险列表',
                    	listeners:{
            	        	show:function(){
            	        		if(!me.strategyRiskEventGrid){
            	        			me.strategyRiskEventGrid =  Ext.create('FHD.view.risk.risk.RiskEventGrid',{
            	        				type:'strategy',
            	        				face:me,
            	                    	border:false,
            	                    	height:FHD.getCenterPanelHeight()-47
            	                    });
            	            		this.add(me.strategyRiskEventGrid);
            	            		this.doLayout();
            	        		}
            	        		//根据左侧选中节点，初始化数据
            	        		if(me.nodeId != ''){
            	        			me.strategyRiskEventGrid.reloadData('strategy',me.nodeId);
            	        		}
            	        	}
                    	}
                    });
            		
                    //历史记录页
                    var strategyRiskHistoryGridContainer =  Ext.create('Ext.container.Container',{
                    	layout:'fit',
                    	title:'历史记录',
                    	listeners:{
            	        	show:function(){
            	        		if(!me.strategyRiskHistoryGrid){
            	        			me.strategyRiskHistoryGrid =  Ext.create('FHD.view.risk.risk.RiskHistoryGrid',{
            	        	        	face:me,
            	        	        	type:'strategy',
            	        	        	border:false,
            	        	        	callback : function(data){
            	        	        		me.strategyTree.getStore().load();
            	        	        	}
            	        	        });
            	            		this.add(me.strategyRiskHistoryGrid);
            	            		this.doLayout();
            	        		}
            	        		//根据左侧选中节点，初始化数据
            	        		if(me.nodeId != ''){
            	        			me.strategyRiskHistoryGrid.reloadData(me.nodeId);
            	        		}
            	        	}
                    	}
                    });
                    //图表分析
				    me.smassessAnalyseContainer =  Ext.create('Ext.container.Container',{
			        	title:'图表分析',
			        	layout : 'fit',
			        	listeners:{
				        	show:function(){
				        		if(!me.smassessAnalyseCardPanel){
				        			me.smassessAnalyseCardPanel =  Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel',{
				                    	face:me,
				                    	type : 'sm',
				                    	border:false
				                    });
				            		this.add(me.smassessAnalyseCardPanel);
				            		this.doLayout();
				        		}
				        		if(me.nodeId != ''){
				        			me.smassessAnalyseCardPanel.reloadData(me.nodeId);
				        		}
				        	}
			        	}
			        });
			        me.smassessAnalyseCardPanel =  Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel',{
                    	face:me,
                    	type : 'sm',
                    	border:false
                    });
			        me.smassessAnalyseContainer.add(me.smassessAnalyseCardPanel);
            		me.smassessAnalyseContainer.doLayout();
            		
                    me.strategyTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
                    	items:[me.smassessAnalyseContainer,strategyRiskEventGridContainer,strategyRiskHistoryGridContainer]
                    });
            		me.strategyContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer',{
            			border:false,
                     	tabpanel:me.strategyTabPanel,
                    	flex:1
            		});
            		me.cardpanel.add(me.strategyContainer);
        		}
        		me.cardpanel.setActiveItem(me.strategyContainer);
        		
        		//2.加载左侧树
        		if(!me.strategyTree){
        	        me.strategyTree = Ext.create('FHD.view.kpi.cmp.StrategyMapTree',{
        	        	rootVisible : true,
        	        	collapsible:false,
        	        	showLight:true,
        	        	border:false,
        	        	reloadNavigator:function(navid,name){
	            	    	var id = navid.split('_')[0];
	            	    	//将导航节点在节点上选中
	            	    	var rootNode = me.strategyTree.getRootNode();//得到根节点
	            			var selectNode = me.findNode(rootNode,id);
	            			me.strategyTree.getSelectionModel().select(selectNode);
	            			me.cardpanel.setActiveItem(me.strategyContainer);
	            			//设置导航
                	        me.navigationBar.renderHtml(me.strategyContainer.id + 'DIV', id, '', 'sm',me.strategyTree.id);
                	        //保存当前节点状态
                			me.nodeId = id;
                			me.nodeType = 'strategy';
                			
                	        //初始化右侧风险事件列表数据
                	        var tab = me.strategyTabPanel;
                	        var activeTab = tab.getActiveTab();
                	        if(activeTab.id == strategyRiskEventGridContainer.id){
                	        	//1 请求风险事件列表
                	        	me.strategyRiskEventGrid.reloadData('strategy',id);
                	        }else if(activeTab.id == strategyRiskHistoryGridContainer.id){
                  	        	//2请求历史记录
                	        	me.strategyRiskHistoryGrid.reloadData(id);
                	        }else if(activeTab.id == me.smassessAnalyseContainer.id){
		            	        	me.smassessAnalyseCardPanel.reloadData(id);
		            	    }
	            		},
                    	listeners:{
                    		afteritemexpand:function(){
                    			//设置导航
                    			me.changeNavigation('strategy');
                    			me.smassessAnalyseCardPanel.reloadData(me.nodeId);
                    		},
                    		itemclick: function(tablepanel, record, item, index, e, options){

                    	    	var id = record.data.id;
                    	    	
                    	    	/**
                    	         * 切换标签
                    	         */
                    	        me.cardpanel.setActiveItem(me.strategyContainer);
                    	    	//设置导航,是否导航到指标
                    	    	if(id.indexOf('_')!=-1){
                    	    		var name = record.data.text;
                    	    		var strategyId = id.split('_')[0];
                    	    		me.navigationBar.renderHtml(me.strategyContainer.id + 'DIV', strategyId, name, 'sm',me.strategyTree.id);
                    	    	}else{
                    	    		me.navigationBar.renderHtml(me.strategyContainer.id + 'DIV', id, '', 'sm',me.strategyTree.id);
                    	    	}
                    	        
                    	        //保存当前节点状态
                    			me.nodeId = id;
                    			me.nodeType = 'strategy';
                    	        //初始化右侧风险事件列表数据
                    	        var tab = me.strategyTabPanel;
                    	        var activeTab = tab.getActiveTab();
                    	        if(activeTab.id == strategyRiskEventGridContainer.id){
                    	        	//1 请求风险事件列表
                    	        	me.strategyRiskEventGrid.reloadData('strategy',id);
                    	        }else if(activeTab.id == strategyRiskHistoryGridContainer.id){
                      	        	//2请求历史记录
                    	        	me.strategyRiskHistoryGrid.reloadData(id);
                    	        }else if(activeTab.id == me.smassessAnalyseContainer.id){
		            	        	me.smassessAnalyseCardPanel.reloadData(id);
		            	   		}
                    		}
                    	}
        	        });
               		this.add(me.strategyTree);
            		this.doLayout();
        		}else{
            		//设置导航
            		me.changeNavigation('strategy');
        		}
        	}
        });

        //流程树
        var processTreeContainer =  Ext.create('Ext.container.Container',{
        	treeTitle:'流程',
        	treeIconCls : 'icon-ibm-icon-metrictypes',
        	layout:'fit',
        	onClick:function(){
    			//1.加载右侧card
        		if(!me.processContainer){
                    //风险事件列表页
                    var processRiskEventGridContainer =  Ext.create('Ext.container.Container',{
                    	layout:'fit',
                    	title:'风险列表',
                    	listeners:{
            	        	show:function(){
            	        		if(!me.processRiskEventGrid){
            	        			me.processRiskEventGrid =  Ext.create('FHD.view.risk.risk.RiskEventGrid',{
            	        				type:'process',
            	        				face:me,
            	                    	border:false,
            	                    	height:FHD.getCenterPanelHeight()-47
            	                    });
            	            		this.add(me.processRiskEventGrid);
            	            		this.doLayout();
            	        		}
            	        		//根据左侧选中节点，初始化数据
            	        		if(me.nodeId != ''){
            	        			me.processRiskEventGrid.reloadData('process',me.nodeId);
            	        		}
            	        	}
                    	}
                    });
            		
                    //历史记录页
                    var processRiskHistoryGridContainer =  Ext.create('Ext.container.Container',{
                    	layout:'fit',
                    	title:'历史记录',
                    	listeners:{
            	        	show:function(){
            	        		if(!me.processRiskHistoryGrid){
            	        			me.processRiskHistoryGrid =  Ext.create('FHD.view.risk.risk.RiskHistoryGrid',{
            	        	        	face:me,
            	        	        	type:'process',
            	        	        	border:false,
            	        	        	callback : function(data){
            	        	        		me.processTree.getStore().load();
            	        	        	}
            	        	        });
            	            		this.add(me.processRiskHistoryGrid);
            	            		this.doLayout();
            	        		}
            	        		//根据左侧选中节点，初始化数据
            	        		if(me.nodeId != ''){
            	        			me.processRiskHistoryGrid.reloadData(me.nodeId);
            	        		}
            	        	}
                    	}
                    });
                    
                    //图表分析
				    me.processassessAnalyseContainer =  Ext.create('Ext.container.Container',{
			        	title:'图表分析',
			        	layout : 'fit',
			        	listeners:{
				        	show:function(){
				        		if(!me.processassessAnalyseCardPanel){
				        			me.processassessAnalyseCardPanel =  Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel',{
				                    	face:me,
				                    	type : 'process',
				                    	border:false
				                    });
				            		this.add(me.processassessAnalyseCardPanel);
				            		this.doLayout();
				        		}
				        		if(me.nodeId != ''){
				        			me.processassessAnalyseCardPanel.reloadData(me.nodeId);
				        		}
				        	}
			        	}
			        });
			        me.processassessAnalyseCardPanel =  Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel',{
                    	face:me,
                    	type : 'process',
                    	border:false
                    });
			        me.processassessAnalyseContainer.add(me.processassessAnalyseCardPanel);
            		me.processassessAnalyseContainer.doLayout();
                    
                    me.processTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
                    	items:[me.processassessAnalyseContainer,processRiskEventGridContainer,processRiskHistoryGridContainer]
                    });
            		me.processContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer',{
            			border:false,
                     	tabpanel:me.processTabPanel,
                    	flex:1
            		});
            		me.cardpanel.add(me.processContainer);
        		}
        		me.cardpanel.setActiveItem(me.processContainer);
        		
        		//2.加载左侧树
        		if(!me.processTree){
                    me.processTree = Ext.create('FHD.view.process.ProcessTree',{
                    	border:false,
        	        	face:me,
        				extraParams : {showLight:true},
        				collapsible: false,
        				reloadNavigator:function(navid,name){
	            	    	var id = navid.split('_')[0];
	            	    	//将导航节点在节点上选中
	            	    	var rootNode = me.processTree.getRootNode();//得到根节点
	            			var selectNode = me.findNode(rootNode,id);
	            			me.processTree.getSelectionModel().select(selectNode);
	            			me.cardpanel.setActiveItem(me.processContainer);
	            			//设置导航
                	        me.navigationBar.renderHtml(me.processContainer.id + 'DIV', id, '', 'process',me.processTree.id);
                	        //保存当前节点状态
                			me.nodeId = id;
                			me.nodeType = 'process';
                			
                	        //初始化右侧风险事件列表数据
                	        var tab = me.processTabPanel;
                	        var activeTab = tab.getActiveTab();
                	        if(activeTab.id == processRiskEventGridContainer.id){
                	        	//1 请求风险事件列表
                	        	me.processRiskEventGrid.reloadData('process',id);
                	        }else if(activeTab.id == processRiskHistoryGridContainer.id){
                  	        	//2请求历史记录
                	        	me.processRiskHistoryGrid.reloadData(id);
                	        }else if(activeTab.id == me.processassessAnalyseContainer.id){
		            	        	me.processassessAnalyseCardPanel.reloadData(id);
		            	    }
	            		},
                    	listeners:{
                    		afteritemexpand:function(){
                    			//设置导航
                    			me.changeNavigation('process');
                    			me.processassessAnalyseCardPanel.reloadData(me.nodeId);
                    		},
                    		itemclick: function(tablepanel, record, item, index, e, options){
                    	    	var id = record.data.id;
    	            			/**
                    	         * 切换标签
                    	         */
                    	        me.cardpanel.setActiveItem(me.processContainer);
                    	    	//设置导航
                    	        me.navigationBar.renderHtml(me.processContainer.id + 'DIV', id, '', 'process',me.processTree.id);
                    	        //保存当前节点状态
                    			me.nodeId = id;
                    			me.nodeType = 'process';
                    			
                    	        //初始化右侧风险事件列表数据
                    	        var tab = me.processTabPanel;
                    	        var activeTab = tab.getActiveTab();
                    	        if(activeTab.id == processRiskEventGridContainer.id){
                    	        	//1 请求风险事件列表
                    	        	me.processRiskEventGrid.reloadData('process',id);
                    	        }else if(activeTab.id == processRiskHistoryGridContainer.id){
                      	        	//2请求历史记录
                    	        	me.processRiskHistoryGrid.reloadData(id);
                    	        }else if(activeTab.id == me.processassessAnalyseContainer.id){
		            	        	me.processassessAnalyseCardPanel.reloadData(id);
		            	    	}
                    		}
                    	}
            	    });
               		this.add(me.processTree);
            		this.doLayout();
        		}else{
            		//设置导航
            		me.changeNavigation('process');
        		}
        	}
        });
        
        //创建左侧折叠树
        var accordionTree = Ext.create("FHD.ux.layout.AccordionTree",{
        	title: '风险',
            iconCls: 'icon-ibm-icon-scorecards',
            width:250,
        	treeArr:[me.riskTreeContainer,orgTreeContainer,strategyTreeContainer,processTreeContainer]
        });
        
        //信息查看页
        me.riskBasicFormViewContainer =  Ext.create('Ext.container.Container',{
        	layout:'fit',
        	title:'信息查看',
        	listeners:{
	        	show:function(){
	        		if(!me.riskBasicFormView){
	        	        me.riskBasicFormView =  Ext.create('FHD.view.risk.cmp.RiskDetailForm',{
	        	        	face:me,
	        	        	border:false,
	        	        	height:FHD.getCenterPanelHeight()-47
	        	        	//autoHeight : true
	        	        });
	            		this.add(me.riskBasicFormView);
	            		this.doLayout();
	        		}
	        		//根据左侧选中节点，初始化数据
	        		if(me.nodeId != ''){
	        			me.riskBasicFormView.reloadData(me.nodeId);
	        		}
	        	}
        	}
        });
        
        //风险事件列表页
        me.riskEventGridContainer =  Ext.create('Ext.container.Container',{
        	layout:'fit',
        	title:'风险列表',
        	listeners:{
	        	show:function(){
	        		if(!me.riskEventGrid){
	        			me.riskEventGrid =  Ext.create('FHD.view.risk.risk.RiskEventGrid',{
	                    	face:me,
	                    	border:false,
	                    	height:FHD.getCenterPanelHeight()-47
	                    });
	            		this.add(me.riskEventGrid);
	            		this.doLayout();
	        		}
	        		//根据左侧选中节点，初始化数据
	        		if(me.nodeId != ''){
	        			me.riskEventGrid.reloadData('risk',me.nodeId);
	        		}
	        	}
        	}
        });

        //历史记录页
        me.riskHistoryGridContainer =  Ext.create('Ext.container.Container',{
        	layout:'fit',
        	title:'历史记录',
        	listeners:{
	        	show:function(){
	        		if(!me.riskHistoryGrid){
	        			me.riskHistoryGrid =  Ext.create('FHD.view.risk.risk.RiskHistoryGrid',{
	        	        	face:me,
	        	        	type:'risk',
	        	        	border:false,
	        	        	autoScroll:true,
	        	        	callback : function(data){
	        	        		me.riskTree.getStore().load();
	        	        	}
	        	        });
	            		this.add(me.riskHistoryGrid);
	            		this.doLayout();
	        		}
	        		//根据左侧选中节点，初始化数据
	        		if(me.nodeId != ''){
	        			me.riskHistoryGrid.reloadData(me.nodeId);
	        		}
	        	}
        	}
        });
        //图表分析
	    me.assessAnalyseContainer =  Ext.create('Ext.container.Container',{
        	title:'图表分析',
        	layout : 'fit',
        	listeners:{
	        	show:function(){
	        		if(!me.assessAnalyseCardPanel){
	        			me.assessAnalyseCardPanel =  Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel',{
	                    	face:me,
	                    	type : 'risk',
	                    	border:false
	                    });
	            		this.add(me.assessAnalyseCardPanel);
	            		this.doLayout();
	        		}
	        		if(me.nodeId != ''){
	        			me.assessAnalyseCardPanel.reloadData(me.nodeId);
	        		}
	        	}
        	}
        });
        me.assessAnalyseCardPanel =  Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel',{
            	face:me,
            	type : 'risk',
            	border:false
        });
        me.assessAnalyseContainer.add(me.assessAnalyseCardPanel);
        me.assessAnalyseContainer.doLayout();
        
        var tabs = [me.assessAnalyseContainer,me.riskEventGridContainer,me.riskHistoryGridContainer,me.riskBasicFormViewContainer];//,me.configContainer
        Ext.apply(me,{
        	tree:accordionTree,
        	tabs:tabs
        });
        
        me.callParent(arguments);
    }
});