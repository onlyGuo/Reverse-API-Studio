<template>
  <div class="home-page" ref="page">
    <div class="bg">
      <div class="block1" :style="('margin-left:' + (bgLocation.x * 6) + 'px;margin-top:' + (bgLocation.y * 6) + 'px;')
      + ('transition: all ' + bgLocation.transition + '.0s ')">
      </div>
      <div class="block2" :style="('margin-right:' + (-bgLocation.x * 3) + 'px;margin-top:' + (bgLocation.y * 3) + 'px;')
      + ('transition: all ' + bgLocation.transition + '.0s ')">
      </div>
      <div class="content">
        <div class="logo">
          <img src="../assets/logo.png">
          <img src="../assets/fvite.png">
        </div>
        <div class="content-center">
          <div class="welcome">
            <img src="../assets/welcome.png">
            <div class="text">欢迎使用FVite Project Editor 开发者平台</div>
          </div>
          <div class="login-box">
            <div class="header">登录</div>

            <div class="input-block">
              <input type="text" placeholder="输入用户名/手机号" v-model="loginForm.username">
            </div>
            <div class="input-block">
              <input type="password" placeholder="输入密码" ref="passwordInput" v-model="loginForm.password">
              <span class="iconfont icon" :class="inputSee" style="color: #adadad; position: absolute; margin-left: -20px; cursor: text; margin-top: 7px;"
                    @mousemove="$refs.passwordInput.type='text'; inputSee = 'icon-see'"
                    @mouseout="$refs.passwordInput.type='password'; inputSee = 'icon-nosee'"></span>
            </div>

            <div class="input-block" style="margin-top: 80px">
              <button @click="login">登录</button>
              <div style="margin-top: 20px; color: #c2c2c2">
                没有账号？ <a href="javascript:alert('请访问云端FVite注册或修改密码');">立即注册</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Axios from "axios";

export default {
  name: "Login",
  data(){
    return {
      bgLocation: {
        x: 0,
        y: 0,
        transition: '.5s'
      },
      inputSee: 'icon-nosee',
      loginForm: {
        username: '',
        password: ''
      }
    }
  },
  mounted() {
    this.$refs.page.onmousemove = this.onMouseMove;
    this.$refs.page.onmouseout = this.clearBgLocation;

  },
  methods: {
    onMouseMove(event){
      event = event || window.event;
      this.bgLocation.x = (1 - event.clientX / (document.body.clientWidth / 2)) * 40;
      this.bgLocation.y = (1 - event.clientY / (document.body.clientHeight / 2)) * 40;
      this.bgLocation.transition = '.0s';
    },
    clearBgLocation(){
      this.bgLocation.transition = '.5s';
      this.bgLocation.x = 0;
      this.bgLocation.y = 0;
    },
    login(){

      // this.$fvite.post('api/v1/user/login', this.loginForm).then(res => {
      //   localStorage.setItem('login-info', JSON.stringify(res.responseBody));
      //   this.$router.replace({
      //     path: '/projects'
      //   })
      // })
      Axios.post('/api/v1/user/login', this.loginForm).then(res => {
        localStorage.setItem('login-info', JSON.stringify(res));
        this.$router.replace({
          path: '/development'
        })
      })
    }
  }
}
</script>

<style scoped lang="less">
a {
  font-weight: bold;
  color: #73d3d3;
  cursor: pointer;

  &.router-link-exact-active {
    color: #73d3d3;
  }
}
  .bg{
    width: 100vw;
    height: 100vh;
    position: fixed;
    background-color: #F3F2F3;
    .block1{
      height: 527px;
      width: 527px;
      border-radius: 50%;
      background: linear-gradient(to right, #58d1d1, #b2f1a0);
      position: fixed;
      top: calc(30vh);
      left: 300px;
      filter: blur(36px);
      opacity: 30%;
    }
    .block2{
      height: 361px;
      width: 361px;
      border-radius: 50%;
      background: linear-gradient(to bottom right, #58d1d1, #b2f1a0);
      position: fixed;
      top: 100px;
      right: 100px;
      filter: blur(36px);
      opacity: 30%;
    }
  }
  .content{
    width: 100vw;
    height: 100vh;
    position: fixed;
    z-index: 1;

    .logo{
      margin-top: 30px;
      margin-left: 60px;
      img{
        height: 50px;
      }
    }
    .content-center{
      padding: 0 60px;
      .welcome{
        margin-top: 20vh;
        margin-left: 150px;
        display: inline-block;
        vertical-align: top;
        img{
          height: 120px;
          margin-bottom: 30px;
        }
        .text{
          font-size: 18px;
          margin-left: 30px;
        }
      }
      .login-box{
        background-color: white;
        width: 400px;
        height: 500px;
        display: inline-block;
        vertical-align: top;
        float: right;
        margin-top: 10vh;
        margin-right: 100px;
        border-radius: 20px;
        box-shadow: rgba(0,0,0,.05) 1px 1px 15px 5px;
        .header{
          font-size: 24px;
          color: #2c3e50;
          padding: 70px 50px;
        }
        .input-block{
          padding: 0 50px;
          margin-bottom: 20px;
          input{
            width: calc(100% - 16px);
            height: 35px;
            &::placeholder{
              color: #dadada;
            }
            &:focus{
              border: 1px solid #73d3d3;
            }
            &:hover{
              border: 1px solid #73d3d3;
            }
          }
          button{
            width: 100%;
            background-color: #72d3d3;
            height: 35px;
            border: #73d3d3;
            color: white;
            border-radius: 5px;
            cursor: pointer;

            &:hover{
              background-color: #a6e5e5;
            }
          }
        }
      }
    }
  }

</style>