/**
 * 风险添加申请表单
 */

Ext.define('FHD.view.risk.riskDataEditFlow.RiskEditApply', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskeditapply',
    requires: [
               'FHD.ux.icm.common.FlowTaskBar'
	],
   
    // 初始化方法
    initComponent: function() {
        var me = this;

        //节点导航
		me.flowtaskbar = Ext.widget('panel',{
			region: 'north',
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.widget('flowtaskbar',{
	    		jsonArray:[
		    		{index: 1, context:'1.风险添加',status:'current'},
		    		{index: 2, context:'2.风险审批',status:'undo'},
		    		{index: 3, context:'3.风险归档',status:'undo'}
		    	],
		    	margin : '5 5 5 5'
    	})});
		
		//申请表单
	  	me.riskForm = Ext.create("FHD.view.risk.cmp.form.RiskShortForm",{
	  		region: 'center',
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
	 			    				me.submit(me.riskForm.ideaApproval.isPass,
	 			    							me.riskForm.ideaApproval.getValue());
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
        	items : [me.flowtaskbar,me.riskForm,me.ideaApprovalFieldSet],
		    listeners: {
				beforerender : function () {
					var me = this;
					me.flowtaskbar.setTitle('修改后的名称');
				}
			}
        });

        
        me.callParent(arguments);
    },
    
    /**
     * 提交
     */
    submit:function(isPass,examineApproveIdea){
		var me=this;
//		me.body.mask("提交中...","x-mask-loading");
//		FHD.ajax({//ajax调用
//			url : __ctxPath+ '/access/formulateplan/riskassessplanapproval.f',
//		    params : {
//		    	businessId:me.businessId,
//		    	executionId:me.executionId,
//		    	isPass:isPass,
//		    	examineApproveIdea:examineApproveIdea
//			},
//			callback : function(data) {
//				me.body.unmask();
//			}
//		});
	},
	
	reloadData:function(){
		var me=this;
	}
   
});