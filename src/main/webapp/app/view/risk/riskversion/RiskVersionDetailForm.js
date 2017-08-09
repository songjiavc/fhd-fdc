/**
 * 风险事件基本信息页面
 *
 * @author
 */
Ext.define('FHD.view.risk.riskversion.RiskVersionDetailForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskversiondetailform',
    requires:[],

    //返回
    backGrid: function(){
        var me = this;
        me.up('riskversioncardmain').showDetailtGrid();
    },
    //清空表单缓存
    resetData: function () {	//id为树节点id
        var me = this;
        //清空组件值
        me.getForm().reset();
      
    },
    //加载表单数据
    reloadData: function (historyId) {	//id是风险事件id
        var me = this;
        FHD.ajax({
            async: false,
            params: {
            	historyId: historyId
            },
            url: __ctxPath + '/risk/riskhistory/findriskdetailbyverid.f',
            callback: function (json) {
                //赋值
            	debugger;
            	me.form.setValues({
            		parentName: json.data.parentName,
            		riskCode: json.data.riskCode,
            		riskName: json.data.riskName,
            		riskDesc: json.data.riskDesc,
            		mainDept: json.data.mainDeptName,
            		relaDept: json.data.relaDeptName,
            		kpiName: json.data.kpiName,
            		processName: json.data.processName
                });
            }
        });
    },
   
    // 初始化方法
    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            autoScroll: true,
            border : false,
            items:[{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[{xtype:'displayfield', fieldLabel : '上级风险', name:'parentName',margin : '30 30 3 30',columnWidth : .5}, 
                	{xtype:'displayfield', fieldLabel : '风险编号', name:'riskCode',margin : '30 30 3 30',columnWidth : .5}]
            },{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[{xtype:'displayfield', fieldLabel : '风险名称', name:'riskName',margin : '30 30 3 30',columnWidth : .5},
                	{xtype:'displayfield', fieldLabel : '风险描述', name:'riskDesc',margin : '30 30 3 30',columnWidth : .5}]
            },{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[{xtype:'displayfield', fieldLabel : '责任部门/人', name:'mainDept',margin : '30 30 3 30',columnWidth : .5},
                	{xtype:'displayfield', fieldLabel : '相关部门/人', name:'relaDept',margin : '30 30 3 30',columnWidth : .5}]
            },{
                layout: 'column',
                frame: true,
                baseCls : 'my-panel-no-border',  //去掉边框
                items:[{xtype:'displayfield', fieldLabel : '影响指标', name:'kpiName',margin : '30 30 3 30',columnWidth : .5},
                	{xtype:'displayfield', fieldLabel : '影响流程', name:'processName',margin : '30 30 3 30',columnWidth : .5}]
            }

            ],
            dockedItems: [{
				xtype: 'toolbar',
	            dock: 'bottom',
	            ui: 'footer',
	            items: ['->', {
	                text: '返回',
	                iconCls: 'icon-control-repeat',
	                height : 40,
	                handler: function () {
	                    me.backGrid();
	                }
	            }]
            }]
            
        });

        me.callParent(arguments);
    }

});