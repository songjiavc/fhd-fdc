Ext.define('FHD.view.myallfolder.SF2riskView.RiskViewCard',{
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.riskviewcard',


    initComponent: function () {
        var me = this;
        //风险排序类表
        me.deptRisksGrid = Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel', {
          face: me,
          type: 'org',
          border: false
        });
        Ext.apply(me, {
            border:false,
            activeItem : 0,
            items: [me.deptRisksGrid]
        });

        me.callParent(arguments);
    }

});