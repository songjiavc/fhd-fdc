Ext.define('FHD.view.sys.organization.import.ImpValidateTabPanel', {
	extend: 'Ext.tab.Panel',
	alias: 'widget.impvalidatetabpanel',
	requires:['FHD.view.sys.organization.import.RoleImpValidateGridPanel','FHD.view.sys.organization.import.OrgImpValidateGridPanel'],
	fileId:null,
	orgImpValidateGridPanel:null,
	roleImpValidateGridPanel:null,
	b_close:function(){
		
	},
	a_close:function(){
		
	},
	doClose:function(){
		var me=this;
		me.b_close();
	},
	setFileId:function(fileId){
		var me=this;
		me.fileId=fileId;
	},
	reloadData:function(){
		var me=this;
		me.roleImpValidateGridPanel.reloadData();
		me.orgImpValidateGridPanel.reloadData();
	},
	validate:function (){
		var me = this;
		me.roleImpValidateGridPanel.validate();
		me.orgImpValidateGridPanel.validate();
    },
	importData:function (ids){
		var me = this;
		me.roleImpValidateGridPanel.importData();
		me.orgImpValidateGridPanel.importData();
	},
	// 初始化方法
	initComponent: function() {
		var me = this;
		me.orgImpValidateGridPanel = Ext.widget('orgimpvalidategridpanel',{
			title:"组织机构",
			fileId:me.fileId
		});
		me.roleImpValidateGridPanel = Ext.widget('roleimpvalidategridpanel',{
			title:"角色权限",
			fileId:me.fileId
		});
		
		Ext.apply(me, {
			deferredRender: false,
			activeTab: 0,
			plain: true,
			items: [me.orgImpValidateGridPanel,me.roleImpValidateGridPanel],
			bbar:{
                items: ['->',
	            {
					name:'importData',
					text : '数据导入',
					iconCls: 'icon-ibm-action-export-to-excel',
					handler:function(){
						me.roleImpValidateGridPanel.importData();
						me.orgImpValidateGridPanel.importData();
						me.doClose();
					}
				},
	            {
					text: FHD.locale.get("fhd.common.close"),
					iconCls: 'icon-ibm-close',
					handler: function () {
						me.doClose();
					}
	            }]
            }
	    });
		
	    me.callParent(arguments);
	}
});