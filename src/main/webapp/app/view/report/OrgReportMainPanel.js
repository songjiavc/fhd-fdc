/*
 * 部门综合报表主面板
 * 
 * ZJ
 * */

Ext.define('FHD.view.report.OrgReportMainPanel', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.orgreportmainpanel',
	requires:[
	          	'FHD.view.report.risk.ReportRiskViewGrid',
	          	'FHD.view.report.risk.OrgTop10RiskPanel'
	         ],
	          
	layout : 'fit',
	
	initComponent: function () {
        var me = this;
        
        
        me.reportTab = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
        	position:'left',
        	plain:false,
        	items: [{
            	xtype:'reportriskviewgrid',
            	authority:'ROLE_ALL_INFO_TABLE_DEPRISK',
            	border:false,
            	type: 'org',
            	title:'部门风险概况'
            }
//        	,{
//            	xtype:'orgtop10riskpanel',
//            	authority:'ROLE_ALL_INFO_TABLE_DEP10RISK',
//            	title:'部门十大风险'
//            }
        	]
        });

        Ext.apply(me, {
        	items : me.reportTab
        });

        me.callParent(arguments);

    }
});