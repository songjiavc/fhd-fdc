/**
 * 
 * 风险整理表格
 */

Ext.define('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.newRiskTidyGrid',
    requires: [
		'FHD.view.risk.assess.newAgainRiskTidy.RiskTidyAssessGrid',
		'FHD.view.risk.assess.quaAssess.QuaAssessEdit',
		'FHD.view.risk.assess.newAgainRiskTidy.RiskTidyDeptApproveGrid'
    ],
    riskDatas : null,
   	edit : function(scoreObjectId, riskId){
    	var me = this;
    	
    	me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
			height : 500,
			riskId : riskId,
			objectId:scoreObjectId
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
   	
    
    onSave:function(data){
    	var me = this;
    	var rows = me.store.getModifiedRecords();
    	var objectId = rows[0].data.objectId;
    	var obj = {
    		score:data.value,
    		scoreDimId:data.field,
    		objectId:objectId
    	};
		FHD.ajax({
			async:false,
			params: {
		       	assessPlanId : me.riskTidyMan.businessId,
		       	params : Ext.encode(obj)
		   	},
		   	url : __ctxPath + '/assess/riskTidy/riskTidySaveAssess.f',
		   	callback: function (ret) {
		   		me.store.load();
		   	}
		});
    },
    
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
    
    delGrid:function(){
  		var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	if(selection.length == 0){
    		FHD.notification('请选中后删除',FHD.locale.get('fhd.common.prompt'));
    		return;
    	}
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {
					me.riskTidyMan.body.mask("删除中...","x-mask-loading");
					var ids = [];
					for(var i=0;i<selection.length;i++){
						ids.push(selection[i].get('objectId'));
					}
					FHD.ajax({
						url : __ctxPath + '/assess/riskTidy/delRiskRbsSf2.f',
						params: {
			              	assessPlanId : me.riskTidyMan.businessId,
			              	ids : ids
						},
						callback : function(data){
							if(data){
								me.riskTidyMan.body.unmask();
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								me.store.load();
								//me.riskTidyMan.assessTree.processTree.extraParams.ids = data.treeIds;
								me.riskTidyMan.assessTree.reloadData();
								/*
									me.riskTidyMan.riskTidyTab.riskCategoryPanel.store.load();
				   	   			    me.riskTidyMan.riskTidyTab.orgTreeGrid.store.load();
					   	   			me.riskTidyMan.riskTidyTab.kpiprocessPanel.store.load();
	   	   			            	me.riskTidyMan.riskTidyTab.processPanel.store.load();
	   	   			            */
							}
						}
					});
				}
			} 
		});
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
		    	assessPlanId : me.riskTidyMan.businessId
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
					click : {
	        			fn:function(g,d,i){
	        				var scoreObjectId = me.getSelectionModel().getSelection()[0].data.objectId;
	        				var assessPlanId = me.riskTidyMan.businessId;
	        				var riskId = me.getSelectionModel().getSelection()[0].data.riskId;
	        				me.edit(scoreObjectId, assessPlanId,riskId);
	        			}
					}
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
            	url : __ctxPath + '/assess/riskTidy/findRiskReByRisk.f',//__ctxPath + "/app/view/risk/assess/riskTidy/list.json",
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
       			},{
    				text : '删除',
    				iconCls : 'icon-del',
    				name:'riskTidyGrid_delete',
    				handler : function(){
    					me.delGrid();
    				},
    				scope : this
    			}, {
    					text : '初始化统计',
    					iconCls : 'icon-cog',
    					handler : function() {
    						Ext.MessageBox.show({
    				    		title : '提示',
    				    		width : 260,
    				    		msg : '确认重新统计吗？',
    				    		buttons : Ext.MessageBox.YESNO,
    				    		icon : Ext.MessageBox.QUESTION,
    				    		fn : function(btn) {
    				    			if (btn == 'yes') {//确认
    				    				Ext.Ajax.timeout = 1000000;
    									me.riskTidyMan.body.mask("重新汇总中...","x-mask-loading");
    									FHD.ajax({
    				   			            url: __ctxPath + '/assess/riskTidy/afreshSummarizing.f' ,
    										params : {
    											assessPlanId : me.riskTidyMan.businessId
    										},
    				   			            callback: function (data) {
    				   			            	if(data){
    				   			            		me.riskTidyMan.body.unmask();
    				   	   			            	me.store.load();
    				   	   			            	var tree = me.riskTidyMan.assessTree;
    				   	   			            	//tree.processTree.extraParams.ids = data.ids;
    				   	   			            	tree.reloadData();
    				   	   			            	/*
    				   	   			            	me.riskTidyMan.riskTidyTab.riskCategoryPanel.store.load();
    							   	   			    me.riskTidyMan.riskTidyTab.orgTreeGrid.store.load();
    								   	   			me.riskTidyMan.riskTidyTab.kpiprocessPanel.store.load();
    				   	   			            	me.riskTidyMan.riskTidyTab.processPanel.store.load();
    				   	   			            	*/
    				   			            	}
    				   			            	
    				   			            }
    				   			        });
    				    			}
    				    		}
    				    	});
    					}
    				},
    				{
    					text : '重新计算',
    					iconCls : 'icon-cog',
    					handler : function() {
    						Ext.MessageBox.show({
    				    		title : '提示',
    				    		width : 260,
    				    		msg : '确认要重新计算吗？',
    				    		buttons : Ext.MessageBox.YESNO,
    				    		icon : Ext.MessageBox.QUESTION,
    				    		fn : function(btn) {
    				    			if (btn == 'yes') {//确认
    				    				Ext.Ajax.timeout = 1000000;
    									me.riskTidyMan.body.mask("重新计算中...","x-mask-loading");
    									FHD.ajax({
    				   			            url: __ctxPath + '/assess/riskTidy/afreshCalu.f' ,
    										params : {
    											assessPlanId : me.riskTidyMan.businessId
    										},
    				   			            callback: function (data) {
    				   			            	if(data){
    				   			            		me.riskTidyMan.body.unmask();
    				   	   			            	me.store.load();
    				   	   			            	var tree = me.riskTidyMan.assessTree;
    				   	   			            //	tree.processTree.extraParams.ids = data.ids;
    				   	   			            	tree.reloadData();
    				   	   			            	/*
    				   	   			            	me.riskTidyMan.riskTidyTab.riskCategoryPanel.store.load();
    							   	   			    me.riskTidyMan.riskTidyTab.orgTreeGrid.store.load();
    								   	   			me.riskTidyMan.riskTidyTab.kpiprocessPanel.store.load();
    				   	   			            	me.riskTidyMan.riskTidyTab.processPanel.store.load();
    				   	   			            	*/
    				   			            	}
    				   			            	
    				   			            }
    				   			        });
    				    			}
    				    		}
    				    	});
    					}
    				}
    			]
            });
        me.callParent(arguments);

        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3]);
        	me.gridParams = me.store.proxy.extraParams;
        	var count = me.store.getCount();
	     	Ext.get('risk-tidy-card-num' + me.id).setHTML(count);
        });
        
	   	me.on('edit', function (event,value) {
	   	    me.onSave(value);
	    });
    }

});