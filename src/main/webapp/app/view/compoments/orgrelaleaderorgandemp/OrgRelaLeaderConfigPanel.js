/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderConfigPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.orgrelaleaderconfigpanel',
	requires: [
    	'FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptInput',
    	'FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderSelectGridPanel',
    	'FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderSelectedGridPanel'
    ],
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	defaults : {
		margin : '10 10 10 10'
	},
	initComponent : function(){
		
		var me = this;
		
		var bussinessStore = Ext.create('Ext.data.Store', {
      		fields: ['id', 'name'],
      		proxy: {
    	    	url: __ctxPath + '/check/findBussinessByAll.f',
    	        type: 'ajax',
    	        reader: {
    	            type: 'json'
    	        }
      		},
      		autoload : false
        });
		
		var bussinessCombo = Ext.widget('combobox',{
			fieldLabel : '所属业务'+'<font color="red">*</font>',
			flex : .35,
			valueField : 'id',
			displayField: 'name',
			store : bussinessStore
		});
		
		//初始化容器中两个组件的第一个  参数列
		var paramPanel = Ext.widget('selectuserbyroleordeptinput',{
			flex : .35,
			multiSelect : false,
			fieldLabel : '人员选择'+'<font color="red">*</font>'
		});
		
		var paramContainer = Ext.widget('container',{
			width : 100,
			layout : {
				type : 'hbox',
				align : 'stretch'
			},
			items : [bussinessCombo,paramPanel]
		});
		
		me.selectGridPanel = Ext.widget('orgrelaleaderselectgridpanel',{
			flex : .5,
			storeAutoLoad : true,
			checked : false
		});
		
		me.selectedGridPanel = Ext.widget('orgrelaleaderselectedgridpanel',{
			flex : .5,
			storeAutoLoad : false,
			checked : false
		});
		
		Ext.apply(me,{
			items : [paramContainer,me.selectGridPanel,me.selectedGridPanel]
		});
		
		me.callParent(arguments);
	}
});