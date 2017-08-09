/**
 * 应对方案展示表单
 */
Ext.define('FHD.view.risk.demo.popupnew.NewDataViewForm', {
	extend : 'Ext.form.Panel',
	alias: 'widget.newdataviewform',
	layout : {
		type : 'column'
	},
	defaults : {
		columnWidth : 1/1
	},
	type:'',
	name:'',
	code:'',
	manageOrg:'',
	bodyPadding: '0 3 3 3',
	border: false,
	initComponent :function() {
		var me = this;
		me.defect = Ext.widget('fieldset',{
			id:'defect',
			defaults:{
			},
			layout : {
				type : 'column'
			},
			collapsed : false,
			collapsible : true,
			title : '缺陷信息',
			items:[
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '缺陷名称', value:me.name},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '缺陷编号',value:me.code},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '责任部门',value:me.manageOrg},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '设计缺陷',value:'无制度'},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '缺陷登记',value:'重要缺陷'},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '缺陷类型',value:'设计缺陷'},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '执行缺陷',value:'无依据'}						
			]
		});
		me.risk = Ext.widget('fieldset',{
			id:'risk',
			defaults:{
			},
			layout : {
				type : 'column'
			},
			collapsed : false,
			collapsible : true,
			title : '风险信息',
			items:[
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险名称', value:me.name},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险编号',value:me.code},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '风险分类', value:'供应商管理风险'},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '责任部门',value:me.manageOrg},
				{xtype: 'displayfield', columnWidth : 0.5, margin : '7 10 0 30', fieldLabel: '协助部门',value:'财务部'}						
			]
		});
		if('风险' == me.type){
			me.defect.hide();
		}else if('缺陷' == me.type){
			me.risk.hide();
		}
		
		Ext.applyIf(me,{
			items:[me.defect, me.risk]
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

