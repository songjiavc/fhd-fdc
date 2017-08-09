/**
 * 
 * 执行预案主面板
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.execute.preplan.ExePreplanMainPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.exepreplanmainpanel',

    requires: [
        'FHD.view.risk.execute.preplan.ExePreplanGrid',
        'FHD.view.risk.execute.preplan.ExePreplanEditPanel'
    ],
    
    showexepreplangrid : function(){
        var me = this;
        me.exepreplangrid.getSelectionModel().clearSelections();
        me.exepreplangrid.onchange(me.exepreplangrid);
        me.getLayout().setActiveItem(me.exepreplangrid);
     },          
              
    showexepreplaneditpanel : function(){
        var me = this;
        me.getLayout().setActiveItem(me.exepreplaneditpanel);
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.exepreplangrid = Ext.widget('exepreplangrid');
        
        me.exepreplaneditpanel = Ext.widget('exepreplaneditpanel');
        
        Ext.apply(me, {
            border:false,
            activeItem : 0,
            items: [me.exepreplangrid, me.exepreplaneditpanel]
        });
        
        me.callParent(arguments);
    }
});