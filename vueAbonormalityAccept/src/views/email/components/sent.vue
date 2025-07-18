<template>
  <div class="inbox-container">
    <div class="inbox-header">
      <div style="margin: 10px;display: flex;align-items: center">
        <el-icon style="font-size: 40px"><Promotion /></el-icon>
        <h1>已发送邮件</h1>
      </div>
      <div class="header-actions">
        <el-button @click="catchData">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
        <el-input
          v-model="searchQuery"
          placeholder="搜索邮件..."
          clearable
          class="search-input"
          @input="searchEmails"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>


    <div class="email-list">
      <div
        v-for="email in emails"
        :key="email.id"
        class="email-item"
        @click="openEmail(email)"
      >
        <div class="email-content">
          <div class="email-subject">
            <span>{{ email.theme }}</span>
          </div>
          <div class="email-preview">
            <span class="recipient-label">收件人：</span>{{ email.receiverId }}
          </div>
        </div>
        <div class="email-meta">
          <div class="email-time">{{ formatDate(email.sendTime.toString()) }}</div>
        </div>
      </div>

      <div v-if="total==0" class="empty-inbox">
        <el-icon class="empty-icon"></el-icon>
        <p>没有找到符合条件的邮件</p>
        <el-button type="primary" @click="resetFilters">重置筛选条件</el-button>
      </div>
    </div>

    <div class="inbox-footer">
      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {Promotion, Search, Refresh} from '@element-plus/icons-vue'
import {findEmailBySender} from "@/api/email.ts";
import {ElMessage} from "element-plus";

// 邮件类型定义
interface Email{
  id: number,
  state: number,
  senderId: number,
  senderName: string,
  receiverId: number,
  receiverName: string,
  theme:  string,
  content: string,
  sendTime: Date,
}

// 状态管理
const emails = ref<Email[]>([])
const searchTheme = ref('')
const seachReceiver = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const username = ref('')
const total = ref(0)

// 刷新方法 获取数据
const catchData = () => {
  if (username.value !== '') {
    findEmailBySender(username.value,currentPage.value, pageSize.value).then((res) => {
      if(res.code === 200){
        emails.value =  res.data.list
        total.value =  res.data.total
        ElMessage.success('获取邮件成功')
      }
    }).catch(e => {
      ElMessage.error(e.msg)
      console.log(e)
    })
  }
}

// 清空搜索栏
const resetFilters = () =>{
  searchTheme.value = ''
  seachReceiver.value = ''
  catchData()
}

// 搜索方法
const searchEmails = () =>{

}

// 生命周期钩子
onMounted(() => {
  const name = localStorage.getItem('username')
  if(name !== null){
    username.value = name
  }else{
    ElMessage.error('请重新登录')
  }
  catchData()
})

// 方法
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return date.toLocaleDateString([], { weekday: 'short' })
  } else {
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' })
  }
}

const openEmail = (email: Email) => {
  // 实际项目中这里会跳转到邮件详情页
  ElMessageBox.alert(email.content, `已发送: ${email.subject}`, {
    confirmButtonText: '关闭',
    customClass: 'email-detail-modal',
    closeOnClickModal: true
  })
}
</script>

<style scoped>
.inbox-container {
  display: flex;
  flex-direction: column;
  margin-top: -20px;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

.inbox-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.header-title h1 {
  font-size: 24px;
  color: #1f2d3d;
  margin: 0;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.search-input {
  width: 300px;
  border-radius: 20px;
}

.inbox-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.select-all {
  margin-right: 10px;
}

.email-list {
  flex: 1;
  overflow-y: auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.email-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: all 0.3s ease;
}

.email-item:hover {
  background-color: #f8f9fc;
}

.email-checkbox {
  margin-right: 15px;
}

.email-content {
  flex: 1;
  min-width: 0;
  margin-right: 20px;
}

.email-subject {
  font-weight: 500;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.email-preview {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recipient-label {
  color: #606266;
  font-weight: 500;
}

.email-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 80px;
}

.email-time {
  font-size: 13px;
  color: #909399;
}

.empty-inbox {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #909399;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  color: #c0c4cc;
}

.empty-inbox p {
  margin-bottom: 20px;
  font-size: 16px;
}

.inbox-footer {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}
</style>