/**
 * 
 * 任务分配
 */

Ext.define('FHD.view.risk.assess.kpiSet.SetMainPanel',{
 	extend: 'Ext.panel.Panel',
 	alias: 'widget.setMainPanel',
 	
 	border:false,
 	height:500,
 	
 	//跳转到角色设置页面
	showRolePanel: function(){
		var me = this;
		var isExist = false;
		var centerPanel = Ext.getCmp('center-panel');
		me.roleMainPanel = Ext.create('FHD.view.sys.role.RoleMainPanel',{
			closable:true,
			title: '角色管理'
    	});
		if(centerPanel){	//程序入口
			for(var i=0;i<centerPanel.items.length;i++){
				if(centerPanel.items.items[i].id.split('-')[0] == me.roleMainPanel.id.split('-')[0]){
					isExist = true;
					me.roleMainPanel = centerPanel.items.items[i];
				}
			}
			if(isExist){
				centerPanel.setActiveTab(me.roleMainPanel);
			}else{
				me.roleMainPanel = centerPanel.add(me.roleMainPanel);
				centerPanel.setActiveTab(me.roleMainPanel);
			}
		}else{	//邮件入口
			me.roleWin = Ext.create('FHD.ux.Window', {
				title:'角色管理',
	   		 	height: 600,
	    		width: 1000,
	    		maximizable: true,
	   			layout: 'fit',
	    		items: [me.roleMainPanel]
			}).show();
		}
	},
 	
    initComponent: function () {
    	var me = this;
    	me.p2=Ext.create('FHD.view.risk.assess.kpiSet.SetAssessTask',{
			businessId:me.businessId,
			border: false,
			margin : '0 0 0 0'
		});
		me.bbar={
			items: ['->',
				   {
       				text: '提交',
    				iconCls: 'icon-operator-submit',
    				handler: function () {
    					//提交工作流
						//验证分配角色
    					var empIds = [];
						var items = me.p2.store.data.items;
						for(var k in items){
							if(!items[k].data.empId){
								empIds = [];
								break;
							}else{
								empIds.push(me.p2.store.data.items[k].data.empId);
							}
						}
						if(!empIds.length){
							FHD.notification('评估人不能为空！',FHD.locale.get('fhd.common.prompt'));
							return ;
						}
						Ext.MessageBox.show({
				    		title : '提示',
				    		width : 260,
				    		msg : '确认提交吗？',
				    		buttons : Ext.MessageBox.YESNO,
				    		icon : Ext.MessageBox.QUESTION,
				    		fn : function(btn) {
				    			if (btn == 'yes') {//确认删除
				    				me.submit(empIds);
				    			}
				    		}
				    	});    					
    				} 
    			}
    		]
		};
		
		Ext.applyIf(me, {
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
        	margin : '0 0 0 0'
		});
    		
    	me.callParent(arguments);
    	
    	FHD.ajax({
            url: __ctxPath + '/access/approval/findStrApproval.f',
            params : {
            	executionId: me.executionId
            },
            callback: function (data) {
                if (data && data.success) {
                	if(data.type == 'complex'){
                		me.flowtaskbar = Ext.widget('panel',{
                			border:false,
                			collapsible : true,
                			collapsed:true,
                			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
                	    		jsonArray:[
                		    		{index: 1, context:'1.计划制定',status:'done'},
						    		{index: 2, context:'2.计划主管审批',status:'done'},
						    		{index: 3, context:'3.计划领导审批',status:'done'},
						    		{index: 4, context:'4.任务分配',status:'current'},
						    		{index: 5, context:'5.风险评估',status:'undo'},
						    		{index: 6, context:'6.任务审批',status:'undo'},
						    		{index: 7, context:'7.单位主管审批',status:'undo'},
						    		{index: 8, context:'8.单位领导审批',status:'undo'},
						    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
						    		{index: 10, context:'10.结果整理',status:'undo'},
						    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
						    		{index: 12, context:'12.风险部门领导审批',status:'undo'}
                		    	],
                		    	margin : '5 5 5 5'
                    	})});
                	}else if(data.type == 'simple'){
                		me.flowtaskbar = Ext.widget('panel',{
                			border:false,
                			collapsible : true,
                			collapsed:true,
                			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
                	    		jsonArray:[
                		    		{index: 1, context:'1.计划制定',status:'done'},
                		    		{index: 2, context:'2.计划审批',status:'done'},
                		    		{index: 3, context:'3.任务分配',status:'current'},
                		    		{index: 4, context:'4.风险评估',status:'undo'},
                		    		{index: 5, context:'5.任务审批',status:'undo'},
                		    		{index: 6, context:'6.结果整理',status:'undo'}
                		    	],
                		    	margin : '5 5 5 5'
                    	})});
                	}
                	
                	FHD.ajax({
    		            url: __ctxPath+ '/assess/quaassess/findAssessName.f',
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
                	me.add(me.flowtaskbar);
                	me.add(me.p2);
                } 
            }
        });
    },
    //任务分配提交
    submit:function(empIds){
		var me=this;
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/access/formulateplan/risktaskdistribute.f',
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	pingguEmpIds: empIds
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