Ext.define('FHD.view.risk.planconformNew.deptFlow.PlanConformOfAssessMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.planConformOfAssessMainDept',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	//风险辨识类型
    	var planType="riskAssessDept";
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