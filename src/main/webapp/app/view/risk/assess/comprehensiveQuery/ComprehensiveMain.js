/**
 * 
 * 大综合查询
 */

Ext.define('FHD.view.risk.assess.comprehensiveQuery.ComprehensiveMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.comprehensiveMain',
    
    requires : [
    ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.id = 'comprehensiveMainId';
        
        me.comprehensiveQueryPanel =  Ext.create('FHD.view.risk.assess.comprehensiveQuery.ComprehensiveQueryPanel');
        
        Ext.apply(me, {
        	border:false,
        	layout: {
    	        type: 'border',
    	        padding: '0 0 5	0'
    	    }
        });
        
        me.callParent(arguments);
    }

});