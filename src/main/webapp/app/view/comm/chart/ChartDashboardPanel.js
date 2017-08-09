Ext.define('FHD.view.comm.chart.ChartDashboardPanel', {
	extend: 'Ext.container.Container',
    alias: 'widget.chartdashboardpanel',
    
    mixins :{
		chartData : 'FHD.view.comm.chart.ChartData'
	},
	
	requires: [
        'FHD.view.comm.chart.GridPanel',
        'FHD.ux.FusionChartPanel'
    ],
    border: true,
    
    /*
     * 图表配置默认选项
     */
    //图表默认选项
    type: 'grid',
    
    /*
     * grid配置默认选项
     */
    //序号
    rowNumberer:false,
    //复选框
    checked:false,
    //搜索框
    searchable:false,
    //分页
    pagable:false,
    //自定义栏位置
    toolRegion:'north',
    
    initComponent: function () {
        var me = this;
        me.exportToExcelAble = false;
    	me.northRegion = Ext.widget('panel',{
    		border:false,
    		title:'自定义',
    		split: true,
        	collapsible : true,
        	width:138,
        	collapseMode:'mini',
        	collapsed: true,
        	maxwidth: 138,
        	overflowX: 'hidden',
		    overflowY: 'auto',
		    region:me.toolRegion
    	});
    	
        Ext.applyIf(me, {
        	layout:{
        		type:'border'
        	},
        	items:[
        	    me.northRegion
        	]
        });
        
        me.setTbar();
        
        
        me.callParent(arguments);
        me.reloadData();
    },
    //导出grid列表
    exportChart:function(item, pressed){
    	var me=this;

    	if(me.showItem){
    		FHD.exportExcel(me.showItem,'exportexcel','test');
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'没有grid数据源!');
    	}
    },
    //导出fusioncharts图片回调函数
    extportCallback:function(objRtn){
    	var me=this;

    	if (objRtn.statusCode=="1"){
    		window.location.href = __ctxPath + "/downloadFile.f?path="+objRtn.fileName;
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),"导出失败，错误描述 : " + objRtn.statusMessage);
    	}
    },
    //点击[查询]按钮生成图表或列表
    onItemToggle:function(item, pressed){
    	var me=this;
    	
    	if(!me.xAxisValue){
			me.cols.xCol=null;
		}
    	if(!me.yAxisValue){
			me.cols.yCol=null;
		}
    	if(me.yAxisValue &&!me.xAxisValue){
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'请选择系列(列)!');
    		return false;
    	}
    	if(!me.zAxisValue){
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'请选择图表类型!');
    		return false;
    	}
    	me.type = me.zAxisValue;
    	me.northRegion.collapse(true);
		me.loadData(false);
    },
    //x轴或y轴下拉框选项改变生成图表类型下拉框选项
    onComboxSelect:function(){
    	var me=this;
    	if(me.xAxisValue){
    		if(me.yAxisValue){
    			//x选中，y选中--交叉，列表，柱，折线
    			me.chartMenu.down('[name=grid]').setDisabled(false);
    			me.chartMenu.down('[name=crossGrid]').setDisabled(false);
    			me.chartMenu.down('[name=msColumnChart]').setDisabled(false);
    			me.chartMenu.down('[name=msLineChart]').setDisabled(false);
    			
    			me.chartMenu.down('[name=angularGaugeChart]').setDisabled(true);
    			me.chartMenu.down('[name=circleChart]').setDisabled(true);
    			me.chartMenu.down('[name=columnChart]').setDisabled(true);
    			me.chartMenu.down('[name=lineChart]').setDisabled(true);
    			me.chartMenu.down('[name=pieChart]').setDisabled(true);
    		}else{
    			//x选中，y未选--列表，饼，柱，折线
    			me.chartMenu.down('[name=grid]').setDisabled(false);
    			me.chartMenu.down('[name=columnChart]').setDisabled(false);
    			me.chartMenu.down('[name=lineChart]').setDisabled(false);
    			me.chartMenu.down('[name=pieChart]').setDisabled(false);
    			
    			me.chartMenu.down('[name=angularGaugeChart]').setDisabled(true);
    			me.chartMenu.down('[name=circleChart]').setDisabled(true);
    			me.chartMenu.down('[name=crossGrid]').setDisabled(true);
    			me.chartMenu.down('[name=msColumnChart]').setDisabled(true);
    			me.chartMenu.down('[name=msLineChart]').setDisabled(true);
    		}
    	}else{
    		
    		if(me.yAxisValue){
    			//x未选，y选中
    		}else{
    			//x未选，y未选--列表，仪表盘，环形
    			me.chartMenu.down('[name=grid]').setDisabled(false);
    			me.chartMenu.down('[name=angularGaugeChart]').setDisabled(false);
    			me.chartMenu.down('[name=circleChart]').setDisabled(false);
    			
    			me.chartMenu.down('[name=crossGrid]').setDisabled(true);
    			me.chartMenu.down('[name=msColumnChart]').setDisabled(true);
    			me.chartMenu.down('[name=msLineChart]').setDisabled(true);
    			me.chartMenu.down('[name=columnChart]').setDisabled(true);
    			me.chartMenu.down('[name=lineChart]').setDisabled(true);
    			me.chartMenu.down('[name=pieChart]').setDisabled(true);
    		}
    	}
    },
    //设置导出按钮的可用性
    setExportToExcelDisabeld:function(exportToExcelAble){
    	var me = this;
    	if(exportToExcelAble){
  			me.zAxisCombo.down('[name=exportToExcel]').setDisabled(false);
  		}else{
  			me.zAxisCombo.down('[name=exportToExcel]').setDisabled(true);
  		}
    },
    //设置tbar的combox选项
    setTbar:function(){
    	var me=this;
    	
      	var store = Ext.create('Ext.data.Store', {
      		fields: ['dataIndex', 'header'],
      		data : me.storeItem
        });
        store.load();
        
      	me.xAxisCombo = Ext.widget('combobox', {
      		fieldLabel:'系列',
      		width:135,
      		labelWidth:30,
      		margin:'1 1 1 1',
      		store: store,
      		displayField: 'header',
      		typeAhead: true,
      		queryMode: 'local',
      		triggerAction: 'all',
      		emptyText: '请选择系列(列)',
      		selectOnFocus: true,
      		iconCls: 'no-icon',
      		listeners:{
      			'select':function(combo,records,index){
      				me.xAxisValue = records[0].data.dataIndex;
      				if(me.xAxisValue){
      					me.cols.xCol=records[0].data;
      				}else{
      					me.cols.xCol=null;
      				}
      				me.onComboxSelect();
      			}
      		} 
      	});
      	me.yAxisCombo = Ext.widget('combobox', {
      		fieldLabel:'图例',
      		width:135,
      		labelWidth:30,
      		margin:'1 1 1 1',
      		store: store,
          	displayField: 'header',
          	typeAhead: true,
          	editable: true,
          	queryMode: 'local',
          	triggerAction: 'all',
          	emptyText: '请选择图例(列)',
          	selectOnFocus: true,
          	iconCls: 'no-icon',
          	listeners:{
      			'select':function(combo,records,index){
      				me.yAxisValue = records[0].data.dataIndex;
      				if(me.yAxisValue){
      					me.cols.yCol=records[0].data;
      				}else{
      					me.cols.yCol=null;
      				}
      				me.onComboxSelect();
      			}
      		} 
      	});
      	
      	me.chartMenu = Ext.widget('menu',{
      		listeners:{
      			mouseleave:function(menu,event){
      				menu.hide();
      			}
      		},
            items: [
	            {
					text : '列表',
					name : 'grid',
					handler : function() {
						me.zAxisValue = 'grid';
						me.setExportToExcelDisabeld(true);
						me.onItemToggle();
					}
				}, {
					text : '仪表盘',
					name : 'angularGaugeChart',
					disabled : true,
					handler : function() {
						me.zAxisValue = 'angularGaugeChart';
						me.setExportToExcelDisabeld(false);
						me.onItemToggle()
					}
				}, {
					text : '环形图',
					name : 'circleChart',
					disabled : true,
					handler : function() {
						me.zAxisValue = 'circleChart';
						me.setExportToExcelDisabeld(false);
						me.onItemToggle()
					}
				}, {
					text : '柱图',
					name : 'columnChart',
					disabled : true,
					handler : function() {
						me.zAxisValue = 'columnChart';
						me.setExportToExcelDisabeld(false);
						me.onItemToggle()
					}
				}, {
					text : '折线图',
					name : 'lineChart',
					disabled : true,
					handler : function() {
						me.zAxisValue = 'lineChart';
						me.setExportToExcelDisabeld(false);
						me.onItemToggle()
					}
				}, {
					text : '饼图',
					name : 'pieChart',
					disabled : true,
					handler : function() {
						me.zAxisValue = 'pieChart';
						me.setExportToExcelDisabeld(false);
						me.onItemToggle()
					}
				}, {
					text : '交叉表',
					name : 'crossGrid',
					disabled : true,
					handler : function() {
						me.zAxisValue = 'crossGrid';
						me.setExportToExcelDisabeld(true);
						me.onItemToggle()
					}
				}, {
					text : '二维柱图',
					name : 'msColumnChart',
					disabled : true,
					handler : function() {
						me.zAxisValue = 'msColumnChart';
						me.setExportToExcelDisabeld(false);
						me.onItemToggle()
					}
				}, {
					text : '二维折线图',
					name : 'msLineChart',
					disabled : true,
					handler : function() {
						me.zAxisValue = 'msLineChart';
						me.setExportToExcelDisabeld(false);
						me.onItemToggle()
					}
				},
		      	{
		      		text:'查看源表',
		      		handler: me.showGrid,
		            scope : this
		      	}
			]
        });
      	me.zAxisCombo = Ext.widget('buttongroup', {
      		columns: 3,
      		items: [
      			{
      				iconCls:'icon-chart-bar',
		            xtype:'splitbutton', 
		            text:'方式',
		            tooltip: '展示方式',
		            menu: me.chartMenu,
		            listeners:{
		      			mouseover:function(split,event){
		      				me.chartMenu.showBy(split);
		      			}
		      		}
		        },
      			{
		      		iconCls : 'icon-ibm-action-export-to-excel',
		      		tooltip: '导出当前表',
		      		text:'导出',
		      		name:'exportToExcel',
		      		disabled:true,
		      		handler: me.exportChart,
		            scope : this
		      	}
	      	]
      	});
      	me.onComboxSelect();
      	me.northRegion.add(me.zAxisCombo);
      	me.northRegion.add(me.xAxisCombo);
      	me.northRegion.add(me.yAxisCombo);
    },
    //显示原数据源
    showGrid:function(){
    	var me=this;
		me.multiColumns = me.config.multiColumns;
    	me.dataJson = me.config.datas;
    	
    	if(me.showItem){
	    	me.remove(me.showItem, true);
    	}
		//列表
		me.showItem = Ext.widget('gridpanel',{
			region:'center',
        	rowNumberer:me.rowNumberer,
        	border: false,
        	checked: me.checked,
        	searchable:me.searchable,
        	pagable:me.pagable,
        	dataJson:me.dataJson,
    		cols:me.multiColumns
    	});
    	me.add(me.showItem);
    },
    //生成grid列表
    generateStore:function(){
    	var me=this;
    	var data=null;
    	if(typeof(me.cols.xCol)=="undefined"&&typeof(me.cols.yCol)=="undefined"){
    		data=me.getBaseData();
    	}else{
    		data=me.getVGridData();
    	}
    	me.multiColumns = data.multiColumns;
    	me.dataJson = data.datas;
	},
	//生成交叉表列表
	crossStore:function(){
    	var me=this;
    	var data=null;
    	if(!me.cols.xCol&&!me.cols.yCol){
    		data=me.getBaseData();
    	}else{
    		data=me.getHGridData();
    	}
    	me.multiColumns = data.multiColumns;
    	me.dataJson = data.datas;
	},
    //生成一维图表xml
    generateOneDimensionalChartXml:function(){
    	var me=this;
    	
    	var chartTotal = me.getChartTotal();
    	me.finishRate = chartTotal.value;
		
    	var data;
    	if('circleChart' == me.type){
    		//环形图
    		data='<chart baseFont="Arial" baseFontSize ="12" lowerLimit="0" origW="300" origH="300" upperLimit="100" gaugeScaleAngle="360" minorTMNumber="0" majorTMNumber="0" showBorder="0"\n\
        		gaugeOuterRadius="100" gaugeInnerRadius="75" gaugeOriginX="235" gaugeOriginY="120" placeValuesInside="0" pivotRadius="1" showGaugeBorder="0"\n\
        		majorTMColor="ffffff" chartLeftMargin="10" chartTopMargin="10" chartBottomMargin="30" placeValuesInside="1" displayValueDistance="999"\n\
        		basefontColor="000000" toolTipBgColor="FFFFFF" majorTMHeight="10" showShadow="0" bgColor="FFFFFF" exportEnabled="1" exportAtClient="0"\n\
        		exportDialogMessage="正在生成，请稍候..." exportHandler="'+ __ctxPath +'/FCExporter.f" exportFormats="JPG=生成JPG图片|PNG=生成PNG图片|PDF=生成PDF文件"\n\
        		showAboutMenuItem="0" aboutMenuItemLabel="关于fusioncharts" exportCallback="Ext.getCmp(\''+me.id+'\').extportCallback" exportAction="save"';
    		
    		//前缀
        	if(me.numberPrefix){
        		data += ' numberPrefix="'+me.numberPrefix+'"';
        	}
        	//后缀
        	if(me.numberSuffix){
        		data += ' numberSuffix="'+me.numberSuffix+'"';
        	}
        	//导出文件名称
        	if(me.exportFileName){
        		data += ' exportFileName="'+me.exportFileName+'"';
        	}
        	data += ">'";
        	
    		data += '<colorRange>';
				data += '<color minValue="0" maxValue="50" code="FF654F"/>';
				data += '<color minValue="50" maxValue="75" code="F6BD0F"/>';
				data += '<color minValue="75" maxValue="100" code="8BBA00"/>';
			data += '</colorRange>';
			
			data += '<dials>';
				data += '<dial value="'+me.finishRate+'" rearExtension="10" baseWidth="10"/>';
			data += '</dials>';
			
			data += '</chart>';
    	}else if('angularGaugeChart' == me.type){
    		//仪表盘
    		data = '<chart baseFont="Arial" baseFontSize ="12" manageResize="1" origW="300" origH="180"  palette="2" bgAlpha="0" bgColor="FFFFFF" lowerLimit="0" upperLimit="100"\n\
    			showBorder="0" basefontColor="000000" chartTopMargin="0" chartBottomMargin="5" toolTipBgColor="FFFFFF" pivotRadius="8"\n\
    			gaugeOuterRadius="90" gaugeInnerRadius="70" gaugeOriginX="145" gaugeOriginY="120" trendValueDistance="5" tickValueDistance="20"\n\
    			manageValueOverlapping="1" autoAlignTickValues="1" exportEnabled="1" exportAtClient="0"\n\
        		exportDialogMessage="正在生成，请稍候..." exportHandler="'+ __ctxPath +'/FCExporter.f" exportFormats="JPG=生成JPG图片|PNG=生成PNG图片|PDF=生成PDF文件"\n\
        		showAboutMenuItem="0" aboutMenuItemLabel="关于fusioncharts" exportCallback="Ext.getCmp(\''+me.id+'\').extportCallback" exportAction="save"';
    		
    		//前缀
        	if(me.numberPrefix){
        		data += ' numberPrefix="'+me.numberPrefix+'"';
        	}
        	//后缀
        	if(me.numberSuffix){
        		data += ' numberSuffix="'+me.numberSuffix+'"';
        	}
        	//导出文件名称
        	if(me.exportFileName){
        		data += ' exportFileName="'+me.exportFileName+'"';
        	}
        	data += ">'";
        	
    		data += '<colorRange>';
				data += '<color minValue="0" maxValue="50" code="CC9900"/>';
				data += '<color minValue="50" maxValue="75" code="FF6600"/>';
				data += '<color minValue="75" maxValue="100" code="FDEBE3"/>';
			data += '</colorRange>';
			
			data += '<dials>';
				data += '<dial value="'+me.finishRate+'" rearExtension="10" baseWidth="10"/>';
			data += '</dials>';
			data += '</chart>';
    	}
    	
    	return data;
    },
    //生成二维图表xml
    generateTwoDimensionalChartXml:function(){
    	var me=this;
    	
    	var data;
    	
    	if('columnChart' == me.type){
    		data='<chart caption="'+me.caption+'" baseFont="Arial" baseFontSize ="12" xAxisName="'+me.cols.xCol.header+'" yAxisName="'+me.cols.valueCol.header+'" labelDisplay="WRAP"\n\
			slantLabels="1" showValues="1" decimals="2" formatNumberScale="0" bgColor="FFFFFF" borderColor="FFFFFF" canvasBorderColor="C0C0C0,FFFFFF"\n\
			canvasBottomMargin="70" plotBorderColor="FFFFFF" showShadow="0" legendPosition="RIGHT" legendNumColumns="1" divLineDashGap="1"\n\
			showAlternateHGridColor="0" showLabels="1"  plotGradientColor="" exportEnabled="1" exportAtClient="0"\n\
    		exportDialogMessage="正在生成，请稍候..." exportHandler="'+ __ctxPath +'/FCExporter.f" exportFormats="JPG=生成JPG图片|PNG=生成PNG图片|PDF=生成PDF文件"\n\
    		showAboutMenuItem="0" aboutMenuItemLabel="关于fusioncharts" exportCallback="Ext.getCmp(\''+me.id+'\').extportCallback" exportAction="save"';
    	}else if('lineChart' == me.type){
    		//labelDisplay="Rotate"
    		data = '<chart caption="'+me.caption+'" baseFont="Arial" baseFontSize ="12" xAxisName="'+me.cols.xCol.header+'" yAxisName="'+me.cols.valueCol.header+'" showAlternateHGridColor="0"\n\
			labelDisplay="WRAP" slantLabels="1" showValues="1" showShadow="0" formatNumberScale="0" bgColor="FFFFFF" borderColor="FFFFFF" canvasBorderColor="C0C0C0,FFFFFF" animation="1" \n\
			baseFontColor="000000" lineColor="FF5904" showAlternateHGridColor="0"\n\
			lineAlpha="85" valuePosition="auto" palette="2" showLabels="1" showColumnShadow="0" exportEnabled="1" exportAtClient="0"\n\
    		exportDialogMessage="正在生成，请稍候..." exportHandler="'+ __ctxPath +'/FCExporter.f" exportFormats="JPG=生成JPG图片|PNG=生成PNG图片|PDF=生成PDF文件"\n\
    		showAboutMenuItem="0" aboutMenuItemLabel="关于fusioncharts" exportCallback="Ext.getCmp(\''+me.id+'\').extportCallback" exportAction="save"';
    	}else if('pieChart' == me.type){
    		data = '<chart caption="'+me.caption+'" baseFont="Arial" baseFontSize ="12" showPercentageInLabel="1" showValues="1" showLabels="1" showLegend="1" legendBorderColor="C0C0C0" legendShadow="0" legendPosition="RIGHT" legendNumColumns="1"\n\
			canvasBorderColor="C0C0C0" formatNumberScale="0" bgcolor="FFFFFF" borderColor="FFFFFF" showShadow="0" showPercentValues="1" legendCaption="'+me.cols.xCol.header+'"  exportEnabled="1" exportAtClient="0"\n\
    		exportDialogMessage="正在生成，请稍候..." exportHandler="'+ __ctxPath +'/FCExporter.f" exportFormats="JPG=生成JPG图片|PNG=生成PNG图片|PDF=生成PDF文件"\n\
    		showAboutMenuItem="0" aboutMenuItemLabel="关于fusioncharts" exportCallback="Ext.getCmp(\''+me.id+'\').extportCallback" exportAction="save"';
    	}
    	
    	//前缀
    	if(me.numberPrefix){
    		data += ' numberPrefix="'+me.numberPrefix+'"';
    	}
    	//后缀
    	if(me.numberSuffix){
    		data += ' numberSuffix="'+me.numberSuffix+'"';
    	}
    	//导出文件名称
    	if(me.exportFileName){
    		data += ' exportFileName="'+me.exportFileName+'"';
    	}
    	data += ">'";
    	
		var chartSum=me.getChartSum();
		var sets1=chartSum.sets;
		for (var j = 0; j < sets1.length; j++) {
			var label=sets1[j].label;
			var value=sets1[j].value;
			if(label==""){
				label="无";
			}
			data += '<set value="'+value+'" label="'+label+'" toolText="'+label+'：'+value+'" />';
		}

    	data += '</chart>'; 
		
    	return data;
    },
    //生成三维图表xml
    generateThreeDimensionalChartXml:function(){
    	var me=this;
    	
    	var data;
    	//showAboutMenuItem 0/1 右键是否显示"关于FusionCharts" aboutMenuItemLabel string 右键关于自定义文字 
    	var data='<chart caption="'+me.caption+'" palette="2" baseFont="Arial" baseFontSize ="12" xAxisName="'+me.cols.xCol.header+'" yAxisName="'+me.cols.valueCol.header+'" labelDisplay="WRAP" slantLabels="1"\n\
    		showAlternateHGridColor="0" showLabels="1" showValues="1" showShadow="0" decimals="2" formatNumberScale="0" bgColor="FFFFFF" canvasbgAlpha="0" plotGradientColor="" legendCaption="'+me.cols.yCol.header+'" \n\
    		divLineAlpha="30" plotSpacePercent="20" canvasBorderColor="C0C0C0,FFFFFF" showLegend="1" legendBorderColor="C0C0C0" legendShadow="0" \n\
    		legendPosition="RIGHT" legendNumColumns="1" showBorder="0" plotBorderColor="FFFFFF" exportEnabled="1" exportAtClient="0" exportDialogMessage="正在生成，请稍候..."\n\
    		exportHandler="'+ __ctxPath +'/FCExporter.f" exportFormats="JPG=生成JPG图片|PNG=生成PNG图片|PDF=生成PDF文件" showAboutMenuItem="0" aboutMenuItemLabel="关于fusioncharts"\n\
    		exportCallback="Ext.getCmp(\''+me.id+'\').extportCallback" exportAction="save"';
    	
    	//前缀
    	if(me.numberPrefix){
    		data += ' numberPrefix="'+me.numberPrefix+'"';
    	}
    	//后缀
    	if(me.numberSuffix){
    		data += ' numberSuffix="'+me.numberSuffix+'"';
    	}
    	//导出文件名称
    	if(me.exportFileName){
    		data += ' exportFileName="'+me.exportFileName+'"';
    	}
    	data += ">'";
    	
		var chartDetail=me.getChartDetail();
		var sets1=chartDetail.sets;
		var datasets={};
		var cols=me.cols;
    	for (var i = 0; i < cols.yCol.values.length; i++) {
    		datasets[cols.yCol.values[i]]=new Array();
    	}
    	data += '<categories>';
		for (var i = 0; i < sets1.length; i++) {
			var label1=sets1[i].label;
			sets2=sets1[i].sets;
			if(label1==""){
				label1="无";
			}
			data += '<category label="'+label1+'" />';;
			
			for (var j = 0; j < sets2.length; j++) {
				var label=sets2[j].label;
				var value=sets2[j].value;
				datasets[label].push(value);
			}
		}
		data += '</categories>';
		
		for (var i = 0; i < cols.yCol.values.length; i++) {
    		var datasetsValues=datasets[cols.yCol.values[i]]
    		var label=cols.yCol.values[i];
    		if(label==""){
    			label="无"
    		}
			data += '<dataset seriesName="'+label+'">';
			for (var j = 0; j < datasetsValues.length; j++) {
				data += '<set value="'+datasets[cols.yCol.values[i]][j]+'" />';
			}
			data += '</dataset>';
    	}
		data += '</chart>';
		
		return data;
    },
    loadData:function(isInit){
    	var me=this;
		if(isInit){
			me.xAxisCombo.setValue(me.xCol.header);
			me.xAxisValue = me.xCol.dataIndex;
			me.yAxisCombo.setValue(me.yCol.header);
			me.yAxisValue = me.yCol.dataIndex;
			me.onComboxSelect();
			me.init({datas:me.dataJson,multiColumns:me.multiColumns,dataType:me.dataType,cols:{xCol:me.xCol,yCol:me.yCol,valueCol:me.valueCol}});
		}
    	//1.生成grid或者char需要的xml
    	if('grid' == me.type){
    		//列表
    		me.storeDataXml = me.generateStore();
    	}else if('crossGrid' == me.type){
    		//列表
    		me.storeDataXml = me.crossStore();
    	}else if('angularGaugeChart' == me.type || 'circleChart' == me.type){
    		//一维图表xml
    		me.chartDataXml = me.generateOneDimensionalChartXml();
    		//gridJson
    		me.storeDataXml = me.generateStore();
    	}else if('columnChart' == me.type || 'lineChart' == me.type || 'pieChart' == me.type){
    		//二维图表xml
    		me.chartDataXml = me.generateTwoDimensionalChartXml();
    		//gridJson
    		me.storeDataXml = me.generateStore();
    	}else if('msColumnChart' == me.type || 'msLineChart' == me.type){
    		//三维图表xml
    		me.chartDataXml = me.generateThreeDimensionalChartXml();
    		//gridJson
    		me.storeDataXml = me.generateStore();
    	}
    	
    	//2.生成grid或chart
    	//me.removeAll();
    	if(me.showItem){
	    	me.remove(me.showItem, true);
    		//删除fusionchart的缓存文件
    		if(FusionCharts(me.showItem.id+"-chart") != undefined){
    			FusionCharts(me.showItem.id+"-chart").dispose();
    		}
    	}
    	if('grid' == me.type || 'crossGrid' == me.type){
    		//列表
    		me.showItem = Ext.widget('gridpanel',{
    			region:'center',
            	rowNumberer:me.rowNumberer,
            	border: me.border,
            	checked: me.checked,
            	searchable:me.searchable,
            	pagable:me.pagable,
            	dataJson:me.dataJson,
        		cols:me.multiColumns
        	});
    	}else if('angularGaugeChart' == me.type){
    		//一维仪表盘
    		me.showItem = Ext.widget('fusionchartpanel',{
    			region:'center',
    			border:me.border,
    			style:'padding:0px 0px 0px 0px',
    			chartType:'AngularGauge',
    			title:me.title,
    			xmlData:me.chartDataXml
    		});
    	}else if('circleChart' == me.type){
    		//一维环形图
    		me.showItem = Ext.widget('fusionchartpanel',{
    			region:'center',
    			border: me.border,
    			style:'padding:0px 0px 0px 0px',
    			chartType:'AngularGauge',
    			title:me.title,
    			xmlData:me.chartDataXml
    		});
    	}else if('columnChart' == me.type){
    		//二维柱图
    		me.showItem = Ext.widget('fusionchartpanel',{
    			region:'center',
    			border: me.border,
    			style:'padding:0px 0px 0px 0px',
    			chartType:'Column2D',
    			title:me.title,
    			xmlData:me.chartDataXml
    		});
    	}else if('lineChart' == me.type){
    		//二维折线图
    		me.showItem = Ext.widget('fusionchartpanel',{
    			region:'center',
    			border: me.border,
    			style:'padding:0px 0px 0px 0px',
    			chartType:'Line',
    			title:me.title,
    			xmlData:me.chartDataXml
    		});
    	}else if('pieChart' == me.type){
    		//二维饼图
    		me.showItem = Ext.widget('fusionchartpanel',{
    			region:'center',
    			border: me.border,
    			style:'padding:0px 0px 0px 0px',
    			chartType:'Pie2D',
    			title:me.title,
    			xmlData:me.chartDataXml
    		});
    	}else if('msColumnChart' == me.type){
    		//三维柱图
    		me.showItem = Ext.widget('fusionchartpanel',{
    			region:'center',
    			border: me.border,
    			style:'padding:0px 0px 0px 0px',
    			chartType:'ScrollColumn2D',
    			title:me.title,
    			xmlData:me.chartDataXml
    		});
    	}else if('msLineChart' == me.type){
    		//三维折线图
    		me.showItem = Ext.widget('fusionchartpanel',{
    			region:'center',
    			border: me.border,
    			style:'padding:0px 0px 0px 0px',
    			chartType:'ScrollLine2D',
    			title:me.title,
    			xmlData:me.chartDataXml
    		});
    	}
    	me.add(me.showItem);
    },
    listeners:{
		resize:function(me,width,height,oldWidth,oldHeight,eOpts){
			//alert(width+"\t"+height+"\t"+oldWidth+"\t"+oldHeight);
			
			if('angularGaugeChart' == me.type || 'circleChart' == me.type){
				if(me.showItem){
			    	me.remove(me.showItem, true);
		    		//删除fusionchart的缓存文件
		    		if(FusionCharts(me.showItem.id+"-chart") != undefined){
		    			FusionCharts(me.showItem.id+"-chart").dispose();
		    		}
		    	}
				if('angularGaugeChart' == me.type){
					//一维仪表盘
					me.showItem = Ext.widget('fusionchartpanel',{
						region:'center',
						border:false,
						style:'padding:0px 0px 0px 0px',
						chartType:'AngularGauge',
						title:me.title,
						xmlData:me.chartDataXml
					});
				}else if('circleChart' == me.type){
					//一维环形图
					me.showItem = Ext.widget('fusionchartpanel',{
						region:'center',
						border:false,
						style:'padding:0px 0px 0px 0px',
						chartType:'AngularGauge',
						title:me.title,
						xmlData:me.chartDataXml
					});
				}
				me.add(me.showItem);
			}
		}
	},
    //重新加载数据
    reloadData: function (){
    	var me = this;
    	FHD.ajax({
            url: me.url,
            params: me.extraParams,
            callback: function (response) {
                me.dataJson = response.datas;
                me.loadData(true);
            }
    	});
    }
});