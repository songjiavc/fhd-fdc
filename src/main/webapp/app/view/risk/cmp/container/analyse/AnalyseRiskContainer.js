/**
 * 结果分析risk页面
 *
 * @author 张健
 */
Ext.define('FHD.view.risk.cmp.container.analyse.AnalyseRiskContainer', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.analyseriskcontainer',

    currentId: '',
    flex: 1,
    navHeight: 22,  //导航条高度，设置为0则为没有导航
    //风险列表新增按钮状态
    addButtonDisable : true, 
    
    //导航条方法链接方法
    navFunId: '',
	//风险新增查看显示的表单，storage风险库，relate风险关联
    formType : 'storage',
    
    //历史事件后续处理方法
    historyCallback: function () {},
    
    //风险事件查看后续事件
    showRiskDetailCallback: function(){},

    //修改导航条方法
    chageNavigationBar: function (nodeId, name) {
        var me = this;
        if (me.navHeight != 0) {
            me.navigationBar.renderHtml(me.tabContainer.id + 'DIV', nodeId, name, 'risk', me.navFunId);
        }
    },
    //风险列表相关导航操作
    chageRiskNavigationBar: function (navId, nodeId, name) {
        var me = this;
        if (me.navHeight != 0) {
            me.navigationBar.renderHtml(navId, nodeId, name, 'risk', me.navFunId);
        }
    },
    
    //改变新增按钮状态
    changeAddbuttonStatus: function(isDisable){
    	var me = this;
    	me.addButtonDisable = isDisable;
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        //创建导航条
        me.navigationBar = Ext.create('FHD.ux.NavigationBars');

        //图表分析
        me.chartAnalyseContainer = Ext.create('Ext.container.Container', {
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_CHART',
            title: '图表分析',
            layout: 'fit',
            onClick: function () {
                if (!me.chartAnalyseCardPanel) {
                    me.chartAnalyseCardPanel = Ext.create('FHD.view.risk.cmp.chart.ChartAnalyseCardPanel', {
                        face: me,
                        type: 'risk',
                        border: false
                    });
                    this.add(me.chartAnalyseCardPanel);
                    this.doLayout();
                }
                if (me.currentId != '') {
                    me.chartAnalyseCardPanel.reloadData(me.currentId);
                }
            }
        });
        
        //风险图形分析的页签
        me.riskGraphContainer =  Ext.create('Ext.container.Container',{
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_FIGURE',
        	layout:'fit',
        	title:'图形分析',
        	onClick:function(){
        		if(!me.riskGraph){
        			//2.表单
        	        me.riskGraph = Ext.create('FHD.view.comm.graph.GraphRelaRiskPanel',{
        			});
            		this.add(me.riskGraph);
            		this.doLayout();
        		}

    			//根据左侧选中节点，初始化数据
        		if(me.currentId != ''){
        			me.riskGraph.initParam({
		                 riskId:me.currentId
		        	});
        			me.riskGraph.reloadData();
        		}
    		}
        	
        });

        //风险事件列表页
        me.riskEventGridContainer = Ext.create('Ext.container.Container', {
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_RISKLIST',
            layout: 'fit',
            title: '风险列表',
            onClick: function () {
                if (!me.riskEventGrid) {
                    me.riskEventGrid = Ext.create('FHD.view.risk.cmp.risk.RiskEventGrid', {
                        border: false,
                        navHeight: me.navHeight,
                        formType: me.formType,
                        showRiskAdd: function (p, parentId, name) {
                            me.add(p);
                            me.reRightLayout(p);
                            me.chageRiskNavigationBar(p.id + 'DIV', parentId, name);
                        },
                        showRiskDetail: function (p, parentId, name) {
                        	me.showRiskDetailCallback();
                            me.add(p);
                            me.reRightLayout(p);
                            me.chageRiskNavigationBar(p.id + 'DIV', parentId, name);
                        },
                        goback: function () {
                            me.reRightLayout(me.tabContainer);
                            me.chageNavigationBar(me.currentId,'');
                            me.riskEventGrid.reloadData();
                        }
                    });
                    this.add(me.riskEventGrid);
                    this.doLayout();
                }
                //根据左侧选中节点，初始化数据
                if (me.currentId != '') {
                    me.riskEventGrid.initParams('risk');
                    me.riskEventGrid.changeAddbuttonStatus(me.addButtonDisable);
                    me.riskEventGrid.reloadData(me.currentId);
                }
            }
        });
        
        //历史事件
        me.historyEventGridContainer = Ext.create('Ext.container.Container', {
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_HISTORYRISK',
            layout: 'fit',
            title: '历史事件',
            onClick: function () {
                if (!me.historyEventGrid) {
                    me.historyEventGrid = Ext.create('FHD.view.risk.hisevent.HistoryEventGridPanel', {
                        border: false,
                        navHeight: me.navHeight,
                        showHistoryAdd: function (p, name) {
                            me.add(p);
                            me.reRightLayout(p);
                            me.chageRiskNavigationBar(p.id + 'DIV', me.currentId, name);
                        },
                        showHistoryDetail: function (p, name) {
                            me.add(p);
                            me.reRightLayout(p);
                            me.chageRiskNavigationBar(p.id + 'DIV', me.currentId, name);
                        },
                        goback: function () {
                            me.reRightLayout(me.tabContainer);
                            me.chageNavigationBar(me.currentId, '');
                            me.historyEventGrid.reloadData();
                        }
                    });
                    this.add(me.historyEventGrid);
                    this.doLayout();
                }
                //根据左侧选中节点，初始化数据
                if (me.currentId != '') {
                    me.historyEventGrid.initParams('risk');
                    me.historyEventGrid.reloadData(me.currentId);
                }
            }
        });

        //历史记录页
        me.riskHistoryGridContainer = Ext.create('Ext.container.Container', {
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_HISTORY',
            layout: 'fit',
            title: '历史记录',
            onClick: function () {
                if (!me.riskHistoryGrid) {
                    me.riskHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
                        face: me,
                        type: 'risk',
                        border: false,
                        autoScroll: true,
                        historyCallback: function (data) {
                            me.historyCallback();
                        }
                    });
                    this.add(me.riskHistoryGrid);
                    this.doLayout();
                }
                //根据左侧选中节点，初始化数据
                if (me.currentId != '') {
                    me.riskHistoryGrid.reloadData(me.currentId);
                }
            }
        });

        //信息查看页
        me.riskBasicFormViewContainer = Ext.create('Ext.container.Container', {
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_BASIC',
            layout: 'fit',
            title: '信息查看',
            onClick: function () {
                if (!me.riskBasicFormView) {
                	var editBtn = Ext.create('Ext.button.Button',{
			            text: '修改',//返回按钮
			            iconCls: 'icon-edit',
			            handler: function () {
			            	if (!me.basicInfo) {
			            		me.riskEventAddForm = Ext.create('FHD.view.risk.cmp.form.RiskStorageForm',{
				    	        	navigatorTitle: '基本信息',
				    	        	showbar:false,
				    	        	type: 'rbs',
				    	        	border: false,
				    	        	last:function(){
				    	        		//验证不通过，返回false
						            	var result = me.riskEventAddForm.save(function(data,editflag){
						            		//传递新保存的riskId到下一个面板
						            		me.riskKpiForm.riskId = data.id;
						            	});
						            	return result;	
				    	        	}
				    			});
				    	        me.riskKpiForm = Ext.create("FHD.view.risk.cmp.form.RiskStorageKpiForm",{
				    	        	navigatorTitle: '风险指标'
				    	        });
				    	        me.basicInfo = Ext.create('FHD.ux.layout.StepNavigator', {
				                    items: [me.riskEventAddForm,me.riskKpiForm],
				                    undo: function () {
				                    	me.riskBasicCardPanel.setActiveItem(me.riskBasicFormView);
			        					me.riskBasicCardPanel.doLayout();
			        					me.riskBasicFormView.reloadData(me.currentId);
				                    }
				                });
			                    me.riskBasicCardPanel.add(me.basicInfo);
			                }
			            	me.riskBasicCardPanel.setActiveItem(me.basicInfo);
    						me.riskBasicCardPanel.doLayout();
    						me.basicInfo.navToFirst();
				            me.riskEventAddForm.reloadData(me.currentId);
				            me.riskKpiForm.reloadData(me.currentId);
			            }
			        });
                    me.riskBasicFormView = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
                    	bbar : ['->',editBtn],
                    	face: me,
                        border: false
                    });
                }
                
            	if(!me.riskBasicCardPanel){
            		me.riskBasicCardPanel = Ext.create('FHD.ux.CardPanel',{
            			border: false,
            			items : [me.riskBasicFormView]
            		});
            		this.add(me.riskBasicCardPanel);
                    this.doLayout();
            	}
            	
                //根据左侧选中节点，初始化数据
                if (me.currentId != '') {
                	me.riskBasicCardPanel.setActiveItem(me.riskBasicFormView);
        			me.riskBasicCardPanel.doLayout();
                    me.riskBasicFormView.reloadData(me.currentId);
                }
            }
        });

        me.tabPanel = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
            items: [me.chartAnalyseContainer, me.riskGraphContainer, me.riskEventGridContainer, me.historyEventGridContainer, me.riskHistoryGridContainer, me.riskBasicFormViewContainer],
            listeners: {
                tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                    if (newCard.onClick) {
                        newCard.onClick();
                    }
                }
            }
        });
        me.tabContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
            border: false,
            navHeight: me.navHeight,
            tabpanel: me.tabPanel,
            flex: 1
        });

        Ext.apply(me, {
            border: false,
            items: me.tabContainer
        });
        me.callParent(arguments);
        //初始加载页面
        if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_CHART')){
            me.chartAnalyseContainer.onClick();
		}else if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_FIGURE')){
			me.riskGraphContainer.onClick();
		}else if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_RISKLIST')){
			me.riskEventGridContainer.onClick();
		}else if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_HISTORYRISK')){
			me.historyEventGridContainer.onClick();
		}else if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_HISTORY')){
			me.riskHistoryGridContainer.onClick();
		}else if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_BASIC')){
			me.riskBasicFormViewContainer.onClick();
		}
        
        me.chartAnalyseContainer.onClick();
    },

    reloadData: function (id) {
        var me = this;
        if (id != null) {
            me.currentId = id;
        }
        me.reRightLayout(me.tabContainer);
        var activeTab = me.tabPanel.getActiveTab();
        if (me.chartAnalyseContainer && activeTab.id == me.chartAnalyseContainer.id) {
            me.chartAnalyseCardPanel.reloadData(me.currentId);
        }else if (me.riskGraphContainer && activeTab.id == me.riskGraphContainer.id) {
            me.riskGraph.initParam({
                riskId:me.currentId
            });
			me.riskGraph.reloadData();
        }else if (me.riskEventGridContainer && activeTab.id == me.riskEventGridContainer.id) {
            me.riskEventGrid.initParams('risk');
            me.riskEventGrid.changeAddbuttonStatus(me.addButtonDisable);
            me.riskEventGrid.reloadData(me.currentId);
        } else if (me.riskHistoryGridContainer && activeTab.id == me.riskHistoryGridContainer.id) {
            me.riskHistoryGrid.reloadData(me.currentId);
        } else if (me.riskBasicFormViewContainer && activeTab.id == me.riskBasicFormViewContainer.id) {
            me.riskBasicFormView.reloadData(me.currentId);
        }else if (me.historyEventGridContainer && activeTab.id == me.historyEventGridContainer.id) {
        	me.historyEventGrid.initParams('risk');
            me.historyEventGrid.reloadData(me.currentId);
        }
    },

    //切换显示页面
    reRightLayout: function (c) {
        var me = this;
        me.setActiveItem(c);
        me.doLayout();
    },
    //基本信息页签在ROOT节点不可点击
    riskFormViewDisable: function (visible) {
        var me = this;
        var activeTab = me.tabPanel.getActiveTab();
        if(visible){
	        if (activeTab.id == me.riskBasicFormViewContainer.id || activeTab.id == me.riskGraphContainer.id || activeTab.id == me.historyEventGridContainer.id) {
	            me.tabPanel.setActiveTab(me.chartAnalyseContainer);
	            me.chartAnalyseContainer.onClick();
	        }
        }
        me.riskBasicFormViewContainer.setDisabled(visible);
        //图形分析和历史事件变灰
        me.riskGraphContainer.setDisabled(visible);
        me.historyEventGridContainer.setDisabled(visible);
    }

});