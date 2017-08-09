/*
 * 评价产生的缺陷的列表 
 * */
 Ext.define('FHD.view.icm.assess.component.AssessDefectGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.assessdefectgrid',
	
	url:__ctxPath+'/icm/assess/findAssessRelaFeedbackDefectListByAssessPlanId.f',
	extraParams:{
		assessPlanId:'',
		executionId:''
	},
	
	pagable:false,
	checked:false,
	feedbackIsAvailable:false,
	
	initComponent:function(){
		var me=this;
		
		me.extraParams.assessPlanId=me.businessId;
		me.extraParams.executionId=me.executionId;
		
		me.levelStore=Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath+'/sys/dic/findDictEntryByTypeId.f',
				extraParams:{
					typeId: 'ca_defect_level'
				},
				reader : {
					type : 'json',
					root : 'dictEnties'
				}
			}
		});
		me.levelStore.load();
		me.typeStore=Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath+'/sys/dic/findDictEntryByTypeId.f',
				extraParams:{
					typeId: 'ca_defect_type'
				},
				reader : {
					type : 'json',
					root : 'dictEnties'
				}
			}
		});
		me.typeStore.load();
		me.orgStore=Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath+'/sys/org/cmp/deptListByCompanyId.f',
				reader : {
					type : 'json',
					root : 'deptList'
				}
			}
		});
		me.orgStore.load();
		me.isAgreeStore=Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath+'/sys/dic/findDictEntryByTypeId.f',
				extraParams:{
					typeId: '0yn'
				},
				reader : {
					type : 'json',
					root : 'dictEnties'
				}
			}
		});
		me.isAgreeStore.load();
		/*
		me.isStore=Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
			    {'id' : 'Y','name' : '是'},
			    {'id' : 'N','name' : '否'}
			]
		});
		*/
		me.cols=[
		    {header:'缺陷id',dataIndex:'defectId',hidden:true},
		    {header:'风险id',dataIndex:'riskId',hidden:true},
		    {header:'流程节点id',dataIndex:'processPointId',hidden:true},
		    {header:'控制措施id',dataIndex:'measureId',hidden:true},
		    {header:'流程ID',dataIndex:'processId',hidden:true},
			{header:'缺陷关联Id',dataIndex:'assessRelaDefectId',hidden:true},
			{header:'流程分类', dataIndex: 'parentProcess', sortable: false,flex:1,hidden:true,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;  
				}
			},
			{header:'流程', dataIndex: 'processName', sortable: false,flex:2,hidden:true,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;  
				}
			},
			{header:'评价点', dataIndex: 'pointName',sortable: false,flex:2,hidden:false,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;  
				}
			},
			{header:'流程节点名称', dataIndex: 'processPointName',sortable: false,flex:2,hidden:true,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;  
				}
			},
			{header:'控制措施名称', dataIndex: 'measureName',sortable: false,flex:2,hidden:true,
				renderer : function(value, metaData, record, colIndex, store, view) { 
					if(value){
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
			    		return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showRiskRelaMeasure('" + record.data.measureId + "')\" >"+value+"</a> ";
			    	}
			    }
			},
			{header:'频率', dataIndex: 'probability',sortable: false,flex:1,
		    	renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="评价点的样本发生频率"';
					return value;
				}
		    },
		    {header:'影响', dataIndex: 'impact',sortable: false,flex:1,
		    	renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="评价点对流程的影响程度"';
					return value;
				}
		    },
			/*
			{header:'评价人',dataIndex:'executeEmpName',sortable: false,flex:2},
			{header:'评价日期',dataIndex:'assessDate',sortable: false,flex:2},
			{header:'复核人',dataIndex:'reviewerEmpName',sortable: false,flex:2},
			{header:'复核日期',dataIndex:'reviewDate',sortable: false,flex:2},
			*/
			{
    			header:'整改责任部门<font color=red>*</font>', 
    			dataIndex: 'orgId',
    			sortable: false,
    			flex:2,
    			editor:Ext.create('FHD.ux.org.DeptSelect',{
    				allowBlank : false,
		    		editable : false,
		    		mode : "local",
					triggerAction : "all",
		    		fieldLabel:''
		    	}),
		    	emptyCellText:'<font color="#808080">请选择</font>',
		    	renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'style="background-color:#FFFBE6"';
					var index = me.orgStore.find('id',value);
					var record = me.orgStore.getAt(index);
					if(record){
						return record.data.name;
					}else{
						if(value){
		    				return value;
		    			}else{
							metaData.tdAttr = 'data-qtip="整改责任部门是缺陷的责任部门，必填，评价完成后，缺陷要由整改责任部门进行整改" style="background-color:#FFFBE6"';
						}
					}
				}
        	},
			{
				header:'缺陷描述<font color=red>*</font>', 
				dataIndex: 'desc',
				sortable: false,
				flex:3,
				editor:{
					allowBlank : false
				},
				emptyCellText:'<font color="#808080">请填写</font>',
		    	renderer:function(value,metaData,record,colIndex,store,view) { 
					if(value){
						metaData.tdAttr = 'data-qtip="'+value+'" style="background-color:#FFFBE6"';
						return value;  
					}else{
						metaData.tdAttr = 'data-qtip="缺陷描述是对缺陷的详细描述，必填" style="background-color:#FFFBE6"';
					}
				}
			},
            {
				header:'缺陷级别<font color=red>*</font>', dataIndex: 'level',sortable: false,flex:2,
				editor:Ext.create('FHD.ux.dict.DictSelectForEditGrid',{
		    		dictTypeId:'ca_defect_level',
		    		allowBlank : false,
		    		editable : false,
		    		mode : "local",
					triggerAction : "all",
		    		fieldLabel:''
		    	}),
		    	emptyCellText:'<font color="#808080">请选择</font>',
		    	renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'style="background-color:#FFFBE6"';
					var index = me.levelStore.find('id',value);
					var record = me.levelStore.getAt(index);
					if(record){
						return record.data.name;
					}else{
						if(value){
		    				return value;
		    			}else{
							metaData.tdAttr = 'data-qtip="缺陷级别是描述缺陷的等级情况，必填" style="background-color:#FFFBE6"';
						}
					}
		    	}
		    },		    
		    {
		    	header:'缺陷类型<font color=red>*</font>', dataIndex: 'type',sortable: false,flex:2,
		    	editor:Ext.create('FHD.ux.dict.DictSelectForEditGrid',{
		    		dictTypeId:'ca_defect_type',
		    		allowBlank : false,
		    		editable : false,
		    		mode : "local",
					triggerAction : "all",
		    		fieldLabel:''
		    	}),
		    	emptyCellText:'<font color="#808080">请选择</font>',
		    	renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'style="background-color:#FFFBE6"';
					var index = me.typeStore.find('id',value);
					var record = me.typeStore.getAt(index);
					if(record){
						return record.data.name;
					}else{
						if(value){
		    				return value;
		    			}else{
							metaData.tdAttr = 'data-qtip="缺陷类型是描述缺陷的类别，必填，穿行测试默认为设计缺陷，抽样测试默认为执行缺陷" style="background-color:#FFFBE6"';
						}
					}
				}
		    }
		];
		
		if (me.feedbackIsAvailable) {
        	var whetherOrNotPass = {
				header:'是否通过', dataIndex: 'isAgree',sortable: false,flex:1.5,
				renderer:function(value){
					var index = me.isAgreeStore.find('id',value);
					var record = me.isAgreeStore.getAt(index);
					if(record!=null){
							return record.data.name;
					}else{
						return value;
					}
				}
			};
			var feedback= {
				header:'反馈意见', dataIndex: 'feedback',sortable: false,flex:2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;  
				}
			};
			var whetherOrNotRisk = {
				header:'风险识别', dataIndex: 'riskName',sortable: false,flex:2,
				/*
				editor:Ext.create('Ext.form.field.ComboBox',{
					store:me.isGoodStore,
					editable : false,
		    		mode : "local",
					triggerAction : "all",
					displayField: 'name',
				    valueField: 'id',
					allowBlank : false,
					listeners: {
				  		change: function(t, newValue, oldValue, options ){
				  			
				  		}
				  	}
				}),
				*/
			    renderer : function(value, metaData, record, colIndex, store, view) { 
					if(value){
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
						var valueNew = value.substring(0,value.length>10?10:value.length);
						if(value.length>10){
							valueNew +="...";
						}
			    		return " <a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').delRisk('" + record.data.riskId + "')\" >"+valueNew+"<font class='icon-close' style='cursor:pointer;'>&nbsp;&nbsp;&nbsp;&nbsp;</font></a>";
			    	}else{
			    		metaData.tdAttr = 'data-qtip="添加风险"'; 
			    		return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').addRisk()\" >添加风险</a> ";
			    	}
			    }
			};
			
            me.colInsert(17, whetherOrNotPass);
            me.colInsert(18, feedback);
            me.colInsert(19, whetherOrNotRisk);
        }
		
		Ext.applyIf(me,{
			layout:{
				type:'fit'
			},
			cols: me.cols,
            checked: me.checked
		});
		
		me.callParent(arguments);
	},
	colInsert: function (index, item) {
        if (index < 0) return;
        if (index > this.cols.length) return;
        for (var i = this.cols.length - 1; i >= index; i--) {
            this.cols[i + 1] = this.cols[i];
        }
        this.cols[index] = item;
    },
    listeners:{
		afterrender:function(me){
			me.orgStore.load({
				callback:function(){
					me.store.load();
				}
			});
		}
	},
	showRiskRelaMeasure:function(measureId){
		var me=this;
		
		me.showRiskRelaMeasureWindow = Ext.create('FHD.ux.Window',{
			title:'控制措施相关风险',
			collapsible:false,
			maximizable:true
    	});
		/*
		me.showRiskRelaMeasurePanel = Ext.create('FHD.view.icm.icsystem.FlowRiskRelaMeasureGrid',{
			measureId: measureId
		});
 		*/
		me.store = Ext.create('Ext.data.Store',{
        	pageSize: 100000,
        	idProperty: 'id',
        	fields: ['id', 'assessementStatus', 'etrend', 'name','parentName','respDeptName','measureStr'],
        	proxy: {
		        type: 'ajax',
		        url: __ctxPath + "/icm/control/findRiskRelaMeasureListByMeasureId.f",
		        extraParams:{
		        	measureId : measureId
		        },
		        reader: {
		            type : 'json',
		            root : 'datas',
		            totalProperty :'totalCount'
		        }
		    },
		    autoLoad:true
        });
		me.showRiskRelaMeasurePanel = Ext.create('Ext.grid.Panel', {
            loadMask: true,
	        store: me.store,
	        overflowX:'hidden',
	        overflowY:'auto',
	        columns: [
	            {
					header : "状态",
					dataIndex : 'assessementStatus',
					sortable : true,
					width : 40,
					renderer : function(v) {
						var color = "";
						var display = "";
						if (v == "icon-ibm-symbol-4-sm") {
							color = "symbol_4_sm";
							display = FHD.locale.get("fhd.alarmplan.form.hight");
						} else if (v == "icon-ibm-symbol-6-sm") {
							color = "symbol_6_sm";
							display = FHD.locale.get("fhd.alarmplan.form.low");
						} else if (v == "icon-ibm-symbol-5-sm") {
							color = "symbol_5_sm";
							display = FHD.locale.get("fhd.alarmplan.form.min");
						} else {
							v = "icon-ibm-symbol-0-sm";
							display = "无";
						}
						return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
								+ "background-position: center top;' data-qtitle='' "
								+ "class='"
								+ v
								+ "'  data-qtip='"
								+ display
								+ "'>&nbsp;</div>";
					}
				},
				{
					header : "趋势",
					dataIndex : 'etrend',
					sortable : true,
					width : 40,
					renderer : function(v) {
						var color = "";
						var display = "";
						if (v == "up") {
							color = "icon-ibm-icon-trend-rising-positive";
							display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
						} else if (v == "flat") {
							color = "icon-ibm-icon-trend-neutral-null";
							display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
						} else if (v == "down") {
							color = "icon-ibm-icon-trend-falling-negative";
							display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
						}
						return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
								+ "background-position: center top;' data-qtitle='' "
								+ "class='"
								+ color
								+ "'  data-qtip='"
								+ display
								+ "'>&nbsp;</div>";
					}
				}, {
					header : '名称',
					dataIndex : 'name',
					sortable : true,
					flex : 1,
					align : 'left'
				}, {
					header : '所属风险',
					dataIndex : 'parentName',
					sortable : true,
					flex : 1
				}, {
					header : '责任部门',
					dataIndex : 'respDeptName',
					sortable : true,
					flex : 1
				}, {
					dataIndex:'measureStr',
					hidden:true
				}, {
	            	dataIndex:'id',
	            	hidden:true
	            }
	        ],
	        plugins: [{
	            ptype: 'rowexpander',
	            rowBodyTpl : new Ext.XTemplate(
	            	'<p><b>风险"{name}"的相关控制措施:</b></p>',
	                '<p>{measureStr}</p>'
	            )
	        }]
	    });
		me.showRiskRelaMeasureWindow.add(me.showRiskRelaMeasurePanel);
		me.showRiskRelaMeasureWindow.show();
	},
	addRisk:function(){
		var me=this;
		
		var selection=me.getSelectionModel().getSelection();
		var orgId=selection[0].get('orgId');
		var desc=selection[0].get('desc');
		var level=selection[0].get('level');
		var probability=selection[0].get('probability');
		FHD.ajax({
			url:__ctxPath+'/icm/assess/defectToRisk.f',
			params:{
				probability:probability,
				defectLevel:level
			},
			callback:function(data){
				if(data){
					/**
					 * data.probability 风险发生可能性
					 * data.impact 风险的影响程度
					 * 风险的添加组件支持风险发生可能性与影响程度时，data.probability和data.impact当做默认值赋值给风险添加组件
					 */
					me.riskPanelWindow = Ext.create('FHD.ux.Window',{
						title:'添加风险',
						collapsible:false,
						maximizable:true,
						buttons:[{
							buttonAlign:'center',
							text:'保存',
							handler:function(){
								me.riskPanel.save(me.riskPanel.callback);
							}
						}]
			    	});
					//创建添加风险panel
					me.riskPanel = Ext.create('FHD.view.risk.cmp.form.RiskShortForm',{
						type:'re',
						hiddenSaveBtn:false,
						callback:function(data){
							if(data){
								//FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								//关闭弹窗
								me.riskPanelWindow.close();
								//渲染风险识别列：附件源文件名称
								selection[0].set("riskId",data.id);
								selection[0].set("riskName",data.name);
								//me.getStore().commitChanges();
							}
						}
					});
					me.riskPanelWindow.add(me.riskPanel);
					me.riskPanelWindow.show();
					me.riskPanel.resetData();
					//给责任部门赋初始值，郑军祥添加
					me.riskPanel.respDeptName.clearValues();
					var value = [];
		        	var obj = {};
		        	obj["deptid"] = orgId;
		        	obj["empid"] = null;
		        	value.push(obj);
		        	me.riskPanel.respDeptName.setHiddenValue(value);
					me.riskPanel.respDeptName.initValue(Ext.encode(value));
					me.riskPanel.form.setValues({
	        			name : desc
	        		});
				}
			}
		});
	},
	delRisk:function(){
		var me=this;
		
		var selection=me.getSelectionModel().getSelection();
		var riskId=selection[0].get('riskId');
		FHD.ajax({
			url:__ctxPath+'/cmp/risk/removeRiskByIds.f',
			params:{
				ids:riskId
			},
			callback:function(data){
				if(data){
					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
					selection[0].set("riskId", "");
					selection[0].set("riskName", "");
					//me.getStore().commitChanges();
				}
			}
		});
	},
    reloadData:function(){
    	var me=this;
    	
    	me.store.load();
    }
});