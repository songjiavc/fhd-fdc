/**
 * @author zhengjunxiang
 * 权限控制在风险库维护中
 */
Ext.define('FHD.view.risk.cmp.risk.RiskEventAddManagerApproveGrid', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.riskeventaddmanagerapprovegrid',
    requires: [
        'FHD.view.risk.assess.utils.GridCells'
    ],

    //按不同的状态查询风险事件. 待审批：waitingApprove；带归档：waitingArchive
    state: 'waitingArchive', 

    /**
     * private 内部属性
     */
    queryUrl: '/risk/flow/findRiskEventByArchiveState',

    showRiskEventDetailWindow:function(riskId){
    	var me = this;
    	var detailForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
        	riskId:riskId
		});
		me.win = Ext.create('Ext.window.Window', {
    		autoScroll:true,
    		title:'风险详细信息',
    		width:800,
    		height:400,
        	items:[detailForm]
		});
    	me.win.show();
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
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetailWindow('" + id + "')\" >" + value + "</a>";
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
            header: "趋势",
            dataIndex: 'etrend',
            sortable: true,
            width: 60,
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
            header: "风险水平",
            dataIndex: 'assessementStatus',
            sortable: true,
            width: 65,
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
        	url:__ctxPath + me.queryUrl,
        	extraParams:{
        		state: me.state
        	},
        	searchable:false,
            border: false,
            columnLines: true,
            cols: cols,
            tbarItems: [{
                    btype: 'edit',
                    name: 'editbutton',
                    disabled: true,
                    handler: function () {
                        me.editFun();
                    }
                }]
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

    reloadData: function () {
        var me = this;
        me.grid.store.load();
    },

    initParams: function (state) {
        var me = this;
        me.state = state;
    },
    
    /**
     * 编辑
     */
    editFun: function () {
        var me = this;
        var selections = me.grid.getSelectionModel().getSelection();
        if (selections.length > 1 || selections.length < 1) {
        	FHD.notification('请选择一条记录.',FHD.locale.get('fhd.common.prompt'));
        	return;
        }
        
        var riskId = selections[0].data.id;
        //弹出修改窗口
    	if(!me.editFormWin){
    		me.editForms = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
	    		type:'re',	//如果是re,上级风险只能选择叶子节点
	    		border:false,
	    		callback:function(){
	    			me.editFormWin.body.unmask();
	    			me.editFormWin.hide();
	    			me.reloadData();
	    		}
	    	});
    		me.editFormWin = new Ext.Window({
				layout:'fit',
				iconCls: 'icon-show',//标题前的图片
				modal:true,//是否模态窗口
				collapsible:true,
				closeAction:'hide',
				width:900,
				height:463,
				title : '风险事件修改',
				maximizable:true,//（是否增加最大化，默认没有）
				constrain:true,
				items : [me.editForms],
				buttons: [
							{
								text: '保存',
								handler:function(){
									me.editFormWin.body.mask("保存中...","x-mask-loading");
									me.editForms.save(me.editForms.callback);
								}
							},
			    			{
			    				text: '关闭',
			    				handler:function(){
			    					me.editFormWin.close();
			    				}
			    			}
			    		]
			});
    	}
	    me.editForms.reloadData(riskId);//加载风险信息
		me.editFormWin.show();
    }
});