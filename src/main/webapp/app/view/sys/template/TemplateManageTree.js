/**
 * 模板管理树
 * 
 * @author 王再冉
 */
Ext.define('FHD.view.sys.template.TemplateManageTree',{
	extend: 'FHD.ux.TreePanel',
    alias: 'widget.templateManageTree',
	
    extraParams: {
    	
    },
    
    refreshTree: function(){
		var me = this;
        me.getStore().load();
	},
	//单击树节点
	clickTreeNode: function(nodeId){
		var me = this;
		me.currentNodeId = nodeId;//当前点击节点的id
		var cardPanel = me.up('templateManageMain').templateManageCard; 
		cardPanel.showTemplateManageGrid();
		cardPanel.templateManageGrid.reloadData(nodeId);
		//cardPanel.templateManageEdit.dictEntryId.setValue(nodeId);
	},
	
    initComponent: function() {
        var me = this;
        me.url = __ctxPath + '/sys/templatemanage/finddictentrytreebysome.f?dictEntryId=template_manage';
        
        Ext.apply(me, {
          	rootVisible: false,
	        width: 240,
	        maxWidth: 350,
	        split: true,
	        collapsible : true,
	        border: true,
	        region: 'west',
	        multiSelect: true,
	        rowLines: false,
	        singleExpand: false,
	        checked: false,
		    url: me.url,
		    listeners : {
		 		 'itemclick' : function(view,re){
		 		 		me.clickTreeNode(re.data.id);
  				 },
  				 load: function (store, records) { //默认选择首节点
   					var selectNode;
   					if(records.childNodes[0]){
   						selectNode = records.childNodes[0];
   						me.getSelectionModel().select(selectNode);//默认选择首节点
		            	me.clickTreeNode(selectNode.data.id);
   					}
                }
 			}
        });

        me.callParent(arguments);
    }

});