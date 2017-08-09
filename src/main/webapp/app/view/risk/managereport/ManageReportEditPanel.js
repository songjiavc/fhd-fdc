Ext.define('FHD.view.risk.managereport.ManageReportEditPanel', {
	extend: 'Ext.form.Panel',
	alias: 'widget.managereporteditpanel',
	requires:['FHD.ux.fileupload.FileUpload'],
	
	reportId : '',
	businessId : '',
	typeId : '',
	archiveStatus : 'saved',
	
	//保存方法
	save: function() {
	   var me = this;
	   if(me.endDate.getValue() != ''){
			if(me.startDate.getValue() > me.endDate.getValue()){
				alert('开始时间要小于结束时间!');
				return false;
			}
		}
	   var form = me.getForm();
	   if(form.isValid()){
		   FHD.submit({
			   form: form,
			   url: __ctxPath + '/managereport/savemanagereport.f',
		       params: {
		       		reportId: me.reportId,
		       		typeId : me.typeId,
		       		businessId : me.businessId,
		       		archiveStatus : me.archiveStatus,
		       		comment : me.editor.html()
		       },
		       callback: function (data) {
	   				me.goback();
		       }
		   });
	   }
	},
	//返回
	goback: function(){
	},
	
	initParams : function(typeId){
		var me = this;
		me.typeId = typeId;
	},
	
	initRiskParams : function(riskId,businessId){
		var me = this;
		var valuedept = [];
    	var objdept = {};
    	objdept["id"] = riskId;
    	valuedept.push(objdept);
		me.relateRisk.initValue(Ext.encode(valuedept));
		me.relateRisk.setHiddenValue(valuedept);
		me.relateRisk.grid.setDisabled(true);
		me.relateRisk.button.setDisabled(true);
		me.businessId = businessId;
	},
	
	resetData:function(){
		var me = this;
		me.reportId = "";
		me.businessId = '';
		me.getForm().reset();
		me.respDeptName.clearValues();
		me.relateRisk.clearValues();
		if(me.editor){
			me.editor.html('');
		}
		
	},
	
	resetTemplate : function(){
		var me = this;
		me.load({
            url: __ctxPath + '/managereport/finemanagereporttemplate.f',
            params: {
                businessId : me.businessId,
        		typeId : me.typeId
            },
            success: function (form, action) {
             	me.editor.html(Ext.JSON.decode(action.response.responseText).data.desc);
            }
        });
	},
	
	//加载表单数据
	reloadData: function(reportId) {
		var me = this;	
		if(reportId != undefined && reportId != null && reportId != ''){
			me.reportId = reportId;
		}
		me.load({
            url: __ctxPath + '/managereport/finemanagereportinfo.f',
            params: {
                reportId: reportId
            },
            success: function (form, action) {
               	var formValue = form.getValues();
               	//me.respDeptName.initValue(formValue.respDeptName);
               	if(action.result.data.respDeptName){
               		var value = Ext.JSON.decode(action.result.data.respDeptName);
	               	me.respDeptName.setValues(value);
               	}
               	me.relateRisk.initValue(formValue.relateRisk);
               	if(formValue.content != ''){
		        	me.editor.html(formValue.content);
			    }else{
			    	me.editor.html('');
				}
            }
        });
	},
       
    // 初始化方法
	initComponent: function() {
        var me = this;
        
        //文件名称
		me.reportName = Ext.widget('textfield', {
		    fieldLabel:"名称"+'<font color=red>*</font>',
		    allowBlank:false,
		    name:'reportName'
		});
		
		//编号
		me.reportCode = Ext.widget('textfield', {
		    fieldLabel:"文件编号",
		    name:'reportCode'
		});
		
		// 关联风险
		me.relateRisk = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelector', {
			title : '请您选择风险',
			fieldLabel : '风险',
			multiSelect: false,
			labelAlign : 'left',
			name : 'relateRisk',
			margin : '7 30 3 30',
			height : 46,
			columnWidth : 1
		});
		
	    //排序
	    me.sort=Ext.widget('numberfield', {
	            fieldLabel:"排序",
	            minValue:0,  
	            name:"sort",
	            value:""
	    });
	    
		//开始时间
		me.startDate = Ext.widget('datefield',{
			    name: 'startDateStr',
			    columnWidth:.5,
			    format: "Y-m-d"
			});
		//结束时间
		me.endDate = Ext.widget('datefield',{
			name: 'endDateStr',
		    columnWidth:.499,
		    format: "Y-m-d"
		});	
		
			
		var labelPlanDisplay={
			    xtype:'displayfield',
			    width: 100,
			    value:'起止日期:',
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
	    
		//责任部门/人
	    /*me.respDeptName=new Ext.create('FHD.ux.org.CommonSelector',{
	    		name:'respDeptName',
	        	fieldLabel : '责任部门/人',// + '<font color=red>*</font>',
				type : 'dept_emp',
				subCompany : false,
				multiSelect : false
	    });*/
	    me.respDeptName=Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel: '责任部门/人' , // 所属部门人员
			labelAlign: 'left',
			labelWidth: 100,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'respDeptName',
			value:''
		});
	    
		//报告内容
		me.content=Ext.widget('textarea', {
			fieldLabel : '内容',
            name:"content",
            height : 300,
            columnWidth : 1,
            value:""
	    });
		
		//附件
		me.FileUpload = Ext.widget('FileUpload', {
			margin: '7 30 10 30',
			labelAlign : 'left',
			labelText: '附件',
			fieldLabel : '附件',
			labelWidth : 100,
			multiSelect: false,//是否多选
			height: 50,
			name : 'fileIds',
			showModel : 'base'
		});    
		
		   //基本信息fieldset
		me.basicinfofieldset = Ext.widget('fieldset', {
		    flex:1,
		    collapsible: true,
		    autoHeight: true,
		    autoWidth: true,
		    defaults: {
		        columnWidth : 1 / 2,
		        margin: '7 30 3 30',
		        labelWidth: 95
		    },
		    layout: {
		        type: 'column'
		    },
		    title: '基本信息',
		    items:[me.reportName, me.reportCode, me.respDeptName,me.dataContainer,me.relateRisk, me.content]
		});
		
		//附件信息fieldset
		me.attachmentfieldset = Ext.widget('fieldset', {
		    flex:1,
		    collapsible: true,
		    collapsed : true,
		    autoHeight: true,
		    autoWidth: true,
		    defaults: {
		       	columnWidth : 1 / 1,
		       	margin: '7 30 3 30',
		        labelWidth: 95
		    },
		    layout: {
		        type: 'column'
		    },
		    title: '附件信息',
		    items:[me.FileUpload]
		});
           
	   	Ext.applyIf(me, {
	       	autoScroll: true,
	       	border : false,
	       	layout: 'column',
		  	bodyPadding: "0 3 3 3",
		  	items:[me.basicinfofieldset, me.attachmentfieldset],
		  	bbar: {items: [ '->',{
				            text: '返回', //保存按钮
				            iconCls: 'icon-control-repeat-blue',
				            handler: function () {
				            	me.goback();
				            }
				        },{text: '保存', //保存按钮
				            iconCls: 'icon-control-stop-blue',
				            handler: function () {
				            	me.save();
				            }
				        }
		  			]
	   		},
	   		listeners:{
            	render:function(){
    		        setTimeout(function(){
    		        	me.editor = KindEditor.create('#' + (me.content.getEl().query('textarea')[0]).id);
    		        	me.editor.resizeType = 1;
    		        });
    	        }  
    		}
	   	});

   		me.callParent(arguments);
	}
	   
});