/*
 * 十大风险分类列表
 * zhengjunxiang
 * */

Ext.define('FHD.view.report.risk.Top10RiskOrgGrid', {
    extend: 'FHD.ux.GridPanel',
	alias: 'widget.top10riskorggrid',
	
	/**
	 * 常量区
	 */
	listUrl:'/riskhistoryversion/findOrgTop10Risk.f',  //查询url
	
	/**
	 * 变量
	 */
	companyId:undefined,	//公司id，在集团的十大风险模块，会传入值
	
	navHeight: 22,
	navData : null,
	//查看接口
    showRiskDetail: function(p,parentId,name){},
    //导航方法
    reLayoutNavigationBar:function(data){},
    
    //新增修改返回接口
    goback: function(){},
    
	initParams:function(companyId){
		var me = this;
		me.companyId = companyId;
	},
	
	initComponent: function () {
        var me = this;
        
        var cols = [{
            dataIndex: 'id',
            invisible: true
        }, {
            dataIndex: 'riskId',
            invisible: true
        }, {
            dataIndex: 'parentId',
            invisible: true
        }, {
            dataIndex: 'adjustHistoryId',
            invisible: true
        }, {
            dataIndex: 'templateId',
            invisible: true
        }, {
            dataIndex: 'num',
            header: '排名',
            sortable:false,
            width:40
        }, {
            dataIndex: 'name',
            header: '风险名称',
            sortable:false,
            flex:2,
            renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                var id = record.data['riskId'];
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetail('" + id + "')\" >" + value + "</a>";
            }
        }, {
            dataIndex: 'parentName',
            sortable:false,
            header:'所属风险'
        }, {
            dataIndex: 'dutyDepartment',
            sortable:false,
            flex:1,
            header:'责任部门'
        }, {
            dataIndex: 'relativeDepartment',
            sortable:false,
            flex:1,
            header:'相关部门'
        }, {
            dataIndex: 'probability',
            sortable:false,
            header: '发生可能性',
            renderer:function(value){
            	if(value!=''){
            		//可以四舍五入
            		var num = Number(value);
            		return num.toFixed(2);
            	}
            }
        }, {
            dataIndex: 'influenceDegree',
            sortable:false,
            header: '影响程度',
            renderer:function(value){
            	if(value!=''){
            		//可以四舍五入
            		var num = Number(value);
            		return num.toFixed(2);
            	}
            }
        }, {
            dataIndex: 'riskScore',
            header: '风险值',
            sortable:false,
            align : 'center',
            renderer:function(value){
            	if(value!=''){
            		//可以四舍五入
            		var num = Number(value);
            		return num.toFixed(2);
            	}
            }
        }, {
            cls: 'grid-icon-column-header grid-statushead-column-header',
			header: "<span data-qtitle='' data-qtip='状态'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'riskStatus',
            sortable: true,
            menuDisabled:true,
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
            header: FHD.locale.get('fhd.common.operate'),
            dataIndex: 'operate',
            sortable: false,
            align:'center',
            width: 65,
            renderer: function (value, metaData, record, colIndex, store, view) {
            	var id = record.data['riskId'];
                //查看信息时，设置导航
                var parentId = record.data['parentId'];;
                var name = record.data['name'];
                if (name.length > 23) {
                    name = name.substring(0, 20) + "...";
                }
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetailWindow('" + id + "','" + parentId + "','" + name + "')\" class='icon-view' data-qtip='查看'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>";
            }
        }];
        
        /** 
         *  风险级别列表
         *  {'id':'0', 'name':'全部'},
			{'id':'2', 'name':'二级风险'},
	        {'id':'3', 'name':'三级风险'},
	        {'id':'-1', 'name':'风险事件'}
         */
        me.levelStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			proxy: {
		         type: 'ajax',
		         url: __ctxPath + '/riskhistoryversion/findAllRiskCatalog.f',
		         reader: {
		             type: 'json',
		             root: 'datas'
		         }
		     }
		});
		me.levelCombo = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '风险级别',
		    labelWidth:63,
		    labelAlign:'right',
			store :me.levelStore,
			valueField : 'id',
			name:'level',
			value : '二级风险',
			displayField : 'name',
			listeners:{
				change:function(c, newValue, oldValue, eOpts){
					me.store.proxy.extraParams.level = newValue;
			        me.store.load();
				}
			}
		});
		
		Ext.apply(me, {
			border : false,
        	checked:false,
        	storeAutoLoad : false,
        	searchable:true,
        	height:800,
            border: false,
            url: __ctxPath + me.listUrl,
            cols: cols,
            tbarItems:[me.levelCombo,{
                    btype: 'op',
                    tooltip: '导出数据到excel',
                    text: '导出',
                    iconCls: 'icon-ibm-action-export-to-excel',
                    handler: function () {
                        me.exportChart();
                    }
                }]
        });
    		
    	me.callParent(arguments);
    	
    	//控制按钮状态
    	me.on("itemclick",function(){
    		var btnEdit = Ext.ComponentQuery.query('button[name=btnEdit]',me)[0];
    		if(btnEdit){
    			btnEdit.setDisabled(false);
    		}
    	});
    	
	},
	
	/**
	 * 查询，按照风险版本，级别，组织条件进行查询
	 */
	reloadData : function(version,level,orgIds){
		var me = this;
		me.store.proxy.url = __ctxPath + me.listUrl;
		if(me.companyId){
			me.store.proxy.extraParams.companyId = me.companyId;
		}
		me.store.proxy.extraParams.version = version;//"'"+version+"'";
		me.store.proxy.extraParams.level = level;
		me.store.proxy.extraParams.orgIds = orgIds;
        me.store.load();
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
                width: 800,
                height: 500,
                collapsible: true,
                autoScroll: true,
                items: riskEventDetailForm
            }).show();
	},
	
	showRiskEventDetailWindow:function(id,parentId,name){
		var me = this;
		if(me.navData){
    		var data = [];
    		Ext.Array.push(data,me.navData)
			data.push({
	               type: 'riskorggrid',
	               id: 'riskorggrid',
	               containerId: me.id,
	               name: name
	        });
    		me.reLayoutNavigationBar(data);
		}

		me.detailId  = id;
	        
        //风险监控指标
        me.riskRelaKpiGridContainer = Ext.create('Ext.container.Container', {
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_REASONQUERY_RISK_RISKLIST',
            layout: 'fit',
            title: '风险监控',
            onClick: function () {
            	if(!me.riskRelaKpiGrid){
            		me.riskRelaKpiGridContainer.remove(me.riskRelaKpiGrid,true);
            	}
                me.riskRelaKpiGrid = Ext.create('FHD.view.risk.cmp.RiskRelaKpiGrid', {
                    face: me,
                    operateType : false,
                    navData: data,
                    border: false,
                    navHeight: me.navHeight,
                    showKpiAdd: function (p, parentId, name) {
                    	me.kpiContainer = Ext.create("Ext.container.Container",{
                    		layout: {
				                align: 'stretch',
				                type: 'vbox'
				            }
                		});
                    	me.navKpiObj = {
				            xtype: 'box',
				            height: 18,
				            style: 'border-left: 1px  #99bce8 solid;',
				            html: '<div id="' + me.id + 'DIV" class="navigation"></div>'
				        };
				        me.kpiContainer.add(me.navKpiObj);
				        me.kpiContainer.add(p);

                    	me.reRightLayout(me.kpiContainer);
                    },
                    undo: function() {
                    	me.reRightLayout(me.tabContainer);
                    	me.riskRelaKpiGrid.reloadData();
                    	if(me.navData){
				    		me.reLayoutNavigationBar(me.riskRelaKpiGrid.navData);
						}
                    },
                    showKpiDetail: function (p, parentId, name) {
                    	me.reRightLayout(p);
                    },
	                reLayoutNavigationBar: function(data){
	                	me.reLayoutNavigationBar(data);
	            	},
                    reRightLayout: function(p){
                    	me.reRightLayout(p);
                    }
                });
                this.add(me.riskRelaKpiGrid);
                this.doLayout();
                //刷新
                me.riskRelaKpiGrid.reloadData(me.detailId);
            }
        });

        //风险图形分析的页签
        me.riskGraphContainer =  Ext.create('Ext.container.Container',{
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_REASONQUERY_RISK_CHART',
        	layout:'fit',
        	title:'图形分析',
        	onClick:function(){
        		if(me.riskGraph){
        			me.riskGraphContainer.remove(me.riskGraph,true);
        		}
        			//2.表单
        	        me.riskGraph = Ext.create('FHD.view.comm.graph.GraphRelaRiskPanel',{
        			});
            		this.add(me.riskGraph);
            		this.doLayout();
    			//刷新
        		me.riskGraph.initParam({
	                 riskId:me.detailId
	        	});
    			me.riskGraph.reloadData();
    		}
        	
        });
        
        //风险事件列表页
        me.riskEventGridContainer = Ext.create('Ext.container.Container', {
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_REASONQUERY_RISK_RISKLIST',
            layout: 'fit',
            title: '风险列表',
            onClick: function () {
                if (me.riskEventGrid) {
                	me.riskEventGridContainer.remove(me.riskEventGrid,true);
                }
                    me.riskEventGrid = Ext.create('FHD.view.risk.cmp.risk.IRiskEventGrid', {
                        face: me,
                        operateType: false,
                        navData: data,
                        border: false,
                        navHeight: me.navHeight,
                        formType: 'relate',//风险的添加是相关添加
                        showRiskAdd: function (p, parentId, name) {
                        	me.reRightLayout(p);
                        },
                        showRiskDetail: function (p, parentId, name) {
                        	me.reRightLayout(p);
                        },
                        goback: function () {
                        	me.reRightLayout(me.tabContainer);
                        	me.riskEventGrid.reloadData();
                        	if(me.navData){
					    		me.reLayoutNavigationBar(me.riskEventGrid.navData);
							}
                        },
		                reLayoutNavigationBar: function(data){
		                	me.reLayoutNavigationBar(data);
		            	}
                    });
                    this.add(me.riskEventGrid);
                    this.doLayout();
                //刷新
                me.riskEventGrid.initParams('risk');
                me.riskEventGrid.reloadData(me.detailId);
            }
        });

        //应对方案
        me.orgriskResponsesmContainer = Ext.create('Ext.container.Container', {
            title: '风险应对',
            authority: 'ROLE_ALL_CONTROL_RESPONS_ORG_RESPONS',
            layout : 'fit',
            onClick: function () {
            	
                if (me.orgresponsePlanEditPanelsm) {
                	me.orgriskResponsesmContainer.remove(me.orgresponsePlanEditPanelsm,true);
                }
                    me.orgresponsePlanEditPanelsm = Ext.create('FHD.view.response.new.SolutionEditPanel', {
                    	type : 'risk',
                        border: false,
                        navHeight: me.navHeight,
                        autoHeight : true
                    });
                    this.add(me.orgresponsePlanEditPanelsm);
                    this.doLayout();
                //刷新
                me.orgresponsePlanEditPanelsm.initParam({
                    type: '0',
            		selectId: me.detailId
                });
                me.orgresponsePlanEditPanelsm.reloadData();
            }
        });

        //历史记录页
        me.riskHistoryGridContainer = Ext.create('Ext.container.Container', {
        	authority:'ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK_HISTORY',
            layout: 'fit',
            title: '历史数据',
            onClick: function () {
                if (me.riskHistoryGrid) {
                	me.riskHistoryGridContainer.remove(me.riskHistoryGrid,true);
                }
                    me.riskHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
                        face: me,
                        type: 'risk',
                        border: false,
                        autoScroll: true,
                        historyCallback: function (data) {
                            me.historyCallback();
                        }
                    });
                    this.add(me.riskHistoryGrid);
                    this.doLayout();
                //刷新
                me.riskHistoryGrid.reloadData(me.detailId);
            }
        });

        me.tabPanel = Ext.create('FHD.ux.layout.treeTabFace.TreeTabTab', { //先去掉 ,me.chartAnalyseContainer
            items: [me.riskRelaKpiGridContainer,me.riskEventGridContainer,me.orgriskResponsesmContainer,me.riskGraphContainer,me.riskHistoryGridContainer],
            listeners: {
                tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                    if (newCard.onClick) {
                        newCard.onClick();
                    }
                }
            }
        });
        
        me.tabContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
            border: false,
            navData : data,
            navHeight: me.navHeight,
            tabpanel: me.tabPanel,
            flex: 1
        });
        
        //先激活第一个面板
		me.riskRelaKpiGridContainer.onClick();

        //刷新页面
        var tab = me.tabPanel.getActiveTab();
        if(tab==me.riskRelaKpiGridContainer){
	        me.riskRelaKpiGrid.reloadData(id);
        }else if(tab==me.riskEventGridContainer){
        	me.riskEventGrid.initParams('risk');
	        me.riskEventGrid.reloadData(id);
        }else if(tab==me.orgriskResponsesmContainer){
            me.orgresponsePlanEditPanelsm.initParam({
                type: 'risk',
        		selectId: id
            });
            me.orgresponsePlanEditPanelsm.reloadData();
        }else if(tab==me.riskGraphContainer){
			me.riskGraph.initParam({
                 riskId:id
        	});
			me.riskGraph.reloadData();
        }else if(tab==me.riskHistoryGridContainer){
        	me.riskHistoryGrid.reloadData(id);
        }else{
        
        }
        
        //跳转页面
        me.showRiskDetail(me.tabContainer, parentId, name);
    },
    reLayoutNavigationBar: function(){},
    go : function(){
    	var me = this;
    	me.reRightLayout(me.tabContainer);
    	if(me.navData){
    		me.reLayoutNavigationBar(me.tabContainer.navData);
		}
    },
    //导出风险排序的Excel
    exportChart: function () {
        var me = this;
        me.headerDatas = [];
        var items = me.columns;
        Ext.each(items, function (item) {
            if (!item.hidden && item.dataIndex != '' && item.dataIndex != 'operate') {
                var value = {};
                value['dataIndex'] = item.dataIndex;
                value['text'] = item.text;
                if(item.dataIndex=='riskStatus'){//对风险状态列自定义header单独处理
                	value['text'] = "状态";
                }
                me.headerDatas.push(value);
            }
        });
		
        var level = me.levelCombo.getValue();
        var query = me.searchField.getValue();
        window.location.href = "riskhistoryversion/findOrgTop10RiskExport.f?exportFileName=" + "" +
            "&sheetName=" + "" + "&headerData=" + Ext.encode(me.headerDatas) + "&style=event"+"&level="+level+"&query="+query;
    }
});