export default {
    key: 'rename',
    name: '重命名',
    keymap: '',
    onClick(x, y, context, vue){
        vue.$editor.prompt('重命名', '请输入新名称', (name) => {
            if (name) {
                vue.$http.post('/api/v1/project/rename?projectId=' + vue.$editor.options.projectId, {name, key:context.key}).then(res => {
                    context.name = name;
                    context.key = context.key.substr(0, context.key.lastIndexOf('/') + 1) + name;
                });
            }
        }, 'rename-' + context.key, context.name);
    }
}