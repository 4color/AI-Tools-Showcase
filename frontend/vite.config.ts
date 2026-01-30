import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import {resolve} from 'path'
import {createHtmlPlugin} from 'vite-plugin-html'

export default defineConfig({
    plugins: [
        vue(),
        createHtmlPlugin({
            /**
             * 在 index.html 中可使用 EJS 变量：
             * 例如：<title><%= VITE_APP_TITLE %></title>
             */
            inject: {
                data: {
                    VITE_APP_TITLE: process.env.VITE_APP_TITLE || 'AI Tools Showcase',
                    NODE_ENV: process.env.NODE_ENV
                },
                /**
                 * 注入一个“变量文件”（你可以放在 public 下，或由后端提供）
                 * 最终会出现在 <head> 中：
                 * <script src="/env.js"></script>
                 */
                tags: [
                ]
            }
        })
    ],
    resolve: {
        alias: {
            '@': resolve(__dirname, 'src')
        }
    },
    server: {
        port: 3000,
        proxy: {
            '/api/': {
                target: 'http://localhost:8080/',
                rewrite: (path) => path.replace(/^\/api/, "/"),
                changeOrigin: true
            }
        }
    }
})