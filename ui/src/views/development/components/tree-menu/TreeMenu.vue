<template>
  <div class="menu-plan" v-if="options.open" :style="'top:' + options.position.y + 'px;left:' + options.position.x + 'px;'">
    <ul style="list-style: none;">
      <li class="item" v-for="menu in list" v-show="menu.show" @click="menu.item.onClick(options.position.x, options.position.y, options.context, this)">
        <div class="icon">
          <img :src="`/icons/tree-menu/${menu.item.key}.svg`" alt="">
        </div>
        {{menu.item.name}}
        <div style="float: right; margin-left: 20px; color: var(--base-foreground-light)" v-if="menu.item.keymap">{{menu.item.keymap}}</div>
      </li>
    </ul>
  </div>
</template>

<script>
import $editor from "../../../../libs/editor/index.js";

export default {
  name: "TreeMenu",
  computed: {
    $editor() {
      return $editor
    }
  },
  data(){
    return {
      list: [],
      options: {
        open: false,
        position: {
          x: 0,y: 0
        },
        context: undefined
      }
    }
  },
  mounted() {
    this.initItemMenu();
  },
  methods: {
    async initItemMenu(){
      const modules = import.meta.glob('./libs/*.js')
      for (const path in modules) {
        const module = await modules[path]()
        this.list.push({
          item: module.default,
          show: false
        })
      }
      console.log(this.list)
    },
    open(position, items, context) {
      this.list.forEach(menu => {
        menu.show = false;
      });
      items.forEach(item => {
        this.list.find(menu => {
          if (menu.item.key === item) {
            menu.show = true;
          }
        });
      });
      this.options.open = true;
      this.options.position = position;
      this.options.context = context
      document.body.addEventListener('click', this.close)
    },
    close(){
      this.options.open = false;
      document.body.removeEventListener('click', this.close)
    }
  },
}
</script>

<style scoped lang="less">
.menu-plan{
  position: fixed;
  background-color: var(--plan-black-background-color);
  box-shadow: 0 2px 8px var(--menu-hover-background-color);

  .item{

    //height: 25px;
    //line-height: 25px;
    padding: 3px 10px;

    &:hover{
      background-color: var(--plan-background-color);
    }

    .icon{
      height: 14px;
      width: 14px;
      display: inline-block;
      text-align: center;
      vertical-align: middle;
      padding-right: 5px;

      //border-right: var(--base-border-color) solid 1px;

      img{
        height: 100%;
        width: 100%;
      }
    }
  }
}
</style>
