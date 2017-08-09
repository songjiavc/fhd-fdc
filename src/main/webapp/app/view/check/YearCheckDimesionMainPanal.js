/**
 * 考评项目管理主面板
 * AUTHOR：Perry Guo
 * DATE:2017-07-13
 */
Ext.define('FHD.view.check.YearCheckDimesionMainPanal',{
	extend:'Ext.panel.Panel',
	alias:'widget.yearCheckDimesionMainPanal',
    layout: {
        type: 'border'
    },

initComponent:function ()
	{
	var me=this;
	me.dimesionGrid=Ext.create('FHD.view.check.YearCheckDimesionGrid',{
	})
		
	   Ext.applyIf(me, {
	   		layout: 'fit',
            border:true,
     	    items:[me.dimesionGrid]
        });

        me.callParent(arguments);	
		
	}
	
	
	
	
})