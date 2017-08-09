Ext.define('FHD.view.icm.import.ProcessImportPanel', {
	extend: 'Ext.form.Panel',
	alias: 'widget.processimportpanel',
	bodyPadding: 10,
	
	hasError:true,  //默认有错误
	countMap:{},	//导入时每个tab的个数信息
	
    fileArray: new Array(),
    fileTypeArray: ["XLS", "XLSX"],
    
	downloadUrl:'/tmp/import/process/内控模板.xls',
	downloadShowName:'内控导入模板',
	
	// 初始化方法
	initComponent: function() {
		var me = this;
    	
    	me.typeStore = Ext.create('Ext.data.Store', {
    	    fields: ['id', 'name'],
    	    data : [
    	        {"id":"all", "name":"全部"},
    	        {"id":"0", "name":"1.流程"},
    	        {"id":"1", "name":"2.流程节点"},
    	        {"id":"2", "name":"3.流程节点关系"},
    	        {"id":"3", "name":"4.控制标准"},
    	        {"id":"4", "name":"5.控制标准-流程-风险"},
    	        {"id":"5", "name":"6.控制措施"},
    	        {"id":"6", "name":"7.流程-流程节点-风险-控制措施"},
    	        {"id":"7", "name":"8.穿行测试评价点"},
    	        {"id":"8", "name":"9.抽样测试评价点"}
    	    ]
    	});
    	
		Ext.applyIf(me, {
	        items: [{
				xtype:'fieldset',
				layout:{
                    align: 'center',
                    pack: 'end',
                    type: 'vbox'
                },
				title: '导入向导',
                items: [{
                    xtype: 'displayfield',
                    flex: 1,
                    width: 413,
                    fieldLabel: '1. 模版下载',
                    labelAlign: 'right',
                    value: '<a href="'+__ctxPath+me.downloadUrl+'"><img src="'+__ctxPath+'/images/icons/action_export_to_excel.gif">'+me.downloadShowName+'</a>'
                }, {
                    xtype: 'filefield',
                    name: 'fileField',
                    flex: 1,
                    width: 413,
                    fieldLabel: '2. 选择文件',
                    emptyText: '选择内控导入模板.....',
                    labelAlign: 'right',
                    buttonText: '浏览',
                    name: 'file',
                    submitValue: false,
                    listeners: {
                        change: function (fb, value) {
                            var me = fb.up('panel');
                            var prefix = value.substring(value.lastIndexOf(".") + 1);
                            if (Ext.Array.indexOf(me.fileTypeArray, prefix.toUpperCase()) != -1) {
                                me.fileArray.push(fb);
                            } else if (value != null && value != "") {
                                Ext.Msg.alert($locale('fileuploadwindow.filetypeerror.title'), $locale('fileuploadwindow.filetypeerror.text') + me.fileTypeArray.join(","));
                            }
                        }
                    }

                }, {
                    xtype: 'combobox',
                    store: me.typeStore,
                    name: 'type',
                    flex: 1,
                    width: 413,
                    fieldLabel: '3. 数据类型',
                    labelAlign: 'right',
                    displayField: 'name',
                    valueField: 'id',
                    value:'all',
                    queryMode: 'local',
                    triggerAction: 'all'
                }, {
                    xtype: 'displayfield',
                    flex: 1,
                    width: 413,
                    fieldLabel: '5. 数据导入',
                    labelAlign: 'right',
                    value: '<button onclick=\'Ext.getCmp("' + me.id + '").uploadFile()\' class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon">上传数据</button>'
                }, {
                    xtype: 'displayfield',
                    flex: 1,
                    width: 413,
                    fieldLabel: '6. 验证信息',
                    labelAlign: 'right',
                    value: '<a href="javascript:void(0)" onclick=\'Ext.getCmp("' + me.id + '").showValidateGrid()\' >查看</a>'
                }]
	        }]
	    });
		
	    me.callParent(arguments);
	},
	/*查看导入的验证信息*/
	showValidateGrid : function(){
		var me = this;
		
		//弹出验证信息页面
    	if(!me.viewWindow){
			var type = me.down('[name="type"]').getValue();
	    	me.importValidatePanel = Ext.create('FHD.view.icm.import.ImportValidatePanel', {
				type:type,
				countMap:me.countMap
			});
			me.viewWindow = Ext.create('FHD.ux.Window', {
				title : "数据验证",
				iconCls : 'icon-spellcheck',
				closeAction:'hide',
				maximizable : true,
				buttonAlign: 'center',
				buttons: [
				    { 
				    	name:'importDataBtn',
						text : '数据导入',
						iconCls: 'icon-ibm-action-export-to-excel',
				    	handler:function(){
				    		//数据验证按钮不可用，防止重复提交
				    		me.viewWindow.down('[name=importDataBtn]').setDisabled(true);
				    		
				    		//滚动条提示
				            me.body.mask("导入数据中...", "x-mask-loading");
			    			FHD.ajax({
			    	            url: __ctxPath + '/icm/process/import/importData.f',
			    	            async:true,
			    	            params: {
			    	            	type: type
			    	            },
			    	            callback: function (response) {
			    	            	me.body.unmask();
			    	                if(response.success && true==response.success){
			    	                	FHD.notification('导入数据成功', FHD.locale.get('fhd.common.prompt'));
			    	                }else{
			    	                	FHD.notification('导入数据失败', FHD.locale.get('fhd.common.prompt'));
			    	                }
			    	                me.viewWindow.close();
			    	            }
			    			});
				    	}
				    },
				    { 
				    	name:'closeBtn',
				    	text: FHD.locale.get("fhd.common.close"),
						iconCls: 'icon-ibm-close',
				    	handler:function(){
				    		me.viewWindow.close();
				    	}
				    }
				]
			});
			me.viewWindow.add(me.importValidatePanel);
			//对于新创建的window,表示不是先上传，再在同一页面操作查看的操作。导入按钮置灰
			me.viewWindow.down('[name=importDataBtn]').setDisabled(true);
    	}
		me.viewWindow.show();
		me.importValidatePanel.reloadData();
	},
	reloadData:function(){
		var me=this;
		
	},
	
	/*上传文件*/
    uploadFile: function () {
		var me = this;
		
		//验证
		var filefield = me.down("filefield").getValue();
		if (filefield=='') {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择上传文件!');
            return false;
        }
		
		var type = me.down('[name="type"]').getValue();
		if (!type) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择数据类型!');
            return false;
        }
		
        //上传数据
        FHD.submit({
        	form: me.getForm(),
            url: __ctxPath + '/icm/process/import/saveTempProcessByExcel.f',
            async:true,
            params: {
            	type: type
            },
            callback: function (response) {
                if(response){
                	me.countMap = response.countMap;//将个数信息放在全局变量中
                	//设置数据导入按钮状态
            		if('0' == response.errorAllCount && '0' != response.correctAllCount){
            			me.hasError = false;
            		}else{
            			me.hasError = true;
            		}
                	
                	//如果之前存在，销毁
                	if(me.viewWindow){
                		me.viewWindow.destroy();
                	}
                	me.importValidatePanel = Ext.create('FHD.view.icm.import.ImportValidatePanel', {
            			type:type,
            			countMap:me.countMap
            		});
            		me.viewWindow = Ext.create('FHD.ux.Window', {
            			title : "数据验证",
            			iconCls : 'icon-spellcheck',
            			closeAction:'hide',
            			maximizable : true,
            			buttonAlign: 'center',
            			buttons: [
            			    { 
            			    	name:'importDataBtn',
            					text : '数据导入',
            					iconCls: 'icon-ibm-action-export-to-excel',
            			    	handler:function(){
            			    		Ext.MessageBox.show({
            							title : '提示',
            							width : 260,
            							msg : '确定导入么？',
            							buttons : Ext.MessageBox.YESNO,
            							icon : Ext.MessageBox.QUESTION,
            							fn : function(btn) {
            								if(btn == 'yes') {
            									//数据验证按钮不可用，防止重复提交
                        			    		me.viewWindow.down('[name=importDataBtn]').setDisabled(true);
                        			    		
                        		    			FHD.ajax({
                        		    	            url: __ctxPath + '/icm/process/import/importData.f',
                        		    	            async:false,
                        		    	            params: {
                        		    	            	type: type
                        		    	            },
                        		    	            callback: function (response) {
                        		    	                if(response.success && true==response.success){
                        		    	                	FHD.notification('导入数据成功', FHD.locale.get('fhd.common.prompt'));
                        		    	                }else{
                        		    	                	FHD.notification('导入数据失败', FHD.locale.get('fhd.common.prompt'));
                        		    	                }
                        		    	                me.viewWindow.close();
                        		    	            }
                        		    			});
            								}
            							}
            						});
            			    	}
            			    },
            			    { 
            			    	name:'closeBtn',
            			    	text: FHD.locale.get("fhd.common.close"),
            					iconCls: 'icon-ibm-close',
            			    	handler:function(){
            			    		me.viewWindow.close();
            			    	}
            			    }
            			]
            		});
            		me.viewWindow.add(me.importValidatePanel);
            		me.viewWindow.show();
            		//设置数据导入按钮状态
            		if(!me.hasError){
            			me.viewWindow.down('[name=importDataBtn]').enable(true);
            		}else{
            			me.viewWindow.down('[name=importDataBtn]').setDisabled(true);
            		}
                }else{
                	me.body.unmask();
                }
            }
    	});
    }
});