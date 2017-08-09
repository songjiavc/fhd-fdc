Ext.define('FHD.view.SASACdemo.companyReport.CompanyReportRiskAnalyForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.companyReportRiskAnalyForm',
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
        
        //风险名称
        me.riskName = Ext.widget('textfield', {//计划名称
	        fieldLabel: '风险名称',
	        editable: false,
	        margin: '7 10 3 30',
	        name: '',
	        columnWidth: .5
	    });
	   	me.riskLevel = Ext.widget('textfield', {//计划名称
	        fieldLabel: '风险水平',
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
	   	
	   	me.influenceRange = Ext.widget('textfield', {//计划名称
	        fieldLabel: '影响范围',
	        margin: '7 10 3 30',
	        name: '',
	        columnWidth: .5
	    });
	   	
	   	me.influenceDesc = Ext.widget('textfield', {//计划名称
	        fieldLabel: '影响描述',
	        margin: '7 10 3 30',
	        name: '',
	        columnWidth: .5
	    });
	   	
	   	me.incomeDesc=Ext.widget('textarea', {
            fieldLabel: "可能导致收入波动幅度",
            name:"",
            margin: '7 10 3 30',
            labelSepartor: "：",
            columnWidth: 1
    	});
	   	me.profitDesc=Ext.widget('textarea', {
            fieldLabel: "可能导致利润波动幅度",
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
     	    items : [me.riskName,me.riskLevel,me.influenceRange,me.influenceDesc,me.desc]
        };
        me.fieldSet2 = {
                xtype:'fieldset',
                title: '波动分析',
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
				            iconCls: 'icon-save',
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