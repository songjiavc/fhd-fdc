/**
 * 
 * 定性评估卡片面板 2017年4月12日15:52:19风险评估到子流程 进行评估时把辨识拆分出来，只保留分析评价 吉志强
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessCard',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.quaAssessCard',
    
    requires: [
			//	 'FHD.view.risk.assess.quaAssess.QuaAssessOpe',
                 'FHD.view.risk.assess.quaAssess.QuaAssessGridSecrecy',
				 'FHD.view.risk.assess.quaAssess.QuaAssessShowGrid',
				 'FHD.view.risk.assess.quaAssess.QuaAssessSubmit',
				 'FHD.view.risk.assess.AssessApproveSubmit',
				 'FHD.view.risk.assess.quaAssess.QuaAssessAdd'
              ],
  
    
    /*
     * 查询打分对象维度 array 
     * 郭鹏
     * 20170511
     * */
    queryArray: function(){
    	var me = this;
    	array = new Array(),
    	Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/findDimCols.f',
		    params : {
		    	executionId: Ext.getCmp('QuaAssessManId').executionId
			},
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
		        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.5});
		        });

		    }
		});
		return array;
    },
    
    quaAssessShow : function(){
    	var me = this;
    	Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/findDimCols.f',
		    params : {
		    	executionId: Ext.getCmp('QuaAssessManId').executionId
			},
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        
		        me.quaAssessShowGrid = Ext.widget('quaAssessShowGrid',
		        		{url:__ctxPath + '/assess/quaAssess/findAssessGridSecrecy.f', array : array, businessId:me.businessId});
				
				me.formwindow = new Ext.Window({
					layout:'fit',
					iconCls: 'icon-show',//标题前的图片
					modal:true,//是否模态窗口
					collapsible:true,
					width:800,
					height:500,
					maximizable:true,//（是否增加最大化，默认没有）
					constrain:true,
					items : [me.quaAssessShowGrid],
					buttons: [
		    			{
		    				text: '关闭',
		    				handler:function(){
		    					me.formwindow.close();
		    				}
		    			},
		    			{
							text : '提交',
							handler : function() {
								var editStates=document.getElementsByName('evaluationStates');
								for(var i = 0; i < editStates.length; i++){
									if(editStates[i].className==''){
										Ext.MessageBox.alert('提示信息','未完成评价任务,请确认任务全部评价结束后再次提交');
										break;
									}else{
										Ext.MessageBox.show({
								    		title : '提示',
								    		width : 260,
								    		msg : '确认提交吗？',
								    		buttons : Ext.MessageBox.YESNO,
								    		icon : Ext.MessageBox.QUESTION,
								    		fn : function(btn) {
								    			if (btn == 'yes') {//确认删除
														me.formwindow.close();
														me.body.mask("提交中...","x-mask-loading");
								    					FHD.ajax({
								    			            url: __ctxPath + '/assess/quaassess/submitAssess.f',
								    			            params: {
								    			            	params : Ext.JSON.encode(me.riskDatas),
								    			            	executionId : me.executionId,
								    			            	assessPlanId : me.businessId
								    			            },
								    			            callback: function (data) {
							    			            		me.body.unmask();
							    			            		if(Ext.getCmp('QuaAssessManId').winId != null){
							    			            			Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).close();
							    			            		}else{
							    			            			window.location.reload();
							    			            		}
								    			            }
								    			        });
													}
								    		}
								    	});
									}
								
								}
								}
							}
		    			
		    		]
				});
				me.formwindow.show();
				me.quaAssessShowGrid.store.proxy.extraParams.assessPlanId = me.businessId;
				me.quaAssessShowGrid.store.proxy.extraParams.executionId = me.executionId;
				me.quaAssessShowGrid.store.load();
				//me.formwindow.maximize();
		    }
		});
    },
              
    showQuaAssessOpe : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.items.items[1]);
  	//	Ext.getCmp('riskBs').toggle(false);
  	//	Ext.getCmp('riskPg').toggle(true);
  	},
  	
  	showQuaAssessGrid : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.items.items[0]);
  	//	Ext.getCmp('riskBs').toggle(true);
  	//	Ext.getCmp('riskPg').toggle(false);
  	},
  	
  	showQuaAssessShowGrid : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.items.items[2]);
  	},
  	
  	getQuaAssessGridRiskDatas : function(){
  		var me = this;
    	return me.quaAssessGrid.riskDatas;
    },
  	
    getAssessPlanNaem : function(){
    	var me = this;
    	
    	FHD.ajax({
            url: __ctxPath + '/findAssessPlanName.f',
            callback: function (data) {
                if (data && data.success) {
                	me.assessPlanName = data.assessPlanName;
                }
            }
        });
    },
    
    assessDownloadFile : function(fileId){
        if(fileId != ''){
        	window.location.href=__ctxPath+"/sys/file/download.do?id="+fileId;
        }else{
        	FHD.notification('该样本没有上传附件!',FHD.locale.get('fhd.common.prompt'));
        }
    },
    
    initComponent: function () {
        var me = this;        
        FHD.ajax({
            url: __ctxPath + '/assess/quaassess/findAssessFileId.f',
            params: {
            	executionId : me.executionId
            },
            callback: function (data) {
                if (data && data.success) {
                	me.assessFileId = data.assessFileId;
                } 
            }
        });
        
        me.id = 'quaAssessCardId';
    //  me.quaAssessOpe = Ext.widget('quaAssessOpe',{quaAssessMan : me.quaAssessMan, border : false});
        me.quaAssessGrid = Ext.widget('quaAssessGridSecrecy',{
        businessId : me.businessId, executionId : me.executionId, border : false,array : me.queryArray(), businessId:me.businessId});
        me.quaAssessGrid.reloadData(me.executionId);
        
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.quaAssessGrid],
            
            tbar :{
            	items : [
						
//						,{
//							text : '',
//							id : 'quaAssessCardNavId'
//						}
						
						{
							text : '',
							id : 'quaAssessCardNavId2'
						}
            	]
            },
            
			bbar : {
				items : [
					'->', {
						text : '添加',
						id : 'assessQuaAddId',
						iconCls : 'icon-add',
						handler : function() {
							me.addAllShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
									schm:'security',
								    border:false,
									state : '2',
									setLoginDept : true,
									executionId : me.executionId,
									assessPlanId : me.businessId,
									hiddenSaveBtn:true,
									userValidate : function(){
									return true;
									},
								callback:function(data){
					if(data){
	            		me.addAllShortForm.body.unmask();
		            	//Ext.MessageBox.alert('添加信息','添加成功');
		            	me.formwindow.close();
		            	me.quaAssessGrid.reloadData(me.executionId);
	            	}}
							});
							
					    	me.formwindow = new Ext.Window({
								layout:'fit',
								iconCls: 'icon-show',//标题前的图片
								modal:true,//是否模态窗口
								collapsible:true,
								width:900,
								height:400,
								title : '风险信息添加',
								maximizable:true,//（是否增加最大化，默认没有）
								constrain:true,
								items : [me.addAllShortForm],
								buttons: [
									{
										text: '保存',
										handler:function(){
											var isAdd = me.addAllShortForm.save(me.addAllShortForm.callback);
											if(isAdd){
												me.addAllShortForm.body.mask("保存中...","x-mask-loading");
											}
										}
									},
					    			{
					    				text: '关闭',
					    				handler:function(){
					    					me.formwindow.close();
					    				}
					    			}
					    		]
							});
							me.formwindow.show();
						}
					},
					{
						text : '提交',
						id : 'quaAssessSubmitId',
						iconCls : 'icon-operator-submit',
						handler : function() {
							me.quaAssessShow();
						}
					}
				]
			}
        });
        
        me.callParent(arguments);        
        
   
        
        me.quaAssessMan.quassessManTitle.on('resize',function(p){
        	if(Ext.getCmp('QuaAssessManId').winId != null){
		    	if(me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).getHeight() - 58);
		    	}else if(me.quaAssessMan.quassessManTitle.collapsed == 'top'){
		    		me.setHeight(Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).getHeight() - 58);
		    	}else if(!me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).getHeight() - 118);
		    	}
        	}else{
        		if(me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 28);
		    	}else if(me.quaAssessMan.quassessManTitle.collapsed == 'top'){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 28);
		    	}else if(!me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 85);
		    	}
        	}
	    });
        
        me.on('resize',function(p){
        	if(Ext.getCmp('QuaAssessManId').winId != null){
		    	if(me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).getHeight() - 58);
		    	}else if(me.quaAssessMan.quassessManTitle.collapsed == 'top'){
		    		me.setHeight(Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).getHeight() - 58);
		    	}else if(!me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).getHeight() - 118);
		    	}
        	}else{
        		if(me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 28);
		    	}else if(me.quaAssessMan.quassessManTitle.collapsed == 'top'){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 28);
		    	}else if(!me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 85);
		    	}
		    
        	}
	    	
	    });
    }
});