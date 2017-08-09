Ext.define('FHD.view.kpi.homepage.myFocusChart', {
	
	extend : 'Ext.panel.Panel',
	
	queryUrl:'',
	
	title:'',
	
	objectType:'',
	
	border:false,
	
	chartHeight:'160',
	
	chartWidth:'260',
	
	searchContent:'',
	
	chartType:'',
	
	query:'',
	
	hiddenStart:1,
	
	limit:4,
	
	chartidArray:[],
	
	layout:{
	    	type:'hbox',
	    	align:'stretch'
	    },
	

	reloadData:function(){
		var me = this;
		
	},
	
	initChartList:function(start,limit){
		var me = this;
		var paraobj = {};
		paraobj.start = start;
		paraobj.limit = limit;
		paraobj.type = me.objectType;
		paraobj.query = me.query;
		me.hiddenStart = start;
		 
		if(me.pcontainer.body != undefined){
        	me.pcontainer.body.mask("加载中...","x-mask-loading");
        }
		FHD.ajax({
            url: me.queryUrl,
            async:true,
            params: {
            	itmes: Ext.JSON.encode(paraobj)
            },
            callback: function(result) {
                if(result){
                	for(var k=0;k<me.chartidArray.length;k++){
                		if(FusionCharts(me.chartidArray[k]) != undefined){
                   		   FusionCharts(me.chartidArray[k]).dispose();
      	             	}
                	}
                	me.removeAll(true);
                	
                	me.chartidArray = [];
                	if((result.totalCount%me.limit)!=0){
                		me.totalpage = parseInt(result.totalCount/me.limit+1);
                	}else{
                		me.totalpage = parseInt(result.totalCount/me.limit);
                	}
                	var datas = result.datas;
                	if(datas){
                		for(var i=0;i<datas.length;i++){
                		var robj = datas[i];
                		var xml = robj.xml;
                		var chartid = "chartId"+i+'-'+robj.objectId;
                		me.chartidArray.push(chartid);
                		var divid = "myfocus_"+me.objectType+i+'-'+robj.objectId;
                		var panel = Ext.create('Ext.container.Container',{
                    		border:false,
                    		chartid:chartid,
                    		layout:'fit',
                    		divid:divid,
                    		xmldata:xml,
                    		flex:1,
                    		html:'<div style="width:'+me.chartWidth+'px;height:'+me.chartHeight+'px;margin-top=20px" id="'+divid+'" ></div>',
                    		listeners: {
                    			afterrender: function (c, opts) {
                    		    	var chart = new FusionCharts(__ctxPath + '/images/chart/'+me.chartType+'.swf', c.chartid,'100%','100%',"0", "1", "FFFFFF", "exactFit");
                    		    	chart.setXMLData(c.xmldata);
                    		    	chart.render(c.divid);
                    			},
                    			resize:function(me,width,height,oldWidth,oldHeight,eOpts){
                    				
                    			}
                    		}
            	    	});
                		me.add(panel);
                	}
                	me.isNext = result.isNext;
                	me.pcontainer.setBtnStatus(me.hiddenStart,me.isNext);
                	if(me.pcontainer.body != undefined){
                    	me.pcontainer.body.unmask();
                    }
                	}
                	
                }
            }
        });
	},
	
	reload:function(){
		var me = this;
		me.initChartList(me.hiddenStart,me.limit);//重新加载图表
	},
	
	setBtnStatus:function (start,isNext){
 		var me=this;
 		//判断上一页
    	if(1 == start){
        	me.preBtn.disable();
        	me.firstBtn.disable();
    	}else{
        	me.preBtn.enable();
        	me.firstBtn.enable();
    	}
    	//判断下一页
    	if(isNext){
    		me.nextBtn.enable();
    		me.lastBtn.enable();
    	}else{
    		me.nextBtn.disable();
    		me.lastBtn.disable();
    	}
 	},
	
	// 初始化方法
	initComponent : function() {
		var me = this;
		
    	
		Ext.applyIf(me, {
			
	    	autoScroll:true
	        ,listeners:{
	    		resize:function(me,width,height,oldWidth,oldHeight,eOpts){
	    					var widthper = 0;
			    			var heightper = 0;
			    			if(oldWidth!=undefined&&!oldHeight!=undefined){
			    				widthper = width-oldWidth;
			    				heightper = height - oldHeight;
			    			}
			    			var divobjs = $("div[id*='myfocus_"+me.objectType+"']");
		    				for(var i=0;i<divobjs.length;i++){
		    					var divobj = divobjs.get(i);
		    					var w = parseInt(divobj.style.width.substring(0,divobj.style.width.lastIndexOf("px")))+parseInt(widthper/me.limit);
		    					var h = parseInt(parseInt(divobj.style.height.substring(0,divobj.style.height.lastIndexOf("px"))));
		    					divobj.style.width = w +"px";
		    					divobj.style.height = h+"px";
		    				}
	    		}
	        }
		});

		me.callParent(arguments);
		
		me.initChartList(1,me.limit);
		
	}

});