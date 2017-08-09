/**
 * 风险主面板  现在不用了
 * @author zhengjunxiang
 */
Ext.define('FHD.view.risk.MainPanel', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.riskMainPanel',
    
    node:null,		//左侧选择的树节点
    nodeId:null,	//左侧选择的树节点id
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
		me.navigationBar.renderHtml(icontainer.id + 'DIV', id, '', navType);
		
		//3.保存当前节点状态
		me.node = selectedNode;
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
	        me.riskEventAddForm = Ext.create('FHD.view.risk.risk.RiskEventAddForm',{
				face:me,
	        	title:'基本信息',
				height:FHD.getCenterPanelHeight()-47
			});
	        /**
	         * 宋佳添加风险应对  
	         */
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
       	if(type=='risk'){
    		icontainer = me.containers;
    	}else if(type=='org'){
    		icontainer = me.orgContainer;
    	}else if(type=='strategy'){
    		icontainer = me.strategyContainer;
    		type = 'sm';
    	}else if(type=='process'){
    		icontainer = me.processContainer;
    	}else{
    		
    	}
       	if(id){
       		if(type=='risk'){	//风险事件重新按所属风险分类导航，其他直接在后面写名称
       			me.navigationBar.renderHtml(me.riskEventAddContainer.id + 'DIV', parentId, name, type);
       		}else{
       	       	me.navigationBar.renderHtml(me.riskEventAddContainer.id + 'DIV', me.nodeId, name, type);
       		}
       	}else{
        	me.navigationBar.renderHtml(me.riskEventAddContainer.id + 'DIV', me.nodeId, '添加风险事件', type);
       	}
    	//设置返回的container
    	me.riskEventAddForm.riskEventGrid = icontainer;
    },
    /**
     * 创建风险事件查看容器
     */
    showRiskEventDetailContainer:function(eventId,parentId,name){
    	var me = this;
    	
       	if(!me.riskEventDetailContainer){    		
    		//风险事件基本信息
	        me.riskEventDetailForm =  Ext.create('FHD.view.risk.risk.RiskEventDetail',{
	        	title:'基本信息',
	        	face:me,
	        	border:false,
	        	autoHeight : true
	        });
	        
	        //风险事件历史记录
			me.riskEventHistoryGrid =  Ext.create('FHD.view.risk.risk.RiskHistoryGrid',{
				title:'历史记录',
	        	type:'riskevent',
	        	border:false,
	        	height:FHD.getCenterPanelHeight()-47
	        });
	        
    		me.riskEventTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
            	items:[me.riskEventDetailForm,me.riskEventHistoryGrid]
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
       	if(type=='risk'){
    		icontainer = me.containers;
    	}else if(type=='org'){
    		icontainer = me.orgContainer;
    	}else if(type=='strategy'){
    		icontainer = me.strategyContainer;
    		type = 'sm';
    	}else if(type=='process'){
    		icontainer = me.processContainer;
    	}else{
    		
    	}
       	if(type=='risk'){	//风险事件重新按所属风险分类导航，其他直接在后面写名称
       		
       		//左侧树定位到当前节点,全部展开
       		me.riskTree.expandAll(function(){
       			me.navigationBar.renderHtml(me.riskEventDetailContainer.id + 'DIV', parentId, name, type);
       		});
       	}else{
       		me.navigationBar.renderHtml(me.riskEventDetailContainer.id + 'DIV', me.nodeId, name, type);
       	}
    	
    	//设置返回的container
    	me.riskEventDetailForm.riskEventGrid = icontainer;
       	//初始化数据
       	me.riskEventDetailForm.reloadData(eventId);
       	me.riskEventHistoryGrid.reloadData(eventId);
    },
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        
        me.id = 'riskMainPanel';
        
        //风险事件查询url
        var risktreeEventUrl = '/cmp/risk/findPotentialRiskEventByRiskId';
        var orgtreeEventUrl = '/cmp/risk/findPotentialRiskEventByOrgId';
        var kpitreeEventUrl = '/cmp/risk/findPotentialRiskEventByKpiId';
        var processtreeEventUrl = '/cmp/risk/findPotentialRiskEventByProcessId';
        
        //风险树
        me.riskTreeContainer =  Ext.create('Ext.container.Container',{
        	treeTitle:'风险',
        	treeIconCls : 'icon-ibm-icon-scorecards',
        	layout:'fit',
        	onClick:function(){
        		//1.加载左侧树
        		if(!me.riskTree){
                    me.riskTree = Ext.create('FHD.view.risk.risk.RiskTreePanel',{
                    	border:false,
                    	face:me,
                    	rbs:true        	
                    }); 
               		this.add(me.riskTree);
            		this.doLayout();
        		}
        		
        		//2.加载右侧tab
        		me.cardpanel.setActiveItem(me.containers);
        		
        		//3.设置导航
        		me.changeNavigation('risk');
        	}
        });
        //初始化风险树
        me.riskTree = Ext.create('FHD.view.risk.risk.RiskTreePanel',{
        	border:false,
        	face:me,
        	rbs:true        	
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
            		//初始化风险事件列表
            		me.orgRiskEventGrid =  Ext.create('FHD.view.risk.risk.RiskEventGrid',{
            			type:'org',
            			face:me,
                    	border:false,
                    	height:FHD.getCenterPanelHeight()-47
                    });
            		orgRiskEventGridContainer.add(me.orgRiskEventGrid);
            		orgRiskEventGridContainer.doLayout();
            		
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
            	        	        	height:FHD.getCenterPanelHeight()-47
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
                    me.orgTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
                    	items:[orgRiskEventGridContainer,orgRiskHistoryGridContainer]
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
                    	id:'treePanel',
                    	showLight:true,
                    	subCompany: false,
                    	companyOnly: false,
                    	checkable:false,
                    	border:false,
                    	face:me,
                    	rootVisible: true,
                		orgFun:function(navid,name){
	            	    	var id = navid.split('_')[0];
	            	    	//将导航节点在节点上选中
	            	    	var rootNode = me.orgTree.getRootNode();//得到根节点
	            			var selectNode = me.findNode(rootNode,id);
	            			me.orgTree.getSelectionModel().select(selectNode);
	            			
	            			//设置导航
	            	        me.navigationBar.renderHtml(me.orgContainer.id + 'DIV', id, '', 'org');
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
	            	        }
	            		},
                    	listeners:{
                    		afteritemexpand:function(){
                    			//设置导航
                    			me.changeNavigation('org');
                    			//初始化右侧风险事件列表数据
                    			me.orgRiskEventGrid.reloadData(me.nodeType,me.nodeId);
                    		},
                    		itemclick: function(tablepanel, record, item, index, e, options){

                    	    	var id = record.data.id;
                    	    	
                    	    	/**
                    	         * 切换标签
                    	         */
                    	        me.cardpanel.setActiveItem(me.orgContainer);
                    	    	//设置导航
                    	        me.navigationBar.renderHtml(me.orgContainer.id + 'DIV', id, '', 'org');
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
            	        		}strategyRiskEventGridContainer
            	        		//根据左侧选中节点，初始化数据
            	        		if(me.nodeId != ''){
            	        			me.strategyRiskEventGrid.reloadData('strategy',me.nodeId);
            	        		}
            	        	}
                    	}
                    });
            		//初始化风险事件列表
            		me.strategyRiskEventGrid =  Ext.create('FHD.view.risk.risk.RiskEventGrid',{
            			type:'strategy',
            			face:me,
                    	border:false,
                    	height:FHD.getCenterPanelHeight()-47
                    });
            		strategyRiskEventGridContainer.add(me.strategyRiskEventGrid);
            		strategyRiskEventGridContainer.doLayout();
            		
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
            	        	        	height:FHD.getCenterPanelHeight()-47
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
            		
                    me.strategyTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
                    	items:[strategyRiskEventGridContainer,strategyRiskHistoryGridContainer]
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
        	        	id:'strtree',
        	        	rootVisible : true,
        	        	collapsible:false,
        	        	showLight:true,
        	        	border:false,
        	        	smFun:function(navid,name){
	            	    	var id = navid.split('_')[0];
	            	    	//将导航节点在节点上选中
	            	    	var rootNode = me.strategyTree.getRootNode();//得到根节点
	            			var selectNode = me.findNode(rootNode,id);
	            			me.strategyTree.getSelectionModel().select(selectNode);
	            			
	            			//设置导航
                	        me.navigationBar.renderHtml(me.strategyContainer.id + 'DIV', id, '', 'sm');
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
                	        }
	            		},
                    	listeners:{
                    		afteritemexpand:function(){
                    			//设置导航
                    			me.changeNavigation('strategy');
                    			//初始化右侧风险事件列表数据
                    			me.strategyRiskEventGrid.reloadData(me.nodeType,me.nodeId);
                    		},
                    		itemclick: function(tablepanel, record, item, index, e, options){

                    	    	var id = record.data.id;
                    	    	
                    	    	/**
                    	         * 切换标签
                    	         */
                    	        me.cardpanel.setActiveItem(me.strategyContainer);
                    	    	//设置导航,是否导航到指标
                    	    	if(id.indexOf('_')!=-1 && id.indexOf('root')==-1){	//不是根节点
                    	    		var name = record.data.text;
                    	    		var strategyId = id.split('_')[0];
                    	    		me.navigationBar.renderHtml(me.strategyContainer.id + 'DIV', strategyId, name, 'sm');
                    	    	}else{
                    	    		me.navigationBar.renderHtml(me.strategyContainer.id + 'DIV', id, '', 'sm');
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
            		//初始化风险事件列表
            		me.processRiskEventGrid =  Ext.create('FHD.view.risk.risk.RiskEventGrid',{
            			type:'org',
            			face:me,
                    	border:false,
                    	height:FHD.getCenterPanelHeight()-47
                    });
            		processRiskEventGridContainer.add(me.processRiskEventGrid);
            		processRiskEventGridContainer.doLayout();
            		
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
            	        	        	height:FHD.getCenterPanelHeight()-47
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
            		
                    me.processTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab",{
                    	items:[processRiskEventGridContainer,processRiskHistoryGridContainer]
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
                    	id:'processTreePanel',
                    	border:false,
        	        	face:me,
        				extraParams : {showLight:true},
        				collapsible: false,
        				processFun:function(navid,name){
	            	    	var id = navid.split('_')[0];
	            	    	//将导航节点在节点上选中
	            	    	var rootNode = me.processTree.getRootNode();//得到根节点
	            			var selectNode = me.findNode(rootNode,id);
	            			me.processTree.getSelectionModel().select(selectNode);
	            			
	            			//设置导航
                	        me.navigationBar.renderHtml(me.processContainer.id + 'DIV', id, '', 'process');
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
                	        }
	            		},
                    	listeners:{
                    		afteritemexpand:function(){
                    			//设置导航
                    			me.changeNavigation('process');
                    			//初始化右侧风险事件列表数据
                    			me.processRiskEventGrid.reloadData(me.nodeType,me.nodeId);
                    		},
                    		itemclick: function(tablepanel, record, item, index, e, options){
                    	    	var id = record.data.id;
                    	    	
    	            			/**
                    	         * 切换标签
                    	         */
                    	        me.cardpanel.setActiveItem(me.processContainer);
                    	    	//设置导航
                    	        me.navigationBar.renderHtml(me.processContainer.id + 'DIV', id, '', 'process');
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
        me.riskEventGrid =  Ext.create('FHD.view.risk.risk.RiskEventGrid',{
        	face:me,
        	border:false,
        	height:FHD.getCenterPanelHeight()-47
        });
		me.riskEventGridContainer.add(me.riskEventGrid);
		me.riskEventGridContainer.doLayout();
        
        
        //风险基本信息页
        me.riskAddFormContainer =  Ext.create('Ext.container.Container',{
        	layout:'fit',
        	title:'基本信息',
        	listeners:{
        		show:function(){
            		if(!me.riskAddForm){//stepPanel
            			//2.表单
            	        me.riskAddForm = Ext.create('FHD.view.risk.risk.RiskAddForm',{
            				face:me,
            	        	callback:function(data,editflag){
            	        		var id = data.id;
            	        		var text = data.name;
            	        		//刷新树节点,如果是添加则添加树节点;如果是修改则修改节点名称
                            	if (!editflag) {
                            		//添加节点
                                    var node = {
                                        iconCls: 'icon-ibm-symbol-0-sm',	//风险水平无对应的显示灯图标
                                        id: id,
                                        text: text,
                                        dbid: id,
                                        leaf: true,
                                        type: 'risk'
                                    };
                                    if (me.node.isLeaf()) {
                                        me.node.data.leaf = false;
                                    }
                                    me.node.appendChild(node);
                                    me.node.expand();
                                    var newNode = me.node.lastChild;
                                    //选中新添加的节点
                                    me.riskTree.getSelectionModel().select(newNode);
                                    me.node = newNode;
                                    me.nodeId = newNode.data.id;
                            	} else {
                                	//编辑节点,需要替换节点名称
                            		var nodeData = me.node.data;
                            		nodeData.text = text;
                            		me.node.updateInfo(true, nodeData);
                            	}  
                            	//刷新导航
                            	me.navigationBar.renderHtml(me.containers.id + 'DIV', id, '', 'risk');
            	        	}
            			});
                		this.add(me.riskAddForm);
                		this.doLayout();
            		}

            		if(me.isload){
            			//根据左侧选中节点，初始化数据
                		if(me.nodeId != ''){
                			me.riskAddForm.reloadData(me.nodeId);
                		}
            		}
        		},
        		hide:function(){
        			me.isload = true;
        		}
        	}
        	
        });
        //  配置中心
        me.configContainer =  Ext.create('Ext.container.Container',{
        	title:'配置中心',
        	//disabled: true,
//        	
        	listeners:{
        		show:function(){
            		if(!me.stepConfigPanel){
            	        /**
            	         *  add by 宋佳 
            	         *  给基本信息增加上一步下一步内容
            	         */
            	        me.riskstandardmanage = Ext.create('FHD.view.risk.riskedit.RiskStandardManage',{
            	        	navigatorTitle:'风险上报标准'
//            	        	back:function(){
//            	        		alert('back');
//            	        	},
            	        });
            	        /**
            	         * add by 宋佳
            	         * 风险管控措施 
            	         */
            	        me.riskmeasuremanage = Ext.create('FHD.view.risk.measureedit.RiskMeasureManage',{navigatorTitle:'风险管控措施'});
            	        /**
            	         * 应对预案维护 response plan
            	         */
            			me.riskresponseplanmanage =Ext.create('FHD.view.risk.responseplan.RiskResponsePlanManage',{navigatorTitle:'应对预案维护'});
            			/**
            			 * 有效性标准 
            			*/
            	        /**
            	         * 设定检查点
            	         */
            			me.checkpointmanage = Ext.create('FHD.view.risk.checkpoint.CheckPointManage',{navigatorTitle:'设定检查点'});
            			/**
            			 * 检查点检查
            			 */
            			me.checkpointcheckmanage = Ext.create('FHD.view.risk.checkpoint.CheckPointCheckManage',{navigatorTitle:'检查点检查'});
            	        me.effectivestandardmanage = Ext.create('FHD.view.risk.effective.EffectiveStandardManage',{navigatorTitle:'风险管理有效性标准'});
            			me.stepConfigPanel = Ext.create('FHD.ux.layout.StepNavigator',{
            				height : FHD.getCenterPanelHeight()-47,
            				items : [me.riskstandardmanage,me.riskmeasuremanage,me.riskresponseplanmanage,me.effectivestandardmanage,me.checkpointmanage,me.checkpointcheckmanage],
            				hiddenUndo : true
            	        });
                		this.add(me.stepConfigPanel);
                		this.doLayout();
            		}
            		
            		/**
            		 * 如果是风险事件,就不重新加载
            		 */
            		if(!me.isload){
            			return;
            		}
            		//根据左侧选中节点，初始化数据
            		if(me.nodeId != ''){
            			FHD.ajax({
                   			async:false,
                   			params: {
                                riskId: me.nodeId
                            },
                            url: __ctxPath + '/risk/findRiskEditInfoById.f',
                            callback: function (ret) {
                            	//显示目标详细信息
//                            	var riskEditFormView = me.riskEditBasicFormView;	//找到步骤导航中的formpanel
//                            	riskEditFormView.reLoadData(ret);
//                            	riskEditFormView.isEdit = true;
//                            	riskEditFormView.editId = me.nodeId;
                            }
                        });
            		}
        		},
        		hide:function(){
        			me.isload = true;
        		}
        	}
        	
        });
        
        // 胡迪新  添加的控制评价DEMO
        me.controlEvaluationContainer = Ext.create('Ext.container.Container',{
        	layout:'fit',
        	title:'控制评价',
        	listeners:{
	        	show:function(){
	        		if(!me.controlEvaluationGrid){
	        			me.controlEvaluationGrid =  Ext.create('Ext.grid.Panel',{
	                    	face:me,
	                    	border:false,
	                    	height:FHD.getCenterPanelHeight()-47,
	                    	store: Ext.create('Ext.data.Store', {
	                    	    fields:['c1', 'c2', 'c3', 'c4', 'c5', 'c6', 'c7', 'c8'],
	                    	    data:{'items':[
	                    	        { 'c1': '生产管理部对于各生产单位如何开展生产准备检查的制度性文件要求尚未落实。',  "c2":"生产准备检查相关记录",  
	                    	        	"c3":"是" , "c4":"签字时间与需求时间倒挂",  "c5":"一般缺陷",  
	                    	        	"c6":"执行缺陷",  "c7":"生产调度部",  "c8":"<a href='javascript:void(0)' onclick=\"Ext.create('FHD.ux.Window',{title:'添加',layout: 'fit',items:[Ext.create('FHD.view.risk.cmp.RiskShortAddForm')]}).show();\" >车间人力、设备、工艺装备、图纸到位情况、生产面积等资源不足，导致无法完成生产计划，影响全年生产任务交付</a>" },
                    	        	{ 'c1': '检验员按图纸及工艺文件要求对外委零件尺寸进行检测，对超差零件进行隔离或报废处理。',  "c2":"零件指令、废品通知单、拒收让步单（Ⅰ、Ⅱ、Ⅲ级）",  
	                    	        	"c3":"否" , "c4":"签字时间与需求时间倒挂",  "c5":"重要缺陷",  
	                    	        	"c6":"设计缺陷",  "c7":"生产调度部",  "c8":"<a href='javascript:void(0)' onclick=\"Ext.create('FHD.ux.Window',{title:'添加',layout: 'fit',items:[Ext.create('FHD.view.risk.cmp.RiskShortAddForm')]}).show();\" >添加风险</a>" }
	                    	    ]},
	                    	    proxy: {
	                    	        type: 'memory',
	                    	        reader: {
	                    	            type: 'json',
	                    	            root: 'items'
	                    	        }
	                    	    }
	                    	}),
	                    	plugins: [
	            	             Ext.create('Ext.grid.plugin.CellEditing', {
	            	                 clicksToEdit: 1
	            	             })
	            	         ],
	                        columns: [
	                            { text: '控制措施', dataIndex: 'c1' ,flex:1,
	                            	renderer: function (value, metaData, record, colIndex, store, view) {
	                                    metaData.tdAttr = 'data-qtip="'+value+'"';
	                                    
	                                    return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showMoreInfo('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
	                                }},
	                            { text: '实施证据', dataIndex: 'c2', editor: 'textfield' ,flex:1},
	                            { text: '是否通过', dataIndex: 'c3', 
	                            	editor: {
		                                xtype: 'combo',
		                                store : Ext.create('Ext.data.Store', {
		                                    fields: ['abbr', 'name'],
		                                    data : [
		                                        {"abbr":"是", "name":"是"},
		                                        {"abbr":"否", "name":"否"}
		                                    ]
		                                }),
	                                    queryMode: 'local',
	                                    displayField: 'name',
	                                    valueField: 'abbr'
	                            	} 
	                            },
	                            { text: '缺陷描述', dataIndex: 'c4', editor: 'textfield' ,flex:1},
	                            { text: '缺陷级别', dataIndex: 'c5' ,  
	                            	editor: {
		                                xtype: 'combo',
		                                store : Ext.create('Ext.data.Store', {
		                                    fields: ['abbr', 'name'],
		                                    data : [
		                                        {"abbr":"重大缺陷", "name":"重大缺陷"},
		                                        {"abbr":"重要缺陷", "name":"重要缺陷"},
		                                        {"abbr":"一般缺陷", "name":"一般缺陷"},
		                                        {"abbr":"例外事项", "name":"例外事项"}
		                                    ]
		                                }),
	                                    queryMode: 'local',
	                                    displayField: 'name',
	                                    valueField: 'abbr'
	                            	} },
	                            { text: '缺陷类型', dataIndex: 'c6', 
		                            	editor: {
			                                xtype: 'combo',
			                                store : Ext.create('Ext.data.Store', {
			                                    fields: ['abbr', 'name'],
			                                    data : [
			                                        {"abbr":"设计缺陷", "name":"设计缺陷"},
			                                        {"abbr":"执行缺陷", "name":"执行缺陷"},
			                                        {"abbr":"双重缺陷", "name":"双重缺陷"}
			                                    ]
			                                }),
		                                    queryMode: 'local',
		                                    displayField: 'name',
		                                    valueField: 'abbr'
		                            	}  },
	                            { text: '整改部门', dataIndex: 'c7' , editor: 'textfield'},
	                            { text: '风险识别', dataIndex: 'c8' , flex:1}
	                        ]
	                    });
	            		this.add(me.controlEvaluationGrid);
	            		this.doLayout();
	        		}
	        	}
        	}
        });
        

        var tabs = new Array();
        
        tabs.push(me.riskEventGridContainer);
        tabs.push(me.riskResponse);
        
        if($ifAllGranted('ROLE_ALARMMGR,ROLE_AUTHORITY2')) {
        	tabs.push(me.controlEvaluationContainer);
        }
        
        tabs.push(me.riskAddFormContainer);//,me.configContainer
        
        Ext.apply(me,{
        	tree:accordionTree,
        	tabs:tabs
        });
        
        me.callParent(arguments);
    }
});