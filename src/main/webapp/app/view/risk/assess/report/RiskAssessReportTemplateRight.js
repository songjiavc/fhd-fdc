/**
 * @author : 邓广义
 *  风险评价报告模板管理
 */
Ext.define('FHD.view.risk.assess.report.RiskAssessReportTemplateRight',{
 	extend: 'Ext.panel.Panel',
 	alias : 'widget.riskassessreporttemplateright',
    requires : [
                'FHD.view.risk.assess.report.RiskAssessReportTemplateForm',
                'FHD.view.risk.assess.report.RiskAssessReportTemplateGrid'
             ],
	reloadData:function(){},
 	
    initComponent: function () {
    	var me = this;
    	me.gridPanel = Ext.widget('riskassessreporttemplategrid');
    	me.formPanel = Ext.widget('riskassessreporttemplateform');
        Ext.apply(me, {
        	region:'center',
        	layout:{
                align: 'stretch',
                 type: 'vbox'
    		},
    		border: true,
    		items:[me.gridPanel]
        });
    	me.callParent(arguments);
    },
    showGridPanel : function(){
			var me = this;
			me.getLayout().setActiveItem(me.gridPanel);
	 },          
           
	 showFormPanel : function(){
		var me = this;
		me.getLayout().setActiveItem(me.formPanel);
//		me.formulatePlanMainPanel.basicPanel.navToFirst();
	}

});