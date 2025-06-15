import {createApp, reactive} from 'vue'
import App from './App.vue'
import router from './router'
import Axios from 'axios'
import * as monaco from "monaco-editor";
import * as animated from 'animate.css'
// 引入 highlight.js 代码高亮工具
import hljs from "highlight.js";
// 使用样式，有多种样式可选
import "highlight.js/styles/github.css";


Axios.defaults.timeout = 10000
Axios.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8'
Axios.defaults.headers.put['Content-Type'] = 'application/json;charset=UTF-8'
Axios.defaults.headers.delete['Content-Type'] = 'application/json;charset=UTF-8'
Axios.interceptors.request.use(
    config => {
        let login = localStorage.getItem('login-info')
        if(null != login){
            login = JSON.parse(login)
        }
        config.headers['Authorization'] = 'Bearer ' + login?.token
        config.headers['companyId'] = login?.company?.id
        return config
    }
)
Axios.interceptors.response.use(
    response => {
        if (response.status === 200) {
            return Promise.resolve(response.data)
        } else {
            console.log(response.data.message)
            return Promise.reject(response.data)
        }
    },
    // 服务器状态码不是200的情况
    error => {
        if (error.response){
            if (error.response.status) {
                // error.response.data = JSON.parse(error.response.data)
                if (error.response.status === 401) {
                    router.replace({
                        path: '/login'
                    })
                } else if (error.response.status === 400) {
                    const msg = error.response.data.message;
                    console.log(msg)
                    alert(msg);
                } else if (error.response.status === 403){
                    console.log("您无权访问该内容")
                } else {
                    console.log("网络错误，请稍后再试")
                }
                return Promise.reject(error.response.data)
            }
        }
    }
)



const app = createApp(App);
app.directive("highlight", function(el) {
    let blocks = el.querySelectorAll("pre code");
    blocks.forEach(block => {
        hljs.highlightBlock(block);
    });
});
app.config.globalProperties.$hljs = hljs;
app.config.globalProperties.$http = Axios
app.config.globalProperties.$fvite = reactive({
    get(uri){
        let login = localStorage.getItem('login-info')
        if(null != login){
            login = JSON.parse(login)
        }
        return Axios.post('/fvite/request?uri=' + encodeURIComponent(uri) + '&method=GET&token=' + login?.token, {});
    },
    post(uri, data){
        debugger
        console.log(data)
        let login = localStorage.getItem('login-info')
        if(null != login && 'undefined' !== login){
            login = JSON.parse(login)
        }
        return Axios.post(uri, data)
        // return Axios.post('/fvite/request?uri=' + encodeURIComponent(uri) + '&method=POST&token=' + login?.token, data);
    }
});

// app.use(animated)
app.use(router)
app.mount('#app')
