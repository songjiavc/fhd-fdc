
Ext.define('FHD.view.risk.assess.report.ReportUploadForm',{
    extend: 'Ext.form.Panel',
    alias: 'widget.reportuploadform',
    owner:'',	//谁弹出的window
    type:'dailyReport',	//风险日常管理报告 dailyReport ；风险评估报告yearlyReport
    
	autoScroll:true,
	border:false,
	bodyPadding:'0 3 3 3',
    idTextfield:null,
    disNameTextfield:null,
    fileUploadEntityFileUpload:null,
	submitData:function(cardpanel){
		var me=this;

		//提交from表单
        var form = me.getForm();
        var vobj = form.getValues();
        if(!form.isValid()){
	        FHD.notification(FHD.locale.get('fhd.common.prompt'),'存在未通过的验证!');
	        return ;
        }
        if(true){//me.idTextfield.getValue()
        	var type = 'risk_manage_report_template';	//日常报告
        	if(me.type == 'yearlyReport'){
        		type = 'risk_assess_report_template';   //评估报告
        	}
	        FHD.submit({
		        form: form,
		        url: __ctxPath + '/report/saveUploadReport.f',
		        params:{
		        	fileId:me.fileId,
		        	type:type
		        },
		        callback: function (flag) {
		        	//关闭
		        	me.owner.uploadWin.close();
		        	me.owner.reloadData(me.type);
		        }
	        });
        }
	},
    initComponent : function() {
    	var me = this;
    	
    	var bbarItems=new Array();
		bbarItems.push('->');
		bbarItems.push({
			xtype:'button',
			iconCls : "icon-add",
        	text:$locale("fhd.common.add"),
			handler : function(){
				me.submitData();
			}
		});
    	me.bbar = Ext.create('Ext.Toolbar', {
			items:bbarItems
        });
    	
    	me.idTextfield = Ext.create("Ext.form.field.Text",{
    		labelWidth : 95,
			disabled : false,
			name : 'id',
			hidden : true
		});
    	me.deleteStatusTextfield = Ext.create("Ext.form.field.Text",{
    		labelWidth : 95,
			disabled : false,
			name : 'deleteStatus',
			value:"1",
			hidden : true
		});
    	me.disNameTextfield = Ext.create("Ext.form.field.Text",{
    		labelWidth : 95,
			disabled : false,
			fieldLabel : '风险报告名称'+ '<font color=red>*</font>',
			name : 'disName',
			allowBlank : false,
			labelAlign: 'left'
		});
    	
    	var i = me;
    	me.fileUploadEntityFileUpload = Ext.create("FHD.ux.fileupload.FileUpload",{
    		readonly:false,
            labelWidth : 95,
            name:'fileUploadEntityId',//名称
            showModel:'base',//显示模式
            multiSelect:false,
            labelText: $locale('fileupdate.labeltext')+ '<font color=red>*</font>',//标题名称
            allowBlank : false,
            labelAlign: 'left',//标题对齐方式
            a_change:function(me,fileUploadEntitys){
            	var fullName = fileUploadEntitys[0].oldFileName;
            	var name = fullName.split('.')[0];
            	i.disNameTextfield.setValue(name);//me和FileUpload组件的me冲突
            	//me.idTextfield.getValue()得不到id
            	i.fileId = fileUploadEntitys[0].id;
        	}
		});
		
    	Ext.applyIf(me, {
			border:false,
			layout:'fit',
			collapsed : false,
			bbar:me.bbar,
        	items:[
        		Ext.create('Ext.form.FieldSet',{
        			layout:'column',
					defaults:{
						columnWidth:1,
						margin : '7 10 0 30'
					},
        			title:'基本信息',
					items:[
						me.idTextfield,
		       		    me.deleteStatusTextfield,
		       		    me.disNameTextfield,
		       		    me.fileUploadEntityFileUpload
					]
        		})
       		    
    		]
		});
        me.callParent(arguments);
    }
    
    
});
