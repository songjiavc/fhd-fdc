/**
 * @author : 邓广义
 *  风险评价报告
 */
Ext.define('FHD.view.risk.assess.report.RiskAssessReport',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.riskassessreport',
    requires : [
'FHD.ux.icm.common.FlowTaskBar'
                ],
	reloadData:function(){
		var me = this;
		var businessId = me.businessId;//评估计划的ID从工作流取得
		var url = __ctxPath + '/sys/report/findReportByAssessPlanIdOrDefaultTemplate.f';
				me.load({
    	        	url:url,
	    	        params:{
	    	        	AssessPlanId:businessId
	    	        },
	    	        failure:function(form,action) {
	    	            alert("err 155");
	    	        },
	    	        success:function(form,action){
	    	        	me.reportId = action.result.data.reportId;
//	    	        	me.currentId = action.result.data.currentId;
    	            	var responseJson = Ext.JSON.decode(action.response.responseText);
            			//加载报告内容
            			me.editor.html(responseJson.data.templateDataText);
	    	        }
	    	    });
	},
    initComponent: function () {
		var me = this;
		var bbar =[//按钮
    	           '->',
    	           {text : "保存",iconCls:'icon-control-stop-blue', handler:me.onSave, scope : this},
    	           {text : "提交",iconCls:'icon-operator-submit', handler:me.onSubmit, scope : this},
    	           {text : "预览",iconCls:'icon-application-view-gallery', handler:me.onPreview, scope : this}
    	           ]
    	
		Ext.applyIf(me,{
			bbar:bbar,
            border: false,
            bodyPadding: "5 5 5 5",
            flex:1,
            items : [Ext.widget('panel',{border:false,items:Ext.widget('flowtaskbar',{
          		jsonArray:[
        		    		{index: 1, context:'1.计划制定',status:'done'},
        		    		{index: 2, context:'2.计划审批',status:'done'},
        		    		{index: 3, context:'3.任务分配',status:'done'},
        		    		{index: 4, context:'4.风险评估',status:'done'},
        		    		{index: 5, context:'5.评估任务审批',status:'done'},
        		    		{index: 6, context:'6.评估结果整理',status:'done'},
        		    		{index: 7, context:'7.评估报告编制',status:'current'},
        		    		{index: 8, context:'8.评估报告审批',status:'undo'}
        		    	],
		    	margin : '5 5 5 5'

        	    	})})],
            overflowY:'auto'
		});
		me.basicFieldset = Ext.widget('fieldset',{
			collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '0 30 3 30',
                labelWidth: 105,
            	labelAlign: 'left',
                columnWidth: .5
            },
            layout: {
                type: 'column'
            },
            title: '基本信息',
            items:[me.templateName,me.templateCode]
    	});
    	
		me.callParent(arguments);
//		me.add(me.basicFieldset);
		me.createReportContent();
		
		me.processFieldset = Ext.widget('fieldset',{
			collapsible: true,
			collapsed : true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '0 30 3 30',
                labelWidth: 105,
            	labelAlign: 'left',
                columnWidth: 1
            },
            layout: {
                type: 'column'
            },
            title: '风险模板列表'
    	});
		me.add(me.processFieldset);
    	
    	me.processGrid = Ext.create('FHD.ux.GridPanel',{
    		height:140,
//    		url: __ctxPath + '/comm/report/findReportProcessList.f',
    		extraParams:{
    			assessplanId:''
    		},
    		searchable:false,
    		checked:false,
    		pagable:false,
    		cols:[{
    			header:'名称',dataIndex:'parentProcessName',flex:3,
    			renderer : function(value, metaData, record, colIndex, store, view) {
					metaData.tdAttr = 'data-qtip="'+value+'"'; 
		 			return value;
				}
    		},{
    			header:'使用名',dataIndex:'processName',flex:3,
    			renderer : function(value, metaData, record, colIndex, store, view) {
					metaData.tdAttr = 'data-qtip="'+value+'"'; 
		 			return value;
				}
    		},{
    			header:'demo',dataIndex:'isPracticeTest',flex:2
    		}]
    	});
    	
    	me.processFieldset.add(me.processGrid);
		
	},
	downloadFun : function(grid, rowIndex, colIndex) {
		var me = this;
		
		window.location.href = __ctxPath + '/sys/report/downloadReport.f';
	},
	onPreview:function(){
		var me = this;
		var html = '';
		me.body.mask("loading...","x-mask-loading");
        FHD.ajax({							
            url: __ctxPath + '/sys/report/previewAssessReport.f',
            async:true,
			params : {
				assessPlanId: me.businessId,
				reportData: me.editor.html()
			},
            callback: function (data) {
            	me.body.unmask();
            	html = data.stream || '';
    	    	me.win=Ext.create('FHD.ux.Window',{
						title : '详细查看',
						flex:1,
						autoHeight:true,
						autoScroll:true,
						collapsible : true,
						modal : true,
						maximizable : true,
						bodyPadding: "10 10 10 10",
						bodyStyle: {
						    background: '#fff'
						},
						html:'<div style="border:1px solid #000;padding:15px">' + html +'</div>'
				}).show();
            }
         });
        

	},
	onSave:function(){
		var me = this;
		var businessId = me.businessId;//评估计划的ID从工作流取得
		
    	if(me.form.isValid()) {
    		me.body.mask("保存中...","x-mask-loading");
    		FHD.submit({
				form : me.form,
				params : {
					id:me.reportId,
					assessPlanId: businessId,
					reportData: me.editor.html()
				},
				url : __ctxPath + '/sys/report/saveAssessReport.f',
				callback: function (data) {
					me.body.unmask();
				}
			});
		}
	},
	approveFn:function(form){
		var empId = form && form.items.items[0].value;
		return empId;
	},
	onSubmit:function(){
		var me = this;
		var empId = '';
		var approve = Ext.create('FHD.view.risk.assess.formulatePlan.FormulateApproverEdit');
		me.subWin = Ext.create('FHD.ux.Window', {
			title:'提交',
   		 	height: 300,
    		width: 600,
   			layout: 'fit',
   			buttonAlign: 'center',
    		items: [approve],
   			fbar: [
   					{ xtype: 'button', text: '确定', handler:function(){
   							empId = me.approveFn(approve);
   							me.subWin.close();
							me.body.mask("提交中...","x-mask-loading");
					        FHD.ajax({								
					            url: __ctxPath  + '/sys/report/doAssessReportWorkFlow.f',
								params : {
										executionId:me.executionId,
										AssessPlanId:me.businessId,
										empId:empId
								},
					            callback: function (data) {
					            	me.body.unmask();
					            	me.winId && Ext.getCmp(me.winId).close();
					            	if(!data.data){
					            		FHD.alert("提交前请先保存！");
					            		return
					            	}
					            }
					         });
   					}},
   					{ xtype: 'button', text: '取消', handler:function(){
   						me.subWin.close();
   					}}
				  ]
		}).show();

	},
	createReportContent:function(){

    	var me = this;
    	
    	me.fieldset = Ext.widget('fieldset',{
			collapsible: true,
            defaults: {
                margin: '7 10 3 30',
                labelWidth: 105,
            	labelAlign: 'left',
                columnWidth: 1
            },
            layout: {
                type: 'column'
            },
            title: '报告信息'
    	});
    	
    	me.add(me.fieldset);
    	
    	me.reportData = Ext.widget('textarea',{
    		name : 'templateDataText',
    		listeners:{
            	afterrender:function(component){
            		//插入自定义菜单
            		KindEditor.plugin('tableprop', function(K) {
						var self = this;
						name = 'tableprop';
						function click(value) {
							self.insertHtml('<p>${' + value + '}</p>');
							self.hideMenu();
						}
						self.clickToolbar(name, function() {
							var menu = self.createMenu({
								name : name,
								width : 150
							});
							menu.addItem({
								title : '一级风险数量',
								click : function() {
									click('一级风险数量');
								}
							});
							menu.addItem({
								title : '二级风险数量',
								click : function() {
									click('二级风险数量');
								}
							});
							menu.addItem({
								title : '三级风险数量',
								click : function() {
									click('三级风险数量');
								}
							});
							menu.addItem({
								title : '风险事件数量',
								click : function() {
									click('风险事件数量');
								}
							});
							menu.addItem({
								title : '新高风险列表',
								click : function() {
									click('新高风险列表');
								}
							});	
							menu.addItem({
								title : '风险事件列表',
								click : function() {
									click('风险事件列表');
								}
							});
							menu.addItem({
								title : '风险列表',
								click : function() {
									click('风险列表');
								}
							});
//							menu.addItem({
//								title : '流程风险分析',
//								click : function() {
//									click('流程风险分析');
//								}
//							});
//							menu.addItem({
//								title : '目标风险分析',
//								click : function() {
//									click('目标风险分析');
//								}
//							});
//							menu.addItem({
//								title : '风险分类分析',
//								click : function() {
//									click('风险分类分析');
//								}
//							});
						});
					});
		        	me.editor = KindEditor.create('#' + (component.getEl().query('textarea')[0]).id,{
		        		minHeight:200,
		        		width:'98.4%',
		        		items : [
							'source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'code', 'cut', 'copy', 'paste',
							'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
							'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
							'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
							'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
							'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
							'flash', 'media', 'insertfile', 'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
							'anchor', 'link', 'unlink', '|', 'about','/','tableprop'
						]
		        	});
		        	me.editor.resizeType = 1;
		        	
    	        }  
    		}
    	});
    	
//    	if('' === me.reportData.getValue()) {
//    		Ext.define('Tpl', {
//			    extend: 'Ext.data.Model',
//			    fields: ['tpldata']
//			});
//    		var store = Ext.create('Ext.data.Store', {
//			    model: 'Tpl',
//			    autoLoad : true,
//			    autoSync : true,
//			    proxy: {
//			        type: 'ajax',
//			        url :'',
//			        reader: {
//			            type: 'xml',
//			            record: 'tpl',
//			            root: 'tpls'
//			        }
//			    },
//			    listeners:{
//			    	load:function(s) {
//			    		me.editor.insertHtml(s.getAt(0).data.tpldata);
//			    	}
//			    }
//			});
//    	}
    	
    	me.fieldset.add(me.reportData);
    
	}

});