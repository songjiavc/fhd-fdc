Ext.define('FHD.demo.treelistselector.TreeListSelector', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.TreeListSelector',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		// 树选择布局表单
		var emp = Ext.create('FHD.ux.treelistselector.TreeListSelector', {
					//treeRootText:'aa',
					title : '选择地区员工',
					noteField : 'userid',
					columns : [{
								dataIndex : 'userid',
								header : '员工编号'
							}, {
								dataIndex : 'realname',
								header : '姓名',
								isPrimaryName : true
							}],
					treeEntityName : 'com.fhd.entity.sys.orgstructure.SysOrganization',
					treeQueryKey : 'orgname',
					parentKey : 'parentOrg.id',
					relationKey : 'orgseq',
					entityName : 'com.fhd.entity.sys.orgstructure.SysEmployee',
					foreignKey : 'sysOrganization.id',
					queryKey : 'realname',
					fieldLabel : '人员',
					labelAlign : 'left',
					multiSelect : true,
					value : '[{id:"chenjie"},{id:"hanwei"}]',
					name : 'emp'
				});

		// 表单panel
		var form = Ext.create("Ext.form.Panel", {
					autoScroll : true,
					border : false,
					bodyPadding : "5 5 5 5",
					// tbar:tbar,
					items : [{
								xtype : 'fieldset',// 基本信息fieldset
								collapsible : true,
								defaults : {
									margin : '7 30 3 30',
									columnWidth : .5
								},
								layout : {
									type : 'column'
								},
								title : "基础信息",
								items : [emp]
							}]
				});

		Ext.applyIf(me, {
					autoScroll : true,
					border : false,
					bodyPadding : "5 5 5 5",
					items : [form]
				});
		me.callParent(arguments);

	}
})