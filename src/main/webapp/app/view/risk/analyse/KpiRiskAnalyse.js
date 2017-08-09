
/**
 * 
 * 风险分析
 */
Ext.define('FHD.view.risk.analyse.KpiRiskAnalyse', {
    extend: 'Ext.form.Panel',
    alias: 'widget.kpiriskanalyse',
    requires: [
               		'FHD.view.risk.analyse.RiskTolerance'
               ],
    loadData : function(a){
    	var me = a||this;
		me.load({
    	        url: __ctxPath + '/app/view/risk/analyse/form.json',
//    	        params:{id:id||me.businessId},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        	var formValue = form.getValues();
    	        }
    	    });
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.kpiArea = Ext.widget('textareafield', {//工作目标
            xtype: 'textareafield',
            rows:4,
            fieldLabel: '物料需求计划偏差率 =   ',
            labelWidth: 135,
            margin: '7 10 0 100',
            name: 'workTage',
            columnWidth: .5
        });
        me.historyTrend = Ext.widget('button',{text:'历时趋势图',margin: '7 10 0 0'});
        var fieldSet = {
            xtype:'fieldset',
            title: '目标量化模型',
            collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            layout: {
     	        type: 'column'
     	    },
     	    items : [me.kpiArea,me.historyTrend]
        };
        var planName = Ext.widget('textfield', {//计划名称
            fieldLabel: '物料需求计划偏差率(%)',
            allowBlank:true,
//            margin: '0 0 0 -10',
            name: 'planName',
            labelWidth: 145,
            width: 510
        });
        var btn1 = Ext.widget('button',{text:'风险分析',margin: '0 10 0 10'});
        var btn2 = Ext.widget('button',{text:'提交',margin: '0 10 0 5',handler:me.onSubmit});
        var container = Ext.widget('container',{
        	layout:{
        		type:'hbox'
        	},
        	margin: '7 10 0 100',
        	items:[planName,btn1,btn2]
        });
        var fieldSet2 = {
                xtype:'fieldset',
                title: '风险量化分析',
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 5 5',
                defaults: {
                        columnWidth : 1,
                        margin: '7 30 7 100',
                        labelWidth: 135
                    },
                layout: {
         	        type: 'column'
         	    },
         	    items : [ 
							container,
							{xtype:'displayfield', fieldLabel : '保证生产的程度', name : 'cd'},
							{xtype:'displayfield', fieldLabel : '段供发生频率', name : 'pl'},
							{xtype:'displayfield', fieldLabel : '物料偏差金额', name : 'je'},
							{xtype:'displayfield', fieldLabel : '物料需求偏差率', name : 'cl'}
         	             ]
        };
        var cols = [
       			 {
     				header : "计算需求数量",
     				dataIndex : 'jsxq',
     				sortable : true,
     				flex : 1,
     				editor:true
     			},
     			{
     				header:'保证生产的程度',
     				dataIndex:'bzsc',
     				flex : 1,
     				editor:true
     			},
     			{
     				header:'断供发生频率（月/次',
     				dataIndex:'dgfs',
     				flex : 1,
     				editor:true
     			},
     			{
     				header:'偏差金额（万元）',
     				dataIndex:'pcje',
     				flex : 1,
     				editor:true
     			},
     			{
     				header:'偏差率',
     				dataIndex:'pcl',
     				flex : 1,
     				editor:true
     			}
                    ];
        var grid = Ext.create('FHD.ux.EditorGridPanel',{
        	url: __ctxPath + '/app/view/risk/analyse/grid.json',
        	layout: 'fit',
        	flex: 1, 
        	border:true,
        	cols:cols,
        	searchable:false,
        	pagable:false,
        	checked:false
        });
        var fieldSet3 = {
                xtype:'fieldset',
                title: '风险偏好控制标准',
                collapsible: true,
                flex:1,
                defaultType: 'textfield',
                margin: '5 5 0 5',
                layout: {
         	        type: 'fit'
         	    },
         	    items : [grid]
            };
        
        Ext.apply(me, {
        	border:false,
        	flex:1,
            items : [fieldSet,fieldSet2,fieldSet3]
        });

       me.callParent(arguments);
       me.loadData();
       me.on('resize',function(p){
    	   grid.setHeight(152);
      	 });
    },
    caculate:function(me){
    	me.loadData(me);
    },
    
    onSubmit:function(){
  		var me = this;
  		var riskTolerancePanel = Ext.widget('risktolerance');
    	me.win=Ext.create('FHD.ux.Window',{
				title : '风险分类统计',
				flex:1,
				autoHeight:true,
				autoScroll:true,
				collapsible : true,
				modal : true,
				maximizable : true,
				items:[riskTolerancePanel],
				bodyPadding: "0"
    		}).show();
    	riskTolerancePanel.renderChart();
    }
});