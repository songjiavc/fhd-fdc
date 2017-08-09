/**
 * 审批
 */
Ext.define('FHD.view.response.new.bpm.SolutionCheckForm',{
	extend:'Ext.form.Panel',
	alias: 'widget.solutioncheckform',
	requires: [
	           'FHD.view.response.SolutionViewForm'
    ],
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	autoScroll:true,
//	bodyPadding:'0 3 3 3',
	layout : {
		type : 'column'
	},
	defaults:{
		columnWidth:1/1
	},
	border:false,
	initComponent : function() {
		var me=this;
        //风险
        var risk={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '对应风险',
		    value:'供应商订货起点与生产需求相差较大',
		    columnWidth: .5
		};
        //风险分类
        var riskClass={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '风险分类',
		    value:'供应商管理风险',
		    columnWidth: .5
		};
        //风险code
        var riskCode={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '风险编号',
		    value:'201308098',
		    columnWidth: .5
		};
		//责任部门
        var orgId={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '责任部门',
		    value:'管理创新部',
		    columnWidth: .5
		};
		//协助部门
        var orgOra={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '协助部门',
		    value:'管理创新部',
		    columnWidth: .5
		};
		//基础信息fieldset
        me.basicInfoFieldset = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '5 5 0 5',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [risk, riskCode, riskClass, orgId, orgOra]
        };
        
        
		//应对计划可编辑列表
		me.solutionList = Ext.create('FHD.view.response.new.SolutionCheckList',{flex : 1});
		
		//fieldSet
		me.childItems={
			xtype : 'fieldset',
			//margin: '7 10 0 30',
			layout : {
				type : 'column'
			},
			defaults : {
				margin: '5 5 0 5',
            	columnWidth: 1
			},
			collapsed: false,
			columnWidth:1/1,
			collapsible : false,
			title : '应对方案',
			items : [me.solutionList]
		};
        
		me.items=[me.basicInfoFieldset,me.childItems];
		
		me.callParent(arguments);
		
	},
	showMoreInfo : function(){ 
		var solutionviewform = Ext.widget('solutionviewform');
		var win = Ext.create('FHD.ux.Window',{
			title:'应对方案详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(solutionviewform);
	},
	showAdviceInfo : function(){ 
		var solutionadviceform = Ext.create('FHD.view.response.new.SolutionAdviceForm');
		var win = Ext.create('FHD.ux.Window',{
			title:'应对方案复合',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(solutionadviceform);
	},
	reloadData:function(){
		var me=this;
	},
	loadData:function(businessId, executionId){
		var me=this;
		me.responsePlanGrid.reloadData();
	}
});