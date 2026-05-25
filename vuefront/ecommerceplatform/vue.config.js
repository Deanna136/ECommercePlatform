const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  devServer: {
    port: 8081, // 前端端口，别和后端 8080 冲突
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端地址
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      },
      '/buyer': {
        target: 'http://localhost:8080', // 后端地址
        changeOrigin: true
      },
      '/admin': {
        target: 'http://localhost:8080', // 后端地址
        changeOrigin: true
      },
      '/seller': {
        target: 'http://localhost:8080', // 后端地址
        changeOrigin: true
      }
    }
  },
  lintOnSave: false
})