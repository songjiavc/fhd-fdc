Ext.define('FHD.view.kpi.export.data.GenerationGatherData', {
	extend : 'Ext.container.Container',

	layout : {
		type : 'vbox',
		align : 'stretch'
	},

	initComponent : function() {
		var me = this;

		var kpiFieldSet = Ext.widget('fieldset', {
					xtype : 'fieldset', // 基本信息fieldset
					autoHeight : true,
					autoWidth : true,
					collapsible : true,
					defaults : {
						margin : '3 30 3 30',
						labelWidth : 100
					},
					layout : {
						type : 'column'
					},
					title : "指标"
				});

		var smFieldSet = Ext.widget('fieldset', {
					xtype : 'fieldset', // 基本信息fieldset
					autoHeight : true,
					autoWidth : true,
					collapsible : true,
					defaults : {
						margin : '3 30 3 30',
						labelWidth : 100
					},
					layout : {
						type : 'column'
					},
					title : "目标"
				});

		var scFieldSet = Ext.widget('fieldset', {
					xtype : 'fieldset', // 基本信息fieldset
					autoHeight : true,
					autoWidth : true,
					collapsible : true,
					defaults : {
						margin : '3 30 3 30',
						labelWidth : 100
					},
					layout : {
						type : 'column'
					},
					title : "记分卡"
				});

		var kpiBtn = Ext.create('Ext.Button', {
					text : '生成数据',
					handler : function() {
						var year = me.yearComb.getValue();
						FHD.ajax({
									async : true,
									url : __ctxPath
											+ '/kpi/data/generationkpigatherdata.f',
									params : {
										year : year
									},
									callback : function(data) {
										if (data) {
											FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
										}
									}
								});
					}
				});

		var smBtn = Ext.create('Ext.Button', {
					text : '生成数据',
					handler : function() {
						var year = me.smyearComb.getValue();
						FHD.ajax({
									async : true,
									url : __ctxPath
											+ '/kpi/data/generationsmgatherdata.f',
									params : {
										year : year
									},
									callback : function(data) {
										if (data) {
											FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
										}
									}
								});
					}
				});

		var scBtn = Ext.create('Ext.Button', {
					text : '生成数据',
					handler : function() {
						var year = me.scyearComb.getValue();
						FHD.ajax({
									async : true,
									url : __ctxPath
											+ '/kpi/data/generationscgatherdata.f',
									params : {
										year : year
									},
									callback : function(data) {
										if (data) {
											FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
										}
									}
								});
					}
				});

		var yearStore = Ext.create('Ext.data.Store', { // 告警方案store
			fields : ['id', 'text'],
			proxy : {
				type : 'ajax',
				url : __ctxPath + '/kpi/data/findyeardata.f',
				reader : {
					type : 'json',
					root : 'datas'
				}
			},
			autoLoad : true
		});

		me.yearComb = Ext.create('Ext.form.field.ComboBox', {
					store : yearStore,
					name : 'alarmId',
					columnWidth : .3,
					fieldLabel : '年',
					allowBlank : true,
					editable : false,
					queryMode : 'local',
					displayField : 'text',
					valueField : 'id',
					triggerAction : 'all'
				});
		me.smyearComb = Ext.create('Ext.form.field.ComboBox', {
					store : yearStore,
					name : 'alarmId',
					columnWidth : .3,
					fieldLabel : '年',
					allowBlank : true,
					editable : false,
					queryMode : 'local',
					displayField : 'text',
					valueField : 'id',
					triggerAction : 'all'
				});
		me.scyearComb = Ext.create('Ext.form.field.ComboBox', {
					store : yearStore,
					name : 'alarmId',
					columnWidth : .3,
					fieldLabel : '年',
					allowBlank : true,
					editable : false,
					queryMode : 'local',
					displayField : 'text',
					valueField : 'id',
					triggerAction : 'all'
				});

		kpiFieldSet.add(me.yearComb);

		kpiFieldSet.add(kpiBtn);

		smFieldSet.add(me.smyearComb);

		smFieldSet.add(smBtn);

		scFieldSet.add(me.scyearComb);

		scFieldSet.add(scBtn);

		Ext.apply(me, {
					items : [kpiFieldSet, smFieldSet, scFieldSet]
				});

		me.callParent(arguments);
	},

	reloadData : function() {
		var me = this;
	}

})