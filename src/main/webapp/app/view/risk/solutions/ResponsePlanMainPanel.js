/**
 * 
 * 风险应急方案（历史事件），应对方案主面板
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.solutions.ResponsePlanMainPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.responseplanmainpanel',

    requires: [
    	'FHD.view.risk.solutions.SolutionsGrid',
    	'FHD.view.risk.solutions.SolutionsMainPanel'
    ],
	
    showSolutionsGrid : function(){
		var me = this;
		me.solutionsgrid.getSelectionModel().clearSelections();
		me.solutionsgrid.onchange(me.solutionsgrid);
		me.getLayout().setActiveItem(me.solutionsgrid);
	 },          
              
              
    showSolutionsMainPanel : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.solutionsmainpanel);
  	},
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.solutionsgrid = Ext.widget('solutionsgrid',{
        	type : 'response'
        });
        me.solutionsmainpanel = Ext.widget('solutionsmainpanel',{
        	type : 'response'
        });
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.solutionsgrid, me.solutionsmainpanel]
        });
        
        me.callParent(arguments);
    }
});