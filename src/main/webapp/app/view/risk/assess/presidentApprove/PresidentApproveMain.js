
Ext.define('FHD.view.risk.assess.presidentApprove.PresidentApproveMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.presidentApproveMain',
    
    requires: [
               'FHD.view.risk.assess.utils.GridCells'
              ],
    
	reloadData:function(){
		 
	},
	
	//提交方法
	submitAssess: function(){
		var me = this;
		var isPass = me.ideaApproval.isPass;
		Ext.MessageBox.show({
    		title : '提示',
    		width : 260,
    		msg : '确认提交吗？',
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
    				me.body.mask("提交中...","x-mask-loading");
					FHD.ajax({
			            url: __ctxPath + '/assess/quaassess/approvalColl.f?executionId=' + me.executionId,
			            params:{
			            	assessPlanId: me.businessId,
			            	isPass:isPass     //  评估业务分管副总审批   2017年4月6日13:31:19 吉志强添加
			            	},
			            callback: function (data) {
			            	if(me.winId != null){
			            		me.body.unmask();
		    					Ext.getCmp(me.winId).close();
		    				}else{
		    					me.body.unmask();
		    					window.location.reload();
		    				}
			            }
			        });
    			}
    		}
    	});
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
					me.submitAssess();
	            }
			}
		];
		me.flowtaskbar = Ext.widget('panel',{
			region: 'north',
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
	    		jsonArray:[
		    		{index: 1, context:'1.计划制定',status:'done'},
		    		{index: 2, context:'2.计划主管审批',status:'done'},
		    		{index: 3, context:'3.计划领导审批',status:'done'},
		    		{index: 4, context:'4.任务分配',status:'done'},
		    		{index: 5, context:'5.风险评估',status:'done'},
		    		{index: 6, context:'6.任务审批',status:'done'},
		    		{index: 7, context:'7.单位主管审批',status:'done'},
		    		{index: 8, context:'8.单位领导审批',status:'done'},
		    		{index: 9, context:'9.业务分管副总审批',status:'current'},
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
	        		type: 'border',
	        		padding: .5
	        	},
	            items : [me.flowtaskbar],
	    	
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
		
		Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/findDimCols.f?executionId=' + me.executionId,
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        var array = new Array();
		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
		        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.3});
		        });
		        
		        me.collectGrid = Ext.create('FHD.view.risk.assess.AssessApproveGridUtil',{
		        	url: __ctxPath + '/access/approval/findleaderapprovegrid.f',
		        	assessApproveSubmit : me, array : array, businessId: me.businessId, executionId: me.executionId});
		        
		        me.collectGrid.store.proxy.extraParams.assessPlanId = me.businessId;
			    me.collectGrid.store.proxy.extraParams.executionId = me.executionId;
			    me.collectGrid.store.load();
		        
		        me.collectGridSet = Ext.create('Ext.form.FieldSet',{
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
			  	
			  	me.downPanel = Ext.create('Ext.form.Panel',{
			  		autoScroll: true,
			  		region: 'center',
		        	border:false,
		            items : [me.collectGridSet, me.ideaApprovalSet]
			  	});
			  	
			  	me.add(me.downPanel);
		    }
		});
    }
});