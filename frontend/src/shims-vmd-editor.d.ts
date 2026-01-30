declare module '@kangc/v-md-editor/lib/preview' {
  import type { Plugin } from 'vue'
  const VMdPreview: Plugin & {
    use: (...args: any[]) => any
  }
  export default VMdPreview
}

declare module '@kangc/v-md-editor' {
  import type { Plugin } from 'vue'
  const VMdEditor: Plugin & {
    use: (...args: any[]) => any
  }
  export default VMdEditor
}

declare module '@kangc/v-md-editor/lib/theme/vuepress.js' {
  const theme: any
  export default theme
}

declare module 'prismjs' {
  const Prism: any
  export default Prism
}

declare module 'prismjs/components/prism-json' {
  const mod: any
  export default mod
}

