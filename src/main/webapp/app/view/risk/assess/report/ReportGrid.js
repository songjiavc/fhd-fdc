/**
 * 风险报告分为日常管理报告和评估报告（年度按照评估计划生成数据）
 * 日常管理报告直接生成,年度报告（评估报告）弹出窗口，选择评估计划再进行生成。通过type类型属性区分
 * @author : 元杰
 *  报告列表
 */
Ext.define('FHD.view.risk.assess.report.ReportGrid',{
    extend: 'FHD.ux.GridPanel',
 	alias : 'widget.reportgrid',
    requires: [
    ],
    columnLines:true,
    
    /**
     * 页面类型分为日常报告dailyReport和年度报告yearlyReport
     */
    type:'dailyReport',
    
    /**
     * 风险管理报告url
     */
    dailyReportUrl : '/comm/report/findRiskManageReport.f',

    /**
     * 风险评估报告url
     */
    yearlyReportUrl : '/comm/report/findRiskAssessReport.f',
    
    initComponent: function () {
    	var me = this;

    	me.cols = [
	     	{header: 'id' ,dataIndex: 'id',sortable: true,width : 10,invisible : true},
	     	{header: 'fileId' ,dataIndex: 'fileId',sortable: true,width : 10,invisible : true},
	     	{header: '报告名称' ,dataIndex: 'name',sortable: true,flex : 1,hidden : false,
	     		renderer:function(value,metaData,record,colIndex,store,view) { 
	     			var id = record.data.fileId;
					return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').download('"+id+"')\">" +value+ "</a>" ;
				}
	     	},
	     	{header: '所属公司' ,dataIndex: 'company',sortable: true,width : 250,hidden : false},
	     	{header: '创建人' ,dataIndex: 'creator',sortable: true,width : 100,hidden : false},
	     	{header: '创建时间' ,dataIndex: 'createTime',sortable: true,width : 200,hidden : false},
	     	{header: '操作' ,dataIndex: 'id',width : 100,hidden : false,
	     		renderer:function(value,metaData,record,colIndex,store,view) {
	     			var id = record.data.fileId;
	     			return "<img src='images/icons/download_min.png' onclick=\"Ext.getCmp('" + me.id + "').download('"+id+"')\"></img>" ;
					//return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').download('"+id+"')\">下载</a>" ;
				}
			}
	      ];
		me.tbar =[
			{text : "删除",iconCls: 'icon-del', handler:me.delTem, scope : this,id:'report_del',disabled:true},
			{text : "生成报告",iconCls: 'icon-world', handler:me.generateReportPopWindow, scope : this,id:'report_generate',disabled:true},
			{text : "上传报告",iconCls: 'icon-folder-upload', handler:me.generateUploadWindow, scope : this,id:'report_upload',disabled:false}
		];
		
    	Ext.apply(me, {
    		border:false,
    	   flex:1,
    	   pagable:false,
    	   searchable:false,
 	       multiSelect: false,
 	       rowLines:true,//显示横向表格线
 	       checked: true, //复选框
 	       autoScroll:false,
 	       tbarItems:me.tbar
         });
         me.callParent(arguments);
     	
     	me.on('selectionchange',function(){me.setStatus(me)});
    },
    setStatus:function(me){
    	var me = this;
        var length = me.getSelectionModel().getSelection().length;
        me.down('#report_del').setDisabled(length === 0);
    },
    delTem:function(){
    	var me = this;
    	var selections = me.getSelectionModel().getSelection();
    	if(!selections.length)return
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : '你确定删除此报告吗？',
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {
					var ids = [];
					Ext.Array.each(selections,
			        	function(item) {
			            	ids.push(item.get("id"));
			        });
					FHD.ajax({
						async:false,
						url:__ctxPath + '/comm/report/removeReportInfomationByIds.f',
						params:{
							ids:ids.join(',')
						},
						callback:function(rec){
							//刷新报告列表
	    					me.reloadData();
						}
					});
				}
			} 
		});
    },
    download:function(id){
    	window.location.href=__ctxPath + '/comm/report/downloadReport.f?id='+id;
    },
    generateReportPopWindow:function(){
    	var me = this;
    	
    	if(me.type == 'yearlyReport'){
    		if(me.generateReportWindow==undefined){
    			var planStore = Ext.create("Ext.data.Store",{
    				fields: ['id', 'planName'],
    				proxy:{
    					type : 'ajax',
    					url:__ctxPath + "/comm/report/findFineshedAssessPlan.f",
    					extraParams:{templateType:'risk_assess_report_template'},
		    			reader: {
				            type : 'json',
				            root : 'datas',
				            totalProperty :'totalCount'
				        }
    				}
    			});
    			me.planCombo = Ext.create('Ext.form.ComboBox', {
				    fieldLabel: '评估计划',
				    name:'assessPlanId',
				    store: planStore,
				    queryMode: 'remote',
				    allowBlank : false,
				    displayField: 'planName',
				    valueField: 'id'
				});
    	   		me.generateReportWindow = Ext.create("Ext.window.Window",{
        			title:'生成报告',
        			height:200,
        			width:300,
        			items:[me.planCombo],
        			closeAction: 'hide',
        			modal:true,
        			buttonAlign:'center',
        			buttons:[{
        				text:'生成',
        				handler:function(){
        					if(me.planCombo.isValid()){
        						var id = me.planCombo.getValue();
        						me.generateYearlyReport(id);
        					}
        				}
        			}]
        		});
    		}
    		me.generateReportWindow.show();
    	}else{
    		me.generateDailyReport();
    	}
    },
    
    /**
     * 上传窗口
     */
    generateUploadWindow:function(){
    	var me = this;
    	var panel = Ext.create("FHD.view.risk.assess.report.ReportUploadForm",{
			owner:me,
			type:me.type
		});
		me.uploadWin = Ext.create('FHD.ux.Window',{
			title:"风险报告上传",
			type:me.type,
			width:700,
    		height:400,
			iconCls: 'icon-edit',
			items: panel, 
			maximizable: true,
			resizable: false
		});
		me.uploadWin.show();
    },
    
    /**
     * 生成日常风险报告
     */
    generateDailyReport:function(){
    	var me = this;

    	//判断是否默认模板
    	var hasDefault = false;
    	hasDefault = true;
    	
    	if(hasDefault){
    		me.body.mask("生成中...","x-mask-loading");
    		FHD.ajax({
    			async:true,
    			url:__ctxPath + '/report/generateRiskReport',
    			params:{
    				month:9
    			},
    			callback:function(rec){
    				me.body.unmask();
    				if(rec.success){
    					FHD.notification("风险报告生成成功",FHD.locale.get('fhd.common.prompt'));
    					//刷新报告列表
    					me.reloadData('dailyReport');
    				}else{
    					Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '风险报告生成失败!');
    				}
    			}
    		});
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.operateFailure'), '没有默认的模板，不能进行生成!');
    	}
    },
    /**
     * 生成年度风险报告
     * id 评估计划id
     */
    generateYearlyReport:function(id){
    	var me = this;

    	//判断是否默认模板
    	var hasDefault = false;
    	hasDefault = true;
    	
    	if(hasDefault){
    		me.body.mask("生成中...","x-mask-loading");
    		FHD.ajax({
    			async:true,
    			url:__ctxPath + '/report/generateRiskAssessReport',
    			params:{
    				id:id,
    				month:9
    			},
    			callback:function(rec){
    				me.body.unmask();
    				if(rec.success){
    					//关闭window
    					me.generateReportWindow.hide();
    					FHD.notification("风险报告生成成功",FHD.locale.get('fhd.common.prompt'));
    					//刷新报告列表
    					me.reloadData('yearlyReport');
    				}else{
    					Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '风险报告生成失败!');
    				}
    			}
    		});
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.operateFailure'), '没有默认的模板，不能进行生成!');
    	}
    },
    /**
     * type:dailyReport日常风险报告;yearlyReport年度风险报告
     */
    reloadData:function(type){
    	var me = this;
    	me.type = type || me.type;//不传入，就直接用之前的那个type	
    	if(type=='yearlyReport'){
        	me.store.proxy.url = __ctxPath + me.yearlyReportUrl;
    	}else{	//dailyReport
        	me.store.proxy.url = __ctxPath + me.dailyReportUrl;
    	}
    	me.store.load();
    },

    destroy:function(){
    	var me = this;
    	if(me.generateReportWindow){
    		me.generateReportWindow.destroy();
    	}
    	
    	me.callParent(arguments);
    }
});