Ext.define('FHD.view.icm.assess.AssessPlanExecuteCardPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.assessplanexecutecardpanel',
    
    activeItem: 0,
    border:false,
    requires: [
       'FHD.view.icm.assess.form.AssessResultForm',
       'FHD.view.icm.assess.form.AssessPlanDraftForm',
       'FHD.view.icm.assess.component.AssessDefectEditGrid'
    ],
    
    tbar: {
        name: 'icm_assessplan_execute_card_topbar',
        items: [
	        {
	            text: '测试底稿',
	            iconCls: 'icon-001',
	            name: 'icm_assessplan_execute_card_details_btn_top',
	            handler: function () {
	            	/*
	            	var assessplanexecutecardpanel = this.up('assessplanexecutecardpanel');
	            	if(assessplanexecutecardpanel){
	            		assessplanexecutecardpanel.setBtnState(0);
	            		assessplanexecutecardpanel.navBtnHandler(this.up('panel'), 0);
	            	}
	            	*/
	            }
	        },
	        '<img src="'+__ctxPath+'/images/icons/show_right.gif">',
	        {
	            text: '结果分析',
	            iconCls: 'icon-002',
	            name: 'icm_assessplan_execute_card_range_btn_top',
	            handler: function () {
	            	/*
	            	var assessplanexecutecardpanel = this.up('assessplanexecutecardpanel');
	            	if(assessplanexecutecardpanel){
	            		assessplanexecutecardpanel.setBtnState(1);
	            		assessplanexecutecardpanel.navBtnHandler(this.up('panel'), 1);
	            	}
	            	*/
	            }
	        },
	        '<img src="'+__ctxPath+'/images/icons/show_right.gif">',
	        {
	            text: '缺陷清单',
	            iconCls: 'icon-003',
	            name: 'icm_assessplan_execute_card_defectlist_btn_top',
	            handler: function () {
	            	/*
	            	var assessplanexecutecardpanel = this.up('assessplanexecutecardpanel');
	            	if(assessplanexecutecardpanel){
	            		assessplanexecutecardpanel.setBtnState(2);
	            		assessplanexecutecardpanel.navBtnHandler(this.up('panel'), 2);
	            	}
	            	*/
	            }
	        }
	    ]
    },
    bbar: {
    	name: 'icm_assessplan_execute_card_bbar',
        items: [
	        '->',
	        {
	            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.back"),//上一步按钮
	            name: 'icm_assessplan_execute_card_pre_btn' ,
	            iconCls: 'icon-operator-back',
	            handler: function () {
	            	var assessplanexecutecardpanel = this.up('assessplanexecutecardpanel');
	            	if(assessplanexecutecardpanel){
	            		assessplanexecutecardpanel.back();
	            	}
	            }
	        }, 
	        {
	            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.last"),//下一步按钮
	            name: 'icm_assessplan_execute_card_next_btn' ,
	            iconCls: 'icon-operator-next',
	            handler: function () {
	            	var assessplanexecutecardpanel = this.up('assessplanexecutecardpanel');
	            	if(assessplanexecutecardpanel){
	            		assessplanexecutecardpanel.last();
	            	}
	            }
	        }, 
	        {
	            text: FHD.locale.get("fhd.common.save"),//保存按钮
	            name: 'icm_assessplan_execute_card_finish_btn' ,
	            iconCls: 'icon-control-stop-blue',
	            handler: function () {
	            	var assessplanexecutecardpanel = this.up('assessplanexecutecardpanel');
	            	if(assessplanexecutecardpanel){
	            		assessplanexecutecardpanel.finish();
	            	}
	            }
	        }, 
	        {
	            text: FHD.locale.get("fhd.common.submit"),//提交按钮
	            name: 'icm_assessplan_execute_card_submit_btn' ,
	            iconCls: 'icon-operator-submit',
	            handler: function () {
	            	var assessplanexecutecardpanel = this.up('assessplanexecutecardpanel');
	            	if(assessplanexecutecardpanel){
	            		assessplanexecutecardpanel.submit();
	            	}
	            }
	        }
	    ]
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        me.assessresultform = Ext.widget('assessresultform',{
        	executionId:me.executionId,
        	businessId:me.businessId,
        	editflag:me.editflag
        });
        
        me.assessplandraftform = Ext.widget('assessplandraftform',{
        	businessId:me.businessId,
        	executionId:me.executionId,
        	editflag:me.editflag
        });
        
        me.assessdefecteditgrid = Ext.widget('assessdefecteditgrid',{
        	businessId:me.businessId,
        	executionId:me.executionId,
        	editflag:me.editflag,
        	pagable:false
        });
        
        me.assessGuidelinesGrid = Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesShowGrid',{
			columnWidth:1/1,
			assessPlanId:me.businessId
		});
        
        Ext.applyIf(me, {
            items: [
                me.assessresultform,
                me.assessplandraftform,
                Ext.create('Ext.panel.Panel',{
                	layout:{
                		type:'vbox',
                		align:'stretch'
                	},
                	autoScroll:true,
                	items:[
						Ext.create('Ext.form.FieldSet',{
							title:'缺陷列表',
							layout:{
		                		type:'vbox',
		                		align:'stretch'
		                	},
		                	items:[me.assessdefecteditgrid]
						}),
						Ext.create('Ext.form.FieldSet',{
							title:'评价标准查看',
							layout:{
		                		type:'vbox',
		                		align:'stretch'
		                	},
		                	collapsed : true,
		        			collapsible : true,
		                	items:[me.assessGuidelinesGrid]
						})
						
                	]
                })
            ]
        });
        
        me.callParent(arguments);
        
        me.loadData(me.businessId, true);
    },
    /**
     * 上一步按钮事件
     */
    back:function(){
    	var me = this;
    	
    	var activePanel = me.getActiveItem();
    	var items = me.items.items;
        var index = Ext.Array.indexOf(items, activePanel);
        //保存数据
    	me.saveActiveItemData(index);
		//设置tbar导航
		me.navBtnHandler(me,index-1);
		//设置bbar按钮状态
		me.setBtnState(index-1);
    },
    /**
     * 下一步按钮事件
     */
    last:function(){
    	var me = this;
    	
    	var activePanel = me.getActiveItem();
    	var items = me.items.items;
        var index = Ext.Array.indexOf(items, activePanel);
        
        if(0 == index){
    		//保存数据
        	me.saveActiveItemData(index);
        	if(me.assessresultform.validateAssessResultGrid()){
        		//下一步验证通过后，设置[结果分析]和[缺陷清单]按钮可用
        		me.down('[name=icm_assessplan_execute_card_range_btn_top]').setDisabled(false);
        		me.down('[name=icm_assessplan_execute_card_defectlist_btn_top]').setDisabled(false);
        		//设置tbar导航
        		me.navBtnHandler(me,index+1);
        		//设置bbar按钮状态
        		me.setBtnState(index+1);
        	}
        }else{
        	//保存数据
        	me.saveActiveItemData(index);
    		//设置tbar导航
    		me.navBtnHandler(me,index+1);
    		//设置bbar按钮状态
    		me.setBtnState(index+1);
        }
    },
    /**
     * 完成按钮事件
     */
    finish:function(){
    	var me = this;
    	
    	var activePanel = me.getActiveItem();
        var items = me.items.items;
        var index = Ext.Array.indexOf(items, activePanel);
        //保存数据
    	me.saveActiveItemData(index);
		//设置tbar导航
		//me.navBtnHandler(me,index);
		//设置bbar按钮状态
		//me.setBtnState(index);
    },
    /**
     * 提交按钮事件
     */
    submit:function(){
    	var me = this;
    	
    	//验证每条缺陷必须选择整改责任部门、缺陷描述、缺陷级别、缺陷类型
		var validateFlag=false;
 		var message = '';
 		
 		var count = me.assessdefecteditgrid.store.getCount();
		for(var i=0;i<count;i++){
			var item = me.assessdefecteditgrid.store.data.get(i);
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
    	
        //保存缺陷清单列表
        var rows = me.assessdefecteditgrid.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		FHD.ajax({
			url:__ctxPath + '/icm/defect/saveDefects.f',
            params: {
               jsonString:Ext.encode(jsonArray),
               assessPlanId:me.businessId
            },
            callback: function (data) {
            	if(data){
            		Ext.MessageBox.show({
			            title: '提示',
			            width: 260,
			            msg: '提交后将不能修改，您确定要提交么?',
			            buttons: Ext.MessageBox.YESNO,
			            icon: Ext.MessageBox.QUESTION,
			            fn: function (btn) {
			                if (btn == 'yes') {
			            		//所有按钮不可用，提交成功后跳转到列表
			                	me.down('[name=icm_assessplan_execute_card_pre_btn]').setDisabled(true);
			                    me.down('[name=icm_assessplan_execute_card_next_btn]').setDisabled(true);
			                    me.down('[name=icm_assessplan_execute_card_finish_btn]').setDisabled(true);
			                    me.down('[name=icm_assessplan_execute_card_submit_btn]').setDisabled(true);
			                    
			            		//提交工作流
			                	FHD.ajax({
			            	        url: __ctxPath + '/icm/assess/assessPlanExecute.f',
			            	        async:false,
			            	        params: {
			            	        	businessId: me.businessId,
			            	        	executionId: me.executionId
			            	        },
			            	        callback: function (data) {
			            	        	if(data){
			            	        		var assessplanbpmsix = me.up('assessplanbpmsix');
			            	            	if(assessplanbpmsix.winId){
			            	            		Ext.getCmp(assessplanbpmsix.winId).close();
			            	            	}else{
			    								//单点登录执行待办关闭window
			    								FHD.closeWindow();
			    							}
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
    //保存指定的cardItem数据
    saveActiveItemData:function(index){
    	var me=this;
    	
    	if(0 == index){
 	    	//测试结果填写:保存评价流程form和评价内容grid
            me.assessresultform.saveData(me);
 	    }else if(1 == index){
 	    	//结果分析
            me.assessplandraftform.saveResultAnalysis();
 	    }else if(2 == index){
 	    	//缺陷清单
            me.assessdefecteditgrid.saveData();
 	    }
    },
    /**
     * 设置导航按钮的选中或不选中状态
     * @param index,要激活的面板索引
     */
    setBtnState: function (index) {
    	var me=this;
    	
        var k = 0;
        var topbar = me.down('[name=icm_assessplan_execute_card_topbar]');
        var btns = topbar.items.items;
        for (var i = 0; i < btns.length; i++) {
            var item = btns[i];
            if (item.pressed != undefined) {
                if (k == index) {
                    item.toggle(true);
                } else {
                    item.toggle(false);
                }
                k++;
            }
        }
    },
    /**
     * 设置导航按钮的事件函数
     * @param {panel} cardPanel cardpanel面板
     * @param index 面板索引值
     */
    navBtnHandler: function (cardPanel, index) {
 	   	var me = this;
 	   	
 	    cardPanel.setActiveItem(index);
 	    me.navBtnState();
 	    if(0 == index){
 	    	//测试结果填写刷新
            me.assessresultform.loadData(me.businessId,me.editflag);
 	    }else if(1 == index){
 	    	//结果分析刷新
            me.assessplandraftform.loadData(me.businessId,me.editflag);
 	    }else if(2 == index){
 	    	//缺陷清单刷新
            me.assessdefecteditgrid.loadData(me.businessId,me.editflag);
 	    }
    },
    /**
     * 设置上一步和下一步按钮的状态
     */
    navBtnState:function(){
    	var me = this;
    	
    	var layout = me.getLayout();
    	me.down('[name=icm_assessplan_execute_card_pre_btn]' ).setDisabled(!layout.getPrev());
        me.down('[name=icm_assessplan_execute_card_next_btn]' ).setDisabled(!layout.getNext());
        me.down('[name=icm_assessplan_execute_card_finish_btn]').setDisabled(false);
        me.down('[name=icm_assessplan_execute_card_submit_btn]' ).setDisabled(layout.getNext());
    },
    /**
     * 初始化tbar和bbar按钮状态
     */
    setInitBtnState:function(){
    	var me=this;
    	
    	me.setBtnState(0);
    	me.navBtnHandler(me,0);
    	//初始化设置[结果分析]和[缺陷清单]按钮不可用
		me.down('[name=icm_assessplan_execute_card_range_btn_top]').setDisabled(true);
		me.down('[name=icm_assessplan_execute_card_defectlist_btn_top]').setDisabled(true);
    },
    loadData:function(businessId,editflag){
    	var me=this;
    	
    	me.businessId = businessId;
    	me.editflag = editflag;
    	me.setInitBtnState();
    },
    reloadData:function(){
    	var me=this;
    	
    }
});