/**
 *    @description 内控标准FORM显示用 
 *    
 *    @author 元杰
 *    @since 2013-3-5
 */
Ext.define('FHD.view.icm.standard.form.StandardControlPlanPreview', {
	extend : 'Ext.form.Panel',
	alias : 'widget.standardcontrolplanpreview',
	items:[],
	frame: false,
    border : false,
	layout : {
		type : 'column'
	},
	defaults : {
		columnWidth : 1/1
	},
	bodyPadding:'0 3 3 3',
	autoScroll : true,
	collapsed : false,
	//传递的参数对象
	paramObj:{
		standardControlId : ''
	},
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},
	reloadData: function() {
       var me = this;
       me.load({
           waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
           url: __ctxPath + '/icm/standard/findstandardControlById.f',
           params: {
               standardControlId: me.paramObj.standardControlId
           },
           success: function (form, action) {
               	return true;
           }
        });
    },
	initComponent :function() {
	  var me = this;
	  
	  //ID
	  me.sandardId = {
			xtype : 'hidden',
			name : 'cid'
	  };
	  //父级
	  me.parent = {
			labelWidth :80,
			xtype : 'displayfield',
			disabled : false,
			lblAlign:'rigth',
			fieldLabel : '分　　类',
			value : '',
			columnWidth : .5,
			name : 'cparent',
			margin: '7 10 10 30',
			maxLength : 200
		};
	  //内部控制编号
	  me.code = {
			labelWidth :80,
			xtype : 'displayfield',
			disabled : false,
			lblAlign:'rigth',
			fieldLabel : '编　　号 ',
			value : '',
			columnWidth : .5,
			name : 'ccode',
			margin: '7 10 10 30',
			maxLength : 200
		};
	  //内部控制要求
	  me.standardName = {
			labelWidth :80,
			xtype : 'displayfield',
			disabled : false,
			lblAlign:'rigth',
			fieldLabel : '内部控制要求 ',
			value : '',
			columnWidth : 1,
			name : 'cname',
			margin: '7 10 10 30',
			maxLength : 200,
			allowBlank : false
		};
       
		/*是-适用范围  */
		me.standardSubCompany= {
			xtype : 'displayfield',
         	fieldLabel : '适用范围 ',
         	disabled : false,
         	name:'csubCompanyName',
         	margin: '7 10 10 30',
         	type : 'dept',
         	labelWidth : 80,
            multiSelect : false
         };
		
        /*否-责任部门  */
		me.standardDepart={
			xtype : 'displayfield',
         	fieldLabel : '责任部门 ',
         	name:'cdeptName',
         	disabled : false,
         	margin: '7 10 10 30',
         	type : 'dept',
         	labelWidth : 80,
            multiSelect : false
         };
         //是否适用于下级单位Radio 此处只用来保存反馈意见
		me.inferiorRadio = {
			xtype:'hidden',
			value :'adviceOnly',
			name : 'inferior'
		};
		/*控制层级*/
		me.controlLevel= {
			xtype : 'displayfield',
         	fieldLabel : '控制层级 ',
         	name:'ccontrolLevel',
         	disabled : false,
         	margin: '7 10 10 30',
         	labelWidth : 80,
            multiSelect : false
         };
       	/*对应流程  */
		me.standardRelaProcess= {
			xtype : 'displayfield',
         	fieldLabel : '对应流程 ',
         	name:'cprocessName',
         	disabled : false,
         	margin: '7 10 10 30',
         	labelWidth : 80,
            multiSelect : false
         };
		
        /*内控要素 */
		me.standardControlPoint={
			xtype : 'displayfield',
		     name:'cstandardControlPoint',
		     margin: '7 10 10 30',
		     labelWidth : 80,
		     multiSelect:false,
		     disabled : false,
		     fieldLabel : '内控要素 '
	     };
	     //处理状态
	 	 me.dealStatus = {
			labelWidth :80,
			xtype : 'displayfield',
			disabled : false,
			lblAlign:'rigth',
			fieldLabel : '处理状态 ',
			value : '',
			columnWidth : .5,
			name : 'cdealStatus',
			margin: '7 10 10 30',
			maxLength : 200,
			renderer : function(value, metaData, record, colIndex, store, view) { 
				if(value == 'N'){
					return "未开始";
				}else if(value == 'H'){
					return "处理中";
				}else if(value == 'U'){
					return "待更新";
				}else if(value == 'O'){
					return "已纳入内控手册运转";
				}else if(value == 'F'){
					return "已完成";
				}
				return value;
			}
		};
	     
		me.items= [{
			xtype : 'fieldset',
			defaults : {
				columnWidth : 1/2
			},//每行显示一列，可设置多列
			layout : {
				type : 'column'
			},
			collapsed : false,
			margin: '8 -9 0 7',
			collapsible : true,
			title : '要求',
			items:[me.sandardId,me.parent, me.code, me.standardName, me.standardSubCompany,me.inferiorRadio,
					me.standardDepart,me.controlLevel, me.standardRelaProcess,
					me.standardControlPoint, me.dealStatus]
            }];
            
		Ext.applyIf(me,{
			items:me.items
		});
		me.callParent(arguments);
		}
	});