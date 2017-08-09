/**
 * 
 * 大综合查询
 * 入口参数
	me.assessPlanId(可有不可有)
 * 
 */

Ext.define('FHD.view.risk.assess.comprehensiveQuery.ComprehensiveQueryPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.comprehensiveQueryPanel',
    
    requires : [
	],
    
    afresh : function() {
    	var me = this;
    	me.planNameCmboBox.setValue(me.planNameCmboBox.store.data.items[0].data.id);
		me.assementStatus.setValue('');
		me.riskStatus.setValue('');
		me.orgM.clearValues();
		me.orgA.clearValues();
		me.assessEmp.clearValues();
		me.riskName.setValue('');
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        me.planNameStore = Ext.create('Ext.data.Store', {
    	    fields: ['id', 'text'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/assess/riskTidy/findPlanComplete.f',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
        
        //评估计划
        me.planNameCmboBox = Ext.create('Ext.form.ComboBox', {
    	    store: me.planNameStore,
    	    fieldLabel: '评估计划',
    	    emptyText:'请选择',//默认为空时的提示  
    	    allowBlank:false,//不允许为空
    	    editable:false,
    	    margin: '7 30 3 30',
    	    queryMode: 'local',
    	    name:'planName',
    	    displayField: 'text',
    	    valueField: 'id',
    	    triggerAction :'all',
    	    columnWidth: .5
    	});
        
        //风险名称
        me.riskName = Ext.widget('textfield', {
            fieldLabel: '风险名称',
            margin: '7 30 3 30',
            name: 'riskName',
            columnWidth: .5,
            allowBlank: true,
            maxLength: 255
        });
        
        //主责部门M
        /*me.orgM = Ext.create('FHD.ux.org.CommonSelector',{
			 fieldLabel: '主责部门',
			 allowBlank:false,//不允许为空
			 id : 'orgM',
		     type : 'dept',
		     multiSelect : true,
		     name:'orgM',
		     margin: '7 30 3 30',
		     columnWidth:.5
		  });*/
        me.orgM = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
			id : 'orgM',
		    type : 'dept',
			fieldLabel: '主责部门', // 所属部门人员
			labelAlign: 'left',
			allowBlank:false,//不允许为空
			labelWidth: 100,
			columnWidth: .5,
			margin: '7 30 3 30',
			height:80,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'orgM',
			value:''
		});
        
        //相关部门A
        /*me.orgA = Ext.create('FHD.ux.org.CommonSelector',{
			 fieldLabel: '相关部门',
			 allowBlank:false,//不允许为空
			 id : 'orgA',
		     type : 'dept',
		     multiSelect : true,
		     name:'orgA',
		     margin: '7 30 3 30',
		     columnWidth:.5
		});*/
        me.orgA = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
			id : 'orgA',
		    type : 'dept',
			fieldLabel: '相关部门', // 所属部门人员
			labelAlign: 'left',
			allowBlank:false,//不允许为空
			labelWidth: 100,
			columnWidth: .5,
			margin: '7 30 3 30',
			height:80,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'orgA',
			value:''
		});
        
        //评估人
        /*me.assessEmp = Ext.create('FHD.ux.org.CommonSelector',{
        	fieldLabel: '评估人',
        	name : 'assessEmp',
        	//id : 'responsNameId',
            type:'emp',
            multiSelect:true,
            margin: '7 30 3 30',
            columnWidth: .5
        });*/
        me.assessEmp = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
		    type : 'emp',
			fieldLabel: '评估人', // 所属部门人员
			labelAlign: 'left',
			allowBlank:false,//不允许为空
			labelWidth: 100,
			columnWidth: .5,
			margin: '7 30 3 30',
			height:80,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'assessEmp',
			value:''
		});
        
        //风险水平(汇总)
        me.riskStatus = Ext.create('Ext.form.NumberField',{
              fieldLabel: '风险水平(汇总)',
              name: 'riskStatus',
              xtype: 'numberfield',
              minValue: 1,
              //maxValue: 100,
              margin: '7 30 3 30',
              columnWidth: .5
        });
        
        me.assementStatusStore = Ext.create('Ext.data.Store', {
    	    fields: ['id', 'text'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/assess/riskTidy/findRiskLevel.f',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
    	});
        
        //风险级别
        me.assementStatus = Ext.create('Ext.form.field.ComboBox', {
    	    store: me.assementStatusStore,
    	    fieldLabel: '风险级别(汇总)',
    	    emptyText:'请选择',//默认为空时的提示  
    	    allowBlank:false,//不允许为空
    	    editable:false,
    	    margin: '7 30 3 30',
    	    queryMode: 'local',
    	    name:'assementStatus',
    	    displayField: 'text',
    	    valueField: 'id',
    	    triggerAction :'all',
    	    columnWidth: .5
    	});
        
        Ext.apply(me, {
        	border : true,
        	margin : '5 5 5 5',
        	layout : {
				align : 'stretch',
				type : 'column'
			},
        	items : [me.planNameCmboBox, me.riskName, me.assementStatus, me.riskStatus, me.orgM, me.orgA, me.assessEmp],
        	bbar : {
                items: ['->',	
 	               {   
 	            	   text: '返回',
 	                   iconCls: 'icon-operator-home',
 	                   handler: function(){
 	                	  me.comprehensiveGrid.formwindow.close();
 	                   }
 	               },
 	              {   
 	            	   text: '重置',
 	                   iconCls: 'icon-control-stop-blue',
 	                   handler: function(){
 	                	 me.afresh();
 	                   }
 	               },
 	               {   
 	            	   text: "确定",
 	                   iconCls: 'icon-control-stop-blue',
 	                   handler: function(){
 	                	    var planId = me.planNameCmboBox.getValue();
	 	     	            var assementStatus = me.assementStatus.getValue();
	 	     	            var riskStatus = me.riskStatus.getValue();
	 	     	            var orgM = me.orgM.getValue();
	 	     	            var orgA = me.orgA.getValue();
	 	     	            var assessEmp = me.assessEmp.getValue();
	 	     	            var riskName = me.riskName.getValue();
	 	     	            var datas = [];
	 	    				var value = {};
	 	     	            
	 	    				value['planIdQuery'] = planId;
	 	    		    	value['assementStatusQuery'] = assementStatus;
	 	    		    	value['riskStatusQuery'] = riskStatus;
	 	    		    	value['orgMQuery'] = orgM;
	 	    		    	value['orgAQuery'] = orgA;
	 	    		    	value['assessEmpQuery'] = assessEmp;
	 	    		    	value['riskNameQuery'] = riskName;
	 	    		    	datas.push(value);
	 	    				
	 	     	            var extraParams = {
	 	     	            		datas : Ext.JSON.encode(datas)
	 	     	            };
	 	     	            
	 	     	            me.comprehensiveGrid.store.proxy.extraParams = extraParams;
	 	    	        	me.comprehensiveGrid.extraParams= extraParams;
	 	     	            me.comprehensiveGrid.store.load();
	 	     	            me.comprehensiveGrid.formwindow.close();
 	                   }
 	               }
                ]
            }
        });
        
        me.callParent(arguments);
        
        me.planNameCmboBox.store.on('load',function(){
        	var planId = 'null';
        	if(me.assessPlanId != null){
        		planId = me.assessPlanId;
        	}else{
        		if(me.planNameCmboBox.store.data.items[0] != null){
        			planId = me.planNameCmboBox.store.data.items[0].data.id;
        		}
        	}
        	
        	Ext.Ajax.request({
    		    url: __ctxPath + '/assess/quaAssess/findDimColsInfos.f',
    		    params:{
    		    	assessPlanId: planId
    		    },
    		    async:  false,
    		    success: function(response){
    		        var text = response.responseText;
    		        var datas = [];
    				var value = {};
    		        array = new Array();
    		        Ext.each(Ext.JSON.decode(text).templateRelaDimensionMapList,function(r,i){
    		        	array.push({dataIndex:r.dimId, header:r.dimName, sortable : true, align: 'center', flex:.4});
    		        });
    		        
    		        me.comprehensiveGrid = 
    		        	Ext.create('FHD.view.risk.assess.comprehensiveQuery.ComprehensiveGrid',
    		        		{url:__ctxPath +'/assess/riskTidy/findRiskAdjustHistoryInfos.f', array : array});
    		        
    		        Ext.getCmp('comprehensiveMainId').add(me.comprehensiveGrid);
    		        
    		        me.planNameCmboBox.setValue(planId);
    		        
    		        
    		        value['planIdQuery'] = planId;
    		    	value['assementStatusQuery'] = '';
    		    	value['riskStatusQuery'] = '';
    		    	value['orgMQuery'] = '';
    		    	value['orgAQuery'] = '';
    		    	value['assessEmpQuery'] = '';
    		    	value['riskNameQuery'] = '';
    		    	datas.push(value);
    		    	
    		    	if(datas != null){
    		    		me.datasMe = datas;
    		    	}
    		    	
    	        	var extraParams = {
    	        			datas : Ext.JSON.encode(me.datasMe)
    	            };
    	        	
    	        	me.comprehensiveGrid.store.proxy.extraParams = extraParams;
    	        	me.comprehensiveGrid.extraParams= extraParams;
    	        	me.comprehensiveGrid.store.load();
    		        
    		    }
    		});
        	
        	
        	
        });
    }
});