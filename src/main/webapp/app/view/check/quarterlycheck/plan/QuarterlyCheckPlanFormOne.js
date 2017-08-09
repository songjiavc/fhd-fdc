/**
 * 
 * 计划制定表单
 */

Ext.define('FHD.view.check.quarterlycheck.plan.QuarterlyCheckPlanFormOne', {
    extend: 'Ext.form.Panel',
    alias: 'widget.quarterlyCheckPlanFormOne',

	resetData : function(){
		var me = this;
	},
    loadData: function(id){
		var me = this;
		me.load({
	        url:  __ctxPath + '/check/quarterlycheck/findQuarterlyCheckPlanById.f',
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
	        	if(action.result.data.beginDataStr){
	        		me.assessPlanTimeStart.setValue(action.result.data.beginDataStr);
	        	}
	        	if(action.result.data.endDataStr){
	        		me.assessPlanTimeEnd.setValue(action.result.data.endDataStr);
	        	}
	        }
	    });
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
             
        me.idField=Ext.widget('hiddenfield',{
            name:"id",
            value:''
        });
        
        me.planName = Ext.widget('textfield', {//计划名称
            fieldLabel: '计划名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '7 10 0 30',
            name: 'name',
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
        me.workTage = Ext.widget('textareafield', {//工作目标
            xtype: 'textareafield',
            rows:3,
            fieldLabel: '抽查内容',
            margin: '7 10 0 30',
            name: 'checkContent',
            columnWidth: .7
        });
        me.rangRequire = Ext.widget('textareafield', {//范围要求
            xtype: 'textareafield',
            rows: 3,
            fieldLabel: '范围要求',
            margin: '7 10 0 30',
            name: 'rangeRequire',
            columnWidth: .7
        });
        //开始时间
        me.assessPlanTimeStart = Ext.widget('datefield',{
        	name: 'beginDateStr',
			columnWidth:.5,
			format: "Y-m-d"
        });
		//结束时间
        me.assessPlanTimeEnd = Ext.widget('datefield',{
        	name: 'endDateStr',
		    columnWidth:.499,
		    format: "Y-m-d"
        });
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
          
        //计划编号
        var planCode = Ext.widget('textfield', {
            xtype: 'textfield',
            rows: 1,
            fieldLabel: '计划编号',
            margin: '7 0 0 30',
            name: 'planCode',
            columnWidth: .4
        });
  	
    	
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
		       
        me.fieldSet = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            layout: {
     	        type: 'column'
     	    },
     	    items : [me.planName,planCode, autoButton, me.reponser, me.contactor, me.assessPlanTime, me.idField ]
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
         	    items : [ me.rangRequire]
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
         	    items : [me.workTage]
            };
        
        Ext.apply(me, {
        	border:false,
            items : [me.fieldSet,fieldSet3,fieldSet2]
        });

       me.callParent(arguments);
       
       
      
    }

});