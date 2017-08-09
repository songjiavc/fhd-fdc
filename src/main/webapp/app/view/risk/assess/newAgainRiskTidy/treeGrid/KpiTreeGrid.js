Ext.define('FHD.view.risk.assess.newAgainRiskTidy.treeGrid.KpiTreeGrid', {
	extend : 'FHD.ux.TreeGridPanel',
	alias : 'widget.kpiTreeGrid',
	requires : [],

	showRisk:function(riskId){
    	var me = this;
    	//单击风险名称，显示风险基本信息组件
		Ext.Ajax.request({
			url: __ctxPath + '/assess/quaAssess/findDimCols.f?assessPlanId=' + me.riskTidyMan.businessId,
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        array = new Array();
		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
		        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.5});
		        });
		        
		        me.riskTidyAssessGrid = Ext.widget('riskTidyAssessGrid',{array : array});
		        me.riskTidyAssessGrid.store.proxy.url = __ctxPath + '/assess/riskTidy/findRiskByAssessPlanIdAndRiskId.f';
		    	me.riskTidyAssessGrid.store.proxy.extraParams.assessPlanId = me.riskTidyMan.businessId;
			    me.riskTidyAssessGrid.store.proxy.extraParams.riskId = riskId;
			    me.riskTidyAssessGrid.store.load();	
		        
		        me.assessGridSet = {
        				xtype : 'fieldset',
        				collapsible : true,
        				title : '评估详细',
        				margin : '10 10 10 10',
        				
        				items : [me.riskTidyAssessGrid]
        		};
		        
		        me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
		        	autoScrollas : true,
					type:'re',	//如果是re,上级风险只能选择叶子节点
		    		border:false,
	    			riskId : riskId
	    		});
	    		me.detailAllForm.add(me.assessGridSet);
	    		
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
					items : [me.detailAllForm]//me.quaAssessEdit
				});
				me.detailAllForm.on('resize',function(p){
		        	me.detailAllForm.setHeight(me.formwindow.getHeight()-20);
			   	});
			   	me.formwindow.show();
		    }
		});
    },
    
    getColsShow: function(){
    	var me = this;
    	var cols = [
		        	{
		        		hidden:true,
		        		text: 'strategyId',
		        	    dataIndex: 'strategyId'
		        	},{
		        		hidden:true,
		        		text: 'kpiId',
		        	    dataIndex: 'kpiId'
		        	},
		        	{
		        		hidden:true,
		        		text: 'riskId',
		        	    dataIndex: 'riskId'
		        	},
		        		{
		        		hidden:true,
		        	    dataIndex: 'linked'
		        	},
		        	
		        	{
		        	    xtype: 'treecolumn', 
		        	    text: "名称",
		        	    flex: 4,
		        	    dataIndex: 'name',
		        	    sortable: true,
		        	    renderer:function(value,metaData,record,colIndex,store,view) {
			            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
			            	if(record.get('riskId')){
			            		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
	     						+ "').showRisk('" + record.get('riskId') + "')\">"+value+"</a>";
			            	}else{
			            		return value;
			            	}
		     				
		     			}
		        	}
		        ];
		return cols;
    },
	
  //保存修改的分值
    onSave:function(data){
    	var me = this;
    	var rows = me.store.getModifiedRecords();
    	var obj = {
    		score:data.value,
    		scoreDimId:data.field
    	};
		var jsonArray=[];
		Ext.each(rows,function(item){
			obj.riskId = item.data.riskId
			jsonArray.push(obj);
		});
		console.log(jsonArray);
	   	 FHD.ajax({
			async:false,
			params: {
		       	assessPlanId : me.riskTidyMan.businessId,
		       	params : Ext.encode(jsonArray)
	        },
	        url : __ctxPath + '/assess/riskTidy/riskTidySaveAssess.f',
	        callback: function (ret) {
	       		me.store.load();
	       }
	 	});
    },
    
    //导出
    exportGrid: function(businessId){
    	var me = this;
    	var items = me.columns;
    	me.headerDatas = [];
		Ext.each(items,function(item){
			if(!item.hidden&&""!=item.dataIndex){
			var value = {};
			value['dataIndex'] = item.dataIndex;
        	value['text'] = item.text;
        	me.headerDatas.push(value);
			}
		});
    	sheetName = 'exportexcel';
    	window.location.href = __ctxPath + "/assess/riskTidy/exportkpicountgrid.f?businessId="+businessId+"&exportFileName="
    							+""+"&sheetName="+sheetName+"&headerData="+Ext.encode(me.headerDatas);
    },
    
	// 初始化方法
	initComponent : function() {
		var me = this;
		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
	        clicksToEdit: me.clicksToEdit
	    });
		
		var cols = me.getColsShow()
	    Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/findDimCols.f',
		    params: {
		    	assessPlanId : me.riskTidyMan.businessId
            },
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        me.array = new Array();
		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
		        	me.array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:1,editor:{
							xtype:'numberfield',
							allowBlank:false,
							allowDecimals: true, // 允许小数点 
							nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
							//hideTrigger: true,  //隐藏上下递增箭头
							keyNavEnabled: true,  //键盘导航
							mouseWheelEnabled: true,  //鼠标滚轮
							step:1
			        	}});
		        });
		        
		        for(var i = 0 ; i < me.array.length; i++){
		        	if(me.array[i].header.indexOf('汇总') != -1){
		        		cols.push(me.array[i]);
		        	}
		        }
		        cols.push({dataIndex:'score', header:'风险值', sortable : true, flex: 1});
		    }
		});
	
		Ext.apply(me, {
			url : __ctxPath + "/assess/riskTidy/kpicountgrid.f?assessPlanId="+me.riskTidyMan.businessId,// 查询列表url
			cols: cols,
			useArrows : true,
			rootVisible : false,
			multiSelect : true,
			border : false,
			rowLines : true,
			singleExpand : false,
			checked : false,
			autoScroll : true,
			plugins: [cellEditing],
			tbarItems:[
				{
	   				text: '展开',
	   				iconCls: 'icon-expand-all',
	   				handler:function(){
	   					me.expandAll();
	   				}
				},
               {
	   				text: '导出',
	   				iconCls: 'icon-ibm-action-export-to-excel',
	   				handler:function(){
	   					me.exportGrid(me.riskTidyMan.businessId);
	   				}
				}
			]
		});
	
		me.callParent(arguments);
		//me.expandAll();
		me.on('edit', function (event,value) {
	   	    me.onSave(value);
	    });
	}
});