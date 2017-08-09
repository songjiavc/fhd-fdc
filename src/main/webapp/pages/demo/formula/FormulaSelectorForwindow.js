Ext.define('FHD.demo.formula.FormulaSelectorForwindow', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.FormulaSelectorForwindow',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		var button = Ext.create('Ext.Button', {
					text : '公式弹窗',
					renderTo : Ext.getBody(),
					handler : function() {
						var formulaforwindowselector = Ext.create(
								'FHD.ux.kpi.FormulaWindow', {
									type : 'category',
									column : 'assessmentValueFormula',
									showType : 'categoryType',
									targetId : 'eda8ffeab0da4159be0ff924108e3883JFK13',
									targetName : '产品研发',
									formula : '1+2',
									onSubmit : function(ret) {

									}
								}).show();
					}
				});

		// 表单panel
		var form = Ext.create("Ext.form.Panel", {
					autoScroll : true,
					border : false,
					bodyPadding : "5 5 5 5",
					items : [{
								xtype : 'fieldset',// 基本信息fieldset
								collapsible : false,
								defaults : {
									margin : '7 30 3 30',
									columnWidth : .2
								},
								layout : {
									type : 'column'
								},
								title : '公式弹窗选择',
								items : [button]
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