<template>
  <div style="height: 100%; width: 100%">
    <div style="height: 100%; width: 100%" ref="container">

    </div>
    <div class="redonly-msg" v-if="!canEdit">
      <div style="margin-left: 20px">
        当前文件正在被{{fileLockNicker}}编辑，您只能查看，不能编辑
      </div>
    </div>

    <div class="debug-bar" :class="{active: ding}" v-show="this.item.key.endsWith('mjs')">
      <div class="icons">
        <span class="icon-debug" title="调试" @click="openDebug"></span>

<!--        <span class="icon-debugstepover active"></span>-->
<!--        <span class="icon-kuaijin active"></span>-->
<!--        <span class="icon-jieshu active"></span>-->
        已连接调试器，<a href="javascript:window.open('/#/docs/debug');">点此</a>查看使用帮助.
        <img src="../../../../../assets/icons/ding.svg" class="ding" alt="钉住" title="钉住" @click="ding = !ding"
             :class="{active: ding}">
      </div>
      <div style="margin-bottom: 5px">
        选择调试方法：
        <select v-model="debugMethod">
          <option v-for="method in debugMethodList" :key="method">{{method}}</option>
        </select>
      </div>
      <div style="margin-bottom: 5px">
        <div ref="inputContainer" style="height: 530px"></div>
      </div>

    </div>
    <script-debug-window v-if="$editor.isDebug(item)" :item="item"></script-debug-window>
  </div>
</template>

<script>
import * as monaco from 'monaco-editor'
import tsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker'
import EditorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker'
import EditorConf from "./EditorConf.js";
import {Uri} from "monaco-editor";
import ScriptDebugWindow from "./ScriptDebugWindow.vue";
import $editor from "../../../../../libs/editor/index.js";
self.MonacoEnvironment = {
  getWorker(_, label) {
    if (['typescript', 'javascript'].includes(label)) {
      return new tsWorker()
    }
    return new EditorWorker()
  },
}
export default {
  name: "ScriptEditor",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {ScriptDebugWindow},
  props: {
    item: {
      type: Object,
      required: true
    },
  },
  data(){
    return {
      loginInfo: {
        company: {}
      },
      fileLockNicker: '',
      canEdit: true,
      lastInputTime: 0,
      ding: false,
      debugMethod: '',
      debugMethodList: [],
    }
  },
  mounted() {
    try{
      this.loginInfo = JSON.parse(localStorage.getItem('login-info'));
    }catch (e) {
      this.$router.push('/login');
    }
    if (null == this.loginInfo){
      this.loginInfo = {
        company: {}
      };
      this.$router.push('/login');
      return;
    }
    this.initEditor();
    this.getScriptMembers();
  },
  methods: {
    getScriptMembers(){
      if (!this.item.key.endsWith('mjs')){
        return;
      }
      let moduleName = this.item.key.replace('//', '');
      moduleName = moduleName.substr(0, moduleName.indexOf('/'));
      this.debugMethodList = [];
      // this.$http.post("/api/v1/project/script-members?projectId=" + this.$editor.options.projectId, this.item).then(res => {
      //   this.debugMethodList = res;
      //   if(this.debugMethodList.length > 0 && (this.debugMethod = this.debugMethodList[0]
      //       || !this.debugMethodList.includes(this.debugMethod))){
      //     this.debugMethod = this.debugMethodList[0];
      //   }
      // });

    },
    initEditor: function (reloadCode) {
      monaco.languages.typescript.javascriptDefaults.setDiagnosticsOptions({
        noSemanticValidation: false,
        noSyntaxValidation: false
      });
      monaco.languages.typescript.javascriptDefaults.setCompilerOptions({
        target: monaco.languages.typescript.ScriptTarget.ES2016,
        allowNonTsExtensions: true,
        // lib:[]
      });

      // 初始化编辑器，确保dom已经渲染
      let editorConf = EditorConf();

      // 确定编辑器类型
      const fileType = this.item.name.substring(this.item.name.lastIndexOf('.') + 1);
      if (fileType === 'ts') {
        editorConf.language = 'typescript';
      } else if (fileType === 'js' || fileType === 'mjs') {
        editorConf.language = 'javascript';
      } else if (fileType === 'json') {
        editorConf.language = 'json';
      } else if (fileType === 'html' || fileType === 'htm' || fileType === 'xml' || fileType === 'svg'
          || fileType === 'vue') {
        editorConf.language = 'html';
      } else if (fileType === 'css') {
        editorConf.language = 'css';
      } else if (fileType === 'md') {
        editorConf.language = 'markdown';
      } else if (fileType === 'mds') {
        editorConf.language = 'fastModel';
      } else if (fileType === 'yml' || fileType === 'yaml') {
        editorConf.language = 'yaml';
      } else {
        editorConf.language = 'plaintext';
      }

      this.item.content = editorConf.value
      if(!this.editor){
        this.editor = monaco.editor.create(this.$refs.container, editorConf);
      }
      let iTextModel = monaco.editor.getModel(Uri.parse(this.item.key));
      if(!iTextModel){
        iTextModel = monaco.editor.createModel(this.item.content, editorConf.language, Uri.parse(this.item.key));
      }else{
        iTextModel.setValue(this.item.content);
      }
      this.editor.setModel(iTextModel);
      this.loadFileContent(editorConf);

      // 监听编辑器内容变化
      this.editor.onDidChangeModelContent(e => {
        this.item.content = this.editor.getValue()
        this.item.needSave = this.item.content !== editorConf.value
        this.item.saveFun = function (){
          editorConf.value = this.content;
        }
      })

      // 创建调试入参
      const debugInputConf = EditorConf();
      debugInputConf.language = 'javascript';
      this.debugInputEditor = monaco.editor.create(this.$refs.inputContainer, debugInputConf);
      let uri = this.item.key + '.debug.mjs'
      let debugInputModel = monaco.editor.getModel(Uri.parse(uri));
      if(!debugInputModel){
        debugInputModel = monaco.editor.createModel(this.item.content, debugInputConf.language, Uri.parse(uri));
      }
      debugInputModel.setValue(`// 入参
export default {
    hello: "hello",
    world: "world"
}`);
      this.debugInputEditor.setModel(debugInputModel);


    },
    loadFileContent(editorConf){
      this.item.loading = true
      this.$http.get("/api/v1/project/read?path=" + this.item.key + "&projectId=" + this.$editor.options.projectId).then(res => {
        editorConf.value = res;
        this.editor.setValue(res);
      }).finally(() => {
        this.item.loading = false
      });
    },
    openDebug(){
      // this.item.content
      // this.$editor.openDebug(this.item)
      // window.open('devtools://devtools/bundled/js_app.html?ws=localhost:4242/bce4c978-e2ac-4c87-8ffd-01929dadb101', '_blank', 'location=yes,scrollbars=yes,status=yes')
      // this.websocket.send(JSON.stringify({
      //   type: 'runDebug',
      //   fileInfo: this.item,
      //   method: this.debugMethod,
      //   params: this.debugInputEditor.getValue()
      // }))
    }
  },
  watch: {
    item: {
      handler(val, oldVal) {
        if (val.key !== oldVal.key) {
          this.initEditor();
        }
      },
      deep: true
    }
  },
  beforeUnmount() {
    if (this.editor) {
      this.editor.dispose();
    }
    // if (this.websocket) {
    //   this.websocket.close();
    // }
  }
}
</script>

<style scoped lang="less">
  .redonly-msg{
    width: 100%;
    position: relative;
    bottom: 40px;
    height: 40px;
    line-height: 40px;
    background: dodgerblue;
    color: #fff;
    z-index: 999;
  }
  .debug-bar{
    position: fixed;
    z-index: 1;
    right: 150px;
    top: 90px;
    height: 20px;
    width: 20px;
    background-color: white;
    box-shadow: rgba(0,0,0,0.2) 1px 1px 5px;
    border-radius: 5px;
    transition: all .3s;
    opacity: 0.2;
    overflow: hidden;
    padding: 5px;
    &:hover{
      opacity: 1;
      width: 500px;
      height: 600px;
    }
    &.active{
      opacity: 1;
      width: 500px;
      height: 600px;
    }

    .icons{
      font-size: 16px;
      span{
        display: inline-block;
        margin-right: 10px;
        color: #b0b0b0;
        width: 20px;
        height: 20px;
        text-align: center;
        line-height: 20px;
        &.active{
          color: #2c3e50;
        }
        &:hover{
          background-color: #e1e1e1;
        }
      }
      .icon-debug{
        color: green;
        &.active{
          color: #b0b0b0;
          &:hover{
            background-color: white;
          }
        }
      }
      .icon{
        height: 18px;
        width: 18px;
        vertical-align: text-top;
        &:hover{
          background-color: #e1e1e1;
        }
      }
      .icon-jieshu{
        margin-left: -2px;
        &.active{
          color: red;
        }
      }
      .ding{
        height: 20px;
        width: 20px;
        float: right;
        padding: 5px;
        &:hover{
          background-color: #d7d7d7;
        }
        &.active{
          background-color: #d7d7d7;
        }
      }
    }

    table{
      border-collapse:collapse;
      border-left: #b9b9b9 solid 1px;
      border-top: #b9b9b9 solid 1px;
      padding: 0;
      width: 100%;
      thead{
        text-align: center;
      }
      td{
        padding: 0;
        margin: 0;
        font-size: 12px;
        border-right: #b9b9b9 solid 1px;
        border-bottom: #b9b9b9 solid 1px;
        color: #888888;
        &.active{
          color: #b9b9b9;
        }
      }
    }

  }
</style>
