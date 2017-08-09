Ext.define('FHD.view.sys.user.UserBaseForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.UserBaseForm',
	border:false,
	autoScroll:true,
	layout:'column',
	collapsed : false,
    reloadUrl:__ctxPath +'/sys/auth/user/findById.f',
    mergeUrl:__ctxPath +'/sys/auth/user/merge.f',
    isUsernameUrl:__ctxPath +'/sys/auth/user/isUsername.f',
    
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
    	        params:{id:me.userId},
    	        failure:function(form,action) {
					FHD.alert("操作失败！");
    	        },
    	        success:function(form,action){
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
				url:me.mergeUrl,
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
    	Ext.apply(Ext.form.field.VTypes, {
    		validateOnChange:false,
		    FHD_view_sys_user_UserBasePanel_Username:function(value,ele) {
		        var flag=false;
				var id=ele.up("form").down('[name="id"]').value;
		        jQuery.ajax({
					type: "POST",
					url: me.isUsernameUrl,
					async:false,
					data: {username:value,id:id},
					success: function(data){
						flag=data;
					},
					error: function(){
						FHD.alert("操作失败！");
					}
				});
		        return flag;
		    },
			FHD_view_sys_user_UserBasePanel_UsernameText:"用户名已被占用，请更改"
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
	            },
	            {
					text: FHD.locale.get("fhd.common.close"),
					iconCls: 'icon-ibm-close',
					handler: function () {
						me.close();
					}
	            }]
            },
            items: [{
				xtype:'fieldset',//基本信息fieldset
				layout:'column',
				columnWidth: 1,
				defaults: {
					margin : '7 10 0 30',
					columnWidth: .5
				},
				title: FHD.locale.get('fhd.common.baseInfo'),
				items: [{
				    xtype: 'textfield',
				    name: 'id',
					hidden : true
				},{
				    xtype: 'textfield',
				    name: 'username',
				    maxLength: 200,
					allowBlank:false,
				    vtype: "FHD_view_sys_user_UserBasePanel_Username",
				    fieldLabel: FHD.locale.get('fhd.sys.auth.user.username')+'<font color=red>*</font>'
				},{
				    xtype: 'textfield',
				    name:'password',
				    maxLength: 200,
				    allowBlank:false,
				    value:'111111',
				    inputType:'password',
				    fieldLabel: FHD.locale.get('fhd.sys.auth.user.password')+'<font color=red>*</font>'
				},{
				    xtype: 'datefield',
				    name:'regdate',
				    maxLength: 200,
				    fieldLabel: FHD.locale.get('fhd.common.regdate')
				},{
				    xtype: 'datefield',
				    name:'expiryDate',
				    maxLength: 200,
				    fieldLabel: FHD.locale.get('fhd.common.abatedate')
				},{
				    xtype: 'datefield',
				    name:'credentialsexpiryDate',
				    maxLength: 200,
				    fieldLabel: FHD.locale.get('fhd.common.credentialsexpiryDate')
				},Ext.create('Ext.form.ComboBox', {
				    name:'userStatus',
				    store: Ext.create('Ext.data.Store', {
					    fields: ['value', 'text'],
					    data : [
					        {"value":'1', "text":"正常"},
					        {"value":'0', "text":"注销"}
					    ]
					}),
					queryMode: 'local',
				    displayField:'text',
				    valueField: 'value',
				    value:'1',
				    allowBlank:false,
				    fieldLabel: FHD.locale.get('fhd.common.status')+'<font color=red>*</font>'
				}),Ext.create('Ext.form.ComboBox', {
				    name:'lockstate',
				    store: Ext.create('Ext.data.Store', {
					    fields: ['value', 'text'],
					    data : [
					        {"value":false, "text":"正常"},
					        {"value":true, "text":"锁定"}
					    ]
					}),
					queryMode: 'local',
				    displayField:'text',
				    valueField: 'value',
				    value:false,
				    allowBlank:false,
				    fieldLabel: FHD.locale.get('fhd.common.lockState')+'<font color=red>*</font>'
				}),Ext.create('Ext.form.ComboBox', {
				    name:'enable',
				    store: Ext.create('Ext.data.Store', {
					    fields: ['value', 'text'],
					    data : [
					        {"value":true, "text":"启用"},
					        {"value":false, "text":"放弃"}
					    ]
					}),
					queryMode: 'local',
				    displayField:'text',
				    valueField: 'value',
				    value:true,
				    allowBlank:false,
				    fieldLabel: FHD.locale.get('fhd.common.enable')+'<font color=red>*</font>'
				}),{
				    xtype: 'textfield',
				    name:'mac',
				    maxLength: 200,
				    fieldLabel: 'MAC地址：'
				},{
				    xtype: 'textfield',
				    name:'homeUrl',
				    maxLength: 200,
				    fieldLabel: '主页URL'
				}]
            }]
        });
        me.callParent(arguments);
    }
});