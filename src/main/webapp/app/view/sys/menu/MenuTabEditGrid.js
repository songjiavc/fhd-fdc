Ext.define('FHD.view.sys.menu.MenuTabEditGrid',{
	extend:'FHD.ux.GridPanel',
	alias: 'widget.menutabeditgrid',
	type:'editgrid',
	url: __ctxPath + '/sys/auth/auth/findPageBySome.f',//查询
	removeUrl: __ctxPath + '/sys/auth/auth/removeByIds.f',//查询
	mergeByDatasStrUrl: __ctxPath + '/sys/auth/auth/mergeByDatasStr.f',//查询
	isAuthorityCodeUrl:__ctxPath +'/sys/auth/auth/isAuthorityCode.f',
	storeSorters : [{
		property : 'etype',
		direction : 'asc'
	},{
		property : 'sn',
		direction : 'asc'
	}],
	parentAuthorityId:'null',
	setParentAuthorityId:function(parentAuthorityId){
		var me=this;
		if(parentAuthorityId){
			me.parentAuthorityId=parentAuthorityId;
			me.down("[name='add']").setDisabled(false);
		}else{
			me.parentAuthorityId='null';
			me.down("[name='add']").setDisabled(true);
		}
	},
	reloadData:function(){
		var me=this;
		me.extraParams.parentId=me.parentAuthorityId;
		if(!me.parentAuthorityId){
			me.extraParams.parentId="null";
		}
		me.getStore().load();
	},
	setStatus: function(){
		var me = this;
		var length = me.getSelectionModel().getSelection().length;
		var rows = me.store.getModifiedRecords();
		var jsonArray=[];
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		if(me.parentAuthorityId&&me.parentAuthorityId!="null"){
			me.down('[name="add"]').setDisabled(false);
		}else{
			me.down('[name="add"]').setDisabled(true);
		}
		me.down('[name="save"]').setDisabled(jsonArray.length==0);
		me.down('[name="remove"]').setDisabled(length==0);
    },
    save:function(){
    	var me=this;
    	
    	var rows = me.store.getModifiedRecords();
    	if(rows.length>0){
			var datas=[];
			var codeMap={};
			var flag=true;
			var errorMessage = '';
			for (var i in rows) {
				var data=rows[i].data;
				var authorityCode=data.authorityCode;
				var authorityName=data.authorityName;
				var id=data.id;
				if(authorityCode && authorityName){
					if(eval("codeMap.code_"+authorityCode)){
						flag=false;
						errorMessage = "权限编号重复!";
					}else{
						eval("codeMap.code_"+authorityCode+"=true");
						jQuery.ajax({
							type: "POST",
							url:me.isAuthorityCodeUrl,
							async:false,
							data: {authorityCode:authorityCode,authorityId:id},
							success: function(data){
								flag=data;
								if(!flag){
									errorMessage = "已存在权限编号!";
								}
							},
							error: function(){
								FHD.alert("操作失败!");
							}
						});
					}
				}else{
					flag=false;
					errorMessage = "权限编号或权限名称为空!";
				}
				if(flag){
					datas.push(data);
				}else{
					break;
				}
			}
			if(flag){
		    	datasStr=Ext.encode(datas);
				jQuery.ajax({
					type : "POST",
					url : me.mergeByDatasStrUrl,
					async:false,
					data : {
						datasStr:datasStr
					},
					success : function(msg) {
						FHD.notification("提示",FHD.locale.get('fhd.common.operateSuccess'));
						me.reloadData();
					},
					error : function() {
						FHD.alert("操作失败!");
					}
				});
			}else{
				Ext.Msg.alert("提示",errorMessage);
				//FHD.alert("功能信息内容错误请检查!");
			}
    	}
    },
	initComponent:function(){//初始化
		Ext.define('MenuTabEditGridDataMapping', {
			extend: 'Ext.data.Model',
			fields:['id','parentId','authorityCode', 'authorityName', 'type', 'icon', 'url','sn']
		});
		var me=this;
		me.tbarItems = [{
			name:'add',
			text:'添加',
			iconCls : 'icon-add',
			handler : function(){//新增方法
				var count = me.getStore().getCount();
				var records=me.getStore().getRange(0,count);
				var maxSn=0;
				for (var i in records) {
					var record=records[i];
					var sn=record.get("sn");
					if(sn&&sn>maxSn){
						maxSn=sn;
					}
				}
				var r = Ext.create('MenuTabEditGridDataMapping',{
					id:'',
					parentId : me.parentAuthorityId,
					authorityCode:'',
					authorityName:'',
					type:'',
					icon:'',
					url:'',
					sn: maxSn+1
				});
				me.store.insert(count, r);
				me.editingPlugin.startEditByPosition({row:count,column:0});
			}
		}, '-', {
			name:'remove',
			text:'删除',
			iconCls : 'icon-del',
			disabled:true,
			handler:function(){//删除方法
				Ext.MessageBox.show({
					title : FHD.locale.get('fhd.common.delete'),
					width : 260,
					msg : FHD.locale.get('fhd.common.makeSureDelete'),
					buttons : Ext.MessageBox.YESNO,
					icon : Ext.MessageBox.QUESTION,
					fn : function(flag) {
						if(flag=='yes') {
							var selection = me.getSelectionModel().getSelection();
							var ids = [];
							for(var i in selection){
								var id = selection[i].get('id');
								if(id){
									ids.push(id)
								}else{
									me.getStore().remove(selection[i]);
								}
							}
							if(ids.length>0){
								var idsStr = ids + "";
								jQuery.ajax({
									type : "POST",
									url : me.removeUrl,
									data : {
										idsStr : idsStr
									},
									success : function(msg) {
										FHD.notification("提示",FHD.locale.get('fhd.common.operateSuccess'));
										me.reloadData();
									},
									error : function() {
										FHD.alert("操作失败！");
									}
								});
							}
						}
					} 
				});
			}
		}, '-', {
			name:'save',
			text:'保存',
			iconCls : 'icon-save',
			disabled:true,
			handler:function(){//保存方法
				me.save();
			}
		}];
		
		me.typeStore=Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
			    {'id' : 'T','name' : 'tab权限'},
			    {'id' : 'B','name' : 'button权限'},
			    {'id' : 'G','name' : 'group权限'}
			]
		});
		
		Ext.apply(me,{
			extraParams:{
				parentId : me.parentAuthorityId,
				etype:'T,B,G'
			},
			cols:[
				{dataIndex:'id',invisible:true},
				{dataIndex:'parentId',invisible:true},
				{header:'编号<font color=red>*</font>',dataIndex:'authorityCode',editor:{allowBlank: false},flex:2},
				{header:'名称<font color=red>*</font>',dataIndex:'authorityName',editor:{allowBlank: false},flex:2},
				{
			    	header:'类型<font color=red>*</font>', dataIndex: 'etype',sortable: false,flex:1,
			    	editor:{
			    		xtype:'combobox',
			    		store :me.typeStore,
			    		valueField : 'id',
			    		displayField : 'name',
			    		allowBlank : false,
			    		editable : false
			    	},
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
								metaData.tdAttr = 'data-qtip="权限类型是必填项!" style="background-color:#FFFBE6"';
							}
						}
					}
			    },
			    {header:'图标',dataIndex:'icon',hidden:false,flex:1,editor:true},
			    {header:'链接地址',dataIndex:'url',hidden:false,flex:1,editor:true},
				{header:'排序',dataIndex:'sn',width:80,
					editor:{
						xtype:'numberfield',
						allowBlank:false,
						minValue: 1,  
						allowDecimals: false, // 允许小数点 
						nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
						keyNavEnabled: true,  //键盘导航
						mouseWheelEnabled: true,  //鼠标滚轮
						step:1
					}
				}
			],
			listeners : {
				selectionchange : function() {
					me.setStatus();
				},
				edit:function(){
				    me.setStatus();
				}
			}
		});
		me.callParent(arguments);
		me.getStore().addListener("load",function(store, records, successful, eOpts ){
			me.setStatus();
		})
    }
});