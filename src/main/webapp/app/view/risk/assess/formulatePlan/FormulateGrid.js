/**
 * 
 * 计划制定表格
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulateGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.formulateGrid',
 	requires: [
 	           'FHD.view.IndexNav',
 	           'FHD.view.risk.assess.formulatePlan.FormulatePlanEdit',
 	           'FHD.view.risk.assess.formulatePlan.FormulatePlanPreview',
 	           'FHD.view.risk.assess.formulatePlan.AssessPlanQuestMonitorMain',
 	          'FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyMan'
	],
	
	newRiskTidyManClick : function(url, businessId, executionId, titles){
		var me = this;
		var centerPanel = Ext.getCmp('center-panel');
		if(me.newRiskTidyMan){
			centerPanel.remove(me.newRiskTidyMan);
			me.newRiskTidyMan = centerPanel.add(Ext.widget('newRiskTidyMan',{
				businessId: businessId,
				executionId: executionId,
				closable:true,
				title: titles
	    	}));
			centerPanel.setActiveTab(me.newRiskTidyMan);
		}else{
			me.newRiskTidyMan = centerPanel.add(Ext.widget('newRiskTidyMan',{
				businessId: businessId,
				executionId: executionId,
				closable:true,
				title: titles
	    	}));
			centerPanel.setActiveTab(me.newRiskTidyMan);
		}
	},
	
	assessPlanQuestMonitorMainClick : function(id, title){
		var me = this;
		var centerPanel = Ext.getCmp('center-panel');
		if(me.p){
			centerPanel.remove(me.p);
			me.p = centerPanel.add(Ext.widget('assessPlanQuestMonitorMain',{
	    		id: id,
	    		title : title,
	    		closable:true
	    	}));
			centerPanel.setActiveTab(me.p);
		}else{
			me.p = centerPanel.add(Ext.widget('assessPlanQuestMonitorMain',{
	    		id: id,
	    		title : title,
	    		closable:true
	    	}));
			centerPanel.setActiveTab(me.p);
		}
	},
	
    edit : function(isAdd,isBtn){
    	var me = this;
    	var prt = me.up('formulatePlanCard');
    	var formulatePlanEdit = prt.formulatePlanMainPanel.p1;
    	var responsPerson = formulatePlanEdit.reponser;
    	var contactPerson = formulatePlanEdit.contactor; 
    	var btnSubmit = Ext.getCmp('btn_tijiao');//提交按钮置灰
    	if(btnSubmit){
    		btnSubmit.setDisabled(true);
    	}
    	if(isAdd){//添加按钮
    		FHD.ajax({
    			url : __ctxPath + '/access/formulateplan/findsystemplate.f',
    			callback : function(data){
    				responsPerson.clearValues();//清空组件缓存
		    		contactPerson.clearValues();
		    		formulatePlanEdit.getForm().reset();
    				if(data.template){//查询默认模板
    					formulatePlanEdit.mb.setValue(data.template);
    				}
		    		formulatePlanEdit.workType.setValue('assess_work_type_year');//工作类型默认‘年度风险评估’
		    		var newBusinId;
		    		me.businessId = newBusinId;
		    		prt.showFormulatePlanMainPanel();
    			}
    		});
    	}else{//修改按钮
    		var selection = me.getSelectionModel().getSelection();
    		var length = selection.length;
    	    	   if (length >= 2) {//判断是否多选
    	    	        FHD.notification(FHD.locale.get('fhd.common.updateTip'),FHD.locale.get('fhd.common.prompt'));
    	    	        return;
    	    	   }else{
    					if(typeof(formulatePlanEdit) != 'undefined'){
    						responsPerson.clearValues();//清空组件缓存
    						contactPerson.clearValues();
    						formulatePlanEdit.getForm().reset();
    						me.businessId = selection[0].get('id');
    						formulatePlanEdit.loadData(me.businessId);
    					}
    					if(isBtn){
    						if("N"==selection[0].data.dealStatus){
	    						prt.showFormulatePlanMainPanel();
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
    	var prt = me.up('formulatePlanCard');
    	var formulatePlanMainPanel = prt.formulatePlanMainPanel;
    	me.formulateplanpreview = Ext.widget('formulateplanpreview',{
    		pid: pid,
    		businessId: me.businessId,
    		formulatePlanMainPanel: formulatePlanMainPanel
    	});
    	me.formulateplanpreview.form.load({
    	        url:__ctxPath + '/access/formulateplan/querypreviewpanelbyplanId.f',
    	        params:{pid:pid},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        }
    	    });
		me.formulateplanpreview.formulatasubmitpreviewGridPanel.store.proxy.url = __ctxPath + '/access/formulateplan/queryriskcircuseegrid.f';
		me.formulateplanpreview.formulatasubmitpreviewGridPanel.store.proxy.extraParams.planId = pid;
		//me.formulateplanpreview.formulatasubmitpreviewGridPanel.store.load();
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'预览',
   		 	height: 530,
    		width: 800,
    		maximizable: true,
   			layout: 'fit',
   			//buttonAlign: 'center',
    		items: [me.formulateplanpreview]
		}).show();
	},
    
     del : function(me){//删除方法
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	for(var i=0;i<selection.length;i++){
			if(selection[i].data.dealStatus=='H'){
				//Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), '处理中，不允许删除！');
				FHD.notification('处理中，不允许删除！',FHD.locale.get('fhd.common.prompt'));
				return ;
			}else if(selection[i].data.dealStatus=='F'){
				//Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), '已完成，不允许删除！');
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
    							//Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
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
    	if (me.down("[name='planEditId']")) {
            me.down("[name='planEditId']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='planDeleteId']")) {
            me.down("[name='planDeleteId']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='wenjuanbtnId']")) {
            me.down("[name='wenjuanbtnId']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
         if (me.down("[name='planMakeId']")) {
            me.down("[name='planMakeId']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
    },
    //问卷监控 
    questMonitorFun: function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	me.assessPlanQuestMonitorMainClick(selection[0].get('id'), '问卷监控-' + selection[0].get('planName'));
    },
    
    //计划整理
    makeUpResult: function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	var businessId = selection[0].get('id');
    	//查询是否到评估结果审批节点
    	FHD.ajax({//ajax调用
				url :  __ctxPath + '/access/formulateplan/findexcutionidbybusinessid.f',
				params : {
					businessId:businessId
				},
				callback : function(data){
					if(data){
						if('true'==data.data.resultNode){
							me.newRiskTidyManClick('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyMan',
									businessId, 
									data.executionId, 
									'评估结果-' + selection[0].get('planName'));
						}
					}
				}
			});
    	
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
     			/*listeners: {
     				click: function(){
     					me.edit();
     				}
     			}*/
	        }/*,{
	            header: "工作类型",
	            dataIndex: 'workType',
	            sortable: true,
	            width:40,
	            flex:1,
	            renderer:function(dataIndex) { 
    				  if(dataIndex == "AL"){
    					  return "因果分析";
    				  }else if(dataIndex == "AK"){
    					  return "层次分析";
    				  }
    			}
	        }*/,{
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
        	url : __ctxPath + "/access/formulateplan/queryAccessPlansPage.f?planType="+"riskAssess",//查询列表url
        	cols:cols,
        	tbarItems:[{
        			btype:'add',
        			handler:function(){
        				me.edit(true,true);
        			}
    			},'-',{
        			btype:'edit',
        			disabled:true,
        			name : 'planEditId',
        			handler:function(){
        				me.edit(false,true);
        			}
    			},'-',{
        			btype:'delete',
        			disabled:true,
        			name : 'planDeleteId',
        			handler:function(){
        				me.del(me);
        			}
    			},'-',{
        			text:'问卷监控',
        			iconCls:'icon-monitor',
        			disabled:true,
        			name : 'wenjuanbtnId',
        			handler:function(){
        				me.questMonitorFun();
        			}
    			},'-',{
        			text:'评估结果',
        			iconCls:'icon-script',
        			disabled:true,
        			name : 'planMakeId',
        			handler:function(){
        				me.makeUpResult();
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
						if(selection[0].data.dealStatus=='N'){//未提交
							if (me.down("[name='wenjuanbtnId']")) {
				                me.down("[name='wenjuanbtnId']").setDisabled(true);
				            }
				            if (me.down("[name='planMakeId']")) {
				                me.down("[name='planMakeId']").setDisabled(true);
				            }
				    	}else if(selection[0].data.dealStatus=='H'){
				    		if (me.down("[name='planEditId']")) {
				                me.down("[name='planEditId']").setDisabled(true);
				            }
				            if (me.down("[name='planDeleteId']")) {
				                me.down("[name='planDeleteId']").setDisabled(true);
				            }
				            if (me.down("[name='planMakeId']")) {
				                me.down("[name='planMakeId']").setDisabled(true);
				            }
				    	}else if(selection[0].data.dealStatus=='F'){
				    		if (me.down("[name='planEditId']")) {
				                me.down("[name='planEditId']").setDisabled(true);
				            }
				            if (me.down("[name='planDeleteId']")) {
				                me.down("[name='planDeleteId']").setDisabled(true);
				            }
				    	}
				    	if(selection.length>=2){
				    		if (me.down("[name='wenjuanbtnId']")) {
				                me.down("[name='wenjuanbtnId']").setDisabled(true);
				            }
				            if (me.down("[name='planMakeId']")) {
				                me.down("[name='planMakeId']").setDisabled(true);
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