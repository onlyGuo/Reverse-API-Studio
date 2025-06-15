export default {
    key: 'delete',
    name: '删除',
    keymap: '',
    onClick(x, y, context, vue){
        vue.$http.delete(`/api/v1/project/delete?path=${context.key}&projectId=${vue.$editor.options.projectId}`).then(res => {
            this.close(context, vue);
            this.delete(vue.$editor.files, context, false);
        });
    },
    delete(files, context, isAll){
        for (let i = 0; i < files.length; i++) {
            if (files[i].type === 'dir'){
                if (files[i].key === context.key){
                    files.splice(i, 1);
                    return;
                }else{
                    this.delete(files[i].child, context, isAll);
                }
            }else{
                if (files[i].key === context.key){
                    files.splice(i, 1);
                    return;
                }
            }
        }
    },
    close(context, vue){
        if (context.type === 'dir'){
            for (let i = 0; i < context.child.length; i++) {
                this.close(context.child[i], vue);
            }
        }else{
            vue.$editor.closeFile(context);
        }
    }
}
