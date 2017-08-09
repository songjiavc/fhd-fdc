Ext.define('FHD.view.sys.menu.MenuBaseForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.menubaseform',
	reloadDataUrl:__ctxPath +'/sys/auth/auth/findById.f',
	mergeUrl:__ctxPath +'/sys/auth/auth/merge.f',
	isAuthorityCodeUrl:__ctxPath +'/sys/auth/auth/isAuthorityCode.f',
	border: false,
    layout:'column',
	columnWidth: 1,
	defaults: {
		margin : '5 10 5 10',
		labelWidth: 95,
		columnWidth: .5
	},
	etype:'M',
	authorityId:'',
	parentAuthorityId:'',
	setAuthorityId:function(authorityId){
		var me=this;
		me.authorityId=authorityId;
		me.down("[name='id']").setValue(me.authorityId);
	},
	setParentAuthorityId:function(parentAuthorityId){
		var me=this;
		me.parentAuthorityId=parentAuthorityId;
		me.down("[name='parentId']").setValue(me.parentAuthorityId);
	},
    editCallBack:function(){
    },
	reloadData:function(){
		var me=this;
		if(""!=me.authorityId) {
			me.form.load({
				url: me.reloadDataUrl,
    	        params:{id:me.authorityId},
    	        failure:function(form,action) {
					FHD.alert("操作失败！");
    	        },
    	        success:function(form,action){
    	        }
    	    });
    	}else{
    		me.form.reset();
    		me.down("[name='id']").setValue(me.authorityId);
    		me.down("[name='parentId']").setValue(me.parentAuthorityId);
    	}
	},
	edit : function(){
		var me = this;
		var form = me.getForm();
		if(me.down("[name='parentId']").getValue()==""){
			FHD.alert("请选择上级菜单");
		}else if(me.down("[name='parentId']").getValue()==me.down("[name='id']").getValue()){
			FHD.alert("不能选择自己为上级菜单");
		}else{
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
		}
	},
    initComponent: function () {
        var me = this;
		Ext.apply(Ext.form.field.VTypes, {
			validateOnChange:false,
		    FHD_view_sys_menu_MenuBaseForm_AuthorityCode:function(value,ele) {
				var flag=false;
				var id=ele.up("form").down('[name="id"]').value;
				jQuery.ajax({
					type: "POST",
					url:me.isAuthorityCodeUrl,
					async:false,
					data: {authorityCode:value,authorityId:id},
					success: function(data){
						flag=data;
					},
					error: function(){
						FHD.alert("操作失败！");
					}
				});
				return flag;
		    },
			FHD_view_sys_menu_MenuBaseForm_AuthorityCodeText:"菜单编号已被占用，请更改"
		});
		Ext.apply(me, {
		    items: [{
		    	xtype: 'textfield',
			    name:'id',
			    hidden : true,
			    value:me.authorityId
			},{
				xtype: 'textfield',
			    name:'authorityCode',
			    fieldLabel: '菜单编号'+'<font color=red>*</font>',
			    allowBlank:false,
			    vtype: "FHD_view_sys_menu_MenuBaseForm_AuthorityCode"
			},{
				xtype: 'textfield',
			    name:'authorityName',
			    fieldLabel: '菜单名称'+'<font color=red>*</font>',
			    allowBlank:false
			},{
				xtype: 'textfield',
			    name:'etype',
			    hidden : true,
			    value:me.etype
			},Ext.create('FHD.ux.menuSelector.MenuSelector', {
				name:'parentId',
				fieldLabel: '上级名称'+'<font color=red>*</font>',
			    value:me.parentAuthorityId
			}),{
				xtype: 'numberfield',
			    name:'sn',
			    fieldLabel: '排列顺序'+'<font color=red>*</font>',
			    allowBlank:false,
			    value:'1'
			},{
				xtype: 'textfield',
				name:'icon',
				fieldLabel: '菜单图标'
			},{
				xtype: 'textfield',
				name:'url',
				fieldLabel: '功能位置'
			}],
			listeners:{
        		afterrender:function(){
        			me.reloadData();
        		}
        	}
		});
        me.callParent(arguments);
    }
});