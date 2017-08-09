/**
 * 评估范围fieldSet，部门承办人列表
 */
Ext.define('FHD.view.response.workplan.workplanmake.WorkPlanDeptTakerGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.workPlanDeptTakerGrid',
	requires: [
       		'FHD.view.risk.assess.formulatePlan.FormulatePlanPreviewGrid'
    ],
	
	risksSelect:function(){
    	var me = this;
		me.win = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{//风险选择组件
			multiSelect:true,
    		modal: true,
    		showLight: true,
    		schm: me.typeId,
		   	onSubmit:function(win){
		   		var workPlanMakeEditMain = me.up('workPlanMakeEditMain');
				var planId = workPlanMakeEditMain.businessId;
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
    					isMain:true
    				},
    				callback : function(data){
    					me.body.unmask();
    					me.store.load();
    				}
    			});
    		}
		}).show();
    },
    
    seeRisksInfoByDeptId: function(seeAll){
		//查看选中部门的风险明细
    	var me = this;
    	var workPlanMakeEditMain = me.up('workPlanMakeEditMain');
		var planId = workPlanMakeEditMain.businessId;//计划id
		var workPlanPreviewGrid = Ext.create('FHD.view.response.workplan.workplanmake.WorkPlanPreviewGrid',{
			height:600,
			width:1100,
			businessId: planId,
			workPlanMakeEditMain: workPlanMakeEditMain
		});
		
		if(!seeAll){//是否查看全部
			var selection = me.getSelectionModel().getSelection();
			workPlanPreviewGrid.deptId = selection[0].get('id');//部门id
			workPlanPreviewGrid.reloadData(planId,workPlanPreviewGrid.deptId);
		}else{
			workPlanPreviewGrid.reloadData(planId,null);
		}
		
		
		var win = Ext.create('FHD.ux.Window', {
    		autoScroll:false,
    		title:'风险事件详细信息',
    		maximizable: true,
    		//maximized: true,//最大化
    		width:900,
    		height:500,
        	items:[workPlanPreviewGrid]
		});
    	win.show();
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
        		   {btype: 'add', /*id:'depttaker_grid_add_workPlan',*/handler:function(){me.risksSelect();}},'-',
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