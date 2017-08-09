Ext.define('FHD.view.sys.documentlib.DocumentLibCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.documentLibCard',
       
	showDocumentLibGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.documentLibGrid);
	},          
              
    showDocumentLibEditPanel : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.documentLibEditPanel);
  	},
              
    initComponent: function () {
        var me = this;
        me.documentLibGrid = Ext.create('FHD.view.sys.documentlib.DocumentLibGrid');
        me.documentLibEditPanel = Ext.create('FHD.view.sys.documentlib.DocumentLibEditPanel',{
        	typeId: me.typeId
        });
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.documentLibGrid, me.documentLibEditPanel]
        });
        
        me.callParent(arguments);
    }

});