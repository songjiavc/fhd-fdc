Ext.define('FHD.view.comm.analysis.ThemeDataSourcePanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.themedatasourcepanel',

    border:false,
    
    // 初始化方法
    initComponent: function () {
        var me = this;
        
        Ext.applyIf(me, {
        	layout: {
				type: 'vbox',
	        	align:'stretch'
	        }
        });
        
        me.callParent(arguments);
    },
   	reloadData:function(){
   		var me=this;
   		//alert('xxxxxxxxxxxxxx');
   		var themeanalysismainpanel = me.up('themeanalysismainpanel');
		if(themeanalysismainpanel){
			me.businessId = themeanalysismainpanel.paramObj.businessId;
		}
		//alert('me.businessId='+me.businessId);
        FHD.ajax({
        	url: __ctxPath + '/themeAnalysis/findThemeAnalysisById.f',
            params: {
            	themeAnalysisId: me.businessId
            },
            callback: function (response) {
                if (response.success) {
                	//alert('callback........');
                	if('layout0'==response.data.layoutType){
                		//布局0
                		if('absolute'==response.data.attribute){
                			//绝对
                			me.remove(me.layoutSetPanel,true);
                			
                			me.layoutSetPanel = Ext.create('FHD.view.comm.analysis.ThemeLayout0Absolute',{
                				//宽度
                			    width:response.data.oneWidth,
                			    //高度
                			    height:response.data.oneHeight,
                			    //主题分析id
                			    businessId:me.businessId,
                			    //布局id和name
                			    onePanelId:me.onePanelId,
                			    title:me.onePanelName
                			});
                			
                			me.add(me.layoutSetPanel);
                			me.layoutSetPanel.reloadData();
                		}else if('relative'==response.data.attribute){
                			//相对
                			me.remove(me.layoutSetPanel,true);
                			
                			me.layoutSetPanel = Ext.create('FHD.view.comm.analysis.ThemeLayout0Relative',{
                				//高度比例
                				heightRatio:response.data.heightRatio,
                			    //宽度比例
                			    widthRatio:response.data.widthRatio,
                			    //主题分析id
                			    businessId:me.businessId,
                			    //布局样式id和name
                			    onePanelId:me.onePanelId,
                			    title:me.onePanelName
                			});
                			
                			me.add(me.layoutSetPanel);
                			me.layoutSetPanel.reloadData();
                		}
                	}else if('layout1'==response.data.layoutType){
                		//布局1
                		if('absolute'==response.data.attribute){
                			//绝对
                			me.remove(me.layoutSetPanel,true);
                			
                			me.layoutSetPanel = Ext.create('FHD.view.comm.analysis.ThemeLayout1Absolute',{
                				//a宽度
                			    aWidth:response.data.oneWidth,
                			    //a高度
                			    aHeight:response.data.oneHeight,
                			    //b宽度
                			    bWidth:response.data.twoWidth,
                			    //a高度
                			    bHeight:response.data.twoHeight,
                			    //c宽度
                			    cWidth:response.data.threeWidth,
                			    //c高度
                			    cHeight:response.data.threeHeight,
                			    //主题分析id
                			    businessId:me.businessId,
                			    //布局样式id和name
                			    onePanelId:me.onePanelId,
                			    aTitle:me.onePanelName,
                			    twoPanelId:me.twoPanelId,
                			    bTitle:me.twoPanelName,
                			    threePanelId:me.threePanelId,
                			    cTitle:me.threePanelName
                			});
                			
                			me.add(me.layoutSetPanel);
                			me.layoutSetPanel.reloadData();
                		}else if('relative'==response.data.attribute){
                			//相对
                			me.remove(me.layoutSetPanel,true);
                			
                			var twoWidthRatioArray = response.data.twoWidthRatio.split(':');
                			var heightRatioArray = response.data.heightRatio.split(':');
                			
                			me.layoutSetPanel = Ext.create('FHD.view.comm.analysis.ThemeLayout1Relative',{
                				//第一行高度比例
                			    oneHeightRatio:heightRatioArray[0],
                			    //第二行高度比例
                			    twoHeightRatio:heightRatioArray[1],
                			    //a宽度比例--有默认值可以不传
                			    //aWidthRatio:1,
                			    //b宽度比例
                			    bWidthRatio:twoWidthRatioArray[0],
                			    //c宽度比例
                			    cWidthRatio:twoWidthRatioArray[1],
                			    //主题分析id
                			    businessId:me.businessId,
                			    //布局样式id和name
                			    onePanelId:me.onePanelId,
                			    aTitle:me.onePanelName,
                			    twoPanelId:me.twoPanelId,
                			    bTitle:me.twoPanelName,
                			    threePanelId:me.threePanelId,
                			    cTitle:me.threePanelName
                			});
                			
                			me.add(me.layoutSetPanel);
                			me.layoutSetPanel.reloadData();
                		}
                	}else if('layout2'==response.data.layoutType){
                		//布局2
                		if('absolute'==response.data.attribute){
                			//绝对
                			me.remove(me.layoutSetPanel,true);
                			
                			me.layoutSetPanel = Ext.create('FHD.view.comm.analysis.ThemeLayout2Absolute',{
                				//a宽度
                			    aWidth:response.data.oneWidth,
                			    //a高度
                			    aHeight:response.data.oneHeight,
                			    //b宽度
                			    bWidth:response.data.twoWidth,
                			    //a高度
                			    bHeight:response.data.twoHeight,
                			    //c宽度
                			    cWidth:response.data.threeWidth,
                			    //c高度
                			    cHeight:response.data.threeHeight,
                			    //d宽度
                			    dWidth:response.data.fourWidth,
                			    //c高度
                			    dHeight:response.data.fourHeight,
                			    //主题分析id
                			    businessId:me.businessId,
                			    //布局样式id和name
                			    onePanelId:me.onePanelId,
                			    aTitle:me.onePanelName,
                			    twoPanelId:me.twoPanelId,
                			    bTitle:me.twoPanelName,
                			    threePanelId:me.threePanelId,
                			    cTitle:me.threePanelName,
                			    fourPanelId:me.fourPanelId,
                			    dTitle:me.fourPanelName
                			});
                			
                			me.add(me.layoutSetPanel);
                			me.layoutSetPanel.reloadData();
                		}else if('relative'==response.data.attribute){
                			//相对
                			me.remove(me.layoutSetPanel,true);
                			
                			var onwWidthRatioArray = response.data.oneWidthRatio.split(':');
                			var twoWidthRatioArray = response.data.twoWidthRatio.split(':');
                			var heightRatioArray = response.data.heightRatio.split(':');
                			
                			me.layoutSetPanel = Ext.create('FHD.view.comm.analysis.ThemeLayout2Relative',{
                				//第一行的高度比例
                			    oneHeightRatio:heightRatioArray[0],
                			    //第二行的高度比例
                			    twoHeightRatio:heightRatioArray[1],
                			    //A的宽度比例
                			    aWidthRatio:onwWidthRatioArray[0],
                			    //B的宽度比例
                			    bWidthRatio:onwWidthRatioArray[1],
                			    //C的宽度比例
                			    cWidthRatio:twoWidthRatioArray[0],
                			    //D的宽度比例
                			    dWidthRatio:twoWidthRatioArray[1],
                			    //主题分析id
                			    businessId:me.businessId,
                			    //布局样式id和name
                			    onePanelId:me.onePanelId,
                			    aTitle:me.onePanelName,
                			    twoPanelId:me.twoPanelId,
                			    bTitle:me.twoPanelName,
                			    threePanelId:me.threePanelId,
                			    cTitle:me.threePanelName,
                			    fourPanelId:me.fourPanelId,
                			    dTitle:me.fourPanelName
                			});
                			
                			me.add(me.layoutSetPanel);
                			me.layoutSetPanel.reloadData();
                		}
                	}
                }
            }
    	});
   	}
});