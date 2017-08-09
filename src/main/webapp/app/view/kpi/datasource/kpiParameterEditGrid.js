 Ext.define('ParameterDataMapping', {
		    extend: 'Ext.data.Model',
		    fields:['id','parameterName','parameterValue']
		});
Ext.define('FHD.view.kpi.datasource.kpiParameterEditGrid',{
	extend: 'Ext.grid.Panel',
	alias: 'widget.kpiParameterEditGrid',
	region:'center',
	layout: 'fit',
	title:'参数设置',
	flex: 2,
	border:false,
	clicksToEdit:1,
	parameterJson: [],
	initComponent : function() {
		var me = this;
		me.store = Ext.create('Ext.data.Store',{
			fields : ['id', 'parameterName','parameterValue'],
			data: me.parameterJson,
			autoLoad: true
		});
		me.columns = [{
			header : 'id',
			dataIndex : 'id',
			sortable : true,
			flex : 1,
			hidden : true
		}, {// 参数名称
			header : '参数名',
			dataIndex : 'parameterName',
			editor:true,
			sortable : true,
			flex : 1
		},// 参数值
		{
			header : '参数值',
			dataIndex : 'parameterValue',
			sortable : true,
			editor:true,
			flex : 1
		}];
		me.tbar = [{
			text : FHD.locale.get('fhd.common.add'),
			iconCls : 'icon-add',
			id : 'add',
			handler :  me.addGrid,
			scope : this
		},{
			text : FHD.locale.get('fhd.common.delete'),
			iconCls : 'icon-del',
			id : 'del',
			handler : me.delGrid,
			disabled : true,
			scope : this
		}];
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
	        clicksToEdit: me.clicksToEdit
	    });		
		Ext.applyIf(me,{
			selModel:Ext.create('Ext.selection.CheckboxModel'),
			plugins: [cellEditing]
		});
		me.callParent(arguments);
   	 me.on('selectionchange', function () {
         me.setstatus()
     });
	 me.on('edit', function () {
         me.setstatus()
     });
	},
	addGrid:function(){//新增方法
		var me = this;
		var count = me.store.data.length;
		var r = Ext.create('ParameterDataMapping',{
			//新增时初始化参数
			id : '',
			parameterName:'参数名',
			parameterValue:'参数值'
		});
		me.store.insert(count, r);
		me.editingPlugin.startEditByPosition({row:count,column:0});
	},
	setstatus:function(){
    	var me = this;
        var length = me.getSelectionModel().getSelection().length;
        var rows = me.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
         me.down('#del').setDisabled(length === 0);
    },
    delGrid:function(){
		var me = this;
		var selection = me.getSelectionModel().getSelection();
		if(!selection.length) return;
		for(var i=0;i<selection.length;i++) {
			var rec = selection[i];
			me.store.remove(rec);			
		}
    },
    saveGrid:function(){
		var me = this;	
        var rows = me.store.data.items;
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		return jsonArray;
    }
})