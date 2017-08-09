/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderMainPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.orgrelaleadermainpanel',
	requires: [
    	'FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderParamPanel',
    	'FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderGridPanel'
    ],
    orgId : '',
    roleId : '',
    valueArray : [],
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	initComponent : function(){
		
		var me = this;
		//初始化容器中两个组件的第一个  参数列
		var paramPanel = Ext.widget('orgrelaleaderparampanel',{
			height : 50
		});
		
		me.gridPanel = Ext.widget('orgrelaleadergridpanel',{
			flex : .92,
			storeAutoLoad : false,
			listeners : {
	        	select : function(c,r,o){
	        		
	        	}
	        }
		});
		
		Ext.apply(me,{
			items : [paramPanel,me.gridPanel]
		});
		
		me.callParent(arguments);
	}
});