/**
 * 结果整理MainPanal
 * AUTHOR:Perry Guo
 * Date:2017-08-01
 */
 Ext.define('FHD.view.check.yearcheck.tidy.YearCheckFinalApproveForPresident',{
 		extend: 'Ext.form.Panel',
    	alias: 'widget.yearCheckFinalApproveForPresident',
    	
    	
    initComponent : function() {
			var me = this;
			me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					me.submitForm(me.formulateApproverSubmitDownMain.ideaApproval.isPass,me.formulateApproverSubmitDownMain.ideaApproval.getValue());
	            }
			}
		];
		me.formulateApproverSubmitDownMain = Ext.create('FHD.view.check.yearcheck.approver.YearCheckPlanApproverDown',{
	  		flex: 1,
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  		winId : me.winId,
	  		margin : '0 0 0 0'
	  	});
				//菜单头进度条
				me.flowtaskbar = Ext.widget('panel', {
							border : false,
							collapsible : true,
							collapsed : true,
							items : Ext.create('FHD.ux.icm.common.FlowTaskBar',
									{
										jsonArray : [{
													index : 1,
													context : '1.考核计划制定',
													status : 'done'
												}, {
													index : 2,
													context : '2.主管领导审批',
													status : 'done'
												}, {
													index : 3,
													context : '3.负责人审批',
													status : 'done'
												}, {
													index : 4,
													context : '4.考评打分',
													status : 'done'
												}, {
													index : 5,
													context : '5.风险办汇总',
													status : 'done'
												}, {
													index : 6,
													context : '6.主管领导汇总审批',
													status : 'done'
												}, {
													index : 7,
													context : '7.负责人汇总审批',
													status : 'done'
												}, {
													index : 8,
													context : '8.集团副总审批',
													status : 'current'
												}],
										margin : '5 5 5 5'
									})
						});
				//考评打分底部内容（fieldSet+Grid）
				me.yearCheckOwenMarkDownPanal = Ext
						.create(
								'FHD.view.check.yearcheck.tidy.YearCheckFinalApproveDownPanal',
								{
									flex : 1,
									businessId : me.businessId,
									executionId : me.executionId,
									isApprove:true,
									winId : me.winId,
									margin : '0 0 0 0'
								})

								
				Ext.apply(me, {
					autoScroll : false,
					border : false,
					layout : {
						align : 'stretch',
						type : 'vbox'
					},
					items : [me.flowtaskbar, me.yearCheckOwenMarkDownPanal]
				});

				me.callParent(arguments);
				
        me.form.load({
	        url: __ctxPath + '/check/yearcheck/findYearCheckPlanById.f',
	        params:{id:me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
		
			},

	submitForm:function (isPass,examineApproveIdea){
	var me=this;
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/check/yearcheck/submTidyMarkforPresident.s',
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	isPass:isPass,
		    	examineApproveIdea:examineApproveIdea
			},
			callback : function(data) {
				me.body.unmask();
				if(me.winId){
					Ext.getCmp(me.winId).close();
				}else{
					window.location.reload();
				}
			}
		});
	},	
 	reloadData:function(){
		var me=this;
	}
 })