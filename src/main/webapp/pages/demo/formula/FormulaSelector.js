Ext.define('FHD.demo.formula.FormulaSelector', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.FormulaSelector',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;

				var formulaselector = Ext.create('FHD.ux.kpi.FormulaSelector',
						{
							type : 'category',// strategy
							column : 'assessmentValueFormula',
							showType : 'categoryType',// strategyType
							targetId : 'eda8ffeab0da4159be0ff924108e3883JFK13',
							targetName : '产品研发',
							formulaTypeName : 'radioName',
							formulaName : 'formulaName',
							fieldLabel : '公式定义'
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
											columnWidth : .5
										},
										layout : {
											type : 'column'
										},
										title : '公式选择',
										items : [formulaselector]
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