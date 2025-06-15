<template>
  <div class="tab-item" :class="{active: $editor.thisFile === item, needSave: item.needSave}" @click="$editor.openFile(item)">
    <file-icon :item="item" />
    {{item.name}}
    <span class="icon" @click.stop="$editor.closeFile(item)">&times;</span>
  </div>
</template>

<script>
import FileIcon from "../FileIcon.vue";
import $editor from "../../../../libs/editor/index.js";

export default {
  name: "OpenEditorTabItem",
  computed: {
    $editor() {
      return $editor
    }
  },
  components: {FileIcon},
  props: {
    item: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped lang="less">
.tab-item {
  display: flex;
  position: relative;
  align-items: center;
  flex-shrink: 0;
  padding: 0 12px;
  border-right: 1px solid var(--base-border-color);
  height: 28px;
  line-height: 28px;
  font-size: 12px;
  cursor: pointer;
  color: var(--base-foreground);
  background: var(--plan-background-color);
  border-bottom: 1px solid var(--base-border-color);
  box-sizing: border-box;
  z-index: 1;
  border-top: 1px solid var(--base-border-color);

  .icon {
    display: inline-block;
    margin-left: 10px;
    cursor: pointer;
    height: 12px;
    width: 12px;
    font-size: 12px;
    text-align: center;
    line-height: 12px;
    color: transparent;

    &:hover {
      background-color: var(--menu-hover-background-color);
    }
  }

  &.active{
    color: var(--base-foreground);
    background: var(--plan-black-background-color);
    border-bottom: 1px solid var(--plan-black-background-color);
  }
  &.needSave{
    color: dodgerblue;
  }
  &:hover {
    background: var(--plan-background-color-wight);
    &.active {
      background: var(--plan-black-background-color);
    }
    .icon{
      color: var(--base-foreground);
    }
  }
}
</style>
