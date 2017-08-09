
Ext.define('FHD.view.risk.managereport.managereportmake.ManageReportPlanMakeCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.managereportplanmakecard',
    
    requires: [
               'FHD.view.risk.managereport.managereportmake.ManageReportPlanMakeGrid',
               'FHD.view.risk.managereport.managereportmake.ManageReportPlanMakeEditMain'
              ],
       
	 showWorkPlanMakeGrid : function(){
			var me = this;
			me.getLayout().setActiveItem(me.managereportplanmakegrid);
	 },          
              
              
    showWorkPlanMakeEditMain : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.managereportplanmakeeditmain);
  		me.managereportplanmakeeditmain.basicPanel.navToFirst();
  	},
  	
    initComponent: function () {
        var me = this;
        me.managereportplanmakegrid = Ext.widget('managereportplanmakegrid');
        me.managereportplanmakeeditmain = Ext.widget('managereportplanmakeeditmain');
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.managereportplanmakegrid, me.managereportplanmakeeditmain]
        });
        
        me.callParent(arguments);
    }

});