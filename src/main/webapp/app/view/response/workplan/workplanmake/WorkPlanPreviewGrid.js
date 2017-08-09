Ext.define('FHD.view.response.workplan.workplanmake.WorkPlanPreviewGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.workPlanPreviewGrid',
    requires: [
    ],
   
    reloadData: function(planId,deptId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/formulateplan/findscoreobjectsinfobyplanidanddeptid.f';
 		me.store.proxy.extraParams.planId = planId;
 		me.store.proxy.extraParams.deptId = deptId;
 		me.store.load();
    },
    
    risksSelect:function(){
    	var me = this;
		me.win = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{//风险选择组件
			multiSelect:true,
    		modal: true,
		   	onSubmit:function(win){
		   		var planId = me.businessId;
    			var values = new Array();
    			var store = win.selectedgrid.store;
    			store.each(function(r){
    			    values.push(r.data.id);
    			});
    			me.body.mask("保存中...","x-mask-loading");
    			FHD.ajax({//ajax调用
    				url : __ctxPath + '/access/formulateplan/savescoreobjectrisks.f',
    				params : {
    					riskIds:values.join(','),
    					planId:planId,
    					isMain: true
    				},
    				callback : function(data){
    					me.body.unmask();
    					//刷新部门承办人列表
    					var workPlanDeptTakerGrid = me.workPlanMakeEditMain.p2.rightgrid;
    					workPlanDeptTakerGrid.store.load();
    					me.store.load();
    				}
    			});
    		}
		}).show();
    },
    //设置按钮可用状态
    setstatus : function(me){
    	if (me.down("[name='workplan_previewgrid_delete']")) {
            me.down("[name='workplan_previewgrid_delete']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
    },
    
    delRisk : function(me){//批量删除
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
		var planId = me.businessId;
    	Ext.MessageBox.show({
    		title : FHD.locale.get('fhd.common.delete'),
    		width : 260,
    		msg : FHD.locale.get('fhd.common.makeSureDelete'),
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
    				var ids = [];
    				var objIds = [];
    				for(var i=0;i<selection.length;i++){
    						ids.push(selection[i].get('scoreDeptId'));
    						objIds.push(selection[i].get('scoreObjId'));
    					}
    				me.body.mask("删除中...","x-mask-loading");
    				FHD.ajax({//ajax调用
    					url : "access/formulateplan/removeriskscoresbyIds.f",
    					params : {
    						ids: ids.join(','),
    						objIds: objIds
    					},
    					callback : function(data){
    						if(data){//删除成功！
    							me.body.unmask();
    							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    							me.store.load();
    							//刷新部门承办人列表
    							var workPlanDeptTakerGrid = me.workPlanMakeEditMain.p2.rightgrid;
    							workPlanDeptTakerGrid.store.load();
    						}
    					}
    				});
    			}
    		}
    	});
    },
    
    delRiskOne: function(me){//逐条删除
    	var selection = me.getSelectionModel().getSelection()[0];//得到选中的记录
    	Ext.MessageBox.show({
    		title : FHD.locale.get('fhd.common.delete'),
    		width : 260,
    		msg : FHD.locale.get('fhd.common.makeSureDelete'),
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
    				me.body.mask("删除中...","x-mask-loading");
    				FHD.ajax({//ajax调用
    					url : "access/formulateplan/removeriskscorebyId.f",
    					params : {
    						id: selection.get('id')
    					},
    					callback : function(data){
    						if(data){//删除成功！
    							me.body.unmask();
    							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    							me.store.load();
    							//刷新部门承办人列表
    							var workPlanDeptTakerGrid = me.workPlanMakeEditMain.p2.rightgrid;
    							workPlanDeptTakerGrid.store.load();
    						}
    					}
    				});
    			}
    		}
    	});
    },
    		
    //导出grid列表
    exportChart:function(planId, sheetName, exportFileName){
    	var me=this;
    	var deptId;
    	var planId;
    	if(me.deptId){//是否是按部门导出
    		deptId = me.deptId;
    	}
    	if(me.businessId){
    		planId = me.businessId;
    	}else{
    		var workPlanMakeEditMain = me.workPlanMakeEditMain;
	    	var workPlanMakeGrid = workPlanMakeEditMain.up('workPlanMakeCard').workPlanMakeGrid;
	    	if(workPlanMakeGrid){
	    		if(workPlanMakeGrid.businessId){
		    		planId = workPlanMakeGrid.businessId;//计划id
		    	}else if(workPlanMakeEditMain.businessId){//新增计划的导出
		    		planId = workPlanMakeEditMain.businessId;
		    	}
	    	}
    	}
    	sheetName = 'exportexcel';
    	window.location.href = __ctxPath + "/access/formulateplan/exportriskscoreobjspage.f?planId="+planId+"&exportFileName="+""+"&sheetName="+sheetName+"&deptId="+deptId;
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.idArray;
        //目标名称 目标责任人 衡量指标 权重  指标说明  指标责任人
        me.cols = [ {dataIndex : 'id', hidden : true}, 
        			{header : "部门", dataIndex : 'deptName', sortable : false, flex : 1},
         			{header : "上级风险", dataIndex : 'parentRiskName', sortable : false, flex : 1,
         				renderer:function(dataIndex) { 
		    				  return dataIndex.split(' ')[0];
    				}},
                    {header : "风险名称", dataIndex : 'riskName', sortable : true, flex : 3,
		                renderer:function(value,metaData,record,rowIndex ,colIndex,store,view){
							metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
							return "<a href=\"javascript:void(0);\">"+value+"</a>";
						},
						listeners:{
			        		click:{
			        			fn:function(g,d,i){
			        			var selection = me.getSelectionModel().getSelection();
			        			var riskId = selection[0].get('riskId');
						    	me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
									height : 500,
					    			riskId : riskId
					    		});
						    	me.formwindow = new Ext.Window({
									layout:'fit',
									iconCls: 'icon-show',//标题前的图片
									modal:true,//是否模态窗口
									collapsible:true,
									width:800,
									height:500,
									title : '风险详细信息查看',
									maximizable:true,//（是否增加最大化，默认没有）
									constrain:true,
									items : [me.detailAllForm],
									buttons: [
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
        				}
        			}},
					{header : "责任类型", dataIndex : 'deptType', sortable : false, flex : .5,
						renderer:function(dataIndex) { 
		    				  if(dataIndex == "M"){
		    					  return "责任部门";
		    				  }else if(dataIndex == "A"){
		    					  return "相关部门";
		    				  }
    				}},
    				{header:'操作',dataIndex:'caozuo',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
				       xtype:'actioncolumn',
				       items: [{
			                icon: __ctxPath+'/images/icons/delete_icon.gif', 
			                tooltip: FHD.locale.get('fhd.common.del'),
			                handler: function(grid, rowIndex, colIndex) {
			                    grid.getSelectionModel().deselectAll();
		    					var rows=[grid.getStore().getAt(rowIndex)];
		    	    			grid.getSelectionModel().select(rows,true);
			                    me.delRiskOne(me);
			                }
			            }]
				    },
				    {dataIndex : 'riskId', hidden : true},
				    {dataIndex : 'scoreDeptId', hidden : true},
				    {dataIndex : 'scoreObjId', hidden : true}
                  ];
        me.tbar = [
        		   {btype: 'add', name:'workplan_previewgrid_add', handler:function(){me.risksSelect();}},
        		   {btype: 'delete', name:'workplan_previewgrid_delete',disabled:true,handler:function(){me.delRisk(me);}},
        		   {iconCls: 'icon-ibm-action-export-to-excel', text:'导出',tooltip: '把当前列表导出到Excel', handler:function(){me.exportChart();}}
                   ];
        
        Ext.apply(me, {
        	region:'center',
            cols:me.cols,
            tbarItems:me.tbar,
		    border: false,
		    columnLines: true,
		    checked: true,
		    pagable : false,
		    searchable : true
        });

        me.on('selectionchange',function(){me.setstatus(me)});
        me.callParent(arguments);
        
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3,4]);
        });
    }

});

