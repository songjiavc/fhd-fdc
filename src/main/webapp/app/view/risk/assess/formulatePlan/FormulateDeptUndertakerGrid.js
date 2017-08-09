/**
 * 评估范围fieldSet，部门承办人列表
 */
Ext.define('FHD.view.risk.assess.formulatePlan.FormulateDeptUndertakerGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.formulateDeptUndertakerGrid',
	requires: [
       		'FHD.view.risk.assess.formulatePlan.FormulatePlanPreviewGrid'
    ],
	risksSelect:function(){
    	var me = this;
		me.win = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{//风险选择组件
			multiSelect:true,
    		modal: true,
			typeId:me.schm,
			schm:me.schm,
		   	onSubmit:function(win){
				var planId = me.planId
    			var values = new Array();
    			var store = win.selectedgrid.store;
    			store.each(function(r){
    			    values.push(r.data.id);
    			});
    			me.body.mask("保存中...","x-mask-loading");
    			FHD.ajax({//ajax调用
    				url :__ctxPath + '/access/planconform/savescoreobjectrisksbyriskids.f',
    				params : {
    					riskIds:values.join(','),
    					planId:planId,
    					typeId:me.schm
    				},
    				callback : function(data){
    					me.body.unmask();
    					me.store.load();
    				}
    			});
    		}
		}).show();
    },
    //按部门添加风险
    risksSelectByOrg: function(){
    	var me = this;
    	var planId = me.planId;
    	var typeId = me.schm;
		me.orgwin = Ext.create('FHD.ux.org.DeptSelectorWindowNew',{//部门选择组件
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
    				url : __ctxPath + '/access/planconform/savescoreobjectrisksbyorgids.f',
    				params : {
    					orgIds: values.join(','),
    					planId: planId,
    					typeId: typeId
    				},
    				callback : function(data){
    					me.body.unmask();
    					me.loadData(planId);
    				}
    			});
			}
		}).show();
    },
    
    
    loadData: function(planId){
    	var me = this;
    	if( me.flowType =="company"){
    		me.store.proxy.url = __ctxPath + '/access/planconform/findscoredeptsgridbyplanid.f';
    	}else{
    		me.store.proxy.url = __ctxPath + '/access/planconform/findscoredeptsgridbyplanid_Dept.f';
    	}
 		me.store.proxy.extraParams.planId = planId;
 		me.store.load();
    },
    seeRisksInfoByDeptId: function(seeAll){
    	var me = this;
    	var deptId;
		var planId = me.planId;
		var typeId = me.schm;
		if(!seeAll){//查看全部
			var selection = me.getSelectionModel().getSelection();
			deptId = selection[0].get('id');
		}
		me.planRisksPreviewGrid = Ext.create('FHD.view.risk.planconformNew.deptFlow.PlanRisksPreviewGridNew',{
			height:600,
			width:1100,
			scroll:'vertical',
			deptId: deptId,
			planId: planId,
			typeId: typeId,
			planConformEditNextGrid: me
		});
		me.planRisksPreviewGrid.loadData(planId,deptId);
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
		var deptAdd = {};
		if( me.flowType =="company"){
			deptAdd  = {text:'按部门添加', iconCls: 'icon-add', handler:function(){me.risksSelectByOrg();}};
		};
		me.id = 'formulateDeptUndertakerGridId';
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
							type : 'ajax',
							//url : __ctxPath + '/access/formulateplan/findempsbydeptids.f'
						}
					}),
					valueField : 'id',
					displayField : 'name',
					allowBlank : false,
					editable : false,
					listeners:{
							expand:function(){
								//点击下拉按钮调用该函数
								var selection = me.getSelectionModel().getSelection();
								var length = selection.length;
								if (length > 0) {
									var deptId = selection[0].get('id');
									//this.store.load({params:{deptId: deptId}});
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
						//页面加载先调用该函数
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
			{header:'操作',dataIndex:'caozuo',hidden:false,editor:false,align:'center',//必须有dataIndex，否则不能导出Excel
			       xtype:'actioncolumn',
			       items: [{
		                icon:  __ctxPath+'/images/icons/trend.gif',
		                tooltip: '查看明细	',
		                handler: function(grid, rowIndex, colIndex) {
			                	grid.getSelectionModel().deselectAll();
		    					var rows=[grid.getStore().getAt(rowIndex)];
		    	    			grid.getSelectionModel().select(rows,true);
		    	    			me.seeRisksInfoByDeptId(false);
		                	}
		       			},{
		       				xtype : 'label',
		       				width : 20
		       			},{
		                icon: __ctxPath+'/images/icons/delete_icon.gif',  // Use a URL in the icon config
		                tooltip: FHD.locale.get('fhd.common.del'),
		                handler: function(grid, rowIndex, colIndex) {
		                	grid.getSelectionModel().deselectAll();
	    					var rows=[grid.getStore().getAt(rowIndex)];
	    	    			grid.getSelectionModel().select(rows,true);
	        				var selection = grid.getSelectionModel().getSelection();
	        				var deptId = selection[0].get('id');
	        				var planId = me.planId;//从隐藏域取id值
	        				FHD.ajax({//ajax调用
	        					url : __ctxPath + '/access/planconform/findrisksgridbyplanidordeptidNew.f',
	        					params : {
	        						deptId: deptId,
	        						planId: planId
	        					},
	        					callback : function(data){
	        						if(data){
	        							var ids = [];
	        		    				var objIds = [];
	        		    				for(var i=0;i<data.length;i++){
	        		    						ids.push(data[i].scoreDeptId);
	        		    						objIds.push(data[i].scoreObjId);
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
	        		    							me.loadData(planId);
	        		    						}
	        		    					}
	        		    				});
	        						}
	        					}
	        				});
		                }
		            }]
			    },
			{dataIndex : 'planId', hidden : true}
		];
		me.tbar = [ {
			text : '按风险添加',
			iconCls : 'icon-add',
			handler : function() {
				me.risksSelect();
			}
		}, deptAdd,

		{text : '查看全部',
			iconCls : 'icon-scorecards',
			handler : function() {
				me.seeRisksInfoByDeptId(true);
			}
		}];
        
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