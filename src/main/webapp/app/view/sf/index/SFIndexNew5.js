/**
 * 新首页
 * 
 * @author 郝静
 */
Ext.define('FHD.view.sf.index.SFIndexNew5', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.sfindexnew',
    reload:true,
    
    onSelect:function(){
    	var me =this;
    	var date = me.value.getDate();
    	var year = me.value.getFullYear();
    	var month = me.value.getMonth();
    	var str = year+'-'+month+'-'+date
		var formPanel = Ext.create('FHD.view.sf.index.MemoForm',{
		});
		me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			title:'添加计划',
			width:500,
			height:350,
			layout: {
				type: 'fit',
	        	align:'stretch'
	        },
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			border:false,
			items : [formPanel],
			buttons: [{
	    				text: '保存',
	    				handler:function(){
	    					var panel = me.up('panel');
	    					formPanel.save(null,panel.memoGrid,me.formwindow,me.value);
	    				}
		    			},{
						text: '关闭',
						handler:function(){
							me.formwindow.close();
						}
				}]
		});
		me.formwindow.show();
    },
    
    showMemo : function(id){
    	var me = this;
 		var formPanel = Ext.create('FHD.view.sf.index.MemoForm',{
		});
    	formPanel.reloadData(id,me.memoGrid);
    },
    
    onMouseMoveFun : function(){
    	$(document).ready(function(){
        	$(".wrap div").hover(function() {
        		$(this).animate({"top": "-80px"}, 300, "swing");
        	},function() {
        		$(this).stop(true,false).animate({"top": "0px"}, 300, "swing");
        	});

        	});
    },
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	var result = me.reloadData();
    	var css = 'position: absolute;top: 130px;left: 150px;font-weight: bold;color: #3D7F9F;';
    	 
    	var html = '<div style="margin-top:20px">' + 
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.risk.define.DefineRiskMainPanel\',\'风险识别\');">' + 
			        '<img src=\'images/homepage/super_mono_3d_85.png\' border="0" style="width:75px; height:75px; padding:5px;float:left;margin:0 25px"/>' + 
			        '<span style="float:left;clear:both;margin: 0 35px">风险识别</span></a>' + 

        			'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.response.workplan.workplanmake.WorkPlanMakeMain\',\'风险应对\');">' + 
			        '<img src=\'images/homepage/super_mono_3d_86.png\' border="0" style="width:75px; height:75px; padding:5px;"/>' + 
			        '<span style="float:left;margin: 0 20px">风险应对</span></a>' + 
        	
					'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.kpi.kpipublish.KpiPublishMain\',\'考核评价\');">' + 
			        '<img src=\'images/homepage/super_mono_3d_19.png\' border="0" style="width:75px; height:75px; padding:5px;float:left;margin:0 25px"/>' + 
			        '<span style="float:left;clear:both;margin: 0 35px">考核评价</span></a>' + 
			        
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.icm.standard.StandardManage\',\'内控标准\');">' + 
			        '<img src=\'images/homepage/super_mono_3d_70.png\' border="0" style="width:75px; height:75px; padding:5px;"/>' + 
			        '<span style="float:left;margin: 0 20px">评估计划</span></a>' + 
			        
        			'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.risk.assess.report.ReportMainPanel\',\'系统报告\');">' + 
			        '<img src=\'images/homepage/super_mono_3d_part2_66.png\' border="0" style="width:75px; height:75px; padding:5px;float:left;margin:0 25px"/>' + 
			        '<span style="float:left;clear:both;margin: 0 35px">指标监控</span></a>' + 
					
			        
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.risk.assess.formulatePlan.FormulateMainPanel\',\'考核评价\');">' + 
			        '<img src=\'images/homepage/super_mono_3d_19.png\' border="0" style="width:75px; height:75px; padding:5px;"/>' + 
			        '<span style="float:left;margin: 0 20px">系统报告</span></a>' + 
			        
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.kpi.kpimonitor.KpiMonitorMain\',\'内控标准\');">' + 
			        '<img src=\'images/homepage/super_mono_3d_70.png\' border="0" style="width:75px; height:75px; padding:5px;float:left;margin:0 25px"/>' + 
			        '<span style="float:left;clear:both;margin: 0 35px">内控标准</span></a>' + 
			        
        			'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.sys.organization.ManPanel\',\'系统报告\');">' + 
			        '<img src=\'images/homepage/super_mono_3d_part2_66.png\' border="0" style="width:75px; height:75px; padding:5px;"/>' + 
			        '<span style="float:left;margin: 0 20px">机构管理</span></a>' +
					'</div>'
    
    	
    	me.backlogPanel = Ext.create('Ext.container.Container',{
        	flex:.9,
        	border:true,
    		style:'padding:1px 0px 0px 1px',
        	layout: {
                type: 'fit'
            },
            items: [result['bgrid']]
		});
		
		me.orgPanel = Ext.create('Ext.panel.Panel',{
        	flex:.1,
//        	height:20,
        	border:true,
        	style:'padding:1px 0px 0px 1px',
        	html:'风险组织结构'
		});
		
		me.con = Ext.create('Ext.container.Container',{
        	flex:1,
        	border:false,
			style:'padding:0px 0px 0px 0px',
        	layout: {
                type: 'vbox', 
                align: 'stretch'
          	},
            items: [me.backlogPanel]
		});
    	
    	me.chartPanel = Ext.create('FHD.ux.FusionChartPanel',{
			border:false,
			style:'padding:1px 0px 0px 1px',
			chartType:'AngularGauge',
			flex:1,
			xmlData:me.chart
		});
    	me.chartPanel2 = Ext.create('FHD.ux.FusionChartPanel',{
			border:false,
			style:'padding:1px 0px 0px 1px',
			chartType:'AngularGauge',
			flex:1,
			xmlData:me.chart4
		});
		
		me.kpiPanel = Ext.create('Ext.panel.Panel', {
            xtype: 'fieldset',
            flex: 1,
            layout: 'fit',
            border:true,
           	style:'padding:1px 0px 0px 1px',
           	tbar:['风险监控'/*,'->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").addFocusKpi()\' >添加指标</a>'*/]
        });

		var param = {"start":1,"limit":2,"type":"kpi","query":""};
        me.myFocusKpiPanel = Ext.create('FHD.view.kpi.homepage.myFocus', {
        	style:'padding:50px 0px 0px 1px',
            objectType: 'kpi',
            chartType: 'AngularGauge',
            queryUrl: __ctxPath + '/sf/index/createkpichartList.f',
            limit:2,
            searchContent: '输入指标名称',
            chartHeight: 130,
            chartWidth: 165,
            pcontainer: me
        });

        me.kpiPanel.add(me.myFocusKpiPanel);
		
		me.riskPanel = Ext.create('Ext.container.Container',{
        	flex:1,
        	border:true,
        	padding:'5 0',
			style:'padding:1px 0px 0px 1px',
        	layout: {
                type: 'fit' 
            },
            items: [result['rgrid']]
		});
		
    	me.datePanel = Ext.create('Ext.picker.Date',{
			border:false, 
//			style:'padding:2px 0px 0px 2px',
			flex:1 
		});
		me.datePanel.on('select', me.onSelect, me.datePanel);
		
		me.planPanel = Ext.create('Ext.container.Container',{
        	flex:.45,
        	border:true, 
			style:'padding:1px 0px 0px 1px',
        	layout: {
                type: 'fit'
            },
            items: [result['mgrid']]
		});
		
		me.dateCon = Ext.create('Ext.panel.Panel', {
			border:true,
          	flex:.76,
          	layout: {
                type: 'hbox',
                align: 'stretch'
          	},
         	items:[me.datePanel/*,me.planPanel*/]
    	});
    	me.modulePanel = Ext.create('Ext.panel.Panel',{
			border:true, 
//			style:'padding:5px 0px 0px 1px',
			padding:'5',
			flex:2.24,
			layout: {
				type: 'vbox',
                align: 'stretch'
//                type: 'table',    
//                columns: 3
            },
//			tbar:['模块快捷入口','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreVedios()\' >设置</a>'],	
			html:html, 
			items:[{xtype:'panel',
					border:false, 
					layout: {
					type: 'hbox',  
	                align: 'stretch' 
					},
					bbar:[{xtype:'field',width:'100',border:false},
					       {xtype:'button',text:'查询'}]
			}]
//			buttons: [{text:'收缩'}] 
			
//				,{
//				width:90,
//				xtype:'button',
//				text : "风险应对",
//				handler:me.save, 
//				margin:'20 10 0 30',
//				scope : this
//			},{
//				width:90,
//				xtype:'button',
//				text : "考核评价",
//				handler:me.save, 
//				margin:'20 10 0 30',
//				scope : this
//			},{
//				width:90,
//				xtype:'button',
//				text : "内控标准",
//				handler:me.save, 
//				margin:'20 10 0 30',
//				scope : this
//			},{
//				width:90,
//				xtype:'button',
//				text : "系统报告",
//				handler:me.save, 
//				margin:'20 10 0 30',
//				scope : this
//			}]
		});
		
    	
		me.newsPanel = Ext.create('Ext.container.Container',{
        	flex:1,
        	border:true,
			style:'padding:1px 0px 0px 1px',
			padding:'5 0',
        	layout: {
                type: 'fit'
            },
            items: [result['newsgrid']]
		});
    	
		var txwjPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			border:false,
			limit:2,
			typeId:'WDK0001',
			flex:1
		});
		var fxbgPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			border:false,
			limit:2,
			typeId:'WDK0002',
			flex:1
		});
		var fxjbPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			border:false,
			limit:2,
			typeId:'WDK0005',
			flex:1
		});
		var fxjhPanel = Ext.create('FHD.view.sf.index.SFRiskFiles',{
			border:false,
			limit:2,
			typeId:'WDK0007',
			flex:1
		});
		me.riskDocPanel = Ext.create('Ext.panel.Panel',{
        	flex:1,
        	border:true,
    		style:'padding:1px 0px 5px 1px',
    		tbar:['风险文档'],
    		layout: {
                type: 'vbox',
                align: 'stretch'
          	},
    		items:[
    			txwjPanel,fxbgPanel,fxjbPanel,fxjhPanel
    		]
		});
		
		me.systemSPanel = Ext.create('Ext.panel.Panel',{
        	flex:1,
        	border:false,
    		style:'padding:1px 0px 0px 1px',
        	layout: {
                type: 'fit',
                align: 'stretch'
            },
            items: [result['form']]
		});
		
    	
		me.vedioPanel = Ext.create('FHD.view.sf.index.SFVedioPanel2',{
		});
    	
    	
    	me.leftCon = Ext.create('Ext.container.Container', {
			border:true,
          	flex:2,
          	layout: {
                type: 'vbox',
                align: 'stretch' 
          	},
          	style:{padding:'0 5 0 0'},
         	items:[me.dateCon,me.modulePanel/*me.con,me.kpiPanel,me.riskPanel*/]
    	});
		me.midCon = Ext.create('Ext.container.Container', {
			border:true,
//			style:{padding:'2px'},
          	flex:4,
          	layout: {
                type: 'vbox',
                align: 'stretch'
          	},
         	items:[me.con,me.riskPanel,me.riskDocPanel/*,me.riskDocPanel*/]
    	});	
    	me.rightCon = Ext.create('Ext.container.Container', {
            flex:4,
            layout: {
                type: 'vbox',
                align: 'stretch'
          	},
          	style:{padding:'0 0 0 5'},
            items:[/*me.dateCon,*/me.kpiPanel,me.newsPanel,me.vedioPanel]
      	});
    	Ext.apply(me, {
    		layout: {
				type: 'hbox',
				align: 'stretch'
	        },
//	        padding:'5',
    		items:[me.leftCon,me.midCon,me.rightCon]
        });
        me.callParent(arguments);
    },

	//初始化数据
	reloadData:function(){
		var me = this;
		
		var riskGrid = Ext.create('FHD.ux.GridPanel',{
			url:__ctxPath + '/cmp/risk/findEventForIndex.f',
			extraParams:{type:"top10"},
			border:true,
			height:50,
			cols:[
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
     				+ "').showRiskDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px;">'+value+'</span>'+"</a>";
     			}
	       	},{
	            header: "发生时间",
	            dataIndex: 'parentName2',
	            sortable: false,
	            //align: 'center',
	          	flex:3,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
     				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
     				+ "').showRiskDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px;">'+value+'</span>'+"</a>";
     			}
	       	},{
	            header: "责任部门",
	            dataIndex: 'dept',
	            sortable: false,
	            //align: 'center',
	           	flex:3,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
     				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
     				+ "').showRiskDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px">'+value+'</span>'+"</a>";
     			}
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
			
			}],
	    	tbar:['重大风险'/*,'->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreRisks()\' >更多...</a>'*/],
	    	hideHeaders:false,
			checked: false,
		    searchable : false,
		    columnLines: true,
		    storeGroupField: '',
		    pagable : false
		});
		var fileGrid = Ext.create('FHD.ux.GridPanel',{
			url:__ctxPath + '/app/view/sf/grid4.json',
			flex:7,
			border:true,
			height:50,
			cols:[{	header: "序",
 	    	   sortable: false,
	    	   dataIndex:'sort',
	    	   width:40
			},{
				dataIndex:'riskId',
				hidden:true,
				width:0
			},{
				dataIndex:'phone',
				flex:.3,
				hidden:true,
				renderer:function(value,metaData,record,colIndex,store,view) {
					return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
					+ record.get('phone') + "'/>";
				}
			},{
				header: "风险名称",
				dataIndex: 'riskName',
				sortable: false,
				//align: 'center',
				flex:1,
				renderer:function(value,metaData,record,colIndex,store,view) {
					metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
					return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
					+ "').showPolicyDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px;text-decoration: underline;">'+value+'</span>'+"</a>";
				}
			}],
			tbar:['风险文档','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showPolicyDetails()\' >更多...</a>'],
			hideHeaders:true,
			checked: false,
			pagable : false,
			searchable : false,
			storeGroupField: ''
		});
		me.backlogGrid = Ext.create('FHD.ux.GridPanel',{
			url:__ctxPath + '/jbpm/processInstance/jbpmHistActinstPage.f',
			extraParams:{assigneeId:__user.empId,endactivity:"execut1",dbversion:0},
			border:true,
			height:50,
			cols:[	
			      	{dataIndex: 'executionId', invisible:true},
					{dataIndex: 'businessId', invisible:true},
					{dataIndex: 'form', invisible:true},
					{dataIndex: 'activityName',invisible:true},
	    			{
	    				header: FHD.locale.get('fhd.common.operate'), 
	    				dataIndex: 'operate', 
	    				sortable: false, 
	    				flex : .2,
	    				align:'center',
	    				renderer: function(value, metaData, record, colIndex, store, view) { 
	                   		 return "<a href=\"javascript:void(0);\">执行<input name='url' type='hidden' value='"+record.get("form")+"'><input name='executionId' type='hidden' value='"+record.get("executionId")+"'><input name='businessId' type='hidden' value='"+record.get("businessId")+"'></a>";
						},
						listeners:{
		            		click:{
		            			fn:me.execute
		            		}
		            	}
					},{
						header: FHD.locale.get('fhd.pages.test.field.name'), 
						dataIndex: 'businessName', 
						sortable: false,
						flex :.6,
						align:'center',
						renderer:function(value, metaData, record, rowIndex, colIndex, store){
							return value + " ( "+record.get("activityName")+" ) ";
				   	 	}
					},
			      	{
	    	            header: "流程节点",
	    	            dataIndex: 'disName',
	    	            sortable: false,
	    	            align: 'center',
	    	            flex:.2,
	    	            renderer:function(value,metaData,record,colIndex,store,view) {
	    	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
	         				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
	         				+ "').showRiskDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px">'+value+'</span>'+"</a>";
	         			}
	    	       },
			       {
			      		header: "完成比例",
			      		dataIndex: 'rate',
			      		sortable: false,
			      		flex:.2,
			      		align:'center',
						renderer:function(value, metaData, record, rowIndex, colIndex, store){
							return value + "%";				
					    }
				}],
	    	tbar:['待办工作'/*,'->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreBacklogs("'+me.id+'")\' >更多...</a>'*/],
	    	hideHeaders:false,
			checked: false,
		    pagable : false,
		    searchable : false,
		    columnLines: true,
//		    storeGroupField: '',
		    storeGroupField:false
		});

		var financeGrid = Ext.create('FHD.ux.GridPanel',{
			url:__ctxPath + '/app/view/sf/zdcxgrid.json',
			border:true,
			height:50,
			cols:[	{	header: "序",
		 	    	   sortable: false,
			    	   dataIndex:'sort',
			    	   width:40
					},
			      	{
	    				dataIndex:'riskId',
	    				hidden:true,
	    				width:0
	    			},{
	    	            header: "名称",
	    	            dataIndex: 'riskName',
	    	            sortable: false,
	    	            //align: 'center',
	    	            flex:.6,
	    	            renderer:function(value,metaData,record,colIndex,store,view) {
	    	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
	         				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
	         				+ "').showRiskDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px">'+value+'</span>'+"</a>";
	         			}
	    	       },
			      	{
	    	            header: "制度类别",
	    	            dataIndex: 'parentName',
	    	            sortable: false,
	    	            //align: 'center',
	    	            flex:.2,
	    	            renderer:function(value,metaData,record,colIndex,store,view) {
	    	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
	         				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
	         				+ "').showRiskDetails('" + record.get('riskId') + "')\">"+'<span style="font-size:13px">'+value+'</span>'+"</a>";
	         			}
	    	       },
			      	{
	    			dataIndex:'search',
	    			flex:.15,
	    			hidden:false,
	    			renderer:function(value,metaData,record,colIndex,store,view) {
	    				return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
	    				+ record.get('search') + "'/>";
	    			}
    		}],
	    	tbar:['制度查询'],
	    	hideHeaders:false,
			checked: false,
		    pagable : false,
		    searchable : false,
		    columnLines: true
		});
		
		var newsGrid = Ext.create('FHD.ux.GridPanel',{
    		url:__ctxPath + '/app/view/sf/news.json',
    		border:true,
    		height:50,
    		cols:[
    		      {
    			dataIndex:'riskId',
    			hidden:true,
    			width:0
    		},{
    			header: "风险名称",
    			dataIndex: 'riskName',
    			sortable: false,
    			//align: 'center',
    			flex:1,
    			renderer:function(value,metaData,record,colIndex,store,view) {
    				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
    				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
    				+ "').showNews('" + record.get('riskId') + "')\">"+'<span style="font-size:13px;text-decoration: underline;">'+value+'</span>'+"</a>";
    			}
    		},{
    			dataIndex:'phone',
    			flex:.15,
    			hidden:false,
    			renderer:function(value,metaData,record,colIndex,store,view) {
    				return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
    				+ record.get('phone') + "'/>";
    			}
    		}],
    		tbar:['新闻公告','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("risk")\' >更多...</a>'],	
    		hideHeaders:true,
    		checked: false,
    		pagable : false,
    		searchable : false,
    		storeGroupField: '',
    		columnLines: true
    	});
		var comName = {
//				style:'width: 50%',
				xtype:me.isView?'displayfield':'textfield',
				lblAlign:'right',
				fieldLabel:'名称',
				value:'',
				name:'comName',
				margin:'20 10 0 40',
				maxLength:200,
				width : 300
		};
		
		var riskStatusStore = Ext.create('Ext.data.Store',{
			fields : ['id', 'name'],
			data : [
				{'id':'01', 'name':'文件'},
				{'id':'02', 'name':'制度'}
			]
		});
		var comType = Ext.create('Ext.form.ComboBox',{
		    fieldLabel: '类型',
			store : riskStatusStore,
			emptyText:'请选择',
			name:'riskStatus',
			margin : '30 10 0 40',
			displayField : 'name',
			editable : false,
			width : 300
		});
		
		var search = {
				xtype:'button',
				text : "查询",
				iconCls: 'icon-magnifier ',
				handler:me.save, 
				margin:'30 10 0 180',
				scope : this
		}
		var formPanel = Ext.create('Ext.form.Panel',{
            style:'padding:0px 0px 0px 0px',
            tbar:['高级查询'],
        	layout: {
				type: 'vbox'
//				align: 'stretch'
	        },
			items:[comType,comName,search]
		});
		
		me.memoGrid = Ext.create('FHD.ux.EditorGridPanel',{
    		url:__ctxPath + '/sf/index/findmemolist.f',
    		border:true,
    		height:50,
    		cols:[
    		{
    			dataIndex:'memoId',
    			hidden:false,
    			width:0
    		},{
    			header: "计划名称",
    			dataIndex: 'memoName',
    			sortable: false,
    			//align: 'center',
    			flex:1.5,
    			renderer:function(value,meta, record){
	    				var id = record.data.id;
	    				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').showMemo('" + id + "')\">"+value+"</a>";
           		}
    		},{
    			header: "计划时间",
    			dataIndex:'memoTime',
    			flex:2,
    			hidden:false
    		},{header:'操作',
    		   dataIndex:'',
    		   hidden:false,
    		   editor:false,
    		   align:'center',
    		   flex:.5,
		       xtype:'actioncolumn',
		       items: [{
	                icon: __ctxPath+'/images/icons/delete_icon.gif',  // Use a URL in the icon config
	                tooltip: FHD.locale.get('fhd.common.delete'),
	                handler: function(grid, rowIndex, colIndex) {
	                	//点击编辑按钮时，自动选中行
	                   var rec = grid.getStore().getAt(rowIndex).data.id;
	                   FHD.ajax({
//	                   	async:false,
                        url:  __ctxPath+'/sf/index/deletememo.f',
                        params: {
                           id:rec
                        },
                        callback: function (data) {
                            if (data) {
                                Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
    							me.memoGrid.store.load();
                            }
                        }
                    	});
	                }
	            }]
			}],
    		tbar:['<div style="padding:0 0 0 65px">记事簿</div>'],	
    		hideHeaders:true,
    		checked: false,
    		pagable : false,
    		searchable : false,
    		columnLines: true
    	});
		 
		return {
				rgrid:riskGrid,
				fgrid:fileGrid,
				bgrid:me.backlogGrid,
				newsgrid:newsGrid,
				mgrid:me.memoGrid,
				form:formPanel

			};
	},
	showMoreBacklogs:function(id){
		var me = this;
		var url = 'FHD.view.bpm.mywork.MyTask';
	    var idurl = url;
	    var centerPanel = Ext.getCmp('center-panel');
	    var tab = centerPanel.getComponent(idurl);
	    if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			if(url.startWith('FHD')){
			    var myTask = Ext.create(url, {
			        closable: true,
			        id:idurl,
			        title:'待办工作'
			    });
			    var p = centerPanel.add(myTask);
			    centerPanel.setActiveTab(p);
			}
		}
	},
	
	showMoreRisks:function(){
	//制度详细查看
		var me = this;
		me.riskList = Ext.create('FHD.view.sf.index.MoreRisksGridPanel',{
			border:false,
			layout:'fit',
			checked: false
		});
		me.formwindow = new Ext.Window({
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			title:'风险列表',
			width:800,
			height:600,
			layout: {
				type: 'fit'
	        },
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.riskList],
			buttons: [{
	    				text: '关闭',
	    				handler:function(){
	    					me.formwindow.close();
	    				}
	    			}]
		});
		me.formwindow.show();
	},
	
	addFocusKpi:function(){    
		
		var selectorWindow = Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow', {
            multiSelect: true,
            onSubmit: function(store) {
//	                var idArray = [];
//	                var items = store.data.items;
//	                Ext.Array.each(items,
//	                function(item) {
//	                    idArray.push(item.data.id);
//	                });
//	                if (idArray.length > 0) {
            		var me = this;
			        var strIdList = [];
			        var scIdList = [];
			        var kpiIdList = [];
			        var para = {
			            'sm': strIdList,
			            'sc': scIdList,
			            'kpi': kpiIdList
			        };
			        var kpiItems = store.data.items;
			        Ext.Array.each(kpiItems, function (obj) {
			            var item = obj.data;
			            var id = item.id;
			            var name = item.name;
			            kpiIdList.push({
			                id: id,
			                isfocus: '0yn_y'
			            });
			        });
					
//				        var kpifocusItems = me.kpifocusgrid.valueMap;
//				        Ext.Array.each(kpifocusItems, function (obj) {
//				            kpiIdList.push(obj);
//				        });
                    FHD.ajax({
			            url: __ctxPath + '/kpi/addobjectfocus.f',
			            params: {
			                items: Ext.JSON.encode(para)
			            },
			            callback: function (data) {
			                if (data && data.success) {
			                	var resultContainer =  Ext.ComponentQuery.query("container[reload=true]") ;
				                if(resultContainer&&resultContainer.length>0){
									 resultContainer[0].myFocusKpiPanel.reload();
								}
			                }
			            }
			        });
			        
//	                }
            }
        }).show();  
        selectorWindow.addComponent();
    },
    execute : function (grid, ele, rowIndex){
    	var jEle=jQuery(ele);
    	var me = this;
		var winId = "win" + Math.random()+"$ewin";
		var taskPanel = Ext.create(jEle.find("[name='url']").val(),{
			executionId : jEle.find("[name='executionId']").val(),
			businessId : jEle.find("[name='businessId']").val(),
			winId: winId
		});
		
		var window = Ext.create('FHD.ux.Window',{
			id:winId,
			title:FHD.locale.get('fhd.common.execute'),
			iconCls: 'icon-edit',//标题前的图片
			maximizable: true,
			listeners:{
				close : function(){
					var resultContainer =  Ext.ComponentQuery.query("container[reload=true]") ;
	                if(resultContainer&&resultContainer.length>0){
						 resultContainer[0].backlogGrid.store.load();
					}
				}
			}
		});
		window.show();
		window.add(taskPanel);
		taskPanel.reloadData();
	},
	showNews:function(){
	//制度详细查看
		var me = this;
		me.riskList = Ext.create('Ext.panel.Panel',{
			border:false,
			layout:'fit',
			checked: false
		});
		me.riskList = Ext.create('Ext.panel.Panel',{
			border:false,
			layout:'fit',
			checked: false
		});
		me.formwindow = new Ext.Window({
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			title:'新闻公告',
			width:400,
			height:300,
			layout: {
				type: 'fit'
	        },
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [
				{
			        xtype: 'panel',
			        flex: 1,
			        html:' &nbsp &nbsp &nbsp &nbsp &nbsp谢根华同志任中航工业沈飞董事长<br><br> &nbsp &nbsp &nbsp &nbsp谢根华同志任中航工业沈飞董事长，袁立同志任中航工业沈飞总经理、党委副书记<br>'		
			    }
			],
			buttons: [{
	    				text: '关闭',
	    				handler:function(){
	    					me.formwindow.close();
	    				}
	    			}]
		});
		me.formwindow.show();
	}
});