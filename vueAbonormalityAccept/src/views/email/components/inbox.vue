<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import {MessageBox, Message, Reading, StarFilled, Refresh, Delete} from "@element-plus/icons-vue"
import {
  readAllEmail,
  countUnreadEmail ,
  findEmailBySenderLevel,
  findEmailByState,
  deleteEmailById ,
  findEmailById,
  findEmailBySender,
  findAllEmail,
  updateEmailState,
  } from '@/api/email'
import {ElMessage} from "element-plus";
import { useRouter } from 'vue-router'

const router = useRouter()

interface Email{
  id: number,
  state: number,
  senderId: number,
  senderName: string,
  senderLevel: number,
  receiverId: number,
  receiverName: string,
  receiverLevel: number,
  theme:  string,
  content: string,
  sendTime: Date,
}

// 状态管理
const emails = ref<Email[]>([])
const filteredEmails = ref<Email[]>([])
const selectedEmail = ref<Email | null>(null)
const detailDialogVisible = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const filterstate = ref<'已读邮件' | '未读邮件' | null>(null)
const filterPriority = ref<'D' | 'C' | 'B' | 'A' | 'O5' | null>(null)
const searchSender = ref('') // 新增发送者搜索变量


// 计算属性
const total = ref(0)


const unreadCount = ref(0) // 未读邮件数量

// 获取未读邮件数量
const fetchUnreadCount = () => {
  countUnreadEmail().then(res => {
    if (res.code === 200) {
      unreadCount.value = res.data
    }
  })
}

// 一键已读所有邮件
const markAllAsRead = () => {
  readAllEmail().then(res => {
    if (res.code === 200) {
      ElMessage.success('所有邮件已标记为已读')
      // 刷新邮件列表和未读数量
      fetchUnreadCount()
      catchEmails()

    }
  }).catch(err => {
    ElMessage.error('操作失败：' + err.msg)
  })
}

// 获取邮件
const catchEmails = () =>{
  findAllEmail(currentPage.value, pageSize.value).then((response) => {
    if(response.code === 200){
      console.log(response)
      emails.value = response.data.list;
      total.value = response.data.total;
      if(emails.value.length > 0){
        countUnreadEmail().then(res => {
          if (res.code === 200&&res.data>0){
            ElMessage.success('邮件更新成功')
            ElMessage.success('您有'+res.data+'封未读邮件')
          } else {
            ElMessage.success('邮件更新成功');
            ElMessage.success('您没有未读邮件');
          }
        })
      }
      fetchUnreadCount() // 更新未读数量
    }
  }).catch((error) => {
    ElMessage.success(error.msg)
  })
}

// 生命周期钩子
onMounted(() => {
  // emails.value = generateEmails()
  catchEmails()
  filteredEmails.value = [...emails.value]
  fetchUnreadCount() // 初始加载未读数量
})

// 邮件时间显示方法
// const formatDate = (dateString: string) => {
//   const date = new Date(dateString)
//   const now = new Date()
//   const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24))
//
//   if (diffDays === 0) {
//     return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
//   } else if (diffDays === 1) {
//     return '昨天'
//   } else if (diffDays < 7) {
//     return date.toLocaleDateString([], { weekday: 'short' })
//   } else {
//     return date.toLocaleDateString([], { month: 'short', day: 'numeric' })
//   }
// }

// 新增：根据状态筛选邮件
const filterByState = () => {
  if (filterstate.value === '未读邮件') {
    findEmailByState(0, currentPage.value, pageSize.value).then(response => {
      if (response.code === 200) {
        emails.value = response.data.list;
        total.value = response.data.total;
      }
    }).catch(error => {
      ElMessage.error(error.msg);
    });
  } else if (filterstate.value === '已读邮件') {
    findEmailByState(1, currentPage.value, pageSize.value).then(response => {
      if (response.code === 200) {
        emails.value = response.data.list;
        total.value = response.data.total;
      }
    }).catch(error => {
      ElMessage.error(error.msg);
    });
  } else {
    // 重置为所有邮件
    catchEmails();
  }
}

// 新增：根据发送者等级筛选邮件
const filterBySenderLevel = () => {
  if (filterPriority.value) {
    // 将等级值转换为数字
    const levelMap: Record<string, number> = {
      '5': 5, // O5
      '4': 4, // A
      '3': 3, // B
      '2': 2, // C
      '1': 1  // D
    };

    const level = levelMap[filterPriority.value];

    if (level) {
      findEmailBySenderLevel(level, currentPage.value, pageSize.value).then(response => {
        if (response.code === 200) {
          emails.value = response.data.list;
          total.value = response.data.total;
        }
      }).catch(error => {
        ElMessage.error(error.msg);
      });
    }
  } else {
    // 重置为所有邮件
    catchEmails();
  }
}
// 修改现有的 filterEmails 方法
const filterEmails = () => {
  // 优先处理状态筛选
  if (filterstate.value) {
    filterByState();
    return;
  }

  // 其次处理发送者等级筛选
  if (filterPriority.value) {
    filterBySenderLevel();
    return;
  }

  // 最后处理发送者名称搜索
  if (searchSender.value) {
    // ...现有代码保持不变...
  } else {
    catchEmails();
  }
}
// 修改 markAsRead 方法
const markAsRead = (email: Email) => {
  const newState = email.state === 0 ? 1 : 0; // 切换状态
  updateEmailState(email.id, newState).then(() => {
    email.state = newState; // 更新本地状态
    fetchUnreadCount(); // 更新未读计数
    ElMessage.success(`邮件已标记为${newState === 0 ? '未读' : '已读'}`);
  }).catch(error => {
    ElMessage.error('状态更新失败: ' + error.msg);
  });
}

// 修改现有的 handleDeletEmail 方法，使用 deleteEmailById
const handleDeleteEmail = (email: Email) => {
  ElMessageBox.confirm(
      '删除邮件不可撤销，请仔细检查内容！是否确认删除这封邮件？',
      '警告',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'error',
      }
  )
      .then(() => {
        deleteEmailById(email.id).then((response) => {
          if (response.code === 200){
            ElMessage({
              type: 'success',
              message: '已删除邮件',
            })
            catchEmails()
            location.reload()
          }
        }).catch((error) => {
          ElMessage.error(error.msg)
        })
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '取消删除',
        })
      })
}

// 修改 viewEmailDetail 方法
const viewEmailDetail = (email: Email) => {
  // 如果邮件未读，则标记为已读
  if (email.state === 0) {
    updateEmailState(email.id, 1).then(() => {
      email.state = 1;
      fetchUnreadCount();
    });
  }
  selectedEmail.value = email;
  detailDialogVisible.value = true;
}

// 添加回复邮件方法
const replyToEmail = (email: Email | null) => {
  if (!email) return;

  // 关闭详情弹窗
  detailDialogVisible.value = false;

  // 跳转到写邮件页面，并携带参数
  router.push({
    path: '/email/drafts', // 确保路由名称匹配
    query: {
      replyTo: email.senderName,
      replySubject: email.theme
    }
  });
}
</script>

<template>
    <div class="inbox-card">
      <div style="text-align: center;">
        <h1>基金会内部通信系统</h1>
        <div class="divider"></div>
      </div>

      <div style="display: flex;
        justify-content: space-between;
        align-items: center;">
        <div class="header-title">
          <el-icon class="header-icon"><MessageBox /></el-icon>
          <h2>收件箱 <span class="email-count">({{ total }} 封邮件, 未读: {{ unreadCount }})
          </span>
          </h2>
        </div>

        <div class="header-actions">
          <el-button @click="markAllAsRead">
            <el-icon><Reading /></el-icon> 一键已读
          </el-button>
          <el-input
              v-model="searchSender"
              placeholder="搜索发送者..."
              clearable
              class="search-input"
              @input="filterEmails"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>

          <el-select v-model="filterstate" placeholder="状态筛选" @change="filterEmails">
            <el-option label="未读邮件" value="未读邮件" />
            <el-option label="已读邮件" value="已读邮件" />
          </el-select>

          <el-select v-model="filterPriority" placeholder="发送者等级筛选" @change="filterEmails">
            <el-option label="O5" value="5" />
            <el-option label="A" value="4" />
            <el-option label="B" value="3" />
            <el-option label="C" value="2" />
            <el-option label="D" value="1" />
          </el-select>

          <el-input
            v-model="searchQuery"
            placeholder="搜索邮件..."
            clearable
            class="search-input"
            @input="filterEmails"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <el-button @click="filterEmails">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </div>

      <div class="email-list">
        <div
          v-for="email in emails"
          :key="email.id"
          class="email-item"
          :class="{
            'unread': email.state === 0,
          }"
          @click="viewEmailDetail(email)"
        >
          <div class="email-state">
            {{email.state}}
          </div>

          <div class="email-sender">
            <span>{{ email.senderName }}</span>
          </div>

          <div class="email-content">
            <div class="email-subject">
              {{ email.theme }}
              <el-tag v-if="email.senderLevel === 5" size="small" type="warning">重要</el-tag>
            </div>
          </div>

          <div class="email-meta">
            <div class="email-time">{{ email.sendTime }}</div>
            <div class="email-actions">
              <el-button size="small" @click.stop="markAsRead(email)">
                <el-icon><Reading /></el-icon>
              </el-button>
              <el-button size="small" @click.stop="handleDeleteEmail(email)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <div v-if="emails.length === 0" class="empty-emails">
          <el-icon  class="empty-icon"><MessageBox /></el-icon>
          <p >没有找到符合条件的邮件</p>
        </div>
      </div>

      <div class="inbox-footer">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="filterEmails.length"
          :page-size="pageSize"
          v-model:current-page="currentPage"
        />
      </div>
    </div>

    <!-- 邮件详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`邮件详情 - ${selectedEmail?.theme || '无主题'}`"
      width="60%"
      class="containment-dialog"
    >
      <div v-if="selectedEmail" class="email-detail">
        <div class="detail-header">
          <h2 class="email-subject">{{ selectedEmail.content }}</h2>
          <div class="priority-tag">
            <el-tag v-if="selectedEmail.senderLevel=== 5" type="warning">重要</el-tag>
          </div>
        </div>

        <div class="detail-info">
          <div class="info-row">
            <span class="info-item"><el-icon><User /></el-icon> 发件人：<strong>{{ selectedEmail.senderName }}</strong></span>
            <span class="info-item"><el-icon><Time /></el-icon> 发送时间：<strong>{{ selectedEmail.sendTime }}</strong></span>
          </div>
          <div class="info-row">
            <span v-if="selectedEmail.hasAttachment" class="info-item"><el-icon><Paperclip /></el-icon> 包含附件</span>
          </div>
          <div class="info-row">
            <span class="info-item"><el-icon><Content /></el-icon> 内容：<strong>{{ selectedEmail.content }}</strong> </span>
          </div>

        </div>

        <div class="email-content">
          {{ selectedEmail.content }}
        </div>

        <div v-if="selectedEmail.hasAttachment" class="email-attachments">
          <h3><el-icon><Folder /></el-icon> 附件</h3>
          <div class="attachment-list">
            <div class="attachment-item">
              <el-icon><Document /></el-icon> SCP-收容报告.pdf
            </div>
            <div class="attachment-item">
              <el-icon><Picture /></el-icon> 监控截图.jpg
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false" class="dialog-button">
          <el-icon><Close /></el-icon> 关闭
        </el-button>
        <el-button type="primary" @click="replyToEmail(selectedEmail)">
          <el-icon><Reply /></el-icon> 回复
        </el-button>
      </template>
    </el-dialog>
</template>

<style scoped>
.inbox-container {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0c0e2a 0%, #1a1f4b 100%);
  overflow: hidden;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
  padding: 20px;
}

@keyframes pulse {
  0% { opacity: 0.3; transform: translate(-50%, -50%) scale(1); }
  100% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.5); }
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent, rgba(93, 139, 244, 0.8), transparent);
  animation: scan 5s linear infinite;
  box-shadow: 0 0 10px rgba(93, 139, 244, 0.7);
  z-index: 1;
}

@keyframes scan {
  0% { top: 0; }
  100% { top: 100%; }
}

.bottom-scan {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #ff6b6b, transparent);
  box-shadow: 0 0 15px rgba(255, 107, 107, 0.7);
  animation: scan-horizontal 10s linear infinite;
  z-index: 1;
}

@keyframes scan-horizontal {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.inbox-card {
  margin: -20px -10px;
  height: 700px;
  border: 1px solid rgba(93, 139, 244, 0.3);
  z-index: 1;
  backdrop-filter: blur(5px);
  display: flex;
  flex-direction: column;
}

.system-title h1 {
  color: #e0e7ff;
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 1px;
  text-shadow: 0 0 10px rgba(93, 139, 244, 0.7);
}

.divider {
  height: 2px;
  background: linear-gradient(90deg, transparent, #5d8bf4, transparent);
  margin: 15px 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title h2 {
  font-size: 20px;
  margin: 0;
  font-weight: 500;
}

.email-count {
  font-size: 16px;
  color: #5d8bf4;
  font-weight: 500;
}

.header-icon {
  font-size: 20px;
  color: #5d8bf4;
  background-color: rgba(93, 139, 244, 0.1);
  border-radius: 8px;
  padding: 8px;
  margin-left: 10px;
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
  padding: 12px 0;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.email-list {
  flex: 1;
  overflow-y: auto;
  border-radius: 8px;
  padding: 10px;
  margin-top: 5px;
}

.email-item {
  display: flex;
  align-items: center;
  height: 45px;
  border-bottom: 1px solid rgba(93, 139, 244, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 6px;
  margin-bottom: 8px;
}

.email-item:hover {
  background: rgba(93, 139, 244, 0.15);
}

.email-item.unread {
  background: rgba(93, 139, 244, 0.1);
  border-left: 3px solid #5d8bf4;
}

.email-item.flagged {
  background: rgba(255, 193, 7, 0.1);
}

.email-item.priority-high {
  border-left: 3px solid #e6a23c;
}

.email-item.priority-urgent {
  border-left: 3px solid #f56c6c;
}

.email-state {
  width: 40px;
  text-align: center;
  font-size: 18px;
  color: #5d8bf4;
}

.email-sender {
  width: 200px;
  font-weight: 500;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.email-subject {
  font-weight: 500;
  font-size: 24px;
  text-shadow:  1px 1px 2px rgba(93, 139, 244, 0.3);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.email-preview {
  font-size: 13px;
  color: #aab6ff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.email-meta {
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.email-time {
  font-size: 13px;
  color: #909399;
  margin-bottom: 0px;
}

.email-actions {
  display: flex;
  gap: 8px;
}

.empty-emails {
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

.empty-emails p {
  margin-bottom: 20px;
  font-size: 16px;
}

.inbox-footer {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid rgba(93, 139, 244, 0.3);
}

/* 邮件详情样式 */
.email-detail {
  position: relative;
  background: rgba(15, 30, 61, 0.85);
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #304878;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #4a6fb3;
}

.priority-tag {
  display: flex;
  gap: 10px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  flex-wrap: wrap;
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #a0b9e0;
}

.info-item strong {
  color: #ffffff;
  font-weight: 500;
}

.email-content {
  flex: 1;
  width: 500px;
}

.email-attachments {
  margin-top: 25px;
}

.email-attachments h3 {
  color: #e0f0ff;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 15px;
  background: rgba(20, 30, 60, 0.5);
  border-radius: 6px;
  color: #a0b9e0;
  border: 1px dashed #4a6fb3;
}

/* 弹窗样式 */
.containment-dialog {
  background: linear-gradient(135deg, #0c1a33, #1a2a4a);
  border: 1px solid #4a6fb3;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(74, 111, 179, 0.5);
}

.dialog-button {
  background: linear-gradient(to right, #4a6fb3, #3a5a9c);
  border: none;
  color: white;
  font-weight: bold;
  transition: all 0.3s ease;
}

.dialog-button:hover {
  background: linear-gradient(to right, #5a7fc3, #4a6aac);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 111, 179, 0.4);
}
</style>