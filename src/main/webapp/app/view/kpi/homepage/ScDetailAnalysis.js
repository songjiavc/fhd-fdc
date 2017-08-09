Ext.define('FHD.view.kpi.homepage.ScDetailAnalysis', {
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
        // 创建导航
        me.createNav();
        //创建记分卡关联的指标列表
        me.createScRelaKpiGrid();
        //初始化图表分析面板
        me.createScChartAnalysis();
        //创建记分卡历史数据列表
        me.createScHistoryGrid();
        // 创建图形分析
        me.createGraphAnalyseContainer();
        //创建风险分析
        me.createRiskAnalysis();
        if (me.scKpiGridContainer) {
            me.scKpiGridContainer.onClick();
        }
        if (!me.tabpanel) {

            me.tabpanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                plain: true,
                items: [me.scKpiGridContainer, me.riskanalysisgridpanelContainer, me.chartanalysisContainer,me.graphAnalyseContainer, me.scHistoryGridContainer],
                listeners: {
                    tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                        if (newCard.onClick) {
                            newCard.onClick();
                        }
                    }
                }
            });

        }
        me.card = Ext.create("FHD.ux.CardPanel", {
            flex: 1,
            border: false
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
    },
    createNav: function () {
        var me = this;
        me.newNav = Ext.create('FHD.ux.NavigationBar');
        me.navObj = {
            xtype: 'box',
            height: 22,
            style: 'border-left: 1px  #99bce8 solid;',
            html: '<div id="' + me.id + 'DIV" class="navigation"></div>',
            listeners: {
                afterrender: function () {
                    if (me.navData) {
                        me.navData.push({
                            type: me.paramObj.type,
                            name: me.paramObj.name,
                            id: me.paramObj.scid,
                            containerId: me.id
                        });
                        me.newNav.renderHtml(me.id + 'DIV', me.navData);
                    }
                }
            }
        };
    },
    //初始化图表分析面板
    createScChartAnalysis: function () {
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
                    dataType: 'sc',
                    border: false
                });

                var paramObj = {
                    objectId: me.paramObj.scid, //目标ID
                    dataType: 'sc'
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
    //创建记分卡关联的指标列表
    createScRelaKpiGrid: function () {
        var me = this;
        me.scKpiGridContainer = Ext.create('Ext.container.Container', {
            title: '度量指标',
            layout: 'fit',
            border: false,
            onClick: function () {
                if (me.scKpiGridContainer) {
                    me.scKpiGridContainer.remove(me.scKpiGrid, true);
                }
                me.scKpiGrid = Ext.create('FHD.view.kpi.cmp.sc.ScKpiGrid', {
                    autoHeight: true,
                    navData: me.navData,
                    reRightLayout: function (p) {
                        var panelId = p.items.items[0].id;
                        if (panelId.indexOf("resultManTabPanel") == -1 && p.needExtraNav) {
                            me.kpiContainer = Ext.create("Ext.container.Container", {
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
                        } else {
                            me.card.setActiveItem(p);
                        }
                    },
                    reLayoutNavigationBar: function (param) {
                        if (me.navData) {
                            me.newNav.renderHtml(me.id + 'DIV', param);
                        }
                    },
                    goback: function () {
                        if (me.navData) {
                            me.newNav.renderHtml(me.id + 'DIV', me.navData);
                        }
                        me.card.setActiveItem(me.tabpanel);
                        me.scKpiGrid.store.load();
                    },
                    undo: function () {
                        if (me.navData) {
                            me.newNav.renderHtml(me.id + 'DIV', me.navData);
                        }
                        me.card.setActiveItem(me.tabpanel);
                        me.scKpiGrid.store.load();
                    }
                });
                me.scKpiGrid.initParam(me.paramObj);
                //重新加载度量指标
                me.scKpiGrid.reloadData();
                this.add(me.scKpiGrid);
                this.doLayout();
            }
        });

    },
    //创建目标历史数据列表
    createScHistoryGrid: function () {
        var me = this;
        me.scHistoryGridContainer = Ext.create('Ext.container.Container', {
            title: '历史数据',
            layout: 'fit',
            border: false,
            onClick: function () {
                if (me.scHistoryGridContainer) {
                    me.scHistoryGridContainer.remove(me.scHistoryGrid, true);
                }
                me.scHistoryGrid = Ext.create('FHD.view.kpi.cmp.sc.ScSmHistoryGrid', {});
                var paramObj = {
                    objectId: me.paramObj.scid, //目标ID
                    type: 'sc'
                };
                me.scHistoryGrid.initParam(paramObj);
                me.scHistoryGrid.reloadData();
                this.add(me.scHistoryGrid);
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
            		me.graphAnalysePanel = Ext.create('FHD.view.comm.graph.GraphRelaCategoryPanel',{
    	                 title:'图形分析',
    	                 extraParams: {
    	                 	categoryId: me.paramObj.scid
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
                    type: 'sc',
                    navData: me.navData,
                    navHeight: '0',
                    containerheight: FHD.getCenterPanelHeight(),
                    showRiskAdd: function (p) {
                        me.card.setActiveItem(p);
                    },
                    showRiskDetail: function (p) {
                        me.card.setActiveItem(p);
                    },
                    goback: function () {
                        me.reLayout(me.tabpanel);
                        me.riskanalysisgridpanel.reloadData(me.paramObj.scid);
                    },
                    reLayoutNavigationBar:function(param) {
	                   	if(param) {
	                   		me.newNav.renderHtml(me.id + 'DIV', param);
	                   	}
                    }
                });
                me.riskanalysisgridpanel.initParam('sc');
                me.riskanalysisgridpanel.reloadData(me.paramObj.scid);
                this.add(me.riskanalysisgridpanel);
                this.doLayout();
            }
        });

    }
});