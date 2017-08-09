/**
 * 风险添加审批列表
 */

Ext.define('FHD.view.risk.riskDataAddFlow.RiskAddApproval', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskaddapproval',
    requires: [
               'FHD.ux.icm.common.FlowTaskBar'
	],
   
	submitUrl:'/risk/flow/saveLeaderApproval',
    // 初始化方法
    initComponent: function() {

        var me = this;

	  	me.riskList = Ext.create("FHD.view.risk.cmp.risk.RiskEventAddApproveGrid",{
	  		region: 'center',
	  		state:'waitingApprove',	//待审批
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  	});
	  	
	    //审批意见
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			columnWidth:1/1,
			executionId:me.executionId
		});
	  	me.ideaApprovalFieldSet = Ext.create('Ext.form.FieldSet',{
			region:'south',
			layout:{
     	        type: 'column'
     	    },
			title:'审批意见',
			collapsible: true,
			margin: '5 5 0 5',
			items:[me.ideaApproval]
	  	});
	  	
		me.flowtaskbar = Ext.widget('panel',{
			title:'审批添加的风险',
			region: 'north',
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.widget('flowtaskbar',{
	    		jsonArray:[
		    		{index: 1, context:'1.风险添加',status:'done'},
		    		{index: 2, context:'2.风险审批',status:'current'},
		    		{index: 3, context:'3.风险归档',status:'undo'}
		    	],
		    	margin : '5 5 5 5'
    	})});
		
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
	 			    				//获取事件ids
	 			    				var ids = [];
	 			    				me.riskList.grid.getStore().each(function(record){
	 			    					ids.push(record.data.id);
	 			    				});
	 			    				me.submit(me.ideaApproval.isPass,me.ideaApproval.getValue(),ids.join(','));
	 			    				//关闭当前窗口
	 			    				if(me.winId){
							    		Ext.getCmp(me.winId).close();
							    		//这块应该刷新待办列表
							    		
							    	}
	 			    			}
	 			    		}
	 			    	});
	 	            }
	 			}
	 		];
		
        Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'border'
        	},
        	items : [me.flowtaskbar,me.riskList,me.ideaApprovalFieldSet],
		    listeners: {
				beforerender : function () {
					var me = this;
					//me.flowtaskbar.setTitle('修改后的名称');
				}
			}
        });

        
        me.callParent(arguments);
    },
    
    /**
     * 提交
     */
    submit:function(isPass,examineApproveIdea,ids){
		var me=this;
		//me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath + me.submitUrl,
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	isPass:isPass,
		    	examineApproveIdea:examineApproveIdea,
		    	ids:ids
			},
			callback : function(data) {
				//me.body.unmask();
			}
		});
	},
	
	reloadData:function(){
		var me=this;
	}
   
});