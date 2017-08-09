/**
 * 组织机构导入
 */
Ext.define('FHD.view.sys.organization.import.OrgImpFormPanel', {
	extend: 'Ext.form.Panel',
	alias: 'widget.orgimpformpanel',
	requires:['FHD.ux.fileupload.FileUpload'],
	
	fileArray: new Array(),
	fileTypeArray: ["XLS", "XLSX"],
	
	//上传全部数据显示
	reloadAllData: function(gridData){
		var me = this;
		me.orgImpValidateGrid.reloadData(gridData.orgmap.datas);//机构列表数据
		me.roleImpValidateGrid.reloadData(gridData.rolemap.datas);//角色列表数据
		me.posiImpValidateGrid.reloadData(gridData.posimap.datas);//岗位列表数据
		me.empGridPanel.reloadData(gridData.empMap.datas);//人员
		me.empOrgGridPanel.reloadData(gridData.empOrgMap.datas);//人员机构
		me.empRoleGridPanel.reloadData(gridData.empRoleMap.datas);//人员角色
		me.empPostGridPanel.reloadData(gridData.empPostMap.datas);//人员岗位
	},
	
	//上传数据
	uploadFile: function () {
        var me = this;
        var url; // 导入临时表中的url
        //提交from表单
        var form = me.getForm();
        var importType = me.down('[name="import"]').getValue();
        if (!importType) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择导入类型！');
            return false;
        }
        if (importType == 'org') {
            url = __ctxPath + '/sys/organization/import/importorgdata.f';
        } else if(importType == 'role'){
        	url = __ctxPath + '/sys/role/import/importroledata.f';
        }else if(importType == 'position'){
        	url = __ctxPath + '/sys/position/import/importposidata.f';
        }else if(importType == 'all'){//全部导入
        	url = __ctxPath + '/sys/organization/import/importalldata.f';
        }else if(importType == 'emp'){//人员
        	url = __ctxPath + '/sys/org/import/importEmp.f';
        }else if(importType == 'empOrg'){//人员机构
        	url = __ctxPath + '/sys/org/import/importEmpOrg.f';
        }else if(importType == 'empRole'){//人员角色
        	url = __ctxPath + '/sys/org/import/importEmpRole.f';
        }else if(importType == 'empPost'){//人员岗位
        	url = __ctxPath + '/sys/org/import/importEmpPost.f';
        }
        if (me.fileArray.length != 0) {
            FHD.submit({
                form: form,
                url: url,
                callback: function (data) {
                	me.body.unmask();
                	me.fileArray = new Array(); 
                    if(data.success && data.datas){
                    	me.showValidateGrid(data.datas,data.errorInfo);
                    }
                }
            });
        } else {
            Ext.Msg.alert('错误', '请选择要上传的数据！');
        }
    },
	
	//查看
	checkImportResult: function () {
        var me = this;
        var checkUrl;//查询临时表的url
        var importType = me.down('[name="import"]').getValue();
        if(importType == 'org'){
	        	checkUrl = __ctxPath + '/sys/organization/import/findalltmpsysorganizations.f';
	        }else if(importType == 'role'){
	        	checkUrl = __ctxPath + '/sys/role/import/findalltmpsysrolesgrid.f';
	        }else if('position' == importType){
	        	checkUrl = __ctxPath + '/sys/position/import/findalltmpsyspositions.f';
	        }else if('emp' == importType){//人员
	        	checkUrl = __ctxPath + '/sys/org/import/findalltmpsysemps.f';
	        }else if(importType == 'empOrg'){//人员机构
	        	checkUrl = __ctxPath + '/sys/org/import/findallemporgs.f';
	        }else if(importType == 'empRole'){//人员角色
	        	checkUrl = __ctxPath + '/sys/org/import/findallemproles.f';
	        }else if(importType == 'empPost'){//人员岗位
	        	checkUrl = __ctxPath + '/sys/org/import/findallempPosis.f';
	        }else if(importType == 'all'){
	        	checkUrl = __ctxPath + '/sys/organization/import/findallTmpDatas.f';
	        }
	        FHD.ajax({
	            url: checkUrl,
	            callback: function (data) {
	            	me.showValidateGrid(data.datas,data.errorInfo);
	            }
	        });
    },
    //弹出查看窗口
    showValidateGrid: function (gridData,isError) {
        var me = this;
        var gridUrl;
        me.window = Ext.create('FHD.ux.Window', {
            title : "数据验证",
        	iconCls : 'icon-spellcheck',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible: true,
            buttons: [
			    { 
			    	name:'importDataBtn',
					text : '数据导入',
					iconCls: 'icon-ibm-action-export-to-excel',
			    	handler:function(){
			    		me.importData();
			    	}
			    },
			    { 
			    	name:'closeBtn',
			    	text: '关闭',
					iconCls: 'icon-ibm-close',
			    	handler:function(){
			    		me.window.close();
			    	}
			    }
			],
			buttonAlign: 'center'
        });
        var importType = me.down('[name="import"]').getValue();//导入类型
        if ('org' == importType) {//机构
            me.validateGrid = Ext.create('FHD.view.sys.organization.import.OrgImpValidateGridPanel');
        }else if('role' == importType){//角色
        	me.validateGrid = Ext.create('FHD.view.sys.organization.import.RoleImpValidateGridPanel');
        }else if('position' == importType){//岗位
        	me.validateGrid = Ext.create('FHD.view.sys.organization.import.PosiImpValidateGridPanel');
        }else if(importType == 'emp'){//人员
        	me.validateGrid = Ext.create('FHD.view.sys.organization.import.EmpGridPanel');
        }else if(importType == 'empOrg'){//人员机构
        	me.validateGrid = Ext.create('FHD.view.sys.organization.import.EmpOrgGridPanel');
        }else if(importType == 'empRole'){//人员角色
        	me.validateGrid = Ext.create('FHD.view.sys.organization.import.EmpRoleGridPanel');
        }else if(importType == 'empPost'){//人员岗位
        	me.validateGrid = Ext.create('FHD.view.sys.organization.import.EmpPostGridPanel');
        } else if('all' == importType){
        	me.validateGrid = Ext.create('Ext.tab.Panel',{
    			border:false
    		});
    		//机构tab
    		me.orgImpValidateGrid = Ext.create('FHD.view.sys.organization.import.OrgImpValidateGridPanel',{
    			title:'1.机构'
    		});
    		me.validateGrid.add(me.orgImpValidateGrid);
    		//角色tab
    		me.roleImpValidateGrid = Ext.create('FHD.view.sys.organization.import.RoleImpValidateGridPanel',{
    			title:'2.角色'
    		});
    		me.validateGrid.add(me.roleImpValidateGrid);
    		//岗位tab
    		me.posiImpValidateGrid = Ext.create('FHD.view.sys.organization.import.PosiImpValidateGridPanel',{
    			title:'3.岗位'
    		});
    		me.validateGrid.add(me.posiImpValidateGrid);
    		//人员TAB
    		me.empGridPanel = Ext.create('FHD.view.sys.organization.import.EmpGridPanel',{
    			title:'4.人员'
    		});
    		me.validateGrid.add(me.empGridPanel);
    		//人员机构TAB
    		me.empOrgGridPanel = Ext.create('FHD.view.sys.organization.import.EmpOrgGridPanel',{
    			title:'5.人员机构'
    		});
    		me.validateGrid.add(me.empOrgGridPanel);
    		//人员角色TAB
    		me.empRoleGridPanel = Ext.create('FHD.view.sys.organization.import.EmpRoleGridPanel',{
    			title:'6.人员角色'
    		});
    		me.validateGrid.add(me.empRoleGridPanel);
    		//人员岗位TAB
    		me.empPostGridPanel = Ext.create('FHD.view.sys.organization.import.EmpPostGridPanel',{
    			title:'7.人员岗位'
    		});
    		me.validateGrid.add(me.empPostGridPanel);
    		me.validateGrid.setActiveTab(0);
        }else {
        	return ;
        }
		if(!isError){//存在错误信息,导入按钮不可用
        	me.window.down('[name=importDataBtn]').setDisabled(true);
		}else{
			me.window.down('[name=importDataBtn]').enable(true);
		}
		if(gridData){//存在列表返回数据--上传；不存在时为查看
			if('all' == importType){//全部导入,需要调每个grid的reloadDate方法
				me.reloadAllData(gridData);
			}else{
				me.validateGrid.reloadData(gridData);
			}
		}
        me.window.add(me.validateGrid);
        me.window.show();
    },
	//数据导入
    importData: function(){
    	var me = this;
    	var importUrl;
    	var importType = me.down('[name="import"]').getValue();//导入类型
    	if('org' == importType){
    		importUrl = __ctxPath + '/sys/organization/import/importtosysorganizationfromtmp.f';
    	}else if('role' == importType){
    		importUrl = __ctxPath + '/sys/role/import/importtosysrolefromtmp.f';
    	}else if('position' == importType){
    		importUrl = __ctxPath + '/sys/position/import/importtosyspositionfromtmp.f';
    	}else if('all' == importType){
    		importUrl = __ctxPath + '/sys/organization/import/importallfromtmp.f';
    	}else if('emp' == importType){
    		importUrl = __ctxPath + '/sys/org/import/importEmpSave.f';
        }else if(importType == 'empOrg'){//人员机构
        	importUrl = __ctxPath + '/sys/org/import/importEmpOrgSave.f';
        }else if(importType == 'empRole'){//人员角色
        	importUrl = __ctxPath + '/sys/org/import/importEmpRoleSave.f';
        }else if(importType == 'empPost'){//人员岗位
        	importUrl = __ctxPath + '/sys/org/import/importEmpPostSave.f';
        }
    	Ext.Msg.confirm('提示', '确定导入数据吗？', function (g) {
            if (g == 'yes') {
            	if(me.window.body){
		    		me.window.body.mask("导入数据中...", "x-mask-loading");
	    		}
                FHD.ajax({
                    url: importUrl,
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
        })
    },
	// 初始化方法
	initComponent: function() {
		var me = this;
		//导入类型
		var importTypeStore = Ext.create('Ext.data.Store', { // 导入类型store
            fields: ['id', 'text'],
            data: [{
                    id: 'all',
                    text: '全部'
                },{
                    id: 'org',
                    text: '1.机构'
                }, {
                    id: 'role',
                    text: '2.角色'
                }, {
                    id: 'position',
                    text: '3.岗位'
                },{
                    id: 'emp',
                    text: '4.员工'
                },{
                    id: 'empOrg',
                    text: '5.员工机构关联'
                },{
                    id: 'empRole',
                    text: '6.员工角色关联'
                },{
                    id: 'empPost',
                    text: '7.员工岗位关联'
                }
            ],
            autoLoad: true
        });
		Ext.apply(me, {
			bodyPadding: 10,
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
                    value: '<a target="_blank" href="'+__ctxPath + '/tmp/import/download.jsp?path=/tmp/import/org/机构机构导入模板.xls"><img src="'+__ctxPath+'/images/icons/action_export_to_excel.gif">组织机构模板</a>'
                }, {
                    xtype: 'filefield',
                    name: 'fileField',
                    flex: 1,
                    width: 413,
                    fieldLabel: '2. 选择文件',
                    emptyText: '选择组织导入模板.....',
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
                }, {
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
	}
});