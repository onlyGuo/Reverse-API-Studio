<template>
  <div>
    <open-editor-tab/>
    <editor/>
  </div>
</template>

<script>
import OpenEditorTab from "./editor/OpenEditorTab.vue";
import Editor from "./editor/Editor.vue";
export default {
  name: "RightArea",
  components: {Editor, OpenEditorTab}
}
</script>

<style scoped>

</style>
