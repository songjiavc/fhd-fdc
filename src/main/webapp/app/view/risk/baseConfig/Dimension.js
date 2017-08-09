
Ext.define('FHD.view.risk.baseConfig.Dimension', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.dimension',
    
    requires: [
               
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	var dimensionGrid,
	    	scoreGrid,
	    	saveDimensionByIdURL = 'risk/saveDimensionById.f',//拷贝维度的url
	    	mergeDimensionBatchURL = 'risk/mergeDimensionBatch.f',//保存维度的url
	    	removeDimensionByIdURL = 'risk/removeDimensionById.f',//删除维度的url
	    	findDimensionListURL = 'risk/findDimensionList.f',//查询维度的url
	    	mergeScoreBatchURL = 'risk/mergeScoreBatch.f',//保存维度分值的url
	    	removeScoreByIdURL = 'risk/removeScoreById.f',//删除维度分值的url
	    	findScoreListURL = 'risk/findScoreList.f',//查询维度分值的url
	        deleteStatusOfDimensionStore = Ext.create('Ext.data.Store',{//myLocale的store
	    		fields : ['id', 'name'],
	    		data : [{'id' : '0','name' : FHD.locale.get('fhd.common.useless')},{'id' : '1','name' : FHD.locale.get('fhd.common.useful')}]
	    	});
    	
    	function addDimension(){//新增维度方法
    		var count = dimensionGrid.getStore().getCount();// 获得总行数
    	    var maxSort;
    	    if("0" != count){
    	    	maxSort = dimensionGrid.getStore().getAt(count-1).get("sort");
    	    }else{
    	    	maxSort=0;
    	    }
    		var rec = Ext.create('Dimension',{
    			code : FHD.locale.get('fhd.risk.baseconfig.newCode'),
    			name : FHD.locale.get('fhd.risk.baseconfig.newName'),
    			deleteStatus:'1',
    			sort:maxSort+1
    		});
    		dimensionGrid.store.insert(count, rec);
    		dimensionGrid.editingPlugin.startEditByPosition({row:count,column:0}); 
    	} 
    	function addScore(){//新增维度分值方法
    		var count = scoreGrid.getStore().getCount();// 获得总行数
    	    var maxSort;
    	    if("0" != count){
    	    	maxSort = scoreGrid.getStore().getAt(count-1).get("sort");
    	    }else{
    	    	maxSort=0;
    	    }
    	    var rec = Ext.create('Score',{
    	    	name : FHD.locale.get('fhd.risk.baseconfig.newName'),
    			value: maxSort+1,
    			sort:maxSort+1
    		});
    		scoreGrid.store.insert(count, rec);
    		scoreGrid.editingPlugin.startEditByPosition({row:count,column:0}); 
    	} 
    	function saveDimensionById(){//拷贝维度方法
    		var dimensionRows = dimensionGrid.store.getModifiedRecords();
    		var selection = dimensionGrid.getSelectionModel().getSelection()[0];
    		if(dimensionRows != null && dimensionRows.length){
    			FHD.notification(FHD.locale.get('fhd.risk.baseconfig.saveFirst'),FHD.locale.get('fhd.common.prompt'));
    			return;
    		}
    		FHD.ajax({
    			url : saveDimensionByIdURL,
    			params : {
    				dimensionId:selection.get('id')
    			},
    			callback : function(data){
    				if(data){
    					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    					dimensionGrid.store.load();
    				}
    			}
    		});
    	}
    	function mergeDimensionBatch(){//保存维度方法
    		var rows = dimensionGrid.store.getModifiedRecords();
    		var jsonArray=[];
    		Ext.each(rows,function(item){
    			if(item.data.code &&　item.data.name){
    				jsonArray.push(item.data);
    			}
    		});
    		FHD.ajax({
    			url : mergeDimensionBatchURL,
    			params : {
    				jsonString:Ext.encode(jsonArray)
    			},
    			callback : function(data){
    				if(data){
    					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    					dimensionGrid.store.load();
    				}
    			}
    		})
    		dimensionGrid.store.commitChanges(); 
    	}
    	function mergeScoreBatch(){//保存维度分值方法
    		var scoreRows = scoreGrid.store.getModifiedRecords();
    		var selection = dimensionGrid.getSelectionModel().getSelection()[0];
    		var jsonArray=[];
    		Ext.each(scoreRows,function(item){
    			if(item.data.value &&　item.data.name){
    				jsonArray.push(item.data);
    			}
    		});
    		FHD.ajax({
    			url : mergeScoreBatchURL,
    			params : {
    				jsonString:Ext.encode(jsonArray),
    				dimensionId:selection.get('id')
    			},
    			callback : function(data){
    				if(data){
    					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    					scoreGrid.store.load();
    				}
    			}
    		})
    		scoreGrid.store.commitChanges(); 
    	}
    	function removeDimensionById(){//删除维度方法
    		var dimensionRows = dimensionGrid.store.getModifiedRecords();
    		var selection = dimensionGrid.getSelectionModel().getSelection()[0];
    		if(dimensionRows != null && dimensionRows.length){
    			FHD.notification(FHD.locale.get('fhd.risk.baseconfig.saveFirst'),FHD.locale.get('fhd.common.prompt'));
    			return;
    		}
    		Ext.MessageBox.show({
    			title : FHD.locale.get('fhd.common.delete'),
    			width : 260,
    			msg : FHD.locale.get('fhd.risk.baseconfig.deleteDimensionConfirm'),
    			buttons : Ext.MessageBox.YESNO,
    			icon : Ext.MessageBox.QUESTION,
    			fn : function(btn) {
    				if (btn == 'yes') {
    					FHD.ajax({
    						url : removeDimensionByIdURL,
    						params : {
    							dimensionId:selection.get('id')
    						},
    						callback : function(data){
    							if(data){
    								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    								dimensionGrid.store.load();
    								scoreGrid.store.load();
    							}
    						}
    					});
    				}
    			}
    		});
    	}
    	function removeScoreById(){//删除维度分值方法
    		var scoreRows = scoreGrid.store.getModifiedRecords();
    		var selection = scoreGrid.getSelectionModel().getSelection()[0];
    		if(scoreRows != null  && scoreRows.length){
    			FHD.notification(FHD.locale.get('fhd.risk.baseconfig.saveFirst'),FHD.locale.get('fhd.common.prompt'));
    			return;
    		}
    		Ext.MessageBox.show({
    			title : FHD.locale.get('fhd.common.delete'),
    			width : 260,
    			msg : FHD.locale.get('fhd.common.makeSureDelete'),
    			buttons : Ext.MessageBox.YESNO,
    			icon : Ext.MessageBox.QUESTION,
    			fn : function(btn) {
    				if (btn == 'yes') {
    					FHD.ajax({
    						url : removeScoreByIdURL,
    						params : {
    							scoreId:selection.get('id')
    						},
    						callback : function(data){
    							if(data){
    								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
    								scoreGrid.store.load();
    							}
    						}
    					});
    				}
    			}
    		});
    	}
    	function onDimensionchange(){//设置维度按钮可用状态
    		var selectionLength = dimensionGrid.getSelectionModel().getSelection().length;
    		if(Ext.getCmp('delDimension') != null){
    			Ext.getCmp('delDimension').setDisabled(selectionLength=== 0);
    		}
    		if(Ext.getCmp('addDimension') != null){
    			Ext.getCmp('addDimension').setDisabled(selectionLength === 0);
    		}
    		if(Ext.getCmp('saveDimension') != null){
    			Ext.getCmp('saveDimension').setDisabled(dimensionGrid.store.getModifiedRecords().length === 0);
    		}
    	}
    	function onScorechange(){//设置维度分值按钮可用状态
    		if(Ext.getCmp('addScore') != null){
    			Ext.getCmp('addScore').setDisabled(dimensionGrid.getSelectionModel().getSelection().length === 0);
    		}
    		if(Ext.getCmp('delScore') != null){
    			Ext.getCmp('delScore').setDisabled(scoreGrid.getSelectionModel().getSelection().length === 0);
    		}
    		if(Ext.getCmp('saveScore') != null){
    			Ext.getCmp('saveScore').setDisabled(scoreGrid.store.getModifiedRecords().length === 0);
    		}
    	}
    	
    	Ext.define('Dimension', {//定义model
	    extend: 'Ext.data.Model',
	   	fields: [{name: 'id', type: 'string'},
		        {name: 'code', type: 'string'},
		        {name: 'name', type:'string'},
		        {name: 'desc', type:'string'},
		        {name: 'sort',type: 'int'},
		        {name: 'deleteStatus',type: 'string'}]
	});
	dimensionGrid = Ext.create('FHD.ux.EditorGridPanel',{//创建可编辑的grid列表，注释同不可编辑的gird
		region: 'north',
		flex: 1.5,
		multiSelect:false,
		checked:false,
		url: findDimensionListURL,
		pagable:false,
		cols:[{dataIndex:'id',width:0},
			{header: FHD.locale.get('fhd.risk.baseconfig.code')+'<font color=red>*</font>', dataIndex: 'code', hideable:false, sortable: false, flex : 1,editor: {allowBlank: false},
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+ value +'"';  
					return value;
				}
		    },
			{header: FHD.locale.get('fhd.risk.baseconfig.name')+'<font color=red>*</font>', dataIndex: 'name', hideable:false, sortable: false, flex : 1,editor: {allowBlank: false},
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+ value +'"';  
					return value;
				}
		    },
			{header: FHD.locale.get('fhd.risk.baseconfig.desc'), dataIndex: 'desc', sortable: false, flex : 3,editor: {},
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+ value +'"';  
					return value;
				}
		    },
			{header: FHD.locale.get('fhd.risk.baseconfig.deletestatus'), dataIndex: 'deleteStatus', sortable: false, width: 60,editor:
				new Ext.form.ComboBox({
					store :deleteStatusOfDimensionStore,
					valueField : 'id',
					displayField : 'name',
					selectOnTab: true,
					lazyRender: true,
					typeAhead: true,
					allowBlank : false,
					editable : false
				}),
				renderer:function(value){
					var index = deleteStatusOfDimensionStore.find('id',value);
					var record = deleteStatusOfDimensionStore.getAt(index);
					if(record!=null){
						return record.data.name;
					}else{
						return '';
					}
				}
			},
			{header: FHD.locale.get('fhd.risk.baseconfig.sort'),dataIndex: 'sort', sortable: false, width: 60,editor:{
				xtype:'numberfield',
				minValue: 1,  
				allowDecimals: false, // 允许小数点 
				nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
				//hideTrigger: true,  //隐藏上下递增箭头
				keyNavEnabled: true,  //键盘导航
				mouseWheelEnabled: true,  //鼠标滚轮
				step:1
			}},
	        {
				header: FHD.locale.get('fhd.common.operate'),dataIndex:'',width:60,hideable:false, 
				align: 'center',
				xtype:'actioncolumn',
				items: [{
	                icon: __ctxPath+'/images/icons/edit.gif',  // Use a URL in the icon config
	                tooltip: FHD.locale.get('fhd.common.edit'),
	                handler: function(grid, rowIndex, colIndex) {
	                	//点击编辑按钮时，自动选中行
                    	grid.getSelectionModel().deselectAll();
    					var rows=[grid.getStore().getAt(rowIndex)];
    	    			grid.getSelectionModel().select(rows,true);
    	    			
	                    var rec = grid.getStore().getAt(rowIndex);
	                    scoreGrid.getStore().on('beforeload', function (store, options) {
	                	    var new_params = { dimensionId: rec.data.id};
	                	    Ext.apply(store.proxy.extraParams,null);
	            	        Ext.apply(store.proxy.extraParams, new_params);
	                	});
	                    scoreGrid.getStore().load();
	                    onScorechange();
	                }
	            }]
	        }],
		tbarItems:[FHD.locale.get('fhd.risk.baseconfig.dimensionManage'),'-'
			,{text : FHD.locale.get('fhd.common.add'),iconCls: 'icon-add',
			handler:addDimension, scope : this},'-'
			,{text : FHD.locale.get('fhd.common.delete'),iconCls: 'icon-del',id:'delDimension', 
				handler:removeDimensionById, disabled : true, scope : this},'-'
			,{text : FHD.locale.get('fhd.common.save'),iconCls: 'icon-save',id:'saveDimension',
					handler:mergeDimensionBatch, disabled : true, scope : this},'-'
			,{text : FHD.locale.get('fhd.common.copyEntity'),iconCls: 'icon-other-copy',id:'addDimension',
						handler:saveDimensionById, disabled : true, scope : this}
			  
		]
	});
	dimensionGrid.store.on('load',onDimensionchange);//执行store.load()时改变按钮可用状态
	dimensionGrid.on('selectionchange',onDimensionchange);//选择记录发生改变时改变按钮可用状态
	dimensionGrid.store.on('update',onDimensionchange);//修改时改变按钮可用状态
	/***dimensionGrid end***/
	/***scoreGrid start***/
	Ext.define('Score', {//定义model
	    extend: 'Ext.data.Model',
	   	fields: [{name: 'id', type: 'string'},
		        {name: 'name', type:'string'},
		        {name: 'value'},
		        {name: 'desc', type:'string'},
		        {name: 'sort',type: 'int'}]
	});
	scoreGrid = Ext.create('FHD.ux.EditorGridPanel',{//创建可编辑的grid列表，注释同不可编辑的gird
		url: findScoreListURL,
		flex: 2,
		multiSelect:false,
		padding:'1 0 0 0',
		region: 'center', 
		pagable:false,
		checked:false,
		cols:[{dataIndex:'id',width:0},
			{header: FHD.locale.get('fhd.risk.baseconfig.value')+'<font color=red>*</font>',dataIndex: 'value', hideable:false, sortable: false, width: 60,editor:{
				xtype:'numberfield',
				minValue: 0,  
				allowBlank: false,
				allowDecimals: true, // 允许小数点 
				nanText: FHD.locale.get('fhd.risk.baseconfig.inputNumber'), 
				//hideTrigger: true,  //隐藏上下递增箭头
				keyNavEnabled: true,  //键盘导航
				mouseWheelEnabled: true,  //鼠标滚轮
				step:1
			}},
			{header: FHD.locale.get('fhd.risk.baseconfig.name')+'<font color=red>*</font>', dataIndex: 'name', hideable:false,sortable: false,flex : 1,editor: {allowBlank: false}},
			{header: FHD.locale.get('fhd.risk.baseconfig.desc'), dataIndex: 'desc', sortable: false, flex : 4,editor: {},
				renderer:function(value,metaData,record,colIndex,store,view) { 
					metaData.tdAttr = 'data-qtip="'+ value +'"';  
					return value;
				}
		    },
			{header: FHD.locale.get('fhd.risk.baseconfig.sort'),dataIndex: 'sort', sortable: false, width: 60,editor:{
				xtype:'numberfield',
				minValue: 1,  
				allowDecimals: false, // 允许小数点 
				nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
				//hideTrigger: true,  //隐藏上下递增箭头
				keyNavEnabled: true,  //键盘导航
				mouseWheelEnabled: true,  //鼠标滚轮
				step:1
			}}],
		tbarItems:[FHD.locale.get('fhd.risk.baseconfig.dimensionScoreManage'),'-'
			,{text : FHD.locale.get('fhd.common.add'),iconCls: 'icon-add',id:'addScore', 
			handler:addScore, disabled : true, scope : this},'-'
			,{text : FHD.locale.get('fhd.common.save'),iconCls: 'icon-save',id:'saveScore',
				handler:mergeScoreBatch, disabled : true, scope : this},'-'
			,{text : FHD.locale.get('fhd.common.delete'),iconCls: 'icon-del',id:'delScore', 
					handler:removeScoreById, disabled : true, scope : this}  
		]
	});
	scoreGrid.store.on('load',onScorechange);//执行store.load()时改变按钮可用状态
	scoreGrid.on('selectionchange',onScorechange);//选择记录发生改变时改变按钮可用状态
	scoreGrid.store.on('update',onScorechange);//修改时改变按钮可用状态
    	
    	Ext.apply(me, {
    		layout: 'border',
            border:false,
     	    items:[dimensionGrid, scoreGrid]
        });
    	
        me.callParent(arguments);
        
    }
});