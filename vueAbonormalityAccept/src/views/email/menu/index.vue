<!-- EmailSidebar.vue -->
<template>
  <el-card style="margin: 10px; border: none; box-shadow: none;">
    <el-button type="primary" style="width: 100%; margin-bottom: 15px;" @click="handleCompose">
      <i class="el-icon-edit"></i> 撰写邮件
    </el-button>

    <el-menu
        :default-active="currentFolder"
        class="el-menu-vertical-demo"
        @open="handleOpen"
        @close="handleClose"
    >
      <el-menu-item index="inbox">
        <i class="el-icon-message"></i>
        <span>收件箱</span>
        <span class="el-badge__content" style="right: 10px;" v-if="unreadCount > 0">{{ unreadCount }}</span>
      </el-menu-item>
      <el-menu-item index="sent">
        <i class="el-icon-send"></i>
        <span>已发送</span>
      </el-menu-item>
      <el-menu-item index="drafts">
        <i class="el-icon-document"></i>
        <span>草稿</span>
      </el-menu-item>
      <el-menu-item index="trash">
        <i class="el-icon-delete"></i>
        <span>垃圾箱</span>
      </el-menu-item>
    </el-menu>

    <el-input
        :value="searchQuery"
      @input="handleSearchInput"
      placeholder="搜索邮件"
      suffix-icon="el-icon-search"
      style="width: 100%; margin: 15px 0;"
    >
    <template #append>
      <el-button @click="handleSearch">搜索</el-button>
    </template>
    </el-input>

    <!-- 其他代码保持不变 -->
  </el-card>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  currentFolder: {
    type: String,
    required: true
  },
  unreadCount: {
    type: Number,
    default: 0
  },
  searchQuery: {
    type: String,
    default: ''
  }
})

const emits = defineEmits(['navigate', 'search', 'compose', 'update:searchQuery'])

// 处理搜索输入
const handleSearchInput = (value: string) => {
  emits('update:searchQuery', value)
}

// 处理搜索按钮点击
const handleSearch = () => {
  emits('search')
}

// 处理撰写邮件
const handleCompose = () => {
  emits('compose')
}

// 处理菜单事件
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}

const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>