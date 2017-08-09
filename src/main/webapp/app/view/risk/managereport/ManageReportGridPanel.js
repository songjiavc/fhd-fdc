Ext.define('FHD.view.risk.managereport.ManageReportGridPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.managereportgridpanel',
    
    queryUrl: __ctxPath + '/managereport/findmanagereportlist.f',
    
    typeId: '',
    archiveStatus : 'saved',
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        
        var cols = [{
            header: "id",
            dataIndex: 'id',
            invisible: true
        }, {
            header: "名称",
            dataIndex: 'reportName',
            sortable: true,
            flex: 2,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showReportDetailContainer('" + record.get('id') + "')\" >" + value + "</a>";
            }
        }, {
            header: "创建时间",
            dataIndex: 'createDate',
            sortable: true,
            flex: .8,
            renderer: function (value, metaData, record, colIndex,
                store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
        }, {
            header: "责任部门",
            dataIndex: 'occuredorg',
            sortable: false,
            flex: .8,
            renderer: function (value, metaData, record, colIndex,
                store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
        }, {
            header: "责任人",
            dataIndex: 'employee',
            sortable: false,
            flex: .8,
            renderer: function (value, metaData, record, colIndex,
                store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
        }, {
            header: "状态",
            dataIndex: 'status',
            sortable: false,
            width : 55,
            renderer: function (v, metaData, record, colIndex,
                store, view) {
                var value='';
            	var show='';
                if (v == 'saved') {
                	value = "<font color=red>"+"待提交"+"</font>";
                	show = '待提交';
                } else if (v == 'examine') {
                	value = "<font color=green>"+"审批中"+"</font>";
                	show = '审批中';
                } else if (v == 'archived') {
                	value = "已归档";
                	show = '已归档';
                } else {
                	value = "" ;
                }
                metaData.tdAttr = 'data-qtip="' + show + '"';
                return '<div style="width: 32px; height: 19px;" >'+value+'</div>';
            }
        }];
        var btns = [{
//        		authority:'ROLE_ALL_RISK_ADD',
                btype: 'add',
                name: 'addbutton',
                handler: function () {
                    me.editFun(true);
                }
            },{
//            	authority:'ROLE_ALL_RISK_EDIT',
                btype: 'edit',
                disabled: true,
                name: 'editbutton',
                handler: function () {
                    me.editFun(false);
                }
            },{
//            	authority:'ROLE_ALL_RISK_EDIT',
                btype: 'delete',
                disabled: true,
                name: 'delbutton',
                handler: function () {
                    me.delFun(false);
                }
            }
        ];
        
        me.grid = Ext.create('FHD.ux.GridPanel',{
        	cols: cols,
            tbarItems: btns,
            border: false,
            checked: true,
            pagable: true,
            rowlines: true,
            storeAutoLoad : false,
            columnLines: true
        });
        
        Ext.apply(me, {
        	items : me.grid
        });
        me.grid.on('selectionchange', function () {
            me.setstatus(me)
        });
        me.callParent(arguments);
    },
    reloadData: function (typeId) {
        var me = this;
        me.reRightLayout(me.grid);
        if(me.down("[name='editbutton']")){
	        me.down("[name='editbutton']").setDisabled(true);
        }
        if(me.down("[name='delbutton']")){
	        me.down("[name='delbutton']").setDisabled(true);
        }
        if (typeId != null && typeId != '') {
            me.typeId = typeId;
        }
        me.grid.store.proxy.url = me.queryUrl;
        me.grid.store.proxy.extraParams.typeId = me.typeId;
        me.grid.store.load();
    },
    initParams: function (typeId) {
        var me = this;
        if(typeId != null ){
	        me.typeId = typeId;
        }
    },
    // 设置按钮可用状态
    setstatus: function (me) {
        if (me.grid.getSelectionModel().getSelection().length == 1) {
        	if(me.grid.getSelectionModel().getSelection()[0].get('status') == 'archived' || me.grid.getSelectionModel().getSelection()[0].get('status') == 'saved'){
	        	if(me.down("[name='editbutton']")){
		        	me.down("[name='editbutton']").setDisabled(false);
	        	}
	        	if(me.down("[name='delbutton']")){
		        	me.down("[name='delbutton']").setDisabled(false);
	        	}
        	}
        }else{
        	if(me.down("[name='editbutton']")){
	            me.down("[name='editbutton']").setDisabled(true);
        	}
        	if(me.down("[name='delbutton']")){
	        	me.down("[name='delbutton']").setDisabled(true);
        	}
        	
        }
    },
    //编辑方法
    editFun: function (isAdd) {
        var me = this;
        var selections = me.grid.getSelectionModel().getSelection();
        if (!me.manageReportEditForm) {
            // 风险事件基本信息
            me.manageReportEditForm = Ext.create('FHD.view.risk.managereport.ManageReportEditPanel', {
            	archiveStatus : me.archiveStatus,
                border: false,
                goback: function () {
               		me.reloadData();
                }
            });
            me.add(me.manageReportEditForm);
        }
        me.manageReportEditForm.initParams(me.typeId);
        me.reRightLayout(me.manageReportEditForm);
        if(isAdd){
        	//新增
        	me.manageReportEditForm.resetData();
        	me.manageReportEditForm.resetTemplate();
        }else{
        	//修改
        	me.manageReportEditForm.reloadData(selections[0].get('id'));
        }
        
    },
    delFun: function (hisid) {
        var me = this;
        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                	var selections = me.grid.getSelectionModel().getSelection();
                	var ids = [];
                    Ext.Array.each(selections,
                        function (item) {
                            ids.push(item.get("id"));
                        });
                    FHD.ajax({
                        url: __ctxPath + '/managereport/deletemanagereport.f',
                        params: {
                        	ids : ids.join(',')
                        },
                        callback: function (data) {
                            FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                            me.reloadData();
                        }
                    });
                }
            }
        });
    },
    showReportDetailContainer: function(reportId){
    	var me = this;
    	var manageReportDetalPanel = Ext.create('FHD.view.risk.managereport.ManageReportDetalPanel', {
            showRiskDetail: function (p) {
                me.showRiskDetail(p);
            },
            goback: function () {
                me.goback();
            }
        });
        var window = Ext.create('FHD.ux.Window', {
            title: '应急预案详情',
            border : false,
            maximizable: true,
            modal: true,
            collapsible: true
        }).show();
        manageReportDetalPanel.reloadData(reportId);
        window.add(manageReportDetalPanel);
    	
    	
    },
    
    //切换显示页面
    reRightLayout: function (c) {
        var me = this;
        me.setActiveItem(c);
        me.doLayout();
    }
});