/*
 * 评价流程的可编辑列表
 * 入参：parameter:{assessPlanId:'评价计划Id',assessPlanType:'评价类型'}
 * */
Ext.define('FHD.view.response.responseplan.SolutionPlanEditGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.solutionplaneditgrid',
	url:__ctxPath +'/app/view/response/responseplan/ResponsePlanData.json',
	requires : [
		'FHD.view.response.SolutionForm',
    	'FHD.view.risk.assess.utils.GridCells'
	],
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
				header : '风险名称',dataIndex : 'riskName',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '应对方案',dataIndex : 'solutionName',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showPlanViewList('" + record.data.id +"')\" >" + value + "</a>";
				}
			},
			{
				header : '责任人',dataIndex : 'personInCharge',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '完成标志',dataIndex : 'indicator',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
				}
			},
			{
				header : '完成时间',dataIndex : 'finishTime',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value; 
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
			    	text: '应对库选择',
			    	tooltip : '应对库选择',
			    	handler :me.addConstructRelaStandard,
			    	scope : this
			    },
			     '-',
			    {
			    	iconCls : 'icon-group-add',
			    	tooltip: '制定方案',
			    	text: '制定方案',
			    	handler :me.addSolutionPlan,
			    	scope : this
			    }
			   
			]
		}
		me.callParent(arguments);
		
        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3]);
        });
		
	},
	showPlanViewList:function(id,dealStatus){
    	var me=this;
    	
    	me.solutionviewform=Ext.widget('solutionviewform',{
    		businessId:id,
			dealStatus:dealStatus
		});
		
		me.solutionviewform.reloadData();
		
		var win = Ext.create('FHD.ux.Window',{
			title:'应对方案详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true,//（是否增加最大化，默认没有）
			buttonAlign: 'center'
    	}).show();
    	
    	win.add(me.solutionviewform);
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
	// 新增  缺陷  选择
    addSolutionPlan : function(){
    	//2.按缺陷选择
    	var solution = Ext.create('FHD.view.response.SolutionDraftForm');
    	var win = Ext.create('FHD.ux.Window',{
			title:'填写应对计划',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(solution);
    },
    //删除流程范围
    delStandard:function(){
    	var me=this;
    },
    reloadData:function(){
    	var me=this;
    	me.store.load();
    }
});