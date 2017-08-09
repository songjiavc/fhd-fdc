/**
 * 首页portal tree
 *
 * @author wzr
 */
Ext.define('FHD.view.sys.sfindex.IndexTreePanel',{
    extend: 'Ext.tree.Panel',
    alias: 'widget.indextreepanel',

    refreshTree: function(){
        var me = this;
    },

    //单击树节点,在portal页面增加相应的模块
    clickTreeNode: function(nodeId,title){
        var me = this;
        me.up('sfindexmain').portalPanel.addPortlet(nodeId,title);
    },


    initComponent: function() {
        var me = this;

        me.treeNodes = Data.makeTreeNodes();

        me.store = Ext.create('Ext.data.TreeStore', {
            root : {
                expanded : true,
                text: "组件",
                children: me.treeNodes
            }
        });

        Ext.apply(me, {
            width: 240,
            split: true,
            collapsible : true,
            border: true,
            region: 'west',
            multiSelect: true,
            rowLines: false,
            singleExpand: false,
            checked: false,
            collapsed: true,
            store: me.store,
            listeners : {
                    'itemdblclick' : function(view,re){
						if(Data.checkStatusById(re.data.id)=="1"){
							me.clickTreeNode(re.data.id,re.data.text);
						}
                    }
                }
            });

        me.callParent(arguments);
    }

});