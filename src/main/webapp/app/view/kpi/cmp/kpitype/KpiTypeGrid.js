Ext.define('FHD.view.kpi.cmp.kpitype.KpiTypeGrid', {
	extend : 'FHD.view.kpi.cmp.kpi.KpiGridPanel',
	alias : 'widget.kpitypekpigrid',
	kpiMainContainer : null, // 指标编辑容器
	mainPanel : null, // 历史数据图表面板
	paramObj : {},
	border : false,
	navData: null,
	/**
	 * 初始化该类所用到的参数
	 */

	initParam : function(paramObj) {
		var me = this;
		paramObj.navId = paramObj.kpitypeid;
		me.paramObj = paramObj;
	},
	reLoadData : function() {
		var me = this;
		if (me.paramObj != undefined) {
			me.store.proxy.extraParams.id = me.paramObj.kpitypeid;
			me.store.load();
		}
	},
	initKpiMainContainer : function() {
		var me = this;
		var param = [];
		if (me.editflag) {
			param.navId = me.paramObj.kpitypeid;
			param.kpiId = me.kpiId;
			param.kpiname = me.kpiname;
			param.selecttypeflag = '';
			param.backType = 'kpi';
			param.editflag = me.editflag;
			param.scid = me.paramObj.scid;
			param.scname = me.scname;
		} else {
			param.navId = me.paramObj.kpitypeid;

			param.kpiname = "";
			param.selecttypeflag = '';
			param.backType = 'kpi';
			param.editflag = me.editflag;
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
	kpiaddFun : function() {
		var me = this;
		var param = {};

		me.editflag = false;
		me.initKpiMainContainer();
		me.reRightLayout(me.kpiMainContainer);
		me.kpiMainContainer.reLoadNav();
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
	},
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			isDisplayWeight:false,
			url : __ctxPath + "/kpi/kpi/findkpityperelaresult.f",
			extraParams : {
				id : me.paramObj.kpitypeid,
				year : FHD.data.yearId,
				month : FHD.data.monthId,
				quarter : FHD.data.quarterId,
				week : FHD.data.weekId,
				eType : FHD.data.eType,
				isNewValue : FHD.data.isNewValue
			},
			checked : true,
			type : 'kpitypekpigrid',
			tbarItems : [

			{
						tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.editkpi"),
						name : 'kpidesign_kpicategory_kpiedit',
						authority : 'ROLE_ALL_KPI_EDIT',
						iconCls : 'icon-edit',
						disabled : true,
						handler : function() {
							me.kpiEditFun();
						},
						text : FHD.locale.get("fhd.common.edit")
					},  {
						tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.delkpi"),
						name : 'kpidesign_kpicategory_kpidel',
						authority : 'ROLE_ALL_KPI_DELETE',
						iconCls : 'icon-del',
						disabled : true,
						handler : function() {
							me.kpiDelFun();
						},
						text : FHD.locale.get("fhd.common.delete")
					},  {
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
						name : 'kpidesign_scorecardkpi_disable',
						authority : 'ROLE_ALL_KPI_ENABLE',
						handler : function() {
							me.enables("0yn_n");
						},
						disabled : true,
						text : FHD.locale.get('fhd.sys.planMan.stop')
					}, {
						tooltip : '关注',
						btype : 'op',
						iconCls : 'icon-kpi-heart-add',
						name : 'kpidesign_scorecardkpi_focus',
						authority : 'ROLE_ALL_KPI_ATTENTION',
						handler : function() {
							me.focus('0yn_y');
						},
						disabled : true,
						text : '关注'
					},

					{
						tooltip : '取消关注',
						btype : 'op',
						iconCls : 'icon-kpi-heart-delete',
						name : 'kpidesign_scorecardkpi_no_focus',
						authority : 'ROLE_ALL_KPI_ATTENTION',
						handler : function() {
							me.focus('0yn_n');
						},
						disabled : true,
						text : '取消关注'
					}, {
						tooltip : FHD.locale.get('fhd.formula.calculate'),
						btype : 'op',
						iconCls : 'icon-calculator',
						name : 'kpidesign_scorecardkpi_calc',
						authority : 'ROLE_ALL_KPI_CALCULATE',
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

		FHD.ajax({
					url : __ctxPath + '/kpi/kpi/mergekpienable.f',
					params : {
						kpiItems : Ext.JSON.encode(paraobj)
					},
					callback : function(data) {
						if (data && data.success) {
							me.store.load();
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
		loadParam.navId = me.paramObj.kpitypeid;
		loadParam.name = name;
		loadParam.kpiname = name;
		loadParam.type = 'kpi';
		loadParam.kgrid = kgrid;
		loadParam.memoTitle = memoTitle;
		loadParam.kpiid = kpiid;
		loadParam.treeId = me.treeId;
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
    reLayoutNavigationBar:function(param) {
		
	},
	setNavData:function(data) {
		var me = this;
		me.navData = data;
	}
});