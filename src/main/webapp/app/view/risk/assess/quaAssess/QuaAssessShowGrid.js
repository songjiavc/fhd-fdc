/**
 * 
 * 定性评估表格
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessShowGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.quaAssessShowGrid',
    
    requires : [ 'FHD.view.risk.assess.quaAssess.QuaAssessEdit'],
    
    edit : function(riskId, templateId){
    	Ext.getCmp('quaAssessCardId').quaAssessOpe.quaAssessEdit.load(riskId, templateId);
    },
    
    edits : function(riskId, rangObjectDeptEmpId,objectId){
    	var me = this;
    	me.quaAssessEdit = Ext.widget('quaAssessEdit',{isEditIdea : false,objectId:objectId});
    	
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
		me.quaAssessEdit.load(riskId, rangObjectDeptEmpId);
		me.quaAssessEdit.on('resize',function(p){
    		me.quaAssessEdit.detailAllForm.setHeight(me.formwindow.getHeight()-62);
    	});
    	me.formwindow.show();
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
				if('riskIcon'==item.dataIndex){
					value['text'] = '风险等级';
				}else{
					value['text'] = item.text;
				}
            	me.headerDatas.push(value);
				}
			});
		
    	sheetName = 'exportexcel';
    	//exportFileName = '评估结果预览数据';
    	if(me.empId){//问卷监控导出相关人员的风险
    		window.location.href = __ctxPath + "/assess/quaassess/exportAssessShowGrid.f?businessId="+me.businessId+"&exportFileName="+""+
    							"&sheetName="+sheetName+"&headerData="+Ext.encode(me.headerDatas)+"&empId="+me.empId;
    	}else{
    		window.location.href = __ctxPath + "/assess/quaassess/exportAssessShowGrid.f?businessId="+me.businessId+"&exportFileName="+""+"&sheetName="+sheetName+"&headerData="+Ext.encode(me.headerDatas)+"&executionId="+Ext.getCmp('QuaAssessManId').executionId;
    	}
    },
    
    getColsShow : function(){
    	var me = this;
    	var array = null;
    	var cols = [
    				{
    					dataIndex:'riskId',
    					hidden:true
    				},
    				{
    					dataIndex:'objectId',
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
    				
//    				{
//    		            header: "评估人",
//    		            dataIndex: 'assessEmpId',
//    		            sortable: true,
//    		            //align: 'center',
//    		            flex:.2
//    		        },
    		        {
    		            header: "上级风险",
    		            dataIndex: 'parentRiskName',
    		            sortable: true,
    		            //align: 'center',
    		            flex:.4
    		        },{
    		            header: "风险名称",
    		            dataIndex: 'riskName',
    		            sortable: true,
    		            //align: 'center',
    		            flex:2,
    		            renderer:function(value,metaData,record,colIndex,store,view) {
    		            	metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+300+'"';
    		            	return "<a href=\"javascript:void(0);\" " +
    		            			"onclick=\"Ext.getCmp('" + me.id + "').edits('" + record.get('riskId') + "','" 
    		            			+ record.get('rangObjectDeptEmpId') +"','" 
    		            			+ record.get('objectId') +"')\">" + 
    		            			record.get('riskName') + "</a>";
    	     			}
    		        }
    	        ];
    	return cols;
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = me.getColsShow();
        
        for(var i = 0 ; i < me.array.length; i++){
        	cols.push(me.array[i]);
        }
        
        cols.push({dataIndex:'riskIcon', sortable : true, width:40,
        	cls: 'grid-icon-column-header grid-statushead-column-header',
        	menuDisabled:true,
        	renderer:function(value,metaData,record,colIndex,store,view) {
        		debugger;
            	return "<div style='width: 23px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" 
            	+ record.get('riskIcon') +"' name='evaluationStates'"+"/>";
 			}	
        });
        
    	Ext.apply(me,{
        	region:'center',
        	scroll : 'vertical',
        	margin : '0 0 0 0',
        	url : me.url,
        	cols:cols,
		    border: false,
		    checked: false,
		    pagable : false,
		    storeAutoLoad:false,
		    searchable : true,
		    columnLines: true,
		    isNotAutoload : false,
		    tbarItems:[{iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}]
        });
        
        me.callParent(arguments);
    	
    	me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [4]);
        });
    }

});