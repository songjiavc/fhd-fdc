/**
 * 评估范围fieldSet，部门承办人列表--风险辨识
 */
Ext.define('FHD.view.risk.riskidentify.PlanConformEditNextGridriskIdentify',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.planConformEditNextGridriskIdentify',
	requires: [
    ],
    //传参方法
    submitPlanByParams: function(approverId,businessId,value){
    	var me = this;
    	var empIds = [];
		var items = me.store.data.items;
		var jsonArray=[];
		Ext.each(items,function(item){
			jsonArray.push(item.data);
		});
		for(var k in items){
			if(!items[k].data.empId){
				 empIds = [];
				 break;
			}else{
				empIds.push(me.store.data.items[k].data.empId);
			}
		}
    	if(me.executionId){
    		FHD.ajax({//ajax调用
				url : __ctxPath + '/access/riskidentify/submitriskidentifyplanbysome.f',
				params : {
					empIds: empIds.join(','),
					approverId: approverId,
					businessId: me.businessId,
					deptEmpId: Ext.encode(jsonArray),
					executionId: me.executionId
				},
				callback : function(data){
					me.body.unmask();
					if(me.winId){
			    		Ext.getCmp(me.winId).close();
			    	}
				}
			});
    	}else{
    		var args = {
				empIds: empIds.join(','),
				approver: approverId,
				businessId: businessId,
				executionId: '',
				entityType: 'riskIdentifyTotal',
				deptEmpId: Ext.encode(jsonArray)
			};
			me.body.mask("提交中...","x-mask-loading");
			FHD.ajax({
				url : __ctxPath + '/access/planconform/startprocessbyvalue.f',
				params : {
					args: Ext.JSON.encode(args),
					value: value
				},
				callback : function(data){
					me.body.unmask();
					var prt = me.up('planConformCard');
					if(prt){
						prt.planConformGrid.store.load();
						//取消列表已选中的列，解决提交后未刷新的重复修改问题
						prt.planConformGrid.getSelectionModel().deselectAll(true);
			    		prt.showPlanConformGrid();
					}
					
				}
			});
    	}
    },
    
    //加载列表数据
    loadData: function(planId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/access/planconform/findscoredeptsgridbyplanid.f';
 		me.store.proxy.extraParams.planId = planId;
 		me.store.load();
    },
    //按部门添加风险
    risksSelectByOrg: function(){
    	var me = this;
    	if(me.up('planConformFormMain')){
    		var planId = me.up('planConformFormMain').planConformEditOne.idField.getValue();//从隐藏域取id值
    	}else{
    		var planId = me.businessId;//从隐藏域取id值
    	}
		me.orgwin = Ext.create('FHD.ux.org.DeptSelectorWindow',{//部门选择组件
			subCompany : false,// 显示子公司
			companyOnly : false,// 显示公司和部门
			rootVisible : true,// 显示根机构
			multiSelect : true,
			onSubmit:function(win){
				var values = new Array();
				var store = win.selectedgrid.store;
    			store.each(function(r){
    			    values.push(r.data.id);
    			});
    			me.body.mask("保存中...","x-mask-loading");
    			FHD.ajax({//ajax调用
    				url : __ctxPath + '/access/riskidentify/savescoreobjectsandscoredeptsbyorgids.f',
    				params : {
    					orgIds: values.join(','),
    					planId: planId
    				},
    				callback : function(data){
    					me.body.unmask();
    					me.loadData(planId);
    				}
    			});
			}
		}).show();
    },
    
    //查看选中部门的风险明细
    seeRisksInfoByDeptId: function(seeAll){
    	var me = this;
    	var deptId;
		var planId = me.up('planConformFormMain').planConformEditOne.idField.getValue();//从隐藏域取id值
		var typeId = me.up('planConformFormMain').planConformEditOne.riskWorkSelect.getValue();// 取流程类型
		if(!seeAll){//查看全部
			var selection = me.getSelectionModel().getSelection();
			deptId = selection[0].get('id');
		}
		me.planRisksPreviewGrid = Ext.create('FHD.view.risk.planconform.PlanRisksPreviewGrid',{
			height:600,
			width:1100,
			scroll:'vertical',
			deptId: deptId,
			planId: planId,
			typeId: typeId,
			planConformEditNextGrid: me
		});
		me.planRisksPreviewGrid.loadData(planId,deptId);
		if(me.planRisksPreviewGrid.down("[name='identify_grid_add']")){
			me.planRisksPreviewGrid.down("[name='identify_grid_add']").setVisible(false);
		}
		me.riskwin = Ext.create('FHD.ux.Window', {
    		autoScroll:false,
    		title:'风险事件详细信息',
    		maximizable: true,
    		width:900,
    		height:500,
        	items:[me.planRisksPreviewGrid]
		});
    	me.riskwin.show();
	},
    
	initComponent:function(){
		var me=this;
		me.cols=[
        	{header: '部门名称', dataIndex: 'deptName', sortable : false, flex: 1 },
        	{header: '风险数量', dataIndex: 'riskCournts', sortable : false, flex: 1 },
        	{header:'承办人<font color=red>*</font>',dataIndex:'empId',flex:1,hidden : true,width:0},
        	{header:'承办人<font color=red>*</font>',dataIndex:'empName', sortable : false,flex:1,
				editor:Ext.create('Ext.form.field.ComboBox',{
					store :Ext.create('Ext.data.Store',{
						autoLoad : false,
						fields : ['id', 'name'],
						proxy : {
							type : 'ajax'
						}
					}),
					valueField : 'id',
					displayField : 'name',
					allowBlank : false,
					editable : false,
					listeners:{
							expand:function(){
								var selection = me.getSelectionModel().getSelection();
								var length = selection.length;
								if (length > 0) {
									var deptId = selection[0].get('id');
									this.store.proxy.url = __ctxPath + '/access/formulateplan/findempsbydeptids.f';
									this.store.proxy.extraParams.deptId = deptId;
									this.store.load();
								}
							},
							select: function(){
								var items = me.getSelectionModel().getSelection();
								var empId = this.getValue();
								var jsonArray=[];
								jsonArray.push(items[0].data);
								FHD.ajax({//ajax调用
    								url : __ctxPath + '/access/formulateplan/saveriskcircuseebysome.f',//保存承办人
    								params : {
    									modifyRecords: Ext.encode(jsonArray),
    									empId: empId
    								},
    								callback : function(data){
    					
    								}
    							});
							}
					}}),
					renderer:function(value,metaData,record,rowIndex ,colIndex,store,view){
						metaData.tdAttr = 'style="background-color:#FFFBE6"';
						var v = this.columns[3].getEditor(record).store.findRecord('id',value);
						if(v){
							record.data.empId = v.data.id;
							return v.data.name;
						}
						return value;
					}
			},
			{dataIndex : 'id', hidden : true}, 
			{
	            header: "操作",
	            dataIndex: '',
	            sortable: true,
	            width:40,
	            flex:1,
	            renderer:function(){
					return "<a href=\"javascript:void(0);\" >查看明细</a>&nbsp;&nbsp;&nbsp;"	//
				},
				listeners:{
	        		click:function(){
	        			me.seeRisksInfoByDeptId(false);
    				}
        		}
			},
			{dataIndex : 'planId', hidden : true}
		];
		
		me.tbar = [
        		   {text:'按部门添加', iconCls: 'icon-add', handler:function(){
        		   		me.risksSelectByOrg();
        		   }},
        		   {text:'查看全部', iconCls: 'icon-scorecards', handler:function(){
        		   		me.seeRisksInfoByDeptId(true);
        		   }}
                   ];
        
        Ext.apply(me, {
            cols:me.cols,
            tbarItems:me.tbar,
		    border: true,
		    columnLines: false,
		    checked: false,
		    pagable : false,
		    searchable : true,
		    type: 'editgrid'
        });
                   
		me.callParent(arguments);
	}
	
});