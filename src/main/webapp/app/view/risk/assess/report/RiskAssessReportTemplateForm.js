/**
 * @author : 邓广义
 *  风险评价报告模板管理表单面板
 */
Ext.define('FHD.view.risk.assess.report.RiskAssessReportTemplateForm',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.riskassessreporttemplateform',
    requires : [
                ],
	reloadData:function(templateId){
		var me = this;
		var url = __ctxPath + '/sys/report/findTemplateByid.f';
				me.load({
    	        	url:url,
	    	        params:{
	    	        	id:templateId
	    	        },
	    	        failure:function(form,action) {
	    	            alert("err 155");
	    	        },
	    	        success:function(form,action){
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
    	           {text : "保存",iconCls: 'icon-save', handler:me.save, scope : this},
    	           {text : "返回",iconCls: 'icon-arrow-undo', handler:me.undo, scope : this}];
		me.templateCode=Ext.create('Ext.form.TextField', {
    	    fieldLabel: '模板编号'+'<font color=red>*</font>',
    	    allowBlank:false,//不允许为空
    	    name:'templateCode',
    	    flex:.5
    	});
		me.templateName=Ext.create('Ext.form.TextField', {
    	    fieldLabel: '模板描述'+'<font color=red>*</font>',
    	    allowBlank:false,//不允许为空
    	    name:'templateName',
    	    flex:.5
    	});
		//模板word附件
		me.fileUploadEntityFileUpload = Ext.create("FHD.ux.fileupload.FileUpload",{
    		readonly:false,
            labelWidth : 100,
            name:'fileUploadEntityId',//名称
            showModel:'base',//显示模式
            multiSelect:false,
            labelText: $locale('fileupdate.labeltext')+ '<font color=red>*</font>',//标题名称
            allowBlank : false,
            margin : '7 30 3 30',
            labelAlign: 'left'//标题对齐方式
		});
    	
		Ext.applyIf(me,{
			tbar:bbar,
            border: false,
            bodyPadding: "5 5 5 5",
            flex:1,
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
            items:[me.templateName,me.templateCode,me.fileUploadEntityFileUpload]
    	});
    	
		me.callParent(arguments);
		me.add(me.basicFieldset);
//		me.createReportContent();
//		
//		me.processFieldset = Ext.widget('fieldset',{
//			collapsible: true,
//			collapsed : true,
//            autoHeight: true,
//            autoWidth: true,
//            defaults: {
//                margin: '0 30 3 30',
//                labelWidth: 105,
//            	labelAlign: 'left',
//                columnWidth: 1
//            },
//            layout: {
//                type: 'column'
//            },
//            title: '风险模板列表'
//    	});
//		me.add(me.processFieldset);
//    	
//    	me.processGrid = Ext.create('FHD.ux.GridPanel',{
//    		height:140,
//    		url: __ctxPath + '/comm/report/findReportProcessList.f',
//    		extraParams:{
//    			assessplanId:''
//    		},
//    		searchable:false,
//    		checked:false,
//    		pagable:false,
//    		cols:[{
//    			header:'名称',dataIndex:'parentProcessName',flex:3,
//    			renderer : function(value, metaData, record, colIndex, store, view) {
//					metaData.tdAttr = 'data-qtip="'+value+'"'; 
//		 			return value;
//				}
//    		},{
//    			header:'使用名',dataIndex:'processName',flex:3,
//    			renderer : function(value, metaData, record, colIndex, store, view) {
//					metaData.tdAttr = 'data-qtip="'+value+'"'; 
//		 			return value;
//				}
//    		},{
//    			header:'demo',dataIndex:'isPracticeTest',flex:2
//    		}]
//    	});
//    	
//    	me.processFieldset.add(me.processGrid);
		
	},
	undo:function(){
		var me = this;
		var mainPanel = me.up('riskassessreporttemplatemain');
		var treePanel = mainPanel.treePanel;
		var rightPanel = mainPanel.rightPanel;
		rightPanel.remove(me);
		var grid = Ext.create('FHD.view.risk.assess.report.RiskAssessReportTemplateGrid');
		grid.reloadData(treePanel.currentNode.data.code);
		grid.down('#report_template_add').setDisabled(false);
		rightPanel.add(grid);
	},
	save:function(){
		var me = this;
		var treePanel= me.up('riskassessreporttemplatemain').treePanel;
		var type = treePanel.currentNode && treePanel.currentNode.data.code;
		
    	if(me.form.isValid()) {
    		FHD.submit({
				form : me.form,
				params : {
					templateId:me.templateId,
					templateData: me.editor.html(),
					templateType: type,
					templateName:me.form.getValues()['templateName'],
					templateCode:me.form.getValues()['templateCode']
				},
				url : __ctxPath + '/sys/report/saveReportTemplate.f?',
				callback: function (data) {
				}
			});
		}
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
						});
					});
		        	me.editor = KindEditor.create('#' + (component.getEl().query('textarea')[0]).id,{
		        		minHeight:200,
		        		width:'100%',
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

    	
    	me.fieldset.add(me.reportData);
    
	}

});