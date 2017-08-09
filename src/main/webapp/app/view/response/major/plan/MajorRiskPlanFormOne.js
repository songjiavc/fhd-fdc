Ext.define('FHD.view.response.major.plan.MajorRiskPlanFormOne',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.majorriskplanformone',
 	border:false,
 	layout:{
		align: 'stretch',
		type: 'form'
	},
 	requires: [
	],
	//加载表单数据
	loadData: function(id){
		var me = this;
		me.load({
	        url:  __ctxPath + '/access/planconform/findplanformbyid.f',
	        params:{id:id},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        	var formValue = form.getValues();
	        	if(action.result.data.responsName){
	        		var value = Ext.JSON.decode(action.result.data.responsName);
	        		me.reponser.setValues(value);
	        	}
	        	if(action.result.data.contactName){
	        		var value = Ext.JSON.decode(action.result.data.contactName);
	        		me.contactor.setValues(value);
	        	}
	        }
	    });
	},
	
	
    initComponent: function () {
    	var me = this;
    	me.idField=Ext.widget('hiddenfield',{
            name:"id",
            value:''
        });
    	//分库标识
        me.schm=Ext.widget('hiddenfield',{
            name:"schm",
            value:me.schm
        });
        //计划类型
        me.planType=Ext.widget('hiddenfield',{
            name:"planType",
            value:me.planType
        });
        me.planName = Ext.widget('textfield', {//计划名称
            fieldLabel: '计划名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 0 30',
            name: 'planName',
            columnWidth: .5
        });
      //计划编号
        var planCode = Ext.widget('textfield', {
            xtype: 'textfield',
            rows: 1,
            fieldLabel: '计划编号',
            margin: '7 0 0 30',
            name: 'planCode',
            columnWidth: .4
        });
        //自动生成机构编号按钮
    	var autoButton = {
            xtype: 'button',
            margin: '7 10 0 10',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'),
            handler: function(){
       			FHD.ajax({
	            	url:__ctxPath+'/standard/standardTree/createStandardCode.f',
	            	params: {
	                	nodeId: me.nodeId
                 	},
	                callback: function (data) {
	                 	me.getForm().setValues({'planCode':data.code});//给code表单赋值
	                }
                });
            },
            columnWidth: .1
        };
    	me.reponser = Ext.create('Ext.ux.form.OrgEmpSelect',{
			type: 'emp',
			fieldLabel: '负责人', // 所属部门人员
			multiSelect:false,
			margin: '7 10 0 30',
			labelWidth: 100,
			columnWidth: .5,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'responsName',
			value:''
		});
    	me.contactor = Ext.create('Ext.ux.form.OrgEmpSelect',{
			type: 'emp',
			fieldLabel: '联系人', // 所属部门人员
			margin: '7 10 0 30',
			labelWidth: 100,
			columnWidth: .5,
			multiSelect:false,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'contactName',
			value:''
		});
    	//开始时间
        me.assessPlanTimeStart = Ext.widget('datefield',{
        	fieldLabel:"开始时间",
        	name: 'beginDataStr',
			format: "Y-m-d"
        });
		//结束时间
        me.assessPlanTimeEnd = Ext.widget('datefield',{
        	fieldLabel:"结束时间",
        	name: 'endDataStr',
		    format: "Y-m-d"
        });
        me.assessPlanTime=Ext.create('Ext.container.Container',{//起止时间
        	layout:'hbox',
        	defaults: {
		        labelWidth: 85,
		        margin: '0 5 5 0'
		    },
     	    items:[me.assessPlanTimeStart, me.assessPlanTimeEnd]
		});
        
    	var fieldsetOne = {
			xtype:"fieldset",
			title:"计划信息",
			collapsible: true,
			collapsed: false,
			layout: 'column',
			defaults: {
                columnWidth : 1 / 2,
                margin: '7 30 3 30',
                labelWidth: 85
            },
	        items :[me.planName,planCode, autoButton, me.reponser, me.contactor,me.assessPlanTime,me.planType,me.schm,me.idField]
		};
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [fieldsetOne],
			buttons: []
		});
    	me.callParent(arguments);
    	
    }

});