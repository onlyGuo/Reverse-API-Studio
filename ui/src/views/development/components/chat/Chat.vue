<template>
  <div style="height: 100%">
    <div style="height: calc(100% - 100px); overflow-y: scroll;overflow-wrap: anywhere;">
      <div class="chat-content" v-for="item in messages" :key="item">
        <div class="message my" v-if="item.formId == loginInfo.user.id">
          <div class="message-header">
            <div class="message-header-left">
              <div class="message-header-left-name">
                <span>{{item.form}}</span>
              </div>
              <div class="message-header-left-avatar">
                <img :src="item.avatar" alt="" v-if="item.avatar">
                <img src="../../../../assets/icons/default-avatar.png" alt="" v-else>
              </div>
            </div>
          </div>
          <div class="message-content">
            <div v-for="msg in item.content" :style="'display: ' + msg.type === 'code'? 'block' : 'inline-block'" style="user-select: text; cursor: text">
              <span v-if="msg.type === 'text'">{{msg.value}}</span>
              <a v-if="msg.type === 'file'" class="link" @click="$editor.openFileKey(msg.attr)">{{msg.value}}</a>
              <pre v-if="msg.type === 'code'" v-highlight v-html="formatCode(msg.value)" style="overflow: auto"></pre>
            </div>
          </div>
        </div>
        <div v-else class="message">
          <div class="message-header">
            <div class="message-header-left">
              <div class="message-header-left-avatar">
                <img :src="item.avatar" alt="" v-if="item.avatar">
                <img src="../../../../assets/icons/default-avatar.png" alt="" v-else>
              </div>
              <div class="message-header-left-name">
                <span>{{item.form}}</span>
              </div>
            </div>
          </div>
          <div class="message-content">
            <div v-for="msg in item.content" :style="'display: ' + msg.type === 'code'? 'block' : 'inline-block'" style="user-select: text; cursor: text">
              <span v-if="msg.type === 'text'">{{msg.value}}</span>
              <a v-if="msg.type === 'file'" class="link" @click="$editor.openFileKey(msg.attr)">{{msg.value}}</a>
              <pre v-if="msg.type === 'code'" v-highlight v-html="formatCode(msg.value)" style="overflow: auto"></pre>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="chat-edit">
      <div class="chat-edit-input">
        <textarea ref="input" placeholder="输入消息" style="resize: none;"></textarea>
      </div>
      <div class="chat-edit-button">
        <button title="添加连接" class="icon-btn" @click="appendLink"><img src="../../../../assets/icons/link.svg"></button>
        <button title="添加代码" class="icon-btn" @click="openCodeEdit"><img src="../../../../assets/icons/code.svg"></button>
        <button title="发送" class="icon-btn send" @click="send"><img src="../../../../assets/icons/send.svg"></button>
      </div>
    </div>
    <window title="选择文件" :height="560" :width="300" :onClose="closeSelectWindow" v-if="showLinkSelect" :callback="selectLinkWindow">
      <folder-tree></folder-tree>
    </window>
    <window title="填入代码" :height="560" :width="700" :onClose="closeCodeEdit" v-if="showCodeEdit" :callback="selectCodeEdit">
      选择代码语言： <select v-model="language">
        <option>javascript</option>
        <option>java</option>
        <option>html</option>
        <option>json</option>
        <option>xml</option>
        <option>yaml</option>
        <option>text</option>
      </select>
      <div ref="code" style="height: calc(100% - 30px); width: 100%"></div>
    </window>
  </div>
</template>

<script>
import Window from "../Window.vue";
import * as monaco from "monaco-editor";
import FolderTree from "../FolderTree.vue";
import $editor from "../../../../libs/editor/index.js";
export default {
  name: "Chat",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {FolderTree, Window},
  props: {
    sendChatMessage: Function,
  },
  data(){
    return {
      showLinkSelect: false,
      showCodeEdit: false,
      messages: [],
      language: 'javascript',
      loginInfo: {
        company: {}
      },
    }
  },
  created() {
    // this.$editor.options.projectId = this.$route.query.id
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
    }
  },
  mounted() {
    // this.initEditor();
    if (!this.$editor.options.chatMessages){
      this.$editor.options.chatMessages = JSON.parse(localStorage.getItem('chat-room-messages') || '{}');
      if (!this.$editor.options.chatMessages['project' + this.$editor.options.projectId]){
        this.$editor.options.chatMessages['project' + this.$editor.options.projectId] = [];
      }
    }
    this.messages = this.$editor.options.chatMessages['project' + this.$editor.options.projectId];
  },
  methods: {
    appendLink(){
      this.showLinkSelect = true;
      // const left = this.$refs.input.value.substring(0, this.$refs.input.selectionStart);
      // const right = this.$refs.input.value.substring(this.$refs.input.selectionEnd);
      // this.$refs.input.value = left + link + right;
    },
    closeSelectWindow(){
      this.showLinkSelect = false;
    },
    selectLinkWindow(){
      if (this.$editor.options.treeSelected){
        const link = '[file:' + this.$editor.options.treeSelected.key + ']' + this.$editor.options.treeSelected.name + '[/file]';
        const left = this.$refs.input.value.substring(0, this.$refs.input.selectionStart);
        const right = this.$refs.input.value.substring(this.$refs.input.selectionEnd);
        this.$refs.input.value = left + link + right;
      }
    },
    formatCode(code){
      return this.$hljs.highlightAuto(code).value;
    },
    initEditor(){
      console.log('initEditor');
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
      // editorConf.readOnly = true;
      editorConf.language = this.language;

      if(!this.editor){
        this.editor = monaco.editor.create(this.$refs.code, editorConf);
      }
    },
    openCodeEdit(){
      this.showCodeEdit = true;
      this.$nextTick(()=>{
        this.initEditor();
      });
    },
    closeCodeEdit(){
      this.editor.dispose()
      this.editor = null;
      this.showCodeEdit = false;
    },
    selectCodeEdit(){
      const code = this.editor.getValue();
      const left = this.$refs.input.value.substring(0, this.$refs.input.selectionStart);
      const right = this.$refs.input.value.substring(this.$refs.input.selectionEnd);
      this.$refs.input.value = left + '\r\n[code:' + this.language + ']' + code + '[/code]' + right;
    },
    send(){
      const content = this.$refs.input.value;
      if (!content){
        return;
      }
      const data = [];
      // 使用正则解析内容: [code:xxx]value[/code]、[file:xxx]value[/file]
      const reg = /\[code:(.*?)\]([\s\S]*?)\[\/code\]|\[file:(.*?)\]([\s\S]*?)\[\/file\]/g;
      let result;
      let lastIndex = 0;
      while ((result = reg.exec(content)) !== null) {
        if (result.index > lastIndex){
          data.push({
            type: 'text',
            value: content.substring(lastIndex, result.index)
          });
        }
        if (result[1]){
          data.push({
            type: 'code',
            attr: result[1],
            value: result[2]
          });
        }else if (result[3]){
          data.push({
            type: 'file',
            attr: result[3],
            value: result[4]
          });
        }
        lastIndex = reg.lastIndex;
      }
      if (lastIndex < content.length){
        data.push({
          type: 'text',
          value: content.substring(lastIndex)
        });
      }
      this.sendChatMessage(data);
      this.$refs.input.value = '';
    }
  },
  watch: {
    language(){
      let value = this.editor.getValue();
      this.editor.dispose();
      this.editor = null;
      this.initEditor();
      this.editor.setValue(value);
    }
  }
}
</script>

<style scoped lang="less">
.chat-content{
  margin-top: 10px;
  .message{
    &.my{
      .message-header-left{
        text-align: right;
      }
      .message-content{
        margin-top: -15px;
        margin-left: 10px;
        margin-right: 50px;
        background-color: var(--base-focus-color);
        padding: 10px;
        border-radius: 10px 0 10px 10px;
        white-space: pre-wrap;
      }
    }
    .message-header{
      .message-header-left-avatar{
        margin-left: 10px;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        overflow: hidden;
        display: inline-block;
        vertical-align: top;
        img{
          width: 100%;
          height: 100%;
        }
      }
      .message-header-left-name{
        display: inline-block;
        vertical-align: top;
        .time{
          font-size: 12px;
        }
      }
    }
    .message-content{
      margin-top: -15px;
      margin-left: 50px;
      margin-right: 10px;
      background-color: var(--list-hover-background-color);
      padding: 10px;
      border-radius: 0 10px 10px 10px;
      white-space: pre-wrap;

      .code{
        padding: 10px;
        border-radius: 5px;
        font-size: 12px;
        white-space: pre-wrap;
        word-break: break-all;
      }
      .link{
        color: #1890ff;
        cursor: pointer;
      }
      .file{
        display: inline-block;
        vertical-align: top;
        margin-left: 10px;
        img{
          width: 20px;
          height: 20px;
        }
      }
    }
  }
}
.chat-edit{
  height: 100px;
  .chat-edit-input{
    vertical-align: top;
    display: inline-block;
    width: calc(100% - 30px);
    height: 100%;
    overflow: hidden;
    textarea{
      height: calc(100% - 2px);
      width: calc(100% - 10px);
      padding: 0 5px 0 5px;
      border-left: none;
      border-right: none;
      border-bottom: none;
      margin: 0;
    }
  }
  .chat-edit-button{
    border-top: var(--base-border-color) solid 1px;
    vertical-align: top;
    display: inline-block;
    width: 30px;
    height: calc(100% - 1px);
    .icon-btn{
      height: 30px;
      width: 30px;
      border-radius: 50%;
      text-align: center;
      display: block;
      margin-bottom: 4px;
      &.send{
        background-color: var(--base-focusBorder);
      }
      img{
        height: 20px;
        width: 20px;
        vertical-align: middle;
      }
    }
  }

}
</style>
