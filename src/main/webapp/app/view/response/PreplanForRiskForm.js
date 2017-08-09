/**
 * 应对预案编辑表单
 */
Ext.define('FHD.view.response.PreplanForRiskForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.preplanforriskform',
    requires:[
    	'FHD.view.response.PreplanForRiskEditGrid'
    ],
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
    //添加要求
    addSolution:function(){
   	   var me = this;
   	   var preplanForm = Ext.create('FHD.view.response.PreplanForm');
//   	   me.standardcontroledit.push(standardControlEdit);
   	   me.add(preplanForm);
   	   Ext.getCmp('icm_standard_submit').setDisabled(false);
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
							Ext.getCmp('icm_standard_submit').setDisabled(true);
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
		//针对风险
		var risk = Ext.create('FHD.view.risk.cmp.RiskSelector', {
			onlyLeaf: false,
			title : '请您选择风险',
			fieldLabel : '针对风险' + '<font color=red>*</font>',
			name : 'risk',
			margin: '7 10 0 30',
			multiSelect: false,
			columnWidth : .5
		});
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
        var preplanforriskeditgrid = Ext.widget('preplanforriskeditgrid',{
        	columnWidth:1/1,
			columnLines: true,
			border:false
        });
        
       	//fieldSet
		var preplanGrid={
			xtype : 'fieldset',
			margin: '5 5 0 5',
			layout : {
				type : 'column'
			},
			collapsed: false,
			columnWidth:1/1,
			collapsible : false,
			title : '预案列表',
			items : [preplanforriskeditgrid]
		};
//        me.bbar = {
//	        items: [
//		        '->',
//		       	{
//	                name: 'icm_defect_undo_btn' ,
//		            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
//		            iconCls: 'icon-operator-home',
//	            	handler: function () {
//	           			return true;
//	            	}
//	            },{
//			    	iconCls : 'icon-add',
//			    	name : 'icm_add_standard_control_btn',
//			    	text:'新增预案',
//			    	handler :me.addSolution,
//			    	scope : this
//			    },{
//			    	iconCls : 'icon-del',
//			    	name : 'icm_del_standard_control_btn',
//			    	text:'删除预案',
//			    	handler :me.delSolution,
//			    	scope : this
//			    },
//		        {
//		            text: FHD.locale.get("fhd.common.submit"),//提交按钮
//		            iconCls: 'icon-operator-submit',
//		            id: 'icm_standard_submit',
//		            handler: function () {
//		            	
//		            }
//		        }
//		    ]
//	    };
        
        Ext.apply(me, {
        	border:false,
            items : [basicInfoFieldset, preplanGrid]
        });
       	me.callParent(arguments);
    }
});