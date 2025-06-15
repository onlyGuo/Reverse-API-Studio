<template>
  <div class="task">
    <div class="name">{{option.name}}</div>
    <div class="desc" v-if="option.status !== 'waiting'">开始于{{option.startTime}},已运行{{option.runTime}}秒</div>
    <div class="desc">{{option.log}}</div>
    <div class="progress" v-if="option.status !== 'waiting'">
      <div class="progress-border">
        <div class="progress-bar" :style="`width: ${option.progress}%`"></div>
      </div>
      <img src="../../../../assets/icons/tree-menu/stop.svg" />
    </div>
  </div>
</template>

<script>
export default {
  name: "TaskItem",
  props: {
    option: {
      type: Object,
      default: () => {
        return {
          name: '任务名称',
          id: '任务ID',
          status: 'running',
          progress: 50,
          createTime: '2022-12-12 12:12:12',
          startTime: '2022-12-12 12:12:12',
          runTime: '20',
          log: '正在同步Nacos配置...',
        }
      }
    }
  }
}
</script>

<style scoped lang="less">
  .task{
    border-bottom: #dedede solid 1px;
    padding: 5px;
    .name{
      overflow: hidden;
    }
    .desc{
      color: #999;
      font-size: 12px;
      overflow: hidden;
      text-overflow:ellipsis; //溢出用省略号显示
      white-space:nowrap; //溢出不换行
    }
    .progress{
      .progress-border{
        width: calc(100% - 40px);
        height: 10px;
        background-color: #e5e5e5;
        display: inline-block;
        .progress-bar{
          height: 100%;
          background-color: #00a0e9;
        }
      }
      img {
        width: 20px;
        height: 20px;
        cursor: pointer;
        vertical-align: middle;
        float: right;
        &:hover{
          background-color: #c7c7c7;
        }
      }
    }
  }
</style>
