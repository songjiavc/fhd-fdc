Ext.define('FHD.view.kpi.datasource.kpiFormulaDsTrigger', {
	extend : 'Ext.form.field.Picker',
	alias : 'widget.formulatrigger',
	editable : false,

	triggerCls : Ext.baseCSSPrefix + 'form-function-trigger',

	growMin : 60,

	cols : 20,

	rows : 4,

	// 更改field模版，用textarea
	fieldSubTpl : [
			'<textarea id="{id}" {inputAttrTpl}',
			'<tpl if="name"> name="{name}"</tpl>',
			'<tpl if="rows"> rows="{rows}" </tpl>',
			'<tpl if="cols"> cols="{cols}" </tpl>',
			'<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
			'<tpl if="size"> size="{size}"</tpl>',
			'<tpl if="maxLength !== undefined"> maxlength="{maxLength}"</tpl>',
			'<tpl if="readOnly"> readonly="readonly"</tpl>',
			'<tpl if="disabled"> disabled="disabled"</tpl>',
			'<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
			' class="{fieldCls} {typeCls}" ',
			'<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
			' autocomplete="off">\n',
			'<tpl if="value">{[Ext.util.Format.htmlEncode(values.value)]}</tpl>',
			'</textarea>', {
				disableFormats : true
			}],

	// 添加行数和列数到模版数据中
	getSubTplData : function() {
		var me = this, fieldStyle = me.getFieldStyle(), ret = me.callParent();

		if (me.grow) {
			if (me.preventScrollbars) {
				ret.fieldStyle = (fieldStyle || '')
						+ ';overflow:hidden;height:' + me.growMin + 'px';
			}
		}

		Ext.applyIf(ret, {
					cols : me.cols,
					rows : me.rows
				});

		return ret;
	},

	fieldStyle: 'height:50px;',

	// 初始化组件
	initComponent : function() {
		var me = this;

		Ext.apply(me, {
					height : me.height
				})

		me.callParent(arguments);
	},
	createMyPicker : function() {
		var me = this;
		if (me.radioValue && me.radioValue == '0sys_use_formular_export') {
			me.window = Ext.create(
					'FHD.view.kpi.datasource.kpiDataSourceWindow', {
						id : 'dataSourseId${param._dc}',
						type : me.type,
						showType : me.showType,
						column : me.column,
						targetId : me.targetId,
						targetName : me.targetName,
						formula : me.value,
						radioValue : me.radioValue,
						dataSource : me.dataSource,
						collectMethod : me.collectMethod,
						localMethodPath : me.localMethodPath,
						parameterJson : me.parameterJson,
						onSubmit : function(ret, dataSource, collectMethod) {
							me.setValue(ret);
							me.setDataSource(dataSource);
							me.setCollectMethod(collectMethod);
						},
						onSubmitLocalMethod : function(ret, parameter) {
							me.setValue(ret);
							me.setLocalMehtodPath(ret);
							me.setParameterJson(parameter);
						},
						clearLocalMethod : function() {
							me.setLocalMehtodPath(null);
							me.setParameterJson(null);
						},
						clearDataSource : function() {
							me.setDataSource(null);
							me.setCollectMethod(null);
						}
					});
		} else {
			me.window = Ext.create('FHD.ux.kpi.FormulaWindow', {
						id : 'formulaId${param._dc}',
						type : me.type,
						showType : me.showType,
						column : me.column,
						targetId : me.targetId,
						targetName : me.targetName,
						formula : me.value,
						radioValue : me.radioValue,
						dataSource : me.dataSource,
						collectMethod : me.collectMethod,
						localMethodPath : me.localMethodPath,
						parameterJson : me.parameterJson,
						onSubmit : function(ret, dataSource, collectMethod) {
							me.setValue(ret);
							me.setDataSource(dataSource);
							me.setCollectMethod(collectMethod);
						},
						onSubmitLocalMethod : function(ret, parameter) {
							me.setValue(ret);
							me.setLocalMehtodPath(ret);
							me.setParameterJson(parameter);
						},
						clearLocalMethod : function() {
							me.setLocalMehtodPath(null);
							me.setParameterJson(null);
						},
						clearDataSource : function() {
							me.setDataSource(null);
							me.setCollectMethod(null);
						},
						onSubmit : function(ret) {
							me.setValue(ret);
						}
					});

		}
		me.window.setFormulaContent(me.getValue());

		me.window.show();
	},
	// override onTriggerClick
	onTriggerClick : function() {
		var me = this;
		me.createMyPicker();
	},
	setTargetId : function(value) {
		var me = this;
		me.targetId = value;
	},
	setTargetName : function(value) {
		var me = this;
		me.targetName = value;
	},
	getTargetId : function() {
		var me = this;
		return me.targetId;
	},
	getTargetName : function() {
		var me = this;
		return me.targetName;
	},
	// 返回结果值给该组件
	getSubmitData : function() {
		var me = this;
		return me.getValue();
	},
	// 获得值
	getValue : function() {
		var me = this;
		return me.value;
	},
	setDataSource : function(value) {
		var me = this;
		me.dataSource = value;
	},
	getDataSource : function() {
		var me = this;
		return me.dataSource;
	},
	setCollectMethod : function(value) {
		var me = this;
		me.collectMethod = value;
	},
	getCollectMethod : function() {
		var me = this;
		return me.collectMethod;
	},
	setLocalMehtodPath : function(value) {
		var me = this;
		me.localMethodPath = value;
	},
	getLocalMethodPath : function() {
		var me = this;
		return me.localMethodPath;
	},
	setParameterJson : function(value) {
		var me = this;
		me.parameterJson = value;
	},
	getParameterJson : function() {
		var me = this;
		return me.parameterJson;
	}
});