/**
 * 考评项目管理主面板
 * AUTHOR：Perry Guo
 * DATE:2017-07-13
 */
Ext.define('FHD.view.check.rule.CheckProjectMainPanel',{
	extend:'Ext.panel.Panel',
	alias:'widget.checkprojectMainPanel',
    layout: {
        type: 'border'
    },

initComponent:function ()
	{
	var me=this;
	me.projectGrid=Ext.create('FHD.view.check.rule.CheckProjectGrid',{
	})
		
	   Ext.applyIf(me, {
	   		layout: 'fit',
            border:true,
     	    items:[me.projectGrid]
        });

        me.callParent(arguments);	
		
	}
	
	
	
	
})