<template>
  <div class="console" ref="box">
<!--    <virtual-list ref="virtualListComponents" :style="'height: 100%; overflow-y: auto;'" :data-component="logComponent" :data-sources="logs" data-key="text"/>-->
    <div class="logs" v-for="log in logs" :key="log.text" >
      <log :log="log.text" :key="log.text"></log>
    </div>
  </div>
</template>

<script>
import VirtualList from 'vue3-virtual-scroll-list';
import Log from "./core/Log.vue";
import $editor from "../../../../libs/editor/index.js";
export default {
  name: "ConsolePlan",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {'virtual-list': VirtualList, Log},
  data(){
    return {
      lock: false,
      logs: [],
      logLength: 5000,
      logComponent: Log,
      height: 220,
      autoScrool: true,
    }
  },
  mounted() {
    this.initWebSocket();
    this.$refs.box.addEventListener('onresize', this.resize);
  },
  unmounted() {
    if(this.websocket !== null){
      this.websocket.close();
    }
  },
  methods: {
    initWebSocket(){
      // this.websocket = new WebSocket("ws://" + location.host + "/api/v1/ws?_docker-log://:" +
      //     this.$editor.options.projectId + "&projectId=" + this.$editor.options.projectId);
      // this.websocket.onmessage = this.onmessage;
      // this.websocket.onopen = this.onopen;
      // this.websocket.onerror = this.onerror;
      // this.websocket.onclose = this.onclose;
    },
    onmessage(event) {
      const msgs = event.data.split('\n');
      for (let i in msgs) {
        msgs[i] = msgs[i].replace('\r', '');
        if (msgs[i] === '') {
          continue;
        }
        this.logs.push({
          text: msgs[i],
        });
        if (this.logs.length > this.logLength) {
          // this.logs.shift();
          this.logs = [];
        }
      }
      if (this.autoScrool) {
        // this.$refs.virtualListComponents.scrollToBottom();
        // this.$forceUpdate();
        this.$nextTick(() => {
          this.$refs.box.scrollTop = this.$refs.box.scrollHeight;
        });
        // this.$refs.box.scrollTop = this.$refs.box.scrollHeight;
      }
    },
    onopen(event) {
      this.websocket.send("open");
    },
    onerror(event) {
      this.reconnect();
    },
    onclose(event) {
      this.reconnect();
    },
    reconnect(){
      if(this.lock){
        return;
      }
      this.lock = true;
      setTimeout(() => {
        this.initWebSocket();
        this.lock = false;
      }, 2000)
    },
    resize(){
      debugger;
      this.height = this.$refs.box.offsetHeight;
    }
  }
}
</script>

<style scoped lang="less">
  .console{
    width: 100%;
    height: 100%;
    overflow: scroll;
    padding: 0 10px;
    user-select: text;
    cursor: text;

    .logs{
      white-space: nowrap;
      user-select: text;
      cursor: text;
    }
  }
</style>
