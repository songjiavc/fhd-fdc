Ext.define('FHD.view.response.major.scheme.MajorRiskAddCounterWindow', {
    extend: 'Ext.window.Window',
	alias: 'widget.majorriskaddcounterwindow',
    title: '添加应对措施',
	modal: true,
	height: 500,
    width: 1000,
    x:160,
    y:50,
    maximizable: true,
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    initComponent : function(){
		var me = this;
		me.addCounterForm = Ext.create('FHD.view.response.major.scheme.MajorRiskAddCounterFormPanel',{
	        	flex: 1,
	        	margin: 2,
	        	columnWidth: 1,
	        	businessId: me.businessId,
	    		schemeObjectId:me.schemeObjectId,
	    		executionObjectId:me.executionObjectId,
	    		schemeType:me.schemeType,
	    		empType:me.empType,
	    		itemId:me.itemId,
	        });
		Ext.applyIf(me,{
            items: [me.addCounterForm]
		});
		
		me.buttonAlign = 'center';
		me.buttons = [
	        	{
		            xtype: 'button',
		            text: $locale('fhd.common.close'),
		            width:70,
		            style: {
		            	marginRight: '10px'    	
		            },
		            handler:function(){
		            	 me.onSubmit(me);
		            	 me.close();
		            }
		        },
		    ];
	        me.callParent(arguments); 
	},
	onSubmit:Ext.emptyFn()
});