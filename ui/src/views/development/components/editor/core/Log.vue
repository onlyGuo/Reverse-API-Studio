<template>
  <div class="log">
    <pre class="log-item" v-for="l in logInfo" :style="'color: ' + l.color" :key="l.text">{{l.text}}</pre>
  </div>
</template>

<script>
export default {
  name: "Log",
  props:{
    index: { // index of current item
      type: Number
    },
    source: { // here is: {uid: 'unique_1', text: 'abc'}
      type: Object,
      default () {
        return {}
      }
    },
    log: String
  },
  data(){
    return {
      logInfo: []
    }
  },
  mounted() {
    this.parseLog();
  },
  methods: {
    parseLog(){
      // 以\x1B[*m 开头和\x1B[0;3*m结尾的字符串, 以开头确定颜色,
      // const log = this.source.text;
      const log = this.log;
      let index = -1;
      let color = 'black';
      let text = '';
      let count = 0;
      while (index < log.length) {
        const logItem = {
          color: 'black',
          text: ''
        }
        let tempIndex = log.indexOf('\x1B[', index + 1);
        if (tempIndex === -1) {
          text = log.substring(index + 1, log.length);
          logItem.text = text;
          this.logInfo.push(logItem);
          break;
        }else{
          index = tempIndex;
        }
        let textIndex = log.indexOf('m', index + 1);
        color = log.substring(index + 2, textIndex);
        if(textIndex === -1){
          logItem.text = log.substring(index + 1, text.length);
          this.logInfo.push(logItem);
          break;
        }
        let colorIndex = log.indexOf('\x1B[0;3', index + 1);

        text = log.substring(textIndex + 1, colorIndex);
        index = colorIndex;
        colorIndex = log.indexOf('m', index + 1);
        if (colorIndex === -1) {
          colorIndex = log.length;
        }
        logItem.text = (count === 0 ? '' : ' ') + text ;
        logItem.color = this.getColor(color);
        this.logInfo.push(logItem);
        index = colorIndex;
        count ++;
      }

    },
    getColor(log){
      switch (log) {
        case '2':
          return "black";
        case '30':
          return "red";
        case '32':
          return "#00a200";
        case '33':
          return "#FFA500";
        case '34':
          return "#0000cc";
        case '35':
          return "purple";
        case '36':
          return "#00a2a2";
        case '37':
          return "#c0c0c0";
        case '90':
          return "gray";
        case '91':
          return "red";
        default:
          return 'black';
      }
    }
  }
}
</script>

<style scoped lang="less">
.log{
  white-space: nowrap;
  .log-item{
    margin: 0;
    padding: 0;
    display: inline-block;
  }
}
</style>