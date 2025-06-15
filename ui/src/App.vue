<template>
  <router-view/>
</template>

<style lang="less">
html, body{
  height: 100%;
  font-size: 14px;
  color: var(--base-foreground);
  font-family: -apple-system, BlinkMacSystemFont, Avenir, Helvetica, Arial, sans-serif, iconfont;
  font-variant: tabular-nums;
  line-height: 1.5;
  font-feature-settings: 'tnum';
  vertical-align: baseline;
  background: transparent;
}
#app{
  height: 100%;
}
#development-page{
  user-select: none;
}
html, body, ul, li, h1, h2, h3, h4, h5, h6, p, a, span, div, img, table, tr, td, th, form, input, textarea, button {
  padding: 0;
  margin: 0;
  border: 0;
  outline: 0;
}

html:root{
  --plan-background-color: #F2F2F2;
  --plan-black-background-color: white;
  --plan-background-color-wight: #ECECEC;
  --list-hover-background-color: #E8E8E8;
  --menu-hover-background-color: #E0E0E0;
  --base-foreground: #4d4d4d;
  --base-foreground-light: #969696;
  --base-border-color: #c9c9c9;
  --base-focusBorder: #1785eb;
  --base-focus-color: #D2E2F4;
}

input, textarea, button, select, option {
  border: solid 1px var(--base-border-color);
  padding: 0 5px;
  height: 25px;
  line-height: 25px;
  transition: all 0.3s;

  &:hover, &:focus {
    border-color: var(--base-focusBorder);
  }
}
</style>
<script setup lang="ts">
</script>
