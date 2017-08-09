/**
 * 风险添加申请表单
 */

Ext.define('FHD.view.risk.riskDataAddFlow.RiskAddApply', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskaddapply',
    requires: [
               'FHD.ux.icm.common.FlowTaskBar'
	],
   
	submitUrl:'/risk/flow/saveRiskApply',
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
		
	  	//操作
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
	 			    			if (btn == 'yes') {
	 			    				me.submit();
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
        	items : [me.flowtaskbar,me.riskForm],
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
    submit:function(){
		var me=this;
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({
			url : __ctxPath + me.submitUrl,
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId
			},
			callback : function(data) {
				me.body.unmask();
			}
		});
	},
	
	reloadData:function(){
		var me=this;
	}
   
});