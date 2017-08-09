/**
 * 
 * 工作计划
 * 
 * @author 胡迪新
 */
Ext.define('FHD.view.icm.icsystem.constructplan.ConstructPlanMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.constructplanmainpanel',

    frame: false,
    
    // 布局
    layout: {
        type: 'border'
    },
    
    border : false,
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.constructplanmenupanel = Ext.create('FHD.view.icm.icsystem.constructplan.ConstructPlanMenuPanel',{
        	region : 'west',
            split:true,
            width: 150,
            collapsible: false
        });
        
        me.constructplancenterpanel = Ext.create('FHD.view.icm.icsystem.constructplan.ConstructPlanCenterPanel',{
        	id:'constructplancenterpanel',
        	region : 'center',
        	border:false
        });
        
        Ext.applyIf(me, {
            items: [me.constructplanmenupanel,me.constructplancenterpanel]
        });

        me.callParent(arguments);
    }
});