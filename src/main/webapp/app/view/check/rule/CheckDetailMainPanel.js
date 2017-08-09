/*考核细则管理主面板
 * AUTHOR：Perry Guo
 * DATE:2017-07-13
 * */
 Ext.define('FHD.view.check.rule.CheckDetailMainPanel',{
 	extend : 'Ext.panel.Panel',
			alias : 'widget.checkdetailmainpanel',
			layout : {
				type : 'border'
			},
initComponent:function ()
	{
	var me=this;
	me.detailGrid=Ext.create('FHD.view.check.rule.CheckDetailGrid',{
	})
		
	   Ext.applyIf(me, {
	   		layout: 'fit',
            border:true,
     	    items:[me.detailGrid]
        });

        me.callParent(arguments);	
		
	}
 	
 
 
 
 
 })