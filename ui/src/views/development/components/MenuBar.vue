<template>
  <div class="menu-bar">
    <div class="menu-bar__item">文件</div>
    <div class="menu-bar__item">编辑</div>
    <div class="menu-bar__item">选项</div>
    <div class="menu-bar__item">跳转</div>
    <div class="menu-bar__item">帮助</div>

  </div>
</template>

<script>
export default {
  name: "MenuBar"
}
</script>

<style scoped lang="less">
  .menu-bar{
    height: 27px;
    z-index: 2;
    position: relative;
    box-sizing: border-box;
    background-color: var(--plan-background-color);
    padding-left: 20px;

    .menu-bar__item{
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 0 12px;
      float: left;
    }
    .menu-bar__item:hover{
      background-color: var(--menu-hover-background-color);
    }
  }
</style>