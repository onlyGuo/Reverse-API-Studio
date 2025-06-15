<template>
  <div>
    <h1>ChromeJS调试器使用帮助</h1>
    <p>FViteBoss2.0支持单步调试功能，通过实现ChromeDevTool的ws协议进行调试。因此，若要使用调试功能，你必须准备一个Chrome浏览器</p>
    <h2>准备工作</h2>
    <p>1. 下载Chrome浏览器</p>
    <p>2. 确认Chrome浏览器能够访问这个页面<code>devtools://devtools/bundled/js_app.html</code></p>
    <h2>脚本地址</h2>
    <p>1. 在FViteBoss2.0的开发者工具中，打开你要调试的脚本</p>
    <p>2. 在打开的脚本标签页下方可以看到该脚本的命名地址，如下图</p>
    <img src="../../assets/docs/debug-script-path.png" alt="debug1">
    <p>3. 项目标识+命名地址 = 脚本地址，例如<code>project-9://example/apis/Test.mjs</code></p>
    <h2>连接器地址</h2>
    <p>1. 通常情况下，连接器地址位Fvite的<code>Host</code>+<code>:4242</code>, 例如localhost:4242,见下图</p>
    <img src="../../assets/docs/debug-connector-path.png" alt="debug2">
    <h2>开始调试</h2>
    <p>1. Chrome访问调试地址：<code>devtools://devtools/bundled/js_app.html?ws={连接器地址}/{脚本地址}</code></p>
    <p>2. 例如，你的Fvite的Host为localhost，你要调试的脚本地址为<code>project-9://example/apis/Test.mjs</code>，那么你的调试地址为<code>devtools://devtools/bundled/js_app.html?ws=localhost:4242/project-9://example/apis/Test.mjs</code></p>
    <p>3. Chrome打开调试地址后，鼠标移动到FVite脚本编辑器又上方的Debug图标，选择你要调试的方法，并输入入参对象后，点击Debug按钮，如下图</p>
    <img src="../../assets/docs/debug-params.jpg" alt="debug3">
    <p>4. 点击Debug按钮后，Chrome调试器会自动命中断点，此时即可开始调试，如下图</p>
    <img src="../../assets/docs/debug-step.png" alt="debug4">
    <p>5. 调试完成后，再次点击FviteBoss脚本编辑器右上方的<code>Debug</code>按钮，即可重新执行脚本</p>
  </div>
</template>

<script>
export default {
  name: "Debug"
}
</script>

<style scoped lang="less">
code{
  border: orange solid 1px;
  padding: 2px;
  border-radius: 3px;
  margin-left: 3px;
  margin-right: 3px;
  background-color: rgba(255, 165, 0, 0.1);
  color: #a66d00;
}
h1{
  margin-top: 20px;
}
h2{
  margin-top: 10px;
}
</style>