/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.selectcompoments.SelectUserByRoleOrDept', {
    extend: 'Ext.container.Container',
    alias: 'widget.selectuserbyroleordept',
	requires: [
    	'FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptParamPanel',
    	'FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptGridPanel'
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
		var paramPanel = Ext.widget('selectuserbyroleordeptparampanel',{
			width : 100
		});
		
		me.gridPanel = Ext.widget('selectuserbyroleordeptgridpanel',{
			flex : .45,
			storeAutoLoad : false,
			checked : false,
			listeners : {
	        	select : function(c,r,o){
	        		var me = this;
	        		//将选中记录copy到已选列表中
	        		var selGridPanel = me.up('selectuserbyroleordept').selGridPanel;
	        		selGridPanel.insertRecord(r);
	        	}
	        }
		});
		
		me.selGridPanel = Ext.widget('selectuserbyroleordeptgridpanel',{
			flex : .45,
			pagable : false,
			multiSelect : me.multiSelect,
			url: __ctxPath + '/sys/auth/user/findSysUserPageByEmpIds.f',
			storeAutoLoad : false,
			checked : false,
			searchable : false,
			afterlayout : true,
			recordArray : me.recordArray,
			insertRecord : function(r){
				var me = this;
				if(!me.multiSelect){
					me.getStore().removeAll();
					me.getStore().insert(0,r);
				}else{
				//判断是否存在该元素
					if(!me.isHasItemInGrid(r.get('id'))){
						me.getStore().insert(0,r);
					}
				}
			},
			listeners : {
				itemdblclick : function(c,r,o){
					//双击删除选中的记录m
					var me = this;
					me.getStore().remove(r);
					me.up('selectuserbyroleordept').gridPanel.getSelectionModel().deselect(r);
				}
			}
		});
		
		Ext.apply(me,{
			items : [paramPanel,me.gridPanel,me.selGridPanel]
		});
		me.callParent(arguments);
		//初始化查询部门
		if(me.orgId != '' || me.roleId != '' || me.useUserOrg){
			if(me.useUserOrg){
				me.orgId =  __user.majorDeptId;
			}
			paramPanel.initParam(me.orgId,me.roleId);
			paramPanel.queryFun();
		}
		//初始化组件的时候使用
		if(me.valueArray.length > 0){
			me.selGridPanel.reloadData({ids : me.valueArray});
		}
	}
});