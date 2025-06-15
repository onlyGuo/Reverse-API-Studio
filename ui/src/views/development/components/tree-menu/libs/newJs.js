export default {
    key: 'new-js',
    name: '新建JavaScript',
    keymap: '',
    onClick(x, y, context, vue){
        vue.$editor.prompt('新建Js', '请输入文件名称', (name) => {
            if(name){
                vue.$http.post('/api/v1/project/jsFile?projectId=' + vue.$editor.options.projectId, {name, key:context.key}).then(res => {
                    res.level = context.level + 1;
                    context.child.push(res);
                    vue.$editor.openFile(res);
                });
            }
        }, 'new-js' + context.key);
    }
}