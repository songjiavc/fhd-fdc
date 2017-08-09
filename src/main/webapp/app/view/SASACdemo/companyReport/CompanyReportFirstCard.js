Ext.define('FHD.view.SASACdemo.companyReport.CompanyReportFirstCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.companyReportFirstCard',
    
    requires: [
              ],
       
	showCompanyReportHistoryGrid : function(){
		var me = this;
		me.down("[name='analysis']").toggle(true);
		me.down("[name='influence']").toggle(false);
		me.getLayout().setActiveItem(me.companyReportHistoryGrid);
	 },          
              
              
    showCompanyReportAnalysGrid : function(){
  		var me = this;
  		me.down("[name='influence']").toggle(true);
  		me.down("[name='analysis']").toggle(false);
  		me.getLayout().setActiveItem(me.companyReportAnalysGrid);
  	},
              
    initComponent: function () {
        var me = this;
        
        me.companyReportHistoryGrid = Ext.create('FHD.view.SASACdemo.companyReport.CompanyReportHistoryGrid');
        
        me.companyReportAnalysGrid = Ext.create('FHD.view.SASACdemo.companyReport.CompanyReportAnalysGrid');
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
        	tbar: [
				  { xtype: 'button', 
				    text: '原因分析',
				    name: 'analysis',
                    icon: __ctxPath + '/images/icons/trend.gif',
                    handler: function () {
                        me.showCompanyReportHistoryGrid();
                    }},'-',
				  { xtype: 'button', 
				    text: '影响风险',
				    name: 'influence',
                    icon: __ctxPath + '/images/icons/chart_bar.png',
                    scope:me,
                    handler: function () {
                        me.showCompanyReportAnalysGrid();
                    }}
				],
			items:[me.companyReportHistoryGrid, me.companyReportAnalysGrid]
        });
        
        me.callParent(arguments);
        me.down("[name='analysis']").toggle(true);
    }

});