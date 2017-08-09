Ext.define('FHD.view.SASACdemo.homepage.MajorRiskAnalysisForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorRiskAnalysisForm',
    requires: [
	],
	
	//返回首页
	backhomePage: function(){
		var me = this;
		var card = me.up('showRiskStatusTabPanel').up('sasaccardpanel');
		card.showHomePageMainPanel();
	},
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
   	 	me.nameStore = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
            data : [
                    {"type":"1", "name":"投资风险"},
                    {"type":"2", "name":"健康安全环保风险"},
                    {"type":"3", "name":"现金流风险"},
                    {"type":"4", "name":"人力资源风险"},
                    {"type":"5", "name":"政策风险"},
                    {"type":"6", "name":"竞争风险"},
                    {"type":"7", "name":"战略管理风险"},
                    {"type":"8", "name":"价格风险"},
                    {"type":"9", "name":"国际化经营风险"},
                    {"type":"10", "name":"宏观经济风险"}
                ]
        });
        //风险名称
   	 	me.riskName = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '风险名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            name : '',
            editable: false,//禁止用户输入
            store: me.nameStore,
            queryMode: 'local',
            displayField: 'name',
            columnWidth: .5,
            margin: '7 10 3 30',
            valueField: 'type'
        });
	   	me.riskType = Ext.widget('textfield', {//计划名称
	        fieldLabel: '所属类型',
	        margin: '7 10 3 30',
	        name: '',
	        columnWidth: .5
	    });
	   	me.desc=Ext.widget('textarea', {
            fieldLabel: "风险描述",
            name:"desc",
            margin: '7 10 3 30',
            labelSepartor: "：",
            columnWidth: 1
    	});
	   	
	   	me.incomeDesc=Ext.widget('textarea', {
            fieldLabel: "对收入的影响分析",
            name:"",
            margin: '7 10 3 30',
            labelSepartor: "：",
            columnWidth: 1
    	});
	   	me.profitDesc=Ext.widget('textarea', {
            fieldLabel: "对利润的影响分析",
            margin: '7 10 3 30',
            name:"",
            labelSepartor: "：",
            columnWidth: 1
    	});
	   	
	   	me.solution=Ext.widget('textarea', {
            fieldLabel: "应对计划",
            margin: '7 10 3 30',
            name:"",
            labelSepartor: "：",
            columnWidth: 1
    	});
	   	
	   	me.supportWork=Ext.widget('textarea', {
            fieldLabel: "需国资委支持的工作",
            margin: '7 10 3 30',
            name:"",
            labelSepartor: "：",
            columnWidth: 1
    	});
	   
        me.fieldSet1 = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            layout: {
     	        type: 'column'
     	    },
     	    items : [me.riskName,me.riskType,me.desc]
        };
        me.fieldSet2 = {
                xtype:'fieldset',
                title: '影响分析',
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 0 5',
                layout: {
                	columnWidth : 1 ,
         	        type: 'column'
         	    },
         	    items : [me.incomeDesc,me.profitDesc]
            };
        me.fieldSet3 = {
                xtype:'fieldset',
                title: '应对与支持',
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 0 5',
                layout: {
                	columnWidth : 1 ,
         	        type: 'column'
         	    },
         	    items : [me.solution,me.supportWork]
            };
        Ext.apply(me, {
        	border:false,
        	autoScroll: true,
        	/*bbar: {items: [ '->',{text: '保存', //保存按钮
				            iconCls: 'icon-control-stop-blue',
				            handler: function () {
				              	FHD.notification('操作成功！','提示');
				            }
				        }
		  			]
	   		},*/
            items : [me.fieldSet1,me.fieldSet2,me.fieldSet3]
        });

       me.callParent(arguments);
    }

});