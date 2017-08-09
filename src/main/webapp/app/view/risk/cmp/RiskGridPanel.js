/**
 * 风险列表
 * 
 * @author zhengjunxiang
 */
Ext.define('FHD.view.risk.cmp.RiskGridPanel', {
    extend: 'FHD.ux.layout.GridPanel',
    alias: 'widget.riskgridpanel',

    /**
	 * public
	 * 接口属性
	 */
    type:'risk',//risk,org,kpi,process
    searchId:null,
	
	/**
	 * public 选择选中的id
	 */
	url:'/cmp/risk/findEventById',
   
    // 初始化方法
    initComponent: function() {
		var me = this;

		me.cols = [
				{
					dataIndex : 'id',
					hidden : true
				},
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
							v = "icon-ibm-symbol-0-sm";
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
				},
				{
					header : "趋势",
					dataIndex : 'etrend',
					sortable : true,
					width : 40,
					renderer : function(v) {
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
						return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
								+ "background-position: center top;' data-qtitle='' "
								+ "class='"
								+ v
								+ "'  data-qtip='"
								+ display
								+ "'></div>";
					}
				}, {
					header : '名称',
					dataIndex : 'name',
					sortable : true,
					flex : 1,
					align : 'left'
				}, {
					header : '所属风险',
					dataIndex : 'belongRisk',
					sortable : true,
					flex : 1
				}, {
					header : '责任部门',
					dataIndex : 'respDeptName',
					sortable : true,
					flex : 1
				} ];

		Ext.apply(me, {
			checked: false,
			url : __ctxPath + me.url,//"/risk/findEventById.f",
			extraParams : {
				type : me.type,
				id : me.searchId
			}
		});

		me.callParent(arguments);
    }
});