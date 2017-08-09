Ext.define('FHD.view.risk.riskidentify.taskset.NewRiskIdentifyTaskGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.newRiskIdentifyTaskGrid',
    requires: [
	],
    
    reloadData: function(){
    	var me = this;
    	me.store.proxy.url = __ctxPath + "/access/riskidentify/findidentifyrisksbybusinessid.f";
 		me.store.proxy.extraParams.businessId = me.businessId;
 		me.store.load();	
    },
    //添加风险
	addRisk: function(){
		
		var me = this;
		me.addAllShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
			type:'re',	//如果是re,上级风险只能选择叶子节点
			border:false,
			schm:me.schm,
			state : '2',
			
			assessPlanId : me.businessId,
			executionId: me.executionId,
        	_type: 'task',
			
			setLoginDept : true,
			hiddenSaveBtn:true,
			userValidate : function(){
				return true;
			},
			callback:function(data){
				me.addAllShortForm.body.unmask();
            	Ext.MessageBox.alert('添加信息','添加成功');
            	me.formwindow.close();
            	me.reloadData();
				/*if(data.id != null){
					var parentId = me.addAllShortForm.parentId.getValue().split(':')[1].replace('}]', "").replace('"', '').replace('"', '')
					FHD.ajax({
			            url: __ctxPath + '/access/riskidentify/savetaskbysome.f',
			            params: {
			            	parentId : parentId,
			            	assessPlanId : me.businessId,
			            	riskId : data.id,
			            	executionId: me.executionId,
			            	type: 'task'
			            },
			            callback: function (data) {
			            	if(data){
			            		me.addAllShortForm.body.unmask();
				            	Ext.MessageBox.alert('添加信息','添加成功');
				            	me.formwindow.close();
				            	me.reloadData();
			            	}
			            }
			        });
				}*/
			}
		});
		
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			width:900,
			height:400,
			title : '风险信息添加',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.addAllShortForm],
			buttons: [
				{
					text: '保存',
					handler:function(){
						var isAdd = me.addAllShortForm.save(me.addAllShortForm.callback);
						if(isAdd){
							me.addAllShortForm.body.mask("保存中...","x-mask-loading");
						}
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
	},
	//显示风险详细信息
    showRiskInfo: function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
		var riskId = selection[0].get('riskId');
		var objectId = selection[0].get('objectId');
		if("" != riskId){
			me.quaAssessEdit = Ext.create('FHD.view.risk.assess.quaAssess.QuaAssessEdit',{
	    		isEditIdea : false,
	    		objectId:objectId
	    	});
	    	me.formwindow = new Ext.Window({
				layout:'fit',
				iconCls: 'icon-show',//标题前的图片
				modal:true,//是否模态窗口
				collapsible:true,
				width:800,
				height:500,
				title : '风险详细信息查看',
				maximizable:true,//（是否增加最大化，默认没有）
				constrain:true,
				items : [me.quaAssessEdit],
				buttons: [
	    			{
	    				text: '关闭',
	    				handler:function(){
	    					me.formwindow.close();
	    				}
	    			}
	    	    ]
			});
			me.formwindow.show();
			me.quaAssessEdit.load(riskId, '');
		}
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [ 
		{header : "riskId",dataIndex:'riskId', hidden: true},
		{header : "planId",dataIndex:'planId', hidden: true},
		{dataIndex :'objectId',hidden : true},
		{
			header : "上级风险",
			dataIndex : 'parentRiskName',
			sortable : false,
			flex : 1
		}, {
			header : "风险名称",
			dataIndex : 'riskName',
			sortable : false,
			flex : 2,
			renderer:function(value,metaData,record,rowIndex ,colIndex,store,view){
						metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
						return "<a href=\"javascript:void(0);\">"+value+"</a>";
					},
			listeners:{
		        		click:{
		        			fn:function(g,d,i){
		        				//单击风险名称，显示风险基本信息组件
		        				me.showRiskInfo();
		        			}
        				}
        			}
		}
		];
		
		me.tbar = [
        		   {text:'添加风险', iconCls: 'icon-add', handler:function(){
        		   		me.addRisk();
        		   }}
                   ];
        
        Ext.apply(me, {
        	region:'center',
        	storeAutoLoad: false,
            cols:cols,
		    border: true,
		    checked: false,
		    columnLines: true,
		    pagable : false,
		    scroll: 'vertical',
		    searchable : true,
		    tbarItems: me.tbar
        });

        me.callParent(arguments);
        
        me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3]);
        });
    }

});