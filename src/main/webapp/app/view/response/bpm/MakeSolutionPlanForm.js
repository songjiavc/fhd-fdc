/**
 * 方案执行上报表单
 * */
Ext.define('FHD.view.response.bpm.MakeSolutionPlanForm', {
	extend : 'Ext.form.Panel',
	alias: 'widget.makesolutionplanform',
	requires: [
       'FHD.view.response.new.SolutionListForBpmView'
    ],
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	initParam : function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},
    autoWidth:true,
	collapsed : false,
	//autoScroll:true,
	border: false,
	initComponent :function() {
		var me = this;
		if(me.type == 'approve'){
			me.solutionlistforbpm = Ext.create('FHD.view.response.new.SolutionFormForView',{
				pagable : false,
				searchable : false
			});
		}else{
			me.solutionlistforbpm = Ext.create('FHD.view.response.new.SolutionListForBpm',{
				scroll: 'vertical',
				pagable : false,
				searchable : false
			});
		}
		
		Ext.applyIf(me,{
			items:[me.solutionlistforbpm
				/*{
					xtype : 'fieldset',
					collapsed : false,
					collapsible : true,
					title : '应对措施列表',
					margin: '5 5 0 5',
					items:[me.solutionlistforbpm]
				}*/
				]
		});
		me.callParent(arguments);
	},
	executionSave:function () {
		var me=this;
		
	},
	
	reloadData:function(){
		var me=this;
		//undo  调用list的reload方法 
		me.solutionlistforbpm.initParam(me.paramObj);
		me.solutionlistforbpm.reloadData();
		//undo  调用自己的reload方法
	}
	});

