/**
 * 年度考核自评主面板
 * AUTHOR:Perry Guo
 * Date:2017-08-01
 */
 Ext.define('FHD.view.check.yearcheck.mark.YearCheckRiskMarkMainPanal',{
 		extend: 'Ext.form.Panel',
    	alias: 'widget.yearCheckRiskMarkMainPanal',
    	
    	
    initComponent : function() {
			var me = this;
			me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					var dataStore=me.yearCheckOwenMarkDownPanal.yearCheckOwenMarkGrid.store;
					var dataSize=dataStore.data.length;
					var modifidSize=dataStore.getModifiedRecords().length
					if(modifidSize!=dataSize){
						Ext.Msg.alert("错误", "请先完成评分后提交");
					}else{
					me.submitForm();
					}
					
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
								'FHD.view.check.yearcheck.mark.YearCheckRiskMarkDownPanal',
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
	submitForm:function (formulateApproverEdit){
	var me=this;
	var modified=me.yearCheckOwenMarkDownPanal.yearCheckOwenMarkGrid.getStore().getModifiedRecords();
		var jsonArray = [];
			Ext.each(modified, function(m) {
						jsonArray.push(m.data);
					})
			var data = Ext.JSON.encode(jsonArray);
			
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/check/yearcheck/subRiskMark.f',
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	data:data
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