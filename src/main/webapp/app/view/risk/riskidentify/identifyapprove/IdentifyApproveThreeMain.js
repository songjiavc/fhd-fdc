Ext.define('FHD.view.risk.riskidentify.identifyapprove.IdentifyApproveThreeMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.identifyApproveThreeMain',
    requires: [
	],
	//提交审批
	submit:function(isPass,examineApproveIdea){
		var me=this;
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/access/riskidentify/submitidentifyapproveone.f',
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
			    			if (btn == 'yes') {//确认
			    				me.submit(me.ideaApproval.isPass, me.ideaApproval.getValue());
			    			}
			    		}
			    	});
	            }
			}
		];
		me.flowtaskbar = Ext.widget('panel',{
			//region: 'north',
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
	    		jsonArray:[
		    		{index: 1, context:'1.计划制定',status:'done'},
		    		{index: 2, context:'2.计划主管审批',status:'done'},
		    		{index: 3, context:'3.计划领导审批',status:'done'},
		    		{index: 4, context:'4.任务分配',status:'done'},
		    		{index: 5, context:'5.风险辨识',status:'done'},
		    		{index: 6, context:'6.辨识汇总',status:'done'},
		    		{index: 7, context:'7.单位主管审批',status:'done'},
		    		{index: 8, context:'8.单位领导审批',status:'done'},
		    		{index: 9, context:'9.业务分管副总审批',status:'current'},
		    		{index: 10, context:'10.结果整理',status:'undo'},
		    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
		    		{index: 12, context:'12.风险部门领导审批',status:'undo'}
		    	],
		    	margin : '5 5 5 5'
    	})});
		
    	me.collectGrid = Ext.create('FHD.view.risk.riskidentify.identifyapprove.IdentifyCollectGrid',{
        	flex: 1,
        	margin: 2,
        	columnWidth: 1,
        	businessId: me.businessId,
        	executionId: me.executionId
        });
	    
        me.collectGridSet = Ext.create('Ext.form.FieldSet',{
			layout:{
     	        type: 'column'
     	    },
			title:'范围',
			collapsible: true,
			margin: '5 5 0 5',
			items:[me.collectGrid]
	  	});
	  	
	  	//审批意见
		me.ideaApproval = Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			columnWidth:1/1,
			executionId:me.executionId
		});
	  	
	  	me.ideaApprovalSet = Ext.create('Ext.form.FieldSet',{
			layout:{
     	        type: 'column'
     	    },
			title:'审批意见',
			collapsible: true,
			margin: '5 5 0 5',
			items:[me.ideaApproval]
	  	});
	  	
        Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items : [me.flowtaskbar,me.collectGrid,me.ideaApprovalSet],
    	
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
        me.collectGrid.reloadData(me.businessId, me.executionId);
        me.callParent(arguments);
    }
});