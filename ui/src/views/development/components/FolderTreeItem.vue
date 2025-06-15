<template>
  <div>
    <div class="file-name" :class="{selected: item.selected}" @click="clickItem" @contextmenu.prevent="openMenu($event)" @dblclick="$editor.openFile(item)">
      <div style="width: 20px; display: inline-block;" :style="'padding-left:' + (10 + 10 * item.level) + 'px'">
          <span class="switch" :class="{'icon-sanjiao1': item.open,'icon-sanjiao3': !item.open}"
                v-if="item.type === 'dir'"></span>
      </div>
      <div v-if="item.type === 'dir'" style="display: inline-block; margin-right: 5px; color: orange">
        <span class="icon-folderopen" style="color: #41a2ff" v-if="isApiFolder(item.key)"></span>
        <span class="icon-folderopen" v-if="!isApiFolder(item.key)"></span>
      </div>
      <div v-else style="display: inline-block; margin-right: 5px; color: orange">
        <span class="icon-json" v-if="item.name.indexOf('.') === -1"></span>
        <span v-else>
            <span class="icon-json" v-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'json'"></span>
            <span class="icon-logo-javascript"
                  v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'js'"></span>

            <img src="../../../assets/icons/vue.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'vue'" />

            <img src="../../../assets/icons/tree-menu/new-api.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'mjs'" />

            <img src="../../../assets/icons/doc.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'md'" />
            <img src="../../../assets/icons/doc.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'doc'" />
            <img src="../../../assets/icons/doc.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'docx'" />

            <img src="../../../assets/icons/lock.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'lock'" />

            <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'svg'" />
            <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'jpg'" />
            <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'jpeg'" />
            <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'png'" />
            <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'gif'" />
            <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'ico'" />
            <img src="../../../assets/icons/image.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'icon'" />

            <img src="../../../assets/icons/html.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'html'" />
            <img src="../../../assets/icons/html.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'xhtml'" />
            <img src="../../../assets/icons/html.svg" style="height: 16px; width: 16px; vertical-align: middle"
                 v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'xml'" />

            <img src="../../../assets/icons/data.svg" style="height: 16px; width: 16px; vertical-align: middle"
               v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'mds'" />
            <img src="../../../assets/icons/spring-icon.svg" style="height: 16px; width: 16px; vertical-align: middle"
               v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'yml'" />
            <img src="../../../assets/icons/spring-icon.svg" style="height: 16px; width: 16px; vertical-align: middle"
               v-else-if="item.name.substr(item.name.lastIndexOf('.') + 1) === 'yaml'" />
            <img src="../../../assets/icons/file.svg" style="height: 16px; width: 16px; vertical-align: middle" v-else/>

          </span>
      </div>
      <span class="icon-wenjian"></span>
      <span>{{ item.name }}</span>
    </div>
    <folder-tree-item :item="it" v-if="item.open" v-for="it in item.child" :key="it.path"></folder-tree-item>
  </div>
</template>

<script>
import TreeMenu from "./tree-menu/TreeMenu.vue";
import $editor from "../../../libs/editor/index.js";

export default {
  name: "FolderTreeItem",
  components: {TreeMenu},
  computed: {
    $editor() {
      return $editor
    }
  },
  props: {
    item: {
      type: Object,
      required: true
    },
  },
  methods:{
    clickItem(){
      this.item.open = !this.item.open;
      this.unselect(this.$editor.files);
      this.item.selected = true;
      this.$editor.options.treeSelected = this.item;
    },
    unselect(files){
      if (files){
        files.forEach(field => {
          field.selected = false;
          if (field.child) {
            this.unselect(field.child);
          }
        });
      }
    },
    openMenu(e){
      let menus = ['delete', 'rename'];
      // 如果是文件夹，则允许新建文件夹
      if (this.item.type === 'dir'){
        menus.push('new-folder');
        menus.push('new-js')
        // 如果是apis，则允许新建api, 且不允许删除, 不允许新建js, 不允许重命名
        if (this.item.key.match(/^\/\/.+\/apis.*/)){
          menus.push('new-api');
          menus.splice(menus.indexOf('new-js'), 1);
          if (this.item.key.match(/^\/\/.+\/apis$/)) {
            menus.splice(menus.indexOf('delete'), 1);
            menus.splice(menus.indexOf('rename'), 1);
          }
        }
        // 如果是assets，新建svg、上传文件，且不允许删除，不允许新建js
        if (this.item.key.match(/^\/\/.+\/assets.*/)){
          menus.push('new-svg');
          menus.push('upload-file');
          menus.splice(menus.indexOf('new-js'), 1);
          if (this.item.key.match(/^\/\/.+\/assets$/)) {
            menus.splice(menus.indexOf('delete'), 1);
            menus.splice(menus.indexOf('rename'), 1);
          }
        }
        // 如果是models，则允许新建模型，且不允许删除，不允许新建js
        if (this.item.key.match(/^\/\/.+\/models.*/)){
          menus.push('new-model');
          menus.splice(menus.indexOf('new-js'), 1);
          if (this.item.key.match(/^\/\/.+\/models$/)) {
            menus.splice(menus.indexOf('delete'), 1);
            menus.splice(menus.indexOf('rename'), 1);
          }
        }
        // 如果是views，则允许新建vue文件，且不允许删除
        if (this.item.key.match(/^\/\/.+\/views.*/)){
          menus.push('new-vue');
          if (this.item.key.match(/^\/\/.+\/views$/)) {
            menus.splice(menus.indexOf('delete'), 1);
            menus.splice(menus.indexOf('rename'), 1);
          }
        }
      }else{
        // 如果是config.js 或 fast-builder.config.js 则不允许删除、不允许重命名
        if (this.item.key.match(/^\/\/.+\/config\.js$/) || this.item.key.match(/^\/\/fast-builder\.config\.js$/)
            || this.item.key.match(/^\/\/fast-builder\.spring\.yml$/)){
          menus.splice(menus.indexOf('delete'), 1);
          menus.splice(menus.indexOf('rename'), 1);
        }
      }

      this.unselect(this.$editor.files);
      this.item.selected = true;
      this.$editor.contentMenu.open({
        x: e.clientX,
        y: e.clientY
      }, menus, this.item);
    },
    isApiFolder(folderKey){
      return /\/\/([a-z]|[A-Z]|_|-)+\/apis/ig.test(folderKey)
    }
  }
}
</script>

<style scoped lang="less">
.file-name:hover{
  background-color: var(--list-hover-background-color)
}
.file-name.selected{
  background-color: var(--base-focus-color);
  //color: var(--plan-background-color-wight);
}
.switch{
  font-size: 12px;
}
</style>
