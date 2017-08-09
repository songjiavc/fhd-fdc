Ext.define('FHD.view.icm.icsystem.FlowRiskRelaMeasureGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.flowriskrelameasuregrid',
    
    url : __ctxPath + "/icm/control/findRiskRelaMeasureListByMeasureId.f",
    extraParams:{
    	measureId : ''
    },
    cols: [], // kpi列表显示的列
    tbarItems: [], // kpi列表上方工具条
    border: false, // 默认不显示border
    checked: false, // 是否可以选中
    pagable:false,
    destoryflag:'true',

    initComponent: function () {
        var me = this;
        
        if(me.measureId){
        	me.extraParams.measureId = me.measureId;
        }
        
        me.cols = [
			{
				header : "状态",
				dataIndex : 'assessementStatus',
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
					} else {
						v = "icon-ibm-symbol-0-sm";
						display = "无";
					}
					return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
							+ "background-position: center top;' data-qtitle='' "
							+ "class='"
							+ v
							+ "'  data-qtip='"
							+ display
							+ "'>&nbsp;</div>";
				}
			},
			{
				header : "趋势",
				dataIndex : 'etrend',
				sortable : true,
				width : 40,
				renderer : function(v) {
					var color = "";
					var display = "";
					if (v == "up") {
						color = "icon-ibm-icon-trend-rising-positive";
						display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
					} else if (v == "flat") {
						color = "icon-ibm-icon-trend-neutral-null";
						display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
					} else if (v == "down") {
						color = "icon-ibm-icon-trend-falling-negative";
						display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
					}
					return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
							+ "background-position: center top;' data-qtitle='' "
							+ "class='"
							+ color
							+ "'  data-qtip='"
							+ display
							+ "'>&nbsp;</div>";
				}
			}, {
				header : '名称',
				dataIndex : 'name',
				sortable : true,
				flex : 1,
				align : 'left'
			}, {
				header : '所属风险',
				dataIndex : 'parentName',
				sortable : true,
				flex : 1
			}, {
				header : '责任部门',
				dataIndex : 'respDeptName',
				sortable : true,
				flex : 1
			}, {
				dataIndex:'measureStr',
				hidden:true
			}, {
            	dataIndex:'id',
            	hidden:true
            }
        ];

        Ext.apply(me, {
            cols: me.cols,
            tbarItems: me.tbarItems,
            border: me.border,
            checked: me.checked,
            plugins: [{
                ptype: 'rowexpander',
                rowBodyTpl : new Ext.XTemplate(
                	'<p><b>MeasureName:</b>{measureStr}</p><br>'/*,
                    '<p><b>Change:</b> {change:this.formatChange}</p><br>',
	                {
	                    formatChange: function(v){
	                        var color = v >= 0 ? 'green' : 'red';
	                        return '<span style="color: ' + color + ';">' + Ext.util.Format.usMoney(v) + '</span>';
	                    }
	                }*/
                )
            }]
        });

        me.callParent(arguments);
    }
});