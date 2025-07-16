<template>
  <el-card style="width: 100%; height: 800px; overflow: hidden;">
    <el-container style="height: 100%;">
      <!-- 左侧导航栏 -->
      <el-aside width="320px" style="height: 100%; overflow: auto;">
        <EmailSidebar
            :currentFolder="currentFolder"
            :unreadCount="unreadCount"
            :searchQuery="searchQuery"
            @update:searchQuery="searchQuery = $event"
            @navigate="handleFolderChange"
            @search="searchEmails"
            @compose="handleComposeEmail"
        />
      </el-aside>

      <!-- 右侧内容区域 -->
      <el-main style="height: 100%; overflow: auto;">
        <!-- 文件夹内容区域 -->
        <div v-if="['inbox', 'sent', 'drafts', 'trash'].includes(currentFolder)">
          <component
              :is="currentFolderComponent"
              :emails="currentEmails"
              :selectedEmail="selectedEmail"
              @selectEmail="handleEmailSelect"
              @deleteEmail="handleDeleteEmail"
          />
        </div>

        <!-- 撰写邮件 -->
        <div v-else-if="currentFolder === 'compose'">
          <ComposeEmailView
              :form="composeForm"
              @saveDraft="saveDraft"
              @sendEmail="sendEmail"
              @addCc="addCc"
              @addBcc="addBcc"
              @uploadChange="handleUploadChange"
              @uploadRemove="handleUploadRemove"
          />
        </div>

        <!-- 邮件详情 -->
        <EmailDetail
            v-if="selectedEmail && currentFolder !== 'compose'"
            :email="selectedEmail"
            @reply="replyEmail"
            @replyAll="replyAllEmail"
            @forward="forwardEmail"
            @delete="deleteEmail"
        />

        <!-- 空状态提示 -->
        <div v-else class="empty-state">
          <i class="el-icon-inbox text-5xl text-gray-300 mb-4"></i>
          <p class="text-gray-500">当前文件夹为空</p>
        </div>
      </el-main>
    </el-container>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import EmailSidebar from './menu/index.vue'
import EmailList from './emailList/index.vue'
import SentEmails from './sendList/index.vue'
import Drafts from './drafts/index.vue'
import Trash from './trashList/index.vue'
import ComposeEmailView from './compose/index.vue'
import EmailDetail from './detail/index.vue'

// 邮箱数据
const emails = ref([
  {
    id: 1,
    from: 'alice@example.com',
    to: 'you@example.com',
    subject: '项目会议邀请',
    content: '<p>你好，我们将于下周一上午10点召开项目进度会议，请准备好相关资料。</p><p>会议将讨论以下内容：</p><ul><li>项目当前进度</li><li>遇到的问题和解决方案</li><li>下一步计划</li></ul><p>请确认你是否能参加。</p><p>祝好，<br>Alice</p>',
    date: new Date('2025-07-14T09:30:00'),
    read: false,
    preview: '你好，我们将于下周一上午10点召开项目进度会议...',
    attachments: [
      { name: '项目规划.docx', size: '2.4MB' },
      { name: '进度报告.xlsx', size: '1.8MB' }
    ],
    folder: 'inbox'
  },
  {
    id: 2,
    from: 'bob@example.com',
    to: 'you@example.com',
    subject: '关于产品更新的反馈',
    content: '<p>嗨，我想分享一些关于我们新产品更新的反馈。</p><p>整体来说，更新后的用户界面更加直观，但我注意到几个小问题：</p><ol><li>搜索功能有时反应较慢</li><li>文件上传时进度条不显示</li><li>移动设备上的菜单布局有些拥挤</li></ol><p>希望这些反馈对你有帮助。</p><p>祝好，<br>Bob</p>',
    date: new Date('2025-07-14T14:15:00'),
    read: false,
    preview: '嗨，我想分享一些关于我们新产品更新的反馈...',
    attachments: [],
    folder: 'inbox'
  },
  {
    id: 3,
    from: 'support@example.com',
    to: 'you@example.com',
    subject: '账户安全通知',
    content: '<p>尊敬的用户，</p><p>我们注意到您的账户最近在异地登录。如果这不是您本人操作，请立即联系我们的客服团队。</p><p>为了保障您的账户安全，建议您：</p><ul><li>立即更改密码</li><li>启用两步验证</li><li>检查并删除可疑的关联设备</li></ul><p>如果是您本人操作，请忽略此邮件。</p><p>祝好，<br>客户支持团队</p>',
    date: new Date('2025-07-13T16:42:00'),
    read: true,
    preview: '尊敬的用户，我们注意到您的账户最近在异地登录...',
    attachments: [],
    folder: 'inbox'
  },
  {
    id: 4,
    from: 'you@example.com',
    to: 'marketing@example.com',
    subject: '关于季度营销计划',
    content: '<p>你好，</p><p>我已经审核了我们的季度营销计划，有几点建议想和你讨论：</p><ol><li>增加社交媒体广告投放</li><li>考虑与行业KOL合作</li><li>优化邮件营销内容</li></ol><p>请查看附件中的详细分析。</p><p>祝好，<br>You</p>',
    date: new Date('2025-07-12T10:30:00'),
    read: true,
    preview: '你好，我已经审核了我们的季度营销计划...',
    attachments: [
      { name: '营销分析.xlsx', size: '1.2MB' }
    ],
    folder: 'sent'
  },
  {
    id: 5,
    from: 'you@example.com',
    to: 'jane@example.com',
    subject: '关于合作洽谈的回复',
    content: '<p>Jane，</p><p>很高兴收到你的邮件，我对我们双方的合作可能性很感兴趣。</p><p>我建议我们在下周三下午3点进行一次电话会议，讨论具体细节。</p><p>请确认这个时间是否合适。</p><p>祝好，<br>You</p>',
    date: new Date('2025-07-11T15:45:00'),
    read: true,
    preview: 'Jane，很高兴收到你的邮件，我对我们双方的合作可能性很感兴趣...',
    attachments: [],
    folder: 'sent'
  },
  {
    id: 6,
    from: 'you@example.com',
    to: 'team@example.com',
    subject: '项目周会总结 [草稿]',
    content: '<p>大家好，</p><p>这是本周项目进度的总结：</p><ul><li>前端开发完成80%</li><li>后端API已全部部署</li><li>测试团队发现5个关键问题</li></ul><p>我们需要在下周解决这些问题，确保项目按时交付。</p><p>请查看附件中的详细报告。</p><p>祝好，<br>You</p>',
    date: new Date('2025-07-10T18:20:00'),
    read: true,
    preview: '大家好，这是本周项目进度的总结...',
    attachments: [
      { name: '项目周报.docx', size: '850KB' }
    ],
    folder: 'draft'
  }
])

// 当前文件夹
const currentFolder = ref('inbox')

// 搜索查询
const searchQuery = ref('')

// 选中的邮件
const selectedEmail = ref(null)

// 撰写邮件表单
const composeForm = reactive({
  id: 0,
  to: '',
  cc: '',
  bcc: '',
  subject: '',
  content: '',
  attachments: []
})

// 控制抄送和密送字段显示
const showCc = ref(false)
const showBcc = ref(false)

// 未读邮件计数
const unreadCount = computed(() => {
  return emails.value.filter(email => !email.read && email.folder === 'inbox').length
})

// 计算属性：根据当前文件夹获取对应组件
const currentFolderComponent = computed(() => {
  switch(currentFolder.value) {
    case 'inbox': return EmailList
    case 'sent': return SentEmails
    case 'drafts': return Drafts
    case 'trash': return Trash
    default: return EmailList
  }
})

// 计算属性：根据当前文件夹过滤邮件
const currentEmails = computed(() => {
  // 应用搜索过滤
  const filteredBySearch = emails.value.filter(email => {
    const search = searchQuery.value.toLowerCase()
    return (
        email.subject.toLowerCase().includes(search) ||
        email.from.toLowerCase().includes(search) ||
        email.content.toLowerCase().includes(search)
    )
  })

  // 按文件夹过滤
  return filteredBySearch.filter(email => email.folder === currentFolder.value)
})

// 处理文件夹切换
const handleFolderChange = (folder: string) => {
  currentFolder.value = folder
  selectedEmail.value = null // 切换文件夹时清除选中的邮件
}

// 处理邮件选择
const handleEmailSelect = (email: any) => {
  selectedEmail.value = { ...email }

  // 如果是未读邮件，标记为已读
  if (!email.read && email.folder === 'inbox') {
    const index = emails.value.findIndex(e => e.id === email.id)
    if (index !== -1) {
      emails.value[index].read = true
    }
  }
}

// 处理撰写邮件
const handleComposeEmail = () => {
  currentFolder.value = 'compose'
  selectedEmail.value = null

  // 重置表单
  resetComposeForm()
}

// 添加抄送字段
const addCc = () => {
  showCc.value = true
}

// 添加密送字段
const addBcc = () => {
  showBcc.value = true
}

// 保存草稿
const saveDraft = () => {
  if (!composeForm.to && !composeForm.subject && !composeForm.content) {
    ElMessage.warning('至少需要填写收件人、主题或内容才能保存草稿')
    return
  }

  // 创建或更新草稿邮件
  const draftEmail = {
    id: composeForm.id || Date.now(),
    from: 'you@example.com',
    to: composeForm.to,
    cc: composeForm.cc,
    bcc: composeForm.bcc,
    subject: composeForm.subject,
    content: composeForm.content,
    date: new Date(),
    read: true,
    preview: composeForm.content.substring(0, 50) + '...',
    attachments: composeForm.attachments.map(file => ({
      name: file.name,
      size: formatFileSize(file.size)
    })),
    folder: 'draft'
  }

  // 检查是否已存在该草稿
  const index = emails.value.findIndex(e => e.id === draftEmail.id && e.folder === 'draft')

  if (index !== -1) {
    // 更新现有草稿
    emails.value.splice(index, 1, draftEmail)
  } else {
    // 添加新草稿
    emails.value.push(draftEmail)
  }

  ElMessage.success('草稿已保存')
  currentFolder.value = 'drafts'
}

// 发送邮件
const sendEmail = () => {
  if (!composeForm.to) {
    ElMessage.error('请输入收件人')
    return
  }

  if (!composeForm.subject) {
    ElMessage.error('请输入主题')
    return
  }

  // 创建新邮件
  const newEmail = {
    id: Date.now(),
    from: 'you@example.com',
    to: composeForm.to,
    cc: composeForm.cc,
    bcc: composeForm.bcc,
    subject: composeForm.subject,
    content: composeForm.content,
    date: new Date(),
    read: true,
    preview: composeForm.content.substring(0, 50) + '...',
    attachments: composeForm.attachments.map(file => ({
      name: file.name,
      size: formatFileSize(file.size)
    })),
    folder: 'sent'
  }

  // 添加到已发送邮件
  emails.value.push(newEmail)

  // 如果是从草稿发送，删除草稿
  if (composeForm.id) {
    const draftIndex = emails.value.findIndex(e => e.id === composeForm.id && e.folder === 'draft')
    if (draftIndex !== -1) {
      emails.value.splice(draftIndex, 1)
    }
  }

  ElMessage.success('邮件发送成功')
  currentFolder.value = 'sent'
  resetComposeForm()
}

// 重置撰写表单
const resetComposeForm = () => {
  composeForm.id = null
  composeForm.to = ''
  composeForm.cc = ''
  composeForm.bcc = ''
  composeForm.subject = ''
  composeForm.content = ''
  composeForm.attachments = []
  showCc.value = false
  showBcc.value = false
}

// 处理上传文件变化
const handleUploadChange = (file: any, fileList: any[]) => {
  composeForm.attachments = fileList
}

// 处理上传文件移除
const handleUploadRemove = (file: any, fileList: any[]) => {
  composeForm.attachments = fileList
}

// 回复邮件
const replyEmail = () => {
  if (!selectedEmail.value) return

  currentFolder.value = 'compose'
  selectedEmail.value = null

  // 填充回复表单
  composeForm.to = selectedEmail.value.from
  composeForm.subject = selectedEmail.value.subject.startsWith('Re: ')
      ? selectedEmail.value.subject
      : 'Re: ' + selectedEmail.value.subject

  // 构建回复内容
  composeForm.content = `
On ${formatFullDate(selectedEmail.value.date)}, ${selectedEmail.value.from} wrote:
${selectedEmail.value.content}

--
Your reply here...
  `.trim()
}

// 回复全部
const replyAllEmail = () => {
  if (!selectedEmail.value) return

  currentFolder.value = 'compose'
  selectedEmail.value = null

  // 构建回复给所有人的收件人列表
  let recipients = [selectedEmail.value.from]

  // 添加原始邮件的收件人（除了自己）
  const originalRecipients = selectedEmail.value.to.split(',').map(email => email.trim())
  recipients = recipients.concat(originalRecipients.filter(email => email !== 'you@example.com'))

  // 添加抄送人（如果有）
  if (selectedEmail.value.cc) {
    const originalCc = selectedEmail.value.cc.split(',').map(email => email.trim())
    recipients = recipients.concat(originalCc.filter(email => email !== 'you@example.com'))
  }

  composeForm.to = recipients.join(', ')
  composeForm.subject = selectedEmail.value.subject.startsWith('Re: ')
      ? selectedEmail.value.subject
      : 'Re: ' + selectedEmail.value.subject

  // 构建回复内容
  composeForm.content = `
On ${formatFullDate(selectedEmail.value.date)}, ${selectedEmail.value.from} wrote:
${selectedEmail.value.content}

--
Your reply here...
  `.trim()
}

// 转发邮件
const forwardEmail = () => {
  if (!selectedEmail.value) return

  currentFolder.value = 'compose'
  selectedEmail.value = null

  composeForm.subject = selectedEmail.value.subject.startsWith('Fwd: ')
      ? selectedEmail.value.subject
      : 'Fwd: ' + selectedEmail.value.subject

  // 构建转发内容
  composeForm.content = `
---------- Forwarded message ----------
From: ${selectedEmail.value.from}
Date: ${formatFullDate(selectedEmail.value.date)}
Subject: ${selectedEmail.value.subject}
To: ${selectedEmail.value.to}

${selectedEmail.value.content}
  `.trim()
}

// 删除邮件
const deleteEmail = () => {
  if (!selectedEmail.value) return

  ElMessageBox.confirm(
      '确定要删除这封邮件吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    const email = selectedEmail.value

    if (email.folder === 'trash') {
      // 从垃圾箱彻底删除
      const index = emails.value.findIndex(e => e.id === email.id)
      if (index !== -1) {
        emails.value.splice(index, 1)
      }
    } else {
      // 移至垃圾箱
      const index = emails.value.findIndex(e => e.id === email.id)
      if (index !== -1) {
        emails.value[index].folder = 'trash'
      }
    }

    selectedEmail.value = null
    ElMessage.success('邮件已删除')
  }).catch(() => {
    // 取消操作
  })
}

// 处理邮件删除（从子组件触发）
const handleDeleteEmail = (emailId: number) => {
  const index = emails.value.findIndex(e => e.id === emailId)
  if (index !== -1) {
    emails.value[index].folder = 'trash'

    if (selectedEmail.value && selectedEmail.value.id === emailId) {
      selectedEmail.value = null
    }

    ElMessage.success('邮件已移至垃圾箱')
  }
}

// 搜索邮件
const searchEmails = () => {
  // 搜索逻辑已在currentEmails计算属性中实现
  // 这里只需要触发重新计算
}

// 格式化日期
const formatDate = (date: Date) => {
  const today = new Date()
  const isToday =
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()

  if (isToday) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  } else {
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' })
  }
}

// 格式化完整日期
const formatFullDate = (date: Date) => {
  return date.toLocaleString([], {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 页面加载时初始化
onMounted(() => {
  // 可以在这里加载邮件数据（从API或本地存储）
})
</script>

<style scoped>
.empty-state {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
}

.email-content {
  line-height: 1.6;
  color: #303133;
  padding: 10px 0;
  font-size: 14px;
}
</style>  