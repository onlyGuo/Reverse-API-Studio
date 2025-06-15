export default {
    key: 'new-vue',
    name: '新建Vue文件',
    keymap: '',
    onClick(x, y, context, vue){
        vue.$editor.prompt('新建Vue文件', '请输入文件名称', (name) => {
            if (name) {
                vue.$http.post('/api/v1/project/vue?projectId=' + vue.$editor.options.projectId, {name, key:context.key}).then(res => {
                    res.level = context.level + 1;
                    context.child.push(res);
                    vue.$editor.openFile(res);
                });
            }
        }, 'new-vue-' + context.key);
    }
}