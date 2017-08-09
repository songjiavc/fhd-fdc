Ext.define('FHD.view.risk.assess.newAgainRiskTidy.grids.RiskTidyOrganization', {
    extend: 'Ext.container.Container',
    alias : 'widget.risktidyorganization',
    requires: [
    ],
    
    showRisk:function(riskId){
    	var me = this;
		Ext.Ajax.request({
		    url:  __ctxPath + '/assess/quaAssess/?assessPlanId=' + me.riskTidyMan.businessId,
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
    zq:function(orgId){
    	var me = this;	
    	if(orgId){//钻取某一组织下的风险
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
		    	me.remove(me.riskOrganizationGrid);
		    	me.riskOrganizationGrid = me.createRiskGrid(riskHeader);
		    	me.riskOrganizationGrid.store.proxy.extraParams.orgId = orgId;
		    	me.riskOrganizationGrid.store.load();
		    	me.add(me.riskOrganizationGrid);
		    	me.riskOrganizationGrid.store.on('load',function(){
        				Ext.widget('gridCells').mergeCells(me.riskOrganizationGrid, [3]);
        		});
    	}else{//默认加载所有组织分类下的风险
    		me.remove(me.riskOrganizationGrid);
		    me.riskOrganizationGrid = me.createRiskGrid();
    		me.riskOrganizationGrid.store.proxy.extraParams.orgId = null;
    		me.riskOrganizationGrid.store.load();
    		me.add(me.riskOrganizationGrid);
    	}
    },
    initComponent: function() {
        var me = this;
        me.id = 'risktidyorganizationId';
        me.riskOrganizationGrid = me.createRiskGrid();
        Ext.apply(me, {
        	layout:{
                type: 'fit'
    		},
    		items:[me.riskOrganizationGrid]
        });
        
        me.callParent(arguments);
    },
    createRiskGrid:function(column){
    	var me = this;
        var cols = [
        			{
        				dataIndex:'id',
        				hidden:true
        			},
        			{
        				dataIndex:'orgId',
        				hidden:true
        			},{
        	            header: "部门名称",
        	            dataIndex: 'deptName',
        	            sortable: true,
        	            //align: 'center',
        	            flex:.4
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
		     				
		     				if(record.get('riskSum') != undefined){
		     					return "<a href=\"javascript:void(0);\" " +
	     						"onclick=\"Ext.getCmp('" + me.id + "').zq('"+ record.get('orgId') + "')\">" + "钻取</a>" ;
		     				}else{
		     					return '';
		     				}
		     				
		     			}
     				},
     				{
        				dataIndex:'riskId',
        				hidden:true
        			}
                ];
              if(column){
              	cols[3]=column;
              }
        var riskOrganizationGrid = Ext.create('FHD.ux.GridPanel',{
        	extraParams : {assessPlanId : me.riskTidyMan.businessId},
        	url: __ctxPath + '/assess/riskTidy/organizationCount.f',
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
		    columnLines: column?true:false,
		    tbar:[{xtype:'button',text:'返回',iconCls:'icon-arrow-undo',handler:function(){
		    	me.zq();
		    }}],
		    isNotAutoload : false
        });
        return riskOrganizationGrid;
    }
});