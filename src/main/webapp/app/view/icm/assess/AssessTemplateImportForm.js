Ext.define("FHD.view.icm.assess.AssessTemplateImportForm",{
	extend: 'Ext.form.Panel',
	alias: 'widget.assesstemplateimportform',
	// 初始化方法
	initComponent: function() {
		var me = this;
		
    	var bbarItems=[
    		'->',
    		{
	            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
	            name: 'icm_assessplan_card_undo_btn' ,
	            iconCls: 'icon-operator-home',
	            handler: function () {
	            	var assesstemplatecardpanel = me.up('assesstemplatecardpanel');
	            	if(assesstemplatecardpanel){
	            		assesstemplatecardpanel.navBtnHandler(0);
	            	}
	            }
	        },
	        {
				xtype:'button',
				iconCls : "icon-spellcheck",
            	text:'数据验证',
				handler : function(){
					me.validate();
				}
			}
    	];
    	me.importFile = Ext.create('FHD.ux.fileupload.FileUpload', {
		    name:'fileId',
		    showModel:'base',
		    windowModel:'upload',
		    labelAlign:'left',
		    fileTypeArray:["xls","xlsx"],
		    allowBlank:false,
		    labelWidth:100,
		    //value:'504061e0-c97d-4c4f-ab05-32b9f059293c',
		    multiSelect:false
		});
		me.uploadFile = Ext.create('FHD.ux.fileupload.FileUpload', {
		    name:'fileId',
		    showModel:'base',
		    windowModel:'upload',
		    labelAlign:'left',
		    allowBlank:false,
		    labelWidth:100,
		    multiSelect:true
		});
		Ext.applyIf(me, {
			bbar:{
	            items:bbarItems
	        },
	        items: [{
				xtype:'fieldset',
				layout:{
                    align: 'center',
                    pack: 'end',
                    type: 'vbox'
                },
				title: '导入向导',
                items: [{
                	xtype:'fieldcontainer',
                	flex: 1,
                    width: 413,
                	fieldLabel: '导入模板',
                    labelAlign: 'right',
                    items:[me.importFile]
                },{
                	xtype:'fieldcontainer',
                	flex: 1,
                    width: 413,
                	fieldLabel: '样本附件',
                    labelAlign: 'right',
                    items:[me.uploadFile]
                }]
	        }]
	    });
	    me.callParent(arguments);
	},
	validate : function(){
		var me = this;
		
		var fileId = me.importFile.getValue();
		var uploadfile = me.uploadFile.getStoreValue();
		var names,objs,obj;
		if (!fileId) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请上传导入模板!');
            return false;
        }
        if(uploadfile){
        	names = new Array();
        	objs = new Array();
	    	uploadfile.each(function(value){
	    		obj = {};
	    		obj["id"] = value.data.id;
	    		obj["fileName"] = value.data.oldFileName;
	    		names.push(value.data.oldFileName);
	    		objs.push(obj);
	    	});
        }
        var myMask = new Ext.LoadMask(Ext.getBody(), {
			msg:"处理中，请稍后..."
		});
		myMask.show();
		FHD.ajax({
            url: __ctxPath + '/icm/assess/validateData.f',
            params: {
            	fileId: fileId,
				uploadFileNames: encodeURI(names.join(":"))
            },
            callback: function (response) {
                if(response && response.success){
                	myMask.hide();
                	Ext.MessageBox.confirm('提示', '验证通过，您确定要导入吗?', function(btn){
						if('yes'==btn){
							myMask.show();
							FHD.ajax({
					            url: __ctxPath + '/icm/assess/saveTempAssessTemplateByExcel.f',
					            params: {
					            	fileId: fileId,
					            	uploadFiles:Ext.JSON.encode(objs)
					            },
					            callback: function (response) {
					                if(response && response.success){
					                	myMask.hide();
					                	FHD.notification('操作成功!','提示');
					                }else{
					                	myMask.hide();
					                	Ext.MessageBox.alert('提示', '操作失败');
					                }
					            }
					    	});
							
						}
				    });
                }else{
                	myMask.hide();
                	if(response.sheet1blankproblem || response.sheet2blankproblem){
                		FHD.alert("请根据模版中第三行的填写说明补充必填项后再导入。<br/>第一个sheet的问题项："+response.sheet1blankproblem+
	                	"<br/>第二个sheet的问题项："+response.sheet2blankproblem);
                	}else if(response.sheet1fileproblem || response.sheet2fileproblem){
                		FHD.alert("已上传的附件的名称与样本名称不对应，请修改样本名称或重新上传与之对应附件。<br/>第一个sheet的问题项："+response.sheet1fileproblem+
	                	"<br/>第二个sheet的问题项："+response.sheet2fileproblem);
                	}else if(response.sheetfileproblem){
                		FHD.alert("请根据模版中“不合格”的样本补充需上传的附件。");
                	}
                	
                }
            }
    	});
	},
	reloadData:function(){
		var me=this;
		
	}
});