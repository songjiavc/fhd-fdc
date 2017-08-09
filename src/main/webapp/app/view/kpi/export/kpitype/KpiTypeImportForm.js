Ext.define('FHD.view.kpi.export.kpitype.KpiTypeImportForm', {
    extend: 'Ext.form.Panel',
    fileArray: new Array(),
    fileTypeArray: ["XLS", "XLSX"],

    findIsCoverage: function () {
       /* var me = this;
        var addStyle = me.down('[name="addDelete"]').getValue();
        var isCoverage = false;
        if (addStyle == 'add') {
            isCoverage = false;
        } else {
            isCoverage = true;
        }
        return isCoverage;*/
    	return false;
    },
    showValidateGrid: function (addStyle,data,result) {
        var me = this;
        
        //导入类型
        var importType = me.down('[name="import"]').getValue();
        var isCoverage = me.findIsCoverage();
        var importUrl = '';
        if ('kpiType' == importType) {
        	importUrl = __ctxPath + '/kpi/kpiTmp/mergeKpiTmpKpiType.f?isCoverage=' + isCoverage;
            me.validateGrid = Ext.create('FHD.view.kpi.export.kpitype.KpiTypeImportGrid', {
                pcontainer: me,
                addStyle: isCoverage,
                importUrl: __ctxPath + '/kpi/kpiTmp/mergeKpiTmpKpiType.f?isCoverage=' + isCoverage,
                type: 'KC'
            });
        } else if ('strategyMap' == importType) {
        	importUrl = __ctxPath + '/sm/import/confirmimportsmdata.f?isCoverage='+ isCoverage;
            me.validateGrid = Ext.create('FHD.view.kpi.export.strategymap.StrategyMapImportGrid', {
                pcontainer: me,
                isCoverage: isCoverage
            });
        } else if ('category' == importType) {
        	importUrl = __ctxPath + '/sc/import/confirmimportscdata.f?isCoverage='+ isCoverage;
            me.validateGrid = Ext.create('FHD.view.kpi.export.category.categoryImportGrid', {
                pcontainer: me,
                isCoverage: isCoverage
            });
        } else if ('kpi' == importType) {
        	importUrl = __ctxPath + '/kpi/kpiTmp/mergeKpiTmpKpi.f?isCoverage=' + isCoverage;
            me.validateGrid = Ext.create('FHD.view.kpi.export.kpitype.KpiTypeImportGrid', {
                pcontainer: me,
                addStyle: isCoverage,
                importUrl: __ctxPath + '/kpi/kpiTmp/mergeKpiTmpKpi.f?isCoverage=' + isCoverage,
                type: 'KPI'
            });
        } else if('all' == importType){
        	
        	importUrl = __ctxPath + '/kpi/import/confirmimportalldata.f?isCoverage=' + isCoverage;
        	
    		me.validateGrid = Ext.create('Ext.tab.Panel',{
    			border:false
    		});
			var kpiTypeValidateGrid = Ext.create('FHD.view.kpi.export.kpitype.KpiTypeImportGrid', {
								title:'1.指标类型',
				                pcontainer: me,
				                addStyle: isCoverage,
				                importUrl: __ctxPath + '/kpi/kpiTmp/mergeKpiTmpKpiType.f?isCoverage=' + isCoverage,
				                type: 'KC'
				             });
			me.validateGrid.add(kpiTypeValidateGrid);
			var kpiValidateGrid = Ext.create('FHD.view.kpi.export.kpitype.KpiTypeImportGrid', {
									title:'2.指标',
					                pcontainer: me,
					                addStyle: isCoverage,
					                importUrl: __ctxPath + '/kpi/kpiTmp/mergeKpiTmpKpi.f?isCoverage=' + isCoverage,
					                type: 'KPI'
					            });
			me.validateGrid.add(kpiValidateGrid);
			var smValidateGrid = Ext.create('FHD.view.kpi.export.strategymap.StrategyMapImportGrid', {
				title:'3.目标和衡量指标',
                pcontainer: me,
                isCoverage: isCoverage
            });
            me.validateGrid.add(smValidateGrid);
            
            var scValidateGrid = Ext.create('FHD.view.kpi.export.category.categoryImportGrid', {
            	title:'4.记分卡和衡量指标',
                pcontainer: me,
                isCoverage: isCoverage
            });
            me.validateGrid.add(scValidateGrid);
            me.validateGrid.setActiveTab(0);
        }
        else {
        	return ;
        }
        
        me.window = Ext.create('FHD.ux.Window', {
            title : "数据验证",
        	iconCls : 'icon-spellcheck',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible: true,
            buttonAlign: 'center',
        				buttons: [
        				    { 
        				    	name:'importDataBtn',
        						text : '数据导入',
        						iconCls: 'icon-ibm-action-export-to-excel',
        				    	handler:function(){
        				    		if(me.window.body){
	        				    		me.window.body.mask("导入数据中...", "x-mask-loading");
        				    		}
        				    		me.window.down('[name=importDataBtn]').setDisabled(true);
        				    		FHD.ajax({
					                    async: true,
					                    url: importUrl,
					                    addStyle: isCoverage,
					                    callback: function (data) {
					                    	if(me.window.body){
					                    		me.window.body.unmask();
					                    	}
					                    	if(data.success) {
					                    		 FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
					                    	} else {
					                    		FHD.notification('导入过程出现错误', FHD.locale.get('fhd.common.prompt'));
					                    	}
					                        me.window.close();
					                    }
					                });
        				    	}
        				    },
        				    { 
        				    	name:'closeBtn',
        				    	text: FHD.locale.get("fhd.common.close"),
        						iconCls: 'icon-ibm-close',
        				    	handler:function(){
        				    		me.window.close();
        				    	}
        				    }
        				]
        
       		 ,
			listeners:{
				close: function(p, e){
					//数据验证按钮可用
					me.window.down('[name=importDataBtn]').enable(true);
				}
			}
        });
		if(result){
        	me.window.down('[name=importDataBtn]').enable(true);
		}else{
			me.window.down('[name=importDataBtn]').setDisabled(true);
		}
        me.window.add(me.validateGrid);
        me.window.show();
    },
    download: function (id) {
        //window.location.href=__ctxPath + '';
    },
    uploadFile: function () {
        var me = this;
        // var filePath = me.down('[name="fileField"]').getValue();
        var url; // 导入临时表中的url
        var mergeUrl; // 导入业务表中的Url
        //提交from表单
        var form = me.getForm();
        var importType = me.down('[name="import"]').getValue();
        if (!importType) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择导入类型！');
            return false;
        }

        /*var addStyle = me.down('[name="addDelete"]').getValue();
        if (!addStyle) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择导入方式！');
            return false;
        }*/
        var isCoverage = me.findIsCoverage();

        if (importType == 'kpiType') {
            url = __ctxPath + '/kpi/kpi/uploadKpitype.f?isCoverage=' + isCoverage;
            mergeUrl = __ctxPath + '/kpi/kpiTmp/mergeKpiTmpKpiType.f?isCoverage=' + isCoverage;
        } else if (importType == 'strategyMap') {
            url = __ctxPath + '/sm/import/uploadsmdatatotmp.f?isCoverage=' + isCoverage;
            mergeUrl = __ctxPath + '/sm/import/confirmimportsmdata.f?isCoverage=' + isCoverage;
        } else if (importType == "category") {
            url = __ctxPath + '/sc/import/uploadscdatatotmp.f?isCoverage=' + isCoverage;
            mergeUrl = __ctxPath + '/sc/import/confirmimportscdata.f?isCoverage=' + isCoverage;
        } else if (importType == "kpi") {
            url = __ctxPath + '/kpi/kpi/uploadKpiTmp.f?isCoverage=' + isCoverage;
            mergeUrl = __ctxPath + '/kpi/kpiTmp/mergeKpiTmpKpi.f?isCoverage=' + isCoverage;
        } else if (importType == 'kpiResult') {
            url = __ctxPath + '/kpi/kpi/uploadKpiResultToDb.f?';
        
        } else if (importType == 'strategyMapResult') {
            url = __ctxPath + '/sm/import/uploadsmresultdatatodbs.f';
            
        } else if(importType =='categoryResult'){
        	url = __ctxPath + '/sc/import/uploadscresultdatatodbs.f';
        } else if(importType == 'all'){
        	url = __ctxPath + '/kpi/import/uploadkpialldatatotmp.f?isCoverage=' + isCoverage;
        	mergeUrl = __ctxPath + '/kpi/import/confirmimportalldata.f?isCoverage=' + isCoverage;
        }
        if (me.fileArray.length != 0) {
            //me.body.mask("上传中...", "x-mask-loading");
            FHD.submit({
                form: form,
                url: url,
                callback: function (data) {
                    me.fileArray = new Array();
                    me.body.unmask();
                    
                    if(importType=='strategyMapResult'||importType=='categoryResult'){
                    		if (data.success&&!data.uploadResult){
                    			var errors = data.errors;
                    			me.showStrategyMapResultValidateGrid(errors);
                    		}else{
                    			//FHD.notification('导入成功', FHD.locale.get('fhd.common.prompt'));
                    		}
                    }else if (importType =='kpiResult'){
                    	if (data.success&&!data.uploadResult){
                    		var errors = data.errors;
                    		me.showKpiResultValidateGrid(errors);
                    	}
                    }
                    else{
                    	if (data.success && !data.uploadResult) {
		                        if (me.getImportDir(importType)) {
		                            //me.showValidateGrid(addStyle,data,false);
		                            me.showValidateGrid('',data,false);
		                        } else {
		                            FHD.notification('导入数据存在错误', FHD.locale.get('fhd.common.prompt'));
		                        }
		
		                    } else {
		                        if (me.getImportDir(importType)) {
		                            if (data.uploadResult) {
		                            	//me.showValidateGrid(addStyle,data,true);
		                            	me.showValidateGrid('',data,true);
		                                /*var msg = '验证通过,确认导入数据么？';
		                                Ext.Msg.confirm('提示', msg, function (g) {
		                                    if (g == 'yes') {
		                                        if (me.body != undefined) {
		                                            me.body.mask("导入数据中...", "x-mask-loading");
		                                        }
		                                        FHD.ajax({
		                                            async: true,
		                                            url: mergeUrl,
		                                            params: {
		                                                addStyle: addStyle
		                                            },
		                                            callback: function (data) {
		                                                if (data) {
		                                                    FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
		                                                } else {
		                                                    FHD.notification('导入过程出现错误', FHD.locale.get('fhd.common.prompt'));
		                                                }
		                                                if (me.body != undefined) {
		                                                    me.body.unmask();
		                                                }
		                                            }
		                                        });
		
		
		                                    }
		                                })*/
		                            }
		                        } else {
		                            if (me.getImportDir(importType)) {
		                                FHD.notification('导入成功', FHD.locale.get('fhd.common.prompt'));
		                            }
		
		                        }
		                    }
                    }
                }
            });
        } else {
            Ext.Msg.alert('错误', '请选择要上传的数据！');
        }
    },
    bodyPadding: 10,
    
    showStrategyMapResultValidateGrid:function(errors){
    	var me = this;
    	me.strategyMapResultWindow = Ext.create('FHD.ux.Window', {
            title : "数据验证",
        	iconCls : 'icon-spellcheck',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible: true,
            buttonAlign: 'center',
        				buttons: [
        				    { 
        				    	name:'closeBtn',
        				    	text: FHD.locale.get("fhd.common.close"),
        						iconCls: 'icon-ibm-close',
        				    	handler:function(){
        				    		me.strategyMapResultWindow.close();
        				    	}
        				    }
        				]
        });
        me.strategyMapResultValidateGrid = Ext.create('FHD.view.kpi.export.data.ImportGatherDataView',{
        	items:errors
        })
        me.strategyMapResultWindow.add(me.strategyMapResultValidateGrid);
        me.strategyMapResultWindow.show();
    },
    showKpiResultValidateGrid:function(errors){
    	var me = this;
    	me.kpiResultWindow = Ext.create('FHD.ux.Window', {
            title : "数据验证",
        	iconCls : 'icon-spellcheck',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible: true,
            buttonAlign: 'center',
        				buttons: [
        				    { 
        				    	name:'closeBtn',
        				    	text: FHD.locale.get("fhd.common.close"),
        						iconCls: 'icon-ibm-close',
        				    	handler:function(){
        				    		me.kpiResultWindow.close();
        				    	}
        				    }
        				]
        });
        me.kpiResultValidateGrid = Ext.create('FHD.view.kpi.export.data.ImportKpiGatherDataView',{
        	items:errors
        })
        me.kpiResultWindow.add(me.kpiResultValidateGrid);
        me.kpiResultWindow.show();
    },

    initComponent: function () {
        var me = this;
        var importTypeStore = Ext.create('Ext.data.Store', { // 导入类型store
            fields: ['id', 'text'],
            data: [
            	{
                    id: 'all',
                    text: '全部'
                },
            	{
                    id: 'kpiType',
                    text: '1.指标类型'
                }, {
                    id: 'kpi',
                    text: '2.指标'
                }, {
                    id: 'strategyMap',
                    text: '3.目标和衡量指标'
                }, {
                    id: 'category',
                    text: '4.记分卡和衡量指标'
                }, {
                    id: 'kpiResult',
                    text: '5.指标采集数据'
                }
                , {
                id: 'strategyMapResult',
                text: '6.目标采集数据'
            }, {
                id: 'categoryResult',
                text: '7.记分卡采集数据'
            }
            ],
            autoLoad: true
        });
        var addStyleStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'text'],
            data: [{
                id: 'add',
                text: '增量导入'
            }, {
                id: 'delete',
                text: '覆盖导入'
            }],
            autoLoad: true
        });
        Ext.applyIf(me, {
            items: [{
                xtype: 'fieldset',
                layout: {
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
                    value: '<a target="_blank" href="'+__ctxPath+ '/tmp/import/download.jsp?path='+'/tmp/import/kpi/监控预警导入模板.xls"><img src="'+__ctxPath+'/images/icons/action_export_to_excel.gif">监控预警导入模板</a>'
                }, {
                    xtype: 'filefield',
                    name: 'fileField',
                    flex: 1,
                    width: 413,
                    emptyText: '选择监控预警导入模板.....',  
                    fieldLabel: '2. 选择文件',
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
                    value: 'all'
                }, 
                	/*{
                    xtype: 'combobox',
                    store: addStyleStore,
                    name: 'addDelete',
                    flex: 1,
                    width: 413,
                    fieldLabel: '4. 导入方式',
                    labelAlign: 'right',
                    displayField: 'text',
                    valueField: 'id',
                    columnWidth: .5,
                    triggerAction: 'all',
                    value: 'add'
                }, */
                	{
                    xtype: 'displayfield',
                    flex: 1,
                    width: 413,
                    fieldLabel: '4. 数据导入',
                    labelAlign: 'right',
                    value: '<button class="x-btn x-form-file-btn x-unselectable x-btn-default-small x-noicon x-btn-noicon x-btn-default-small-noicon" onclick=\'Ext.getCmp("' + me.id + '").uploadFile()\' >上传数据</button>'
                }, {
                    xtype: 'displayfield',
                    flex: 1,
                    width: 413,
                    fieldLabel: '5. 验证信息',
                    labelAlign: 'right',
                    value: '<a href="javascript:void(0)" onclick=\'Ext.getCmp("' + me.id + '").checkImportResult()\' >查看</a>'
                }]
            }]
        });

        me.callParent(arguments);
    },
    checkImportResult: function () {
        var me = this;
        //var addStyle = me.down('[name="addDelete"]').getValue();
        var addStyle = '';
        me.showValidateGrid(addStyle);
    },
    getImportDir: function (type) {
        if (type == 'kpi' || type == 'kpiType' || type == 'strategyMap' || type == 'category'||type=='all') {
            return true;
        } else {
            return false;
        }
    }
});