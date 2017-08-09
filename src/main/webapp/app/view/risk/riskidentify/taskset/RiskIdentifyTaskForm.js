Ext.define('FHD.view.risk.riskidentify.taskset.RiskIdentifyTaskForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.riskIdentifyTaskForm',
    requires: [
    ],
    //保存综合表
    saveEmpRisks: function(){
    	var me = this;
    	var empIds = me.empInput.getValue();
    	FHD.ajax({//ajax调用
			url : __ctxPath + '/access/riskidentify/saveidentifyobjdeptempgridbysome.f',//保存对象，人员，部门综合表
			params : {
				businessId: me.businessId,
				empIds: empIds
			},
			callback : function(data){
		
			}
		});
    },
    formReLoad: function(pid){
    	var me = this;
    	me.form.load({
	        url:__ctxPath + '/access/formulateplan/querypreviewpanelbyplanId.f',
	        params:{pid:pid},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
    	});
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
		me.empInput = Ext.create('FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptInput',{
			columnWidth : .7
		});
        me.fieldSet1 = {
                xtype:'fieldset',
                title: '基础信息',
                collapsible: true,
                collapsed : true,//初始化收缩
                margin: '5 5 0 5',
                defaults: {
                        columnWidth : 1 / 2,
                        margin: '7 30 3 30',
                        labelWidth: 95
                    },
                layout: {
         	        type: 'column'
         	    },
         	    items : [ 	{xtype:'displayfield', fieldLabel : '计划名称', name:'planName'},
//         	              	{xtype:'displayfield', fieldLabel : '计划编号', name:'planCode'},
         	    			{xtype:'displayfield', fieldLabel : '起止日期', name : 'beginendDateStr'},
    						{xtype:'displayfield', fieldLabel : '联系人', name : 'contactName'},
    						{xtype:'displayfield', fieldLabel : '负责人', name : 'responsName'}
//    						{xtype:'displayfield', fieldLabel : '范围要求', name:'rangeReq'},
//    						{xtype:'displayfield', fieldLabel : '工作目标', name:'workTage'}
    						]
    						
            };
      
        me.newRiskIdentifyTaskGrid = Ext.create('FHD.view.risk.riskidentify.taskset.NewRiskIdentifyTaskGrid',{
        	businessId: me.businessId,
        	schm:me.schm,
        	executionId : me.executionId
        });
        me.newRiskIdentifyTaskGrid.reloadData();
        me.fieldSet4 = {
                xtype:'fieldset',
                title: '风险信息',
                collapsed : false,
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 0 5',
         	    items : [me.newRiskIdentifyTaskGrid]
            };
         
        me.fieldSet5 = {
            xtype:'fieldset',
            title: '辨识人',
            layout: {
     	        type: 'column'
     	    },
            collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 5 5',
     	    items : [me.empInput]
        };
        
        Ext.apply(me, {
        	autoScroll:true,
        	storeAutoLoad: false,
        	border:false,
            items : [me.fieldSet1, me.fieldSet4, me.fieldSet5]
        });
		
        me.callParent(arguments);
    	me.on('resize',function(p){
			me.setHeight(FHD.getCenterPanelHeight()-30);
		});
    }

});