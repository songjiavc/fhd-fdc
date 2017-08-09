Ext.define('FHD.view.risk.cmp.RiskRelaKpiGrid', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.riskrelakpigrid',
    requires: ['FHD.ux.TipColumn'],
    avHeight: 22,
    queryUrl: '/cmp/risk/findRiskRelaKpiById',
    xmlMap: null,
    type:'risk',
    operateType: true,
    navData: null,
    //获取当前年份
    getYear: function () {
        var myDate = new Date();
        var year = myDate.getFullYear();
        return year;
    },
    initComponent: function () {
    	var me = this;
        me.chartPanel = Ext.create('FHD.view.kpi.cmp.chart.ColumnChartPanel', {
            border: false,
            width: 350,
            height: 230
        });
        //	备注panel	
        var memotippanel = Ext.create('FHD.view.kpi.cmp.kpi.memo.MemoTipPanel');
        me.cols = [{
            dataIndex: 'riskId',
            invisible: true
        }, {
            dataIndex: 'kpiId',
            invisible: true
        }, {
            dataIndex: 'kgrId',
            invisible: true
        }, {
            cls: 'grid-icon-column-header grid-statushead-column-header',
            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'assessmentStatus',
            sortable: true,
            width: 40,
            renderer: function (v) {
                var color = "";
                var display = "";
                if (v == "icon-ibm-symbol-4-sm") {
                    color = "symbol_4_sm";
                    display = FHD.locale.get("fhd.alarmplan.form.hight");
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
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                    "background-position: center top;' data-qtitle='' " +
                    "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
            }
        }, {
            cls: 'grid-icon-column-header grid-trendhead-column-header',
            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'directionstr',
            sortable: true,
            width: 40,
            renderer: function (v) {
                var color = "";
                var display = "";
                if (v == "icon-ibm-icon-trend-rising-positive") {
                    color = "icon_trend_rising_positive";
                    display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
                } else if (v == "icon-ibm-icon-trend-neutral-null") {
                    color = "icon_trend_neutral_null";
                    display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
                } else if (v == "icon-ibm-icon-trend-falling-negative") {
                    color = "icon_trend_falling_negative";
                    display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                    "background-position: center top;' data-qtitle='' " +
                    "class='" + v + "'  data-qtip='" + display + "'></div>";
            }
        },
        	{
            header: "注释", //添加备注信息列 by haojing
            xtype: 'tipcolumn',
            tips: {
                items: [memotippanel],
                renderer: function (cellIndex, rowIndex, tooltip) {
                    var data = me.items.items[0].store.data.items[rowIndex].data.memoStr;
                    memotippanel.renderData(data, tooltip);
                }
            },
            dataIndex: 'isMemo',
            sortable: false,
            flex: 0.5,
            renderer: function (v, rowIndex, cellIndex) {
                var type = me.type;
                var kpiid = cellIndex.data.kpiId;
                var kgrid = cellIndex.data.kgrId;
                var name = cellIndex.data.kpiName;
                var dateRange = cellIndex.data.timePeriod;
                var memoTitle = "注释——" + name + "(" + dateRange + ")";
                var memohref = "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').gatherResultFun('" + name + "," + kgrid + "," + memoTitle + "," + kpiid + "')\" >";
                if ("1" == v) {
                    var memoStr = cellIndex.data.memoStr;
                    var str = memoStr.split('!@#$');
                    if (str[1] == '0alarm_startus_h') {
                        if (type) {
                            return memohref + "<image src='images/icons/icon_comment_importance_high.gif'   />" + "</a>";
                        } else {
                            return "<image src='images/icons/icon_comment_importance_high.gif'   />";
                        }

                    }
                    if (str[1] == '0alarm_startus_l') {
                        if (type) {
                            return memohref + "<image src='images/icons/icon_comment_importance_low.gif'   />" + "</a>";
                        } else {
                            return "<image src='images/icons/icon_comment_importance_low.gif'   />";
                        }

                    }
                    if (str[1] == '0alarm_startus_n') {
                        if (type) {
                            return memohref + "<image src='images/icons/icon_note.gif'   />" + "</a>";
                        } else {
                            return "<image src='images/icons/icon_note.gif'   />";
                        }

                    }

                } else {
                    if (type) {
                        return memohref + "<image src='images/icons/icon_noreport_properties.gif'  />" + "</a>";
                    } else {
                        return "<image src='images/icons/icon_noreport_properties.gif'  />"
                    }

                }
            }
        }
        , {
            header: FHD.locale.get('fhd.kpi.kpi.form.name'),
            xtype: 'tipcolumn',
            tips: {
                width: 350,
                height: 230,
                maxWidth : 1000,
                items: [me.chartPanel],
                renderer: function (cellIndex, rowIndex, tooltip) {
                   if(me.jsonMap) { 
                   	 var  jsonData  = me.jsonMap[me.items.items[0].store.data.items[rowIndex].data.kpiId];
                   	 var  maxValue =  me.maxMap[me.items.items[0].store.data.items[rowIndex].data.kpiId];
                   	 var  minValue =  me.minMap[me.items.items[0].store.data.items[rowIndex].data.kpiId];
                   	 me.cols[6].tips.items[0].loadData(jsonData,maxValue,minValue);                  	
                   }  
                }
            },
            dataIndex: 'kpiName',
            sortable: true,
            flex: 3,
            renderer: function (v, rowIndex, cellIndex) {
                var kpiid = cellIndex.data.kpiId;
                var dateRange = cellIndex.data.timePeriod;
                var name = cellIndex.data.kpiName;
                var memoTitle = "注释——" + name + "(" + dateRange + ")";
                var type = me.type;
                if (type) {
                    var kgrid = cellIndex.data.kgrId;
                    return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showKpiBasicInfo('" + kpiid + "')\" >" + v + "</a>";
                } else {
                    return v;
                }
            }
        }
         ,{
            header: '权重',
            dataIndex: 'weight',
            sortable: true,
            flex: 1.1
        },{
            header: '频率',
            dataIndex: 'frequency',
            sortable: true,
            flex: 1.1
        }, {
            header: FHD.locale.get('fhd.kpi.kpi.form.finishValue'),
            dataIndex: 'finishValue',
            sortable: true,
            flex: 1.1,
            align: 'right'
        }, {
            header: FHD.locale.get('fhd.kpi.kpi.form.targetValue'),
            dataIndex: 'targetValue',
            sortable: true,
            flex: 1.1,
            align: 'right'
        },{
            header: FHD.locale.get('fhd.kpi.kpi.form.assessmentValue'),
            dataIndex: 'assessmentValue',
            sortable: true,
            flex: 1.1,
            align: 'right'
        } , 
        	
        {
            dataIndex: 'timePeriod',
            hidden: true
        },
        {
            header: FHD.locale.get('fhd.kpi.kpi.form.dateRange'),
            dataIndex: 'dateRange',
            sortable: true,
            flex: 1.2,
            renderer: function (v) {
                return "<div data-qtitle='' data-qtip='" + v + "'>" + v + "</div>";
            }
        },
        {
            header: "操作",
            dataIndex: 'operate',
            sortable: false,
            width: 65,
            renderer: function (v, rowIndex, cellIndex) {
            
                var kpiid = cellIndex.data.kpiId;
                var dateRange = cellIndex.data.timePeriod;
                var name = cellIndex.data.kpiName;
                var memoTitle = "注释——" + name + "(" + dateRange + ")";
                var kgrid = cellIndex.data.kgrId;                   
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').gatherResultFun('" + name + "," + kgrid + "," + memoTitle + "," + kpiid + "')\" class='icon-view' data-qtip='查看'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>"
            }
        }, {    dataIndex: 'memoStr',
                hidden: true
            }];
        me.btnEdit = Ext.create('Ext.Button', {
			tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.editkpi"),
			iconCls : 'icon-edit',
			disabled : true,
			//authority : 'ROLE_ALL_KPI_EDIT',
			handler : function() {
				me.kpiEditFun();
			},
			text : FHD.locale.get("fhd.common.edit")
		});
        me.grid = Ext.create('FHD.ux.GridPanel', {
            border: false,
            columnLines: false,
            cols: me.cols,
            tbarItems :[{
				tooltip : FHD.locale.get('fhd.kpi.kpi.op.addkpi'),
				iconCls : 'icon-add',
				name : 'kpiadd',
				handler : function() {
					me.kpiaddFun();
				},
				text : FHD.locale
						.get('fhd.strategymap.strategymapmgr.subLevel')
			},me.btnEdit,
			{
				tooltip : FHD.locale.get('fhd.kpi.kpi.op.relakpi'),
				btype : 'op',
				name : 'kpiRisk_rela',
				iconCls : 'icon-plugin-add',
				//authority : 'ROLE_ALL_CATEGORY_RELAKPI',
				handler : function() {
					me.kpiRelaFun();
				},
				text : '关联指标'
			},
			{
				tooltip : '取消关联',
				btype : 'op',
				name : 'kpiRisk_delete',
				iconCls : 'icon-plugin-delete',
				//authority : 'ROLE_ALL_CATEGORY_RELAKPI',
				handler : function() {
					me.delkpiRelaFun();
				},
			    disabled : true,
				text : '取消关联'
			},
			{
				name : 'start',
				btype : 'op',
				authority : 'ROLE_ALL_KPI_ENABLE',
				tooltip : FHD.locale.get('fhd.sys.planMan.start'),
				iconCls : 'icon-plan-start',
				handler : function() {
					me.enables("0yn_y");
				},
				disabled : true,
				text : FHD.locale.get('fhd.sys.planMan.start')
			}, {
				name : 'stop',
				btype : 'op',
				authority : 'ROLE_ALL_KPI_ENABLE',
				tooltip : FHD.locale.get('fhd.sys.planMan.stop'),
				iconCls : 'icon-plan-stop',
				handler : function() {
					me.enables("0yn_n");
				},
				disabled : true,
				text : FHD.locale.get('fhd.sys.planMan.stop')
			}, {
				name : 'focus',
				btype : 'op',
				tooltip : '关注',
				authority : 'ROLE_ALL_KPI_ATTENTION',
				iconCls : 'icon-kpi-heart-add',
				handler : function() {
					me.focus('0yn_y');
				},
				disabled : true,
				text : '关注'
			},

			{
				name : 'noFocus',
				btype : 'op',
				tooltip : '取消关注',
				authority : 'ROLE_ALL_KPI_ATTENTION',
				iconCls : 'icon-kpi-heart-delete',
				handler : function() {
					me.focus('0yn_n');
				},
				disabled : true,
				text : '取消关注'
			}, {
				name : 'cal',
				btype : 'op',
				authority : 'ROLE_ALL_KPI_CALCULATE',
				tooltip : FHD.locale.get('fhd.formula.calculate'),
				iconCls : 'icon-calculator',
				handler : function() {
					me.recalc();
				},
				disabled : true,
				text : FHD.locale.get('fhd.formula.calculate')

			}]
        });
        Ext.apply(me, {
            items: [me.grid]
        });
        me.callParent(arguments);
		me.grid.on('selectionchange', function() {
					me.setBtnState();
				});
        me.grid.store.on('refresh', function (store, options) {
            if (store.data.length != 0) {

                if ( me.grid.body != undefined) {
                     me.grid.body.mask("读取中...", "x-mask-loading");
                }
            }

            var kpiId = [];
            var yearId = "";
            var yearIdTemp = new Array();
            for (var i = 0; i < store.data.length; i++) {
            	kpiId.push(store.data.items[i].data.kpiId);
            }

            var paraobj = {};
            paraobj.eType = '0frequecy_all';
            paraobj.kpiId = kpiId.join(",");
            paraobj.isNewValue = FHD.data.isNewValue
            if (FHD.data.yearId == '') {
                paraobj.year = me.getYear();
            } else {
                paraobj.year = FHD.data.yearId;
            }

            FHD.ajax({
                url: __ctxPath + '/kpi/kpi/createtable.f?edit=false',
                params: {
                    condItem: Ext.JSON.encode(paraobj)
                },
                callback: function (data) {
                    if (data && data.success) {
                        me.jsonMap = data.jsonMap;
                    	me.maxMap = data.maxMap;
                    	me.minMap = data.minMap;
                        if ( me.grid.store.data.length != 0) {
                            if ( me.grid.body != undefined) {
                                 me.grid.body.unmask();
                            }
                        }

                    }
                }
            });
        });

    },
    reloadData: function (id) {
        var me = this;
        if (id != null) {
            me.currentId = id;
        }
        me.grid.store.proxy.url = __ctxPath + me.queryUrl;
        me.grid.store.proxy.extraParams.id = me.currentId;
        me.grid.store.load();
    },
    initParams: function (id) {
        var me = this;
        me.currentId = id;
    },
    /**
	 * 添加指标
	 */
	kpiaddFun : function() {
		var me = this;
		var param = {};
		me.editflag = false;
		me.initKpiMainContainer(null, me.currentId, "风险指标添加");
	},
	kpiEditFun : function() {
		var me = this;
		var selections = me.grid.getSelectionModel().getSelection();
		var length = selections.length;
		if (length > 0) {
			if (length >= 2) {
				Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),
						FHD.locale.get('fhd.kpi.kpi.prompt.editone'));
				return;
			} else {
				var selection = selections[0]; // 得到选中的记录
				var kpiId = selection.get('kpiId'); // 获得指标ID
				var kpiname = selection.get('kpiName');
				me.kpiId = kpiId;
				me.kpiname = kpiname;
				me.editflag = true;
				me.initKpiMainContainer(kpiId,me.currentId,kpiname);
			}
		} else {
			Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),
					'请选择一条指标.');
			return;
		}
	},
	initKpiMainContainer: function (id, parentId, name) {
		var me = this;
		var param = [];
		FHD.ajax({
				url : __ctxPath
						+ '/cmp/risk/findRiskNameById.f',
				async:false,
				params : {
					
					riskId : me.currentId
	
				},
				callback : function(data) {
					if (data && data.success) {
						param.riskName = data.riskName;
					}
				}
			});
	    if (me.editflag) {
	    	param.navId =  me.currentId;
	        param.riskId = me.currentId;
			param.kpiId = id;
			param.kpiname = me.kpiname;
			param.selecttypeflag = '';
			param.editflag = true;
			param.backType = 'risk';
		} else {
		    param.navId =  me.currentId;
		    param.riskId = me.currentId;
			param.kpiId = '';
			param.kpiname = '';
			param.selecttypeflag = '';
			param.editflag = me.editflag;
			param.backType = 'risk';
		}
	    me.kpiMainContainer = Ext.create('FHD.view.kpi.cmp.kpi.KpiMain', {
					pcontainer : me,
					paramObj : param,
					undo : me.undo,
					treeId : me.navFunId
				});
		me.showKpiAdd(me.kpiMainContainer, parentId, name);
		if(me.navData) {
		    var data = [];
			for(i = 0;i<me.navData.length;i++) {
				data.push(me.navData[i]);
			}
		    data.push({
			type: 'deptsmkpiEdit',
			name: param.kpiname ? param.kpiname : '添加指标',
			id: param.kpiId ? param.kpiId : 'newkpiId',
			containerId: me.kpiMainContainer.id
		    });
			me.reLayoutNavigationBar(data);
		}
		
	},
	setBtnState : function() {
		var me = this;
		//var btns = me.getDockedItems('toolbar[dock="top"]');
		if (me.btnEdit) {
			me.btnEdit.setDisabled(me.grid.getSelectionModel().getSelection().length === 0);
		}
		if (me.btnDel) {
			me.btnDel.setDisabled(me.grid.getSelectionModel().getSelection().length === 0);
		}
		if (me.grid.down("[name='stop']")) {
			me.grid.down("[name='stop']").setDisabled(me.grid.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.grid.down("[name='start']")) {
			me.grid.down("[name='start']").setDisabled(me.grid.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.grid.down("[name='cal']")) {
			me.grid.down("[name='cal']").setDisabled(me.grid.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.grid.down("[name='focus']")) {
			me.grid.down("[name='focus']").setDisabled(me.grid.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.grid.down("[name='noFocus']")) {
			me.grid.down("[name='noFocus']").setDisabled(me.grid.getSelectionModel()
					.getSelection().length === 0);
		}
	    if (me.grid.down("[name='kpiRisk_delete']")) {
			me.grid.down("[name='kpiRisk_delete']").setDisabled(me.grid.getSelectionModel()
					.getSelection().length === 0);
		}
	},
	/**
	 * 删除指标
	 */
	kpiDelFun : function() {
		var me = this;
		var grid = me.grid;
		Ext.MessageBox.show({
					title : '取消关联',
					width : 260,
					msg : '确定取消关联选中的指标么',
					buttons : Ext.MessageBox.YESNO,
					icon : Ext.MessageBox.QUESTION,
					fn : function(btn) {
						if (btn == 'yes') { // 确认删除
							var kpiids = [];
							var selections = grid.getSelectionModel()
									.getSelection();
							Ext.Array.each(selections, function(item) {
										kpiids.push(item.get("kpiId"));
									});

							FHD.ajax({
										url : __ctxPath
												+ '/cmp/risk/removeriskrelakpi.f',
										params : {
											kpiItems : Ext.JSON.encode(kpiids),
											riskId : me.currentId

										},
										callback : function(data) {
											if (data && data.success) {
												grid.store.load();
											}
										}
									});
						}
					}
				});
	},
  undo : function() {

	},
  focus : function(focus) {
		var me = this;
		var paraobj = {};
		paraobj.focus = focus;
		paraobj.kpiids = [];
		var selections = me.getSelectionModel().getSelection();
		Ext.Array.each(selections, function(item) {
					paraobj.kpiids.push(item.get("kpiId"));
				});
		if (me.body != undefined) {
			if ('Y' == focus) {
				me.body.mask("关注中...", "x-mask-loading");
			} else {
				me.body.mask("取消关注中...", "x-mask-loading");
			}
		}
		FHD.ajax({
					url : __ctxPath + '/kpi/kpi/mergekpifoucs.f',
					params : {
						kpiItems : Ext.JSON.encode(paraobj)
					},
					callback : function(data) {
						if (data && data.success) {
							me.store.load();
							if (me.body != undefined) {
								me.body.unmask();
							}
						}
					}
				});
	},
	recalc : function() {
		var me = this.grid;
		var objArr = [];
		var selections = me.getSelectionModel().getSelection();
		for (var i = 0; i < selections.length; i++) {
			var obj = {};
			obj.kpiId = selections[i].get('kpiId');
			obj.timeperiod = selections[i].get('timePeriod');
			objArr.push(obj);
		}
		if (me.body != undefined) {
			me.body.mask("计算中...", "x-mask-loading");
		}
		FHD.ajax({
					url : __ctxPath + '/formula/reformulacalculate.f',
					params : {
						kpiItems : Ext.JSON.encode(objArr)
					},
					callback : function(data) {
						if (data && data.success) {
							me.store.load();
							if (me.body != undefined) {
								me.body.unmask();
							}
						}
					}
				});

	},
	enables : function(enable) {
		var me = this.grid;
		var paraobj = {};
		paraobj.enable = enable;
		paraobj.kpiids = [];
		var selections = me.getSelectionModel().getSelection();
		Ext.Array.each(selections, function(item) {
					paraobj.kpiids.push(item.get("kpiId"));
				});
		if (me.body != undefined) {
			if ('0yn_y' == enable) {
				me.body.mask("启用中...", "x-mask-loading");
			} else {
				me.body.mask("停用中...", "x-mask-loading");
			}
		}
		FHD.ajax({
					url : __ctxPath + '/kpi/kpi/mergekpienable.f',
					params : {
						kpiItems : Ext.JSON.encode(paraobj)
					},
					callback : function(data) {
						if (data && data.success) {
							me.store.load();
							if (me.body != undefined) {
								me.body.unmask();
							}
						}
					}
				});
	},
	gatherResultFun : function(obj) {
		var me = this;
		var paraobj = obj.split(",");
		var name = paraobj[0];
		var kgrid = paraobj[1];
		var memoTitle = paraobj[2];
		var kpiid = paraobj[3];
		PARAM.navId =  me.currentId;
		PARAM.name = name;
		PARAM.kpiname = name;
		PARAM.type = 'risk';
		PARAM.kgrid = kgrid;
		PARAM.memoTitle = memoTitle;
		PARAM.treeId = me.navFunId;
		PARAM.kpiid = kpiid;
		var data = null;
	    if(me.navData) {
			data = [];
			for(i = 0; i< me.navData.length;i++) {
				data.push(me.navData[i]);
			}
			data.push({
			  id: kpiid,
			  type: 'kpiGraph',
			  name: name
			});
		}
		if (me.mainPanel == null) {
			me.mainPanel = Ext.create('FHD.view.kpi.cmp.kpi.result.MainPanel',
					{
						pcontainer : me,
						goback : me.undo,
						operateType: me.operateType,
					    navData: me.navData,
						go: function() {
							me.reRightLayout(me.mainPanel);
							if(me.navData) {
							   me.reLayoutNavigationBar(data);
					        }	
						},
						reLayoutNavigationBar: me.reLayoutNavigationBar
					});
		}
	    me.showKpiDetail(me.mainPanel.load(PARAM),me.currentId,name);
	    if(me.navData) {
			me.reLayoutNavigationBar(data);
		}	
	},
    /**
	 * 关联指标
	 */
	kpiRelaFun : function() {
		var me = this;
		var selectorWindow = Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow', {
			multiSelect : true,
			onSubmit : function(store) {
				var idArray = [];
				var items = store.data.items;
				Ext.Array.each(items, function(item) {
							idArray.push(item.data.id);
						});
				if (idArray.length > 0) {
					var paraobj = {
						riskId : me.currentId,
						kpiIds : idArray
					};
					FHD.ajax({
								url : __ctxPath
										+ '/cmp/risk/mergeRiskrelakpi.f',
								params : {
									param : Ext.JSON.encode(paraobj)
								},
								callback : function(data) {
									if (data && data.success) {
										me.grid.store.load();
									}
								}
							});
				}

			}
		}).show();
		selectorWindow.addComponent();
	},
	
	delkpiRelaFun : function() {
		var me = this.grid;
		var paraobj = {};
		paraobj.riskId = this.currentId;
		paraobj.kpiIds = [];
		var selections = me.getSelectionModel().getSelection();
		Ext.Array.each(selections, function(item) {
					paraobj.kpiIds.push(item.get("kpiId"));
				});
		Ext.MessageBox.show({
            title: '提示',
            width: 260,
            msg: '确认取消关联选中的指标吗？',
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                    FHD.ajax({
						url : __ctxPath + '/cmp/risk/deleteRiskrelakpi.f',
						params : {
							param : Ext.JSON.encode(paraobj)
						},
						callback : function(data) {
							if (data && data.success) {
								me.store.load();
								if (me.body != undefined) {
									me.body.unmask();
								}
							}
						}
					});
                }
            }
        });		
	},
	
	reLayoutNavigationBar: function() {
		
	},
    showKpiBasicInfo: function(id) {
		var me = this;
	    me.kpiBasicInfoForm = Ext.create('FHD.view.kpi.cmp.kpi.KpiBasicInfoForm', {});
	    var paramObj = {
	        kpiid: id //目标ID
	    };
	    me.kpiBasicInfoForm.initParam(paramObj);
	    me.kpiBasicInfoForm.reloadData();
	    me.window = Ext.create('FHD.ux.Window', {
	        title: '指标标基本信息',
	        items: [],
	        closeAction: 'destroy',
	        maximizable: true,
	        collapsible : true
	    });
	    me.window.show(); 
	    me.window.add(me.kpiBasicInfoForm);  
    }
})