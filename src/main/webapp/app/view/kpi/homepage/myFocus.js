Ext.define('FHD.view.kpi.homepage.myFocus', {

    extend: 'Ext.container.Container',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    queryUrl: '',

    chartType: '',

    chartHeight: '',

    chartWidth: '',

    pcontainer: null,

    searchContent: '',

    objectType: '',
    
    preImg:__ctxPath + '/images/icons/pre-arrow.png',
    
    preDisableImg:__ctxPath + '/images/icons/pre-arrow-disable.png',
    
    nextImg :__ctxPath + '/images/icons/next-arrow.png',
    
    nextDisableImg:__ctxPath + '/images/icons/next-arrow-disable.png',
    
    reload:function(){
		var me = this;
		me.centerpanel.initChartList(1,me.centerpanel.limit);
		me.setBtnStatus(me.centerpanel.hiddenStart,me.centerpanel.isNext);
    },
    query:function(queryname){
    	var me = this;
    	me.centerpanel.query = queryname;
    	me.centerpanel.initChartList(1,me.centerpanel.limit);
    	me.centerpanel.query = '';
    },
	setBtnStatus:function (start,isNext){
	 		var me=this;
	 		//判断上一页
	    	if(1 == start){
	    		me.preBtn.getEl().dom.src = me.preDisableImg;
	    	}else{
	        	me.preBtn.getEl().dom.src = me.preImg;
	    	}
	    	//判断下一页
	    	if(isNext){
	    		me.nextBtn.getEl().dom.src = me.nextImg;
	    	}else{
	    		me.nextBtn.getEl().dom.src = me.nextDisableImg;
	    	}
	 	},
    // 初始化方法
    initComponent: function () {
        var me = this;
        me.centerpanel = Ext.create('FHD.view.kpi.homepage.myFocusChart', {
            objectType: me.objectType,
            chartType: me.chartType,
            queryUrl: me.queryUrl,
            searchContent: me.searchContent,
            chartHeight: me.chartHeight,
            chartWidth: me.chartWidth,
            pcontainer: me,
            flex: 9,
            border: false
        });

        me.preBtn = Ext.create('Ext.Img', {
            xtype: 'image',
            style:'cursor: pointer',
            listeners: {
                click: function () {
                	if(me.centerpanel.hiddenStart==1){
                		return;
                	}
                    var start = me.centerpanel.hiddenStart - 1;
                    me.centerpanel.hiddenStart = start;
                    me.centerpanel.reload();
                    //me.setBtnStatus(me.centerpanel.hiddenStart,me.centerpanel.isNext);
                },
                element: "el",
                scope: me
            }
        });

        me.preBtnContainer = Ext.create('Ext.container.Container', {
            flex: 0.5,
            border: false,
            layout: {
                //pack: 'center',
                align: 'center',
                type: 'vbox'
            },
            items: [
                me.preBtn
            ]
        });

        me.nextBtn = Ext.create('Ext.Img', {
            xtype: 'image',
            style:'cursor: pointer',
            listeners: {
                click: function () {
                	if(me.centerpanel.totalpage==me.centerpanel.hiddenStart){
                		return ;
                	}
                    var start = me.centerpanel.hiddenStart + 1;
                    me.centerpanel.hiddenStart = start;
                    me.centerpanel.reload();
                    //me.setBtnStatus(me.centerpanel.hiddenStart,me.centerpanel.isNext);
                    
                },
                element: "el",
                scope: me
            }
        });


        me.nextBtnContainer = Ext.create('Ext.container.Container', {
            flex: 0.5,
            border: false,
            layout: {
                //pack: 'center',
                align: 'center',
                type: 'vbox'
            },
            items: [
                me.nextBtn
            ]
        });

        Ext.applyIf(me, {
            listeners: {
                mouseover: function () {
                		if(me.preBtn.getEl().dom.src.indexOf('pre-arrow')==-1){
                			me.preBtn.getEl().dom.src = me.preDisableImg;
                		}
                    	if(me.nextBtn.getEl().dom.src.indexOf('next-arrow')==-1){
	                   		me.nextBtn.getEl().dom.src = me.nextImg;
                    	}
	                    me.preBtn.getEl().dom.style.visibility = 'visible'
	                    me.nextBtn.getEl().dom.style.visibility = 'visible';
	                    
	                    me.preBtn.getEl().dom.style.display = 'block'
	                    me.nextBtn.getEl().dom.style.display = 'block';
	                    
	                    me.preBtn.doComponentLayout();
	                    me.nextBtn.doComponentLayout();
                    
                },
                mouseout: function () {
                    	me.preBtn.getEl().dom.style.visibility = 'hidden';
                    	me.nextBtn.getEl().dom.style.visibility = 'hidden';
                    	
                    	me.preBtn.getEl().dom.style.display = 'none';
                    	me.nextBtn.getEl().dom.style.display = 'none';
                    	
                    	me.preBtn.doComponentLayout();
                    	me.nextBtn.doComponentLayout();
                	
                },
                afterrender:function (c, opts) {
                	me.setBtnStatus(me.centerpanel.hiddenStart,me.centerpanel.isNext);
                },
                element: "el"
            }
        });

        me.callParent(arguments);

        me.add(me.preBtnContainer);
        me.add(me.centerpanel);
        me.add(me.nextBtnContainer);

    }

});