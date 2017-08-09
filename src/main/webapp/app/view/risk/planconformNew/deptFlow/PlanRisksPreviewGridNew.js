Ext.define('FHD.view.risk.planconformNew.deptFlow.PlanRisksPreviewGridNew', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.planRisksPreviewGridNewDept',
    requires: [
    ],
	//加载列表数据   
    loadData: function(planId,deptId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/planconform/findrisksgridbyplanidordeptid.f';
	 	me.store.proxy.extraParams.planId = planId;
	 	me.store.proxy.extraParams.deptId = deptId;//根据部门查看，为空时，默认查询计划下全部
	 	me.store.load();
    },
    //弹出风险详细信息组件
    showRiskWindow: function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
		var riskId = selection[0].get('riskId');
		var scoreObjId = selection[0].get('scoreObjId');
		if("" != riskId){
			me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
				height : 500,
				riskId : riskId,
				objectId:scoreObjId
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
    },
    //新增风险
    risksSelect:function(){
    	var me = this;
		me.win = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{//风险选择组件
			multiSelect:true,
    		modal: true,
		   	onSubmit:function(win){
		   		var values = new Array();
				var store = win.selectedgrid.store;
    			store.each(function(r){
    			    values.push(r.data.id);
    			});
    			me.body.mask("保存中...","x-mask-loading");
    			FHD.ajax({//ajax调用
    				url : __ctxPath + '/access/planconform/savescoreobjectrisksbyriskids.f',
    				params : {
    					riskIds: values.join(','),
    					planId: me.planId,
    					typeId: me.typeId,
    					deptId: me.deptId
    				},
    				callback : function(data){
    					me.body.unmask();
    					me.loadData(me.planId, me.deptId);
    					me.planConformEditNextGrid.loadData(me.planId);
    					if(!data.success){
    						FHD.notification('部分风险没有所属部门，未保存！',FHD.locale.get('fhd.common.prompt'));
    					}
    				}
    			});
    		}
		}).show();
    },
    
    //设置按钮可用状态
    setstatus : function(me){
    	me.down("[name='risk_grid_delete']").setDisabled(me.getSelectionModel().getSelection().length === 0);
    },
    //批量删除
    delRisks: function(me){
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
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
    					url : __ctxPath + '/access/planconform/removeriskscoresbyidsandobjids.f',
    					params : {
    						ids: ids.join(','),
    						objIds: objIds
    					},
    					callback : function(data){
    						if(data){//删除成功！
    							me.body.unmask();
    							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    							me.loadData(me.planId, me.deptId);
    							//刷新部门承办人列表
    							me.planConformEditNextGrid.loadData(me.planId);
    						}
    					}
    				});
    			}
    		}
    	});
    },
    
    //导出grid列表
    exportChart:function(sheetName, exportFileName){
    	var me=this;
    	sheetName = 'exportexcel';
    	var query = me.searchField.lastValue;
    	if(!query){
    		query = "";
    	}
    	window.location.href = __ctxPath + "/access/formulateplan/exportriskscoreobjspage.f?planId="
    						+me.planId+"&exportFileName="+""+"&sheetName="+sheetName+"&deptId="+me.deptId+"&query="+query;
    },
     
    // 初始化方法
    initComponent: function() {
        var me = this;
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
			        				me.showRiskWindow();
		        				}
        					}
        				}},
					{header : "责任类型", dataIndex : 'deptType', sortable : false, flex : .5,
						renderer:function(dataIndex) { 
		    				  if(dataIndex == "M"){
		    					  return "责任部门";
		    				  }else if(dataIndex == "A"){
		    					  return "相关部门";
		    				  }else if(dataIndex == "C"){
		    					  return "参与部门";
		    				  }
    				}},
    				{header:'操作',dataIndex:'caozuo',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
				       xtype:'actioncolumn',
				       items: [{
			                icon: __ctxPath+'/images/icons/delete_icon.gif',  // Use a URL in the icon config
			                tooltip: FHD.locale.get('fhd.common.del'),
			                handler: function(grid, rowIndex, colIndex) {
			                    grid.getSelectionModel().deselectAll();
		    					var rows=[grid.getStore().getAt(rowIndex)];
		    	    			grid.getSelectionModel().select(rows,true);
			                    me.delRisks(me);
			                }
			            }]
				    },
				    {dataIndex : 'riskId', hidden : true},
				    {dataIndex : 'scoreDeptId', hidden : true},
				    {dataIndex : 'scoreObjId', hidden : true}
                  ];
        me.tbar = [
        		   {btype: 'add', name:'identify_grid_add', handler:function(){me.risksSelect();}},
        		   {btype: 'delete', name:'risk_grid_delete',disabled:true,handler:function(){me.delRisks(me);}},
        		   {iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}
                   ];
        
        Ext.apply(me, {
        	region:'center',
            cols:me.cols,
            tbarItems:me.tbar,
            storeAutoLoad: false,
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


