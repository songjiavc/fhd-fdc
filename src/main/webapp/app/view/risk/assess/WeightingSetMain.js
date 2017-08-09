/**
 * 
 * 定性评估主面板
 */

Ext.define('FHD.view.risk.assess.WeightingSetMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.weightingSetMain',
    
	  // 初始化方法
	  initComponent: function() {
	      var me = this;
	      
	      me.departmentGrid = Ext.create('FHD.view.risk.assess.DepartmentGrid',{height:130});
	      me.roleGrid = Ext.create('FHD.view.risk.assess.RoleGrid',{height:200});
	      me.weightAlarmPlanPanel = Ext.create('FHD.view.risk.assess.WeightAlarmPlanPanel');
	      
	      //me.rightWeightsetPanel = Ext.widget('rightWeightsetPanel');
	      
	      me.departmentGridFieldSet = {
	  			xtype : 'fieldset',
	  			title : '部门权重设定方案',
	  			margin : '5 5 0 5',
	  			items : [me.departmentGrid ]
	  		};
	      
	      me.roleGridFieldSet = {
	  			xtype : 'fieldset',
	  			title : '职务',
	  			margin : '5 5 0 5',
	  			items : [me.roleGrid]
	  		};
	  		
	  	 me.alarmPlanFieldSet = {
				xtype : 'fieldset',
				title : '告警方案',
				margin : '5 5 0 5',
				items : [me.weightAlarmPlanPanel]
			};
	      
	      Ext.apply(me, {
	      	  border:false,
	      	  autoScroll: true,
	      	  layout: {
	      		  align: 'stretch',
	              type: 'vbox'
	          },
	          items : [me.departmentGridFieldSet, me.roleGridFieldSet, me.alarmPlanFieldSet]
	      });
	
	      me.callParent(arguments);
	      
	     me.on('resize',function(p){
	  	 	 
	  	 });
	  }

});