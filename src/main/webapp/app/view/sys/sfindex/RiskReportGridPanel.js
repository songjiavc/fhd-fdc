Ext.define('FHD.view.sys.sfindex.RiskReportGridPanel',{
    extend: 'FHD.ux.GridPanel',
    initComponent: function () {
    	var me = this;
    	 var cols = [{
             header: "id",
             dataIndex: 'id',
             hidden: true
         }, {
             header: "风险分类",
             dataIndex: 'riskName',
             sortable: false,
             flex: 1.5,
             hidden:false,
             menuDisabled: true,
             renderer: function(v,rowIndex,cellIndex) {   
             	 var riskId = cellIndex.data.id;            	 
            	 return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskBasicInfo('" + riskId + "')\" >" + v + "</a>";
             }
         },
          {
            header: "分值",
            dataIndex: 'riskScore',
            sortable: true,
            flex: 0.5,
            menuDisabled: true,
            align: 'center'
        },{
            header: "状态",
            dataIndex: 'riskStatus',
            sortable: true,
            width: 40,
            menuDisabled: true,
            renderer: function (v) {
                var color = "";
                var display = "";
                
                if (v == "icon-ibm-symbol-4-sm") {
                    color = "icon-flag-red";
                    display = FHD.locale.get("fhd.alarmplan.form.hight");
                } else if (v == "icon-ibm-symbol-6-sm") {
                    color = "icon-flag-green";
                    display = FHD.locale.get("fhd.alarmplan.form.low");
                } else if (v == "icon-ibm-symbol-5-sm") {
                    color = "icon-flag-yellow";
                    display = FHD.locale.get("fhd.alarmplan.form.min");
                } else if (v == "icon-ibm-symbol-safe-sm") {
                	color = "icon-flag-blue";
                    display = "安全";
                } else {
                	return "";
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                    "background-position: center top;' data-qtitle='' " +
                    "class='" + color + "'  data-qtip='" + display + "'>&nbsp</div>";
            }

        },
         {
             header: "风险指标",
             dataIndex: 'kpiName',
             sortable: true,
             flex: 2.5,
             menuDisabled: true
         }/*, {
             header: "权重",
             dataIndex: 'kpiWeight',
             sortable: false,
             flex: 0.5,
             menuDisabled: true,
             align: 'center'
         }, {
             header: "所属部门",
             dataIndex: 'deptName',
             sortable: true,
             flex: 1,
             menuDisabled: true
         }, {
             header: "频率",
             dataIndex: 'kpiFrequency',
             sortable: false,
             flex: 0.8,
             menuDisabled: true,
             align: 'right'
         }, {
             header: "目标值",
             dataIndex: 'targetValue',
             sortable: true,
             flex: 1,
             menuDisabled: true,
	         align: 'right'
         }, {
             header: "完成值",
             dataIndex: 'finishValue',
             sortable: true,
             flex: 1,
             menuDisabled: true,
	         align: 'right'
         }, {
             header: "评估值",
             dataIndex: 'assessMentValue',
             sortable: true,
             flex: 1,
             menuDisabled: true,
	         align: 'right'
         }, {
             header: "状态",
             dataIndex: 'status',
             sortable: true,
             width: 40,
             menuDisabled: true,
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
             header: "时间",
             dataIndex: 'timePeriod',
             sortable: false,
             menuDisabled: true,
             flex: 1
         }*/];
         Ext.apply(me, {
             cols: cols,
             checked: false,
             url: __ctxPath + '/kpi/report/findAllRiskKpiDetail.f',
             border: false,
             checked: false,
             pagable: true,
             rowLines: true,
             columnLines: true,
             searchable: false,
             header:false,
             storeAutoLoad: false,
             extraParams: {
             	 year: FHD.data.yearId,
                 month: FHD.data.monthId,
                 quarter: FHD.data.quarterId,
                 week: FHD.data.weekId,
                 isNewValue: FHD.data.isNewValue,
                 eType: FHD.data.eType,
                 isGroup: false
             },
             viewConfig: {
                 getRowClass: function (record, rowIndex, rowParams, store) {
                     return "row-w";
                 }
             }
         });
         me.callParent(arguments);
         me.on('afterlayout', function () {
             Ext.create('FHD.view.kpi.cmp.GridMergeCell').mergeCells(me, [2, 3,4,7]);
         });
    },
    reLoadData: function () {
        var me = this;        
        me.store.proxy.extraParams.year = FHD.data.yearId;
        me.store.proxy.extraParams.month = FHD.data.monthId;
        me.store.proxy.extraParams.quarter = FHD.data.quarterId;
        me.store.proxy.extraParams.week = FHD.data.weekId;
        me.store.proxy.extraParams.isNewValue = FHD.data.isNewValue;
        me.store.proxy.extraParams.eType = FHD.data.eType;
        me.store.load();
    },
    exportExcel: function() {
        var me=this;
    	me.headerDatas = [];
    	me.timePeriod = {};
    	me.timePeriod.year = FHD.data.yearId;
        me.timePeriod.month = FHD.data.monthId;
        me.timePeriod.quarter = FHD.data.quarterId;
        me.timePeriod.week = FHD.data.weekId;
        me.timePeriod.isNewValue = FHD.data.isNewValue;
        me.timePeriod.eType = FHD.data.eType;
    	var items = me.columns;
		Ext.each(items,function(item){
			if(!item.hidden && item.dataIndex != ''){
				var value = {};
				value['dataIndex'] = item.dataIndex;
	        	value['text'] = item.text;
	        	me.headerDatas.push(value);
			}
		});
		window.location.href = "kpi/report/exportriskkpireport.f?id="+""+"&exportFileName="+""+
								"&sheetName="+""+"&headerData="+Ext.encode(me.headerDatas)+"&timePeriod="+Ext.encode(me.timePeriod);
    },
    showRiskBasicInfo: function(id) {
    	var me = this;
	    me.riskBasicInfoForm = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail');
        me.riskBasicInfoForm.reloadData(id);
        me.window = Ext.create('FHD.ux.Window', {
            title: '指标标基本信息',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible : true
        });
        me.window.show(); 
        me.window.add(me.riskBasicInfoForm);  
    }
})