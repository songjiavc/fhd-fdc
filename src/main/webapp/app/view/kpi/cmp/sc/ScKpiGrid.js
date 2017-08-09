Ext.define('FHD.view.kpi.cmp.sc.ScKpiGrid', {
	extend : 'FHD.view.kpi.cmp.kpi.KpiGridPanel',

	border : false,
	kpiMainContainer : null, // 指标编辑容器
	mainPanel : null, // 历史数据图表面板
	paramObj : {},
    navData: null,
	initParam : function(paramObj) {
		var me = this;
		paramObj.navId = paramObj.scid;
		me.paramObj = paramObj;
	},
	reloadData : function() {
		var me = this;
		me.reLoadData();
	},
	reLoadData : function() {
		var me = this;
		if (me.paramObj != undefined) {
			me.store.proxy.extraParams.id = me.paramObj.scid;
			me.store.load();
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
							var paraobj = {};
							paraobj.categoryid = me.paramObj.scid;
							paraobj.kpiids = [];
							var selections = me.getSelectionModel()
									.getSelection();
							Ext.Array.each(selections, function(item) {
										paraobj.kpiids.push(item.get("id"));
									});

							FHD.ajax({
										url : __ctxPath
												+ '/kpi/kpi/removekpibatch.f',
										params : {
											kpiItems : Ext.JSON.encode(paraobj)
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
						categoryId : me.paramObj.scid,
						kpiIds : idArray
					};
					FHD.ajax({
								url : __ctxPath
										+ '/kpi/category/mergecategoryrelakpi.f',
								params : {
									param : Ext.JSON.encode(paraobj)
								},
								callback : function(data) {
									if (data && data.success) {
										me.store.load();
									}
								}
							});
				}

			}
		}).show();

		selectorWindow.addComponent();
	},

	initKpiMainContainer : function() {
		var me = this;
		var param = [];
		if (me.editflag) {
			param.navId = me.paramObj.scid;
			param.kpiId = me.kpiId;
			param.kpiname = me.kpiname;
			param.selecttypeflag = '';
			param.backType = 'sc';
			param.editflag = me.editflag;
			param.scid = me.paramObj.scid;
			param.scname = me.paramObj.scname;
		} else {
			param.navId = me.paramObj.scid;
			param.scname = me.scname;
			param.kpiname = "";
			param.selecttypeflag = '';
			param.backType = 'sc';
			param.editflag = me.editflag;
			param.scid = me.paramObj.scid;
			param.scname = me.paramObj.scname;
		}
		/**
		 * 初始化右侧指标容器
		 */
		me.kpiMainContainer = Ext.create('FHD.view.kpi.cmp.kpi.KpiMain', {
					pcontainer : me,
					paramObj : param,
					undo : me.undo,
					treeId : me.treeId,
					needExtraNav: true
				});
		me.reRightLayout(me.kpiMainContainer);
		if(me.navData) {
		    var data = [];
			for(i = 0;i<me.navData.length;i++) {
				data.push(me.navData[i]);
			}
		    data.push({
			type: 'deptsckpiEdit',
			name: param.kpiname ? param.kpiname : '添加指标',
			id: param.kpiId ? param.kpiId : 'newkpiId',
			containerId: me.kpiMainContainer.id
		    });
			me.reLayoutNavigationBar(data);
		}

	},
	/**
	 * 添加指标
	 */
	kpiaddFun : function() {
		var me = this;
		var param = {};

		me.editflag = false;
		me.initKpiMainContainer();
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
	// 指标列表监听事件
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
		if (me.down("[name='kpidesign_kpicategory_kpiedit']")) {
			me.down("[name='kpidesign_kpicategory_kpiedit']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='kpidesign_kpicategory_kpidel']")) {
			me.down("[name='kpidesign_kpicategory_kpidel']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='kpidesign_scorecardkpi_disable']")) {
			me.down("[name='kpidesign_scorecardkpi_disable']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='kpidesign_scorecardkpi_enable']")) {
			me.down("[name='kpidesign_scorecardkpi_enable']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='kpidesign_scorecardkpi_calc']")) {
			me.down("[name='kpidesign_scorecardkpi_calc']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='kpidesign_scorecardkpi_focus']")) {
			me.down("[name='kpidesign_scorecardkpi_focus']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='kpidesign_scorecardkpi_no_focus']")) {
			me.down("[name='kpidesign_scorecardkpi_no_focus']").setDisabled(me
					.getSelectionModel().getSelection().length === 0);
		}
	    if (me.down("[name='cacel_kpisc_rela']")) {
			me.down("[name='cacel_kpisc_rela']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
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
			url : __ctxPath + "/kpi/category/findcategoryrelakpiresult.f",
			extraParams : {
				id : me.paramObj.scid,
				year : FHD.data.yearId,
				month : FHD.data.monthId,
				quarter : FHD.data.quarterId,
				week : FHD.data.weekId,
				eType : FHD.data.eType,
				isNewValue : FHD.data.isNewValue
			},
			tbarItems : [{
				tooltip : FHD.locale.get('fhd.kpi.kpi.op.addkpi'),
				iconCls : 'icon-add',
				authority : 'ROLE_ALL_KPI_ADD',
				name : 'kpidesign_kpicategory_kpiadd',
				handler : function() {
					me.kpiaddFun();
				},
				text : FHD.locale
						.get('fhd.strategymap.strategymapmgr.subLevel')
			},

		    {
				tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.editkpi"),
				name : 'kpidesign_kpicategory_kpiedit',
				iconCls : 'icon-edit',
				authority : 'ROLE_ALL_KPI_EDIT',
				disabled : true,
				handler : function() {
					me.kpiEditFun();
				},
				text : FHD.locale.get("fhd.common.edit")
			},  {
				tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.delkpi"),
				name : 'kpidesign_kpicategory_kpidel',
				iconCls : 'icon-del',
				authority : 'ROLE_ALL_KPI_DELETE',
				disabled : true,
				handler : function() {
					me.kpiDelFun();
				},
				text : FHD.locale.get("fhd.common.delete")
			},

			{
				tooltip : FHD.locale.get('fhd.kpi.kpi.op.relakpi'),
				btype : 'op',
				name : 'kpidesign_kpicategory_kpirela',
				iconCls : 'icon-plugin-add',
				authority : 'ROLE_ALL_CATEGORY_RELAKPI',
				handler : function() {
					me.kpiRelaFun();
				},
				text : '关联'
			}, 
			{
				tooltip : '取消关联',
				btype : 'op',
				name : 'cacel_kpisc_rela',
				iconCls : 'icon-plugin-delete',
				//authority : 'ROLE_ALL_CATEGORY_RELAKPI',
				handler : function() {
					me.kpiCancelRelaFun();
				},
				text : '取消关联'
			},{
				tooltip : FHD.locale.get('fhd.sys.planMan.start'),
				btype : 'op',
				iconCls : 'icon-plan-start',
				name : 'kpidesign_scorecardkpi_enable',
				authority : 'ROLE_ALL_KPI_ENABLE',
				handler : function() {
					me.enables("0yn_y");
				},
				disabled : true,
				text : FHD.locale.get('fhd.sys.planMan.start')
			}, {
				tooltip : FHD.locale.get('fhd.sys.planMan.stop'),
				btype : 'op',
				iconCls : 'icon-plan-stop',
				authority : 'ROLE_ALL_KPI_ENABLE',
				name : 'kpidesign_scorecardkpi_disable',
				handler : function() {
					me.enables("0yn_n");
				},
				disabled : true,
				text : FHD.locale.get('fhd.sys.planMan.stop')
			}, {
				tooltip : '关注',
				btype : 'op',
				authority : 'ROLE_ALL_KPI_ATTENTION',
				iconCls : 'icon-kpi-heart-add',
				name : 'kpidesign_scorecardkpi_focus',
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
				name : 'kpidesign_scorecardkpi_no_focus',
				handler : function() {
					me.focus('0yn_n');
				},
				disabled : true,
				text : '取消关注'
			}, {
				tooltip : FHD.locale.get('fhd.formula.calculate'),
				btype : 'op',
				iconCls : 'icon-calculator',
				authority : 'ROLE_ALL_KPI_CALCULATE',
				name : 'kpidesign_scorecardkpi_calc',
				handler : function() {
					me.recalc();
				},
				disabled : true,
				text : FHD.locale.get('fhd.formula.calculate')

			}

			],
			type : 'scorecardkpigrid'
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
		loadParam.navId = me.paramObj.scid;
		loadParam.name = name;
		loadParam.kpiname = name;
		loadParam.type = 'sc';
		loadParam.kgrid = kgrid;
		loadParam.memoTitle = memoTitle;
		loadParam.treeId = me.treeId;
		loadParam.kpiid = kpiid;
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
						goback : me.goback,
						go: function() {
							me.mainPanel.reloadData();
							me.reRightLayout(me.mainPanel);
						},
						reLayoutNavigationBar: me.reLayoutNavigationBar
					});
		}
        me.mainPanel.setNavData(me.navData);
		me.mainPanel.paramObj = loadParam;
		me.reRightLayout(me.mainPanel.load(loadParam));
		if(me.navData) {
			me.reLayoutNavigationBar(data);
		}
	},
	goback : function() {

	},
	reRightLayout : function() {

	},
	undo : function() {

	},
	/**
	 * 取消关联指标
	 */
	kpiCancelRelaFun : function() {
		var me = this;
		Ext.MessageBox.show({
					title : '取消关联',
					width : 260,
					msg : '确认取消关联选中的指标么',
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
												+ '/kpi/kpi/removescrelakpiByIds.f',
										params : {
											kpiItems : Ext.JSON.encode(kpiids),
											scId : me.paramObj.scid

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
	reLayoutNavigationBar:function() {
		
	},
	setNavData:function(data) {
		var me = this;
		me.navData = data;
	}
});