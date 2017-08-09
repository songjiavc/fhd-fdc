
Ext.define('FHD.view.sys.import.RiskImportFrom',{
    extend: 'Ext.form.Panel',
    alias: 'widget.riskimportfrom',
    
    bodyPadding: 10,
    
    fileArray:new Array(),
    fileTypeArray:["XLS","XLSX"],
    savingMask : null,
	chooseWay:[
		{value:'dataBase',name:$locale('fileuploadwindow.chooseway.database')},
		{value:'fileDir',name:$locale('fileuploadwindow.chooseway.filedir')}
	],
    addFile:function(fb, value){
    	var me=this.up('riskimportfrom');
    	var prefix=value.substring(value.lastIndexOf(".")+1);
    	if(Ext.Array.indexOf(me.fileTypeArray,prefix.toUpperCase())!=-1){
	    	me.updateProgress(1);
			me.fileArray.push(fb);
    	}else if(value!=null&&value!=""){
    		Ext.Msg.alert($locale('fileuploadwindow.filetypeerror.title'),$locale('fileuploadwindow.filetypeerror.text')+me.fileTypeArray.join(","));
    	}
    },
    reloadData:function(){
    	var me=this;
    	me.addButton.reset();
    	me.fileArray = new Array();
    	me.fileArray.length = 0;
    },
    downloadTemp:function(){
    	window.location.href = __ctxPath + "/app/view/sys/import/RiskTemplate.xls";
    },
    updateProgress:function(status){
    	var me=this;
    	var text=null;
    	if(status==0){
    		text=$locale('fileuploadwindow.updateprogress.null');
    	}else if(status==1){
    		text=$locale('fileuploadwindow.updateprogress.finish');
    	}else if(status==2){
    		text=$locale('fileuploadwindow.updateprogress.updataing');
    	}
    },
	submitData:function(cardpanel){
		var me=this;
		//提交from表单
        var form = me.getForm();
        if(me.fileArray.length != 0){
	       	FHD.submit({
		        form: form,
		        url: __ctxPath + '/dataimport/uploadRiskDataUp.f',
		        callback: function(data) {
		        	me.reloadData();
	        		me.showRiskList();
		        }
	        });
        }else{
    		//Ext.Msg.alert('错误','请选择要上传的数据！');
    	}
	},
	showRiskList:function(){
    	var me=this;
    	var winId = "win" + Math.random()+"$ewin";
    	me.riskFromExcelList=Ext.create('FHD.view.sys.import.RiskFromExcelList',{
    		winId:winId,
    		riskImportFun : function(){
				me.savingMask = new Ext.LoadMask(Ext.getBody(), {
					msg:"处理中..."
				});
				me.savingMask.show();
    			FHD.ajax({
					url : __ctxPath+ '/dataimport/risk/importRiskToBD.f',
					params : {
					},
					callback : function(data) {
						me.savingMask.hide();
						if(data.success){
							FHD.notification('导入数据成功',FHD.locale.get('fhd.common.prompt'));
						}else{
							Ext.Msg.alert('失败','数据导入失败！');	 								
						}
					}
				});
    		}     	
    	});
		
		me.riskFromExcelList.reloadData();
		
		var win = Ext.create('FHD.ux.Window',{
			id:winId,
			title:'数据验证',
			iconCls : 'icon-spellcheck',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true,//（是否增加最大化，默认没有）
			//maximized :true,
			buttonAlign: 'center',
			buttons:[{
        		text:'数据导入',
        		id : 'riskfromExcelListAddSaveId',
        		iconCls: 'icon-ibm-action-export-to-excel',
        		handler:function(){me.riskFromExcelList.confirm(); }
        		}, {
        		text:'关闭',
        		iconCls: 'icon-ibm-close',
        		handler:function(){me.riskFromExcelList.closeWin();}
        		}]
    	}).show();
    	
    	win.add(me.riskFromExcelList);
    },
    initComponent : function() {
    	var me = this;
    	
    	var importTypeStore = Ext.create('Ext.data.Store', { // 导入类型store
            fields: ['id', 'text'],
            data: [
                {
                    id: 'riskType',
                    text: '全部'
                }
            ],
            autoLoad: true
        });
    	
		me.addButton=Ext.widget('filefield',{
        	name:'file',
        	flex: 1,
            width: 413,
            fieldLabel: '2. 选择文件',
            emptyText: '选择风险导入模板.....', 
            labelAlign: 'right',
            buttonText: '浏览',
            name: 'file',
            submitValue: false,
        	listeners: {
	            change: me.addFile
	        }
		});
		
		var addStyleStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'text'],
            data: [{
                id: 'add',
                text: '增量导入'
            }],
            autoLoad: true
        });
		
		Ext.apply(me, {
			border:false,
			collapsed : false,
        	items:[{
	        		xtype: 'fieldset',
	                layout: {
	                    align: 'center',
	                    pack: 'end',
	                    type: 'vbox'
	                },
        			title:'导入向导',
        			items: [{
	                    xtype: 'displayfield',
	                    flex: 1,
	                    width: 413,
	                    fieldLabel: '1. 模版下载',
	                    labelAlign: 'right',
	                    value: '<a target="_blank" href="'+__ctxPath + '/tmp/import/download.jsp?path=/app/view/sys/import/风险导入模板.xls"><img src="'+__ctxPath+'/images/icons/action_export_to_excel.gif">风险模板</a>'
	                    
	                }, me.addButton
	                ,{
	                    xtype: 'combobox',
	                    store: importTypeStore,
	                    name: 'import',
	                    flex: 1,
	                    width: 413,
	                    fieldLabel: '3. 数据类型',
	                    labelAlign: 'right',
	                    displayField: 'text',
	                    valueField: 'id',
	                    columnWidth: .5,
	                    triggerAction: 'all',
	                    value: 'riskType'
	                },          
//	                {
//	                    xtype: 'combobox',
//	                    store: addStyleStore,
//	                    name: 'addDelete',
//	                    flex: 1,
//	                    width: 413,
//	                    fieldLabel: '4. 导入方式',
//	                    labelAlign: 'right',
//	                    displayField: 'text',
//	                    valueField: 'id',
//	                    columnWidth: .5,
//	                    triggerAction: 'all',
//	                    value: 'add'
//	                },
	                {
	                    xtype: 'displayfield',
	                    flex: 1,
	                    width: 413,
	                    fieldLabel: '4. 数据导入',
	                    labelAlign: 'right',
	                    value: '<button class=\'x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon\'' 
	                    	 + 'onclick=\'Ext.getCmp("' + me.id + '").submitData()\' >上传数据</button>'
	                },{
	                    xtype: 'displayfield',
	                    flex: 1,
	                    width: 413,
	                    fieldLabel: '5. 验证信息',
	                    labelAlign: 'right',
	                    value: '<a href="javascript:void(0)" onclick=\'Ext.getCmp("' + me.id + '").showRiskList()\' >查看</a>'
	                }]
//					items:[
//		       		    me.disNameTextfield,
//		       		    me.addButton
//					]
			}]
		});
        me.callParent(arguments);
        me.reloadData();
    }
});
