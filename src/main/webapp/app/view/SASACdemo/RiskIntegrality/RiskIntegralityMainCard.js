Ext.define('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityMainCard',{
	extend: 'Ext.panel.Panel',
    alias: 'widget.riskIntegralityMainCard',
    
    requires: [
              ],
       
	 showRiskIntegralityMain : function(){
		var me = this;
		me.card.getLayout().setActiveItem(me.riskIntegralityMain);
	 },          
              
              
    showRiskIntegralityRelaGrid : function(){
  		var me = this;
  		me.card.getLayout().setActiveItem(me.riskIntegralityRelaGrid);
  	},
              
    initComponent: function () {
        var me = this;
        me.riskIntegralityMain = Ext.create('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityMain');
        
        me.riskIntegralityRelaGrid = Ext.create('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityRelaGrid');
        
        me.card = Ext.create('FHD.ux.CardPanel',{
        	border:false,
        	activeItem : 0,
        	items: [me.riskIntegralityMain,me.riskIntegralityRelaGrid]
        });
        
        Ext.apply(me, {
        	border:false,
        	layout: 'fit',
            items: [me.card]
        });
        
        me.callParent(arguments);
    }

});