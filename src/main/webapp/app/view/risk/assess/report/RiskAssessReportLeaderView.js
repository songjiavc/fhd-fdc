/**
 * @author : 邓广义
 *  风险评价报告模板管理主面板
 */
Ext.define('FHD.view.risk.assess.report.RiskAssessReportLeaderView',{
 	extend: 'Ext.container.Container',
 	alias : 'widget.riskassessreportleaderview',
    requires : [
                	'FHD.view.bpm.mywork.MyTask',
                	'FHD.ux.icm.common.FlowTaskBar'
             ],
             reloadData:function(){
//         		var me = this;
//         		var businessId = me.businessId;//评估计划的ID从工作流取得
//         		var executionId = me.executionId;
//         		var winId = me.winId;
         		
//         		var url = __ctxPath + '/sys/report/findRiskAssessReportByAssessId.f';
//         				FHD.ajax({
//             	        	url:url,
//             	        	async:false,
//         	    	        params:{
//         	    	        	AssessPlanId:businessId,
//         	    	        	executionId:executionId,
//         	    	        	winId:winId
//         	    	        },
//         	    	       callback:function(data){}
//         	    	    });
         	},
             initComponent: function () {
         		var me = this;
         		var businessId = me.businessId;//评估计划的ID从工作流取得
         		var executionId = me.executionId;
         		var winId = me.winId;
         		var url = __ctxPath + '/sys/report/findRiskAssessReportByAssessId.f';
         				FHD.ajax({
             	        	url:url,
             	        	async:false,
         	    	        params:{
         	    	        	AssessPlanId:businessId,
         	    	        	executionId:executionId,
         	    	        	winId:winId
         	    	        },
         	    	       callback:function(data){
         	    	    	 
         	    	    	   if(data){
         	    	    		   me.CData = data.data;
         	    	    	   }
         	    	       }
         	    	    });
         	
         		var bbr = ['->',  {text : "下载",iconCls:'icon-page-word', handler:me.downloadFun, scope : this},
  	    	              {text : "提交工作流",iconCls:'icon-operator-submit', handler:me.onSubmit, scope : this}]
         		Ext.applyIf(me,{
                     border: false,
                     bodyPadding: "5 5 5 5",
                     flex:1,
                 	layout:{
                        align: 'stretch',
                        type: 'vbox'
            		},
                     overflowY:'auto'
         		});
             	
         		me.callParent(arguments);
         		var toppanel = Ext.widget('panel',{border:false,items:Ext.widget('flowtaskbar',{
            		jsonArray:[
        		    		{index: 1, context:'1.计划制定',status:'done'},
        		    		{index: 2, context:'2.计划审批',status:'done'},
        		    		{index: 3, context:'3.任务分配',status:'done'},
        		    		{index: 4, context:'4.风险评估',status:'done'},
        		    		{index: 5, context:'5.评估任务审批',status:'done'},
        		    		{index: 6, context:'6.评估结果整理',status:'done'},
        		    		{index: 7, context:'7.评估报告编制',status:'done'},
        		    		{index: 8, context:'8.评估报告审批',status:'current'}
        		    	],margin : '5 5 5 5'

        	    	})});
         		me.bottompanel = Ext.widget('panel',{
         			html:'<div style="border:1px solid #000;padding:15px">' + me.CData +'</div>',
         			flex:1,
         			bbar:bbr,
					autoScroll:true,
         			bodyBorder:false,
         			bodyStyle: {
         			    background: '#fff'
         			},
         			bodyPadding: "10 10 10 10"
         		});
         		me.add(toppanel);
         		me.add(me.bottompanel);
         		
         	},
         	downloadFun : function(grid, rowIndex, colIndex) {
         		var me = this;
         		window.location.href = __ctxPath + '/sys/report/downloadReport.f?assessPlanId='+me.businessId;
         	},
         	onPreview:function(){},
         	onSave:function(){},
         	onSubmit:function(){
         		//最后一步 关闭工作流
         		var me = this;
         		me.bottompanel.body.mask("提交中...","x-mask-loading");
         		var businessId = me.businessId;
         		var executionId = me.executionId;
         		var url = __ctxPath + '/sys/report/doAssessReportWorkFlowEnd.f';
         				FHD.ajax({
             	        	url:url,
         	    	        params:{
         	    	        	executionId:executionId,
         	    	        	AssessPlanId:businessId,
         	    	        },
         	    	       callback:function(data){
         	    	    	  me.bottompanel.body.unmask();
         	    	    	  Ext.getCmp(me.winId).close();
         	    	       }
         	    	    });
         	
         	},

         });