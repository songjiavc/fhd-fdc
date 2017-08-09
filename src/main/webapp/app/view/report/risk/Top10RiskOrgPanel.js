/*
 * 部门风险的十大风险页签，右侧二级风险排名
 * zhengjunxiang
 */

Ext.define('FHD.view.report.risk.Top10RiskOrgPanel', {
    extend: 'Ext.panel.Panel',
	alias: 'widget.top10riskorgpanel',
	
	//查看接口
    showRiskDetail: function(p,parentId,name){},
  
    //新增修改返回接口
    goback: function(){},
    
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
        me.grid = Ext.create("FHD.view.report.risk.Top10RiskOrgGrid",{
        	showRiskDetail:me.showRiskDetail,
        	goback:me.goback
        });
        me.add(me.grid);
        me.doLayout();
        
    	me.callParent(arguments);
	},
	
	reloadData : function(){
		var me = this;
		me.grid.reloadData(null,'2',null);
	}
});