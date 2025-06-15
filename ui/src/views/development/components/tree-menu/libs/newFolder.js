export default {
    key: 'new-folder',
    name: '新建文件夹',
    keymap: '',
    param: {},
    onClick(x, y, context, vue){
        console.log(x, y, context, vue.$editor)
        vue.$editor.prompt('新建文件夹', '请输入文件夹名称', (name) => {
            if (name) {
                // vue.$editor.newFolder(name)
                vue.$http.post('/api/v1/project/folder?projectId=' + vue.$editor.options.projectId, {
                    name,
                    key: context.key + '/' + name
                }).then(res => {
                    res.level = context.level + 1
                    context.child.push(res)
                });
            }
        }, 'new-folder');

    }
}