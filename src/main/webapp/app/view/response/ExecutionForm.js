/**
 * 方案执行上报表单
 * */
Ext.define('FHD.view.response.ExecutionForm', {
	extend : 'Ext.form.Panel',
	alias: 'widget.executionform',
	requires: [
       'FHD.view.response.SolutionViewForm',
       'FHD.view.response.ExecutionHistoryList'
    ],
	layout : {
		type : 'column'
	},
	defaults : {
		columnWidth : 1/1
	},
//	bodyPadding: '0 3 3 3',
    autoWidth:true,
	collapsed : false,
	autoScroll:true,
	border: false,
	initComponent :function() {
		var me = this;
		me.solutionviewform = Ext.widget('solutionviewform');
		me.executionHistory = Ext.widget('executionhistorylist',{flex:1});
		Ext.applyIf(me,{
			items:[
				me.solutionviewform,
				{
					xtype : 'fieldset',
					margin : '7 30 0 13',
					collapsed : false,
					collapsible : true,
					title : '历史记录',
					items:[me.executionHistory]
				},
				{
					xtype : 'fieldset',
					defaults : {
						margin : '7 10 0 30'
					},//每行显示一列，可设置多列
					layout : {
						type : 'column'
					},
					margin : '7 30 0 13',
					collapsed : false,
					collapsible : false,
					title : '执行情况 <font color=red>*</font>',
					items:[
						{xtype: 'numberfield', fieldLabel: '进度&nbsp;(%)<font color=red>*</font>', columnWidth : 0.2, allowBlank:false, name : '', maxValue: 100, minValue: 0},
						{xtype: 'textareafield', fieldLabel: '执行情况<font color=red>*</font>', allowBlank:false, name : '',columnWidth : 1}
					]
			}]
		});
		me.callParent(arguments);
	},
	executionSave:function () {
		var me=this;
		
	},
	loadData: function(){
		var me = this;
		me.reloadData();
	},
	reloadData:function(){
		var me=this;
	}
});

