Ext.define('FHD.view.kpi.report.ScKpiReportGroupGridPanel', {
    extend: 'FHD.ux.GridPanel',
    initComponent: function () {
    	var me = this;
    	 var cols = [{
             header: "id",
             dataIndex: 'id',
             hidden: true
         }, {
             header: "记分卡",
             dataIndex: 'scName',
             sortable: true,
             flex: 1.5,
             hidden:true,
             menuDisabled: true,
             renderer: function(v,rowIndex,cellIndex) {
            	 var scId = cellIndex.data.id;
            	 return "<a href='javascript:void(0)' onclick=\"analyseScorecard('" + scId + "')\" >" + v + "</a>"
             }
         },
         {
             header: "指标",
             dataIndex: 'kpiName',
             sortable: true,
             flex: 2.5,
             menuDisabled: true,
             renderer:function(value, rowIndex, cellIndex){
           	  var v = cellIndex.data.status;
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
                 var kpiNameSpan = "";
                 var statusSpan = "";
                 if(value){
               	   kpiNameSpan = "<span>" + value + "</span>";
               	   statusSpan =  "<span style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                     "background-position: center top;' data-qtitle='' " +
                     "class='" + v + "'  data-qtip='" + display + "'>&nbsp&nbsp&nbsp&nbsp</span>";
                 }                 
                 var spanChart = "<span style='width: 45px;'>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp" + "</span>"
                 return "<div>" + spanChart + statusSpan + kpiNameSpan + "</div>";
           }
         },{
             header: "分值",
             dataIndex: 'scAssessValue',
             sortable: true,
             flex: 0.5,
             hidden:true,
             menuDisabled: true,
             align:'center'
         }, {
             header: "状态",
             dataIndex: 'scStatus',
             sortable: true,
             width: 40,
             hidden:true,
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

         }, {
             header: "权重",
             dataIndex: 'kpiWeight',
             sortable: true,
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
             sortable: true,
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
//                     v = "icon-ibm-underconstruction-small";
//                     display = "无";
                 	return "";
                 }
                 return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                     "background-position: center top;' data-qtitle='' " +
                     "class='" + color + "'  data-qtip='" + display + "'>&nbsp</div>";
             }
         }, {
             header: "时间",
             dataIndex: 'timePeriod',
             sortable: true,
             menuDisabled: true,
             flex: 1
         }];
         var features=Ext.create('Ext.grid.feature.Grouping',{
 	        groupHeaderTpl:  Ext.create('Ext.XTemplate',
 	        	    '<div>',	
                     '{rows:this.formatRowData}',
                     '<span>{rows:this.formatSmValue}</span>',   
                     '<span>{rows:this.formatRowName}</span>',                
 	        	    '</div>',
 	        	    {
 	        	       formatRowData: function(rows) {
 	        	    	   var v = rows[0].get('scStatus');
 	        	           var color = "";
 	                       var display = "";
 	                       var smName = rows[0].get('smName');
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
 	                       	   color = "icon-flag-white";
 	                           display = "安全";
 	                       } else {
// 	                           v = "icon-ibm-underconstruction-small";
// 	                           display = "无";
 	                    	   color = "icon-flag-white";
 	                    	   display = "无";
 	                    //   	return "";
 	                       }
 	                       
 	                     return "<span style='background-repeat: no-repeat;" +
 	                     "background-position: center top;'" +
 	                     "class='" + color + "' " + ">&nbsp&nbsp&nbsp&nbsp&nbsp" +"</span>";
 	
 	        	        },
 	        	        formatSmValue:function(rows) {
 	        	        	if(rows.length > 0) {
 	        	        		var m = rows[0].get('scAssessValue');
 	        	        		if(m) {
 	        	        			return "&nbsp&nbsp" + m + "&nbsp&nbsp " ;
 	        	        		} else {
 	        	        			return "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
 	        	        		}	        	        		
 	        	        	} else {
 	        	        		return "";
 	        	        	}
 	        	        },
 	        	       formatRowName:function(rows) {
	        	        	if(rows.length > 0) {
 	        	        		var m = rows[0].get('scName');
 	                             return m;        	        		
 	        	        	} else {
 	        	        		return "";
 	        	        	}   
 	        	       }
 	        	    }
 	        	),
 	        hideGroupedHeader: true,
 	        collapsible: true,
 	        startCollapsed: false
 	    });
         Ext.apply(me, {
             cols: cols,
             border: false,
             checked: false,
             url: __ctxPath + '/kpi/report/findAllKpiScDetail.f',
             pagable: true,
             rowLines: true,
             columnLines: false,
             searchable: false,
             header:false,
             storeAutoLoad: false,
             storeGroupField: 'id',
             features: features,
             extraParams: {
             	 year: FHD.data.yearId,
                 month: FHD.data.monthId,
                 quarter: FHD.data.quarterId,
                 week: FHD.data.weekId,
                 isNewValue: FHD.data.isNewValue,
                 eType: FHD.data.eType
             },
             viewConfig: {
                 getRowClass: function (record, rowIndex, rowParams, store) {
                     return "row-w";
                 }
             }
         });
         me.callParent(arguments);
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
		window.location.href = "kpi/report/exportsckpireport.f?id="+""+"&exportFileName="+""+
								"&sheetName="+""+"&headerData="+Ext.encode(me.headerDatas)+"&timePeriod="+Ext.encode(me.timePeriod);
    }
})    