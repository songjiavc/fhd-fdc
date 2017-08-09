Ext.define('FHD.view.SASACdemo.companyReport.CompanyReportFirstMain',{
	extend: 'Ext.panel.Panel',
    alias: 'widget.companyReportFirstMain',
    
    requires: [
              ],
       
    initComponent: function () {
        var me = this;
        me.companyReportFirstGrid = Ext.create('FHD.view.SASACdemo.companyReport.CompanyReportFirstGrid',{
        	region: 'north',
        	margin: '1 1 1 1',
        	height: 150
        });
        
        me.cardPanel = Ext.create('FHD.view.SASACdemo.companyReport.CompanyReportFirstCard',{
        	region: 'center'
        });
        	
        Ext.apply(me, {
        	border:false,
        	layout: 'border',
        	items: [me.companyReportFirstGrid,me.cardPanel]
        });
        
        me.callParent(arguments);
    }

});