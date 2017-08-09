Ext.define('FHD.view.sys.role.UserRoleGridPanel', {
	extend: 'FHD.ux.GridPanel',
	alias: 'widget.userRoleGridPanel',
	
	tbarItems:null,
	title:"人员列表",
	roleId:null,
	companyId:null,
	url:__ctxPath +'/sys/auth/role/findSysUserPageByRoleId.f',
    saveUrl:__ctxPath +'/sys/auth/role/saveEmpRole.f',
    removeUrl:__ctxPath +'/sys/auth/role/removeUserRole.f',
	multiSelect: true,
	border:true,
	rowLines:true,//显示横向表格线
	checked: true, //复选框
	autoScroll:true,
	setRoleId:function(roleId){
		var me=this;
		me.roleId=roleId;
		me.extraParams.roleId=me.roleId;
	},
	reloadData:function(){
		var me=this;
		me.store.load();
	},
	//新增
	save:function (){
		var me = this;
		me.selectNodeId = me.up('roleMainPanel').roleTreePanel.selectNodeId;
		var empSelectorWindow = Ext.create('FHD.ux.org.EmpSelectorWindow',{
		type:'emp',
		multiSelect:true,
		onSubmit:function(empSelectorWindow){
			var empIds = new Array();
			empSelectorWindow.selectedgrid.store.each(function(r){
				empIds.push(r.get("id"));
			});
			if(empIds.length>0){
				var empIdsStr=empIds+"";
				FHD.ajax({
					url: me.saveUrl,
					params: {
						roleId:me.roleId,
						empIdsStr:empIdsStr
					},
					callback: function(data){
						if(data.length>0){
							FHD.notification(data+"所属公司只能有一名公司风险管理员！","提示");
						}else{
							FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),"提示");
						}
						me.store.load();
					}
				});
			}
		}
	});
	empSelectorWindow.show();
    },
	remove:function (ids){
		var me = this;
		Ext.MessageBox.confirm('警告', FHD.locale.get('fhd.common.makeSureDelete'), function showResult(btn){
			if (btn == 'yes') {//确认删除
				userIdsStr=ids+"";
				jQuery.ajax({
					type: "POST",
					url: me.removeUrl,
					data: {roleId:me.roleId,userIdsStr:userIdsStr},
					success: function(msg){
						FHD.notification( FHD.locale.get('fhd.common.operateSuccess'),"提示");
						me.store.load();
					},
					error: function(){
						FHD.alert("操作失败！");
					}
				});
			}
		});
	},
    // 初始化方法
	initComponent: function() {
		var me = this;
		//员工列表项
		me.rolePersonGridColums =[{	
			dataIndex : 'id',
			hidden : true
		},{
			header : '用户名',
			flex : 1,
			dataIndex : 'username',
			sortable : true
		},{
			header : '员工姓名',
			flex : 1,
			dataIndex : 'realname',
			sortable : true	
		},{
			text : '拥有角色',
			flex : 3,
			dataIndex : 'roleNames',
			sortable : true
		},{
			text : '所属部门',
			flex : 3,
			dataIndex : 'orgname',
			sortable : true
		}];
		
		me.tbarItems=[{
			name:'save',
			text : FHD.locale.get('fhd.common.add'),
			iconCls: 'icon-add',
			handler:function(){me.save();}
		},'-',{
			name:'remove',
			text : FHD.locale.get('fhd.common.delete'),
			iconCls: 'icon-del',
			disabled : true,
			handler:function(){
				var selection = me.getSelectionModel().getSelection();
				var ids=new Array();
				for (var i in selection) {
					ids.push(selection[i].get("id"));
				}
				me.remove(ids);
			}
		}];
		
		Ext.apply(me, {
			extraParams:{roleId:me.roleId,companyId:me.companyId},
			cols:me.rolePersonGridColums,//cols:为需要显示的列
			storeSorters:[{property:'username',direction:'asc'}],
			listeners:{
				selectionchange:function(){
					var me = this;
					var selection = me.getSelectionModel().getSelection();
					me.down("[name='remove']").setDisabled(selection.length<=0);
				}
			}
		});
		
		me.callParent(arguments);
    } 
   
});