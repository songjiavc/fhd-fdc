Ext.define('AssessGuidelinesDataMapping', {
	 extend: 'Ext.data.Model',
	 fields:['id','name','comment','sort', 'dictype']
});
Ext.define('FHD.view.icm.assess.baseset.AssessGuidelinesEditGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.assessguidelineseditgrid',
	
	url: __ctxPath + '/icm/assess/baseset/findAssessGuidelinesBySome.f',
	region:'center',
	objectType:{},
	pagable : false,
	searchable:false,
	layout: 'fit',
	flex: 1,
	
	addGrid:function(){
		var me = this;
		
		var count = me.store.data.length;
		var maxSort = me.getStore().getAt(count-1) && me.getStore().getAt(count-1).get("sort") || 0;
		var r = Ext.create('AssessGuidelinesDataMapping',{
			//新增时初始化参数
			id : '',
			sort: maxSort+1,
			name:'模板名称',
			dictype:'nonfinancial_defect'
		});
		me.store.insert(count, r);
		//alert(me.store.data.length);
		me.editingPlugin.startEditByPosition({row:count,column:0});
		//me.store.commitChanges();
	},
	saveGrid:function(){
		var me = this;
		
		var rows = me.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		//if(!this.validate(jsonArray)) return ;
		
		FHD.ajax({
			url: __ctxPath + '/icm/assess/baseset/saveAssessGuidelines.f',
			params : {
				modifyRecords:Ext.encode(jsonArray)
			},
			callback : function(data){
				if(data){
					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
					me.store.load();
				}
			}
		});
		me.store.commitChanges();
	},
	delGrid:function(){//删除方法
		var me = this;
		
		var selection = me.getSelectionModel().getSelection();
		if(!selection.length){
			return;
		}
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : '评价模板删除，相对应的模板项也需要删除，你确定删除吗？',
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {
					var ids = [];
					for(var i=0;i<selection.length;i++){
						var delId = selection[i].get('id');
						if(delId){
							ids.push(delId);
						}else {
							Ext.Msg.alert('提示','没有保存的记录无法删除!');
							return false;
						}
					}
					FHD.ajax({
						url : __ctxPath + '/icm/assess/baseset/delAssessGuidelinesById.f',
						params : {
							id : ids.join(',')
						},
						callback: function (data) {
		                    if (data) {
		                    	me.reloadData();
		                    	me.assessguidelinespropertyeditgrid && me.assessguidelinespropertyeditgrid.reloadData();
		                    	me.assessguidelinespropertyeditgrid && me.up('assessguidelinesmainpanel').remove(me.assessguidelinespropertyeditgrid);
		                    	
		                        FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
		                    }else{
		                    	FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
		                    }
		                }
					});
				}
			}
		});
	},
	setstatus:function(){
		var me = this;
		
	    var length = me.getSelectionModel().getSelection().length;
	    var rows = me.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		me.down('[name=icm_assessguidelines_save]').setDisabled(jsonArray.length === 0);
		me.down('[name=icm_assessguidelines_del]').setDisabled(length === 0);
	},
	//初始化
	initComponent:function(){
		var me=this;
		
		var assessStore = Ext.create('Ext.data.Store', {
		    fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath + '/sys/dic/findDictEntryBySome.f',
				extraParams:{
					typeId: 'assessment_temp_type'
				},
				reader:{
					type:'json',
					root:'children'
				}
			},
			autoload:false
		});
		assessStore.load();
		
		me.tbarItems = ['评价模板列表','-',{
			iconCls : 'icon-add',
			text:'添加',
			name: 'icm_assessguidelines_add',
			tooltip: '添加评价模板',
			scope : this,
			handler: function(){
				me.addGrid();
			}
		}, '-', {
			iconCls : 'icon-del',
			text:'删除',
			name: 'icm_assessguidelines_del',
			tooltip: '删除评价模板',
			scope : this,
			handler: function(){
				me.delGrid();
			},
			disabled:true
		}, '-', {
			iconCls : 'icon-save',
			text:'保存',
			name: 'icm_assessguidelines_save',
			tooltip: '保存评价模板',
			scope : this,
			handler: function(){
				me.saveGrid();
			},
			disabled:true
		}];
		
		me.cols=[{
			header:'评价标准模板类型'+'<font color=red>*</font>',
			dataIndex:'dictype',
			hidden:false,
			width:140,
			editor: Ext.create('FHD.ux.dict.DictSelectForEditGrid',{
				dictTypeId:'assessment_temp_type',
				fieldLabel:'',
				editable:false
			}),
			allowBlank:false,
	    	renderer:function(value){
			    var curModel = assessStore.findRecord("id",value);
			    if(curModel!=null){
			    	return curModel.raw.name;
				}
	    	}
	   	},
	   	{
	   		header:'评价标准名称'+'<font color=red>*</font>',
	   		dataIndex:'name',
	   		hidden:false,
	   		editor: {allowBlank: false},
	   		flex:1,
	   		renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
	   	},
	   	{
	   		header:'说明',
	   		dataIndex:'comment',
	   		hidden:false,
	   		editor:true,
	   		flex:1,
	   		renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
	   	},
		{
	   		header:'排序',
	   		dataIndex:'sort',
	   		hidden:false,
	   		editor:{
				xtype:'numberfield',
				allowBlank:false,
				minValue: 1,  
				allowDecimals: false, // 允许小数点 
				nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
				//hideTrigger: true,  //隐藏上下递增箭头
				keyNavEnabled: true,  //键盘导航
				mouseWheelEnabled: true,  //鼠标滚轮
				step:1
	   		}
	   	},
	   	{
	   		header:'操作',
	   		dataIndex:'',
	   		hidden:false,
	   		editor:false,
	   		align:'center',
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
	    			var assessguidelinesmainpanel = me.up('assessguidelinesmainpanel');
	    			// assessguidelinesmainpanel.assessguidelinespropertyeditgrid && assessguidelinesmainpanel.assessguidelinespropertyeditgrid.remove(true);
                	   
	    			me.assessguidelinespropertyeditgrid && assessguidelinesmainpanel.remove(me.assessguidelinespropertyeditgrid);
            	   
	    			me.assessguidelinespropertyeditgrid = Ext.create('FHD.view.icm.assess.baseset.AssessGuidelinesPropertyEditGrid',{
                		parentId:rec.data.id,
                		parentName:rec.data.name,
                		as_tmlt_type:rec.data.dictype
	    			});
	    			assessguidelinesmainpanel.add(me.assessguidelinespropertyeditgrid);
                   
	    			me.assessguidelinespropertyeditgrid.parentId = rec.data.id;
	    			me.assessguidelinespropertyeditgrid.parentName = rec.data.name;
	    			me.assessguidelinespropertyeditgrid.as_tmlt_type = rec.data.dictype;//赋值模板类型
	    			//me.assessguidelinespropertyeditgrid.down('[name=icm_assessguidelinesprop_add]').setDisabled(false);
	    			me.assessguidelinespropertyeditgrid.reloadData(rec.data.id);
                }
            }]
		}];
		me.on('selectionchange', function(){
			me.setstatus();
	    });
		me.on('edit', function(){
	        me.setstatus();
	    });
		
		me.callParent(arguments);
	},
	reloadData: function(){
		var me = this;
		
		me.store.load();
	}
});