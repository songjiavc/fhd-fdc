/**
 * 
 * 风险整理上下面板
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.quaAssessPanel',
    
    requires: [
               'FHD.view.risk.assess.utils.InfoNav',
               'FHD.view.risk.assess.quaAssess.QuaAssessCardNew'
              ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        me.id = 'quaAssessPanelId';
        me.quaAssessCard = Ext.widget('quaAssessCardNew',{executionId : me.executionId, businessId : me.businessId, quaAssessMan : me.quaAssessMan, winId : me.winId});
        me.infoNavTempPanel = Ext.create('Ext.panel.Panel',{border:false});
        
        Ext.apply(me, {
        	border:false,
        	region:'center',
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
            items: [me.infoNavTempPanel, me.quaAssessCard]
        });

        me.callParent(arguments);
    }

});