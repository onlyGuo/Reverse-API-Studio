<template>
  <div>
    <div class="left">
      <div style="text-align: center; font-size: 20px">文档目录</div>
      <div class="menu-item">开发者账号注册与登录</div>
      <div class="menu-item">快速开始</div>
      <div class="menu-item">创建项目</div>
      <div class="menu-item">目录结构</div>
      <div class="menu-item">模型开发</div>
      <div class="menu-item">接口开发</div>
      <div class="menu-item">内置域对象</div>
      <div class="menu-item">模型对象与JS对象</div>
      <div class="menu-item">多人协同</div>
      <div class="menu-item">分布式</div>
      <div class="menu-item">分布式事务</div>
      <div class="menu-item">打包为分布式项目</div>
      <div class="menu-item">分布式缓存</div>
      <div class="menu-item">跨服务调用</div>
      <div class="menu-item">WebSocket开发</div>
      <div class="menu-item">MQTT开发</div>
      <div class="menu-item">ETL开发</div>
      <div class="menu-item">解耦合与开发规范</div>
      <div class="menu-item">发布模块到模块市场</div>
      <div class="menu-item">版本迭代</div>
      <div class="menu-item">引用市场模块</div>
      <div class="menu-item">克隆市场模块</div>
      <div class="menu-item">
        Debug(ChromeDevTools)
      </div>
      <div class="menu-item">
        Debug(FVite)
      </div>
    </div>
    <div class="page-info right"><router-view></router-view></div>
  </div>
</template>

<script>
export default {
  name: "Index"
}
</script>

<style scoped lang="less">
.left{
  width: 200px;
  height: 100%;
  background-color: #f5f5f5;
  position: fixed;
  top: 0;
  left: 0;
  overflow: auto;
  .menu-item{
    padding: 10px 20px;
    cursor: pointer;
    &:hover{
      background-color: #e6e6e6;
    }
  }
}
.right{
  position: absolute;
  top: 0;
  left: 200px;
  width: calc(100% - 220px);
  height: calc(100% - 20px);
  overflow: auto;
}
.page-info{
  padding: 10px;
}
</style>