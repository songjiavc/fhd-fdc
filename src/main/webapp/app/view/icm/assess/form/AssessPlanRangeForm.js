/**
 * 评价计划编辑第二步：选择范围
 */
Ext.define('FHD.view.icm.assess.form.AssessPlanRangeForm',{
	extend:'Ext.panel.Panel',
	alias: 'widget.assessplanrangeform',
	
	autoScroll:true,
	bodyPadding:'0 3 3 3',
	border:false,
	layout : {
		type : 'column'
	},
	defaults:{
		columnWidth:1/1
	},
	
	initComponent : function() {
		var me=this;
		
		//评价计划预览表单
		me.basicInfo=Ext.create('FHD.view.icm.assess.form.AssessPlanPreviewForm',{
			columnWidth:1/1,
			isShowGroupPers:false,
			businessId:me.businessId
		});
		//评价计划可编辑列表
		me.assessPlanGrid=Ext.create('FHD.view.icm.assess.component.AssessPlanRelaProcessEditGrid',{
			//height : FHD.getCenterPanelHeight()/2,
			columnWidth:1/1,
			businessId:me.businessId
		});
		//fieldSet
		me.childItems={
			xtype : 'fieldset',
			margin:'10 0 10 0',
			layout : {
				type : 'column'
			},
			collapsed: false,
			columnWidth:1/1,
			collapsible : true,
			title : '评价范围',
			items : [me.assessPlanGrid]
		};
		me.items=[me.basicInfo,me.childItems];
		me.callParent(arguments);
	},
	/*
	listeners:{
		afterrender:function(me){
			me.loadData(me.businessId,true);
		}
	},
	*/
	operateColumn:function(){
		var me=this;
		
		if(me.businessId){
			FHD.ajax({
			     url : __ctxPath+ '/icm/assess/findAssessPlanMeasureByAssessPlanId.f',
			     params : {
			    	 assessPlanId:me.businessId
				 },
				 callback : function(data) {
					 if (data && data.success) {
						me.assessPlanMeasureId = data.assessMeasureId;
						if('ca_assessment_measure_0'== me.assessPlanMeasureId){
							//穿行测试--隐藏抽样测试列
							me.assessPlanGrid.down('[dataIndex=isPracticeTest]').hide();
							me.assessPlanGrid.down('[dataIndex=isPracticeTestShow]').show();
							me.assessPlanGrid.down('[dataIndex=practiceNum]').show();
							me.assessPlanGrid.down('[dataIndex=isSampleTest]').hide();
							me.assessPlanGrid.down('[dataIndex=isSampleTestShow]').hide();
							me.assessPlanGrid.down('[dataIndex=coverageRate]').hide();
						}else if('ca_assessment_measure_1' == me.assessPlanMeasureId){
							//抽样测试--隐藏穿行测试列
							me.assessPlanGrid.down('[dataIndex=isPracticeTest]').hide();
							me.assessPlanGrid.down('[dataIndex=isPracticeTestShow]').hide();
							me.assessPlanGrid.down('[dataIndex=practiceNum]').hide();
							me.assessPlanGrid.down('[dataIndex=isSampleTest]').hide();
							me.assessPlanGrid.down('[dataIndex=isSampleTestShow]').show();
							me.assessPlanGrid.down('[dataIndex=coverageRate]').show();
						}else if('ca_assessment_measure_2' == me.assessPlanMeasureId){
							//全部：穿行测试和抽样测试
							me.assessPlanGrid.down('[dataIndex=isPracticeTest]').show();
							me.assessPlanGrid.down('[dataIndex=isPracticeTestShow]').hide();
							me.assessPlanGrid.down('[dataIndex=practiceNum]').show();
							me.assessPlanGrid.down('[dataIndex=isSampleTest]').show();
							me.assessPlanGrid.down('[dataIndex=isSampleTestShow]').hide();
							me.assessPlanGrid.down('[dataIndex=coverageRate]').show();
						}
						me.assessPlanGrid.store.proxy.extraParams.assessPlanId = me.businessId;
				    	me.assessPlanGrid.store.load();
					 }
				 }
			});
		}
	},
	loadData:function(businessId,editflag){
		var me=this;
		me.businessId=businessId;
		me.editflag=editflag;
		
		//基本信息和详细信息刷新
		me.basicInfo.getForm().load({
            url: __ctxPath+'/icm/assess/findAssessPlanForView.f',
            params : {
            	assessPlanId:me.businessId
			},
            success: function (form, action) {
            	me.assessPlanTypeId = action.result.data.typeId;
				//评价类型
				if('ca_assessment_etype_self' != me.assessPlanTypeId){
					//组成员显示
					me.down('[name=handlerEmp]').show();
				}else{
					if(!me.isShowGroupPers){
						me.down('[name=handlerEmp]').hide();
					}
				}
				me.assessPlanMeasureId = action.result.data.assessMeasureId;
				me.operateColumn();
         	   	return true;
            },
            failure: function (form, action) {
         	   	return false;
            }
		});
		
		//选择范围刷新
		me.assessPlanGrid.extraParams.businessId = businessId;
		me.assessPlanGrid.businessId = businessId;
		
		me.assessPlanGrid.store.proxy.extraParams.assessPlanId = businessId;
    	me.assessPlanGrid.store.load();
	},
	reloadData:function(){
		var me=this;
		
	}
});