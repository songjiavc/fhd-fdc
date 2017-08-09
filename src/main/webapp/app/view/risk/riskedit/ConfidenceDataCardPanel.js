/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.riskedit.ConfidenceDataCardPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.confidencedatacardpanel',
    requires: [
    	'FHD.view.risk.riskedit.ConfidenceField'
    ],
    autoWidth : true,
    autoScroll : true,
    margin : '2 2 2 2',
    border : false,
    initComponent: function() {
        var me = this;
        me.redFieldContainer = Ext.widget('fieldcontainer',
        	{
        		layout : {
        			type : 'vbox',
        			align : 'stretch'
        		},
        		border:false
        	});
        me.yellowFieldContainer = Ext.widget('fieldcontainer',
        	{
        		layout : {
        			type : 'vbox',
        			align : 'stretch'
        		},
        		border:false,
        		
        		items:[]
        	});
        
        me.greenFieldContainer = Ext.widget('fieldcontainer',
        	{
        		layout : {
        			type : 'vbox',
        			align : 'stretch'
        		},
        		border:false
        });
        
        Ext.apply(me, {
            items: [
                me.redFieldContainer,me.yellowFieldContainer,me.greenFieldContainer
            ]
        });

        me.callParent(arguments);
    }

});