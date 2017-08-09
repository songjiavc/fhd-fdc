/*
 * 评价流程的可编辑列表
 * 入参：parameter:{assessPlanId:'评价计划Id',assessPlanType:'评价类型'}
 * */
Ext.define('FHD.view.response.responseplan.ResponsePlanEmployeeForeach',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.responseplanemployeeforeach',
	requires: [
		'FHD.view.risk.assess.utils.GridCells'
    ],
	url:__ctxPath +'/app/view/response/responseplan/SelectEmpolyee.json',
	checked :  false,
    searchable:false,
    pagable : false,
    autoHeight : true,
    columnLines : true,
	cols:new Array(),
	
	initComponent:function(){
		var me=this;
    	//header
		//合规诊断store
		var isDiagnosisStore=Ext.create('Ext.data.Store', {
		    fields: ['value', 'text'],
		    data : [
		        {"value":true, "text":"张三"},
		        {"value":false, "text":"李四"}
		    ]
		});
		me.cols=[
			{header:'风险分类', dataIndex: 'riskClass', sortable: false,flex:1},
			{header:'风险名称', dataIndex: 'riskName', sortable: false,flex:3,
			renderer : function(value,metaData,record,colIndex,store,view){ 
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showMoreInfo('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
				}
			},
			{header:'承办人', dataIndex: 'Employee',sortable: false,flex:1,editor:Ext.create('Ext.form.ComboBox', {
				    store: isDiagnosisStore,
				    queryMode: 'local',
				    displayField:'text',
				    valueField: 'value'
				}),
				renderer:function(value,metaData,record,colIndex,store,view){
					if(value){
						metaData.tdAttr = 'data-qtip="请选择风险应对方案制订责任人。" style="background-color:#FFFBE6"';
						return value;
					}else{
						metaData.tdAttr = 'data-qtip="请选择风险应对方案制订责任人。" style="background-color:#FFFBE6"';
						return '';
					}
				}
			},
			{header:'建议', dataIndex: 'option', sortable: false,flex:2,editor:true,
				renderer:function(value,metaData,record,colIndex,store,view){
					if(value){
						metaData.tdAttr = 'data-qtip="请填写建议说明。" style="background-color:#FFFBE6"';
						return value;
					}else{
						metaData.tdAttr = 'data-qtip="如果诊断结果为合格，请填写控制描述。" style="background-color:#FFFBE6"';
						return '';
					}
				}
			}
		];
		me.tbarItems=[
			    {
			    	iconCls : 'icon-group-add',
			    	tooltip: '按风险分配',
			    	text: '按风险分配',
			    	handler :me.addConstructRelaDefect,
			    	scope : this
			    },'-', {
					iconCls : 'icon-edit',
					text: '选择评估计划',tooltip: '选择评估计划',
					handler : me.editNote,
					scope : this
					//disabled : true
				}
			   
		];
		me.callParent(arguments);
		me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [1]);
        });
	},
	saveData:function(){
		var me = this;
		var jsonArray=[];
		var rows = me.store.getModifiedRecords();
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		if(jsonArray.length>0){
//			var validateFlag = false;
//			var count = me.store.getCount();
//			for(var i=0;i<count;i++){
//				var item = me.store.data.get(i);
//	 			if(item.get('diagnosis') == null){
//	 				reason = '诊断结果不能为空!';
//	 				validateFlag = true;
//	 				break;
//	 			}else if(item.get('diagnosis') == true && (item.get('proof') == "" || item.get('proof') == null)){
//					reason = '诊断结果为合格的,实施证据不能为空!';
//					validateFlag = true;
//					break;
//				}else if(item.get('diagnosis') == false && (item.get('description') == ""|| item.get('description') == null)){
//					reason = '诊断结果为不合格的,缺陷描述不能为空!';
//					validateFlag = true;
//					break;
//				}
//			}
//			if(validateFlag){
//	 			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), reason);
//	 			return false;
//	 		}
			 FHD.ajax({
    		     url : __ctxPath+ '/icm/icsystem/mergeconstructplanreladiagnosesbatch.f',
    		     params : {
    		    	 modifiedRecord:Ext.encode(jsonArray),
    		    	 type : 'save'
    			 },
    			 callback : function(data) {
    				 if (data) {
    					 FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
						 me.store.load();  
    				 }
    			 }
    		});
    		return true;
		}else{
			FHD.notification("无修改记录!",FHD.locale.get('fhd.common.prompt'));
			return false;
		}
	},
	saveSubmitData:function(){
		var me = this;
		var jsonArray=[];
		var rows = me.store.getModifiedRecords();
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		var validateFlag = false;
		var count = me.store.getCount();
		for(var i=0;i<count;i++){
			var item = me.store.data.get(i);
 			if(item.get('diagnosis') == null){
 				reason = '诊断结果不能为空!';
 				validateFlag = true;
 				break;
 			}else if(item.get('diagnosis') == true && (item.get('proof') == "" || item.get('proof') == null)){
				reason = '诊断结果为合格的,实施证据不能为空!';
				validateFlag = true;
				break;
			}else if(item.get('diagnosis') == true && (item.get('controldesc') == "" || item.get('controldesc') == null)){
				reason = '诊断结果为合格的,控制描述不能为空!';
				validateFlag = true;
				break;
			}else if(item.get('diagnosis') == false && (item.get('description') == ""|| item.get('description') == null)){
				reason = '诊断结果为不合格的,缺陷描述不能为空!';
				validateFlag = true;
				break;
			}
		}
		if(validateFlag){
 			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), reason);
 			return false;
 		}
	 	FHD.ajax({
			url : __ctxPath+ '/icm/icsystem/mergeconstructplanreladiagnosesbatch.f',
		    params : {
		    	modifiedRecord:Ext.encode(jsonArray),
		    	type : 'save'
			},
    		callback : function(data) {
    			if (data) {
					FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
					me.store.load();  
    			}
    		}
    	});
    	return true;
	},
    reloadData:function(){
    	var me=this;
    	me.store.proxy.extraParams.constructPlanId = me.businessId;
    	me.store.proxy.extraParams.executionId = me.executionId;
    	me.store.load();
    },
    showMoreInfo : function(){ 
		var riskdetailform = Ext.create('FHD.view.risk.cmp.RiskDetailForm');
		var form = riskdetailform.getForm();
		form.setValues({
			parentName : '生产管理风险',
			code : 'R7200010001',
			name : '生产计划编排不完整或不合理，影响生产效率',
			desc : '生产计划编排不完整或不合理，影响生产效率',
			respDeptName : '生产管理部',
			relaDeptName : '内控部'
		});
		var win = Ext.create('FHD.ux.Window',{
			title:'风险详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(riskdetailform);
	}
});