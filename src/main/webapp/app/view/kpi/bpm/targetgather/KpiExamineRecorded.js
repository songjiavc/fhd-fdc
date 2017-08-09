Ext.define('FHD.view.kpi.bpm.targetgather.KpiExamineRecorded', {
    extend: 'Ext.form.Panel',
    border:false,
    submit:function(isSubmit){
    	var me=this;
    	
    	Ext.MessageBox.show({
                        title: '提示',
                        width: 260,
                        msg: '确认提交吗？',
                        buttons: Ext.MessageBox.YESNO,
                        icon: Ext.MessageBox.QUESTION,
                        fn: function (btn) {
                            if (btn == 'yes') {
                                var isPass = me.cideaApproval.isPass;
						        var idea =  me.cideaApproval.getValue();
						        var itemes = {
						        				isPass:isPass,
						        				idea:idea,
						        				executionId:me.executionId,
						        				businessId:me.businessId,
						        				isSubmit:isSubmit,
						        				valueType: 'target' //采集类型为目标值
						        			  };
								me.body.mask("提交中...","x-mask-loading");
								FHD.ajax({//ajax调用
									url : __ctxPath+ '/kpi/kpi/kpigatherresultapproval.f',
								    params : {
								    	items:Ext.JSON.encode(itemes)
									},
									callback : function(data) {
										me.body.unmask();
										if(me.winId){
											Ext.getCmp(me.winId).close();
										}else{
											FHD.closeWindow();
										}
										
									}
								});
                            } 
                        }
                    });
                    
    },
    
    initComponent: function () {
        var me = this;
        var items = {};
        items.executionId = me.executionId;
        items.businessId = me.businessId;
        me.extraParams = {
            items: Ext.JSON.encode(items)
        }
        me.saveBtn = Ext.create('Ext.button.Button',{
        	text:'保存',
        	iconCls:'icon-control-stop-blue',
        	handler: function() {
                    me.submit(false);
            }
        });
        me.submitBtn = Ext.create('Ext.button.Button',{
        	text:'提交',
        	iconCls:'icon-operator-submit',
        	handler: function() {
                    me.submit(true);
            }
        });
        me.bbars = [
       		 '->',me.saveBtn,me.submitBtn
        ];
        //创建流程导航
        if (!me.bpmtBar) {
            me.jsonArray = [{
                index: 1,
                context: '1.目标值采集',
                status: 'done'
            }, {
                index: 2,
                context: '2.采集审批',
                status: 'current'
            }];
            
            me.bpmtBarPanel = Ext.widget('panel', {
	            title: '目标值采集数据收集',
	            border:false,
	            collapsed:true,
	            collapsible:true,
	            layout:'fit'
        	});
        	
            me.bpmtBar = Ext.create('FHD.ux.icm.common.FlowTaskBar', {
                jsonArray: me.jsonArray
            });
            me.bpmtBarPanel.add(me.bpmtBar);
        }
        // 目标值採集頁面
        if (!me.resultInput) {
            me.resultInput = Ext.create('FHD.view.kpi.bpm.targetgather.KpiTargetResultInput', {
            	layout: 'fit',
                extraParams: me.extraParams,
                isEdit:false,
                flex:2
            });
        }
        if(!me.cideaApproval){
            me.cideaApprovalContainer = Ext.create('Ext.container.Container',{
            		layout:'fit',
            		flex:2,
            		columnWidth:1/1,
            		autoHeight: true
            	});
            me.cideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
    				executionId:me.executionId,
    				autoScroll:true,
    				autoHeight: true
    			});

    	  	me.cideaApprovalContainer.add(me.cideaApproval);
    	  	me.cideaApprovalFieldSet = Ext.create('Ext.form.FieldSet',{
    			layout:{
         	        type: 'column'
         	    },
    			title:'审批意见',
    			collapsible: true,
    			collapsed: true,
    			autoHeight: true,
    			margin: '5 5 0 5',
    			items:[me.cideaApprovalContainer]			
    	  	});
    	  	me.cideaApprovalFieldSet.on('beforeexpand',function() {
    	  	});
            }
            
        Ext.apply(me, {
        	border:false,
        	autoScroll: true,
        	autoHeight: true,
        	bodyStyle: 'background:#FFFFFF;',
        	bbar:me.bbars,
            layout: {
                align: 'stretch',
                type: 'vbox'
            },
            items: [me.bpmtBarPanel, me.resultInput,me.cideaApprovalFieldSet]
        })


        me.callParent(arguments);
        

    },
    reloadData: function () {
        var me = this;
    },
    undo: function () {

    }
    

});