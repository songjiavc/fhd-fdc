/*
 * 评价流程的可编辑列表
 * 入参：parameter:{assessPlanId:'评价计划Id',assessPlanType:'评价类型'}
 * */
Ext.define('FHD.view.icm.icsystem.constructplan.ConstructPlanRelaStandardViewGrid',{
	extend:'FHD.ux.EditorGridPanel',
	alias: 'widget.constructplanrelastandardviewgrid',
	url: '',
	extraParams:{
		businessId:''
	},
	pagable :false,
	checked : false,
	cols:new Array(),
	tbarItems:new Array(),
	bbarItems:new Array(),
	searchable : true,
	border : false,
	sortableColumns : false,
	initComponent:function(){
		var me=this;
		me.cols=[
			{header : '标准名称',dataIndex : 'standardName',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return value;
				}},
			{header : '内控要求',dataIndex : 'controlRequirement',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showDiagnosesAndDefectViewList('" + record.data.id+"','"+ record.data.diagnosesId+"','"+ record.data.isNormallyDiagnosis+"')\" >" + value + "</a>"; 
				}},
			{header : '内控要素',dataIndex :'controlPoint',flex : 2},
			{header : '流程id',dataIndex :'processId',hidden : true},
			{header : 'id',dataIndex :'id',hidden : true},
			{header : 'diagnosesId',dataIndex :'diagnosesId',hidden : true},
			
			{header : '流程',dataIndex :'processName',flex : 2,
				renderer:function(value,metaData,record,colIndex,store,view) { 
		    		metaData.tdAttr = 'data-qtip="'+value+'"';
					return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.processId +"')\" >" + value + "</a>"; 
			}},
			{header : '责任部门',dataIndex :'standardRelaOrg',flex : 1},
			{header : '是否合规诊断',dataIndex :'isNormallyDiagnosis',flex : 1, hidden:false},
			{header : '是否流程梳理',dataIndex :'isProcessEdit',flex : 1, hidden:false},
			{header:'建设责任人',dataIndex:'constructPlanEmp'}
		];
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
    reloadData:function(){
    	var me=this;
    	me.store.proxy.url = __ctxPath+ '/icm/icsystem/findconstructplanrelastandardlistbypageforview.f'
    	me.store.proxy.extraParams.constructPlanId = me.extraParams.businessId;
    	me.store.load();
    },
    showProcessView:function(processid){
    	var me = this;
    	var grid = Ext.create('FHD.view.icm.icsystem.bpm.PlanProcessEditTabPanelForView',{paramObj:{processId:processid},readOnly:true});;
    	grid.reloadData();
    	me.win=Ext.create('FHD.ux.Window',{
			title : '详细查看',
			flex:1,
			autoHeight:true,
			autoScroll:true,
			collapsible : true,
			modal : true,
			maximizable : true,
			listeners:{
				close : function(){
				}
			}
		}).show();
		me.win.add(grid);
    },
    showDiagnosesAndDefectViewList:function(id,diagnosesId,isNormallyDiagnosis){
    	var me=this;
    	if(isNormallyDiagnosis == '否'){
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '该计划没有进行合规诊断!');
    		return false;
    	}
    	me.diagnosesverticalinfopanel =Ext.create('FHD.view.icm.icsystem.constructplan.DiagnosesVerticalInfoPanel',{
			planRelaStandardId:id,
			diagnosesId : diagnosesId
		});
		if(diagnosesId){
			var popWin = Ext.create('FHD.ux.Window',{
				title:'诊断结果明细',
				//modal:true,//是否模态窗口
				collapsible:false,
				maximizable:true//（是否增加最大化，默认没有）
			}).show();
			popWin.add(me.diagnosesverticalinfopanel);
			me.diagnosesverticalinfopanel.getInitParams();
		}else{
			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '合规诊断节点尚未执行!');
		}
//		me.diagnosesverticalinfopanel.reloadData();
    }
});