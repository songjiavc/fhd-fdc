Ext.define('FHD.view.kpi.cmp.kpi.KpiGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.fhdkpigrid',
    requires: ['FHD.ux.TipColumn'],
    url: '', // kpi列表Url地址
    cols: [], // kpi列表显示的列
    isDisplayPreResult: false,
    tbarItems: [], // kpi列表上方工具条
    border: true, // 默认不显示border
    checked: true, // 是否可以选中
    nameLink: true,
    type: '',
    xmlMap: null, //针对不同指标的效果图map类型(KEY:指标ID,VALUE:指标效果图)
    jsonMap:null,
    isDisplayWeight: true,
    _insert: function (index, item) {
        if (index < 0) return;
        if (index > this.cols.length) return;
        for (var i = this.cols.length - 1; i >= index; i--) {
            this.cols[i + 1] = this.cols[i];
        }
        this.cols[index] = item;
    },

    //获取当前年份
    getYear: function () {
        var myDate = new Date();
        var year = myDate.getFullYear();
        return year;
    },
    onDestroy:function(){
    	if(this.chartPanel){
    		this.chartPanel.destroy();
    	}
    	if(this.memotippanel){
    		this.memotippanel.destroy();
    	}
    	this.callParent(arguments);
    	
    },

    initComponent: function () {
        var me = this;
//        me.chartPanel = Ext.create('FHD.ux.FusionChartPanel', {
//            border: false,
//            width: 310,
//            height: 230,
//            chartType: 'MSColumn2D',
//            xmlData: ''
//        });
        me.chartPanel = Ext.create('FHD.view.kpi.cmp.chart.ColumnChartPanel', {
            border: false,
            width: 350,
            height: 230
        });
        //	备注panel	
        me.memotippanel = Ext.create('FHD.view.kpi.cmp.kpi.memo.MemoTipPanel');

        //var belongKpiContainer = Ext.widget('container');

        me.cols = [];
        var assessmentStatusCol = {
            cls: 'grid-icon-column-header grid-statushead-column-header',
            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'assessmentStatus',
            sortable: true,
            menuDisabled:true,
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
        };
        me.cols.push(assessmentStatusCol);

        var directionstrCol = {
            cls: 'grid-icon-column-header grid-trendhead-column-header',
            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'directionstr',
            sortable: true,
            menuDisabled:true,
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
        };
        me.cols.push(directionstrCol);

        var isMemoCol = {
            header: "注释", //添加备注信息列 by haojing
            xtype: 'tipcolumn',
            tips: {
                items: [me.memotippanel],
                renderer: function (cellIndex, rowIndex, tooltip) {
                    var data = me.items.items[0].store.data.items[rowIndex].data.memoStr;
                    me.memotippanel.renderData(data, tooltip);
                }
            },
            dataIndex: 'isMemo',
            sortable: false,
            flex: 0.5,
            renderer: function (v, rowIndex, cellIndex) {
                var type = me.type;
                var kpiid = cellIndex.data.id;
                var kgrid = cellIndex.data.kgrId;
                var name = cellIndex.data.name;
                var dateRange = cellIndex.data.dateRange;
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
        };

        me.cols.push(isMemoCol);

        var nameCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.name'),
            xtype: 'tipcolumn',
           
            tips: {
                width: 350,
                height: 230,
                maxWidth : 1000,
                items: [me.chartPanel],
                padding: '0 0 0 0',
                margin: '0 0 0 0',
                layout : 'fit',
                renderer: function (cellIndex, rowIndex, tooltip) {
                   // me.cols[3].tips.items[0].loadXMLData(me.xmlMap[me.items.items[0].store.data.items[rowIndex].data.id]);
                   if(me.jsonMap) { 
                   	 var  jsonData  = me.jsonMap[me.items.items[0].store.data.items[rowIndex].data.id];
                   	 var  maxValue =  me.maxMap[me.items.items[0].store.data.items[rowIndex].data.id];
                   	 var  minValue =  me.minMap[me.items.items[0].store.data.items[rowIndex].data.id];
                   	 me.cols[3].tips.items[0].loadData(jsonData,maxValue,minValue);                  	
                   }                 
                }
            },
            dataIndex: 'name',
            sortable: true,
            flex: 3,
            renderer: function (v, rowIndex, cellIndex) {
                var kpiid = cellIndex.data.id;
                var dateRange = cellIndex.data.dateRange;
                var name = cellIndex.data.name;
                var memoTitle = "注释——" + name + "(" + dateRange + ")";
                var type = me.type;
                if (type) {
                    var kgrid = cellIndex.data.kgrId;
                    return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showKpiBasicInfo('" + kpiid + "')\" >" + v + "</a>";
                } else {
                    return v;
                }
            }
        };
        me.cols.push(nameCol);

        
		if (me.isDisplayWeight) {
	        var eweightCol = {
	            header: '权重',
	            dataIndex: 'eweight',
	            sortable: false,
	            flex: 1,
	            align: 'right',
	            menuDisabled: true
	        };
	
	        me.cols.push(eweightCol);
		}
        
		
		var gatherFrequenceDictCol = {
            header: '频率',
            dataIndex: 'gatherFrequenceDict',
            sortable: false,
            flex: 0.8,
            align: 'right',
            menuDisabled: true
        };
        me.cols.push(gatherFrequenceDictCol);

        var finishValueCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.finishValue'),
            dataIndex: 'finishValue',
            sortable: true,
            flex: 1.1,
            align: 'right'
        };
        me.cols.push(finishValueCol);


        var targetValueCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.targetValue'),
            dataIndex: 'targetValue',
            sortable: true,
            flex: 1.1,
            align: 'right'
        };
        me.cols.push(targetValueCol);

        
        /*var unitsStrCol = {
            header: '单位',
            dataIndex: 'unitsStr',
            sortable: true,
            flex: 0.8,
            align: 'right'
        };
        me.cols.push(unitsStrCol);*/
        
        var assessmentValueCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.assessmentValue'),
            dataIndex: 'assessmentValue',
            sortable: true,
            flex: 1.1,
            align: 'right'
        };

        me.cols.push(assessmentValueCol);

        
        

        var dateRangeCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.dateRange'),
            dataIndex: 'dateRange',
            sortable: true,
            flex: 1.2,
            renderer: function (v) {
                return "<div data-qtitle='' data-qtip='" + v + "'>" + v + "</div>";
            }
        };

        me.cols.push(dateRangeCol);
        
        var operateCol = {
            header: "操作",
            dataIndex: 'operate',
            sortable: false,
            width: 65,
            renderer: function (v, rowIndex, cellIndex) {
            
                var kpiid = cellIndex.data.id;
                var dateRange = cellIndex.data.dateRange;
                var name = cellIndex.data.name;
                var memoTitle = "注释——" + name + "(" + dateRange + ")";
                var kgrid = cellIndex.data.kgrId;                   
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').gatherResultFun('" + name + "," + kgrid + "," + memoTitle + "," + kpiid + "')\" class='icon-view' data-qtip='查看'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看</a>"
            }
        };
        if(me.type) {
           me.cols.push(operateCol);	
        }

        var timeperiodHidden = {
            dataIndex: 'timeperiod',
            invisible: true
        };
        me.cols.push(timeperiodHidden);

        var memoStrHidden = {
            dataIndex: 'memoStr',
            invisible: true
        };
        me.cols.push(memoStrHidden);

        var kgrIdHidden = {
            dataIndex: 'kgrId',
            invisible: true
        };

        me.cols.push(kgrIdHidden);

        if (me.isDisplayPreResult) {
            var preCol = {
                header: FHD.locale.get('fhd.kpi.kpi.form.prefinishValue'),
                dataIndex: 'preFinishValue',
                sortable: true,
                flex: 0.8,
                align: 'right'
            };
            var preYearCol = {
                header: FHD.locale.get('fhd.kpi.kpi.form.preYearfinishValue'),
                dataIndex: 'preYearFinishValue',
                sortable: true,
                flex: 0.8,
                align: 'right'
            };
            me._insert(6, preCol);
            me._insert(7, preYearCol);
        }
		/*所属指标列隐藏*/
        /*if (!me.isDisplayPreResult && me.type == "scorecardkpigrid") {
            var belongKpiCol = {
            	xtype: 'tipcolumn',
                tips: {
                    items: [belongKpiContainer],
                    renderer: function (cellIndex, rowIndex, tooltip) {
                        var data = me.items.items[0].store.data.items[rowIndex].data.belongKpi;
                        tooltip.setWidth(75);
                        tooltip.setHeight(30);
                        var htmlstr = "关联指标";
                        if (data == "1") {
                            htmlstr = "创建指标";
                        }
                        var p = belongKpiContainer.items.items[0];
                        belongKpiContainer.remove(p);
                        belongKpiContainer.add({
                            border: false,
                            html: htmlstr
                        });
                    }
                },
                header: FHD.locale.get('fhd.kpi.grid.belongKpi'),
                dataIndex: 'belongKpi',
                sortable: false,
                flex: 1,
                renderer: function (v) {
                    var text = "";
                    if ("1" == v) {
                        return "<image src='images/icons/icon_diagram.gif'  />";
                    } else if ("0" == v) {
                    	return "<image src='images/icons/icon_diagram_def.gif'  />"
                    }
                }
            }
            me._insert(7, belongKpiCol);
        }*/


        Ext.apply(me, {
            cols: me.cols,
            url: me.url,
            storeAutoLoad:false,
            tbarItems: me.tbarItems,
            border: me.border,
            checked: me.checked
        });

        me.callParent(arguments);
        me.store.on('refresh', function (store, options) {
            if (store.data.length != 0) {
                if (me.body != undefined) {
                    me.body.mask("读取中...", "x-mask-loading");
                }
            }

            var kpiId = [];
            var yearId = "";
            var yearIdTemp = new Array();
            for (var i = 0; i < store.data.length; i++) {
            	kpiId.push(store.data.items[i].data.id);
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
                        if (me.store.data.length != 0) {
                            if (me.body != undefined) {
                                me.body.unmask();
                            }
                        }

                    }
                }
            });
        });
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
            title: '指标基本信息',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible : true
        });
        me.window.show(); 
        me.window.add(me.kpiBasicInfoForm);  
    }
});