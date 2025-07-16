<template>
  <el-card style="margin: 10px; border: none; box-shadow: none;">
    <el-input
        v-model="searchQuery"
        placeholder="搜索草稿"
        suffix-icon="el-icon-search"
        style="width: 100%; margin-bottom: 15px;"
    >
      <template #append>
        <el-button @click="searchEmails">搜索</el-button>
      </template>
    </el-input>

    <el-divider content-position="left">草稿 ({{ emails.length }})</el-divider>

    <el-table
        :data="emails"
        stripe
        style="width: 100%;"
        @row-click="handleEmailClick"
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="收件人" prop="to">
        <template #default="scope">
          <div>
            {{ scope.row.to || '(无收件人)' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="主题" prop="subject">
        <template #default="scope">
          <div>
            {{ scope.row.subject || '(无主题)' }}
          </div>
          <div class="text-gray-500 text-sm" v-if="scope.row.preview">
            {{ scope.row.preview }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="日期" prop="date" width="120" sortable>
        <template #default="scope">
          <div>
            {{ formatDate(scope.row.date) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template #default="scope">
          <el-button size="mini" type="primary" @click="editDraft(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="emails.length"
        style="margin-top: 15px;"
    >
    </el-pagination>
  </el-card>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'

const props = defineProps({
  emails: {
    type: Array,
    required: true
  },
  onEmailSelect: {
    type: Function,
    required: true
  },
  onCompose: {
    type: Function,
    required: true
  }
})

const emits = defineEmits(['refresh'])

// 搜索和分页
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

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

// 处理邮件点击
const handleEmailClick = (row: any) => {
  props.onEmailSelect(row)
}

// 编辑草稿
const editDraft = (draft: any) => {
  props.onCompose()

  // 延迟设置表单值，确保对话框已打开
  setTimeout(() => {
    // 这里应该通过事件或状态管理传递草稿数据
    console.log('编辑草稿:', draft)
    // 实际应用中应该使用事件总线或状态管理来传递数据
  }, 300)
}

// 搜索邮件
const searchEmails = () => {
  console.log('搜索草稿:', searchQuery.value)
  // 实际应用中应该调用API进行搜索
  emits('refresh')
}

// 处理分页相关
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
}

// 处理选择变化
const handleSelectionChange = (val: any[]) => {
  console.log('选中的草稿:', val)
}
</script>

<style scoped>
.text-gray-500 {
  color: #909399;
}

.text-sm {
  font-size: 12px;
}
</style>