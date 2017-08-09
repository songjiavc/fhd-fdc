Ext.define('FHD.view.icm.assess.form.AssessResultAnalysisForm',{
	extend : 'Ext.form.Panel',
	alias: 'widget.assessresultanalysisform',
	
	autoScroll:false,
	border:false,
	
	initComponent : function() {
		var me = this;
		
		//结果分析
		me.assessResultAnalysis = Ext.widget('textareafield',{
			name: 'sampleAnalysisOfResults',
			hideLabel : true,
			fieldLabel: '结果分析',
			labelWidth : 80,
			margin: '7 10 0 30',
			readOnly:true,
			row : 5
		});

		Ext.apply(me, {
			layout: {
				type : 'column'
			},
			defaults:{
				columnWidth:1
			},
			items:[me.assessResultAnalysis]
		});
		
		me.callParent(arguments);
		
		me.loadData(me.businessId);
	},
	loadData:function(businessId){
		var me=this;
		
		me.getForm().load({
	        url:__ctxPath + '/icm/assess/findResultAnalysisByAssessplanIdAndExecutionId.f',
	        params:{
	        	businessId: me.businessId,
	        	executionId: me.executionId
	        },
	        success: function (form, action) {
	     	   return true;
	        },
	        failure: function (form, action) {
	     	   return false;
	        }
		});
	},
	reloadData:function(){
		var me=this;
		
	}
 });