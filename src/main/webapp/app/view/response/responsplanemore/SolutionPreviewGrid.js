Ext.define('FHD.view.response.responsplanemore.SolutionPreviewGrid',{
	extend: 'FHD.ux.GridPanel',
    alias: 'widget.solutionPreviewGrid',
    requires: [
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
                return value;
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
		
        me.callParent(arguments);
        me.on('afterLayout',function(){ 
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3]);
        });
    },
    colInsert: function (index, item) {
        if (index < 0) return;
        if (index > this.cols.length) return;
        for (var i = this.cols.length - 1; i >= index; i--) {
            this.cols[i + 1] = this.cols[i];
        }
        this.cols[index] = item;
    },
	reloadData:function(){
		var me=this;
		me.store.proxy.url = __ctxPath + '/responseplan/findresponselistbybusinessid.f',//'/responseplan/workflow/findapproveGgridbybusinessid.f';
		me.store.proxy.extraParams = {
        	businessId : me.paramObj.businessId,
        	executionId : me.paramObj.executionId
        };
		me.store.load();
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