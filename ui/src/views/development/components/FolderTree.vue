<template>
  <div style="height: 100%">
    <div v-for="item in $editor.files">
      <folder-tree-item :item="item" :key="item.path" />
    </div>
  </div>
</template>

<script>
import FolderTreeItem from "./FolderTreeItem.vue";
import $editor from "../../../libs/editor/index.js";
export default {
  name: "FolderTree",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {FolderTreeItem},
  data(){
    return {
      files: []
    }
  },
  mounted() {
    this.$editor.options.treeSelected = null;
    this.$editor.initWorkDir('/api/v1/project/dir?path=/&projectId=' + this.$editor.options.projectId, ()=> {
      this.$forceUpdate();
      this.$editor.treeView = this;
    });
  },
  methods: {
  }
}
</script>

<style scoped lang="less">

</style>
