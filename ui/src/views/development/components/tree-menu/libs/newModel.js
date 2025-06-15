export default {
    key: 'new-model',
    name: '新建模型',
    keymap: '',
    onClick(x, y, context, vue){
        vue.$editor.prompt('新建模型', '请输入模型名称', (name) => {
            if (name) {
                vue.$http.post('/api/v1/project/model?projectId=' + vue.$editor.options.projectId, {name, key:context.key}).then(res => {
                    res.level = context.level + 1;
                    context.child.push(res);
                    vue.$editor.openFile(res);
                });
            }
        }, 'new-vue-' + context.key);
    }
}
