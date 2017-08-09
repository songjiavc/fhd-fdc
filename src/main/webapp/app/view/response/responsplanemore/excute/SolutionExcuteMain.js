Ext.define('FHD.view.response.responsplanemore.excute.SolutionExcuteMain', {
	extend:'Ext.panel.Panel',
	aligs:'widget.solutionExcuteMain',
	requires: [
    ],
    border: false,
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	//获得导航item
	getItems: function(){
		var me = this;
		FHD.ajax({
            url: __ctxPath + '/access/riskidentify/findriskidentifydescription.f',
            params : {
            	executionId: me.executionId
            },
            async: false,
            callback: function (data) {
            	var items;
                me.description = data;
                if('riskResponseExcuteMore'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
				    			{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划主管审批',status:'done'},
					    		{index: 3, context:'3.计划领导审批',status:'done'},
					    		{index: 4, context:'4.任务分配',status:'done'},
					    		{index: 5, context:'5.方案制定',status:'done'},
					    		{index: 6, context:'6.方案审批',status:'done'},
					    		{index: 7, context:'7.单位主管审批',status:'done'},
					    		{index: 8, context:'8.单位领导审批',status:'done'},
					    		{index: 9, context:'9.业务分管副总审批',status:'done'},
					    		{index: 10, context:'10.方案执行',status:'current'},
					    		{index: 11, context:'11.风险应对主管审批',status:'undo'},
					    		{index: 12, context:'12.风险应对领导审批',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }else{
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
				    			{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划审批',status:'done'},
					    		{index: 3, context:'3.任务分配',status:'done'},
					    		{index: 4, context:'4.方案制定',status:'done'},
					    		{index: 5, context:'5.方案审批',status:'done'},
					    		{index: 6, context:'6.方案执行',status:'current'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }
                return items;
            }
        });
	},
	//提交方法
	submitWindow: function(){
		var me = this;
		if('riskResponseExcuteMore'==me.description){//多节点
			me.formulateSubmitMainPanel = Ext.create('FHD.view.risk.assess.formulatePlan.FormulateSubmitMainPanel');
			var formulateApproverEdit = me.formulateSubmitMainPanel.formulateapproveredit;//审批人页面
			me.subWin_assess = Ext.create('FHD.ux.Window', {
				title:'选择审批人',
	   		 	height: 200,
	    		width: 600,
	    		layout: {
	     	        type: 'fit'
	     	    },
	   			buttonAlign: 'center',
	   			closeAction: 'hide',
	    		items: [me.formulateSubmitMainPanel],
	   			fbar: [
	   					{ xtype: 'button', text: '确定', handler:function(){me.btnConfirm(formulateApproverEdit);}},
	   					{ xtype: 'button', text: '取消', handler:function(){me.subWinhide(formulateApproverEdit);}}
					  ]
			}).show();
		}else{
			me.btnConfirm();
		}
	},
	//提交审批
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
    				if('riskResponseExcuteMore'==me.description){
						var approverId = form.items.items[0].value;
						if(!approverId){
							FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
							return ;
						}
						if(me.subWin_assess){
	    					me.subWin_assess.hide();
							form.approver.clearValues();
	    				}
	    				me.body.mask("提交中...","x-mask-loading");
						FHD.ajax({//ajax调用
							url : __ctxPath+ '/responseplan/workflow/solutionexcutetosupervisor.f',
						    params : {
						    	businessId:me.businessId,
						    	executionId:me.executionId,
						    	approverId: approverId
							},
							callback : function(data) {
								me.body.unmask();
								if(me.winId){
									Ext.getCmp(me.winId).close();
								}
							}
						});
					}else{
						FHD.ajax({
			    		    url : __ctxPath+ '/response/workflow/executesolution.f',
			    		    params : {
			    		    	businessId:me.businessId,
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
					}
    			}
    		}
    	});
	},
	//关闭提交窗口
	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},
	
	initComponent : function() {
		var me=this;
		me.getItems();
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
							me.submitWindow();
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
            border: false,
        	items: me.flowtaskbarItem
        });
		
		me.solutionexecutionform = Ext.create('FHD.view.response.bpm.SolutionExecutionForm');
		
		me.downpanel = Ext.create('Ext.panel.Panel',{
			flex: 1,
			autoScroll: true,
        	border:false,
			items:[me.solutionexecutionform]
		});
		Ext.applyIf(me, {
			layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
			items:[me.flowtaskbar,me.downpanel]
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
				if(data.success){
					me.solutionexecutionform.initParam(data.data);
	     			me.solutionexecutionform.reloadData();
	     			me.solutionexecutionform.solutionformforview.basicInfoFieldset.collapse();
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