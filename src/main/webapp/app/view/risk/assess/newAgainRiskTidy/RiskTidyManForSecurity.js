/**
 * 
 * 风险整理主体内容面板
 */

Ext.define('FHD.view.risk.assess.newAgainRiskTidy.RiskTidyManForSecurity', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.risktidymanforsecurity',
    requires: [
		'FHD.view.risk.assess.newAgainRiskTidy.RiskTidyTabForSecurity',
		'FHD.view.risk.assess.newAgainRiskTidy.AssessTreeForSecurity',
		'FHD.view.risk.assess.utils.GridCells'
	],
    reloadData : function(){
    },       
    setAssessTitle : function(){
    	var me = this;
    	if(me.executionId == undefined){
			FHD.ajax({
	            url: __ctxPath + '/assess/quaassess/findAssessName.f',
	            params: {
	            	assessPlanId : me.businessId
	            },
	            callback: function (data) {
	                if (data && data.success) {
	                	var assessPlanName = data.assessPlanName;
	                	me.riskTidyTopPanel.setTitle('计划名称:' + assessPlanName);
	                } 
	            }
	        });
		}else{
			FHD.ajax({
	            url: __ctxPath + '/assess/quaassess/findAssessName.f',
	            params: {
	            	assessPlanId : me.businessId
	            },
	            callback: function (data) {
	                if (data && data.success) {
	                	var assessPlanName = data.assessPlanName;
	                	me.riskTidyTopPanel.setTitle('计划名称:' + assessPlanName);
	                } 
	            }
	        });
		}
    },
    
    // 初始化方法
    initComponent: function() {
    	Ext.Ajax.timeout = 1000000;
        var me = this;
        
        if(me.result != null){
        	me.nav = false;
    	}else{
    		me.nav = true;
    	}
        
        Ext.apply(me, {
    		layout: {
    			type: 'border',
    			padding: .5
    	    },
        	border:false
        });

//        me.callParent(arguments);
        
        if(!me.nav){
        	//没有导航,评估结果信息管理
        	FHD.ajax({
                url: __ctxPath + '/assess/riskTidy/riskRbsOrRe.f',
                params: {
                	assessPlanId : me.businessId
	            },
                callback: function (data) {
                    if (data && data.success) {
                    	var riskRbsOrReListMap = {ids:data.riskRbsOrReListMap};
                    	var ids = {ids:data.ids};
                    	me.riskTidyTab = Ext.widget('risktidytabforsecurity',{riskTidyMan : me});
                    	me.assessTree = Ext.widget("assesstreeforsecurity",{extraRiskParams : riskRbsOrReListMap, region:'west',schm : me.schm,
                    		extraParams : ids, businessId : me.businessId, riskTidyMan : me,
                    		assessPlanId : me.businessId, collapseds : false, nav : false});
                    	
                    	me.riskTidyTopPanel =  Ext.widget('panel',{
            	        	hidden:true
            	        });
                    	
                    	me.add(me.riskTidyTopPanel);
                    	me.add(me.assessTree);
                    	me.add(me.riskTidyTab);
                    	
                    	if(me.winId){
                    		Ext.getCmp(me.winId).body.unmask();
                    	}else{
                    		me.body.unmask();
                    	}
                    	
                    	me.setAssessTitle();
                    	
                    }
                }
            });
        }else{
        	FHD.ajax({
                url: __ctxPath + '/assess/riskTidy/isRiskTidySummarizing.f',
                params: {
                	assessPlanId : me.businessId
	            },
                callback: function (data) {
                    if (data.success) {
                    	if(me.winId){
                    		//Ext.getCmp(me.winId).body.mask("数据汇总中,请耐心等待...","x-mask-loading");
                    	}else{
                    		me.body.mask("数据汇总中,请耐心等待...","x-mask-loading");
                    	}
                    	
                    	FHD.ajax({
                            url: __ctxPath + '/assess/riskTidy/riskTidySummarizing.f',
                            params: {
                            	assessPlanId : me.businessId,
                            	executionId : me.executionId,
                            	type : 'jbpm'
    			            },
                            callback: function (data) {
                                if (data.success) {
		                    		me.riskTidyTopPanel = Ext.widget('panel',{
		                    			border : false,
		                	        	collapsible : true,
		                	        	collapsed : true,
		                	        	region : 'north',
		                    			collapsed:true,
		                    			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
		                    	    		jsonArray:[
		                    		    		{index: 1, context:'1.计划制定',status:'done'},
		                    		    		{index: 2, context:'2.计划主管审批',status:'done'},
		                    		    		{index: 3, context:'3.计划领导审批',status:'done'},
		                    		    		{index: 4, context:'4.任务分配',status:'done'},
		                    		    		{index: 5, context:'5.风险评估',status:'done'},
		                    		    		{index: 6, context:'6.任务审批',status:'done'},
		                    		    		{index: 7, context:'7.单位主管审批',status:'done'},
		                    		    		{index: 8, context:'8.单位领导审批',status:'done'},
		                    		    		{index: 9, context:'9.业务分管副总审批',status:'done'},
		                    		    		{index: 10, context:'10.结果整理',status:'current'},
		                    		    		{index: 11, context:'11.保密部门主管审批',status:'undo'}
		                    		    	],
		                    		    	margin : '5 5 5 5'
		                        	})});
                                	
                                	FHD.ajax({
                                        url: __ctxPath + '/assess/riskTidy/riskRbsOrRe.f',
                                        params: {
                                        	assessPlanId : me.businessId
                			            },
                                        callback: function (data) {
                                            if (data && data.success) {
                                            	me.add(me.riskTidyTopPanel);
                                            	
                                            	var riskRbsOrReListMap = {ids:data.riskRbsOrReListMap};
                                            	var ids = {ids:data.ids};
                                            	
                                            	me.riskTidyTab = Ext.widget('risktidytabforsecurity',{riskTidyMan : me});
                                            	if(me.winId != me.id){//如果是计划列表中的整理页面，不需要导航
                                            		me.assessTree = Ext.widget("assesstreeforsecurity",{extraRiskParams : riskRbsOrReListMap, region:'west',
                                                		schm : me.schm,extraParams : ids, businessId : me.businessId, riskTidyMan : me,
                                                		assessPlanId : me.businessId, collapseds : false});
                                            	}else{
                                            		me.assessTree = Ext.widget("assesstreeforsecurity",{extraRiskParams : riskRbsOrReListMap, region:'west',
                                                		extraParams : ids, businessId : me.businessId, riskTidyMan : me,
                                                		assessPlanId : me.businessId, collapseds : false});
                                            	}
                                            	me.add(me.assessTree);
                                            	me.add(me.riskTidyTab);
                                            	
                                            	if(me.winId){
                                            		Ext.getCmp(me.winId).body.unmask();
                                            	}else{
                                            		me.body.unmask();
                                            	}
                                            	
                                            	me.setAssessTitle();
                                            	
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }else{
                    	if(me.winId){
                    		Ext.getCmp(me.winId).body.mask("数据加载中,请耐心等待....","x-mask-loading");
                    	}else{
                    		me.body.mask("数据加载中,请耐心等待...","x-mask-loading");
                    	}
                    	
                    	FHD.ajax({
                            url: __ctxPath + '/assess/riskTidy/riskRbsOrRe.f',
                            params: {
                            	assessPlanId : me.businessId,
                            	executionId : me.executionId,
                            	type : 'jbpm'
    			            },
                            callback: function (data) {
                                if (data && data.success) {
                                	if(data.type == 'complex'){
                                		me.jbmpType = 'complex';
                                		me.riskTidyTopPanel = Ext.widget('panel',{
                                			border : false,
                            	        	collapsible : true,
                            	        	collapsed : true,
                            	        	region : 'north',
                                			collapsed:true,
                                			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
                                	    		jsonArray:[
                                		    		{index: 1, context:'1.计划制定',status:'done'},
                                		    		{index: 2, context:'2.计划主管审批',status:'done'},
                                		    		{index: 3, context:'3.计划领导审批',status:'done'},
                                		    		{index: 4, context:'4.任务分配',status:'done'},
                                		    		{index: 5, context:'5.风险评估',status:'done'},
                                		    		{index: 6, context:'6.任务审批',status:'done'},
                                		    		{index: 7, context:'7.单位主管审批',status:'done'},
                                		    		{index: 8, context:'8.单位领导审批',status:'done'},
                                		    		{index: 9, context:'9.业务分管副总审批',status:'done'},
                                		    		{index: 10, context:'10.结果整理',status:'current'},
                                		    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
                                		    		{index: 12, context:'12.风险部门领导审批',status:'undo'}
                                		    	],
                                		    	margin : '5 5 5 5'
                                    	})});
                                	}else if(data.type == 'simple'){
                                		 me.jbmpType = 'simple';
    		                    		 me.riskTidyTopPanel =  Ext.widget('panel',{
    		                    	        	border : false,
    		                    	        	collapsible : true,
    		                    	        	collapsed : true,
    		                    	        	region : 'north',
    		                    	        	items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
    		                    				jsonArray:[
    		                    				    		{index: 1, context:'1.计划制定',status:'done'},
    		                    				    		{index: 2, context:'2.计划审批',status:'done'},
    		                    				    		{index: 3, context:'3.任务分配',status:'done'},
    		                    				    		{index: 4, context:'4.风险评估',status:'done'},
    		                    				    		{index: 5, context:'5.任务审批',status:'done'},
    		                    				    		{index: 6, context:'6.结果整理',status:'current'}
    		                    				    	],margin : '5 5 5 5'
    		                    			    })
    		                    			    
    		                    	        });
                                	}
                                	
                                	me.add(me.riskTidyTopPanel);
                                	
                                	var riskRbsOrReListMap = {ids:data.riskRbsOrReListMap};
                                	var ids = {ids:data.ids};
                                	me.riskTidyTab = Ext.widget('risktidytabforsecurity',{riskTidyMan : me});
                                	
                                	if(me.winId){//如果是计划列表中的整理页面，不需要导航
                                		me.assessTree = Ext.widget("newAssessTree",{extraRiskParams : riskRbsOrReListMap, region:'west',
                                    		extraParams : ids, businessId : me.businessId, riskTidyMan : me, 
                                    		assessPlanId : me.businessId, collapseds : false});
                                	}else{
                                		//如果是计划列表中的整理页面，删除按钮置灰
                                		if (me.riskTidyTab.riskTidyGrid.down("[name='riskTidyGrid_delete']")) {
    							           	me.riskTidyTab.riskTidyGrid.down("[name='riskTidyGrid_delete']").setVisible(false);
    							        }
                                		me.assessTree = Ext.widget("newAssessTree",{extraRiskParams : riskRbsOrReListMap, region:'west',
                                    		extraParams : ids, businessId : me.businessId, riskTidyMan : me,
                                    		assessPlanId : me.businessId, collapseds : false});
                                	}
                                	me.add(me.assessTree);
                                	me.add(me.riskTidyTab);
                                	
                                	if(me.winId){
                                		Ext.getCmp(me.winId).body.unmask();
                                	}else{
                                		me.body.unmask();
                                	}
                                	
                                	me.setAssessTitle();
                                }
                            }
                        });
                    }
                }
            });
        }
        me.callParent(arguments);
        
        
    }
});