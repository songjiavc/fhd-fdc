Ext.define('FHD.view.SASACdemo.SASACMainProtal', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.sasacMainProtal',

    requires: [
    	'FHD.view.SASACdemo.homepage.SASACcardpanel'
    ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.sasaccardpanel = Ext.widget('sasaccardpanel');
        
        Ext.applyIf(me, {
        	border : false,
        	layout: 'fit',
            items: [me.sasaccardpanel]
        });

        me.callParent(arguments);
    }

});