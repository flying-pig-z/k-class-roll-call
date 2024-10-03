const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({

  transpileDependencies: true,
  lintOnSave: false,
  devServer: {
    open: false, // 编译完成是否打开网页
    host: '0.0.0.0', // 指定使用地址，默认localhost,0.0.0.0代表可以被外界访问
    port: 9091, // 访问端口
    proxy: {
    }
  },
})



