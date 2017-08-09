Ext.define('FHD.view.sys.documentlib.documentWindow.DocumentSelectWindow', {
    extend: 'Ext.window.Window',
	alias: 'widget.documentSelectWindow',
    
    height: 500,
    width: 1000,
    layout: {
        type: 'border'
    },
    title: '请选择文档',
	modal: true,
    maximizable: true,
    
    //接口
    lefttree:null,
    columns:[{dataIndex: 'code',header: '编号'},{dataIndex: 'name',header: '名称',isPrimaryName:true}],
    multiSelect:true,

    //内部组件
    candidategrid:null,
    selectedgrid:null,
    
    initComponent: function() {
        var me = this;
        
        me.candidateColumns = [
        	{
				header: "id",
				dataIndex:'id',
				hidden:true
			},{
	            header: "文件名称",
	            dataIndex: 'documentName',
	            sortable: true,
	            flex: 1
	        },{
	            header: "创建时间",
	            dataIndex: 'createTimeStr',
	            sortable: false,
	            width:40,
	            flex:1
	        }];   //待选列表项
        me.gridColumns = [{
				header: "id",
				dataIndex:'id',
				hidden:true
			},{
	            header: "文件名称",
	            dataIndex: 'documentName',
	            sortable: true,
	            flex: 1
	        },{
	            header: "创建时间",
	            dataIndex: 'createTimeStr',
	            sortable: false,
	            width:40,
	            flex:1
	        }];		//列表项
        me.modelFields = ['id','documentName','createTimeStr'];	//列表实体定义

        me.gridColumns.push({
        	xtype:'templatecolumn',
        	tpl:'<font class="icon-del-min" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>',
        	width:70,
        	listeners:{
        		click:{
        			fn:function(g,d,i){
        				g.store.removeAt(i);
        				//更新已选择列表选中的数量
        	    		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
        			}
        		}
        	}
        });
        
        //2. 构建grid的model
        Ext.define('CandidateRecord', {
            extend: 'Ext.data.Model',
            fields: me.modelFields
        });
        
		var candidategridStore = Ext.create('Ext.data.Store',{
        	pageSize: 100000,
        	idProperty: 'id',
        	fields:me.modelFields,
        	proxy: {
		        type: 'ajax',
		        reader: {
		            type : 'json',
		            root : 'datas',
		            totalProperty :'totalCount'
		        }
		    }
        	
        });
        
        var selectgridStore = Ext.create('Ext.data.Store', {
			idProperty: 'id',
			fields:me.modelFields
		});
        
        //3. 构建树
        me.lefttree = Ext.create('FHD.view.sys.documentlib.documentWindow.DocumentSelectTree',{
        	face:me,
        	split : true,
        	region:'west'
        });
        
        //待选择显示选中记录的数量
        me.candidatelabel = Ext.create("Ext.form.Label",{
        	text:'(0)',
        	padding: '0 0 2 0'
        });
        me.candidategrid = Ext.create('Ext.grid.Panel',{
            flex: 1,
            loadMask: true,
            store:candidategridStore,
            columns: me.candidateColumns,
            //selModel:Ext.create('Ext.selection.CheckboxModel'),
		    tbar : [
				'<b>待选择</b>',me.candidatelabel,'-',
				Ext.create('Ext.button.Button', {
					text : '全部选择',
					handler : function() {
		        		//添加全部
						me.selectedgrid.store.insert(0,me.candidategrid.store.getRange(0,me.candidategrid.store.getTotalCount()-1));
						me.selectedgrid.store.loadRecords(me.selectedgrid.store.getRange());
			    		//更新已选择列表选中的数量
			    		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
					}
				}),
				Ext.create('Ext.button.Button', {
					text : '全部取消',
					handler : function() {
						//取消全部
						me.selectedgrid.store.remove(me.candidategrid.store.getRange(0,me.candidategrid.store.getTotalCount()-1));
        				//更新已选择列表选中的数量
        	    		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
					}
				}),
				'->',
				Ext.create('Ext.ux.form.SearchField', {
					width : 150,
					paramName : 'query',
					store : candidategridStore,
					emptyText : FHD.locale
							.get('searchField.emptyText')
				})]
        });
        //显示选中记录的数量
        me.label = Ext.create("Ext.form.Label",{
        	text:'(0)',
        	padding: '0 0 2 0'
        });
        me.selectedgrid = Ext.create('Ext.grid.Panel',{
            flex: 1,
            store:selectgridStore,
            columns: me.gridColumns,
            tbar:['<b>已选择</b>',me.label]
        });
        Ext.applyIf(me, {
            items: [me.lefttree,
                {
                    xtype: 'container',
                    activeItem: 0,
                    layout: {
                        type: 'border'
                    },
                    region: 'center',
                    items: [
                        {
                            xtype: 'container',
                            layout: {
                                align: 'stretch',
                                type: 'vbox'
                            },
                            region: 'center',
                            items: [me.candidategrid,me.selectedgrid]
                        }
                    ]
                }
            ]
        });
        me.buttonAlign = 'center';
        me.buttons = [
        	{
	            xtype: 'button',
	            text: $locale('fhd.common.confirm'),
	            width:70,
	            style: {
	            	marginRight: '10px'    	
	            },
	            handler:function(){
	            	me.onSubmit(me);
	            	me.close();
	            }
	        },
	        {
	            xtype: 'button',
	            text: $locale('fhd.common.close'),
	            width:70,
	            style: {
	            	marginLeft: '10px'    	
	            },
	            handler:function(){
	            	me.close();
	            }
	        }
	    ];
        me.callParent(arguments); 

        // 待选择列表
        me.candidategrid.on('select',function(t,node,i,o){
    		me.rid = node.data.id;
        	if(Ext.isEmpty(me.selectedgrid.store.getById(node.data.id))){
        		if(!me.multiSelect) {
        			me.selectedgrid.store.removeAll();
        		}
        		//3. 构建一条记录
	    	    var recordInstance = {};
        		for(var key in me.modelFields){
	    	    	recordInstance[me.modelFields[key]] = node.data[me.modelFields[key]];
	    	    }
        		var candidateRecord = new CandidateRecord(recordInstance);
	    		me.selectedgrid.store.insert(0,candidateRecord);
	    		//更新已选择列表选中的数量
	    		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
        	}
        });

    },
    setValue:function(selecteds){
    	var me = this;
    	Ext.each(selecteds,function(selected){
        	me.selectedgrid.store.insert(0,selected);
        });
    	//更新已选择列表选中的数量
		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
    },
    onSubmit:Ext.emptyFn()
});
