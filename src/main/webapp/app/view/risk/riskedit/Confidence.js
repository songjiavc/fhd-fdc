/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.riskedit.Confidence', {
    extend: 'Ext.container.Container',
    alias: 'widget.confidence',
    requires: [
    	'FHD.view.risk.riskedit.ConfidenceKpi',
    	'FHD.view.risk.riskedit.ConfidenceDataCardPanel'
    ],
	layout: {
        type: 'vbox',
        align : 'stretch'
    },
    kpiname : '',
    border : false,
	initComponent: function() {
		//定量
		var me = this;
        me.confidenceKpi = Ext.widget('confidencekpi',{confidenceKpiId : me.kpiId});
        me.confidencedatacardpanel = Ext.widget('confidencedatacardpanel');
        Ext.apply(me, {
            items: [
               me.confidenceKpi,
               me.confidencedatacardpanel
            ]
        });

        me.callParent(arguments);
    },
    delSelf : function(){
    	var self = this;
    	upPanel = self.up('fieldset');
    	upPanel.remove(self);
    }
});