/**
 * 年度考核计划主面板
 * AUTOR:Perry Guo
 * DATE:2017-07-29
 */
 Ext.define('FHD.view.check.quarterlycheck.plan.QuarterlyCheckMainPanel',{
 	   extend: 'Ext.panel.Panel',
 	   alias: 'widget.quarterlyCheckMainPanel',
 	 layout: {
        type: 'fit'
    },
    initComponent:function ()
    	{
    	var me=this;
    	me.quarterlyCheckCard=Ext.create('FHD.view.check.quarterly.plan.QuarterlyCheckCard',{
 
    	})
    	
    	   Ext.apply(me, {
            border:true,
     	    items:[me.quarterlyCheckCard]
        });
        me.callParent(arguments);
    	}
 	
 	
 })