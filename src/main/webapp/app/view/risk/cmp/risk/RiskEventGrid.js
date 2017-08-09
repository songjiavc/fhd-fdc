/**
 * @author zhengjunxiang
 * 权限控制在风险库维护中
 */
Ext.define('FHD.view.risk.cmp.risk.RiskEventGrid', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.riskeventgrid',
    requires: [
        'FHD.view.risk.assess.utils.GridCells'
    ],

    currentId: '',
    navHeight: 22,
    addButtonStatus : true,
    //按不同的类型查询风险事件 risk，org,sm,process
    type: 'risk', 
    //风险新增查看显示的表单，storage风险库，relate风险关联,define风险定义
    formType : 'storage',
 
    /**
     * private 内部属性
     */
    queryUrl: '/cmp/risk/findEventById',

    //新增,修改接口
    showRiskAdd: function(p,parentId,name){},
    //查看接口
    showRiskDetail: function(p,parentId,name){},
    //新增修改返回接口
    goback: function(){},
    //改变新增按钮状态
    changeAddbuttonStatus: function(isDisable){
    	var me = this;
    	if(me.down("[name='addbutton']")){
	    	me.down("[name='addbutton']").setDisabled(isDisable);
    	}
    },

    initComponent: function () {
        var me = this;

        //1.列表
        var cols = [{
            dataIndex: 'id',
            invisible: true
        }, {
            dataIndex: 'belongRisk',
            invisible: true
        }, {
            dataIndex: 'parentId',
            invisible: true
        }, {
            dataIndex: 'parentId',
            invisible: true
        }, {
            header: '所属风险',
            dataIndex: 'parentName',
            sortable: false,
            width: 150,
            renderer: function (value, metaData, record, colIndex, store, view) {
                var detail = record.data['belongRisk'];
                return "<div data-qtitle='' data-qtip='" + detail + "'>" + value + "</div>";
            }
        }, {
            header: '名称',
            dataIndex: 'name',
            sortable: false,
            flex: 2,
            align: 'left',
            renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                var id = record.data['id'];
                //查看信息时，设置导航
                var parentId = record.data['parentId'];
                var name = record.data['name'];
                if (name.length > 33) {
                    name = name.substring(0, 30) + "...";
                }
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetailContainer('" + id + "','" + parentId + "','" + name + "')\" >" + value + "</a>";
            }
        }, {
            header: '责任部门',
            dataIndex: 'respDeptName',
            sortable: false,
            width: 100,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        }, {
            header: '相关部门',
            dataIndex: 'relaDeptName',
            sortable: false,
            width: 100,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        }, {
        	cls: 'grid-icon-column-header grid-statushead-column-header',
			header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>", 
            dataIndex: 'assessementStatus',
            sortable: true,
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
                    display = FHD.locale
                        .get("fhd.alarmplan.form.low");
                } else if (v == "icon-ibm-symbol-5-sm") {
                    color = "symbol_5_sm";
                    display = FHD.locale
                        .get("fhd.alarmplan.form.min");
                } else {
                    v = "icon-ibm-underconstruction-small";
                    display = "无";
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
            }
        }, {
            cls: 'grid-icon-column-header grid-trendhead-column-header',
			header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'etrend',
            sortable: true,
            width: 40,
            renderer: function (v) {
                var color = "";
                var display = "";
                if (v == "icon-ibm-icon-trend-rising-positive") {
                    color = "icon_trend_rising_positive";
                    display = FHD.locale
                        .get("fhd.kpi.kpi.prompt.positiv");
                } else if (v == "icon-ibm-icon-trend-neutral-null") {
                    color = "icon_trend_neutral_null";
                    display = FHD.locale
                        .get("fhd.kpi.kpi.prompt.flat");
                } else if (v == "icon-ibm-icon-trend-falling-negative") {
                    color = "icon_trend_falling_negative";
                    display = FHD.locale
                        .get("fhd.kpi.kpi.prompt.negative");
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'></div>";
            }
        }, {
            header: '状态',
            dataIndex: 'isNew',
            sortable: false,
            width: 60,
            renderer: function (value, metaData, record, colIndex, store, view) {
                var icon = 'icon-status-assess_mr';
                if (record.data['isNew']) {
                    icon = 'icon-status-assess_new';
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + icon + "'/>";
            }
        }];
        me.grid = Ext.create('FHD.ux.GridPanel', {
            border: false,
            columnLines: true,
            cols: cols,
            tbarItems: [{
                    btype: 'add',
                    name: 'addbutton',
                    authority:'ROLE_ALL_RISK_ADD',
                    handler: function () {
                        me.addFun();
                    }
                }, {
                    btype: 'edit',
                    name: 'editbutton',
                    authority:'ROLE_ALL_RISK_EDIT',
                    disabled: true,
                    handler: function () {
                        me.editFun();
                    }
                }, {
                    btype: 'delete',
                    name: 'delbutton',
                    authority:'ROLE_ALL_RISK_DELETE',
                    disabled: true,
                    handler: function () {
                        me.delFun();
                    }
                },
                {
                    btype: 'op',
                    authority:'ROLE_ALL_RISK_EXPORT',
                    tooltip: '导出数据到excel',
                    text: '导出',
                    iconCls: 'icon-ibm-action-export-to-excel',
                    handler: function () {
                        me.exportChart();
                    }
                }
            ]
        });

        Ext.apply(me, {
            items: [me.grid]
        });
        me.callParent(arguments);

        me.grid.on('afterlayout', function () {
            Ext.widget('gridCells').mergeCells(me.grid, [2]);
        });

        //记录发生改变时改变按钮可用状态
        me.grid.on('selectionchange', function () {
            me.setstatus(me.grid);
        });
    },

    // 设置按钮可用状态
    setstatus: function (me) {
    	if(me.down("[name='delbutton']")){
	        me.down("[name='delbutton']").setDisabled(me.getSelectionModel().getSelection().length === 0);
    	}
        if(me.down("[name='editbutton']")){
	        if (me.getSelectionModel().getSelection().length == 1) {
	            me.down("[name='editbutton']").setDisabled(false);
	        } else {
	            me.down("[name='editbutton']").setDisabled(true);
	        }
    	}
    },
    
    reloadData: function (id) {
        var me = this;
        if(me.down("[name='editbutton']")){
	        me.down("[name='editbutton']").setDisabled(true);
        }
        if(me.down("[name='delbutton']")){
	        me.down("[name='delbutton']").setDisabled(true);
        }
        if (id != null) {
            me.currentId = id;
        }
        me.grid.store.proxy.url = __ctxPath + me.queryUrl;
        me.grid.store.proxy.extraParams.id = me.currentId;
        me.grid.store.proxy.extraParams.type = me.type;
        //添加风险分库标识
        me.grid.store.proxy.extraParams.schm = me.schm;
        //手动点击第一页
        me.grid.dockedItems.items[2].moveFirst();
        //me.grid.store.load();
    },

    initParams: function (type) {
        var me = this;
        me.type = type;
    },

    /**
     * 添加
     */
    addFun: function () {
        var me = this;
        me.showRiskEventAddContainer(null, me.currentId, "风险事件添加");
    },

    /**
     * 编辑
     */
    editFun: function () {
        var me = this;
        var selection = me.grid.getSelectionModel().getSelection()[0];
        var id = selection.data.id;
        //编辑时，设置导航
        var parentId = selection.data.parentId;
        var name = selection.data.name;
        if (name.length > 33) {
            name = name.substring(0, 30) + "...";
        }
        //切换到风险事件添加card
        me.showRiskEventAddContainer(id, parentId, name);
        //编辑时展开树
    },

    showRiskEventAddContainer: function (id, parentId, name) {
        var me = this;
        if (!me.riskEventAddContainer) {
            //风险事件基本信息
        	var formArr = [];
        	if(me.formType == 'storage'){
//	            me.riskEventAddForm = Ext.create('FHD.view.risk.cmp.form.RiskStorageForm', {
//	            	type: 're',
//	            	border: false,
//	                title: '基本信息',
//	                showbar: true,
//	                goback: function(){
//	                	me.goback();
//	                }
//	            });
	            //2.表单，分步骤
    	        me.riskEventAddForm = Ext.create('FHD.view.risk.cmp.form.RiskStorageForm',{
    	        	navigatorTitle: '基本信息',
    	        	showbar:false,
    	        	type: 're',
    	        	border: false,
                    schm: me.schm,//添加风险分库标识
    	        	last:function(){
    	        		if(me.riskEventAddForm.respDeptName.getValue() == null){
    	        			FHD.notification("责任部门/人不能为空", FHD.locale.get('fhd.common.prompt'));
    	        			return false;
    	        		}
    	        		
    	        		//验证不通过，返回false
		            	var result = me.riskEventAddForm.save(function(data,editflag){
		            		//传递新保存的riskId到下一个面板
		            		me.riskKpiForm.riskId = data.id;
		            	});
		            	return result;	
    	        	}
    			});
    	        me.riskKpiForm = Ext.create("FHD.view.risk.cmp.form.RiskStorageKpiForm",{
    	        	navigatorTitle: '风险指标',
    	        	back:function(){
    	        		//返回后，变成编辑状态
    	        		me.riskEventAddForm.isEdit = true;//返回上一步变成编辑状态，否则重复添加
    	        		me.riskEventAddForm.riskId=me.riskKpiForm.riskId;
    	        	}
    	        });
    	        me.basicInfo = Ext.create('FHD.ux.layout.StepNavigator', {
    	        	title:'基本信息',
                    items: [me.riskEventAddForm,me.riskKpiForm],
                    undo: function () {
                    	//跳转到风险事件tab
                    	me.goback();
                    }
                });
                formArr.push(me.basicInfo);
        	}else if(me.formType == 'relate'){
        		me.riskEventAddForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
        			type: 're',
        			border: false,
	                title: '基本信息',
	                showbar: true,
                    schm: me.schm,//添加风险分库标识
	                goback: function(){
	                	me.goback();
	                	//刷新
	                	me.grid.store.load();
	                }
	            });
	            formArr.push(me.riskEventAddForm);
        	}else if(me.formType == 'define'){
        		me.riskEventAddForm = Ext.create('FHD.view.risk.cmp.form.RiskShortForm', {
        			type: 're',
        			border: false,
	                title: '基本信息',
	                showbar: true,
                    schm: me.schm,//添加风险分库标识
	                goback: function(){
	                	me.goback();
	                }
	            });
	            formArr.push(me.riskEventAddForm);
        	}
            /**
             * 宋佳添加风险应对
             */
            me.riskEventTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: formArr
            });
            me.riskEventAddContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                navHeight: me.navHeight,
                tabpanel: me.riskEventTabPanel,
                flex: 1
            });
        }

        if (id) {
        	if(me.formType == 'storage'){//me.formType == 'storage'
        		//切换到第一个step
        		me.basicInfo.navToFirst();
        		//将step页签变成可编辑状态
	    		me.basicInfo.setAddState(false);
	            me.riskEventAddForm.reloadData(id);
	            me.riskKpiForm.reloadData(id);
        	}else{
            	me.riskEventAddForm.reloadData(id);
        	}
        } else {
        	if(me.formType ==  'define'){
        		me.riskEventAddForm.resetData(me.currentId);
        		me.riskKpiForm.resetData();
        	}else{
        		if(me.formType == 'storage'){
					//切换到第一个step
        			me.basicInfo.navToFirst();
        			//将step页签变成可添加状态
	    			me.basicInfo.setAddState(true);
        			me.riskEventAddForm.resetData(me.type, me.currentId);
        			me.riskKpiForm.resetData();
        		}else{
        			me.riskEventAddForm.resetData(me.type, me.currentId);
        			me.riskKpiForm.resetData();
        		}
        	}
        }

        me.showRiskAdd(me.riskEventAddContainer, parentId, name);

    },

    /**
     * 创建风险事件查看容器
     */
    showRiskEventDetailContainer: function (id, parentId, name) {
        var me = this;
	
        if (!me.riskEventDetailContainer) {
            //风险事件基本信息
    		me.riskEventDetailForm = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
                title: '基本信息',
                border: false,
                autoHeight: true,
                showbar: true,
                goback: function(){
                	me.goback();
                }
            });
        	
			//图表分析
	        me.riskTrendLinePanel = Ext.create('FHD.view.risk.cmp.chart.RiskTrendLinePanel',{
	        	title : '图表分析',
	        	type : 'risk',
	        	border:false
	        });
	        
	        //风险图形分析的页签
	        me.riskGraphContainer =  Ext.create('Ext.container.Container',{
	        	layout:'fit',
	        	title:'图形分析',
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
	        
            //风险事件历史记录
            me.riskEventHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
                title: '历史记录',
                type: 'riskevent',
                border: false
            });

            me.riskEventTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.riskEventDetailForm,me.riskTrendLinePanel, me.riskGraphContainer, me.riskEventHistoryGrid]
            });
            me.riskEventDetailContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                navHeight: me.navHeight,
                tabpanel: me.riskEventTabPanel,
                flex: 1
            });
        }
        
        //加到if外面，保证function中变量变化
//        me.riskEventTabPanel.on({
//        	 tabchange: function (tabPanel, newCard, oldCard, eOpts) {
//	                if (newCard) {
//	                    newCard.reloadData(id);
//	                }
//	            }
//        });
        
        //全部刷新
        me.riskEventDetailForm.reloadData(id);
        me.riskTrendLinePanel.reloadData(id);
        me.riskGraphContainer.reloadData(id);
        me.riskEventHistoryGrid.reloadData(id);
        
        me.showRiskDetail(me.riskEventDetailContainer, parentId, name);
//        //刷新
//        var activeTab = me.riskEventTabPanel.getActiveTab();
//        if(activeTab.id == me.riskEventDetailForm.id){
//        	me.riskEventDetailForm.reloadData(id);
//        }else if(activeTab.id == me.riskTrendLinePanel.id){
//        	me.riskTrendLinePanel.reloadData(id);
//        }else if(activeTab.id == me.riskGraphContainer.id){
//        	me.riskGraphContainer.reloadData(id);
//        }else if(activeTab.id == me.riskEventHistoryGrid.id){
//        	me.riskEventHistoryGrid.reloadData(id);
//        }
    },

    /**
     * 删除
     */
    delFun: function () {
        var me = this;
        var deleteCheckUrl = '/risk/risk/findRiskCanBeRemoved.f';
        var delUrl = '/risk/risk/removeRiskById.f';
        var selections = me.grid.getSelectionModel().getSelection();
        if (selections.length > 0) {
        	//1.判断是否可以删除风险，如果有叶子节点和风险已经别打分，将不能进行删除
        	var canBeRemoved = true;
        	for(var i=0;i<selections.length;i++){
        		var id = selections[i].data.id;
            	FHD.ajax({
            		async:false,
    				url : __ctxPath + deleteCheckUrl + "?id=" + id,
    				callback : function(data) {
    					if(data.success) {//删除成功！
    						
    					}else{
    						if(data.type == 'hasChildren'){	//分类下有子风险
    							Ext.MessageBox.show({
    		            			title:'操作错误',
    		            			msg:'该风险下有下级风险，不允许删除!'
    		            		});
    							canBeRemoved = false;
    						}else if(data.type == 'hasRef'){	//hasRef 在其他模块被引用了
    							Ext.MessageBox.show({
    		            			title:'操作错误',
    		            			msg:'该风险已经被使用，不允许删除!'
    		            		});
    							canBeRemoved = false;
    						}else{
    							
    						}
    					}
    				}
    			});
        	}

        	//2.开始删除
        	if(canBeRemoved){
                Ext.MessageBox.show({
                    title: FHD.locale.get('fhd.common.delete'),
                    width: 260,
                    msg: FHD.locale.get('fhd.common.makeSureDelete'),
                    buttons: Ext.MessageBox.YESNO,
                    icon: Ext.MessageBox.QUESTION,
                    fn: function (btn) {
                        if (btn == 'yes') { //确认删除
                            var ids = [];
                            Ext.Array.each(selections,
                                function (item) {
                                    ids.push(item.get("id"));
                                });
                            FHD.ajax({ //ajax调用
                                url: __ctxPath + delUrl + "?ids=" + ids.join(','),
                                callback: function (data) {
                                    if (data) { //删除成功！
                                        FHD.notification(FHD.locale
                                            .get('fhd.common.operateSuccess'),
                                            FHD.locale.get('fhd.common.prompt'));
                                        me.reloadData();
                                    }
                                }
                            });
                        }
                    }
                });
        	}
        } else {
            FHD.notification('请选择一条指标.',FHD.locale.get('fhd.common.prompt'));
            return;
        }
    },
    /**
     * 启动和关闭
     */
    enablesFn: function (enable) {
        var me = this;

        var selections = me.getSelectionModel().getSelection();
        if (selections.length > 0) {
            var ids = [];
            Ext.Array.each(selections,
                function (item) {
                    ids.push(item.get("id"));
                });
            FHD.ajax({
                url: __ctxPath + '/risk/enableRisk',
                params: {
                    ids: ids.join(','),
                    isUsed: enable
                },
                callback: function (data) {
                    if (data) {
                        me.store.load();
                        if (enable == '0yn_y') {
                            Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '启用成功!');
                        } else {
                            Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '停用成功!');
                        }

                    }
                }
            });
        } else {
            FHD.notification('请选择一条指标.',FHD.locale.get('fhd.common.prompt'));
            return;
        }

    },
    exportChart: function () {
        var me = this;
        me.headerDatas = [];
        var items = me.grid.columns;
        Ext.each(items, function (item) {
            if (!item.hidden && item.dataIndex != '') {
                var value = {};
                value['dataIndex'] = item.dataIndex;
                value['text'] = item.text;
                if(item.dataIndex=='assessementStatus'){
                	value['text'] = "状态";
                }else if(item.dataIndex=='etrend'){
                	value['text'] = "趋势";
                }
                me.headerDatas.push(value);
            }
        });

        window.location.href = "cmp/risk/exportriskgrid.f?id=" + me.currentId + "&type=" + me.type + "&exportFileName=" + "" +
            "&sheetName=" + "" + "&headerData=" + Ext.encode(me.headerDatas) + "&style=" + "event";
    }

});