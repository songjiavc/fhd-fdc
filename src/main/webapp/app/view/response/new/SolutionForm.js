/**
 * 应对措施编辑表单
 */
Ext.define('FHD.view.response.new.SolutionForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.solutionform',
    requires:['FHD.ux.fileupload.FileUpload',
              'FHD.view.risk.cmp.form.RiskRelateFormDetail'
              ],
    autoScroll: true,
	collapsed : false,
	riskId : '',
	showBack : true,
	type : 'risk',
	collapsable : false,
	archiveStatus : 'saved',
    border : false,
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        //措施名称
        var solutionName = Ext.widget('textfield', {
            fieldLabel: '措施名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            name: 'solutionName'
        });
        var riskId = Ext.widget('textfield', {
		    name : 'riskId',
		    value: '',
		    hidden : true
		});
		var descHtml = Ext.widget('textfield', {
		    name : 'descHtml',
		    value: '',
		    hidden : true
		});
        var solutionId = Ext.widget('textfield', {
		    name : 'id',
		    value: '',
		    hidden : true
		});
        var solutionType = Ext.widget('textfield', {
		    name : 'type',
		    value: '',
		    hidden : true
		});
        var riskAssessPlan = Ext.widget('textfield', {
		    name : 'riskAssessPlanId',
		    value: '',
		    hidden : true
		});
		var archiveStatus = Ext.widget('textfield', {
		    name : 'archiveStatus',
		    value: '',
		    hidden : true
		});
        //措施编号
        var solutionCode = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: '措施编号',
            margin: '7 10 10 30',
            name: 'solutionCode',
            columnWidth: .4
        });
        //自动生成机构编号按钮
    	var autoButton = {
            xtype: 'button',
            margin: '7 10 10 10',
            
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'),
            handler: function(){
       			FHD.ajax({
	            	url:__ctxPath+'/standard/standardTree/createStandardCode.f',
	            	params: {
	                	nodeId: me.nodeId
                 	},
	                callback: function (data) {
	                 	me.getForm().setValues({'solutionCode':data.code});//给code表单赋值
	                }
                });
            },
            columnWidth: .1
        };
         /*责任部门  */
		/*me.orgId = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '责任部门/人' + '<font color=red>*</font>',
			name:'orgId',
			type : 'dept_emp',
			allowBlank : false,
			multiSelect : false
		});*/
		me.orgId = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel: '责任部门/人' + '<font color=red>*</font>', // 所属部门人员
			labelAlign: 'left',
			labelWidth: 100,
			store: [],
			allowBlank : false,
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'orgId',
			value:''
		});
        //措施描述
        me.solutionDesc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '措施描述' + '<font color=red>*</font>',
            name: 'solutionDesc',
            height : 180,
            columnWidth: 1
        });
        //完成标志
        var indicator = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '完成标志' + '<font color=red>*</font>',
            name: 'completeIndicator',
            allowBlank : false,
            columnWidth: .5
        });
        //预计成本
        var cost = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '预计成本',
            name: 'cost'
        });
        //预计收效
        var income = Ext.widget('textfield', {
            xtype: 'textareafield',
            fieldLabel: '预计收效',
            name: 'income'
        });
        //附件
        var attachment = Ext.widget('FileUpload', {
			labelAlign : 'left',
			labelText : '附件',
			labelWidth : 100,
			columnWidth: 1,
			name : 'fileId',
			height: 23,
			showModel : 'base'
		});
		
		//开始时间
		me.startDate = Ext.widget('datefield',{
			    name: 'expectStartTime',
			    columnWidth:.5,
			    allowBlank : false,
			    format: "Y-m-d"
			});
		//结束时间
		me.endDate = Ext.widget('datefield',{
			name: 'expectEndTime',
		    columnWidth:.499,
		    allowBlank : false,
		    format: "Y-m-d"
		});
		
		var labelPlanDisplay={
			    xtype:'displayfield',
			    width: 100,
			    value:'起止日期:'+ '<font color=red>*</font>',
			    style: {
		            //float: 'left',
		            marginRight:'4px'
//		            marginBottom: '10px'
        			}
			    
			};
		var labelDisplay1={
				    xtype:'displayfield',
				    value:'&nbsp;至&nbsp;',
				    margin: '0 12 0 12'
				};
		me.dataContainer=Ext.create('Ext.container.Container',{//起止时间
     	    layout:{
     	    	type:'column'  
     	    },
     	    columnWidth : .5,
     	    items:[labelPlanDisplay, me.startDate, labelDisplay1, me.endDate]
		});
		
         //应对策略
        var strategy = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'stategy',
			labelAlign : 'left',
			dictTypeId : 'rm_response_strategy',
			multiSelect : false,
			fieldLabel : '应对策略',
			editable : false
		});
        
        // 风险事件
        if(me.type == 'risk'){
			me.riskSelector = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
				margin : '0 0 0 0',
	            border: false,
	            showbar: false
	        });
		}else if(me.type == 'dept' || me.type == 'all'){
			me.riskSelector = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelector', {
				fieldLabel : '风险',
				multiSelect: false,
				riskmyfoldertreevisable : true,
				labelAlign : 'left',
				height: 23,
				name : 'riskSelect',
				columnWidth : 1
			});
		}

		me.riskfieldSet = Ext.widget('fieldset', {
			title : '风险信息',
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			layout : {
				type : 'fit'
			},
			margin: '5 5 0 5',
            defaults : {
            	margin: '7 10 10 30'
            },
			items : [me.riskSelector
					]
		});
		
		if(me.type == 'risk'){
			me.riskfieldSet.collapse();
		}
        
        //基础信息fieldset
        var basicInfoFieldset = {
            xtype:'fieldset',
            title : '应对信息',
            border : true,
            collapsible: false,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '7 10 10 30',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [riskId,solutionId,solutionType,riskAssessPlan,archiveStatus,descHtml,solutionName, solutionCode, autoButton, strategy,me.orgId, me.dataContainer,indicator,cost,income,me.solutionDesc,attachment]
        };
        
        var bbarArray = [];
        if(me.showBack){
        	bbarArray = ['->',	
               {   
            	   text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),
                   iconCls: 'icon-operator-home',
                   handler: me.callback
               },
               	{   
            	   text: FHD.locale.get("fhd.common.save"),
                   iconCls: 'icon-control-stop-blue',
                   handler: me.save
               }
           ];
        }else{
        	bbarArray = ['->',	
               	{   
            	   text: FHD.locale.get("fhd.common.save"),
                   iconCls: 'icon-control-stop-blue',
                   handler: me.save
               }
           ];
        }
        
        Ext.apply(me, { bbar : {
               items: bbarArray
           },
        	border:false,
            items : [me.riskfieldSet,basicInfoFieldset]
            ,
	   		listeners:{
            	render:function(){
    		        setTimeout(function(){
    		        	me.editor = KindEditor.create('#' + (me.solutionDesc.getEl().query('textarea')[0]).id,{
                            uploadJson : __ctxPath + '/kindeditor/uploadjson.do',
                            fileManagerJson : __ctxPath + '/kindeditor/filemanagerjson.do',
                        });
    		        	me.editor.resizeType = 1;
    		        });
    	        }  
    		}
        });

       me.callParent(arguments);
    },
    save: function() {
	   	var me = this.up('solutionform');
	   	var solutionForm = me.getForm();
		if(me.editor.html() == ''){
			alert('请输入措施描述!');
			return false;
		}
		if(me.endDate.getValue() != ''){
			if(me.startDate.getValue() > me.endDate.getValue()){
				alert('开始时间要小于结束时间!');
				return false;
			}
		}
    	if(solutionForm.isValid()) {
			if(Ext.decode(me.orgId.getValue())[0].empid == ''){
				FHD.notification("请选择责任人!",FHD.locale.get('fhd.common.prompt'));
				return false;
			}
    		var riskId = '';
    		if(me.type == 'risk'){
    			riskId = me.riskId;
    		}else if(me.type == 'dept' || me.type == 'all'){
    			if(me.riskSelector.getValue() != ''){
	    			riskId = Ext.decode(me.riskSelector.getValue())[0].id;
    			}
    		}
		   	solutionForm.setValues({//paramObj
		   		riskId : riskId,
            	id : me.paramObj.solutionId,
            	type : me.paramObj.type,
            	archiveStatus : me.archiveStatus,
            	riskAssessPlanId : me.paramObj.businessId,
            	descHtml : me.editor.html()
		    }); 
		    
    		FHD.submit({
				form : solutionForm,
				url : __ctxPath + '/response/saveresponsesolution.f',
				callback: function (data) {
					if(!data.success){
						if(data.info){
							Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),data.info);
						}
					}else{
						me.callback();
					}
				}
			});
		}
	},
	reloadData: function() {
        var me = this;
        me.load({
            waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
            url: __ctxPath + '/response/loadsolutionformbysolutionid.f',
            params: {
                solutionId : me.paramObj.solutionId
            },
            success: function (form, action) {
            	if(me.type == 'risk'){
            		me.riskSelector.reloadData(Ext.decode(action.result.data.riskId)[0].id);
            	}else if(me.type == 'dept' || me.type == 'all'){
            		if(action.result.data.riskId != '' && action.result.data.riskId != null && action.result.data.riskId != undefined){
	            		me.riskSelector.initValue(action.result.data.riskId);
            		}else{
            			me.riskSelector.clearValues();
            		}
            	}
            	if(action.result.data.orgId){
            		var value = Ext.JSON.decode(action.result.data.orgId);
            		me.orgId.setValues(value);
            	}
            	if(action.result.data.solutionDesc != ''){
	            	me.editor.html(action.result.data.solutionDesc);
            	}else{
            		me.editor.html('');
            	}
            	
                return true;
            },
            failure: function (form, action) {
                return true;
            }
        });
	},
	callback : function(){},
	
    clearFormData:function(){
		var me = this; 
		me.getForm().reset();
		me.orgId.clearValues();
		if(me.editor){
			me.editor.html('');
		}
		// 责任部门
		var valuedept = [];
    	//var objdept = {};
    	//objdept["deptid"] = __user.majorDeptId;
    	//objdept["empid"] = __user.empId;
    	var objdept = {empid:__user.empId,empno:__user.empNo,empname:__user.empName,deptid: __user.majorDeptId,deptno:__user.majorDeptNo,deptname:__user.majorDeptName};
    	valuedept.push(objdept);
    	//me.orgId.setHiddenValue(valuedept);
    	me.orgId.setHideValue(valuedept);
		//me.orgId.initValue(Ext.encode(valuedept));
    	me.orgId.setValues(valuedept);
		if(me.type == 'risk'){
			// 风险
			me.riskSelector.reloadData(me.paramObj.riskId);
			me.riskId = me.paramObj.riskId;
		}else if(me.type == 'dept' || me.type == 'all' ){
			me.riskSelector.clearValues();
		}
		
	}
});