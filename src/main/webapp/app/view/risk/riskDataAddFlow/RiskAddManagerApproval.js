/**
 * 风险添加审批列表
 */

Ext.define('FHD.view.risk.riskDataAddFlow.RiskAddManagerApproval', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskaddManagerapproval',
    requires: [
               'FHD.ux.icm.common.FlowTaskBar'
	],
   
	submitUrl:'/risk/flow/saveManagerApproval',
    // 初始化方法
    initComponent: function() {
        var me = this;

	  	me.riskList = Ext.create("FHD.view.risk.cmp.risk.RiskEventAddManagerApproveGrid",{
	  		region: 'center',
	  		state:'waitingArchive',	//待归档
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  	});
	  	
		me.flowtaskbar = Ext.widget('panel',{
			region: 'north',
			title:'归档添加的风险',
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.widget('flowtaskbar',{
	    		jsonArray:[
		    		{index: 1, context:'1.风险添加',status:'done'},
		    		{index: 2, context:'2.风险审批',status:'done'},
		    		{index: 3, context:'3.风险归档',status:'current'}
		    	],
		    	margin : '5 5 5 5'
    	})});
		
	  	me.bbar=[
	 		    '->',
	 		    {
	 				text:'结束本流程',
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
	 			    				me.submit(ids.join(','));
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
        	items : [me.flowtaskbar,me.riskList],
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
    submit:function(ids){
		var me=this;
		//me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ me.submitUrl,
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
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