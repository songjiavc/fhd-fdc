/**
 * 结果分析sm页面
 *
 * @author 张健
 */
Ext.define('FHD.view.risk.cmp.container.analyse.AnalyseSmContainer', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.analysesmcontainer',

    currentId: '',
    flex: 1,
    navHeight: 22,  //导航条高度，设置为0则为没有导航
    
    //导航条方法链接方法
    navFunId: '',

    //历史事件后续处理方法
    historyCallback: function () {

    },

    //修改导航条方法
    chageNavigationBar: function (nodeId, name) {
        var me = this;
        if (me.navHeight != 0) {
            me.navigationBar.renderHtml(me.tabContainer.id + 'DIV', nodeId, name, 'sm', me.navFunId);
        }
    },
    //风险列表相关导航操作
    chageRiskNavigationBar: function (navId, nodeId, name) {
        var me = this;
        if (me.navHeight != 0) {
            me.navigationBar.renderHtml(navId, me.currentId, name, 'sm', me.navFunId);
        }
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        //创建导航条
        me.navigationBar = Ext.create('FHD.ux.NavigationBars');

        var treeArr = [];
        //图表分析
        if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_TARGET_CHART')){
            me.chartAnalyseContainer = Ext.create('Ext.container.Container', {
                title: '图表分析',
                layout: 'fit',
                onClick: function () {
                    if (!me.chartAnalyseCardPanel) {
                        me.chartAnalyseCardPanel = Ext.create('FHD.view.risk.cmp.chart.ChartAnalyseCardPanel', {
                            face: me,
                            type: 'sm',
                            schm: me.schm,
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
            treeArr.push(me.chartAnalyseContainer);
        }

        //风险事件列表页
        if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_TARGET_RISKLIST')){
            me.riskEventGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '风险列表',
                onClick: function () {
                    if (!me.riskEventGrid) {
                        me.riskEventGrid = Ext.create('FHD.view.risk.cmp.risk.RiskEventGrid', {
                            face: me,
                            border: false,
                            navHeight: me.navHeight,
                            schm: me.schm,
                            showRiskAdd: function (p, parentId, name) {
                                me.add(p);
                                me.reRightLayout(p);
                                me.chageRiskNavigationBar(p.id + 'DIV', parentId, name);
                            },
                            showRiskDetail: function (p, parentId, name) {
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
                        me.riskEventGrid.initParams('sm');
                        me.riskEventGrid.reloadData(me.currentId);
                    }
                }
            });
            treeArr.push(me.riskEventGridContainer);
        }
        
        //历史记录页
        if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_TARGET_HISTORY')){
            me.riskHistoryGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '历史记录',
                onClick: function () {
                    if (!me.riskHistoryGrid) {
                        me.riskHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
                            face: me,
                            type: 'sm',
                            border: false,
                            autoScroll: true,
                            schm: me.schm,
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
            treeArr.push(me.riskHistoryGridContainer);
        }

        me.tabPanel = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
            items: treeArr,
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
        if(treeArr.length>0){
        	treeArr[0].onClick();
        }
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
        } else if (me.riskEventGridContainer && activeTab.id == me.riskEventGridContainer.id) {
            me.riskEventGrid.initParams('sm');
            me.riskEventGrid.reloadData(me.currentId);
        } else if (me.riskHistoryGridContainer && activeTab.id == me.riskHistoryGridContainer.id) {
            me.riskHistoryGrid.reloadData(me.currentId);
        } else if (me.riskBasicFormViewContainer && activeTab.id == me.riskBasicFormViewContainer.id) {
            me.riskBasicFormView.reloadData(me.currentId);
        }
    },

    //切换显示页面
    reRightLayout: function (c) {
        var me = this;
        me.setActiveItem(c);
        me.doLayout();
    }
});