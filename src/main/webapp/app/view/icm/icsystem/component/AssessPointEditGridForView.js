/*
 * 评价产生的缺陷的可编辑列表 
 * */

Ext.define('FHD.view.icm.icsystem.component.AssessPointEditGridForView',{
	extend:'FHD.ux.GridPanel',
	alias: 'widget.assesspointeditgridforview',
	url: __ctxPath + '/assess/findassesspointlistbysome.f',
	region:'center',
	objectType:{},
	storeAutoLoad:false,
	pagable : false,
	searchable:false,
	border : false,
	sortableColumns : false,
	layout: 'fit',
	readOnly : false,
	checked : false,
	initParam:function(paramObj){
		var me = this;
		me.paramObj = paramObj;
	},
	initComponent:function(){
		var me=this;
		Ext.apply(me,{
			extraParams:{
				processPointId : me.processPointId,
				measureId : me.measureId,
				processId : me.processId,
				type : me.type
			}
		});
		me.cols=[ 
				  {header:'评价点',dataIndex:'assessDesc',flex:1},
			      {header:'实施证据', dataIndex: 'comment',flex:1}
			      ];
		me.callParent(arguments);
	},
	reloadData :function(){
		var me = this;
        me.store.proxy.extraParams = me.paramObj;
		me.store.load();
	}
});