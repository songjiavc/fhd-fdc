/**
 * 年度考核自我评价底部Panal(fileSite+Grid)
 * AUTHOR:Perry Guo
 * Date:2017-08-01
 */
 Ext.define('FHD.view.check.yearcheck.mark.YearCheckOwenMarkDownPanal',{
	  extend: 'Ext.form.Panel',
      alias: 'widget.yearCheckOwenMarkDownPanal',
      
      initComponent:function (){
      		  var me=this;
              me.fieldSet = {
					xtype : 'fieldset',
					title : '基础信息',
					collapsible : true,
					collapsed : true,// 初始化收缩
					margin : '5 5 0 5',
					defaults : {
						columnWidth : 1 / 2,
						margin : '7 30 3 30',
						labelWidth : 95
					},
					layout : {
						type : 'column'
					},
					items : [{
								xtype : 'displayfield',
								fieldLabel : '计划名称',
								name : 'name'
							}, {
								xtype : 'displayfield',
								fieldLabel : '起止日期',
								name : 'beginendDateStr'
							}, {
								xtype : 'displayfield',
								fieldLabel : '联系人',
								name : 'cName'
							}, {
								xtype : 'displayfield',
								fieldLabel : '负责人',
								name : 'rName'
							}]
				};
		me.yearCheckOwenMarkGrid=Ext.create('FHD.view.check.yearcheck.mark.YearCheckOwenMarkGrid',{
				flex:1,
				margin:2,
				columnWidth :1,
				businessId : me.businessId,
				executionId : me.executionId
				
		})		
				
		me.fieldSet2 = Ext.create('Ext.form.FieldSet',{
			layout:{
     	        type: 'column'
     	    },
			title:'评估范围',
			collapsible: true,
			margin: '5 5 0 5',
			items:[me.yearCheckOwenMarkGrid]
	  	});
	  	
      
		Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items : [me.fieldSet,me.fieldSet2]
        });

        
        me.callParent(arguments);	
				
      }
 	
 	
 })