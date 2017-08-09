Ext.define('FHD.view.check.quarterly.plan.QuarterlyCheckCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.quarterlyCheckCard',
       
	 showPlanConformGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.yearCheckPlanGrid);
	 },          
              
              
    showQuarterlyCheckFormMain : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.quarterlyCheckFormMain);
  		me.quarterlyCheckFormMain.basicPanel.navToFirst();
  	},
  	
    initComponent: function () {
        var me = this;
        me.quarterlyCheckPlanGrid = Ext.create('FHD.view.check.quarterlycheck.plan.QuarterlyCheckGrid',{
		});
        me.quarterlyCheckFormMain = Ext.create('FHD.view.check.quarterlycheck.plan.QuarterlyCheckFormMain',{
        });
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.quarterlyCheckPlanGrid, me.quarterlyCheckFormMain]
        });
        
        me.callParent(arguments);
    }

});