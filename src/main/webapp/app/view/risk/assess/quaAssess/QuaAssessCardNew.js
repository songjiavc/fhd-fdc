/**
 * 
 * 定性评估卡片面板
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessCardNew',{
	extend: 'FHD.ux.CardPanel',
    alias: 'widget.quaAssessCardNew',
    
    requires: [
    				'FHD.view.risk.assess.quaAssess.QuaAssessGrid',
				 'FHD.view.risk.assess.quaAssess.QuaAssessOpe',
                 
				 'FHD.view.risk.assess.quaAssess.QuaAssessShowGrid',
				 'FHD.view.risk.assess.quaAssess.QuaAssessSubmit',
				 'FHD.view.risk.assess.AssessApproveSubmit',
				 'FHD.view.risk.assess.quaAssess.QuaAssessAdd'
              ],
    
    quaAssessIdentification : function(){
    	var me = this;
    	
		Ext.getCmp('quaAssessCardId').showQuaAssessGrid();
		
		Ext.getCmp('riskBs').toggle(true);
		Ext.getCmp('riskBs').setDisabled(false);
		
		Ext.getCmp('riskPg').setDisabled(true);
		Ext.getCmp('riskPg').toggle(false);
		
		Ext.getCmp('upbzId').disable();
		Ext.getCmp('nextId2').enable();
		Ext.getCmp('assessQuaAddId').enable();
		Ext.getCmp('quaAssessSubmitId').disable();
		Ext.getCmp('showId').disable();
    },

    quaAssessRisk : function(){
    	var me = this;
    	
    	me.showQuaAssessOpe();
		me.quaAssessOpe.load( me.getQuaAssessGridRiskDatas());
//		Ext.getCmp('nextId2').disable();
//		Ext.getCmp('assessQuaAddId').disable();
		Ext.getCmp('quaAssessSubmitId').enable();
		Ext.getCmp('showId').enable();
		
//		Ext.getCmp('riskBs').toggle(false);
//		Ext.getCmp('riskBs').setDisabled(true);
		
		Ext.getCmp('riskPg').setDisabled(false);
		Ext.getCmp('riskPg').toggle(true);
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
		        array = new Array();
		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
		        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.5});
		        });
		        
		        me.quaAssessShowGrid = Ext.widget('quaAssessShowGrid',
		        		{url:__ctxPath + '/assess/quaAssess/findAssessShowGrid.f', array : array, businessId:me.businessId});
				
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
								var count = 0;
								for(var i = 0; i < me.quaAssessOpe.riskDatas.length; i++){
									if(document.getElementById(me.quaAssessOpe.riskDatas[i].riskId + 'panelImg').src.indexOf('error') != -1){
										Ext.MessageBox.alert('提示信息','未完成评价任务,请确认任务全部评价结束后再次提交');
										count++;
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
													if(count == 0){
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
  		me.getLayout().setActiveItem(me.items.items[0]);
//  		Ext.getCmp('riskBs').toggle(false);
  		Ext.getCmp('riskPg').toggle(true);
  	},
  	
  	showQuaAssessGrid : function(){
  		var me = this;
  		me.getLayout().setActiveItem(me.items.items[0]);
//  		Ext.getCmp('riskBs').toggle(true);
  		Ext.getCmp('riskPg').toggle(false);
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
        me.quaAssessOpe = Ext.widget('quaAssessOpe',{quaAssessMan : me.quaAssessMan, border : false});
        me.quaAssessGrid = Ext.widget('quaAssessGrid',{
        	businessId : me.businessId, executionId : me.executionId, border : false});
        //me.quaAssessGrid.store.load();
        me.quaAssessGrid.reloadData(me.executionId);
        Ext.apply(me, {
        	border:false,
        	activeItem : 0,
            items: [me.quaAssessOpe, me.quaAssessGrid],
            
            tbar :{
            	items : [
						 '->'
						,{
							text : '',
							id : 'quaAssessCardNavId2'
						},{
							text : '评价标准下载',
							id : 'assessQuaDownloadFileId',
							iconCls : 'icon-download-min',
							handler : function() {
								if(me.assessFileId == undefined){
									Ext.MessageBox.alert('提示信息','此模板附件未找到');
								}
								else if(me.assessFileId == 'null'){
									Ext.MessageBox.alert('提示信息','此模板还未上传附件');
								}else{
									me.assessDownloadFile(me.assessFileId);
								}
							}
						} 
            	]
            },
            
			bbar : {
				items : [
					
					'->',
					{
						text : '预览',
						id : 'showId',
						iconCls : 'icon-page-green',
						handler : function() {
							me.quaAssessShow();
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
        //查询评估的风险进行打分 吉志强2017年4月12日13:44:58 ///////////  begin   /////////////
      
        FHD.ajax({
            url:  __ctxPath + '/assess/quaAssess/findAssessGrid.f',
            params: {
            	assessPlanId : me.businessId,
            	executionId : me.executionId
            },
            callback: function (data) {
        	    var riskDatas = [];
            	for(var i=0;i<data.length;i++){
            		var value = {};
                	var record = data[i];
                	value['riskId'] = record.riskId;
                	value['templateId'] = record.templateId;
                	value['rangObjectDeptEmpId'] = record.rangObjectDeptEmpId;
                	value['riskName'] = record.riskName;
                	value['objectId'] = record.objectId;
                	if(riskDatas.length>0){//将me.riskDatas中所有riskId取出放进riskIdArray数组
                		var riskIdArray = [];
                		for(var i=0;i<riskDatas.length;i++){
                			riskIdArray.push(riskDatas[i].riskId);
                		}
                		if(!(Ext.Array.contains(riskIdArray,value.riskId))){//判断数组中是否已经存在value值，不存在则保存
                			riskDatas.push(value);
                		}
                	}else{
    	            	riskDatas.push(value);
                	}
            	}
            	 me.quaAssessOpe.load(riskDatas);
            }
        });
        /////////////////////  end   /////////////
		
		
        
        
        
        
        
        
        
        
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
		    	if(Ext.getCmp(me.quaAssessOpe.onId + 'panel') != undefined){
		    		Ext.getCmp(me.quaAssessOpe.onId + 'panel').setWidth(Ext.getCmp(me.quaAssessMan.winId).getWidth() - 70);
		    	}
        	}else{
        		if(me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 28);
		    	}else if(me.quaAssessMan.quassessManTitle.collapsed == 'top'){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 28);
		    	}else if(!me.quaAssessMan.quassessManTitle.collapsed){
		    		me.setHeight(Ext.getCmp('QuaAssessManId').getHeight() - 85);
		    	}
		    	if(Ext.getCmp(me.quaAssessOpe.onId + 'panel') != undefined){
		    		Ext.getCmp(me.quaAssessOpe.onId + 'panel').setWidth(Ext.getCmp('QuaAssessManId').getWidth() - 70);
		    	}
        	}
	    	
	    });
    }
});