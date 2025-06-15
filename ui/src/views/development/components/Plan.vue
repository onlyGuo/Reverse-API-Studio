<template>
  <div class="plan">
    <div class="title">{{title}}
      <!-- titleBar插槽 -->
      <div class="title-bar"><slot name="titleBar"></slot></div>

    </div>
    <div class="content">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "Plan",
  props: {
    title: String,
  },
}
</script>

<style scoped lang="less">
  .plan{
    height: 100%;
    width: 100%;
    background-color: var(--plan-background-color);
    //border: 1px solid var(--base-border-color);

    .title{
      background-color: var(--plan-background-color-wight);
      flex-direction: row;
      user-select: none;
      justify-content: space-between;
      align-items: center;
      padding: 0 8px;
      border-top: 1px solid var(--base-border-color);
      box-sizing: border-box;
      height: 28px;
      line-height: 28px;
    }
    .title-bar{
      display: inline-block;
      vertical-align: top;
      position: fixed;
      right: 0;
    }
    .content{
      height: calc(100% - 28px);
      background-color: var(--plan-background-color);
    }
  }
</style>