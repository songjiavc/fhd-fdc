Ext.define('FHD.demo.treeselector.TreeSelector', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.TreeSelector',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;

//		var region = Ext.create('FHD.ux.treeselector.TreeSelector', {
//					title : '请您选择部门',
//					noteField : 'orgcode',
//					columns : [{
//								dataIndex : 'orgcode',
//								header : '部门编号'
//							}, {
//								dataIndex : 'orgname',
//								header : '部门名称',
//								isPrimaryName : true
//							}],
//					entityName : 'com.fhd.entity.sys.orgstructure.SysOrganization',
//					parentKey : 'parentOrg.id',
//					relationKey : 'orgseq',
//					value : '[{id:"XD00"},{id:"eda8ffeab0da4159be0ff924108e3883"}]',
//					fieldLabel : '部门',
//					labelAlign : 'left',
//					multiSelect : true,
//					checkable : true,
//					name : 'deptFirst'
//				});
		
		var region = Ext.create('FHD.ux.treeselector.TreeSelector', {
			title : '请您选择风险',
        	treeRootText:'风险',
			columns : [{
						dataIndex : 'code',
						header : '风险编号'
					}, {
						dataIndex : 'name',
						header : '风险名称',
						isPrimaryName : true
					}],
			treeUrl:'/cmp/risk/getRiskTreeRecord',
			initUrl:'/cmp/risk/getRiskByIds',
			fieldLabel : '风险分类',
			labelAlign : 'left',
			multiSelect : true,
			name : 'risk',
			columnWidth : '.5'
		});

		// 表单panel
		var form = Ext.create("Ext.form.Panel", {
					autoScroll : true,
					border : false,
					bodyPadding : "5 5 5 5",
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
								items : [region]
							}]
				});
		Ext.applyIf(me, {
					autoScroll : true,
					border : false,
					items : [form]
				});
		me.callParent(arguments);

	}
})