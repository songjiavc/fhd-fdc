/**
 * 
 * 计划制定表单(审批)
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulateApproverSubmitMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.formulateApproverSubmitMain',
   
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
			    				me.submit(me.formulateApproverSubmitDownMain.ideaApproval.isPass,
			    							me.formulateApproverSubmitDownMain.ideaApproval.getValue());
			    			}
			    		}
			    	});
	            }
			}
		];
	  	
	  	me.formulateApproverSubmitDownMain = Ext.create('FHD.view.risk.assess.formulatePlan.FormulateApproverSubmitDownMain',{
	  		flex: 1,
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  		winId : me.winId,
	  		margin : '0 0 0 0'
	  	});
	  	
		me.flowtaskbar = Ext.widget('panel',{
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
	    		jsonArray:[
		    		{index: 1, context:'1.计划制定',status:'done'},
		    		{index: 2, context:'2.计划审批',status:'current'},
		    		{index: 3, context:'3.任务分配',status:'undo'},
		    		{index: 4, context:'4.风险评估',status:'undo'},
		    		{index: 5, context:'5.评估任务审批',status:'undo'},
		    		{index: 6, context:'6.结果整理',status:'undo'}
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
            items : [me.flowtaskbar,me.formulateApproverSubmitDownMain],
    	
	    listeners: {
			beforerender : function () {
				var me = this;
				FHD.ajax({
		            url: __ctxPath + '/assess/quaassess/findAssessName.f?assessPlanId=' + me.businessId,
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
    },
    
    submit:function(isPass,examineApproveIdea){
		var me=this;
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/access/formulateplan/riskassessplanapproval.f',
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
				}else{
					window.location.reload();
				}
			}
		});
	},
	 reloadData:function(){
		var me=this;
	}
    
   
});