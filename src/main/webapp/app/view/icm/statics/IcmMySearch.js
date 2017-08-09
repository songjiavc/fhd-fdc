Ext.define('FHD.view.icm.statics.IcmMySearch', {
	extend: 'Ext.container.Container',
    alias: 'widget.icmmysearch',
	border:false,
	title:'导航',
    orgId:'',
    layout:'fit',
    style:'background:#fff',
    // 初始化方法
    initComponent: function() {
		var me = this;
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
        me.tbspacer = {
            xtype:'tbspacer',
            width:5,
            height:5
        };
        me.panel = Ext.create('Ext.container.Container', {	        
	    	border:false,
	    	region:'center',       
	        layout: {
				type: 'vbox',
	        	align:'stretch'
	        }
	    });
    	if(me.isIcDept){
        	var treePanel = Ext.create('FHD.view.icm.statics.IcmMyDatasTreePanel',{
				region : 'west',
	            split:true,
	            width: 200,
	            collapsible: false
			});
			me.add(treePanel);
			me.searchFieldSet = Ext.create('Ext.form.FieldSet',{
				title:'高级查询',
				margin:'4 0 4 0',
				collapsed: true
			});
			me.searchContainer = Ext.create('FHD.view.icm.statics.AdvancedQuery',{
				searchObject:[
			    	{
			    		id:'standard',
			    		url:'FHD.view.icm.statics.IcmMyStandardInfo',
			    		title:'标准',
			    		context:'要求编号, 要求内容, 责任部门, 内控标准, 控制层级, 内控要素, 处理状态, 更新日期'
			    	},
			    	{
			    		id:'process',
			    		url:'FHD.view.icm.statics.IcmMyProcessInfo',
			    		title:'流程',
			    		context:'流程编号, 流程名称, 流程分类, 发生频率, 责任部门, 责任人, 更新日期'
			    	},
			    	{
			    		id:'defect',
			    		url:'FHD.view.icm.statics.IcmMyDefectInfo',
			    		title:'缺陷',
			    		context:'缺陷描述, 缺陷等级, 缺陷类型, 整改状态, 整改责任部门, 更新日期'
			    	},
			    	{
			    		id:'assessresult',
			    		url:'FHD.view.icm.statics.AssessResultStaticsGrid',
			    		title:'评价结果',
			    		context:'评价计划, 评价人, 流程, 评价方式, 流程节点, 控制措施, 评价点, 样本有效状态 , 评价完成状态, 更新日期'
			    	}
			    ],
				margin:'30 0 20 150'
			});
			me.menuIndexFieldSet = Ext.create('Ext.form.FieldSet',{
				title:'功能导航',
				margin:'4 0 4 0',
				collapsed: true
			});
			me.menuIndexContainer = Ext.create('Ext.container.Container',{
				height:150,
				layout: {
			        type: 'hbox',
			        align: 'stretch'
			    },
			    defaults:{
			    	margin:'10 10 10 10',
			    	layout: {
				        type: 'column'
				    },
				    defaults:{
				    	columnWidth:1,
				    	margin:'5 0 0 0'
				    }
			    },
				items:[
					{
						xtype:'fieldset',
						title:'内控标准',
						flex:1,
						items:[
							{
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >发起内控标准更新</a>",
						        flex: 1
						    },
						    {
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >内控标准维护</a>",
						        flex: 1
						    }
						]
					},
					{
						xtype:'fieldset',
						title:'体系建设',
						flex:1,
						items:[
							{
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >发起体系建设/更新</a>",
						        flex: 1
						    },
						    {
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >内控手册管理</a>",
						        flex: 1
						    }
						]
					},
					{
						xtype:'fieldset',
						title:'内控评价',
						flex:1,
						items:[
							{
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >发起内控评价计划</a>",
						        flex: 1
						    },
						    {
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >评价报告管理</a>",
						        flex: 1
						    },
						    {
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >公司年度评价报告</a>",
						        flex: 1
						    },
						    {
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >集团年度评价报告</a>",
						        flex: 1
						    }
						]
					},
					{
						xtype:'fieldset',
						title:'整改优化',
						flex:1,
						items:[
							{
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >发起整改优化计划</a>",
						        flex: 1
						    },
						    {
						        xtype: 'label',
						        html: "<a href=\"javascript:void(0);\" >缺陷管理</a>",
						        flex: 1
						    }
						]
					}
				]
			});
			
        	me.planFieldSet = Ext.create('Ext.form.FieldSet',{
				title:'计划监控',
				margin:'4 0 4 0',
				collapsed: true
			});
			
			
        	me.constructplancountchart = Ext.create('FHD.view.icm.statics.ConstructPlanCountChart',{
				flex:1,
				toolRegion:'east'
			});
			me.assessplancountchart = Ext.create('FHD.view.icm.statics.AssessPlanCountChart',{
				flex:1,
				toolRegion:'east'
			});
			me.improvecountchart = Ext.create('FHD.view.icm.statics.ImproveCountChart',{
				flex:1,
				toolRegion:'east'
			});
        	me.planContainer = Ext.create('Ext.container.Container',{
				layout: {
					type: 'hbox',
			    	align:'stretch'
			    },
			    border:false,
			    flex:3,
				items:[
					me.constructplancountchart,
				    me.tbspacer,
				    me.assessplancountchart,
				    me.tbspacer,
				    me.improvecountchart
				]
			});
			
			me.panel.add(me.searchFieldSet);
			me.panel.add(me.searchContainer);
			me.panel.add(me.menuIndexFieldSet);
			me.panel.add(me.menuIndexContainer);
        	me.panel.add(me.planFieldSet);
        	me.panel.add(me.planContainer);
        }
        
	  	/*me.standardcountchart = Ext.create('FHD.view.icm.statics.StandardCountChart',{
			flex:1,
			toolRegion:'east'
		});
		me.defectcountchart = Ext.create('FHD.view.icm.statics.DefectCountChart',{
			flex:1,
			toolRegion:'east'
		});
		me.assessresultcountchart = Ext.create('FHD.view.icm.statics.AssessResultCountChart',{
			flex:1,
			toolRegion:'east'
		});
        me.dataFieldSet = Ext.create('Ext.form.FieldSet',{
			title:'数据监控',
			collapsed: true
		});
		me.dataContainer = Ext.create('Ext.container.Container',{
			layout: {
				type: 'hbox',
		    	align:'stretch'
		    },
		    border:false,
		    flex:6,
			items:[
			    me.standardcountchart,
			    me.tbspacer,
			    me.assessresultcountchart,
			    me.tbspacer,
			    me.defectcountchart
			]
		});
		me.panel.add(me.dataFieldSet);
        me.panel.add(me.dataContainer);
        me.panel.add(me.tbspacer);*/
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
//	    		me.constructplancountchart.extraParams.orgId = me.orgId;
//				me.constructplancountchart.reloadData();
//				
//				me.assessplancountchart.extraParams.orgId = me.orgId;
//				me.assessplancountchart.reloadData();
//				
//				me.improvecountchart.extraParams.orgId = me.orgId;
//				me.improvecountchart.reloadData();
    		}
    	}
    	/*me.standardcountchart.extraParams.orgId = me.orgId;
		me.standardcountchart.reloadData();
    	
    	me.assessresultcountchart.extraParams.orgId = me.orgId;
		me.assessresultcountchart.reloadData();
		
		me.defectcountchart.extraParams.orgId = me.orgId;
		me.defectcountchart.reloadData();*/
    }
});