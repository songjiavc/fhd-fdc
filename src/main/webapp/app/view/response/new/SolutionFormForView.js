/**
 * 应对措施编辑表单
 */
Ext.define('FHD.view.response.new.SolutionFormForView', {
    extend: 'Ext.form.Panel',
    alias: 'widget.solutionformforview',
    autoScroll: true,
    layout : {
    	type : 'vbox',
    	align : 'stretch'
    },
    requires: [
   	],
    border : false,
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.riskdetailform = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail');
	    /*me.riskFieldSet = Ext.widget('fieldset',{
			title : '风险信息',
            border : true,
            collapsible: true,
            collapsed : true,
            layout: {
            	type :'vbox',
            	align : 'stretch'
            }
	    });
	    me.riskFieldSet.add(me.riskdetailform);*/
        //措施名称
        var solutionName = Ext.widget('displayfield', {
            fieldLabel: '措施名称',
            name: 'solutionName'
        });
        var riskId = Ext.widget('textfield', {
		    name : 'riskId',
		    value: '',
		    hidden : true
		});
        var solutionId = Ext.widget('textfield', {
		    name : 'id',
		    value: '',
		    hidden : true
		});
		var solutionCode = Ext.widget('displayfield', {
            fieldLabel: '措施编号',
            name: 'solutionCode'
        });
       
        
         /*责任部门  */
		me.orgId = Ext.widget('displayfield', {
			fieldLabel : '责任部门/人',
			name:'orgId',
			type : 'dept',
			allowBlank : false,
			multiSelect : false
		});
        //措施描述riskAssessPlanId : me.paramObj.businessId,riskAssessPlanId : me.paramObj.businessId,
        me.solutionDesc = Ext.widget('displayfield', {
            fieldLabel: '措施描述',
            name: 'solutionDesc',
            columnWidth : 1
        });
        //完成标志
        var indicator = Ext.widget('displayfield',{
            fieldLabel: '完成标志',
            rows : 1,
            name: 'completeIndicator',
            columnWidth: .5
        });
        //预计成本
        var cost = Ext.widget('displayfield',{
            fieldLabel: '预计成本',
            name: 'cost'
        });
        //预计收效
        var income = Ext.widget('displayfield',{
            fieldLabel: '预计收效',
            name: 'income'
        });
        //附件
        var attachment = Ext.widget('displayfield', {
			fieldLabel: '附件',
			name : 'fileId',
			renderer : function(ralValues){
				if(ralValues == null || ralValues == ''){
				}else{
					var returnValue = "";
					Ext.each(Ext.decode(ralValues),function(ralValue){
						if(returnValue == null || returnValue == ''){
							returnValue = "<a href='javascript:void(0)'onclick='Ext.getCmp(\""+me.id+"\").downloadFile(\""+
							ralValue.fileId + "\")' >"+ralValue.fileName+"</a>";
						}else{
							returnValue += ";" + "<a href='javascript:void(0)'onclick='Ext.getCmp(\""+me.id+"\").downloadFile(\""+
							ralValue.fileId + "\")' >"+ralValue.fileName+"</a>";
						}
					});
				}
				return returnValue;
			} 
		});   
		//开始时间
		me.startDate = {
				xtype: 'displayfield',
			    name: 'expectStartTime',
			    columnWidth:.5
			};
		//结束时间
		me.endDate = {
				xtype: 'displayfield',
			    name: 'expectEndTime',
			    columnWidth:.499
			};
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
         //应对策略
        var strategy = Ext.widget('displayfield',{
			name : 'stategy',
			fieldLabel : '应对策略'
		});
		//基础信息fieldset
        me.basicInfoFieldset = Ext.widget('fieldset',{
            title : '应对措施',
            border : true,
            collapsible: true,
            collapsed : false,
            defaultType: 'textfield',
            defaults : {
            	margin: '7 30 3 30',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [riskId,solutionId,solutionName, solutionCode,strategy,me.orgId, me.dataContainer,indicator,cost,income,me.solutionDesc,attachment]
        });
       Ext.apply(me,{
           items : [me.riskdetailform,me.basicInfoFieldset]
       });
       me.callParent(arguments);
    },
	reloadData: function() {
        var me = this;
        me.load({
            waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
            url: __ctxPath + '/response/loadsolutionformbysolutionidforview.f',
            params: {
                solutionId : me.paramObj.solutionId
            },
            success: function (form, action) {
            	if(action.result.data.riskId != ''){
            		//me.riskFieldSet.setVisible(true);
            		me.riskdetailform.setVisible(true);
	                me.riskdetailform.reloadData(action.result.data.riskId);
            	}else{
					//me.riskFieldSet.setVisible(false);
            		me.riskdetailform.setVisible(false);
            	}
            }
        });
	},
	downloadFile : function(fileId){
        if(fileId != ''){
        	window.location.href=__ctxPath+"/sys/file/download.do?id="+fileId;
        }else{
        	FHD.notification('该样本没有上传附件!',FHD.locale.get('fhd.common.prompt'));
        }
   }
});