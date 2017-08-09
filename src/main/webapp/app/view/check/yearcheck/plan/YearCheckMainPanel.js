/**
 * 年度考核计划主面板
 * AUTOR:Perry Guo
 * DATE:2017-07-29
 */
 Ext.define('FHD.view.check.yearcheck.plan.YearCheckMainPanel',{
 	   extend: 'Ext.panel.Panel',
 	   alias: 'widget.yearcheckmainpanel',
 	 layout: {
        type: 'fit'
    },
    initComponent:function ()
    	{
    	var me=this;
    	me.yearCheckCard=Ext.create('FHD.view.check.yearcheck.plan.YearCheckCard',{
 
    	})
    	
    	   Ext.apply(me, {
            border:true,
     	    items:[me.yearCheckCard]
        });
        me.callParent(arguments);
    	}
 	
 	
 })