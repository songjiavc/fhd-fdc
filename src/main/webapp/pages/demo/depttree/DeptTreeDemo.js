Ext.define('FHD.demo.depttree.DeptTreeDemo', {
    extend: 'Ext.container.Container',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
 
        me.treePanel = Ext.create("FHD.ux.org.DeptTree",{
        	rootVisible:true,
        	checkable:false,
			showLight : true
		});
        
        Ext.applyIf(me, {
        	layout:'fit',
            items: [me.treePanel]
        });
        
        me.callParent(arguments);
    }
});