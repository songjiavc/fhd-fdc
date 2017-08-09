/*
 * 
 * 部门风险辨识计划管理
 * 2017年3月29日17:50:12
 * @author Jzq
 */

Ext.define('FHD.view.risk.planconformNew.deptFlow.PlanConformOfIdentityMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.planConformOfIdentityMainDept',
    // 初始化方法
    initComponent: function() {
    	//风险辨识类型
    	var planType="riskIdentifyDept";
    	var me = this;
    	//右侧面板
    	me.planConformCard = Ext.create('FHD.view.risk.planconformNew.deptFlow.PlanConformCardNew',{
			typeId:me.typeId,
			planType:planType
		});
    	
    	Ext.apply(me, {
    		layout: 'fit',
			typeId:me.typeId,//分库标志
            border:true,
     	    items:[me.planConformCard]
        });
    	
        me.callParent(arguments);
        
    }
});