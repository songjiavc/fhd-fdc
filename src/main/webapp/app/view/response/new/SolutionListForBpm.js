/*
 * 内控评价列表页面 
 * */
Ext.define('FHD.view.response.new.SolutionListForBpm',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.solutionlistforbpm',
    requires: [
    	'FHD.view.response.SolutionViewForm'
    ],
    url : '',
    columnLines: true,
    pagable:false,
    layout: 'fit',
    flex : 12,
    border:false,
	//可编辑列表为只读属性
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	//显示风险基本信息
	showRisk: function(riskId){
		var me = this;
		me.riskdetailform = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail');
		me.riskdetailform.reloadData(riskId);
		var riskwin = Ext.create('FHD.ux.Window',{
			title:'风险基本信息',
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
	    }).show();
		riskwin.add(me.riskdetailform);
	},
    initComponent: function(){
    	var me = this;
		//评价计划列表
        me.cols = [
        	{header : '风险分类Id',dataIndex : 'riskParentId',sortable : false, hidden : true},
        	{header : '风险Id',dataIndex : 'riskId',sortable : false, hidden : true},
    		{
    			header : '风险名称',dataIndex : 'riskName',sortable : false, flex : 5,height : '19px',
    	     	renderer : function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="'+value+'"';
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showRisk('" +record.get('riskId')+"')\" >" + value +"</a>";
            }
    		}, 
 			{header : '措施id',dataIndex : 'measureId',sortable : false, hidden : true},
 			{header : '措施名称',dataIndex : 'measureName',sortable : false, flex : 5,height: '30px',
 			 renderer: function (value, metaData, record, colIndex, store, view) {
 				if(value){//value为空不提示
 					metaData.tdAttr = 'data-qtip="'+value+'"';
 				}
                var id = record.get('measureId');
                if(id == null){
                	var btnUrl = "";
                }else{
                	var	btnUrl = "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showSolution('" + id + "','"+ record.get('riskId')+"')\" >" + value +"</a>";
                }
                return btnUrl;
            }
 			},
 			{header : '',dataIndex : 'orgId',sortable : false, hidden : true},
 			{header : '主责部门',dataIndex : 'orgName',sortable : false, flex : 1,height: '30px'
 			},
 			{header : '',dataIndex : 'empId',sortable : false, hidden : true},
 			{header : '责任人',dataIndex : 'empName',sortable : false, flex : 1,
 			renderer:function(value,metaData,record,colIndex,store,view) { 
			    	return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;"
								+ "background-position: center top;' data-qtitle='' "
								+ ">"+value+"</div>";
			    		}
 			}
 			
 			
		];
		
        me.tbarItems = [
        	{iconCls : 'icon-add',text: '添加应对措施',tooltip: '添加应对措施',handler :me.addSolution,scope : this},
			'-', 
			{iconCls : 'icon-edit',text: '修改',tooltip: '修改措施',handler :me.editMeasure,disabled: true,scope : this},
			'-', 
			{iconCls : 'icon-del',text: '删除',tooltip: '删除应对方案',handler :me.delMeasure,disabled: true,scope : this}
        ];
        me.callParent(arguments);
        me.on('selectionchange',function(){me.onchange()});
        //me.on('selectionchange',me.onchange);//选择记录发生改变时改变按钮可用状态
        me.on('afterLayout',function(){ 
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [4]);
        });
        me.onchange();
    },
    colInsert: function (index, item) {
        if (index < 0) return;
        if (index > this.cols.length) return;
        for (var i = this.cols.length - 1; i >= index; i--) {
            this.cols[i + 1] = this.cols[i];
        }
        this.cols[index] = item;
    },
    addSolution:function(){
    	var me=this;
    	var selection = me.getSelectionModel().getSelection();//得到选中的记
    	var solutionform = Ext.create('FHD.view.response.new.SolutionForm',{
    		archiveStatus : 'examine',//审批中
    		/* 覆盖solutionform 自己的cancel方法  */
    		callback : function(){
    			win.close();
    			me.reloadData();
    		}
    	});
	    solutionform.initParam({
	    	'type' : '1',
    		'riskId' : selection[0].get('riskId'),
    		'solutionId' : '',
    		'businessId' : me.paramObj.businessId
    	});
    	solutionform.clearFormData();
    	//solutionform.riskFieldSet.add(solutionform.riskdetailform);
    	//solutionform.riskdetailform.reloadData(selection[0].get('riskId'));
    	var win = Ext.create('FHD.ux.Window',{
			title:'添加应对措施',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(solutionform);
    },
    //修改措施
    editMeasure:function(){
    	var win = Ext.create('FHD.ux.Window',{
			title:'添加应对计划',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	var me=this;
    	var solutionform = Ext.widget('solutionform',{
    		archiveStatus : 'examine',//审批中
    		/* 覆盖solutionform 自己的cancel方法  */
    		callback : function(){
    			win.close();
    			me.reloadData();
    		}
    	});
	    var selection = me.getSelectionModel().getSelection();//得到选中的记
	    solutionform.initParam({
    		'riskId' : selection[0].get('riskId'),
    		'solutionId' : selection[0].get('measureId')
    	});
	    solutionform.clearFormData();
    	solutionform.reloadData();
    	win.add(solutionform);
    },
    //修改措施
    delMeasure:function(){
    	var me=this;
    	var delUrl = '';
    	var solutioneditpanel = me.up('solutioneditpanel');
    	var selection = me.getSelectionModel().getSelection();//得到选中的记录
    	if(0 == selection.length){
    		FHD.notification(FHD.locale.get('fhd.common.msgDel'),FHD.locale.get('fhd.common.prompt'));
    	}else{
    		delUrl = '/response/removeresponselistbyids.f';
			Ext.MessageBox.show({
				title : FHD.locale.get('fhd.common.delete'),
				width : 260,
				msg : FHD.locale.get('fhd.common.makeSureDelete'),
				buttons : Ext.MessageBox.YESNO,
				icon : Ext.MessageBox.QUESTION,
				fn : function(btn) {
					if(btn == 'yes') {
	 					var ids = [];
						for(var i = 0; i < selection.length; i++) {
							var isSubmit=selection[i].get('status');
					    	if(isSubmit=='P'){
					    		FHD.notification('对不起,您不能删除已提交的数据!',FHD.locale.get('fhd.common.prompt'));
					    		return false;
					    	}else{
					    		ids.push(selection[i].get('measureId'));
					    	}
	 					}
	 					FHD.ajax({
	 						url : __ctxPath+ delUrl,
	 						params : {
	 							/*measureIds*/
	 							ids : ids
	 						},
	 						callback : function(data) {
	 							if (data.success) { 
	                                FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
		 							me.store.load();
	                            } else {
	                                FHD.notification(FHD.locale.get('fhd.common.operateFailure'),FHD.locale.get('fhd.common.prompt'));
	                            }
	   						}
						});
					}
				}
			});
    	}
    },
	reloadData:function(){
		var me=this;
		me.store.proxy.url = __ctxPath + '/responseplan/findresponselistbybusinessid.f';
		me.store.proxy.extraParams = {
        	businessId : me.paramObj.businessId,
        	executionId : me.paramObj.executionId
        };
		me.store.load();
	},
	onchange :function(){//设置你按钮可用状态
		var me = this;   // iconCls : 'icon-del',
		var selection = me.getSelectionModel().getSelection();
		if(selection.length == 1){//选择风险大于1，不能添加应对措施
			me.down('[iconCls=icon-add]').setDisabled(false);
		}else{
			me.down('[iconCls=icon-add]').setDisabled(true);
		}
		//me.down('[iconCls=icon-add]').setDisabled(me.getSelectionModel().getSelection().length === 0);
		if(selection.length > 0 && selection[0].get('measureId')!= null){
			if(selection.length == 1){
				me.down('[iconCls=icon-edit]').setDisabled(false);
			}else{
				me.down('[iconCls=icon-edit]').setDisabled(true);
			}
			me.down('[iconCls=icon-del]').setDisabled(false);
		}else{
			me.down('[iconCls=icon-edit]').setDisabled(true);
			me.down('[iconCls=icon-del]').setDisabled(true);
		}
	},
	showSolution : function(id,riskId){
		var me=this;
		var solutionformforview = Ext.create('FHD.view.response.new.SolutionFormForView');
		solutionformforview.basicInfoFieldset.expand();
		//solutionformforview.riskFieldSet.expand();
		solutionformforview.riskdetailform.expand();
    	solutionformforview.initParam({
    		'solutionId' : id,
    		'riskId' : riskId
    	});
	    solutionformforview.reloadData();
	    
	    var win = Ext.create('FHD.ux.Window',{
				title:'应对措施详细信息',
				collapsible:false,
				maximizable:true//（是否增加最大化，默认没有）
	    }).show();
		win.add(solutionformforview);
	}
});