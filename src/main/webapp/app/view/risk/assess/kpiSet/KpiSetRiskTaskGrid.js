/**
 * 按风险分配列表
 */
Ext.define('FHD.view.risk.assess.kpiSet.KpiSetRiskTaskGrid',{
	extend:'FHD.ux.GridPanel',
	alias: 'widget.kpisetrisktaskgrid',
	requires: [
       
    ],
	pagable:false,
    searchable:true,
    rowNumberer:false,
    autoLoad:false,
    type: 'editgrid',
	border: false,
	
	initComponent:function(){
		var me=this;
		
		me.empStore = Ext.create('Ext.data.Store',{
			autoLoad:false,
			fields : ['id', 'name'],
			proxy : {
				type : 'ajax',
				url : __ctxPath + '/access/kpiSet/findempsbyuserdeptidandroles.f'
			}
		});
		
		me.cols=[
		    {header: 'riskId',  dataIndex: 'riskId', hidden: true},
        	{header: '风险名称', dataIndex: 'parRiskName', sortable : true, flex: 1 },
        	{header:'评估人<font color=red>*</font>',dataIndex:'empId',flex:1,emptyCellText:'<font color="#808080">请选择</font>',
				editor:Ext.create('Ext.form.field.ComboBox',{
					checkField: 'checked',//多选
    				multiSelect : true,
    				separator: ',',
					store : me.empStore,
					valueField : 'id',
					displayField : 'name',
					allowBlank : false,
					editable : false
					}),
					renderer:function(value,metaData,record,rowIndex ,colIndex,store,view){
						metaData.tdAttr = 'style="background-color:#FFFBE6"';
						var names;
						var nameValue;
						if(value){//评估人多选
							if(value.length>1){//评估人多选
								if(''!=this.columns[3].getEditor(record).rawValue){
									return this.columns[3].getEditor(record).rawValue;
								}else{
									var v = this.columns[3].getEditor(record).store.findRecord('id',value);
									if(v){
										record.data.empId = v.data.id;
										return v.data.name;
									}
									return value;
								}
							}else{
								var v = this.columns[3].getEditor(record).store.findRecord('id',value);
								if(v){
									record.data.empId = v.data.id;
									return v.data.name;
								}
								return value;
							}
						}
					}
			}
		];
		me.callParent(arguments);
	}
	
});