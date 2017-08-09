Ext.define('FHD.view.risk.assess.kpiSet.security.SetAssessTask', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.setassesstask',
    
    flex:1,
    //按风险分配窗口
    riskTaskWindow: function(){
    	var me = this;
    	var riskIdArray=[];
    	var items = me.store.data.items;
    	Ext.each(items,function(item){//循环取出每一列的风险id
				riskIdArray.push(item.data.riskId);
			});
    	var kpiSetRiskTaskGrid = Ext.create('FHD.view.risk.assess.kpiSet.KpiSetRiskTaskGrid');
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
        //添加风险
	addRisk: function(){
		
		var me = this;
		me.addAllShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
			type:'re',	//如果是re,上级风险只能选择叶子节点
			border:false,
			schm:me.schm,
			state : '2',
			
			assessPlanId : me.businessId,
			executionId: me.executionId,
        	_type: 'task',
			
			setLoginDept : true,
			hiddenSaveBtn:true,
			userValidate : function(){
				return true;
			},
			callback:function(data){
				me.addAllShortForm.body.unmask();
            	Ext.MessageBox.alert('添加信息','添加成功');
            	me.formwindow.close();
            	me.winConfirm(kpiSetRiskTaskGrid);
			}
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
			FHD.notification('评估人不能为空！',FHD.locale.get('fhd.common.prompt'));
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
				url : __ctxPath + '/access/kpiSet/findempsbyuserdeptidandroles.f'
			}
		});
        var cols = [ 
		{header : "riskId",dataIndex:'riskId', hidden: true},
		{header : "planId",dataIndex:'planId', hidden: true},
		{header : "scoreObjId",dataIndex:'scoreObjId', hidden: true},
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
		        				var selection = me.getSelectionModel().getSelection();
			        			var riskId = selection[0].get('riskId');
			        			var objectId = selection[0].get('scoreObjId');
						    	me.quaAssessEdit = Ext.create('FHD.view.risk.assess.quaAssess.QuaAssessEdit',{isEditIdea : false,objectId:objectId});
						    	
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
		{header:'评估人<font color=red>*</font>',dataIndex:'empId',flex:2,
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
            		text:'按风险分配',
            		iconCls : 'icon-menu',
            		handler:function(){
            			me.riskTaskWindow();
            		}
            	  },
            	   {text:'添加风险', iconCls: 'icon-add', handler:function(){
        		   		me.addRisk();
        		   }}
            	  ],
		    border: false,
		    checked: false,
		    columnLines: true,
		    pagable : false,
		    type: 'editgrid',
		    searchable : true
        });

        me.callParent(arguments);
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3]);
        });
    }

});