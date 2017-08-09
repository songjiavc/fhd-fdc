Ext.define('FHD.view.risk.contingencyPlan.planCollectApprove.PlanCollectTidyGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.plancollecttidygridpanel',
   	requires: [ ],
    
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
            title: '报告详情',
            maximizable: true,
            modal: true,
            collapsible: true,
            autoScroll: true,
            items: manageReportDetalPanel
        }).show();
        manageReportDetalPanel.reloadData(reportId);
    	
    	
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
    //编辑方法
    editFun: function (isAdd) {
        var me = this;
        var selections = me.getSelectionModel().getSelection();
        
        var manageReportEditForm = Ext.create('FHD.view.risk.managereport.ManageReportEditPanel', {
        	archiveStatus : 'examine',//审批中
            border: false,
            goback: function () {
            	window.close();
           		me.store.load();
            }
        });
        manageReportEditForm.initParams(me.type);
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
                	var selections = me.getSelectionModel().getSelection();
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
                            me.store.load();
                        }
                    });
                }
            }
        });
    },
    // 设置按钮可用状态
    setstatus: function (me) {
        if (me.getSelectionModel().getSelection().length == 1) {
        	if(me.getSelectionModel().getSelection()[0].get('reportName') != '' && me.getSelectionModel().getSelection()[0].get('reportName') != undefined){
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
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = [
			{
				dataIndex:'id',
				hidden:true
			},{
				dataIndex:'riskid',
				hidden:true
		    },{
				dataIndex:'reportid',
				hidden:true
		    }, {
	            header: "风险名称",
	            dataIndex: 'riskName',
	            sortable: false,
	            flex: 1,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	            	metaData.tdAttr = 'data-qtip="' + value + '"';
	                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetail('" + record.get('riskid') + "')\" >" + value + "</a>";;
	            }
	        }, {
	            header: "预案名称",
	            dataIndex: 'reportName',
	            sortable: false,
	            flex: 1,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showReportDetailContainer('" + record.get('reportid') + "')\" >" + value + "</a>";
	            }
	        }
        ];
        
    	Ext.apply(me,{
        	layout: 'fit',
        	url : __ctxPath + '/contingencyplan/findcontingencyplanbyrisk.f',
        	extraParams : {
    			assessPlanId : me.assessPlanId
        	},
        	cols:cols,
        	autoScroll:true,
        	border: false,
		    checked: true,
		    pagable : false,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : true,
		    tbarItems:[
               '<span style="font-size:12px;font-weight:bold;color: #15498b;margin-right:0">应急预案总数:</span>'+ 
				"<span id='risk-tidy-card-num" + me.id + "'>" + 0 + "</span>",
               {
   				text: '修改',
   				iconCls: 'icon-edit',
   				name: 'editbutton',
   				disabled : true,
   				handler:function(){
   					me.editFun(false);
   				}
   			},{
				text : '删除',
				iconCls : 'icon-del',
				name: 'delbutton',
				disabled : true,
				handler : function(){
					me.delFun();
				},
				scope : this
			}
			]
        });
    	
        me.callParent(arguments);
        
        me.on('afterlayout', function () {
            Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [5]);
        });
		me.on('selectionchange', function () {
            me.setstatus(me)
        });
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3]);
        	me.gridParams = me.store.proxy.extraParams;
        	var count = me.store.getCount();
	     	Ext.get('risk-tidy-card-num' + me.id).setHTML(count);
        });
    }
});