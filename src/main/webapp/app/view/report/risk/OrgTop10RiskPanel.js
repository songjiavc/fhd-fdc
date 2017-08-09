/*
 * 集团十大风险面板，包括左侧集团树和右侧二级风险排名
 * zhengjunxiang
 */

Ext.define('FHD.view.report.risk.OrgTop10RiskPanel', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.orgtop10riskpanel',
	
	initComponent: function () {
        var me = this;
	    Ext.apply(me,{
	    	layout:'fit',
			typeId: me.typeId,	//菜单配置-分库标识
	    	border:false
	    });
        
    	me.callParent(arguments);
	},
	
	onRender:function(){
		var me = this;
        //右侧风险排名
        me.grid = Ext.create("FHD.view.report.risk.Top10RiskGrid",{
			typeId: me.typeId //菜单配置-分库标识
        });
        me.add(me.grid);
        me.doLayout();
        
    	me.callParent(arguments);
	}
});