Ext.define('FHD.view.kpi.cmp.chart.HightChartKpiTrend', {
	extend : 'Ext.panel.Panel',
	style : 'padding:5px 5px 0px 5px',
	border : true,
	timeRefresh: true,
	initParam : function(paramObj) {
		var me = this;
		me.paramObj = paramObj;
	},
	findStatusImg:function(cssName){
		var me = this;
		var img = "";
		if(cssName=="icon-ibm-symbol-4-sm"){
			img = "url(images/icons/symbol_high_sm.gif)";
		}else if(cssName=="icon-ibm-symbol-5-sm"){
			img = "url(images/icons/symbol_mid_sm.gif)";
		}else if(cssName=="icon-ibm-symbol-6-sm"){
			img = "url(images/icons/symbol_low_sm.gif)";
		}
		return img;
	},

	initChart : function(data) {
		var me = this;
		if (me.chartPanel) { // 仪表盘
			me.remove(me.chartPanel, true);
		}
		var storeArray = [];
		if (data.totalCount != "0") {
			var datas = data.datas;
			for (var i = 0; i < datas.length; i++) {
				var valueObj = {};
				var valueArr = [];
				var v = datas[i];
				valueObj.name = v.name;
				if (v.januaryValue == "") {
					valueArr.push(null);
				} else {
					if(v.januaryStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.januaryValue);
						var img = me.findStatusImg(v.januaryStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.januaryValue));
					}
					
				}
				if (v.februaryValue == "") {
					valueArr.push(null);
				} else {
					if(v.februaryStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.februaryValue);
						var img = me.findStatusImg(v.februaryStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.februaryValue));
					}
				}
				if (v.marchValue == "") {
					valueArr.push(null);
				} else {
					if(v.marchStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.marchValue);
						var img = me.findStatusImg(v.marchStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.marchValue));
					}
				}
				if (v.aprilValue == "") {
					valueArr.push(null);
				} else {
					if(v.aprilStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.aprilValue);
						var img = me.findStatusImg(v.aprilStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.aprilValue));
					}
				}
				if (v.mayValue == "") {
					valueArr.push(null);
				} else {
					if(v.mayStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.mayValue);
						var img = me.findStatusImg(v.mayStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.mayValue));
					}
				}
				if (v.juneValue == "") {
					valueArr.push(null);
				} else {
					if(v.juneStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.juneValue);
						var img = me.findStatusImg(v.juneStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.juneValue));
					}
				}
				if (v.julyValue == "") {
					valueArr.push(null);
				} else {
					if(v.julyStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.julyValue);
						var img = me.findStatusImg(v.julyStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.julyValue));
					}
				}
				if (v.aguestValue == "") {
					valueArr.push(null);
				} else {
					if(v.aguestStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.aguestValue);
						var img = me.findStatusImg(v.aguestStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.aguestValue));
					}
				}
				if (v.septemberValue == "") {
					valueArr.push(null);
				} else {
					if(v.septemberStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.septemberValue);
						var img = me.findStatusImg(v.septemberStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.septemberValue));
					}
				}
				if (v.octoberValue == "") {
					valueArr.push(null);
				} else {
					if(v.octoberStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.octoberValue);
						var img = me.findStatusImg(v.octoberStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.octoberValue));
					}
				}
				if (v.novemberValue == "") {
					valueArr.push(null);
				} else {
					if(v.novemberStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.novemberValue);
						var img = me.findStatusImg(v.novemberStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.novemberValue));
					}
				}
				if (v.decemberValue == "") {
					valueArr.push(null);
				} else {
					if(v.decemberStatus!=""){
						var vobj = {};
						vobj.y = parseFloat(v.decemberValue);
						var img = me.findStatusImg(v.decemberStatus);
						vobj.marker = {symbol:img};
						valueArr.push(vobj);
					}else{
						valueArr.push(parseFloat(v.decemberValue));
					}
				}
				valueObj.data = valueArr;
				storeArray.push(valueObj);
			}
		}

		me.chartPanel = Ext.create('Ext.panel.Panel', {
					border : false,
					html : '<div id="'
							+ me.id
							+ 'DIV" style="height:500px; min-width: 1000px"></div>',
					listeners : {
						afterrender : function(c, opts) {
							me.renderChart(storeArray);
						}
					}
				});

		me.add(me.chartPanel);

	},
	
	load:function(){
    	var me = this;
    	me.reloadData();
    },

	reloadData : function() {
		var me = this;
		var paraobj = {};
		paraobj.isNewValue = FHD.data.isNewValue
		if (FHD.data.yearId == '') {
			//paraobj.yearId = new Date().getFullYear();
		} else {
			paraobj.yearId = FHD.data.yearId;
		}
		paraobj.monthId = FHD.data.monthId;
		paraobj.objectId = me.paramObj.objectId;
		paraobj.dataType = me.paramObj.dataType;
		paraobj.quarterId = FHD.data.quarterId;
		paraobj.weekId = FHD.data.weekId;

		FHD.ajax({
					url : __ctxPath + '/kpi/cmp/findobjectrelakpitrend.f',
					params : {
						condItem : Ext.JSON.encode(paraobj)
					},
					callback : function(data) {
						if (data && data.success) {
							me.initChart(data);
						}
					}
				});

	},
	onDestroy : function() {
		if (this.chartcontainer) {
			this.chartcontainer.destroy();
		}
		this.removeAll(true);
		if (Ext.isIE) {
			CollectGarbage();
		}
		this.callParent(arguments);
	},

	initComponent : function() {
		var me = this;
		me.chartPanel = Ext.create('Ext.panel.Panel', {
			border : false,
			html : '<div id="' + me.id
					+ 'DIV" style="height: 500px; min-width: 1000px"></div>'
			});

		me.callParent(arguments);

		me.add(me.chartPanel);

	},
	renderChart : function(storeArray) {
		var me = this;
		var yearId = FHD.data.yearId;
		var chartId = me.id + 'DIV';
		var options = {
			credits : {
				enabled : false
			},
			chart : {
				renderTo : chartId
			},
			title : {
				text : '指标实际值趋势分析',
				x : -20,
				style:{
					fontSize: '14px',
					fontWeight:'900'
				}
				// center
			},
			subtitle : {
				text : '',
				x : -20,
				style:{
					fontSize: '12px',
					fontWeight:'700'
				}
			},
			xAxis : {
				categories : ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月',
						'9月', '10月', '11月', '12月']
			},
			yAxis : {
				title : {
					text : ''
				},
				plotLines : [{
							value : 0,
							width : 1,
							color : '#808080'
						}]
			},
			tooltip : {
				valueSuffix : ''
			},
			legend : {
				layout : 'vertical',
				align : 'right',
				verticalAlign : 'middle',
				borderWidth : 0
			},
			series : storeArray
		};
		me.chartcontainer = new Highcharts.Chart(options);
	}
});