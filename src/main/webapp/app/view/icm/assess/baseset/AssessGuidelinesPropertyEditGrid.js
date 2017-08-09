Ext.define('AssessGuidelinesPropertyDataMapping', {
    extend: 'Ext.data.Model',
    fields:['id','content','minValue','maxValue','judgeValue','sort', 'dictype','parentId','parentName']
});
Ext.define('FHD.view.icm.assess.baseset.AssessGuidelinesPropertyEditGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.assessguidelinespropertyeditgrid',
	
	findUrl: __ctxPath + "/icm/assess/baseset/findAssessGuidelinesPropertiesByAGId.f",
	region:'center',
	objectType:{},
	pagable : false,
	searchable:false,
	//height: 240,
	flex: 1,
	layout: 'fit',
	margin: '2 0 0 0',
	
	addGrid:function(){
		var me = this;
		var count = me.store.data.length;
		var maxSort = me.getStore().getAt(count-1) && me.getStore().getAt(count-1).get("sort") || 0;
		var r = Ext.create('AssessGuidelinesPropertyDataMapping',{
			//新增时初始化参数
			id:'',
			parentId : me.parentId,
			parentName : me.parentName,
			minValue: 1,
			maxValue: 1,
			judgeValue: 1,
			sort: maxSort+1,
			dictype :'ca_defect_level_0',
			content : '描述'
		});
		me.store.insert(count, r);
		me.editingPlugin.startEditByPosition({row:count,column:0});
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
			url: __ctxPath + '/icm/assess/baseset/saveAssessGuidelinesProperty.f',
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
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
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
						url : __ctxPath + '/icm/assess/baseset/delAssessGuidelinesPropertyById.f',//删除
						params : {
							ids : ids.join(',')
						},
						callback: function (data) {
			                if (data) {
			                	me.reloadData();
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
	setstatus: function(){
		var me = this;
		
	    var length = me.getSelectionModel().getSelection().length;
	    var rows = me.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		me.down('[name=icm_assessguidelinesprop_save]').setDisabled(jsonArray.length === 0);
		me.down('[name=icm_assessguidelinesprop_del]').setDisabled(length === 0);
	},
	initComponent:function(){//初始化
		var me=this;
	
		var assessStore = Ext.create('Ext.data.Store', {
		    fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath + '/sys/dic/findDictEntryBySome.f',
				extraParams:{
					typeId: 'ca_defect_level'
				},
				reader:{
					type:'json',
					root:'children'
				}
			},
			autoload:false
		});
		assessStore.load();
		me.tbarItems = ['模板项列表','-',{
			text:'添加',
			iconCls : 'icon-add',
			name: 'icm_assessguidelinesprop_add',
			tooltip: '添加模板项',
			handler: function(){
				me.addGrid();
			},
			scope : this
		}, '-', {
			text:'删除',
			iconCls : 'icon-del',
			name: 'icm_assessguidelinesprop_del',
			tooltip: '删除模板项',
			handler: function(){
				me.delGrid();
			},
			disabled :true,
			scope : this
		}, '-', {
			text:'保存',
			iconCls : 'icon-save',
			name: 'icm_assessguidelinesprop_save',
			tooltip: '保存模板项',
			handler: function(){
				me.saveGrid();
			},
			disabled :true,
			scope : this
		}];
	
		me.cols=[ 
	        'sample_assessment_standard'!=me.as_tmlt_type?{header:'缺陷等级 '+'<font color=red>*</font>',dataIndex:'dictype',hidden:false,width:100,
	        editor:Ext.create('FHD.ux.dict.DictSelectForEditGrid',{
			    dictTypeId:'ca_defect_level',
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
	    }:{dataIndex:'dictype',hidden:true},
	    //{header:'所属评价标准模板',dataIndex:'parentName',hidden:false,editor:false,flex:1},
	    {dataIndex:'parentId',hidden:true},
		{header:'描述 '+'<font color=red>*</font>',dataIndex:'content',hidden:false,editor: {allowBlank: false},flex:2},
	    'sample_assessment_standard'==me.as_tmlt_type?{header:'最小值 '+'<font color=red>*</font>',dataIndex:'minValue',hidden:false,editor:{
			xtype:'numberfield',
			allowBlank:false,
			minValue: 1,  
			allowDecimals: true, // 允许小数点 
			nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
			//hideTrigger: true,  //隐藏上下递增箭头
			keyNavEnabled: true,  //键盘导航
			mouseWheelEnabled: true,  //鼠标滚轮
			step:1
	    },width:60}:{dataIndex:'minValue',hidden:true},
	    //根据模板类型 决定是否显示列
	    'sample_assessment_standard'==me.as_tmlt_type?{header:'最大值 '+'<font color=red>*</font>',dataIndex:'maxValue',hidden:false,editor:{
			xtype:'numberfield',
			allowBlank:false,
			minValue: 1,  
			allowDecimals: true, // 允许小数点 
			nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
			//hideTrigger: true,  //隐藏上下递增箭头
			keyNavEnabled: true,  //键盘导航
			mouseWheelEnabled: true,  //鼠标滚轮
			step:1
	    },width:60}:{dataIndex:'maxValue',hidden:true},
	    'sample_assessment_standard'==me.as_tmlt_type?{header:'判定值 '+'<font color=red>*</font>',dataIndex:'judgeValue',hidden:false,editor:{
			xtype:'numberfield',
			allowBlank:false,
			minValue: 1,  
			allowDecimals: true, // 允许小数点 
			nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
			//hideTrigger: true,  //隐藏上下递增箭头
			keyNavEnabled: true,  //键盘导航
			mouseWheelEnabled: true,  //鼠标滚轮
			step:1
	    },width:60}:{dataIndex:'judgeValue',hidden:true},
	    {header:'排序',dataIndex:'sort',hidden:false,editor:{
			xtype:'numberfield',
			allowBlank:false,
			minValue: 1,  
			allowDecimals: false, // 允许小数点 
			nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
			//hideTrigger: true,  //隐藏上下递增箭头
			keyNavEnabled: true,  //键盘导航
			mouseWheelEnabled: true,  //鼠标滚轮
			step:1
	    },width:80}];
		me.on('selectionchange', function () {
		    me.setstatus();
		});
		me.on('edit', function () {
	        me.setstatus();
	    });
		
		me.callParent(arguments);
	},
	reloadData :function(id){
		var me = this;
		me.store.proxy.extraParams.assessGuidelinesId =  id || me.store.proxy.extraParams.assessGuidelinesId ;
		me.store.proxy.url = me.findUrl;
		me.store.load();
	}
});