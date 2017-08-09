Ext.define('FHD.view.myallfolder.kpifolder.DeptScBasicInfo', {
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
        me.scBasicinfoContainer = Ext.create('FHD.view.kpi.cmp.sc.ScBasicInfoContainer', {
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

        me.comp.push(me.scBasicinfoContainer);

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
			type:'deptsc',
			name: me.paramObj.scname ? me.paramObj.scname : '添加记分卡',
			id: me.paramObj.scid ? me.paramObj.scid : 'newDeptScId',
			containerId: me.id
		})
         me.navigationBar.renderHtml(me.id + 'DIV', data);
         
    },
    //重新加载页面
    reloadData: function () {
        var me = this;
		me.scBasicinfoContainer.reloadData(me.paramObj);
		me.scBasicinfoContainer.scBasicPanel.navToFirst();
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