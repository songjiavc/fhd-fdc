Ext.define('FHD.view.response.workplan.workplantask.TaskGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.taskGridPanel',
    requires: [
	],
    
    flex:1,
    //按风险分配窗口
    riskTaskWindow: function(){
    	var me = this;
    	var riskIdArray=[];
    	var items = me.store.data.items;
    	Ext.each(items,function(item){//循环取出每一列的风险id
				riskIdArray.push(item.data.riskId);
			});
    	var setRiskTaskGrid = Ext.create('FHD.view.response.workplan.workplantask.SetRiskTaskGrid');
    	setRiskTaskGrid.store.proxy.url = __ctxPath + '/access/kpiSet/queryassesstaskbyriskid.f';
    	setRiskTaskGrid.store.proxy.extraParams.riskIds = riskIdArray.join(',');
    	
    	me.riskWin = Ext.create('FHD.ux.Window', {
			title:'按风险分配',
   		 	height: 450,
    		width: 600,
   			layout: 'fit',
   			buttonAlign: 'center',
    		items: [setRiskTaskGrid],
   			fbar: [
   					{ xtype: 'button', text: '保存', handler:function(){me.winConfirm(setRiskTaskGrid);}},
   					{ xtype: 'button', text: '取消', handler:function(){me.riskWin.hide();}}
				  ]
		}).show();
    },
    
    winConfirm: function(grid){
    	var me = this;
		var empIds = [];
		var items = grid.store.data.items;
		for(var k in items){
				if(!items[k].data.empId){
					 empIds = [];
					 break;
				}else{
					empIds.push(items[k].data.empId);
				}
		}
		if(!empIds.length){
			FHD.notification('承办人不能为空！',FHD.locale.get('fhd.common.prompt'));
			return ;
		}
		var rows = grid.store.data.items;//取出列表每一行数据	.getModifiedRecords()
			var jsonArray=[];
			Ext.each(rows,function(item){
				jsonArray.push(item.data);
			});
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
    				url : __ctxPath + '/access/kpiSet/saveObjdeptempbysome.f',//保存对象，人员，部门综合表
    				params : {
    					modifyRecords:Ext.encode(jsonArray),
    					planId:me.businessId
    				},
    				callback : function(data){
    					me.body.unmask();
    					me.store.load();
    				}
    			});
    	me.riskWin.hide();
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        //目标名称 目标责任人 衡量指标 权重  指标说明  指标责任人
        var emps = Ext.create('Ext.data.Store',{
							autoLoad:true,
							fields : ['id', 'name'],
							proxy : {
								type : 'ajax',
								url : __ctxPath + '/access/kpiSet/findempsbyuserdeptId.f'
							}
							});
        
        var cols = [ 
		{header : "riskId",dataIndex:'riskId', hidden: true},
		{header : "planId",dataIndex:'planId', hidden: true},
		{
			header : "上级风险",
			dataIndex : 'parentRiskName',
			sortable : false,
			flex : 1
		}
		, {
			header : "风险名称",
			dataIndex : 'riskName',
			sortable : false,
			flex : 3,
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
								height : 460,
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
							me.detailAllForm.on('resize',function(p){
					    		me.detailAllForm.setHeight(me.formwindow.getHeight()-20);
						   	});
							me.formwindow.show();
	        			}
    				}
    			}
		}, {
			header : "责任部门",
			dataIndex : 'mainOrgName',
			sortable : false,
			flex : .5
		},{
			header : "相关部门",
			dataIndex : 'relaOrgName',
			sortable : false,
			flex : .5
		},
		{header:'承办人<font color=red>*</font>',dataIndex:'empId',flex:.5,emptyCellText:'<font color="#808080">请选择</font>',
				editor:Ext.create('Ext.form.field.ComboBox',{
					store : emps,
					checkField: 'checked',//多选
    				multiSelect : false,
    				separator: ',',
					valueField : 'id',
					displayField : 'name',
					allowBlank : false,
					editable : false,
					listeners:{
							select:function(){//监听下拉框值改变,保存
								var items = me.getSelectionModel().getSelection();
								var empId = this.getValue();
								var jsonArray=[];
								jsonArray.push(items[0].data);
								FHD.ajax({//ajax调用
    								url : __ctxPath + '/access/kpiSet/saveobjdeptempgridbysome.f',//保存对象，人员，部门综合表
    								params : {
    									modifyRecords: Ext.encode(jsonArray),
    									empIds: empId
    								},
    								callback : function(data){
    					
    								}
    							});
							}
					}
					}),
					renderer:function(value,metaData,record,rowIndex ,colIndex,store,view){
						metaData.tdAttr = 'style="background-color:#FFFBE6"';
						if(Ext.isString(value)){
							value = value.split(',');
						}
						if(Ext.isString(record.data.empName)){
							record.data.empName = record.data.empName.split(',');
							record.data.empId = record.data.empId.split(',');
						}
						
						var displayName = new Array();
	                	if(value){
	                		Ext.Array.each(value,function(r,i){
	                			var rec = emps.findRecord('id',r);
	                			if(rec){
	                				displayName.push(rec.data.name);
	                			}
	                		});
		                	if(displayName.length>0){
		                		metaData.tdAttr = 'style="background-color:#FFFBE6" data-qtip="'+displayName.join(',')+'"'; 
		                		return displayName.join(',');
		                	}else{
		                		if(record.data.empName){
			                		metaData.tdAttr = 'style="background-color:#FFFBE6" data-qtip="'+record.data.empName+'"'; 
	                				return record.data.empName;
			                	}else{
			                		return $locale('fhd.common.pleaseSelect');
			                	}
		                	}
	                	}else{
	                		return $locale('fhd.common.pleaseSelect');
	                	}
					}
			},{
				dataIndex : 'empName',
				sortable : false,
				width: 0
			}
		];
        
        Ext.apply(me, {
        	region:'center',
        	url : __ctxPath + "/access/kpiSet/queryassesstaskspage.f"+"?businessId="+me.businessId,//列表查询url
            cols:cols,
            tbarItems:[ 
                  {
            		text:'按风险分配',
            		iconCls : 'icon-menu',
            		handler:function(){
            			me.riskTaskWindow();
            		}
            	  }],
		    border: false,
		    checked: false,
		    columnLines: true,
		    pagable : false,
		    type: 'editgrid',
		    searchable : true,
		    scroll: 'vertical'
        });

        me.callParent(arguments);
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3]);
        });
    }

});