Ext.define('FHD.view.risk.riskidentify.tidy.IdentifyTidyGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.identifyTidyGrid',
   	requires: [ ],
    
    edit : function(scoreObjectId){
    	
    	var me = this;

    	me.riskTidyAssessGrid = Ext.create('FHD.view.risk.riskidentify.tidy.IdentifyTidyAssessGrid',{
			storeAutoLoad : false	
    	});
    	
        me.riskTidyDeptApproveGrid = Ext.create('FHD.view.risk.riskidentify.tidy.IdentifyTidyDeptApproveGrid',{
        	storeAutoLoad : false
        });
        
        me.assessGridSet = {
				xtype : 'fieldset',
				collapsible : true,
				title : '辨识详细',
				margin : '10 10 10 10',
				items : [me.riskTidyAssessGrid]
		};
		
        me.riskTidyDeptApproveGridSet = {
				xtype : 'fieldset',
				collapsible : true,
				title : '部门风险管理员意见',
				margin : '10 10 10 10',
				items : [me.riskTidyDeptApproveGrid]
		};
        if(me.operType == 'common'){
	        me.addShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormForCleanUp', {
	    		scoreObjectId:scoreObjectId,
	    		schm:me.schm,
	    		border:false,
	    		callback:function(data){
	    			if(data.flag == 'success'){
	    				FHD.notification('修改成功!',data.message);
		    			me.formwindow.body.unmask();
		    			me.formwindow.close();
		    			me.store.load();
	    			}
	    		}
	    	});
    		me.formwindow = Ext.widget('fhdwindow',{
		 			iconCls: 'icon-show',//标题前的图片
		 			modal:false,//是否模态窗口
		 			bodyStyle : {
						background : 'white'
					},
		 			collapsible:true,
		 			title:'风险事件',
		 			width : 1000,
		 			height : 700,
		 			autoScroll : true,
		 			layout: {
		 				type: 'vbox',
		 	        	align:'stretch'
		 	        },
		 			maximizable:true,//（是否增加最大化，默认没有）
		 			items : [me.addShortForm,me.assessGridSet,me.riskTidyDeptApproveGridSet],
		 			buttons: [
		 				{
		 				    text:'保存',
		 				    handler:function(){
		 				 	   me.formwindow.body.mask("保存中...","x-mask-loading");
		 				 	   me.addShortForm.merge(scoreObjectId,me.addShortForm.callback);
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
        }else{
        	
        	me.addShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormForCleanUpDetail', {
				scoreObjectId : scoreObjectId
			});
			
			me.formwindow = Ext.widget('fhdwindow',{
				iconCls: 'icon-show',//标题前的图片
				modal:false,//是否模态窗口
				bodyStyle : {
					background : 'white'
				},
				collapsible:true,
				title:'风险事件',
				width : 1000,
				height : 700,
				autoScroll : true,
				layout: {
					type: 'vbox',
		        	align:'stretch'
		        },
				maximizable:true,//（是否增加最大化，默认没有）
				items : [me.addShortForm,me.assessGridSet,me.riskTidyDeptApproveGridSet]
			});
			me.formwindow.show();
        }
		//宋佳更改传入参数类型
        me.addShortForm.reloadData(scoreObjectId);
		me.riskTidyAssessGrid.reloadData(scoreObjectId);
	   	me.riskTidyDeptApproveGrid.reloadData(scoreObjectId);
    },
    
    //导出grid列表
    exportChart:function(businessId, sheetName, exportFileName){
    	var me=this;
    	var businessId;
    	var type;
    	var typeId;
    	me.headerDatas = [];
    	var query = me.searchField.lastValue;
    	if(!query){
    		query = "";
    	}
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
            	value['text'] = item.text;
            	me.headerDatas.push(value);
				}
			});
		
    	sheetName = 'exportexcel';
    	window.location.href = __ctxPath + "/access/riskidentify/exportidenntifytidygrid.f?businessId="+businessId+"&exportFileName="
    							+""+"&sheetName="+sheetName+"&headerData="+Ext.encode(me.headerDatas)
    							+"&type="+type+"&typeId="+typeId+"&query="+query;
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
						ids.push(selection[i].get('riskId'));
					}
					FHD.ajax({
						url : __ctxPath + '/assess/riskTidy/delRiskRbs.f',
						params: {
			              	assessPlanId : me.riskTidyMan.businessId,
			              	ids : ids
						},
						callback : function(data){
							if(data){
								me.riskTidyMan.body.unmask();
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								me.store.load();
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
	            flex:.1,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
	            	return value;
	            }
	        },{
				dataIndex:'riskId',
				hidden:true
		    },{
	            header: "风险名称",
	            dataIndex: 'riskName',
	            sortable: true,
	            flex:.5,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
	            	return "<a href=\"javascript:void(0);\">"+value+"</a>";
     			},
     			listeners:{
	        		click:{
	        			fn:function(g,d,i){
	        				var scoreObjectId = me.getSelectionModel().getSelection()[0].data.id;
	        				me.edit(scoreObjectId);
        				}
					}
				}
	        }, 
	        {
				dataIndex:'templateId',
				hidden:true
			},{
				header:"辨识人数",
				dataIndex:'empCount',
				flex:.1,
				hidden:false
			},
			{	header: "状态",
				dataIndex:'riskStatus',
				flex:.1,           
				renderer:function(value,metaData,record,colIndex,store,view) {
            		return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
            		+ value + "'/>";
 				}
			},
			{	header: "标识",
				dataIndex:'deleteStatus',
				flex:.1,           
				renderer:function(value,metaData,record,colIndex,store,view) {
					var label = "";
					if(value == 100){
						label = "<font color='red'>(已删除)</font> ";
					}else{
						label = "<font color='green'>(正常)</font> ";
					}
            		return label;
 				}
			}
        ];
        
    	Ext.apply(me,{
        	region:'center',
        	layout: 'fit',
        	type: 'editgrid',
        	url : __ctxPath + '/access/riskidentify/findriskrebyrisk.f',
        	extraParams : me.extraParams,
        	cols:cols,
        	//autoScroll:true,
        	scroll: 'vertical',
        	border: false,
		    checked: true,
		    pagable : false,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : true,
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
			}
			]
        });
    	
        me.callParent(arguments);

        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3]);
        	me.gridParams = me.store.proxy.extraParams;
        	var count = me.store.getCount();
	     	Ext.get('risk-tidy-card-num' + me.id).setHTML(count);
        });
    }
});