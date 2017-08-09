Ext.define('FHD.view.risk.managereport.managereportmake.ManageReportPlanMakeGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.managereportplanmakegrid',
 	requires: [
 	           'FHD.view.IndexNav',
 	           'FHD.view.response.workplan.workplanmake.WorkPlanMakePreview'
	],
    edit : function(isAdd,isBtn){
    	var me = this;
    	var prt = me.up('managereportplanmakecard');
    	var managereportplanmakeeditmain = prt.managereportplanmakeeditmain.p1;
    	var responsPerson = managereportplanmakeeditmain.reponser;
    	var contactPerson = managereportplanmakeeditmain.contactor; 
    	var btnSubmit = Ext.getCmp('btn_tijiao_workPlan');//提交按钮置灰
    	if(btnSubmit){
    		btnSubmit.setDisabled(true);
    	}
    	if(isAdd){//添加按钮
    		responsPerson.clearValues();//清空组件缓存
    		contactPerson.clearValues();
    		managereportplanmakeeditmain.getForm().reset();
    		managereportplanmakeeditmain.contingencyType.setValue('preplan_event_risk');//工作类型默认值‘应对方案制定’
    		var newBusinId;
    		me.businessId = newBusinId;
    		prt.showWorkPlanMakeEditMain();
    	}else{//修改按钮
    		var selection = me.getSelectionModel().getSelection();
    		var length = selection.length;
    	    	   if (length >= 2) {//判断是否多选
    	    	        FHD.notification(FHD.locale.get('fhd.common.updateTip'),FHD.locale.get('fhd.common.prompt'));
    	    	        return;
    	    	   }else{
    					if(typeof(managereportplanmakeeditmain) != 'undefined'){
    						responsPerson.clearValues();//清空组件缓存
    						contactPerson.clearValues();
    						managereportplanmakeeditmain.getForm().reset();
    						me.businessId = selection[0].get('id');
    						managereportplanmakeeditmain.loadData(me.businessId);
    					}
    					if(isBtn){
    						if("N"==selection[0].data.dealStatus){
	    						prt.showWorkPlanMakeEditMain();
	    					}else{
	    						me.pid = selection[0].get('id');
	    						me.previewWindow(me,me.pid);//预览窗口
	    					}
    					}else{
    						me.pid = selection[0].get('id');
	    						me.previewWindow(me,me.pid);//预览窗口
    					}
    	    	   	}
    	}
    },
    //预览窗口
    previewWindow:function(me,pid){
    	var prt = me.up('managereportplanmakecard');
    	var managereportplanmakeeditmain = prt.managereportplanmakeeditmain;
    	me.workPlanMakePreview = Ext.widget('workPlanMakePreview',{
    		pid: pid,
    		businessId: me.businessId,
    		workPlanMakeEditMain: managereportplanmakeeditmain
    	});
    	me.workPlanMakePreview.form.load({
    	        url:__ctxPath + '/access/formulateplan/querypreviewpanelbyplanId.f',
    	        params:{pid:pid},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        }
    	    });
		me.workPlanMakePreview.workPlanPreviewSubmitGrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryriskcircuseegrid.f';
		me.workPlanMakePreview.workPlanPreviewSubmitGrid.store.proxy.extraParams.planId = pid;
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'预览',
   		 	height: 530,
    		width: 800,
    		maximizable: true,
   			layout: 'fit',
    		items: [me.workPlanMakePreview]
		}).show();
	},
    
     del : function(me){//删除方法
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	for(var i=0;i<selection.length;i++){
			if(selection[i].data.dealStatus=='H'){
				FHD.notification('处理中，不允许删除！',FHD.locale.get('fhd.common.prompt'));
				return ;
			}else if(selection[i].data.dealStatus=='F'){
				FHD.notification('已完成，不允许删除！',FHD.locale.get('fhd.common.prompt'));
				return ;
			}
    	}
    	Ext.MessageBox.show({
    		title : FHD.locale.get('fhd.common.delete'),
    		width : 260,
    		msg : FHD.locale.get('fhd.common.makeSureDelete'),
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
    				var ids = [];
    				for(var i=0;i<selection.length;i++){
    					ids.push(selection[i].get('id'));
    				}
    				FHD.ajax({//ajax调用
    					url : me.delUrl,
    					params : {
    						ids:ids.join(',')
    					},
    					callback : function(data){
    						if(data){//删除成功！
    							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    							me.store.load();
    						}
    					}
    				});
    			}
    		}
    	});
    },
    
    setstatus : function(me){//设置按钮可用状态
    	//Ext.getCmp('planEditId_workPlan').setDisabled(me.getSelectionModel().getSelection().length === 0);
    	//Ext.getCmp('planDeleteId_workPlan').setDisabled(me.getSelectionModel().getSelection().length === 0);
    	if (me.down("[name='planEditId_workPlan']")) {
            me.down("[name='planEditId_workPlan']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
    	if (me.down("[name='planDeleteId_workPlan']")) {
            me.down("[name='planDeleteId_workPlan']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.businessId;
        me.delUrl = 'access/formulateplan/removeriskassessplanbyid.f';
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "计划名称",
	            dataIndex: 'planName',
	            sortable: true,
	            width:40,
	            flex:2,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').edit(false,false)\">"+value+"</a>";
     			}
	        },{
	            header: "负责人",
	            dataIndex: 'responsName',
	            sortable: false,
	            width:40,
	            flex:.5
	        },{
	            header: "联系人",
	            dataIndex: 'contactName',
	            sortable: false,
	            width:40,
	            flex:.5
	        },{
	            header: "开始时间",
	            dataIndex: 'beginDataStr',
	            sortable: false,
	            width:40,
	            flex:1
	        },{
	            header: "结束时间",
	            dataIndex: 'endDataStr',
	            sortable: false,
	            width:40,
	            flex:1
	        },{
	            header: "处理状态",
	            dataIndex: 'dealStatus',
	            sortable: true,
	            width:40,
	            flex:1,
	            renderer:function(dataIndex) { 
    				  if(dataIndex == "N"){
    					  return '<span style="color:red;">未开始</span>';
    				  }else if(dataIndex == "H"){
    					  return '<span style="color:green;">处理中</span>';
    				  }else if(dataIndex == "F"){
    					  return "已完成";
    				  }else if(dataIndex == "A"){
    					  return "逾期";
    				  }else if(dataIndex == "E"){
    					  return "已评价";
    				  }else if(dataIndex == "R"){
    					  return "已复核";
    				  }
    			}
	        }
        ];
       
        Ext.apply(me,{
        	region:'center',
        	url : __ctxPath + "/access/formulateplan/queryAccessPlansPage.f",//查询列表url
        	extraParams : {
        		planType : 'riskContingencyPlanTotal'
        	},
        	cols:cols,
        	tbarItems:[{
        			btype:'add',
        			//authority:'ROLE_ALL_CONTROL_RESPONSPLAN_ADD',
        			handler:function(){
        				me.edit(true,true);
        			}
    			},'-',{
        			btype:'edit',
        			//authority:'ROLE_ALL_CONTROL_RESPONSPLAN_EDIT',
        			disabled:true,
        			name : 'planEditId_workPlan',
        			handler:function(){
        				me.edit(false,true);
        			}
    			},'-',{
        			btype:'delete',
        			//authority:'ROLE_ALL_CONTROL_RESPONSPLAN_DELET',
        			disabled:true,
        			name : 'planDeleteId_workPlan',
        			handler:function(){
        				me.del(me);
        			}
    			}],
		    border: false,
		    checked : true,
		    pagable : true,
		    listeners: {
				selectionchange : function () {
					var me = this;
					var selection = me.getSelectionModel().getSelection();
					if(selection.length){
						if(selection[0].data.dealStatus=='H'){
				    		//Ext.getCmp('planEditId_workPlan').setDisabled(true);
							if (me.down("[name='planEditId_workPlan']")) {
				                me.down("[name='planEditId_workPlan']").setDisabled(true);
				            }
							//Ext.getCmp('planDeleteId_workPlan').setDisabled(true);
				            if (me.down("[name='planDeleteId_workPlan']")) {
				                me.down("[name='planDeleteId_workPlan']").setDisabled(true);
				            }
				    	}else if(selection[0].data.dealStatus=='F'){
				    		//Ext.getCmp('planEditId_workPlan').setDisabled(true);
				    		if (me.down("[name='planEditId_workPlan']")) {
				                me.down("[name='planEditId_workPlan']").setDisabled(true);
				            }
							//Ext.getCmp('planDeleteId_workPlan').setDisabled(true);
				            if (me.down("[name='planDeleteId_workPlan']")) {
				                me.down("[name='planDeleteId_workPlan']").setDisabled(true);
				            }
				    	}
					}
				}
			}
        });
        me.on('selectionchange',function(){me.setstatus(me)});
        me.callParent(arguments);
    }

});