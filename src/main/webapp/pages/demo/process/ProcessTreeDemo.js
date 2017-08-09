Ext.define('FHD.demo.process.ProcessTreeDemo', {
    extend: 'Ext.container.Container',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        me.treePanel = Ext.create("FHD.ux.process.ProcessTree",{extraParams : {
			showLight : true
		}});
        Ext.applyIf(me, {
        	layout:'fit',
            items: [me.treePanel]
        });
        
        me.callParent(arguments);
    }
});