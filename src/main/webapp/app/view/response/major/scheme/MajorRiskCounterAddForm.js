Ext.define('FHD.view.response.major.scheme.MajorRiskCounterAddForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskcounteraddform',
    defaults: {
        columnWidth : 1,
        margin: '7 30 3 30',
        labelWidth: 95
    },
    layout: {
	        type: 'column'
	    },
    saveCounter:function(itemId){
			var me = this;
			var form = me.getForm();
			var executionEmpId = null;
			executionEmpId = me.empInput.getValue();
			me.counterType.setValue(me.schemeType);
			if(executionEmpId.length ==0){
				FHD.notification('执行人不能为空！',FHD.locale.get('fhd.common.prompt'));
				return ;
			}
			var ret = {};
			if(form.isValid()){
	    		FHD.submit({
	    			form:form,
	    			params:{
	    				itemId:itemId,
	    				executionEmpId:executionEmpId,
	    			},
	    			url: __ctxPath + '/majorResponse/saveCounter',
	    			callback:function(data){
	    				var result = data.data;
	    				var isOK = data.success;
	    				if(isOK){
	    					ret.result = result;
	    					ret.isValid = true;//保存成功了
	    				}else{
	    					ret.isValid = false;//保存失败了
	    				}
	    			}
	    		});
	    		
	    	}else{
	    		return false;
	    	}
		},
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.description = {xtype:'textareafield', fieldLabel : '措施描述', name:'description',allowBlank: false,rows:1,cols:100};
        me.startTime = {xtype:'datefield', fieldLabel : '实施时间', name : 'startTimeStr',allowBlank: true  ,format: 'Y-m-d'};
		me.finishTime = {xtype:'datefield', fieldLabel : '完成时间', name : 'finishTimeStr',allowBlank: true,format: 'Y-m-d'};
        me.dateContainer = {
				xtype:'container',
				layout:'hbox',
				defaults: {
			        labelWidth: 95,
			        margin: '0 30 3 0'
			    },
				items:[me.startTime,me.finishTime]
		};
        me.empInput = Ext.create('FHD.view.compoments.selectcompoments.SelectUserByRoleOrDeptInput',{
    		fieldLabel : '实施人',
    		labelWidth:95,
    		columnWidth : 1,
    		multiSelect : true,
    		useUserOrg : true,   //默认true，根据登录用户所在部门进行筛选
			orgId : "",          //可选  部门id给定则根据指定部门筛选
    		roleId : ""          //可选 给定角色将根据角色id筛选
    	});
        me.target = {xtype:'textareafield', fieldLabel : '管理目标', name:'target',allowBlank: true,rows:1,cols:100};
        me.biaozhi = {xtype:'textareafield', fieldLabel : '完成标志', name:'completeSign',allowBlank: true,rows:1,cols:100};
        me.filedId = Ext.widget('hiddenfield',{name:"id",value:''});
        me.counterType = Ext.widget('hiddenfield',{name:"type",value:''});
        me.saveBtn = { 
        		xtype: 'button',
        		minWidth:60,
        		maxWidth:80,
        		text: '保存',
        		handler:function(){
        			me.saveCounter(me.itemId);
        		}
        };
        Ext.apply(me, {
        	autoScroll: true,
        	border:false,
            items : [me.description,me.dateContainer,me.empInput,me.target,me.biaozhi,me.filedId,me.counterType],
            buttons: [
            	 me.saveBtn 
            ]
        });
        me.callParent(arguments);
        /*me.form.load({
	        url: __ctxPath + '/majorResponse/getMajorRiskTaskSelectEmp',
	        params:{businessId: me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });*/
      
    }
});