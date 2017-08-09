/**
 * 沈飞首页-视频面板 
 * 
 * @author 郝静
 */
Ext.define('FHD.view.sf.index.SFVedioPanel', {
    extend: 'Ext.panel.Panel',
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	Ext.apply(me, {
    	        	flex:1,
        	border:true,
    		style:'padding:1px 0px 0px 1px',
        	layout: {
                type: 'fit'  
            },
//           	html:'<div id="imgADPlayer" style="width:100%;height:100%"></div>',
           	html:'<div id="imgADPlayer" style="width:100%;height:100%"></div>'
//           	tbar:['视频','->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreVedios()\' >更多...</a>']
		});
        me.callParent(arguments);
    },
    afterRender:function(){
    	var me = this;
    	me.initPlayer();
    	me.PImgPlayer.addItem( "test", "http://www.test.com", __ctxPath+"/app/view/sf/images/780.jpg");
    	me.PImgPlayer.addItem( "test1", "http://www.test.com", __ctxPath+"/app/view/sf/images/702.jpg");
    	me.PImgPlayer.init('imgADPlayer','420','280');
    },
    click:function(){
		listeners:{
			click:{
				fn:me.execute
			}
	    }
    },
    showMoreVedios:function(nodeId){
		var me = this;
		me.formwindow = new Ext.Window({
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			title:'视频',
			width:550,
			height:490,
			layout: {
				type: 'fit'
	        },
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : {
           			xtype:'flash',
           			url:__ctxPath + '/scripts/ckplayer6.3/ckplayer/ckplayer.swf',
           			flashVars:{
//							f:'http://movie.ks.js.cn/flv/other/1_0.flv',
							f:__ctxPath + '/app/view/sf/vedio.mp4',
							c:0,
							b:1
						},
					flashParams:{bgcolor:'#FFF',allowFullScreen:true,allowScriptAccess:'always'}},
			buttons: [{
	    				text: '关闭',
	    				handler:function(){
	    					me.formwindow.close();
	    				}
	    	}]
		});
		me.formwindow.show();
	},
	initPlayer:function(){
		/**************************************************   
		名称: 图片轮播类 
		示例:   
		        页面中已经存在名为imgPlayer(或者别的ID也行)的节点.   
		        PImgPlayer.addItem( "test", "http://www.test.com", "http://www.test.com/images/wy.jpg");   
		        PImgPlayer.addItem( "test2", "http://www.test.com", "http://www.test.com/images/wy.jpg");   
		        PImgPlayer.addItem( "test3", "http://www.test.com", "http://www.test.com/images/wy.jpg");   
		        PImgPlayer.init( "imgPlayer", 200, 230 );   
		备注:   
		        适用于一个页面只有一个图片轮播的地方.   
		***************************************************/   
		var me = this;
		me.PImgPlayer = {   
		        _timer : null,   
		        _items : [],   
		        _container : null,   
		        _index : 0,   
		        _imgs : [],   
		        intervalTime : 5000,        //轮播间隔时间   
		        init : function( objID, w, h, time ){
		                this.intervalTime = time || this.intervalTime;   
		                this._container = document.getElementById(objID)||{};   
		                this._container.style.display = "block";   
		                this._container.style.width = w + "px";   
		                this._container.style.height = h + "px";   
		                this._container.style.position = "relative";   
		                this._container.style.overflow = "hidden";   
		                //this._container.style.border = "1px solid #000";   
		                var linkStyle = "display: block; TEXT-DECORATION: none;";   
		                if( document.all ){   
		                        linkStyle += "FILTER:";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Barn(duration=0.5, motion='out', orientation='vertical') ";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Barn ( duration=0.5,motion='out',orientation='horizontal') ";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Blinds ( duration=0.5,bands=10,Direction='down' )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.CheckerBoard()";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Fade(duration=0.5,overlap=0)";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.GradientWipe ( duration=1,gradientSize=1.0,motion='reverse' )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Inset ()";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Iris ( duration=1,irisStyle=PLUS,motion=out )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Iris ( duration=1,irisStyle=PLUS,motion=in )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Iris ( duration=1,irisStyle=DIAMOND,motion=in )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Iris ( duration=1,irisStyle=SQUARE,motion=in )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Iris ( duration=0.5,irisStyle=STAR,motion=in )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.RadialWipe ( duration=0.5,wipeStyle=CLOCK )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.RadialWipe ( duration=0.5,wipeStyle=WEDGE )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.RandomBars ( duration=0.5,orientation=horizontal )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.RandomBars ( duration=0.5,orientation=vertical )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.RandomDissolve ()";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Spiral ( duration=0.5,gridSizeX=16,gridSizeY=16 )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Stretch ( duration=0.5,stretchStyle=PUSH )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Strips ( duration=0.5,motion=rightdown )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Wheel ( duration=0.5,spokes=8 )";   
		                        linkStyle += "progid:DXImageTransform.Microsoft.Zigzag ( duration=0.5,gridSizeX=4,gridSizeY=40 ); width: 100%; height: 100%";   
		                }   
		                //   
		                var ulStyle = "top:0;margin:0 0 29px 0;width:"+w+"px;position:absolute;z-index:999;right:0px;FILTER:Alpha(Opacity=30,FinishOpacity=90, Style=1);overflow: hidden;bottom:-1px;height:16px; border-right:1px solid #fff;";  
		                var ulStyle2 = "margin:0;padding:5px 5px 0 5px;width:"+w+"px;position:absolute;;overflow: hidden;bottom:-1px;height:24px; border-right:1px solid #fff;font-size:14px;font-weight:bold;text-align:center;background:#fff;"; 
		                //   
		                var liStyle = "margin:0;list-style-type: none; margin:0;padding:0; float:right;";   
		                //   
		                var baseSpacStyle = "clear:both; display:block; width:23px;line-height:18px; font-size:12px; FONT-FAMILY:'宋体';opacity: 0.6;";   
		                baseSpacStyle += "border:1px solid #fff;border-right:0;border-bottom:0;";   
		                baseSpacStyle += "color:#fff;text-align:center; cursor:pointer; ";   
		                //   
		                var ulHTML = "";   
		                for(var i = this._items.length -1; i >= 0; i--){   
		                        var spanStyle = "";   
		                        if( i==this._index ){   
		                                spanStyle = baseSpacStyle + "background:#00ff00;";   
		                        } else {                                   
		                                spanStyle = baseSpacStyle + "background:#333333;";   
		                        }   
		                        ulHTML += "<li style=\""+liStyle+"\">";   
		                        ulHTML += "<span onmouseover=\"Ext.getCmp('"+me.id+"').PImgPlayer.mouseOver(this);\"  style=\""+spanStyle+"\" onclick=\"Ext.getCmp('"+me.id+"').PImgPlayer.play("+i+");return false;\" herf=\"javascript:;\">" + (i+1) + "</span>";   
		                        ulHTML += "</li>";   
		                } 
		                //   
		                var html = "<a href='javascript:void(0)' target=\"_blank\" style=\""+linkStyle+"\"></a><ul style=\""+ulStyle+"\">"+ulHTML+"</ul><h1 style=\""+ulStyle2+"\" id=\"tit\">"+this._items[0].title+"</h1>";   
		                this._container.innerHTML = html;
		                var link = this._container.getElementsByTagName("A")[0];           
		                link.style.width = w + "px";   
		                link.style.height = h + "px";   
		                link.style.background = 'url(' + this._items[0].img + ') no-repeat center top';   
		                //   
//		                this._timer = setInterval( "_self.play()", this.intervalTime );   
		        },   
		        addItem : function( _title, _link, _imgURL ){   
		                this._items.push ( {title:_title, link:_link, img:_imgURL } );   
		                var img = new Image();   
		                img.src = _imgURL;   
		                this._imgs.push( img );   
		        },   
		        play : function( index ){
		                if( index!=null ){   
		                        this._index = index;   
//		                        clearInterval( this._timer );   
//		                        this._timer = setInterval( "_self.play()", this.intervalTime );   
		                } else {   
		                        this._index = this._index<this._items.length-1 ? this._index+1 : 0;   
		                }   
		                var link = this._container.getElementsByTagName("A")[0];           
		                if(link.filters){   
		                        var ren = Math.floor(Math.random()*(link.filters.length));   
		                        link.filters[ren].Apply();   
		                        link.filters[ren].play();   
		                }   
		                link.href = this._items[this._index].link;   
		               // link.title = this._items[this._index].title;   
		                link.style.background = 'url(' + this._items[this._index].img + ') no-repeat center top'; 
		                
		                //// title
		                //var title = this._container.getElementsByTagName("h1")[0];     
		                document.getElementById('tit').innerHTML = this._items[this._index].title; 
		                  
		                //   
		                var liStyle = "margin:0;list-style-type: none; margin:0;padding:0; float:right;";   
		                var baseSpacStyle = "clear:both; display:block; width:23px;line-height:18px; font-size:12px; FONT-FAMILY:'宋体'; opacity: 0.6;";   
		                baseSpacStyle += "border:1px solid #fff;border-right:0;border-bottom:0;";   
		                baseSpacStyle += "color:#fff;text-align:center; cursor:pointer; ";   
		                var ulHTML = "";   
		                for(var i = this._items.length -1; i >= 0; i--){   
		                        var spanStyle = "";   
		                        if( i==this._index ){   
		                                spanStyle = baseSpacStyle + "background:#00ff00;";   
		                        } else {                                   
		                                spanStyle = baseSpacStyle + "background:#333333;";   
		                        }   
		                        ulHTML += "<li style=\""+liStyle+"\">";   
		                        ulHTML += "<span onmouseover=\"Ext.getCmp('"+me.id+"').PImgPlayer.mouseOver(this);\"  style=\""+spanStyle+"\" onclick=\"Ext.getCmp('"+me.id+"').PImgPlayer.play("+i+");return false;\" herf=\"javascript:;\">" + (i+1) + "</span>";   
		                        ulHTML += "</li>";   
		                }   
		                this._container.getElementsByTagName("UL")[0].innerHTML = ulHTML;           
		        },   
		        mouseOver : function(obj){   
		                var i = parseInt( obj.innerHTML );   
		                if( this._index!=i-1){   
		                        obj.style.color = "#ff0000";   
		                }   
		        },   
		        mouseOut : function(obj){   
		                obj.style.color = "#fff";   
		        }   
		} ;
		
	}
    
});