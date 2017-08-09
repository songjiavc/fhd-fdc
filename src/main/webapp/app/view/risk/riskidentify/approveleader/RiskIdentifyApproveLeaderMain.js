Ext.define('FHD.view.risk.riskidentify.approveleader.RiskIdentifyApproveLeaderMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskIdentifyApproveLeaderMain',
    requires: [
	],
	//提交审批
	submit:function(isPass,examineApproveIdea){
		var me=this;
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/access/riskidentify/submitriskidentifyapprovalbyleader.f',
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	isPass:isPass,
		    	examineApproveIdea:examineApproveIdea
			},
			callback : function(data) {
				me.body.unmask();
				if(me.winId){
					Ext.getCmp(me.winId).close();
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
	  	me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					//提交工作流
					Ext.MessageBox.show({
			    		title : '提示',
			    		width : 260,
			    		msg : '确认提交吗？',
			    		buttons : Ext.MessageBox.YESNO,
			    		icon : Ext.MessageBox.QUESTION,
			    		fn : function(btn) {
			    			if (btn == 'yes') {//确认删除
			    				me.submit(me.riskIdentifyApproveDown.ideaApproval.isPass,
			    							me.riskIdentifyApproveDown.ideaApproval.getValue());
			    			}
			    		}
			    	});
	            }
			}
		];
	  	me.riskIdentifyApproveDown = Ext.create('FHD.view.risk.riskidentify.approve.RiskIdentifyApproveDown',{
	  		//region: 'center',
	  		flex: 1,
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  		winId : me.winId,
	  		margin : '0 0 0 0'
	  	});
		me.flowtaskbar = Ext.widget('panel',{
			//region: 'north',
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
	    		jsonArray:[
		    		{index: 1, context:'1.计划制定',status:'done'},
		    		{index: 2, context:'2.计划主管审批',status:'done'},
		    		{index: 3, context:'3.计划领导审批',status:'current'},
		    		{index: 4, context:'4.任务分配',status:'undo'},
		    		{index: 5, context:'5.风险辨识',status:'undo'},
		    		{index: 6, context:'6.辨识汇总',status:'undo'},
		    		{index: 7, context:'7.单位主管审批',status:'undo'},
		    		{index: 8, context:'8.单位领导审批',status:'undo'},
		    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
		    		{index: 10, context:'10.结果整理',status:'undo'},
		    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
		    		{index: 12, context:'12.风险部门领导审批',status:'undo'}
		    	],
		    	margin : '5 5 5 5'
    	})});
		
        Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items : [me.flowtaskbar,me.riskIdentifyApproveDown],
    	
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
        
        me.form.load({
	        url: __ctxPath + '/access/formulateplan/queryassessplanbyplanId.f',
	        params:{businessId:me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
    }
});