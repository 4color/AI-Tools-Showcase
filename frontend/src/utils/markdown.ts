/**
 * Markdown 工具函数
 */

export interface TocItem {
  id: string
  title: string
  level: number
}

/**
 * 从 markdown 内容中提取目录
 * @param markdown markdown 内容
 * @returns 目录项数组
 */
export function extractTocFromMarkdown(markdown: string): TocItem[] {
  if (!markdown) return []
  
  const toc: TocItem[] = []
  const lines = markdown.split('\n')
  
  lines.forEach((line) => {
    // 匹配标题行 (# ## ### 等)
    const match = line.match(/^(#{1,6})\s+(.+)$/)
    if (match) {
      const level = match[1].length
      const title = match[2].trim()
      // 生成 ID：将标题转换为小写，替换空格和特殊字符为连字符
      const id = title
        .toLowerCase()
        .replace(/[^\w\s-]/g, '')
        .replace(/\s+/g, '-')
        .replace(/-+/g, '-')
        .replace(/^-|-$/g, '')
      
      toc.push({ id, title, level })
    }
  })
  
  return toc
}

/**
 * 为 markdown 内容中的标题添加 ID 属性
 * @param markdown markdown 内容
 * @returns 添加了 ID 的 markdown 内容
 */
export function addIdsToHeadings(markdown: string): string {
  if (!markdown) return ''
  
  return markdown.replace(/^(#{1,6})\s+(.+)$/gm, (match, hashes, title) => {
    const id = title
      .toLowerCase()
      .replace(/[^\w\s-]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .replace(/^-|-$/g, '')
    return `${hashes} ${title} {#${id}}`
  })
}
