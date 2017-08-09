Ext.define('FHD.demo.treegrid.TreeGridEdit', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.TreeGridEdit',
	initComponent : function() {
		var me = this;
		me.id = 'TreeEditList';
		var treeGrid = Ext.getCmp('TreeGridList');
		var formButtons = [// 表单按钮
		{
					text : FHD.locale.get('fhd.common.save'),
					handler : save
				}, {
					text : FHD.locale.get('fhd.common.cancel'),
					handler : cancel
				}];
		/** *attribute end** */
		/** *function start** */
		function save() {// 保存方法
			var form = formPanel.getForm();
			if (form.isValid()) {
				if (isAdd) {// 新增
					FHD.submit({
								form : form,
								url : addUrl,
								callback : function(data) {
									treegrid.store.load()
								}
							});// 参数依次为form，url，store
				} else {// 更新
					FHD.submit({
								form : form,
								url : updateUrl,
								callback : function(data) {
									treegrid.store.load()
								}
							});
				}
				treeGrid.formwindow.close();
			}

		}
		function cancel() {// 取消方法
			treeGrid.formwindow.close();
		}
		/** *function end** */
		/** *form start** */
		var formColums = [// form表单的列
		{
					xtype : 'hidden',
					name : 'id',
					id : 'id'
				}, {
					xtype : 'treecombox',
					name : 'parentId',
					fieldLabel : FHD.locale
							.get('fhd.pages.test.field.parentName'),
					valueField : 'id',
					displayField : 'text',
					vtype : 'treeNode',
					maxPickerHeight : 200,
					maxPickerWidth : 250,
					store : treeGrid.treeComboxStore
				}, {
					xtype : 'textfield',
					fieldLabel : FHD.locale.get('fhd.pages.test.field.title')
							+ '<font color=red>*</font>',
					name : 'title',
					vtype : 'uniqueTitle',
					allowBlank : false
				}, {
					xtype : 'textfield',
					fieldLabel : FHD.locale.get('fhd.pages.test.field.name')
							+ '<font color=red>*</font>',
					name : 'name',
					allowBlank : false
				}, {
					xtype : 'numberfield',
					fieldLabel : 'NUMBER',
					name : 'num',
					maxValue : 92,
					minValue : 0,
					allowDecimals : true, // 允许小数点
					nanText : '请输入数字',
					step : 0.5,
					allowBlank : true
				}, {
					xtype : 'combobox',
					fieldLabel : FHD.locale
							.get('fhd.pages.test.field.myLocale')
							+ '<font color=red>*</font>',
					store : treeGrid.fieldstore,
					displayField : 'name',
					valueField : 'id',
					name : 'myLocale',
					allowBlank : false,
					editable : false
				}];
		var formPanel = Ext.create('Ext.form.Panel', {
					bodyPadding : 5,
					layout : 'fit',
					items : [{
								xtype : 'fieldset',
								defaults : {
									columnWidth : 1 / 1
								},// 每行显示一列，可设置多列
								layout : {
									type : 'column'
								},
								bodyPadding : 5,
								collapsed : false,
								collapsible : false,
								title : FHD.locale.get('fhd.common.baseInfo'),
								items : formColums
							}],
					buttons : formButtons
				});
		/** *form end** */
		/** *CustomValidator start** */
		Ext.apply(Ext.form.field.VTypes, {
					// 验证title不重复
					uniqueTitle : function(val, field) {
						var id = field.up("form").down('#id');
						var flag = false;
						FHD.ajax({
									url : 'test/checkTitle.f',
									async : false,// 这一项必须加上，否则后台返回true,flag的值也为false
									params : {
										title : val,
										id : id.value
									},
									callback : function(data) {
										if (data) {
											flag = true;
										}
									}
								})
						return flag;
					},
					uniqueTitleText : FHD.locale
							.get('fhd.pages.test.uniqueMsg'),
					// 验证parentId的合法性
					treeNode : function(val, field) {
						var parentId = field.up("form").down('[name=parentId]').value;
						var id = field.up("form").down('#id').value;
						var flag = false;
						if (parentId == null || parentId == '') {
							flag = true;
						} else if (parentId == id) {
							flag = false;
						} else {
							FHD.ajax({
										url : 'test/checkParent.f',
										async : false,// 这一项必须加上，否则后台返回true,flag的值也为false
										params : {
											parentId : parentId,
											id : id
										},
										callback : function(data) {
											if (data) {
												flag = true;
											}
										}
									})
						}
						return flag;
					},
					treeNodeText : FHD.locale.get('fhd.pages.test.treeNodeMsg')
				});
		/** *CustomValidator end** */

		treeGrid.on('resize', function(me) {
					formPanel.setWidth(me.getWidth() - 10);

				})
		if (typeof(treeGrid.paramId) != "undefined") {
			formPanel.form.load({
						url : 'test/getTestMvc.f',
						params : {
							id : treeGrid.paramId
						},
						failure : function(form, action) {
							alert('页面初始化错误');
						}
					});
		}
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [formPanel]
		});
        me.callParent(arguments);
	}
})