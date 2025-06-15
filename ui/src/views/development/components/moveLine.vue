<template>
    <div ref="line" class="move-line" :class="{x: type === 'x', y: type === 'y'}"
         :style="'left:' + left + 'px; top:' + top + 'px; width:' + width + ';height:' + height"
         @mousedown="onMousedown">
    </div>
</template>

<script>
export default {
  name: "moveLine",
  props: {
    left: {
      type: Number,
      default: 0
    },
    top: {
      type: Number,
      default: 0
    },
    bottom: {
      type: Number,
      default: 0
    },
    width: {
      type: String,
      default: "0px"
    },
    height: {
      type: String,
      default: "0px"
    },
    type: {
      type: String,
      default: "x"
    },
    onMove: {
      type: Function,
      default: function () {}
    }
  },
  data(){
    return {
      option: {
        left: 0,
        top: 0,
      }
    }
  },
  mounted(){
    this.option.left = this.left;
    this.option.top = this.top;
  },
  methods: {
    onMousedown(e){
      window.addEventListener("mousemove", this.onMousemove);
      window.addEventListener("mouseup", this.onMouseup);
      // this.$refs.line.addEventListener("mousemove", this.onMousemove);
      // this.$refs.line.addEventListener("mouseup", this.onMouseup);
    },
    onMousemove(e){
      if (this.type === "x") {
        this.option.left = e.pageX - this.$refs.line.offsetWidth / 2;
      } else if (this.type === "y") {
        this.option.top = e.pageY - this.$refs.line.offsetHeight / 2;
      }else{
        this.option.left = e.pageX - this.$refs.line.offsetWidth / 2;
        this.option.top = e.pageY - this.$refs.line.offsetHeight / 2;
      }
      this.onMove(this.option);
    },
    onMouseup(e){
      window.removeEventListener("mousemove", this.onMousemove);
      window.removeEventListener("mouseup", this.onMouseup);
    }
  },
}
</script>

<style scoped lang="less">
.move-line{
  position: fixed;
  background-color: var(--base-focusBorder);
  opacity: 0;
  transition: opacity .3s;
  z-index: 2;

  &.x{
    cursor: col-resize;
  }
  &.y{
    cursor: row-resize;
  }
}
.move-line:hover{
  opacity: 1;
}
</style>