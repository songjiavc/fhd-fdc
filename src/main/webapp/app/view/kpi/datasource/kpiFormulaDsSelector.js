Ext.define('FHD.view.kpi.datasource.kpiFormulaDsSelector', {
	extend : 'Ext.form.FieldContainer',
	alias : 'widget.formulaselector',

	allowBlank : true,
	labelWidth : 100,
	fieldLabel : FHD.locale.get('fhd.formula.formulaDefinition'),
	height : 80,
	/**
	 * 要计算的对象的类型
	 */
	type : 'kpi',
	/**
	 * 应用程序数据显示的类型
	 */
	showType : 'all',
	/**
	 * 要计算的对象的列属性
	 */
	column : 'assessmentValueFormula',
	/**
	 * 要计算的对象的id
	 */
	targetId : '',
	/**
	 * 要计算的对象的名称
	 */
	targetName : '',
	/**
	 * 选择计算类型的name属性
	 */
	formulaTypeName : '',
	/**
	 * 公式name属性
	 */
	formulaName : '',

	/**
	 * @cfg {Number} 列数
	 */
	triggerCols : 20,

	/**
	 * @cfg {Number} 行数
	 */
	triggerRows : 3,

	/**
	 * 外部数据源
	 */
	dataSource : '',

	/**
	 * 外部采集方式
	 */
	collectMethod : '',
	/**
	 * 本地方法路径
	 */
	localMethodPath : '',
	/**
	 * 参数方法JSON
	 */
	parameterJson : [],
	select : null,
	trigger : null,

	initComponent : function() {
		var me = this;
		me.trigger = Ext.create('FHD.view.kpi.datasource.kpiFormulaDsTrigger', {
					hideLabel : true,
					emptyText : '',
					flex : 1.5,
					allowBlank : me.allowBlank,
					cols : me.triggerCols,
					rows : me.triggerRows,
					name : me.formulaName,
					type : me.type,
					showType : me.showType,
					column : me.column,
					targetId : me.targetId,
					targetName : me.targetName,
					dataSource : me.dataSource,
					collectMethod : me.collectMethod,
					parameterJson : me.parameterJson,
					localMethodPath : me.localMethodPath
				});

		me.select = Ext.create('FHD.ux.dict.DictRadio', {
			dictTypeId : '0sys_use_formular',
			hideLabel : true,
			columns : 4,
			labelAlign : 'center',
			flex : 1,
			allowBlank : me.allowBlank,
			name : me.formulaTypeName,
			defaultValue : '0sys_use_formular_manual',
			listeners : {
				change : function(t, newValue, oldValue, options) {
					if (!newValue[me.formulaTypeName]) {
						me.trigger.setDisabled(true);
					} else if (newValue[me.formulaTypeName] == "0sys_use_formular_manual") {
						me.trigger.setDisabled(true);
					} else if (newValue[me.formulaTypeName] == "0sys_use_formular_formula") {
						me.trigger.setDisabled(false);
					} else if (newValue[me.formulaTypeName] == '0sys_use_formular_export') {
						me.trigger.setDisabled(false);
					}
					me.trigger.radioValue = newValue[me.formulaTypeName];
				}
			}
		});


		Ext.applyIf(me, {
			layout : {
				type : 'vbox',
				align : 'stretch'
			},
			height : me.height,
			labelWidth : me.labelWidth,
			fieldLabel : me.fieldLabel,
			items : [me.select, me.trigger]
			});

		me.callParent(arguments);
	},
	setTargetId : function(value) {
		var me = this;
		me.trigger.setTargetId(value);
	},
	setTargetName : function(value) {
		var me = this;
		me.trigger.setTargetName(value);
	},
	setRadioValue : function(value) {
		var me = this;
		me.select.setValue(value);
	},
	getRadioValue : function() {
		var me = this;
		var value = '';
		me.select.items.each(function(item, idx) {
					if (item.checked) {
						value = item.inputValue;
						return;
					}
				});
		return value;
	},
	setTriggerValue : function(value) {
		var me = this;
		me.trigger.setValue(value);
	},
	getTriggerValue : function() {
		var me = this;
		return me.trigger.getValue();
	},
	setDataSource : function(value) {
		var me = this;
		me.trigger.setDataSource(value);
	},
	getDataSource : function() {
		var me = this;
		return me.trigger.getDataSource();
	},
	setCollectMethod : function(value) {
		var me = this;
		me.trigger.setCollectMethod(value);
	},
	getCollectMethod : function() {
		var me = this;
		return me.trigger.getCollectMethod();
	},
	setLocalMehtodPath : function(value) {
		var me = this;
		me.trigger.setLocalMehtodPath(value);
	},
	getLocalMethodPath : function() {
		var me = this;
		return me.trigger.getLocalMethodPath();
	},
	setParameterJson : function(value) {
		var me = this;
		me.trigger.setParameterJson(value);
	},
	getParameterJson : function() {
		var me = this;
		return me.trigger.getParameterJson();
	}
});