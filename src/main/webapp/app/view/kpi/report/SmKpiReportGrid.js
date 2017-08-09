Ext.define('FHD.view.kpi.report.SmKpiReportGrid', {
    extend: 'FHD.ux.GridPanel',
	
	initComponent : function() {
		var me = this;
		me.cols = [];
		// 指标名称
		var nameCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.name'),
            dataIndex: 'name',
            sortable: false,
            flex: 3,
            menuDisabled: true,
            renderer: function(v) {
            	 return "<div data-qtitle='' data-qtip='" + v + "'>" + v + "</div>";
            }
        };
        me.cols.push(nameCol);
        // 权重
        var weightCol = {
	            header: '权重',
	            dataIndex: 'eweight',
            sortable: false,
	            flex: 0.8,
	            align: 'right',
	            menuDisabled: true
        };
        me.cols.push(weightCol);
        // 频率
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
            align: 'right',
            menuDisabled: true
        };
        me.cols.push(finishValueCol);


        var targetValueCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.targetValue'),
            dataIndex: 'targetValue',
            sortable: true,
            flex: 1.1,
            align: 'right',
            menuDisabled: true
        };
        me.cols.push(targetValueCol);

        
        var assessmentValueCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.assessmentValue'),
            dataIndex: 'assessmentValue',
            sortable: true,
            flex: 1.1,
            align: 'right',
            menuDisabled: true
        };

        me.cols.push(assessmentValueCol);
		// 评估状态
	    var assessmentStatusCol = {
            cls: 'grid-icon-column-header grid-statushead-column-header',
            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'assessmentStatus',
            sortable: false,
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
        };
        me.cols.push(assessmentStatusCol);
        var dateRangeCol = {
            header: FHD.locale.get('fhd.kpi.kpi.form.dateRange'),
            dataIndex: 'dateRange',
            sortable: false,
            flex: 1.2,
            menuDisabled: true,
            renderer: function (v) {
                return "<div data-qtitle='' data-qtip='" + v + "'>" + v + "</div>";
            }
        };

        me.cols.push(dateRangeCol);


        var kgrIdHidden = {
            dataIndex: 'kgrId',
            hidden: true
        };

        me.cols.push(kgrIdHidden);
		Ext.apply(me, {
			url : __ctxPath + "/kpi/kpistrategymap/findsmrelakpiresult.f",
			extraParams : {
				id : me.smid,
				year : FHD.data.yearId,
				month : FHD.data.monthId,
				quarter : FHD.data.quarterId,
				week : FHD.data.weekId,
				eType : FHD.data.eType,
				isNewValue : FHD.data.isNewValue
			},
			checked : false,
			pageable: false,
			header: false,
			border: false,
			searchable: false
		
		});

		me.callParent(arguments);
	},

	/**
	 * 重新加载列表数据
	 */
	reLoadData : function() {
		var me = this;
        me.store.proxy.extraParams.year = FHD.data.yearId;
        me.store.proxy.extraParams.month = FHD.data.monthId;
        me.store.proxy.extraParams.quarter = FHD.data.quarterId;
        me.store.proxy.extraParams.week = FHD.data.weekId;
        me.store.proxy.extraParams.isNewValue = FHD.data.isNewValue;
        me.store.proxy.extraParams.eType = FHD.data.eType;
        me.store.load();       
	}
	
});