/**
 * 年度考核自评主面板
 * AUTHOR:Perry Guo
 * Date:2017-08-01
 */
 Ext.define('FHD.view.check.yearcheck.mark.YearCheckOwenMarkMainPanal',{
 		extend: 'Ext.form.Panel',
    	alias: 'widget.yearCheckMarkMainPanal',
    	
    	
    initComponent : function() {
			var me = this;
			me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					me.submit();
	            }
			}
		];
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
													status : 'current'
												}, {
													index : 5,
													context : '5.风险办汇总',
													status : 'undo'
												}, {
													index : 6,
													context : '6.主管领导汇总审批',
													status : 'undo'
												}, {
													index : 7,
													context : '7.负责人汇总审批',
													status : 'undo'
												}, {
													index : 8,
													context : '8.集团副总审批',
													status : 'undo'
												}],
										margin : '5 5 5 5'
									})
						});
						
				//考评打分底部内容（fieldSet+Grid）
				me.yearCheckOwenMarkDownPanal = Ext
						.create(
								'FHD.view.check.yearcheck.mark.YearCheckOwenMarkDownPanal',
								{
									flex : 1,
									businessId : me.businessId,
									executionId : me.executionId,
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
	    //提交选择审批人
    submit:function(){
    	
    			var me = this;
		me.formulateSubmitMainPanel = Ext
				.create('FHD.view.risk.assess.formulatePlan.FormulateSubmitMainPanel');
		var formulateApproverEdit = me.formulateSubmitMainPanel.formulateapproveredit;// 审批人页面
		me.subWin_assess = Ext.create('FHD.ux.Window', {
					title : '选择审批人',
					height : 200,
					width : 600,
					layout : {
						type : 'fit'
					},
					buttonAlign : 'center',
					closeAction : 'hide',
					items : [me.formulateSubmitMainPanel],
					fbar : [{
								xtype : 'button',
								text : '确定',
								handler : function() {
									me.submitForm(formulateApproverEdit);
								}
							}, {
								xtype : 'button',
								text : '取消',
								handler : function() {
									me.subWinhide(formulateApproverEdit);
								}
							}]
				}).show();
		
	},
	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},
	submitForm:function (formulateApproverEdit){
	var me=this;
	var modified=me.yearCheckOwenMarkDownPanal.yearCheckOwenMarkGrid.getStore().getModifiedRecords();
	
		var jsonArray = [];
			Ext.each(modified, function(m) {
						jsonArray.push(m.data);
					})
			var data = Ext.JSON.encode(jsonArray);
			
	var approverId = formulateApproverEdit.items.items[0].value;
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/check/yearcheck/subOwenMark.f',
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	approverId:approverId,
		    	data:data
			},
			callback : function(data) {
				me.body.unmask();
				if(me.winId){
					me.subWin_assess.hide();
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