Ext.define('FHD.view.myallfolder.kpifolder.KpiEditMainPanel',{
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
    	me.createNav();
    	// 创建指标详细信息标记面板
    	me.createKpiMain();
    	// 创建导航信息下面的Card面板 
        me.card = Ext.create("FHD.ux.CardPanel",{
        	flex:1,
        	border:false
        });
        me.card.add(me.kpiMainPanel);
        Ext.applyIf(me, {
            items: [
            	me.navObj,
                me.card
            ],
            listeners: {
                destroy: function (me, eOpts) {
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
    createNav:function() {
    	var me = this;
    	me.newNav = Ext.create('FHD.ux.NavigationBar');
        me.navObj = {
                xtype: 'box',
                height: 40,
                style: 'border-left: 1px  #99bce8 solid;',
                html: '<div id="' + me.id + 'DIV" class="navigation"></div>',
                listeners: {
                    afterrender: function () {
					    	if(me.navData) {
							    var data = [];
								for(i = 0;i<me.navData.length;i++) {
									data.push(me.navData[i]);
								}
							    data.push({
								type: 'deptkpiEdit',
								name: me.paramObj.kpiname ? me.paramObj.kpiname : '添加指标',
								id: me.paramObj.kpiId ? me.paramObj.kpiId : 'newkpiId',
								containerId: me.kpiMainPanel.id
							    });
							    me.newNav.renderHtml(me.id + 'DIV',data);
					    	}
                    		
                    	}
                    }
                }
    },
    createKpiMain:function() {
    	var me = this;
        me.kpiMainPanel = Ext.create('FHD.view.kpi.cmp.kpi.KpiMain', {
            pcontainer: me,
            paramObj: me.paramObj,
            undo: me.undo
        });
    },
    undo: function() {    	
    },
    reLoadData: function() {
    	//var me = this;   	
		//me.newNav.renderHtml(me.id + 'DIV',data);
    },
    reRightLayout:function(p) {
    	var me = this;
	    me.card.setActiveItem(p);
	}
    
})