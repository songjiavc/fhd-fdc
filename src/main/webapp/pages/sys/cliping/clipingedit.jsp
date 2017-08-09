<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
<script>
var saveUrl='sys/cliping/saveCategory.f';
var category='<%=request.getParameter("category")%>';
var cpan;
//全选反选第一栏
function select1(tt)
{
	var c1 = Ext.getCmp('c1');
 
	
	for(i=0;i<c1.items.length;i++)
	    {
	    if(!c1.items.items[i].disabled)
		{
	    c1.items.items[i].setValue(tt);
		}
	    }
}


//全选反选第一栏
function select2(tt)
{
	var c2 = Ext.getCmp('c2');
 
	
	for(i=0;i<c2.items.length;i++)
	    {
	    if(!c2.items.items[i].disabled)
		{
	    c2.items.items[i].setValue(tt);
		}
	    }
}

function save(){//保存方法
    var c1 = Ext.getCmp('c1');
    var c2 = Ext.getCmp('c2');
    var items = '';
	for(i=0;i<c1.items.length;i++)
	    {
	    items+=c1.items.items[i].inputValue+':'+c1.items.items[i].getValue()+",";
	    }
	
	for(i=0;i<c2.items.length;i++)
	    {
	    items+=c2.items.items[i].inputValue+':'+c2.items.items[i].getValue()+",";
	    
	    }

	FHD.ajax({
		url : saveUrl,// 获取面板的地址
		params:{saveStr:items},
		callback : function(){
		 
		 Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
			
		}
	}); 
}

//初始化全选框状态
function init()
{
    var c1 = Ext.getCmp('c1');
    
    var c1count=c1.items.length;
    var c1check=0;
	for(i=0;i<c1.items.length;i++)
	    {
	    if(c1.items.items[i].getValue())
		{
		c1check++;
		}
	    
	    }
	if(c1count==c1check)
	{
	    document.getElementById('c1c').checked=true;
	}


	    var c2 = Ext.getCmp('c2');
	    
	    var c2count=c2.items.length;
	    var c2check=0;
		for(i=0;i<c2.items.length;i++)
		    {
		    if(c2.items.items[i].getValue())
			{
			c2check++;
			}
		    
		    }
		if(c2count==c2check)
		{
		    document.getElementById('c2c').checked=true;
		}
}

 
Ext.onReady(function() {
Ext.QuickTips.init();

var createStore = function(id) { // 创建树面板数据源
	return Ext.create('Ext.data.TreeStore', {
		proxy : {
			type : 'ajax', // 获取方式
			url : 'sys/menu/loadMenuTrees.do' // 获取树节点的地址
		}
	});
};


	
	 var items1=new Array();
	    var items2=new Array();
	
	FHD.ajax({
		url : 'sys/cliping/findClipingByCategory.f?category=' + encodeURI(category),// 获取面板的地址
		callback : function(data){
		   
			Ext.each(data,function(r,index){
			    
		            if(r.type=='function')
		        	{
		        	var label=r.name;
		        	var disabled=false;
		        	var checked=false;
		        	 if(r.isSystem==1)
	        		    {
	        		    disabled=true;
	        		    label+='<font color="red">*</font>';
	        		    }
		        	 
					    if(r.status==1)
						{
						checked=true;
						}
					    
		        	items1.push({id:'aaa'+'_'+r.id,xtype:'checkbox',boxLabel:label,inputValue:r.id,name:'function',checked:checked,disabled:disabled});
	        		
	        		
	        		
		        	}else
		        	{
		        	  	var label=r.name;
			        	var disabled=false;
			        	var checked=false;
			        	 if(r.isSystem==1)
		        		    {
		        		    disabled=true;
		        		    label+='<font color="red">*</font>';
		        		    }
					    if(r.status==1)
						{
						checked=true;
						}
			        	items2.push({id:'bbb'+'_'+r.id,xtype:'checkbox',boxLabel:label,inputValue:r.id,name:'function',checked:checked,disabled:disabled});
		        		
		        		
					    
		        	}
		           
			});    
			
		cpan=new Ext.form.FormPanel({
				//frame:true,
				layout:'column',
				height:FHD.getCenterPanelHeight()-26,
				defaults: {
	                margin: '10 10 10 10',
	                columnWidth : 1
	            },
				renderTo:'gncjsub',
				border:false,
				style : 'border-left: 1px  #99bce8 solid !important;' +
						'border-bottom: 1px  #99bce8 solid !important;' +
						'border-right: 1px  #99bce8 solid !important;',
				items:[{
					xtype:'fieldset',
					title:'<input id="c1c"  type="checkbox" onclick="select1(this.checked)"/></img>tab页签',
					defaultType: 'textfield',
					items:[
						{  
							xtype: 'checkboxgroup',  
							hideLabel:true,
							labelWidth:0,
							id:'c1',
							columns: 4,
							items:eval(items1)
					}
						]
				},
				
				{
					xtype:'fieldset',
					
					title:'<input id="c2c" type="checkbox"  onclick="select2(this.checked)"/></img>表单字段',
					defaultType: 'textfield',
					items:[
						{  
							xtype: 'checkboxgroup',  
							id:'c2',
							hideLabel:true,
							labelWidth:0,
							columns: 4  ,
							items:eval(items2)
					}
						]
				}],
				buttonAlign:'right',
				bbar: [ 
				        
				        '->',{
					text: '保存',
					iconCls: 'icon-save',
					handler:save
				}
					]
			});
			
	 

			init();
		}
	});
	
 
});


</script>
  </head>
  
  <body>
   <div id='gncjsub' height="100%"></div>	
  </body>
</html>
