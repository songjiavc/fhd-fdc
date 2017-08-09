/**
 * 主面板
 * 
 */
Ext.define('FHD.view.risk.assess.formularSet.FormularSetEditMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.formularSetEditMain',
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	//右侧面板
    	me.rightPanel = Ext.create('FHD.view.risk.assess.formularSet.FormularSetEditRightPanel');
    	
    	//树
       	me.lefttree = Ext.create('FHD.view.risk.assess.formularSet.FormularSetEditTreePanel');
       	
    	Ext.apply(me, {
            border:false,
     		layout: {
     	        type: 'border',
     	        padding: '0 0 5	0'
     	    },
     	    items:[me.lefttree,me.rightPanel]
        });
    	
        me.callParent(arguments);
        
       /* me.rightPanel.on('resize',function(p){
    		me.rightPanel.setHeight(FHD.getCenterPanelHeight());
    	});*/
    	
    }
});