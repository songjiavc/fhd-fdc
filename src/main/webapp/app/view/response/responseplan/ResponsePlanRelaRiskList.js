/*
 * 评价流程的可编辑列表
 * 入参：parameter:{assessPlanId:'评价计划Id',assessPlanType:'评价类型'}
 * */
Ext.define('FHD.view.response.responseplan.ResponsePlanRelaRiskList',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.responseplanrelarisklist',
	url:__ctxPath +'/app/view/response/responseplan/SelectRiskInfo.json',
	extraParams:{
		businessId:''
	},
	pagable :false,
	cols:new Array(),
	tbarItems:new Array(),
	bbarItems:new Array(),
	sortableColumns : false,
	//可编辑列表为只读属性
	readOnly : false,
	searchable : false,
	
	initComponent:function(){
		var me=this;
		
		me.cols=[
		    {dataIndex : 'id',hidden:true},
		    {
				header : '风险分类',dataIndex : 'riskClass',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '风险名称',dataIndex : 'riskName',flex : 2,
	    		renderer : function(value,metaData,record,colIndex,store,view){ 
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showMoreInfo('" + record.data.id + "','" + record.data.dealStatus +"')\" >" + value + "</a>"; 
				}
			},
			{
				header : '风险描述',dataIndex : 'riskDesc',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '主责部门',dataIndex : 'orgOr',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '相关部门',dataIndex : 'orgRela',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '评估时间',dataIndex : 'assessTime',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '状态',dataIndex : 'status',flex : 2,
				renderer : function(v) {
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

			}
		];
		if(me.readOnly){
			me.searchable = false;
		}else{
		//tbar
			me.tbarItems=[
			    {
			    	iconCls : 'icon-add',
			    	text: '选择指标',
			    	tooltip : '应对库选择',
			    	handler :me.addConstructRelaStandard,
			    	scope : this
			    },
			     '-',
			    {
			    	iconCls : 'icon-group-add',
			    	tooltip: '选择风险',
			    	text: '选择风险',
			    	handler :me.addConstructRelaDefect,
			    	scope : this
			    }
			   
			]
		}
		me.callParent(arguments);
	},
	colInsert: function (index, item) {
        if (index < 0) return;
        if (index > this.cols.length) return;
        for (var i = this.cols.length - 1; i >= index; i--) {
            this.cols[i + 1] = this.cols[i];
        }
        this.cols[index] = item;
    },
	saveData:function(){
		var me=this;
		var validateFlag = false;
		var repeatProcessFlag = false;
		var count = me.store.getCount();
		var num = 0;
		for(var i=0;i<count;i++){
			var item = me.store.data.get(i);
 			if(item.get('constructPlanEmp') == ''){
				validateFlag = true;
			}
			if(item.get('isProcessEdit')==false){
				continue;
			}
			
			for(var j=i+1;j<count;j++){
				var pp = me.store.data.get(j);
				if(item.get('processName') == pp.get('processName')){
					if(pp.get('isProcessEdit')==true){
						num++;
					}
				}
			}
		}
		if(num > 0){
			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '同一个流程不允许多处进行梳理!');
 			return false;
		}
		if(validateFlag){
 			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '体系建设责任人不允许为空!');
 			return false;
 		}
		var jsonArray=[];
		var rows = me.store.getModifiedRecords();
		Ext.each(rows,function(item){
			jsonArray.push(item.data);
		});
		if(jsonArray.length>0){
			 FHD.ajax({
    		     url : __ctxPath+ '/icm/icsystem/mergeconstructplanrelastandardbatch.f',
    		     params : {
    		    	 modifiedRecord:Ext.encode(jsonArray)
    			 },
    			 callback : function(data) {
    				 if (data) {
    					 FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
						 me.store.load();  
    				 }
    			 }
    		});
		}
		return true;
	},
	//新增标准选择
    addConstructRelaStandard : function(){
    	var me=this;
    	me.kpiselectwindow = Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow',{
	    	multiSelect:true,
//	    	selectedvalues:me.getGridStroe(),
	    	onSubmit:function(store){
	    	}
	    });
    	me.kpiselectwindow.show();
    	me.kpiselectwindow.addComponent();
    },
	// 鏂板  缂洪櫡  閫夋嫨
    addConstructRelaDefect : function(){
    	//2.鎸夌己闄烽�鎷�
    	var me=this;
		me.riskeventselectorwindow = Ext.create('FHD.ux.riskEvent.RiskEventSelectorWindow');
		me.riskeventselectorwindow.show();
    },
	// 新增  缺陷  选择
//    addSolutionPlan : function(){
//    	//2.按缺陷选择
//    	var solution = Ext.create('FHD.view.response.SolutionForm');
//    	var win = Ext.create('FHD.ux.Window',{
//			title:'填写应对计划',
//			//modal:true,//是否模态窗口
//			collapsible:false,
//			maximizable:true//（是否增加最大化，默认没有）
//    	}).show();
//    	win.add(solution);
//    },
    //删除流程范围
    delStandard:function(){
    	var me=this;
    },
    reloadData:function(){
    	var me=this;
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
			title:'风险信息列表',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(riskdetailform);
	}
});