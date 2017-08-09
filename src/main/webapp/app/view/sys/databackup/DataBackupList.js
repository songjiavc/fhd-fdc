/*数据库备份管理列表*/
Ext.define('FHD.view.sys.databackup.DataBackupList',{
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.databackuplist',
    layout: 'fit',
    border: false,
    searchable:false,
    pagable:false,
    url:__ctxPath + '/sys/databackup/findDataBackupList.f', //调用后台url
    cols: [ 
        {dataIndex : 'id',invisible:true},
        {dataIndex : 'filePath',invisible:true},
        {header : '名称',dataIndex : 'fileName',sortable: false, minWidth : 150,flex:1,
        	renderer:function(value,metaData,record,colIndex,store,view) { 
				metaData.tdAttr = 'data-qtip="点击‘'+value+'’下载备份文件"'; 
				return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+this.id+"').downloadDataBackup('" + encodeURI(value) + "','" + encodeURI(record.data.filePath) +"')\" >" + value + "</a>"; 
			}
        }, 
        /*{header : '主机地址',dataIndex : 'host',sortable: false, width : 100},
        {header : '数据库名',dataIndex : 'dbName',sortable: false, width : 160},*/
        {header : '大小',dataIndex : 'fileSize',sortable: false, width : 160,xtype: "numbercolumn", format: "0,000 KB"}, 
        {header : '修改日期',dataIndex : 'fileDate',sortable: false, width : 260}
    ],
	initComponent:function(){
		var me=this;
		
		Ext.apply(me,{
			tbarItems:[
			    {iconCls : 'icon-database-save',text:'备份',tooltip: '备份MySql数据库',handler :me.dataBackupAdd,scope : this}, 
			    '-',
			    {iconCls : 'icon-database-refresh',name : 'dataBackupRestore ',text:'恢复',tooltip: '用选中的备份文件来还原当前数据库',handler :me.dataBackupRestore,scope : this}, 
			    '-',
			    {iconCls : 'icon-database-delete',name : 'dataBackupDel',text:'删除',tooltip: '删除选中的备份文件',handler :me.dataBackupDel,scope : this}
		    ]
		});
		me.callParent(arguments);
		me.store.on('load', function () {
            me.setstatus()
        });
        me.on('selectionchange', function () {
            me.setstatus()
        });
	},
	setCenterContainer:function(compent){
    	this.removeAll(true);
    	this.add(compent);
    },
    setstatus: function(){
    	var me = this;
        var length = me.getSelectionModel().getSelection().length;
		if(me.down('[name=dataBackupDel]')){
			me.down('[name=dataBackupDel]').setDisabled(length === 0);
		}
		if(me.down('[name=dataBackupRestore ]')){
			var row = me.getSelectionModel()[0];
			if(row){
				if(row.get('status') != 'D' && length == 1){
					me.down('[name=dataBackupRestore ]').setDisabled(false);
				}else{
					me.down('[name=dataBackupRestore ]').setDisabled(true);
				}
			}else{
				me.down('[name=dataBackupRestore ]').setDisabled(length != 1);
			}
		}
    },
	
	//新增备份
	dataBackupAdd:function(){
		var me=this;
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			msg:"数据库备份中，可能需要几分钟..."
		});
		myMask.show();
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/sys/databackup/backup.f',
			callback : function(data) {
				if (data && data.success) {//备份成功！
					myMask.hide();
					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
					me.store.load();
				}else{
					FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
				}
			}
		});
	},
	//恢复
	dataBackupRestore:function() {
		var me=this;
		var selection = me.getSelectionModel().getSelection();//得到选中的记录
		Ext.MessageBox.show({
			title : '提示',
			width : 300,
			msg : "数据库将会被覆盖，您是否已经提前备份？",
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {//确认删除
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						msg:"数据库恢复中，可能需要几分钟..."
					});
					myMask.show();
					FHD.ajax({//ajax调用
						url : __ctxPath+ '/sys/databackup/load.f',
						params : {
							fileName : selection[0].get('fileName')
						},
						callback : function(data) {
							if (data && data.success) {//还原成功！
								myMask.hide();
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
							}else{
								myMask.hide();
								FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
							}
						}
					});
				}
			}
		});
    },
    //删除
    dataBackupDel:function(){
    	var me=this;
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {//确认删除
					var filePaths = [];
					for ( var i = 0; i < selection.length; i++) {
						filePaths.push(selection[i].get('filePath'));
					}
					FHD.ajax({//ajax调用
						url : __ctxPath+ '/sys/databackup/remove.f',
						params : {
							filePaths : filePaths
						},
						callback : function(data) {
							if (data && data.success) {//删除成功！
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								me.store.load();
							}else{
								FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
							}
						}
					});
				}
			}
		});
    },
    //下载
    downloadDataBackup: function(fileName,filePath){
    	window.location.href=__ctxPath+"/sys/databackup/download.f?fileName="+fileName+"&filePath="+filePath;
    }
    
});