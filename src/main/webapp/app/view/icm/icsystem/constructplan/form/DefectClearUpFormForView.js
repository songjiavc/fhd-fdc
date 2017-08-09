/**
 * 流程基本信息编辑页面
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.icm.icsystem.constructplan.form.DefectClearUpFormForView', {
	   extend: 'Ext.form.Panel',
	   alias: 'widget.defectclearupformforview',
       requires: [
    	
       ],
       frame: false,
       border : false,
       paramObj : {
       	   measureId : ""
       },
       autoScroll : false,
       initParam:function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
		},
       addComponent: function () {
	    	var me = this;
	    	//基本信息fieldset
	        me.basicinfofieldset = Ext.widget('fieldset', {
	            flex:1,
	            collapsible: false,
	            autoHeight: true,
	            autoWidth: true,
	            defaults: {
	                columnWidth : 1 / 2,
	                margin: '7 30 3 30',
	                labelWidth: 95
	            },
	            layout: {
	                type: 'column'
	            },
	            title: '缺陷'
	        });
	        // 建设计划id隐藏域
	       me.diagnosesDefectId = Ext.widget('textfield', {name: 'diagnosesDefectId',hidden : true });
	        
			// 标准名称
			me.standardName = Ext.widget('displayfield', {
	            name : 'standardName',
	            fieldLabel : '标准名称',
	            value: '',
	            columnWidth: .5
	        });
	        me.basicinfofieldset.add(me.diagnosesDefectId,me.standardName);
//			// 对应流程
//			me.processName = Ext.widget('displayfield', {
//	            name : 'processName',
//	            fieldLabel : '对应流程',
//	            value: '',
//	            columnWidth: .5
//	        });
//	        me.basicinfofieldset.add(me.processName);
			// 责任部门
			me.standardRelaOrg = Ext.widget('displayfield', {
	            name : 'standardRelaOrg',
	            fieldLabel : '责任部门',
	            value: '',
	            columnWidth: .5
	        });
	        me.basicinfofieldset.add(me.standardRelaOrg);
			// 内控要求
			me.controlRequirement = Ext.widget('displayfield', {
	            name : 'controlRequirement',
	            fieldLabel : '内控要求',
	            columnWidth: 1
	        });
	        me.basicinfofieldset.add(me.controlRequirement);
			// 诊断结果
			me.diagnosis = Ext.widget('displayfield', {
	            name : 'diagnosis',
	            fieldLabel : '诊断结果',
	            value: '',
	            columnWidth: .5
	        });
	        me.basicinfofieldset.add(me.diagnosis);
			// 诊断结果
			me.proof = Ext.widget('displayfield', {
	            name : 'proof',
	            fieldLabel : '实施证据',
	            value: '',
	            columnWidth: .5
	        });
	        me.basicinfofieldset.add(me.proof);
			// 内控要求
			me.controldesc = Ext.widget('displayfield', {
	            name : 'controldesc',
	            fieldLabel : '控制描述',
	            value: '',
	            columnWidth: .5
	        });
	        me.basicinfofieldset.add(me.controldesc);
	        /* 是否认同 */
			me.isAgree = Ext.widget('displayfield',
				{
			 	margin : '7 5 5 30',
				name:'isAgree',
				fieldLabel : '是否同意',
				columnWidth: .5,
				renderer:function(value,metaData) {
					return "<div data-qtitle='' data-qwidth=250 data-qtip='反馈节点是否同意："+value + "'>" + value + "</div>";
				}
			});
			me.basicinfofieldset.add(me.isAgree);
	        me.feedbackoptions = Ext.widget('displayfield', {
	            name : 'feedbackoptions',
	            fieldLabel : '反馈意见' ,
	            columnWidth: .5
	        });
	        me.basicinfofieldset.add(me.feedbackoptions);
	        me.add(me.basicinfofieldset);
	    },
	    // 初始化方法
       initComponent: function() {
           var me = this;
           Ext.applyIf(me);
           me.callParent(arguments);
           //向form表单中添加控件
		   me.addComponent();
       },
	   reloadData: function() {
	       var me = this;
	       me.load({
	           url: __ctxPath + '/icm/icsystem/loaddefectclearupviewformdata.f',
	           params: {
	               defectId : me.paramObj.defectId,
	               diagnosesId : me.paramObj.diagnosesId
	           },
	           success: function (form, action) {
	               return true;
	           }
	        });
	    }
});