Ext.define('FHD.view.response.major.SchemeMakeCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.schememakecard',
       

    changeLayout:function(type){
    	var me = this;
    	if(type =="grid"){
    		me.getLayout().setActiveItem(me.schemeMakeRiskGrid);
    	}else if(type == "form"){
    		me.getLayout().setActiveItem(me.schemeMakeForm);
    	}
    },
    initComponent: function () {
        var me = this;
        me.schemeMakeRiskGrid = Ext.create('FHD.view.response.major.SchemeMakeRiskGrid',{
		});
       
        me.schemeMakeForm = Ext.create('FHD.view.response.major.SchemeMakeForm',{
        	
        });
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.schemeMakeRiskGrid,me.schemeMakeForm]
        });
        
        me.callParent(arguments);
    }

});