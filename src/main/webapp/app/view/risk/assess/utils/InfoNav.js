/**
 * 
 * 操作导航
 */

Ext.define('FHD.view.risk.assess.utils.InfoNav', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.infoNav',
    
    setInfo : function(assessPlanName, isAssessCount, isNotAssessCount, totalCount){
    	var me = this;
    	
//    	me.info = '<div>　</div>' +
//	     '<div  align="left">　评估计划:' + assessPlanName + '　已评估/未评估:<font color="#00CC66">' 
//	     + isAssessCount + '</font>/<font color="#FF0000">' + isNotAssessCount + '</font>　　评估总数:' 
//	     + totalCount + '　　当前页/总页:' + pageCount + '/' + totalPageCount + 　　　
//	     '</div>';
    	//me.body.update(me.info);
    	
//    	me.info = '评估计划:' + assessPlanName + 
//    	'　已评估/总数:<font color="#00CC66">'+ isAssessCount + '</font>/<font>' + totalCount + '</font>' + 
//    	'总页:' + pageCount + '/' + totalPageCount ;
    	
    	me.info = //'评估计划:' + assessPlanName + 
    	'风险数量:<font color="red"> '+ isAssessCount + ' /</font> ' + totalCount + '';
    	//'　页数:<font color="red">' + pageCount + '/</font>' + totalPageCount + '';
    	
    	//me.quaAssessTotalPageCount = totalPageCount;
    	
//    	Ext.getCmp('quaAssessCardId').totalPageCount = totalPageCount;
    	Ext.getCmp('quaAssessCardNavId2').setText(me.info);
    	//Ext.getCmp('assessTextFieldId').setValue(pageCount);
//        Ext.getCmp('assessNo3').setText('共<font color="red">' + totalPageCount + '</font>页');
    	//me.body.update(me.info);
	    return me.info;
    },
    
    setInfoRiskTidy : function(assessPlanName, isAssessCount, isNotAssessCount, totalCount){
    	var me = this;
    	me.info = '<div>　</div>' +
	     '<div  align="left">　评估计划:' + assessPlanName + '　已评估/未评估:<font color="#00CC66">' 
	     + isAssessCount + '</font>/<font color="#FF0000">' + isNotAssessCount + '</font>　　评估总数:' + totalCount + 　　　
	     '</div>';
    	
    	me.body.update(me.info)
	    return me.info;
    },
    
    loadRiskTidy : function(){
    	var me = this;
    	
    	FHD.ajax({
            url: __ctxPath + '/assess/riskTidy/findRiskTidyAssessCount.f?assessPlanId=' + Ext.getCmp('riskTidyManId').businessId,
            callback: function (data) {
                if (data && data.success) {
                	var assessPlanName = data.assessPlanName;
                	var isAssessCount = data.isAssessCount;
                	var isNotAssessCount = data.isNotAssessCount;
                	var totalCount = data.totalCount;
                	
                	me.setInfoRiskTidy(
                			assessPlanName, 
                			isAssessCount, 
                			isNotAssessCount, 
                			totalCount
                			);
                }
            }
        });
    },
    
    load : function(riskDatas){
    	var me = this;
    	
    	FHD.ajax({
            url: __ctxPath + '/assess/quaassess/findAssessCount.f?executionId=' + me.executionId,
            params: {
            	params : Ext.JSON.encode(riskDatas)
            },
            callback: function (data) {
                if (data && data.success) {
                	
                	var assessPlanName = data.assessPlanName;
                	var isAssessCount = data.isAssessCount;
                	var isNotAssessCount = data.isNotAssessCount;
                	var totalCount = data.totalCount;
                	//var totalPageCount = data.totalPageCount;
                	//totalPageCount = Number(totalPageCount);//+ 1);
                	
                	me.setInfo(
                			assessPlanName, 
                			isAssessCount, 
                			isNotAssessCount, 
                			totalCount
                			//totalPageCount
                			//totalPageCount
                			);
                }
                
            }
        });
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;;
        
        Ext.apply(me, {
        	border : false,
        	margin : '5 5 5 5'
        });

        me.callParent(arguments);
    }

});