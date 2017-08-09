

Ext.define('FHD.view.icm.icsystem.bpm.ConstructPlanResultsRepair', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.constructplanresultsrepair',
	requires: [
        'FHD.ux.icm.common.FlowTaskBar',
        'FHD.view.icm.icsystem.constructplan.form.ConstructPlanPreviewForm'
    ],
	layout : {
		type : 'anchor'
	},
    plain: true,
    autoScroll:true,
    //传递的参数对象
    paramObj:{},
    /**
     * 设置激活的tab页签
     */
    setActiveItem:function(index){
    	me = this;
    	me.setActiveTab(index);
    },
    
    initParam:function(paramObj){
    	var me = this;
    	me.paramObj = paramObj;
    },
    
    initComponent: function() {
        var me = this;
        //基本信息
		me.basicInfo=Ext.widget('constructplanpreviewform',{
			columnWidth:1/1,
			border:false
		});
        //流程列表
        me.planprocesseditrepair = Ext.create('FHD.view.icm.icsystem.bpm.PlanProcessEditRepair',{id:'planprocesseditrepair',border : false,executionId : me.executionId});
        me.approvalIdeaGrid = Ext.create('FHD.view.comm.bpm.ApprovalIdeaGrid',{
			executionId: me.executionId,
			title:'审批意见历史列表',
			height:200,
			columnWidth:1
		});
		//fieldSet
		var fieldSet={
			xtype : 'fieldset',
				layout : {
					type : 'column'
				},
				collapsed : true,
				collapsible : true,
				columnWidth:1,
				collapsible : true,
				title : '审批意见列表',
				items : [me.approvalIdeaGrid]
		};
        me.bbar=[
		    '->',
		    {
	            text: '提交', //保存按钮
	            iconCls: 'icon-operator-submit',
	            handler: function () {
               		me.submit();
	            }
	        }
		];
        Ext.applyIf(me, {
        	layout:{
        		align: 'stretch',
        		type:  'vbox'
        	},
        	tabBar:{
        		style : 'border-right: 1px  #99bce8 solid;'
        	},
            items: [Ext.widget('flowtaskbar',{
    		jsonArray:[
	    		{index: 1, context:'1.流程和矩阵调整',status:'current'},
	    		{index: 2, context:'2.成果审批',status:'undo'}
	    	]
    		}),me.basicInfo,me.planprocesseditrepair,fieldSet]
        });
        me.callParent(arguments);
    },
   submit:function(){
		var me=this;
		var planprocessedittabpanel = me.down('[alias=widget.planprocessedittabpanel]');
		var floweditpanelforworkflow = me.down('[id=floweditpanelforworkflow]');
		var form = floweditpanelforworkflow.getForm();
		
		if(!form.isValid()&&planprocessedittabpanel.hidden==false) {
			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),"流程基本信息维护中有必填项未添加。");
		}else{
			FHD.ajax({//ajax调用
				url : __ctxPath+ '/icm/icsystem/constructplanresultsrepair.f',
			    params : {
			    	businessId : me.businessId,
			    	executionId : me.executionId
				},
				callback : function(data) {
					if(data.success){
						if(me.winId){
							Ext.getCmp(me.winId).close();
						}
					}else{
						Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),data.pointInfo);
					}
				}
			});
		}
	},
    reloadData : function() {
    	var me = this;
    	me.basicInfo.initParam({
    		businessId : me.businessId
    	});
    	me.basicInfo.reloadData();
    	me.planprocesseditrepair.initParam({
    		constructPlanId : me.businessId,
    		executionId 	: me.executionId
    	});
    	me.planprocesseditrepair.reloadData();
//    	me.grid.store.load();
    }
});