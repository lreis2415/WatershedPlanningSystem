module.exports = {
  publicPath:'./',
  lintOnSave: false,
  transpileDependencies: [
    'vue-echarts',
    'resize-detector'
  ],
  devServer: {
    proxy: {
      '^/api': {
        //target: 'http://192.168.1.13:8081',
        target: 'http://127.0.0.1:8081',
        ws: true,
        changeOrigin: true,
        pathRewrite:{
          '^/api': '/'
        }
      },
    }
  }
}
