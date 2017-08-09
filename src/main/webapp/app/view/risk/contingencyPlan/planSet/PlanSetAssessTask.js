Ext.define('FHD.view.risk.contingencyPlan.planSet.PlanSetAssessTask', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.plansetassesstask',
    requires: [
 	           'FHD.view.risk.contingencyPlan.planSet.PlanSetRiskTaskGrid',
 	           'FHD.view.risk.assess.quaAssess.QuaAssessEdit'
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
    	var kpiSetRiskTaskGrid = Ext.widget('plansetrisktaskgrid');
    	//kpiSetRiskTaskGrid.store.load({params:{riskIds: riskIdArray.join(',')}});
    	kpiSetRiskTaskGrid.store.proxy.url = __ctxPath + '/access/kpiSet/queryassesstaskbyriskid.f';
		kpiSetRiskTaskGrid.store.proxy.extraParams.riskIds = riskIdArray.join(',');
    	
    	me.riskWin = Ext.create('FHD.ux.Window', {
			title:'按风险分配',
   		 	height: 450,
    		width: 600,
   			layout: 'fit',
   			buttonAlign: 'center',
    		items: [kpiSetRiskTaskGrid],
   			fbar: [
   					{ xtype: 'button', text: '保存', handler:function(){me.winConfirm(kpiSetRiskTaskGrid);}},
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
			//Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), '承办人不能为空！');
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
        var emps=Ext.create('Ext.data.Store',{
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
			flex : .5
		}
		, {
			header : "风险名称",
			dataIndex : 'riskName',
			sortable : false,
			flex : 1.5,
			renderer:function(value,metaData,record,rowIndex ,colIndex,store,view){
						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
						return "<a href=\"javascript:void(0);\">"+value+"</a>";
					},
			listeners:{
		        		click:{
		        			fn:function(g,d,i){
		        				//单击风险名称，显示风险基本信息组件
		        				/*var detailForm = Ext.create('FHD.view.risk.cmp.RiskDetailForm', {
		        		        	riskId:me.getSelectionModel().getSelection()[0].data.riskId,//'9322cf77-a8f4-4d08-bb69-bcae3cb4fdf3',
		        		        	height:360
		        				});
	        				var win = Ext.create('Ext.window.Window', {
	                    		autoScroll:true,
	                    		title:'风险事件详细信息',
	                    		width:800,
	                    		height:400,
	                        	items:[detailForm]
	                		});
	                    	win.show();*/
		        				var selection = me.getSelectionModel().getSelection();
			        			var riskId = selection[0].get('riskId');
						    	me.quaAssessEdit = Ext.widget('quaAssessEdit',{isEditIdea : false});
						    	
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
									items : [me.quaAssessEdit],
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
								me.quaAssessEdit.load(riskId, '');
		        			}
        				}
        			}
		}, {
			header : "责任部门",
			dataIndex : 'mainOrgName',
			sortable : false,
			flex : .5
		},
		{
//			hidden : true,
			header : "相关部门",
			dataIndex : 'relaOrgName',
			sortable : false,
			flex : .5
		},
//		{
//			hidden : true,
//			header : "参与部门",
//			dataIndex : 'joinOrgName',
//			sortable : false,
//			flex : .5
//		},
		{header:'承办人<font color=red>*</font>',dataIndex:'empId',flex:2,
				editor:Ext.create('Ext.form.field.ComboBox',{
					store :emps,
					checkField: 'checked',//多选
    				multiSelect : true,
    				separator: ',',
					valueField : 'id',
					displayField : 'name',
					allowBlank : false,
					editable : false,
					listeners:{
							/*expand: function(emps){
								if(emps.store.data.items.length == 0){//没有符合条件的人员
									FHD.alert('该部门中没有符合风险评价准则中设置权重角色的员工，请为您部门的员工分配相应的角色！');
								}
							},*/
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
						/*var v = this.columns[6].getEditor(record).store.findRecord('id',value);
						if(v){
							record.data.empId = v.data.id;
							return v.data.name;
						}
						return value;*/
					}
			},{
				dataIndex : 'empName',
				sortable : false,
				width: 1
			}
		];
        
        Ext.apply(me, {
        	region:'center',
        	url : __ctxPath + "/access/kpiSet/queryassesstaskspage.f"+"?businessId="+me.businessId,//列表查询url
            cols:cols,
            tbarItems:[ 
                  {
            		//btype:'custom',
            		text:'按风险分配',
            		iconCls : 'icon-menu',
            		handler:function(){
            			me.riskTaskWindow();
            		}
            	  }/*,{
            		btype:'custom',
            		text:'工作说明',
            		iconCls : 'icon-emp',
            		handler:function(){
            			//alert('save');
            		}
                  }*/],
		    border: true,
		    checked: false,
		    columnLines: true,
		    pagable : false,
		    type: 'editgrid',
		    searchable : true
        });

        me.callParent(arguments);
        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3]);
        });
    }

});