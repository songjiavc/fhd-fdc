Ext.define('FHD.view.risk.assess.newAgainRiskTidy.grids.RiskTidyRiskCategory', {
    extend: 'Ext.container.Container',
    alias : 'widget.risktidyriskcategory',
    requires: [
    ],
    
    zq:function(riskId,isRbs){
    	var me = this;
    	if(riskId){
    		me.isRbs = isRbs;
    		if(isRbs=='rbs'){
		    	me.riskCategoryGrid.store.proxy.extraParams.riskId = riskId;
		    	me.riskCategoryGrid.store.load();
    		}else{
    			FHD.alert("无法进行钻取!");
    		}
    	}else{
    		me.riskCategoryGrid.store.proxy.extraParams.riskId = null;
    		me.riskCategoryGrid.store.load();
    	}
    },
    //单击风险名称，显示风险基本信息组件
    showRisk: function(){
    	var me = this;
    	var riskId = me.riskCategoryGrid.getSelectionModel().getSelection()[0].data.riskId;
		Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/?assessPlanId=' + me.riskTidyMan.businessId,
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        array = new Array();
		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
		        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, flex:.5});
		        });       
		        
		        me.riskTidyAssessGrid = Ext.widget('riskTidyAssessGrid',
		        		{url:__ctxPath +'/assess/riskTidy/findRiskByAssessPlanIdAndRiskId.f', array : array, assessPlanId : me.riskTidyMan.businessId, riskId : riskId});	
		        
		        me.assessGridSet = {
        				xtype : 'fieldset',
        				collapsible : true,
        				title : '评估详细',
        				margin : '10 10 10 10',
        				
        				items : [me.riskTidyAssessGrid]
        		};
		        
		    	me.detailAllForm = Ext.create('FHD.view.risk.relate.RelateRiskDetail', {
					height : 360,
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
					items : [me.detailAllForm]
				});
				me.detailAllForm.on('resize',function(p){
		        	me.detailAllForm.setHeight(me.formwindow.getHeight()-20);
			   	});
				me.formwindow.show();
		    }
		});
    },
    
    initComponent: function() {
        var me = this;
        me.id = 'risktidyriskcategoryId',
        Ext.apply(me, {
        	layout:{
                type: 'fit'
    		},
    		items:[]
        });
        
        me.callParent(arguments);
        
        me.riskCategoryGrid = me.createRiskGrid();
        me.add(me.riskCategoryGrid);
    },
    
    createRiskGrid:function(){
    	var me = this;
        me.cols = [
			{
				dataIndex:'riskId',
				hidden:true,
				width:0
			},{
				dataIndex:'isRbs',
				hidden:true,
				width:0
			},{
	            header: "风险名称",
	            dataIndex: 'riskCategoryName',
	            sortable: true,
	            //align: 'center',
	            flex:1,
	            renderer:function(value,metaData,record,colIndex,store,view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
	            	return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').showRisk()\">"+value+"</a>";
     			}
	        },{
	            header: "风险数量",
	            dataIndex: 'riskSum',
	            sortable: true,
	            //align: 'center',
	            flex:.1
	        },
	        {
	            header: "风险分值",
	            dataIndex: 'riskStatus',
	            sortable: true,
	            //align: 'center',
	            flex:.1
	        },{
	            header: "风险水平",
	            dataIndex: 'riskLevel',
	            sortable: true,
	            //align: 'center',
	            flex:.1,
	            renderer:function(value,metaData,record,colIndex,store,view) {
		            	return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
		            	+ value + "'/>";
				}
	            
	        },
	        {header: '操作' ,dataIndex: 'name', sortable: true, flex : .1,hidden:false,//align: 'center',
     			renderer:function(value,metaData,record,colIndex,store,view) {
     				if(record.get('isRbs') == 're'){
     					return '';
     				}else{
     					return "<a href=\"javascript:void(0);\" " +
     							"onclick=\"Ext.getCmp('" + me.id + "').zq('"+ record.get('riskId')+ "','"+record.get('isRbs')+ "')\">" +
     									"" + "钻取</a>" ;
     				}
     			}
			}
        ];
        
        var riskCategoryGrid = Ext.create('FHD.ux.GridPanel',{
        	extraParams : {assessPlanId : me.riskTidyMan.businessId},
        	url: __ctxPath + '/assess/riskTidy/riskCategoryCount.f',
        	cols:me.cols,
        	split: true,
           	collapsible : false,
        	region:'west',
        	flex:1,
        	autoScroll:true,
        	border: false,
		    checked: false,
		    pagable : false,
		    searchable : false,
		    columnLines: false,
		    tbar:[{xtype:'button',text:'返回',iconCls:'icon-arrow-undo',handler:function(){
		    	me.zq();
		    }}],
		    isNotAutoload : false
        });
        
       return riskCategoryGrid;
    }
});