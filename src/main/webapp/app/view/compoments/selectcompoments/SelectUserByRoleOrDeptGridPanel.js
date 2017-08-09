/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptGridPanel', {
    extend: 'FHD.ux.GridPanel',
    autoLoad : false,
    url: __ctxPath + '/sys/auth/user/findSysUserPageByRoleIdAndDeptId.f',
    alias: 'widget.selectuserbyroleordeptgridpanel',
	pageSize : 10,
	extraParams: {
    	checkable: false,
    	subCompany: false,
    	companyOnly : false
    },
	initParam : function(extraParams){
		var me = this;
		me.extraParams = extraParams;
	},
    
	reloadData : function(extraParams){
		var me=this;
		me.store.proxy.extraParams = extraParams;
    	me.store.load();
	},
	
	initComponent : function(){
		var me = this;
		var cols = [
			{
				dataIndex:'id',
				hidden:true
			},{
	            header: "人员编号",
	            dataIndex: 'empcode',
	            flex:1
	        },{
	        	header: "人员名称",
	            dataIndex: 'empname',
	            flex:1
	        },{
	        	header : "部门名称",
	        	dataIndex : 'orgName',
	        	flex : 1
	        },{
	        	header : "角色名称",
	        	dataIndex : 'roleName',
	        	flex : 1
	        }
        ];
        Ext.apply(me,{
        	cols : cols
        });
        me.callParent(arguments);
	}
});