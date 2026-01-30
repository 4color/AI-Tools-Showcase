/**
 * Markdown 渲染工具函数
 */

/**
 * 生成标题 ID
 */
export function generateHeadingId(title: string): string {
  return title
    .toLowerCase()
    .replace(/[^\w\s-]/g, '')
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-|-$/g, '')
}

/**
 * 简单的 markdown 渲染函数
 */
export function renderMarkdown(markdown: string): string {
  if (!markdown) return ''
  
  let html = markdown
  
  // 先处理代码块（避免代码块内的内容被误处理）
  const codeBlocks: string[] = []
  html = html.replace(/```[\s\S]*?```/g, (match) => {
    const placeholder = `__CODE_BLOCK_${codeBlocks.length}__`
    codeBlocks.push(match)
    return placeholder
  })
  
  // 处理行内代码
  html = html.replace(/`([^`]+)`/g, '<code class="bg-slate-100 text-slate-800 px-1.5 py-0.5 rounded text-sm font-mono">$1</code>')
  
  // 处理标题（需要先处理，以便生成正确的 ID）
  html = html.replace(/^(#{1,6})\s+(.+)$/gm, (match, hashes, title) => {
    const level = hashes.length
    const id = generateHeadingId(title.trim())
    const tag = `h${level}`
    const classes = level === 1 ? 'text-3xl font-bold text-slate-900 mt-8 mb-6' :
                   level === 2 ? 'text-2xl font-semibold text-slate-900 mt-8 mb-4' :
                   'text-xl font-semibold text-slate-900 mt-6 mb-4'
    return `<${tag} id="${id}" class="${classes}">${title.trim()}</${tag}>`
  })
  
  // 处理粗体
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong class="font-semibold">$1</strong>')
  // 处理斜体（避免与粗体冲突）
  html = html.replace(/(?<!\*)\*([^*]+?)\*(?!\*)/g, '<em>$1</em>')
  
  // 处理链接
  html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" class="text-primary-600 hover:underline" target="_blank" rel="noopener noreferrer">$1</a>')
  
  // 处理列表（先处理有序列表，再处理无序列表）
  const lines = html.split('\n')
  const processedLines: string[] = []
  let inList = false
  let listType = ''
  
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i]
    const orderedMatch = line.match(/^(\d+)\.\s+(.+)$/)
    const unorderedMatch = line.match(/^[-*]\s+(.+)$/)
    
    if (orderedMatch || unorderedMatch) {
      const content = orderedMatch ? orderedMatch[2] : unorderedMatch![1]
      const currentListType = orderedMatch ? 'ol' : 'ul'
      
      if (!inList || listType !== currentListType) {
        if (inList) {
          processedLines.push(`</${listType}>`)
        }
        processedLines.push(`<${currentListType} class="list-disc list-inside space-y-2 my-4">`)
        inList = true
        listType = currentListType
      }
      processedLines.push(`<li class="ml-4 mb-2">${content}</li>`)
    } else {
      if (inList) {
        processedLines.push(`</${listType}>`)
        inList = false
      }
      processedLines.push(line)
    }
  }
  
  if (inList) {
    processedLines.push(`</${listType}>`)
  }
  
  html = processedLines.join('\n')
  
  // 恢复代码块
  codeBlocks.forEach((code, index) => {
    const placeholder = `__CODE_BLOCK_${index}__`
    const codeContent = code.replace(/```[\w]*\n?/g, '').replace(/```$/g, '')
    html = html.replace(placeholder, `<pre class="bg-slate-900 text-slate-100 rounded-lg p-4 overflow-x-auto my-4"><code>${codeContent}</code></pre>`)
  })
  
  // 处理段落（空行分隔）
  html = html.split(/\n\s*\n/).map(para => {
    para = para.trim()
    if (!para || para.startsWith('<') || para.match(/^<[h|u|o|p|d]/)) {
      return para
    }
    return `<p class="text-slate-600 leading-relaxed mb-4">${para}</p>`
  }).join('\n\n')
  
  // 处理单个换行
  html = html.replace(/\n(?!\n)/g, '<br>')
  
  return html
}
