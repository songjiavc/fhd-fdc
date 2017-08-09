/*
 * 评价产生的缺陷的可编辑列表 
 * */
 Ext.define('ProcessPointPre', {
		    extend: 'Ext.data.Model',
		    fields:['id','processId','pointId', 'pointName', 'pointPreId','contition']
		});
Ext.define('FHD.view.icm.icsystem.component.ParentNoteEditGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.parentnoteeditgrid',
	url: __ctxPath + '/process/findParentListByPointId.f',
	region:'center',
	objectType:{},
	pagable : false,
	border : false,
	storeAutoLoad:false,
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},
	searchable:false,
	layout: 'fit',
	addGrid:function(){//新增方法
		var me = this;
		if(me.processStore.getCount() == 0){
			Ext.Msg.alert('提示','没有流程节点可供选择!');
			return false;
		}else{
			var r = Ext.create('ProcessPointPre',{
				pointId:me.paramObj.processPointId,
				processId : me.paramObj.processId
	//    			pointName:values.name
			});
			me.store.insert(0, r);
			me.editingPlugin.startEditByPosition({row:0,column:0});
		}
	},
	saveGrid:function(){//保存方法
		var me = this;
		var rows = me.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		FHD.ajax({
			url: __ctxPath + '/processpoint/processpoint/saveprocesspointrela.f',
			params : {
				modifiedRecord:Ext.encode(jsonArray)
			},
			callback : function(data){
				if(data){
					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
					me.store.commitChanges();
				}
			}
		})
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
					if(selection){
						me.store.remove(selection);
					}
					var ids = [];
					for(var i=0;i<selection.length;i++){
						ids.push(selection[i].get('id'));
					}
					FHD.ajax({
						url : __ctxPath + '/processpoint/removeparentpointbyid.f',
						params : {
							ids:ids.join(',')
						},callback: function (data) {
	                        if (data) {
	                            FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
	                        }else{
	                        	FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
	                        };
	                    }
					});
				}
			} 
		});
	},
	initComponent:function(){
    	var me=this;
    	me.on('selectionchange',me.onchange);//选择记录发生改变时改变按钮可用状态
    	Ext.apply(me,{
    		selModel: Ext.create('Ext.selection.CheckboxModel',{mode:'SINGLE'}),
    		extraParams:{
    			processPointId : me.processPointId,
    			processId : me.processId
    		}
    	});
    	me.processStore = Ext.create('Ext.data.Store',{//myLocale的store
			fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath + '/processpoint/findallprocesspointbyprocessid.f'
			},
			autoLoad:false
		});
		me.processStore.load();
    	me.tbarItems = [{
					iconCls : 'icon-add',
					text: '添加',tooltip: '上级节点',
					handler : me.addGrid,
					scope : this
				}, '-', {
					iconCls : 'icon-del',
					text: '删除',tooltip: '下级节点',
					handler : me.delGrid,
					scope : this
				}];
    	me.cols=[ {header:'节点ID',dataIndex:'pointId',hidden:true},
    			  {header:'流程Id',dataIndex:'processId',hidden:true},
			      {header:'节点名称',dataIndex:'pointName',hidden:true},
			      {header:'父节点',dataIndex:'pointPreId',hidden:false,flex : 1
			      ,editor:Ext.widget('combobox',{
					   store :me.processStore,
					   valueField : 'id',
					   displayField : 'name',
					   allowBlank : false,
					   editable : false
			      }),
			      renderer:function(value){
						var index = me.processStore.find('id',value);
						var record = me.processStore.getAt(index);
						if(record!=null){
							return record.data.name;
						}else{
							return value;
						}
				  }},
			      {header:'入口条件', dataIndex: 'contition', sortable: false,flex:1,editor:true}
			      ];
    	me.callParent(arguments);
    	me.onchange();
    },
    reloadData :function(){
		var me = this;
		me.processStore.proxy.extraParams = me.paramObj;
		me.processStore.load();
        me.store.proxy.extraParams = me.paramObj;
		me.store.load();
		
	},
	onchange :function(){//设置你按钮可用状态
		var me = this;   // iconCls : 'icon-del',
		me.down('[iconCls=icon-del]').setDisabled(me.getSelectionModel().getSelection().length != 1);
	}
});