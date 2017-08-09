Ext.define('FHD.view.sys.user.UserRoleForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userRoleForm',
    userRoleFieldset:null,
    userRoleFieldContainer:null,
	border:false,
	autoScroll:true,
	layout:'column',
	collapsed : false,
    reloadUrl:__ctxPath +'/sys/auth/role/findByUserId.f',
    userRoleMergeUrl:__ctxPath +'/sys/auth/user/userRoleMerge.f',
    allRoleUrl:__ctxPath +'/sys/auth/role/findByAll.f',
    
    userId:null,
    editCallBack:function(){
    },
    closeCallBack:function(){
    },
    setUserId:function(userId){
		var me=this;
		me.userId=userId;
		me.down("[name='id']").setValue(me.userId);
	},
    reloadData : function(){
    	var me = this;
    	if(me.userId) {
			me.form.load({
				url: me.reloadUrl,
    	        params:{userId:me.userId},
    	        failure:function(form,action) {
					FHD.alert("操作失败！");
    	        },
    	        success:function(form,action){
    	        	var data=action.result.data;
    	        	var roleIds=data.roleIds;
    	        	for (var i in roleIds) {
    	        		roleId=roleIds[i];
    	        		var inputValue=roleId.inputValue;
    	        		var checked=roleId.checked;
    	        		var checkedbox=me.down("[name='roleIds'][inputValue='"+inputValue+"']");
    	        		checkedbox.setValue(checked);
    	        	}
    	        }
    	    });
    	}
    },
	edit : function(){
		var me = this;
		var form = me.getForm();
		if(form.isValid()){
			FHD.submit({
				form:form,
				url:me.userRoleMergeUrl,
				callback:function(data){
					me.editCallBack();
				}
			});
		}
	},
	close : function(){
		var me = this;
		me.closeCallBack();
	},
    initComponent: function () {
    	var me = this;
    	me.userRoleFieldset=Ext.create("Ext.form.FieldSet",{
    		layout:'column',
			columnWidth: 1,
			defaults: {
				margin : '7 10 0 30',
				labelWidth: 95,
				columnWidth: .5
			},
			title:"拥有角色"
    	});
    	me.userRoleFieldset.add({
		    xtype: 'textfield',
		    name: 'id',
			hidden : true,
			value:me.userId
		});
		jQuery.ajax({
			type: "POST",
			async:false,
			url: me.allRoleUrl,
			success: function(roles){
				for (var i in roles) {
					var role=roles[i];
					var id=role.id;
					var roleName=role.roleName;
					me.userRoleFieldset.add({
						xtype: 'checkboxfield',
						name: 'roleIds',
						inputValue:id,
						boxLabel:roleName
					});
				}
			},
			error: function(){
				FHD.alert("操作失败！");
			}
		});
        Ext.apply(me, {
        	listeners:{
        		afterrender:function(){
        			me.reloadData();
        		}
        	},
            items: [me.userRoleFieldset]
        });
        me.callParent(arguments);
    }
});