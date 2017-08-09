/**
 * 
 * 风险预防方案（潜在风险），应对预案主面板
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.solutions.PrePlanMainPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.preplanmainpanel',

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
        	type : 'preplan'
        });
        me.solutionsmainpanel = Ext.widget('solutionsmainpanel',{
        	type : 'preplan'
        });
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.solutionsgrid, me.solutionsmainpanel]
        });
        
        me.callParent(arguments);
    }
});