Ext.define('FHD.view.sf.index.AssessAnalyseMainPanel', {
   extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.assessanalysemainpanel',
    
    nodeId:null,		//左侧选择的树节点id
    nodeType:null,	//risk,org,strategy,process
    isload:true,	//是否点击页签时页签内部数据重新加载，默认是true,但是当添加风险分类是，不希望数据加载，此时使用
    /**
     * 按id在树上查找节点
     */
    findNode:function(root,nodeid){},
    /**
     * 选中左侧树节点，并设置导航
     * type:risk,org,strategy,process
     */
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
       	var type ='risk';
       	var icontainer = null;
       	var itree = null;
       	if(type=='risk'){
    		icontainer = me.containers;
    		itree = me.riskTree;
    	}
    	//设置返回的container
    	me.riskEventDetailForm.riskEventGrid = icontainer;
    	me.riskEventDetailForm.loadConainer = me.riskEventGrid;
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
        me.nodeId = me.businessId;
        
        
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
        me.assessAnalyseCardPanel.reloadData(me.nodeId);
        me.assessAnalyseContainer.add(me.assessAnalyseCardPanel);
        me.assessAnalyseContainer.doLayout();
        var tabs = [me.assessAnalyseContainer,me.riskEventGridContainer,me.riskHistoryGridContainer,me.riskBasicFormViewContainer];//,me.configContainer
        Ext.apply(me,{
        	tree:null,
        	tabs:tabs,
			listeners:{
				afterrender:function(self,eopts){
					me.navigationBar.renderHtml(me.containers.id + 'DIV', me.businessId, '', 'risk');
				}
			}
        });
        
        me.callParent(arguments);
    }
});