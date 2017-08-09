/**
 * 
 * 执行应对措施主面板
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.execute.solutions.ExeSolutionsMainPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.exesolutionsmainpanel',

    requires: [
    	'FHD.view.risk.execute.solutions.ExeSolutionsGrid',
    	'FHD.view.risk.execute.solutions.ExeSolutionsEditPanel'
    ],
    
    showexesolutionsgrid : function(){
		var me = this;
		me.exesolutionsgrid.getSelectionModel().clearSelections();
		me.exesolutionsgrid.onchange(me.exesolutionsgrid);
		me.getLayout().setActiveItem(me.exesolutionsgrid);
	 },          
              
    showexesolutionseditpanel : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.exesolutionseditpanel);
  	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.exesolutionsgrid = Ext.widget('exesolutionsgrid');
        
        me.exesolutionseditpanel = Ext.widget('exesolutionseditpanel');
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.exesolutionsgrid, me.exesolutionseditpanel]
        });
        
        me.callParent(arguments);
    }
});