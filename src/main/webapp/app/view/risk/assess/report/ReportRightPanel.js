/**
 * @author : 元杰
 *  报告右面面板
 */
Ext.define('FHD.view.risk.assess.report.ReportRightPanel',{
 	extend: 'Ext.panel.Panel',
 	alias : 'widget.reportrightpanel',
    requires : [
        'FHD.view.risk.assess.report.ReportGrid'
     ],
	reloadData:function(){},
 	
    initComponent: function () {
    	var me = this;
    	me.gridPanel = Ext.widget('reportgrid');
        Ext.apply(me, {
        	region:'center',
        	layout:{
                align: 'stretch',
                 type: 'vbox'
    		},
    		border: false,
    		items:[me.gridPanel]
        });
    	me.callParent(arguments);
    }
});