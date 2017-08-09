/**
 * 机构管理右面板
 * 
 * @author 金鹏祥
 */
Ext.define('FHD.view.sys.organization.RightPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.rightPanel',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.id = 'rightPanel';
    	//图片面板
    	me.cardPanel = Ext.create('FHD.view.sys.organization.CardPanel');
    	me.navigationBar = Ext.create('FHD.ux.NavigationBars',{
        	type: 'org'
        });
    	
        Ext.apply(me, {
        	region:'center',
        	border:false,
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
    		items:[
    		    {
	    			xtype:'box',
	    			height:20,
	    			style : 'border-left: 1px  #99bce8 solid;',
	    			html:'<div id="organizationNavDiv" class="navigation"></div>'
    		},me.cardPanel]
        });

        me.callParent(arguments);
    }
});