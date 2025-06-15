<template>
  <div>
    <div class="title" @click.stop="openOrClose">
      <span class="icon" :class="{'icon-zhankai': !open, 'icon-zhankai1': open}"></span>
      {{option.title}}
      <span style="float: right; display: inline-block; height: 25px" v-if="option.btn">
        <img class="btn-group" :class="{disabled: item.disabled}" v-for="item in option.btn" @click.stop="defaultFun(item)"
             :src="`/icons/tree-menu/${item.icon}.svg`" />
      </span>
    </div>
    <div class="content" :class="{open: open}">
      <div class="content-inner">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FoldingPanel",
  props: {
    option: {
      title: String,
      isOpen: {
        type: Boolean,
        default: true
      },
      btn: {
        type: Array,
        default: () => []
      },
      id: {
        type: String,
        default: ''
      },
      onOpen: {
        type: Function,
        default: () => {}
      },
      onClose: {
        type: Function,
        default: () => {}
      }
    }
  },
  data() {
    return {
      open: this.option.isOpen
    };
  },
  methods:{
    openOrClose(){
      this.open = !this.open
      if(this.open){
        this.option.onOpen(this.option.id);
      }else{
        this.option.onClose(this.option.id);
      }
    },
    defaultFun(item){
      if (item.disabled) {
        return;
      }
      if (item.func) {
        item.func(this);
      }
    },
    reloadStatus(status){
      this.open = status;
    }
  }
}
</script>

<style scoped lang="less">
  .title{
    height: 28px;
    line-height: 28px;
    padding: 0 10px;
    background-color: var(--menu-hover-background-color);
    //cursor: pointer;
    //.icon{
    //  cursor: pointer;
    //}
  }
  .content{
    height: 0;
    background-color: var(--plan-background-color);
    transition: height .2s;
    overflow: hidden;
  }
  .content.open{
    height: calc(100% - 28px);
  }
  .btn-group{
    cursor: pointer;
    height: 20px;
    width: 20px;
    margin-top: 5px;
    display: inline-block;
    overflow: hidden;
    margin-left: 5px;

    &:hover{
      background-color: rgba(0,0,0,.1);
    }
    &.disabled{
      opacity: .3;
      cursor: not-allowed;
    }
    &.disabled:hover{
      background-color: transparent;
    }
  }
</style>
