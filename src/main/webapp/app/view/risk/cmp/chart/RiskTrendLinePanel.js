/**
 * 历史趋势
 *
 * @author ZJ
 */
Ext.define('FHD.view.risk.cmp.chart.RiskTrendLinePanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.risktrendlinepanel',

    autoScroll: true,
    currentId: '',
    type: '',
    height: FHD.getCenterPanelHeight(),
    initComponent: function () {
        var me = this;
        me.chartcontainer = Ext.create('FHD.ux.FusionChartPanel', {
            chartType: 'MSLine',
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
            url: __ctxPath + '/cmp/risk/getrisktrendline.f',
            params: {
                id: me.currentId,
                type: me.type
            },
            callback: function (result) {
                if (result && result.success) {
                    me.chartcontainer.loadXMLData(result.data);
                }
            }
        })
    },

    initParams: function (type) {
        var me = this;
        me.type = type;
    }

})