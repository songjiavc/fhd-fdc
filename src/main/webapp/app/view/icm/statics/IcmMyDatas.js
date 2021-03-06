/**
 * 数据
 * @author 邓广义
 */
Ext.define('FHD.view.icm.statics.IcmMyDatas', {
	extend: 'Ext.container.Container',
    alias: 'widget.icmmydatas',
	border:false,
	title:'',
	requires: [
	     /*'FHD.view.icm.icsystem.form.MeaSureEditFormForView'	*/
    ],
    orgId:'',
    layout:'fit',
    // 初始化方法
    initComponent: function() {
    	 var me = this;
		 me.limit = 5;
		 me.gridHeight = me.limit*32+44;
		 //me.gridWidth = 341;
		 me.isIcDept = '';
         FHD.ajax({								//判断是否为内控部门
            url: __ctxPath + '/icm/statics/judgeificmdept.f',
            async:false,
            callback: function (data) {
            	me.isIcDept = data;
            }
         });
         
		 Ext.apply(me, {
     	    border:false,
     		layout: {
     			type: 'border'
    		}
         });
         me.callParent(arguments);
		 var tagAStyle= 'direction:none;text-decoration:none;font-size:12px;color:#04408c'; //a标签样式
		 me.processGrid = Ext.create('FHD.ux.GridPanel', {
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
			tbar:['<a style='+tagAStyle+'>流程</a>','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("process")\' >更多...</a>'],
			height: me.gridHeight,
			//width: me.gridWidth,
			flex:25,
			extraParams:{limit:me.limit},
			checked:false,
			hideHeaders:true,
			searchable:false,
			pagable : false
		});	
//		me.riskGrid = Ext.create('FHD.ux.GridPanel', {
//			tbar:['<a style='+tagAStyle+'>风险</a>','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("risk")\' >更多...</a>'],
//			url:__ctxPath + '/icm/statics/findriskbysome.f',
//			cols: [
//				{dataIndex:'id',hidden:true},
//				{ header: '风险编号',  dataIndex: 'riskCode' ,flex: 1 ,hidden: true,
//					renderer:function(value,metaData,record,colIndex,store,view) {
//						metaData.tdAttr = 'data-qtip="'+value+'" '; 
//						return value;
//					}
//				},
//				{ header: '风险名称', dataIndex: 'riskName', flex: 2  ,
//					renderer:function(value,metaData,record,colIndex,store,view) {
//						metaData.tdAttr = 'data-qtip="'+value+"("+record.data.riskCode+")"+'" '; 
//							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','risk')\" >" + value + "</a>"; 
//					}
//				},
//				{ header: '更新日期', dataIndex: 'updateDate', width: 90}
//			],
//			height: me.gridHeight,
//			flex:25,
//			//width: me.gridWidth,
//			extraParams:{limit:me.limit},
//			checked:false,
//			hideHeaders:true,
//			searchable:false,
//			pagable : false
//		});
		/*me.ruleGrid = Ext.create('FHD.ux.GridPanel', {
			tbar:['<a style='+tagAStyle+'>制度</a>','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("rule")\' >更多...</a>'],
			url:__ctxPath + '/icm/statics/icmmyinstitutiondatas.f',
			cols: [
				{dataIndex:'id',hidden:true},
				{ header: '制度编号',  dataIndex: 'code' ,flex: 1 ,hidden: true,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'" '; 
						return value;
					}
				},
				{ header: '制度名称', dataIndex: 'name', flex: 2  ,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','rule')\" >" + value + "</a>"; 
					}
				},
				{ header: '更新日期', dataIndex: 'updateDate', width: 90}
			],
			height: me.gridHeight,
			flex:25,
			//width: me.gridWidth,
			extraParams:{limit:me.limit},
			checked:false,
			hideHeaders:true,
			searchable:false,
			pagable : false
		});	*/
		me.standardGrid = Ext.create('FHD.ux.GridPanel', {
			tbar:['<a style='+tagAStyle+'>标准</a>','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("standard")\' >更多...</a>'],
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
			flex:25,
			//width: me.gridWidth,
			extraParams:{limit:me.limit},
			checked:false,
			hideHeaders:true,
			searchable:false,
			pagable : false
		});	
		/*me.measureGrid = Ext.create('FHD.ux.GridPanel', {
			tbar:['<a style='+tagAStyle+'>控制措施</a>','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("control")\' >更多...</a>'],
			url:__ctxPath + '/icm/statics/icmmycontroldatas.f',
			cols: [
				{dataIndex:'id',hidden:true},
				{ header: '控制措施编号',  dataIndex: 'code' ,flex: 1 ,hidden: true,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'" '; 
						return value;
					}
				},
				{ header: '控制措施名称', dataIndex: 'name', flex: 2  ,
					renderer:function(value,metaData,record,colIndex,store,view) {
						metaData.tdAttr = 'data-qtip="'+value+'"'; 
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "','control')\" >" + value + "</a>"; 
					}
				},
				{ header: '更新日期', dataIndex: 'updateDate', width: 90}
			],
			height: me.gridHeight,
			//width: me.gridWidth,
			flex:25,
			extraParams:{limit:me.limit},
			checked:false,
			hideHeaders:true,
			searchable:false,
			pagable : false
		});*/
		
		me.assessResultGrid = Ext.create('FHD.ux.GridPanel',{
			tbar:['<a style='+tagAStyle+'>评价结果</a>','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("assessresult")\' >更多...</a>'],
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
			flex:25,
			extraParams:{limit:me.limit},
			checked:false,
			hideHeaders:true,
			searchable:false,
			pagable : false
		});
		
		me.defectGrid = Ext.create('FHD.ux.GridPanel',{
			tbar:['<a style='+tagAStyle+'>缺陷</a>','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("defect")\' >更多...</a>'],
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
			flex:25,
			extraParams:{limit:me.limit},
			checked:false,
			hideHeaders:true,
			searchable:false,
			pagable : false
		});
		
		
		me.tbspacer = {
            xtype:'tbspacer',
            width:5,
            height:5
        };
		me.assessresultcountchart=Ext.create('FHD.view.icm.statics.AssessResultCountChart',{
			flex:1.5,
			toolRegion:'west',
			//默认显示图表类型--可选
			myType:'msColumnChart',
			//默认系列与图例选项--可选
			myXCol:{dataIndex:'processName', header:'流程'},
			myYCol:{dataIndex:'isValid', header:'样本有效状态'}
		});
		me.defectcountchart=Ext.create('FHD.view.icm.statics.DefectCountChart',{
			flex:1,
			toolRegion:'east'
		});
		
	    me.panel = Ext.create('Ext.panel.Panel', {	        
	    	border:false,
	    	region:'center',       
	        layout: {
				type: 'vbox',
	        	align:'stretch'
	        }
	    });
			
		var treePanel = Ext.create('FHD.view.icm.statics.IcmMyDatasTreePanel',{
			region : 'west',
            split:true,
            width: 200,
            collapsible: false
		});
		
        if(me.isIcDept){
        	me.add(treePanel);
        	me.constructplancountchart = Ext.create('FHD.view.icm.statics.ConstructPlanCountChart',{
				flex:1,
				toolRegion:'west'
			});
        	me.assessplancountchart = Ext.create('FHD.view.icm.statics.AssessPlanCountChart',{
				toolRegion:'west'
			});
			me.improvecountchart = Ext.create('FHD.view.icm.statics.ImproveCountChart',{
				flex:1,
				toolRegion:'west'
			});
			
			me.constructPlanPanel = Ext.create('Ext.panel.Panel',{
				tbar:['->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("constructplan")\' >更多...</a>'],
				flex:1,
				layout:'fit',
				items:[
					me.constructplancountchart
				]
			});
			
			me.assessPlanPanel = Ext.create('Ext.panel.Panel',{
				tbar:['->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("assessplan")\' >更多...</a>'],
				flex:1,
				layout:'fit',
				items:[
					me.assessplancountchart
				]
			});
			
			me.rectifyPlanPanel = Ext.create('Ext.panel.Panel',{
				tbar:['->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreProcessInfo("rectifyplan")\' >更多...</a>'],
				flex:1,
				layout:'fit',
				items:[
					me.improvecountchart
				]
			});
			
        	me.planFieldSet = Ext.create('Ext.form.FieldSet',{
				title:'计划监控',
				margin:'4 0 4 0',
				collapsed: true
			});
        	me.planContainer = Ext.create('Ext.container.Container',{
				layout: {
					type: 'hbox',
			    	align:'stretch'
			    },
			    border:false,
			    flex:9,
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
        	me.panel.add(me.planFieldSet);
        	me.panel.add(me.planContainer);
        }else{
        	me.latestChart = Ext.create('Ext.container.Container',{
				layout: {
					type: 'hbox',
			    	align:'stretch'
			    },
			    border:false,
			   	flex:10,
				items:[
					me.assessresultcountchart,
					me.defectcountchart
				]
			});
        	me.panel.add(me.latestChart);
        }
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
		    flex:12,
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
						/*me.ruleGrid,
						me.tbspacer,
					    me.measureGrid,
					    me.tbspacer,*/
				    	me.assessResultGrid,
				    	me.tbspacer,
				    	me.defectGrid,
				    	me.tbspacer
					]
				})
			]
		});
        me.panel.add(me.latestFieldSet);
        me.panel.add(me.latestContainer);
        me.panel.add(me.tbspacer);
        me.add(me.panel);
       
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
    			me.constructplancountchart.extraParams.orgId = me.orgId;
		    	me.constructplancountchart.reloadData();
		    	
		    	me.assessplancountchart.extraParams.orgId = me.orgId;
		    	me.assessplancountchart.reloadData();
		    	
		    	me.improvecountchart.extraParams.orgId = me.orgId;
		    	me.improvecountchart.reloadData();
    		}
    	}else{
    		me.assessresultcountchart.extraParams.orgId = me.orgId;
			me.assessresultcountchart.reloadData();
			
			me.defectcountchart.extraParams.orgId = me.orgId;
			me.defectcountchart.reloadData();
    	}
		
		me.standardGrid.store.proxy.extraParams.limit = me.limit;
		me.standardGrid.store.proxy.extraParams.orgId = me.orgId;
		me.standardGrid.store.load();
		
		me.processGrid.store.proxy.extraParams.limit = me.limit;
		me.processGrid.store.proxy.extraParams.orgId = me.orgId;
		me.processGrid.store.load();
		
		//me.riskGrid.store.proxy.extraParams.limit = me.limit;
		//me.riskGrid.store.proxy.extraParams.orgId = me.orgId;
		//me.riskGrid.store.load();
	/*	
		me.ruleGrid.store.proxy.extraParams.limit = me.limit;
		me.ruleGrid.store.proxy.extraParams.orgid = me.orgId;
		me.ruleGrid.store.load();
		
		me.measureGrid.store.proxy.extraParams.limit = me.limit;
		me.measureGrid.store.proxy.extraParams.orgid = me.orgId;
		me.measureGrid.store.load();*/
		
		me.assessResultGrid.store.proxy.extraParams.limit = me.limit;
		me.assessResultGrid.store.proxy.extraParams.orgId = me.orgId;
		me.assessResultGrid.store.load();
		
		me.defectGrid.store.proxy.extraParams.limit = me.limit;
		me.defectGrid.store.proxy.extraParams.orgId = me.orgId;
		me.defectGrid.store.load();
    },
    showProcessView:function(id,type){
    	var me = this;
    	
    	var map = {
	    	process:'FHD.view.icm.icsystem.bpm.PlanProcessEditTabPanelForView',        //流程
	    	risk:'FHD.view.icm.icsystem.form.RiskEditFormForView',       //风险
	    	/*rule:'FHD.view.icm.rule.RuleEditPanelForView',		         //制度*/
	    	standard:'',
	    	/*control:'FHD.view.icm.icsystem.form.MeaSureEditFormForView', //控制措施*/
	    	defect:'FHD.view.icm.defect.form.DefectFormForView'
    	};
    	if(!map[type]){
    		alert("努力实现中");
    		return 
    	}
    	var grid = null;
    	if(type=='process'){
    		grid = Ext.create(map[type],{paramObj:{processId:id},readOnly:true});
    	}
    	else if(type=='risk'){
    		grid = Ext.create(map[type],{paramObj:{processRiskId:id}});
    		grid.getInitData();
    	}
    	/*else if(type=='rule'){
    		grid = Ext.create(map[type],{paramObj:{ruleId:id}});
    	}*/
    	else if(type=='standard'){
    		grid = null;
    	}
    	/*else if(type=='control'){
    		grid = Ext.create(map[type],{paramObj:{measureId:id}});
    	}*/
    	else if(type=='defect'){
    		grid = Ext.create(map[type],{defectId:id});
    	}
    	else if(type=='assessplan'){
    		grid = Ext.create(map[type],{assessPlanId:id});
    	}
    	else if(type=='rectifyplan'){
    		grid = Ext.create(map[type],{improveId:id});
    	}
    	else if(type=='constructplan'){
    		grid = Ext.create(map[type],{businessId:id});
    	}
    	
    	grid.reloadData();
    	me.win=Ext.create('FHD.ux.Window',{
			title : '详细查看',
			flex:1,
			autoHeight:true,
			collapsible : true,
			modal : true,
			maximizable : true,
			items:[grid]
		}).show();
    },
	showMoreProcessInfo:function(key){
		var me = this;
		
		var menu = {
			process:['FHD.view.icm.statics.IcmMyProcessInfo','流程'],	          //流程
			risk:['FHD.view.icm.statics.RiskControlMatrix','风险控制'],			          //风险
			/*rule:['FHD.view.icm.statics.IcmMyRuleInfo','全部制度'],			          //制度	
*/			standard:['FHD.view.icm.statics.IcmMyStandardInfo','全部标准'],	          //标准
			/*control:['FHD.view.icm.statics.IcmMyControlInfo','全部控制措施'],	   		  //控制措施
*/			defect:['FHD.view.icm.statics.IcmMyDefectInfo','全部缺陷'],		          //缺陷
			assessresult:['FHD.view.icm.statics.AssessResultStaticsGrid','评价结果'],
			assessplan:['FHD.view.icm.statics.IcmMyAssessPlanInfo','评价计划'],     //评价计划
			rectifyplan:['FHD.view.icm.statics.IcmMyRectifyPlanInfo','整改计划'],   //整改计划
			constructplan:['FHD.view.icm.statics.IcmMyConstructPlanInfo','体系计划']//体系计划
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
				var p = centerPanel.add(Ext.create(url,{
					id:url,
					title: menu.text,
					tabTip:menu.text,
					closable:true
				}));
				if(p.initParam){
					p.initParam({orgId:me.orgId});
				}
				centerPanel.setActiveTab(p);
			}
		}
	},
	listeners:{
		resize:function(grid){
			grid.limit = Math.round((grid.processGrid.getHeight())/23-1)
			grid.reloadData();
		}
		
	}
});