/**
 * 主面板
 *
 * @author 王鑫
 */
Ext.define('FHD.view.kpi.cmp.kpi.result.MainPanel', {
    extend: 'Ext.container.Container',
    layout: 'fit',
    border: false,
    isChartOnly:false,
    navData: null,
    operateType: true,
    goback: function () {},
    isGather: false, // 是否为采集数据录入页面
    paramObj: {},//初始化参数对象

    init: function (param) {
        var me = this;
        //创建采集结果历史数据页签
        me.createKpiGatherHostoryTab(param);
        var items;
        if (!me.isChartOnly) {
            //创建指标录入页面
            me.createKpiInputTab(param);
            // 创建图标分析页签
            me.createChartAnalyse(param);

            me.createRiskAnalyse(param);

            if(me.riskAnalysePanelContainer){
        		me.riskAnalysePanelContainer.onClick();
        	}
        
            items = [
                me.riskAnalysePanelContainer,
                me.resultMainPanelContainer,
                me.ChartAnalyseContainer,
                me.tablePanelContainer
            ];
           
        } else {
        	 //创建指标录入页面(代办过来的)
            me.createKpiInputTab(param);
            
            if(me.resultMainPanelContainer){
	        	me.resultMainPanelContainer.onClick();
        	}
        	
            items = [me.resultMainPanelContainer];
        }
        
        
        me.tabPanel = Ext.create('FHD.view.kpi.cmp.kpi.result.resultManTabPanel', {
            flex: 1,
            items: items,
            listeners: {
                tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                    //判断树中是否有选中的元素
                    if (newCard.onClick) {
                        newCard.onClick();
                    }
                    if (newCard.reloadData) {
                        newCard.reloadData();
                    }
                }
            }
        });

        me.tabPanel.setActiveTab(0);

    },

    createKpiInputTab: function (param) {
        var me = this;
        if(me.tablePanelContainer){
        	me.remove(me.tablePanelContainer,true);
        }
        me.tablePanelContainer = Ext.create('Ext.container.Container', {
            title: '历史数据',
            layout: 'fit',
            border: false,
            timeRefresh: true,
            load:function(){
            	//点击时间控件时,刷新指标采集数据录入表单
            	this.onClick();
            },
            onClick: function () {
                me.resultParam = param;
                if (!me.resultParam.paraobj) {
                    me.resultParam.paraobj = {};
                }
                me.resultParam.paraobj.eType = FHD.data.eType;
                me.resultParam.paraobj.kpiname = me.resultParam.kpiname;
                FHD.data.kpiName = me.resultParam.kpiname;
                me.resultParam.paraobj.timeId = me.resultParam.timeId;
                me.resultParam.paraobj.year = FHD.data.yearId;
                me.resultParam.paraobj.isNewValue = FHD.data.isNewValue;
                me.resultParam.paraobj.containerId = me.id;
                me.resultParam.paraobj.isGather = me.isGather;
                FHD.data.edit = false;
                FHD.ajax({
                    async: false,
                    url: __ctxPath + '/kpi/kpi/createtable.f?edit=' + FHD.data.edit,
                    params: {
                        condItem: Ext.JSON.encode(me.resultParam.paraobj)
                    },
                    callback: function (data) {
                        if (data && data.success) {
                        	
                        	if (me.tablePanel&&me.tableMainPanel) {
                                me.tableMainPanel.remove(me.tablePanel, true);
                            }
                            me.tablePanel = Ext.create('FHD.view.kpi.cmp.kpi.result.TablePanel', {
                                html: data.tableHtml,
                                height: 280,
                                pcontainer: me,
                                region:'center'
                            });
                            
                        	
                        	if (me.memomainpanel&&me.tableMainPanel) {
                                me.tableMainPanel.remove(me.memomainpanel, true);
                            }
                        	me.memomainpanel = Ext.create('FHD.view.kpi.cmp.kpi.memo.MemoMainPanel',
							{   
								title:'注释',
					            region:'south',
					            collapsible: true,
					            collapsed:true,
					            floatable:false,
					            maxHeight:200,
					            split: true,
					            kgrid: param.kgrid
					        });
					        
							me.memomainpanel.getData(param.kgrid);
							me.memomainpanel.kgrid = param.kgrid;
							me.memomainpanel.setTitle(param.memoTitle);
							
							if(me.tableMainPanel){
                        		 me.tablePanelContainer.remove(me.tableMainPanel, true);
                        	}
                        	me.tableMainPanel = Ext.create('Ext.panel.Panel',{
                        		layout:'border',
                        		border:false
                        	});
                        	
                        	me.tableMainPanel.add(me.tablePanel);
                        	
							me.tableMainPanel.add(me.memomainpanel);
							
                            me.tablePanelContainer.add(me.tableMainPanel);
                            me.tablePanelContainer.doLayout();

                        }
                    }
                });
            }
        });


    },

    //创建采集结果历史数据页签
    createKpiGatherHostoryTab: function (param) {
        var me = this;
        if(me.resultMainPanelContainer){
        	me.remove(me.resultMainPanelContainer,true);
        }
        me.resultMainPanelContainer = Ext.create('Ext.container.Container', {
            title: '图表分析',
            layout: 'fit',
            border: false,
            onClick: function () {
                //采集结果-历史数据tab
                if (me.resultMainPanel) {
                    me.resultMainPanelContainer.remove(me.resultMainPanel, true);
                }
                me.resultMainPanel = Ext.create('FHD.view.kpi.cmp.kpi.result.ResultMainPanel', {
                    pcontainer: me,
                    goback: me.goback
                }).load(param);
                this.add(me.resultMainPanel);
                this.doLayout();
            }
        });


    },

    //创建风险分析页签
    createRiskAnalyse: function (param) {
        var me = this;
        if(me.riskAnalysePanelContainer){
        	me.remove(me.riskAnalysePanelContainer,true);
        }
        var navHeight = 0;
        if (param.treeId) {
            navHeight = 22;
        }
        me.riskAnalysePanelContainer = Ext.create('Ext.container.Container', {
            title: '风险分析',
            layout: 'fit',
            border: false,
            onClick: function () {
                //采集结果-历史数据tab
                if (me.riskAnalysePanel) {
                    me.riskAnalysePanelContainer.remove(me.riskAnalysePanel, true);
                }
                me.riskAnalysePanel = Ext.create('FHD.view.risk.cmp.risk.RiskAnalysisGridPanel', {
                    type: 'kpi',
                    border: false,
                    operateType: me.operateType,
                    navData: param.navData,
                    autoHeight: true,
                    navHeight: navHeight,
                    showRiskAdd: function (p, name) {
                        if (me.pcontainer) {
                            me.pcontainer.reRightLayout(p);
                            if (param.treeId) {
                                me.navigationBar.renderHtml(p.id + 'DIV', param.navId, name, param.type, param.treeId);
                            }
                        } else {
                            me.up('panel').setActiveItem(p);
                        }
                    },
                    showRiskDetail: function (p, name) {
                        if (me.pcontainer) {
                            me.pcontainer.reRightLayout(p);
                            if (param.treeId) {
                                me.navigationBar.renderHtml(p.id + 'DIV', param.navId, name, param.type, param.treeId);
                            }
                        } else {
                            me.up('panel').setActiveItem(p);
                        }

                    },
                    goback: function () {
                        if (me.pcontainer) {
                            me.pcontainer.reRightLayout(me);
                            me.riskAnalysePanel.reloadData(param.kpiid);
                            if(param.navData) {
		                   		me.reLayoutNavigationBar(param.navData);
		                   	}
                        } else {
                            me.load(param);
                            me.tabPanel.setActiveTab(1);
                            me.up('panel').setActiveItem(me.tabPanel);
                        }
                    },
                    reLayoutNavigationBar:function(data) {
	                   	if(param.navData) {
	                   		me.reLayoutNavigationBar(data);
	                   	}
                    }
                });
                me.riskAnalysePanel.reloadData(param.kpiid);
                this.add(me.riskAnalysePanel);
                this.doLayout();
            }
        });

    },

    //
    createChartAnalyse: function (param) {
        var me = this;
		if(me.ChartAnalyseContainer){
			me.remove(me.ChartAnalyseContainer,true);
		}
        me.ChartAnalyseContainer = Ext.create('Ext.container.Container', {
            title: '图形分析',
            layout: 'fit',
            border: false,
            reloadData: function () {
                if (me.ChartAnalyse.reloadData) {
                    me.ChartAnalyse.reloadData();
                }
            },
            onClick: function () {
                //采集结果-历史数据tab
                if (me.ChartAnalyse) {
                    me.ChartAnalyseContainer.remove(me.ChartAnalyse, true);
                }
                me.ChartAnalyse = Ext.create('FHD.view.comm.graph.GraphRelaKpiPanel', {
                    extraParams: {
                        kpiId: param.kpiid
                    }
                });
                this.add(me.ChartAnalyse);
                this.doLayout();
            }
        });


    },
    //
    load: function (param) {
        var me = this;
//        if (me.resultMainPanel != null) {
//            me.removeAll(true);
//        }
        if(me.riskAnalysePanel != null) {
        	me.removeAll(true);
        }
        var data = null;
        if(me.navData) {
        	data = [];
        	for (i=0;i<me.navData.length;i++){
        		data.push(me.navData[i]);
        	}
        	
        	data.push({
        		id: param.kpiid,
        		name: param.kpiname,
        		type:  'kpiGraph',
        		containerId: me.id
        	})
        }
        param.navData = data;
        me.init(param);

		if(!me.isChartOnly){
			me.navigationBar = Ext.create('FHD.ux.NavigationBars');
	        var nav = {
	            xtype: 'box',
	            height: 22,
	            style: 'border-left: 1px  #99bce8 solid;',
	            html: '<div id="' + me.id + 'DIV" class="navigation"></div>',
	            listeners: {
	                afterrender: function () {
                        if (param.treeId) {
	                        me.navigationBar.renderHtml(me.id + 'DIV', param.navId, param.name, param.type, param.treeId)
	                    }
	                }
	            }
	        };
	        if (param.treeId) {
	            me.add(nav);
	        }
		}
        
        me.add(me.tabPanel);
        return me;
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
            border: true,
            layout: {
                align: 'stretch',
                type: 'vbox'
            },
            items: []
        });

        me.callParent(arguments);
    },
    reloadData:function() {
    	var me = this;
    	me.load(me.paramObj);
    	var data = null;
        if(me.navData) {
        	data = [];
        	for (i=0;i<me.navData.length;i++){
        		data.push(me.navData[i]);
        	}
        	
        	data.push({
        		id: me.paramObj.kpiid,
        		name: me.paramObj.kpiname,
        		type:  'kpiGraph',
        		containerId: me.id
        	})
           me.reLayoutNavigationBar(data);
        }
    },
    setNavData:function(data) {
    	var me = this;
    	me.navData = data;
    }
});