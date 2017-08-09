/**
 * 风险库org页面
 *
 * @author 张健
 */
Ext.define('FHD.view.risk.cmp.container.storage.StorageOrgContainer', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.storageorgcontainer',

    currentId: '',
    flex: 1,
    navHeight: 22,  //导航条高度，设置为0则为没有导航
    
    //导航条方法链接方法
    navFunId: '',

    //修改导航条方法
    chageNavigationBar: function (nodeId, name) {
        var me = this;
        if (me.navHeight != 0) {
            me.navigationBar.renderHtml(me.tabContainer.id + 'DIV', nodeId, name, 'org', me.navFunId);
        }
    },
    //风险列表相关导航操作
    chageRiskNavigationBar: function (navId, nodeId, name) {
        var me = this;
        if (me.navHeight != 0) {
            me.navigationBar.renderHtml(navId, me.currentId, name, 'org', me.navFunId);
        }
    },
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        //创建导航条
        me.navigationBar = Ext.create('FHD.ux.NavigationBars');

        //风险事件列表页
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
                    me.riskEventGrid.initParams('org');
                    me.riskEventGrid.reloadData(me.currentId);
                }
            }
        });
        
        //历史记录页
//        me.riskHistoryGridContainer = Ext.create('Ext.container.Container', {
//            layout: 'fit',
//            title: '历史记录',
//            onClick: function () {
//                if (!me.riskHistoryGrid) {
//                    me.riskHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
//                        face: me,
//                        type: 'org',
//                        border: false,
//                        autoScroll: true,
//                        historyCallback: function (data) {
//                            me.historyCallback();
//                        }
//                    });
//                    this.add(me.riskHistoryGrid);
//                    this.doLayout();
//                }
//                //根据左侧选中节点，初始化数据
//                if (me.currentId != '') {
//                    me.riskHistoryGrid.reloadData(me.currentId);
//                }
//            }
//        });

        me.tabPanel = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
            items: [me.riskEventGridContainer],//, me.riskHistoryGridContainer
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
        me.riskEventGridContainer.onClick();
    },

    reloadData: function (id) {
        var me = this;
        if (id != null) {
            me.currentId = id;
        }
        me.reRightLayout(me.tabContainer);
        var activeTab = me.tabPanel.getActiveTab();
        if (me.riskEventGridContainer && activeTab.id == me.riskEventGridContainer.id) {
            me.riskEventGrid.initParams('org');
            me.riskEventGrid.reloadData(me.currentId);
        }else if (me.riskHistoryGridContainer && activeTab.id == me.riskHistoryGridContainer.id) {
            me.riskHistoryGrid.reloadData(me.currentId);
        }
    },

    //切换显示页面
    reRightLayout: function (c) {
        var me = this;
        me.setActiveItem(c);
        me.doLayout();
    }

});