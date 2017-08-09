Ext.define('FHD.view.sys.template.TemplateManageCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.templateManageCard',
    
    requires: [
              ],
       
	 showTemplateManageGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.templateManageGrid);
	 },          
              
              
    showTemplateManageEdit : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.templateManageEdit);
  	},
              
    initComponent: function () {
        var me = this;
        
        me.templateManageGrid = Ext.create('FHD.view.sys.template.TemplateManageGrid');
        me.templateManageEdit = Ext.create('FHD.view.sys.template.TemplateManageEdit');
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.templateManageGrid, me.templateManageEdit]
        });
        
        me.callParent(arguments);
    }

});