Ext.define('FHD.view.interaction.InteractionCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.interactionCard',
    //显示列表   
	showInteractionGrid : function(){
		var me = this;
		me.getLayout().setActiveItem(me.interactionGrid);
	},          
    //显示发帖编辑页面          
    showInteractionForm : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.interactionForm);
  	},
  	//显示回帖页面
  	showReplyFormMain : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.replyFormMain);
  	},
  	
    initComponent: function () {
        var me = this;
        me.interactionGrid = Ext.create('FHD.view.interaction.InteractionGrid');
        me.interactionForm = Ext.create('FHD.view.interaction.InteractionForm');
        me.replyFormMain = Ext.create('FHD.view.interaction.ReplyFormMain');
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.interactionGrid, me.interactionForm, me.replyFormMain]
        });
        
        me.callParent(arguments);
    }

});