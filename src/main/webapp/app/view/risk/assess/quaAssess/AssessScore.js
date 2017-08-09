Ext.define('FHD.view.risk.assess.quaAssess.AssessScore', {
	alias : 'widget.assessScore',
	
	meMap : null,
	
	show : function(html){
		//alert(html);
	},
	
	saveFun : function(dimId, idS, quAassessOpe){
		var me = this;
		var value = {};
		var parry = [];
		var values = idS.split('--');
		
		value['dimId'] = dimId;//维度ID
		value['rangObjectDeptEmpId'] = values[0];//综合ID
		value['dimValue'] = values[1];//维度分值
		parry.push(value);
		//quAassessOpe.body.mask("分值保存中...","x-mask-loading");
		FHD.ajax({
            url: __ctxPath + '/assess/quaAssess/saveDicValue.f',
            params: {
            	params : Ext.JSON.encode(parry)
            },
            callback: function (data) {
            	if (data && data.success) {
            		//quAassessOpe.body.unmask();
            	}
            }
        });
	},
	
    assessApp : function(divId, quAassessOpe){
		var oStar = document.getElementById(divId);
		var aLi = oStar.getElementsByTagName("li");
		var oUl = oStar.getElementsByTagName("ul")[0];
		var oSpan = oStar.getElementsByTagName("span")[1];
		//var oP = oStar.getElementsByTagName("p")[0];
		
		var oP = document.getElementById('ppp');
		
		var me = this;
		me.meMap.put(divId + 'iScore', 0);
		me.meMap.put(divId + 'iStar', 0);
		
//		if(Ext.getCmp(Ext.getCmp('quaAssessPanelId').quaAssessCard.quaAssessOpe.onId + 'panel') != undefined){
//    		Ext.getCmp(Ext.getCmp('quaAssessPanelId').quaAssessCard.quaAssessOpe.onId + 'panel').setWidth(Ext.getCmp(Ext.getCmp('QuaAssessManId').winId).getWidth() - 70);
////    		$('#ppp').css('right','400px');
////    		$('#ppp').css('right',Ext.getCmp(me.quaAssessOpe.onId + 'panel').getWidth() - 623 + 'px');
//    	}
		
//		var aMsg = [
//					"很不满意|差得太离谱，与卖家描述的严重不符，非常不满",
//					"不满意|部分有破损，与卖家描述的不符，不满意",
//					"一般|质量一般，没有卖家描述的那么好",
//					"满意|质量不错，与卖家描述的基本一致，还是挺满意的",
//					"非常满意|质量非常好，与卖家描述的完全一致，非常满意"
//					];
		
		for (i = 1; i <= aLi.length; i++){
			aLi[i - 1].index = i;
			//鼠标移过显示分数
			aLi[i - 1].onmouseover = function (){
				fnPoint(this.index);
				//alert(divId);
				var descAll = quAassessOpe.dimDescAllMap[divId];
				var aHtml = this.innerHTML.split('--');
				//浮动层显示
//				var quaAssessOpeOid = Ext.getCmp('quaAssessPanelId').quaAssessCard.quaAssessOpe.onId;
				var QuaAssessManId = Ext.getCmp('QuaAssessManId').winId;
				oP.style.display = "block";
//				alert(Ext.getCmp('quaAssessPanelId').quaAssessCard.quaAssessOpe.onId + 'panel');
				if(Ext.getCmp(QuaAssessManId) != null){
					if(Ext.getCmp(QuaAssessManId).maximized){
						$('#ppp').css('right', 550);
					}else{
						$('#ppp').css('right', 550);
					}
				}else{
					if(Ext.getCmp('QuaAssessManId').maximized){
						$('#ppp').css('right', 550);
					}else{
						$('#ppp').css('right', 550);
					}
				}
				
				$('#ppp').css('top', 0);
				
//				console.log(oP.style);
//				oP.style.right = '150px';
				//计算浮动层位置
//				oP.style.left = oUl.offsetLeft + this.offsetWidth - 68 + "px";
//				oP.style.top =  "40px";
//				
				//alert(Ext.getCmp(quAassessOpe.onId + 'panel').style.left);
				
//				oP.style.left = "100px";
//				oP.style.top =  "40px";
				//匹配浮动层文字内容
//				oP.innerHTML = "<em><b>" + this.index + "</b> 分 " + aMsg[this.index - 1].match(/(.+)\|/)[1] + "</em>" + aMsg[this.index - 1].match(/\|(.+)/)[1]
				oP.innerHTML = "<em><br/><b><font color='FF6633'>" + aHtml[4].replace('.0', '') + "分</font></b>(" + aHtml[1] +  ")<br/>" + "<font size='2.5px'>" + descAll + "</fon>";
			};
			
			//鼠标离开后恢复上次评分
			aLi[i - 1].onmouseout = function (){
				fnPoint();
				//关闭浮动层
				oP.style.display = "none"
			};
			
			//点击后进行评分处理
			aLi[i - 1].onclick = function (){
				
				var aHtml = this.innerHTML.split('--');
				me.meMap.put(divId + 'iStar', this.index);
				me.saveFun(aHtml[2], aHtml[3] + '--' + aHtml[4], quAassessOpe);
				
				Ext.create('Ext.panel.Panel', {border : false, html : 'aa'});
				oP.style.display = "none";
				var innserHtml = '';
				var nr = (aHtml[4].replace('.0', '')) + " 分 (" + aHtml[1] + ")----" + quAassessOpe.dicDescAllMap[divId + "--" + aHtml[4]];
				if(quAassessOpe.dicDescAllMap[divId + "--" + aHtml[4]].length != 0){
					innserHtml = "<a href='javascript:void(0);' data-qtip="+ quAassessOpe.dicDescAllMap[divId + "--" + aHtml[4]] +" data-qwidth=300>" + nr + "</a>";
				}else{
					innserHtml = "<a href='javascript:void(0);'>" + nr + "</a>";
				}
				
				if(innserHtml.length > 150){
					oSpan.innerHTML = innserHtml.substring(0, 150) + "...";
				}else{
					oSpan.innerHTML = innserHtml;
				}
			}
		}
		
		//评分处理
		function fnPoint(iArg){
			//分数赋值
			//alert(map.get(divId + 'iStar'));
			me.meMap.put(divId + 'iScore', iArg || me.meMap.get(divId + 'iStar'));
			for (i = 0; i < aLi.length; i++) aLi[i].className = i < me.meMap.get(divId + 'iScore') ? "on" : "";	
		}
		
	},

	assessInitApp : function(divId, iScore, score, msg, quAassessOpe){
		var oStar = document.getElementById(divId);
		var aLi = oStar.getElementsByTagName("li");
		var oP = oStar.getElementsByTagName("p")[0];
		var oSpan = oStar.getElementsByTagName("span")[1];
		var me = this;
		//Ext.QuickTips.init();
		me.meMap.put(divId + 'iStar', iScore);
		for (i = 0; i < aLi.length; i++) aLi[i].className = i < iScore ? "on" : "";	
	
		oP.style.display = "none";
		var nr = (score.replace('.0', '')) + " 分 (" + msg + ")----" + quAassessOpe.dicDescAllMap[divId + "--" + score];
		var innserHtml = '';
		if(quAassessOpe.dicDescAllMap[divId + "--" + score].length != 0){
			innserHtml = "<a href='javascript:void(0);' data-qtip="+ quAassessOpe.dicDescAllMap[divId + "--" + score] +" data-qwidth=300>" + nr + "</a>";
		}else{
			innserHtml = "<a href='javascript:void(0);'>" + nr + "</a>";
		}
		
		if(innserHtml.length > 150){
			oSpan.innerHTML = innserHtml.substring(0, 150) + "...";
		}else{
			oSpan.innerHTML = innserHtml;
		}
	}
});