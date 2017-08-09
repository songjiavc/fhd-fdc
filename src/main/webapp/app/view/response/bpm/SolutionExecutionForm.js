/**
 * 方案执行上报表单
 * */
Ext.define('FHD.view.response.bpm.SolutionExecutionForm', {
	extend : 'Ext.form.Panel',
	alias: 'widget.solutionexecutionform',
	requires: [
    ],
    layout : {
    	type : 'vbox',
    	align : 'stretch'
    },
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
//	bodyPadding: '0 3 3 3',
    autoWidth:true,
	collapsed : false,
	autoScroll:true,
	border: false,
	initComponent :function() {
		var me = this;
		me.solutionformforview = Ext.create('FHD.view.response.new.SolutionFormForView'/*,{ margin : '0 0 0 0'}*/);
		me.executionHistory = Ext.create('FHD.view.response.ExecutionHistoryList');
		// 填写执行情况   
		var solutionExecuteId = Ext.widget('textfield',{
			name : 'solutionExecutionId',
			hidden : true
		});
		// 填写执行情况   
		var realCost = Ext.widget('textfield',{
			fieldLabel: '实际成本',
			regex: /^\d+$/,
			regexText: "只能为数字",
			name : 'realCost'
		});
		var realIncome = Ext.widget('textfield',{
			fieldLabel: '实际收效',
			regex: /^\d+$/,
			regexText: "只能为数字",
			name : 'realIncome'
		});
		var realStartTime = Ext.widget('datefield',{
			fieldLabel : '实际开始时间<font color=red>*</font>',
			name : 'realStartTime',
			allowBlank : false,
			format: 'Y-m-d'
		});
		var realFinishTime = Ext.widget('datefield',{
			fieldLabel : '实际结束时间<font color=red>*</font>',
			name : 'realFinishTime',
			allowBlank : false,
			format: 'Y-m-d'
		});
		me.progress = Ext.widget('numberfield',{
			fieldLabel : '进度&nbsp;(%)<font color=red>*</font>',
			allowBlank : false,
			regex : /^(?=[0-9]{1,2}$)|(?=100$)/,
			regexText: "只能是0-100之间的数",
			name : 'progress'
		});
		var desc = Ext.widget('textareafield',{
			fieldLabel : '执行情况<font color=red>*</font>',
			allowBlank : false,
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
			title : '执行情况 ',
			items : [solutionExecuteId,realStartTime,realFinishTime,realCost,realIncome,me.progress,desc]
		});
		Ext.applyIf(me,{
			items:[
				me.solutionformforview,
				{
					xtype : 'fieldset',
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
	reloadData:function(){
		var me=this;
		me.solutionformforview.initParam({
			solutionId : me.paramObj.solutionId,
			riskId : me.paramObj.riskId
		});
		me.solutionformforview.reloadData();
		//undo  调用list的reload方法 
		me.executionHistory.initParam({
			solutionExecutionId : me.paramObj.solutionExecuteId
		});
		me.executionHistory.reloadData();
		//undo  调用自己的reload方法
	},
	save: function() {
		var me = this;
		var form = me.getForm();
		form.setValues({
			"solutionExecutionId" : me.paramObj.solutionExecuteId
		});
		if(form.isValid()){
			if(me.expandIsValid()){
				Ext.Msg.alert('提示','已经存在完成比例为100%的记录,不能再次保存.');
			}else{
				FHD.submit({
					form : me.getForm(),
					url : __ctxPath + '/response/responsesolutionexecutecontrol/saveExecutionHistory.f',
					callback: function (data) {
						if(!data.success){
							Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),data.info);
							flag = false;
						}else{
							me.executionHistory.reloadData();
						}
					}
				});
			}
			return true;
		}else{
			return false;
		}
	},
	expandIsValid : function(){
		var me = this;
		var flag = false;
		var historyDatas = me.executionHistory.store.data;
		Ext.each(historyDatas.items,function(item){
			if(item.get('progress') == '100%'){
				flag = true;
			}
		});
		return flag;
	}
});

