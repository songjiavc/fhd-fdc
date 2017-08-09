/**
 * 报表风险列表
 * 
 * @author ZJ
 */
Ext.define('FHD.view.report.risk.ReportRiskGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.reportriskgridpanel',

    /**
	 * public
	 * 接口属性
	 */
    value : '',
	queryUrl: __ctxPath + '/risk/risk/showreportriskgrid.f',
   
    // 初始化方法
    initComponent: function() {
		var me = this;
		var cols = [{
            dataIndex: 'id',
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
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetailContainer('" + id + "')\" >" + value + "</a>";
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
		
		Ext.apply(me, {
			cols:cols,
		    border: false,
		    checked: false,
		    pagable : true,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : true,
            tbarItems:[{
    			text : '导出excel',
    			tooltip: '导出excel',
    			iconCls : 'icon-ibm-action-export-to-excel',
    			handler:function(){
    				 me.exportChart();
    			}
			}]
		});

		me.callParent(arguments);
		
		me.on('afterlayout',function(){ 
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [1]);
        });
		
    },
    
    reloadData:function(companyid,orgid,type){
    	var me = this;
    	me.companyid = companyid;
    	me.orgid = orgid;
    	me.type = type;
    	me.store.proxy.url = me.queryUrl;
    	me.store.proxy.extraParams = {
    		companyid : companyid,
    		orgid : orgid,
    		type : type
    	};
    	me.store.load();
    },
	
	showRiskEventDetailContainer : function(id) {
		var me = this;
		// 风险事件基本信息
		riskEventDetailForm = Ext.create(
				'FHD.view.risk.cmp.form.RiskFullFormDetail', {
					title : '基本信息',
					showbar : true,
					border : false,
					goback : function() {
						window.close();
					}
				});

		//风险图形分析的页签
		me.riskGraph = Ext.create('FHD.view.comm.graph.GraphRelaRiskPanel',{
			title:'图形分析'
		});
		//根据左侧选中节点，初始化数据
		me.riskGraph.initParam({
             riskId:id
    	});
		me.riskGraph.reloadData();
        
		// 风险事件历史记录
		riskEventHistoryGrid = Ext.create(
				'FHD.view.risk.cmp.risk.RiskHistoryGrid', {
					title : '历史记录',
					type : 'riskevent',
					showbar : false,
					border : false
				});

		riskEventTabPanel = Ext.create(
				"FHD.ux.layout.treeTabFace.TreeTabTab", {
					items : [riskEventDetailForm,me.riskGraph,
							riskEventHistoryGrid]
				});
		riskEventDetailContainer = Ext.create(
				'FHD.ux.layout.treeTabFace.TreeTabContainer', {
					border : false,
					navHeight : '0',
					tabpanel : riskEventTabPanel,
					flex : 1
				});
		var window = Ext.create('FHD.ux.Window',{
			title:'风险事件详情',
			maximizable: true,
			modal:true,
			width:800,
			height: 500,
			collapsible:true,
			autoScroll : true,
			items : riskEventDetailContainer
		}).show();
		riskEventDetailForm.reloadData(id);
		riskEventHistoryGrid.reloadData(id);
	},
	
	exportChart : function(){
		var me=this;
    	me.headerDatas = [];
    	var items = me.columns;
		Ext.each(items,function(item){
			if(!item.hidden && item.dataIndex != ''){
				var value = {};
				value['dataIndex'] = item.dataIndex;
	        	value['text'] = item.text;
	        	me.headerDatas.push(value);
			}
		});
		window.location.href = "risk/risk/exportreportriskgrid.f?id="+""+"&companyid="+me.companyid+"&orgid="+me.orgid+"&type="+me.type+"&exportFileName="+""+
								"&sheetName="+""+"&headerData="+Ext.encode(me.headerDatas)+"&style="+"event";
	}
    
});