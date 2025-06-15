import {reactive} from "vue";
import * as monaco from "monaco-editor";
import Axios from "axios";

const $editor = reactive({
    options: {
        messages: {},
        tasks: [],
        running: 'stop',
        projectId: '0'
    },
    setExtraLib: function (moduleName, projectId) {
        // 默认都要加载的约束
        // const defaultLib = ['java.rt.core.d.ts', 'java.db.core.d.ts']
        const defaultLib = ['java.db.core.d.ts']
        monaco.languages.typescript.javascriptDefaults.setExtraLibs([])
        this.addExtraLib(defaultLib);
        if (moduleName) {
            Axios.get(`/api/v1/project/module-dts/${moduleName}?projectId=${projectId}`).then(res => {
                monaco.languages.typescript.javascriptDefaults.addExtraLib(res,
                    'ts:filename/core-' + moduleName + '.d.ts');
            })
        }
    },
    /**
     * 加载第三方依赖包
     */
    addMavenLibs(appId){
        const libUri = 'ts:filename/maven-libs/' + appId + '.d.ts';
        if(this.options[libUri]){
            monaco.languages.typescript.javascriptDefaults.addExtraLib(this.options[libUri], libUri);
        }
        Axios.get('/boss/api/app/' + appId + '/maven-libs.d.ts').then(res => {
            if (!res || res == ''){
                return;
            }
            this.options[libUri] = res;
            monaco.languages.typescript.javascriptDefaults.addExtraLib(this.options[libUri], libUri);
        }).catch(() => {
            console.log('未能解析第三方依赖包，可能是因为jar包未下载,尝试重启微应用解决。')
        });
    },
    addExtraLib(filenames){
        for(let i in filenames){
            const libUri = 'ts:filename/' + filenames[i];
            if(this.options[libUri]){
                monaco.languages.typescript.javascriptDefaults.addExtraLib(this.options[libUri], libUri);
            }else{
                Axios.get('/dts/' + filenames[i]).then(res => {
                    this.options[libUri] = res;
                    monaco.languages.typescript.javascriptDefaults.addExtraLib(this.options[libUri], libUri);
                });
            }

        }
    },
    settings: {
        leftArea: {
            width: 300,
            open: true,
            tab: 'file'
        },
        consolePlan:{
            height: 250
        }
    },
    /**
     * 文件列表
     */
    files: [],
    /**
     * 打开的文件列表
     */
    openFiles: [],
    /**
     * 当前编辑的文件
     */
    thisFile: undefined,
    /**
     * 当前editor实例
     */
    editor: undefined,
    /**
     * 上下文菜单
     */
    contentMenu: undefined,
    /**
     * 通过文件的key打开文件
     * @param key
     * @param child
     */
    openFileKey(key, child){
        if (!child){
            child = this.files;
        }
        for (let i in child){
            if (child[i].key === key){
                this.openFile(child[i]);
                return;
            }
            if (child[i].child){
                this.openFileKey(key, child[i].child);
            }
        }
    },
    /**
     * 打开文件
     */
    openFile(file){
        if (file.name.indexOf('.') === -1 || file.name.lastIndexOf('.') === file.name.length) {
            return;
        }
        if (file.type === 'dir') {
            return;
        }
        this.thisFile = file;
        this.selectFileModuleApiSuggestions(file);
        let isOpen = false;
        for (let i in this.openFiles) {
            if (this.openFiles[i] === file) {
                isOpen = true;
                break;
            }
        }
        if (!isOpen){
            this.openFiles.push(file);
        }
    },
    closeFile(file){
        if(file.needSave){
            this.confirm('是否要关闭', '该文件已更改但尚未保存, 确定要关闭吗?', (value) => {
                if(value){
                    for (let i in this.openFiles) {
                        if (this.openFiles[i] === file) {
                            this.openFiles.splice(i, 1);
                            if (i > 0){
                                this.thisFile = this.openFiles[i - 1];
                            }else if (i < this.openFiles.length){
                                this.thisFile = this.openFiles[i];
                            }else{
                                this.thisFile = undefined;
                            }
                            break;
                        }
                    }
                }
            })
        }else{
            for (let i in this.openFiles) {
                if (this.openFiles[i] === file) {
                    this.openFiles.splice(i, 1);
                    if (i > 0){
                        this.thisFile = this.openFiles[i - 1];
                    }else if (i < this.openFiles.length){
                        this.thisFile = this.openFiles[i];
                    }else{
                        this.thisFile = undefined;
                    }
                    break;
                }
            }
        }
    },
    save(){
        if (this.thisFile) {
            this.thisFile.loading = true;
            Axios.post('/api/v1/project/write?projectId=' + this.options.projectId, {
                key: this.thisFile.key,
                content: this.thisFile.content
            }).then(() => {
                console.log('保存成功');
                this.thisFile.needSave = false;
                if(this.thisFile.saveFun){
                    this.thisFile.saveFun();
                }
            }).catch(e => {
                alert(e.message);
            }).finally(() => {
                this.thisFile.loading = false;
            })
        }
    },

    /**
     * 展出消息提示
     * @param title
     *      标题
     * @param message
     *      消息
     * @param type
     *      类型 success、info、warning、error
     */
    message(title, message, type){
        const id = new Date().getTime();
        this.options.messages[id] = {title: title, message: message, type: type ? type: 'success', id: id};
    },
    /**
     * 展出成功消息
     * @param title
     *      标题
     * @param message
     *      消息
     */
    messageSuccess(title, message){
        this.message(title, message, 'success');
    },
    /**
     * 展出信息消息
     * @param title
     *      标题
     * @param message
     *      消息
     */
    messageInfo(title, message){
        this.message(title, message, 'info');
    },
    /**
     * 展出警告消息
     * @param title
     *      标题
     * @param message
     *      消息
     */
    messageWarning(title, message){
        this.message(title, message, 'warning');
    },
    /**
     * 展出错误消息
     * @param title
     *      标题
     * @param message
     *      消息
     */
    messageError(title, message){
        this.message(title, message, 'error');
    },
    /**
     * 初始化文件目录
     * @param url
     *      接口地址
     */
    initWorkDir(url, callback){
        Axios.get(url).then(res => {
            this.files = res
            if (callback) {
                callback(res)
            }
        });
    },
    promptWindowList: {},
    promptFieldsWindowList: {},
    confirmWindowList: {},
    prompt(title, placeholder, callback, id, defaultValue){
        const pw = {
            title: title,
            placeholder: placeholder,
            callback: callback,
            height: '130px',
            width: '350px',
            window: undefined,
            id: id,
            value: defaultValue
        };
        if (!pw.id) {
            // 生成一个唯一ID
            pw.id = Math.random().toString(36).substr(2);
        }

        // 如果已经存在，则抖动, 且不再创建
        if (this.promptWindowList[pw.id]) {
            this.promptWindowList[pw.id].window.shake();
            return;
        }

        this.promptWindowList[pw.id] = pw;
        return pw.id;
    },
    promptFields(title, fieldList, option, callback, id, ){
        // fieldList = [{placeholder, fieldName, defaultValue}]
        const pw = {
            title: title,
            callback: callback,
            height: (option && option.height) ? option.height : '130px',
            width: (option && option.width) ? option.width : '350px',
            window: undefined,
            id: id,
            fieldList: fieldList,
        };
        if (!pw.id) {
            // 生成一个唯一ID
            pw.id = Math.random().toString(36).substr(2);
        }

        // 如果已经存在，则抖动, 且不再创建
        if (this.promptFieldsWindowList[pw.id]) {
            this.promptFieldsWindowList[pw.id].window.shake();
            return;
        }

        this.promptFieldsWindowList[pw.id] = pw;
        return pw.id;
    },
    confirm(title, content, callback, id){
        const pw = {
            title: title,
            content: content,
            height: '130px',
            width: '350px',
            window: undefined,
            value: null,
            id: id,
            callback: callback
        };
        if (!pw.id) {
            // 生成一个唯一ID
            pw.id = Math.random().toString(36).substr(2);
        }

        // 如果已经存在，则抖动, 且不再创建
        if (this.confirmWindowList[pw.id]) {
            this.confirmWindowList[pw.id].window.shake();
            return;
        }

        this.confirmWindowList[pw.id] = pw;
        return pw.id;
    },
    moduleApiSuggestions: {},
    selectFileModuleApiSuggestions(file){
        let moduleName = file.key.replace('//', '');
        moduleName = moduleName.substr(0, moduleName.indexOf('/'));
        let language = 'javascript';
        if(file.key.endsWith('.vue') || file.key.endsWith('.VUE')){
            language = 'html';
        }else if (file.key.endsWith('.js') || file.key.endsWith('.JS') || file.key.toUpperCase().endsWith(".MJS")) {
            language = 'javascript';
        }else{
            return;
        }
        if(/([a-z]|[A-Z])+\/apis\/.+/ig.test(file.key.replace("//", ''))){
            // 设置Model和Dao的提示
            this.setExtraLib(moduleName, this.options.projectId);
            return;
        }

        // 设置API的提示
        if(file.key.endsWith('.vue') || file.key.endsWith('.VUE')){
            Axios.get("/api/v1/project/module-apis?module=" + moduleName).then(res => {
                this.setModuleApiSuggestions(language, res);
            });
        }
    },
    setModuleApiSuggestions(language, suggestions){
        let needInit = false;
        if(!this.moduleApiSuggestions[language]){
            needInit = true;
        }
        this.moduleApiSuggestions[language] = suggestions;
        if(needInit){
            monaco.languages.registerCompletionItemProvider(
                language,
                {
                    triggerCharacters: ['this.', '$api.', '.'],
                    provideCompletionItems: (model, position) =>{
                        const { lineNumber, column } = position
                        // 光标前文本
                        const textBeforePointer = model.getValueInRange({
                            startLineNumber: lineNumber,
                            startColumn: 0,
                            endLineNumber: lineNumber,
                            endColumn: column
                        }).trim();

                        if(textBeforePointer === 'this.'){
                            return {suggestions: [
                                    {
                                        label: '$api', // 显示的提示内容
                                        kind: monaco.languages.CompletionItemKind['Class'], // 用来显示提示内容后的不同的图标
                                        insertText: "$api", // 选择后粘贴到编辑器中的文字
                                        detail: '后台接口方法', // 提示内容后的说明
                                    }
                                ]};
                        }

                        if (textBeforePointer.endsWith('$api.')) {
                            let suggestions = [];
                            for(let i in this.moduleApiSuggestions[language]){
                                suggestions.push({
                                    label: this.moduleApiSuggestions[language][i].className,
                                    kind: monaco.languages.CompletionItemKind['Class'],
                                    insertText: this.moduleApiSuggestions[language][i].className,
                                })
                            }
                            return {suggestions: suggestions};
                        }

                        let suggestions = [];
                        for(let i in this.moduleApiSuggestions[language]){
                            if (textBeforePointer.endsWith(this.moduleApiSuggestions[language][i].className + ".")) {
                                for(let j in this.moduleApiSuggestions[language][i].apis){
                                    suggestions.push({
                                        label: j,
                                        kind: monaco.languages.CompletionItemKind['Method'],
                                        insertText: j,
                                        detail: this.moduleApiSuggestions[language][i].apis[j].description
                                    })
                                }
                                return {suggestions: suggestions};
                            }
                        }
                        return {suggestions: []};
                    }
                }
            );
        }
    },
    openDebug(file){
        file.debug = true;
    },
    closeDebug(file){
        file.debug = false;
    },
    isDebug(file){
        return file.debug;
    }
})


// 模型描述语言
monaco.languages.register({id: 'fastModel'});
// 定义语言的基本特性
monaco.languages.setMonarchTokensProvider('fastModel', {
    // 为了插件尚未被 token 解析的内容，设置 defaultToken 为 invalid
    defaultToken: 'invalid',
    // 关键字定义
    keywords: [
        'require', 'scope', 'length'
    ],
    // 类型定义
    typeKeywords: [
        'boolean', 'double', 'int', 'string', 'longtext', 'object', 'datetime', 'time'
    ],
    // 操作符定义
    operators: [
        ':',
    ],

    // 定义常见的正则表达式
    symbols:  /[=><!~?:&|+\-*\/\^%]+/,

    // C# 样式字符串
    escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,

    // 语言的主要 token 生成器
    tokenizer: {
        root: [
            // 标识符与关键字
            [/[a-z_$][\w$]*/, { cases: { '@typeKeywords': 'keyword',
                    '@keywords': 'keyword',
                    '@default': 'identifier' } }],
            [/[A-Z][\w\$]*/, 'type.identifier' ],  // to show class names nicely

            // 空格
            { include: '@whitespace' },

            // 括号与运算符
            [/[{}()\[\]]/, '@brackets'],
            [/[<>](?!@symbols)/, '@brackets'],
            [/@symbols/, { cases: { '@operators': 'operator',
                    '@default'  : '' } } ],

            // @ 注释.
            // 作为示例，我们在这些 token 上发出调试日志消息
            [/@\s*[a-zA-Z_\$][\w\$]*/, { token: 'annotation', log: 'annotation token: $0' }],

            // 各类数字定义
            [/\d*\.\d+([eE][\-+]?\d+)?/, 'number.float'],
            [/0[xX][0-9a-fA-F]+/, 'number.hex'],
            [/\d+/, 'number'],

            // 分隔符
            [/[;,.]/, 'delimiter'],

            // 字符串定义
            [/'([^'\\]|\\.)*$/, 'string.invalid' ],  // non-teminated string
            [/'/,  { token: 'string.quote', bracket: '@open', next: '@string' } ],
            [/'[^\\']'/, 'string'],
            [/(')(@escapes)(')/, ['string','string.escape','string']],
            [/'/, 'string.invalid']
        ],
        // 自定义规则 - 备注
        comment: [
            [/[^\/*]+/, 'comment' ],
            [/\/\*/,    'comment', '@push' ],    // nested comment
            ["\\*/",    'comment', '@pop'  ],
            [/[\/*]/,   'comment' ]
        ],
        // 自定义规则 - 字符串
        string: [
            [/[^\\']+/,  'string'],
            [/@escapes/, 'string.escape'],
            [/\\./,      'string.escape.invalid'],
            [/'/,        { token: 'string.quote', bracket: '@close', next: '@pop' } ]
        ],
        // 自定义规则 - 空格
        whitespace: [
            [/[ \t\r\n]+/, 'white'],
            [/\/\*/,       'comment', '@comment' ],
            [/\/\/.*$/,    'comment'],
        ],
    },
})
monaco.languages.registerCompletionItemProvider('fastModel', {
    provideCompletionItems: () => {
        return {
            suggestions: [
                {
                    label: 'string(String, varchar(255)',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'string',
                    documentation: '字段类型为字符串，对应数据库类型varchar(255)',
                },
                {
                    label: 'boolean(Boolean, int(1))',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'boolean',
                    documentation: '取值范围true/false，对应数据库类型int(1)',
                },
                {
                    label: 'double(Number, decimal(20,2))',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'double',
                    documentation: '字段类型为number，对应数据库类型decimal(20,2)',
                },
                {
                    label: 'int(Number, int(11))',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'int',
                    documentation: '字段类型为number，对应数据库类型int(11)',
                },
                {
                    label: 'longtext(String, longtext)',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'longtext',
                    documentation: '字段类型为字符串，对应数据库类型longtext',
                },{
                    label: 'object(Object, json)',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'object',
                    documentation: '字段类型为内联模型对象，对应数据库类型json',
                },{
                    label: 'datetime(Date, datetime)',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'datetime',
                    documentation: '字段类型为日期时间，对应数据库类型datetime',
                },{
                    label: 'time(Time, time)',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'time',
                    documentation: '字段类型为纯时间，对应数据库类型time',
                },{
                    label: 'require',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'require ',
                    documentation: '声明一个必填字段',
                },{
                    label: 'length 设置长度',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'length: ${1:1}-${2:50}, ',
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    documentation: '设置长度',
                },{
                    label: 'scope 设置取值范围',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: 'scope: ${1:0}-${2:100}, ',
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    documentation: '设置取值范围',
                },{
                    label: 'reqf 快速声明必填字段',
                    kind: monaco.languages.CompletionItemKind.Keyword,
                    insertText: ['require ${1:数据类型} ${2:字段名} (\'${3:数据库字段名}\', \'${4:描述}\')[', '\t$0', ']'].join('\n'),
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    documentation: '快速声明一个必填字段',
                },{
                    label: 'field[快速声明一个字段]',
                    kind: monaco.languages.CompletionItemKind.Snippet,
                    insertText: ['${1:数据类型} ${2:字段名} (\'${3:描述}\')[', '\t$0', ']'].join('\n'),
                    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                    documentation: '快速声明一个字段',
                }
            ]};
    }
});

export default $editor;
