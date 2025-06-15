<template>
  <div class="editor">
    <div class="title" v-if="$editor.thisFile">
      <file-icon :item="$editor.thisFile" />
      <div style="display: inline-block; vertical-align: top">{{$editor.thisFile.needSave ? '[未保存] ' : ''}}{{ $editor.thisFile.key }}</div>
    </div>
    <div class="content" :style="'height:' + (vh - $editor.settings.consolePlan.height - 81) + 'px'">
      <script-editor v-for="item in $editor.openFiles" v-show="$editor.thisFile === item" :item="item" :key="item.key"/>
    </div>

    <div class="console-plan" :style="'height:' + $editor.settings.consolePlan.height + 'px'">
      <move-line :width="'calc(100% - ' + ($editor.settings.leftArea.width + 48) + 'px)'"
                 :left="$editor.settings.leftArea.width + 48"
                 height="4px" type="y" :top="vh - $editor.settings.consolePlan.height" :onMove="onResize"/>
      <plan title="OUTPUT">
        <console-plan ref="consolePlan"></console-plan>
        <template #titleBar>
          <div class="title-bar">
            <div class="title-bar-item" @click="clearConsole">
              <img src="../../../../assets/icons/tree-menu/delete.svg" class="title-bar-delete" title="清空日志" />
            </div>
            <div class="title-bar-item">
              <input type="checkbox" v-model="autoScrool" style="vertical-align: middle"/>
              自动滚动
            </div>
          </div>
        </template>
      </plan>
    </div>
  </div>
</template>

<script>
import ScriptEditor from "./core/ScriptEditor.vue";
import MoveLine from "../moveLine.vue";
import Plan from "../Plan.vue";
import ConsolePlan from "./ConsolePlan.vue";
import FileIcon from "../FileIcon.vue";
import $editor from "../../../../libs/editor/index.js";

export default {
  name: "Editor",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {ScriptEditor, MoveLine, Plan, ConsolePlan, FileIcon},
  data(){
    return {
      vh: document.body.offsetHeight,
      autoScrool: true,
    }
  },
  mounted() {
    window.onresize = () => {
      this.vh = document.body.offsetHeight;
    }
  },
  methods: {
    onResize(config){
      this.$editor.settings.consolePlan.height = this.vh - config.top;
    },
    clearConsole(){
      this.$refs.consolePlan.logs = [];
    },
  },
  watch: {
    autoScrool(val){
      this.$refs.consolePlan.autoScrool = val;
    }
  }
}
</script>

<style scoped lang="less">
  .editor{
    width: 100%;
    height: 100%;

    .title{
      font-size: 12px;
      height: 24px;
      line-height: 24px;
      vertical-align: middle;
      width: calc(100% - 10px);
      border-bottom: solid 1px var(--plan-background-color-wight);
      padding-left: 10px;
      color: var(--base-foreground-light);
      cursor: pointer;

      &:hover{
        color: var(--base-foreground);
      }
    }

    .console-plan{
      width: 100%;
      border-top: solid 1px var(--plan-background-color-wight);
      position: fixed;
      bottom: 0;

      .title-bar{
        .title-bar-item{
          display: inline-block;
          margin-right: 10px;
          .title-bar-delete{
            height: 20px;
            width: 20px;
            vertical-align: middle;
            &:hover{
              background-color: var(--menu-hover-background-color);
            }
          }
        }
      }
    }

    .content{
      //background-color: #42b983;
    }
  }
</style>
