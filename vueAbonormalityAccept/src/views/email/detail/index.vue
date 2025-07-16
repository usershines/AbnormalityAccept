<template>
  <div>
    <div class="flex items-center mb-6">
      <img
          src="https://picsum.photos/id/1005/40/40"
          alt="发件人头像"
          class="w-10 h-10 rounded-full mr-3"
      >
      <div>
        <div class="font-bold text-lg">{{ email.from }}</div>
        <div class="text-sm text-gray-500">
          发送给 <span class="font-medium">{{ email.to }}</span> · {{ formatFullDate(email.date) }}
        </div>
        <div class="text-sm text-gray-500" v-if="email.cc">
          抄送给 <span class="font-medium">{{ email.cc }}</span>
        </div>
      </div>
    </div>

    <div class="border-t border-b py-4 my-4">
      <el-tag v-if="email.attachments && email.attachments.length > 0"
              effect="plain"
              type="info"
              @click="showAttachments = !showAttachments"
      >
        <i class="el-icon-paperclip mr-1"></i>
        {{ email.attachments.length }} 个附件
        <i class="el-icon-arrow-down" :class="{'rotate-180': showAttachments}"></i>
      </el-tag>
    </div>

    <div v-show="showAttachments" class="mb-6">
      <el-card class="mb-4">
        <el-row :gutter="20">
          <el-col :span="8" v-for="(attachment, index) in email.attachments" :key="index">
            <el-card shadow="hover" class="text-center">
              <i class="el-icon-document text-3xl text-gray-400 mb-2"></i>
              <p class="font-medium truncate">{{ attachment.name }}</p>
              <p class="text-xs text-gray-500">{{ attachment.size }}</p>
              <el-button size="mini" type="primary" class="mt-2" @click="downloadAttachment(attachment)">
                下载
              </el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <div class="email-content" v-html="email.content"></div>

    <div class="flex justify-end mt-6">
      <el-button type="primary" size="small" @click="$emit('reply')">
        <i class="el-icon-reply mr-1"></i> 回复
      </el-button>
      <el-button type="primary" size="small" @click="$emit('replyAll')" class="ml-2">
        <i class="el-icon-reply-all mr-1"></i> 回复全部
      </el-button>
      <el-button type="primary" size="small" @click="$emit('forward')" class="ml-2">
        <i class="el-icon-forward mr-1"></i> 转发
      </el-button>
      <el-button type="danger" size="small" @click="$emit('delete')" class="ml-2">
        <i class="el-icon-delete mr-1"></i> 删除
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'

const props = defineProps({
  email: {
    type: Object,
    required: true
  }
})

const emits = defineEmits(['reply', 'replyAll', 'forward', 'delete'])

// 附件显示控制
const showAttachments = ref(false)

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

// 下载附件
const downloadAttachment = (attachment: any) => {
  console.log('下载附件:', attachment)
  // 实际应用中应该调用API下载附件
}
</script>

<style scoped>
.font-bold {
  font-weight: bold;
}

.text-gray-500 {
  color: #909399;
}

.text-sm {
  font-size: 12px;
}

.text-lg {
  font-size: 18px;
}

.email-content {
  line-height: 1.6;
  color: #303133;
  padding: 10px 0;
  font-size: 14px;
}

.rotate-180 {
  transform: rotate(180deg);
}
</style>