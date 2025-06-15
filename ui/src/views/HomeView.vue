<template>
  <div class="home">
    <div class="header">
      <div class="title">
        <img src="../assets/logo.png" class="logo">
        FViteBoss Project Editor
      </div>
      <div class="groups">
        当前团队：
        <span class="groupName">
          xxx<span class="iconfont icon icon-zhankai1"></span>
        </span>
        <div class="group-list">
          <div class="item" v-for="item in companyList" @click="selectGroup(item)">{{item.name}}</div>
        </div>
      </div>
    </div>
    <div class="body">
      <div class="projects">
        <div class="project" v-for="project in projectList" @click.stop="openProject(project)">
          <img src="../assets/logo.png">
          <div class="title">{{project.projectName}}</div>
          <div class="menus">
            <span class="iconfont icon icon-zhankai1"></span>
            <div class="menu">
              <div class="item" @click.stop="openProject(project)">进入开发</div>
              <div class="item" @click.stop="otherAction(project)">浏览包仓库</div>
              <div class="item" @click.stop="otherAction(project)">单机打包</div>
              <div class="item" @click.stop="otherAction(project)">分布式打包</div>
              <div class="item" @click.stop="pushSource(project)">推送本地修改</div>
              <div class="item" @click.stop="pullSource(project)">拉取最新版本</div>
              <div class="item" @click.stop="otherAction(project)">更新数据库</div>
              <div class="item" @click.stop="otherAction(project)">编辑配置信息</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.home, .home *{
  font-family: -apple-system, BlinkMacSystemFont, Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.home{
  color: #2c3e50;
  .header{
    background-color: #2c3e50;
    color: white;
    height: 50px;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    .title{
      display: inline-block;
      font-size: 25px;
      line-height: 50px;
      .logo{
        height: 40px;
        vertical-align: middle;
      }
    }
    .groups{
      display: inline-block;
      height: 50px;
      line-height: 50px;
      .groupName{
        cursor: pointer;
        .icon{
          display: inline-block;
          margin-left: 5px;
        }
      }
      &:hover .group-list{
        display: block;
      }
      .group-list{
        display: none;
        position: absolute;
        right: 20px;
        margin-top: -10px;
        z-index: 1;
        box-shadow: rgba(0,0,0,.15) 1px 1px 15px 5px;
        background-color: rgba(255, 255, 255, .5);
        backdrop-filter: blur(20px);
        border-radius: 5px;
        overflow: hidden;
        .item{
          color: #2c3e50;
          line-height: 8px;
          background-color: transparent;
          padding: 10px;
          cursor: pointer;
          transition: all .15s;
          &:hover{
            background-color: #42b983;
            color: white;
          }
        }
      }
    }
  }
  .body{
    padding: 20px;
    .projects{
      //display: flex;
      //flex-wrap: wrap;
      //justify-content: space-between;
      .project{
        cursor: pointer;
        margin: 20px;
        height: 150px;
        width: calc(calc(100% - calc(200px)) / 5);
        background-color: #2c3e50;
        display: inline-block;
        transition: all .3s;
        &:hover{
          box-shadow: rgba(0,0,0,.25) 5px 5px 15px 5px;
        }
        img{
          height: 100%;
          width: 100%;
          object-fit: contain;
        }
        .title{
          position: absolute;
          z-index: 1;
          margin-top: -35px;
          height: 30px;
          width: calc(calc(calc(100% - calc(200px)) / 5) - 28px);
          line-height: 30px;
          background-color: rgba(255, 255, 255, .5);
          backdrop-filter: blur(20px);
          padding: 0 10px;
        }
        .menus{
          margin-top: -35px;
          position: absolute;
          line-height: 30px;
          margin-left: calc(calc(calc(100% - calc(200px)) / 5) - 30px);
          .icon{
            z-index: 2;
            position: absolute;
          }
          &:hover .menu{
            display: block;
          }
          .menu{
            display: none;
            position: absolute;
            width: 150px;
            z-index: 3;
            box-shadow: rgba(0,0,0,.15) 1px 1px 15px 5px;
            background-color: rgba(255, 255, 255, .5);
            backdrop-filter: blur(30px);
            line-height: 10px;
            border-radius: 5px;
            overflow: hidden;
            top: 20px;
            .item{
              padding: 10px;
              &:hover{
                background-color: #42b983;
                color: white;
              }
            }
          }
        }
      }
    }
  }
}
.code{
  background-color: antiquewhite;
  color: orangered;
  display: inline-block;
  padding: 0 3px;
  border-radius: 3px;
  box-shadow: rgba(0,0,0,.04) 1px 1px 5px 1px;
}

nav {
  padding: 30px;
}
a {
  font-weight: bold;
  color: #42b983;
  cursor: pointer;

  &.router-link-exact-active {
    color: #42b983;
  }
}
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

</style>

<script>

export default {
  name: 'HomeView',
  data(){
    return {
      loginInfo: {
        company: {}
      },
      companyList: [],
      projectList: []
    }
  },
  created() {
    try{
      this.loginInfo = JSON.parse(localStorage.getItem('login-info'));
    }catch (e) {
      this.$router.push('/login');
    }
    if (null == this.loginInfo){
      this.loginInfo = {
        company: {}
      };
      this.$router.push('/login');
      return;
    }

    this.$fvite.get('api/v1/project?page=1&pageSize=9999').then(res => {
      this.projectList = res.responseBody.list;
    });
    this.$fvite.get('api/v1/company/list').then(res => {
      this.companyList = res.responseBody
    })
  },
  methods: {
    selectGroup(item){
      this.$fvite.post('api/v1/company/switch', item).then(res => {
        this.loginInfo = res.responseBody;
        localStorage.setItem('login-info', JSON.stringify(this.loginInfo));
        this.$fvite.get('api/v1/project?page=1&pageSize=9999').then(res => {
          this.projectList = res.responseBody.list;
        });
      })
    },
    openProject(project){
      this.$router.push({
        path: '/development',
        query: {
          id: project.id
        }
      })
    },
    pushSource(project){
      this.$http.post("api/v1/project/" + project.id + "/push").then(res => {
        alert('推送成功');
      }).catch(err => {
        alert('推送失败:' + err.message);
      })
    },
    pullSource(project){
      this.$http.post("api/v1/project/" + project.id + "/pull").then(res => {
        alert('拉取成功');
      }).catch(err => {
        alert('拉取失败:' + err.message);
      })
    },
    otherAction(){
      alert('待完善')
    }
  }
}
</script>
