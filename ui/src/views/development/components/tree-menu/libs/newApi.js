export default {
    key: 'new-api',
    name: '新建接口',
    keymap: '',
    onClick(x, y, context, vue){
        vue.$editor.promptFields('新建接口', [
            {
                name: 'name',
                placeholder: '请输入接口名称',
            },
            {
                name: 'model',
                placeholder: '请输入监听模型名称',
            },
            {
                name: 'uri',
                placeholder: '请输入监听URI',
                value: 'v1/chat/completions'
            }
        ], {height: '230px'}, (data) => {
            if(!data){
                vue.$editor.message("错误:", '请输入接口名称', 'error')
                return false;
            }
            if (!data.name) {
                vue.$editor.message("错误:", '请输入接口名称', 'error')
                return false;
            }
            if (!data.model) {
                vue.$editor.message("错误:", '请输入监听模型名称', 'error')
                return false;
            }
            if (!data.uri) {
                vue.$editor.message("错误:", '请输入监听URI', 'error')
                return false;
            }
            const req = {
                name: data.name,
                model: data.model,
                uri: data.uri,
                key: context.key,
            }
            vue.$http.post('/api/v1/project/api?projectId=' + vue.$editor.options.projectId, req).then(res => {
                res.level = context.level + 1;
                context.child.push(res);
                vue.$editor.openFile(res);
            });
        }, 'new-api');
    }
}
