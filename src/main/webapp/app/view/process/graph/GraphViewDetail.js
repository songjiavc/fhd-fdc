/*
 * 在流程图上右键点击查看详细
 * */
Ext.define('FHD.view.process.graph.GraphViewDetail',{
	extend: 'Ext.panel.Panel',
	alias: 'widget.graphviewdetail',
	/*
		extraParams,扩展参数：例如：{id:'risk-01-02',type:'risk'},目前type只支持：'process','processPoint','risk','controlMeasure'
	*/
	initParam:function(extraParams){
	   	var me = this;
	   	me.extraParams = extraParams;
    },
	// 初始化方法
    initComponent: function() {
    	var me = this;
    	me.callParent(arguments);
    },
    //显示明细
    showView:function(id,type){
    	var me = this;
    	var map = {
	    	process:'FHD.view.icm.icsystem.bpm.PlanProcessEditTabPanelForView',//流程
	    	processPoint:'FHD.view.icm.icsystem.form.NoteEditFormForView',//流程节点
	    	risk:'FHD.view.icm.icsystem.form.RiskEditFormForView',//风险
	    	//standard:'FHD.view.icm.standard.form.StandardControlPlanPreview',//标准要求
	    	//defect:'FHD.view.icm.defect.form.DefectFormForView',//缺陷
	    	sm:'FHD.view.kpi.cmp.sm.SmBasicInfoForm',//目标
	    	sc:'FHD.view.kpi.cmp.sc.ScBasicInfoForm',//记分卡
	    	controlMeasure:'FHD.view.icm.icsystem.form.MeaSureEditFormForView', //控制措施
	    	document:'FHD.view.sys.documentlib.DocumentLibPreviewPanel' //文档
	    	//TODO 在这里添加其他对象的支持
    	};
    	if(!map[type]){
    		//alert("努力实现中");
    		return; 
    	}
    	var grid = null;
    	if(type=='process'){
    		grid = Ext.create(map[type],{paramObj:{processId:id},readOnly:true});
    	}
    	else if(type=='processPoint'){
    		grid = Ext.create(map[type]);
    		grid.initParam({
				processPointId : id
			});
    	}
    	else if(type=='risk'){
    		grid = Ext.create(map[type],{paramObj:{processRiskId:id}});
    		grid.getInitData();
    	}
    	/*else if(type=='standard'){
    		grid = Ext.create(map[type]);
    		grid.initParam({
			 	standardControlId : id
			});
    	}*/
    	/*else if(type=='defect'){
    		grid = Ext.create(map[type],{defectId:id});
    	}*/
    	else if(type=='controlMeasure'){
    		grid = Ext.create(map[type],{paramObj:{measureId:id}});
    	}else if(type=='sm'){
    		grid = Ext.create(map[type]);
    		grid.initParam({
				smid : id
			});
    	}else if(type=='sc'){
    		grid = Ext.create(map[type]);
    		grid.initParam({
				scid : id
			});
    	}else if(type=='document'){
    		grid = Ext.create(map[type],{docId:id});
    	}//TODO 在这里添加其他对象的支持
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
    //重新加载
    reloadData:function(){
    	var me = this;
    	if(me.extraParams){
    		me.showView(me.extraParams.id,me.extraParams.type);
    	}
    }
})