/**
 * 应对方案展示表单
 */
Ext.define('FHD.view.response.SolutionViewForm', {
	extend : 'Ext.form.Panel',
	alias: 'widget.solutionviewform',
	layout : {
		type : 'column'
	},
	defaults : {
		columnWidth : 1/1
	},
	bodyPadding: '0 3 3 3',
	border: false,
	initComponent :function() {
		var me = this;
		Ext.applyIf(me,{
			items:[{
//					xtype : 'fieldset',
//					defaults:{
//					},
//					layout : {
//						type : 'column'
//					},
//					collapsed : false,
//					collapsible : true,
//					title : '计划信息',
//					items:[
////						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '计划编号', value:'20130820001'},
//						{xtype: 'displayfield', columnWidth : 1, margin : '7 10 0 30', fieldLabel: '计划名称', value:'制定不同物资的采购流程'}					
//					]
//				},{
					xtype : 'fieldset',
					defaults:{
					},
					layout : {
						type : 'column'
					},
					collapsed : false,
					collapsible : true,
					title : '风险信息',
					items:[
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险名称', value:'生产计划编排不完整或不合理，影响生产效率'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险分类', value:'生产管理风险'},
//						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险编号',value:'201308098'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '责任部门',value:'生产管理部'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '协助部门',value:'内控部'}						
					]
				},{
					xtype : 'fieldset',
					defaults : {
						columnWidth : 1
					},//每行显示一列，可设置多列
					layout : {
						type : 'column'
					},
					collapsed : false,
					collapsible : true,
					title : '应对方案',
					items:[
						{xtype: 'displayfield', columnWidth : 1, margin : '7 10 0 30', fieldLabel: '方案名称', value:'严格按照企业内控制定有效生产计划'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '方案编号', value:'2013080098'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '责任部门',value:'管理创新部'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '协助部门',value:'财务部'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '负责人', value:'邢军'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel:'预计成本', value:'10万元'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel:'预计收效', value:'100万元'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel:'预计开始日期', value:'2013-08-09'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel:'预计结束日期', value:'2013-08-19'},
						{xtype: 'displayfield', columnWidth : 1, margin : '7 10 0 30', fieldLabel: '方案描述',value:'物料需求变差率'},
						{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '完成标志',value:'《生产计划》'}
					]
			}]
		});
		me.callParent(arguments);
	},
	loadData: function(improveId,executionId){
		var me = this;
	},
	reloadData:function(){
		var me=this;
	}
});

