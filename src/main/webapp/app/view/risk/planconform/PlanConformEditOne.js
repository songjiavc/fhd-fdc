/**
 * 
 * 计划制定表单
 */

Ext.define('FHD.view.risk.planconform.PlanConformEditOne', {
    extend: 'Ext.form.Panel',
    alias: 'widget.planConformEditOne',
    requires: [
		'FHD.view.risk.assess.quaAssess.commAssess.RiskAssessOpe'
	],
	
	//加载模板数据
	loadMbsStore: function(){
		var me = this;
		me.mbs.load({
			    scope   : this,
			    url: __ctxPath + '/access/formulateplan/findtemplates.f',
			    callback: function(records, operation, success) {
			    }
			});
	},
	//流程选择监听函数
	workFlowLisener: function(combo,records,eOpts){
		var me = this;
		if('riskAssess'==records[0].data.type){//风险评估计划
			me.loadMbsStore();
			me.mb.allowBlank = false;
			me.templatacontainer.setDisabled(false);
			me.templatacontainer.setVisible(true);
			me.contingencyType.allowBlank = true;
			me.contingencyType.setVisible(false);
		}else if('riskContingencyPlanTotal'==records[0].data.type){//风险评估计划
			me.contingencyType.allowBlank = false;
			me.contingencyType.setVisible(true);
			me.mb.allowBlank = true;//必填验证去掉
			me.templatacontainer.setVisible(false);
		}else{//风险辨识,风险应对
			me.loadMbsStore();
			me.mb.allowBlank = true;//必填验证去掉
			me.templatacontainer.setVisible(false);
			me.contingencyType.allowBlank = true;
			me.contingencyType.setVisible(false);
		}
	},
	
	resetData : function(){
		var me = this;
		me.mb.allowBlank = true;//必填验证去掉
		me.templatacontainer.setVisible(false);
		me.contingencyType.allowBlank = true;//必填验证去掉
		me.contingencyType.setVisible(false);
	},
	
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
	        	me.riskWorkSelect.setValue(formValue.planType);
	        	if(action.result.data.responsName){
	        		var value = Ext.JSON.decode(action.result.data.responsName);
	        		me.reponser.setValues(value);
	        	}
	        	if(action.result.data.contactName){
	        		var value = Ext.JSON.decode(action.result.data.contactName);
	        		me.contactor.setValues(value);
	        	}
	        	if(formValue.templateId){
	        		me.loadMbsStore();
	        		me.templatacontainer.setDisabled(false);
					me.templatacontainer.setVisible(true);
	        		me.mb.setValue(formValue.templateId);
	        	}else{//不存在模板，隐藏模板container
	        		me.mb.allowBlank = true;//必填验证去掉
					me.templatacontainer.setVisible(false);
	        	}
	        	if(formValue.contingencyType){
	        		me.contingencyType.allowBlank = false;
					me.contingencyType.setVisible(true);
					me.contingencyType.setValue(formValue.contingencyType);
	        	}else{//不存在模板，隐藏模板container
	        		me.contingencyType.allowBlank = true;//必填验证去掉
					me.contingencyType.setVisible(false);
	        	}
	        }
	    });
	},
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var querySelectFlowType =  __ctxPath + '/access/planconform/findriskworkflowtype.f';//流程选择
        
        me.idField=Ext.widget('hiddenfield',{
            name:"id",
            value:''
        });
        //2017年3月28日11:02:29 吉志强添加分库标示
        //alert("schm:"+me.schm)
        me.schm=Ext.widget('hiddenfield',{
            name:"schm",
            value:me.schm
        });
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
            fieldLabel: '工作目标',
            margin: '7 10 0 30',
            name: 'workTage',
            columnWidth: .7
        });
        me.rangRequire = Ext.widget('textareafield', {//范围要求
            xtype: 'textareafield',
            rows: 3,
            fieldLabel: '范围要求',
            margin: '7 10 0 30',
            name: 'rangeReq',
            columnWidth: .7
        });
        //开始时间
        me.assessPlanTimeStart = Ext.widget('datefield',{
        	name: 'beginDataStr',
			columnWidth:.5,
			format: "Y-m-d"
        });
		//结束时间
        me.assessPlanTimeEnd = Ext.widget('datefield',{
        	name: 'endDataStr',
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
        //工作类型，暂时不要
        me.states = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: '',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
        });
        //评估模板
   	 	me.mbs = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: '',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }
        });
        me.selectFlowType = Ext.create('Ext.data.Store', {
            fields: ['type', 'name'],
    	    proxy: {
    	         type: 'ajax',
    	         url: querySelectFlowType,
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
        });
        //工作类型
        me.workType = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '工作类型',
            name : 'workType',
            editable: false,//禁止用户输入
            disabled: true,
            store: me.states,
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
        //评估模板
   	 	me.mb = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '评估模板'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            name : 'templateId',
            editable: false,//禁止用户输入
            store: me.mbs,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'type',
            flex : 10
        });
        //模板按钮
        me.templatebutton = Ext.create('Ext.button.Button',{
            iconCls:'icon-magnifier',
            height: 22,
            width: 22,
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
						collapsible:true,
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
    	});
    	me.templatacontainer.setVisible(false);
    	
    	//应急预案类型
    	me.contingencyType = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'contingencyType',
			margin : '7 10 0 30', 
    		columnWidth : .5,
			labelAlign : 'left',
			allowBlank:false,//不允许为空
			dictTypeId : 'file_classify_preplan',
			multiSelect : false,
			fieldLabel: '预案类型'+'<font color=red>*</font>',
			editable : false
		});
        me.contingencyType.setVisible(false);
    	
   	 	//负责人
        /*me.reponser = Ext.create('FHD.ux.org.CommonSelector',{
        	fieldLabel: '负责人',
        	name : 'responsName',
            type:'emp',
            multiSelect:false,
            margin: '7 10 0 30',
            columnWidth: .5
        });*/
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
        //联系人
        /*me.contactor = Ext.create('FHD.ux.org.CommonSelector',{
        	fieldLabel: '联系人',
        	name : 'contactName',
            type:'emp',
            multiSelect:false,
            margin: '7 10 0 30',
            columnWidth: .5
        });*/
        
        
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
		
        //流程选择
        me.riskWorkSelect = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '流程选择'+'<font color=red>*</font>',
            allowBlank:false,
            name : 'planType',
            /*隐藏流程选择器
             * change by  郭鹏
             * 20170420
             * */
            hidden : 'true',
            store: me.selectFlowType,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'type',
            margin: '7 10 0 30',
            columnWidth: .5,
            listeners: {
            	select:function(combo,records,eOpts){//根据流程加载评估模板和工作类型
            		me.workFlowLisener(combo,records,eOpts);
            	}
            }
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
     	    //2017年3月28日13:07:19 吉志强   添加分库标识me.schm
     	    items : [me.planName,planCode, autoButton, me.reponser, me.contactor, me.riskWorkSelect, me.assessPlanTime,
     	    			me.templatacontainer, me.contingencyType, me.idField,me.schm/*me.workType,*/ ]
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