Ext.define('FHD.view.risk.managereport.ManageReportDetalPanel', {
	extend: 'Ext.form.Panel',
	alias: 'widget.managereportdetalpanel',
	requires:['FHD.ux.fileupload.FileUpload'],
	
	reportId : '',
	
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
				if(action.result.data.relateRisk != ''){
            		me.riskdetailform.setVisible(true);
	                me.riskdetailform.reloadData(Ext.decode(action.result.data.relateRisk)[0].id);
            	}else{
            		me.riskdetailform.setVisible(false);
            	}
            }
        });
	},
       
    // 初始化方法
	initComponent: function() {
        var me = this;
        me.riskdetailform = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail',{flex:1});
        //文件名称
        me.reportName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '名称',
            name: 'reportName',
            margin : '7 30 3 30',
            labelWidth : 100,
            columnWidth: .5
        });
        
		//编号
		me.reportCode = Ext.widget('displayfield', {
		    fieldLabel:"文件编号",
		    name:'reportCode'
		});
		
		// 关联风险
		me.relateRisk = Ext.widget('displayfield', {
			fieldLabel : '风险',
			name : 'relateRiskStr',
			margin : '7 30 3 30',
			columnWidth : 1
		});
		
	    //开始时间
		me.startDate = {
				xtype: 'displayfield',
			    name: 'startDateStr',
			    columnWidth:.5
			};
		//结束时间
		me.endDate = {
				xtype: 'displayfield',
			    name: 'endDateStr',
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
	    
		//责任部门/人
	    me.respDeptName = Ext.widget('displayfield', {
	    		name:'respDeptNameStr',
	        	fieldLabel : '责任部门/人'// + '<font color=red>*</font>',
	    });
	    
		//报告内容
		me.content=Ext.widget('displayfield', {
				fieldLabel : '内容',
	            name:"content",
	            value:"",
	            columnWidth : 1
	    });
	    
	    var attachment = Ext.widget('displayfield', {
			fieldLabel: '附件',
			name : 'fileId',
			renderer : function(ralValues){
				if(ralValues == null || ralValues == ''){
					return '';
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
					return returnValue;
				}
			} 
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
		    title: '应急预案信息',
		    items:[me.reportName, me.reportCode, me.respDeptName,me.dataContainer,me.relateRisk, me.content,attachment]
		});
		
	   	Ext.applyIf(me, {
	       	autoScroll: true,
	       	border : false,
	       	layout: 'vbox',
		  	bodyPadding: "0 3 3 3",
		  	items:[me.riskdetailform,me.basicinfofieldset]
	   	});

   		me.callParent(arguments);
	},
	downloadFile : function(fileId){
        if(fileId != ''){
        	window.location.href=__ctxPath+"/sys/file/download.do?id="+fileId;
        }else{
        	FHD.notification('该样本没有上传附件!',FHD.locale.get('fhd.common.prompt'));
        }
   }
	   
});