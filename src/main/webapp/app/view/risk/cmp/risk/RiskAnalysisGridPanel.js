/**
 *
 * 风险分析组件 type : 分析类型，默认为战略目标 sm 战略目标，sc 记分卡
 * 被使用在考核指标发布，指标监控下
 */
Ext.define('FHD.view.risk.cmp.risk.RiskAnalysisGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskanalysisgridpanel',
    queryUrl: 'cmp/risk/findriskanalysis.f',
    currentId: '',
    operateType : 'true',
    type: 'sm',
    navHeight: 22,
    navData : null,
    
    //新增接口
    showRiskAdd: function(p,name){},
    //修改接口
    showRiskDetail: function(p,name){},
    //新增修改返回接口
    goback: function(){},
    //导航变更方法
    reLayoutNavigationBar: function(data){},
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        var cols = [{
            header: "id",
            dataIndex: 'id',
            invisible: true
        }, {
            header: "目标指标",
            exportText : '目标指标',
            dataIndex: 'aimTarget',
            sortable: false,
            flex: .6,
            renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return value;
            }
        }, {
            header: "风险名称",
            exportText : '目标风险',
            dataIndex: 'riskname',
            sortable: false,
            flex: 2,
            renderer: function (value, metaData, record, colIndex,
                store, view) {
            	var id = record.data['id'];
            	id = id.split('_')[1];
                metaData.tdAttr = 'data-qtip="' + value + '"';
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetail('" + id + "')\" >" + value + "</a>";
            }
        }, {
            header: '责任部门',
            exportText : '责任部门',
            dataIndex: 'respDeptName',
            sortable: false,
            width: 100,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        }, {
            header: "状态",
            exportText : '状态',
            dataIndex: 'archiveStatus',
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
        }, {
            cls: 'grid-icon-column-header grid-statushead-column-header',
            exportText : '风险水平',
            dataIndex: 'assessementStatus',
            sortable: false,
            width: 40,
            renderer: function (v) {
                var color = "";
                var display = "";
                if (v == "icon-ibm-symbol-4-sm") {
                    color = "symbol_4_sm";
                    display = FHD.locale
                        .get("fhd.alarmplan.form.hight");
                } else if (v == "icon-ibm-symbol-6-sm") {
                    color = "symbol_6_sm";
                    display = FHD.locale.get("fhd.alarmplan.form.low");
                } else if (v == "icon-ibm-symbol-5-sm") {
                    color = "symbol_5_sm";
                    display = FHD.locale.get("fhd.alarmplan.form.min");
                } else if (v == "icon-ibm-symbol-safe-sm") {
                    display = "安全";
                } else {
                    v = "icon-ibm-underconstruction-small";
                    display = "无";
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
            }
        }];
        
        if(me.operateType){
        	cols.push({
	            header: FHD.locale.get('fhd.common.operate'),
            	exportText : '操作',
	            notExport: true,
	            dataIndex: 'operate',
	            sortable: false,
	            align:'center',
	            width: 65,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	            	var id = record.data['id'];
	                id = id.split('_')[1];
	                if(id != '' && id != null){
		                var name = record.data['riskname'];
		                if (name != null && name != undefined && name.length > 23) {
		                    name = name.substring(0, 20) + "...";
		                }
		                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetailContainer('" + id + "','" + name + "')\" class='icon-view' data-qtip='查看'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>";
	                }else{
	                	return '';
	                }
	            }
	        });
        }
        
        var btns = [{
        		authority:'ROLE_ALL_RISK_ADD',
                btype: 'add',
                disabled: true,
                name: 'addbutton',
                handler: function () {
                    me.editFun(true);
                }
            }, {
            	authority:'ROLE_ALL_RISK_EDIT',
                btype: 'edit',
                disabled: true,
                name: 'editbutton',
                handler: function () {
                    me.editFun(false);
                }
            }, {
            	authority:'ROLE_ALL_KPI_RELARISK',
            	btype: 'op',
                text: '关联风险',
                disabled: true,
                name: 'relatebutton',
                iconCls: 'icon-plugin-add',
                handler: function () {
                    me.risksSelect();
                }
            }, {
            	authority:'ROLE_ALL_RISK_EXPORT',
            	btype: 'op',
                text: '导出',
                iconCls: 'icon-ibm-action-export-to-excel',
                handler: function () {
                    me.exportChart();
                }
            }
            //				, {
            //					text : '回归分析',
            //					iconCls : 'icon-linechart',
            //					handler : function() {
            //						me.regressionFun();
            //					}
            //				}, {
            //					text : '风险承受度',
            //					iconCls : 'icon-ibm-icon-metrics-16',
            //					handler : function() {
            //						me.toleranceFun();
            //					}
            //				}
        ];
        Ext.apply(me, {
            cols: cols,
            tbarItems: btns,
            border: false,
            checked: true,
            pagable: true,
            rowlines: true,
            columnLines: true
        });
        me.on('selectionchange', function () {
            me.setstatus(me)
        });
        me.on('afterlayout', function () {
            Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [2]);
        });
        me.callParent(arguments);
        me.store.on('beforeload', function () {
            if (me.beforestart == me.store.currentPage) {
                me.beforestart = 0;
                me.store.proxy.extraParams.start = 0;
                me.store.loadPage(1);
            } else {
                me.beforestart = me.store.currentPage;
                me.store.proxy.extraParams.start = (me.store.currentPage - 1) * me.store.pageSize;
            }
        });
    },
    reloadData: function (id) {
        var me = this;
        if(me.down("[name='editbutton']")){
	        me.down("[name='editbutton']").setDisabled(true);
        }
        if(me.down("[name='addbutton']")){
	        me.down("[name='addbutton']").setDisabled(true);
        }
        if(me.down("[name='relatebutton']")){
	        me.down("[name='relatebutton']").setDisabled(true);
        }
        if (id != null && id != '' && id != undefined) {
            me.currentId = id;
        }
        me.store.proxy.url = me.queryUrl;
        me.store.proxy.extraParams.id = me.currentId;
        me.store.proxy.extraParams.type = me.type;
        me.store.load();
    },
    initParam: function (type) {
        var me = this;
        if(type != null ){
	        me.type = type;
        }
    },
    //编辑方法
    editFun: function (isAdd) {
        var me = this;
        var selections = me.getSelectionModel().getSelection();
        if (isAdd) {
            me.showRiskEventAddContainer(null, '添加风险事件');
        } else {
            var name = selections[0].get('riskname');
            if (name != null && name != undefined && name.length > 28) {
                name = name.substring(0, 25) + "...";
            }
            var riskid = selections[0].get('id');
            riskid = riskid.split('_')[1]; // 风险
            me.showRiskEventAddContainer(riskid, name);
        }
    },
    // 回归分析
    regressionFun: function () {
        var me = this;
        me.riskRegressionAnalyMainPanel = Ext
            .widget('riskRegressionAnalyMainPanel');
        me.preWin = Ext.create('FHD.ux.Window', {
            title: '回归分析',
            height: 600,
            width: 940,
            layout: 'fit',
            items: [me.riskRegressionAnalyMainPanel]
            /*
             * fbar: [ { xtype: 'button', text: '确定',
             * handler:function(){me.preWin.hide();}} ]
             */
        }).show();
    },
    // 风险承受度
    toleranceFun: function () {
        var me = this;
        me.kpiRiskAnalyse = Ext.create('FHD.view.risk.analyse.KpiRiskAnalyse');
        me.toleWin = Ext.create('FHD.ux.Window', {
            title: '回归分析',
            height: 600,
            width: 900,
            layout: 'fit',
            items: [me.kpiRiskAnalyse]
        }).show();
    },
    // 风险关联
    risksSelect: function () {
        var me = this;
        var selections = me.getSelectionModel().getSelection();
        if (selections.length == 1) {
            var kpiid = selections[0].get('id');
            kpiid = kpiid.split('_')[0]; // 影响指标
            FHD.ajax({
                url: 'cmp/risk/findinflukpi.f',
                params: {
                    kpiid: kpiid,
                    id: me.currentId,
                    type: me.type
                },
                callback: function (data) {
                    Ext.define('Risk', {
                        extend: 'Ext.data.Model',
                        fields: ['id', 'code', 'name']
                    });
                    me.win = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow', { // 风险选择组件
                        multiSelect: true,
                        modal: true,
                        onSubmit: function (win) {
                            var selectedgridstores = win.selectedgrid.store;
                            var riskids = [];
                            Ext.each(selectedgridstores.data.items, function (item) {
                                riskids.push(item.data.id);
                            });
                            FHD.ajax({
                                url: 'cmp/risk/savekpirelarisk.f',
                                params: {
                                    riskids: riskids.join(','),
                                    kpiid: kpiid,
                                    id: me.currentId
                                },
                                callback: function (data) {
                                    FHD.notification(
                                            FHD.locale
                                            .get('fhd.common.operateSuccess'),
                                            FHD.locale
                                            .get('fhd.common.prompt'));
                                    me.store.load();
                                }
                            });
                        }
                    }).show();
                    // 给window赋值
                    for (var i = 0; i < data.datas.length; i++) {
                        var riskent = new Risk({
                            id: data.datas[i].id,
                            code: data.datas[i].code,
                            name: data.datas[i].name
                        });
                        me.win.setValue(riskent);
                    }
                }
            });
        } else {
            FHD.notification('请选择一条目标指标.',FHD.locale.get('fhd.common.prompt'));
            return;
        }
    },
    // 设置按钮可用状态
    setstatus: function (me) {
        if (me.getSelectionModel().getSelection().length == 1) {
        	if(me.down("[name='editbutton']")){
	            if (me.getSelectionModel().getSelection()[0].data.riskname == null || me.getSelectionModel().getSelection()[0].data.riskname == '' || me.getSelectionModel().getSelection()[0].data.archiveStatus == 'examine') {
	                me.down("[name='editbutton']").setDisabled(true);
	            } else {
	                me.down("[name='editbutton']").setDisabled(false);
	            }
        	}
        	if(me.down("[name='addbutton']")){
	            me.down("[name='addbutton']").setDisabled(false);
        	}
        	if(me.down("[name='relatebutton']")){
	            me.down("[name='relatebutton']").setDisabled(false);
        	}
        } else {
        	if(me.down("[name='editbutton']")){
	            me.down("[name='editbutton']").setDisabled(true);
        	}
        	if(me.down("[name='addbutton']")){
	            me.down("[name='addbutton']").setDisabled(true);
        	}
        	if(me.down("[name='relatebutton']")){
	            me.down("[name='relatebutton']").setDisabled(true);
        	}
        }
    },
    //风险事件编辑
    showRiskEventAddContainer: function (id, name) {
        var me = this;
        if (!me.riskEventAddContainer) {
            // 风险事件基本信息
            me.riskEventAddForm = Ext.create('FHD.view.risk.cmp.form.RiskStorageForm',{
	        	navigatorTitle: '基本信息',
	        	showbar:false,
	        	archiveStatus:'saved',
	        	type: 're',
	        	border: false,
	        	last:function(){
	        		//验证不通过，返回false
	            	var result = me.riskEventAddForm.save(function(data,editflag){
	            		//传递新保存的riskId到下一个面板
	            		me.solutioneditpanel.initParam({
				            type: '0',//自动任务
				            selectId: data.id
				        });
				        me.solutioneditpanel.reloadData();
	            	});
	            	return result;	
	        	}
			});
			me.solutioneditpanel = Ext.create("FHD.view.response.new.SolutionEditPanel",{
	        	navigatorTitle: '风险应对',
	        	showType : 'window'
	        });
	        me.basicInfo = Ext.create('FHD.ux.layout.StepNavigator', {
	        	title:'基本信息',
                items: [me.riskEventAddForm,me.solutioneditpanel],
                undo: function () {
                	//跳转到风险事件tab
                	me.goback();
                }
            });
            
            me.riskEventTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
            	border: false,
                items: [me.basicInfo]
            });
            me.riskEventAddContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                navHeight: me.navHeight,
                tabpanel: me.riskEventTabPanel,
                flex: 1
            });
        }
        if (id) {
        	me.basicInfo.navToFirst();
        		//将step页签变成可编辑状态
    		me.basicInfo.setAddState(false);
            me.riskEventAddForm.reloadData(id);
            me.solutioneditpanel.initParam({
	            type: '0',
	            selectId: id
	        });
            me.solutioneditpanel.reloadData();
        } else {
        	//切换到第一个step
			me.basicInfo.navToFirst();
			//将step页签变成可添加状态
			me.basicInfo.setAddState(true);
            me.riskEventAddForm.resetData('sm', '_'+me.getSelectionModel().getSelection()[0].data.id.split('_')[0]);
        }
        me.showRiskAdd(me.riskEventAddContainer, name);
        if(me.navData){
    		var data = [];
    		Ext.Array.push(data,me.navData)
			data.push({
	               type: 'iriskevent',
	               id: 'iriskevent',
	               containerId: me.id,
	               name: name
	        });
    		me.reLayoutNavigationBar(data);
		}
    },
	showRiskEventDetail: function(id){
		var me = this;
            // 风险事件基本信息
        var riskEventDetailForm = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
            border: false,
            showbar: false
        });
        var window = Ext.create('FHD.ux.Window', {
                title: '风险基本信息',
                maximizable: true,
                modal: true,
                width: 800,
                height: 500,
                collapsible: true,
                autoScroll: true
            }).show();
        riskEventDetailForm.reloadData(id);
       	window.add(riskEventDetailForm);
	},
    //风险事件查看
    showRiskEventDetailContainer: function (id, name) {
        var me = this;
        if(me.navData){
    		var data = [];
    		Ext.Array.push(data,me.navData)
			data.push({
	               type: 'riskanalyse',
	               id: 'riskanalyse',
	               containerId: me.id,
	               name: name
	        });
    		me.reLayoutNavigationBar(data);
		}
        if (!me.riskEventDetailContainer) {
            me.historyEventGrid = Ext.create('FHD.view.risk.hisevent.HistoryEventGridPanel', {
            	navData : data,
        		title: '历史事件',
        		reLayoutNavigationBar : function(date){
        			me.reLayoutNavigationBar(date);
        		},
                border: false
            });
        	
            me.solutioneditpanel = Ext.create('FHD.view.response.new.SolutionEditPanel', {
            	navData : data,
            	businessType : 'analysis',
            	title: '风险应对',
            	reLayoutNavigationBar : function(date){
        			me.reLayoutNavigationBar(date);
        		},
                type: 'risk'
            });
            
            me.riskEventHistoryGrid = Ext.create('FHD.view.risk.cmp.container.RiskHistoryContainer', {
            	title: '历史数据',
                type: 'riskevent',
                border: false
            });
	        
	        me.riskGraphContainer =  Ext.create('Ext.container.Container',{
	        	title: '图形分析',
	        	layout:'fit',
	        	reloadData:function(riskId){//alert(riskId);
	        		if(!me.riskGraph){
	        			//2.表单
	        	        me.riskGraph = Ext.create('FHD.view.comm.graph.GraphRelaRiskPanel',{
	        			});
	            		this.add(me.riskGraph);
	            		this.doLayout();
	        		}
	        		
	    			//根据左侧选中节点，初始化数据
	        		me.riskGraph.initParam({
		                 riskId:riskId
		        	});
	        		me.riskGraph.reloadData();
	        	}	        	
	        });
            
            me.riskEventTabPanel = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', {
                items: [me.solutioneditpanel,me.historyEventGrid,me.riskGraphContainer,me.riskEventHistoryGrid,me.riskEventDetailForm],
                navData: data,
                listeners : {
                	tabchange : function (tabPanel, newCard, oldCard, eOpts) {
                		newCard.reloadData();
                		if(me.navData){
	                		me.reLayoutNavigationBar(me.riskEventTabPanel.navData);
                		}
                	}
                }
            });
            me.riskEventDetailContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                navHeight: me.navHeight,
                tabpanel: me.riskEventTabPanel,
                flex: 1
            });
        }
        me.historyEventGrid.navData = data;
        me.solutioneditpanel.navData = data;
        me.riskGraphContainer.navData = data;
        me.riskEventTabPanel.navData = data;
        
        me.historyEventGrid.reloadData(id);
        me.solutioneditpanel.initParam({
            type: '0',//自动任务
            selectId: id
        });
        me.solutioneditpanel.reloadData();
        me.riskEventHistoryGrid.reloadData(id);
        me.riskGraphContainer.reloadData(id);
        me.showRiskDetail(me.riskEventDetailContainer, name);
    },
    //导出excel方法
    exportChart: function () {
        var me = this;
        me.headerDatas = [];
        var items = me.columns;
        Ext.each(items, function (item) {
            if (!item.hidden && item.dataIndex != '' && !item.notExport) {
                var value = {};
                value['dataIndex'] = item.dataIndex;
                value['text'] = item.exportText;
                me.headerDatas.push(value);
            }
        });
        window.location.href = "cmp/risk/exportriskgrid.f?id=" + me.currentId + "&type=" + me.type + "&exportFileName=" + "" +
            "&sheetName=" + "" + "&headerData=" + Ext.encode(me.headerDatas) + "&style=" + "analysis";
    },
    go : function(){
    	var me = this;
    	me.riskEventTabPanel.getLayout().getActiveItem().go();
    },
	setNavData:function(data) {
		var me = this;
		me.navData = data;
	}
});