Ext.define('FHD.demo.IndexMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.indexmainpanel',

    treepanel:null,		//左侧组件树导航
    mainpanel:null,		//右侧主面板，里面放着demepanel
    demopanel:null,		//右侧组件demo面板
    initDemoPanel:function(componentName){
    	var me = this;
    	me.mainpanel.remove(me.demopanel);
    	if(componentName!=''){
    		me.demopanel = Ext.create(componentName,{
    			border:false,
	    		height:FHD.getCenterPanelHeight()-3
	    	});
    	}else{	//点击目录展示空面板
    		me.demopanel = Ext.create("Ext.panel.Panel",{
    			border:false,
    			title:'暂无',
	    		height:FHD.getCenterPanelHeight()-3
	    	});
    	}
    	
		me.mainpanel.add(me.demopanel);
    },
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        me.id = 'indexMainPanel';
        me.tree = Ext.create('Ext.tree.Panel', {
		    region: 'west',
		    split: true,
	        collapsible : true,
	        border:false,
	        maxWidth:300,
	        height:FHD.getCenterPanelHeight()-3,
		    width:220,
			root : {
				text : '开发帮助',
				iconCls : 'icon-help',
				expanded : true,
				autoLoad : true,
				children : [{
					text : '公共控件',
					iconCls : 'icon-folder',
					expanded : true,
					children : [{
						text:'树',
				    	iconCls:'icon-folder',
				    	expanded : false,
						children : [{
							text : '树',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '树扩展',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '树列表',
							iconCls : 'icon-information',
							leaf : true
						}]            
					}, {
						text:'列表',
				    	iconCls:'icon-folder',
				    	expanded : false,
						children : [{
							text : '普通列表',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '可编辑列表',
							iconCls : 'icon-information',
							leaf : true
						}]            
					}, {
						text:'表单',
				    	iconCls:'icon-folder',
				    	expanded : false,
						children : [{
							text : '字典选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '带radio的公式选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '公式选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '公式弹窗选择',
							iconCls : 'icon-information',
							leaf : true
						}]            
					}, {
						text:'图表',
				    	iconCls:'icon-folder',
				    	expanded : false,
						children : [{
							text : '蝶型图',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '树型图',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '图表控件',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '点线图',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '饼状图',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '柱状图',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '散点图',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '权重图',
							iconCls : 'icon-information',
							leaf : true
						}]            
					}, {
						text:'布局',
				    	iconCls:'icon-folder',
				    	expanded : false,
						children : [{
							text : '步骤导航布局控件',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '折叠树布局控件',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '基础管理布局控件',
							iconCls : 'icon-information',
							leaf : true
						}]            
					}, {
						text:'其他',
				    	iconCls:'icon-folder',
				    	expanded : false,
						children : [{
							text : '上传文件',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text:'导航组件',
					    	iconCls:'icon-information',
							leaf: true
						}, {
							text:'采集频率',
					    	iconCls:'icon-information',
							leaf: true
						}, {
							text : '树选择控件',
							iconCls : 'icon-information',
							leaf : true
						}]            
					}, {
						text:'业务对象',
				    	iconCls:'icon-folder',
				    	expanded : true,
						children : [{
			        		text:'风险',
			        		iconCls:'icon-folder',
			        		expanded: false,
			        		children:[{
									text : '风险树',
									iconCls : 'icon-information',
									leaf : true
								}, {
									text : '风险事件列表',
									iconCls : 'icon-information',
									leaf : true
								}, {
									text : '风险事件添加表单',
									iconCls : 'icon-information',
									leaf : true
								}, {
									text : '风险事件查看表单',
									iconCls : 'icon-information',
									leaf : true
								}, {
									text : '风险事件选择',
									iconCls : 'icon-information',
									leaf : true
								}, {
									text : '风险分类选择',
									iconCls : 'icon-information',
									leaf : true
								}
			        		]
			        	}, {
			        		text:'组织',
			        		iconCls:'icon-folder',
			        		expanded : false,
			        		children: [{
								text : '组织树',
								iconCls : 'icon-information',
								leaf : true
							}, {
								text : '部门人员',
								iconCls : 'icon-information',
								leaf : true
							}, {
								text : '人员选择',
								iconCls : 'icon-information',
								leaf : true
							}]
			        	}, {
			        		text:'目标/指标',
			        		iconCls:'icon-folder',
			        		leaf : false,
			        		children:[{
								text : '目标指标树',
								iconCls : 'icon-information',
								leaf : true
							}]
			        	}, {
			        		text:'流程',
			        		iconCls:'icon-folder',
			        		leaf: false,
			        		children:[{
								text : '流程树',
								iconCls : 'icon-information',
								leaf : true
							}]
			        	}]            
					}]
				}, {
					text : '业务控件',
					iconCls : 'icon-folder',
					expanded : true,
					children : [{
						text : '风险评估',
						iconCls : 'icon-folder',
						expanded: false,
						children:[{
							text : '普通列表升级',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '可编辑列表升级',
							iconCls : 'icon-information',
							leaf : true
						}]
					}, {
		        		text:'监控预警',
		        		iconCls:'icon-folder',
		        		expanded: false,
		        		children:[{
							text:'指标树',
						    	iconCls:'icon-information',
							leaf: true
						},
						{
							text:'指标选择',
						    	iconCls:'icon-information',
							leaf: true
						},
						{
							text:'目标树',
						    	iconCls:'icon-information',
							leaf: true
						},
						{
							text:'目标选择',
						    	iconCls:'icon-information',
							leaf: true
						},
						{
	        				text:'指标类型选择',
	  		              	iconCls:'icon-information',
	        				leaf: true
	        			}]
					}, {
						text : '内控控制',
						iconCls : 'icon-folder',
						expanded : false,
						children : [{
							text : '内控评价计划选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '内控评价计划弹窗选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '内控整改计划选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '内控整改计划弹窗选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '内控缺陷选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '内控缺陷弹窗选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '流程选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '制度选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '内控标准树',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '内控标准选择',
							iconCls : 'icon-information',
							leaf : true
						}, {
							text : '内控要求选择',
							iconCls : 'icon-information',
							leaf : true
						}]
					}]
				}]
			},
			listeners : {
				itemclick : function(view, node) {
					switch (node.data.text) {
						case '组织树' :
							me.initDemoPanel('FHD.demo.depttree.DeptTreeDemo');
							break;
						case '流程树' :
							me.initDemoPanel('FHD.demo.process.ProcessTreeDemo');
							break;
						case '目标指标树' :
							me.initDemoPanel('FHD.view.kpi.cmpdemo.StrategyMapTreeDemo');
							break;
						case '普通列表' :
							me.initDemoPanel('FHD.demo.grid.GridList');
							break;
						case '可编辑列表' :
							me.initDemoPanel('FHD.demo.editgrid.EditGridList');
							break;
						case '树' :
							me.initDemoPanel('FHD.demo.tree.Tree');
							break;
						case '树扩展' :
							me.initDemoPanel('FHD.demo.commonTree.CommonTree');
							break;
						case '树列表' :
							me.initDemoPanel('FHD.demo.treegrid.TreeGridList');
							break;
						case '上传文件' :
							me.initDemoPanel('FHD.demo.FileUpload.FileUpLoad');
							break;
						case '字典选择' :
							me.initDemoPanel('FHD.demo.dict.Dictselect');
							break;
						case '带radio的公式选择' :
							me.initDemoPanel('FHD.demo.formula.FormulaSelector');
							break;
						case '公式选择' :
							me.initDemoPanel('FHD.demo.formula.FormulaTrigger');
							break;
						case '公式弹窗选择' :
							me.initDemoPanel('FHD.demo.formula.FormulaSelectorForwindow');
							break;
						case '树选择控件' :
							me.initDemoPanel('FHD.demo.treeselector.TreeSelector');
							break;
//						case '树列表选择控件' :
//							me.initDemoPanel('FHD.demo.treelistselector.TreeListSelector');
//							break;
//						case '列表选择控件' :
//							me.initDemoPanel('FHD.demo.listselector.Listselector');
//							break;
						case '步骤导航' :
							me.initDemoPanel('FHD.demo.stepnavigator.StepNavigator');
							break;
						case '蝶型图' :
							me.initDemoPanel('FHD.demo.meshStructureChart.MeshStructureChart');
							break;
						case '树型图' :
							me.mainpanel.remove(me.demopanel);
							me.demopanel=Ext.create('FHD.ux.treeChar.TreeCharExt',{
								path:__ctxPath,
								height:FHD.getCenterPanelHeight()-3,
								treeCharModel:"edit",
								root:{
									id:"root",
									value:"根节点",
									image:__ctxPath+'/pages/demo/mxgraph/images/symbol_4_med.gif'
								},open:function(node){
									var nodes=new Array();
									var node={
										value:"节点1",
										image:__ctxPath+'/pages/demo/mxgraph/images/symbol_6_med.gif'
									}
									nodes.push(node);
									var node={
										value:"节点2",
										image:__ctxPath+'/pages/demo/mxgraph/images/symbol_5_med.gif'
									}
									nodes.push(node);
									return nodes;
								},add:function(parent){
						        	var node={
					        			value:"新建节点",
										image:__ctxPath+"/scripts/component/treeChar/images/open.png"
						        	}
						        	return node;
								},del:function(node){
									return true;
								}
							});
							me.mainpanel.add(me.demopanel);
							break;
						case '普通列表升级' :
							me.initDemoPanel('FHD.demo.layout.GridPanelDemo');
							break;
						case '可编辑列表升级' :
							me.initDemoPanel('FHD.demo.layout.EditorGridPanelDemo');
							break;
						case '步骤导航布局控件' :
							me.initDemoPanel('FHD.demo.layout.StepNavigatorDemo');
							break;
						case '基础管理布局控件' :
							me.initDemoPanel('FHD.demo.layout.TreeTabFaceDemo');
							break;
						case '多表管理布局控件' :
							me.initDemoPanel('FHD.demo.layout.MultiLayoutDemo');
							break;
						case '折叠树布局控件' :
							me.initDemoPanel('FHD.demo.layout.AccordionTreeDemo');
							break;
						case '图表控件' :
							me.initDemoPanel('FHD.demo.chart.ChartDashboardDemo');
							break;
						case '点线图' :
							me.initDemoPanel('FHD.demo.chart.ChartPointLineDemo');
							break;
						case '饼状图' :
							me.initDemoPanel('FHD.demo.chart.ChartPieDemo');
							break;
						case '柱状图' :
							me.initDemoPanel('FHD.demo.chart.ChartColumnarDemo');
							break;
						case '散点图' :
							me.initDemoPanel('FHD.demo.chart.ChartSelectScatterDemo');
							break;	
						case '权重图' :
							me.initDemoPanel('FHD.demo.chart.ChartHeatMapDemo');
							break;		
						case '风险事件选择' :
							me.initDemoPanel('FHD.view.risk.cmpdemo.RiskEventSelectorDemo');
							break;
						case '内控整改计划选择' :
							me.initDemoPanel('FHD.demo.rectifyplanselector.Rectifyplanselector');
							break;
						case '内控整改计划弹窗选择' :
							me.initDemoPanel('FHD.demo.rectifyplanselector.Rectifyplanselectorforwindow');
							break;
						case '内控缺陷选择' :
							me.initDemoPanel('FHD.demo.defectselector.Defectselector');
							break;
						case '内控缺陷弹窗选择' :
							me.initDemoPanel('FHD.demo.defectselector.Defectselectorforwindow');
							break;
						case '流程选择' :
							me.initDemoPanel('FHD.demo.process.Processselect');
							break;
						case '制度选择' :
							me.initDemoPanel('FHD.demo.rule.RuleSelect');
							break;
						case '部门人员':
		    			    me.initDemoPanel('FHD.demo.deptAndEmpSelect.DeptAndEmpSelect');
		    			    break;
						case '人员选择':
		    			    me.initDemoPanel('FHD.demo.empSelect.EmpSelect');
		    			    break;
		    			case '内控标准树':
			    			me.initDemoPanel('FHD.demo.standard.Standardtree');
				    		break;
		    			case '内控标准选择':
			    			me.initDemoPanel('FHD.demo.standard.Standardselect');
				    		break;
		    			case '内控要求选择':
			    			me.initDemoPanel('FHD.demo.standard.Requireselect');
				    		break;
				        case '指标树':
			    			me.initDemoPanel('FHD.demo.kpitree.Kpitree');
				    		break; 
		    		    case '指标选择':
			    			me.initDemoPanel('FHD.demo.kpitree.Kpiselect');
				    		break;
		    		    case '指标类型选择':
			    			me.initDemoPanel('FHD.demo.kpitree.Kpitypeselect');
				    		break;
		    		    case '目标树':
			    			me.initDemoPanel('FHD.demo.strategymaptree.StrategyMaptree');
				    		break;
			    		case '目标选择':
			    			me.initDemoPanel('FHD.demo.strategymaptree.StrategyMapselect');
				    		break;
				    	case '时间选择':
		    			    me.initDemoPanel('FHD.demo.time.Time');
	    				    break;
		    		    case '导航组件':
			    			me.initDemoPanel('FHD.demo.navigation.Navigation');
		    				break;
			    		case '风险选择':
			    			me.initDemoPanel('FHD.demo.risk.RiskSelect');
		    				break;
			    		case '采集频率':
			    			me.initDemoPanel('FHD.demo.collect.Collect');
		    				break;
			    		case '风险树':
			    			me.initDemoPanel('FHD.view.risk.cmpdemo.RiskTreePanelDemo');
		    				break;
			    		case '风险分类选择':
			    			me.initDemoPanel('FHD.view.risk.cmpdemo.RiskSelectorDemo');
		    				break;
			    		case '风险事件添加表单':
			    			me.initDemoPanel('FHD.view.risk.cmpdemo.RiskAddFormDemo');
		    				break;
			    		case '风险事件查看表单':
			    			me.initDemoPanel('FHD.view.risk.cmpdemo.RiskDetailFormDemo');
		    				break;
			    		case '风险事件列表':
			    			me.initDemoPanel('FHD.view.risk.cmpdemo.RiskGridPanelDemo');
		    				break;
						default :
							me.initDemoPanel('');
					}
				}
			}
		});
		me.mainpanel=Ext.create('Ext.panel.Panel',{
			border:false,
			autoScroll:true,
			height:FHD.getCenterPanelHeight()-3,
			region: 'center'
		});	
        
        Ext.apply(me, {
            autoScroll:false,
            border:false,
		    layout: {
		        type: 'border'
		    },
		    defaults: {
	            border:false
	        },
	        height:FHD.getCenterPanelHeight()-3,
		    items:[me.tree,me.mainpanel]
        });
        
        me.callParent(arguments);      
    }
});