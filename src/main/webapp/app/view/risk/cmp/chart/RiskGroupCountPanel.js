/**
 * 分类分析
 *
 * @author ZJ
 */
Ext.define('FHD.view.risk.cmp.chart.RiskGroupCountPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.riskgroupcountpanel',

    autoScroll: true,
    currentId: '',
    type: '',
    //评估计划
    assessPlanId: '',
    initComponent: function () {
        var me = this;
        me.chartcontainer = Ext.create('FHD.ux.FusionChartPanel', {
            chartType: 'Doughnut2D',
            border: false,
            xmlData: ''
        });
        Ext.applyIf(me, {
            layout: 'fit',
            items: me.chartcontainer
        });
        me.callParent(arguments);
    },

    reloadData: function (id) {
        var me = this;
        if (id != null) {
            me.currentId = id;
        }
        FHD.ajax({
            url: __ctxPath + '/cmp/risk/getriskgroupcount.f',
            params: {
                id: me.currentId,
                type: me.type,
                assessPlanId: me.assessPlanId
            },
            callback: function (result) {
                me.chartcontainer.loadXMLData(result.data);
            }
        })
    },

    initParams: function (type) {
        var me = this;
        me.type = type;
    }

})