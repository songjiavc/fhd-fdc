Ext.define('FHD.view.SASACdemo.homepage.SASACcardpanel',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.sasaccardpanel',
    
    requires: [
               'FHD.view.SASACdemo.homepage.HomePageMainPanel',
               'FHD.view.SASACdemo.homepage.SASACkpiRiskWaring',
               'FHD.view.SASACdemo.homepage.RiskCategoryDetail'
              ],
       
	 showHomePageMainPanel : function(){
			var me = this;
			me.getLayout().setActiveItem(me.homePageMainPanel);
	 },          
              
              
    showSasackpiRiskWaring : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.sasackpiRiskWaring);
  	},
  	
  	showRiskCategoryDetail : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.riskCategoryDetail);
  	},
  	
  	
              
    initComponent: function () {
        var me = this;
        me.homePageMainPanel = Ext.widget('homePageMainPanel');
        me.sasackpiRiskWaring = Ext.widget('sasackpiRiskWaring');
        me.riskCategoryDetail = Ext.widget('riskCategoryDetail');
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.homePageMainPanel,me.sasackpiRiskWaring,me.riskCategoryDetail]
        });
        
        me.callParent(arguments);
    }

});