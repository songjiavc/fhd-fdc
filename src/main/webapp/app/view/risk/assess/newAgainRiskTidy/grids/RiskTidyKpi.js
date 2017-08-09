Ext.define('FHD.view.risk.assess.newAgainRiskTidy.grids.RiskTidyKpi', {
    extend: 'Ext.container.Container',
    alias : 'widget.risktidykpi',
    requires: [
    ],
    showRiskByRiskId:function(riskId){
    	var me = this;
		Ext.Ajax.request({
		    url: __ctxPath + '/assess/quaAssess/findDimCols.f?assessPlanId=' + me.riskTidyMan.businessId,
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
    zq:function(id,type){
    	var me = this;
    	if(id){
    			if(type=='T'){	//目标 加载流程列表或子目标列表
    				me.remove(me.riskKpiGrid);
    				me.riskKpiGrid = me.createRiskGrid(type);
    				me.add(me.riskKpiGrid);
    			}
    			if(type=='K'){	//流程 加载风险列表
    				me.remove(me.riskKpiGrid);
    				me.riskKpiGrid = me.createRiskGrid(type);
    				me.add(me.riskKpiGrid);
    			}
    			if(type=='R'){
    				FHD.alert("无法进行钻取!");
    				return;
    			}
		    	me.riskKpiGrid.store.proxy.extraParams.tarOrKpiId = id;
		    	me.riskKpiGrid.store.proxy.extraParams.kOrT = type;
		    	me.riskKpiGrid.store.load();
    	}else{
			me.remove(me.riskKpiGrid);
    		me.riskKpiGrid = me.createRiskGrid();
    		me.riskKpiGrid.store.proxy.extraParams.tarOrKpiId = null;
    		me.riskKpiGrid.store.proxy.extraParams.kOrT = null;
    		me.riskKpiGrid.store.load();
    		me.add(me.riskKpiGrid);
    	}
    },
    initComponent: function() {
        var me = this;
        me.riskKpiGrid = me.createRiskGrid();
        Ext.apply(me, {
        	layout:{
                type: 'fit'
    		},
    		items:[me.riskKpiGrid]
        });
        
        me.callParent(arguments);
    },
    createRiskGrid:function(type){
    	var me = this;
        var cols = [
        			{
        				dataIndex:'strategyId',
        				hidden:true
        			},{
        	            header: "目标名称",
        	            dataIndex: 'strategyName',
        	            sortable: true,
        	            hidden:false,
        	            //align: 'center',
        	            flex:.8
        	        },{
        	            dataIndex: 'kpiId',
        	            sortable: true,
        	            hidden:true
        	            
        	        },{
        	            dataIndex: 'riskId',
        	            sortable: true,
        	            hidden:true
        	            
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
        	            flex:.2
        	        },{
        	            header: "风险水平",
        	            dataIndex: 'riskLevel',
        	            sortable: true,
        	            //align: 'center',
        	            flex:.2,
        	            renderer:function(value,metaData,record,colIndex,store,view) {
				            	return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
				            	+ value + "'/>";
     					}
        	            
        	        },
        	        {header: '操作' ,dataIndex: 'name', sortable: true, flex : .1,hidden:false,//align: 'center',
		     			renderer:function(value,metaData,record,colIndex,store,view) {
		     				if(record.get('riskSum') != ''){
		     					var kpiId = record.get('kpiId');
			     				var strategyId = record.get('strategyId');
			     				var riskId = record.get('riskId');
			     				var id = kpiId || strategyId || riskId;
			     				var type ;
			     				if(kpiId){
			     					type = 'K';
			     				}
			     				if(strategyId){
			     					type = 'T';
			     				}
			     				if(riskId){
			     					type = 'R';
			     				}
			     				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id + "').zq('"+id+"','"+type+ "')\">" + "钻取</a>" ;
		     				}else{
		     					return '';
		     				}
		     			}
     				}
                ];
        if(type&&type=='T'){
        	
        	var column = {
	    		header:'指标名称',
	    		dataIndex:'kpiName',
	    		sortable: true,
		        flex:.8,
		       	renderer:function(value,metaData,record,colIndex,store,view) {
	        	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+100+'" ';
				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
				+ "').showRisk('" + record.get('riskId') + "')\">"+value+"</a>";
				}
        	};
        	
        	cols[1] = column;
        }
        if(type&&type=='K'){
        	
        	var riskCol = {
     				dataIndex:'riskId',
     				hidden:true
     			};
        	
    		var column = {
	    		header:'风险名称',
	    		dataIndex:'riskName',
	    		sortable: true,
    	        flex:2,
    	       	renderer:function(value,metaData,record,colIndex,store,view) {
            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
 				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
 				+ "').showRiskByRiskId('" + record.get('riskId') + "')\">"+value+"</a>";
    	       	}
 			};
 			
    		cols[0] = riskCol;
    		cols[1] = column;
    		cols[3].hidden = true;
    		cols[4].hidden = true;
    		//cols[6].hidden = true;
        }
        var riskKpiGrid = Ext.create('FHD.ux.GridPanel',{
        	extraParams : {assessPlanId : me.riskTidyMan.businessId},
        	url: __ctxPath + '/assess/riskTidy/targetCount.f',
        	cols:cols,
        	split: true,
           	collapsible : false,
        	region:'west',
        	flex:1,
        	autoScroll:true,
        	border: false,
		    checked: false,
		    pagable : false,
		    searchable : false,
		   	tbar:[{xtype:'button',text:'返回',iconCls:'icon-arrow-undo',handler:function(){
		    	me.zq();
		    }}],
		    columnLines: false,
		    isNotAutoload : false
        });
        return riskKpiGrid;
    }
});