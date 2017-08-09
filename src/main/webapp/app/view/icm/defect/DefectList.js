/*缺陷管理列表*/
Ext.define('FHD.view.icm.defect.DefectList',{
    extend: 'Ext.container.Container',
    alias: 'widget.defectlist',
    
    layout: 'fit',
	cols:[],
	tbar:[],
	tbarItems:[],
	defectIds:'',
	idSeq:'',
	upName:'',
	
	initComponent:function(){
		var me=this;
		me.defectGrid = Ext.create('FHD.ux.GridPanel', { //实例化一个grid列表
            border: false,
            region: 'center',
            url:__ctxPath + '/icm/defect/findDefectListBypage.f', //调用后台url
            cols: [ 
                {dataIndex : 'id',invisible:true},
                {dataIndex : 'code',invisible:true}, 
                {header : '缺陷描述',dataIndex : 'desc',sortable: false, flex : 3},
                {header : '责任部门',dataIndex : 'defectRelaOrg',sortable: false, flex : 1}, 
                {header : '缺陷等级',dataIndex : 'controlRequirement',sortable: false}, 
                {header : '缺陷类型',dataIndex : 'type',sortable: false}, 
                {header : '整改状态',dataIndex : 'dealstatus',sortable: false, renderer : function(value, metaData, record, colIndex, store, view) {
					if('N' == value){
						return '未开始';
					}else if('H' == value){
						return '处理中';
					}else if('F' == value){
						return '已完成';
					}else{
						return '';
					}
				}},
                {header : '状态',dataIndex : 'status',sortable: false, renderer : function(value, metaData, record, colIndex, store, view) { 
					if('S' == value){
						return '已保存';
					}else if('P' == value){
						return '已提交';
					}else{
						return '';
					}
				}},
				{header : '创建日期',dataIndex : 'createTime',sortable: false}
            ],
			tbarItems:[
			    {iconCls : 'icon-add',name : 'defect_add',text:'添加',tooltip: '添加缺陷',handler :me.addDefect,scope : this}, 
			    '-',
			    {iconCls : 'icon-edit',name : 'defect_edit',text:'修改',tooltip: '修改缺陷',handler :me.editDefect,scope : this}, 
			    '-',
			    {iconCls : 'icon-del',name : 'defect_del',text:'删除',tooltip: '删除缺陷',handler :me.delDefect,scope : this}
    	    ]
        });
		me.defectGrid.store.on('load', function () {
            me.setstatus()
        });
        me.defectGrid.on('selectionchange', function () {
            me.setstatus()
        });
	
	me.callParent(arguments);
	me.add(me.defectGrid);
	},
	setCenterContainer:function(compent){
    	this.removeAll(true);
    	this.add(compent);
    },
    setstatus: function(){
    	var me = this;
        var length = me.defectGrid.getSelectionModel().getSelection().length;
		if(me.defectGrid.down('[name=defect_del]')){
			me.defectGrid.down('[name=defect_del]').setDisabled(length === 0);
		}
		if(me.defectGrid.down('[name=defect_edit]')){
			var row = me.defectGrid.getSelectionModel()[0];
			if(row){
				if(row.get('status') != 'D' && length == 1){
					me.defectGrid.down('[name=defect_edit]').setDisabled(false);
				}else{
					me.defectGrid.down('[name=defect_edit]').setDisabled(true);
				}
			}else{
				me.defectGrid.down('[name=defect_edit]').setDisabled(length != 1);
			}
		}
    },
	
	//添加 的
	addDefect:function(){
		var me=this;
		me.setCenterContainer(Ext.create('FHD.view.icm.defect.form.DefectForm',{parameter:{type:'no'},upCompent:me}));
	},
	//修改
	editDefect:function() {
		var me=this;
    	var selection = me.defectGrid.getSelectionModel().getSelection();//得到选中的记录
    	var defectId=selection[0].get('id');
    	var isSubmit=selection[0].get('status');
    	if(isSubmit=='P'){
    		FHD.notification('对不起,您不能修改已提交的数据!',FHD.locale.get('fhd.common.prompt'));
    		return false;
    	}
    	var defectForm = Ext.create('FHD.view.icm.defect.form.DefectForm',{
    		parameter:{
    			defectId:defectId
    		},
    		upCompent:me
    	});
    	defectForm.loadData(defectId);
    	me.setCenterContainer(defectForm);
    },
    //删除
    delDefect:function(){
    	var me=this;
    	var selection = me.defectGrid.getSelectionModel().getSelection();//得到选中的记录
    	var flag = true;
    	for ( var i = 0; i < selection.length; i++) {
			if(selection[i].get('status')=='P'){
				flag = false;
			}
		}
		if(flag){
			Ext.MessageBox.show({
    			title : FHD.locale.get('fhd.common.delete'),
    			width : 260,
    			msg : FHD.locale.get('fhd.common.makeSureDelete'),
    			buttons : Ext.MessageBox.YESNO,
    			icon : Ext.MessageBox.QUESTION,
    			fn : function(btn) {
					if (btn == 'yes') {//确认删除
						var ids = [];
						for ( var i = 0; i < selection.length; i++) {
							ids.push(selection[i].get('id'));
						}
						FHD.ajax({//ajax调用
							url : __ctxPath+ '/icm/defect/removeDefectByIdBatch.f',
							params : {
								defectIds : ids
							},
							callback : function(data) {
								if (data) {//删除成功！
									FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
									me.defectGrid.store.load();
								}
							}
						});
					}
    			}
    		});
		}else{
			FHD.notification('对不起,您不能删除已提交的数据!',FHD.locale.get('fhd.common.prompt'));
			return false;
		}
    }
});