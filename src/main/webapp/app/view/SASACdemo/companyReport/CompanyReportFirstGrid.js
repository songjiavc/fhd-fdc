Ext.define('FHD.view.SASACdemo.companyReport.CompanyReportFirstGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.companyReportFirstGrid',
 	requires: [
 	           
	],
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = [
	        {
	            header: "指标名称",
	            dataIndex: 'kpiName',
	            sortable: true,
	            flex: 1
	        },{
	        	header: "上期实际值",
				dataIndex:'realData',
				hidden:false,
				flex: 1,
				editor:{
						xtype:'numberfield',
						//allowBlank:false,
						allowDecimals: true, // 允许小数点 
						//nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
						//hideTrigger: true,  //隐藏上下递增箭头
						keyNavEnabled: true,  //键盘导航
						mouseWheelEnabled: true,  //鼠标滚轮
						step:1
		        	}
			},{
	        	header: "下月末预测值",
				dataIndex:'nextData',
				hidden:false,
				flex: 1,
				editor:{
						xtype:'numberfield',
						//allowBlank:false,
						allowDecimals: true, // 允许小数点 
						//nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
						//hideTrigger: true,  //隐藏上下递增箭头
						keyNavEnabled: true,  //键盘导航
						mouseWheelEnabled: true,  //鼠标滚轮
						step:1
		        	}
			},{
	        	header: "季度末预测值",
				dataIndex:'endData',
				hidden:false,
				flex: 1,
				editor:{
						xtype:'numberfield',
						//allowBlank:false,
						allowDecimals: true, // 允许小数点 
						//nanText: FHD.locale.get('fhd.risk.baseconfig.inputInteger'),  
						//hideTrigger: true,  //隐藏上下递增箭头
						keyNavEnabled: true,  //键盘导航
						mouseWheelEnabled: true,  //鼠标滚轮
						step:1
		        	}
			}
        ];
       
        Ext.apply(me,{
        	url : __ctxPath + '/app/view/SASACdemo/companyReport/firstgrig.json',//查询列表url
        	cols:cols,
        	type: 'editgrid',
		    border: true,
		    checked : false,
		    pagable : false,
		    tbarItems:[{
        			btype:'save',
        			handler:function(){
        				
        			}
    			}],
		    searchable:false
        });
       
        me.callParent(arguments);
    }

});