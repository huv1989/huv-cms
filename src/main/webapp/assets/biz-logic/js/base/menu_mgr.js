$(function(){
    function addHoverDom(treeId, treeNode) {
        var sObj = $("#" + treeNode.tId + "_span");
        if (!treeNode.isParent || $("#addBtn_"+treeNode.tId).length > 0) return;
        var addStr = "<span class='button add' id='addBtn_" + treeNode.tId + "' title='新增' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        var btn = $("#addBtn_"+treeNode.tId);
        if (btn) btn.bind("click", function(){
            $('#menuEditForm').resetForm();
            $('#parentID').val(treeNode.id);
             $('#parentName').val(treeNode.menuName);
             $('#id').val('');
            $("#radio-false").attr("checked","true"); 
             $('#parentID').val(treeNode.id);
            $('#menuName').focus();
            return false;
        });
    }
    function removeHoverDom(treeId, treeNode) {
        $("#addBtn_"+treeNode.tId).unbind().remove();
    }
    var setting = {
        edit: {
            enable: true,
            renameTitle: '编辑',
            removeTitle: '删除',
            showRenameBtn: function(treeId, treeNode){if(treeNode.id == 0){return false;} else {return true;}},
            showRemoveBtn: function(treeId, treeNode){if(treeNode.id == 0){return false;} else {return true;}},
            drag:{isCopy : false,isMove : false}
        },
        data: {
            simpleData: { enable: true, idKey: "id", pIdKey: "parentID" },
            key : { name : "showName",url: "mUrl"},
            keep : {parent: true, leaf: true}
        },
        callback : {
            onClick : function(event, treeId, treeNode) {
                if(treeNode.isParent) {
                    zTree.expandNode(treeNode, !treeNode.open, false, true);
                }
            },
            beforeRemove:function(treeId, treeNode) {
                zTree.selectNode(treeNode);
                $.messager.confirm('提醒','确定要删除该'+(treeNode.isParent ? '及其子' : '')+'菜单吗？',function(r) {
                    if(r){
                        $._ajaxPost('menu/del',{id:treeNode.id},function(r){
                            if(r.r) {
                                zTree.removeNode(treeNode);
                                $('#menuEditForm').resetForm();
                            }
                            asyncbox.tips(r.m,r.r ? 'success' : 'error');
                        });
                    }
                });
                return false;
            },
            beforeEditName : function(treeId, treeNode) {
                if(treeNode.parentID == null)treeNode.parentID = 0;
                $('#menuEditForm')._jsonToForm(treeNode);
                $('#parentID').val(treeNode.parentID);
                $('#id').val(treeNode.id);
                $("#radio-false").attr("checked","true"); 
                $('#menuName').val(treeNode.menuName);
                $('#remark').val(treeNode.remark);
                $('#sort').val(treeNode.sort);
                $("input[name='sysmenus.status'][value=" +treeNode.status+"]").attr("checked",true); 
                $('#menuName').focus();
                $('#url').val(treeNode.url);
                var parent = treeNode.getParentNode();
                $('#parentName').val(parent == null ? '无' : parent.showName);
                zTree.selectNode(treeNode);
                return false;
            }
        },
        view : {
            showTitle : false,
            selectedMulti : false,
            autoCancelSelected : false,
            addHoverDom : addHoverDom,
            removeHoverDom : removeHoverDom,
            fontCss : function(treeId, treeNode){
                if(treeNode.status == 0){
                    return {color:"red"};
                } else {
                    return {color:"black"};
                }
            }
        }
    };
    $.fn.zTree.init($("#menuPanel"), setting, window.menus);
    var zTree = $.fn.zTree.getZTreeObj("menuPanel");
    zTree.expandAll(true);
    $('#menuEditForm').submit(function(){
        if(!$(this).form('validate')){return;}
        var node = $(this)._formToJson();
        var id = $("#id").val();
        if($.trim(id) == '') {
            $._ajaxPost('menu/add', node, function(r){
                if(r.r) {
                    var parentNode = zTree.getNodeByParam('id', r.parentID, null);
                    node.id = r.id;
                    node.showName = $.trim(r.remark) == '' ? r.menuName : r.menuName + '[' + r.remark + ']';
                    zTree.addNodes(parentNode, node);
                    zTree.selectNode(zTree.getNodeByParam('id', node.id));
                    $('#menuEditForm').resetForm();
                }
                asyncbox.tips(r.m,r.r ? 'success' : 'error');
            });
        } else {
            $._ajaxPost('menu/update', node, function(r){
                if(r.r) {
                    var treeNode = zTree.getNodeByParam('id', id, null);
                    for(var key in node){
                        treeNode[key] = node[key];
                    }
                    treeNode.isParent = r.isParent == 'true' ? true : false;
                    treeNode.showName = $.trim(treeNode.remark) == '' ? treeNode.menuName : treeNode.menuName + '[' + treeNode.remark + ']';
                    zTree.updateNode(treeNode);
                    $('#menuEditForm').resetForm();
                }
                asyncbox.tips(r.m,r.r ? 'success' : 'error');
            });
        }
    });
});
function custom(width, height){
    $('#menuMgrPanel,#editMenu').height(height - 29);
}