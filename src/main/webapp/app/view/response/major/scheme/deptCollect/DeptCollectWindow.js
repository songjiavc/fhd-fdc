Ext.define('FHD.view.response.major.scheme.deptCollect.DeptCollectWindow', {
    extend: 'Ext.window.Window',
	alias: 'widget.deptcollectwindow',
    title: '部门汇总',
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
		me.addCounterForm = Ext.create('FHD.view.response.major.scheme.deptCollect.DeptCollectSchemeMakeMainPanel',{
	        	flex: 1,
	        	margin: 2,
	        	columnWidth: 1,
	        	businessId: me.businessId,
	        	executionId:me.executionId
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