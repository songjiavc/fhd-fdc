Ext.define('FHD.view.kpi.bpm.finishgather.KpiFinishResultInput', {
    extend: 'FHD.ux.EditorGridPanel',
    requires: ['FHD.ux.TipColumn'],
    extraParams: {},
    layout: 'fit',
    border: false,
    
    showToolTip : function(component){
		var me = this;
		var htmldesc = '采集说明:</br>';
		var selection = me.getSelectionModel().getSelection();
		if(null!=selection&&selection.length>0){
			var selectedRow = selection[0];
			htmldesc = htmldesc + selectedRow.get("gatherDesc");
		}
		var toolTipContainer = Ext.create('Ext.tip.ToolTip', {
	        target: component.el,
	        name : 'toolTipContainer',
	        anchor: 'left',
	        autoHide: true,
	        width : 150,
	        height : 200,
	        dismissDelay: 15000,
	        html : htmldesc
	    }).show();
	},
	
    initComponent: function () {
        var me = this;
        me.bpmStart = false;
        me.queryUrl = __ctxPath + '/kpi/kpi/showFinishGatherList.f';
        me.saveUrl = __ctxPath + '/kpi/kpi/saveFinishGatherGatherList.f';
        me.type = 'bpm';
        var memotippanel = Ext.create('FHD.view.kpi.cmp.kpi.memo.MemoTipPanel');
        if (me.isEdit) {
            me.editColumn = {
                header: '实际值',
                dataIndex: 'finishValue',
                sortable: true,
                flex: 1,
                editor: {
                    xtype: 'numberfield',
                    allowBlank: true,
                    minValue: 0,
                    maxValue: 100000,
                    listeners : {
						focus : function(component){
							me.showToolTip(component);
						}
					}
                },
                renderer: function (value, metaData, record, colIndex, store, view) {
                    metaData.tdAttr = 'style="background-color:#FFFBE6"';
                    return value;
                }

            };
        } else {
            me.editColumn = {
                header: '实际值',
                dataIndex: 'finishValue',
                sortable: true,
                flex: 1
            };
        }

        // 显示列
        me.cols = [{
                header: 'id',
                dataIndex: 'id',
                sortable: true,
                flex: 1,
                invisible: true
            }, {
                header: 'kpiId',
                dataIndex: 'kpiId',
                sortable: true,
                flex: 1,
                invisible: true
            }, {
                header: 'kgrId',
                dataIndex: 'kgrId',
                sortable: true,
                flex: 1,
                invisible: true
            }, {
                header: 'memoStr',
                dataIndex: 'memoStr',
                sortable: true,
                flex: 1,
                invisible: true
            }, {
                header: 'gatherDesc',
                dataIndex: 'gatherDesc',
                sortable: false,
                flex: 1,
                invisible: true
            },{
                header: '指标名称',
                dataIndex: 'kpiName',
                sortable: true,
                flex: 1.5
            }, {
                header: '目标值',
                dataIndex: 'targetValue',
                sortable: true,
                flex: 1
            },
            me.editColumn, {
                header: '单位',
                dataIndex: 'units',
                sortable: true,
                flex: 1
            }, {
                header: '时间维度',
                dataIndex: 'timePeriod',
                sortable: true,
                flex: 1
            }, {
                cls: 'grid-icon-column-header grid-statushead-column-header',
                header: "<span data-qtitle='' data-qtip='" + '前期状态' + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
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
                header: "注释", //添加备注信息列 
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
                width: 50,
                renderer: function (v, rowIndex, cellIndex) {
                    var type = me.type;
                    var kpiid = cellIndex.data.kpiId;
                    var kgrid = cellIndex.data.kgrId;
                    var name = cellIndex.data.kpiName;
                    var dateRange = cellIndex.data.timePeriod;
                    var memoTitle = "注释——" + name + "(" + dateRange + ")";
                    var memohref = "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showMemoEditWindow('" + name + "," + kgrid + "," + memoTitle + "," + kpiid + "')\" >";
                    var memoSpan;
                    if ("1" == v) {
                        var memoStr = cellIndex.data.memoStr;
                        var str = memoStr.split('!@#$');
                        if (str[1] == '0alarm_startus_h') {
                            if (type) {
                                memoSpan =  memohref + "<image src='"+__ctxPath+"/images/icons/icon_comment_importance_high.gif'   />" + "</a>";
                            } else {
                                memoSpan = "<image src='"+__ctxPath+"/images/icons/icon_comment_importance_high.gif'   />";
                            }

                        }
                        if (str[1] == '0alarm_startus_l') {
                            if (type) {
                                memoSpan =  memohref + "<image src='"+__ctxPath+"/images/icons/icon_comment_importance_low.gif'   />" + "</a>";
                            } else {
                                memoSpan =  "<image src='"+__ctxPath+"/images/icons/icon_comment_importance_low.gif'   />";
                            }

                        }
                        if (str[1] == '0alarm_startus_n') {
                            if (type) {
                                memoSpan =  memohref + "<image src='"+__ctxPath+"/images/icons/icon_note.gif'   />" + "</a>";
                            } else {
                                memoSpan =  "<image src='"+__ctxPath+"/images/icons/icon_note.gif'   />";
                            }

                        }

                    } else {
                        if (type) {
                            memoSpan =  memohref + "<image src='"+__ctxPath+"/images/icons/icon_noreport_properties.gif'  />" + "</a>";
                        } else {
                            memoSpan =  "<image src='"+__ctxPath+"/images/icons/icon_noreport_properties.gif'  />"
                        }
                    }                  
                    return memoSpan;
                }
            },
            {
            header: "操作",
            dataIndex: 'operate',
            sortable: false,
            width: 65,
            renderer: function (v, rowIndex, cellIndex) {
            
                var kpiid = cellIndex.data.kpiId;
                var dateRange = cellIndex.data.dateRange;
                var name = cellIndex.data.kpiName;
                var memoTitle = "注释——" + name + "(" + dateRange + ")";
                var kgrid = cellIndex.data.kgrId;                   
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').gatherResultFun('" + name + "," + kgrid + "," + memoTitle + "," + kpiid + "')\" class='icon-view' data-qtip='查看'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>"
            }
           },
             {
                header: '最大值',
                dataIndex: 'maxValue',
                sortable: true,
                flex: 1,
                hidden: true
            },
            {
                header: '最小值',
                dataIndex: 'minValue',
                sortable: true,
                flex: 1,
                hidden: true
            }
        ];
        Ext.apply(me, {
            multiSelect: true,
            border: false,
            rowLines: true, // 显示横向表格线
            columnLines: true,
            checked: false, // 复选框
            autoScroll: true,
            cols: me.cols, // cols:为需要显示的列
            extraParams: me.extraParams,
            url: me.queryUrl,
            pagable: false
        });

        me.callParent(arguments);
    },
    last: function (promptFlag) {
        var me = this;
        var jsobj = [];
        var kpiName = null;
        var maxValue = null;
        var minValue = null;
        var finishValue = null;
        me.getSelectionModel().selectAll();
        var selection = me.getSelectionModel().getSelection();
        for (var i = 0; i < selection.length; i++) {
            var kpi = {};
            kpi.id = selection[i].get('id');
            kpi.kpivalue = selection[i].get('finishValue');
            if(kpi.kpivalue==null){
            	continue;
            }
            kpiName = selection[i].get('kpiName'); 
            maxValue = selection[i].get('maxValue'); 
            minValue = selection[i].get('minValue'); 
            finishValue = selection[i].get('finishValue'); 
            if(!me.validateInputValue(finishValue,maxValue,minValue,kpiName)) {
            	return false;
            }
            jsobj.push(kpi);
        }
        FHD.ajax({
            url: me.saveUrl,
            async: false,
            params: {
                param: Ext.JSON.encode(jsobj),
                bpmStart: me.up('panel').bpmStart,
                examinePerson: me.pcontainer.selector.getValue(),
                executionId: me.executionId
            },
            callback: function (data) {
                if (data && data.success) {
                	if(promptFlag){
	                    Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.operateSuccess'));
                	}
                    me.isLast = true;
                } else {
                    me.isLast = false;
                }
                me.store.commitChanges();
            }
        });
        return me.isLast;
    },
    gatherResultFun: function (obj) {
        var me = this;
        var paraobj = obj.split(",");
        var name = paraobj[0];
        var kgrid = paraobj[1];
        var memoTitle = paraobj[2];
        var kpiid = paraobj[3];
        PARAM.name = name;
        PARAM.kpiname = name;
        PARAM.kgrid = kgrid;
        PARAM.memoTitle = memoTitle;
        PARAM.kpiid = kpiid;
        me.mainPanel = Ext.create('FHD.view.kpi.cmp.kpi.result.MainPanel', {
            pcontainer: me,
            isChartOnly:true,
            isGather: true,
            goback: function () {
                if (me.window) {
                    me.window.close();
                }
            }
        });
		
        me.window = Ext.create('FHD.ux.Window', {
            title: name,
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible : true
        });
        me.window.show();
        var detailPanel = me.mainPanel.load(PARAM);
        me.window.add(detailPanel);

    },
    validateInputValue: function(value,maxValue,minValue,kpiname) {
    	var msg = '指标[' + kpiname + ']实际值' ;
    	var result = true;
    	if(value) {
    		if(maxValue && value > maxValue) {
    			msg += '不能大于';
    			msg +=  maxValue;
    			result = false;
    		}
    		if(minValue && value < minValue) {
    			msg += '不能小于';
    			msg +=  minValue;
    			result = false;
    		}
    	}
    	if(!result){
    	   Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), msg);
    	}    	
    	return result;
    },
    showMemoEditWindow:function(obj) {
    	//展示选择采集结果对应的注释信息
    	var me = this;
        var paraobj = obj.split(",");
        var name = paraobj[0];
        var kgrid = paraobj[1];
        var memoTitle = paraobj[2];
        var kpiid = paraobj[3];
	    me.memomainpanel = Ext.create('FHD.view.kpi.cmp.kpi.memo.MemoMainPanel',
		{   
			title:'注释',
	        split: true,
	        kgrid: kgrid,
	        layout:{
     	    	type:'hbox',
                align:'stretch'
     	    }
	    });       
		me.memomainpanel.getData(kgrid);
		me.memomainpanel.kgrid = kgrid;
		me.memomainpanel.setTitle(memoTitle);
		me.memoEditWindow = Ext.create('Ext.window.Window', {
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible : true,
            	layout : 'fit',
				collapsible : true,
				modal : true,
				draggable : false,
				height: Ext.getBody().getHeight() * 0.6,
			width : Ext.getBody().getWidth() * 0.65
        });
        me.memoEditWindow.on('close',function(panel, eOpts) {
            var record=me.getSelectionModel().getSelection();            
            FHD.ajax({
            async: false,
            url: __ctxPath + '/kpi/kpi/findmemoinfobykgrid.f',
            params: {
                id: kgrid
            },
            callback: function (data) {
            	 if(data.memoStr) {
            	 	record[0].set('memoStr',data.memoStr);
            	 } else {
            	 	record[0].set('memoStr',"");
            	 }           	 
                if(data.memoStr) {
                 record[0].set('isMemo',"1");	
                } else{
                 record[0].set('isMemo',"");	
                }            	
            }
            })
         });
       me.memoEditWindow.show();
       me.memoEditWindow.add(me.memomainpanel);
    }
})