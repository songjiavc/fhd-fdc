Ext.define('FHD.view.icm.assess.bpm.AssessPlanBpmEight', {
    extend: 'Ext.form.Panel',
    alias: 'widget.assessplanbpmeight',
    
    autoScroll:true,
	//bodyPadding:'0 3 3 3',
    border:false,
    requires: [
       'FHD.ux.icm.common.FlowTaskBar'
    ],
    
	initComponent : function() {
	    var me = this;
	    
	    me.bbar=[
 		    '->',
 		    {
 				text: '保存',
 				id:'assess_plan_bpm_eight_save_btn',
 				iconCls: 'icon-control-stop-blue',
 				handler: function(){
 					 me.save();
 				}
 			},
 		    {
 				text:'提交',
 				id:'assess_plan_bpm_eight_submit_btn',
 				iconCls: 'icon-operator-submit',
 				handler: function () {
 					//提交工作流
 					me.submit();
 	            }
 			}/*,
 			{
 				text:'关闭',
 				iconCls: 'icon-control-fastforward-blue',
 				handler: function () {
 					if(me.winId){
 						Ext.getCmp(me.winId).close();
 					}
 	            }
 			}
 			*/
 		];
	    
		//评价计划预览表单
		me.basicInfo=Ext.create('FHD.view.icm.assess.form.AssessPlanPreviewForm',{
			columnWidth:1/1,
			//bodyPadding:'0 3 3 3',
			businessId:me.businessId
		});
		
		me.assessGuidelinesGrid = Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesShowGrid',{
			columnWidth:1/1,
			margin: '7 10 0 30',
			
			assessPlanId:me.businessId
		});
		
		me.assessGuidelinesGridPanel={
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : true,
			columnWidth:1/1,
			collapsible : true,
			title: '评价标准查看',
			items :[me.assessGuidelinesGrid]
		};
		
		me.assessDefectGrid=Ext.create('FHD.view.icm.assess.component.AssessDefectGrid',{
			margin: '7 10 0 30',
			columnWidth:1/1,
			businessId:me.businessId
		});
		
		me.assessDefectGridPanel={
			xtype : 'fieldset',
			//margin: '0 3 3 3',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : true,
			title: '缺陷汇总',
			items :[me.assessDefectGrid]
		};
		
		me.contentContainer = Ext.create('Ext.panel.Panel',{
			flex : 1,
			border:false,
			layout:{
				type:'column'
        	},
        	defaults:{
        		margin:'0 30 10 10',
        		columnWidth:1/1
        	},
        	overflowX:'hidden',
			overflowY:'auto',
        	items:[me.basicInfo,me.assessGuidelinesGridPanel,me.assessDefectGridPanel]
		});
		
		Ext.applyIf(me, {
			layout : {
				align:'stretch',
				type : 'vbox'
			},
			items:[Ext.widget('flowtaskbar',{
        		jsonArray:[
 		    		{index: 1, context:'1.内控测试',status:'done'},
		    		{index: 2, context:'2.测试结果复核',status:'done'},
		    		{index: 3, context:'3.汇总整理',status:'current'},
		    		{index: 4, context:'4.缺陷反馈',status:'undo'},
		    		{index: 5, context:'5.缺陷调整',status:'undo'},
		    		{index: 6, context:'6.缺陷确认',status:'undo'}
 		    	]
 	    	}),me.contentContainer]
		});
		
		me.callParent(arguments);
		
		if(me.businessId){
			me.basicInfo.loadData(me.businessId);
		}
	},
	save:function(){
		var me=this;
		
		var rows = me.assessDefectGrid.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		FHD.ajax({
			url : __ctxPath + '/icm/assess/mergeDefectBatch.f',
			params : {
				jsonString:Ext.encode(jsonArray)
			},
			callback : function(data){
				if(data){
					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
				}
			}
		});
		me.assessDefectGrid.store.commitChanges(); 
	},
	submit:function(){
		var me=this;
		
		//验证每条缺陷必须选择整改责任部门、缺陷描述、缺陷级别、缺陷类型
		var validateFlag=false;
 		var message = '';
 		
 		var count = me.assessDefectGrid.store.getCount();
		for(var i=0;i<count;i++){
			var item = me.assessDefectGrid.store.data.get(i);
			if(item.get('orgId')=='' || item.get('orgId')==null || item.get('orgId')==undefined){
				message += "'整改责任部门'字段不能为空!<br/>";
				validateFlag=true;
			}
 			if(item.get('desc')=='' || item.get('desc')==null || item.get('desc')==undefined){
 				message += "'缺陷描述'字段不能为空!<br/>";
 				validateFlag=true;
 			}
			if(item.get('type')=='' || item.get('type')==null || item.get('type')==undefined){
				message += "'缺陷类型'字段不能为空!<br/>";
				validateFlag=true;
			}
 			if(item.get('level')=='' || item.get('level')==null || item.get('level')==undefined){
 				message += "'缺陷级别'字段不能为空!<br/>";
 				validateFlag=true;
 			}
 			
 			if(''!=message){
 				break;
 			}
 		}
 		
 		if(validateFlag){
 			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), message);
 			return false;
 		}
 		
		//保存缺陷汇总信息
		var rows = me.assessDefectGrid.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		/*缺陷汇总时，缺陷信息不能为空，否则后台会给所有人发待办*/
		var count = me.assessDefectGrid.store.getTotalCount();
		if(count==0){
			//终止工作流
			FHD.ajax({
				url : __ctxPath+ '/icm/assess/assessDefectSummaryStop.f',
				params : {
					businessId:me.businessId,
					executionId:me.executionId
				},
				callback : function(data) {
					if(data==true){
						if(me.winId){
							Ext.getCmp(me.winId).close();
						}else{
							//单点登录执行待办关闭window
							FHD.closeWindow();
						}
						Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), "缺陷信息为空,此次评价计划流转已结束");
					}else{
						FHD.notification(data,FHD.locale.get('fhd.common.prompt'));
					}
				}
			});
 			return false;
		}
		FHD.ajax({
			url : __ctxPath + '/icm/assess/mergeDefectBatch.f',
			params : {
				jsonString:Ext.encode(jsonArray)
			},
			callback : function(data){
				if(data){
					Ext.MessageBox.show({
			            title: '提示',
			            width: 260,
			            msg: '提交后将不能修改，您确定要提交么?',
			            buttons: Ext.MessageBox.YESNO,
			            icon: Ext.MessageBox.QUESTION,
			            fn: function (btn) {
			                if (btn == 'yes') {
			                	//所有按钮不可用
								Ext.getCmp('assess_plan_bpm_eight_save_btn').setDisabled(true);
								Ext.getCmp('assess_plan_bpm_eight_submit_btn').setDisabled(true);
								
								//提交工作流
								FHD.ajax({
									url : __ctxPath+ '/icm/assess/assessDefectSummary.f',
									params : {
										businessId:me.businessId,
										executionId:me.executionId
									},
									callback : function(data) {
										if(data==true){
											if(me.winId){
												Ext.getCmp(me.winId).close();
											}else{
												//单点登录执行待办关闭window
												FHD.closeWindow();
											}
										}else{
											FHD.notification(data,FHD.locale.get('fhd.common.prompt'));
										}
									}
								});
			                }
			            }
					});
				}
			}
		});
	},
	reloadData : function(){
		var me=this;
		
	}
});