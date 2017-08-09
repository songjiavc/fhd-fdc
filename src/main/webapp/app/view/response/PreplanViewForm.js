/**
 * 应对方案展示表单
 */
Ext.define('FHD.view.response.PreplanViewForm', {
	extend : 'Ext.form.Panel',
	alias: 'widget.preplanviewform',
	layout : {
		type : 'column'
	},
	defaults : {
		columnWidth : 1/1
	},
	type:'',
	bodyPadding: '0 3 3 3',
	border: false,
	initComponent :function() {
		var me = this;
		me.planName = {xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '预案名称', value:'2012第四季度营销指标类风险应对计划'};
		me.planCode = {xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '预案编号',value:'YD20130825001'};
		me.risk = Ext.widget('displayfield', {columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '对应风险',value:'供应商订货起点与生产需求相差较大'});
		me.target = Ext.widget('displayfield', {columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '对应指标',value:'供货计划完成率'});
		me.respOrg = {xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '主责部门',value:'管理创新部'};
		me.relOrg = {xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '协助部门',value:'财务部'};
		me.personInChrg = {xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '责任人',value:'邢军'};
		me.planDesc = {xtype: 'displayfield', columnWidth : 1, margin : '7 10 0 30', fieldLabel: '预案描述',value:'预案描述'};		
		
		Ext.applyIf(me,{
			items:[{
			xtype:'fieldset',
			defaults:{
			},
			layout : {
				type : 'column'
			},
			collapsed : false,
			collapsible : true,
			title : '预案信息',
			items:[
				 me.risk, me.target,me.planName, me.planCode, me.respOrg, me.relOrg, me.personInChrg, me.planDesc
				]
			}]
		});
		
		if('risk' == me.type){
			me.target.hide();
		}else if('target' == me.type){
			me.risk.hide();
		}
		
		me.callParent(arguments);
	},
	loadData: function(improveId,executionId){
		var me = this;
	},
	reloadData:function(){
		var me=this;
	}
});

