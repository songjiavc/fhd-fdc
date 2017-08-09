Ext.define('FHD.view.risk.riskversion.RiskVersionCardMain',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.riskversioncardmain',
       
	 showVersionGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.versionGrid);
	 },  
	 
	 showVersionForm : function(){
		var me = this;
		me.getLayout().setActiveItem(me.versionForm);
	 },  
	 
	 showDetailtGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.detailtGrid);
	 }, 
       
	 showDetailtForm : function(){
		var me = this;
		me.getLayout().setActiveItem(me.detailtForm);
	 },
	 
	 showAddDelGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.addDelRisksGrid);
	 },
              
  	
    initComponent: function () {
        var me = this;
        //风险版本列表
        me.versionGrid = Ext.create('FHD.view.risk.riskversion.RiskVersionGrid',{
			typeId:me.typeId	//风险分库标识
		});
        //风险版本表单
        me.versionForm = Ext.create('FHD.view.risk.riskversion.RiskVersionForm',{
			typeId:me.typeId	//风险分库标识
		});
        //版本风险明细树列表
        me.detailtGrid = Ext.create('FHD.view.risk.riskversion.RiskDetailTreeGrid',{
			typeId:me.typeId	//风险分库标识
		});
        //风险事件基本信息表单
        me.detailtForm = Ext.create('FHD.view.risk.riskversion.RiskVersionDetailForm',{
			typeId:me.typeId	//风险分库标识
		});
        //新增、删除风险明细列表
        me.addDelRisksGrid = Ext.create('FHD.view.risk.riskversion.RiskVersionAddDelRisksGrid',{});
        
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
        	autoDestroy: true,
            items: [me.versionGrid,me.versionForm,me.detailtGrid,me.detailtForm]
        });
        
        me.callParent(arguments);
        me.versionGrid.reloadData();
    }

});