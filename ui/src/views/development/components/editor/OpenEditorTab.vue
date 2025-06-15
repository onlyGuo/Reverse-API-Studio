<template>
  <div class="tab-box" v-if="$editor.openFiles && $editor.openFiles.length > 0">
    <div style="border-bottom: solid 1px var(--base-border-color); height: 0; width: 100%; display: flex; position: fixed; margin-top: 27px"></div>
    <open-editor-tab-item v-for="(item, index) in $editor.openFiles"
                          :key="index" :item="item" />
  </div>
</template>

<script>
import OpenEditorTabItem from "./OpenEditorTabItem.vue";
import $editor from "../../../../libs/editor/index.js";

export default {
  name: "OpenEditorTab",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {OpenEditorTabItem}
}
</script>

<style scoped lang="less">
  .tab-box {
    overflow-y: hidden;
    display: flex;
    background-color: var(--plan-background-color);
    //border-bottom: 1px solid var(--base-border-color);
    height: 28px;
  }
</style>
