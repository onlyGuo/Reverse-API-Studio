<template>
  <div class="left-area" :style="'width:' + (48 + ($editor.settings.leftArea.open? $editor.settings.leftArea.width : 0)) + 'px'">
    <div class="tab-plan">
      <div class="item" :class="{selected: $editor.settings.leftArea.open && $editor.settings.leftArea.tab === 'file'}"
           @click="openOrClosePlan('file')">
        <span class="icon icon-files-empty"></span>
      </div>
      <div class="item" :class="{selected: $editor.settings.leftArea.open && $editor.settings.leftArea.tab === 'task'}"
           @click="openOrClosePlan('task')">
        <img src="../../../assets/icons/task.svg" class="icon" style="height: 30px; width: 30px; margin-top: 5px" />
        <span class="task-count" v-show="$editor.options.tasks.length > 0">{{$editor.options.tasks.length}}</span>
      </div>
      <div class="item" :class="{selected: $editor.settings.leftArea.open && $editor.settings.leftArea.tab === 'chat'}"
           @click="openOrClosePlan('chat')">
        <img src="../../../assets/icons/chat.svg" class="icon" style="height: 27px; width: 27px; margin-top: 5px" />
        <span class="task-count" v-show="$editor.options.tasks.length > 0">{{$editor.options.tasks.length}}</span>
      </div>
    </div>
    <div class="plan-content" >
      <plan title="EXPLORER" v-show="$editor.settings.leftArea.tab === 'file'">
        <folding-panel :option="{title: 'PROJECT', isOpen: true, btn: [
            {icon: 'start', func: startProject, disabled: !($editor.options.running === 'stop')},
            {icon: 'restart', func: restartProject, disabled: !($editor.options.running === 'running')},
            {icon: 'stop', func: stopProject, disabled: !($editor.options.running === 'running' || $editor.options.running === 'resting')},
            {icon: 'new-folder', func: newModule},
        ]}" style="height: 100%">
          <folder-tree />
        </folding-panel>
      </plan>
      <plan title="TASKS" v-show="$editor.settings.leftArea.tab === 'task'">
        <task-list></task-list>
      </plan>
      <plan title="CHAT" v-show="$editor.settings.leftArea.tab === 'chat'">
        <chat :sendChatMessage="sendChatMessage"></chat>
      </plan>
      <move-line v-if="$editor.settings.leftArea.open" :left="48 + ($editor.settings.leftArea.open? $editor.settings.leftArea.width - 2 : 0)"
                 :top="28" width="4px" height="calc(100% - 28px)" type="x" :onMove="onMove"></move-line>
    </div>
  </div>
</template>

<script>
import Chat from "./chat/Chat.vue";
import TaskList from "./tasks/TaskList.vue";
import TreeMenu from "./tree-menu/TreeMenu.vue";
import MoveLine from "./moveLine.vue";
import FolderTree from "./FolderTree.vue";
import FoldingPanel from "./FoldingPanel.vue";
import Plan from "./Plan.vue";
import $editor from "../../../libs/editor/index.js";

export default {
  name: "LeftArea",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {Chat, TaskList, TreeMenu, MoveLine, FolderTree, FoldingPanel, Plan},
  props: {
    sendChatMessage: {
      type: Function,
      required: true
    }
  },
  data(){
    return {
      lastWidth: 0
    }
  },
  methods: {
    openOrClosePlan(tab) {
      if(tab !== this.$editor.settings.leftArea.tab) {
        this.$editor.settings.leftArea.tab = tab;
        if (!this.$editor.settings.leftArea.open){
          this.$editor.settings.leftArea.width = this.lastWidth;
          this.lastWidth = 0;
          this.$editor.settings.leftArea.open = true;
          this.$editor.settings.leftArea.tab = tab;
        }
      } else {
        if (this.$editor.settings.leftArea.open){
          this.lastWidth = this.$editor.settings.leftArea.width;
          this.$editor.settings.leftArea.width = 0;
          this.$editor.settings.leftArea.open = false;
        }else{
          this.$editor.settings.leftArea.width = this.lastWidth;
          this.lastWidth = 0;
          this.$editor.settings.leftArea.open = true;
          this.$editor.settings.leftArea.tab = tab;
        }
      }
    },
    onMove(e) {
      this.$editor.settings.leftArea.width = e.left - 48 + 2;
    },
    newModule() {
      this.$editor.prompt('新建模块', '输入模块名称', (name) => {
        if (name) {
          this.$http.post('/api/v1/project/module/new?moduleName=' + name + "&projectId=" +
              this.$editor.options.projectId, {}).then(res => {
            this.$http.get('/api/v1/project/dir?path=/&projectId=' + this.$editor.options.projectId).then(res => {
              for (let i in res){
                if (res[i].name === name){
                  this.$editor.files.push(res[i])
                  return
                }
              }
            })
          });
        }
      }, 'newModule');
    },
    startProject(){
      this.$http.post("api/v1/internal-task/register", {
        name: '启动项目',
        action: 'project-start',
        params: {
          projectId: this.$editor.options.projectId
        }
      }).then(() => {
        this.$editor.options.running = 'running';
        this.$editor.messageInfo('启动项目', '项目开始启动，请在任务列表中查看进度');
      }).catch((e) => {
        this.$editor.messageError('启动项目', '项目启动失败:' + e.message);
      })
    },
    stopProject(){
      this.$http.post("api/v1/project/" + this.$editor.options.projectId + "/stop").then(() => {
        this.$editor.options.running = 'stop';
        this.$editor.messageSuccess('停止项目', '项目已停止');
      }).catch((e) => {
        this.$editor.messageError('停止项目', '项目停止失败:' + e.message);
      })
    },
    restartProject(){
      this.$http.post("api/v1/project/" + this.$editor.options.projectId + "/stop").then(() => {
        this.$editor.options.running = 'stop';
        this.startProject();
      }).catch((e) => {
        this.$editor.messageError('重启项目', '项目停止失败:' + e.message);
      })
    },
  },
  mounted() {
    // this.$refs.treeMenu.open(['delete']);
  },
}
</script>

<style scoped lang="less">
  .left-area{
    height: 100%;
    background-color: var(--plan-background-color-wight);
    border-right: 1px solid var(--base-border-color);
    .tab-plan{
      height: calc(100% - 1px);
      display: inline-block;
      flex-direction: column;
      justify-content: space-between;
      //border-right: 1px solid var(--base-border-color);
      border-top: 1px solid var(--base-border-color);
      width: 48px;

      .item{
        width: 100%;
        height: 40px;
        text-align: center;
        margin-top: 8px;
        cursor: pointer;
        border-left: 3px solid transparent;

        .icon{
          font-size: 20px;
          width: 20px;
          line-height: 40px;
          cursor: pointer;
        }
        .task-count{
          display: inline-block;
          padding: 2px 5px;
          background-color: #41a2ff;
          color: white;
          border-radius: 5px;
          font-size: 8px;
          position: relative;
          top: -40px;
          left: 10px;
          vertical-align: center;
          -webkit-box-shadow: 0 0 5px rgba(0,0,0,.2);
        }
      }
      .item.selected{
        border-left: 3px solid var(--base-focusBorder);
      }
    }

    .plan-content{
      width: calc(100% - 49px);
      height: 100%;
      display: inline-block;
      vertical-align: top;
      border-left: 1px solid var(--base-border-color);
    }
  }
</style>
