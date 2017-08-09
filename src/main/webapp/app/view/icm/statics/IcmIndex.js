Ext.define('FHD.view.icm.statics.IcmIndex', {
	extend: 'Ext.container.Container',
    alias: 'widget.icmindex',
	border: false,
	title: '首页',
    orgId: '',
    layout: 'fit',
    style: 'background:#fff',
    // 初始化方法
    initComponent: function() {
		var me = this;
		me.limit = 5;
		me.gridHeight = me.limit*24+30;
		
		me.isIcDept = '';
		FHD.ajax({								//判断是否为内控部门
			url: __ctxPath + '/icm/statics/judgeificmdept.f',
            async:false,
            callback: function (data) {
            	me.isIcDept = data;
            }
		});
		
        me.callParent(arguments);
        
        me.tbspacer = {
            xtype:'tbspacer',
            width:5,
            height:5
        };
        me.panel = Ext.create('Ext.container.Container', {	        
	    	border:false,
	        layout: {
				type: 'vbox',
	        	align:'stretch'
	        }
	    });
	    me.myTaskGrid = Ext.create('FHD.view.bpm.mywork.MyTask',{
			tbar: [
				'我的待办',
				'-',
				{
					iconCls:'icon-arrow-refresh-blue',
					handler:function(){
						me.myTaskGrid.reloadData();
					}
				}
			],
			pagable:false,
			margin: '3 5 3 5',
			flex:2,
			searchable:false
		});
    	if(me.isIcDept){
			me.menuIndexFieldSet = Ext.create('Ext.form.FieldSet',{
				title:'高级查询',
				margin: '3 5 3 5',
				collapsed: false,
				layout: {
					type: 'vbox',
					padding:'5',
                    align:'center'
		        },
		        defaults:{
		        	//height:35,
		        	width: 100,
		        	margin:'0 0 5 0'
		        },
				items:[
					/*{
				    	xtype: 'button',
				    	text: '标准维护/更新',
				    	name: 'standardplan',
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },
				    {
				    	xtype: 'button',
				    	text: '体系建设/更新',
				    	name: 'constructplan',
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },
				    {
				    	xtype: 'button',
				    	text: '评价计划',
				    	name: 'assessplan',
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },
				    {
				    	xtype: 'button',
				    	text: '整改优化计划',
				    	name: 'rectifyplan',
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },*/
				    {
				    	xtype: 'button',
				    	text: '标准',
				    	name: 'standard',
				    	flex:1,
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },
				    {
				    	xtype: 'button',
				    	text: '流程',
				    	name: 'process',
				    	flex:1,
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },
				    {
				    	xtype: 'button',
				    	text: '控制矩阵',
				    	name: 'risk',
				    	flex:1,
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },
				    /*{
				    	xtype: 'button',
				    	text: '控制措施',
				    	name: 'controlMeasure',
				    	flex:1,
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },*/
				    {
				    	xtype: 'button',
				    	text: '缺陷',
				    	name: 'defect',
				    	flex:1,
				    	handler:me.searchObjectQuery,
				    	scope:this
				    },
				    {
				    	xtype: 'button',
				    	text: '评价结果',
				    	name: 'assessresult',
				    	flex:1,
				    	handler:me.searchObjectQuery,
				    	scope:this
				    }
				]
			});

			me.assessPlanGrid = Ext.create('FHD.ux.GridPanel',{
				tbar:['评价计划'],
				url: __ctxPath + '/icm/assess/findAssessPlanListByParams.f?companyId='+__user.companyId+'&dealStatus=H,F,A',
				cols: [
					{dataIndex:'id',hidden:true},
					{ header: '评价计划名称',  dataIndex: 'name' ,flex: 2,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'"';
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','assessplan')\" >" + value + "</a>"; 
						}
					},
					{header: '实际进度', dataIndex: 'actualProgress',sortable: false,  width: 50,
						renderer:function(value,metaData,record,colIndex,store,view) {
							if(record.data.dealStatus!='N'){
								return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessViewList('" + record.data.id +"')\" >" + value +'%' + "</a>"; 
							}
							if(value){
								value = value +'%';
							}
							return value;
						}
					},
					{ header: '更新日期', dataIndex: 'createTime', width: 90}
				],
				height: me.gridHeight,
				extraParams:{limit:me.limit},
				border:false,
				checked:false,
				hideHeaders:true,
				searchable:false,
				pagable : false
			});
			
			me.rectifyPlanGrid = Ext.create('FHD.ux.GridPanel',{
				tbar:['整改优化计划'],
				url:__ctxPath + '/icm/improve/findImproveListBypage.f?companyId='+__user.companyId+'&dealStatus=H,F',
				cols: [
					{dataIndex:'id',hidden:true},
					{ header: '整改计划名称',  dataIndex: 'name' ,flex: 2,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'"';
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','rectifyplan')\" >" + value + "</a>"; 
						}
					},
					{header: '实际进度', dataIndex: 'actualProgress',sortable: false,  width: 50,
						renderer:function(value,metaData,record,colIndex,store,view) {
							if(record.data.dealStatus!='N'){
								return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessViewList('" + record.data.id +"')\" >" + value +'%' + "</a>"; 
							}
							if(value){
								value = value +'%';
							}
							return value;
						}
					},
					{ header: '更新日期', dataIndex: 'createTime', width: 90}
				],
				height: me.gridHeight,
				extraParams:{limit:me.limit},
				border:false,
				checked:false,
				hideHeaders:true,
				searchable:false,
				pagable : false
			});
			
			me.constructPlanGrid = Ext.create('FHD.ux.GridPanel',{
				tbar:['体系建设/更新计划'],
				url:__ctxPath + '/icm/icsystem/constructplan/findconstructplansbypage.f?companyId='+__user.companyId+'&status=P,D',
				cols: [
					{dataIndex:'id',hidden:true},
					{ header: '体系计划名称',  dataIndex: 'name' ,flex: 2,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'"';
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','constructplan')\" >" + value + "</a>"; 
						}
					},
					{header: '实际进度', dataIndex: 'actualProgress',sortable: false, width: 50,
						renderer:function(value,metaData,record,colIndex,store,view) {
							if(record.data.dealStatus!='N'){
								return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessViewList('" + record.data.id +"')\" >" + value +'%' + "</a>"; 
							}
							if(value){
								value = value +'%';
							}
							return value;
						}
					},					
					{ header: '更新日期', dataIndex: 'createTime', width: 90}
				],
				height: me.gridHeight,
				extraParams:{limit:me.limit},
				border:false,
				checked:false,
				hideHeaders:true,
				searchable:false,
				pagable : false
			});
        	me.constructplancountchart = Ext.create('FHD.view.icm.statics.ConstructPlanCountChart',{
				flex:1,
				toolRegion:'west'
			});
        	me.assessplancountchart = Ext.create('FHD.view.icm.statics.AssessPlanCountChart',{
        		flex:1,
				toolRegion:'west'
			});
			me.improvecountchart = Ext.create('FHD.view.icm.statics.ImproveCountChart',{
				flex:1,
				toolRegion:'west'
			});
			
			me.constructPlanPanel = Ext.create('Ext.panel.Panel',{
				flex:1,
				layout: {
					type: 'vbox',
			    	align:'stretch'
			    },
				items:[
					me.constructplancountchart,
					me.constructPlanGrid
				]
			});
			
			me.assessPlanPanel = Ext.create('Ext.panel.Panel',{
				flex:1,
				layout: {
					type: 'vbox',
			    	align:'stretch'
			    },
				items:[
					me.assessplancountchart,
					me.assessPlanGrid
				]
			});
			
			me.rectifyPlanPanel = Ext.create('Ext.panel.Panel',{
				flex:1,
				layout: {
					type: 'vbox',
			    	align:'stretch'
			    },
				items:[
					me.improvecountchart,
					me.rectifyPlanGrid
				]
			});
			
        	me.planContainer = Ext.create('Ext.container.Container',{
				layout: {
					type: 'hbox',
			    	align:'stretch'
			    },
			    margin:'5 0 0 0',
			    border:false,
			    flex:4.5,
				items:[
					me.tbspacer,
				    me.constructPlanPanel,
				    me.tbspacer,
				    me.assessPlanPanel ,
				    me.tbspacer,
				    me.rectifyPlanPanel,
				    me.tbspacer
				]
			});
			
        	//me.panel.add(me.planFieldSet);
			me.panel.add(Ext.create('Ext.container.Container',{
				layout: {
					type: 'hbox',
		        	align:'stretch'
		        },
		        flex:2.5,
				items:[
					me.myTaskGrid,
					Ext.create('FHD.view.icm.statics.StandardCountChart',{
						border:true,
						flex:1.5,
						margin: '3 5 3 5',
						extraParams:{orgId:me.orgId},
						myType: 'pieChart',
						myXCol: {header:'内控要素',dataIndex:'controlPoint'},
						myYCol: '',
						toolRegion:'west'
					}),
					me.menuIndexFieldSet
				]
			}));
        	me.panel.add(me.planContainer);
        	//me.panel.add(me.menuIndexFieldSet);
        	
        }else{
        	 me.processGrid = Ext.create('FHD.ux.GridPanel', {
				tbar:['流程','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("process")\' >更多...</a>'],
				cols: [
					{dataIndex:'id',hidden: true},
					{ header: '流程编号',  dataIndex: 'processCode' ,flex: 1 , hidden: true,
						renderer:function(value,metaData,record,colIndex,store,view) {
								metaData.tdAttr = 'data-qtip="'+value+'" '; 
								return value;
						}
					},
					{ header: '流程名称', dataIndex: 'processName', flex: 2 ,
						renderer:function(value,metaData,record,colIndex,store,view) {
								metaData.tdAttr = 'data-qtip="'+value+"("+record.data.processCode+")"+'" '; 
								return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','process')\" >" + value + "</a>"; 
						}
					},
					{ header: '更新日期', dataIndex: 'updateDate', width: 90}
				],
				url: __ctxPath+'/icm/statics/findprocessbysome.f',
				height: me.gridHeight,
				//width: me.gridWidth,
				flex:1,
				extraParams:{limit:me.limit},
				checked:false,
				hideHeaders:true,
				searchable:false,
				pagable : false
			});	
//			me.riskGrid = Ext.create('FHD.ux.GridPanel', {
//				tbar:['风险','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("risk")\' >更多...</a>'],
//				url:__ctxPath + '/icm/statics/findriskbysome.f',
//				cols: [
//					{dataIndex:'id',hidden:true},
//					{ header: '风险编号',  dataIndex: 'riskCode' ,flex: 1 ,hidden: true,
//						renderer:function(value,metaData,record,colIndex,store,view) {
//							metaData.tdAttr = 'data-qtip="'+value+'" '; 
//							return value;
//						}
//					},
//					{ header: '风险名称', dataIndex: 'riskName', flex: 2  ,
//						renderer:function(value,metaData,record,colIndex,store,view) {
//							metaData.tdAttr = 'data-qtip="'+value+"("+record.data.riskCode+")"+'" '; 
//								return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','risk')\" >" + value + "</a>"; 
//						}
//					},
//					{ header: '更新日期', dataIndex: 'updateDate', width: 90}
//				],
//				height: me.gridHeight,
//				//width: me.gridWidth,
//				flex:1,
//				extraParams:{limit:me.limit},
//				checked:false,
//				hideHeaders:true,
//				searchable:false,
//				pagable : false
//			});
			me.standardGrid = Ext.create('FHD.ux.GridPanel', {
				tbar:['标准','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("standard")\' >更多...</a>'],
				url:__ctxPath + '/icm/statics/findstandardbysome.f',
				cols: [
					{dataIndex:'id',hidden:true},
					{ header: '要求编号',  dataIndex: 'standardCode' ,flex: 1 ,hidden: true,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'" '; 
							return value;
						}
					},
					{ header: '要求名称', dataIndex: 'standardName', flex: 2  ,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+"("+record.data.standardCode+")"+'"'; 
								return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','standard')\" >"+  value + "</a>"; 
						}
					},
					{ header: '更新日期', dataIndex: 'updateDate', width: 90}
				],
				height: me.gridHeight,
				//width: me.gridWidth,
				flex:1,
				extraParams:{limit:me.limit},
				checked:false,
				hideHeaders:true,
				searchable:false,
				pagable : false
			});	
			me.controlMeasureGrid = Ext.create('FHD.ux.GridPanel', {
				tbar:['控制措施','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("controlMeasure")\' >更多...</a>'],
				url:__ctxPath + '/icm/statics/findcontrolmeasurebysome.f',
				cols: [
					{dataIndex:'id',hidden:true},
					{ header: '控制编号',  dataIndex: 'measureCode' ,flex: 1 ,hidden: true,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'" '; 
							return value;
						}
					},
					{ header: '控制名称', dataIndex: 'measureName', flex: 2  ,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+"("+record.data.measureCode+")"+'"'; 
								return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','controlMeasure')\" >"+  value + "</a>"; 
						}
					},
					{ header: '更新日期', dataIndex: 'updateDate', width: 90}
				],
				height: me.gridHeight,
				//width: me.gridWidth,
				flex:1,
				extraParams:{limit:me.limit},
				checked:false,
				hideHeaders:true,
				searchable:false,
				pagable : false
			});	
			me.assessResultGrid = Ext.create('FHD.ux.GridPanel',{
				tbar:['评价结果','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("assessresult")\' >更多...</a>'],
				url:__ctxPath + '/icm/statics/findassessresultbysome.f',
				cols: [
					{dataIndex:'id',hidden:true},
					{ header: '评价点',  dataIndex: 'assessPointName' ,flex: 2,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'"';
							return value; 
						}
					},
					{ header: '样本有效状态', dataIndex: 'isQualified', width: 90,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'"';
							return value; 
						}
					},
					{ header: '更新日期', dataIndex: 'updateDate', width: 90}
				],
				height: me.gridHeight,
				//width: me.gridWidth,
				flex:1,
				extraParams:{limit:me.limit},
				checked:false,
				hideHeaders:true,
				searchable:false,
				pagable : false
			});
			
			me.defectGrid = Ext.create('FHD.ux.GridPanel',{
				tbar:['缺陷','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("defect")\' >更多...</a>'],
				url:__ctxPath + '/icm/statics/finddefectbysome.f',
				cols: [
					{dataIndex:'id',hidden:true},
					{ header: '缺陷描述',  dataIndex: 'defectDesc' ,flex: 2,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'"';
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','defect')\" >" + value + "</a>"; 
						}
					},
					{ header: '责任部门', dataIndex: 'orgName', flex: 1,hidden: true,
						renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'"';
							return value; 
						}
					},
					{ header: '更新日期', dataIndex: 'updateDate', width: 90}
				],
				height: me.gridHeight,
				//width: me.gridWidth,
				flex:1,
				extraParams:{limit:me.limit},
				checked:false,
				hideHeaders:true,
				searchable:false,
				pagable : false
			});
	        
	        me.latestFieldSet = Ext.create('Ext.form.FieldSet',{
				title:'最新动态',
				margin:'4 0 4 0',
				collapsed: true
			});
			me.latestContainer = Ext.create('Ext.container.Container',{
				layout: {
					type: 'vbox',
			    	align:'stretch'
			    },
			    border:false,
			    flex:4,
				items:[
					Ext.create('Ext.container.Container',{
						layout: {
							type: 'hbox',
					    	align:'stretch'
					    },
					    border:false,
					    flex:1,
						items:[
							me.tbspacer,
							me.standardGrid,
							me.tbspacer,
						    me.processGrid,
						    me.tbspacer,
					    	//me.riskGrid,
					    	me.tbspacer
						]
					}),
					me.tbspacer,
					Ext.create('Ext.container.Container',{
						layout: {
							type: 'hbox',
					    	align:'stretch'
					    },
					    border:false,
					    flex:1,
						items:[
							me.tbspacer,
					    	me.controlMeasureGrid,
					    	me.tbspacer,
					    	me.defectGrid,
							me.tbspacer,
					    	me.assessResultGrid,
					    	me.tbspacer
						]
					})
				]
			});
			me.panel.add(me.myTaskGrid);
	        me.panel.add(me.latestFieldSet);
	        me.panel.add(me.latestContainer);
        }
        me.add(me.panel);
    },
    showProcessView:function(id,type){
    	var me = this;
    	var map = {
	    	process:'FHD.view.icm.icsystem.bpm.PlanProcessEditTabPanelForView',        //流程
	    	risk:'FHD.view.icm.icsystem.form.RiskEditFormForView',       //风险
	    	/*rule:'FHD.view.icm.rule.RuleEditPanelForView',		         //制度*/
	    	standard:'FHD.view.icm.standard.form.StandardControlPlanPreview',
	    	controlMeasure:'FHD.view.icm.icsystem.form.MeaSureEditFormForView', //控制措施
	    	defect:'FHD.view.icm.defect.form.DefectFormForView',
	    	assessplan:'FHD.view.icm.assess.form.AssessPlanPreview',     //评价计划
	    	rectifyplan:'FHD.view.icm.rectify.form.ImproveViewForm',     //整改计划
	    	constructplan:'FHD.view.icm.icsystem.constructplan.form.ConstructPlanRangeFormForView'    //体系计划
    	};
    	if(!map[type]){
    		alert("努力实现中");
    		return; 
    	}
    	var grid = null;
    	if(type=='process'){
    		grid = Ext.create(map[type],{paramObj:{processId:id},readOnly:true});
    	}
    	else if(type=='risk'){
    		grid = Ext.create(map[type],{paramObj:{processRiskId:id}});
    		grid.getInitData();
    	}
    	else if(type=='standard'){
    		grid = Ext.create(map[type]);
    		grid.initParam({
			 	standardControlId : id
			});
    	}
    	else if(type=='defect'){
    		grid = Ext.create(map[type],{defectId:id});
    	}
    	else if(type=='controlMeasure'){
    		grid = Ext.create(map[type],{paramObj:{measureId:id}});
    	}
    	else if(type=='assessplan'){
    		grid = Ext.create(map[type],{assessPlanId:id});
    	}
    	else if(type=='rectifyplan'){
    		grid = Ext.create(map[type],{improveId:id});
    	}
    	else if(type=='constructplan'){
    		grid = Ext.create(map[type],{paramObj:{businessId:id}});
    	}
    	
    	grid.reloadData();
    	me.win=Ext.create('FHD.ux.Window',{
			title : '详细查看',
			flex:1,
			autoHeight:true,
			collapsible : true,
			modal : true,
			maximizable : true
		}).show();
		me.win.add(grid);
    },
    showMoreProcessInfo:function(key){
		var me = this;
		var menu = {
			process:['FHD.view.icm.statics.IcmMyProcessInfo','流程'],	          //流程
			risk:['FHD.view.icm.statics.RiskControlMatrix','控制矩阵'],			          //风险
			standard:['FHD.view.icm.statics.IcmMyStandardInfo','标准'],	          //标准
			defect:['FHD.view.icm.statics.IcmMyDefectInfo','缺陷'],		          //缺陷
			controlMeasure:['FHD.view.icm.statics.IcmMyControlInfo','控制措施'],		  
			assessresult:['FHD.view.icm.statics.AssessResultStaticsGrid','评价结果']
		};
		menu.url = menu[key][0];
		menu.text = menu[key][1];
		
		var url = menu.url;
		var centerPanel = Ext.getCmp('center-panel');
		var tab = centerPanel.getComponent(url);
		if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			if(url.startWith('FHD')){
				var info = Ext.create(url,{
					id:url,
					title: menu.text,
					tabTip:menu.text,
					closable:true
				});
				info.reloadData();
				var p = centerPanel.add(info);
				p.initParam({orgId:me.orgId});
				centerPanel.setActiveTab(p);
			}
		}
	},
	searchObjectQuery:function(button){
		var me = this;
		var menu = {
			process:['FHD.view.icm.statics.IcmMyProcessInfo','流程'],	          //流程
			risk:['FHD.view.icm.statics.RiskControlMatrix','控制矩阵'],			          //风险
			standard:['FHD.view.icm.statics.IcmMyStandardInfo','标准'],	          //标准
			defect:['FHD.view.icm.statics.IcmMyDefectInfo','缺陷'],		          //缺陷
			controlMeasure:['FHD.view.icm.statics.IcmMyControlInfo','控制措施'],		          //控制措施
			assessresult:['FHD.view.icm.statics.AssessResultStaticsGrid','评价结果'],
			standardplan:['FHD.view.icm.standard.StandardPlan','标准维护/更新'],
			assessplan:['FHD.view.icm.assess.AssessPlan','评价计划'],     //评价计划
			rectifyplan:['FHD.view.icm.rectify.RectifyImprovePanel','整改优化计划'],   //整改计划
			constructplan:['FHD.view.icm.icsystem.constructplan.ConstructPlanMainPanel','体系建设/更新']//体系计划
		};
		menu.url = menu[button.name][0];
		menu.text = menu[button.name][1];
		
		var url = menu.url;
		var centerPanel = Ext.getCmp('center-panel');
		var tab = centerPanel.getComponent(url);
		if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			if(url.startWith('FHD')){
				var info = Ext.create(url,{
					id:url,
					title: menu.text,
					tabTip:menu.text,
					closable:true
				});
				info.reloadData();
				var p = centerPanel.add(info);
				p.initParam({orgId:me.orgId});
				centerPanel.setActiveTab(p);
			}
		}
	},
	showProcessViewList:function(id){
		var me=this;
		me.processInstanceView =Ext.create('FHD.view.bpm.processinstance.ProcessInstanceView',{
			businessId:id
		});
		me.processInstanceView.reloadData();
		var popWin = Ext.create('FHD.ux.Window',{
			title:'进度信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
		}).show();
		popWin.add(me.processInstanceView);
	},
    reloadData:function(){
    	var me=this;
    	if(!me.orgId){
    		if(me.isIcDept){
    			me.orgId = __user.companyId;
    		}else{
    			me.orgId = __user.majorDeptId
    		}
    	}
    	if(me.isIcDept){
    		if(me.orgType && (me.orgType =='0orgtype_c' || me.orgType =='0orgtype_sc') ){
//	    		me.constructplancountchart.extraParams.orgId = me.orgId;
//				me.constructplancountchart.reloadData();
//				
//				me.assessplancountchart.extraParams.orgId = me.orgId;
//				me.assessplancountchart.reloadData();
//				
//				me.improvecountchart.extraParams.orgId = me.orgId;
//				me.improvecountchart.reloadData();
    		}
    	}else{
    		me.standardGrid.store.proxy.extraParams.limit = me.limit;
			me.standardGrid.store.proxy.extraParams.orgId = me.orgId;
			me.standardGrid.store.load();
			
			me.processGrid.store.proxy.extraParams.limit = me.limit;
			me.processGrid.store.proxy.extraParams.orgId = me.orgId;
			me.processGrid.store.load();
			
			me.controlMeasureGrid.store.proxy.extraParams.limit = me.limit;
			me.controlMeasureGrid.store.proxy.extraParams.orgId = me.orgId;
			me.controlMeasureGrid.store.load();
			
			//me.riskGrid.store.proxy.extraParams.limit = me.limit;
			//me.riskGrid.store.proxy.extraParams.orgId = me.orgId;
			//me.riskGrid.store.load();
	
			me.assessResultGrid.store.proxy.extraParams.limit = me.limit;
			me.assessResultGrid.store.proxy.extraParams.orgId = me.orgId;
			me.assessResultGrid.store.load();
			
			me.defectGrid.store.proxy.extraParams.limit = me.limit;
			me.defectGrid.store.proxy.extraParams.orgId = me.orgId;
			me.defectGrid.store.load();
    	}
    },
	listeners:{
		resize:function(panel){
			if(panel.processGrid){
				panel.limit = Math.round((panel.processGrid.getHeight())/23-1)
				panel.reloadData();
			}
			
		}
		
	}
});