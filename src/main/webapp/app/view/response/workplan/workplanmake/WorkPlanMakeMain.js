/**
 * 主面板
 * 
 * @author 
 */
Ext.define('FHD.view.response.workplan.workplanmake.WorkPlanMakeMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.workPlanMakeMain',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//右侧面板
    	me.workPlanMakeCard = Ext.create('FHD.view.response.workplan.workplanmake.WorkPlanMakeCard',{
    		typeId : me.typeId
    	});
    	
    	Ext.apply(me, {
    		layout: 'fit',
            border:false,
     	    items:[me.workPlanMakeCard]
        });
    	
        me.callParent(arguments);
        
    }
});