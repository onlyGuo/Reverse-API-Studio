<template>
  <div style="display: inline-block; margin-right: 5px; color: orange">
<!--    <img style="width: 16px; height: 16px; vertical-align: middle"-->
<!--         v-if="" src="../../../assets/icons/loading.svg"/>-->
<!--    <v-loading v-if="!$editor.thisFile.loading"></v-loading>-->
    <div class="sk-chase" v-if="item.loading">
      <div class="sk-chase-dot"></div>
      <div class="sk-chase-dot"></div>
      <div class="sk-chase-dot"></div>
      <div class="sk-chase-dot"></div>
      <div class="sk-chase-dot"></div>
      <div class="sk-chase-dot"></div>
    </div>
    <span v-else>
      <img src="../../../assets/icons/file.svg" style="height: 16px; width: 16px; vertical-align: middle"
           v-if="item.name.indexOf('.') === -1 || item.name.lastIndexOf('.') === item.name.length"/>
      <span v-else>
        <span class="icon-json" v-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'json'"></span>
        <span class="icon-logo-javascript"
              v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'js'"></span>
        <img src="../../../assets/icons/vue.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'vue'"/>

        <img src="../../../assets/icons/doc.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'md'"/>
        <img src="../../../assets/icons/doc.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'doc'"/>
        <img src="../../../assets/icons/doc.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'docx'"/>

        <img src="../../../assets/icons/lock.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'lock'"/>

        <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'svg'"/>
        <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'jpg'"/>
        <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'jpeg'"/>
        <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'png'"/>
        <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'gif'"/>
        <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'ico'"/>
        <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'icon'"/>

        <img src="../../../assets/icons/html.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'html'"/>
        <img src="../../../assets/icons/html.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'xhtml'"/>
        <img src="../../../assets/icons/html.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'xml'"/>

        <img src="../../../assets/icons/data.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'mds'"/>
        <img src="../../../assets/icons/ts.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'ts'"/>
        <img src="../../../assets/icons/spring-icon.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'yml'"/>
        <img src="../../../assets/icons/spring-icon.svg" style="height: 16px; width: 16px; vertical-align: middle"
             v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase() === 'yaml'"/>


        <img src="../../../assets/icons/file.svg" style="height: 16px; width: 16px; vertical-align: middle" v-else/>
      </span>
    </span>
  </div>
</template>

<script>
export default {
  name: "FileIcon",
  props: {
    item: {
      type: Object,
      required: true
    }
  },
  methods: {
    getFileType(item) {
      return item.name.substr(item.name.lastIndexOf('.') + 1).toLowerCase();
    }
  }
}
</script>

<style scoped lang="less">
.sk-chase {
  width: 12px;
  height: 12px;
  position: relative;
  animation: sk-chase 2.5s infinite linear both;
}

.sk-chase-dot {
  width: 100%;
  height: 100%;
  position: absolute;
  left: 0;
  top: 0;
  animation: sk-chase-dot 2.0s infinite ease-in-out both;
}

.sk-chase-dot:before {
  content: '';
  display: block;
  width: 25%;
  height: 25%;
  background-color: var(--base-foreground-light);
  border-radius: 100%;
  animation: sk-chase-dot-before 2.0s infinite ease-in-out both;
}

.sk-chase-dot:nth-child(1) { animation-delay: -1.1s; }
.sk-chase-dot:nth-child(2) { animation-delay: -1.0s; }
.sk-chase-dot:nth-child(3) { animation-delay: -0.9s; }
.sk-chase-dot:nth-child(4) { animation-delay: -0.8s; }
.sk-chase-dot:nth-child(5) { animation-delay: -0.7s; }
.sk-chase-dot:nth-child(6) { animation-delay: -0.6s; }
.sk-chase-dot:nth-child(1):before { animation-delay: -1.1s; }
.sk-chase-dot:nth-child(2):before { animation-delay: -1.0s; }
.sk-chase-dot:nth-child(3):before { animation-delay: -0.9s; }
.sk-chase-dot:nth-child(4):before { animation-delay: -0.8s; }
.sk-chase-dot:nth-child(5):before { animation-delay: -0.7s; }
.sk-chase-dot:nth-child(6):before { animation-delay: -0.6s; }

@keyframes sk-chase {
  100% { transform: rotate(360deg); }
}

@keyframes sk-chase-dot {
  80%, 100% { transform: rotate(360deg); }
}

@keyframes sk-chase-dot-before {
  50% {
    transform: scale(0.4);
  } 100%, 0% {
      transform: scale(1.0);
    }
}
</style>