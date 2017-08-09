
Ext.define('FHD.view.response.workplan.workplanmake.WorkPlanMakeCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.workPlanMakeCard',
    
    requires: [
               'FHD.view.response.workplan.workplanmake.WorkPlanMakeGrid',
               'FHD.view.response.workplan.workplanmake.WorkPlanMakeEditMain'
              ],
       
	 showWorkPlanMakeGrid : function(){
			var me = this;
			me.getLayout().setActiveItem(me.workPlanMakeGrid);
	 },          
              
              
    showWorkPlanMakeEditMain : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.workPlanMakeEditMain);
  		me.workPlanMakeEditMain.basicPanel.navToFirst();
  	},
  	
    initComponent: function () {
        var me = this;
        me.workPlanMakeGrid = Ext.widget('workPlanMakeGrid',{
        	typeId : me.typeId
        });
        me.workPlanMakeEditMain = Ext.widget('workPlanMakeEditMain',{
        	typeId : me.typeId
        });
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.workPlanMakeGrid, me.workPlanMakeEditMain]
        });
        
        me.callParent(arguments);
    }

});