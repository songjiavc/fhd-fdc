Ext.define('FHD.view.kpi.cmp.myfolder.MyKpiGrid', {
	extend : 'FHD.view.kpi.cmp.kpi.KpiGridPanel',

	border : false,
	kpiMainContainer : null, // 指标编辑容器
	mainPanel : null, // 历史数据图表面板
	paramObj : {},
	navData: null,
	initParam : function(paramObj) {
		var me = this;
		paramObj.navId = paramObj.smid;
		me.paramObj = paramObj;
	},
	reLoadData : function() {
		var me = this;
		me.store.load();
	},
	initKpiMainContainer : function() {
		var me = this;
		var param = [];
		if (me.editflag) {
			param.navId = 'allkpi';
			param.kpiId = me.kpiId;
			param.kpiname = me.kpiname;
			param.selecttypeflag = '';
			param.backType = 'myfolder';
			param.editflag = me.editflag;
		} else {
			param.navId = 'allkpi';
			param.kpiname = "";
			param.selecttypeflag = '';
			param.backType = 'myfolder';
			param.editflag = me.editflag;
			param.empType = 'dept_emp';
		}
		/**
		 * 初始化右侧指标容器
		 */
        me.kpiMainContainer = Ext.create('FHD.view.myallfolder.kpifolder.KpiEditMainPanel', {
            pcontainer: me,
            paramObj: param,
            undo: me.undo,
            navData : me.navData
        });

	},
	/**
	 * 添加指标
	 */
	kpiaddFun : function() {
		var me = this;
		var param = {};
		me.editflag = false;
		me.initKpiMainContainer();
		me.reRightLayout(me.kpiMainContainer);
	},

	/**
	 * 编辑指标
	 */
	kpiEditFun : function() {
		var me = this;
		var selections = me.getSelectionModel().getSelection();
		var length = selections.length;
		if (length > 0) {
			if (length >= 2) {
				Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),
						FHD.locale.get('fhd.kpi.kpi.prompt.editone'));
				return;
			} else {
				var selection = selections[0]; // 得到选中的记录
				var kpiId = selection.get('id'); // 获得指标ID
				var kpiname = selection.get('name');
				me.kpiId = kpiId;
				me.kpiname = kpiname;
				me.editflag = true;
				me.initKpiMainContainer();
				me.reRightLayout(me.kpiMainContainer);
			}
		} else {
			Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),
					'请选择一条指标.');
			return;
		}
	},
	/**
	 * 删除指标
	 */
	kpiDelFun : function() {
		var me = this;
		Ext.MessageBox.show({
					title : FHD.locale.get('fhd.common.delete'),
					width : 260,
					msg : FHD.locale.get('fhd.common.makeSureDelete'),
					buttons : Ext.MessageBox.YESNO,
					icon : Ext.MessageBox.QUESTION,
					fn : function(btn) {
						if (btn == 'yes') { // 确认删除
							var kpiids = [];
							var selections = me.getSelectionModel()
									.getSelection();
							Ext.Array.each(selections, function(item) {
										kpiids.push(item.get("id"));
									});

							FHD.ajax({
										url : __ctxPath
												+ '/kpi/kpi/removecommonkpibatch.f',
										params : {
											kpiItems : Ext.JSON.encode(kpiids)
										},
										callback : function(data) {
											if (data && data.success) {
												me.store.load();
											}
										}
									});
						}
					}
				});
	},
	addListerner : function() {
		var me = this;
		me.store.on('load', function() {
					me.setBtnState();
				});
		me.on('selectionchange', function() {
					me.setBtnState();
				}); // 选择记录发生改变时改变按钮可用状态

	},
	// 改变按钮状态
	setBtnState : function() {
		var me = this;
		if (me.down("[name='mykpi_kpiedit']")) {
			me.down("[name='mykpi_kpiedit']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='mykpi_kpidel']")) {
			me.down("[name='mykpi_kpidel']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='mykpi_disable']")) {
			me.down("[name='mykpi_disable']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='mykpi_enable']")) {
			me.down("[name='mykpi_enable']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='mykpi_calc']")) {
			me.down("[name='mykpi_calc']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='mykpi_focus']")) {
			me.down("[name='mykpi_focus']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='mykpi_no_focus']")) {
			me.down("[name='mykpi_no_focus']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
	},
	recalc : function() {
		var me = this;
		var objArr = [];
		var selections = me.getSelectionModel().getSelection();
		for (var i = 0; i < selections.length; i++) {
			var obj = {};
			obj.kpiId = selections[i].get('id');
			obj.timeperiod = selections[i].get('timeperiod');
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
	focus : function(focus) {
		var me = this;
		var paraobj = {};
		paraobj.focus = focus;
		paraobj.kpiids = [];
		var selections = me.getSelectionModel().getSelection();
		Ext.Array.each(selections, function(item) {
					paraobj.kpiids.push(item.get("id"));
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
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
					isDisplayWeight:false,
					url : __ctxPath + "/kpi/myfolder/findmykpirelaresult.f",
					extraParams : {
						year : FHD.data.yearId,
						month : FHD.data.monthId,
						quarter : FHD.data.quarterId,
						week : FHD.data.weekId,
						eType : FHD.data.eType,
						isNewValue : FHD.data.isNewValue
					},
					checked : true,
					type : 'mykpigrid',
					tbarItems : [{
						tooltip : FHD.locale.get('fhd.kpi.kpi.op.addkpi'),
						iconCls : 'icon-add',
						authority : 'ROLE_ALL_KPI_ADD',
						name : 'mykpi_kpiadd',
						handler : function() {
							me.kpiaddFun();
						},
						text : FHD.locale
								.get('fhd.strategymap.strategymapmgr.subLevel')
					},

				   {
						tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.editkpi"),
						name : 'mykpi_kpiedit',
						authority : 'ROLE_ALL_KPI_EDIT',
						iconCls : 'icon-edit',
						disabled : true,
						text : FHD.locale.get("fhd.common.edit"),
						handler : function() {
							me.kpiEditFun();
						}

					}, {
						tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.delkpi"),
						name : 'mykpi_kpidel',
						authority : 'ROLE_ALL_KPI_DELETE',
						iconCls : 'icon-del',
						disabled : true,
						text : FHD.locale.get("fhd.common.delete"),
						handler : function() {
							me.kpiDelFun();
						}
					}, {
						tooltip : FHD.locale.get('fhd.sys.planMan.start'),
						btype : 'op',
						authority : 'ROLE_ALL_KPI_ENABLE',
						iconCls : 'icon-plan-start',
						name : 'mykpi_enable',
						text : FHD.locale.get('fhd.sys.planMan.start'),
						handler : function() {
							me.enables("0yn_y");
						},
						disabled : true
					}, {
						tooltip : FHD.locale.get('fhd.sys.planMan.stop'),
						btype : 'op',
						authority : 'ROLE_ALL_KPI_ENABLE',
						iconCls : 'icon-plan-stop',
						name : 'mykpi_disable',
						text : FHD.locale.get('fhd.sys.planMan.stop'),
						handler : function() {
							me.enables("0yn_n");
						},
						disabled : true
					}, {
						tooltip : '关注',
						btype : 'op',
						authority : 'ROLE_ALL_KPI_ATTENTION',
						iconCls : 'icon-kpi-heart-add',
						name : 'mykpi_focus',
						handler : function() {
							me.focus('0yn_y');
						},
						disabled : true,
						text : '关注'
					},

					{
						tooltip : '取消关注',
						btype : 'op',
						authority : 'ROLE_ALL_KPI_ATTENTION',
						iconCls : 'icon-kpi-heart-delete',
						name : 'mykpi_no_focus',
						handler : function() {
							me.focus('0yn_n');
						},
						disabled : true,
						text : '取消关注'
					},

					{
						tooltip : FHD.locale.get('fhd.formula.calculate'),
						btype : 'op',
						iconCls : 'icon-calculator',
						authority : 'ROLE_ALL_KPI_CALCULATE',
						name : 'mykpi_calc',
						handler : function() {
							me.recalc();
						},
						disabled : true,
						text : FHD.locale.get('fhd.formula.calculate')

					}

					]
				});

		me.callParent(arguments);

		me.addListerner();
	},
	enables : function(enable) {
		var me = this;
		var paraobj = {};
		paraobj.enable = enable;
		paraobj.kpiids = [];
		var selections = me.getSelectionModel().getSelection();
		Ext.Array.each(selections, function(item) {
					paraobj.kpiids.push(item.get("id"));
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
		var loadParam = {};
		loadParam.kpiname = name;
		loadParam.type = 'myfolder';
		loadParam.kgrid = kgrid;
		loadParam.name = name;
		loadParam.navId = me.paramObj.smid;
		loadParam.memoTitle = memoTitle;
		loadParam.kpiid = kpiid;
        if (me.historyDataPanel == null) {
            me.historyDataPanel = Ext.create('FHD.view.myallfolder.kpifolder.DeptRelaKpiAnalysePanel', {
                goback: me.goback,
                navData: me.navData,
                go:function() {
                	 me.historyDataPanel.reLoadData();
                	 me.reRightLayout(me.historyDataPanel);
                }                
            });
        }
        me.historyDataPanel.paramObj = loadParam;
        me.historyDataPanel.card.setActiveItem(me.historyDataPanel.mainPanel);
        me.reRightLayout(me.historyDataPanel);
        me.historyDataPanel.reLoadData();


	},
	goback : function() {

	},
	reRightLayout : function() {

	},
	undo : function() {

	}

});