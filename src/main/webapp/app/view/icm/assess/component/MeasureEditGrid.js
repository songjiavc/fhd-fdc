/*
 * 控制措施可编辑列表
 * */
Ext.define('FHD.view.icm.assess.component.MeasureEditGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.measureeditgrid',
	
	url: __ctxPath + '/icm/assess/findAssessResultPageBySome.f',
	extraParams:{
		assessPlanId:'',
		processId:'',
		assessorId:'',
		testType:'ca_assessment_measure_1',
		isAll:''
	},
	cols:[],
	tbar:[],
	tbarItems:[],
	pagable:false,
	checked:false,
	
	initComponent:function(){
		var me=this;
		
		me.extraParams.assessPlanId=me.businessId;
		me.extraParams.processId=me.processId;
		me.extraParams.testType=me.testType;
		me.extraParams.assessorId = me.assessorId;
		me.extraParams.isAll = me.isAll;

		me.isGoodStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
			    {'id' : true,'name' : '有效'},
			    {'id' : false,'name' : '无效'}
			]
		});
		me.defectStore=Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath + '/icm/defect/findAllDefect.f'
			}
		});
		
		me.defectStore.load();
		
		me.cols=[
			{header:'操作', dataIndex: '',sortable: false,width:70,  
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="请点击此处"';
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.parentId+"').showSampleWindow('" + record.data.assessResultId + "','sampling','"+me.processId+"')\" >样本测试</a>";
				}
			},
			{header:'控制措施', dataIndex: 'measureName', sortable: false,flex:2,hidden:true,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;  
				}
			},
			{header:'评价点', dataIndex: 'assessPointDesc',sortable: false,flex:2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;  
				}
			},
			{header:'实施证据', dataIndex: 'assessSampleName',sortable: false,flex:2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;  
				}
			},
			{header:'有效 | 无效 | 不适用', dataIndex: 'assessSampleName',sortable: false,width:123,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					return  record.data.qualifiedNumber+"|"+record.data.notQualifiedNumber+"|"+record.data.notApplyNumber;
				}
			},
			{header:'结果', dataIndex: 'hasDefect',sortable: false,width:50,  
				renderer:function(value,metaData,record,colIndex,store,view) { 
					var index = me.isGoodStore.find('id',value);
					var record = me.isGoodStore.getAt(index);
					if(record){
		    			return record.data.name;
					}else{
		    			 return value;
					}
				}
			},
			{header:'调整结果', dataIndex: 'hasDefectAdjust',sortable: false,width:80, 
				editor:new Ext.form.ComboBox({
					store :me.isGoodStore,
					valueField : 'id',
					displayField : 'name',
					selectOnTab: true
				}),
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'style="background-color:#FFFBE6"';
					var index = me.isGoodStore.find('id',value);
					var record = me.isGoodStore.getAt(index);
		    		if(record){
		    			return record.data.name;
		    		}else{
		    			if(value){
		    				return value;
		    			}else{
							metaData.tdAttr = 'data-qtip="如需调整结果，请点击这里，否则，无需选择" style="background-color:#FFFBE6"';
						}
		    			
		    		}
				}
			},
			{header:'调整说明', dataIndex: 'adjustDesc',sortable: false,flex:1,editor: {},
				renderer:function(value,metaData,record,colIndex,store,view) { 
					if(value){
						metaData.tdAttr = 'data-qtip="'+value+'" style="background-color:#FFFBE6"';
						return value;  
					}else{
						metaData.tdAttr = 'data-qtip="如果调整了评价结果，请填写“补充说明”，否则，无需填写" style="background-color:#FFFBE6"';
					}
					
				}
			},
			{header:'缺陷描述', dataIndex: 'comment',sortable: false,flex:2,editor: {},
				renderer:function(value,metaData,record,colIndex,store,view) { 
					if(value){
						metaData.tdAttr = 'data-qtip="'+value+'" style="background-color:#FFFBE6"';
						return value;  
					}else{
						metaData.tdAttr = 'data-qtip="如果控制无效，请填写“缺陷描述”，否则，无需填写" style="background-color:#FFFBE6"';
					}
				}
			},
			{header:'影响程度', dataIndex: 'impact',sortable: false,width:80, 
				editor: {
		    		allowBlank: true,
		    		xtype: 'numberfield',
			        minValue:0,
			        maxValue:5
		    	},
				renderer:function(value,metaData,record,colIndex,store,view) { 
					if(value){
						metaData.tdAttr = 'data-qtip="'+value+'" style="background-color:#FFFBE6"';
						return value;  
					}else{
						metaData.tdAttr = 'data-qtip="如控制无效，请填写“影响程度”，为0~5之间的数，否则，无需填写" style="background-color:#FFFBE6"';
					}
				}
			},

			{dataIndex:'assessResultId',hidden:true},
			{dataIndex:'effectiveNumber',hidden:true},
			{dataIndex:'defectNumber',hidden:true},
			{dataIndex:'qualifiedNumber',hidden:true},
			{dataIndex:'notQualifiedNumber',hidden:true},
			{dataIndex:'notApplyNumber',hidden:true},
			{dataIndex:'testType',hidden:true},
			{dataIndex:'assessPointId',hidden:true},
			{header:'控制措施ID', dataIndex: 'measureId',hidden:true}
	    ];
		/*
	    me.on('select',function(){
			var selectionDate=me.getSelectionModel().getSelection();
			if(null!=selectionDate[0].get('assessResultId')){
				self4.assessResultId=selectionDate[0].get('assessResultId');	
			}
		});
		*/
		me.callParent(arguments);
	},
	/*
	listeners:{
		afterrender:function(me){
			me.isGoodStore.load({
				callback:function(){
					//me.loadData(me.businessId,me.processId);
					me.store.load();
				}
			});
		}
	},
	*/
	loadData:function(businessId,processId){
		var me=this;
		
		me.extraParams.assessPlanId=businessId;
		me.extraParams.processId=processId;
		me.store.load();
	},
	reloadData:function(){
    	var me=this;
    	me.store.load();
    }
});