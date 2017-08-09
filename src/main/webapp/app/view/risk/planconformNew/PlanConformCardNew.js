Ext.define('FHD.view.risk.planconformNew.PlanConformCardNew',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.planConformCardNew',
       
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
        me.planConformGrid = Ext.create('FHD.view.risk.planconformNew.PlanConformGridNew',{
			typeId:me.typeId,
			//计划类型参数
			planType:me.planType
		});
		//alert(me.typeId);
        me.planConformFormMain = Ext.create('FHD.view.risk.planconformNew.PlanConformFormMainNew',{
        	typeId : me.typeId,
        	//计划类型参数
			planType:me.planType
        });
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.planConformGrid, me.planConformFormMain]
        });
        
        me.callParent(arguments);
    }

});