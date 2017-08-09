/*
 * 集团十大风险面板，包括左侧集团树和右侧二级风险排名
 * zhengjunxiang
 */

Ext.define('FHD.view.report.risk.GroupTop10RiskPanel', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.grouptop10riskpanel',
	
	initComponent: function () {
        var me = this;
        
	    Ext.apply(me,{
	    	layout:'fit',
	    	border:false
	    });
        
    	me.callParent(arguments);
	},
	
	onRender:function(){
		var me = this;
        //右侧风险排名
        me.grid = Ext.create("FHD.view.report.risk.Top10RiskGridGroupPanel",{

        });
        me.add(me.grid);
        me.doLayout();
        
    	me.callParent(arguments);
	}	
});