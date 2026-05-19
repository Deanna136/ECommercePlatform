const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  devServer: {
    port: 8081, // 前端端口，别和后端 8080 冲突
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端地址
        changeOrigin: true,
        pathRewrite: {
          '^/api': '' // 前端请求 /api/admin/xxx 会被代理到 http://localhost:8080/admin/xxx
        }
      }
    }
  },
  lintOnSave: false
})