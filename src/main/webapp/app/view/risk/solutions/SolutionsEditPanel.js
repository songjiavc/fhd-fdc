/**
 * 应对措施编辑页面
 */
Ext.define('FHD.view.risk.solutions.SolutionsEditPanel',{
    extend : 'Ext.form.Panel',
    alias: 'widget.solutionseditpanel',
    layout : {
                type: 'vbox',
                align : 'center'
            },
    preplanId : '',//预案ID，新增的时候需要跟关联表保存
    solutionsId : '',//应对方案ID，修改的时候跟句ID去修改应急方案信息
    iseffective : '',
    border : true,
	autoHeight: true,
	kpiItems : [],
	initComponent: function() {
		var me = this;
		
		var name =  Ext.widget('textfield', {
			name : 'name', 
			fieldLabel: "措施名称"+ '<font color=red>*</font>',
			margin: '7 10 0 20',
			width:500,
			allowBlank:false
		});
		
//		var sysposi =  Ext.widget('textfield', {
//			name : 'sysposi', 
//			fieldLabel: "责任岗位"+ '<font color=red>*</font>',
//			margin: '7 10 0 20',
//			value: me.kpiname,
//			width:500,
//			allowBlank:false
//		});
		
		var desc = Ext.widget('textareafield', {
	            fieldLabel:'工作内容'+ '<font color=red>*</font>',
	            labelAlign : 'left',
	            margin: '7 10 0 20',
	            row : 5,
	            width: 600,
	            name : 'desc',
	            labelWidth : 100,
	            allowBlank:false
	        });
	        
		var type=Ext.create('FHD.ux.dict.DictSelect',{
	 			name:'type',
	 		    dictTypeId:'ic_control_level',
	 		    margin: '7 10 0 20',
	 		    labelWidth : 100,
	 		    labelAlign : 'left',
	 		    value:'',
	 		    multiSelect:false,
	 		    fieldLabel : '工作类别'+ '<font color=red>*</font>',
	 		    allowBlank:false
	 	    });
		
		var finishDate = Ext.widget('datefield', {
            xtype: 'datefield',
            format: 'Y-m-d',
            name: 'planFinishDate',
            margin: '7 10 0 20',
            fieldLabel: '计划完成时间'+ '<font color=red>*</font>', //开始日期
            width:300,
            editable : false,
            allowBlank: false
        });
	    
	    var estimatedResult =  Ext.widget('textfield', {
			name : 'estimatedResult', 
			fieldLabel: "预计收效"+ '<font color=red>*</font>',
			margin: '7 10 0 20',
			width:500,
			allowBlank:false
		});
		
		var estimatedCost =  Ext.widget('textfield', {
			name : 'estimatedCost', 
			fieldLabel: "预计成本"+ '<font color=red>*</font>',
			margin: '7 10 0 20',
			width:500,
			allowBlank:false
		});
	    
//        var responsibleDept = Ext.create('FHD.ux.org.CommonSelector', {
//        	margin: '7 10 0 20',
//            fieldLabel: '责任人', //采集部门
//            labelAlign: 'left',
//            width: 400,
//            name: 'gatherDept',
//            multiSelect: false,
//            type: 'dept_emp',
//            labelWidth: 95
//        });
//        var delQuantification = Ext.widget('label',{
//        	width: 40,
//        	margin: '7 10 0 600',
//			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").delDl()'>删除</a>"
//        });
//        
//        var responsibleDeptCon = Ext.create('Ext.container.Container', {
////           width:1000,
//           layout:{
//     	    	type:'hbox'  
//     	   },
//           items : [responsibleDept,delQuantification]
//        });
//        
//		var effectType= Ext.create('Ext.form.RadioGroup',{
//		 fieldLabel: "预计收效",
//		 width: 400,
//		 margin: '7 10 0 20',
//	     vertical: true,
//         items: [
//            { boxLabel: "很高", name: 'effectType', inputValue: '0',checked:true},
//            { boxLabel: "高", name: 'effectType', inputValue: '1'},
//            { boxLabel: "一般", name: 'effectType', inputValue: '2'},
//            { boxLabel: "低", name: 'effectType', inputValue: '3'},
//            { boxLabel: "很低", name: 'effectType', inputValue: '4'}]
//        });
//		
//		var addQuantification = Ext.widget('label',{
//        	margin: '7 10 0 295',
//			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addDl()'>增加</a>"
//        });
//        
//        var schemeFieldset = Ext.create('Ext.container.Container', {
//           layout:{
//     	    	type:'hbox'  
//     	    },
//           items : [effectType,addQuantification]
//        });
//		
//		me.kpiFieldSet =  Ext.create('Ext.container.Container', {
//           margin: '7 10 0 20',
//           items : [kpifieldarr]
//        });
//        var kpiEtc =  Ext.widget('textfield', {
//			name : 'kpi', 
//			fieldLabel: "其它",
//			margin: '0 10 0 125',
//			value: me.kpiname,
//			width:500,
//			colspan : 5
//		});
//		
//		
//		var actualCost= Ext.create('Ext.form.RadioGroup',{
//		 fieldLabel: "实际成本",
//		 width: 400,
//		 margin: '7 10 0 20',
//	     vertical: true,
//         items: [
//            { boxLabel: "很高", name: 'actualCost', inputValue: '0',checked:true},
//            { boxLabel: "高", name: 'actualCost', inputValue: '1'},
//            { boxLabel: "一般", name: 'actualCost', inputValue: '2'},
//            { boxLabel: "低", name: 'actualCost', inputValue: '3'},
//            { boxLabel: "很低", name: 'actualCost', inputValue: '4'}]
//        });
//		
//		var cost = Ext.widget('textfield', {
//            xtype: 'textfield',
//            fieldLabel: '直接经济成本',
//            margin: '7 10 0 120',
//            value: '',
//            maxLength: 255,
//            width: 500
//        });
//        var unit ={
//        	 xtype:'displayfield',
//        	 margin: '7 10 0 20',
//    		 width:40,
//    	     vertical: true,
//    	     value:'万元'
//        };
//        
//        var costCon=Ext.create('Ext.container.Container',{
//        	margin: '20 10 0 0',
//     	    layout:{
//     	    	type:'column'  
//     	    },
//     	    width:1000,
//     	    items:[cost,unit]
//        });
//        var costEtc = Ext.widget('textfield', {
//            xtype: 'textfield',
//            fieldLabel: '其它',
//            margin: '7 10 0 120',
//            value: '',
//            maxLength: 255,
//            width: 500
//        });
//        var costCon=Ext.create('Ext.container.Container',{
//     	    layout:{
//     	    	type:'vbox'  
//     	    },
//     	    width: 1000,HG
//     	    items:[actualCost,costCon,costEtc]
//        });
		var subbutton = Ext.widget('button', {
	        text: FHD.locale.get('fhd.common.save'),
	        width : 100,
	        handler: function () {
	            me.save();
	        }
	    });
		var celbutton = Ext.widget('button', {
	        text: FHD.locale.get('fhd.common.cancel'),
	        width : 100,
	        handler: function () {
	            me.cancel();
	        }
	    });
        Ext.apply(me, {
        	autoScroll: true,
            items: [ {
                    xtype : 'fieldset',
                    collapsed : false,
                    collapsible : false,
                    title : '应对措施',
                    items : [ 
                    	name,type,finishDate,estimatedResult,estimatedCost,desc
                    		]
                },{
                    	xtype : 'fieldcontainer',
                    	layout: {
                    		type: 'column'
                         },
                         border : false,
                         items : [subbutton,celbutton]
                    }
            ]
        });
        me.callParent(arguments);
    },
    	
    save : function(){//保存方法
    	var me = this;
        var form = me.getForm();
        console.log(me);
        if (form.isValid()) {
    		if(me.isAdd){//新增保存
    			FHD.submit({
					form : form,
					url : 'chf/solutions/savesolutions.f' + '?preplanId=' + me.preplanId + '&iseffective=' + me.iseffective,
					callback : function(data) {
						me.iseffective = '0';
						me.preplanId = data.id;
						me.up('window').close();
					}
					});
    		}else{
    			FHD.submit({
    				form : form,
    				url : 'chf/solutions/savesolutions.f' + '?solutionsId=' + me.solutionsId + '&preplanId=' + me.preplanId + '&iseffective=' + me.iseffective,
    				callback:function(data){
    					me.iseffective = '0';
						me.preplanId = data.id;
    					me.up('window').close();
    				}
    			});	
    		}
    	}
        
	},
	
	cancel : function(){//取消方法
		var me = this;
    	me.up('window').close();
	},
	
	editLoadForm : function(){
		var me = this;
		if(typeof(me.solutionsId) != 'undefined') {
    		me.form.load({
    	        url:'chf/solutions/editloadform',
    	        params:{solutionsId:me.solutionsId},
    	        failure:function(form,action) {
    	            alert("err 155");
    	        },
    	        success:function(form,action){
    	        }
    	    });
    	}
	}
 });