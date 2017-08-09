/**
 * 
 * 计划制定卡片面板
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulatePlanCardnew',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.formulatePlanCardnew',
    
	showFormulateGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.formulateGrid);
	},
              
    showFormulatePlanMainPanel : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.formulatePlanMainPanel);
  		me.formulatePlanMainPanel.basicPanel.navToFirst();
  	},
  	
  	showAssessPlanQuestMonitorMain : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.assessPlanQuestMonitorMain);
  	},
  	
    initComponent: function () {
        var me = this;
        me.formulateGrid = Ext.create('FHD.view.risk.assess.formulatePlan.FormulatePlanGridnew',{
			typeId: me.typeId	//菜单配置-分库标识
		});
        me.formulateContainer = Ext.create('Ext.container.Container');
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
			typeId: me.typeId,	//菜单配置-分库标识
            items: [me.formulateGrid, me.formulateContainer]
        });
        
        me.callParent(arguments);
    }

});