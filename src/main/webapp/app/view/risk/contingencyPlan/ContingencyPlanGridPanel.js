Ext.define('FHD.view.risk.contingencyPlan.ContingencyPlanGridPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.contingencyplangridpanel',
    
    queryUrl: __ctxPath + '/contingencyplan/findcontingencyplanlist.f',
    
    executionId : '',
    businessId: '',
    showbar : true,
    showStatus: false,
    type : 'all',//all 显示计划全部数据,emp 显示当前操作人的数据,dept 当前用户所在机构的数据
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        
        var cols = [{
            header: "riskid",
            dataIndex: 'riskid',
            invisible: true
        }, {
            header: "reportid",
            dataIndex: 'reportid',
            invisible: true
        }, {
            header: "风险名称",
            dataIndex: 'riskName',
            sortable: false,
            flex: 2,
            renderer: function (value, metaData, record, colIndex, store, view) {
            	metaData.tdAttr = 'data-qtip="' + value + '"';
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetail('" + record.get('riskid') + "')\" >" + value + "</a>";;
            }
        }, {
            header: "预案名称",
            dataIndex: 'reportName',
            sortable: false,
            flex: 2,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showReportDetailContainer('" + record.get('reportid') + "')\" >" + value + "</a>";
            }
        }, {
            header: "责任部门",
            dataIndex: 'occuredOrg',
            sortable: false,
            flex: .8,
            renderer: function (value, metaData, record, colIndex,
                store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
        }];
        if(me.showStatus){
        	cols.push({
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
	        });
        }
        var btns = [];
        if(me.showbar){
        	me.checked = true;
	        btns = [{
	//        		authority:'ROLE_ALL_RISK_ADD',
	                btype: 'add',
	                disabled: true,
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
        }else{
        	me.checked = false;
        }
        
        me.grid = Ext.create('FHD.ux.GridPanel',{
        	cols: cols,
            tbarItems: btns,
            checked : me.checked,
            border: false,
            pagable: false,
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
        me.grid.on('afterlayout', function () {
        	if(me.checked){
	            Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me.grid, [2]);
        	}else{
        		Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me.grid, [1]);
        	}
        });
        me.callParent(arguments);
    },
    reloadData: function (executionId,businessId) {
        var me = this;
        if(me.down("[name='addbutton']")){
	        me.down("[name='addbutton']").setDisabled(true);
        }
        if(me.down("[name='editbutton']")){
	        me.down("[name='editbutton']").setDisabled(true);
        }
        if(me.down("[name='delbutton']")){
	        me.down("[name='delbutton']").setDisabled(true);
        }
        if (executionId != null && executionId != '') {
            me.executionId = executionId;
        }
        if (businessId != null && businessId != '') {
            me.businessId = businessId;
        }
        me.grid.store.proxy.url = me.queryUrl;
        me.grid.store.proxy.extraParams.executionId = me.executionId;
        me.grid.store.proxy.extraParams.businessId = me.businessId;
        me.grid.store.proxy.extraParams.type = me.type;
        me.grid.store.load();
    },
    initParams: function (businessId) {
        var me = this;
        if(businessId != null ){
	        me.businessId = businessId;
        }
    },
    // 设置按钮可用状态
    setstatus: function (me) {
        if (me.grid.getSelectionModel().getSelection().length == 1) {
        	if(me.down("[name='addbutton']")){
	        	me.down("[name='addbutton']").setDisabled(false);
        	}
        	if(me.grid.getSelectionModel().getSelection()[0].get('reportName') != '' && me.grid.getSelectionModel().getSelection()[0].get('reportName') != undefined){
	        	if(me.down("[name='editbutton']")){
		        	me.down("[name='editbutton']").setDisabled(false);
	        	}
	        	if(me.down("[name='delbutton']")){
		        	me.down("[name='delbutton']").setDisabled(false);
	        	}
        	}
        }else{
        	if(me.down("[name='addbutton']")){
	        	me.down("[name='addbutton']").setDisabled(true);
        	}
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
        
        var manageReportEditForm = Ext.create('FHD.view.risk.managereport.ManageReportEditPanel', {
        	archiveStatus : 'examine',//审批中
            border: false,
            goback: function () {
            	window.close();
           		me.reloadData();
            }
        });
        if(isAdd){
        	//新增
        	manageReportEditForm.resetData();
	        manageReportEditForm.initRiskParams(selections[0].get('riskid'),me.businessId);
	        manageReportEditForm.resetTemplate();
        }else{
        	//修改
        	manageReportEditForm.reloadData(selections[0].get('reportid'));
        	manageReportEditForm.initRiskParams(selections[0].get('riskid'),me.businessId);
        }
        var window = Ext.create('FHD.ux.Window', {
            title: '预案管理',
            maximizable: true,
            modal: true,
            collapsible: true,
            autoScroll: true,
            items: manageReportEditForm
        }).show();
    },
    delFun: function () {
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
                            ids.push(item.get("reportid"));
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
            maximizable: true,
            collapsible: true,
            autoScroll: true
        }).show();
        manageReportDetalPanel.reloadData(reportId);
        window.add(manageReportDetalPanel);
    	
    	
    },
    
    showRiskEventDetail: function(id){
		var me = this;
            // 风险事件基本信息
        var riskEventDetailForm = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
            border: false,
            showbar: false
        });
        riskEventDetailForm.reloadData(id);
        var window = Ext.create('FHD.ux.Window', {
                title: '风险基本信息',
                maximizable: true,
                modal: true,
                collapsible: true,
                autoScroll: true,
                items: riskEventDetailForm
            }).show();
	},
    
    //切换显示页面
    reRightLayout: function (c) {
        var me = this;
        me.setActiveItem(c);
        me.doLayout();
    }
});