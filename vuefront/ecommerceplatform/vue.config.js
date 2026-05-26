const { defineConfig } = require('@vue/cli-service')
const path = require('path')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    open: true,
    proxy: {
      '/seller': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass(req) {
          const accept = req.headers.accept || ''
          if (req.method === 'GET' && accept.includes('text/html')) {
            return '/index.html'
          }
        }
      },
      '/buyer': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass(req) {
          const accept = req.headers.accept || ''
          if (req.method === 'GET' && accept.includes('text/html')) {
            return '/index.html'
          }
        }
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass(req) {
          const accept = req.headers.accept || ''
          if (req.method === 'GET' && accept.includes('text/html')) {
            return '/index.html'
          }
        }
      }
    }
  },

  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    }
  },
  lintOnSave: false
})