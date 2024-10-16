import axios from "axios";
// 创建axios，赋给变量service
export const service = axios.create({
    baseURL: `http://8.210.250.29:9090/`,
    // baseURL: `http://localhost:9090/`,
    timeout: 150000, // 超时
    crossDomain: true,
});

//添加请求拦截器
service.interceptors.request.use(
    //在发请求之前打上token和跨域的标记
    function (config) {
        //在这里，代码将 "Access-Control-Allow-Origin" 的值设置为 "*"，
        // 意味着允许所有的源（即任意域名）进行跨域访问。这种配置通常用于开发
        // 和测试环境，以便允许任何源发起跨域请求。
        config.headers['Access-Control-Allow-Origin'] = '*';
        config.headers['Access-Control-Allow-Credentials'] = true;
        let token = window.localStorage.getItem("token")
        if (token) {
            //我们headers里面的令牌取名叫Authorization
            config.headers.Authorization = token;
        }
        return config;
    },
    function (error) {
        //返回一个被拒绝的 Promise 对象，并将错误 error 作为拒绝的原因
        return Promise.resolve(error.response.data);
    }
);
//添加响应拦截器
service.interceptors.response.use(
    function (response) {
        //使用响应数据返回响应；
        const data = response;
        // 如果data有东西，就返回data.data，如果没有就返回response
        if (data) {
            return Promise.resolve(data.data);
        }
        return response;
    },
    function (error) {
        //返回一个被拒绝的 Promise 对象，并将错误 error 作为拒绝的原因
        return Promise.resolve(error.response.data);
    }
);
// 将service 导出
export default service;