/**
 * 结果分析risk页面
 *
 * @author 张健
 */
Ext.define('FHD.view.risk.cmp.container.RiskHistoryContainer', {
    extend: 'Ext.container.Container',
    alias: 'widget.riskhistorycontainer',

    currentId: '',
    type : '',

    // 初始化方法
    initComponent: function () {
        var me = this;

        me.riskTrendLinePanel = Ext.create('FHD.view.risk.cmp.chart.RiskTrendLinePanel',{
        	type : me.type,
        	border:false,
        	flex : 1
        });
        
        me.riskHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
            type : me.type,
            border: false,
            pagable : false,
            autoScroll: true,
        	flex : 1,
            callback: function (data) {
            	me.riskTrendLinePanel.reloadData(me.currentId);
            }
        });


        Ext.apply(me, {
            border: false,
            layout : {
            	type : 'vbox',
            	align : 'stretch'
            },
            items: [me.riskHistoryGrid,me.riskTrendLinePanel]
        });
        me.callParent(arguments);
        
    },

    reloadData: function (id) {
        var me = this;
        if (id != null) {
            me.currentId = id;
        }
        me.riskHistoryGrid.reloadData(me.currentId);
        me.riskTrendLinePanel.reloadData(me.currentId);
    }

});