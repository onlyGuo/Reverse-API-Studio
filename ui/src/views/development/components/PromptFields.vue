<template>
  <div ref="box" class="box" :style="'height: ' + option.height + ';width: ' +
      option.width + ';left:' + position.x + 'px;top:' + position.y + 'px;'">
    <div class="prompt__header" @mousedown="onMousedown">
      <span class="prompt__title">{{ option.title }}</span>
      <button class="prompt__close" @click="close">
        <span class="icon icon-close-bold"></span>
      </button>
    </div>
    <div class="prompt__body">
      <div style="height: 50px; line-height: 50px; padding-left: 20px" v-for="field in option.fieldList">
        <input type="text" :placeholder="field.placeholder" v-model="field.value" style="width: 290px"/>
      </div>
    </div>
    <div class="prompt__footer">
      <button class="button button--primary active" @click="confirm">确定</button>
      <button class="button button--primary" @click="close">取消</button>
    </div>
  </div>
</template>

<script>
import $editor from "../../../libs/editor/index.js";

export default {
  name: "PromptFields",
  computed: {
    $editor() {
      return $editor
    }
  },
  props: {
    option: {
      type: Object,
      required: true
    },
  },
  methods: {
    close() {
      // this.$refs.box.classList.add('animate__animated', 'animate__bounceOut');
      this.$refs.box.classList.add('fade-out');
      setTimeout(() => {
        delete this.$editor.promptFieldsWindowList[this.option.id];
      }, 250);
    },
    onMousedown(e) {
      this.startX = e.clientX;
      this.startY = e.clientY;
      this.startPosition = {
        x: this.position.x,
        y: this.position.y
      };
      document.addEventListener('mousemove', this.onMousemove);
      document.addEventListener('mouseup', this.onMouseup);
    },
    onMousemove(e) {
      this.position.x = this.startPosition.x + e.clientX - this.startX;
      this.position.y = this.startPosition.y + e.clientY - this.startY;
      if (this.position.x < 0) {
        this.position.x = 0;
      }
      if (this.position.y < 0) {
        this.position.y = 0;
      }
      if (this.position.x > document.body.clientWidth - this.$refs.box.clientWidth) {
        this.position.x = document.body.clientWidth - this.$refs.box.clientWidth;
      }
      if (this.position.y > document.body.clientHeight - this.$refs.box.clientHeight) {
        this.position.y = document.body.clientHeight - this.$refs.box.clientHeight;
      }
    },
    onMouseup() {
      document.removeEventListener('mousemove', this.onMousemove);
      document.removeEventListener('mouseup', this.onMouseup);
    },
    // 抖动窗口
    shake() {
      // 先将窗口处于最顶层
      this.$refs.box.style.zIndex = this.$editor.promptFieldsWindowList.length;
      // 开始抖动
      this.$refs.box.classList.add('animate__animated', 'animate__shakeX');
      setTimeout(() => {
        this.$refs.box.classList.remove('animate__animated', 'animate__shakeX');
      }, 500);
    },
    // 确认
    confirm() {
      const value = {};
      this.option.fieldList.forEach(field => {
        value[field.name] = field.value;
      });
      if (this.option.callback(value) === false) {
        return;
      }
      this.close();
    }



    // show() {
    //   this.$refs.box.classList.add("fade-in");
    //   setTimeout(() => {
    //     this.$refs.box.classList.remove('fade-in');
    //   }, 300);
    // }
  },
  data(){
    return {
      position: {
        x: 0,
        y: 0,
      },
      startX: 0,
      startY: 0,
      startPosition: {
        x: 0,
        y: 0,
      },
      value: "",
    }
  },
  mounted() {
    this.position = {
      x: (document.body.clientWidth - this.$refs.box.clientWidth) / 2,
      y: (document.body.clientHeight - this.$refs.box.clientHeight) / 2
    };

    this.$refs.box.classList.add('animate__animated', 'animate__bounceInDown');
    setTimeout(() => {
      this.$refs.box.classList.remove('animate__animated', 'animate__bounceInDown');
    }, 500);

    this.option.window = this;
    console.log(this.option);
  }
}
</script>

<style scoped lang="less">
.animate__animated {animation-duration: 0.5s;}
//.prompt{
//  height: 100%;
//  width: 100%;
//  position: fixed;
//  z-index: 10;
//  //background-color: rgba(0,0,0,.2);
//
//
//}
.box{
  //box-shadow: 0 0 10px rgba(0,0,0,.2);
  border: var(--base-border-color) solid 1px;
  position: fixed;

  // 窗口打开和关闭动画
  &.fade-in{
    animation: fadeIn .3s ease-in;
  }
  &.fade-out{
    animation: fadeOut .3s ease-out;
  }

  @keyframes fadeIn{
    0%{
      opacity: 0;
      margin-top: -100px;
    }
    100%{
      opacity: 1;
      margin-top: 0;
    }
  }
  @keyframes fadeOut{
    0%{
      opacity: 1;
      margin-top: 0;
    }
    100%{
      opacity: 0;
      margin-top: -100px;
    }
  }
}

.prompt__header{
  background-color: var(--plan-background-color);
  height: 25px;

  .prompt__title{
    width: calc(100% - 50px);
    display: inline-block;
    line-height: 25px;
    margin-left: 5px;
  }
  .prompt__close{
    float: right;
    line-height: 25px;
    margin-right: 5px;
  }
}
.icon{
  font-family: -apple-system, BlinkMacSystemFont, Avenir, Helvetica, Arial, sans-serif, iconfont;;
}

.prompt__body{
  padding: 5px;
  overflow-y: auto;
  height: calc(100% - 75px);
  background-color: var(--plan-black-background-color);
}
.prompt__footer{
  height: 39px;
  border-top: 1px solid var(--base-border-color);
  background-color: var(--plan-background-color);

  .button {
    float: right;
    margin-right: 5px;
    height: 25px;
    margin-top: 8px;
    padding: 0 10px;
    background-color: var(--plan-background-color-wight);
    border: var(--base-border-color) solid 1px;
    transition: all .3s;

    &:hover{
      background-color: var(--base-focus-color);
      border: var(--base-focusBorder) solid 1px;
    }

    &.active {
      border: var(--base-focusBorder) solid 1px;
    }
  }
}
</style>
