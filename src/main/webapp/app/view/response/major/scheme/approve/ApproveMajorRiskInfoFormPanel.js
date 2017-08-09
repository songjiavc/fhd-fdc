Ext.define('FHD.view.response.major.scheme.approve.ApproveMajorRiskInfoFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.approvemajorriskinfoformpanel',
    defaults: {
        columnWidth : 1/3,
        margin: '7 30 3 30',
        labelWidth: 95
    },
    layout: {
	        type: 'column'
	    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.majorRiskId = Ext.widget('hiddenfield',{ name:"riskId",value:''});
        me.majorRiskName = {xtype:'displayfield', fieldLabel : '重大风险名称', name:'majorRiskName'};
        me.deptName = {xtype:'displayfield', fieldLabel : '部门', name:'deptName'};
        me.deptType = {xtype:'displayfield', fieldLabel : '责任类型', name:'deptType'};
        me.deptId = Ext.widget('hiddenfield',{ name:"deptId",value:''});
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.majorRiskName,me.deptName,me.deptType,me.majorRiskId,me.deptId]
        });
        me.callParent(arguments);
        me.form.load({
	        url: __ctxPath + '/majorResponse/getMajorRiskTaskSelectEmpOfDeptIdInVarible',
	        params:{
	        	businessId: me.businessId,
	        	executionId :me.executionId
	        },
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
      
    }
});