Ext.define('FHD.view.sys.user.UserPasswordForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userpasswordform',
    
	border:false,
	autoScroll:true,
	layout:'column',
    
    initComponent: function () {
    	var me = this;
    	
        Ext.apply(me, {
            items: [{
			    xtype: 'textfield',
			    name:'oldPassword',
			    labelWidth : 80,
			    margin : '7 10 0 30',
				columnWidth : 1,
			    maxLength: 200,
			    allowBlank:false,
			    inputType:'password',
			    vtype : 'alphanum',
			    fieldLabel: '原密码'+'<font color=red>*</font>'
			},{
			    xtype: 'textfield',
			    name:'newPassword',
			    labelWidth : 80,
			    margin : '7 10 0 30',
				columnWidth : 1,
				minLength: 6,
			    maxLength: 200,
			    allowBlank:false,
			    inputType:'password',
			    vtype : 'alphanum',
			    fieldLabel: '新密码'+'<font color=red>*</font>'
			},{
			    xtype: 'textfield',
			    name:'confirmPassword',
			    labelWidth : 80,
			    margin : '7 10 0 30',
				columnWidth : 1,
				minLength: 6,
			    maxLength: 200,
			    allowBlank:false,
			    inputType:'password',
			    vtype : 'alphanum',
			    fieldLabel: '确认新密码'+'<font color=red>*</font>'
			}]
        });
        
        me.callParent(arguments);
    },
    modPassword:function(popWindow){
    	var me=this;
    	
    	var oldPassword = me.down('[name="oldPassword"]').getValue();
    	var newPassword = me.down('[name="newPassword"]').getValue();
    	var confirmPassword = me.down('[name="confirmPassword"]').getValue();
    	
    	FHD.ajax({
            url: __ctxPath +'/sys/auth/user/modPassword.f',
            params: {
            	oldPassword: oldPassword,
            	newPassword: newPassword,
            	confirmPassword: confirmPassword
            },
            callback: function (response) {
                if("1"==response.type){
                	FHD.notification(response.message,'提示');
                }else if("2"==response.type){
                	FHD.notification(response.message,'提示');
                }else if("3"==response.type){
                	FHD.notification(response.message,'提示');
                }else if("4"==response.type){
                	FHD.notification(response.message,'提示');
                	popWindow.close();
                }
            }
		});
    },
    reloadData : function(){
    	var me = this;
    	
    }
});