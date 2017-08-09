Ext.define('FHD.view.kpi.datasource.kpiDataSourceWindow', {
	extend : 'Ext.window.Window',
	alias : 'widget.kpiDataSouceWindow',
	requires: [
              ],
	mixins : {
		formula : 'FHD.ux.kpi.Formula'
	},
	requires : ['FHD.view.kpi.datasource.kpiParameterEditGrid'],
	height : 500,
	width : 720,
	modal : true,
	maximizable : true,
	border : false,
	dataBaseValid : false,
	localPathValid : false,
	initComponent : function() {
		var me = this;
		me.queryDsUrl = 'kpi/dataSource/findAllDataSource.f';
		var dbSourceStore = Ext.create('Ext.data.Store', {
					fields : ['id', 'text'],
					proxy : {
						type : 'ajax',
						url : me.queryDsUrl,
						reader : {
							type : 'json',
							root : 'datas'
						}
					},
					autoLoad : true
				});
		var collectMethodStore = Ext.create('Ext.data.Store', {
					fields : ['id', 'text'],
					data : [{
								id : '01',
								text : 'SQL'
							}, {
								id : '02',
								text : 'PROC'
							}],
					autoLoad : true
				});
		var dataTreePanel = Ext.create('Ext.tree.Panel', {
					flex : 1,
					border : true,
					region : 'west',
					maxWidth : 220,
					title : FHD.locale.get('fhd.formula.operateArea'),
					autoScroll : false,
					root : {
						text : '外部程序',
						iconCls : 'icon-folder',
						expanded : true,
						autoLoad : true,
						children : [{
									text : '数据库',
									iconCls : 'icon-information',
									leaf : true
								}, {
									text : '本地方法',
									iconCls : 'icon-information',
									leaf : true
								}]
					},
					listeners : {
						itemclick : function(view, node) {
							switch (node.data.text) {
								case '数据库' :
									cardPanel.setActiveItem(0);
									if (!me.dataBaseValid) {
										Ext.getCmp('save${param._dc}')
												.setDisabled(true);
									}
									break;
								case '本地方法' :
									if (!me.localPathValid) {
										Ext.getCmp('save${param._dc}')
												.setDisabled(true);
									}
									cardPanel.setActiveItem(1);
									break;
								default :
									break;
							}
						}
					}
				});
		var localLab = Ext.create('Ext.form.Label', {
			flex : 0.3,
			text : FHD.locale.get('fhd.formula.targetName') + '：'+ me.targetName,
			cls : "cssLabel",//
			autoShow : true,
			margin: '3 30 3 10'
			});

		// 数据库类型
		var dbSource = Ext.create('Ext.form.field.ComboBox', {
					store : dbSourceStore,
					emptyText : FHD.locale.get('fhd.common.pleaseSelect'),
					editable : false,
					queryMode : 'local',
					fieldLabel : '数据源' + '<font color=red>*</font>',
					allowBlank : false,// 不允许为空
					name : 'dbSource',
					displayField : 'text',
					triggerAction : 'all',
					columnWidth : 1,
					listeners : {
						render : function() {
							dbSource.setValue(me.dataSource);
						}
					}
				});
		// 采集方式（sql检索或存储过程）
		var collectMethod = Ext.create('Ext.form.field.ComboBox', {
					store : collectMethodStore,
					emptyText : FHD.locale.get('fhd.common.pleaseSelect'),
					editable : false,
					queryMode : 'local',
					fieldLabel : '采集方式' + '<font color=red>*</font>',
					allowBlank : false,// 不允许为空
					name : 'collectMethod',
					displayField : 'text',
					valueField : 'id',
					triggerAction : 'all',
					columnWidth : 1,
					listeners : {
						render : function() {
							collectMethod.setValue(me.collectMethod);
						}
					}
				});

		var field = Ext.create('Ext.form.FieldSet', {
					collapsible : false,
					border : true,
					height : 100,
					defaults : {
						margin : '3 30 3 30',
						labelWidth : 100
					},
					layout : {
						type : 'form'
					},
					items : [dbSource, collectMethod]
					
				});
		var dataFormPanel = Ext.create('Ext.form.Panel', {
			border : false,
			layout : {
				type : 'vbox',
				align : 'stretch'
			},
			flex : 1,
			title : '外部数据注入',
			listeners : {
				render : function() {
					setTimeout(function() {
								me.editor = KindEditor.create(
										'textarea[name="content"]', {
											width : pa.getWidth(),
											height : pa.getHeight() - 5,
											newlineTag : 'br',
											resizeType : 0,
											allowPreviewEmoticons : false,
											allowImageUpload : false,
											items : [],
											allowFileManager : false,
											afterCreate : function(id) {
												// 当修改时，设置公式默认值
												if (me.formulaContent == undefined) {
													this.html("");
												} else {
													if(me.dataSource) {
														this.html(me.formulaContent);
													}													
												}
											},
											afterFocus : function() {
												if (this.html() == undefined
														|| this.html() == '') {
													this.text("");
												} else {
													var orginalValue = this
															.html()
															.replace(
																	/<[^>].*?>/g,
																	"");
													this.html("").appendHtml(orginalValue);
												}
											}
										});
							}, 1000);
				}
			}
		});
		var pa = Ext.create('Ext.container.Container', {
			flex : 5,
			margin : '2 2 4 2',
			html : '<textarea id="textarea${param._dc}" name="content" style="width:100%;height:100%;"></textarea>',
			listeners : {
				resize : function(t, width, height) {
					if (me.editor) {
						me.editor.resize(width, height);
					}
				}
			}
		});
		if ('' != me.targetName && undefined != me.targetName) {
			dataFormPanel.add(field);
			dataFormPanel.add(pa);
		} else {
			dataFormPanel.add(field);
			dataFormPanel.add(pa);
		}

		var methodPath = Ext.create('Ext.form.TextField', {
					fieldLabel : '本地方法路径' + '<font color=red>*</font>',
					allowBlank : false,// 不允许为空
					name : 'methodPath',
					listeners : {
						render : function() {
							methodPath.setValue(me.localMethodPath);
						}
					}
				});

		var methodField = Ext.create('Ext.form.FieldSet', {
					collapsible : false,
					flex:2,
					border : true,
					layout : {
						type : 'form'
					},
					items : [methodPath]
				});
		var gridPanel = Ext.widget("kpiParameterEditGrid", {
					parameterJson : me.parameterJson
				});
		var localFormPanel = Ext.create('Ext.form.Panel', {
					border : false,
					layout : {
						type : 'vbox',
						align : 'stretch'
					},
					flex : 1,
					title : '本地方法'
				});
		if ('' != me.targetName && undefined != me.targetName) {
			localFormPanel.add(localLab);
			localFormPanel.add(methodField);
		} else {
			localFormPanel.add(methodField);
		}

		var localMethodPanel = Ext.create('Ext.panel.Panel', {
					flex : 2,
					border:false,
					layout: {
		                align: 'stretch',
		                type: 'vbox'
		            },
					items : [localFormPanel, gridPanel]
				});

		var cardPanel = Ext.create('FHD.ux.CardPanel', {
					border : false,
					region : 'center',
					activeItem : 0,
					items : [dataFormPanel, localMethodPanel]
				});

		Ext.applyIf(me, {
					border : false,
					layout : {
						type : 'border',
						padding : '0 0 0 0'
					},
					items : [dataTreePanel, cardPanel],
					listeners: {
						afterrender: function(){
							if(me.dataSource){
								cardPanel.setActiveItem(0);
							} else if(me.localMethodPath){
								cardPanel.setActiveItem(1);
							}
						}
					}
				});
		me.buttons = [{
			xtype : 'button',
			text : FHD.locale.get('fhd.formula.validate'),
			handler : function() {
				var activeItem = cardPanel.getActiveItem();
				if (activeItem == dataFormPanel) {
					if ('' != dbSource.getValue()
							&& undefined != dbSource.getValue()
							&& '' != collectMethod.getValue()
							&& undefined != collectMethod.getValue()) {
						me.dataBaseValid = true;
						Ext.getCmp('save${param._dc}').setDisabled(false);
						FHD.notification(FHD.locale.get('fhd.formula.validateSuccess'),FHD.locale.get('fhd.common.prompt'));
					} else {
						me.dataBaseValid = false;
						Ext.getCmp('save${param._dc}').setDisabled(true);
						Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),
								FHD.locale.get('fhd.formula.vallidateFailure'));
					}
				} else {
					// 本地方法面板验证
					if ('' != methodPath.getValue() && undefined != methodPath.getValue()) {
	                   
						me.validatePath(methodPath.getValue(),gridPanel,me);						
					} 
					else {
						me.localPathValid = false;
						Ext.getCmp('save${param._dc}').setDisabled(true);
						Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),
								FHD.locale.get('fhd.formula.vallidateFailure'));
					}
				}
			}
		}, {
			id : 'save${param._dc}',
			xtype : 'button',
			text : FHD.locale.get('fhd.common.confirm'),
			disabled : true,
			handler : function() {
				var activeItem = cardPanel.getActiveItem();
				if (activeItem == dataFormPanel) {
					var dataBaseContent = me.editor.html().replace(
							/<[^>].*?>/g, "");
					dataBaseContent = me.replaceall(dataBaseContent, "&lt;",
							"<");
					dataBaseContent = me.replaceall(dataBaseContent, "&gt;",
							">");
					dataBaseContent = me.replaceall(dataBaseContent, "&amp;",
							"&");
					me.onSubmit(dataBaseContent, dbSource.getValue(),
							collectMethod.getValue());
					me.clearLocalMethod();
				} else {
					var parameter = gridPanel.saveGrid();
					Ext.getCmp('save${param._dc}').setDisabled(false);
					FHD.notification(FHD.locale.get('fhd.formula.validateSuccess'),FHD.locale.get('fhd.common.prompt'));
					me.onSubmitLocalMethod(methodPath.getValue(), parameter);
					me.clearDataSource();
				}
				me.close();
			}
		}, {
			xtype : 'button',
			text : FHD.locale.get('fhd.common.close'),
			handler : function() {
				me.close();
			}
		}]
		me.callParent(arguments);
	},
	setFormulaContent : function(value) {
		var me = this;
		me.formulaContent = value;
	},
	setTargetId : function(value) {
		var me = this;
		me.targetId = value;
	},
	getTargetId : function() {
		var me = this;
		return me.targetId;
	},
	setTargetName : function(value) {
		var me = this;
		me.targetName = value;
	},
	validatePath: function(value,gridpanel,me) {
		var length = gridpanel.store.data.items.length;
		FHD.ajax({// ajax调用
			url : 'kpi/dataSource/validateMethodPath.f',
			params : {
				methodPath : value,
				paramSize: length
			},
			callback : function(data) {
				if (data) {
					me.localPathValid = true;
					Ext.getCmp('save${param._dc}').setDisabled(false);
					FHD.notification(FHD.locale.get('fhd.formula.validateSuccess'),FHD.locale.get('fhd.common.prompt'));
				} else {
					me.localPathValid = false;
					Ext.getCmp('save${param._dc}').setDisabled(true);
					Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),
							FHD.locale.get('fhd.formula.vallidateFailure'));
				}
			}
		});

	},
	onSubmit : Ext.emptyFn()
})