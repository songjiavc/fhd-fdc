Ext.define('FHD.view.risk.comprehensivequery.RiskCompreCardMain',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.riskcomprecardmain',
       
	showRiskCompreQuary : function(){
		var me = this;
		me.getLayout().setActiveItem(me.riskCompreQuary);
	},
	 
	showRiskRbsDetail : function(){
		var me = this;
		me.getLayout().setActiveItem(me.riskRbsDetail);
	},
  	
    initComponent: function () {
        var me = this;
        //查询主页面
        me.riskCompreQuary = Ext.create('FHD.view.risk.comprehensivequery.RiskCompreQueryPanel',{});
      
        me.riskRbsDetail = Ext.create('FHD.view.risk.comprehensivequery.RiskCompreDetailGrid',{});
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
        	autoDestroy: true,
            items: [me.riskCompreQuary,me.riskRbsDetail]
        });
        
        me.callParent(arguments);
    }

});