/**
 * 
 * 工作计划左侧功能树
 * 
 * @author 胡迪新
 */
Ext.define('FHD.view.response.responseplan.ResponsePlanCenterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.responseplancenterpanel',
	requires : [
		
	],
	layout:'fit',
    // 初始化方法
	initComponent : function(){
		var me = this;
		Ext.apply(me,{
			items : [Ext.create('FHD.view.response.responseplan.ResponsePlanEditPanel')]
		});
		me.callParent(arguments);
	}
  
});