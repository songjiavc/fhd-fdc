/**
 * 风险图谱的风险列表
 * 
 * @author ZJ
 */
Ext.define('FHD.view.risk.cmp.chart.RiskHeatMapGridPanel', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.riskheatmapgridpanel',

    /**
	 * public
	 * 接口属性
	 */
    type:'',//risk,org,kpi,process
    value : '',
	colsArray : null,
	isgoback : false,
	queryUrl: __ctxPath + '/cmp/risk/getheatriskgrid.f',
	navHeight : 22,
   
    // 初始化方法
    initComponent: function() {
		var me = this;
		var cols = [{
			dataIndex : 'id',
			hidden : true
		},{
			dataIndex : 'belongRisk',
			hidden : true
		},{
			header : '所属风险',
			dataIndex : 'parentName',
			sortable : false,
			width : 150,
			renderer: function (value, metaData, record, colIndex, store, view) {
				var detail = record.data['belongRisk'];
				metaData.tdAttr = 'data-qtip="'+detail+'"';                    
    			return value;
            }
		},{
			header : '名称',
			dataIndex : 'name',
			sortable : false,
			flex : 2,
			align : 'left',
			renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="'+value+'"';
                var id = record.data['id'];
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showRiskEventDetailContainer('" + id + "')\" >" + value +"</a>";
            }
		},{
			header : '责任部门',
			dataIndex : 'respDeptName',
			sortable : false,
			width : 100,
			renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="'+value+'"';                    
    			return value;
            }
		},{
			header : '相关部门',
			dataIndex : 'relaDeptName',
			sortable : false,
			width : 100,
			renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="'+value+'"';                    
    			return value;
            }
		}];
		
		if(me.colsArray){
			for(var i = 0 ; i < me.colsArray.length; i++){
	        	cols.push(me.colsArray[i]);
	        }
		}
		
		Ext.apply(me, {
			cols:cols,
		    border: false,
		    checked: false,
		    pagable : false,
		    searchable : false,
		    columnLines: true,
		    isNotAutoload : true
		});

		me.callParent(arguments);
		
		me.on('afterlayout',function(){ 
        	Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [3]);
        });
		
    },
    
    reloadData:function(id){
    	var me = this;
    	if(id != null && id != ''){
	    	me.value = id;
    	}
    	me.store.proxy.url = me.queryUrl;
    	me.store.proxy.extraParams = {
    		id : me.value,
    		type : me.type,
    		xvalue : me.xvalue,
    		yvalue : me.yvalue,
    		showgroup : me.showgroup,
    		assessPlanId : me.assessPlanId,
    		xscore : me.xscore,
    		yscore : me.yscore
    	};
    	me.store.load();
    },
    
    initParam : function (paramObj){
		var me = this;
		me.value = paramObj.id;
		me.type = paramObj.type;
		me.xvalue = paramObj.xvalue;
		me.yvalue = paramObj.yvalue;
		me.showgroup = paramObj.showgroup;
		me.assessPlanId = paramObj.assessPlanId;
		me.xscore = paramObj.xscore;
		me.yscore = paramObj.yscore;
	},
	
	showRiskEventDetailContainer : function(id) {
		var me = this;
		// 风险事件基本信息
		riskEventDetailForm = Ext.create(
				'FHD.view.risk.cmp.form.RiskFullFormDetail', {
					title : '基本信息',
					showbar : true,
					border : false,
					goback : function() {
						window.close();
					}
				});

		// 风险事件历史记录
		riskEventHistoryGrid = Ext.create(
				'FHD.view.risk.cmp.risk.RiskHistoryGrid', {
					title : '历史记录',
					showbar : false,
					type : 'riskevent',
					border : false
				});

		riskEventTabPanel = Ext.create(
				"FHD.ux.layout.treeTabFace.TreeTabTab", {
					items : [riskEventDetailForm,
							riskEventHistoryGrid]
				});
		riskEventDetailContainer = Ext.create(
				'FHD.ux.layout.treeTabFace.TreeTabContainer', {
					border : false,
					navHeight : '0',
					tabpanel : riskEventTabPanel,
					flex : 1
				});
		var window = Ext.create('FHD.ux.Window',{
			title:'风险事件详情',
			maximizable: true,
			modal:true,
			width:800,
			height: 500,
			collapsible:true,
			autoScroll : true,
			items : riskEventDetailContainer
		}).show();
		riskEventDetailForm.reloadData(id);
		riskEventHistoryGrid.reloadData(id);
	}
    
});