Ext.define('FHD.view.icm.statics.AdvancedQuery',{
	extend: 'Ext.container.Container',
    alias: 'widget.icmmysearch',
	border:false,
	title:'高级查询',
    orgId:'',
    searchURL:'',
    searchTitle:'',
    searchObject:[
    	{
    		id:'standard',
    		url:'FHD.view.icm.statics.IcmMyStandardInfo',
    		title:'标准',
    		context:'要求编号, 要求内容, 责任部门, 内控标准, 控制层级, 内控要素, 处理状态, 更新日期'
    	},
    	{
    		id:'target',
    		url:'',
    		title:'目标',
    		context:'暂无'
    	},
    	{
    		id:'process',
    		url:'FHD.view.icm.statics.IcmMyProcessInfo',
    		title:'流程',
    		context:'流程编号, 流程名称, 流程分类, 发生频率, 责任部门, 责任人, 更新日期'
    	},
    	{
    		id:'kpi',
    		url:'',
    		title:'指标',
    		context:'暂无'
    	},
    	{
    		id:'defect',
    		url:'FHD.view.icm.statics.IcmMyDefectInfo',
    		title:'缺陷',
    		context:'缺陷描述, 缺陷等级, 缺陷类型, 整改状态, 整改责任部门, 更新日期'
    	},
    	{
    		id:'assessresult',
    		url:'FHD.view.icm.statics.AssessResultStaticsGrid',
    		title:'评价结果',
    		context:'评价计划, 评价人, 流程, 评价方式, 流程节点, 控制措施, 评价点, 样本有效状态 , 评价完成状态, 更新日期'
    	},
    	{
    		id:'controlMeasure',
    		url:'',
    		title:'控制措施',
    		context:'暂无'
    	}
    ],
    // 初始化方法
	initComponent: function() {
		var me = this;
		Ext.apply(me, {
     	    layout: {
		        type: 'vbox',
		        align: 'stretch'
		    }
        });
        var searchObjectContainer = Ext.create('Ext.container.Container',{
        	layout: {
		        type: 'hbox',
		        align: 'middle'
		    },
		    margin:'0 0 0 70 '
        })
        Ext.each(me.searchObject,function(item,index){
        	searchObjectContainer.add(
        		{
			        xtype: 'label',
			        html:"<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('"+me.id+"').searchObjectChange('"+item.id+"')\">"+item.title+"</a>",
			        margin: '0 20 0 0 '
			    }
        	)
        });
        me.callParent(arguments);
        me.add(searchObjectContainer);
        me.add(Ext.create('Ext.container.Container',{
			layout: {
		        type: 'hbox',
		        align: 'stretch'
		    },
		    margin:'10 0 10 0',
			items:[{
		        xtype: 'label',
		        forId: 'searchObject',
		        text: "请选择",
		        margin:'5 0 0 0',
		        width:70
		    },
			{
		        xtype: 'textfield',
		        name:'searchText',
		        hideLabel: true,
		        height:30,
		        width: 480
		    }, 
		    {
		    	xtype: 'button',
		    	margin:'0 190 0 10',
		    	text: '查询',
		    	handler:me.searchObjectQuery,
		    	scope:this,
		    	width:80
		    }
			/*{
		        xtype: 'label',
		        html: "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('"+me.id+"').searchObjectQuery()\">查询</a>",
		        margin:'0 190 0 10',
		        width:80
		    }*/]
		}));
		me.add(Ext.create('Ext.container.Container',{
			layout: {
		        type: 'hbox',
		        align: 'bottom'
		    },
			margin:'0 0 0 0',
			items:[
			    {
			        xtype: 'label',
			        text:"查询范围:",
			        width: 70
			    },
			    {
			        xtype: 'label',
			        forId: 'searchRange',
			        text:"无"
			    }
			]
		}));
	},
	searchObjectChange:function(key){
    	var me = this;
		Ext.each(me.searchObject,function(item,index){
			if(key == item.id){
				me.searchURL = item.url;
				me.searchTitle = item.title;
    			me.down('label[forId=searchObject]').setText(item.title);
    			me.down('label[forId=searchRange]').setText(item.context);
			}
        });
    },
	searchObjectQuery:function(){
		var me = this;
		if(!me.searchURL){
			return;
		}
		if(!me.searchTitle){
			return;
		}
		var searchText = me.down('[name=searchText]').getValue();
		var centerPanel = Ext.getCmp('center-panel');
		var tab = centerPanel.getComponent(me.searchURL);
		if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			if(me.searchURL.startWith('FHD')){
				var p = centerPanel.add(Ext.create(me.searchURL,{
					id:me.searchURL,
					title: me.searchTitle,
					tabTip:me.searchTitle,
					closable:true
				}));
				p.initParam({orgId:me.orgId,query:searchText});
				centerPanel.setActiveTab(p);
			}
		}
	}
});