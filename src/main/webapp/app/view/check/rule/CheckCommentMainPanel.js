/*考核内容管理主面板
 * AUTHOR：Perry Guo
 * DATE:2017-07-13
 * */
 Ext.define('FHD.view.check.rule.CheckCommentMainPanel',{
 	extend : 'Ext.panel.Panel',
			alias : 'widget.checkmommentmainpanel',
			layout : {
				type : 'border'
			},
initComponent:function ()
	{
	var me=this;
	me.commentGrid=Ext.create('FHD.view.check.rule.CheckCommentGrid',{
	})
		
	   Ext.applyIf(me, {
	   		layout: 'fit',
            border:true,
     	    items:[me.commentGrid]
        });

        me.callParent(arguments);	
		
	}
 	
 
 
 
 
 })