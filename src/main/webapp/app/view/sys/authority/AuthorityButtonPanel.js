Ext.define('FHD.view.sys.authority.AuthorityButtonPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.authorityButtonPanel',
    autoScroll:true,
	layout: {
		align: 'center',
		pack: 'center',
		type: 'vbox',
		padding:'5'
	},
	saveAllButton:null,
	saveButton:null,
	removeButton:null,
	removeAllButton:null,
	
	saveAllCallBack:function(){
		
	},
	saveCallBack:function(){
		
	},
	removeCallBack:function(){
		
	},
	removeAllCallBack:function(){
		
	},
    initComponent: function () {
		var me = this;
		me.saveAllButton=Ext.create('Ext.Button', {
			iconCls : "x-tbar-page-last",
			handler : function(){
				me.saveAllCallBack();
			}
		});
		me.saveButton=Ext.create('Ext.Button', {
			iconCls : "x-tbar-page-next",
			disabled:true,
			handler : function(){
				me.saveCallBack();
			}
		});
		me.removeButton=Ext.create('Ext.Button', {
			iconCls : "x-tbar-page-prev",
			disabled:true,
			handler : function(){
				me.removeCallBack();
			}
		});
		me.removeAllButton=Ext.create('Ext.Button', {
			iconCls : "x-tbar-page-first",
			handler : function(){
				me.removeAllCallBack();
			}
		});
		Ext.apply(me, {
			defaults:{
				margin:20
			},
			border:false,
    		style : 'border-left: 1px  #99bce8 solid !important;' +
    				'border-right: 1px  #99bce8 solid !important;',
			items:[
				me.saveAllButton,
				me.saveButton,
				me.removeButton,
				me.removeAllButton
			]
		});
        me.callParent(arguments);
    }
});