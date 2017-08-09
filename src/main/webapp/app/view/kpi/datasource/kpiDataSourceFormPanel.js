Ext.define('FHD.view.kpi.datasource.kpiDataSourceFormPanel', {
	extend : 'Ext.form.Panel',
	alias : 'widget.formEditPanel',
	isAdd: true,
	requires: [
              ],
	// 初始化方法
	initComponent : function() {
		var me = this;
		me.id = 'DateSourceForm';
		me.saveUrl = 'kpi/dataSource/saveDataSource.f';
		me.delUrl = 'kpi/dataSource/removeDataSourceById.f';
		me.connTestUrl ='kpi/dataSource/dataBaseConnectionTest.f';
		var dbTypeStore = Ext.create('Ext.data.Store', {// 机构类型store
			fields : ['id', 'text'],
			data : [{
						id : 'oracle',
						text : 'Oracle'
					}, {
						id : 'sqlServer',
						text : 'SqlServer'
					}
					, {
						id : 'mySQL',
						text : 'mySQL '
					}
					],
			autoLoad : true
		});
		var bbar = [// 菜单项
		{
				       text: '连接测试',
				       authority:'ROLE_ALL_SYS_BASESET_DSM_LINETEST',
				       iconCls: 'icon-connect',
				       handler: me.connTest,
				       scope: this		
			},
		       '->',
		       {
					text : "保存",
					authority:'ROLE_ALL_SYS_BASESET_DSM_SAVE',
					iconCls : 'icon-save',
					handler : me.save,
					scope : this
				}];
		// 数据源
		var driverName = Ext.create('Ext.form.TextField', {
					fieldLabel : '数据源名称' + '<font color=red>*</font>',
					allowBlank : false,// 不允许为空
					name : 'driverName',
					columnWidth : .5
				});
		// ip
		var ip = Ext.create('Ext.form.TextField', {
					fieldLabel : 'IP' + '<font color=red>*</font>',
					allowBlank : false,// 不允许为空
					name : 'ip',
					columnWidth : .5
				});
		
		// 端口
		var port = Ext.create('Ext.form.TextField', {
					fieldLabel : '端口' + '<font color=red>*</font>',
					allowBlank : false,// 不允许为空
					name : 'port',
					columnWidth : .5
				});
		// 数据库名称
		var dataBaseName = Ext.create('Ext.form.TextField', {
			fieldLabel : '数据库名称' + '<font color=red>*</font>',
			allowBlank : false,// 不允许为空
			name : 'dataBaseName',
			columnWidth : .5
		});
		
		// 登陆用户名
		var userName = Ext.create('Ext.form.TextField', {
					fieldLabel : '用户名' + '<font color=red>*</font>',
					allowBlank : false,// 不允许为空
					name : 'userName',
					columnWidth : .5
				});
		// 用户密码
		var passWord = Ext.create('Ext.form.TextField', {
					fieldLabel : '登陆密码',
					name : 'passWord',
					columnWidth : .5
				});
		// 数据库类型
		var dbType = Ext.create('Ext.form.field.ComboBox', {
					store : dbTypeStore,
					emptyText : FHD.locale.get('fhd.common.pleaseSelect'),
					editable : false,
					queryMode : 'local',
					fieldLabel : '数据库类型' + '<font color=red>*</font>',
					allowBlank : false,// 不允许为空
					name : 'dbType',
					displayField : 'text',
					valueField : 'id',
					triggerAction : 'all',
					columnWidth : .5,
					listeners: {
						select: function(combo,record) {
							if(record[0].data.id  == 'mySQL') {
								port.setValue('3306');
							} else if (record[0].data.id  == 'oracle'){
								port.setValue('1521');
							} else {
								port.setValue('1433');
							}
						}
					}
				});
		Ext.applyIf(me, {
					layout : 'column',
					width : FHD.getCenterPanelWidth() - 258,
					region: 'center',
					bodyPadding : "0 3 3 3",
					autoScroll : true,
					border : false,
					bbar : bbar,
					items : [{
								xtype : 'fieldset',// 基本信息fieldset
								collapsible : false,
								height : 300,
								defaults : {
									margin : '3 30 3 30',
									labelWidth : 100
								},
								layout : {
									type : 'column'
								},
								title : "数据源信息",
								items : [dbType,driverName, ip,port,dataBaseName, userName, passWord
										]
							}]

				});
		me.callParent(arguments);
	},
	load : function() {
		var me = this;
		me.form.waitMsgTarget = true;
		if (typeof(me.dataSourceId) != 'undefined') {
			me.form.load({
				 waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
						url : 'kpi/dataSource/findDataSourcebyid.f',
						params : {
							id : me.dataSourceId
						},
						failure : function(form, action) {
							alert("err 155");
						},
						success : function(form, action) {
							var formValue = form.getValues();
						}
					});
		}
	},
	addDs : function() {
		var me = this;
		me.isAdd = true;
		me.dataSourceId = null;
		me.getForm().reset();
	},
	save : function() {
		var me = this;
		var form = me.getForm();
		var treePanel = Ext.getCmp("DataSourceTree");
		if (form.isValid()) {
			if (me.isAdd) {
				FHD.submit({
					form : form,
					url : me.saveUrl,
					callback : function(objectMaps) {
						var node = {
							id : objectMaps.data.id,
							text : objectMaps.data.text,
							leaf : true,
							expanded : objectMaps.data.expanded
						};
						treePanel.getRootNode().appendChild(node);
						treePanel.getRootNode().expand();
						treePanel.getSelectionModel().select(treePanel
								.getRootNode().lastChild);
						treePanel
								.itemclickTree(treePanel.getRootNode().lastChild);
					}
				});
			} else {
				FHD.submit({
							form : form,
							url : me.saveUrl + '?id=' + me.dataSourceId,
							callback : function(objectMaps) {
								var nodeInfo = {
									id : objectMaps.data.id,
									text : objectMaps.data.text,
									leaf : true,
									expanded : objectMaps.data.expanded
								};
								var node=treePanel.getSelectionModel().getLastSelected();
								node.updateInfo(true,nodeInfo);
							}
						});
			}

		}

	},
	del : function() {
		var me = this;
		var treePanel = Ext.getCmp("DataSourceTree");
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {// 确认删除
					var id = me.dataSourceId;
					FHD.ajax({// ajax调用
						url : me.delUrl,
						params : {
							id : id
						},
						callback : function(data) {
							if (data) {// 删除成功！
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								treePanel.store.load();
								treePanel.getSelectionModel().select(treePanel
										.getRootNode());
								treePanel
										.itemclickTree(treePanel.getRootNode());
							} else {
								Ext.MessageBox.alert(FHD.locale
												.get('fhd.common.prompt'),
										'删除失败');
							}
						}
					});
				}
			}
		});
	},
	connTest: function() {
		var me = this;
		var form = me.getForm();
		var objvalues = form.getValues();
		if (form.isValid()) {
			if(me.body != undefined){
	        	me.body.mask("正在连接...","x-mask-loading");
	        }
			FHD.ajax({// ajax调用
				url : me.connTestUrl,
				params : {
					values : Ext.JSON.encode(objvalues)
				},
				timeout: 6000,
				callback : function(data) {
					if(me.body != undefined){
                    	me.body.unmask();
                    }
					if (data) {// 删除成功！
						FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
					} else {
						Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'连接测试失败');
					}
				}
			});
			
		}
		
	}
});