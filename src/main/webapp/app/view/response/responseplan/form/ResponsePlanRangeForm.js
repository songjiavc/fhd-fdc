/**
 * 应对计划编辑第二步：选择范围
 */
Ext.define('FHD.view.response.responseplan.form.ResponsePlanRangeForm',{
	extend:'Ext.form.Panel',
	alias: 'widget.responseplanrangeform',
	requires: [
       'FHD.view.response.responseplan.form.ResponsePlanPreviewForm',
       'FHD.view.response.responseplan.ResponsePlanRelaStandardEditGrid'
    ],
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	autoScroll:true,
	bodyPadding:'0 3 3 3',
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
		me.responsePlanGrid=Ext.widget('responseplanrelastandardeditgrid',{
			columnWidth:1/1,
			border:false
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
		me.items=[me.basicInfo,me.childItems];
		me.callParent(arguments);
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
		//基本信息和详细信息刷新
		me.basicInfo.initParam(me.paramObj);
		me.basicInfo.reloadData();
		//选择范围刷新
		me.operateColumn();
		/*
		if(editflag){
			//基本信息和详细信息刷新
			me.basicInfo.loadData(businessId);
			//选择范围刷新
			me.constructPlanGrid.extraParams.businessId = businessId;
			me.constructPlanGrid.businessId = businessId;
			
			me.constructPlanGrid.store.proxy.extraParams.assessPlanId = businessId;
        	me.constructPlanGrid.store.load();
		}else{
			//基本信息和详细信息刷新
			me.basicInfo.loadData(businessId);
			//选择范围刷新
			me.constructPlanGrid.extraParams.businessId = businessId;
			me.constructPlanGrid.businessId = businessId;
			
			me.constructPlanGrid.store.proxy.extraParams.assessPlanId = businessId;
        	me.constructPlanGrid.store.load();
		}
		*/
	}
});