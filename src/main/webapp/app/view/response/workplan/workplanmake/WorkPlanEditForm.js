Ext.define('FHD.view.response.workplan.workplanmake.WorkPlanEditForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.workPlanEditForm',
    requires: [
		'FHD.view.risk.assess.quaAssess.commAssess.RiskAssessOpe'
	],
    loadData : function(id){
    	var me = this;
		me.load({
    	        url:'access/formulateplan/findriskassessplanById.f',
    	        params:{id:id||me.businessId},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        	var formValue = form.getValues();
    	        	me.workType.setValue('assess_work_type_project');
    	        	//me.mb.setValue(formValue.templateId);
    	        }
    	    });
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var businessId = me.businessId;
        var queryRangeTypeUrl = '';//评估范围模板下拉菜单Url
        var queryModelUrl = 'access/formulateplan/findtemplates.f';//评估模板
        var queryWorkTypeUrl = 'access/formulateplan/findworktypestore.f?planType='+'responsePlan';
        
        me.planName = Ext.widget('textfield', {//计划名称
            fieldLabel: '计划名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 0 30',
            name: 'planName',
            columnWidth: .5
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
        var collectRate = Ext.create('FHD.ux.collection.CollectionSelector', {//采集频率
    	    name:'collectRate',
    	    //id: 'collectRateId_plan',
    	    labelText : '采集频率',
    	    single:false,
    	    value:'',
    	    margin: '7 10 0 30', 
    	    columnWidth: .5
    	});
        var workTage = Ext.widget('textareafield', {//工作目标
            xtype: 'textareafield',
            rows:3,
            fieldLabel: '工作目标',
            margin: '7 10 0 30',
            name: 'workTage',
            columnWidth: .7
        });
        var rangRequire = Ext.widget('textareafield', {//范围要求
            xtype: 'textareafield',
            rows: 3,
            fieldLabel: '范围要求',
            margin: '7 10 0 30',
            name: 'rangeReq',
            columnWidth: .7
        });
        //开始时间
		me.assessPlanTimeStart = {
				xtype: 'datefield',
			    name: 'beginDataStr',
			    columnWidth:.5,
			    format: "Y-m-d"
			};
		//结束时间
		me.assessPlanTimeEnd = {
				xtype: 'datefield',
			    name: 'endDataStr',
			    columnWidth:.499,
			    format: "Y-m-d"
			};
		var labelPlanDisplay={
			    xtype:'displayfield',
			    width: 100,
			    value:'起止日期:',
			    style: {
		            marginRight:'4px'
        			}
			};
		var labelDisplay1={
				    xtype:'displayfield',
				    value:'&nbsp;至&nbsp;',
				    margin: '0 12 0 12'
				};
		me.assessPlanTime=Ext.create('Ext.container.Container',{//起止时间
	     	    layout:{
	     	    	type:'column'  
	     	    },
	     	    margin: '7 10 0 30',
	     	    columnWidth : .5,
	     	    items:[labelPlanDisplay, me.assessPlanTimeStart, labelDisplay1, me.assessPlanTimeEnd]
			});
        
        var states = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: queryWorkTypeUrl,
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
        });
        //评估模板
   	 	/*me.mbs = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: queryModelUrl,
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
        });*/
        me.workType = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '工作类型'+'<font color=red>*</font>',
            name : 'workType',
            editable: false,//禁止用户输入
            allowBlank:false,//不允许为空
            store: states,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'type',
            margin: '7 10 0 30',
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
       /* //评估模板
   	 	me.mb = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '评估模板'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            name : 'templateId',
            editable: false,//禁止用户输入
            store: me.mbs,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'type',
            //margin: '7 10 0 30',
            //columnWidth: .5
            flex : 10
        });
        //模板按钮
        me.templatebutton = Ext.create('Ext.button.Button',{
            iconCls:'icon-magnifier',
            height: 22,
            width: 22,
            //flex : 0.38,
            handler:function(){
            	if(me.mb.getValue() != null && me.mb.getValue() != ''){
            		me.riskAssessOpe = Ext.widget('riskAssessOpe');
					me.riskAssessOpe.loadInit(me.mb.getValue());
            		var templatewindow = Ext.create('FHD.ux.Window',{
						title:'模板维度打分查看',
						maximizable: true,
						modal:true,
						width:800,
						height: 400,
						collapsible: true,
						autoScroll : true,
						items : me.riskAssessOpe,
						buttonAlign: 'center',
						fbar: [
		   					{ xtype: 'button', text: '关闭', handler:function(){templatewindow.close();}}
						  ]
					}).show();
            	}
		    }
    	});
    	me.templatacontainer = Ext.create('Ext.form.FieldContainer',{
    		margin : '7 10 0 30', 
    		columnWidth : .5,
    		layout : 'hbox',
    		items : [
    			me.mb,me.templatebutton
    		]
    	});*/
   	 	//负责人
        me.reponser = Ext.create('FHD.ux.org.CommonSelector',{
        	fieldLabel: '负责人',
        	name : 'responsName',
            type:'emp',
            multiSelect:false,
            margin: '7 10 0 30',
            columnWidth: .5
        });
        //联系人
        me.contactor = Ext.create('FHD.ux.org.CommonSelector',{
        	fieldLabel: '联系人',
        	name : 'contactName',
            type:'emp',
            multiSelect:false,
            margin: '7 10 0 30',
            columnWidth: .5
        });
        //流程选择
        var riskWorkSelect = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '流程选择',
            name : 'riskWorkSelectName',
            store: '',
            queryMode: 'local',
            displayField: 'name',
            valueField: 'type',
            margin: '7 10 0 30',
            columnWidth: .5
        });
        var fieldSet = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            layout: {
     	        type: 'column'
     	    },
     	    items : [me.planName,planCode, autoButton, me.reponser, me.contactor, me.workType, /*collectRate*/, /*riskWorkSelect,*/ me.assessPlanTime]
        };
        var fieldSet2 = {
                xtype:'fieldset',
                title: '范围要求',
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 0 5',
                layout: {
         	        type: 'column'
         	    },
         	    items : [ rangRequire]
            };
        var fieldSet3 = {
                xtype:'fieldset',
                title: '工作目标',
                collapsible: true,
                defaultType: 'textfield',
                margin: '5 5 0 5',
                layout: {
         	        type: 'column'
         	    },
         	    items : [workTage]
            };
        
        Ext.apply(me, {
        	border:false,
            items : [fieldSet,fieldSet3,fieldSet2]
        });

       me.callParent(arguments);
       if(businessId){
       		 me.loadData();
       } 
    }

});