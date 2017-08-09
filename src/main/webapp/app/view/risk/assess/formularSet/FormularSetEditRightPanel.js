/**
 * 右面板
 * 
 */
Ext.define('FHD.view.risk.assess.formularSet.FormularSetEditRightPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.formularSetEditRightPanel',
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.formularSetEditFormPanel = Ext.create('FHD.view.risk.assess.formularSet.FormularSetEditFormPanel');
    	me.formularSetEditButtonPanel = Ext.create('FHD.view.risk.assess.formularSet.FormularSetEditButtonPanel');
        Ext.applyIf(me, {
        	region:'center',
        	border:false,
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
    		items:[
    			me.formularSetEditFormPanel, me.formularSetEditButtonPanel
    		   ]
        });

        me.callParent(arguments);
    }
});