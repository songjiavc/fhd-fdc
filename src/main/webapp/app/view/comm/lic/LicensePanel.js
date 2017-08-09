Ext.define('FHD.view.comm.lic.LicensePanel',{
	extend: 'Ext.form.Panel',
	
	layout:{
		type:'vbox',
		align:'stretch'
	},
	
	
	initComponent:function(){
		var me = this,endDate,remainingDays;
		
		me.endDate = Ext.widget('displayfield',{
	        flex: 1,
	        width:400,
			fieldLabel: '到期日期'
		});
		
		me.remainingDays = Ext.widget('displayfield',{
	        flex: 1,
	        width:400,
			fieldLabel: '剩余天数'
		})
		
		Ext.applyIf(me,{
			items:[Ext.create('Ext.form.Panel',{
				border:false,
			    bodyPadding: 10,
				items:[{
				    xtype:'fieldset',
				    title:'许可证认证',
					layout:{
	                    align: 'center',
	                    pack: 'end',
	                    type: 'vbox'
	                },
				    items:[me.endDate,me.remainingDays,{
						xtype:'filefield',
				        flex: 1,
				        width:400,
						fieldLabel: '上传新许可证',
						name :'licensefile',
				        allowBlank: false,
				        buttonText: '浏览...',
				        msgTarget: 'side',
				        validator: function(v){
				        	return Ext.String.endsWith(v,'.lic') ? true : '许可证文件类型不正确';
				        }
					}]
				}],
				buttons:[{
					text:'更新',
					handler:function(){
						var form = this.up('form').getForm();
			            if(form.isValid()){
			            	form.submit({
			            		url: __ctxPath + '/savelicense.f',
			            		success: function(fp, o) {
			            			var s = o.result.msg.split(',');
			            			me.endDate.setValue("<font color='red' size='4'>" + s[0] + "</font>");
			            			me.remainingDays.setValue("<font color='red' size='4'>" + s[1] + "天</font>");
			                        FHD.notification("上传成功！");
			                    }
			            		
			            	});
			            }
					}
				}]
			})]
			
		});
		
		
		me.callParent(arguments);
		
		FHD.ajax({
			url: __ctxPath + '/findlicense.f',
			callback:function(v){
    			var s = v.msg.split(',');
    			me.endDate.setValue("<font color='red' size='4'>" + s[0] + "</font>");
    			me.remainingDays.setValue("<font color='red' size='4'>" + s[1] + "天</font>");
			}
		});
	}
});