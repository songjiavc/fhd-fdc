Ext.define('FHD.view.kpi.cmp.sm.SmKpiGrid', {
	extend : 'FHD.view.kpi.cmp.kpi.KpiGridPanel',

	border : false,
	kpiMainContainer : null, // 指标编辑容器
	mainPanel : null, // 历史数据图表面板
	paramObj : {},
	navData: null,
	/**
	 * 初始化该类所用到的参数
	 */

	initParam : function(paramObj) {
		var me = this;
		paramObj.navId = paramObj.smid;
		me.paramObj = paramObj;
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
												+ '/kpi/kpi/removesmrelakpibatch.f',
										params : {
											kpiItems : Ext.JSON.encode(kpiids),
											smId : me.paramObj.smid

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
												+ '/kpi/kpi/removesmrelakpiByIds.f',
										params : {
											kpiItems : Ext.JSON.encode(kpiids),
											smId : me.paramObj.smid

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

	initKpiMainContainer : function() {
		var me = this;
		var param = [];
		if (me.editflag) {
			param.navId = me.paramObj.smid;
			param.smid = me.paramObj.smid;
			param.kpiId = me.kpiId;
			param.kpiname = me.kpiname;
			param.selecttypeflag = '';
			param.backType = 'sm';
			param.editflag = true;
			param.smname = me.paramObj.smname;
		} else {
			param.navId = me.paramObj.smid;
			param.smid = me.paramObj.smid;
			param.kpiId = '';
			param.kpiname = '';
			param.selecttypeflag = '';
			param.backType = 'sm';
			param.editflag = me.editflag;
			param.smname = me.paramObj.smname;
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
			type: 'deptsmkpiEdit',
			name: param.kpiname ? param.kpiname : '添加指标',
			id: param.kpiId ? param.kpiId : 'newkpiId',
			containerId: me.kpiMainContainer.id
		    });
			me.reLayoutNavigationBar(data);
		}
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
			}
		} else {
			Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),
					'请选择一条指标.');
			return;
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
		me.reRightLayout(me.kpiMainContainer);
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
		var btns = me.getDockedItems('toolbar[dock="top"]');
		if (me.btnEdit) {
			me.btnEdit
					.setDisabled(me.getSelectionModel().getSelection().length === 0);
		}
		if (me.btnDel) {
			me.btnDel
					.setDisabled(me.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='stop']")) {
			me.down("[name='stop']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='start']")) {
			me.down("[name='start']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='cal']")) {
			me.down("[name='cal']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='focus']")) {
			me.down("[name='focus']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='noFocus']")) {
			me.down("[name='noFocus']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
	    if (me.down("[name='cacel_kpism_rela']")) {
			me.down("[name='cacel_kpism_rela']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
	},
	initComponent : function() {
		var me = this;
		me.btnEdit = Ext.create('Ext.Button', {
			tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.editkpi"),
			iconCls : 'icon-edit',
			disabled : true,
			authority : 'ROLE_ALL_KPI_EDIT',
			handler : function() {
				me.kpiEditFun();
			},
			text : FHD.locale.get("fhd.common.edit")
		});
		me.btnDel = Ext.create('Ext.Button', {
			tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.delkpi"),
			iconCls : 'icon-del',
			disabled : true,
			authority : 'ROLE_ALL_KPI_DELETE',
			handler : function() {
				me.kpiDelFun();
			},
			text : FHD.locale.get("fhd.common.delete")
		});
		Ext.apply(me, {
			url : __ctxPath + "/kpi/kpistrategymap/findsmrelakpiresult.f",
			extraParams : {
				id : me.paramObj.smid,
				year : FHD.data.yearId,
				month : FHD.data.monthId,
				quarter : FHD.data.quarterId,
				week : FHD.data.weekId,
				eType : FHD.data.eType,
				isNewValue : FHD.data.isNewValue
			},
			checked : true,
			type : 'strategyobjectivekpigrid',
			tbarItems : [{
				tooltip : FHD.locale.get('fhd.kpi.kpi.op.addkpi'),
				iconCls : 'icon-add',
				name : 'kpiadd',
				authority : 'ROLE_ALL_KPI_ADD',
				handler : function() {
					me.kpiaddFun();
				},
				text : FHD.locale
						.get('fhd.strategymap.strategymapmgr.subLevel')
			}, me.btnEdit, me.btnDel, 
			{
				tooltip : FHD.locale.get('fhd.kpi.kpi.op.relakpi'),
				btype : 'op',
				name : 'kpism_rela',
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
				name : 'cacel_kpism_rela',
				iconCls : 'icon-plugin-delete',
				//authority : 'ROLE_ALL_CATEGORY_RELAKPI',
				handler : function() {
					me.kpiCancelRelaFun();
				},
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

		me.callParent(arguments);

		me.addListerner();
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

	reloadData : function() {
		var me = this;
		me.reLoadData();
	},
	/**
	 * 重新加载列表数据
	 */
	reLoadData : function() {
		var me = this;
		if (me.paramObj != undefined) {
			me.store.proxy.extraParams.id = me.paramObj.smid;
			me.store.load();
		}
	},
	gatherResultFun : function(obj) {
		var me = this;
		var paraobj = obj.split(",");
		var name = paraobj[0];
		var kgrid = paraobj[1];
		var memoTitle = paraobj[2];
		var kpiid = paraobj[3];
		var loadParam = {
			
		};
		loadParam.navId = me.paramObj.smid;
		loadParam.name = name;
		loadParam.kpiname = name;
		loadParam.type = 'sm';
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
	reRightLayout : function(p) {
		var me = this;
	},
	goback : function() {
		var me = this;
	},
	undo : function() {

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
						smId : me.paramObj.smid,
						kpiIds : idArray
					};
					FHD.ajax({
								url : __ctxPath
										+ '/kpi/kpistrategymap/mergesmrelakpi.f',
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
	reLayoutNavigationBar:function(param) {
		
	},
	setNavData:function(data) {
		var me = this;
		me.navData = data;
	}
});