/**
 * 方案制定表单
 */
Ext.define('FHD.view.response.major.scheme.MajorRiskSchemeMakeMainPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskschememakemainpanel',
    requires: [
	],
	//获得导航item
	getItems: function(){
		var me = this;
		me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
    		jsonArray:[
    			{index: 1, context:'1.计划制定',status:'done'},
	    		{index: 2, context:'2.计划主管审批',status:'done'},
	    		{index: 3, context:'3.计划领导审批',status:'done'},
	    		{index: 4, context:'4.任务分配',status:'done'},
	    		{index: 5, context:'5.方案制定',status:'current'},
	    		{index: 6, context:'6.部门汇总',status:'undo'},
	    		{index: 7, context:'7.部门主管审批',status:'undo'},
	    		{index: 8, context:'8.部门领导审批',status:'undo'},
	    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
	    		{index: 10, context:'10.方案审批(风管办公室)',status:'undo'},
	    		{index: 11, context:'11.结果整理',status:'undo'}
	    	],
	    	margin : '5 5 5 5'
		});
	},
	//提交
	submitWindow: function(){
		var me = this;
		
		me.body.mask("提交中...","x-mask-loading");
		var majorRiskId = me.schemeDown.majorRiskInfoForm.majorRiskId.getValue();
		var deptId = me.schemeDown.majorRiskInfoForm.deptId.getValue();
		FHD.ajax({
			url:__ctxPath + "/majorResponse/doProcessForMakeSchemeOfCommonEmp",
			params:{
				executionId:me.executionId,
				businessId:me.businessId,
				deptId:deptId,
				majorRiskId:majorRiskId,
			},
			async: false,
			callback: function (data) {
				me.body.unmask();
				if(me.winId){
					Ext.getCmp(me.winId).close();
				}
			}
		});
	},
	//提交
	btnConfirm:function(form){
		var me=this;
		Ext.MessageBox.show({
    		title : '提示',
    		width : 260,
    		msg : '确认提交吗？',
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认
    				me.body.mask("提交中...","x-mask-loading");
    				//提交计划审批，执行流程
    				/*FHD.ajax({//ajax调用
						url : __ctxPath+ '/majorResponse/majorRiskPlanApprove',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea,
					    	approverId: approverId,
					    	approverKey:"planLeaderApproval"//计划领导审批
						},
						callback : function(data) {
							me.body.unmask();
							if(me.winId){
								Ext.getCmp(me.winId).close();
							}
						}
					});*/
    			}
    		}
    	});
	},
	reloadData:function(){
		var me=this;
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.getItems();
	  	me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					//提交工作流
					me.submitWindow();
	            }
			}
		];
	  	var schemeType = "0";//非汇总
	  	var empType = "1";//普通员工
	  	me.schemeDown = Ext.create('FHD.view.response.major.scheme.MajorRiskSchemeDownFormPanel',{
	  		flex: 1,
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  		schemeType :schemeType,
	  		empType: empType,
	  		winId : me.winId,
	  		margin : '0 0 0 0'
	  	});
	  	//流程节点导航
		me.flowtaskbar = Ext.widget('panel',{
			border:false,
			collapsible : true,
			collapsed:true,
			items: me.flowtaskbarItem
		});
        Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items : [me.flowtaskbar,me.schemeDown],
		    listeners: {
				beforerender : function () {
					var me = this;
					FHD.ajax({
			            url: __ctxPath + '/assess/quaassess/findAssessName.f',
			            params : {
			            	assessPlanId: me.businessId
			            },
			            callback: function (data) {
			                if (data && data.success) {
			                	var assessPlanName = data.assessPlanName;
			                	me.flowtaskbar.setTitle('计划名称:' + assessPlanName);
			                } 
			            }
			        });
				}
			}
        });
        me.callParent(arguments);
        
        /*me.form.load({
	        url: __ctxPath + '/access/formulateplan/queryassessplanbyplanId.f',
	        params:{businessId:me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });*/
    }
});