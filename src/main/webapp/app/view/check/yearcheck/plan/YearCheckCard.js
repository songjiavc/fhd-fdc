Ext.define('FHD.view.check.yearcheck.plan.YearCheckCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.yearcheckcard',
       
	 showPlanConformGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.yearCheckPlanGrid);
	 },          
              
              
    showyearCheckFormMain : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.yearCheckFormMain);
  		me.yearCheckFormMain.basicPanel.navToFirst();
  	},
  	
    initComponent: function () {
        var me = this;
        me.yearCheckPlanGrid = Ext.create('FHD.view.check.yearcheck.plan.YearCheckPlanGrid',{
		});
        me.yearCheckFormMain = Ext.create('FHD.view.check.yearcheck.plan.YearCheckFormMain',{
        });
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.yearCheckPlanGrid, me.yearCheckFormMain]
        });
        
        me.callParent(arguments);
    }

});