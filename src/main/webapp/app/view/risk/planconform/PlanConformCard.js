Ext.define('FHD.view.risk.planconform.PlanConformCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.planConformCard',
       
	 showPlanConformGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.planConformGrid);
	 },          
              
              
    showPlanConformFormMain : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.planConformFormMain);
  		me.planConformFormMain.basicPanel.navToFirst();
  	},
  	
    initComponent: function () {
        var me = this;
        me.planConformGrid = Ext.create('FHD.view.risk.planconform.PlanConformGrid',{
			typeId:me.typeId
		});
        me.planConformFormMain = Ext.create('FHD.view.risk.planconform.PlanConformFormMain',{
        	typeId : me.typeId
        });
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.planConformGrid, me.planConformFormMain]
        });
        
        me.callParent(arguments);
    }

});