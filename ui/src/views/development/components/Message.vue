<template>
  <div class="message animate__animated animate__bounceInRight" :class="{
    info: option.type === 'info',
    success: option.type === 'success',
    warning: option.type === 'warning',
    error: option.type === 'error',
  }" ref="box">
    <div class="title">{{option.title}}
      <!-- 关闭按钮 -->
      <span class="icon icon-close-bold" @click="close"></span>
    </div>
    <div class="content">
<!--      <div class="icon">-->
<!--        <span class="icon icon-info" v-if="option.type === 'info'"></span>-->
<!--        <span class="icon icon-success" v-if="option.type === 'success'"></span>-->
<!--        <span class="icon icon-warning" v-if="option.type === 'warning'"></span>-->
<!--        <span class="icon icon-error" v-if="option.type === 'error'"></span>-->
<!--      </div>-->
      <div class="text">{{option.message}}</div>
    </div>
  </div>
</template>

<script>
import $editor from "../../../libs/editor/index.js";

export default {
  name: "Message",
  computed: {
    $editor() {
      return $editor
    }
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {
          title: '',
          message: '',
          type: 'info',
          id: '',
        }
      }
    },
    id: {
      type: Number,
      default: 0
    }
  },
  data(){
    return {
      autoClose: true
    }
  },
  mounted() {
    // 当鼠标移入时，不自动关闭
    this.$refs.box.addEventListener('mouseenter', () => {
      this.autoClose = false;
    });
    this.$refs.box.addEventListener('mouseleave', () => {
      this.autoClose = true;
    });
    setTimeout(() => {
      if (this.autoClose) {
        this.close();
      } else {
        this.$refs.box.addEventListener('mouseleave', this.close);
      }
    }, 3000)
  },
  methods: {
    close() {
      if(this.$refs.box){
        this.$refs.box.classList.add('animate__animated', 'animate__bounceOutRight');
        setTimeout(() => {
          delete this.$editor.options.messages[this.id];
        }, 300)
      }
    }
  }
}
</script>

<style scoped lang="less">
.animate__animated {animation-duration: 0.3s;}
.message{
  background-color: #42b983;
  color: white;
  min-width: 250px;
  max-width: 450px;
  margin-bottom: 5px;
  box-shadow: 0 0 10px rgba(0,0,0,0.2);
  border-radius: 3px 0 0 3px;
  transition: all .2s;

  // 鼠标移入时，上浮效果
  &:hover{
    margin-top: -2px;
    box-shadow: 0 5px 10px rgba(0,0,0,0.4);
  }

  &.info{
    background-color: #41a2ff;
  }
  &.success{
    background-color: #42b983;
  }
  &.warning{
    background-color: #f0ad4e;
  }
  &.error{
    background-color: #d9534f;
  }

  .title{
    border-bottom: rgba(255, 255, 255, .3) solid 1px;
    padding: 5px;
    .icon-close-bold{
      float: right;
      cursor: pointer;
    }
  }
  .content{
    padding: 10px;
  }
}
</style>
