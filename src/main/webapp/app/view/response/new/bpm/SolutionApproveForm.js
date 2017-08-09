/**
 * 审批
 */
Ext.define('FHD.view.response.new.bpm.SolutionApproveForm',{
	extend:'Ext.form.Panel',
	alias: 'widget.solutionapproveform',
	requires: [
    ],
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	autoScroll:true,
//	bodyPadding:'0 3 3 3',
	layout : {
		type : 'column'
	},
	defaults:{
		columnWidth:1/1
	},
	border:false,
	initComponent : function() {
		var me=this;
        //风险
        var risk={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '对应风险',
		    value:'供应商订货起点与生产需求相差较大',
		    columnWidth: .5
		};
        //风险分类
        var riskClass={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '风险分类',
		    value:'供应商管理风险',
		    columnWidth: .5
		};
        //风险code
        var riskCode={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '风险编号',
		    value:'201308098',
		    columnWidth: .5
		};
		//责任部门
        var orgId={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '责任部门',
		    value:'管理创新部',
		    columnWidth: .5
		};
		//协助部门
        var orgOra={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '协助部门',
		    value:'管理创新部',
		    columnWidth: .5
		};
		
        me.solutionviewform = Ext.create('FHD.view.response.new.SolutionViewForm');
		
		//是否通过Radio
		me.isPassRadioDemo = Ext.create('FHD.ux.dict.DictRadio', {
		    margin: '7 10 0 10',
			labelWidth:80,
			labelAlign:'left',
			fieldLabel:'是否通过 ',
			hideLabel:true,
			dictTypeId:'0yn',
			columnWidth:.5,
			defaultValue :'0yn_y',
			name : 'isPassRadio',
			id : 'isPassRadio'
		});
		
		//审批意见Demo
        me.adviceDemo = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '审批意见' + '<font color=red>*</font>',
            margin: '7 10 0 10',
            name: 'indicator',
            columnWidth: 1
        });
		
		//审批意见fieldset
        me.approveAdvice = Ext.widget('fieldset',{
			defaults : {
				columnWidth : 1/1
			},//每行显示一列，可设置多列
			layout : {
				type : 'column'
			},
			name : 'advicefieldset',
			collapsed : false,
//			margin: '8 10 0 10',
			collapsible : true,
			title : '审批',
			items : [me.isPassRadioDemo, me.adviceDemo]
        });
        
		me.items=[me.solutionviewform, me.approveAdvice];
		
		me.callParent(arguments);
		
	},
	showMoreInfo : function(){ 
		var solutionviewform = Ext.create('FHD.view.response.new.SolutionViewForm');
		var win = Ext.create('FHD.ux.Window',{
			title:'应对方案详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(solutionviewform);
	},
	reloadData:function(){
		var me=this;
	},
	loadData:function(businessId, executionId){
		var me=this;
		me.responsePlanGrid.reloadData();
	}
});