/**
 * 流程基本信息编辑页面
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.icm.icsystem.form.NoteEditFormForView', {
	extend: 'Ext.form.Panel',
	alias: 'widget.noteeditformforview',
	requires: [
		'FHD.view.icm.icsystem.component.ParentNoteEditGridForView',
		'FHD.view.icm.icsystem.component.AssessPointEditGridForView'
	],
	frame: false,
	border : false,
	paramObj : {
		processId : "",
		processPointId : ""
	},
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},
	autoScroll : true,
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
            title: '节点信息'
        });
        me.add(me.basicinfofieldset);
        //维护上级节点  暂时注释
//        me.parentNotefieldset = Ext.widget('fieldset', {
//            flex:1,
//            collapsible: true,
//            autoHeight: true,
//            autoWidth: true,
//            defaults: {
//                columnWidth : 1,
//                labelWidth: 95
//            },
//            layout: {
//                type: 'column'
//            },
//            title: '维护上级节点',
//            collapsed : false
//        });
//        me.parentnoteeditgridforview =  Ext.widget('parentnoteeditgridforview',{
//		    	processId : me.paramObj.processId,
//				processPointId : me.processPointId,
//				checked : false
//		    });    //将展示父节点组件创建
//        me.parentNotefieldset.add(me.parentnoteeditgridforview);
//        me.add(me.parentNotefieldset);
        //维护评价店
        me.assesspointfieldset = Ext.widget('fieldset', {
            flex:1,
            autoHeight: true,
            collapsible: false,
            autoWidth: true,
            defaults: {
                columnWidth : 1,
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            title: '评价点信息',
            collapsed : false
        });
        me.add(me.assesspointfieldset);
        me.assesspointeditgrid =  Ext.widget('assesspointeditgridforview');    //将展示父节点组件创建
        me.assesspointfieldset.add(me.assesspointeditgrid);
        me.code = Ext.widget('displayfield', {
            name : 'code',
            fieldLabel : '节点编号'
        });
		me.basicinfofieldset.add(me.code);
		//名称
		me.notename = Ext.widget('displayfield', {
			fieldLabel : '节点名称',
			name : 'name'
		});
        me.basicinfofieldset.add(me.notename);
        /*节点类型*/
		me.notestyle = Ext.widget('displayfield',{
			name : 'pointType',
			fieldLabel : '节点类型'
		});
        /*流程接口*/
		me.flowinterface = Ext.create('FHD.ux.process.ProcessSelector', {
			name : 'relaProcess',
			multiSelect : false,
	        fieldLabel: '选择流程'
		});
        /*责任部门  */
		me.noteDepart = Ext.widget('displayfield',{
			fieldLabel : '责任部门',
			name:'orgName'
		});
        me.basicinfofieldset.add(me.noteDepart);
        /*员工单选 */
		me.noteradio = Ext.widget('displayfield',{
			fieldLabel : '责&nbsp;&nbsp;任&nbsp;人',
			name:'empName'
		});
        me.basicinfofieldset.add(me.noteradio);
         /*配合部门  */
		me.noteCrdDepart = Ext.widget('displayfield',{
			fieldLabel : '配合部门',      
			name:'CrdorgName'
		});
		me.basicinfofieldset.add(me.noteCrdDepart);
		//排序
		me.notesort = Ext.widget('displayfield',{
			fieldLabel : '排&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;序',
			name : 'sort'
		});
		me.basicinfofieldset.add(me.notesort);
		//节点描述
        me.desc = Ext.widget('displayfield', {
			fieldLabel : '节点描述',
			name : 'desc',
			columnWidth : 1
        });
        me.basicinfofieldset.add(me.desc);
        //信息 输入 
        me.infoInput = Ext.widget('displayfield', {
			fieldLabel : '信息输入',
			columnWidth : 1,
			name : 'infoInput'
        });
        me.basicinfofieldset.add(me.infoInput);
		//信息输出 
        me.infoOutput = Ext.widget('displayfield', {
			fieldLabel : '信息输出',
			columnWidth : 1,
			name : 'infoOutput'
        });
        me.basicinfofieldset.add(me.infoOutput);
        me.riskId =  Ext.widget('displayfield', {
			fieldLabel : '风险事件',
			name : 'riskId',
			columnWidth : 1
        });
		me.basicinfofieldset.add(me.riskId);
	},
	reloadData: function() {
		var me = this;
		me.load({
			url: __ctxPath + '/ProcessPoint/ProcessPoint/editProcessPointforview.f',
			params: {
				processEditID: me.paramObj.processPointId
			},
			success: function (form, action) {
			return true;
			}
		});
		me.assesspointeditgrid.initParam({
	    	processId: me.paramObj.processId,
            processPointId: me.paramObj.processPointId
	    });
	    me.assesspointeditgrid.reloadData();
	},
	// 初始化方法
	initComponent: function() {
		var me = this;
		Ext.applyIf(me);
		me.callParent(arguments);
		//向form表单中添加控件
		me.addComponent();
	}
});