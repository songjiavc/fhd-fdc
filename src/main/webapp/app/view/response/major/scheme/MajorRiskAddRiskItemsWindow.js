Ext.define('FHD.view.response.major.scheme.MajorRiskAddRiskItemsWindow', {
    extend: 'Ext.window.Window',
	alias: 'widget.majorriskaddriskitemswindow',
    title: '添加风险事项',
	modal: true,
	height: 500,
    width: 1000,
    x:150,
    y:50,
    maximizable: true,
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    initComponent : function(){
		var me = this;
		me.addRiskItemsForm = Ext.create('FHD.view.response.major.scheme.MajorRiskAddRiskItemsFormPanel',{
	        	flex: 1,
	        	margin: 2,
	        	columnWidth: 1,
	        	businessId: me.businessId,
	        	executionId:me.executionId,
	    		schemeObjectId:me.schemeObjectId,
	    		executionObjectId:me.executionObjectId,
	    		schemeType:me.schemeType,
	    		empType:me.empType
	        });
		Ext.applyIf(me,{
            items: [me.addRiskItemsForm]
		});
		
		me.buttonAlign = 'center';
		me.buttons = [
	        	{
		            xtype: 'button',
		            text: $locale('fhd.common.confirm'),
		            width:70,
		            style: {
		            	marginRight: '10px'    	
		            },
		            handler:function(){
		            	me.onSubmit(me);
		            	me.close();
		            }
		        },
		        {
		            xtype: 'button',
		            text: $locale('fhd.common.close'),
		            width:70,
		            style: {
		            	marginLeft: '10px'    	
		            },
		            handler:function(){
		            	me.close();
		            }
		        }
		    ];
	        me.callParent(arguments); 
	},
	onSubmit:Ext.emptyFn()
});