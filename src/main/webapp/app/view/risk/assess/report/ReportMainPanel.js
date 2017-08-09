/**
 * @author : 元杰
 *  报告管理主面板
 */
Ext.define('FHD.view.risk.assess.report.ReportMainPanel',{
 	extend: 'Ext.panel.Panel',
 	alias : 'widget.reportmainpanel',
    requires : [
        'FHD.view.risk.assess.report.ReportTree',
        'FHD.view.risk.assess.report.ReportRightPanel'
     ],
	reloadData:function(){},
 	
    initComponent: function () {
    	var me = this;
    	
    	me.treePanel = Ext.widget('reporttree');
    	me.rightPanel = Ext.widget('reportrightpanel');
    	
    	Ext.apply(me, {
            border:false,
            style:{
            	border:'none'
            },
            frame: false,
     		layout: {
     	        type: 'border',
     	        padding: '0 0 5	0'
     	    },
     	    items:[me.treePanel,me.rightPanel]
        });
    	
        me.callParent(arguments);
    }

});