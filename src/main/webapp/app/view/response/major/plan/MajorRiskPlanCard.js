Ext.define('FHD.view.response.major.plan.MajorRiskPlanCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.majorriskplancard',
    
    changeLayout:function(type){
    	var me = this;
    	if(type =="grid"){
    		me.getLayout().setActiveItem(me.majorRiskPlanGridPanel);
    	}else if(type == "form"){
    		me.getLayout().setActiveItem(me.majorRiskPlanFormMainPanel);
    	}
    },
    initComponent: function () {
        var me = this;
        me.majorRiskPlanFormMainPanel = Ext.create('FHD.view.response.major.plan.MajorRiskPlanFormMainPanel',{
        	planType:me.planType,
        	schm:me.schm
		});
        
        me.majorRiskPlanGridPanel = Ext.create('FHD.view.response.major.plan.MajorRiskPlanGridPanel',{
        	planType:me.planType,
        	schm:me.schm
		});
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.majorRiskPlanGridPanel,me.majorRiskPlanFormMainPanel]
        });
        
        me.callParent(arguments);
    }

});