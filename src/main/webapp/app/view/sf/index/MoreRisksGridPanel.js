Ext.define('FHD.view.sf.index.MoreRisksGridPanel', {
	extend : 'FHD.ux.GridPanel',
    
	url : __ctxPath + '/cmp/risk/findEventForIndex.f',
	border : true,

	reloadData : function() {
		var me = this;
		me.store.load();
	},
	showRiskDetails: function(riskId){
		var me = this;
		if(riskId){
			me.onMenuClickbb('FHD.view.sf.index.AssessAnalyseMainPanel','风险分析',riskId)
		}
	},
	
	onMenuClickbb: function(url,title,businessId,nodeId){
 		var url = url;
 		var text = title;//FHD.titleJs[url];
 		var centerPanel = parent.Ext.getCmp('center-panel');
 		var tab = centerPanel.getComponent(url);
 		if(tab){
 			centerPanel.remove(tab);
 		}
 		var p = centerPanel.add(parent.Ext.create(url,{
			id:url,
			typeId:businessId,
			businessId:businessId,
			nodeId:nodeId,
			title: text,
			tabTip: text,
			closable:true
		}));
		centerPanel.setActiveTab(p);
 			
 	},
	
	initComponent : function() {
		var me = this;

		Ext.apply(me, {
			extraParams : {
				companyId : me.companyId
			},
			storeSorters : [{
				property : 'username',
				direction : 'asc'
			}],
			cols : [
	      	{
				dataIndex:'id',
				hidden:true,
				width:0
			},{
	            header: "风险名称",
	            dataIndex: 'name',
	            sortable: false,
	            //align: 'center',
	           	flex:4,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
     				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
     				+ "').showRiskDetails('" + record.get('id') + "')\">"+'<span style="font-size:13px;">'+value+'</span>'+"</a>";
     			}
	       	},{
	            header: "所属类别",
	            dataIndex: 'parentName',
	            sortable: false,
	            //align: 'center',
	          	flex:3
	            /*renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
     				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
     				+ "').showRiskDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px;">'+value+'</span>'+"</a>";
     			}*/
	       	},{
	            header: "责任部门",
	            dataIndex: 'dept',
	            sortable: false,
	            //align: 'center',
	           	flex:2
	            /*renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
     				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
     				+ "').showRiskDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px">'+value+'</span>'+"</a>";
     			}*/
	      	},{
				header: "水平",
				dataIndex:'status',
				sortable: false,
				flex:1,
				hidden:false,
	            renderer:function(v,metaData,record,colIndex,store,view) {
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
					return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
							+ "background-position: center top;' data-qtitle='' "
							+ "class='"
							+ v
							+ "'  data-qtip='"
							+ display
							+ "'>&nbsp</div>";
				}
			},{

				header: "趋势",
				dataIndex:'etrend',
				sortable: false,
				flex:1,
				hidden:false,
	            renderer:function(v,metaData,record,colIndex,store,view) {
					var color = "";
					var display = "";
					if (v == "up") {
						v = "icon-ibm-icon-trend-rising-positive"
						color = "icon_trend_rising_positive";
						display = FHD.locale
								.get("fhd.kpi.kpi.prompt.positiv");
					} else if (v == "flat") {
						v = "icon-ibm-icon-trend-neutral-null"
						color = "icon_trend_neutral_null";
						display = FHD.locale
								.get("fhd.kpi.kpi.prompt.flat");
					} else if (v == "down") {
						v = "icon-ibm-icon-trend-falling-negative"
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
			
			}]
		});
		me.callParent(arguments);
	}
});
