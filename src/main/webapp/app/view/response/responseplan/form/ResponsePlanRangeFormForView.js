/**
 * 评价计划编辑第二步：选择范围
 */
Ext.define('FHD.view.response.responseplan.form.ResponsePlanRangeFormForView',{
	extend:'Ext.form.Panel',
	alias: 'widget.responseplanrangeformforview',
	requires: [
       'FHD.view.response.responseplan.form.ResponsePlanPreviewForm',
       'FHD.view.response.responseplan.ResponsePlanRelaStandardViewGrid'
    ],
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	autoScroll:true,
	bodyPadding:'0 3 3 3',
	border: false,
	layout : {
		type : 'column'
	},
	defaults:{
		columnWidth:1/1
	},
	
	initComponent : function() {
		var me=this;
		//评价计划预览表单
		me.basicInfo=Ext.widget('responseplanpreviewform',{
			columnWidth:1/1,
			businessId:me.businessId
		});
		//评价计划可编辑列表
		me.responsePlanGrid=Ext.widget('responseplanrelastandardviewgrid',{
			columnWidth:1/1,
			searchable : false,
			columnLines : true
		});
		//审批意见
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			executionId:me.executionId,
			flex : 1
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
			collapsible : true,
			title : '建设范围',
			items : [me.responsePlanGrid]
		};
		me.items=[me.basicInfo,me.childItems,me.ideaApproval];
		me.callParent(arguments);
	},
	reloadData:function(){
		
	}
});