Ext.define('FHD.view.myallfolder.kpifolder.DeptRelaSmGrid', {
	extend : 'FHD.ux.GridPanel',
	requires:['FHD.ux.TipColumn'],
	extraParams : {},
	layout : 'fit',
	border : false,
	paramObj : {},
	queryUrl : '',
	navData : [],//导航条信息
	/**
	 * 初始化该类所用到的参数
	 */

	initParam : function(paramObj) {
		var me = this;
		me.paramObj = paramObj;
	},
	reRightLayout : function() {
		var me = this;
	},

	// 添加同级或下级
	addFun : function(type) {
		var me = this;
		var length = me.getSelectionModel().getSelection().length;
		if (length == 0) {
			if ("same" == type) {
				me.reloadSmBaicInfoData('undefined', '', "sm_root", "目标库",
						false);
			}
		} else {
			var selectedRecord = me.getSelectionRecord();
			if (selectedRecord) {
				var smid = selectedRecord.get('id');
				if ("same" == type) {// 添加同级
					// 查询出父记分卡id和name
					FHD.ajax({
								async : false,
								url : __ctxPath
										+ '/kpi/kpistrategymap/findparentbyid.f',
								params : {
									id : smid
								},
								callback : function(data) {
									var parentid = data.parentid;
									var parentname = data.parentname;
									if (!parentid) {
										parentid = "sm_root";
									}
									if (!parentname) {
										parentname = "目标库";
									}
									me.reloadSmBaicInfoData('undefined', '',
											parentid, parentname, false);
								}
							});

				} else if ("sub" == type) {// 添加下级
					var parentid = selectedRecord.get('id'); // 获得记分卡ID
					var parentname = selectedRecord.get('name');// 记分卡名称
					me.reloadSmBaicInfoData('undefined', '', parentid,
							parentname, false);
				}
			}
		}

	},
	/**
	 * 删除
	 */
	delFun : function() {
		var me = this;
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') { // 确认删除
					var selectionRecord = me.getSelectionRecord();
					if (selectionRecord) {
						FHD.ajax({
							url : __ctxPath
									+ '/kpi/kpistrategymap/removestrategymap.f',
							params : {
								id : selectionRecord.get('id')
							},
							callback : function(ret) {
								if (ret && !ret.result) {
									Ext.MessageBox.alert(FHD.locale
													.get('fhd.common.prompt'),
											'存在下级,不能删除!');
								} else {
									me.store.load();
									FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								}
							}
						});
					}

				}
			}
		});
	},
	/**
	 * 编辑
	 */
	editFun : function() {
		var me = this;
		var selectedRecord = me.getSelectionRecord();
		if (selectedRecord) {
			var smid = selectedRecord.get('id'); // 获得记分卡ID
			var smname = selectedRecord.get('name');// 记分卡名称
			me.reloadSmBaicInfoData(smid, smname, 'sm_root', '目标库', true);
		}
	},
	// 初始化记分卡基本信息数据
	reloadSmBaicInfoData : function(smid, smname, parentid, parentname,
			editflag) {
		var me = this;
		var paramObj = {
			smid : smid, // 记分卡ID
			smname : smname,// 记分卡名称
			parentid : parentid, // 父记分卡ID
			parentname : parentname, // 父记分卡名称
			editflag : editflag, // 是否是编辑状态
			navid : me.paramObj.navid,// 树节点id
			navtype : me.paramObj.navtype,// 导航类型
			treeid : me.paramObj.treeid,// 树id
			empType :  'dept_emp'
		};
		me.smBasicInfoComp.initParam(paramObj);
		me.reRightLayout(me.smBasicInfoComp);
		me.smBasicInfoComp.reloadData();
	},
	
	destroy:function(){
    	if(this.smBasicInfoComp){
    		if(this.smBasicInfoComp.navigationBar) {
    			this.smBasicInfoComp.navigationBar.destroy();
    		}    		
    		this.smBasicInfoComp.destroy();
    	}
    	this.callParent(arguments);
    },
    
	// 创建目标基本信息
	createSmBasicInfo : function() {
		var me = this;
		if (!me.smBasicInfoComp) {
			me.smBasicInfoComp = Ext.create(
					'FHD.view.myallfolder.kpifolder.DeptSmBasicInfo', {
						undo : me.undo,
						navData: me.navData
					});
		}
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
		if (me.down("[name='add_sm_sub']")) {
			me.down("[name='add_sm_sub']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.btnEdit) {
			me.btnEdit
					.setDisabled(me.getSelectionModel().getSelection().length === 0);
		}
		if (me.btnDel) {
			me.btnDel
					.setDisabled(me.getSelectionModel().getSelection().length === 0);
		}
		if (me.down("[name='sm_stop']")) {
			me.down("[name='sm_stop']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='sm_start']")) {
			me.down("[name='sm_start']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='sm_focus']")) {
			me.down("[name='sm_focus']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='sm_noFocus']")) {
			me.down("[name='sm_noFocus']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}
		if (me.down("[name='sm_calc']")) {
			me.down("[name='sm_calc']").setDisabled(me.getSelectionModel()
					.getSelection().length === 0);
		}

	},

	enables : function(enable) {
		var me = this;
		var paraobj = {};
		paraobj.enable = enable;
		paraobj.smids = [];
		var selections = me.getSelectionModel().getSelection();
		Ext.Array.each(selections, function(item) {
					paraobj.smids.push(item.get("id"));
				});

		if (me.body != undefined) {
			if ('0yn_y' == enable) {
				me.body.mask("启用中...", "x-mask-loading");
			} else {
				me.body.mask("停用中...", "x-mask-loading");
			}
		}
		FHD.ajax({
					params : {
						smItems : Ext.JSON.encode(paraobj)
					},
					url : __ctxPath + '/kpi/kpistrategymap/mergesmenable.f',
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
			obj.type = 'strategy';
			obj.id = selections[i].get('id');
			obj.timeperiod = selections[i].get('timeperiod') == null
					? ''
					: selections[i].get('timeperiod');
			obj.gatherResultId = selections[i].get('kgrId') == null
					? ''
					: selections[i].get('kgrId');
			objArr.push(obj);
		}
		if (me.body != undefined) {
			me.body.mask("计算中...", "x-mask-loading");
		}
		FHD.ajax({
					url : __ctxPath + '/formula/deptcategoryformulacalculate.f',
					params : {
						items : Ext.JSON.encode(objArr)
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
		paraobj.smids = [];
		var selections = me.getSelectionModel().getSelection();
		Ext.Array.each(selections, function(item) {
					paraobj.smids.push(item.get("id"));
				});

		if (me.body != undefined) {
			if ('0yn_y' == focus) {
				me.body.mask("关注中...", "x-mask-loading");
			} else {
				me.body.mask("取消关注中...", "x-mask-loading");
			}
		}

		FHD.ajax({
					params : {
						smItems : Ext.JSON.encode(paraobj)
					},
					url : __ctxPath + '/kpi/kpistrategymap/mergesmfocus.f',
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

	// 记分卡详细分析
	detailAnalysis : function(value) {
		var me = this;
		var para = value.split(",");
		var id = para[0];
		var name = para[1];
		var param = {
						smid : id,// 目标ID,
						navId:"deptsm",
						name:name,
						treeId:me.treeId,
						type:"departmentfolder"
				    }
		if(null!=me.detailAnaysisPanel){
			me.removePanel(me.detailAnaysisPanel);
		}
		var data = [];
		for(i=0;i<me.navData.length;i++) {
			data.push(
			me.navData[i]
			);
		};
		me.detailAnaysisPanel = Ext.create('FHD.view.kpi.homepage.SmDetailAnalysis',{
				paramObj : param,
				reRightLayout:function(p){
					me.reRightLayout(p);
				},
				navData: data,
				go:function() {						
                    me.detailAnaysisPanel.card.setActiveItem(me.tabpanel);
                    me.detailAnaysisPanel.smKpiGrid.store.load();
                    me.detailAnaysisPanel.newNav.renderHtml(me.detailAnaysisPanel.id + 'DIV',me.detailAnaysisPanel.navData);
				},
				goback:function(){
					me.detailAnaysisPanel.card.setActiveItem(me.tabpanel);
                    me.detailAnaysisPanel.smKpiGrid.store.load();
                    me.detailAnaysisPanel.newNav.renderHtml(me.detailAnaysisPanel.id + 'DIV',me.detailAnaysisPanel.navData);					
				}
			});
		me.reRightLayout(me.detailAnaysisPanel);		
	},
	// 获得选择的记录
	getSelectionRecord : function() {
		var me = this;
		var selection = null;
		var selections = me.getSelectionModel().getSelection();
		var length = selections.length;
		if (length > 0) {
			if (length >= 2) {
				Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),
						"请选择一个目标");
				return;
			} else {
				selection = selections[0];
			}
		}
		return selection;
	},

	initComponent : function() {
		var me = this;
		//var focuspanel = Ext.widget('container');
		//var statuspanel = Ext.widget('container');

		// 创建目标基本信息对象
		me.createSmBasicInfo();
		// 显示列
		me.cols = [{
			cls : 'grid-icon-column-header grid-statushead-column-header',
			header : "<span data-qtitle='' data-qtip='"
					+ FHD.locale.get("fhd.sys.planEdit.status")
					+ "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
			dataIndex : 'assessmentStatus',
			menuDisabled:true,
			sortable : true,
			width : 40,
			renderer : function(v) {
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
				return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
						+ "background-position: center top;' data-qtitle='' "
						+ "class='"
						+ v
						+ "'  data-qtip='"
						+ display
						+ "'>&nbsp</div>";
			}
		}, {
			cls : 'grid-icon-column-header grid-trendhead-column-header',
			header : "<span data-qtitle='' data-qtip='"
					+ FHD.locale.get("fhd.kpi.kpi.form.directionto")
					+ "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
			dataIndex : 'directionstr',
			sortable : true,
			width : 40,
			menuDisabled:true,
			renderer : function(v) {
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
				return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
						+ "background-position: center top;' data-qtitle='' "
						+ "class='"
						+ v
						+ "'  data-qtip='"
						+ display
						+ "'></div>";
			}
		}, {
			header : FHD.locale.get('fhd.kpi.kpi.form.name'),
			dataIndex : 'name',
			sortable : true,
			flex : 2,
			renderer : function(value, meta, record) {
				var id = record.data.id;
				var name = value;
				meta.tdAttr = 'data-qtip="' + value + '"';
				return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"
						+ me.id + "').showBasicInfo('" + id
						+ "')\" >" + value + "</a>";
			}
		}, {
			header : '所属人',
			dataIndex : 'owerName',
			sortable : true,
			flex : 2
		}, {
			header : '上级目标',
			dataIndex : 'parentName',
			sortable : true,
			flex : 2,
			renderer : function(value, meta, record) {
				var id = record.data.parentKpiId;
				var name = value;
				if (null != name) {
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"
							+ me.id
							+ "').detailAnalysis('"
							+ id
							+ ","
							+ value
							+ "')\" >" + value + "</a>";
				} else {
					return "目标库";
				}
			}
		}, {
			header : FHD.locale.get('fhd.kpi.kpi.form.assessmentValue'),
			dataIndex : 'assessmentValue',
			sortable : true,
			flex : 1.1,
			align : 'right'
		}, {
			header : FHD.locale.get('fhd.kpi.kpi.form.dateRange'),
			dataIndex : 'dateRange',
			sortable : true,
			flex : 1,
			renderer : function(v) {
				return "<div data-qtitle='' data-qtip='" + v + "'>" + v
						+ "</div>";
			}
		},
		{
            header: FHD.locale.get('fhd.common.operate'),
            dataIndex: 'operate',
            sortable: false,
            align:'center',
            width: 65,
            renderer: function (value, metaData, record, colIndex, store, view) {
            	var id = record.data.id;
                var name = record.data.name;
	            return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').detailAnalysis('" + id + "," + name + "')\" class='icon-view' data-qtip='查看'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>";
            }
        }
			/*{
			header : FHD.locale.get('fhd.sys.planMan.start'),
			xtype : 'tipcolumn',
			tips : {
				items : [statuspanel],
				renderer : function(cellIndex, rowIndex, tooltip) {
					var data = me.items.items[0].store.data.items[rowIndex].data.kpistatus;
					tooltip.setWidth(75);
					tooltip.setHeight(30);
					var htmlstr = "启用";
					if (data == "0yn_y") {
						htmlstr = "停用";
					}
					var p = statuspanel.items.items[0];
					statuspanel.remove(p);
					statuspanel.add({
								border : false,
								html : htmlstr
							});
				}
			},
			dataIndex : 'statusStr',
			sortable : false,
			flex : 0.5,
			renderer : function(v) {
				var type = me.type;
                if ("0yn_y" == v) {
                	if($ifAnyGranted('ROLE_ALL_TARGET_ENABLE')) {
                		return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').enables('" + '0yn_n' + "')\" >" + "<image src='images/icons/state_ok.gif'/>" + "</a>";                    		
                	}
                    else {
                    	return "<image src='images/icons/state_ok.gif'/>";
                    }
                }
                if ("0yn_n" == v || "" == v) {
                	if($ifAnyGranted('ROLE_ALL_TARGET_ENABLE')) {
                		 return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').enables('" + '0yn_y' + "')\" >" + "<image src='images/icons/state_error.gif'/>" + "</a>";
                		
                	}else {
                		return "<image src='images/icons/state_error.gif'/>";
                	}
                   
                }
			}
		}, {
			header : '关注',
			xtype : 'tipcolumn',
			tips : {
				items : [focuspanel],
				renderer : function(cellIndex, rowIndex, tooltip) {
					var data = me.items.items[0].store.data.items[rowIndex].data.kpifocus;
					tooltip.setWidth(75);
					tooltip.setHeight(30);
					var htmlstr = "关注";
					if (data == "0yn_y") {
						htmlstr = "取消关注";
					}
					var p = focuspanel.items.items[0];
					focuspanel.remove(p);
					focuspanel.add({
								border : false,
								html : htmlstr
							});
				}
			},
			dataIndex : 'isFocus',
			sortable : false,
			flex : 0.5,
			renderer : function(v) {
				var type = me.type;
                if ("0yn_y" == v) {
                	if($ifAnyGranted('ROLE_ALL_TARGET_ATTENTION')) {
                		 return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').focus('" + '0yn_n' + "')\" >" + "<image src='images/icons/kpi_heart.png' />" + "</a>";
                		
                	} else {
                		 return "<image src='images/icons/kpi_heart.png' />";
                	}
                   
                }
                if ("0yn_n" == v || "" == v || null == v) {
                	if($ifAnyGranted('ROLE_ALL_TARGET_ATTENTION')){
                		return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').focus('" + '0yn_y' + "')\" >" + "<image src='images/icons/kpi_heart_add.png' />" + "</a>";
                	} else {
                		 return "<image src='images/icons/kpi_heart_add.png' />";
                	}
                   
                }
			}

		}*/
		, {
			dataIndex : 'id',
			invisible : true
		}, {
			dataIndex : 'parentKpiId',
			invisible : true
		}, {
			dataIndex : 'timeperiod',
			invisible : true
		}, {
			dataIndex : 'kgrId',
			invisible : true
		}];
		me.btnAddSame = Ext.create('Ext.Button', {
					tooltip : '添加',
					authority : 'ROLE_ALL_TARGET_ADD',
					iconCls : 'icon-add',
					name : 'add_sm_same',
					handler : function() {
						me.addFun('same');
					},
					text : '添加'
				});
		me.btnAddSub = Ext.create('Ext.Button', {
					tooltip : '添加下级',
					authority : 'ROLE_ALL_TARGET_ADD',
					iconCls : 'icon-add',
					name : 'add_sm_sub',
					disabled : true,
					handler : function() {
						me.addFun('sub');
					},
					text : '添加下级'
				});
		me.btnEdit = Ext.create('Ext.Button', {
					tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.editkpi"),
					iconCls : 'icon-edit',
					authority : 'ROLE_ALL_TARGET_EDIT',
					disabled : true,
					handler : function() {
						me.editFun();
					},
					text : FHD.locale.get("fhd.common.edit")
				});
		me.btnDel = Ext.create('Ext.Button', {
					tooltip : FHD.locale.get("fhd.kpi.kpi.toolbar.delkpi"),
					iconCls : 'icon-del',
					authority : 'ROLE_ALL_TARGET_DELETE',
					disabled : true,
					handler : function() {
						me.delFun();
					},
					text : FHD.locale.get("fhd.common.delete")
				});
		Ext.apply(me, {
					url : me.queryUrl,
					storeAutoLoad:false,
					reRightLayout : me.reRightLayout,
					border : false,
					checked : true, // 复选框
					cols : me.cols, // cols:为需要显示的列
					extraParams : {
						id : me.paramObj.smid,
						year : FHD.data.yearId,
						month : FHD.data.monthId,
						quarter : FHD.data.quarterId,
						week : FHD.data.weekId,
						eType : FHD.data.eType,
						isNewValue : FHD.data.isNewValue,
						dataType : me.paramObj.dataType
					},
					tbarItems : [me.btnAddSame, me.btnAddSub,
							me.btnEdit,  me.btnDel,  {
								name : 'sm_start',
								btype : 'op',
								tooltip : FHD.locale
										.get('fhd.sys.planMan.start'),
								iconCls : 'icon-plan-start',
								authority : 'ROLE_ALL_TARGET_ENABLE',
								handler : function() {
									me.enables("0yn_y");
								},
								disabled : true,
								text : FHD.locale.get('fhd.sys.planMan.start')
							}, {
								name : 'sm_stop',
								btype : 'op',
								authority : 'ROLE_ALL_TARGET_ENABLE',
								tooltip : FHD.locale
										.get('fhd.sys.planMan.stop'),
								iconCls : 'icon-plan-stop',
								handler : function() {
									me.enables("0yn_n");
								},
								disabled : true,
								text : FHD.locale.get('fhd.sys.planMan.stop')
							}, {
								name : 'sm_focus',
								btype : 'op',
								tooltip : '关注',
								authority : 'ROLE_ALL_TARGET_ATTENTION',
								iconCls : 'icon-kpi-heart-add',
								handler : function() {
									me.focus('0yn_y');
								},
								disabled : true,
								text : '关注'
							},

							{
								name : 'sm_noFocus',
								btype : 'op',
								tooltip : '取消关注',
								authority : 'ROLE_ALL_TARGET_ATTENTION',
								iconCls : 'icon-kpi-heart-delete',
								handler : function() {
									me.focus('0yn_n');
								},
								disabled : true,
								text : '取消关注'
							}, {
								tooltip : FHD.locale
										.get('fhd.formula.calculate'),
								btype : 'op',
								iconCls : 'icon-calculator',
								name : 'sm_calc',
								authority : 'ROLE_ALL_TARGET_CALCULATE',
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
	/**
	 * 重新加载列表数据
	 */
	reloadData : function() {
		var me = this;
		me.queryUrl = __ctxPath + "/myfolder/finddeptrelasmsc.f";// 树查询url
		if (me.paramObj != undefined) {
			me.store.proxy.url = me.queryUrl;
			me.store.proxy.extraParams.dataType = me.paramObj.dataType;
			me.store.load();
		}
	},
	showBasicInfo: function(id) {
		var me = this;
	    me.smBasicInfoForm = Ext.create('FHD.view.kpi.cmp.sm.SmBasicInfoForm', {});
        var paramObj = {
            smid: id //目标ID
        };
        me.smBasicInfoForm.initParam(paramObj);
        me.smBasicInfoForm.reloadData();
        me.window = Ext.create('FHD.ux.Window', {
            title: '目标基本信息',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible : true
        });
        me.window.show(); 
        me.window.add(me.smBasicInfoForm);    
     
	}
})