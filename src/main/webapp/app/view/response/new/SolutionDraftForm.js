/**
 * 应对方案编辑表单
 */
Ext.define('FHD.view.response.new.SolutionDraftForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.solutiondraftform',
    requires:['FHD.view.response.SolutionViewForm'],
    
    autoScroll: true,
    solutionForm : [],
    selectArray : [],
    loadData : function(id){
    	var me = this;
		me.load({
	        url:'',
	        params:{},
	        failure:function(form,action) {
	            alert("加载数据失败");
	        },
	        success:function(form,action){
	        	var formValue = form.getValues();
	        }
	    });
    },
    addSolutionFromDatabase:function(){
		var solutionviewform = Ext.widget('solutionviewform');
		var win = Ext.create('FHD.ux.Window',{
			title:'应对方案详细信息',
			//modal:true,//是否模态窗口
			collapsible:false,
			maximizable:true//（是否增加最大化，默认没有）
    	}).show();
    	win.add(solutionviewform);
    },
    //添加要求
    addSolution:function(){
   	   var me = this;
   	   var solutionForm = Ext.create('FHD.view.response.new.SolutionForm');
//   	   me.standardcontroledit.push(standardControlEdit);
   	   me.add(solutionForm);
   	   Ext.getCmp('icm_standard_submit1').setDisabled(false);
	},
	//删除要求
    delSolution:function(){
		var me = this;
		if(me.selectArray.length > 0){
			Ext.MessageBox.show({
				title : FHD.locale.get('fhd.common.delete'),
				width : 260,
				msg : FHD.locale.get('fhd.common.makeSureDelete'),
				buttons : Ext.MessageBox.YESNO,
				icon : Ext.MessageBox.QUESTION,
				fn : function(btn) {
					if (btn == 'yes') {// 确认删除
						for(var i = 0;i<me.selectArray.length;i++){
//							Ext.getCmp(me.selectArray[i]).standardName.value = '0';
//							Ext.getCmp(me.selectArray[i]).standardDepart.value = '0';
//							Ext.getCmp(me.selectArray[i]).standardSubCompany.value = '0';
//							Ext.getCmp(me.selectArray[i]).standardControlPoint.value = '0';
							Ext.Array.remove(me.solutionForm,Ext.getCmp(me.selectArray[i]));
							me.remove(me.selectArray[i]);
						}
						me.selectArray.length = 0;
						if(me.solutionForm.length == 0){
							Ext.getCmp('icm_standard_submit1').setDisabled(true);
						}
					}else{
						return false;
					}
				}
	    	});
		}else{
			Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '请选择要删除的要求!');
		}
	},
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        me.flowtaskbar=Ext.widget('panel',{
        	title: "风险应对方案-方案复合",
            region:'north',
            collapsed:true,
            collapsible: true,
            maxHeight:200,
            split: true,
            border: false,
        	items:[
	        	Ext.widget('flowtaskbar',{
	    		jsonArray:[
		    		{index: 1, context:'1.方案制定',status:'done'},
		    		{index: 2, context:'2.方案审批',status:'done'},
		    		{index: 3, context:'3.方案执行',status:'done'},
		    		{index: 4, context:'4.方案复合',status:'current'}
		    	]
	    		})
        	]
        });
        
        //风险
        var risk={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '对应风险',
		    value:'供应商订货起点与生产需求相差较大',
		    columnWidth: .5
		};
        //风险分类
        var riskClass={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '风险分类',
		    value:'供应商管理风险',
		    columnWidth: .5
		};
        //风险code
        var riskCode={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '风险编号',
		    value:'201308098',
		    columnWidth: .5
		};
		//责任部门
        var orgId={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '责任部门',
		    value:'管理创新部',
		    columnWidth: .5
		};
		//协助部门
        var orgOra={
        	margin: '7 10 10 30',
		    xtype:'displayfield',
		    fieldLabel: '协助部门',
		    value:'管理创新部',
		    columnWidth: .5
		};
		//基础信息fieldset
        var basicInfoFieldset = {
            xtype:'fieldset',
            title: '基础信息',
            collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '5 5 0 5',
            	columnWidth: .5
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [risk, riskCode, riskClass, orgId, orgOra]
        };
        
        me.bbar = {
	        items: [
		        '->',
		       	{
	                name: 'icm_defect_undo_btn' ,
		            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
		            iconCls: 'icon-operator-home',
	            	handler: function () {
	            		var solutioneditpanel = me.up('solutioneditpanel');
    					solutioneditpanel.setActiveItem(solutioneditpanel.solutionlist);
	           			return true;
	            	}
//	            },{
//			    	iconCls : 'icon-add',
//			    	name : 'icm_add_standard_control_btn',
//			    	text:'选择已有方案',
//			    	handler :me.addSolutionFromDatabase,
//			    	scope : this
			    },
//		        {
//		            text: FHD.locale.get("fhd.common.save"),//保存按钮
//		            iconCls: 'icon-control-stop-blue',
//		            handler: function () {
//		            	var constructplancardpanel = this.up('constructplancardpanel');
//		            	if(constructplancardpanel){
//		            		constructplancardpanel.finish();
//		            	}
//		            }
//		        }, 
		        {
		            text: FHD.locale.get("fhd.common.submit"),//提交按钮
		            iconCls: 'icon-operator-submit',
		            id: 'icm_standard_submit1',
		            handler: function () {
		            	
		            }
		        }
		    ]
	    };
        
	   var preplanList = Ext.create('FHD.view.response.new.PreplanList');
	    
	    //预案列表fieldset
        var preplanFieldset = {
            xtype:'fieldset',
            title: '预案列表',
            collapsible: true,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            defaults : {
            	margin: '5 5 0 5',
            	columnWidth: 1
            },
            layout: {
     	        type: 'column'
     	    },
     	    items : [preplanList]
        };
	    
        Ext.apply(me, {
        	border:false,
            items : [Ext.create('FHD.view.response.new.SolutionForm')]
        });
       	me.callParent(arguments);
    }
});