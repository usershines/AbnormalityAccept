<!-- 修改 ChatView.vue -->
<script lang="ts" setup>
import { chatStream } from '@/api/AIChat.ts'
import { Graph } from '@antv/g6'
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { marked } from 'marked'

// 原有响应式数据
const userInput = ref('')
const aiResponse = ref('')
const isLoading = ref(false)
const graphContainer = ref<HTMLDivElement | null>()
let graph: Graph | null = null
const renderedResponse = computed(() => {
  if (aiResponse.value) {
    return marked(aiResponse.value)
  }
  return ''
})

// 知识图谱数据
const kgData = ref({
  nodes: [],
  edges: [],
})

// 原有函数保持不变
const getAIResponse = async (input: string) => {
  isLoading.value = true
  aiResponse.value = ''

  initGraph()

  chatStream(
      userInput.value,
      handleResponse,
  )
}

const handleResponse = async (data: any) => {
  isLoading.value = false
  console.log(data)
  if (data.type == 'text') {
    aiResponse.value += data.data
  }
  if (data.type == 'graph') {
    updateKGData(data.data)
  }

  // 如果响应包含图数据，则更新图
}

// 初始化图
const initGraph = () => {
  // 先销毁可能存在的旧实例
  if (graph) {
    graph.destroy()
    graph = null
  }

  if (graphContainer.value) {
    console.log('创建新的图实例')
    graph = new Graph({
      container: graphContainer.value,
      autoResize: true,
      data: { nodes: [], edges: [] }, // 初始为空数据
      layout: {
        type: 'd3-force',
        preventOverlap: true,
        collide: {
          radius: 35,
          strength: 0.8,
        },
        link: {
          distance: 200,
          strength: 0.1,
        },
      },
      node: {
        style: {
          fill: '#409eff',
          labelText: (datum): string => {
            return datum.label ? String(datum.label) : ''
          },
        },
      },
      edge: {
        style: {
          stroke: '#ccc',
          labelText: (datum): string => {
            return datum.label ? String(datum.label) : ''
          },
        },
      },
      behaviors: ['drag-canvas', 'zoom-canvas', 'drag-element-force'],
    })
  }
}

onMounted(() => {
  console.log('初始化图容器')
  initGraph();
})

onBeforeUnmount(() => {
  if (graph) {
    graph.destroy()
  }
})

// 更新知识图谱
const updateKGData = (data: any) => {
  console.log('更新图数据')
  if (graph) {
    console.log('添加图数据')
    graph.addData(data)
    graph.render()
    console.log('渲染图数据')
  }
}

const handleSubmit = () => {
  if (userInput.value.trim() && !isLoading.value) {
    getAIResponse(userInput.value)
  }
}

const clearConversation = () => {
  userInput.value = ''
  aiResponse.value = ''
  isLoading.value = false
  if (graph) {
    graph.clear().then(() => {
      if (graph) graph.render()
    })
  }
}
</script>

<template>
  <div class="main-container">
    <!-- 左侧聊天区域 -->
    <div class="left-panel">
      <div class="chat-container">
        <!-- 原有聊天界面保持不变 -->
        <div class="header">
          <h1>AI 助手</h1>
          <button @click="clearConversation" class="clear-btn">清除</button>
        </div>

        <div class="response-section" v-if="aiResponse || isLoading">
          <div class="ai-response">
            <div v-if="isLoading" class="loading">
              <span>AI正在思考中...</span>
            </div>
            <div v-else class="response-content" v-html="renderedResponse"></div>
          </div>
        </div>

        <div class="input-section">
          <textarea
              v-model="userInput"
              placeholder="请输入您的问题..."
              rows="3"
              class="user-input"
              @keydown.enter.exact.prevent="handleSubmit"
          ></textarea>
          <button
              @click="handleSubmit"
              :disabled="!userInput.trim() || isLoading"
              class="submit-btn"
          >
            {{ isLoading ? '处理中...' : '发送' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 右侧知识图谱区域 -->
    <div class="right-panel">
      <div class="kg-section">
        <h3>相关知识图谱</h3>
        <div ref="graphContainer" class="graph-container"></div>
      </div>
    </div>
  </div>
</template>
<style scoped>
/* 主容器布局 - 亮色简约风格 */
.main-container {
  display: flex;
  height: 740px;
  gap: 24px;
  font-family: 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  width: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: #ffffff; /* 纯白背景 */
}

.left-panel {
  flex: 1;
  min-width: 300px;
  max-width: 600px;
}

.right-panel {
  flex: 1;
  min-width: 400px;
}

/* 聊天容器样式调整 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 95%;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.header h1 {
  color: #333333;
  margin: 0;
  font-size: 1.4rem;
  font-weight: 600;
}

.clear-btn {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
  color: #495057;
}

.clear-btn:hover {
  background-color: #e9ecef;
  border-color: #dee2e6;
}

/* 响应区域样式 */
.response-section {
  flex: 1;
  min-height: 0;
  margin-bottom: 20px;
  overflow: hidden;
}

.ai-response {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #fafafa;
  border-radius: 8px;
  padding: 16px;
  overflow: hidden;
}

.response-content {
  flex: 1;
  overflow-y: auto;
  line-height: 1.7;
  color: #333;
  font-size: 0.95rem;
  padding-right: 8px;
}

/* 输入区域样式 */
.input-section {
  margin-top: auto;
}

.user-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  resize: vertical;
  font-size: 0.95rem;
  margin-bottom: 12px;
  box-sizing: border-box;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  background-color: #ffffff;
}

.user-input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.submit-btn {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: background-color 0.2s ease;
  width: 100%;
  font-weight: 500;
}

.submit-btn:hover:not(:disabled) {
  background-color: #337ecc;
}

.submit-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

/* 加载状态样式 */
.loading {
  display: flex;
  align-items: center;
  color: #666;
  padding: 16px;
  justify-content: center;
  height: 100%;
}

.loading::before {
  content: '';
  width: 20px;
  height: 20px;
  border: 2px solid #e0e0e0;
  border-top-color: #409eff;
  border-radius: 50%;
  margin-right: 10px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 知识图谱区域样式 */
.kg-section {
  height: 95%;
  width: 95%;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 10px 10px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.kg-section h3 {
  margin: 0 0 16px 0;
  padding: 0;
  flex: 0 0 auto;
  color: #333333;
  font-size: 1.1rem;
  font-weight: 600;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.graph-container {
  flex: 1 1 auto;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  min-height: 0;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  background-color: #fafafa;
}

/* Markdown 内容样式优化 */
.response-content :deep(h1),
.response-content :deep(h2),
.response-content :deep(h3) {
  margin: 1em 0 0.5em 0;
  font-weight: 600;
  color: #2d3748;
}

.response-content :deep(p) {
  margin: 0.8em 0;
}

.response-content :deep(ul),
.response-content :deep(ol) {
  margin: 0.8em 0;
  padding-left: 1.8em;
}

.response-content :deep(li) {
  margin: 0.3em 0;
}

.response-content :deep(code) {
  background-color: #f1f1f1;
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 0.9em;
}

.response-content :deep(pre) {
  background-color: #f1f1f1;
  padding: 1em;
  border-radius: 5px;
  overflow-x: auto;
  margin: 1em 0;
}

.response-content :deep(blockquote) {
  border-left: 3px solid #e2e8f0;
  padding-left: 1em;
  margin: 1em 0;
  color: #64748b;
}

/* 滚动条美化 */
.response-content::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.response-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.response-content::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.response-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>