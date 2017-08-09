Ext.define('FHD.view.SASACdemo.homepage.ShowRiskStatusTabPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.showRiskStatusTabPanel',
    
    requires: [
    ],
    
    showKpiRiskWaringGrid : function(){
  		var me = this;
  		me.down("[name='kiprisk']").toggle(true);
		me.down("[name='mostrisk']").toggle(false);
  		me.getLayout().setActiveItem(me.kpiRiskWaringGrid);
  	},
  	
  	showMajorRiskAnalysisGrid : function(){
  		var me = this;
  		me.down("[name='mostrisk']").toggle(true);
		me.down("[name='kiprisk']").toggle(false);
  		me.getLayout().setActiveItem(me.majorRiskAnalysisGrid);
  	},
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.kpiRiskWaringGrid = Ext.create('FHD.view.SASACdemo.homepage.KpiRiskWaringGrid');//列表
    	
    	me.majorRiskAnalysisGrid = Ext.create('FHD.view.SASACdemo.homepage.MajorRiskAnalysisGrid');
    	
    	Ext.apply(me, {
        	border:false,
        	activeItem : 0,
        	tbar: [
				  { xtype: 'button', 
				    text: '关键业绩指标风险预警',
				    name: 'kiprisk',
                    icon: __ctxPath + '/images/icons/trend.gif',
                    handler: function () {
                        me.showKpiRiskWaringGrid();
                    }},'-',
				  { xtype: 'button', 
				    text: '重大分析预测分析',
				    name: 'mostrisk',
                    icon: __ctxPath + '/images/icons/chart_bar.png',
                    handler: function () {
                        me.showMajorRiskAnalysisGrid();
                    }}
				],
        	items: [me.kpiRiskWaringGrid,me.majorRiskAnalysisGrid]
        });
    	
        me.callParent(arguments);
        me.down("[name='kiprisk']").toggle(true);
    }
});