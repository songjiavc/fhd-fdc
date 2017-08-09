Ext.define('FHD.view.sys.role.RoleBasePanel', {
	extend: 'Ext.form.Panel',
	alias: 'widget.roleBasePanel',
	title:"基本信息",
	border:false,
	layout:'column',
	collapsed : false,
	reloadUrl:__ctxPath +'/sys/auth/role/findById.f',
	mergeUrl:__ctxPath +'/sys/auth/role/merge.f',
	isRoleNameUrl:__ctxPath +'/sys/auth/role/isRoleName.f',
	
	roleId:null,
	editCallBack:function(id){
	},
	setRoleId:function(roleId){
		var me=this;
		me.roleId=roleId;
		me.down("[name='id']").setValue(me.roleId);
	},
	reloadData : function(){
		var me = this;
		if(""!=me.roleId) {
			me.form.load({
				url: me.reloadUrl,
		        params:{id:me.roleId},
		        failure:function(form,action) {
					FHD.alert("操作失败！");
		        },
		        success:function(form,action){
		        }
		    });
		}else{
			me.form.reset();
			me.down("[name='id']").setValue(me.roleId);
		}
	},
	edit : function(){
		var me = this;
		var form = me.getForm();
		if(form.isValid()){
			FHD.submit({
				form:form,
				url:me.mergeUrl,
				callback:function(data){
					var id=data.id;
					me.editCallBack(id);
				}
			});
		}
	},
	initComponent: function () {
		var me = this;
		Ext.apply(Ext.form.field.VTypes, {
			validateOnChange:false,
		    FHD_view_sys_role_RoleBasePanel_RoleCode:function(value,ele) {
		        var flag=false;
				var id=ele.up("form").down('[name="id"]').value;
		        jQuery.ajax({
					type: "POST",
					url: __ctxPath +'/sys/auth/role/isRoleCode.f',
					async:false,
					data: {roleCode:value,roleId:id},
					success: function(data){
						flag=data;
					},
					error: function(){
						FHD.alert("操作失败！");
					}
				});
		        return flag;
		    },
			FHD_view_sys_role_RoleBasePanel_RoleCodeText:"角色编号已被占用，请更改",
		    FHD_view_sys_role_RoleBasePanel_RoleName:function(value,ele) {
		        var flag=false;
		        var id=ele.up("form").down('[name="id"]').value;
		        jQuery.ajax({
					type: "POST",
					url: __ctxPath +'/sys/auth/role/isRoleName.f',
					async:false,
					data: {roleName:value,roleId:id},
					success: function(data){
						flag=data;
					},
					error: function(){
						FHD.alert("操作失败！");
					}
				});
		        return flag;
		    },
		    FHD_view_sys_role_RoleBasePanel_RoleNameText:"角色名称已被占用，请更改"
		});
	    Ext.apply(me, {
	    	listeners:{
	    		afterrender:function(){
	    			me.reloadData();
	    		}
	    	},
	        bbar:{
	            items: ['->',
	            {
					text: FHD.locale.get("fhd.common.save"),//保存按钮
					iconCls: 'icon-save',
					handler: function () {
						me.edit();
					}
	            }]
	        },
	        items: [{
				xtype:'fieldset',//基本信息fieldset
				layout:'column',
				columnWidth: 1,
				defaults: {
					margin : '7 10 0 30',
					labelWidth: 95,
					columnWidth: .5
				},
				title: FHD.locale.get('fhd.common.baseInfo'),
				items: [{
				    xtype: 'textfield',
				    name: 'id',
					hidden : true,
					value:me.roleId
				},{
					xtype: 'textfield',
					name: 'roleCode',
					maxLength: 200,
					allowBlank:false,
					vtype: "FHD_view_sys_role_RoleBasePanel_RoleCode",
					fieldLabel: '角色编号'+'<font color=red>*</font>'
				},{
				    xtype: 'textfield',
				    name:'roleName',
				    maxLength: 200,
				    allowBlank:false,
				    vtype: "FHD_view_sys_role_RoleBasePanel_RoleName",
				    fieldLabel: '角色名称'+'<font color=red>*</font>'
				},{
				    xtype: 'numberfield',
				    name:'sort',
				    maxLength: 200,
				    fieldLabel: '角色排序'
				},{
				    xtype: 'textfield',
				    name:'homeUrl',
				    maxLength: 200,
				    fieldLabel: '首页功能位置'
				}]
	        }]
	    });
	    me.callParent(arguments);
	}
});