Ext.define('FHD.view.kpi.homepage.SmDetailAnalysis', {
    extend: 'Ext.container.Container',
    layout: {
                align: 'stretch',
                type: 'vbox'
            },
    title: '',
    border: false,
    paramObj: {},
    autoDestroy: true,
    navData: null,//导航信息
    // 初始化方法
    initComponent: function () {
        var me = this;
		me.createNav();
        //初始化图表分析面板
        me.createSmChartAnalysis();
        //创建目标关联的指标列表
        me.createSmRelaKpiGrid();
        //创建目标历史数据列表
        me.createSmHistoryGrid();
        // 创建图形分析
        me.createGraphAnalyseContainer();
        
        me.createRiskAnalysis();

        if (me.smKpiGridContainer) {
            me.smKpiGridContainer.onClick();
        }
        if (!me.tabpanel) {

            me.tabpanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                plain: true,
                items: [me.smKpiGridContainer, me.riskanalysisgridpanelContainer,me.chartanalysisContainer,me.graphAnalyseContainer, me.strHistoryGridContainer],
                listeners: {
                    tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                        if (newCard.onClick) {
                            newCard.onClick();
                        }
                    }
                }
            });

        }
		
        me.card = Ext.create("FHD.ux.CardPanel",{
        	flex:1,
        	border:false
        });
		me.card.add(me.tabpanel);
        Ext.applyIf(me, {
            items: [
            	me.navObj,
                me.card
            ],
            listeners: {
                destroy: function (me, eOpts) {
                	if(me.newNav) {
                	   me.newNav.destroy();
                	}                	
                    me.el.remove();
                    me.removeAll(true);
                    me = null;
                    if (Ext.isIE) {
                        CollectGarbage();
                    }
                }
            }
        });

        me.callParent(arguments);

    },
    //初始化参数
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    //重新初始化数据
    reloadData: function () {
        var me = this;
    },
    //card重新布局
    reLayout: function (p) {
        var me = this;
        me.reRightLayout(p);
//        me.setActiveItem(p);
    },
    
    createNav:function(){
    	var me = this;
    	me.newNav = Ext.create('FHD.ux.NavigationBar');
        me.navObj = {
            xtype: 'box',
            height: 22,
            style: 'border-left: 1px  #99bce8 solid;',
            html: '<div id="' + me.id + 'DIV" class="navigation"></div>',
            listeners: {
                afterrender: function () {
                	if(me.navData) {
                		me.navData.push({
                			type: 'deptsm',
                			name: me.paramObj.name,
                			id: me.paramObj.smid,
                			containerId: me.id
                		});
                		me.newNav.renderHtml(me.id + 'DIV',me.navData);
                	}
                }
            }
        };
    },
    //初始化图表分析面板
    createSmChartAnalysis: function () {
        var me = this;
        me.chartanalysisContainer = Ext.create('Ext.container.Container', {
            title: '图表分析',
            layout: 'fit',
            border: false,
            onClick: function () {
                if (me.chartanalysisContainer) {
                    me.chartanalysisContainer.remove(me.chartanalysis, true);
                }
                me.chartanalysis = Ext.create('FHD.view.kpi.cmp.chart.ChartMainPanel', {
                    dataType: 'str',
                    //title: '图表分析',
                    border: false
                });

                var paramObj = {
                    objectId: me.paramObj.smid, //目标ID
                    dataType: 'str'
                };
                //初始化图表分析页面
                me.chartanalysis.initParam(paramObj);
                //重新加载图表分析页面
                me.chartanalysis.reloadData();
                this.add(me.chartanalysis);
                this.doLayout();
            }
        });
    },
    //创建目标关联的指标列表
    createSmRelaKpiGrid: function () {
        var me = this;
        me.smKpiGridContainer = Ext.create('Ext.container.Container', {
            title: '度量指标',
            layout: 'fit',
            border: false,
            onClick: function () {
                if (me.smKpiGridContainer) {
                    me.smKpiGridContainer.remove(me.smKpiGrid, true);
                }
                me.smKpiGrid = Ext.create('FHD.view.kpi.cmp.sm.SmKpiGrid', {
                    autoHeight: true,
                    navData: me.navData,
                    reRightLayout: function (p) {
                    	var panelId = p.items.items[0].id;
                    	if(panelId.indexOf("resultManTabPanel")==-1 && p.needExtraNav){
	                    		me.kpiContainer = Ext.create("Ext.container.Container",{
		                    		layout: {
						                align: 'stretch',
						                type: 'vbox'
						            }
	                    		});
		                    	me.navKpiObj = {
						            xtype: 'box',
						            height: 18,
						            style: 'border-left: 1px  #99bce8 solid;',
						            html: '<div id="' + me.id + 'DIV" class="navigation"></div>'
						        };
						        me.kpiContainer.add(me.navKpiObj);
						        me.kpiContainer.add(p);
						        me.card.setActiveItem(me.kpiContainer);
                    	}
                    	else{
                    		me.card.setActiveItem(p);
                    	}
                    },
                   reLayoutNavigationBar:function(param) {
                   	if(me.navData) {
                   		me.newNav.renderHtml(me.id + 'DIV', param);
                   	}
                   },
                   goback: function () {
                   	    if(me.navData) {
                   	    	  me.newNav.renderHtml(me.id + 'DIV',me.navData);
                   	    }                	  
                        me.card.setActiveItem(me.tabpanel);
                        me.smKpiGrid.store.load();
                    },
                    undo: function () {  
                    	if(me.navData) {
                    	   me.newNav.renderHtml(me.id + 'DIV',me.navData);
                    	}
                	 
                        me.card.setActiveItem(me.tabpanel);
                        me.smKpiGrid.store.load();
                    }
                });
                me.smKpiGrid.initParam(me.paramObj);
                //重新加载度量指标
                me.smKpiGrid.reloadData();
                this.add(me.smKpiGrid);
                this.doLayout();
            }
        });
    },
    //创建目标历史数据列表
    createSmHistoryGrid: function () {
        var me = this;
        me.strHistoryGridContainer = Ext.create('Ext.container.Container', {
            title: '历史数据',
            layout: 'fit',
            border: false,
            onClick: function () {
                if (me.strHistoryGridContainer) {
                    me.strHistoryGridContainer.remove(me.strHistoryGrid, true);
                }
                me.strHistoryGrid = Ext.create('FHD.view.kpi.cmp.sc.ScSmHistoryGrid', {});
                var paramObj = {
                    objectId: me.paramObj.smid, //目标ID
                    type: 'str'
                };
                me.strHistoryGrid.initParam(paramObj);
                me.strHistoryGrid.reloadData();
                this.add(me.strHistoryGrid);
                this.doLayout();
            }
        });
    },
    //
    createSmBasicInfoForm: function () {
        var me = this;
        me.smBasicInfoFormContainer = Ext.create('Ext.container.Container', {
            title: '基本信息',
            layout: 'fit',
            border: false,
            onClick: function () {
                if (me.smBasicInfoFormContainer) {
                    me.smBasicInfoFormContainer.remove(me.smBasicInfoForm, true);
                }
                me.smBasicInfoForm = Ext.create('FHD.view.kpi.cmp.sm.SmBasicInfoForm', {});
                var paramObj = {
                    smid: me.paramObj.smid //目标ID
                };
                me.smBasicInfoForm.initParam(paramObj);
                me.smBasicInfoForm.reloadData();
                this.add(me.smBasicInfoForm);
                this.doLayout();
            }
        });

    },
    createGraphAnalyseContainer: function () {
        var me = this;
    	me.graphAnalyseContainer = Ext.create('Ext.container.Container',{
    	    title:'图形分析',
    	    layout: 'fit',
            border: false,
            onClick: function () {
            	if(!me.graphAnalysePanel){
            		me.graphAnalysePanel = Ext.create('FHD.view.comm.graph.GraphRelaStrategyMapPanel',{
    	                 title:'图形分析',
    	                 extraParams: {
    	                 	strategyMapId: me.paramObj.smid
    	                 }
    	            })
            	}
            	me.graphAnalysePanel.reloadData();
                //me.strMainTabPanel.activeType = "graphAnalyse";
                this.add(me.graphAnalysePanel);
                this.doLayout();
            }
    	});

    },
    createRiskAnalysis: function () {
        var me = this;
        me.riskanalysisgridpanelContainer = Ext.create('Ext.container.Container', {
            title: '风险分析',
            layout: 'fit',
            border: false,
            onClick: function () {
                if (me.riskanalysisgridpanelContainer) {
                    me.riskanalysisgridpanelContainer.remove(me.riskanalysisgridpanel, true);
                }
                if(me.navData){
	        		var data = [];
	        		Ext.Array.push(data,me.navData)
        		}
                me.riskanalysisgridpanel = Ext.create('FHD.view.risk.cmp.risk.RiskAnalysisGridPanel', {
                    type: 'sm',
                    navHeight: 0,
                    navData: data,
                    showRiskAdd: function (p,name) {
                        me.card.setActiveItem(p);
                    },
                    showRiskDetail: function (p) {
                        me.card.setActiveItem(p,name);
                    },
                    goback: function () {
                        me.card.setActiveItem(me.tabpanel);
                        me.riskanalysisgridpanel.reloadData(me.paramObj.smid);
                    },
                    reLayoutNavigationBar:function(param) {
	                   	if(param) {
	                   		me.newNav.renderHtml(me.id + 'DIV', param);
	                   	}
                    }
                });
                me.riskanalysisgridpanel.initParam('sm');
                me.riskanalysisgridpanel.reloadData(me.paramObj.smid);
                this.add(me.riskanalysisgridpanel);
                this.doLayout();
            }
        });
    }
});