/**
 * @authority  jia.song
 * @description 风险整理后  风险管理部主管审批
 * @date   2017-5-8
 */

Ext.define('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyGridForSubmitForSecurity', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.newrisktidygridforsubmitforsecurity',
    
    
    requires: [
				 'FHD.view.risk.assess.newAgainRiskTidy.RiskTidyAssessGrid',
				 'FHD.view.risk.assess.quaAssess.QuaAssessEdit',
				 'FHD.view.risk.assess.newAgainRiskTidy.RiskTidyDeptApproveGrid'
            ],
    
    riskDatas : null,
    
  	
    //导出grid列表
    exportChart:function(businessId, sheetName, exportFileName){
    	var me=this;
    	var businessId;
    	var type;
    	var typeId;
    	me.headerDatas = [];
    	if(me.gridParams){
    		if(me.gridParams.assessPlanId){
    			businessId = me.gridParams.assessPlanId
    		}
    		if(me.gridParams.type){
    			type = me.gridParams.type;
    		}
    		if(me.gridParams.typeId){
    			typeId = me.gridParams.typeId;
    		}
    	}
    	var items = me.columns;
			Ext.each(items,function(item){
				if(!item.hidden&&""!=item.dataIndex){
				var value = {};
				value['dataIndex'] = item.dataIndex;
            	//value['text'] = item.text;
				if('riskIcon'==item.dataIndex){
					value['text'] = '风险等级';
				}else{
					value['text'] = item.text;
				}
            	me.headerDatas.push(value);
				}
			});
		
    	sheetName = 'exportexcel';
    	//exportFileName = '评估结果预览数据';
    	window.location.href = __ctxPath + "/assess/riskTidy/exportrisktidygrid.f?businessId="+businessId+"&exportFileName="
    							+""+"&sheetName="+sheetName+"&headerData="+Ext.encode(me.headerDatas)
    							+"&type="+type+"&typeId="+typeId;
    },
    edit : function(scoreObjectId, riskId){
    	var me = this;
    	me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
			height : 500,
			riskId : riskId
		});
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			width:800,
			height:500,
			title : '风险详细信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.detailAllForm]
		});
		me.formwindow.show();
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.riskDatas = [];
        //me.id = 'riskTidyGridId';
        var array = [];
        Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/findDimCols.f',
		    params: {
		    	assessPlanId : me.businessId
            },
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
		        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.1,editor:{
						xtype:'numberfield',
						allowBlank:false,
						minValue: 1,  
						maxValue: 5,
						allowDecimals: true, // 允许小数点 
						nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
						//hideTrigger: true,  //隐藏上下递增箭头
						keyNavEnabled: true,  //键盘导航
						mouseWheelEnabled: true,  //鼠标滚轮
						step:1
		        	}});
		        });
		    }});
        
        var cols = [
			{
				dataIndex:'id',
				hidden:true
			},{
	            header: "上级风险",
	            dataIndex: 'riskParentName',
	            sortable: true,
	            //align: 'center',
	            flex:.3,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
	            	return value;
	            }
     			
	        },{
				 dataIndex:'objectId',
					hidden:true
		    },{
	            header: "风险名称",
	            dataIndex: 'riskName',
	            sortable: true,
	            //align: 'center',
	            flex:.5,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
	            	return "<a href=\"javascript:void(0);\">"+value+"</a>";
     			},
     			listeners:{
	        		
//	        		click:{
//	        			fn:function(g,d,i){
//	        				var riskId = me.getSelectionModel().getSelection()[0].data.riskId;
//	        				var assessPlanId = me.getSelectionModel().getSelection()[0].data.assessPlanId;
//	        				var scoreObjectId = me.getSelectionModel().getSelection()[0].data.scoreObjectId;
//	        				if(me.riskTidyMan.winId != null){
//	        					me.edit(riskId, assessPlanId, scoreObjectId);
//	        				}else{//评估结果列表入口
//	        					//单击风险名称，显示风险基本信息组件
//	        					Ext.Ajax.request({
//	        					    url: __ctxPath + '/assess/quaAssess/findDimCols.f',
//	        					    params: {
//	        					    	assessPlanId : assessPlanId
//	        			            },
//	        					    async:  false,
//	        					    success: function(response){
//	        					        var text = response.responseText;
//	        					        var assessEamil = Ext.JSON.decode(text).assessEamil;
//	        					        if(me.riskTidyMan.winId != null){
//	        					        	me.edit(riskId, assessPlanId, scoreObjectId);
//	        					        }else{
//	        					        	array = new Array();
//		        					        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
//		        					        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.4});
//		        					        });
//		        					        
//		        					        me.riskTidyAssessGrid = Ext.widget('riskTidyAssessGrid',{array : array});
//		        					        me.riskTidyAssessGrid.store.proxy.url = __ctxPath + '/assess/riskTidy/findRiskByAssessPlanIdAndRiskId.f';
//		        					    	me.riskTidyAssessGrid.store.proxy.extraParams.assessPlanId = assessPlanId;
//		        						    me.riskTidyAssessGrid.store.proxy.extraParams.riskId = riskId;
//		        						    me.riskTidyAssessGrid.store.load();
//		        					        
//		        					        me.riskTidyDeptApproveGrid = Ext.widget('riskTidyDeptApproveGrid');
//		        					        me.riskTidyDeptApproveGrid.store.proxy.url = __ctxPath + '/assess/riskTidy/findRiskByDeptRiskIdea.f';
//		        					    	me.riskTidyDeptApproveGrid.store.proxy.extraParams.assessPlanId = assessPlanId;
//		        						    me.riskTidyDeptApproveGrid.store.proxy.extraParams.scoreObjectId = scoreObjectId;
//		        						    me.riskTidyDeptApproveGrid.store.load();
//		        					        
//		        					        me.assessGridSet = {
//		        			        				xtype : 'fieldset',
//		        			        				collapsible : true,
//		        			        				title : '评估详细',
//		        			        				margin : '10 10 10 10',
//		        			        				items : [me.riskTidyAssessGrid]
//		        			        		};
//		        					        
//		        					        me.riskTidyDeptApproveGridSet = {
//		        			        				xtype : 'fieldset',
//		        			        				collapsible : true,
//		        			        				title : '部门风险管理员意见',
//		        			        				margin : '10 10 10 10',
//		        			        				items : [me.riskTidyDeptApproveGrid]
//		        			        		};
//		        					        
//		        					        me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
//		        					        	autoScrollas : true,
//												type:'re',	//如果是re,上级风险只能选择叶子节点
//									    		border:false,
//								    			riskId : riskId
//								    		});
//								    		me.detailAllForm.add(me.assessGridSet);
//								    		me.detailAllForm.add(me.riskTidyDeptApproveGridSet);
//								    		
//		    						    	me.formwindow = new Ext.Window({
//		    									layout:'fit',
//		    									iconCls: 'icon-show',//标题前的图片
//		    									modal:true,//是否模态窗口
//		    									collapsible:true,
//		    									width:800,
//		    									height:500,
//		    									title : '风险详细信息查看',
//		    									maximizable:true,//（是否增加最大化，默认没有）
//		    									constrain:true,
//		    									items : [me.detailAllForm]//me.quaAssessEdit
//		    								});
//		    						    	
//		    								me.detailAllForm.on('resize',function(p){
//		    						        	me.detailAllForm.setHeight(me.formwindow.getHeight()-20);
//		    							   	});
//		    							   	me.formwindow.show();
//	        					        }
//	        					    }
//	        					});
//	        				}
//        			}
//				}
			}
	        }, 
			{
				header:"评估人数",
				dataIndex:'assessEmpSum',
				flex:.1,
				hidden:false
			}
        ];
        for(var i = 0 ; i < array.length; i++){
        	cols.push(array[i]);
        }
        cols.push({
			header:"综合分",
			dataIndex:'riskScore',
			flex : .1,
			hidden : false
		});
       /* 
        for(var i = 0 ; i < array.length; i++){
        	cols.push(array[i]);
        }
        */
        cols.push({
            dataIndex: 'riskIcon',
            sortable: true,
            width:40,
        	cls: 'grid-icon-column-header grid-statushead-column-header',
        	menuDisabled:true,
            renderer:function(value,metaData,record,colIndex,store,view) {
            	/*
            	var value = {};
            	value['riskId'] = record.get('riskId');
            	value['templateId'] = record.get('templateId');
            	value['rangObjectDeptEmpId'] = '';
            	me.riskDatas.push(value);
            	*/
            	return "<div style='width: 23px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
            	+ record.get('riskIcon') + "'/>";
 			}
        });
		/*
        cols.push({
			header: "状态",
			dataIndex:'riskStatus',
			flex:.1,           
			renderer:function(value,metaData,record,colIndex,store,view) {
            	return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
            	+ value + "'/>";
 			}
		});
		*/
    	Ext.apply(me,{
        	region:'center',
        	layout: 'fit',
        	type: 'editgrid',
        	url : __ctxPath + '/assess/riskTidy/findRiskReByRiskForSecurity.f',//__ctxPath + "/app/view/risk/assess/riskTidy/list.json",
        	extraParams : me.extraParams,
        	cols:cols,
        	autoScroll:true,
        	border: false,
		    checked: true,
		    pagable : false,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : true,
		    storeAutoLoad:false,
		    tbarItems:[
               '<span style="font-size:12px;font-weight:bold;color: #15498b;margin-right:0">风险总数:</span>'+ 
				"<span id='risk-tidy-card-num" + me.id + "'>" + 0 + "</span>",
               {
   				text: '导出',
   				iconCls: 'icon-ibm-action-export-to-excel',
   				handler:function(){
   					me.exportChart();
   				}
   			}]
        });
        
    	
    	
        me.callParent(arguments);

        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3]);
        	var count = me.store.getCount();
	     	Ext.get('risk-tidy-card-num' + me.id).setHTML(count);
        });
    }

});