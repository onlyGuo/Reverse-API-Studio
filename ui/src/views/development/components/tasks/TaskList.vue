<template>
  <div class="task-list" style="height: 100%">
    <folding-panel ref="running" :option="{title: 'RUNNING(' + getStatusCount('running') + ')', isOpen: running.open, btn: [], onOpen: onOpen, onClose: onClose, id: 'running'}"
                   style="overflow: auto; transition: height .2s" :style="running.open ? 'height: calc(100% - 56px);' : 'height: 28px;'">
      <task-item v-for="item in $editor.options.tasks" v-show="item.status === 'running'" :option="item" :key="item.id"></task-item>
    </folding-panel>
    <folding-panel ref="waiting" :option="{title: 'QUEUE(' + getStatusCount('waiting') + ')', isOpen: waiting.open, btn: [], onOpen: onOpen, onClose: onClose, id: 'waiting'}"
                   style="overflow: auto; transition: height .2s" :style="waiting.open ? 'height: calc(100% - 56px);' : 'height: 28px;'">
      <task-item v-for="item in $editor.options.tasks" v-show="item.status === 'waiting'" :option="item" :key="item.id"></task-item>
    </folding-panel>

    <folding-panel ref="all" :option="{title: 'ALL(' + getStatusCount() + ')', isOpen: all.open, btn: [], onOpen: onOpen, onClose: onClose, id: 'all'}"
                   style="overflow: auto; transition: height .2s" :style="all.open ? 'height: calc(100% - 56px);' : 'height: 28px;'">
      <task-item v-for="item in $editor.options.tasks" :option="item" :key="item.id"></task-item>
    </folding-panel>
  </div>
</template>

<script>
import TaskItem from "./TaskItem.vue";
import FoldingPanel from "../FoldingPanel.vue";
import $editor from "../../../../libs/editor/index.js";

export default {
  name: "TaskList",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {TaskItem, FoldingPanel},
  data(){
    return {
      taskList: [],
      running: {
        open: true
      },
      waiting: {
        open: false
      },
      all: {
        open: false
      }
    }
  },
  mounted() {
  },
  methods: {
    getStatusCount(status){
      if(!status){
        return this.$editor.options.tasks.length;
      }
      let count = 0;
      for(let i = 0; i < this.$editor.options.tasks.length; i++){
        if(this.$editor.options.tasks[i].status === status){
          count++;
        }
      }
      return count;
    },
    onOpen(id){
      if(id === 'running'){
        this.waiting.open = false;
        this.all.open = false;
        this.running.open = true;
        this.$refs.waiting.reloadStatus(this.waiting.open);
        this.$refs.all.reloadStatus(this.all.open);
      }else if (id === 'waiting'){
        this.running.open = false;
        this.all.open = false;
        this.waiting.open = true;
        this.$refs.running.reloadStatus(this.running.open);
        this.$refs.all.reloadStatus(this.all.open);
      }else{
        this.running.open = false;
        this.waiting.open = false;
        this.all.open = true;
        this.$refs.waiting.reloadStatus(this.waiting.open);
        this.$refs.running.reloadStatus(this.running.open);
      }
    },
    onClose(id){
      if (id === 'running') {
        this.running.open = false;
      } else {
        this.waiting.open = false;
      }
    }
  }

}
</script>

<style scoped>

</style>
