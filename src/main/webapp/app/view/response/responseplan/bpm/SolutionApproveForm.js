/**
 * 审批
 */
Ext.define('FHD.view.response.responseplan.bpm.SolutionApproveForm',{
	extend:'Ext.form.Panel',
	alias: 'widget.solutionapproveform',
	requires: [
       'FHD.view.response.responseplan.form.ResponsePlanPreviewForm',
       'FHD.view.response.responseplan.SolutionPlanEditGrid'
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
		//应对计划预览表单
		me.basicInfo=Ext.widget('responseplanpreviewform',{
			columnWidth:1/1,
			businessId:me.businessId,
			border:false
		});
		//应对计划可编辑列表
		me.responsePlanGrid=Ext.widget('solutionplaneditgrid',{
			columnWidth:1/1,
			border:false,
			columnLines: true,
			margin: '7 10 10 10',
			readOnly: true
		});
		
		//fieldSet
		me.childItems={
			xtype : 'fieldset',
			//margin: '7 10 0 30',
			layout : {
				type : 'column'
			},
			collapsed: false,
			columnWidth:1/1,
			collapsible : false,
			title : '计划范围',
			items : [me.responsePlanGrid]
		};
		
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
        
		me.items=[me.basicInfo,me.childItems, me.approveAdvice];
		
		me.callParent(arguments);
		
//		if(false){
//			me.approvalidea = Ext.widget('approvalidea',{executionId:null,bodyPadding: '0 3 3 3'}); 
//			me.standardAdvice.add(me.approvalidea);
//			me.add(me.standardAdvice);
//   	    }
	},
	operateColumn : function(){
		var me = this;
		if(me.up('constructplancardpanel').constructplanform.items.items[0].items.items[6].lastValue == 'diagnoses'){
			me.constructPlanGrid.down('[dataIndex=isProcessEdit]').hide();
			me.constructPlanGrid.down('[dataIndex=isNormallyDiagnosis]').show();
		}else if(me.up('constructplancardpanel').constructplanform.items.items[0].items.items[6].lastValue == 'process'){
			me.constructPlanGrid.down('[dataIndex=isNormallyDiagnosis]').hide();
			me.constructPlanGrid.down('[dataIndex=isProcessEdit]').show();
		}else{
			me.constructPlanGrid.down('[dataIndex=isNormallyDiagnosis]').show();
			me.constructPlanGrid.down('[dataIndex=isProcessEdit]').show();
		}
		me.constructPlanGrid.extraParams=me.paramObj;
    	me.constructPlanGrid.reloadData();
	},
	reloadData:function(){
		var me=this;
	},
	loadData:function(businessId, executionId){
		var me=this;
		me.responsePlanGrid.reloadData();
	}
});