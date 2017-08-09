/**
 * 评价执行第一步：测试结果填写
 */
Ext.define('FHD.view.icm.assess.form.AssessResultForm',{
	extend: 'Ext.form.Panel',
    alias: 'widget.assessresultform',
    
    autoScroll:true,
    bodyPadding:'0 3 3 3',
    border:false,
	
	initComponent : function() {
	    var me = this;
	    
		var arr = '<img src="'+__ctxPath+'/images/resultset_next.png">';
		//评价计划预览表单
		me.basicInfo=Ext.create('FHD.view.icm.assess.form.AssessPlanPreviewForm',{
			columnWidth:1/1,
			businessId:me.businessId
		});
		
		me.approvalIdeaGrid = Ext.create('FHD.view.comm.bpm.ApprovalIdeaGrid',{
			executionId: me.executionId,
			title:'',
			margin:'7 10 0 30',
			columnWidth:1
		});
		//fieldSet
		var approvalIdeaFieldSet={
			xtype : 'fieldset',
			layout : {
				type : 'column'
			},
			collapsed : true,
			collapsible : true,
			columnWidth:1/1,
			collapsible : true,
			title : '审批意见列表',
			items : [me.approvalIdeaGrid]
		};
		
		me.assessGuidelinesGrid = Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesShowGrid',{
			columnWidth:1/1,
			margin: '7 10 0 30',
			assessPlanId:me.businessId
		});
		
		me.assessGuidelinesGridPanel={
			xtype : 'fieldset',
			layout : {
				type : 'column'
			},
			collapsed : true,
			columnWidth:1/1,
			collapsible : true,
			title: '评价标准查看',
			items :[me.assessGuidelinesGrid]
		};
		
		me.processTestGrid=Ext.create('FHD.view.icm.assess.component.ProcessTestGrid',{
			searchable:false,
    		pagable : false,
    		margin: '7 10 0 30',
			columnWidth:1/1,
			parentId:me.id,
			executionId:me.executionId,
			businessId:me.businessId
		});
		
		me.processGridFieldSet={
			xtype : 'fieldset',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : false,
			title: '1.选择评价方式',
			items :[me.processTestGrid]
		};
		
		me.items=[
			{
	 			xtype : 'fieldset',
				layout : {
					type : 'column'
				},
				collapsed : true,
				columnWidth:1/1,
				collapsible : true,
				title : '评价计划信息',
				items : me.basicInfo
   			},
		    approvalIdeaFieldSet,
		    me.assessGuidelinesGridPanel,
		    me.processGridFieldSet
		];
		me.callParent(arguments);
		
		if(me.businessId){
			me.basicInfo.loadData(me.businessId);
		}
	},
	showSampleWindow:function(assessResultId,type,processId){
		var me=this;
		
		me.processId=processId;
		if('through' == type){
			//穿行测试，根据要穿行的次数，自动生成样本
			FHD.ajax({
    	        url:__ctxPath +'/icm/assess/saveThroughSample.f',
    	        async:false,
    	        params: {
    		        assessPlanId:me.businessId,
    		        assessResultId: assessResultId
    	        },
    	        callback: function (data) {
    	        	if(data){
    	        		me.createSampleWindow(me.id,type,me.businessId,assessResultId,processId);
    	        	}
    	        }
        	});
		}else{
			//抽样测试
			me.createSampleWindow(me.id,type,me.businessId,assessResultId,processId);
		}
	},
	createSampleWindow:function(id,type,businessId,assessResultId,processId){
		var me=this;
		
		//me.assessResultId当作一个变量，用来保存样本弹窗中的参数
		me.assessResultId = assessResultId;
		
		me.samplePanel=Ext.create('FHD.view.icm.assess.form.SampleTestForm',{
			parentId:me.id,
			type:type,
			businessId:me.businessId,
			assessResultId:assessResultId,
			processId:processId
		});
		
		me.viewWindow = Ext.create('FHD.ux.Window',{
			title:'样本测试',
			collapsible:false,
			maximizable:true,//（是否增加最大化，默认没有）
			items:[me.samplePanel],
			buttonAlign: 'center',
			buttons: [
				{ 
					text: '上一条',
					name:'sample_pre_btn',
					handler:function(){
						var index = me.pointTestEditGrid.store.find('assessResultId', me.assessResultId);
						me.setSampleBtn(index-1);
						
						//保存当前评价结果的样本数据
						if(me.saveSampleResult(me.assessResultId)){
							//上一条评价结果的assessResultId
							var record = me.pointTestEditGrid.store.getAt(index-1);
							if(record){
								me.assessResultId = record.data.assessResultId;
								me.samplePanel.assessResultId=me.assessResultId;
								me.samplePanel.sampleEditGrid.assessResultId=me.assessResultId;
								if('through' == type){
									//穿行测试，根据要穿行的次数，自动生成样本
									FHD.ajax({
						    	        url:__ctxPath +'/icm/assess/saveThroughSample.f',
						    	        async:false,
						    	        params: {
						    		        assessPlanId: me.businessId,
						    		        assessResultId: me.assessResultId
						    	        },
						    	        callback: function (data) {
						    	        	if(data){
						    	        		//刷新上一条评价结果的样本数据
												me.samplePanel.loadData(me.assessResultId);
						    	        	}
						    	        }
						        	});
								}else{
									//刷新上一条评价结果的样本数据
									me.samplePanel.loadData(me.assessResultId);
								}
							}
						}else{
							me.setSampleBtn(index);
						}
					}
				},
				{ 
					text: '下一条',
					name:'sample_next_btn',
					handler:function(){
						var index = me.pointTestEditGrid.store.find('assessResultId', me.assessResultId);
						me.setSampleBtn(index+1);
						
						//保存当前评价结果的样本数据
						if(me.saveSampleResult(me.assessResultId)){
							//下一条评价结果的assessResultId
							var record = me.pointTestEditGrid.store.getAt(index+1);
							if(record){
								me.assessResultId = record.data.assessResultId;
								me.samplePanel.assessResultId=me.assessResultId;
								me.samplePanel.sampleEditGrid.assessResultId=me.assessResultId;
								if('through' == type){
									//穿行测试，根据要穿行的次数，自动生成样本
									FHD.ajax({
						    	        url:__ctxPath +'/icm/assess/saveThroughSample.f',
						    	        async:false,
						    	        params: {
						    		        assessPlanId: me.businessId,
						    		        assessResultId: me.assessResultId
						    	        },
						    	        callback: function (data) {
						    	        	if(data){
						    	        		//刷新下一条评价结果的样本数据
												me.samplePanel.loadData(me.assessResultId);
						    	        	}
						    	        }
						        	});
								}else{
									//刷新下一条评价结果的样本数据
									me.samplePanel.loadData(me.assessResultId);
								}
							}
						}else{
							me.setSampleBtn(index);
						}
					}
				},
			    { 
			    	text: '保存',
			    	handler:function(){
			    		me.saveSampleResult(me.assessResultId);
			    	}
			    },
			    { 
			    	text: '关闭',
			    	handler:function(){
			    		me.viewWindow.close();
			    	}
			    }
			],
			listeners:{
				'close' : function(){
					//刷新评价流程form
		    		//me.loadFormData(businessId,processId);
		    		//刷新评价内容grid
		    		me.pointTestEditGrid.store.load();
		    		//刷新评价流程grid
		    		me.processTestGrid.store.load();
				}
			}
    	});
		
		var index = me.pointTestEditGrid.store.find('assessResultId', me.assessResultId);
		me.setSampleBtn(index);
		
		me.viewWindow.show();
	},
	setSampleBtn:function(index){
		var me=this;
		
		var count = me.pointTestEditGrid.store.getCount();
		if(index == 0){
			//第一条评价结果,[上一条]按钮不可用
			if(index == count-1){
				me.viewWindow.down('[name=sample_pre_btn]').setDisabled(true);
				me.viewWindow.down('[name=sample_next_btn]').setDisabled(true);
			}else{
				me.viewWindow.down('[name=sample_pre_btn]').setDisabled(true);
				me.viewWindow.down('[name=sample_next_btn]').enable(true);
			}
		}else if(index == count-1){
			//最后一条评价结果，[下一条]按钮不可用
			me.viewWindow.down('[name=sample_pre_btn]').enable(true);
			me.viewWindow.down('[name=sample_next_btn]').setDisabled(true);
		}else{
			//所有按钮都可用
			me.viewWindow.down('[name=sample_pre_btn]').enable(true);
			me.viewWindow.down('[name=sample_next_btn]').enable(true);
		}
	},
	saveSampleResult:function(assessResultId){
		var me=this;
		
 		var jsonArray=[];
 		
 		/*
 		 * 验证规则：样本编号不能为空，是否有效不能为空
 		 * 1).当是否有效为：是(Y)，不验证
 		 * 2).当是否有效为：否(N)，说明不能为空，且需要上传附件
 		 * 3).当是否有效为：不适用(Y/N)，补充样本来源不能为空
 		 */
 		var validateFlag=false;
 		var message = '';
 		
 		var count = me.samplePanel.sampleEditGrid.store.getCount();
		for(var i=0;i<count;i++){
			var item = me.samplePanel.sampleEditGrid.store.data.get(i);
			
 			if(item.get('code')=='' || item.get('code')==null || item.get('code')==undefined){
 				message += "'样本编号'字段不能为空!<br/>";
 				validateFlag=true;
 			}
 			if(item.get('isQualified')=='' || item.get('isQualified')==null || item.get('isQualified')==undefined){
 				message += "'是否有效'字段不能为空!<br/>";
 				validateFlag=true;
 			}
 			if(item.get('isQualified')=='N'){
 				//否
 				if(item.get('comment')=='' || item.get('comment')==null || item.get('comment')==undefined){
 					message += "无效的样本，说明字段不能为空!<br/>";
 					validateFlag=true;
 				}
 				//影响程度
				if(item.get('impact')=='' || item.get('impact')==null || item.get('impact')==undefined){
					message += "无效的样本，影响程度不能为空!<br/>";
					validateFlag=true;
				}
 				if(item.get('fileId')==''){
 					message += "无效的样本，必须上传附件!<br/>";
 					validateFlag=true;
 				}
 			}else if(item.get('isQualified')=='NAN'){
 				//不适用
 				if(item.get('isHasSupplement')!='Y'){
 					message += "不适用的样本，必须有补充样本!<br/>";
 					validateFlag=true;
 				}
 			}
 			
 			if(''!=message){
 				break;
 			}else{
 				jsonArray.push(item.data);
 			}
 		}
 		
 		if(validateFlag){
 			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), message);
 			return false;
 		}
 		
 		var jsonString=Ext.encode(jsonArray);
 		FHD.ajax({
			url:__ctxPath +'/icm/assess/mergeAssessSampleBatch.f',
            params: {
            	jsonString:jsonString,
            	assessResultId:assessResultId
            },
            callback: function (data) {
            	if(data){
            		FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
            		//样本列表
            		me.samplePanel.sampleEditGrid.store.load();
            		if(me.practiceTestListfieldset){
            			//刷新评价流程form
            			me.loadFormData(me.businessId,me.processId);
            		}
		    		//刷新评价内容grid
		    		me.pointTestEditGrid.store.load();
		    		//刷新评价流程grid
		    		me.processTestGrid.store.load();
            	}
            }
		});
 		return true;
	},
	addPracticeTestList:function(isAll,processId,assessorId){
		var me=this;
		
		me.processId=processId;

		me.remove("pointTestEditGridFieldset",true);
		me.remove("practiceTestListFieldset",true);
		
		me.calculate={
			xtype:'displayfield',
			id:'assessResult',
			fieldLabel: '结果',
			labelWidth : 120,
			margin: '7 10 5 30',
			columnWidth : 1 / 1
		};
		var comboStore = Ext.create('Ext.data.Store',{
	    	fields : ['id', 'name'],
			data : [
			    {'id' : 'Y','name' : '完全符合'},
			    {'id' : 'YORN','name' : '部分符合'},
			    {'id' : 'N','name' : '不符合'},
			    {'id' : 'NAN','name' : '不适用'}
			]
		});
		me.combo= Ext.create('Ext.form.ComboBox',{
			fieldLabel: '调整结果',
			id:'isDesirableAdjustCombo',
			labelWidth : 120,
			store :comboStore,
			emptyText:'请选择',
			valueField : 'id',
			margin: '7 10 0 30',
			columnWidth : 1 / 2,
			name:'isDesirableAdjust',
			displayField : 'name',
			queryMode: 'local',
			editable : false
		});

		me.testDesc={
   			xtype: "textarea",
   			margin: '7 10 0 10',
   			columnWidth : 1 / 2,
   			fieldLabel: "调整说明",
   			id: "memo",
   			name:"assessmentDesc",
   			labelSepartor: "：",
   			value:""
   		};
   
		me.practiceTestListfieldset={
	 		id:'practiceTestListFieldset',
	 		xtype : 'fieldset',
			layout : {
				type : 'column'
			},
			collapsed : false,
			columnWidth:1/1,
			collapsible : false,
			title : '3.填写穿行测试结论',
			items : [me.calculate,me.combo,me.testDesc]
   		};
		
		me.pointTestEditGrid=Ext.create('FHD.view.icm.assess.component.PointTestEditGrid',{
			testType:'ca_assessment_measure_0',
			businessId:me.businessId,
			processId:processId,
			assessorId:assessorId,
			isAll:isAll,
			parentId:me.id,
			margin: '7 10 0 30',
			listeners:{
				'afterrender':function(grid,eOpts){
					grid.store.load({
						callback:function(){
							//设置滚动条到列表最下面
							var d = me.body.dom;
							d.scrollTop = d.scrollHeight - d.offsetHeight;
							
							//刷新评价流程form
							me.loadFormData(me.businessId, processId);
						}
					});
				}
			}
		});
		var inputDesc = {
	    	xtype:'label',
	    	margin:'0 0 0 30',
	    	html:'<font color=red>注:</font>&nbsp;&nbsp;[填写说明]<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;1.“样本测试”：点击“样本测试”，填写样本及样本有效状态；<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;2.“调整结果”：根据“样本测试”，系统会给出一个结果，可进行人工调整。<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;3.“调整说明”：调整后，要简要描述一下调整的原因。<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;4.“缺陷描述”：最终判定“无效”时，要填写“缺陷描述”。<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;5.“影响程度”：评价结果最终“无效”时，主观判断该评价点对本流程的设计有效性的影响程度，1~5。<br/>'
	    };
		me.pointTestEditGridFieldset={
			id:'pointTestEditGridFieldset',
			xtype : 'fieldset',
			layout : {
				type : 'fit'
			},
			collapsed : false,
			collapsible : false,
			title: '2.填写评价结论及缺陷信息',
			items :[me.pointTestEditGrid,inputDesc]
		};
		me.add(me.pointTestEditGridFieldset);
		me.add(me.practiceTestListfieldset);
	},
	addSampleTestList:function(isAll,processId,assessorId){
		var me=this;
		
		me.processId=processId;

		me.remove("pointTestEditGridFieldset",true);
		me.remove("practiceTestListFieldset",true);
		
		me.pointTestEditGrid = Ext.create('FHD.view.icm.assess.component.MeasureEditGrid',{
			testType:'ca_assessment_measure_1',
			businessId:me.businessId,
			processId:processId,
			assessorId:assessorId,
			isAll:isAll,
			parentId:me.id,
			margin: '7 10 0 30',
			listeners:{
				'afterrender':function(grid,eOpts){
					grid.store.load({
						callback:function(){
							//设置滚动条到列表最下面
							var d = me.body.dom;
							d.scrollTop = d.scrollHeight - d.offsetHeight;
						}
					});
				}
			}
		});
		var inputDesc = {
	    	xtype:'label',
	    	margin:'0 0 0 30',
	    	html:'<font color=red>注:</font>&nbsp;&nbsp;[填写说明]<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;1.“样本测试”：点击“样本测试”，填写样本及样本有效状态；<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;2.“调整结果”：根据“样本测试”，系统会给出一个结果，可进行人工调整。<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;3.“调整说明”：调整后，要简要描述一下调整的原因。<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;4.“缺陷描述”：最终判定“无效”时，要填写“缺陷描述”。<br/>' +
	    		'&nbsp;&nbsp;&nbsp;&nbsp;5.“影响程度”：评价结果最终“无效”时，主观判断该评价点对本控制的执行有效性的影响程度，1~5。<br/>'
	    };
		me.pointTestEditGridFieldset={
			id:'pointTestEditGridFieldset',
			xtype : 'fieldset',
			layout : {
				type : 'fit'
			},
			collapsed : false,
			collapsible : false,
			title: '2.填写评价结论及缺陷信息',
			items :[me.pointTestEditGrid,inputDesc]
		};
		me.add(me.pointTestEditGridFieldset);
	},
	validateAssessResultGrid:function(){
		var me=this;
		
		var validateFlag=false;
		var assessProcessMessage = '';
		var assessResultMessage = '';
		var message = '';
		
		/*
		 * 评价流程验证规则：
		 * 1).穿行测试未测试数不为0
		 * 2).抽样测试未测试数不为0
		 */
		var totalCount = me.processTestGrid.store.getCount();
		for(var j=0;j<totalCount;j++){
			var row = me.processTestGrid.store.data.get(j);
			
			if(row.get('numByPracticeTest')!='0'){
				assessProcessMessage += "穿行测试还有未测试的评价结果!<br/>";
				validateFlag=true;
			}
			if(row.get('numBySampleTest')!='0'){
				assessProcessMessage += "抽样测试还有未测试的评价结果!<br/>";
				validateFlag=true;
			}
			
			if(''!=assessProcessMessage){
 				break;
 			}else{
 				continue;
 			}
		}
		
		/*
 		 * 评价结果验证规则：
 		 * 1).结果不能为空
 		 * 2).调整结果为空，则判断结果为'否'，缺陷描述必填
 		 * 3).调整结果不为空，且为'否'时，缺陷描述必填
 		 * 4).调整结果不为空，与结果不一致时，调整说明必填
 		 */
		if(me.pointTestEditGrid != undefined){
			var count = me.pointTestEditGrid.store.getCount();
			for(var i=0;i<count;i++){
				var item = me.pointTestEditGrid.store.data.get(i);
				
	 			if(typeof(item.get('hasDefect')) == "string"){
	 				assessResultMessage += "'结果'字段不能为空，每个评价点必须进行样本测试!<br/>";
	 				validateFlag=true;
	 			}
	 			if(typeof(item.get('hasDefectAdjust')) == "string"){
	 				//调整结果为空
	 				if(!item.get('hasDefect')){
	 					//结果为否
	 					if(item.get('comment')=='' || item.get('comment')==null || item.get('comment')==undefined){
	 						assessResultMessage += "'结果'字段为否，'缺陷描述'字段不能为空!<br/>";
	 						validateFlag=true;
	 					}
	 					//影响程度
	 					/*
	 					if(item.get('impact')=='' || item.get('impact')==null || item.get('impact')==undefined){
	 						assessResultMessage += "'结果'字段否，'影响程度'字段不能为空!<br/>";
	 						validateFlag=true;
	 					}
	 					*/
	 				}
	 			}else{
	 				//调整结果不为空
	 				if(!item.get('hasDefectAdjust')){
	 					//调整结果为否
	 					if(item.get('comment')=='' || item.get('comment')==null || item.get('comment')==undefined){
	 						assessResultMessage += "'调整结果'字段为否，'缺陷描述'字段不能为空!<br/>";
	 						validateFlag=true;
	 					}
	 					//影响程度
	 					/*
	 					if(item.get('impact')=='' || item.get('impact')==null || item.get('impact')==undefined){
	 						assessResultMessage += "'结果'字段为否，'影响程度'字段不能为空!<br/>";
	 						validateFlag=true;
	 					}
	 					*/
	 				}
	 				if(item.get('hasDefectAdjust') != item.get('hasDefect')){
	 					//结果与调整结果不一致
	 					if(item.get('adjustDesc')=='' || item.get('adjustDesc')==null || item.get('adjustDesc')==undefined){
	 						assessResultMessage += "'调整说明'字段不能为空!<br/>";
	 						validateFlag=true;
	 					}
	 				}
	 			}

	 			if(''!=assessResultMessage){
	 				break;
	 			}else{
	 				continue;
	 			}
			}
		}
 		
 		if(validateFlag){
 			if('' != assessProcessMessage){
 				message += assessProcessMessage; 
 			}
 			if('' != assessResultMessage){
 				message += assessResultMessage; 
 			}
 			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), message);
 			return false;
 		}else{
 			return true;
 		}
	},
	saveData:function(p){
		var me=this;
		
		//调整结果
		var isDesirableAdjust=me.getForm().getValues().isDesirableAdjust;
		
		//如果调整结果不一样，给出提示
		if(me.pointTestEditGrid != undefined){
			var count = me.pointTestEditGrid.store.getCount();
			for(var i=0;i<count;i++){
				var item = me.pointTestEditGrid.store.data.get(i);//debugger
				if(item.get('hasDefectAdjust')!=""){
		 			if(item.get('hasDefect') == item.get('hasDefectAdjust')){
		 				Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), "调整结果和结果必须不一样");
		 				return;
		 			}else{
		 				continue;
		 			}
				}else{
					continue;
				}
			}
		}
		
		//调整说明
		var assessmentDesc=me.getForm().getValues().assessmentDesc;
		if(!(assessmentDesc==undefined)){
			
			//保存评价流程form
			FHD.ajax({
				url:__ctxPath+'/icm/assess/saveResultForm.f',
	            params: {
	            	isDesirableAdjust:isDesirableAdjust,
	            	assessmentDesc:assessmentDesc,
	            	assessPlanId:me.businessId,
	            	processId:me.processId
	            },
	            callback: function (data) {
	            	if(data){
	            		//刷新评价流程form
	            		me.loadFormData(me.businessId,me.processId);
	            	}
	            }
			});
		}
		
		//保存评价内容grid
		if(me.pointTestEditGrid){
			var rows = me.pointTestEditGrid.store.getModifiedRecords();
			var jsonArray=[];
			Ext.each(rows,function(item){
				jsonArray.push(item.data);
			});
			FHD.ajax({
				url:__ctxPath + '/icm/assess/mergeAssessResultBatch.f',
                params: {
                	jsonString:Ext.encode(jsonArray),
                	assessPlanId:me.businessId,
                	processId:me.processId
                },
                callback: function (data) {
                	if(data){
        				FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
        				//刷新评价内容grid
        	    		me.pointTestEditGrid.store.load();
        	    		//刷新评价流程grid
        	    		me.processTestGrid.store.load();
        	    		if(p){
        	    			p.assessplandraftform.loadData(me.businessId,true);
        	    		}
                	}	
                }
			});			
		}
	},
	loadFormData:function(businessId,processId){
		var me=this;
		 
		//刷新评价流程form
		me.getForm().load({
	        url:__ctxPath + '/icm/assess/findAssessResult.f',
	        params:{
	        	assessPlanId: businessId,
	        	processId: processId
	        },
	        success: function (form, action) {
	     	   return true;
	        },
	        failure: function (form, action) {
	     	   return false;
	        }
		});
	},
	loadData:function(businessId,editflag){
		var me=this;
		
		me.businessId=businessId;
		me.editflag=editflag;
		//grid刷新
		me.processTestGrid.extraParams.assessPlanId=me.businessId;
		me.processTestGrid.store.load();
	},
	reloadData:function(){
		var me=this;
		
		me.processTestGrid.store.load();
	}
});
