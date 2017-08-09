Ext.define('FHD.view.sf.index.SFRiskFileMain',{
	extend:'Ext.form.Panel',
	
	initComponent : function(){
		var me = this;
		
		me.txwjPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			fn:me.fn,
			border:false,
	        style: {
	        	backgroundColor: '#EFEFEF'
	        },
			limit:1,
			typeId:'WDK0001',
			flex:1,
			height:40
		});
		me.fxbgPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			fn:me.fn,
			border:false,
			limit:1,
			typeId:'WDK0002',
			flex:1,
			height:40
		});
		me.fxjbPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			fn:me.fn,
			border:false,
			limit:1,
			typeId:'WDK0006',
			flex:1,
			height:40
		});
		me.fxjhPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			fn:me.fn,
			border:true,
			limit:1,
			typeId:'WDK0003',
			flex:1,
			height:40
		});
		me.fxbsPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			fn:me.fn,
			border:true,
			limit:1,
			typeId:'WDK0007',
			flex:1,
			height:40
		});
		
		Ext.apply(me,{
        	border:true,
    		style:'padding:1px 0px 5px 1px',
			layout: {
                type: 'vbox',
                align: 'stretch'
          	},
			items:[me.txwjPanel,me.fxbgPanel,me.fxjbPanel,me.fxjhPanel,me.fxbsPanel],
		});
		me.callParent(arguments);
	}
    
})