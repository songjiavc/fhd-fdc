/**
 * 
 * 风险整理表格
 */

Ext.define('FHD.view.risk.assess.newAgainRiskTidy.RiskTidyGridForSecurity', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.risktidygridforsecurity',
    requires: [
				 'FHD.view.risk.assess.newAgainRiskTidy.RiskTidyAssessGrid',
				 'FHD.view.risk.assess.quaAssess.QuaAssessEdit',
				 'FHD.view.risk.assess.newAgainRiskTidy.RiskTidyDeptApproveGrid'
            ],
    riskDatas : null,
    addRisk : function(){
    	var me = this;
    	me.addShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
    		saveUrl : '/risk/relate/saveRiskInfoForSecurity.f',
    		type:'re',	//如果是re,上级风险只能选择叶子节点
    		schm : 'security',
    		assessPlanId :  me.riskTidyMan.businessId,
    		executionId :  me.riskTidyMan.executionId,
    		dimList : Ext.JSON.encode(me.dimList),
    		border:false,
    		grid : me,
    		callback : function(grid){
 				me.formwindow.close();
 				me.riskTidyMan.assessTree.reloadData();
 			},
    		save : function(callback) {
				var me = this;
				var form = me.getForm();
				//责任部门
				var respDeptName = me.respDeptName.getValue();
				//相关部门
				var relaDeptName = me.relaDeptName.getValue();
				if (form.isValid() && me.customValidate()) {
					FHD.submit({
						form : form,
						url : __ctxPath + me.saveUrl,
						params : {
							isRiskClass : 're', // 风险还是风险事件
							//state:me.state,
							archiveStatus:me.archiveStatus,
							schm:'security',
							parentId: me.parentId.getValue().split(':')[1].replace('}]', "").replace('"', '').replace('"', ''),
							assessPlanId : me.assessPlanId,
							executionId: me.executionId,
							responseText : form.getValues().responseText,
				        	_type: 'task',
				        	dimList : me.dimList
						},
						callback : function(data) {
							if(callback){
								callback(me.grid);
							}
						}
					});
				}else{
					me.body.unmask();
					return false;
				}
			}
		});
		me.responseText = Ext.widget('textareafield',{name : 'responseText' });
		me.responseFieldSet = Ext.widget('fieldset',{
			collapsible : false,
			layout : 'fit',
			title : '应对意见',
			margin : '10 10 10 10',
			items : [me.responseText]
		});
		me.addShortForm.add(me.responseFieldSet);
    	me.formwindow = new Ext.Window({
 			layout:'fit',
 			iconCls: 'icon-show',//标题前的图片
 			modal:true,//是否模态窗口
 			collapsible:true,
 			title:'风险事件',
 			width:1000,
 			height:410,
 			autoScroll : true,
 			layout: {
 				type: 'vbox',
 	        	align:'stretch'
 	        },
 			maximizable:true,//（是否增加最大化，默认没有）
 			constrain:true,
 			items : [me.addShortForm],
 			buttons: [
 				{
 				    text:'保存',
 				    handler:function(){
 				 	   me.addShortForm.save(me.addShortForm.callback);
 				 	   
 					}
 				},
     			{
     				text: '关闭',
     				handler:function(){
     					me.formwindow.close();
     				}
     		}]
 		});
		me.formwindow.show();
    },
	edit : function(scoreObjectId,riskId){
    	var me = this;
		me.addShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
    		findUrl : '/cmp/risk/findSecurityRiskEditInfoById.f',
    		mergeUrl:'/risk/relate/mergeSecurityRiskInfo.f',
    		type:'re',	//如果是re,上级风险只能选择叶子节点
    		schm : 'security',
    		assessPlanId :  me.riskTidyMan.businessId,
    		executionId :  me.riskTidyMan.executionId,
    		scoreObjectId : scoreObjectId,
    		riskId : riskId,
    		dimList : Ext.JSON.encode(me.dimList),
    		border:false,
    		grid : me,
    		callback : function(grid){
 				me.formwindow.close();
 				me.riskTidyMan.assessTree.reloadData();
 			},
 			reloadData: function (id) {	//id是风险事件id
		    	var me = this;
		    	me.isEdit = true;
		    	
		    	//将变灰的数据项恢复过来
		    	me.parentId.grid.setDisabled(false);
				me.parentId.button.setDisabled(false);
				
		    	FHD.ajax({
		   			async:false,
		   			params: {
		                objectId : id
		            },
		            url: __ctxPath + me.findUrl,
		            callback: function (json) {
		            	//赋值
		            	me.form.setValues({
		        			parentId : json.parentId,
		        			code : json.code,
		        			name : json.name,
		        			desc : json.desc,
		        			influProcessureName : json.influProcessureName,
		        			templateId : json.templateId,
		        			responseText : json.responseText
		        		});
		
		        		//上级风险
		        		me.parentId.initValue();
		        		//责任部门，可能没有sethidden值
		        		
		        		if(json.respDeptName){
		        			var value = Ext.JSON.decode(json.respDeptName);
			        		me.respDeptName.setValues(value);
			        		
		        		}
		        		//相关部门
		        		if(json.relaDeptName){
		        			var value = Ext.JSON.decode(json.relaDeptName);
			        		me.relaDeptName.setValues(value);
			        		
		        		}
		            }
		        });
				
    		},
    		save : function(callback) {
				var me = this;
				var form = me.getForm();
				//责任部门
				var respDeptName = me.respDeptName.getValue();
				//相关部门
				var relaDeptName = me.relaDeptName.getValue();
				if (form.isValid() && me.customValidate()) {
					FHD.submit({
						form : form,
						url : __ctxPath + me.mergeUrl,
						params : {
							isRiskClass : 're', // 风险还是风险事件
							//state:me.state,
							archiveStatus:me.archiveStatus,
							scoreObjectId : me.scoreObjectId,
							schm:'security',
							parentId: me.parentId.getValue().split(':')[1].replace('}]', "").replace('"', '').replace('"', ''),
							assessPlanId : me.assessPlanId,
							executionId: me.executionId,
							riskId : me.riskId,
							responseText : form.getValues().responseText,
				        	_type: 'task',
				        	dimList : me.dimList
						},
						callback : function(data) {
							if(callback){
								callback(me.grid);
							}
						}
					});
				}else{
					me.body.unmask();
					return false;
				}
			}
		});
		me.responseText = Ext.widget('textareafield',{name : 'responseText' });
		me.responseFieldSet = Ext.widget('fieldset',{
			collapsible : false,
			layout : 'fit',
			title : '应对意见',
			margin : '10 10 10 10',
			items : [me.responseText]
		});
		me.addShortForm.add(me.responseFieldSet);
    	me.formwindow = new Ext.Window({
 			layout:'fit',
 			iconCls: 'icon-show',//标题前的图片
 			modal:true,//是否模态窗口
 			collapsible:true,
 			title:'风险事件',
 			width:1000,
 			height:410,
 			autoScroll : true,
 			layout: {
 				type: 'vbox',
 	        	align:'stretch'
 	        },
 			maximizable:true,//（是否增加最大化，默认没有）
 			constrain:true,
 			items : [me.addShortForm],
 			buttons: [
 				{
 				    text:'保存',
 				    handler:function(){
 				 	   me.addShortForm.save(me.addShortForm.callback);
 				 	   
 					}
 				},
     			{
     				text: '关闭',
     				handler:function(){
     					me.formwindow.close();
     				}
     		}]
 		});
 		me.addShortForm.reloadData(scoreObjectId);
		me.formwindow.show();
    },
    
    del : function(id){
    	var me = this;
    },
    
    onSave:function(data){
    	var me = this;
    	var rows = me.store.getModifiedRecords();
    	var objectId = rows[0].data.objectId;
    	var obj = {
    		score:data.value,
    		scoreDimId:data.field,
    		objectId:objectId
    	};
		FHD.ajax({
			async:false,
			params: {
		       	assessPlanId : me.riskTidyMan.businessId,
		       	params : Ext.encode(obj)
		   	},
		   	url : __ctxPath + '/assess/riskTidy/riskTidySaveAssess.f',
		   	callback: function (ret) {
		   		me.store.load();
		   	}
		});
    },
    
    //导出grid列表
    exportChart:function(businessId, sheetName, exportFileName){
    	var me=this;
    	var businessId;
    	var type;
    	var typeId;
    	me.headerDatas = [];
    	if(me.gridParams){
    		if(me.gridParams.assessPlanId){
    			businessId = me.gridParams.assessPlanId
    		}
    		if(me.gridParams.type){
    			type = me.gridParams.type;
    		}
    		if(me.gridParams.typeId){
    			typeId = me.gridParams.typeId;
    		}
    	}
    	var items = me.columns;
			Ext.each(items,function(item){
				if(!item.hidden&&""!=item.dataIndex){
				var value = {};
				value['dataIndex'] = item.dataIndex;
            	//value['text'] = item.text;
				if('riskIcon'==item.dataIndex){
					value['text'] = '风险等级';
				}else{
					value['text'] = item.text;
				}
            	me.headerDatas.push(value);
				}
			});
		
    	sheetName = 'exportexcel';
    	//exportFileName = '评估结果预览数据';
    	window.location.href = __ctxPath + "/assess/riskTidy/exportrisktidygrid.f?businessId="+businessId+"&exportFileName="
    							+""+"&sheetName="+sheetName+"&headerData="+Ext.encode(me.headerDatas)
    							+"&type="+type+"&typeId="+typeId;
    },
    
    delGrid:function(){
  		var me = this;
    	var selection = me.getSelectionModel().getSelection();
    	if(selection.length == 0){
    		FHD.notification('请选中后删除',FHD.locale.get('fhd.common.prompt'));
    		return;
    	}
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {
					me.riskTidyMan.body.mask("删除中...","x-mask-loading");
					var ids = [];
					for(var i=0;i<selection.length;i++){
						ids.push(selection[i].get('objectId'));
					}
					FHD.ajax({
						url : __ctxPath + '/assess/riskTidy/delRiskRbsSf2ForSecurity.f',
						params: {
			              	assessPlanId : me.riskTidyMan.businessId,
			              	ids : ids
						},
						callback : function(data){
							if(data){
								me.riskTidyMan.body.unmask();
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								me.store.load();
								//me.riskTidyMan.assessTree.processTree.extraParams.ids = data.treeIds;
								me.riskTidyMan.assessTree.reloadData();
								/*
									me.riskTidyMan.riskTidyTab.riskCategoryPanel.store.load();
				   	   			    me.riskTidyMan.riskTidyTab.orgTreeGrid.store.load();
					   	   			me.riskTidyMan.riskTidyTab.kpiprocessPanel.store.load();
	   	   			            	me.riskTidyMan.riskTidyTab.processPanel.store.load();
	   	   			            */
							}
						}
					});
				}
			} 
		});
  	},
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
			{
				dataIndex:'id',
				hidden:true
			},{
	            header: "上级风险",
	            dataIndex: 'riskParentName',
	            sortable: true,
	            //align: 'center',
	            flex:.1,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
	            	return value;
	            }
	        },{
	        	dataIndex : 'riskId',
	        	hidden : true
	        },{
				dataIndex:'objectId',
				hidden:true
		    },{
	            header: "风险名称",
	            dataIndex: 'riskName',
	            sortable: true,
	            //align: 'center',
	            flex:.3,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
	            	return "<a href=\"javascript:void(0);\">"+value+"</a>";
     			},
     			listeners:{
	        		click:{
	        			fn:function(g,d,i){
	        				var scoreObjectId = me.getSelectionModel().getSelection()[0].data.objectId;
	        				var assessPlanId = me.riskTidyMan.businessId;
	        				var riskId = me.getSelectionModel().getSelection()[0].data.riskId;
	        				me.edit(scoreObjectId,riskId);
	        			}
					}
     			}
	        },{
	        	header:"部门辨识意见",
				dataIndex:'idea',
				flex:.1,
				renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
	            	return "<a href=\"javascript:void(0);\">"+value+"</a>";
     			}
	        },{
	        	header:"部门应对意见",
				dataIndex:'residea',
				flex:.1,
				renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
	            	return "<a href=\"javascript:void(0);\">"+value+"</a>";
     			}
	        },
			{
				header : "辨识部门",
				dataIndex:'orgName',
				flex:.1,
				hidden:false
			},{
				dataIndex : 'riskId',
				hidden : true
			},{
				header : "辨识责任人",
				dataIndex:'empName',
				flex:.1,
				hidden:false
			}
        ];
		/*
        cols.push({
			header: "状态",
			dataIndex:'riskStatus',
			flex:.1,           
			renderer:function(value,metaData,record,colIndex,store,view) {
            	return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
            	+ value + "'/>";
 			}
		});
		*/
        	Ext.apply(me,{
            	region:'center',
            	layout: 'fit',
            	type: 'editgrid',
            	url : __ctxPath + '/assess/riskTidy/findTidyRiskReByRiskForSecurity.f',//__ctxPath + "/app/view/risk/assess/riskTidy/list.json",
            	extraParams : me.extraParams,
            	cols:cols,
            	autoScroll:true,
            	border: false,
    		    checked: true,
    		    pagable : false,
    		    searchable : true,
    		    columnLines: true,
    		    isNotAutoload : true,
    		    storeAutoLoad:false,
    		    tbarItems:[
                   '<span style="font-size:12px;font-weight:bold;color: #15498b;margin-right:0">风险总数:</span>'+ 
    				"<span id='risk-tidy-card-num" + me.id + "'>" + 0 + "</span>",
                   {
       				text: '导出',
       				iconCls: 'icon-ibm-action-export-to-excel',
       				handler:function(){
       					me.exportChart();
       				}
       			},{
    				text : '删除',
    				iconCls : 'icon-del',
    				name:'riskTidyGrid_delete',
    				handler : function(){
    					me.delGrid();
    				},
    				scope : this
    			},{
    					text : '添加风险',
    					iconCls : 'icon-cog',
    					handler : function() {
    						me.addRisk();
    					}
    				}
    			]
            });
        me.callParent(arguments);

        me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3,5]);
        	me.gridParams = me.store.proxy.extraParams;
        	var count = me.store.getCount();
	     	Ext.get('risk-tidy-card-num' + me.id).setHTML(count);
        });
        
	   	me.on('edit', function (event,value) {
	   	    me.onSave(value);
	    });
    }

});