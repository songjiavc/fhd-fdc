

Ext.define('FHD.view.risk.assess.comprehensiveQuery.ComprehensiveGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.comprehensiveGrid',
    
    requires : [ 
    	],
    
    getColsShow : function(){
    	var me = this;
    	var array = null;
    	var cols = [
    				{
    					dataIndex:'riskId',
    					hidden:true
    				},
    				{
    					dataIndex:'assessPlanId',
    					hidden:true
    				},
    				{
    		            header: "评估计划",
    		            dataIndex: 'planName',
    		            hidden:true,
    		            sortable: true,
    		            flex:.4,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'" ';
    		            	document.getElementById('planGridNameId').innerHTML = '评估计划:' + value;
    		            	return value;
    	     			}
    		        },
    				{
    		            header: "上级风险",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            flex:.4,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'" ';
    		            	return value;
    	     			}
    		        },
    		        {
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            flex:1,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'" ';
	        				return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetailContainer('" + record.get('riskId') + "')\" >" + value + "</a>";
    	     			}
    		        },
    		        {
    		            header: "责任部门",
    		            dataIndex: 'orgM',
    		            sortable: true,
    		            hidden:true,
    		            flex:.4,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'" ';
    		            	return value;
    	     			}
    		        },
    		        {
    		            header: "相关部门",
    		            dataIndex: 'orgA',
    		            sortable: true,
    		            hidden:true,
    		            flex:.4,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'" ';
    		            	return value;
    	     			}
    		        }
    	        ];
    	
    	return cols;
    },
    
    //导出到excel
    exportChart:function(businessId, sheetName, exportFileName){
    	var me=this;
    	me.headerDatas = [];
    	
    	var items = me.columns;
			Ext.each(items,function(item){
				if(!item.hidden){
				var value = {};
				value['dataIndex'] = item.dataIndex;
            	value['text'] = item.text;
            	me.headerDatas.push(value);
				}
			});
		
		   if(me.gPanel == null){
	  		   me.gPanel = Ext.getCmp('comprehensiveMainId').comprehensiveQueryPanel;
	  	   }	
		   
		var planId = me.gPanel.planNameCmboBox.getValue();
        var assementStatus = me.gPanel.assementStatus.getValue();
        var riskStatus = me.gPanel.riskStatus.getValue();
        var orgM = me.gPanel.orgM.getValue();
        var orgA = me.gPanel.orgA.getValue();
        var assessEmp = me.gPanel.assessEmp.getValue();
        var riskName = me.gPanel.riskName.getValue();
        
        var start = 0;
        var limit = 20;
        window.location.href = __ctxPath + 
        "/assess/riskTidy/exportRiskAdjustHistoryInfos.f" +
        "?start=" + start + 
        "&limit=" + limit + 
        "&planIdQuery=" + planId + 
        "&assementStatusQuery=" + assementStatus + 
        "&riskStatusQuery=" + riskStatus + 
        "&orgMQuery=" + orgM + 
        "&orgAQuery=" + orgA + 
        "&assessEmpQuery=" + assessEmp + 
        "&riskNameQuery=" + riskName + 
        "&headerData=" + Ext.encode(me.headerDatas);
    },
    
    showRiskEventDetailContainer: function (id, parentId, name) {
        var me = this;
	
            //风险事件基本信息
    		me.riskEventDetailForm = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
                title: '基本信息',
                border: false,
                autoHeight: true,
                showbar: true,
                goback: function(){
                	window.close();
                }
            });
        	
			//图表分析
	        me.riskTrendLinePanel = Ext.create('FHD.view.risk.cmp.chart.RiskTrendLinePanel',{
	        	title : '图表分析',
	        	type : 'risk',
	        	border:false
	        });
	        
	        //风险图形分析的页签
	        me.riskGraphContainer =  Ext.create('Ext.container.Container',{
	        	layout:'fit',
	        	title:'图形分析',
	        	reloadData:function(riskId){//alert(riskId);
	        		if(!me.riskGraph){
	        			//2.表单
	        	        me.riskGraph = Ext.create('FHD.view.comm.graph.GraphRelaRiskPanel',{
	        			});
	            		this.add(me.riskGraph);
	            		this.doLayout();
	        		}
	        		
	    			//根据左侧选中节点，初始化数据
	        		me.riskGraph.initParam({
		                 riskId:riskId
		        	});
	        		me.riskGraph.reloadData();
	        	}	        	
	        });
	        
            //风险事件历史记录
            me.riskEventHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
                title: '历史记录',
                showbar : false,
                type: 'riskevent',
                border: false
            });

            me.riskEventTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.riskEventDetailForm,me.riskTrendLinePanel, me.riskGraphContainer, me.riskEventHistoryGrid]
            });
            me.riskEventDetailContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                navHeight: me.navHeight,
                tabpanel: me.riskEventTabPanel,
                flex: 1
            });
        
        //全部刷新
        me.riskEventDetailForm.reloadData(id);
        me.riskTrendLinePanel.reloadData(id);
        me.riskGraphContainer.reloadData(id);
        me.riskEventHistoryGrid.reloadData(id);
        
        var window = Ext.create('FHD.ux.Window', {
                title: '风险图谱详情',
                maximizable: true,
                modal: true,
                width: 800,
                height: 500,
                collapsible: true,
                autoScroll: true,
                items: me.riskEventDetailContainer
            }).show();
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = me.getColsShow();
       
        for(var i = 0 ; i < me.array.length; i++){
        	if(me.array[i].header.indexOf('汇总') != -1){
        		cols.push(me.array[i]);
        	}
        }
        
        cols.push({dataIndex:'riskStatus', header:'风险值', sortable : true, align: 'center', flex:.25});
        cols.push({dataIndex:'assessementStatus',  sortable : true, width:40,
        	cls: 'grid-icon-column-header grid-statushead-column-header', header:'',
        	menuDisabled:true,
        	renderer:function(value,metaData,record,colIndex,store,view) {
            	return "<div style='width: 23px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
            	+ record.get('assessementStatus') + "'/>";
 			}
        });
        
        cols.push({dataIndex:'empName', header:'评估人', sortable : true, flex:.25});
        for(var i = 0 ; i < me.array.length; i++){
        	if(me.array[i].header.indexOf('评估') != -1){
        		cols.push(me.array[i]);
        	}
        }
        cols.push({dataIndex:'riskScoreValue', header:'风险值', sortable : true, align: 'center', flex:.25});
        cols.push({dataIndex:'riskIcon', sortable : true, width:40,
        	cls: 'grid-icon-column-header grid-statushead-column-header', header:'',
        	menuDisabled:true,
        	renderer:function(value,metaData,record,colIndex,store,view) {
            	return "<div style='width: 23px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
            	+ record.get('riskIcon') + "'/>";
 			}
        });
        
    	Ext.apply(me,{
        	region:'center',
        	margin : '5 5 5 5',
        	url : me.url,
        	cols:cols,
		    border: true,
		    checked: false,
		    storeAutoLoad:false,
		    pagable : true,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : false,
		    tbarItems:[
		               '<span id="planGridNameId" style="font-size:12px;font-weight:bold;color: #15498b;margin-right:0"></span>',
		               {iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}},
		               {iconCls: 'icon-style-go', text:'高级查询', handler:function(){
		            	   
			            	   if(me.gPanel == null){
			            		   me.gPanel = Ext.getCmp('comprehensiveMainId').comprehensiveQueryPanel;
			            		   me.formwindow = new Ext.Window({
					   					layout:'fit',
					   					iconCls: 'icon-show',//标题前的图片
					   					modal:true,//是否模态窗口
					   					collapsible:true,
					   					width:800,
					   					height:350,
					   					maximizable:true,//（是否增加最大化，默认没有）
					   					constrain:true,
					   					title : '综合--高级查询',
					   					closeAction : 'hide'
				   				   });
			            		   me.formwindow.add(me.gPanel);
				            	   me.formwindow.show();
			            	   }else{
			            		   me.formwindow.show();
			            	   }
			            	   
		            		   
		               }}
		    ]
        });
        
        me.callParent(arguments);
    	
    	me.store.on('load',function(){
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [1,2,3,4,5]);
        });
    }
});