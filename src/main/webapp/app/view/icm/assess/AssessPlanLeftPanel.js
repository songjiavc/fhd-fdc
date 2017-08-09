Ext.define('FHD.view.icm.assess.AssessPlanLeftPanel', {
    extend: 'FHD.ux.MenuPanel',
    alias: 'widget.assessplanleftpanel',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
		
        me.assessPlanDashboard = {
	        text: '驾驶舱',
	        iconCls:'icon-btn-home',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){
				FHD.ajax({
					url : __ctxPath+ '/icm/assess/findAssessPlanChartXmlByCompanyId.f',
					async:false,
				    params : {
				    	companyId: __user.companyId
					},
					callback : function(data) {
						if(data){
							var assessplancenterpanel = me.up('panel').assessplancenterpanel;
							if(FusionCharts("assessplan_finish_rate-chart") != undefined){
					 		   	FusionCharts("assessplan_finish_rate-chart").dispose();
					     	}
					    	
					    	if(FusionCharts("assessplan_systemdefectrate_rate-chart") != undefined){
					  		   	FusionCharts("assessplan_systemdefectrate_rate-chart").dispose();
					      	}
					    	
					    	if(FusionCharts("assessplan_performerror_rate-chart") != undefined){
					  		   	FusionCharts("assessplan_performerror_rate-chart").dispose();
					      	}
							assessplancenterpanel.removeAll(true);
							assessplancenterpanel.add(Ext.create('FHD.view.icm.assess.AssessPlanDashboard',{
								finishRateXml:data.finishRateXml,
								finishRate:data.finishRate,
								systemDefectRateXml:data.systemDefectRateXml,
								systemDefectRate:data.systemDefectRate,
								performErrorRateXml:data.performErrorRateXml,
								performErrorRate:data.performErrorRate,
								defectLevelXml:data.defectLevelXml,
								orgDefectXml:data.orgDefectXml
							}));
						}
					}
				});
			}
	    };
        
        me.assessPlanMainPanel = {
	        text: '评价计划',
	        iconCls:'icon-btn-assessPlan',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var assessplancenterpanel = me.up('panel').assessplancenterpanel;
				assessplancenterpanel.removeAll(true);
				assessplancenterpanel.add(Ext.create('FHD.view.icm.assess.AssessPlanMainPanel'));
			}
	    };
        
        me.testReportList = {
	        text: '测试报告',
	        iconCls:'icon-btn-testReport',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var assessplancenterpanel = me.up('panel').assessplancenterpanel;
				assessplancenterpanel.removeAll(true);
				var testreportlist = Ext.create('FHD.view.comm.report.assess.TestReportList');
				assessplancenterpanel.add(testreportlist);
			}
	    };
        
        me.companyYearReportList = {
	        text: '公司年度评价报告',
	        iconCls:'icon-btn-testReport',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var assessplancenterpanel = me.up('panel').assessplancenterpanel;
				assessplancenterpanel.removeAll(true);
				var companyyearreportlist = Ext.create('FHD.view.comm.report.assess.CompanyYearReportList');
				assessplancenterpanel.add(companyyearreportlist);
			}
	    };
        
        me.groupYearReportList = {
	        text: '集团年度评价报告',
	        iconCls:'icon-btn-testReport',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var assessplancenterpanel = me.up('panel').assessplancenterpanel;
				assessplancenterpanel.removeAll(true);
				var groupyearreportlist = Ext.create('FHD.view.comm.report.assess.GroupYearReportList');
				assessplancenterpanel.add(groupyearreportlist);
			}
	    };
        
        me.assessGuidelinesMainPanel = {
	        text: '基础设置',
	        iconCls:'icon-btn-set',
	        scale: 'large',
			iconAlign: 'top',
			handler:function(){	
				var assessplancenterpanel = me.up('panel').assessplancenterpanel;
				assessplancenterpanel.removeAll(true);
				assessplancenterpanel.add(Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesMainPanel'));
			}
	    };
	    
        Ext.applyIf(me, {
        	autoScroll:true
        });

        me.callParent(arguments);
        
        //驾驶舱
        if($ifAllGranted('ROLE_ALL_REVIEW_ICASSESS_DASHBOARD')){
        	me.add(me.assessPlanDashboard);
        }
        //内控评价
        if($ifAllGranted('ROLE_ALL_REVIEW_ICASSESS_ASSESSPLAN')){
        	me.add(me.assessPlanMainPanel);
        }
        //测试报告
        if($ifAllGranted('ROLE_ALL_REVIEW_ICASSESS_REPORT')){
        	me.add(me.testReportList);
        }
        //公司报告
        if($ifAllGranted('ROLE_ALL_REVIEW_ICASSESS_COMPANYREPORT')){
        	me.add(me.companyYearReportList);
        }
        //集团报告
        if($ifAllGranted('ROLE_ALL_REVIEW_ICASSESS_GROUPREPORT')){
        	me.add(me.groupYearReportList);
        }
        //基础设置
        if($ifAllGranted('ROLE_ALL_REVIEW_ICASSESS_CONFIGURATION')){
        	me.add(me.assessGuidelinesMainPanel);
        }
    }
});