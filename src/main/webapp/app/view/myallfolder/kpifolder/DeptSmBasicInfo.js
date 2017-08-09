Ext.define('FHD.view.myallfolder.kpifolder.DeptSmBasicInfo', {
    extend: 'Ext.container.Container',
    layout: 'fit',

    paramObj: {},

    requires: [],

    isNavigator: true,

    initComponent: function () {
        var me = this;
        me.comp = [];
        //导航对象
        if(me.isNavigator) {
       	 me.navigationBar = Ext.create('FHD.ux.NavigationBar');
        }

        //记分卡基本信息
        me.smBasicinfoContainer = Ext.create('FHD.view.kpi.cmp.sm.SmBasicInfoContainer', {
        	flex:1,
        	undo:me.undo
        });
        if (me.isNavigator) {
            me.comp.push({
                xtype: 'box',
                height: 40,
                style: 'border-left: 1px  #99bce8 solid;',
                html: '<div id="' + me.id + 'DIV" class="navigation"></div>',
                listeners: {
                    afterrender: function () {
                        me.refreshNavigator();
                    }
                }
            });
        }

        me.comp.push(me.smBasicinfoContainer);

        Ext.apply(me, {
            isNavigator: me.isNavigator,
            layout: {
                align: 'stretch',
                type: 'vbox'
            },
            items: me.comp
        });

        me.callParent(arguments);

    },
    //刷新导航
    refreshNavigator: function () {
         var me = this;
 		 var data = [];
		 for(i=0;i<me.navData.length;i++) {
			data.push(
			me.navData[i]
			);
		};
		data.push({
			type:'deptsm',
			name: me.paramObj.smname ? me.paramObj.smname : '添加目标',
			id: me.paramObj.smid ? me.paramObj.smid : 'newDeptSmId',
			containerId: me.id
		})
         me.navigationBar.renderHtml(me.id + 'DIV', data);
    },
    //重新加载页面
    reloadData: function () {
        var me = this;
		me.smBasicinfoContainer.reloadData(me.paramObj);
		me.smBasicinfoContainer.smBasicPanel.navToFirst();
		me.refreshNavigator();
    },
    //记分卡基本信息面板返回事件
    undo:function(){
    	var me = this;
    },
    //重新布局
    reRightLayout:function(){
    	var me = this;
    },
    //初始化参数
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    }

});