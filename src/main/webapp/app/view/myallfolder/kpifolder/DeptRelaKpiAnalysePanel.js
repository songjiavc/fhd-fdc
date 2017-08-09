Ext.define('FHD.view.myallfolder.kpifolder.DeptRelaKpiAnalysePanel',{
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
    	// 创建新导航
//    	me.createNav();
    	// 创建指标历史数据分析组件
    	me.createKpiHistoryDataAnalyse();
    	// 创建导航信息下面的Card面板 
        me.card = Ext.create("FHD.ux.CardPanel",{
        	flex:1,
        	border:false
        });
        me.card.add(me.mainPanel);
        Ext.applyIf(me, {
            items: [
            	me.navObj,
                me.card
            ],
            listeners: {
                destroy: function (me, eOpts) {
                	me.newNav.destroy();
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
    /*
    createNav:function() {
    	var me = this;
    	me.newNav = Ext.create('FHD.ux.NavigationBar');
        me.navObj = {
                xtype: 'box',
                height: 22,
                style: 'border-left: 1px  #99bce8 solid;',
                html: '<div id="' + me.id + 'DIV" class="navigation"></div>',
                listeners: {
                    afterrender: function () {
                    	    var data = null;
					    	if(me.navData) {
					    		data = [];
					    		for(i = 0;i<me.navData.length;i++) {
					    			data.push(me.navData[i]);
					    		}
								data.push({
										type: 'deptkpi',
										name: me.paramObj.kpiname,
										id: me.paramObj.kpiid,
										containerId: me.mainPanel.id
								 });
					    	}
                    		me.newNav.renderHtml(me.id + 'DIV',data);
                    	}
                    }
                }
    },
    */
    createKpiHistoryDataAnalyse:function() {
    	var me = this;

        if (me.mainPanel == null) {
            me.mainPanel = Ext.create('FHD.view.kpi.cmp.kpi.result.MainPanel', {
                pcontainer: me,
                goback: me.goback,
                navData : me.navData,
//                reLayoutNavigationBar:function(param) {
//                	me.newNav.renderHtml(me.id+ 'DIV',param);
//                },
	            reRightLayout: function(p) {
	             	me.card.setActiveItem(p);
	            },
	            go:function() {
        	    	var data = null;
			        if(me.navData) {
						data = [];
						for(i = 0;i<me.navData.length;i++) {
							data.push(me.navData[i]);
						}
						data.push({
								type: 'deptkpi',
								name: me.paramObj.kpiname,
								id: me.paramObj.kpiid,
								containerId: me.id
						 });
					}
				    me.mainPanel.load(me.paramObj);
	            	me.card.setActiveItem(me.mainPanel);
//	            	me.newNav.renderHtml(me.id+ 'DIV',data);
	            }
            });
        }
    },
    reLoadData: function() {
    	var me = this;
        me.mainPanel.load(me.paramObj);
    	var data = null;
    	 if(me.navData) {
    		data = [];
    		for(i = 0;i<me.navData.length;i++) {
    			data.push(me.navData[i]);
    		}
			data.push({
					type: 'deptkpi',
					name: me.paramObj.kpiname,
					id: me.paramObj.kpiid,
					containerId: me.mainPanel.id
			 });
    	 }
		me.newNav.renderHtml(me.id + 'DIV',data);
    },
    reRightLayout:function(p) {
    	var me = this;
	    me.card.setActiveItem(p);
	}
})