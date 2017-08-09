Ext.define('FHD.view.risk.managereport.ManageReportMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.managereportmainpanel',

    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法
    initComponent: function() {
        var me = this;
        //me.typeId = 'document_library_knowledge';
        
        me.manageReportGridPanel = Ext.create('FHD.view.risk.managereport.ManageReportGridPanel',{
        	archiveStatus : 'archived',
        	border : true,
            region: 'center'
        });
        
        me.manageReportTreePanel = Ext.create('FHD.view.risk.managereport.ManageReportTreePanel',{
        	region: 'west',
        	typeId : me.typeId,
        	clickTreeNode : function(nodeId,type){
        		me.manageReportGridPanel.reloadData(nodeId);
        	}
        });
        
        Ext.applyIf(me, {
            items: [me.manageReportTreePanel,me.manageReportGridPanel]
        });

        me.callParent(arguments);
    }

});