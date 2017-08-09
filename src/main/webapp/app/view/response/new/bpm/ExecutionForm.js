/**
 * 方案执行上报表单
 * */
Ext.define('FHD.view.response.new.bpm.ExecutionForm', {
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
		me.solutionviewform = Ext.create('FHD.view.response.new.SolutionViewForm');
		me.executionHistory = Ext.widget('executionhistorylist',{flex:1});
		// 填写执行情况   
		var realCost = Ext.widget('textfield',{
			fieldLabel: '实际成本' + '<font color=red>*</font>',
			name : 'realCost'
		});
		var realIncome = Ext.widget('textfield',{
			fieldLabel: '实际收效' + '<font color=red>*</font>',
			name : 'realIncome'
		});
		var realStartTime = Ext.widget('datefield',{
			fieldLabel : '实际开始时间<font color=red>*</font>',
			name : 'realStartTime'
		});
		var realFinishTime = Ext.widget('datefield',{
			fieldLabel : '实际结束时间<font color=red>*</font>',
			name : 'realFinishTime'
		});
		var progress = Ext.widget('numberfield',{
			fieldLabel : '进度&nbsp;(%)<font color=red>*</font>',
			name : 'progress'
		});
		var desc = Ext.widget('textareafield',{
			fieldLabel : '执行情况<font color=red>*</font>',
			name : 'desc'
		});
		var execFieldSet = Ext.widget('fieldset',{
			layout : 'column',
			defaults : {
				margin : '7 10 0 30',
				columnWidth : .5
//				allowBlank : false
			},
			collapsed : false,
			collapsible : false,
			title : '执行情况 <font color=red>*</font>',
			items : [realCost,realIncome,realStartTime,realFinishTime,progress,desc]
		});
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
				execFieldSet
				]
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

