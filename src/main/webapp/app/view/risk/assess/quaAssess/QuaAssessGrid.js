/**
 * 
 * 定性评估表格
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.quaAssessGrid',
    
    requires : [ 'FHD.view.risk.assess.quaAssess.QuaAssessEdit' ],
    
    riskDatas : null,
    
    reloadData: function(executionId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/assess/quaAssess/findAssessGrid.f';//查询列表
    	me.store.proxy.extraParams.assessPlanId = me.businessId;
	    me.store.proxy.extraParams.executionId = executionId;
	    me.store.load();
    },
    
    edit : function(riskId, rangObjectDeptEmpId){
    	var me = this;
    	
    	me.quaAssessEdit = Ext.widget('quaAssessEdit',{isEditIdea : true});
    	
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			width:900,
			height:463,
			title : '风险详细信息查看',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.quaAssessEdit],
			buttons: [
    			{
    				text: '修改',
    				handler:function(){
    					FHD.ajax({
    			            url: __ctxPath + '/assess/quaassess/saveEditIdea.f',
    			            params: {
    			            	editIdeaContent : me.quaAssessEdit.editIdea.getValue(),
    			            	objectDeptEmpId : rangObjectDeptEmpId
    			            },
    			            callback: function (data) {
    			            	me.store.load();
    			            	me.riskDatas = [];
    			            	Ext.MessageBox.alert('修改信息','修改成功');
    			            	me.formwindow.close();
    			            	//Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
    			            }
    			        });
    				}
    			},
    			{
    				text: '关闭',
    				handler:function(){
    					me.formwindow.close();
    				}
    			}
    	    ]
		});
		me.formwindow.show();
		me.quaAssessEdit.load(riskId, rangObjectDeptEmpId);
		me.quaAssessEdit.on('resize',function(p){
    		me.quaAssessEdit.detailAllForm.setHeight(me.formwindow.getHeight()-62);
	   	});
    },
    
    delGrid:function(riskId){//删除新增风险方法
  		var me = this;
    	var selection = me.getSelectionModel().getSelection();
		Ext.MessageBox.show({
			title : FHD.locale.get('fhd.common.delete'),
			width : 260,
			msg : FHD.locale.get('fhd.common.makeSureDelete'),
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				if (btn == 'yes') {
					FHD.ajax({
						url : __ctxPath + '/assess/quaassess/deletetaskbysome.f',
						params: {
			              	assessPlanId : me.businessId,
			              	riskId : riskId
						},
						callback : function(data){
							if(data){
								FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
								me.riskDatas = [];
								me.store.load();
							}
						}
					});
				}
			} 
		});
  	},
  	
  	editGrid : function(riskId){//修改新增风险方法
  		var me = this;
  		if(!riskId){
  			Ext.MessageBox.alert('修改信息','请选择一条风险事件进行修改!');
  			return;
  		}
  		//风险信息组件
		me.editForms = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
    		type:'re',	//如果是re,上级风险只能选择叶子节点
    		border:false,
    		setLoginDept: true,//只读属性 
    		callback:function(){
    			me.editFormWin.body.unmask();
    			me.editFormWin.hide();
    			me.riskDatas = [];
    			me.store.load();
    		}
    	});
	    me.editForms.reloadData(riskId);//加载风险信息
		me.editFormWin = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			width:900,
			height:463,
			title : '风险信息修改',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.editForms],
			buttons: [
						{
							text: '保存',
							handler:function(){
								var isAdd = me.editForms.save(me.editForms.callback);
								if(!isAdd){
									me.editFormWin.body.mask("保存中...","x-mask-loading");
								}
							}
						},
		    			{
		    				text: '关闭',
		    				handler:function(){
		    					me.editFormWin.close();
		    				}
		    			}
		    		]
		});
		me.editFormWin.show();
	},
    
    getColsInit : function(){
    	var me = this;
    	
    	var cols = [
    				{
    					dataIndex:'riskId',
    					hidden:true
    				},
    				{
    					dataIndex:'templateId',
    					hidden:true
    				},
    				{
    					dataIndex : 'rangObjectDeptEmpId',
    					hidden:true
    				},
    				
    		        {
    		            header: "上级风险",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            flex:.4
    		        },
    		        {
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            flex:2,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	var value = {};
    		            	
    		            	value['riskId'] = record.get('riskId');
    		            	value['templateId'] = record.get('templateId');
    		            	value['rangObjectDeptEmpId'] = record.get('rangObjectDeptEmpId');
    		            	value['riskName'] = record.get('riskName');
    		            	if(me.riskDatas.length>0){//将me.riskDatas中所有riskId取出放进riskIdArray数组
    		            		var riskIdArray = [];
    		            		for(var i=0;i<me.riskDatas.length;i++){
    		            			riskIdArray.push(me.riskDatas[i].riskId);
    		            		}
    		            		if(!(Ext.Array.contains(riskIdArray,value.riskId))){//判断数组中是否已经存在value值，不存在则保存
    		            			me.riskDatas.push(value);
    		            		}
    		            	}else{
        		            	me.riskDatas.push(value);
    		            	}
    		            	
    		            	/*value['riskId'] = record.get('riskId');
    		            	value['templateId'] = record.get('templateId');
    		            	value['rangObjectDeptEmpId'] = record.get('rangObjectDeptEmpId');
    		            	value['riskName'] = record.get('riskName');
    		            	me.riskDatas.push(value);*/
    		            	
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('riskId') + "','" 
    		            			+ record.get('rangObjectDeptEmpId') + "')\">" + 
    		            			record.get('riskName') + "</a>";
    	     			}
    		        },
    		        {
    		            header: "操作",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            flex:.5,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	if(record.get('riskTitle').indexOf('new') != -1){
    		            		return "<a href=\"javascript:void(0);\" " +
		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('riskId') + "','" 
		            			+ record.get('rangObjectDeptEmpId') + "')\">意见反馈</a>" + 
    		            		
    		            		 "　<a href=\"javascript:void(0);\" " +
		            			"onclick=\"Ext.getCmp('" + me.id + "').editGrid('" + record.get('riskId') + "')\">修改</a>"+ 
    		            		
    		            		 "　<a href=\"javascript:void(0);\" " +
		            			"onclick=\"Ext.getCmp('" + me.id + "').delGrid('" + record.get('riskId') + "')\">删除</a>";
    		            	}else{
    		            		return "<a href=\"javascript:void(0);\" " +
		            			"onclick=\"Ext.getCmp('" + me.id + "').edit('" + record.get('riskId') + "','" 
		            			+ record.get('rangObjectDeptEmpId') + "')\">意见反馈</a>";
    		            	}
    	     			}
    		        },
    		        {
    		            header: "修改意见",
    		            dataIndex: 'editIdeaContent',
    		            sortable: false,
    		            flex:2
    		        },
    		        {
    		            header: "状态",
    		            dataIndex: 'riskTitle',
    		            sortable: true,
    		            flex:.2,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' " +
    		            			"class='" + record.get('riskTitle') + "'/>";
    	     			}
    		        }
    	        ];
    	
    	return cols;
    },
    
    getSubmit : function(){
    	var me = this;
    	
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.riskDatas = [];
        
        var cols = null;
        
        cols = me.getColsInit();
        
        Ext.apply(me,{
        	region:'center',
        	//margin : '5 5 5 5',
        	//url : me.url,
        	cols:cols,
        	scroll : 'vertical',
        	storeAutoLoad:false,
		    border: false,
		    checked: false,
		    pagable : false,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : true
        });
        me.callParent(arguments);
        
        me.store.on('load',function(){
        	//Ext.getCmp('quaAssessCardNavId').setText('风险数量:' + me.store.data.length);
        	
        	me.infoNav = Ext.widget('infoNav',{height : 10, executionId : me.executionId, businessId : me.businessId});
            me.infoNav.load(me.riskDatas);
        	
//        	Ext.widget('gridCells').mergeCells(me, [3,4,5]);
        	//me.on('selectionchange',function(){me.setstatus(me)});
        });
    }
});