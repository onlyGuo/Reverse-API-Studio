<template>
  <div class="main" id="development-page" ref="developmentPage">
    <menu-bar></menu-bar>
    <div style="height: calc(100% - 27px); float: left">
      <left-area :sendChatMessage="sendChatMessage"></left-area>
    </div>
    <div style="height: calc(100% - 27px); float: right; vertical-align: top"
         :style="'width: calc(100% - ' + (49 + $editor.settings.leftArea.width) +'px)'">
      <right-area></right-area>
    </div>
    <tree-menu ref="treeMenu"></tree-menu>
    <prompt v-for="item in $editor.promptWindowList" :option="item"></prompt>
    <confirm v-for="item in $editor.confirmWindowList" :option="item"></confirm>
    <prompt-fields v-for="item in $editor.promptFieldsWindowList" :option="item"></prompt-fields>
    <div style="
              position: fixed;
              z-index: 3; right: 0">
      <message v-for="item in $editor.options.messages" :option="item" :id="item.id" :key="item.id"></message>
    </div>

  </div>
</template>

<script>
import Message from "./components/Message.vue";
import Confirm from "./components/Confirm.vue";
import Prompt from "./components/Prompt.vue";
import TreeMenu from "./components/tree-menu/TreeMenu.vue";
import RightArea from "./components/RightArea.vue";
import LeftArea from "./components/LeftArea.vue";
import MenuBar from "./components/MenuBar.vue";
import $editor from "../../libs/editor/index.js";
import PromptFields from "./components/PromptFields.vue";

export default {
  name: "Index",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {PromptFields, Message, Confirm, Prompt, TreeMenu, RightArea, LeftArea, MenuBar},
  data(){
    return {
      loginInfo: {
        company: {}
      },
    }
  },
  created() {
  },
  mounted() {
    // 添加Ctrl+S快捷键的监听
    window.addEventListener("keydown", this.onKeyDown);
    this.$editor.contentMenu = this.$refs.treeMenu
    this.$editor.promptWindow = this.$refs.prompt
  },
  beforeDestroy() {

  },
  unmounted() {
    // 移除Ctrl+S快捷键的监听
    window.removeEventListener("keydown", this.onKeyDown);
  },
  methods: {
    onKeyDown(e) {
      //可以判断是不是mac，如果是mac,ctrl变为花键
      if (e.keyCode === 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
        e.preventDefault();
        this.$editor.save();
      }
      if (e.keyCode === 82 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
        e.preventDefault();
        this.$editor.save();
      }
    },
    sendChatMessage(msg) {
    },
  }
}
</script>

<style scoped lang="less">
html, body{
  width: 100%;
  height: 100%;
}
.main{
  height: 100%;
  width: 100%;
  cursor: default;
}
</style>
