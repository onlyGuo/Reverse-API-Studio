<template>
  <div class="debug-window">
    <iframe
      src="devtools://devtools/bundled/js_app.html?ws=localhost:4242/f31ee18c-6f0c-468b-9b4c-3635441bb45b"
      frameborder="0"
      width="100%"
      allow="geolocation; microphone; camera; midi; vr; accelerometer; gyroscope; payment; ambient-light-sensor; encrypted-media; usb"
      height="100%"></iframe>
  </div>
</template>

<script>
export default {
  name: "ScriptDebugWindow",
  props: {
    item: {
      type: Object,
      required: true
    },
  },
  mounted() {

  }
}
</script>

<style scoped lang="less">
.debug-window{
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: white;
  z-index: 2;
}
</style>