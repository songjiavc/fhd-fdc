Ext.define('FHD.view.response.major.task.MajorRiskTaskSelectEmpFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorrisktaskselectempformpanel',
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
        me.empInput = Ext.create('FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptInput',{
        		fieldLabel : '应对人',
        		columnWidth : 1,
        		labelWidth:95,
        		multiSelect : true,
        		useUserOrg : true,   //默认true，根据登录用户所在部门进行筛选
    			orgId : "",          //可选  部门id给定则根据指定部门筛选
        		roleId : ""          //可选 给定角色将根据角色id筛选
        	});
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.majorRiskName,me.deptName,me.deptType, me.empInput,me.majorRiskId,me.deptId]
        });
        me.callParent(arguments);
        me.form.load({
	        url: __ctxPath + '/majorResponse/getMajorRiskTaskSelectEmp',
	        params:{businessId: me.businessId,executionId:me.executionId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        	var result = action.result.data;
	        	 me.empInput.setValue(result.empList)
	        }
	    });
      
    }
});