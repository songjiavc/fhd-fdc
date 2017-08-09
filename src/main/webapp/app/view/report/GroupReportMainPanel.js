/*
 * 集团综合报表主面板
 * 
 * ZJ
 * */

Ext.define('FHD.view.report.GroupReportMainPanel', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.groupreportmainpanel',
	requires:[
	          	'FHD.view.report.risk.ReportRiskViewGrid',
	          	'FHD.view.report.risk.GroupTop10RiskPanel'
	         ],
	layout : 'fit',
	
	initComponent: function () {
        var me = this;
        
        
        me.reportTab = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
        	position:'left',
        	plain:false,
            items: [{
            	xtype:'reportriskviewgrid',
            	authority:'ROLE_ALL_INFO_GROUPTABLE_COMPANYRISK',
            	type: 'group',
            	border:false,
            	title:'下级公司风险概况'
            },{
            	xtype:'grouptop10riskpanel',
            	authority:'ROLE_ALL_INFO_GROUPTABLE_COMPANY10RISK',
            	title:'下级公司风险'
            }]
        });

        Ext.apply(me,{
        	items : me.reportTab
        });

        me.callParent(arguments);

    }
});