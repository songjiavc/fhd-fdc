Ext.define('FHD.view.risk.cmp.form.RiskRelateFormDetailWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.riskrelateformdetailwindow',

    riskId:undefined,	//风险id
    
    initComponent: function () {
        var me = this;
  
        //创建详细信息
        me.riskDetail = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail',{
        	
        });
        
        Ext.apply(me, {
            autoScroll: false,
            layout:'fit',
            items:[me.riskDetail]
        });
        me.callParent(arguments);
    },
    reloadData:function(riskId){
    	me.riskId = riskId;
    	
    }
});