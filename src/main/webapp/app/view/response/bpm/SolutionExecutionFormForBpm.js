Ext.define('FHD.view.response.bpm.SolutionExecutionFormForBpm', {
	extend:'Ext.panel.Panel',
	aligs:'widget.solutionexecutionformforbpm',
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	requires: [
		'FHD.view.response.bpm.SolutionExecutionForm',
		'FHD.ux.icm.common.FlowTaskBar'
    ],
	autoScroll:true,
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
//	bodyPadding:'0 3 3 3',
	initComponent : function() {
		var me=this;
		me.bbar={
			items: [
				'->',{
					text: '保存',
    				iconCls: 'icon-control-stop-blue',
    				handler: function () {
					    me.solutionexecutionform.save();
    				} 
				},{
       				text: '提交',
    				iconCls: 'icon-operator-submit',
    				handler: function () {
						if(me.isFinish()){
							me.solutionexecutionform.save();
							FHD.ajax({
				    		    url : __ctxPath+ '/response/workflow/responseexecution.f',
				    		    params : {
				    				executionId : me.executionId
				    			 },
				    			 callback : function(data) {
				    				if(data.success){
				    					if(me.winId){
											Ext.getCmp(me.winId).close();
										}
				    				}else{
				    					Ext.Msg.alert("提示","工作流执行失败！");
				    					return false;
				    				}
				    				
				    			 }
				    		});
						}else{
							Ext.Msg.alert("提示","任务尚未完成,不能提交！");
				    		return false;
						}
    				}
				}
    		]
		};
		
		me.flowtaskbar=Ext.widget('panel',{
            collapsed:true,
            collapsible: true,
            title : '应对执行',
            maxHeight:200,
            split: true,
            border: false,
        	items:[
	        	Ext.widget('flowtaskbar',{
	    		jsonArray:[
		    		{index: 1, context:'1.方案制定',status:'done'},
		    		{index: 2, context:'2.方案审批',status:'done'},
		    		{index: 3, context:'3.方案执行',status:'current'}
		    	]
	    		})
        	]
        });
		
		me.solutionexecutionform = Ext.widget('solutionexecutionform');
		Ext.applyIf(me, {
			items:[me.flowtaskbar,me.solutionexecutionform]
		});
		me.callParent(arguments);
	},
	reloadData:function(){
		var me=this;
		//初始化业务页面的数据
		FHD.ajax({
			url:__ctxPath+'/response/bpm/getbpmvalues.f',
			params: {
				executeId : me.executionId
			},
	     	callback: function (data) {
	     		/*
	     		 * data : {
	     		 * 	   solutionId : '',
	     		 *     solutionExecuteId : '',
	     		 *     empId : ''
	     		 * }
	     		 */
				if(data.success){
					me.solutionexecutionform.initParam(data.data);
	     			me.solutionexecutionform.reloadData();
				}else{
					Ext.Msg.alert("提示","初始化工作流数据失败！");
					return false;
				}
	     		
	         }
         });
		
	},
	isFinish : function(){
		var me = this;
		var flag = false;
		var historyDatas = me.solutionexecutionform.executionHistory.store.data;
		Ext.each(historyDatas.items,function(item){
			if(item.get('progress') == '100%'){
				flag = true;
			}
		});
		if(!flag){
			if(me.solutionexecutionform.progress.getValue() == '100'){
				flag = true;
			}
		}
		return flag;
	}
});