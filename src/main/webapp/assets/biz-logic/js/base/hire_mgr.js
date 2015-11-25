display("role"); // 需要翻译的类型
$(function() {
    $('#hireEditDialog').dialog({
        buttons:[{text:'保存',handler:function(){
            if(!$('#hireEditForm').form('validate')){return;}
            $('#hireEditForm')._ajaxForm(function(r){
                if(r.r){
                	$('#hireEditDialog').dialog('close');$('#grid').datagrid('reload');
                	$.messager.alert('操作提示', r.m,'info');
                	}else{
                		$.messager.alert('操作提示', r.m,'error');
                		}
            });
        }},{text:'关闭',handler:function(){$('#hireEditDialog').dialog('close');}}]
    });
    $('#userRoleDialog').dialog({buttons:[{text:'关闭',handler:function(){$('#userRoleDialog').dialog({closed:true});}}]});
    var grid = $('#grid')._datagrid({
        checkOnSelect:false,
        selectOnCheck:false,
        frozenColumns:[[
            {field:'ck',checkbox:true}
        ]],
        toolbar : [{
                    text : '创建招聘',
                    iconCls : 'icon-add',
                    handler : handler_add
                }, '-', {
                    text : '删除所选',
                    iconCls : 'icon-remove',
                    handler : batch_del
                }, '-' ,{
                    text : '预览',
                    iconCls : 'icon-tip',
                    handler : preview
                }, '-',{
                    text : '覆盖正式',
                    iconCls : 'icon-tip',
                    handler : cover
                }, '-']
    });
    
    var setting = {
		check : {
			enable : true, chkStyle : "checkbox", chkboxType : { "Y" : "s", "N" : "s" }
		},
		data : {
			simpleData : { enable : true, idKey : "id" ,pIdKey : "parentRole"},
			key : { name : "roleName" }
		},
		callback : {
			onCheck : function(event, treeId, treeNode){
				console.info(treeNode);
				$._ajaxPost('user/roleChecked',{userID:$('#roleZtee').data('userID'),roleID:treeNode.id,checked:treeNode.checked},function(r){
					if(r.r){
						var nodes = zTree.getCheckedNodes(true);
						var rowIndex = $('#roleZtee').data('rowIndex');
						var data = $('#grid').datagrid('getRows')[rowIndex];
						data.roles = [];
						for(var i in nodes){
							data.roles.push(nodes[i].id);
						}
						data.roles = data.roles.join(",");
						$('#grid').datagrid('refreshRow', rowIndex);
					}
				});
			}
		},
		view : { showTitle : false, selectedMulti : false, autoCancelSelected : false }
	};
	$.fn.zTree.init($("#roleZtee"), setting, window.roles);
	var zTree = $.fn.zTree.getZTreeObj("roleZtee");
	
    $('#queryButton').click(function(){
        var params = $('#queryForm')._formToJson();
        $(grid).datagrid('load',params);
    });

    /*cover*/
    function cover(){
		$.messager.confirm('操作提示', '确定要覆盖前端静态页吗？', function(r) {
			if (r) {
				 $._ajaxPost('hire/cover',null, function(r){
			            if(r.r){
			            	$.messager.alert('操作提示', r.m,'info');
			            	}else{
			            	$.messager.alert('操作提示', r.m,'error');
			            	}
			        });
			}
		});
    }
    /*预览*/
    function preview(){
        $._ajaxPost('hire/preview',null, function(r){
            if(r.r){
            	window.open("pager/biz-logic/temp/jobs.html");
            	}else{
            	$.messager.alert('操作提示', r.m,'error');
            	}
        });
    }
    /*新增*/
    function handler_add() {
        $('#hireEditForm').attr('action','hire/add').resetForm();
        $('#id').val('');
        $('#hireEditDialog').dialog('open').dialog("setTitle","新增招聘");
    }
    /*批量删除*/
    function batch_del() {
        var check = $('#grid').datagrid('getChecked');
        if(check.length > 0){
            $.messager.confirm('操作提示', '确定要删除所选招聘？', function(r){
                if (r){
                    var userIds = new Array();
                    for(var i in check){
                        userIds[i] = check[i].id;
                    }
                    $._ajaxPost('hire/batchDel',{userIds:userIds.join('|')},function(r){
                        if(r.r){
                        	$.messager.alert('操作提示', r.m,'info');
                        	$('#grid').datagrid('reload');
                        	}else{
                        		$.messager.alert('操作提示', r.m,'error');
                        		}
                    });
                }
            });
        }
    }
});
var formatter = {
    status : function(value, rowData, rowIndex) {
        if(value == 1){ return '<font color=green>正常</font>'; } else { return '<font color=red>停用</font>'; }
    },
    roles : function(data, rowData, rowIndex) {
    	var value = data && data.split(",")
        var arr = [];
        for(i in value) {
            arr.push($.fn.display.role[value[i]]);
        }
        return arr.join(',');
    },
    posts : function(value, rowData, rowIndex) {
        var arr = [];
        for(i in value) {
            arr.push($.fn.display.post[value[i]]);
        }
        return arr.join(',');
    },
    opt : function(value, rowData, rowIndex) {
    	var   html = '<a class="spacing a-blue" onclick="updHire('+rowIndex+');" href="javascript:void(0);">修改</a>';
            html+= '<a class="spacing a-red" onclick="delUser('+rowIndex+');" href="javascript:void(0);">删除</a>';
        return html;
    }
};

/*修改*/
function updHire(rowIndex) {
    $('#hireEditForm').attr('action','hire/update').resetForm();
    var data = $('#grid').datagrid('getRows')[rowIndex];
    $('#hireEditForm')._jsonToForm(data);
    $('#id').val(data.id);
    $('#position').val(data.position);
    $('#location').val(data.location);
    $('#edu').val(data.edu);
    $('#experience').val(data.experience);
    $('#pay').val(data.pay);
    $('#attract').val(data.attract);
    $('#req').val(data.req);
    $('#duty').val(data.duty);
    if(data.status == 0){
    	$('#status2').attr('checked','checked');
    }else{
    	$('#status1').attr('checked','checked');
    }
    $('#hireEditDialog').dialog('open').dialog('setTitle','修改新闻');
}
/*删除*/
function delUser(rowIndex) {
    $.messager.confirm('操作提示', '确定要删除该招聘？', function(r){
        if (r){
            var data = $('#grid').datagrid('getRows')[rowIndex];
            $._ajaxPost('hire/del',{id:data.id}, function(r){
                if(r.r){
                	$.messager.alert('操作提示', r.m,'info');
                	$('#grid').datagrid('reload');
                	}else{
                	$.messager.alert('操作提示', r.m,'error');
                		}
            });
        }
    });
}