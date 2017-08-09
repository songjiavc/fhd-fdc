Ext.define('FHD.view.risk.assess.newAgainRiskTidy.grids.RiskTidyProcess', {
    extend: 'Ext.container.Container',
    alias : 'widget.risktidyprocess',
    requires: [
    ],
    showRisk:function(riskId){
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
		    		me.detailAllForm.setHeight(me.formwindow.getHeight());
			   	});
				me.formwindow.show();
		    }
		});
    },
    
    zq:function(processId,isLeaf){
    	var me = this;
    	if(processId != ''){
    		if(isLeaf=='true'){//如果是显示风险列表
		    	var riskHeader = {
			    		header:'风险名称',
			    		dataIndex:'riskName',
			    		sortable: true,
	        	        flex:2,
	        	       	renderer:function(value,metaData,record,colIndex,store,view) {
		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
	     				return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id
	     				+ "').showRisk('" + record.get('riskId') + "')\">"+value+"</a>";
	     				}
			    	};
		    	
    			me.remove(me.riskProcessGrid);
    			me.riskProcessGrid = me.createRiskGrid(riskHeader);
    			me.add(me.riskProcessGrid);
		    	me.riskProcessGrid.store.on('load',function(){
    				Ext.widget('gridCells').mergeCells(me.riskProcessGrid, [3]);
    		});
    		}else{
    		}
		    	me.riskProcessGrid.store.proxy.extraParams.processId = processId;
		    	me.riskProcessGrid.store.load();
    	}else{//返回
    		if(!isLeaf){
    			FHD.alert("无法进行钻取!");
    			return
    		}
    		//me.renderChart();
    		me.remove(me.riskProcessGrid);
		    me.riskProcessGrid = me.createRiskGrid();
    		me.riskProcessGrid.store.proxy.extraParams.processId = null;
    		me.riskProcessGrid.store.load();
    		me.add(me.riskProcessGrid);
    	}
    },
    initComponent: function() {
        var me = this;
        me.riskProcessGrid = me.createRiskGrid();
        Ext.apply(me, {
        	layout:{
                type: 'fit'
    		},
    		items:[me.riskProcessGrid]
        });
        me.callParent(arguments);
    },
    createRiskGrid:function(column){
    	var me = this;
    	me.id = 'risktidyorganization';
        me.cols = [
        			{
        				dataIndex:'processId',
        				hidden:true
        			},{
        				dataIndex:'isLeaf',
        				hidden:true
        			},{
        	            header: "流程名称",
        	            dataIndex: 'processName',
        	            sortable: true,
        	            //align: 'center',
        	            flex:.8
        	        },{
        	            header: "流程及风险数量",
        	            dataIndex: 'riskSum',
        	            sortable: true,
        	            xtype: 'gridcolumn',
        	            //align: 'center',
        	            flex:.2
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
		     				if(record.get('riskName') != undefined){
		     					return '';
		     				}else{
		     					return "<a href=\"javascript:void(0);\" " +
	     						"onclick=\"Ext.getCmp('" + me.id + "').zq('"+ record.get('processId')+ "','"+record.get('isLeaf')+ "')\">" 
	     						+ "钻取</a>" ;
		     				}
		     			}
     				},
     				{
        				dataIndex:'riskId',
        				hidden:true
        			}
                ];
        if(column){
        	me.cols[2]=column;
        	me.cols[3].hidden = true;
          }
        var riskProcessGrid = Ext.create('FHD.ux.GridPanel',{
        	extraParams : {assessPlanId : me.riskTidyMan.businessId},
        	url: __ctxPath + '/assess/riskTidy/processCount.f',
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
		    	me.zq('',1);
		    }}],
		    isNotAutoload : false
        });
        return riskProcessGrid;
    }
});